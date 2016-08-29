package dk.magenta.eark.erms.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;
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
	
	private static final String MAPPING_XSD = "mapping.xsd";
	private static final String EAD_XSD = "ead3.xsd";
	private String error;
	
	public boolean isXmlValid(InputStream in) {
		
		Source mappingXsdStream = new StreamSource(XmlValidator.class.getClassLoader().getResourceAsStream(XmlValidator.MAPPING_XSD));
		Source eadXsdStream = new StreamSource(XmlValidator.class.getClassLoader().getResourceAsStream(XmlValidator.EAD_XSD));
			
		boolean success = true;
		try {
			// NOTE: The order of the arguments in the constructor in the next line matters!! (which it should not)
			XMLReaderJDOMFactory schemaFactory = new XMLReaderXSDFactory(eadXsdStream, mappingXsdStream);
			SAXBuilder builder = new SAXBuilder(schemaFactory);
			builder.build(in);
		} catch (JDOMException e) {
			error = e.getMessage();
			success = false;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return success;
		
	}
	
	public String getErrorMessage() {
		return error;
	}
}
