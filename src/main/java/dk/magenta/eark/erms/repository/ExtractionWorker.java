package dk.magenta.eark.erms.repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.TreeSet;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.FileableCmisObject;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.Tree;
import org.jdom2.Element;
import org.jdom2.JDOMException;

import dk.magenta.eark.erms.Constants;
import dk.magenta.eark.erms.ead.EadBuilder;
import dk.magenta.eark.erms.ead.MappingParser;
import dk.magenta.eark.erms.ead.MetadataMapper;
import dk.magenta.eark.erms.json.JsonUtils;

// Let's not make this an interface for now
public class ExtractionWorker {

	private JsonObject json;
	private Session session;
	private MappingParser mappingParser;
	private MetadataMapper metadataMapper;
	private EadBuilder eadBuilder;
	private Set<String> excludeList;

	public ExtractionWorker(JsonObject json, CmisSessionWorker cmisSessionWorker) {
		this.json = json;
		session = cmisSessionWorker.getSession();
		metadataMapper = new MetadataMapper();
	}

	/**
	 * Performs the extraction process
	 * 
	 * @param json
	 *            the request JSON
	 * @param cmisSessionWorker
	 * @return JSON object describing the result
	 */
	JsonObject extract() {

		JsonObjectBuilder builder = Json.createObjectBuilder();

		// Get the mapping
		String mapName = json.getString(Constants.MAP_NAME);
		try {
			InputStream mappingInputStream = new FileInputStream(new File("/home/andreas/.erms/mappings/mapping.xml")); // TODO:
																														// Change
																														// this!
			mappingParser = new MappingParser(mapName, mappingInputStream);
			mappingInputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return JsonUtils.addErrorMessage(builder, "Mapping file not found!").build();
		} catch (java.io.IOException e) {
			e.printStackTrace();
			return JsonUtils.addErrorMessage(builder, "An I/O error occured while handling the mapping file!").build();
		}

		// Load the excludeList into a TreeSet in order to make searching the
		// list fast
		JsonArray excludeList = json.getJsonArray(Constants.EXCLUDE_LIST);
		this.excludeList = new TreeSet<String>();
		for (int i = 0; i < excludeList.size(); i++) {
			this.excludeList.add(excludeList.getString(i));
		}

		// Create EadBuilder
		try {
			// TODO: change this! - uploading of the EAD template should be
			// handles elsewhere
			InputStream eadInputStream = new FileInputStream(new File("/home/andreas/.erms/mappings/ead_template.xml"));
			eadBuilder = new EadBuilder(eadInputStream);
			eadInputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return JsonUtils.addErrorMessage(builder, "EAD template file not found!").build();
		} catch (JDOMException e) {
			builder.add("validationError", eadBuilder.getValidationErrorMessage());
			return JsonUtils.addErrorMessage(builder, "EAD template file not valid according to ead3.xsd").build();
		} catch (IOException e) {
			e.printStackTrace();
			return JsonUtils.addErrorMessage(builder, "An I/O error occured while handling the EAD template file!")
					.build();
		}

		// Start iteration over the CMIS tree
		JsonArray exportList = json.getJsonArray(Constants.EXPORT_LIST);
		for (int i = 0; i < exportList.size(); i++) {
			// We know (assume) that the values are strings

			// Get the CMIS object
			String objectId = exportList.getString(i);
			CmisObject cmisObject = session.getObject(objectId);

			// Get the CMIS types
			String cmisType = cmisObject.getType().getId();
			String semanticType = mappingParser.getSemanticTypeFromCmisType(cmisType);

			// Set the top-level aggregation level (we assume that all nodes in the exportList are at the same level)
			eadBuilder.setTopAggregationLevel(cmisType);
			
			Element c = metadataMapper.map(cmisObject, mappingParser.getHooksFromSemanticType(semanticType),
					mappingParser.getCElementFromSemanticType(semanticType));
			
			// write to EAD

			Folder folder = (Folder) cmisObject;
			for (Tree<FileableCmisObject> tree : folder.getDescendants(-1)) {
				handleNode(tree);
			}
		}

		return null;
	}

	private void handleNode(Tree<FileableCmisObject> tree) {
		// System.out.println(tree.getItem().getId());
		CmisObject node = tree.getItem();

		// EAD: update current aggregation level
		// eadBuilder.setCurrentAggregationLevel(node.getType().getId());

		String childObjectId = node.getId();
		if (!excludeList.contains(childObjectId)) {
			// System.out.println("not in list...");
			// Build EAD

			// Get the CMIS object type id
			String objectTypeId = node.getType().getId();
			if (isObjectTypeInSematicStructure(objectTypeId)) {
				// extract data to EAD

			} else {
				// for each child
				// handleNode()
			}

			/*
			 * if node.objectType in semantic structure: if node is not semantic
			 * leaf insert metadata into EAD for t in tree.getChildren:
			 * handleNode(t)
			 * 
			 * else
			 */

			// Mapping mapping = null;
			// try {
			// mapping = dbConnectionStrategy.getMapping("LocalTest");
			// } catch (SQLException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// System.out.println(mapping.getSyspath());
		}
	}

	private boolean isObjectTypeInSematicStructure(String objectTypeId) {
		Set<String> cmisObjectTypes = mappingParser.getObjectTypes().getAllCmisTypes();
		if (cmisObjectTypes.contains(objectTypeId)) {
			return true;
		}
		return false;
	}

	// TODO: we will need something like the below later one
	// Observer design pattern etc...

	// /**
	// * Gets the status of the extraction process
	// * @return
	// */
	// public JsonObject getStatus() {
	// return null;
	// }

}
