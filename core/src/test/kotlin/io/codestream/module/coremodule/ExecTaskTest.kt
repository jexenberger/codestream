package io.codestream.module.coremodule

import io.codestream.core.defaultCondition
import io.codestream.util.OS
import org.junit.Test
import kotlin.test.assertNotNull

class ExecTaskTest {

    @Test
    fun testExecute() {
        val cmd = if (OS.os().unixVariant) "uname -a" else "dir"
        val coreModule = CoreModule()
        val (ctx, defn) = createTaskContext(coreModule, "exec", condition = defaultCondition())
        val exec = ExecTask()
        exec.cmd = cmd
        exec.varName = "test"
        exec.dir = "/"
        exec.execute(defn.id, ctx)
        assertNotNull(ctx["test"])
    }
}