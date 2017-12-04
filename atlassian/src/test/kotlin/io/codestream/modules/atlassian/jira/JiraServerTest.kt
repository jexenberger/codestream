package io.codestream.modules.atlassian.jira

import org.junit.Test

class JiraServerTest {

    @Test
    fun testLoad() {
        JiraServer.load()?.let { println(it) } ?: kotlin.test.fail("no jira config found. Configure in ${JiraServer.defaultPath}")
    }
}