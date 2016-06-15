
package fr.pigeo.rimap.rimaprcp.admintools.core.ui.parts;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import fr.pigeo.rimap.rimaprcp.admintools.core.constants.AdminToolsEventConstants;
import fr.pigeo.rimap.rimaprcp.admintools.core.editors.PadreCatalogEditor;
import fr.pigeo.rimap.rimaprcp.core.catalog.ICatalog;
import fr.pigeo.rimap.rimaprcp.core.services.catalog.catalogs.FolderNode;
import fr.pigeo.rimap.rimaprcp.core.services.catalog.catalogs.PadreCatalog;

public class LayertreeEditorPart {
	@Inject
	Logger logger;

	@Inject
	MDirtyable dirty;

	private Text text;
	private PadreCatalogEditor editor;

	@Inject
	public LayertreeEditorPart() {

	}

	@PostConstruct
	public void postConstruct(Composite parent) {
		parent.setLayout(new GridLayout(1, false));

		text = new Text(parent, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
		text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

	}

	@Inject
	@Optional
	private void subscribeCatalogLoad(@UIEventTopic(AdminToolsEventConstants.ADMINTOOLS_CATALOG_LOAD) ICatalog catalog,
			IEclipseContext context) {
		if (catalog instanceof PadreCatalog) {
			this.loadEditor((PadreCatalog) catalog, context);
			this.dirty.setDirty(false);
		}
	}

	@Inject
	@Optional
	private void subscribeCatalogFix(@UIEventTopic(AdminToolsEventConstants.ADMINTOOLS_CATALOG_FIX) ICatalog catalog,
			IEclipseContext context) {
		if (catalog instanceof PadreCatalog) {
			if (editor==null) {
				this.loadEditor((PadreCatalog) catalog, context);
			}
			editor.fixLayertree();
			this.text.setText(this.editor.toString(true));
			this.dirty.setDirty(true);

		}
	}

	private void loadEditor(PadreCatalog catalog, IEclipseContext context) {
		// create a new local_ context
		IEclipseContext catalogContext = EclipseContextFactory.create();
		catalogContext.set(PadreCatalog.class, catalog);

		// connect new local context with context hierarchy
		catalogContext.setParent(context);

		this.editor = ContextInjectionFactory.make(PadreCatalogEditor.class, catalogContext);
		//Injection reuses the object if it has already been created once. So we reset the values.
		this.editor.reset();
		this.text.setText(this.editor.toString(true));
	}

	public Text getText() {
		return text;
	}

	public void setText(Text text) {
		this.text = text;
	}

	@Persist
	public void save() {
		logger.warn("Should implement save method in LayertreeEditorPart");
	}

}