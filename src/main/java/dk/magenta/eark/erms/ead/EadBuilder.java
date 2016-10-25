package dk.magenta.eark.erms.ead;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;

import dk.magenta.eark.erms.Constants;
import dk.magenta.eark.erms.xml.XmlHandler;

public class EadBuilder {
	
	public static final Namespace eadNs = Namespace.getNamespace(Constants.EAD_NAMESPACE); 
	
	private Document ead;
	private XmlHandler xmlHandler;
	private Element topLevelElement;
	private MappingParser mappingParser;
	
	/**
	 * Constructor
	 * @param in the EAD template
	 * @throws JDOMException Throws exception if the provided ead template is not valid
	 */
	public EadBuilder(InputStream in, XmlHandler xmlHandler, MappingParser mappingParser) throws JDOMException {
		this.xmlHandler = xmlHandler;
		this.mappingParser = mappingParser;
		ead = xmlHandler.readAndValidateXml(in, "ead3.xsd"); // This responsibility should be moved away from the EadBuilder
		
		buildControlSection();
		topLevelElement = buildArchdescSection();
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
	
	private void buildControlSection() {
		// Generate UUID for the <recordid>
		Element recordid = MappingUtils.extractElements(ead, "recordid", eadNs).get(0);
		recordid.setText(UUID.randomUUID().toString());
		
		// Set <titleproper> (in <filedesc> section) to same value as <agencyname> from the mapping
		Element titleproper = MappingUtils.extractElements(ead, "titleproper", eadNs).get(0);
		titleproper.setText(mappingParser.getAgencyName());
		
		// Set <agencyname> (in <maintenanceagency>) to <agencyname> from the mapping
		Element agencyname = MappingUtils.extractElements(ead, "agencyname", eadNs).get(0);
		agencyname.setText(mappingParser.getAgencyName());
		
		// Set <eventdatetime>
		Date now = new Date();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss z");
		Element eventdatetime = MappingUtils.extractElements(ead, "eventdatetime", eadNs).get(0);
		eventdatetime.setText(simpleDateFormat.format(now));
	}
	
	/**
	 * Put the content of <abstract> from the mapping XML file into the <archdesc> 
	 * @return The <dsc> element, i.e. the topLevelElement for further EAD building
	 */
	private Element buildArchdescSection() {
		// Set <abstract> (in the <did> section)
		Element abstractElement = MappingUtils.extractElements(ead, "abstract", eadNs).get(0);
		abstractElement.setText(mappingParser.getAbstract());
		
		// Add <dsc> element and return this as the topLevelElement
		Element archdesc = MappingUtils.extractElements(ead, "archdesc", eadNs).get(0);
		Element dsc = new Element("dsc", eadNs);
		archdesc.addContent(dsc);

		return dsc;
	}
}
