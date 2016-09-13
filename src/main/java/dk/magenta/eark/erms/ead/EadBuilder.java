package dk.magenta.eark.erms.ead;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.output.XMLOutputter;

import dk.magenta.eark.erms.Constants;

public class EadBuilder {
	
	public static final Namespace eadNs = Namespace.getNamespace(Constants.EAD_NAMESPACE); 
	
	private Document ead;
	private XmlHandler xmlHandler;
	private Element topLevelElement;
	
	/**
	 * 
	 * @param in
	 * @throws JDOMException Throws exception if the provided ead.xml is not valid
	 */
	public EadBuilder(InputStream in) throws JDOMException {
		xmlHandler = new XmlHandlerImpl();
		ead = xmlHandler.readAndValidateXml(in, "ead3.xsd");
		
		// Put into own method
		Element root = ead.getRootElement();
		Element archdesc = root.getChild("archdesc", eadNs);
		Element dsc = new Element("dsc", eadNs);
		dsc.setAttribute("desctype", "combined");
		archdesc.addContent(dsc);
		topLevelElement = dsc;
	}
	
	public void addCElement(Element c, Element parent) {
		parent.addContent(c);
	}
	
	public void addDaoElement(Element dao, Element parentCElement) {
		Element did = parentCElement.getChild("did", eadNs);
		did.addContent(dao);
	}
	
	public Element getTopLevelElement() {
		return topLevelElement;
	}
	
	/**
	 * For testing... TODO: move to XmlHandler
	 */
	public void writeXml(String filename) {
		try {
			FileWriter writer = new FileWriter(filename);
			XMLOutputter outputter = new XMLOutputter();
			outputter.output(ead, writer);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getValidationErrorMessage() {
		return xmlHandler.getErrorMessage();
	}
}
