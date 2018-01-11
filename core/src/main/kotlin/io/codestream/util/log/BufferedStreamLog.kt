package io.codestream.util.log

import com.fasterxml.jackson.module.kotlin.readValue
import io.codestream.util.json.json
import java.io.File

data class BufferedStreamLog(val outputPath: String, val group: String, val name: String, val id: String) : Log {
    override var enableDebug: Boolean = false

    val messages = mutableListOf<LogEntry>()

    val path = "$outputPath/$group/$name"

    private val file: File by lazy {
        val path = path
        val folder = File(path)
        if (!folder.exists()) {
            folder.mkdirs()
        }
        File(folder, id)
    }

    @Synchronized
    override fun log(entry: LogEntry) {
        messages += entry
        writeBuffer()
    }


    internal fun writeBuffer() {
        file.writeText(json.writerWithDefaultPrettyPrinter().writeValueAsString(this))
    }

    companion object {


        fun fromFile(outputPath: String, group: String, name: String, id: String): BufferedStreamLog? {
            val path = "$outputPath/$group/$name/$id"
            val file = File(path)
            return if (file.exists()) json.readValue(file.readText()) else null
        }

    }
}