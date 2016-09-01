package dk.magenta.eark.erms.ead;

import java.io.InputStream;

import org.jdom2.Document;
import org.jdom2.JDOMException;

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
	
}
