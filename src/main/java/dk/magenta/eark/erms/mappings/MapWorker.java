package dk.magenta.eark.erms.mappings;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import javax.json.JsonObject;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.sql.SQLException;

/**
 * @author lanre.
 */
public interface MapWorker {


    /**
     * Save a mapping.xml file in the file system and meta information into the db
     * @param mappingName name with which to identify the file in the db
     * @param mapFile the file itself
     * @throws IOException 
     * @throws FileAlreadyExistsException 
     * @throws SQLException 
     */
    void saveMapping(String mappingName, File mapFile, FormDataContentDisposition fileMetadata) throws FileAlreadyExistsException, IOException, SQLException;

    /**
     * Gets the Json object representing the requested mapping from the db
     * @param mappingName
     * @return
     */
    JsonObject getMapping(String mappingName);

    /**
     * Returns a mapping Object. Not advisable for use in JAX-RS resources
     * @param mapName
     * @return a Map object or a Map.EMPTY object if it can not find a mapping object from the db
     */
    Mapping getMappingObject(String mapName);

    /**
     * Return a list of mappings on the system
     * @return
     */
    JsonObject getMappings();

    /**
     * removes a mapping from the FS and db
     * @param mappingName name of the mapping to remove
     * @return JsonObject containing the success of the operation.
     */
    JsonObject deleteMapping(String mappingName);

}
