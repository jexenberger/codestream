package io.codestream.module.coremodule

import io.codestream.runtime.StreamContext
import org.junit.Before
import org.junit.Test

class EchoTaskTest {

    @Before
    fun setUp() {

    }

    @Test
    fun testExecute() {
        val echo = EchoTask()
        echo.value = "hello world"
        //don't know how we will test this
        echo.execute(testId(), StreamContext())
    }

}