package io.codestream.util.ssh

import com.jcraft.jsch.ChannelShell
import com.jcraft.jsch.Session
import java.io.InputStream
import java.io.OutputStream

class SSHShell(val session: Session, val prompt: String = "$", val eol: String = "\n", val emulation: String?) {

    val channel: ChannelShell
    val input: InputStream
    val output: OutputStream

    init {
        channel = getExecChannel()
        input = channel.inputStream
        output = channel.outputStream
    }

    private fun getExecChannel(): ChannelShell {
        val channel = session.openChannel("shell") as ChannelShell
        channel.connect()
        emulation?.let { channel.setPtyType(it) }
        return channel
    }

    fun exec(cmd: String, handler: (String) -> Unit) {
        drainBuffer()
        output.write(cmd.toByteArray())
        output.write("\n".toByteArray())
        output.flush()
        var output = ""
        var buffer = ""
        do {
            output = drainBuffer()
            buffer += output
        } while (!output.equals(""))
        buffer = buffer.trim()
        buffer.splitToSequence(eol).filter { it.endsWith(prompt) }.forEach {
            handler(it)
        }
    }

    private fun drainBuffer(): String {
        val arr = ByteArray(input.available())
        input.read(arr)
        val data = String(arr)
        return data
    }

    fun disconnect() {
        channel.disconnect()
    }

}