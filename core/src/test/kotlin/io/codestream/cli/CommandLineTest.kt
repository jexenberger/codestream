package io.codestream.cli

import com.xenomachina.argparser.ArgParser
import org.junit.Test


fun main(args: Array<String>) {
    System.setProperty("cs.installation.folder", "/home/julian/IdeaProjects/codestream/distro/target/distro-1.0-SNAPSHOT-dep")
    val args = ArgParser(arrayOf("run", "/home/julian/Documents/cds/start-task.yaml", "-D"))
    val app = CommandLineApp(args)
    app.run()
}

class CommandLineTest {

    @Test
    fun testRunStream() {
        System.setProperty("cs.installation.folder", "src/test/resources")
        val args = ArgParser(arrayOf("run", "src/test/resources/sample.yml", "-D", "-I", "saying=UBER FUNKY SAYING"))
        val app = CommandLineApp(args)
        app.run()
    }

    @Test
    fun testRunStreamWithInputs() {
        System.setProperty("cs.installation.folder", "src/test/resources")
        val args = ArgParser(arrayOf("run", "src/test/resources/with_inputs.yml", "-D"))
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