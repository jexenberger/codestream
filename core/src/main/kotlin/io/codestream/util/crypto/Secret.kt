package io.codestream.util.crypto

import io.codestream.runtime.runtime
import java.util.*

data class Secret(val cipherText: ByteArray, val key: ByteArray = runtime.systemKey.encoded, private val cipherHandler: CipherHandler = DESede()) {


    constructor(plainText: String,
                key: ByteArray = runtime.systemKey.encoded,
                cipherHandler: CipherHandler = DESede())
            : this(cipherHandler.encrypt(plainText.toByteArray(), key), key, cipherHandler)


    val cipherTextBase64: String
        get() = Base64.getEncoder().encodeToString(cipherText)

    val value: String
        get() = String(decrypt(value = cipherText, keyFile = key, handler = cipherHandler))


    override fun toString(): String {
        return cipherTextBase64
    }

    companion object {

        fun decryptBase64(value: ByteArray, keyFile: ByteArray, handler: CipherHandler): String {
            val decoded = Base64.getDecoder().decode(value)
            return String(decrypt(decoded, keyFile, handler))
        }

        fun encrypt(value: ByteArray, keyFile: ByteArray, handler: CipherHandler) = handler.encrypt(value, keyFile)

        fun decrypt(value: ByteArray, keyFile: ByteArray, handler: CipherHandler) = handler.decrypt(value, keyFile)
    }

}