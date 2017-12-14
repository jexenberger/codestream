package io.codestream.modules.atlassian.bitbucket

import org.junit.Test
import kotlin.test.assertEquals

class BitbucketFunctionsTest {


    @Test
    fun testBranchFromJira() {
        val result = BitbucketFunctions().branchFromJira("TST-123", "Function to add stuff to new feature")
        assertEquals("TST-123-function-to-add", result)
    }

    @Test
    fun testGetTaskFromBranch() {
        val result = BitbucketFunctions().getTaskFromBranch("TST-123-function-to-add")
        assertEquals("TST-123", result)
    }

    @Test
    fun testGetTaskFromBranchWithTypicalBitbucketPrefix() {
        val result = BitbucketFunctions().getTaskFromBranch("feature/TST-123-function-to-add")
        assertEquals("TST-123", result)
    }
}