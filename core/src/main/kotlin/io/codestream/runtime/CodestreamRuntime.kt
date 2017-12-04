package io.codestream.runtime

import io.codestream.core.*
import io.codestream.resourcemodel.EmptyResourceRegistry
import io.codestream.resourcemodel.Resource
import io.codestream.resourcemodel.ResourceRegistry
import io.codestream.util.bind
import io.codestream.util.fail
import io.codestream.util.ok
import io.codestream.util.transformation.LambdaTransformer
import io.codestream.util.transformation.TransformerService
import io.codestream.yaml.YAMLStreamBuilder
import java.io.File

class CodestreamRuntime(modulePaths: Array<String>) {

    var registry: ResourceRegistry? = EmptyResourceRegistry()

    init {
        ModuleLoader(modulePaths).load()
        TransformerService.addConverter(String::class, Resource::class, LambdaTransformer<String, Resource?> { input ->
            registry?.get(input)
        })
    }

    fun runStream(stream: File,
                  inputParms: Map<String, Any?>,
                  ctx: StreamContext = StreamContext(),
                  inputResolver: (String, Parameter) -> String = { _, _ -> "" }): TaskError? {
        val inputs = LinkedHashMap<String, Any?>()
        inputs += inputParms
        val stream = YAMLStreamBuilder(stream).build()
        stream.parameters.forEach { t, u ->
            if (!inputs.containsKey(t)) {
                inputs += Pair(t, inputResolver(t, u))
            }
        }
        return stream.run(inputs, ctx)
    }

    fun runTask(task: TaskType, parms: Map<String, Any?>, ctx: StreamContext = StreamContext()): TaskError? {
        val id = TaskId("_default", "_default")
        val module = task.module?.let { it } ?: return invalidModule(id, "${task.namespace} is not a valid module")
        val defn = ExecutableDefinition(task, id, defaultCondition(), parms)
        return module
                .create(defn, ctx)
                .bind { executable ->
                    executable.bind(defn, ctx)?.let { fail<Executable, TaskError>(it) } ?: ok(executable)
                }
                .bind {
                    ifTask(defn.id, defn.type, it) {
                        it.execute(id, ctx)
                    }?.let { fail<Executable, TaskError>(it) } ?: ok(it)
                }.right
    }


    companion object {
        private var rt: CodestreamRuntime? = null

        var resourceRegistry: ResourceRegistry?
            get() = runtime.registry
            set(value) {
                runtime.registry = value
            }

        val runtime: CodestreamRuntime
            get() = rt?.let { it } ?: throw IllegalStateException("runtime not initialised, call init()")

        @Synchronized
        fun init(modulePaths: Array<String>, force:Boolean = false): CodestreamRuntime {
            if (force || rt == null) {
                rt = CodestreamRuntime(modulePaths)
            }
            return runtime
        }
    }
}