package io.codestream.kts

import io.codestream.module.coremodule.echo
import io.codestream.module.coremodule.foreach
import io.codestream.module.coremodule.runStream
import io.codestream.module.coremodule.sleep
import io.codestream.util.mapOf
import org.junit.Test
import java.io.File
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

                echo("#{aValue}")
                echo("#{aValue}").onlyIf { _, _ -> true }

                foreach("1, 2, 3") {
                    echo("hello world - 1")
                    echo("hello world - 2")
                    echo("hello world - 3")
                    echo("#{theIterator}")
                    sleep(2000)
                }.onlyIf { _, _ -> true }

                runStream("test.yml") {
                    mapOf {
                        put("one" to 1)
                        put("two" to 2)

                    }
                }

            }

        }
    }

    @Test
    fun testRunStreamFromFile() {
        val factory = ScriptEngineManager()
        val engine = factory.getEngineByName("kotlin")
        val newContext = SimpleScriptContext()
        //val engineScope = newContext.getBindings(ScriptContext.ENGINE_SCOPE)
        engine.eval(File("src/test/resources/sample.kt").reader(), newContext)
    }
}