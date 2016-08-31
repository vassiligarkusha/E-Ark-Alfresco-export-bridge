package dk.magenta.eark.erms.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.filter.ElementFilter;
import org.jdom2.filter.Filter;
import org.jdom2.input.SAXBuilder;

/**
 * 
 * @author andreas
 *
 */
public class MappingParser {

	private static final String mapNs = "http://www.magenta.dk/eark/erms/mapping/1.0";
	private static final String eadNs = "http://ead3.archivists.org/schema/";

	private String mappingId;
	private ObjectTypeMap objectTypeMap;
	private Map<String, List<Hook>> hooks;
	private Map<String, Element> CElements;
	private Namespace mappingNamespace;
	private Namespace eadNamespace;
	private Document mappingDocument;

	
	public MappingParser(String mappingId, InputStream in) {
		this.mappingId = mappingId;
		mappingNamespace = Namespace.getNamespace(mapNs);
		eadNamespace = Namespace.getNamespace(eadNs);
		buildMappingDocument(in);
	}

	
	public Document buildMappingDocument(InputStream in) {
		SAXBuilder builder = new SAXBuilder();
		Document xml = null;
		try {
			xml = builder.build(in);
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		mappingDocument = xml;
		return xml;
	}
	

	public ObjectTypeMap getObjectTypes() {
		if (objectTypeMap != null) {
			return objectTypeMap;
		}
		objectTypeMap = new ObjectTypeMap();
		List<Element> objectTypes = extractElements(mappingDocument, "objectType", mappingNamespace);
		for (Element objectType : objectTypes) {
			String repoType = objectType.getAttributeValue("id");
			String cmisType = objectType.getTextTrim();
			objectTypeMap.addObjectType(repoType, cmisType);
		}
		return objectTypeMap;
	}
	
	
	public Map<String, List<Hook>> getHooks() {
		if (hooks != null) {
			return hooks;
		}
		hooks = new HashMap<String, List<Hook>>();
		List<Element> templates = extractElements(mappingDocument, "template", mappingNamespace);
		for (Element template : templates) {
			Element hooksElement = extractElements(template, "hooks", mappingNamespace).get(0);
			List<Element> hookElements = extractElements(hooksElement, "hook", mappingNamespace);
			List<Hook> hookList = new LinkedList<Hook>();
			for (Element hookElement : hookElements) {
				Hook hook;
				if (hookElement.getAttribute("attribute", mappingNamespace) == null) {
					hook = new Hook(hookElement.getAttributeValue("path"), hookElement.getTextTrim());
				} else {
					hook = new Hook(hookElement.getAttributeValue("path"), hookElement.getTextTrim(),
							hookElement.getAttributeValue("attribute"));
				}
				hookList.add(hook);
			}
			hooks.put(template.getAttributeValue("id"), hookList);
		}
		return hooks;
	}
	
	
	public Map<String, Element> getCElements() {
		if (CElements != null) {
			return CElements;
		}
		CElements = new HashMap<String, Element>();
		List<Element> templates = extractElements(mappingDocument, "template", mappingNamespace);
		for (Element template : templates) {
			Element ead = extractElements(template, "ead", mappingNamespace).get(0);
			Element c = ead.getChild("c", mappingNamespace);
			CElements.put(template.getAttributeValue("id"), c);
		}
		return CElements;
	}

	
	public ObjectTypeMap getObjectTypeMap() {
		return objectTypeMap;
	}

	
	public String getMappingId() {
		return mappingId;
	}

	
	/**
	 * Extracts all descending Elements from a Document or an Element
	 * 
	 * @param obj
	 *            Must be either a Document or an Element
	 * @param elementName
	 * @param namespace
	 * @return List of descending Elements
	 */
	private List<Element> extractElements(Object obj, String elementName, Namespace namespace) {
		Filter<Element> filter = new ElementFilter(elementName, namespace);
		Iterator<Element> iterator = null;
		if (obj instanceof Document) {
			iterator = ((Document) obj).getDescendants(filter);
		} else if (obj instanceof Element) {
			iterator = ((Element) obj).getDescendants(filter);
		} else {
			throw new RuntimeException("Cannot extract Elements from " + obj);
		}
		List<Element> elements = new LinkedList<Element>();
		while (iterator.hasNext()) {
			elements.add(iterator.next());
		}
		return elements;
	}
}
