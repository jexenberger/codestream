package io.codestream.runtime

import io.codestream.core.*
import io.codestream.resourcemodel.EmptyResourceRegistry
import io.codestream.resourcemodel.Resource
import io.codestream.resourcemodel.ResourceRegistry
import io.codestream.util.transformation.LambdaTransformer
import io.codestream.util.transformation.TransformerService
import io.codestream.yaml.YAMLStreamBuilder
import java.io.File

class CodestreamRuntime(modulePaths: Array<String>) {

    var registry: ResourceRegistry? = EmptyResourceRegistry()
    var runningStreams = mutableMapOf<String, Stream>()

    init {
        ModuleLoader(modulePaths).load()
        TransformerService.addConverter(String::class, Resource::class, LambdaTransformer<String, Resource?> { input ->
            registry?.get(input)
        })
    }

    fun runStream(streamFile: File,
                  inputParms: Map<String, Any?>,
                  ctx: StreamContext = StreamContext(),
                  inputResolver: (String, Parameter) -> String = { _, _ -> "" }): TaskError? {
        val inputs = LinkedHashMap<String, Any?>()
        inputs += inputParms
        val stream = YAMLStreamBuilder(streamFile).build()
        stream.parameters.forEach { t, u ->
            if (!inputs.containsKey(t)) {
                inputs += Pair(t, inputResolver(t, u))
            }
        }
        return try {
            runningStreams[stream.id] = stream
            stream.run(inputs, ctx)
        } finally {
            runningStreams.remove(stream.id)
        }
    }

    fun runTask(task: TaskType, parms: Map<String, Any?>, ctx: StreamContext = StreamContext()): TaskError? {
        val id = TaskId("_default", "_default")
        val module = task.module?.let { it } ?: return invalidModule(id, "${task.namespace} is not a valid module")
        val binding = MapBinding(id, task, parms)
        val defn = ExecutableDefinition<Task>(task, id, binding.toBinding(), defaultCondition())
        return module
                .createTask(defn, ctx)
                .map({ executable ->
                    defn.binding(id, ctx, executable)?.let { it }
                }, {
                    it
                })
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
        fun init(modulePaths: Array<String>, force: Boolean = false): CodestreamRuntime {
            if (force || rt == null) {
                rt = CodestreamRuntime(modulePaths)
            }
            return runtime
        }
    }
}