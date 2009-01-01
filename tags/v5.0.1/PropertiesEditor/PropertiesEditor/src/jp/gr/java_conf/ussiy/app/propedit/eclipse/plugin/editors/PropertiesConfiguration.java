package jp.gr.java_conf.ussiy.app.propedit.eclipse.plugin.editors;

import jp.gr.java_conf.ussiy.app.propedit.eclipse.plugin.PropertiesEditorPlugin;
import jp.gr.java_conf.ussiy.app.propedit.eclipse.plugin.preference.PropertiesEditorPreference;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.reconciler.MonoReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

public class PropertiesConfiguration extends SourceViewerConfiguration {

	private PropertiesDoubleClickStrategy doubleClickStrategy;

	private ColorManager colorManager;
	
	private PropertiesEditor editor;

	public PropertiesConfiguration(ColorManager colorManager, PropertiesEditor editor) {

		this.colorManager = colorManager;
		this.editor = editor;
	}

	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {

		return new String[] { IDocument.DEFAULT_CONTENT_TYPE, PropertiesPartitionScanner.PROPERTIES_COMMENT, PropertiesPartitionScanner.PROPERTIES_SEPARATOR, PropertiesPartitionScanner.PROPERTIES_VALUE };
	}

	public ITextDoubleClickStrategy getDoubleClickStrategy(ISourceViewer sourceViewer, String contentType) {

		if (doubleClickStrategy == null) {
			doubleClickStrategy = new PropertiesDoubleClickStrategy();
		}
		return doubleClickStrategy;
	}

	public IPreferenceStore getPreferenceStore() {

		return PropertiesEditorPlugin.getDefault().getPreferenceStore();
	}

	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {

		IPreferenceStore pStore = getPreferenceStore();

		PresentationReconciler reconciler = new PresentationReconciler();

		RGB rgb = PreferenceConverter.getColor(pStore, PropertiesEditorPreference.P_COMMENT_COLOR);
		Color color = colorManager.getColor(rgb);
		TextAttribute attr = new TextAttribute(color);
		NonRuleBasedDamagerRepairer ndr = new NonRuleBasedDamagerRepairer(attr);
		reconciler.setDamager(ndr, PropertiesPartitionScanner.PROPERTIES_COMMENT);
		reconciler.setRepairer(ndr, PropertiesPartitionScanner.PROPERTIES_COMMENT);

		rgb = PreferenceConverter.getColor(pStore, PropertiesEditorPreference.P_SEPARATOR_COLOR);
		color = colorManager.getColor(rgb);
		attr = new TextAttribute(color);
		ndr = new NonRuleBasedDamagerRepairer(attr);
		reconciler.setDamager(ndr, PropertiesPartitionScanner.PROPERTIES_SEPARATOR);
		reconciler.setRepairer(ndr, PropertiesPartitionScanner.PROPERTIES_SEPARATOR);

		rgb = PreferenceConverter.getColor(pStore, PropertiesEditorPreference.P_VALUE_COLOR);
		color = colorManager.getColor(rgb);
		attr = new TextAttribute(color);
		ndr = new NonRuleBasedDamagerRepairer(attr);
		reconciler.setDamager(ndr, PropertiesPartitionScanner.PROPERTIES_VALUE);
		reconciler.setRepairer(ndr, PropertiesPartitionScanner.PROPERTIES_VALUE);

		PropertiesScanner scanner = new PropertiesScanner(colorManager, pStore);
		DefaultDamagerRepairer dr = new DefaultDamagerRepairer(scanner);
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		return reconciler;
	}

	/**
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getReconciler(org.eclipse.jface.text.source.ISourceViewer)
	 */
	public IReconciler getReconciler(ISourceViewer sourceViewer) {
        PropertiesReconcilingStrategy strategy = new PropertiesReconcilingStrategy();
        strategy.setEditor(editor);
        
        MonoReconciler reconciler = new MonoReconciler(strategy,false);
        
        return reconciler;
	}
	
}