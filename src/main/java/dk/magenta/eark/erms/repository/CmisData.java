package dk.magenta.eark.erms.repository;

/**
 * For representing nodes in the repository - this is the datatype that should
 * be stored in the repository tree nodes
 * @author Andreas Kring <andreas@magenta.dk>
 *
 */
public interface CmisData {
	public String getObjectId();
	public String getObjectType();
}
