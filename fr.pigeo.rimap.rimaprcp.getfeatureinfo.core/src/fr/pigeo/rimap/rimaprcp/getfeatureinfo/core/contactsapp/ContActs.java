package fr.pigeo.rimap.rimaprcp.getfeatureinfo.core.contactsapp;

import java.awt.Component;
import java.awt.Cursor;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ContActs {
	private JsonNode node;
	private ArrayList<ContactEntry> contactsList = new ArrayList<ContactEntry>();
	private LinkedHashMap<String, CounterEntry> counter;
	private int totalCount = 0;
	private int totalInhabitantsCount = 0;
	private String[] contact_modes = { "sms", "email", "rue", "tv", "radio", "other" };

	private CloseableHttpClient httpClient;

	public ContActs(InputStream in, CloseableHttpClient httpClient) {
		this.httpClient = httpClient;
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

	public ArrayList<ContactEntry> getContactsList() {
		return contactsList;
	}

	public String sendMessage(String mode, String title, String content) {
		if (mode.equalsIgnoreCase("sms")) {
			// checkOVHSMSAPI();
			return sendSMSMessage(title, content);
			//return sendSMSMessageToMeWithFree(title, content);
		}
		return "";
	}

	private String sendSMSMessage(String title, String content) {
		String AK = "putyourAKhere";
		String AS = "putyourAShere";
		String CK = "putyourCKhere";
		String ServiceName = "putyourOVHServiceNamehere";
		String url = "https://eu.api.ovh.com/1.0/sms/" + ServiceName + "/jobs/";
		String METHOD = "POST";
		String message = title.toUpperCase()+" \\n "+content;
		System.out.println(message);
		message = message.replace(System.getProperty("line.separator"), " \\n ");
		System.out.println(message);
		try {
			URL QUERY = new URL(url);
			String BODY = "{\"receivers\":[\"002202030222\"],\"message\":\"" + message
					+ "\",\"priority\":\"high\",\"sender\":\"J. POMMIER\",\"senderForResponse\":false,\"noStopClause\":true}";

			long TSTAMP = new Date().getTime() / 1000;

			System.out.println(BODY);
			// Création de la signature
			String toSign = AS + "+" + CK + "+" + METHOD + "+" + QUERY + "+" + BODY + "+" + TSTAMP;
			String signature = "$1$" + HashSHA1(toSign);
			System.out.println(signature);
			HttpURLConnection req = (HttpURLConnection) QUERY.openConnection();
			req.setRequestMethod(METHOD);
			req.setRequestProperty("Content-Type", "application/json");
			req.setRequestProperty("X-Ovh-Application", AK);
			req.setRequestProperty("X-Ovh-Consumer", CK);
			req.setRequestProperty("X-Ovh-Signature", signature);
			req.setRequestProperty("X-Ovh-Timestamp", "" + TSTAMP);

			if (!BODY.isEmpty()) {
				req.setDoOutput(true);
				java.io.DataOutputStream wr = new java.io.DataOutputStream(req.getOutputStream());
				wr.writeBytes(BODY);
				wr.flush();
				wr.close();
			}

			String inputLine;
			BufferedReader in;
			int responseCode = req.getResponseCode();
			if (responseCode == 200) {
				// Récupération du résultat de l'appel
				in = new BufferedReader(new InputStreamReader(req.getInputStream()));
			} else {
				in = new BufferedReader(new InputStreamReader(req.getErrorStream()));
			}
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// Affichage du résultat
			System.out.println(response.toString());
		} catch (MalformedURLException e) {
			final String errmsg = "MalformedURLException: " + e;
		} catch (IOException e) {
			final String errmsg = "IOException: " + e;
		}
		return "OK";
	}

	private String checkOVHSMSAPI() {
		String AK = "putyourAKhere";
		String AS = "putyourAShere";
		String CK = "putyourCKhere";
		String url = "https://eu.api.ovh.com/1.0/sms/";
		try {
			String BODY = "";

			URL QUERY = new URL(url);
			long TSTAMP = new Date().getTime() / 1000;

			// Création de la signature
			String toSign = AS + "+" + CK + "+GET+" + QUERY + "+" + BODY + "+" + TSTAMP;
			String signature = "$1$" + HashSHA1(toSign);
			HttpURLConnection req = (HttpURLConnection) QUERY.openConnection();
			req.setRequestMethod("GET");
			req.setRequestProperty("Content-Type", "application/json");
			req.setRequestProperty("X-Ovh-Application", AK);
			req.setRequestProperty("X-Ovh-Consumer", CK);
			req.setRequestProperty("X-Ovh-Signature", signature);
			req.setRequestProperty("X-Ovh-Timestamp", "" + TSTAMP);

			String inputLine;
			BufferedReader in;
			int responseCode = req.getResponseCode();
			if (responseCode == 200) {
				// Récupération du résultat de l'appel
				in = new BufferedReader(new InputStreamReader(req.getInputStream()));
			} else {
				in = new BufferedReader(new InputStreamReader(req.getErrorStream()));
			}
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// Affichage du résultat
			System.out.println(response.toString());
		} catch (MalformedURLException e) {
			final String errmsg = "MalformedURLException: " + e;
		} catch (IOException e) {
			final String errmsg = "IOException: " + e;
		}
		return "OK";
	}

	private String HashSHA1(String text) {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-1");
			byte[] sha1hash = new byte[40];
			md.update(text.getBytes("iso-8859-1"), 0, text.length());
			sha1hash = md.digest();
			return convertToHex(sha1hash);
		} catch (NoSuchAlgorithmException e) {
			final String errmsg = "NoSuchAlgorithmException: " + text + " " + e;
			return errmsg;
		} catch (UnsupportedEncodingException e) {
			final String errmsg = "UnsupportedEncodingException: " + text + " " + e;
			return errmsg;
		}
	}

	private static String convertToHex(byte[] data) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < data.length; i++) {
			int halfbyte = (data[i] >>> 4) & 0x0F;
			int two_halfs = 0;
			do {
				if ((0 <= halfbyte) && (halfbyte <= 9))
					buf.append((char) ('0' + halfbyte));
				else
					buf.append((char) ('a' + (halfbyte - 10)));
				halfbyte = data[i] & 0x0F;
			} while (two_halfs++ < 1);
		}
		return buf.toString();
	}

	public String sendSMSMessageToMeWithFree(String title, String content) {
		System.out.println("Sending message " + content);
		String url = "https://smsapi.free-mobile.fr/sendmsg";
		String user = "17413902";
		String pass = "Xx8bcLCzHkmjp5";
		if (httpClient == null) {
			return "ERROR: HttpClient null. Message expedition aborted";
		}

		HttpPost post = new HttpPost(url);

		String statusCode = "";
		String request = "{\"user\":\"" + user + "\", \"pass\":\"" + pass + "\", \"msg\":\"" + content + "\"}";
		try {
			StringEntity entity = new StringEntity(request);
			post.setHeader("Accept", "application/json");
			post.setHeader("Content-type", "application/json");
			post.setEntity(entity);

			System.out.println(request);

			HttpResponse response = httpClient.execute(post);
			statusCode = String.valueOf(response.getStatusLine()
					.getStatusCode());
			System.out.println(statusCode);
			// EntityUtils.consumeQuietly(response.getEntity());
		} catch (ClientProtocolException e) {
		} catch (SocketTimeoutException e) {
		} catch (IOException e) {
		} finally {
			post.releaseConnection();
		}

		return "OK";

	}
}
