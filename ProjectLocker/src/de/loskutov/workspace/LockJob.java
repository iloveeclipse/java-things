package de.loskutov.workspace;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.progress.IProgressService;

public class LockJob extends WorkspaceJob {

	private final IResource resource;

	static {
		IProgressService service = PlatformUI.getWorkbench().getProgressService();
		service.registerIconForFamily(AbstractUIPlugin.imageDescriptorFromPlugin("projectLocker", "icons/lock.gif"), LockJob.class);
	}

	public LockJob(String name, IResource resource) {
		super(name);
		//		setRule(resource);
		setUser(true);
		setSystem(true);
		this.resource = resource;
	}

	public IStatus run2(IProgressMonitor monitor) {

		while(!monitor.isCanceled()){
			try {
				Thread.sleep(1000);
				Path path = Paths.get(System.getProperty("user.home"), ".stopLockJob");
				if(Files.exists(path)){
					try {
						Files.delete(path);
					} catch (IOException e) {
						e.printStackTrace();
					}
					return Status.CANCEL_STATUS;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
				return Status.CANCEL_STATUS;
			}
		}
		return Status.OK_STATUS;
	}

	@Override
	public boolean belongsTo(Object family) {
		return super.belongsTo(family) || LockJob.class == family;
	}

	@Override
	public String toString() {
		return super.toString() + " on " + resource;
	}

	@Override
	public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
		IWorkspaceRunnable workspaceRunnable = new IWorkspaceRunnable() {
			@Override
			public void run(IProgressMonitor pm) throws CoreException {
				try {
					run2(pm);
				} catch (Exception e) {
					// Re-throw as OperationCanceledException, which will be
					// caught and re-thrown as InterruptedException below.
					throw new OperationCanceledException(e.getMessage());
				}
				// CoreException and OperationCanceledException are propagated
			}
		};
		ResourcesPlugin.getWorkspace().run(workspaceRunnable,
				resource, IResource.NONE, monitor);

		return run2(monitor);
	}

}
