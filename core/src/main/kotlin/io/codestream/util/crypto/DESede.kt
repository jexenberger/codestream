package io.codestream.util.crypto

import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.SecretKeySpec


class DESede : CipherHandler {

    override fun encrypt(plainText: ByteArray, key: ByteArray): ByteArray {
        val cipher = Cipher.getInstance(ALGORITHM)
        val secretKey = SecretKeySpec(key, ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        return cipher.doFinal(plainText)
    }

    override fun decrypt(cipherText: ByteArray, key: ByteArray): ByteArray {
        val secretKey = SecretKeySpec(key, ALGORITHM)
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, secretKey)
        return cipher.doFinal(cipherText)
    }

    companion object {
        private val ALGORITHM = "DESede"

        fun generateKey(): ByteArray {
            val key = KeyGenerator.getInstance(ALGORITHM)
            key.init(168)
            val s = key.generateKey()
            val raw = s.encoded
            return raw
        }

    }
}


