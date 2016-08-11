package dk.magenta.eark.erms.repository;

/**
 * For representing nodes in the repository - this is the datatype that should
 * be stored in the repository tree
 * @author Andreas Kring <andreas@magenta.dk>
 *
 */
public interface CmisNode {
	public String getObjectId();
	public String getObjectType();
}
