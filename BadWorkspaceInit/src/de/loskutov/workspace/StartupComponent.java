package de.loskutov.workspace;

import org.eclipse.core.internal.runtime.InternalPlatform;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osgi.service.datalocation.Location;
import org.osgi.framework.Bundle;

public class StartupComponent {

	public StartupComponent() {
		super();
	}

	public void start() {
		Job job = new Job("Bad workspace init") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				// IContainer c = null;
				// String encoding = ResourcesPlugin.getEncoding();
				Location location = Platform.getInstanceLocation();
				if(location.isSet()) {
					return Status.OK_STATUS;
				}
				IPath pLocation = Platform.getLocation();
				System.err.println("Got platform location: " + pLocation);
				// should throw an exception now, but instead we can get
				// workspace initialized to default location
				IWorkspace workspace = ResourcesPlugin.getWorkspace();
				if(workspace == null) {
					return Status.OK_STATUS;
				}
				String message = "Workspace is there: " + workspace;
				System.err.println(message);
				IStatus error = new Status(IStatus.ERROR, getClass(), message, new Exception("Using default workspace!!!"));
				return error;
			}
		};
		job.schedule();
	}
}
