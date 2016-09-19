package dk.magenta.eark.erms.ead;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
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
		Source[] sourcesArray = new Source[sources.size()];
		sourcesArray = sources.toArray(sourcesArray);		
		
		Document doc = null;
		try {
			// NOTE: The order of the arguments in the constructor in the next line matters!! (which they should not)
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
	public boolean isXmlValid(Document document, String schemaLocation) {
		Path tmp = Paths.get(System.getProperty("java.io.tmpdir"), "candidate_ead.xml");
		XmlHandler.writeXml(document, tmp);
		boolean success = true;
		try {
			InputStream in = new FileInputStream(tmp.toFile());
			readAndValidateXml(in, schemaLocation);
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JDOMException e) {
			errorMessage = e.getMessage();
			success = false;
		}
		return success;
	}
	
	@Override
	public String getErrorMessage() {
		return errorMessage;
	}
}
