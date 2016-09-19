package dk.magenta.eark.erms.ead;

import java.io.InputStream;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;

import dk.magenta.eark.erms.Constants;

public class EadBuilder {
	
	public static final Namespace eadNs = Namespace.getNamespace(Constants.EAD_NAMESPACE); 
	
	private Document ead;
	private XmlHandler xmlHandler;
	private Element topLevelElement;
	
	/**
	 * Constructor
	 * @param in the EAD template
	 * @throws JDOMException Throws exception if the provided ead template is not valid
	 */
	public EadBuilder(InputStream in, XmlHandler xmlHandler) throws JDOMException {
		this.xmlHandler = xmlHandler;
		ead = xmlHandler.readAndValidateXml(in, "ead3.xsd"); // This responsibility should be moved away from the EadBuilder
		
		// Put into own method
		Element root = ead.getRootElement();
		Element archdesc = root.getChild("archdesc", eadNs);
		Element dsc = new Element("dsc", eadNs);
		dsc.setAttribute("dsctype", "combined");
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
	
	public Document getEad() {
		return ead;
	}
	
	public String getValidationErrorMessage() {
		return xmlHandler.getErrorMessage();
	}
}
