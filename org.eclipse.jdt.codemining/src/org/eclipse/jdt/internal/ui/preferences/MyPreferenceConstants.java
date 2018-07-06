package org.eclipse.jdt.internal.ui.preferences;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;

public class MyPreferenceConstants {

	/**
	 * A named preference that stores the value for "Show references" codemining.
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 *
	 * @since 3.14
	 */
	public static final String EDITOR_JAVA_CODEMINING_SHOW_REFERENCES = "java.codemining.references"; //$NON-NLS-1$

	/**
	 * A named preference that stores the value for "Show references" only if there
	 * is at least one reference.
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 *
	 * @since 3.14
	 */
	public static final String EDITOR_JAVA_CODEMINING_SHOW_REFERENCES_AT_LEAST_ONE = "java.codemining.references.atLeastOne"; //$NON-NLS-1$

	/**
	 * A named preference that stores the value for "Show implementations"
	 * codemining.
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 *
	 * @since 3.14
	 */
	public static final String EDITOR_JAVA_CODEMINING_SHOW_IMPLEMENTATIONS = "java.codemining.implementations"; //$NON-NLS-1$

	/**
	 * A named preference that stores the value for "Show implementations" only if
	 * there is at least one implementation.
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 *
	 * @since 3.14
	 */
	public static final String EDITOR_JAVA_CODEMINING_SHOW_IMPLEMENTATIONS_AT_LEAST_ONE = "java.codemining.implementations.atLeastOne"; //$NON-NLS-1$

	/**
	 * A named preference that stores the value for "Show revision recent change".
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 *
	 * @since 3.14
	 */
	public static final String EDITOR_JAVA_CODEMINING_SHOW_REVISION_RECENT_CHANGE = "java.codemining.sccm.revison.recent.change"; //$NON-NLS-1$

	/**
	 * A named preference that stores the value for "Show revision recent change
	 * with date".
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 *
	 * @since 3.14
	 */
	public static final String EDITOR_JAVA_CODEMINING_SHOW_REVISION_RECENT_CHANGE_WITH_DATE = "java.codemining.sccm.revison.recent.change.withDate"; //$NON-NLS-1$

	/**
	 * A named preference that stores the value for "Show authors".
	 * <p>
	 * Value is of type <code>Boolean</code>.
	 * </p>
	 *
	 * @since 3.14
	 */
	public static final String EDITOR_JAVA_CODEMINING_SHOW_REVISION_AUTHORS = "java.codemining.sccm.revision.authors"; //$NON-NLS-1$

	private static boolean initialized;

	/**
	 * Initializes the given preference store with the default values.
	 *
	 * @param store the preference store to be initialized
	 *
	 */
	public static void initializeDefaultValues(IPreferenceStore store) {
		store.setDefault(MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_REFERENCES, JavaCore.ENABLED);
		store.setDefault(MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_REFERENCES_AT_LEAST_ONE, JavaCore.ENABLED);
		store.setDefault(MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_IMPLEMENTATIONS, JavaCore.ENABLED);
		store.setDefault(MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_IMPLEMENTATIONS_AT_LEAST_ONE,
				JavaCore.DISABLED);
		store.setDefault(MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_REVISION_RECENT_CHANGE, JavaCore.ENABLED);
		store.setDefault(MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_REVISION_RECENT_CHANGE_WITH_DATE,
				JavaCore.ENABLED);
		store.setDefault(MyPreferenceConstants.EDITOR_JAVA_CODEMINING_SHOW_REVISION_AUTHORS, JavaCore.DISABLED);
	}

	/**
	 * Returns the JDT-UI preference store.
	 *
	 * @return the JDT-UI preference store
	 */
	public static IPreferenceStore getPreferenceStore() {
		IPreferenceStore store = PreferenceConstants.getPreferenceStore();
		if (!initialized) {
			MyPreferenceConstants.initializeDefaultValues(store);
			initialized = true;
		}
		return store;
	}
}
