package io.codestream.runtime

import io.codestream.core.ExecutableDefinition
import io.codestream.core.Module
import io.codestream.core.Parameter


class StreamBuilder(name: String, group: String, desc: String = "") {

    var runnables = arrayOf<RunExecutable<*>>()
    val parameters: MutableMap<String, Parameter> = mutableMapOf()
    val stream: Stream by lazy {
        Stream(name, group, desc, runnables, null, parameters)
    }

    fun define(init: StreamBuilder.() -> Unit): StreamBuilder {
        init()
        return this
    }

    fun input(parameter: Parameter): StreamBuilder {
        parameters[parameter.name] = parameter
        return this
    }

    fun task(defn: ExecutableDefinition<*>, init: (StreamBuilder.() -> Unit)? = null): StreamBuilder {
        defn.module.executableType(defn.type)?.let {
            when (it) {
                Module.AllowedTypes.task -> runnables += RunTask(defn)
                Module.AllowedTypes.group -> {
                    val old = runnables
                    runnables = arrayOf()
                    init?.let { it() }
                    val groupTask = RunGroupTask(defn, runnables)
                    runnables = old
                    runnables += groupTask
                }
            }
        }
        return this
    }
}