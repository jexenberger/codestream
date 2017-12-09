package io.codestream.kts

import io.codestream.module.coremodule.echo
import org.junit.Test
import java.io.File
import javax.script.ScriptContext
import javax.script.ScriptEngineManager
import javax.script.SimpleScriptContext


class KtsStreamBuilderTest {

    @Test
    fun testStream() {
        stream(name = "MyCoolTestStream") {

            inputs {
                input("aValue", "A value to enter", required = true, optionsList = arrayOf("one", "two"))
                input<String>("aValueTwo", "A second value to enter")
            }

            tasks {
                echo("\${aValue}")
                echo("\${aValue}")
                        .onlyIf { defn, ctx -> true }
            }

        }
    }

    @Test
    fun testRunStreamFromFile() {
        val factory: ScriptEngineManager = ScriptEngineManager()
        val engine = factory.getEngineByName("kotlin")
        val newContext = SimpleScriptContext()
        val engineScope = newContext.getBindings(ScriptContext.ENGINE_SCOPE)
        engine.eval(File("src/test/resources/sample.kt").reader(), newContext)
    }
}