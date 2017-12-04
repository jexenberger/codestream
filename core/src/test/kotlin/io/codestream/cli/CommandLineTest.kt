package io.codestream.cli

import com.xenomachina.argparser.ArgParser
import org.junit.Test

class CommandLineTest {

    @Test
    fun testRunStream() {
        val args = ArgParser(arrayOf("-I", "saying=UBER FUNKY SAYING", "-s", "src/test/resources/sample.yml"))
        val app = CommandLineApp(args)
        app.run()
    }

    @Test
    fun testRunTask() {
        val args = ArgParser(arrayOf("-I", "items=HELLO TASK IS EXECUTED", "-t", "core::echo"))
        val app = CommandLineApp(args)
        app.run()
    }
}