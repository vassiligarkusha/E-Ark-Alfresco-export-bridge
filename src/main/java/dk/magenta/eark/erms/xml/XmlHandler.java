package dk.magenta.eark.erms.xml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.output.XMLOutputter;

public interface XmlHandler {
	
	/**
	 * Generates JDOM document from an XML input stream. No validation is performed.
	 * @param in
	 * @return
	 */
	public Document readXml(InputStream in);
	
	/**
	 * Generates JDOM document from an XML input stream. Validation is performed according 
	 * to the provided XML schemas. A JDOMException is thrown if the XML is not valid.
	 * @param in
	 * @param schemas The schemas to validate against (NOTE: the must be provided in the correct order!). The 
	 * schemas must be located in the src/main/resources folder
	 * @return
	 * @throws JDOMException
	 */
	public Document readAndValidateXml(InputStream in, String... schemas) throws JDOMException;

	/**
	 * Gets the latest JDOM error message (e.g. a validation error message)
	 * @return
	 */
	public String getErrorMessage();
	
	/**
	 * Get the location of the generated EAD which have passed validation
	 * @return the {@link}Path to the generated and valid EAD 
	 */
	public Path getCandidateEad();
	
	/**
	 * Validates a JDOM document
	 * @param document
	 * @param schemaLocation path to the XML schema in the resources folder
	 * @return true if the document is valid and false otherwise
	 */
	public boolean isXmlValid(Document document, String schemaLocation);
	
	/**
	 * Write an XML element to System.out (for debugging)
	 * @param e
	 */
	public static void writeXml(Element e) {
		XMLOutputter outputter = new XMLOutputter();
		try {
			outputter.output(e, System.out);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	

	/**
	 * Write a JDOM document as an XML file (no validation)
	 * @param document
	 * @param filename
	 */
	public static void writeXml(Document document, String filename) {
		writeXml(document, Paths.get(filename));
	}
	
	
	public static void writeXml(Document document, Path filePath) {
		try {
			Path folderPath = filePath.getParent();
			if (!Files.isDirectory(folderPath)) {
				Files.createDirectories(folderPath);
			}
			File f = filePath.toFile();
			f.delete();
			FileWriter writer = new FileWriter(f);
			XMLOutputter outputter = new XMLOutputter();
			outputter.output(document, writer);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
}
