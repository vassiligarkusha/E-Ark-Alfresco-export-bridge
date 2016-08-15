/**
 * NOTE: interface not yet stable - more methods can be added etc.
 */

package dk.magenta.eark.erms.repository;

/**
 * Repository tree that holds the semantic structure of the archive. 
 * Use this tree to create an iterator that should be used during the extraction of 
 * the repository metadata. Also provides an easy way to add new nodes to the tree
 * @author Andreas Kring <andreas@magenta.dk>
 *
 * @param <T>
 */
public interface RepositoryTree<E> extends Iterable<CmisData> {
	public void addTreeNode(E parentObjectId, TreeNode<CmisData> childTreeNode);
	public TreeNode<CmisData> getRoot();
	// public void removeTreeNode(String objectId);
}
