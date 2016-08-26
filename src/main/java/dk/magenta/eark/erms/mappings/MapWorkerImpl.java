package dk.magenta.eark.erms.mappings;

import dk.magenta.eark.erms.*;
import dk.magenta.eark.erms.exceptions.ErmsIOException;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.io.File;
import java.util.List;

/**
 * @author lanre.
 */
public class MapWorkerImpl implements MapWorker {
    private final Logger logger = LoggerFactory.getLogger(MapWorkerImpl.class);
    private DatabaseConnectionStrategy dbConnectionStrategy;
    private String mapRoot;

    public MapWorkerImpl() {
        try {
            PropertiesHandler propertiesHandler = new PropertiesHandlerImpl("settings.properties");
            this.dbConnectionStrategy = new JDBCConnectionStrategy(propertiesHandler);
            this.mapRoot = replaceTokens(propertiesHandler.getProperty("mapping.root"));
            Utils.checkDirExists(this.mapRoot, true);
            System.out.print("Map root located at: "+ mapRoot);
        } catch (Exception sqe) {
            System.out.println("====> Error <====\nUnable to initialise mapping worker due to: " + sqe.getMessage());
            logger.error("====> Error <====\nUnable to initialise mapping worker due to: " + sqe.getMessage());
        }
    }

    @Override
    public void saveMapping(String mappingName, File mapFile, FormDataContentDisposition fileMetadata){
        try{
            System.out.println();
            //TODO: Must check for existence of an already existing mapping with the same name and return an error msg
            try {
                final String finalPath = mapRoot + "/" + fileMetadata.getFileName();
                //Are we able to move the file?
                if (mapFile.renameTo( new File(finalPath) ) ) {
                    //After moving let's store details that allow us to retrieve it later to the db
                    if (!dbConnectionStrategy.saveMapping(mappingName, finalPath, fileMetadata)) {
                        //if we didn't manage to save the information then remove the file
                        mapFile.delete();
                        throw new ErmsIOException("Unable to persist file details in the db.");
                    }
                }
                else throw new NullPointerException("Internal system error: Unable to move the temp file to mapping root");
            }
            catch(Exception ge){
                ge.printStackTrace();
                logger.error("Unable to save file due to: \n" + ge.getMessage());
            }
        }
        catch(Exception ge){
            ge.printStackTrace();
        }
    }

    /**
     * Gets the Json object representing the requested mapping from the db
     * @param mappingName
     * @return
     */
    @Override
    public JsonObject getMapping(String mappingName) {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        try {
            Mapping mp = this.dbConnectionStrategy.getMapping(mappingName);
            if (!mp.isEmpty) {
                jsonObjectBuilder.add("mapping", mp.toJsonObject());
                jsonObjectBuilder.add(Constants.SUCCESS, true);
            }
        }
        catch (Exception ge){
            ge.printStackTrace();
        }
        return jsonObjectBuilder.build();
    }

    /**
     * Return a list of mappings on the system
     *
     * @return
     */
    @Override
    public JsonObject getMappings() {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        try {
            List<Mapping> mappings = this.dbConnectionStrategy.getMappings();
            if(mappings.size() > 0){
                mappings.forEach(t -> jsonArrayBuilder.add(t.toJsonObject()));
                jsonObjectBuilder.add("mappings", jsonArrayBuilder);
                jsonObjectBuilder.add(Constants.SUCCESS, true);
            }
        }
        catch (Exception ge){
            ge.printStackTrace();
        }
        return jsonObjectBuilder.build();
    }

    @Override
    public JsonObject deleteMapping(String mappingName) {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        try {
            Mapping mp = this.dbConnectionStrategy.getMapping(mappingName);
            if (!mp.isEmpty) {
                boolean res = this.dbConnectionStrategy.deleteMapping(mappingName);
                if (res) {
                    jsonObjectBuilder.add("dbEntryDeleted", true);
                }
                //TODO: clean up orphaned files
                //Not a critical error though we would need to be able to clean up "orphaned" files at some point
                File mappingFile = new File(mp.getSyspath());
                if(mappingFile.exists() && mappingFile.delete()){
                    jsonObjectBuilder.add("fileDeleted", true);
                }
            }
            else{
                jsonObjectBuilder.add(Constants.ERRORMSG, "Unable to instantiate mapping object before deletion");
                jsonObjectBuilder.add(Constants.SUCCESS, false);
            }
        }
        catch(Exception ge){
            ge.printStackTrace();
            jsonObjectBuilder.add(Constants.ERRORMSG, "Unable to delet mapping");
            jsonObjectBuilder.add(Constants.SUCCESS, false);
        }
        return jsonObjectBuilder.build();
    }

    private String replaceTokens(String token){
        String tmp = StringUtils.substringAfterLast(token, "}");
        String tokenStr =  System.getProperty(StringUtils.substringBetween(token, "${", "}"));
        return tokenStr + tmp;
    }
}
