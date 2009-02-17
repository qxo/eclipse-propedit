package jp.gr.java_conf.ussiy.app.propedit.eclipse.plugin.action;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import jp.gr.java_conf.ussiy.app.propedit.eclipse.plugin.PropertiesEditorPlugin;
import jp.gr.java_conf.ussiy.app.propedit.eclipse.plugin.editors.PropertiesEditor;
import jp.gr.java_conf.ussiy.app.propedit.eclipse.plugin.preference.PropertiesPreference;
import jp.gr.java_conf.ussiy.app.propedit.util.EncodeChanger;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;

public class ShowUnicodeEscAction implements IEditorActionDelegate {
	private PropertiesEditor textEditor;

	/**
	 * @see org.eclipse.ui.IEditorActionDelegate#setActiveEditor(org.eclipse.jface.action.IAction, org.eclipse.ui.IEditorPart)
	 */
	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		if (targetEditor instanceof PropertiesEditor) {
			textEditor = (PropertiesEditor)targetEditor;
		}
	}

	/**
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		Shell shell = new Shell(textEditor.getSite().getShell(), SWT.SHELL_TRIM);
		URL url = PropertiesEditorPlugin.getDefault().getBundle().getEntry("/"); //$NON-NLS-1$
		String path = "icons/previewPage.gif"; //$NON-NLS-1$
		ImageDescriptor descriptor = null;
		try {
			descriptor = ImageDescriptor.createFromURL(new URL(url, path));
		} catch (MalformedURLException e) {
			descriptor = ImageDescriptor.getMissingImageDescriptor();
		}
		Image image = descriptor.createImage();
		shell.setImage(image);
		shell.setSize(600, 400);
		shell.setLayout(new FillLayout());

		// editor
		StyledText text = new StyledText(shell, SWT.H_SCROLL | SWT.V_SCROLL);
		text.setEditable(false);
		Display display = Display.getCurrent();
		RGB rgb = new RGB(240, 240, 240);
		Color color = new Color(display, rgb);
		text.setBackground(color);
		
		// set font
		Font font = textEditor.getFont();
		text.setFont(font);
		
		// set title
		shell.setText(textEditor.getTitle());
		
		// set unicode text
		String editorText = textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput()).get();
		if (PropertiesEditorPlugin.getDefault().getPreferenceStore().getBoolean(PropertiesPreference.P_NOT_CONVERT_COMMENT)) {
			try {
				text.setText(EncodeChanger.unicode2UnicodeEscWithoutComment(editorText));
			} catch (IOException e) {
			}
		} else {
			text.setText(EncodeChanger.unicode2UnicodeEsc(editorText));
		}

		// window open
		shell.open();
	}

	/**
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

}
