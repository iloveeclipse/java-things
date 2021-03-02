package de.loskutov.workspace;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.statushandlers.StatusManager;

public class CustomHandler extends BaseHandler {

	public CustomHandler() {
		super();
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IResource resource = getFromActiveEditor(event);
		if(resource != null) {
			Status status = new Status(IStatus.INFO, getClass(), "Selected: " + resource);
			StatusManager.getManager().handle(status, StatusManager.LOG | StatusManager.SHOW);
		}
		return resource;
	}



}
