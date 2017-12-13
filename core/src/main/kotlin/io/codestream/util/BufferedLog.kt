package io.codestream.util

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.io.File

class BufferedLog(val outputPath: String, val group: String, val name: String, val id: String) : Log {


    val messages = mutableListOf<String>()
    val errors = mutableListOf<Pair<String, String>>()

    private val file: File by lazy {
        val path = "$outputPath/$group/$name"
        val folder = File(path)
        if (!folder.exists()) {
            folder.mkdirs()
        }
        File(folder, id)
    }


    @Synchronized
    override fun log(msg: Any?) {
        messages += msg.toString()
        writeBuffer()
    }

    @Synchronized
    override fun debug(msg: Any?) {
        messages += msg.toString()
        writeBuffer()
    }

    @Synchronized
    override fun info(msg: Any?) {
        messages += msg.toString()
        writeBuffer()
    }

    @Synchronized
    override fun error(msg: Any?, vararg exception: Exception) {
        errors += msg.toString() to exception.map { it.message }.joinToString(separator = ",")
        writeBuffer()
    }

    internal fun writeBuffer() {
        file.writeText(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this))
    }

    companion object {

        @JsonIgnore
        private val mapper = ObjectMapper().registerKotlinModule()


        fun fromFile(outputPath: String, group: String, name: String, id: String): BufferedLog? {
            val path = "$outputPath/$group/$name/$id"
            val file = File(path)
            return if (file.exists()) mapper.readValue<BufferedLog>(file.readText()) else null
        }

    }
}