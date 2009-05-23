/**
 * 
 */
package jp.gr.java_conf.ussiy.app.propedit.eclipse.plugin.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import jp.gr.java_conf.ussiy.app.propedit.eclipse.plugin.PropertiesEditorPlugin;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
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
		log("properties removing '" + project.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$
		this.propertyMap.remove(project);
	}
	
	/**
	 * @param project
	 */
	public void loadProjectProperties(IProject project) {
		log("properties loading '" + project.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$
		if (!project.isOpen()) {
			this.propertyMap.remove(project);
			return;
		}
		IJavaProject jProject = JavaCore.create(project);
		IPath outputPath = null;
		try {
			outputPath = jProject.getOutputLocation();
		} catch (JavaModelException e) {
		}
		IFile[] pFiles = PropertiesFileUtil.findFileExt(project, outputPath, "properties"); //$NON-NLS-1$
		Map list = new HashMap();
		for (int j = 0; j < pFiles.length; j++) {
			Properties prop = new Properties();
			try {
				prop.load(pFiles[j].getContents());
			} catch (IOException e) {
				IStatus status = new Status(IStatus.ERROR, PropertiesEditorPlugin.PLUGIN_ID, IStatus.OK, e.getMessage(), e);
				ILog log = PropertiesEditorPlugin.getDefault().getLog();
				log.log(status);
			} catch (CoreException e) {
				IStatus status = new Status(IStatus.ERROR, PropertiesEditorPlugin.PLUGIN_ID, IStatus.OK, e.getMessage(), e);
				ILog log = PropertiesEditorPlugin.getDefault().getLog();
				log.log(status);
			}
			list.put(pFiles[j], prop);
		}
		propertyMap.put(project, list);
	}
	
	public Properties getProperty(IProject project) {
		Properties prop = new Properties();
		Map properties = (Map)propertyMap.get(project);
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
		Map properties = (Map)propertyMap.get(project);
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
	
	private void log(String msg) {
		IStatus status = new Status(IStatus.INFO, PropertiesEditorPlugin.PLUGIN_ID, msg);
		ILog log = PropertiesEditorPlugin.getDefault().getLog();
		log.log(status);
	}

}
