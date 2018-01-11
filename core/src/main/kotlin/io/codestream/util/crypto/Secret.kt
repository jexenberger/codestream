package io.codestream.util.crypto

import io.codestream.util.system
import java.util.*

data class Secret(val cipherText: ByteArray, var keyFile: String = "${system.homeDir}/.cs/key", private val cipherHandler: CipherHandler = DESede()) {


    constructor(value: String,
                keyFile: String = "${system.homeDir}/.cs/key",
                cipherHandler: CipherHandler = DESede()) :
            this(Secret.encrypt(value, keyFile, cipherHandler),
                    keyFile,
                    cipherHandler)

    val cipherTextBase64: String
        get() = Base64.getEncoder().encodeToString(cipherText)

    val value: String
        get() = decrypt(cipherText, keyFile, cipherHandler)


    override fun toString(): String {
        return cipherTextBase64
    }

    companion object {

        fun decryptBase64(value: ByteArray, keyFile: String, handler: CipherHandler): String {
            val decoded = Base64.getDecoder().decode(value)
            return decrypt(decoded, keyFile, handler)
        }

        fun encrypt(value: String, keyFile: String, handler: CipherHandler): ByteArray {
            val store = SimpleKeyStore(file = keyFile)
            return store.load("key")?.let {
                handler.encrypt(value.toByteArray(), it)
            } ?: throw IllegalArgumentException("unable to retrieve key, please configure")
        }

        fun decrypt(value: ByteArray, keyFile: String, handler: CipherHandler): String {
            val store = SimpleKeyStore(file = keyFile)
            return store.load("key")?.let {
                String(handler.decrypt(value, it))
            } ?: throw IllegalArgumentException("unable to retrieve key, please configure")
        }
    }

}