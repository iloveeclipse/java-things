package de.loskutov.workspace;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.handlers.HandlerUtil;

public class BuildHandler extends AbstractHandler {

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

	private IResource getFromSelection(ExecutionEvent event) {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if(selection == null || !(selection instanceof IStructuredSelection) || selection.isEmpty()){
			return null;
		}
		IStructuredSelection sel = (IStructuredSelection) selection;
		Object object = sel.getFirstElement();
		IResource project = getAdapter(object, IResource.class);
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

	private IResource getFromActiveEditor(ExecutionEvent event) {
		IEditorInput input = HandlerUtil.getActiveEditorInput(event);
		if (!(input instanceof IFileEditorInput)) {
			return null;
		}
		IFileEditorInput fei = (IFileEditorInput) input;
		IResource project = fei.getFile().getProject();
		return project;
	}
}
