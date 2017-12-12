package io.codestream.util.ssh

import com.jcraft.jsch.Channel
import com.jcraft.jsch.ChannelExec
import com.jcraft.jsch.Session
import java.io.*


class SSHSession(val session: Session, val xForwarding: Boolean = false, keepAlive:Boolean = true) {


    fun exec(cmd: String, handler: (String) -> Unit) {
        var session: Session = session
        var channel: Channel? = null
        try {
            channel = session.openChannel("exec")
            (channel as ChannelExec).setCommand(cmd)
            val res = ByteArrayOutputStream()
            channel.setXForwarding(xForwarding)
            channel.setErrStream(res)
            channel.setOutputStream(res)
            channel.connect()
            val input = channel.inputStream
            return input.bufferedReader().forEachLine {
                handler(it)
            }
        } finally {
            channel?.let { it.disconnect() }
            disconnect(session)
        }
    }

    fun exec(cmd: String, handler: (InputStream, OutputStream, InputStream) -> Unit) {
        var session: Session = session
        var channel: Channel? = null
        try {
            channel = session.openChannel("exec")
            (channel as ChannelExec).setCommand(cmd)
            channel.connect()
            handler(channel.inputStream, channel.outputStream, channel.errStream)
        } finally {
            channel?.let { it.disconnect() }
            disconnect(session)
        }
    }

    private fun disconnect(session: Session) {
        session.disconnect()
    }

    protected fun waitForAck(input: InputStream): String? {
        val b = input.read()
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
        out.write(0)
        out.flush()
    }

    fun scpFrom(localPath: String, remoteFile: String): String? {
        val cmd = "scp -f ${remoteFile}"
        var result: String? = null
        exec(cmd) { input, output, _ ->
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
                var file: String?
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

    fun scpTo(localFile: String, remoteFile: String): String? {
        val file = File(localFile)
        if (!file.exists()) {
            return "$localFile does not exist"
        }
        val cmd = "scp -t ${remoteFile}"
        var result: String? = null
        exec(cmd) { input, output, _ ->
            val command = "C0644 ${file.length()} ${file.name}\n"
            output.write(command.toByteArray())
            output.flush()
            waitForAck(input)
            file.forEachBlock(1024) { bytes, read ->
                output.write(bytes, 0, read)
            }
            output.flush()
            sendAck(output)
            result = waitForAck(input)?.let { it }
        }
        return result
    }



}