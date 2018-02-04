package io.codestream.util.ssh

import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.nio.file.Files

class SCP(val session: SSHSession) {

    protected fun waitForAck(input: InputStream): String? {
        val b = input.read()
        println("CHARACTER -> ${b}")
        if (b == -1) {
            return "no response"
        } else if (b != 0) {
            val sb = StringBuilder()

            var c = input.read()
            while (c > 0 && c != '\n'.toInt()) {
                sb.append(c.toChar())
                c = input.read()
            }

            return when (b) {
                1 -> "server error: ${sb}"
                2 -> "fatal error: ${sb}"
                else -> "unknown error '${b}' : $sb"
            }

        }
        return null
    }

    protected fun sendAck(out: OutputStream) {
        out.write(byteArrayOf(0))
        out.flush()
    }


    fun from(localPath: String, remoteFile: String): String? {
        val cmd = "scp -f ${remoteFile}"
        var result: String? = null
        session.exec(cmd) { input, output, _ ->
            val buf = ByteArray(1024)
            sendAck(output)
            input.read(buf, 0, 1)
            val channelRet = buf[0].toChar()
            if (channelRet == 'C') {
                input.read(buf, 0, 5)
                var filesize = 0L
                while (true) {
                    if (input.read(buf, 0, 1) < 0) {
                        // error
                        break
                    }
                    if (buf[0] == ' '.toByte()) break
                    filesize = filesize * 10L + (buf[0] - '0'.toByte())
                }
                val file: String?
                var i = 0
                while (true) {
                    input.read(buf, i, 1)
                    if (buf[i] == 0x0a.toByte()) {
                        file = String(buf, 0, i)
                        break
                    }
                    i++
                }
                sendAck(output)
                val pathToUse = if (File(localPath).isDirectory) {
                    File(localPath, file)
                } else {
                    File(localPath)
                }
                val fos = FileOutputStream(pathToUse)
                var read = input.read(buf)
                while (read > 0) {
                    fos.write(buf, 0, read)
                    read = input.read(buf, 0, input.available())
                }
                fos.flush()
                fos.close()
            } else {
                result = "Uknown channel error on ACK -> $channelRet"
            }
        }
        return result
    }

    fun to(localFile: String, remoteFile: String): String? {
        val file = File(localFile)
        if (!file.exists()) {
            return "$localFile does not exist"
        }
        val cmd = "scp -t $remoteFile"
        var result: String? = null
        session.exec(cmd) { input, output, _ ->
            println(file.length())
            val command = "C0644 ${file.length()} ${file.name}\n"
            output.write(command.toByteArray())
            output.flush()
            waitForAck(input)
            Files.copy(file.toPath(), output)
            output.flush()
            sendAck(output)
            output.flush()
            output.flush()
            result = waitForAck(input)
            result = waitForAck(input)

        }
        return result
    }

}