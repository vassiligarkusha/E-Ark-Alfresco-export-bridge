package dk.magenta.eark.erms.repository;

import java.util.Iterator;

/**
 * 
 * @author Andreas Kring <andreas@magenta.dk>
 *
 * @param <String>
 */
public class RepositoryTreeImpl<String> implements RepositoryTree<String> {

	private TreeNode<CmisData> root;
	
	public RepositoryTreeImpl(TreeNode<CmisData> root) {
		this.root = root;
	}
	
	@Override
	public void addTreeNode(String parentObjectId,	TreeNode<CmisData> treeNode) {
		root.addChild(treeNode);
	}
	
	@Override
	public TreeNode<CmisData> getRoot() {
		return root;
	}
	
	@Override
	public Iterator<CmisData> iterator() {
			// TODO Auto-generated method stub
		return null;
	}
}
