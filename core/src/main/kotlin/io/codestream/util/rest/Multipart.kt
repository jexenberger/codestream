package io.codestream.util.rest

import java.io.File
import java.io.OutputStream
import java.nio.charset.Charset
import java.util.*


data class Multipart(val file: File, val contentType: String = contentTypeFromFile(file), val encoding: String = "UTF-8") {


    fun write(stream: OutputStream, id: String) {
        val httpRequestBodyWriter = stream.bufferedWriter(Charset.forName(encoding))

        httpRequestBodyWriter.write("\n$id\n")
        httpRequestBodyWriter.write("Content-Disposition: form-data;"
                + "name=\"${file.nameWithoutExtension}\";"
                + "filename=\"" + file.name + "\""
                + "\nContent-Type: $contentType\n")
        httpRequestBodyWriter.write("Content-Transfer-Encoding: binary\n\n")
        httpRequestBodyWriter.flush()
        file.inputStream().copyTo(stream)
        httpRequestBodyWriter.flush()

    }

    companion object {

        private val contentTypes = mapOf(
                "yaml" to "application/x-yaml",
                "yml" to "application/x-yaml",
                "json" to "application/json",
                "html" to "text/html",
                "htm" to "text/html",
                "js" to "application/javascript",
                "pdf" to "application/pdf",
                "xml" to "application/xml",
                "zip" to "application/zip",
                "txt" to "text/plain"
        )

        fun contentTypeFromFile(file: File) = contentTypes[file.extension] ?: "application/octet-stream"

        fun getId() = "------------------------${UUID.randomUUID()}"

        fun write(stream: OutputStream, encoding: String, id: String = getId(), parts: Array<Multipart>) {
            for (part in parts) {
                part.write(stream, id)
            }
            val httpRequestBodyWriter = stream.bufferedWriter(Charset.forName(encoding))
            httpRequestBodyWriter.write("\n$id--\n")
            httpRequestBodyWriter.flush()
        }
    }
}