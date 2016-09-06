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
	private String topAggregationLevel;
	private String currentAggregationLevel; // Maybe not needed...
	private Element currentElementAggregationLevel;
	
	
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
		currentElementAggregationLevel = dsc;
	}
	
	public void addCElement(Element c, String aggregationLevel) {
		currentElementAggregationLevel.addContent(c);
		currentElementAggregationLevel = c;
	}
	
	public void setTopAggregationLevel(String topAggregationLevel) {
		this.topAggregationLevel = topAggregationLevel;
	}
	
	public void setCurrentAggregationLevel(String currentAggregationLevel) {
		this.currentAggregationLevel = currentAggregationLevel;
	}
	
	public String getCurrentAggregationLevel() {
		return currentAggregationLevel;
	}
	
	public String getValidationErrorMessage() {
		return xmlHandler.getErrorMessage();
	}
}
