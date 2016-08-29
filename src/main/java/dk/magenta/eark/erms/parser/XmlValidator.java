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
	
	public static boolean isXmlValid(InputStream in) {
		
		// TODO: should be working with Source objects - but it's not...
//		Source mappingXsdStream = new StreamSource(XmlValidator.class.getClassLoader().getResourceAsStream("mapping.xsd"));
//		Source eadXsdStream = new StreamSource(XmlValidator.class.getClassLoader().getResourceAsStream("ead3.xsd"));

		// Work around 
		File mappingXsdTempFile = null;
		File eadXsdTempFile = null;
		try {
			mappingXsdTempFile = File.createTempFile("mapping", ".xsd");
			eadXsdTempFile = File.createTempFile("ead3", ".xsd");
			FileOutputStream out1 = new FileOutputStream(mappingXsdTempFile);
			FileOutputStream out2 = new FileOutputStream(eadXsdTempFile);
			IOUtils.copy(XmlValidator.class.getClassLoader().getResourceAsStream("mapping.xsd"), out1);
			IOUtils.copy(XmlValidator.class.getClassLoader().getResourceAsStream("ead3.xsd"), out2);
			out1.close();
			out2.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(mappingXsdTempFile.getAbsolutePath());
		
//		File mappingXsdFile = new File(mappingXsdTempFile.getAbsolutePath());
//		File eadXsdFile = new File(eadXsdTempFile.getAbsolutePath());

//		File mappingXsdFile = new File("/home/andreas/eark/E-Ark-Alfresco-export-bridge/src/main/resources/mapping.xsd");
//		File eadXsdFile = new File("/home/andreas/eark/E-Ark-Alfresco-export-bridge/src/main/resources/ead3.xsd");

		File mappingXsdFile = new File("/tmp/mapping8223386477909649322xsd");
		File eadXsdFile = new File("/tmp/ead31569627369953784711xsd");

//		File mappingXsdFile = new File("/home/andreas/eark/E-Ark-Alfresco-export-bridge/src/main/resources/mapping8223386477909649322xsd");
//		File eadXsdFile = new File("/home/andreas/eark/E-Ark-Alfresco-export-bridge/src/main/resources/ead31569627369953784711xsd");

		
		
		/*
		try {
			BufferedReader reader = new BufferedReader(new FileReader(mappingXsdFile));
			String line = reader.readLine();
			while (line != null) {
				System.out.println(line);
				line = reader.readLine();
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		*/
		
		boolean success = true;
		try { 
			XMLReaderXSDFactory schemaFactory = new XMLReaderXSDFactory(mappingXsdFile, eadXsdFile);
//			XMLReaderJDOMFactory schemaFactory = new XMLReaderXSDFactory(mappingXsdStream, eadXsdStream);
			SAXBuilder builder = new SAXBuilder(schemaFactory);
			builder.build(in);
		} catch (JDOMException e) {
			e.printStackTrace();
			success = false;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return success;
		
	}
}
