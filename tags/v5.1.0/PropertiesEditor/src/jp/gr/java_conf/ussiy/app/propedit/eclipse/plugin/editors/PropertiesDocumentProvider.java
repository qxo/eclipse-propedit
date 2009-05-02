package jp.gr.java_conf.ussiy.app.propedit.eclipse.plugin.editors;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import jp.gr.java_conf.ussiy.app.propedit.eclipse.plugin.PropertiesEditorPlugin;
import jp.gr.java_conf.ussiy.app.propedit.eclipse.plugin.listener.IPropertiesDocumentListener;
import jp.gr.java_conf.ussiy.app.propedit.eclipse.plugin.preference.PropertiesPreference;
import jp.gr.java_conf.ussiy.app.propedit.eclipse.plugin.property.PropertyUtil;
import jp.gr.java_conf.ussiy.app.propedit.util.EncodeChanger;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.FileDocumentProvider;
import org.eclipse.ui.texteditor.ResourceMarkerAnnotationModel;

public class PropertiesDocumentProvider extends FileDocumentProvider {
	private static final String EXTENSION_POINT = "jp.gr.java_conf.ussiy.app.propedit.listeners"; //$NON-NLS-1$

	protected List computePropertiesDocumentListeners() {
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint extensionPoint = registry.getExtensionPoint(EXTENSION_POINT);
		IExtension[] extensions = extensionPoint.getExtensions();
		ArrayList results = new ArrayList();
		for (int i = 0; i < extensions.length; i++) {
			IConfigurationElement[] elements = extensions[i].getConfigurationElements();
			for (int j = 0; j < elements.length; j++) {
				try {
					Object listener = elements[j].createExecutableExtension("class"); //$NON-NLS-1$
					if (listener instanceof IPropertiesDocumentListener) {
						results.add(listener);
					}
				} catch(CoreException e) {
					e.printStackTrace();
				}
			}
		}
		
		return results;
	}
	
	protected IDocument createDocument(Object element) throws CoreException {

		String readEncode = PropertiesEditorPlugin.getDefault().getPreferenceStore().getString(PropertiesPreference.P_ENCODE);
		if (readEncode == null || readEncode.equals("")) { //$NON-NLS-1$
			readEncode = getDefaultEncoding();
		}
		IDocument document = null;
		if (element instanceof IEditorInput) {
			document = createEmptyDocument();
			IProject project = ((IFileEditorInput)element).getFile().getProject();
			readEncode = PropertyUtil.getEncode(project, readEncode);
			if (!setDocumentContent(document, (IEditorInput) element, readEncode)) {
				document = null;
			}
		}
		
		List listeners = computePropertiesDocumentListeners();
		for (int i = 0; i < listeners.size(); i++) {
			IPropertiesDocumentListener listener = (IPropertiesDocumentListener)listeners.get(i);
			try {
				listener.beforeConvertAtLoadingDocument(document.get(), element);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		if (document != null) {
			document.set(EncodeChanger.unicodeEsc2Unicode(document.get()));

			for (int i = 0; i < listeners.size(); i++) {
				IPropertiesDocumentListener listener = (IPropertiesDocumentListener)listeners.get(i);
				try {
					listener.afterConvertAtLoadingDocument(document.get(), element);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}

			IDocumentPartitioner partitioner = new FastPartitioner(new PropertiesPartitionScanner(), new String[] { IDocument.DEFAULT_CONTENT_TYPE, PropertiesPartitionScanner.PROPERTIES_COMMENT, PropertiesPartitionScanner.PROPERTIES_SEPARATOR, PropertiesPartitionScanner.PROPERTIES_VALUE });
			partitioner.connect(document);
			document.setDocumentPartitioner(partitioner);
		}
		return document;
	}

	protected void doSaveDocument(IProgressMonitor monitor, Object element, IDocument document, boolean overwrite) throws CoreException {

		if (element instanceof IFileEditorInput) {

			IFileEditorInput input = (IFileEditorInput) element;

			try {
				IProject project = input.getFile().getProject();
				String encoding = PropertyUtil.getEncode(project, PropertiesEditorPlugin.getDefault().getPreferenceStore().getString(PropertiesPreference.P_ENCODE));
				if (encoding == null || encoding.equals("")) { //$NON-NLS-1$
					encoding = getDefaultEncoding();
				}

				List listeners = computePropertiesDocumentListeners();
				for (int i = 0; i < listeners.size(); i++) {
					IPropertiesDocumentListener listener = (IPropertiesDocumentListener)listeners.get(i);
					try {
						listener.beforeUnicodeConvertAtSavingDocument(monitor, document.get(), input);
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
				
				String uniEscStr = null;
				if (PropertyUtil.getNotAllConvert(project, PropertiesEditorPlugin.getDefault().getPreferenceStore().getBoolean(PropertiesPreference.P_NOT_ALL_CONVERT))) {
					uniEscStr = document.get();
				} else if (PropertyUtil.getNotConvertComment(project, PropertiesEditorPlugin.getDefault().getPreferenceStore().getBoolean(PropertiesPreference.P_NOT_CONVERT_COMMENT))) {
					uniEscStr = EncodeChanger.unicode2UnicodeEscWithoutComment(document.get());
				} else {
					uniEscStr = EncodeChanger.unicode2UnicodeEsc(document.get());
				}
				
				for (int i = 0; i < listeners.size(); i++) {
					IPropertiesDocumentListener listener = (IPropertiesDocumentListener)listeners.get(i);
					try {
						listener.afterUnicodeConvertAtSavingDocument(monitor, uniEscStr, input);
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
				
				InputStream stream = new ByteArrayInputStream(uniEscStr.toString().getBytes(encoding));
				IFile file = input.getFile();

				if (file.exists()) {

					FileInfo info = (FileInfo) getElementInfo(element);

					if (info != null && !overwrite) {
						checkSynchronizationState(info.fModificationStamp, file);

						// inform about the upcoming content change
					}
					fireElementStateChanging(element);
					try {
						file.setContents(stream, overwrite, true, monitor);
					} catch (CoreException x) {
						// inform about failure
						fireElementStateChangeFailed(element);
						throw x;
					} catch (RuntimeException x) {
						// inform about failure
						fireElementStateChangeFailed(element);
						throw x;
					}

					// If here, the editor state will be flipped to "not dirty".
					// Thus, the state changing flag will be reset.

					if (info != null) {

						ResourceMarkerAnnotationModel model = (ResourceMarkerAnnotationModel) info.fModel;
						model.updateMarkers(info.fDocument);

						info.fModificationStamp = computeModificationStamp(file);
					}

				} else {
					super.doSaveDocument(monitor, element, document, overwrite);
					return;
				}

			} catch (IOException x) {
				IStatus s = new Status(IStatus.ERROR, PlatformUI.PLUGIN_ID, IStatus.OK, x.getMessage(), x);
				throw new CoreException(s);
			}

		} else {
			super.doSaveDocument(monitor, element, document, overwrite);
		}
	}
}