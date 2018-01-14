package io.codestream.util

import io.codestream.runtime.StreamContext
import javax.script.ScriptContext
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager
import javax.script.SimpleScriptContext


object Eval {

    private val factory = ScriptEngineManager()
    private val defaultScriptEngine = "groovy"
    private val engine = factory.getEngineByName(defaultScriptEngine)
    private val engines = mutableMapOf<String, ScriptEngine>()


    val defaultEngine get() = engineByName(defaultScriptEngine)


    fun engineByName(name: String): ScriptEngine {
        return engines.getOrPut(name) { factory.getEngineByName(name) }
    }


    fun <K> eval(script: String, variables: Map<String, Any?> = mapOf(), scriptEngine: ScriptEngine = engineByName(defaultScriptEngine)): K {
        val newContext = SimpleScriptContext()
        val engineScope = newContext.getBindings(ScriptContext.ENGINE_SCOPE)
        variables.forEach({ key: String, value: Any? -> engineScope.put(key, value) })
        @Suppress("UNCHECKED_CAST")
        return scriptEngine.eval(script, newContext) as K
    }

    fun <K> eval(script: String, variables: StreamContext, scriptEngine: ScriptEngine = engineByName(defaultScriptEngine)): K {
        val newContext = SimpleScriptContext()
        newContext.setBindings(variables, ScriptContext.ENGINE_SCOPE)
        @Suppress("UNCHECKED_CAST")
        return scriptEngine.eval(script, newContext) as K
    }

    fun isScriptString(expr: String): Boolean {
        val isDollarForm = expr.stringify().startsWith("\${") && expr.stringify().endsWith("}")
        val isHashForm = expr.stringify().startsWith("#{") && expr.stringify().endsWith("}")
        return isDollarForm || isHashForm
    }

    fun extractScriptString(expr: String): String {
        if (isScriptString(expr)) {
            val stringify = expr.stringify()
            return stringify.substring(2, stringify.length - 1)
        }
        return expr
    }
}