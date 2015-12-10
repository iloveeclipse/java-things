package de.loskutov.workspace;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.internal.util.PrefUtil;

public class LockHandler extends AbstractHandler {

	public LockHandler() {
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IProject project = getFromActiveEditor(event);
		if(project == null){
			project = getFromSelection(event);
		}
		if(project == null){
			return null;
		}

		String hint = "To unlock, create empty .stopLockJob file in "
				+ System.getProperty("user.home") + " directory or cancel the lock job!";
		boolean confirm = MessageDialog.openConfirm(
				HandlerUtil.getActiveShell(event), "Confirm lock",
				"Lock the project " + project.getName() + "?" + "\n " + hint);

		if (confirm) {
			System.out.println(hint);
			IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
			if(window != null){
				try {
					PrefUtil.getAPIPreferenceStore().setValue(IWorkbenchPreferenceConstants.SHOW_SYSTEM_JOBS, true);
					window.getActivePage().showView(IPageLayout.ID_PROGRESS_VIEW);
				} catch (PartInitException e) {
					// ignore
				}
			}
			LockJob job = new LockJob("Locking " + project, project);
			job.schedule();
		}
		return null;
	}

	private IProject getFromSelection(ExecutionEvent event) {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if(selection == null || !(selection instanceof IStructuredSelection) || selection.isEmpty()){
			return null;
		}
		IStructuredSelection sel = (IStructuredSelection) selection;
		Object object = sel.getFirstElement();
		IProject project = getAdapter(object, IProject.class);
		if(project == null){
			IResource res = getAdapter(object, IResource.class);
			if(res == null){
				res = getAdapter(object, IContainer.class);
			}
			if(res == null){
				res = getAdapter(object, IFile.class);
			}
			if(res != null){
				project = res.getProject();
			}
		}
		return project;
	}

	private static <T> T getAdapter(Object o, Class<T> clazz){
		Object adapter = Platform.getAdapterManager().getAdapter(o, clazz);
		return clazz.cast(adapter);
	}

	private IProject getFromActiveEditor(ExecutionEvent event) {
		IEditorInput input = HandlerUtil.getActiveEditorInput(event);
		if (!(input instanceof IFileEditorInput)) {
			return null;
		}
		IFileEditorInput fei = (IFileEditorInput) input;
		IProject project = fei.getFile().getProject();
		return project;
	}
}
