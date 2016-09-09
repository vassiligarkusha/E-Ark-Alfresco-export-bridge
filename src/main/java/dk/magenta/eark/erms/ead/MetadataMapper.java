package dk.magenta.eark.erms.ead;

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
import dk.magenta.eark.erms.repository.CmisPathHandler;

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

			if (xpath.contains("attribute")) {
				XPathExpression<Attribute> expression = factory.compile(xpath, Filters.attribute(), null, ead);
				Attribute target = expression.evaluate(clone).get(0);
				target.setValue(value);
			} else {
				XPathExpression<Element> expression = factory.compile(xpath, Filters.element(), null, ead);
				Element target = expression.evaluate(clone).get(0);
				target.setText(value);
			}
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
	public Element mapDaoElement(CmisObject cmisObj, List<Hook> hooks, Element c, CmisPathHandler pathHandler) {

		return null;
	}
	
	public void removeDaoElements(Element c) {
		Element did = c.getChild("did", ead);
		
		ElementFilter filter = new ElementFilter("dao", ead);
		did.removeContent(filter);
		filter = new ElementFilter("daoset", ead);
		did.removeContent(filter);
	}
}
