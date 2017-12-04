package io.codestream.module.coremodule

import io.codestream.core.defaultCondition
import org.junit.Before
import org.junit.Test

class EchoTaskTest {

    @Before
    fun setUp() {

    }

    @Test
    fun testExecute() {
        val (ctx, defn) = createTaskContext(CoreModule(), "exec", condition = defaultCondition())
        val echo = EchoTask()
        echo.value = "hello world"
        //don't know how we will test this
        echo.execute(defn.id, ctx)
    }

}