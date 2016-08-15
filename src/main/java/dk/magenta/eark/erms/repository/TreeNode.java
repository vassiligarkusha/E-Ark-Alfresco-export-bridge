package dk.magenta.eark.erms.repository;

import java.util.List;

/**
 * TreeNodes (which are trees themselves) that is used to build the repository tree
 * @author Andreas Kring <andreas@magenta.dk>
 *
 * @param <T>
 */
public interface TreeNode<T> {

	public void addChild(T child);
	public void addChild(TreeNode<T> child);
	public TreeNode<T> getParent();
	public void setParent(TreeNode<T> parent);
	public List<TreeNode<T>> getChildren();
	public T getData();
	public boolean isRoot();
	
}
