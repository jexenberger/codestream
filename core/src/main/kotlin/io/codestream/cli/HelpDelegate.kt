package io.codestream.cli

import io.codestream.core.Module
import io.codestream.core.TaskType


fun displayModules() {
    Module.forEach {
        val name = it.name
        val underline = underline(name)
        val description = it.description
        Console.line(underline)
        Console.line(description)
        Console.newLine()
    }
}

private fun underline(name: String): List<String> {
    val underline = (1..name.length).map { "=" }
    Console.line(name)
    return underline
}

fun displayModuleHelp(moduleName: String) {
    val module = Module[moduleName]
    module?.let { mod ->
        underline(module.name)
        Console.line(module.description)
        Console.newLine()
        underline("Tasks")
        mod.tasks.forEach {
            Console.line(" \t${it.name.padEnd(30)}: ${mod.documentation(it)?.description}")
        }
    } ?: Console.red("Module: $moduleName does not exist")
}

fun displayTaskHelp(task: String) {
    try {
        val type = TaskType.fromString(task)
        type.module?.let { mod ->
            mod.documentation(type)?.let {
                underline(type.fqn)
                Console.line(it.description)
                underline("Parameters")
                Console.newLine()
                it.params.forEach { parm ->
                    Console.line(" \t${it.name.padEnd(30)}: ${parm.description}")
                    Console.line(" \t${"  type".padEnd(30)}: ${parm.type}")
                    Console.newLine()
                }
            } ?: Console.red("Task: ${type.name} does not exist in module ${mod.name}")

        } ?: Console.red("Module: ${type.namespace} does not exist")
    } catch (e: IllegalArgumentException) {
        Console.red(e.message)
    }
}