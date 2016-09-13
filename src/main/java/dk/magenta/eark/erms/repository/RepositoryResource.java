package dk.magenta.eark.erms.repository;

import dk.magenta.eark.erms.Constants;
import dk.magenta.eark.erms.db.DatabaseConnectionStrategy;
import dk.magenta.eark.erms.db.JDBCConnectionStrategy;
import dk.magenta.eark.erms.ead.MappingParser;
import dk.magenta.eark.erms.exceptions.ErmsRuntimeException;
import dk.magenta.eark.erms.json.JsonUtils;
import dk.magenta.eark.erms.repository.profiles.Profile;
import dk.magenta.eark.erms.system.PropertiesHandlerImpl;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author lanre.
 */


@Path("repository")
public class RepositoryResource {
    private final Logger logger = LoggerFactory.getLogger(RepositoryResource.class);
    private static Map<String, Set<String>> mapObjectTypes = new HashMap<>();
    private Cmis1Connector cmis1Connector;
    private DatabaseConnectionStrategy dbConnectionStrategy;

    public static final String FOLDER_OBJECT_ID = "folderObjectId";
    public static final String DOCUMENT_OBJECT_ID = "documentObjectId";
    public static final String MAP_NAME = "mapName";

    public RepositoryResource() {

        try {
            this.cmis1Connector = new Cmis1Connector();
            this.dbConnectionStrategy = new JDBCConnectionStrategy(new PropertiesHandlerImpl("settings.properties"));
        } catch (SQLException sqe) {
            System.out.println("====> Error <====\nUnable to acquire db resource due to: " + sqe.getMessage());
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("connect")
    public JsonObject connect(JsonObject json) {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        JsonObject response;
        if (json.containsKey(Profile.NAME) && json.containsKey(MAP_NAME) ) {
            String profileName = json.getString(Profile.NAME);
            String mapName = json.getString(MAP_NAME);

            try {
                //Get a session worker
                CmisSessionWorker sessionWorker =this.getSessionWorker(profileName, mapName);

                //Build the json for the repository info
                response = sessionWorker.getRepositoryInfo();
                builder.add("repositoryInfo", response);
                builder.add("rootFolder", sessionWorker.getRootFolder());

            } catch (Exception e) {
                builder.add(Constants.SUCCESS, false);
                builder.add(Constants.ERRORMSG, e.getMessage());
            }

            builder.add(Constants.SUCCESS, true);

        } else {
            builder.add(Constants.SUCCESS, false);
            builder.add(Constants.ERRORMSG, "The connection profile does not have a name!");
        }

        return builder.build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("getDocument")
    public JsonObject Document(JsonObject json) {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        if (json.containsKey(DOCUMENT_OBJECT_ID) && json.containsKey(Profile.NAME) && json.containsKey(MAP_NAME)) {
            String profileName = json.getString(Profile.NAME);
            String documentObjectId = json.getString(DOCUMENT_OBJECT_ID);
            String mapName = json.getString(MAP_NAME);
            boolean includeContentStream = json.getBoolean("includeContentStream", false);

            try {
                //Get a session worker
                CmisSessionWorker sessionWorker = this.getSessionWorker(profileName, mapName);

                //Build the json for the repository info
                builder.add("document", sessionWorker.getDocument(documentObjectId, includeContentStream));

            } catch (Exception e) {
                builder.add(Constants.SUCCESS, false);
                builder.add(Constants.ERRORMSG, e.getMessage());
            }

            builder.add(Constants.SUCCESS, true);

        } else {
            builder.add(Constants.SUCCESS, false);
            builder.add(Constants.ERRORMSG, "The connection profile does not have a name!");
        }

        return builder.build();
    }

    /**
     * Just returns a folder object
     * @param json
     * @return
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("getFolder")
    public JsonObject getFolder(JsonObject json) {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        if (json.containsKey(FOLDER_OBJECT_ID) && json.containsKey(Profile.NAME) && json.containsKey(MAP_NAME)) {

            String profileName = json.getString(Profile.NAME);
            String folderObjectId = json.getString(FOLDER_OBJECT_ID);
            String mapName = json.getString(MAP_NAME);

            try {
                CmisSessionWorker cmisSessionWorker = this.getSessionWorker(profileName, mapName);
                String objTest =  objectSetToQueryString(mapObjectTypes.get(mapName));

                //Build the json for the repository info
                builder.add("folder", cmisSessionWorker.getFolder(folderObjectId, mapObjectTypes.get(mapName)));

            } catch (Exception e) {
                builder.add(Constants.SUCCESS, false);
                builder.add(Constants.ERRORMSG, e.getMessage());
            }

            builder.add(Constants.SUCCESS, true);

        } else {
            builder.add(Constants.SUCCESS, false);
            builder.add(Constants.ERRORMSG, "The connection profile does not have a name!");
        }

        return builder.build();
    }

    /**
     *
     * @param profileName
     * @param objectId
     * @return
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("isroot/{objectId}/in/{profileName}")
    public JsonObject isROOT(@PathParam("objectId") String objectId, @PathParam("profileName") String profileName) {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        if (StringUtils.isNotBlank(objectId) && StringUtils.isNotBlank(profileName)) {
            try {
                profileName = URLDecoder.decode(profileName, "UTF-8");
                objectId = URLDecoder.decode(objectId, "UTF-8");
                CmisSessionWorker cmisSessionWorker = this.getSessionWorker(profileName,null);
                JsonObject rootFolder = cmisSessionWorker.getRootFolder();
                String repoRoot =  rootFolder.getJsonObject("properties").getString("objectId") ;

                //Build the json for the repository info
                builder.add("isRoot", objectId.equalsIgnoreCase(repoRoot));

            } catch (Exception e) {
                builder.add(Constants.SUCCESS, false);
                builder.add(Constants.ERRORMSG, e.getMessage());
            }

            builder.add(Constants.SUCCESS, true);

        } else {
            builder.add(Constants.SUCCESS, false);
            builder.add(Constants.ERRORMSG, "One or more parameters missing or malformed");
        }

        return builder.build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("extract")
    public JsonObject extract(JsonObject json) {
        JsonObjectBuilder builder = Json.createObjectBuilder();

        // TODO: the JsonUtils methods below should not take a builder as an
        // argument...

        // Check if the mandatory keys are in the request JSON
        String[] mandatoryJsonKeys = { Profile.NAME, Constants.MAP_NAME, Constants.EXPORT_LIST,
                Constants.EXCLUDE_LIST };
        if (!JsonUtils.containsCorrectKeys(json, mandatoryJsonKeys)) {
            JsonUtils.addKeyErrorMessage(builder, mandatoryJsonKeys);
            return builder.build();
        }

        // Check that the profile name and the mapping name are not blank
        if (!StringUtils.isNotBlank(json.getString(Profile.NAME))
                || !StringUtils.isNotBlank(json.getString(Constants.MAP_NAME))) {
            builder.add(Constants.SUCCESS, false);
            builder.add(Constants.ERRORMSG, "Blank values are not allowed in the request JSON");
            return builder.build();
        }

        // Check that the exportList is a none-empty array
        if (!JsonUtils.isArrayNoneEmpty(json, Constants.EXPORT_LIST)) {
            JsonUtils.addArrayErrorMessage(builder, Constants.EXPORT_LIST);
            return builder.build();
        }

        // Check that the excludeList is an array
        if (!JsonUtils.isArray(json, Constants.EXCLUDE_LIST)) {
            JsonUtils.addArrayErrorMessage(builder, Constants.EXCLUDE_LIST);
            return builder.build();
        }

        // Everything OK in the request JSON - begin extraction
        String profileName = json.getString(Profile.NAME),  mapName = json.getString(Constants.MAP_NAME);
        ExtractionWorker extractionWorker = new ExtractionWorker(json, getSessionWorker(profileName, mapName));

        return extractionWorker.extract();
    }

    /**
     * Returns a cmis session worker instance given a profile name
     * @param profileName
     * @return
     */
    private CmisSessionWorker getSessionWorker(String profileName, String mapName){
        try{
            //First let's see if we have the cmis object types already saved in the set
            if(!mapObjectTypes.containsKey(mapName)){
                MappingParser mappingParser = new MappingParser(mapName);
                mapObjectTypes.put(mapName,mappingParser.getObjectTypeFromMap());
            }

            //Retrieve the connection profile
            Profile connProfile = this.dbConnectionStrategy.getProfile(profileName);
            //Get a CMIS session object
            Session repoSession = this.cmis1Connector.getSession(connProfile);
            //Instantiate a session worker
            return new CmisSessionWorkerImpl(repoSession);
        }
        catch(Exception ge){
            logger.error("Unable to create session worker due to: " + ge.getMessage());
            throw new ErmsRuntimeException(ge.getMessage());
        }
    }

    /**
     * Reduces the set of string to a single string to use in the CMIS query. So the resulting string looks like
     * cmis:A, cmis:B, cmis:...., cmis:document
     * @param objectSet
     * @return
     */
    private String objectSetToQueryString(Set<String> objectSet){
        String result ="";
        result = objectSet.stream().map(t -> {
            if (StringUtils.countMatches(t, ":") >= 2) {
                /*StringBuilder tmp = new StringBuilder(t);
                tmp.insert(StringUtils.indexOf(t,":"), "\\");*/
                return StringUtils.substringAfter(t, ":");
            } else return t;

        }).reduce("", (acc, item) -> acc + "cmis:objectTypeId = \'" + item + "\' OR ");
        result += "cmis:objectTypeId = \'cmis:folder\'";
        return result;
    }
}
