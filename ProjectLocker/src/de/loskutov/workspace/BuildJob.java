package de.loskutov.workspace;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.progress.IProgressService;

public class BuildJob extends WorkspaceJob {

	private final IResource resource;

	static {
		IProgressService service = PlatformUI.getWorkbench().getProgressService();
		service.registerIconForFamily(AbstractUIPlugin.imageDescriptorFromPlugin("projectLocker", "icons/build.png"), BuildJob.class);
	}

	private final int warmupBuilds = 3;
	int count = 10;
	long time;
	List<Long> times = new ArrayList<>();
	ILog log = Platform.getLog(Platform.getBundle("projectLocker"));

	public BuildJob(String name, IResource resource) {
		super(name);
		setRule(resource.getWorkspace().getRoot());
		setUser(true);
		setSystem(false);
		this.resource = resource;
		if(resource instanceof IFolder || resource instanceof IFile) {
			resource = resource.getProject();
		}
	}

	@Override
	public boolean belongsTo(Object family) {
		return super.belongsTo(family) || BuildJob.class == family;
	}

	@Override
	public String toString() {
		return super.toString() + " on " + resource.getName();
	}

	@Override
	public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
		String message = "Going to run " + count + " builds on " + resource;
		reportMemoryUse();
		log.log(new Status(IStatus.INFO, BuildJob.class, message));
		try {
			for (int i = 0; i < count; i++) {
				if(monitor.isCanceled()) {
					break;
				}
				build(monitor);
			}
		} finally {
			printStatistics();
		}
		return monitor.isCanceled()? Status.CANCEL_STATUS : Status.OK_STATUS;
	}

	private void reportMemoryUse() {
		System.gc();
		System.runFinalization();
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
		}
		System.gc();
		System.runFinalization();
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
		}
		System.gc();
		System.runFinalization();
		try {
			Thread.sleep(150);
		} catch (InterruptedException e) {
		}
		Runtime runtime = Runtime.getRuntime();
		long totalMemory = runtime.totalMemory();
		long used = totalMemory - runtime.freeMemory();
		String message = "Used memory : " + humanReadableByteCountBin(used) + ", total: " + humanReadableByteCountBin(totalMemory);
		log.log(new Status(IStatus.INFO, BuildJob.class, message));
	}

	void build(IProgressMonitor monitor) throws CoreException {
		long start = System.currentTimeMillis();
		if(resource instanceof IWorkspaceRoot) {
			IWorkspaceRoot root = (IWorkspaceRoot) resource;
			root.getWorkspace().build(IncrementalProjectBuilder.FULL_BUILD, monitor);
		} else {
			resource.getProject().build(IncrementalProjectBuilder.FULL_BUILD, monitor);
		}
		long stop = System.currentTimeMillis();
		times.add(Long.valueOf(stop - start));
		if(times.size() > warmupBuilds) {
			time += stop - start;
		}
	}

	void printStatistics() {
		String message = "All times: " + times;
		log.log(new Status(IStatus.INFO, BuildJob.class, message));
		if(times.size() > warmupBuilds) {
			for (int i = 0; i < warmupBuilds && i < times.size(); i++) {
				times.remove(0);
			}
		}
		long median = median(times);
		message = times.size() + " builds in " + time  + " ms, median: " + median + " ms";
		log.log(new Status(IStatus.INFO, BuildJob.class, message));
		reportMemoryUse();
	}

	private long median(List<Long> list) {
		if(list.isEmpty()) {
			return -1;
		}
		Collections.sort(list);
		if(list.size() % 2 == 1) {
			return list.get(list.size() / 2);
		}
		return (list.get(list.size() / 2) + list.get(list.size() / 2 - 1)) / 2;
	}

	public static String humanReadableByteCountBin(long bytes) {
		long absB = bytes == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(bytes);
		if (absB < 1024) {
			return bytes + " B";
		}
		long value = absB;
		CharacterIterator ci = new StringCharacterIterator("KMGTPE");
		for (int i = 40; i >= 0 && absB > 0xfffccccccccccccL >> i; i -= 10) {
			value >>= 10;
			ci.next();
		}
		value *= Long.signum(bytes);
		return String.format("%.1f %ciB", value / 1024.0, ci.current());
	}
}
