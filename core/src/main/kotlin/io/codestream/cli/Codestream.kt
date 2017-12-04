@file:JvmName("Codestream")

package io.codestream.cli

import com.xenomachina.argparser.ArgParser
import io.codestream.runtime.TaskQueues

fun main(args: Array<String>) {
    val application = CommandLineApp(ArgParser(args))
    application.run()
    TaskQueues.shutdown()

}