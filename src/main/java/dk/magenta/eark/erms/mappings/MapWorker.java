package dk.magenta.eark.erms.mappings;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import java.io.File;

/**
 * @author lanre.
 */
public interface MapWorker {


    /**
     * Save a mapping.xml file in the file system and meta information into the db
     * @param mappingName name with which to identify the file in the db
     * @param mapFile the file itself
     */
    void saveMapping(String mappingName, File mapFile, FormDataContentDisposition fileMetadata);

    void deleteMapping(String mappingName);

}
