package dk.magenta.eark.erms.parser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

public class MappingParser {

	public Document buildMappingDocument(InputStream in) {
		SAXBuilder builder = new SAXBuilder();
		builder.setValidation(true);
		builder.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", 
				"http://www.w3.org/2001/XMLSchema");
		builder.setProperty("http://java.sun.com/xml/jaxp/properties/schemaSource", new File("mapping.xsd"));
		Document xml;
		try {
			xml = builder.build(in);
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
