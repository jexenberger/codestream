package io.codestream.runtime

import io.codestream.core.ExecutableDefinition
import io.codestream.core.Module
import io.codestream.core.Parameter
import io.codestream.core.invalidModule


class StreamBuilder(var name: String, var group: String, var desc: String = "", val echo: Boolean = true) {

    var runnables = arrayOf<RunExecutable<*>>()
    val parameters: MutableMap<String, Parameter> = mutableMapOf()
    var before: ExecutableDefinition<*>? = null
    var after: ExecutableDefinition<*>? = null
    var onError: ExecutableDefinition<*>? = null
    val stream: Stream by lazy {
        Stream(id = name,
                group = group,
                desc = desc,
                runnables = runnables,
                onError = onError,
                parameters = parameters,
                echo = echo,
                before = before,
                after = after
        )
    }


    fun define(init: StreamBuilder.() -> Unit): StreamBuilder {
        init()
        return this
    }

    fun before(defn: ExecutableDefinition<*>): StreamBuilder {
        this.before = defn
        return this
    }

    fun after(defn: ExecutableDefinition<*>): StreamBuilder {
        this.after = defn
        return this
    }

    fun onError(defn: ExecutableDefinition<*>): StreamBuilder {
        this.onError = defn
        return this
    }

    fun input(parameter: Parameter): StreamBuilder {
        parameters[parameter.name] = parameter
        return this
    }

    fun task(defn: ExecutableDefinition<*>, init: (StreamBuilder.() -> Unit)? = null): StreamBuilder {
        defn.module.executableType(defn.type)?.let {
            when (it) {
                Module.AllowedTypes.task -> runnables += RunTask(defn, echo = this.echo)
                Module.AllowedTypes.group -> {
                    val old = runnables
                    runnables = arrayOf()
                    init?.let { it() }
                    val groupTask = RunGroupTask(defn, runnables, echo = this.echo)
                    runnables = old
                    runnables += groupTask
                }
            }
        } ?: throw invalidModule(defn.id, "${defn.type.fqn} is not a recognised type")
        return this
    }
}