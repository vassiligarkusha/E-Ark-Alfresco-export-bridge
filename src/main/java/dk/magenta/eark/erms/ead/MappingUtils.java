package dk.magenta.eark.erms.ead;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.ElementFilter;
import org.jdom2.filter.Filter;

public class MappingUtils {

	/**
	 * Extracts all descending Elements from a Document or an Element
	 * 
	 * @param obj
	 *            Must be either a Document or an Element
	 * @param elementName
	 * @param namespace
	 * @return List of descending Elements
	 */
	static List<Element> extractElements(Object obj, String elementName, Namespace namespace) {
		Filter<Element> filter = new ElementFilter(elementName, namespace);
		Iterator<Element> iterator = null;
		if (obj instanceof Document) {
			iterator = ((Document) obj).getDescendants(filter);
		} else if (obj instanceof Element) {
			iterator = ((Element) obj).getDescendants(filter);
		} else {
			throw new RuntimeException("Cannot extract Elements from " + obj);
		}
		List<Element> elements = new LinkedList<Element>();
		while (iterator.hasNext()) {
			elements.add(iterator.next());
		}
		return elements;
	}
	
	/**
	 * For debugging - prints out a JDOM element (which does not contain other elements)
	 * @param e
	 */
	public static void printElement(Element e) {
		StringBuilder builder = new StringBuilder("<").append(e.getName()).append(" ");
		List<Attribute> attributes = e.getAttributes();
		for (Attribute a : attributes) {
			builder.append(a.getName()).append("=").append(a.getValue()).append(" ");
		}
		builder.append(">").append(e.getText()).append("</").append(e.getName()).append(">");
		System.out.println(builder.toString());
	}
}
