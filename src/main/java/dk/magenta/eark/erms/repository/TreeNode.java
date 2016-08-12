package dk.magenta.eark.erms.repository;

import java.util.List;

public interface TreeNode<T> {

	public void addChild(T child);
	public TreeNode<T> getParent();
	public List<TreeNode<T>> getChildren();
	public T getData();
	// public T findNode(String id);
	public boolean isRoot();
	
}
