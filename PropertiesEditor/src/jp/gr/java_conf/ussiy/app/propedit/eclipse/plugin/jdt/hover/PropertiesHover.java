/**
 * 
 */
package jp.gr.java_conf.ussiy.app.propedit.eclipse.plugin.jdt.hover;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

import jp.gr.java_conf.ussiy.app.propedit.eclipse.plugin.PropertiesEditorPlugin;
import jp.gr.java_conf.ussiy.app.propedit.eclipse.plugin.resources.Messages;
import jp.gr.java_conf.ussiy.app.propedit.eclipse.plugin.util.PropertiesFileUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.text.java.hover.IJavaEditorTextHover;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;

/**
 *
 */
public class PropertiesHover implements IJavaEditorTextHover {
	
	private IEditorPart editorPart;
	
	/**
	 * @see org.eclipse.jdt.ui.text.java.hover.IJavaEditorTextHover#setEditor(org.eclipse.ui.IEditorPart)
	 */
	public void setEditor(IEditorPart editorPart) {
		this.editorPart = editorPart;
	}

	/**
	 * @see org.eclipse.jface.text.ITextHover#getHoverInfo(org.eclipse.jface.text.ITextViewer, org.eclipse.jface.text.IRegion)
	 */
	public String getHoverInfo(ITextViewer textViewer, IRegion region) {
		IDocument document = textViewer.getDocument();
		int offset = region.getOffset();
		try {
			int lineNum = document.getLineOfOffset(offset);
			int lineOffset = document.getLineOffset(lineNum);
			int lineLength = document.getLineLength(lineNum);
			String source = document.get();
			int startIdx = -1;
			int tmp = offset - 1;
			while (tmp >= lineOffset) {
				tmp = source.lastIndexOf("\"", tmp); //$NON-NLS-1$
				if (tmp < lineOffset) {
					startIdx = -1;
					break;
				}
				if (tmp > 0) {
					if (document.getChar(tmp - 1) == '\\') {
						continue;
					} else {
						startIdx = tmp + 1;
						break;
					}
				} else if (tmp == 0) {
					startIdx = 0 + 1;
					break;
				} else {
					startIdx = -1;
					break;
				}
			}
			tmp = offset + 1;
			int endIdx = -1;
			while (tmp < lineOffset + lineLength) {
				tmp = source.indexOf("\"", tmp); //$NON-NLS-1$
				if (tmp > lineOffset + lineLength) {
					endIdx = -1;
					break;
				}
				if (tmp > 0) {
					if (document.getChar(tmp - 1) == '\\') {
						continue;
					} else {
						endIdx = tmp;
						break;
					}
				} else if (tmp == 0) {
					endIdx = 0;
					break;
				} else {
					endIdx = -1;
				}
			}
			
			if (startIdx == -1 || endIdx == -1 || startIdx == endIdx) {
				return null;
			}
			
			String key =  textViewer.getDocument().get(startIdx, endIdx - startIdx);
			
			return getPropertyValue(key);

		} catch (BadLocationException e) {
			IStatus status = new Status(IStatus.ERROR, PropertiesEditorPlugin.PLUGIN_ID, 0, e.getMessage(), e);
			ILog log = PropertiesEditorPlugin.getDefault().getLog();
			log.log(status);
			return null;
		}
	}
	
	private String getPropertyValue(String targetKey) {
		if (targetKey == null || targetKey.equals("")) { //$NON-NLS-1$
			return ""; //$NON-NLS-1$
		}
		
		IEditorInput editorInput = this.editorPart.getEditorInput();
		
		if (editorInput instanceof IFileEditorInput) {
			
			IFileEditorInput fEditorInput = (IFileEditorInput)editorInput;
			IProject project = fEditorInput.getFile().getProject();
			IJavaProject jProject = JavaCore.create(project);
		
			IPath outputPath = null;
			try {
				outputPath = jProject.getOutputLocation();
			} catch (JavaModelException e) {
				IStatus status = new Status(IStatus.ERROR, PropertiesEditorPlugin.PLUGIN_ID, 0, e.getMessage(), e);
				ILog log = PropertiesEditorPlugin.getDefault().getLog();
				log.log(status);
			}
	
			IFile[] pFiles = PropertiesFileUtil.findFileExt(project, outputPath, "properties"); //$NON-NLS-1$
			
			Properties list = new Properties();
			for (int i = 0; i < pFiles.length; i++) {
				Properties prop = new Properties();
				try {
					prop.load(pFiles[i].getContents());
				} catch (IOException e) {
					IStatus status = new Status(IStatus.ERROR, PropertiesEditorPlugin.PLUGIN_ID, 0, e.getMessage(), e);
					ILog log = PropertiesEditorPlugin.getDefault().getLog();
					log.log(status);
				} catch (CoreException e) {
					IStatus status = new Status(IStatus.ERROR, PropertiesEditorPlugin.PLUGIN_ID, 0, e.getMessage(), e);
					ILog log = PropertiesEditorPlugin.getDefault().getLog();
					log.log(status);
				}
				
				if (prop.containsKey(targetKey)) {
					list.put(pFiles[i].getFullPath().toPortableString(), prop.getProperty(targetKey));
				}
			}
			
			if (list.isEmpty()) {
				return null;
			}
			
			Enumeration enu = list.keys();
			StringBuffer buf = new StringBuffer();
			buf.append("&lt;").append(Messages.getString("eclipse.propertieseditor.hover.key")).append(":").append(targetKey).append("&gt;<br/>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			while (enu.hasMoreElements()) {
				String path = (String)enu.nextElement();
				String value = (String)list.get(path);
				value = value.replaceAll("\r\n", "<br/>"); //$NON-NLS-1$ //$NON-NLS-2$
				value = value.replaceAll("\n", "<br/>"); //$NON-NLS-1$ //$NON-NLS-2$
				value = value.replaceAll("\r", "<br/>"); //$NON-NLS-1$ //$NON-NLS-2$
				buf.append("&lt;").append(Messages.getString("eclipse.propertieseditor.hover.file")).append(":").append(path).append("&gt;<br/>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				buf.append(value);
				buf.append("<br/>"); //$NON-NLS-1$
			}
			return buf.toString();
		} else {
			return null;
		}
	}
	
	/**
	 * @see org.eclipse.jface.text.ITextHover#getHoverRegion(org.eclipse.jface.text.ITextViewer, int)
	 */
	public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
		return null;
	}

}
