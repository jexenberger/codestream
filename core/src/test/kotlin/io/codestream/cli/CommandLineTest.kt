package io.codestream.cli

import com.xenomachina.argparser.ArgParser
import org.junit.Test

class CommandLineTest {

    @Test
    fun testRunStream() {
        val args = ArgParser(arrayOf("run", "src/test/resources/sample.yml", "-D", "-I", "saying=UBER FUNKY SAYING"))
        val app = CommandLineApp(args)
        app.run()
    }

    @Test
    fun testRunTask() {
        val args = ArgParser(arrayOf("task", "core::echo", "-D", "-I", "value=HELLO TASK IS EXECUTED"))
        val app = CommandLineApp(args)
        app.run()
    }
}