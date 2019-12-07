package jp.gr.java_conf.ussiy.app.propedit.eclipse.plugin.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import jp.gr.java_conf.ussiy.app.propedit.eclipse.plugin.PropertiesEditorPlugin;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class PropertiesFileUtil {

	public static IFile[] findFileExt(IContainer container, IPath excludePath, String extension,final Pattern ignorePathPattern) {
		IResource[] list = null;
		ILog log = PropertiesEditorPlugin.getDefault().getLog();
		try {
			list = container.members();
		} catch (CoreException e) {
			IStatus status = new Status(IStatus.ERROR, PropertiesEditorPlugin.PLUGIN_ID, IStatus.OK, e.getMessage(), e);
			log.log(status);
			return new IFile[0];
		}
		if (list == null) {
			return new IFile[0];
		}
		List fileList = new ArrayList();
		for (int i = 0; i < list.length; i++) {
			IResource res = list[i];
			if (ignorePathPattern != null && ignorePathPattern.matcher(res.getProjectRelativePath().toString()).find()) {
			    continue;
			}
			if (res instanceof IFile) {
				if (extension.equals(res.getFileExtension())) {
				    fileList.add((IFile)res);
				}
			} else 	if (res instanceof IContainer) {
				if (excludePath != null && excludePath.matchingFirstSegments(res.getFullPath()) == excludePath.segmentCount()) {
					continue;
				}
				IFile[] files = findFileExt((IContainer)res, excludePath, extension, ignorePathPattern);
				for (int j = 0; j < files.length; j++) {
					fileList.add(files[j]);
				}
			}
		}
		
		return (IFile[])fileList.toArray(new IFile[0]);
	}

  private static void logInfo(ILog log, Object locationURI) {
    IStatus status = new Status(IStatus.INFO, PropertiesEditorPlugin.PLUGIN_ID,"find:"+locationURI);
    log.log(status);
  }
}
