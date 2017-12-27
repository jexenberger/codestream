package io.codestream.util.crypto

interface CipherHandler {
    fun encrypt(plainText: ByteArray, key: ByteArray): ByteArray

    fun decrypt(cipherText: ByteArray, key: ByteArray): ByteArray
}