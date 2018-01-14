package io.codestream.runtime

import io.codestream.core.Module
import io.codestream.core.TaskId
import io.codestream.resourcemodel.EmptyResourceRegistry
import io.codestream.resourcemodel.Resource
import io.codestream.resourcemodel.ResourceRegistry
import io.codestream.util.Eval
import io.codestream.util.OS
import io.codestream.util.YamlFactory
import io.codestream.util.coerce
import io.codestream.util.log.FileLog
import io.codestream.util.log.RunLog
import io.codestream.util.transformation.TransformerService
import java.time.LocalDateTime
import java.util.*
import javax.script.Bindings
import javax.script.ScriptEngine
import kotlin.reflect.KClass

data class StreamContext(val id: String = UUID.randomUUID().toString(),
                         val timeStamp: LocalDateTime = LocalDateTime.now(),
                         var variables: MutableMap<String, Any?> = mutableMapOf<String, Any?>(),
                         var parent: StreamContext? = null,
                         val log: RunLog = RunLog(FileLog(createTempFile(suffix = id).absolutePath), CodestreamRuntime.log),
                         var resources: ResourceRegistry = EmptyResourceRegistry()) : Bindings {


    init {
        this.put("\$ctx", this)
        this.put("\$env", OS.os().env)
        this.put("\$system", OS.os().props)
        this.put("\$resources", this.resources)
        this.put("\$os", OS.os())
        Module.registerFunctionObjects(this)
    }

    val rootId: String
        get() = parent?.id ?: id

    val depthCnt: Int
        get() = parent?.let { 1 + it.depthCnt } ?: 0

    fun asMap(): Map<String, Any?> {
        val contextVar = mapOf<String, Any>(Pair("id", id), Pair("timeStamp", timeStamp))
        val varMap = (mapOf<String, Any?>() + variables).filterKeys { !fixedKeys().contains(it) }
        return parent?.let { mapOf(Pair("this", varMap), Pair("parent", it.asMap())) + contextVar }
                ?: mapOf(Pair("this", varMap)) + contextVar
    }

    fun toYaml(): String {
        val yaml = YamlFactory.yaml()
        return yaml.dump(asMap())
    }


    @Synchronized
    operator fun set(variable: String, value: Any?) {
        put(variable, value)
    }

    override operator fun get(variable: String): Any? {
        val result = variables[variable]
        return result?.let {
            result
        } ?: parent?.let { it[variable] }
    }

    operator fun plusAssign(variables: Map<String, Any?>) {
        variables.forEach { t, u ->
            this[t] = u
        }
    }

    @SuppressWarnings("IMPLICIT_CAST_TO_ANY")
    inline fun <reified K> evalTo(script: Any?, typeHint: KClass<*>? = null): K? {
        val type = typeHint?.let { it } ?: K::class
        return script?.let {
            val value: K = processEval<K>(it, type) ?: return null
            return when (value) {
                is Collection<*> -> value.mapNotNull { v ->
                    v?.let { entry -> processEval<Any?>(entry, entry::class) }
                }
                is Array<*> -> {
                    var elementType: KClass<*>? = null
                    coerce<K>(value.mapNotNull { v ->
                        v?.let { entry ->
                            elementType = entry::class
                            processEval<Any?>(entry, entry::class)
                        }
                    }.toTypedArray(), elementType?.let { it } ?: Any::class)
                }
            //to short circuit Map as it is also a resource
                is Resource -> {
                    value
                }
                is Map<*, *> -> value.mapValues { v ->
                    v.value?.let { entry -> processEval<Any?>(entry, entry::class) }
                }
                else -> value
            } as K?
        }
    }

    inline fun <reified K> processEval(it: Any, typeHint: KClass<*>?): K? {
        val value: K? = if (Eval.isScriptString(it.toString())) {
            evalScript<K>(it.toString())
        } else {
            val type = typeHint?.let { hint -> hint } ?: K::class
            TransformerService.convert(it, type)
        }
        return value
    }


    inline fun <reified K> evalScript(script: String, engine: ScriptEngine = Eval.defaultEngine): K? {
        return Eval.eval<K?>(Eval.extractScriptString(script), this, engine)
    }

    fun subContext(): StreamContext {
        return StreamContext(
                id = UUID.randomUUID().toString(),
                timeStamp = LocalDateTime.now(),
                variables = mutableMapOf(),
                parent = this,
                log = this.log,
                resources = this.resources
        )
    }

    fun log(id: TaskId, msg: Any) {
        log.info(id, msg)
    }

    fun echo(msg: Any) {
        log.echo(msg)
    }

    fun error(id: TaskId, msg: Any) {
        log.error(id, msg)
    }

    fun debug(id: TaskId, msg: Any) {
        log.debug(id, msg)
    }


    override fun containsValue(value: Any?): Boolean {
        if (!this.variables.containsValue(value)) {
            return parent?.containsValue(value) ?: false
        }
        return true
    }

    @Synchronized
    override fun clear() {
        variables.clear()
        parent?.clear()
    }

    @Synchronized
    override fun putAll(from: Map<out String, Any>) {
        for ((k, v) in from) {
            set(k, v)
        }
    }

    override fun containsKey(key: String?): Boolean {
        if (!this.variables.containsKey(key)) {
            return parent?.containsKey(key) ?: false
        }
        return true
    }

    @Synchronized
    override fun put(key: String?, value: Any?): Any? {
        Objects.requireNonNull(key, "Key cannot be null")
        return parent?.let {
            if (it.containsKey(key)) {
                return it.put(key, value)
            } else {
                setLocalVariable(key, value)
            }
        } ?: setLocalVariable(key, value)
    }

    private fun setLocalVariable(variable: String?, value: Any?): Any? {
        return variable?.let {
            val old = variables[it]
            variables[it] = value
            old
        }
    }

    override fun isEmpty(): Boolean {
        return variables.isEmpty() && parent?.isEmpty() ?: true
    }

    override fun remove(key: String?): Any? {
        val result = variables.remove(key)
        return result ?: parent?.remove(key)
    }

    override fun toString(): String {
        return "StreamContext[$id]"
    }

    override val size: Int
        get() = variables.size + (parent?.size ?: 0)

    override val entries: MutableSet<MutableMap.MutableEntry<String, Any?>>
        get() {
            val entries = mutableSetOf<MutableMap.MutableEntry<String, Any?>>()
            entries.addAll(parent?.entries ?: mutableSetOf())
            entries.addAll(variables.entries)
            return entries
        }
    override val keys: MutableSet<String>
        get() {
            val keys = mutableSetOf<String>()
            keys.addAll(parent?.keys ?: mutableSetOf())
            keys.addAll(variables.keys)
            return keys
        }
    override val values: MutableCollection<Any?>
        get() {
            val values = mutableSetOf<Any?>()
            values.addAll(parent?.values ?: mutableSetOf())
            values.addAll(variables.values)
            return values
        }

    @Suppress("UNCHECKED_CAST")
    companion object {

        val local: ThreadLocal<StreamContext> = ThreadLocal()

        var workingScope: StreamContext?
            get() = local.get()?.let { it }
            set(scope) = local.set(scope)


        fun fixedKeys() = arrayOf("\$ctx", "\$env", "\$system")

        fun fromYaml(yamlStr: String, ctx: StreamContext = StreamContext()): StreamContext {
            val yaml = YamlFactory.yaml()
            val data: Map<String, Any?> = yaml.load(yamlStr) as Map<String, Any?>
            return fromMap(data, ctx)

        }

        fun fromMap(data: Map<String, Any?>, ctx: StreamContext = StreamContext()): StreamContext {
            val thisVar = mutableMapOf<String, Any?>()
            thisVar.putAll(data["this"] as Map<String, Any?>)
            val parent: Map<String, Any?>? = data["parent"] as Map<String, Any?>?
            ctx.variables = thisVar
            parent?.let { parentMap ->
                val parentVar = mutableMapOf<String, Any?>()
                parentVar.putAll(parentMap)
                val parentCtx = ctx.parent?.let {
                    it
                } ?: StreamContext(UUID.randomUUID().toString(), LocalDateTime.now(), parentVar)
                fromMap(parentVar, parentCtx)
                ctx.parent = parentCtx
            }
            return ctx
        }


    }
}

