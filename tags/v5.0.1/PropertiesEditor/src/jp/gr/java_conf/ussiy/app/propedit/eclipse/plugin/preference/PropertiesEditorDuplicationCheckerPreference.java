package jp.gr.java_conf.ussiy.app.propedit.eclipse.plugin.preference;

import jp.gr.java_conf.ussiy.app.propedit.PropertiesEditor;
import jp.gr.java_conf.ussiy.app.propedit.eclipse.plugin.PropertiesEditorPlugin;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PropertiesEditorDuplicationCheckerPreference extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public static final String P_CHECK_KEY = "checkKeyPreference"; //$NON-NLS-1$

	private BooleanFieldEditor duplicateCheckFieldEditor;

	public PropertiesEditorDuplicationCheckerPreference() {

		super(GRID);
		setPreferenceStore(PropertiesEditorPlugin.getDefault().getPreferenceStore());
		setDescription(PropertiesEditor.res.getString("eclipse.propertieseditor.preference.page.title")); //$NON-NLS-1$
	}

	public void createFieldEditors() {

		Composite parent = getFieldEditorParent();

		duplicateCheckFieldEditor = new BooleanFieldEditor(P_CHECK_KEY, PropertiesEditor.res.getString("eclipse.propertieseditor.preference.checkduplication"), parent); //$NON-NLS-1$

		addField(duplicateCheckFieldEditor);
	}

	public void init(IWorkbench workbench) {

	}

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#performApply()
	 */
	protected void performApply() {
		super.performApply();
	}

}