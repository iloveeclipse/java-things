package de.loskutov.workspace;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.internal.util.PrefUtil;

public class LockHandler extends BaseHandler {

	public LockHandler() {
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IResource resource = getFromActiveEditor(event);
		if(resource == null){
			resource = getFromSelection(event);
		}
		if(resource == null){
			resource = ResourcesPlugin.getWorkspace().getRoot();
		}

		String hint = "To unlock, create empty stopLockJob file in "
				+ System.getProperty("user.home") + " directory or cancel the lock job!";
		boolean confirm = MessageDialog.openConfirm(
				HandlerUtil.getActiveShell(event), "Confirm lock",
				"Lock " + resource + "?" + "\n " + hint);

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
			LockJob job = new LockJob("Locking " + resource, resource);
			job.schedule();
		}
		return null;
	}

}
