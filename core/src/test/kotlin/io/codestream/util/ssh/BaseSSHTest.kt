package io.codestream.util.ssh

import io.codestream.TestSettings
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TestName

abstract class BaseSSHTest {
    val settings: TestSettings = TestSettings
    @get:Rule
    var name = TestName()

    @Before
    fun setUp() {
        println(name.methodName)
        if (name.methodName.indexOf("scp") > 0) {
            MockSSHServer.start(true)
        } else {
            MockSSHServer.start()
        }
    }

    @After
    fun tearDown() {
        MockSSHServer.stop()
    }
}