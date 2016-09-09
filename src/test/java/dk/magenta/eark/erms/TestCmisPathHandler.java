package dk.magenta.eark.erms;

import static org.junit.Assert.*;

import org.junit.Test;

import dk.magenta.eark.erms.repository.CmisPathHandler;

public class TestCmisPathHandler {

	@Test
	public void relativePathShouldBeCorrect() {
		String topLevel = "/a/b/c/d";
		CmisPathHandler cmisPathHandler = new CmisPathHandler(topLevel);
		assertEquals("e/f", cmisPathHandler.getRelativePath("/a/b/c/d/e/f").toString());
		
	}
}
