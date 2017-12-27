package io.codestream.util.git

import org.junit.Test
import kotlin.test.assertEquals

class GitBranchTest {

    @Test
    fun testShortName() {
        assertEquals("testing", GitBranch("refs/heads/testing").shortName)
        assertEquals("testing", GitBranch("refs/remotes/origin/testing").shortName)
    }
}