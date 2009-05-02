package jp.gr.java_conf.ussiy.app.propedit.eclipse.plugin.editors;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import jp.gr.java_conf.ussiy.app.propedit.eclipse.plugin.PropertiesEditorPlugin;
import jp.gr.java_conf.ussiy.app.propedit.eclipse.plugin.preference.PropertiesEditorPreference;
import jp.gr.java_conf.ussiy.app.propedit.eclipse.plugin.preference.PropertiesPreference;
import jp.gr.java_conf.ussiy.app.propedit.eclipse.plugin.property.PropertyUtil;
import jp.gr.java_conf.ussiy.app.propedit.eclipse.plugin.resources.Messages;
import jp.gr.java_conf.ussiy.app.propedit.util.EncodeChanger;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.ide.IGotoMarker;
import org.eclipse.ui.part.MultiPageEditorPart;

/**
 * An example showing how to create a multi-page editor. This example has 2 pages:
 */
public class MultiPagePropertiesEditor extends MultiPageEditorPart implements IGotoMarker {

	/** The text editor used in page 0. */
	private PropertiesEditor editor;

	/** The text widget used in page 1. */
	private StyledText text;

	/**
	 * Creates a multi-page editor example.
	 */
	public MultiPagePropertiesEditor() {
		super();
	}

	/**
	 * Creates page 0 of the multi-page editor, which contains a text editor.
	 */
	void createPage0() {
		URL url = PropertiesEditorPlugin.getDefault().getBundle().getEntry("/"); //$NON-NLS-1$
		String path = "icons/editPage.gif"; //$NON-NLS-1$
		ImageDescriptor descriptor = null;
		try {
			descriptor = ImageDescriptor.createFromURL(new URL(url, path));
		} catch (MalformedURLException e) {
			descriptor = ImageDescriptor.getMissingImageDescriptor();
		}
		Image image = descriptor.createImage();
		try {
			editor = new PropertiesEditor();
			int index = addPage(editor, getEditorInput());
			setPageText(index, Messages.getString("eclipse.tabname.edit")); //$NON-NLS-1$
			setPartName(editor.getTitle());
			setPageImage(index, image);
			IPreferenceStore pStore = PropertiesEditorPlugin.getDefault().getPreferenceStore();
			RGB rgb = PreferenceConverter.getColor(pStore, PropertiesEditorPreference.P_BACKGROUND_COLOR);
			Color color = new ColorManager().getColor(rgb);
			editor.setBackground(color);
		} catch (PartInitException e) {
			ErrorDialog.openError(getSite().getShell(), "Error creating nested text editor", null, e.getStatus()); //$NON-NLS-1$
		}
	}

	/**
	 * Creates page 1 of the multi-page editor.
	 */
	void createPage1() {
		URL url = PropertiesEditorPlugin.getDefault().getBundle().getEntry("/"); //$NON-NLS-1$
		String path = "icons/previewPage.gif"; //$NON-NLS-1$
		ImageDescriptor descriptor = null;
		try {
			descriptor = ImageDescriptor.createFromURL(new URL(url, path));
		} catch (MalformedURLException e) {
			descriptor = ImageDescriptor.getMissingImageDescriptor();
		}
		Image image = descriptor.createImage();

		Composite composite = new Composite(getContainer(), SWT.NONE);
		FillLayout layout = new FillLayout();
		composite.setLayout(layout);
		text = new StyledText(composite, SWT.H_SCROLL | SWT.V_SCROLL);
		text.setEditable(false);
		Display display = Display.getCurrent();
		RGB rgb = new RGB(240, 240, 240);
		Color color = new Color(display, rgb);
		text.setBackground(color);

		int index = addPage(composite);
		setPageText(index, Messages.getString("eclipse.tabname.preview")); //$NON-NLS-1$
		setPageImage(index, image);
	}

	/**
	 * Creates the pages of the multi-page editor.
	 */
	protected void createPages() {

		createPage0();
		createPage1();
		
		IPreferenceStore apiStore = PropertiesEditorPlugin.getDefault().getPreferenceStore();
		
		Composite container = getContainer();
		if (container != null && container instanceof CTabFolder) {
			apiStore.addPropertyChangeListener(
				new IPropertyChangeListener() {
			        public void propertyChange(PropertyChangeEvent event) {
						if (IWorkbenchPreferenceConstants.SHOW_TRADITIONAL_STYLE_TABS.equals(event.getProperty())) {
							setTabStyle();
						}				
			        }
				}
			);
			CTabFolder tabFolder = (CTabFolder)getContainer();

			Display display = Display.getCurrent();
			Color titleForeColor = display.getSystemColor(SWT.COLOR_TITLE_FOREGROUND);
			Color titleBackColor1 = display.getSystemColor(SWT.COLOR_TITLE_BACKGROUND);
			Color titleBackColor2 = display.getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT);
	
			tabFolder.setSelectionForeground(titleForeColor);
			tabFolder.setSelectionBackground(
					new Color[] {titleBackColor1, titleBackColor2},
					new int[] {100},
					true
			);
			setTabStyle();
		}
	}
	
	protected void setTabStyle() {
		IPreferenceStore apiStore = PropertiesEditorPlugin.getDefault().getPreferenceStore();
		boolean simple = apiStore.getBoolean(IWorkbenchPreferenceConstants.SHOW_TRADITIONAL_STYLE_TABS);

		Composite container = getContainer();
		if (container != null && container instanceof CTabFolder) {
			CTabFolder tabFolder = (CTabFolder)getContainer();
			tabFolder.setSimple(simple);
		}
		
	}

	/**
	 * Saves the multi-page editor's document.
	 */
	public void doSave(IProgressMonitor monitor) {

		getEditor(0).doSave(monitor);
	}

	/**
	 * Saves the multi-page editor's document as another file. Also updates the text for page 0's tab, and updates this multi-page editor's input to correspond to the nested editor's.
	 */
	public void doSaveAs() {

		IEditorPart editor = getEditor(0);
		editor.doSaveAs();
		setPageText(0, editor.getTitle());
		setInput(editor.getEditorInput());
	}

	public void gotoMarker(IMarker marker) {

		setActivePage(0);
		IDE.gotoMarker(getEditor(0), marker);
	}

	public void init(IEditorSite site, IEditorInput editorInput) throws PartInitException {

		//        if (!(editorInput instanceof IFileEditorInput)) {
		//            throw new PartInitException(
		//                "Invalid Input: Must be IFileEditorInput");
		//        }
		super.init(site, editorInput);
	}

	public boolean isSaveAsAllowed() {

		return true;
	}

	protected void pageChange(int newPageIndex) {

		super.pageChange(newPageIndex);
		String editorText = editor.getDocumentProvider().getDocument(editor.getEditorInput()).get();
		IProject project = ((IFileEditorInput)editor.getEditorInput()).getFile().getProject();
		if (PropertyUtil.getNotConvertComment(project, PropertiesEditorPlugin.getDefault().getPreferenceStore().getBoolean(PropertiesPreference.P_NOT_CONVERT_COMMENT))) {
			try {
				text.setText(EncodeChanger.unicode2UnicodeEscWithoutComment(editorText));
			} catch (IOException e) {
			}
		} else {
			text.setText(EncodeChanger.unicode2UnicodeEsc(editorText));
		}
	}

	public PropertiesEditor getEditor() {

		return editor;
	}

	/**
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {
		return getEditor().getAdapter(adapter);
	}
	
	/**
	 * @see org.eclipse.ui.IWorkbenchPart#dispose()
	 */
	public void dispose() {
		editor.dispose();
		text.dispose();
		super.dispose();
	}
}