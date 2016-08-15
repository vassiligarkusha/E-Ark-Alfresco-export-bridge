package dk.magenta.eark.erms.repository;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import dk.magenta.eark.erms.repository.RepositoryTreeNode;
import dk.magenta.eark.erms.repository.TreeNode;

public class TestTreeNode {

	private TreeNode<String> root;
	private TreeNode<String> node1;
	
	@Before
	public void TestTreeNode() {
		root = new RepositoryTreeNode<String>("root");
		node1 = new RepositoryTreeNode<String>("node1");
		node1.addChild("node11");
		node1.addChild("node12");

	}
	
	@Test
	public void shouldStoreDataInNode() {
		assertEquals("root", root.getData());
		assertEquals(0, root.getChildren().size());
	}
	
	@Test
	public void rootNodeShouldNotHaveParentNull() {
		assertNull(root.getParent());
	}
	
	@Test
	public void child1ShouldHaveRootAsParent() {
		root.addChild("child1");
		List<TreeNode<String>> children = root.getChildren();
		TreeNode<String> child1 = children.get(0);
		assertEquals("root", child1.getParent().getData());
		assertEquals(0, child1.getChildren().size());
		assertEquals("child1", child1.getData());
	}

	@Test
	public void shouldAddSimpleTreeCorrectly() {
		root.addChild(node1);
		TreeNode<String> node11 = node1.getChildren().get(0);
		TreeNode<String> node12 = node1.getChildren().get(1);
		assertEquals("root", node11.getParent().getParent().getData());
		assertEquals("root", node12.getParent().getParent().getData());
		assertEquals("node12", root.getChildren().get(0).getChildren().get(1).getData());
	}

	@Test
	public void isRootShouldReturnCorrectValue() {
		assertTrue(root.isRoot());
		root.addChild(node1);
		assertFalse(node1.isRoot());
	}
	
	@Ignore
	@Test
	public void test() {
		assertTrue(false);
	}

}
