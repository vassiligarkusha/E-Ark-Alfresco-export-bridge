package dk.magenta.eark.erms.repository;

import java.util.LinkedList;
import java.util.List;

public class RepositoryTreeNode implements TreeNode<CmisNode> {

	private CmisNode data;
	private TreeNode<CmisNode> parent;
	private List<TreeNode<CmisNode>> children;
	
	public RepositoryTreeNode(CmisNode data) {
		this.data = data;
		children = new LinkedList<TreeNode<CmisNode>>();
		
	}
	
	@Override
	public void addChild(CmisNode child) {
		// TODO Auto-generated method stub

	}

	@Override
	public TreeNode<CmisNode> getParent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TreeNode<CmisNode>> getChildren() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CmisNode getData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CmisNode findNode(String id) {
		// TODO Auto-generated method stub
		return null;
	}

}
