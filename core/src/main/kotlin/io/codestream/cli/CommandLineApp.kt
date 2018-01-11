package io.codestream.cli

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default
import com.xenomachina.argparser.mainBody
import io.codestream.runtime.CodestreamRuntime
import io.codestream.runtime.ModuleLoader
import io.codestream.util.log.ConsoleLog
import io.codestream.util.log.Log
import io.codestream.util.whenTrue


enum class Command(val description: String, val handler: (Log, CodestreamRuntime, String?, Map<String, Any?>, debug: Boolean) -> Unit) {
    run("Run a stream file. COMMAND_OPTION is passed stream file", ::run),
    task("Run a Task. COMMAND_OPTION is fully qualified task type", ::task),
    help("Display help information. COMMAND_OPTION is optional help topic", ::help)

}

class CommandLineApp(args: ArgParser) {

    val log = ConsoleLog()

    val command: Command? by args.positional(name = "COMMAND", help = "Can be 'run' to run a stream, 'task' to run a task or 'help' for help pages") {
        Command.valueOf(this)
    }.default(null)

    val commandOption: String? by args.positional(name = "COMMAND_OPTION", help = "Option associated with the command").default(null)


    //Input parameters passed at run time
    val inputParms by args.adding("-I", "--input", help = "input parameter (format: [name]=[items]") {
        val parts = this.split("=")
        Pair(parts[0], parts[1])
    }

    val debug by args.flagging("-D", "--debug", help = "Run with debug output").default(false)

    //Module Paths
    val modulePaths by args.adding("-M", "--modulepath", help = "Path for loading modules") {
        this
    }



    //Codestream Runtime
    val csRuntime by lazy {
        val paths = if (modulePaths.isEmpty()) arrayOf(ModuleLoader.defaultPath) else modulePaths.toTypedArray()
        debug.whenTrue { log.info("DEBUG ENABLED") }
        log.enableDebug = debug
        CodestreamRuntime.init(paths, theLog = log)
    }


    fun run() = mainBody("cs") {
        log.debug("Set home folder -> ${CodestreamRuntime.homeFolder}")
        log.debug(modulePaths)
        val parms = inputParms.toTypedArray().toMap()
        command?.handler?.invoke(log, csRuntime, commandOption, parms, debug)
    }
}