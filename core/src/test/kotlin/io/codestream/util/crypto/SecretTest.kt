package io.codestream.util.crypto

import org.junit.Test
import java.util.*
import kotlin.test.assertEquals

class SecretTest {


    private val key = DESede.generateKey()



    @Test
    fun testDecrypt() {
        val handler = DESede()
        val encrypt = handler.encrypt("hello world".toByteArray(), key.encoded)
        val decrypt = Secret.decrypt(encrypt, key.encoded, handler)
        assertEquals("hello world", String(decrypt))

    }

    @Test
    fun testEncrypt() {
        val handler = DESede()
        val encrypt = handler.encrypt("hello world".toByteArray(), key.encoded)
        val check = Secret.encrypt("hello world".toByteArray(), key.encoded, handler)
        assertEquals(String(encrypt), String(check))
    }

    @Test
    fun testCipherTextBase64() {
        val handler = DESede()
        val encrypt = Base64.getEncoder().encodeToString(handler.encrypt("hello world".toByteArray(), key.encoded))
        val secret = Secret("hello world", key.encoded)
        assertEquals(encrypt, secret.cipherTextBase64)
    }
}