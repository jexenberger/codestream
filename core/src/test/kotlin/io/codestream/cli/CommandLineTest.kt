package io.codestream.cli

import com.xenomachina.argparser.ArgParser
import org.junit.Test




class CommandLineTest {

    @Test
    fun testRunStream() {
        System.setProperty("cs.installation.folder", "src/test/resources")
        val args = ArgParser(arrayOf("run", "src/test/resources/sample.yml", "-D", "-I", "saying=UBER FUNKY SAYING", "-I", "environment=uat"))
        val app = CommandLineApp(args)
        app.run()
    }

    @Test
    fun testRunStreamWithInputs() {
        System.setProperty("cs.installation.folder", "src/test/resources")
        val args = ArgParser(arrayOf("run", "src/test/resources/with_inputs.yml", "-D", "-I", "hello=test"))
        val app = CommandLineApp(args)
        app.run()
    }

    @Test
    fun testRunTask() {
        val args = ArgParser(arrayOf("task", "core::echo", "-D", "-I", "cipherText=HELLO TASK IS EXECUTED"))
        val app = CommandLineApp(args)
        app.run()
    }
}