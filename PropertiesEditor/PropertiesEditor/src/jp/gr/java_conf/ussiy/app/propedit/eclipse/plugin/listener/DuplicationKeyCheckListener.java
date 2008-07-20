package jp.gr.java_conf.ussiy.app.propedit.eclipse.plugin.listener;

import jp.gr.java_conf.ussiy.app.propedit.eclipse.plugin.checker.CheckAndMarkDuplicateKey;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IFileEditorInput;

public class DuplicationKeyCheckListener implements IPropertiesDocumentListener {

	/**
	 * @see jp.gr.java_conf.ussiy.app.propedit.eclipse.plugin.listener.IPropertiesDocumentListener#afterConvertAtLoadingDocument(java.lang.String, java.lang.Object)
	 */
	public void afterConvertAtLoadingDocument(String content, Object element) {
		if (element instanceof IFileEditorInput) {
			CheckAndMarkDuplicateKey camd = new CheckAndMarkDuplicateKey();
			try {
				camd.checkAndMarkDuplicateKeyInString(content, (IFileEditorInput)element);
			} catch(CoreException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @see jp.gr.java_conf.ussiy.app.propedit.eclipse.plugin.listener.IPropertiesDocumentListener#afterUnicodeConvertAtSavingDocument(org.eclipse.core.runtime.IProgressMonitor, java.lang.String, java.lang.Object)
	 */
	public void afterUnicodeConvertAtSavingDocument(IProgressMonitor monitor,
			String content, Object element) {
	}
	
	/**
	 * @see jp.gr.java_conf.ussiy.app.propedit.eclipse.plugin.listener.IPropertiesDocumentListener#beforeConvertAtLoadingDocument(java.lang.String, java.lang.Object)
	 */
	public void beforeConvertAtLoadingDocument(String content, Object element) {
	}

	/**
	 * @see jp.gr.java_conf.ussiy.app.propedit.eclipse.plugin.listener.IPropertiesDocumentListener#beforeUnicodeConvertAtSavingDocument(org.eclipse.core.runtime.IProgressMonitor, java.lang.String, java.lang.Object)
	 */
	public void beforeUnicodeConvertAtSavingDocument(IProgressMonitor monitor,
			String content, Object element) {
		if (element instanceof IFileEditorInput) {
			CheckAndMarkDuplicateKey camd = new CheckAndMarkDuplicateKey();
			try {
				camd.checkAndMarkDuplicateKeyInString(content, (IFileEditorInput)element);
			} catch(CoreException e) {
				e.printStackTrace();
			}
		}
	}

}
