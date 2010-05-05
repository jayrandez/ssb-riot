package demerit;

import static org.w3c.dom.Node.ATTRIBUTE_NODE;
import static org.w3c.dom.Node.CDATA_SECTION_NODE;
import static org.w3c.dom.Node.COMMENT_NODE;
import static org.w3c.dom.Node.DOCUMENT_TYPE_NODE;
import static org.w3c.dom.Node.ENTITY_NODE;
import static org.w3c.dom.Node.ENTITY_REFERENCE_NODE;
import static org.w3c.dom.Node.NOTATION_NODE;
import static org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE;
import static org.w3c.dom.Node.ELEMENT_NODE;
import static org.w3c.dom.Node.TEXT_NODE;

import java.io.*;
import java.util.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.w3c.dom.*;

/**
 * An exteremely detailed class which is designed to manage the settings.xml file.
 * Responsible for:
 * Keeping track of allowed modules, decrypting/encrypting database connection information,
 * storing library-specific information.
 */
public class SettingsManager {
	
	public final static String builtInKey = "?? ?AbC D1 23 4BU1 Lt I|\\| 09 88!! !?";
	public final static String checkString = "PasswordCorrect.";
	
	private Document settings;
	private String masterKey;
	private Boolean sane;
	private File settingsFile;
	private Boolean needsExternal;
	
	public SettingsManager(File settingsFile) {
		this.settingsFile = settingsFile;
		sane = true;
		masterKey = builtInKey;
		try {
			DocumentBuilder docBuild = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			settings = docBuild.parse(settingsFile);
			validateDocument();
		}
		catch(Exception ex) {
			sane = false;
			Core.debug("Settings file isn't well formed or doesn't exist.");
			generateDocument();
		}
		if(!validateKey()) {
			needsExternal = true;
		}
		else {
			needsExternal = false;
		}
	}
	
	private void generateDocument() {
		Core.debug("Generating new settings document.");
		try {
			DocumentBuilder docBuild = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			settings = docBuild.newDocument();
			settings.appendChild(settings.createComment("This is a sensitive file. Please refrain from editing it by hand.\nIf it is damaged you will likely need to re-run " + Core.applicationName + " setup."));
			Element root = settings.createElement("settings");
			Element checkstring = settings.createElement("checkstring");
			SecurityString cipher = new SecurityString(checkString);
			String encrypted = cipher.encryptAes(masterKey);
			Text checkstringValue = settings.createTextNode(encrypted);
			checkstring.appendChild(checkstringValue);
			root.appendChild(checkstring);
			settings.appendChild(root);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			Core.debug("Error in document builder creation.");
			Core.fatal("Settings Generator");
		}
	}
	
	private void validateDocument() throws Exception {
		sane = true;
		Element root = settings.getDocumentElement();
		if(root == null) {
			sane = false;
			throw new Exception();
		}
		
		Boolean checkStringFound = false;
		NodeList children = root.getChildNodes();
		Node child = null;
		
		for(int i = 0; i < children.getLength(); i++) {
			child = children.item(i);
			if(child.getNodeName().equals("checkstring")) {
				checkStringFound = true;
				break;
			}
		}
		
		if(!checkStringFound) {
			sane = false;
			throw new Exception();
		}
		
		Text checkStringText = (Text)child.getFirstChild();
		if(checkStringText == null || checkStringText.getLength() == 0) {
			sane = false;
			throw new Exception();
		}
	}
	
	public void saveDocument() {
		try {
			Core.debug("Saving settings.xml");
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			StreamResult result = new StreamResult(settingsFile);
			DOMSource source = new DOMSource(settings);
			transformer.transform(source, result);
		}
		catch(Exception ex) {
			Core.message("The settings file couldn't be saved. It might be opened in another program.\nPlease close it if you want any configuration changes to be saved.");
		}
	}
	
	public Boolean useExternalKey(String key) {
		masterKey = builtInKey + key;
		return validateKey();
	}
	
	public Boolean needsExternalKey() {
		return needsExternal;
	}
	
	private Boolean validateKey() {
		Boolean validKey = false;
		Node checkNode = getUpToUniqueNode("checkstring");
		Text textBlock = (Text)checkNode.getFirstChild();
		textBlock.normalize();
		SecurityString encrypted = new SecurityString(textBlock.getData());
		String decrypted = "";
		try {
			decrypted = encrypted.decryptAes(masterKey);
		}
		catch (Exception ex) {
			//ex.printStackTrace();
		}
		if(decrypted.equals(checkString)) {
			validKey = true;
		}
		return validKey;
	}
	
	public Boolean isSane() {
		return sane;
	}
	
	public ConnectionData getConnectionData() {
		ConnectionData connectionData = new ConnectionData();
		SecurityString encrypted = new SecurityString(getNodeText(getUpToUniqueNode("database/password")));
		String decrypted = "";
		try {
			decrypted = encrypted.decryptAes(masterKey);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		connectionData.driver = "mysql";
		connectionData.host = getNodeText(getUpToUniqueNode("database/hostname"));
		connectionData.port = getNodeText(getUpToUniqueNode("database/port"));
		connectionData.dbname = getNodeText(getUpToUniqueNode("database/dbname"));
		connectionData.user = getNodeText(getUpToUniqueNode("database/username"));
		connectionData.pass = decrypted;
		return connectionData;
	}
	
	public void setUserCredentials(String username, String password) {
		try {
			SecurityString crypt = new SecurityString(password);
			password = crypt.encryptAes(masterKey);
		}
		catch(Exception ex) {
			Core.message("The database information couldn't be encrypted so it won't be saved.");
			return;
		}
		Node userBlock = clearNode(getUpToUniqueNode("database/username"));
		Node passBlock = clearNode(getUpToUniqueNode("database/password"));
		Node userText = settings.createTextNode(username);
		Node passText = settings.createTextNode(password);
		userBlock.appendChild(userText);
		passBlock.appendChild(passText);
	}
	
	public void setDatabaseInfo(String hostname, String port, String dbName) {
		Node hostBlock = clearNode(getUpToUniqueNode("database/hostname"));
		Node portBlock = clearNode(getUpToUniqueNode("database/port"));
		Node nameBlock = clearNode(getUpToUniqueNode("database/dbname"));
		Node hostText = settings.createTextNode(hostname);
		Node portText = settings.createTextNode(port);
		Node nameText = settings.createTextNode(dbName);
		hostBlock.appendChild(hostText);
		portBlock.appendChild(portText);
		nameBlock.appendChild(nameText);
	}
	
	public void setLibraryID(String libraryID) {
		Node library = clearNode(getUpToUniqueNode("local/library"));
		Text libraryText = settings.createTextNode(libraryID);
		library.appendChild(libraryText);
	}
	
	public void enableModule(String moduleName) {
		Node moduleBlock = getUpToUniqueNode("modules");
		NodeList moduleEntries = moduleBlock.getChildNodes();
		for(int i = 0; i < moduleEntries.getLength(); i++) {
			Node module = moduleEntries.item(i);
			if(module.getNodeType() == ELEMENT_NODE) {
				if(getAttribute(module, "name").equals(moduleName)) {
					setAttribute(module, "enabled", "1");
					return;
				}
			}
		}
		Node module = settings.createElement("module");
		setAttribute(module, "name", moduleName);
		setAttribute(module, "enabled", "1");
		moduleBlock.appendChild(module);
	}
	
	public void disableModule(String moduleName) {
		Node moduleBlock = getUpToUniqueNode("modules");
		NodeList moduleEntries = moduleBlock.getChildNodes();
		for(int i = 0; i < moduleEntries.getLength(); i++) {
			Node module = moduleEntries.item(i);
			if(module.getNodeType() == ELEMENT_NODE) {
				if(getAttribute(module, "name").equals(moduleName)) {
					setAttribute(module, "enabled", "0");
					return;
				}
			}
		}
		Node module = settings.createElement("module");
		setAttribute(module, "name", moduleName);
		setAttribute(module, "enabled", "0");
		moduleBlock.appendChild(module);
	}
	
	public void forgetModule(String moduleName) {
		Node moduleBlock = getUpToUniqueNode("modules");
		NodeList moduleEntries = moduleBlock.getChildNodes();
		for(int i = 0; i < moduleEntries.getLength(); i++) {
			Node module = moduleEntries.item(i);
			if(module.getNodeType() == ELEMENT_NODE) {
				if(getAttribute(module, "name").equals(moduleName)) {
					moduleBlock.removeChild(module);
					return;
				}
			}
		}
	}
	
	public ArrayList<String> flushAllowedModules() {
		ArrayList<String> allowed = new ArrayList<String>();
		Node moduleBlock = getUpToUniqueNode("modules");
		NodeList moduleEntries = moduleBlock.getChildNodes();
		for(int i = 0; i < moduleEntries.getLength(); i++) {
			Node module = moduleEntries.item(i);
			if(module.getNodeType() == ELEMENT_NODE) {
				if(getAttribute(module, "enabled").equals("1")) {
					allowed.add(getAttribute(module, "name"));
				}
			}
		}
		return allowed;
	}
	
	public ArrayList<String> flushKnownModules() {
		ArrayList<String> known = new ArrayList<String>();
		Node moduleBlock = getUpToUniqueNode("modules");
		NodeList moduleEntries = moduleBlock.getChildNodes();
		for(int i = 0; i < moduleEntries.getLength(); i++) {
			Node module = moduleEntries.item(i);
			if(module.getNodeType() == ELEMENT_NODE) {
				known.add(getAttribute(module, "name"));
			}
		}
		return known;
	}

	public String getLibraryID() {
		Node library = getUpToUniqueNode("local/library");
		return getNodeText(library);
	}
	
	private String getAttribute(Node node, String name) {
		NamedNodeMap attributes = node.getAttributes();
		Attr attribute = (Attr)attributes.getNamedItem(name);
		if(attribute == null) {
			setAttribute(node, name, "");
		}
		return attribute.getNodeValue();
	}
	
	private void setAttribute(Node node, String name, String value) {
		NamedNodeMap attributes = node.getAttributes();
		Attr attribute = (Attr)attributes.getNamedItem(name);
		if(attribute == null) {
			attribute = settings.createAttribute(name);
		}
		attribute.setValue(value);
		attributes.setNamedItem(attribute);
	}
	
	private Node getUpToUniqueNode(String listing) {
		Element root = settings.getDocumentElement();
		return getUpToUniqueNode(root, listing);
	}
	
	private Node getUpToUniqueNode(Node parent, String listing) {
		if(listing.length() > 0) {
			String[] parts = listing.split("/");
			String subParts = "";
			for(int i = 1; i < parts.length; i++) {
				subParts += "/" + parts[i];
			}
			if(subParts.length() > 1)
				subParts = subParts.substring(1);
			NodeList children = parent.getChildNodes();
			for(int i = 0; i < children.getLength(); i++) {
				Node child = children.item(i);
				if(child.getNodeName().equals(parts[0])) {
					return getUpToUniqueNode(child, subParts);
				}
			}
			Node child = settings.createElement(parts[0]);
			parent.appendChild(child);
			return getUpToUniqueNode(child, subParts);
		}
		else {
			return parent;
		}
	}
	
	private String getNodeText(Node node) {
		NodeList children = node.getChildNodes();
		for(int i = 0; i < children.getLength(); i++) {
			Node current = children.item(i);
			if(current.getNodeType() == TEXT_NODE) {
				return ((Text)current).getData();
			}
		}
		return "";
	}
	
	/*private void debugNodeType(Node node) {
		switch(node.getNodeType()) {
			case ATTRIBUTE_NODE:
				Core.debug("attrib");
				break;
			case CDATA_SECTION_NODE:
				Core.debug("cdata");
				break;
			case COMMENT_NODE:
				Core.debug("comment");
				break;
			case DOCUMENT_TYPE_NODE:
				Core.debug("doctype");
				break;
			case ENTITY_NODE:
				Core.debug("ent");
				break;
			case ENTITY_REFERENCE_NODE:
				Core.debug("entref");
				break;
			case NOTATION_NODE:
				Core.debug("note");
				break;
			case PROCESSING_INSTRUCTION_NODE:
				Core.debug("processing");
				break;
			case ELEMENT_NODE:
				Core.debug("elem");
				break;
			case TEXT_NODE:
				Core.debug("text");
				break;
		}
	}*/
	
	private Node clearNode(Node node) {
		String name = node.getNodeName();
		Node parent = node.getParentNode();
		parent.removeChild(node);
		node = settings.createElement(name);
		parent.appendChild(node);
		return node;
	}
}
