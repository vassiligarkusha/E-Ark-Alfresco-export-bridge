package dk.magenta.eark.erms.ead;

import dk.magenta.eark.erms.Constants;
import dk.magenta.eark.erms.exceptions.ErmsIOException;
import dk.magenta.eark.erms.mappings.MapWorker;
import dk.magenta.eark.erms.mappings.MapWorkerImpl;
import dk.magenta.eark.erms.mappings.Mapping;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.ElementFilter;
import org.jdom2.filter.Filter;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

/**
 * 
 * @author andreas
 *
 */
public class MappingParser {

	private String mappingId;
	private ObjectTypeMap objectTypeMap;
	private Map<String, List<Hook>> hooks;
	private Map<String, Element> CElements;
	private Namespace mappingNamespace;
	private Document mappingDocument;
	private XmlHandler xmlHandler;

	public MappingParser(String mappingId) {
		this.mappingId = mappingId;
        MapWorker mapWorker = new MapWorkerImpl();
        Mapping map = mapWorker.getMappingObject(mappingId);
        mappingNamespace = Namespace.getNamespace(Constants.MAPPING_NAMESPACE);
        xmlHandler = new XmlHandlerImpl();
        try {
            FileInputStream tmpFile = new FileInputStream(map.getSyspath());
            buildMappingDocument(new BufferedInputStream(tmpFile));
        }
        catch(FileNotFoundException fnfe){
            throw new ErmsIOException("Unable to find the mapping file. Check that: "+map.getSyspath() +" exists");
        }
	}

	public MappingParser(String mappingId, InputStream in) {
		this.mappingId = mappingId;
		mappingNamespace = Namespace.getNamespace(Constants.MAPPING_NAMESPACE);
		xmlHandler = new XmlHandlerImpl();
		buildMappingDocument(in);
	}

	/**
	 * Build the JDOM mapping.xml Document from an input stream
	 * (the mapping.xml has been validated earlier on)
	 * @param in stream containing the mapping.xml
	 * @return JDOM representation of the mapping.xml
	 */
	public Document buildMappingDocument(InputStream in) {
		mappingDocument = xmlHandler.readXml(in);
		return mappingDocument;
	}
	
	/**
	 * Extract the objectTypes from the mapping.xml and put these into an ObjectTypeMap
	 * @return the ObjectTypeMap containing the datastructures
	 */
	public ObjectTypeMap getObjectTypes() {
		if (objectTypeMap != null) {
			return objectTypeMap;
		}
		objectTypeMap = new ObjectTypeMap();
		List<Element> objectTypes = extractElements(mappingDocument, "objectType", mappingNamespace);
		for (Element objectType : objectTypes) {
			String semanticType = objectType.getAttributeValue("id");
			boolean leaf = Boolean.parseBoolean(objectType.getAttributeValue("leaf").trim());
			String cmisType = objectType.getTextTrim();
			objectTypeMap.addObjectType(semanticType, cmisType, leaf);
		}
		return objectTypeMap;
	}
	
	/**
	 * Gets map of hooks
	 * @return map from semantic name to list of Hooks 
	 */
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
				Hook hook = new Hook(hookElement.getAttributeValue("xpath"), hookElement.getTextTrim());
				hookList.add(hook);
			}
			hooks.put(template.getAttributeValue("id"), hookList);
		}
		return hooks;
	}
	

	public List<Hook> getHooksFromSemanticType(String semanticType) {
		getHooks();
		return hooks.get(semanticType);
	}
	
	
	public List<Hook> getHooksFromCmisType(String cmisType) {
		String semanticType = getObjectTypes().getSemanticTypeFromCmisType(cmisType);
		return getHooksFromSemanticType(semanticType);
	}
	
	
	public String getSemanticTypeFromCmisType(String cmisType) {
		return getObjectTypes().getSemanticTypeFromCmisType(cmisType);
	}
	
	/**
	 * Get map from semantic type to c element
	 * @return map from semantic type to c element
	 */
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

	public Element getCElementFromSemanticType(String semanticType) {
		return getCElements().get(semanticType);
	}
	
	public Element getCElementFromCmisType(String cmisType) {
		String semanticType = getObjectTypes().getSemanticTypeFromCmisType(cmisType);
		return getCElementFromSemanticType(semanticType);
	}

    /**
     * Returns the set of all the cmis types defined in the mapping.xml document
     * @return
     */
	public Set<String> getObjectTypeFromMap() {
        return getObjectTypes().getAllCmisTypes();
	}
	
	public String getMappingId() {
		return mappingId;
	}

	
	public Document getMappingDocument() {
		return mappingDocument;
	}
	
	public boolean isLeaf(String cmisType) {
		String semanticType = getObjectTypes().getSemanticTypeFromCmisType(cmisType);
		return getObjectTypes().isLeaf(semanticType);
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
