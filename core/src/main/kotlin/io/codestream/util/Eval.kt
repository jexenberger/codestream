package io.codestream.util

import io.codestream.runtime.StreamContext
import javax.script.ScriptContext
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager
import javax.script.SimpleScriptContext


object Eval {

    val factory: ScriptEngineManager = ScriptEngineManager()
    val SCRIPT_ENGINE = "nashorn"


    fun <K> eval(script: String, variables: Map<String, Any?> = mapOf()): K {
        val newContext = SimpleScriptContext()
        val engine: ScriptEngine = factory.getEngineByName(SCRIPT_ENGINE)
        val engineScope = newContext.getBindings(ScriptContext.ENGINE_SCOPE)
        variables.forEach({ key: String, value: Any? -> engineScope.put(key, value) })
        @Suppress("UNCHECKED_CAST")
        return engine.eval(script, newContext) as K
    }

    fun <K> eval(script: String, variables: StreamContext): K {
        val newContext = SimpleScriptContext()
        val engine: ScriptEngine = factory.getEngineByName(SCRIPT_ENGINE)
        newContext.setBindings(variables, ScriptContext.ENGINE_SCOPE)
        @Suppress("UNCHECKED_CAST")
        return engine.eval(script, newContext) as K
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