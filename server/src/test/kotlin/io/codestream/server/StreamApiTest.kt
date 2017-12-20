package io.codestream.server

import com.fasterxml.jackson.module.kotlin.readValue
import io.codestream.util.json.json
import io.codestream.util.ok
import org.junit.Test
import kotlin.test.assertTrue

class StreamApiTest {


    @Test
    fun testRunStream() {
        val mapper = json
        val sampleBody = """{
            "async" : false,
            "parameters" : {
               "saying" : "hello world"
             }}"""
        val runStream = mapper.readValue<StreamRun>(sampleBody)
        val result = StreamApi().runStream("src/test","resources","sample", runStream)
        println(result)
        result.map({
            println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(it))
            true
        }, {
            println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(it))
            false
        })
        assertTrue { result.ok() }
    }
}