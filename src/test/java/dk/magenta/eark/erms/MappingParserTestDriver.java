package dk.magenta.eark.erms;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import dk.magenta.eark.erms.parser.MappingParser;
import dk.magenta.eark.erms.parser.XmlValidator;

public class MappingParserTestDriver {

	public static void main(String[] args) throws FileNotFoundException {
		
		InputStream in = MappingParser.class.getClassLoader().getResourceAsStream("mapping.xml");
		
		// FileInputStream f = new FileInputStream(new File("mapping.xml"));
		System.out.println(XmlValidator.isXmlValid(in));
	}

}
