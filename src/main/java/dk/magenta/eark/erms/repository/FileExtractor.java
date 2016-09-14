package dk.magenta.eark.erms.repository;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.data.ContentStream;

public class FileExtractor {

	private Path rootExtractionPath;
	private Path dataFilePath;
	private Session session;
		
	/**
	 * Constructor
	 * @param rootExtractionPath the OS root path for storing the entire extraction
	 */
	public FileExtractor(Path rootExtractionPath, Session session) {
		this.rootExtractionPath = rootExtractionPath;
		this.session = session;
	}
	
	/**
	 * Writes the given CMIS object to the given path (which must be writeable)
	 * @param path the path relative to the rootExtractionPath
	 * @param objectId
	 * @throws IOException 
	 */
	public void writeCmisDocument(Path path, String objectId) throws IOException {
		Document cmisDocument = (Document) session.getObject(objectId);
		ContentStream contentStream = cmisDocument.getContentStream();
		
		if (contentStream != null) {
			BufferedInputStream in = new BufferedInputStream(contentStream.getStream());
			// Path p = rootExtractionPath.resolve(getDataFilePath()).resolve(path);
			// Files.copy(in, rootExtractionPath.resolve(getDataFilePath()).resolve(path));
			Files.copy(in, rootExtractionPath.resolve(path));
			in.close();
		} else {
			// Create empty file
		}
	}
	
	private Path getDataFilePath() {
		if (dataFilePath != null) {
			return dataFilePath;
		}
		return Paths.get("representations", "rep1", "data");
	}
	
}
