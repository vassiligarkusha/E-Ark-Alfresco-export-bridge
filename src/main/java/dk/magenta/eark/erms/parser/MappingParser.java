package dk.magenta.eark.erms.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
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
	private Map<String, Hook> hooks;
	private Namespace mappingNamespace;
	private Namespace eadNamespace;
	private Document mappingDocument;
	
	public MappingParser(String mappingId) {
		this.mappingId = mappingId;
		objectTypeMap = new ObjectTypeMap();
		mappingNamespace = Namespace.getNamespace(mapNs);
		eadNamespace = Namespace.getNamespace(eadNs);
	}
	
	public MappingParser(String mappingId, InputStream in) {
		this(mappingId);
		buildMappingDocument(in);
		extractObjectTypes(mappingDocument);
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
	
	public ObjectTypeMap extractObjectTypes(Document doc) {
		Filter<Element> filter = new ElementFilter("objectType", mappingNamespace);
		Iterator<Element> iterator = doc.getDescendants(filter);
		while (iterator.hasNext()) {
			Element objectType = iterator.next();
			String repoType = objectType.getAttributeValue("id");
			String cmisType = objectType.getTextTrim();
			objectTypeMap.addObjectType(repoType, cmisType);
		}
		return objectTypeMap;
	}
	
	public Map<String, Hook> extractHooks(Document doc) {
		Filter<Element> filter = new ElementFilter("template", mappingNamespace);
		
		return null;
	}
	
	public ObjectTypeMap getObjectTypeMap() {
		return objectTypeMap;
	}
}
