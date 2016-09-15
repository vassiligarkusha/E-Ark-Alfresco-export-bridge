package dk.magenta.eark.erms.repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
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
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.jdom2.Element;
import org.jdom2.JDOMException;

import dk.magenta.eark.erms.Constants;
import dk.magenta.eark.erms.ead.EadBuilder;
import dk.magenta.eark.erms.ead.MappingParser;
import dk.magenta.eark.erms.ead.MetadataMapper;
import dk.magenta.eark.erms.ead.XmlHandler;
import dk.magenta.eark.erms.json.JsonUtils;

// Let's not make this an interface for now
public class ExtractionWorker {

	private JsonObject json;
	private Session session;
	private MappingParser mappingParser;
	private MetadataMapper metadataMapper;
	private EadBuilder eadBuilder;
	private FileExtractor fileExtractor;
	private Set<String> excludeList;
	private CmisPathHandler cmisPathHandler;
	private boolean removeFirstDaoElement;

	public ExtractionWorker(JsonObject json, CmisSessionWorker cmisSessionWorker) {
		this.json = json;
		session = cmisSessionWorker.getSession();
		metadataMapper = new MetadataMapper();
		removeFirstDaoElement = true;
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
			InputStream mappingInputStream = new FileInputStream(
					new File("/home/andreas/eark/E-Ark-Alfresco-export-bridge/src/main/resources/mapping.xml")); // TODO:
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

		// Get the exportPath and create the FileExtractor
		fileExtractor = new FileExtractor(Paths.get(json.getString(Constants.EXPORT_PATH)), session);
		
		// Create EadBuilder
		try {
			// TODO: change this! - uploading of the EAD template should be
			// handled elsewhere
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

		// Start traversing the CMIS tree
		JsonArray exportList = json.getJsonArray(Constants.EXPORT_LIST);
		for (int i = 0; i < exportList.size(); i++) {
			// We know (assume) that the values in the exportList are strings

			// Get the CMIS object
			String objectId = exportList.getString(i);
			CmisObject cmisObject = session.getObject(objectId);

			// Get the CMIS types
			String cmisType = cmisObject.getType().getId();
			String semanticType = mappingParser.getSemanticTypeFromCmisType(cmisType);

			// Store the parent path for this current top-level node in the
			// CmisPathHandler
			Folder cmisFolder = (Folder) cmisObject;
			String folderPath;
			if (cmisFolder.isRootFolder()) {
				folderPath = cmisFolder.getPath();
			} else {
				folderPath = cmisFolder.getFolderParent().getPath();
			}
			cmisPathHandler = new CmisPathHandler(folderPath);

			// Get element for the current node in the exportList and add to
			// EAD
			Element c = metadataMapper.mapCElement(cmisObject, mappingParser.getHooksFromSemanticType(semanticType),
					mappingParser.getCElementFromSemanticType(semanticType));
			eadBuilder.addCElement(c, eadBuilder.getTopLevelElement());

			// This way of traversing the CMIS tree follows the example given in
			// the official documentation - see
			// http://chemistry.apache.org/java/developing/guide.html
			
			for (Tree<FileableCmisObject> tree : cmisFolder.getDescendants(-1)) {
				if (mappingParser.isLeaf(cmisType)) {
					handleLeafNodes(tree, c, cmisType, cmisFolder.getPath());
				} else {
					handleNode(tree, c);
				}
			}
		}

		// For debugging
		XmlHandler.writeXml(eadBuilder.getEad(), "/tmp/ead.xml");

		builder.add(Constants.SUCCESS, true);
		return builder.build();
	}

	private void handleNode(Tree<FileableCmisObject> tree, Element parent) {
		// System.out.println(tree.getItem().getId());

		CmisObject node = tree.getItem();
		String childObjectId = node.getId(); // E.g. a nodeRef in Alfresco...
		if (!excludeList.contains(childObjectId)) {

			// Get the CMIS object type id
			String cmisType = node.getType().getId();

			if (isObjectTypeInSematicStructure(cmisType)) {
				Element c = metadataMapper.mapCElement(node, mappingParser.getHooksFromCmisType(cmisType),
						mappingParser.getCElementFromCmisType(cmisType));
				eadBuilder.addCElement(c, parent);
				if (!mappingParser.isLeaf(cmisType)) {
					for (Tree<FileableCmisObject> children : tree.getChildren()) {
						handleNode(children, c);
					}
				} else {
					removeFirstDaoElement = true;
					// Flatten the folder/file structure below here and store
					// the metadata in <dao> elements

					// Get CMIS path for the semantic leaf (e.g. for the
					// "record")
					String cmisPath = ((Folder) node).getPath();

					// If no children -> remove dao element from c element
					List<Tree<FileableCmisObject>> children = tree.getChildren();
					if (children.isEmpty()) {
						metadataMapper.removeDaoElements(c);
					} else {
						for (Tree<FileableCmisObject> child : children) {
							handleLeafNodes(child, c, cmisType, cmisPath);
						}
					}

					// Make variable (hardcode) containing path to store EAD and
					// files // TODO: fix this
					// traverse rest of cmis tree

				}

			}

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

	private void handleLeafNodes(Tree<FileableCmisObject> tree, Element semanticLeaf, String semanticLeafCmisType,
			String parentPath) {
		
		CmisObject node = tree.getItem();
		String cmisObjectTypeId = node.getId();

		if (!excludeList.contains(cmisObjectTypeId)) {
			if (node.getBaseTypeId().equals(BaseTypeId.CMIS_DOCUMENT)) {
				String pathToParentFolder = cmisPathHandler.getRelativePath(parentPath);
				String pathToNode = pathToParentFolder + "/" + node.getName();

				// Create <dao> element
				Element dao = metadataMapper.mapDaoElement(node,
						mappingParser.getHooksFromCmisType(semanticLeafCmisType), semanticLeaf, pathToNode);
				// MappingUtils.printElement(dao);

				// Insert <dao> element into <c> element
				if (removeFirstDaoElement) {
					// The first <dao> element is the one from the template -
					// must be removed
					metadataMapper.removeDaoElements(semanticLeaf);
					removeFirstDaoElement = false;
				}
				eadBuilder.addDaoElement(dao, semanticLeaf);
				
				// Extract the file contents
				Path filePath = Paths.get(pathToNode);
				try {
					fileExtractor.writeCmisDocument(filePath, cmisObjectTypeId);
				} catch (IOException e) {
					// TODO: create JSON
					e.printStackTrace();
				}
								

			} else if (node.getBaseTypeId().equals(BaseTypeId.CMIS_FOLDER)) {
				String pathToNode = ((Folder) node).getPath();
				for (Tree<FileableCmisObject> child : tree.getChildren()) {
					handleLeafNodes(child, semanticLeaf, semanticLeafCmisType, pathToNode);
				}
			} else {
				// Not handled...
			}
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
