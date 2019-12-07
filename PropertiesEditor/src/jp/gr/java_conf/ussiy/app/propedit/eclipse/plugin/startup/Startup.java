/**
 * 
 */
package jp.gr.java_conf.ussiy.app.propedit.eclipse.plugin.startup;

import java.util.Iterator;
import java.util.regex.Pattern;

import jp.gr.java_conf.ussiy.app.propedit.eclipse.plugin.PropertiesEditorPlugin;
import jp.gr.java_conf.ussiy.app.propedit.eclipse.plugin.preference.PropertiesPreference;
import jp.gr.java_conf.ussiy.app.propedit.eclipse.plugin.util.ProjectProperties;
import jp.gr.java_conf.ussiy.app.propedit.eclipse.plugin.util.ResourceChange;
import jp.gr.java_conf.ussiy.app.propedit.eclipse.plugin.util.ResourceChangeList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IStartup;


/**
 *
 */
public class Startup implements IStartup {

    private void log(final String msg) {
        final IStatus status = new Status(IStatus.INFO, PropertiesEditorPlugin.PLUGIN_ID, IStatus.OK, msg, null);
        final ILog log = PropertiesEditorPlugin.getDefault().getLog();
        log.log(status);
    }
    
	/**
	 * @see org.eclipse.ui.IStartup#earlyStartup()
	 */
	public void earlyStartup() {
	    IPreferenceStore preference = PropertiesEditorPlugin.getDefault().getPreferenceStore();
	    final String loadOnStartup = preference.getString(PropertiesPreference.P_LOAD_ALL_PROPERTY_ON_STARTUP);
	    log(PropertiesPreference.P_LOAD_ALL_PROPERTY_ON_STARTUP+":"+loadOnStartup +".  You may disable/enable on ( Preferences-> PropertiesEditor )");
	    IWorkspace workspace = PropertiesEditorPlugin.getWorkspace();
	    if(!Boolean.parseBoolean(loadOnStartup)){
	        log(PropertiesPreference.P_LOAD_ALL_PROPERTY_ON_STARTUP+" is diabled!");
	    } else { 
	         ProjectProperties.getInstance().loadAllProperty(workspace);
	    }
	    final boolean loadOnChange =  preference.getBoolean(PropertiesPreference.P_LOAD_PROPERTY_ON_PROJECT_CHANGE);
	    log(PropertiesPreference.P_LOAD_PROPERTY_ON_PROJECT_CHANGE+":"+loadOnChange+" You may disable/enable on ( Preferences-> PropertiesEditor )");
        if (!loadOnChange) {
	         return;
	    }
		workspace.addResourceChangeListener(new IResourceChangeListener() {
			public void resourceChanged(IResourceChangeEvent event) {
				ProjectProperties pp = ProjectProperties.getInstance();
				
				IResourceDelta delta = event.getDelta();

				ResourceChangeList rcs = getEventType(delta);
				if (rcs.size() == 0) {
					return;
				}
				Iterator ite = rcs.iterator();
				while (ite.hasNext()) {
					ResourceChange rc = (ResourceChange)ite.next();
					switch (rc.getType()) {
					case ResourceChange.PROJECT_ADD:
						pp.loadProjectProperties(rc.getProject());
						break;
					case ResourceChange.PROJECT_DELETE:
						pp.deleteProjectProperties(rc.getProject());
						break;
					case ResourceChange.PROJECT_MOVE_TO:
						pp.loadProjectProperties(rc.getProject());
						break;
					case ResourceChange.PROJECT_MOVE_FROM:
						pp.deleteProjectProperties(rc.getProject());
						break;
					case ResourceChange.PROJECT_OPEN:
						pp.loadProjectProperties(rc.getProject());
						break;
					case ResourceChange.PROJECT_CLOSE:
						pp.deleteProjectProperties(rc.getProject());
						break;
					case ResourceChange.PROPERTIES_CHANGE:
						pp.loadProjectProperties(rc.getProject());
						break;
					}
				}
			}
			
			private ResourceChangeList getEventType(IResourceDelta delta) {
				ResourceChangeList resourceChangeList = new ResourceChangeList();

				if (delta == null) return resourceChangeList;

				final Pattern ignorePathPattern = ProjectProperties.getIgnorePathPattern();
				int kind = delta.getKind();
				int flags = delta.getFlags();
				if (kind == IResourceDelta.CHANGED
						&& ((flags & IResourceDelta.OPEN) != 0)) {
					IProject project = (IProject)delta.getResource();
					if (project.isOpen()) {
						resourceChangeList.add(new ResourceChange(ResourceChange.PROJECT_OPEN, project));
					} else {
						resourceChangeList.add(new ResourceChange(ResourceChange.PROJECT_CLOSE, project));
					}
				} else if (delta.getResource() instanceof IProject) {
					IProject project = (IProject)delta.getResource();
					if (kind == IResourceDelta.ADDED) {
						resourceChangeList.add(new ResourceChange(ResourceChange.PROJECT_ADD, project));
					} else if (kind == IResourceDelta.REMOVED) {
						resourceChangeList.add(new ResourceChange(ResourceChange.PROJECT_DELETE, project));
					} else if (kind == IResourceDelta.MOVED_TO) {
						resourceChangeList.add(new ResourceChange(ResourceChange.PROJECT_MOVE_TO, project));
					} else if (kind == IResourceDelta.MOVED_FROM) {
						resourceChangeList.add(new ResourceChange(ResourceChange.PROJECT_MOVE_FROM, project));
					}
				} else if (delta.getResource() instanceof IFile) {
					IFile file = (IFile)delta.getResource();
					String ext;
					if (ignorePathPattern != null && ignorePathPattern.matcher(file.getProjectRelativePath().toString()).find()) {
					    ext = null;
					} else {
					    ext = file.getFileExtension();
					}
					if (ext != null && ext.equals("properties")) { //$NON-NLS-1$
						IJavaProject jProject = JavaCore.create(file.getProject());
						IPath outputPath = null;
						try {
							outputPath = jProject.getOutputLocation();
						} catch (JavaModelException e) {
						}
						if (outputPath == null || outputPath.matchingFirstSegments(file.getFullPath()) != outputPath.segmentCount()) {
							resourceChangeList.add(new ResourceChange(ResourceChange.PROPERTIES_CHANGE, delta.getResource().getProject()));
						}
					}
				}

				IResourceDelta[] deltas = delta.getAffectedChildren();
				for (int i = 0; i < deltas.length; i++) {
					ResourceChangeList rcs = getEventType(deltas[i]);
					resourceChangeList.addAll(rcs);
				}
				return resourceChangeList;
			}

		});
	}

}
