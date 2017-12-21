package io.codestream.cli

import io.codestream.core.Module
import io.codestream.core.TaskType


fun displayModules() {
    Console.newLine()
    Console.line("modules:")
    Module.forEach {
        Console.line("  ${it.name.padEnd(25)}  ${it.description}")
    }
    Console.newLine()
}

private fun underline(name: String): String {
    val underline = "".padEnd(name.length, '=')
    Console.line(name)
    Console.line(underline)
    return underline
}

fun displayModuleHelp(moduleName: String) {
    val module = Module[moduleName]
    module?.let { mod ->
        Console.newLine()
        underline(module.name)
        Console.newLine()
        Console.line(module.description)
        Console.newLine()
        Console.line("tasks:")
        mod.tasks.forEach {
            Console.line("  ${it.name.padEnd(25)}  ${mod.documentation(it)?.description}")
        }
        Console.newLine()
    } ?: Console.red("Module: $moduleName does not exist")
}

fun displayTaskHelp(task: String) {
    try {
        val type = TaskType.fromString(task)
        type.module?.let { mod ->
            mod.documentation(type)?.let {
                Console.newLine()
                underline("Task: ${type.fqn}")
                Console.newLine()
                Console.line(it.description)
                Console.newLine()
                Console.line("parameters:")
                it.params.forEach { parm ->
                    Console.line("  ${parm.name.padEnd(25)}  ${parm.description}")
                    Console.line("  ${"".padEnd(25)}  Type:    ${parm.type}")
                    Console.newLine()
                }
                Console.newLine()
            } ?: Console.red("Task: ${type.name} does not exist in module ${mod.name}")


        } ?: Console.red("Module: ${type.namespace} does not exist")
    } catch (e: IllegalArgumentException) {
        Console.red(e.message)
    }
}