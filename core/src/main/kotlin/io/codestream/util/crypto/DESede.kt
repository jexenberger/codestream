package io.codestream.util.crypto

import java.security.Key
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.SecretKeySpec


class DESede : CipherHandler {

    override fun encrypt(plainText: ByteArray, key: ByteArray): ByteArray {
        val secretKey = SecretKeySpec(key, ALGORITHM)
        return encrypt(plainText, secretKey)
    }

    override fun encrypt(plainText: ByteArray, secretKey: Key): ByteArray {
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        return cipher.doFinal(plainText)
    }

    override fun decrypt(cipherText: ByteArray, key: ByteArray): ByteArray {
        val secretKey = SecretKeySpec(key, ALGORITHM)
        return decrypt(secretKey, cipherText)
    }

    override fun decrypt(secretKey: Key, cipherText: ByteArray): ByteArray {
        val cipher = Cipher.getInstance(ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, secretKey)
        return cipher.doFinal(cipherText)
    }

    companion object {
        private val ALGORITHM = "DESede"

        fun generateKey(): Key {
            val key = KeyGenerator.getInstance(ALGORITHM)
            key.init(168)
            return key.generateKey()

        }

    }
}


