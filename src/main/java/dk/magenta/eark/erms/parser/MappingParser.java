package dk.magenta.eark.erms.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

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
// Should maybe be static
public class MappingParser {

	private static final String mapNs = "http://www.magenta.dk/eark/erms/mapping/1.0";
	private static final String eadNs = "http://ead3.archivists.org/schema/";
	
	private ObjectTypeMap objectTypeMap;
	private Namespace mappingNamespace;
	private Namespace eadNamespace;
	
	public MappingParser() {
		objectTypeMap = new ObjectTypeMap();
		mappingNamespace = Namespace.getNamespace(mapNs);
		eadNamespace = Namespace.getNamespace(eadNs);
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
		return xml;
	}
	
	public ObjectTypeMap extractObjectTypes(Document doc) {
		Filter filter = new ElementFilter("objectType", mappingNamespace);
		Iterator iterator = doc.getDescendants(filter);
		while (iterator.hasNext()) {
			Element objectType = (Element) iterator.next();
			String repoType = objectType.getAttributeValue("id");
			String cmisType = objectType.getTextTrim();
			objectTypeMap.addObjectType(repoType, cmisType);
		}
		return objectTypeMap;
	}
	
}
