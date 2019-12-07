/**
 * 
 */
package jp.gr.java_conf.ussiy.app.propedit.eclipse.plugin.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import jp.gr.java_conf.ussiy.app.propedit.eclipse.plugin.PropertiesEditorPlugin;
import jp.gr.java_conf.ussiy.app.propedit.eclipse.plugin.preference.PropertiesPreference;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

/**
 *
 */
public class ProjectProperties {
	
	private static ProjectProperties instance = null;
	
	private Map propertyMap = new HashMap();

	private ProjectProperties() {
	}
	
	public static ProjectProperties getInstance() {
		if (ProjectProperties.instance == null) {
			ProjectProperties.instance = new ProjectProperties();
		}
		return ProjectProperties.instance;
	}
	
	public void loadAllProperty(IWorkspace workspace) {
		IProject[] projects = workspace.getRoot().getProjects();
		for (int i = 0; i < projects.length; i++) {
			IProject project = projects[i];
			if (project.isOpen()) {
				loadProjectProperties(project);
			}
		}
	}

	public void deleteProjectProperties(IProject project) {
		log("properties removing for project '" + project.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$
		this.propertyMap.remove(project);
	}
	
	/**
	 * @param project
	 */
	public void loadProjectProperties(IProject project) {
		log("properties loading for project '" + project.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$
		if (!project.isOpen()) {
			this.propertyMap.remove(project);
			return;
		}
		final Pattern ignorePathPattern = getIgnorePathPattern();
		IJavaProject jProject = JavaCore.create(project);
		IPath outputPath = null;
		try {
			outputPath = jProject.getOutputLocation();
		} catch (JavaModelException e) {
		}
		IFile[] pFiles = PropertiesFileUtil.findFileExt(project, outputPath, "properties", ignorePathPattern); //$NON-NLS-1$
		Map list = new HashMap();
		for (int j = 0; j < pFiles.length; j++) {
			log("loading file '" + pFiles[j].getFullPath() + "'"); //$NON-NLS-1$ //$NON-NLS-2$
			Properties prop = new Properties();
			InputStream is = null;
			try {
				is = pFiles[j].getContents();
				prop.load(is);
			} catch (IOException e) {
				IStatus status = new Status(IStatus.ERROR, PropertiesEditorPlugin.PLUGIN_ID, IStatus.OK, e.getMessage(), e);
				ILog log = PropertiesEditorPlugin.getDefault().getLog();
				log.log(status);
			} catch (CoreException e) {
				IStatus status = new Status(IStatus.ERROR, PropertiesEditorPlugin.PLUGIN_ID, IStatus.OK, e.getMessage(), e);
				ILog log = PropertiesEditorPlugin.getDefault().getLog();
				log.log(status);
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (IOException e) {
					}
					is = null;
				}
			}
			list.put(pFiles[j], prop);
		}
		propertyMap.put(project, list);
	}
        public static Pattern getIgnorePathPattern() {
            String ignorePathStr = PropertiesEditorPlugin.getDefault().getPreferenceStore().getString(PropertiesPreference.P_IGNORE_PATH_PATTERN);
            final Pattern ignorePathPattern = ignorePathStr == null || (ignorePathStr = ignorePathStr.trim()).isEmpty() ? null 
                : Pattern.compile(ignorePathStr,Pattern.CASE_INSENSITIVE);
            return ignorePathPattern;
        }
	
	public Properties getProperty(IProject project) {
		Properties prop = new Properties();
		Map properties = getProjectPropertiesMap(project);
		Iterator ite = properties.keySet().iterator();
		while (ite.hasNext()) {
			IFile file = (IFile)ite.next();
			Properties p = (Properties)properties.get(file);
			prop.putAll(p);
		}
		return prop;
	}
	
	public Map getProperty(IProject project, String key) {
		Map list = new HashMap();
		Map properties = getProjectPropertiesMap(project);
		Iterator ite = properties.keySet().iterator();
		while (ite.hasNext()) {
			IFile file = (IFile)ite.next();
			Properties p = (Properties)properties.get(file);
			if (p.containsKey(key)) {
				list.put(file, p);
			}
		}
		return list;
	}

    protected Map getProjectPropertiesMap(final IProject project) {
        Map properties = (Map)propertyMap.get(project);
        if( properties == null){
            properties = new HashMap();
            propertyMap.put(project, properties);
        }
        return properties;
    }
	
	private void log(String msg) {
		IStatus status = new Status(IStatus.INFO, PropertiesEditorPlugin.PLUGIN_ID, IStatus.OK, msg, null);
		ILog log = PropertiesEditorPlugin.getDefault().getLog();
		log.log(status);
	}

}
