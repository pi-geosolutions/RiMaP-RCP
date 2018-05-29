package fr.pigeo.rimap.rimaprcp.getfeatureinfo.core.contactsapp;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ContActs {
	private JsonNode node;
	private ArrayList<ContactEntry> contactsList = new ArrayList<ContactEntry>();
	private LinkedHashMap<String, CounterEntry> counter;
	private int totalCount = 0;
	private int totalInhabitantsCount = 0;
	private String[] contact_modes = { "sms", "email", "rue", "tv", "radio", "other" };

	public ContActs(InputStream in) {
		this.contactsList.clear();
		this.initializeCounter();
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			node = objectMapper.readValue(in, JsonNode.class);
			JsonNode children = node.get("features");
			if (children.isArray()) {
				this.loadFeatures(children);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void initializeCounter() {
		this.totalCount = 0;
		this.counter = new LinkedHashMap<>(this.contact_modes.length);
		for (String mode : this.contact_modes) {
			this.counter.put(mode, new CounterEntry(mode, 0, 0));
		}
	}

	private void loadFeatures(JsonNode list) {
		Iterator<JsonNode> itr = list.iterator();
		while (itr.hasNext()) {
			JsonNode child = itr.next();
			JsonNode properties = child.get("properties");
			ContactEntry ce = new ContactEntry(properties);
			contactsList.add(ce);
			this.count(ce);
		}
		System.out.println(contactsList.size() + " features collected");
	}

	private void count(ContactEntry ce) {
		this.totalCount += 1;
		totalInhabitantsCount += ce.getInhabitants();
		CounterEntry e = this.counter.get(ce.getContact_mode());
		if (e != null) {
			e.increment(ce.getInhabitants());
		}
	}

	public List<CounterEntry> getCounter() {
		return new ArrayList<CounterEntry>(this.counter.values());
	}

	public int getTotalCount() {
		return totalCount;
	}
	public int getTotalInhabitantsCount() {
		return totalInhabitantsCount;
	}

	/*
	 * Counter class, will be used to display summary in the bottom part fo the
	 * application
	 */
	public class CounterEntry {
		private String category;
		private int occurences = 0;
		private int inhabitants = 0;

		public CounterEntry(String cat) {
			this.category = cat;
		}

		public CounterEntry(String cat, int nb, int people) {
			this.category = cat;
			this.occurences = nb;
			this.inhabitants = people;
		}

		public void reset() {
			this.occurences = 0;
		}

		public void increment(int people) {
			this.occurences++;
			this.inhabitants += people;
		}

		public String getCategory() {
			return category;
		}

		public int getCount() {
			return occurences;
		}

		public int getInhabitants() {
			return this.inhabitants;
		}
		
		public boolean canSendMessage() {
			return (category.equalsIgnoreCase("sms") || category.equalsIgnoreCase("email"));
		}
		
		public String getSendLabel() {
			if (category.equalsIgnoreCase("sms")) {
				return "Send SMS";
			}
			if (category.equalsIgnoreCase("email")) {
				return "Send EMAIL";
			}
			return "";
			
		}
	}
}
