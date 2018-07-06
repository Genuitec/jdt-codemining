package org.eclipse.jdt.internal.ui.preferences;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.dialogs.StatusInfo;
import org.eclipse.jdt.internal.ui.preferences.FilteredPreferenceTree.PreferenceTreeNode;
import org.eclipse.jdt.internal.ui.wizards.IStatusChangeListener;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.LayoutUtil;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

public class JavaEditorCodeMiningConfigurationBlock extends OptionsConfigurationBlock {

	// Preference store keys, see JavaCore.getOptions

	// --------------------- General

	private static final Key PREF_SHOW_REFERENCES = getJDTUIKey(
			MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_REFERENCES);

	private static final Key PREF_SHOW_REFERENCES_AT_LEAST_ONE = getJDTUIKey(
			MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_REFERENCES_AT_LEAST_ONE);

	private static final Key PREF_SHOW_IMPLEMENTATIONS = getJDTUIKey(
			MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_IMPLEMENTATIONS);

	private static final Key PREF_SHOW_IMPLEMENTATIONS_AT_LEAST_ONE = getJDTUIKey(
			MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_IMPLEMENTATIONS_AT_LEAST_ONE);

	public static final Key PREF_SHOW_REVISION_RECENT_CHANGE = getJDTUIKey(
			MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_REVISION_RECENT_CHANGE);

	public static final Key PREF_SHOW_REVISION_RECENT_CHANGE_WITH_DATE = getJDTUIKey(
			MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_REVISION_RECENT_CHANGE_WITH_DATE); // $NON-NLS-1$

	public static final Key PREF_SHOW_REVISION_AUTHORS = getJDTUIKey(
			MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_REVISION_AUTHORS); // $NON-NLS-1$

	private static final String SETTINGS_SECTION_NAME = "JavaEditorCodeMiningConfigurationBlock"; // $NON-NLS-1

	private static final String ENABLED = JavaCore.ENABLED;
	private static final String DISABLED = JavaCore.DISABLED;

	String[] enabledDisabled = new String[] { ENABLED, DISABLED };

	private PixelConverter fPixelConverter;

	private PreferenceTree fFilteredPrefTree;

	public JavaEditorCodeMiningConfigurationBlock(IStatusChangeListener context, IProject project,
			IWorkbenchPreferenceContainer container) {
		super(context, project, getKeys(), container);
	}

	public static Key[] getKeys() {
		return new Key[] { PREF_SHOW_REFERENCES, PREF_SHOW_REFERENCES_AT_LEAST_ONE, PREF_SHOW_IMPLEMENTATIONS,
				PREF_SHOW_IMPLEMENTATIONS_AT_LEAST_ONE, 
				PREF_SHOW_REVISION_RECENT_CHANGE,
				PREF_SHOW_REVISION_RECENT_CHANGE_WITH_DATE, PREF_SHOW_REVISION_AUTHORS };
	}

	/*
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		fPixelConverter = new PixelConverter(parent);
		setShell(parent.getShell());

		Composite mainComp = new Composite(parent, SWT.NONE);
		mainComp.setFont(parent.getFont());
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		mainComp.setLayout(layout);

		createIgnoreOptionalProblemsLink(mainComp);

		Composite spacer = new Composite(mainComp, SWT.NONE);
		spacer.setLayoutData(new GridData(0, 0));

		Composite commonComposite = createStyleTabContent(mainComp);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.heightHint = fPixelConverter.convertHeightInCharsToPixels(30);
		commonComposite.setLayoutData(gridData);

		validateSettings(null, null, null);

		return mainComp;
	}

	private Composite createStyleTabContent(Composite folder) {
		fFilteredPrefTree = new PreferenceTree(this, folder,
				MyPreferencesMessages.JavaEditorCodeMiningConfigurationBlock_common_description);
		final ScrolledPageContent sc1 = fFilteredPrefTree.getScrolledPageContent();

		int nColumns = 3;
		Composite composite = sc1.getBody();
		GridLayout layout = new GridLayout(nColumns, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		composite.setLayout(layout);

		// --- General
		createGeneralSection(nColumns, composite);
		// --- Git
		createGitSection(nColumns, composite);

		IDialogSettings settingsSection = JavaPlugin.getDefault().getDialogSettings().getSection(SETTINGS_SECTION_NAME);
		restoreSectionExpansionStates(settingsSection);

		return sc1;
	}

	private void createGeneralSection(int nColumns, Composite parent) {
		int defaultIndent = 0;
		int extraIndent = LayoutUtil.getIndent();
		String label = MyPreferencesMessages.JavaEditorCodeMiningConfigurationBlock_section_general;
		Key twistieKey = OptionsConfigurationBlock.getLocalKey("JavaEditorCodeMiningPreferencePage_section_general"); //$NON-NLS-1$
		PreferenceTreeNode<?> section = fFilteredPrefTree.addExpandableComposite(parent, label, nColumns, twistieKey,
				null, false);
		ExpandableComposite excomposite = getExpandableComposite(twistieKey);

		Composite inner = createInnerComposite(excomposite, nColumns, parent.getFont());

		// - Show references
		fFilteredPrefTree.addCheckBox(inner,
				MyPreferencesMessages.JavaEditorCodeMiningConfigurationBlock_showReferences_label, PREF_SHOW_REFERENCES,
				enabledDisabled, defaultIndent, section);
		// - Show references (Only if there is at least one reference)
		fFilteredPrefTree.addCheckBox(inner,
				MyPreferencesMessages.JavaEditorCodeMiningConfigurationBlock_showReferences_atLeastOne_label,
				PREF_SHOW_REFERENCES_AT_LEAST_ONE, enabledDisabled, extraIndent, section);

		// - Show implementations
		fFilteredPrefTree.addCheckBox(inner,
				MyPreferencesMessages.JavaEditorCodeMiningConfigurationBlock_showImplementations_label,
				PREF_SHOW_IMPLEMENTATIONS, enabledDisabled, defaultIndent, section);
		// - Show implementations (Only if there is at least one implementation)
		fFilteredPrefTree.addCheckBox(inner,
				MyPreferencesMessages.JavaEditorCodeMiningConfigurationBlock_showImplementations_atLeastOne_label,
				PREF_SHOW_IMPLEMENTATIONS_AT_LEAST_ONE, enabledDisabled, extraIndent, section);
	}

	private void createGitSection(int nColumns, Composite parent) {
		final int defaultIndent = 0;
		int extraIndent = LayoutUtil.getIndent();
		String label = MyPreferencesMessages.JavaEditorCodeMiningConfigurationBlock_section_scsm;
		Key twistieKey = OptionsConfigurationBlock.getLocalKey("JavaEditorCodeMiningPreferencePage_section_sccm"); //$NON-NLS-1$
		PreferenceTreeNode<?> section = fFilteredPrefTree.addExpandableComposite(parent, label, nColumns, twistieKey,
				null, false);
		ExpandableComposite excomposite = getExpandableComposite(twistieKey);

		Composite inner = createInnerComposite(excomposite, nColumns, parent.getFont());

		// - Show git recent change
		fFilteredPrefTree.addCheckBox(inner,
				MyPreferencesMessages.JavaEditorCodeMiningConfigurationBlock_showRevisionRecentChange,
				PREF_SHOW_REVISION_RECENT_CHANGE, enabledDisabled, defaultIndent, section);
		fFilteredPrefTree.addCheckBox(inner,
				MyPreferencesMessages.JavaEditorCodeMiningConfigurationBlock_showRevisionRecentChangeWithDate,
				PREF_SHOW_REVISION_RECENT_CHANGE_WITH_DATE, enabledDisabled, extraIndent, section);
		// Show authors
		fFilteredPrefTree.addCheckBox(inner,
				MyPreferencesMessages.JavaEditorCodeMiningConfigurationBlock_showRevisionAuthors,
				PREF_SHOW_REVISION_AUTHORS, enabledDisabled, defaultIndent, section);

		// - Show git changes
//		fFilteredPrefTree.addCheckBox(inner,
//				MyPreferencesMessages.JavaEditorCodeMiningConfigurationBlock_showGitChanges_label,
//				PREF_SHOW_GIT_CHANGES, enabledDisabled, defaultIndent, section);
	}

	private Composite createInnerComposite(ExpandableComposite excomposite, int nColumns, Font font) {
		Composite inner = new Composite(excomposite, SWT.NONE);
		inner.setFont(font);
		inner.setLayout(new GridLayout(nColumns, false));
		excomposite.setClient(inner);
		return inner;
	}

	@Override
	protected void validateSettings(Key changedKey, String oldValue, String newValue) {
		if (!areSettingsEnabled()) {
			return;
		}
		fContext.statusChanged(new StatusInfo());
	}

	@Override
	protected String[] getFullBuildDialogStrings(boolean workspaceSettings) {
		return null;
	}

	@Override
	public void dispose() {
		IDialogSettings section = JavaPlugin.getDefault().getDialogSettings().addNewSection(SETTINGS_SECTION_NAME);
		storeSectionExpansionStates(section);
		super.dispose();
	}
}
