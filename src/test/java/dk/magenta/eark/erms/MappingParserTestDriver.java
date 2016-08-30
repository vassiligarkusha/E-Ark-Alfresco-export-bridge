package dk.magenta.eark.erms;

import java.io.InputStream;

import org.jdom2.Document;

import dk.magenta.eark.erms.parser.MappingParser;
import dk.magenta.eark.erms.parser.ObjectTypeMap;

public class MappingParserTestDriver {

	public static void main(String[] args) {
		InputStream in = MappingParser.class.getClassLoader().getResourceAsStream("mapping.xml");
		MappingParser mp = new MappingParser();
		Document doc = mp.buildMappingDocument(in);
		ObjectTypeMap objectTypeMap = mp.extractObjectTypes(doc);
		
		System.out.println(objectTypeMap);

	}

}
