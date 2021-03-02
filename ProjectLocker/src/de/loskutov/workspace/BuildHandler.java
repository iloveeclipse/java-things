package de.loskutov.workspace;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;

public class BuildHandler extends BaseHandler {

	public BuildHandler() {
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

		BuildJob job = new BuildJob("Building " + resource, resource);
		job.schedule();
		return null;
	}

}
