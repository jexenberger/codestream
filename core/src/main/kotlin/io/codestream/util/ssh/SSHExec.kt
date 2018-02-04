package io.codestream.util.ssh

import com.jcraft.jsch.ChannelExec
import com.jcraft.jsch.Session
import java.io.InputStream
import java.io.OutputStream

class SSHExec(val session: Session,
              val xForwarding: Boolean = false) {


    private fun getExecChannel(cmd: String): ChannelExec {
        val channel = session.openChannel("exec")
        val execChannel = channel as ChannelExec
        execChannel.setCommand(cmd)
        execChannel.setXForwarding(xForwarding)
        execChannel.connect()
        return execChannel
    }


    fun run(cmd: String, handler: (String) -> Unit) {
        var execChannel: ChannelExec? = null
        try {
            execChannel = getExecChannel(cmd)
            val input = execChannel.inputStream
            return input.bufferedReader().forEachLine {
                handler(it)
            }
        } finally {
            execChannel?.let { it.disconnect() }
        }
    }

    fun run(cmd: String, handler: (InputStream, OutputStream, InputStream) -> Unit) {
        var execChannel: ChannelExec? = null
        try {
            execChannel = getExecChannel(cmd)
            handler(execChannel.inputStream, execChannel.outputStream, execChannel.errStream)

        } finally {
            execChannel?.let { it.disconnect() }
        }
    }

}