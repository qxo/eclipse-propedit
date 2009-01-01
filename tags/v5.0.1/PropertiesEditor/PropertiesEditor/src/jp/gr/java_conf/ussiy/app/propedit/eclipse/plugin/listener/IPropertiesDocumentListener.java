package jp.gr.java_conf.ussiy.app.propedit.eclipse.plugin.listener;

import org.eclipse.core.runtime.IProgressMonitor;

public interface IPropertiesDocumentListener {

	public void beforeUnicodeConvertAtSavingDocument(IProgressMonitor monitor, String content, Object element);
	
	public void afterUnicodeConvertAtSavingDocument(IProgressMonitor monitor, String content, Object element);
	
	public void beforeConvertAtLoadingDocument(String content, Object element);
	
	public void afterConvertAtLoadingDocument(String content, Object element);
	
}
