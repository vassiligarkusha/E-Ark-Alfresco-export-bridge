package dk.magenta.eark.erms.mappings;

import dk.magenta.eark.erms.Constants;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * @author lanre.
 */


@Path("mapping")
public class Mapping {
    private final Logger logger = LoggerFactory.getLogger(Mapping.class);

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/upload")
    public JsonObject uploadMapping(@FormDataParam("mappingName") String mapName,
                                    @FormDataParam("file") InputStream fileInputStream,
                                    @FormDataParam("file") FormDataContentDisposition fileMetaData) {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        if (StringUtils.isNotBlank(mapName) && fileInputStream != null) {
            try {
                //Read the file into a temp file
                //TODO
                /*Create a temp file we might need to use this for pre-processing before storing. e.g. validation */
                File tempFile = File.createTempFile("mapping", ".xml");

                //The java 8 way
                int rd  = (int) Files.copy(fileInputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                IOUtils.closeQuietly(fileInputStream);

                System.out.print("\n The number of bytes read - "+ rd +" to "+tempFile.getAbsolutePath()+"\n");

                MapWorker mapWorker = new MapWorkerImpl();
                mapWorker.saveMapping(mapName, tempFile, fileMetaData);

            } catch (Exception e) {
                e.printStackTrace();
                builder.add(Constants.SUCCESS, false);
                builder.add(Constants.ERRORMSG, e.getMessage());
            }

            builder.add(Constants.SUCCESS, true);
            builder.add(Constants.MESSAGE, "Mapping successfully saved");

        } else {
            builder.add(Constants.SUCCESS, false);
            builder.add(Constants.ERRORMSG, "Unable to persist uploaded mapping. Check system logs for details!");
        }

        return builder.build();
    }


}
