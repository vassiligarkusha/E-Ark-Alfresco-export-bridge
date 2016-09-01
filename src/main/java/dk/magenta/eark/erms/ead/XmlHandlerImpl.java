package dk.magenta.eark.erms.ead;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaderJDOMFactory;
import org.jdom2.input.sax.XMLReaderXSDFactory;

public class XmlHandlerImpl implements XmlHandler {

	private String errorMessage;
	
	@Override
	public Document readXml(InputStream in) {
		SAXBuilder builder = new SAXBuilder();
		Document doc = null;
		try {
			doc = builder.build(in);
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return doc;
	}
	
	@Override
	public Document readAndValidateXml(InputStream in, String... schemas) throws JDOMException {

		List<Source> sources = new ArrayList<Source>();
		for (int i = 0; i < schemas.length; i++) {
			sources.add(new StreamSource(XmlHandlerImpl.class.getClassLoader().getResourceAsStream(schemas[i])));
		}
		Source[] sourcesArray = (Source[]) sources.toArray();
		
		Document doc = null;
		try {
			// NOTE: The order of the arguments in the constructor in the next line matters!! (which it should not)
			XMLReaderJDOMFactory schemaFactory = new XMLReaderXSDFactory(sourcesArray);
			SAXBuilder builder = new SAXBuilder(schemaFactory);
			doc = builder.build(in);
		} catch (JDOMException e) {
			errorMessage = e.getMessage();
			throw e;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return doc;
	}
	
	@Override
	public String getErrorMessage() {
		return errorMessage;
	}
}
