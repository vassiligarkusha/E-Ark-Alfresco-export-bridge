package dk.magenta.eark.erms.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class TestRepositoryTree {

	private CmisData cmisData;
	private TreeNode<CmisData> root;
	
	@Before
	public void setUp() {
		cmisData = new CmisDataImpl("id0", "objetType");
		root = new RepositoryTreeNode<CmisData>(cmisData);
	}
	
	@Test
	public void shouldStoreTreeNode() {
		RepositoryTree repositoryTree = new RepositoryTreeImpl(root);
		assertNotNull(repositoryTree.getRoot());
		assertEquals("id0", repositoryTree.getRoot().getData().getObjectId());
	}

	@Test
	public void shouldAddNodeToRootCorrectly() {
		CmisData cmisData1 = new CmisDataImpl("id1", "objectType");
		TreeNode<CmisData> node1 = new RepositoryTreeNode<CmisData>(cmisData1);
		RepositoryTree repositoryTree = new RepositoryTreeImpl(root);
		repositoryTree.addTreeNode("id0", node1);
		assertEquals("id1", repositoryTree.getRoot().getChildren().get(0).getData().getObjectId());
	}
	
	@Test
	public void shouldAddNodeToChildCorrectly() {
		CmisData cmisData1 = new CmisDataImpl("id1", "objetType");
		CmisData cmisData11 = new CmisDataImpl("id11", "objetType");
		TreeNode<CmisData> node1 = new RepositoryTreeNode<CmisData>(cmisData1);
		TreeNode<CmisData> node11 = new RepositoryTreeNode<CmisData>(cmisData11);
		root.addChild(node1);
		
		RepositoryTree repositoryTree = new RepositoryTreeImpl(root);
		repositoryTree.addTreeNode("id1", node11);;
		assertEquals("id11", repositoryTree.getRoot().getChildren().get(0).getChildren().get(0).getData().getObjectId());
	}

	// should add node to child node correctly
	
	@Ignore
	@Test
	public void test() {
		assertTrue(false);
	}

	// Should be able to add node to tree
	
}
