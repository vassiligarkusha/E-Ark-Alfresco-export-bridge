package dk.magenta.eark.erms.ead;

import java.io.InputStream;

import org.jdom2.Document;
import org.jdom2.JDOMException;

public class EadBuilder {
	
	private Document ead;
	private XmlHandler xmlHandler;
	
	/**
	 * 
	 * @param in
	 * @throws JDOMException Throws exception if the provided ead.xml is not valid
	 */
	public EadBuilder(InputStream in) throws JDOMException {
		xmlHandler = new XmlHandlerImpl();
		ead = xmlHandler.readAndValidateXml(in, "ead3.xsd");
	}
	
	
	
}
