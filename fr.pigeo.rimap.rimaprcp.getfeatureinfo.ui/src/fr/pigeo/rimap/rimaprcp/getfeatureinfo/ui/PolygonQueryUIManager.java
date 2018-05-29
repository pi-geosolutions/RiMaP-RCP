package fr.pigeo.rimap.rimaprcp.getfeatureinfo.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.EventTopic;
import org.eclipse.e4.core.services.nls.Translation;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

import fr.pigeo.rimap.rimaprcp.getfeatureinfo.core.PolygonQuery;
import fr.pigeo.rimap.rimaprcp.getfeatureinfo.core.constants.QueryEventConstants;
import fr.pigeo.rimap.rimaprcp.getfeatureinfo.core.contactsapp.ContActs;
import fr.pigeo.rimap.rimaprcp.getfeatureinfo.core.i18n.Messages;

@Creatable
@Singleton
public class PolygonQueryUIManager {
	@Inject
	EPartService partService;
	@Inject
	EModelService modelService;
	@Inject
	MApplication application;
	@Inject
	@Translation
	Messages messages;

	@Inject
	PolygonQuery pq;

	final private String partContainerID = "fr.pigeo.rimap.rimaprcp.partstack.bottom";
	final private String partDescriptorID = "fr.pigeo.rimap.rimaprcp.getfeatureinfo.ui.partdescriptor.pq";
	final private String partDescriptorTag = "PolygonQuery";
	final private String contactViewPartContainerID = "fr.pigeo.rimap.rimaprcp.partstack.0";
	final private String contactViewPartDescriptorID = "fr.pigeo.rimap.rimaprcp.getfeatureinfo.ui.partdescriptor.contactsview";
	final private String contactViewPartDescriptorTag = "ContActsView";

	private Locale locale = Locale.ENGLISH;

	@Inject
	public PolygonQueryUIManager(IEclipseContext context) {
		locale = (Locale) context.get(TranslationService.LOCALE);
	}

	public void enable(boolean val) {
		if (pq == null) {
			return;
		}
		pq.setEnabled(val);
	}

	@Inject
	@Optional
	void onPolygonClose(@UIEventTopic(QueryEventConstants.POLYGONQUERY_READY) PolygonQuery pq) {
		// Create a PolygonQueryResultsPart
		MPart part = partService.createPart(partDescriptorID);
		part.setCloseable(true);
		// add it an existing stack and show it
		MPartStack stack = (MPartStack) modelService.find(partContainerID, application);

		removeSiblings(stack);
		stack.getChildren()
				.add(part);
		/*
		 * int siblings = countSiblings(stack);
		 * if (siblings > 1) {
		 * part.setLabel("(" + siblings + ")");
		 * }
		 */
		if (!stack.isVisible()) {
			stack.setVisible(true);
		}
		partService.showPart(part, EPartService.PartState.ACTIVATE);

	}

	@Inject
	@Optional
	void onViewContact(
			@UIEventTopic(QueryEventConstants.POLYGONQUERY_MS_SHOW_SMS_CONTACTS_LIST) Map<String, Object> h) {

		String mode = (String) h.get("mode");
		ContActs ct = (ContActs) h.get("contacts");
		System.out.println("View this dataset ");
		
		// Create a PolygonQueryResultsPart
		MPart part = partService.createPart(contactViewPartDescriptorID);
		part.getTransientData().put("message", "ok");
		part.getTransientData().put("mode", mode);
		part.getTransientData().put("contacts", ct);
		part.setCloseable(true);
		part.setLabel(messages.polygonquery_ms_view_title + " "+mode);
		// add it to an existing stack and show it
		MPartStack stack = (MPartStack) modelService.find(contactViewPartContainerID, application);

		stack.getChildren()
				.add(part);
		if (!stack.isVisible()) {
			stack.setVisible(true);
		}
		partService.showPart(part, EPartService.PartState.ACTIVATE);

	}

	private int countSiblings(MPartStack stack) {
		List<String> tags = new ArrayList<>();
		tags.add(partDescriptorTag);
		List<MPart> elementsWithTags = modelService.findElements(application, partDescriptorID, MPart.class, null);
		// System.out.println("Found parts(s) : " + elementsWithTags.size());
		return elementsWithTags.size();
	}

	private void removeSiblings(MPartStack stack) {
		List<String> tags = new ArrayList<>();
		tags.add(partDescriptorTag);
		List<MPart> elementsWithTags = modelService.findElements(application, partDescriptorID, MPart.class, null);
		Iterator<MPart> it = elementsWithTags.iterator();
		while (it.hasNext()) {
			MPart part = it.next();
			partService.hidePart(part);
		}
	}
}
