package dk.magenta.eark.erms.ead;

import java.nio.file.Path;
import java.util.List;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.ElementFilter;
import org.jdom2.filter.Filters;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

import dk.magenta.eark.erms.Constants;

public class MetadataMapper {

	private Namespace ead;
	private XPathFactory factory;
	
	public MetadataMapper() {
		ead = Namespace.getNamespace("ead", Constants.EAD_NAMESPACE);
		factory = XPathFactory.instance();
	}
	
	public Element mapCElement(CmisObject cmisObj, List<Hook> hooks, Element c) {
		Element clone = c.clone();
		for (Hook hook : hooks) {
			String cmisPropertyId = hook.getCmisPropertyId();
			String xpath = hook.getXpath();
			String value = cmisObj.getProperty(cmisPropertyId).getValueAsString();

			findXmlNodeAndInsertCmisData(xpath, value, clone);
		}
		return clone;
	}
	
	/**
	 * Create a dao element to be used in the EAD for a semantic leaf node 
	 * @param cmisObj the current sub-leaf node
	 * @param hooks
	 * @param c the leaf node that should contain the dao element
	 * @return the CMIS data filled out dao element
	 */
	public Element mapDaoElement(CmisObject cmisObj, List<Hook> hooks, Element c, Path filePath) {
		// The c element MUST contain a dao element
		Element clone = c.clone();
		for (Hook hook : hooks) {
			String xpath = hook.getXpath();
			// This may break if there is also daoset elements
			if (xpath.contains("dao")) {
				String cmisPropertyId = hook.getCmisPropertyId();
				String value = cmisObj.getProperty(cmisPropertyId).getValueAsString();

				findXmlNodeAndInsertCmisData(xpath, value, clone);
			}
		}
		Element dao = MappingUtils.extractElements(clone, "dao", ead).get(0).clone();
		
		return dao;
	}
	
	public void removeDaoElements(Element c) {
		Element did = c.getChild("did", ead);
		
		ElementFilter filter = new ElementFilter("dao", ead);
		did.removeContent(filter);
		filter = new ElementFilter("daoset", ead);
		did.removeContent(filter);
	}
	
	private void findXmlNodeAndInsertCmisData(String xpath, String value, Element cClone) {
		if (xpath.contains("attribute")) {
			XPathExpression<Attribute> expression = factory.compile(xpath, Filters.attribute(), null, ead);
			Attribute target = expression.evaluate(cClone).get(0);
			target.setValue(value);
		} else {
			XPathExpression<Element> expression = factory.compile(xpath, Filters.element(), null, ead);
			Element target = expression.evaluate(cClone).get(0);
			target.setText(value);
		}
		
	}
}
