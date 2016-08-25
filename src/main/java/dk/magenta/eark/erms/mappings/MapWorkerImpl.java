package dk.magenta.eark.erms.mappings;

import dk.magenta.eark.erms.*;
import dk.magenta.eark.erms.exceptions.ErmsIOException;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

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

    @Override
    public void deleteMapping(String mappingName) {

    }

    private String replaceTokens(String token){
        String tmp = StringUtils.substringAfterLast(token, "}");
        String tokenStr =  System.getProperty(StringUtils.substringBetween(token, "${", "}"));
        return tokenStr + tmp;
    }
}
