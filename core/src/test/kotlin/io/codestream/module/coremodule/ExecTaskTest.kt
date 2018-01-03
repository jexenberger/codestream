package io.codestream.module.coremodule

import io.codestream.runtime.StreamContext
import io.codestream.util.OS
import org.junit.Test
import kotlin.test.assertNotNull

class ExecTaskTest {

    @Test
    fun testExecute() {
        val cmd = if (OS.os().unixVariant) "ls" else "dir"
        val exec = ExecTask()
        exec.cmd = cmd
        exec.outputVar = "test"
        exec.dir = "/"
        exec.echo = true
        val ctx = StreamContext()
        exec.execute(testId(), ctx)
        assertNotNull(ctx["test"])
    }
}