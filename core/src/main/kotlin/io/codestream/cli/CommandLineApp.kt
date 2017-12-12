package io.codestream.cli

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default
import com.xenomachina.argparser.mainBody
import io.codestream.core.ModuleLoader
import io.codestream.core.TaskType
import io.codestream.runtime.CodestreamRuntime
import java.io.File

class CommandLineApp(args: ArgParser) {


    //Stream name
    val streamName: File? by args.storing("-s", "--stream", help = "stream YAML/Kotlin src to run") {
        File(this)
    }.default(null)

    //Task to Run
    val task: String? by args.storing("-t", "--task", help = "Task to run").default(null)

    //Input parameters passed at run time
    val inputParms by args.adding("-I", "--input", help = "input parameter (format: [name]=[items]") {
        val parts = this.split("=")
        Pair(parts[0], parts[1])
    }

    //Module Paths
    val modulePaths by args.adding("-M", "--modulepath", help = "Path for loading modules") {
        this
    }.default(listOf(ModuleLoader.defaultPath))

    //Codestream Runtime
    val csRuntime by lazy { CodestreamRuntime.init(modulePaths.toTypedArray()) }


    fun run() = mainBody("cs") {

        if (streamName == null && task == null) {
            println("stream or task is required")
        } else {

            streamName?.let {
                val result = csRuntime.runStream(it, inputParms = inputParms.toMap(), inputResolver = { _, parm ->
                    var done: Boolean
                    var input: String?
                    val isRequired = parm.required
                    val prompt = "${parm.desc} ${if (isRequired) "(required)" else ""} :"
                    do {
                        print(prompt)
                        input = readLine()?.trim()
                        done = !(isRequired && input.isNullOrEmpty())
                    } while (!done)
                    input!!
                })
                result?.let { errors ->
                    println(errors.message)
                    errors.errors.forEach { println(it.message) }
                }
            }
            task?.let {
                csRuntime.runTask(TaskType.fromString(it), inputParms.toMap())
            }
        }
    }

}