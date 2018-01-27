package io.codestream.modules.atlassian

import io.fabric8.mockwebserver.DefaultMockServer
import org.junit.After
import org.junit.Before

open class BaseMockHttpServer {
    val settings: AtlassianTestSettings = AtlassianTestSettings.get()
    var server: DefaultMockServer? = null
    @Before
    fun setUp() {
        server = DefaultMockServer()
        server?.start(4444)
    }

    @After
    fun tearDown() {
        server?.shutdown()
    }
}