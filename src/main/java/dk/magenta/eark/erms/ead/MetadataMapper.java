package dk.magenta.eark.erms.ead;

import java.io.IOException;
import java.util.List;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.output.XMLOutputter;
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
	
	public Element map(CmisObject cmisObj, List<Hook> hooks, Element c) {
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
	
	
	
	
}
