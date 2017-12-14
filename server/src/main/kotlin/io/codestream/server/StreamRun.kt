package io.codestream.server

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.codestream.util.log.BufferedStreamLog
import java.io.File

data class StreamRun(val async: Boolean = false,
                     val parameters: Map<String, Any?>,
                     var id: String?,
                     var log: BufferedStreamLog?) {
    companion object {

        @JsonIgnore
        private val mapper = ObjectMapper().registerKotlinModule()


        fun fromFile(outputPath: String, group: String, name: String, id: String): StreamRun? {
            val path = "$outputPath/$group/$name/$id"
            val file = File(path)
            return if (file.exists()) mapper.readValue<StreamRun>(file.readText()) else null
        }

    }
}