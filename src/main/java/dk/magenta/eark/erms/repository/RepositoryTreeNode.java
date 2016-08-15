package dk.magenta.eark.erms.repository;

import java.util.LinkedList;
import java.util.List;

public class RepositoryTreeNode<T> implements TreeNode<T> {

	private T data;
	private TreeNode<T> parent;
	private List<TreeNode<T>> children;
	
	public RepositoryTreeNode(T data) {
		this.data = data;
		children = new LinkedList<TreeNode<T>>();
	}
	
	@Override
	public void addChild(T child) {
		TreeNode<T> childNode = new RepositoryTreeNode<T>(child);
		childNode.setParent(this);
		children.add(childNode);
	}

	@Override
	public void addChild(TreeNode<T> childNode) {
		childNode.setParent(this);
		children.add(childNode);
	}
	
	@Override
	public TreeNode<T> getParent() {
		return parent;
	}

	@Override
	public void setParent(TreeNode<T> parent) {
		this.parent = parent;
	}
	
	@Override
	public List<TreeNode<T>> getChildren() {
		return children;
	}

	@Override
	public T getData() {
		return data;
	}

	@Override
	public boolean isRoot() {
		if (parent == null) {
			return true;
		}
		return false;
	}

}
