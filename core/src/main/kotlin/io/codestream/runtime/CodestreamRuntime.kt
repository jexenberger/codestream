package io.codestream.runtime

import io.codestream.core.*
import io.codestream.resourcemodel.EmptyResourceRegistry
import io.codestream.resourcemodel.Resource
import io.codestream.resourcemodel.ResourceRegistry
import io.codestream.util.crypto.CipherHandler
import io.codestream.util.crypto.DESede
import io.codestream.util.crypto.Secret
import io.codestream.util.log.ConsoleLog
import io.codestream.util.log.Log
import io.codestream.util.system
import io.codestream.util.transformation.LambdaTransformer
import io.codestream.util.transformation.TransformerService
import org.codehaus.groovy.runtime.GStringImpl
import java.io.File
import java.security.Key
import javax.crypto.spec.SecretKeySpec

val runtime: CodestreamRuntime = CodestreamRuntime.runtime

class CodestreamRuntime(modulePaths: Array<String>, val log: Log = CodestreamRuntime.log) {


    var registry: ResourceRegistry? = EmptyResourceRegistry()
    var runningStreams = mutableMapOf<String, Stream>()
    val cipherHandler: CipherHandler = DESede()
    val systemKeyPath = "${CodestreamRuntime.configPath}/key"
    val systemKey = initKey()


    var path: String = ""

    init {
        CodestreamRuntime.rt = this


        ModuleLoader(modulePaths, log).load()
        TransformerService.addConverter(String::class, Resource::class, LambdaTransformer<String, Resource?> { input ->
            registry?.get(input)
        })
        TransformerService.addConverter(String::class, Secret::class, LambdaTransformer<String, Secret> { input ->
            Secret(input)
        })
        TransformerService.addConverter(Secret::class, String::class, LambdaTransformer<Secret, String> { input ->
            input.value
        })
        TransformerService.addConverter(Secret::class, String::class, LambdaTransformer<Secret, String> { input ->
            input.value
        })
        TransformerService.addConverter(GStringImpl::class, String::class, LambdaTransformer<GStringImpl, String> { input ->
            input.toString()
        })
    }

    private fun initKey(): Key {
        val path = File(systemKeyPath)
        return if (path.exists()) {
            SecretKeySpec(path.readBytes(), cipherHandler.algorithm)
        } else {
            val key = cipherHandler.generateKey()
            path.writeBytes(key.encoded)
            key
        }
    }

    fun runStream(streamFile: File,
                  inputParms: Map<String, Any?>,
                  ctx: StreamContext = StreamContext(),
                  debug: Boolean = false,
                  inputResolver: (String, Parameter) -> String = { _, _ -> "" }): TaskError? {
        ctx.log.debug = debug
        val inputs = LinkedHashMap<String, Any?>()
        inputs += inputParms
        val stream = YAMLStreamBuilder(streamFile, log = ctx.log.displayLog).build()
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

    fun runTask(task: TaskType, parms: Map<String, Any?>, ctx: StreamContext = StreamContext(), debug: Boolean = false): TaskError? {
        ctx.log.debug = debug
        val id = TaskId("_default", "_default", task)
        val module = task.module?.let { it } ?: return invalidModule(id, "${task.namespace} is not a valid module")
        val defn = ExecutableDefinition<Task>(task, id, module.binding<Task>(task, parms)!!, defaultCondition())
        return module
                .createTask(defn, ctx)
                .map({ executable ->
                    RunTask(defn).run(ctx)
                }, {
                    it
                })
    }


    companion object {

        val log = ConsoleLog()

        val configPath = "${system.homeDir}/.cs"


        var homeFolder: String
            get() = codestreamPath
            set(value) {
                codestreamPath = value
            }

        private var codestreamPath: String = System.getProperty("cs.installation.folder", "")

        private var rt: CodestreamRuntime? = null


        var resourceRegistry: ResourceRegistry?
            get() = runtime.registry
            set(value) {
                runtime.registry = value
            }

        val runtime: CodestreamRuntime
            get() = rt?.let { it } ?: throw IllegalStateException("runtime not initialised, call init()")

        @Synchronized
        fun init(modulePaths: Array<String>, force: Boolean = false, theLog: Log = CodestreamRuntime.log): CodestreamRuntime {
            if (force || rt == null) {
                rt = CodestreamRuntime(modulePaths, theLog)
            }
            return runtime
        }
    }
}