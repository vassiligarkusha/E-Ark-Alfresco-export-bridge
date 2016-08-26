package dk.magenta.eark.erms.parser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaderJDOMFactory;
import org.jdom2.input.sax.XMLReaderXSDFactory;

/**
 * For validating the mapping.xml provided by the end-user
 * @author Andreas Kring <andreas@magenta.dk>
 *
 */
public class XmlValidator {
	
	public static boolean isXmlValid(InputStream in) {
		
//		Source mappingXsdStream = new StreamSource(XmlValidator.class.getClassLoader().getResourceAsStream("mapping.xsd"));
//		Source eadXsdStream = new StreamSource(XmlValidator.class.getClassLoader().getResourceAsStream("ead3.xsd"));
		
		File mappingXsdFile = new File("/home/andreas/eark/E-Ark-Alfresco-export-bridge/src/main/resources/mapping.xsd");
		File eadXsdFile = new File("/home/andreas/eark/E-Ark-Alfresco-export-bridge/src/main/resources/ead3.xsd");
		
		boolean success = true;
		try { 
			XMLReaderJDOMFactory schemaFactory = new XMLReaderXSDFactory(mappingXsdFile, eadXsdFile);
			// XMLReaderJDOMFactory schemaFactory = new XMLReaderXSDFactory(mappingXsdStream, eadXsdStream);
			SAXBuilder builder = new SAXBuilder(schemaFactory);
			builder.build(in);
		} catch (JDOMException e) {
			success = false;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return success;
	}
}
