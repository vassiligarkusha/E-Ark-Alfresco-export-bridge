package dk.magenta.eark.erms.ead;

import java.io.InputStream;

import org.jdom2.Document;
import org.jdom2.JDOMException;

public class EadBuilder {
	
	private Document ead;
	private XmlHandler xmlHandler;
	private String topAggregationLevel;
	private String currentAggregationLevel;
	
	/**
	 * 
	 * @param in
	 * @throws JDOMException Throws exception if the provided ead.xml is not valid
	 */
	public EadBuilder(InputStream in, String topAggregationLevel) throws JDOMException {
		this.topAggregationLevel = topAggregationLevel;
		xmlHandler = new XmlHandlerImpl();
		ead = xmlHandler.readAndValidateXml(in, "ead3.xsd");
	}
	
	public void setCurrentAggregationLevel(String currentAggregationLevel) {
		this.currentAggregationLevel = currentAggregationLevel;
	}
	
	public String getCurrentAggregationLevel() {
		return currentAggregationLevel;
	}
	
}
