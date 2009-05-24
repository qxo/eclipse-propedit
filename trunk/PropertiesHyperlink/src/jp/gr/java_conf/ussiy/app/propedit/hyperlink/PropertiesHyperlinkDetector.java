package jp.gr.java_conf.ussiy.app.propedit.hyperlink;

import jp.gr.java_conf.ussiy.app.propedit.eclipse.plugin.editors.detector.IHyperlinkDetector;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.internal.ui.propertiesfileeditor.PropertyKeyHyperlink;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

public class PropertiesHyperlinkDetector implements IHyperlinkDetector {

	private ITextEditor editor;
	
	public void setTextEditor(ITextEditor editor) {
		this.editor = editor;
	}

	public IHyperlink[] detectHyperlinks(ITextViewer viewer,
			IRegion region, boolean canMulti) {

		try {
			IStorageEditorInput editorInput = (IStorageEditorInput) editor
					.getEditorInput();
			IDocument document = editor.getDocumentProvider().getDocument(
					editorInput);

			int lineNum = document.getLineOfOffset(region.getOffset());
			int lineTopIdx = document.getLineOffset(lineNum);
			int endPos = lineTopIdx;
			if (endPos - 2 >= 0) {
				char c1 = document.getChar(endPos - 2);
				char c2 = document.getChar(endPos - 1);
				if (c1 == '\\' && (c2 == '\r' || c2 == '\n')) {
					return null;
				}
			}
			if (endPos - 3 >= 0) {
				char c1 = document.getChar(endPos - 3);
				char c2 = document.getChar(endPos - 2);
				char c3 = document.getChar(endPos - 1);
				if (c1 == '\\' && c2 == '\r' && c3 == '\n') {
					return null;
				}
			}
			
			{
				char c = document.getChar(endPos);
				if (c == '#' || c == '!') {
					return null;
				}
			}

			boolean escapeFlg = false;
			boolean spaceFlg = true;
			char beforeChar = 0;
			char beforeBeforeChar = 0;
			while (endPos < document.getLength()) {
				char c = document.getChar(endPos);
				if (c == '\\') {
					escapeFlg = !escapeFlg;
					endPos++;
					continue;
				}
				if (c == ' ' || c == '\t') {
					if (spaceFlg) {
						endPos++;
						continue;
					}
				} else {
					spaceFlg = false;
				}
				if (c == '=' || c == '\r' || c == '\n' || c == '\t' || c == ' ' || c == ':') {
					if (!escapeFlg) {
						if (!(beforeBeforeChar == '\\' && beforeChar == '\r' && c == '\n')) {
							break;
						}
					} else {
						escapeFlg = !escapeFlg;
					}
				} else {
					escapeFlg = false;
				}
				beforeBeforeChar = beforeChar;
				beforeChar = c;
				endPos++;
			}
			int length = endPos - lineTopIdx;

			String tmp = document.get(lineTopIdx, length);
			String key = tmp.trim();
			int offset = tmp.indexOf(key);

			IHyperlink[] hyperlinks = new PropertyKeyHyperlink[] { new PropertyKeyHyperlink(
					new Region(lineTopIdx + offset, key.length()),
					key, editor), };
			
			return hyperlinks;

		} catch (BadLocationException e) {
			IStatus status = new Status(IStatus.ERROR,
					Activator.PLUGIN_ID, IStatus.OK, e.getMessage(), e);
			ILog log = Activator.getDefault().getLog();
			log.log(status);
			ErrorDialog
					.openError(
							null,
							Messages.getString("PropertiesHyperlinkDetector.1"), Messages.getString("PropertiesHyperlinkDetector.2"), status); //$NON-NLS-1$ //$NON-NLS-2$
			return null;
		}
	}

}
