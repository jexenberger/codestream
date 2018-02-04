package io.codestream.module.cryptomodule

import io.codestream.core.*
import io.codestream.runtime.StreamContext
import io.codestream.runtime.runtime
import io.codestream.util.crypto.CipherHandler
import io.codestream.util.crypto.DESede
import java.io.File
import javax.crypto.spec.SecretKeySpec

enum class Algorthims(val handler: CipherHandler) {
    desede(DESede())
}

@TaskDescriptor("encrypt", description = "Encrypt a value from a key file, stored in plain text")
class EncryptTask : Task, SetOutput {


    @TaskProperty(description = "Variable to set with the value of the resultant cipher text as a byte array")
    override var outputVar: String = "\$cipherText"

    @TaskProperty(description = "File which stored key in plain bytes (not base 64)")
    var keyFile: File = File(runtime.systemKeyPath)

    @TaskProperty(description = "String to encrypt")
    var value: String = ""

    @TaskProperty(description = "Algorithm to use (currently on triple DES (desede) is supported")
    var algorithm: Algorthims = Algorthims.desede

    override fun execute(id: TaskId, ctx: StreamContext): TaskError? {
        if (!keyFile.exists() || keyFile.isDirectory) {
            return invalidParameter(id, "${keyFile.absolutePath} does not exist or is a directory")
        }

        val handler = algorithm.handler
        ctx[outputVar] = handler.encrypt(
                value.toByteArray(),
                SecretKeySpec(keyFile.readBytes(), handler.algorithm).encoded
        )
        return done()
    }
}