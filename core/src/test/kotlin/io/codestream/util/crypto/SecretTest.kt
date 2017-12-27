package io.codestream.util.crypto

import org.junit.Test
import java.util.*
import kotlin.test.assertEquals

class SecretTest {

    private val file = createTempFile("crypto", "test").absolutePath
    private val simpleKeyStore = SimpleKeyStore(file)

    private val key = DESede.generateKey()

    init {
        simpleKeyStore.store("key", key)
    }


    @Test
    fun testDecrypt() {
        val handler = DESede()
        val encrypt = handler.encrypt("hello world".toByteArray(), key)
        val decrypt = Secret.decrypt(encrypt, file, handler)
        assertEquals("hello world", decrypt)

    }

    @Test
    fun testEncrypt() {
        val handler = DESede()
        val encrypt = handler.encrypt("hello world".toByteArray(), key)
        val check = Secret.encrypt("hello world", file, handler)
        assertEquals(String(encrypt), String(check))
    }

    @Test
    fun testCipherTextBase64() {
        val handler = DESede()
        val encrypt = String(Base64.getEncoder().encode(handler.encrypt("hello world".toByteArray(), key)))
        val secret = Secret("hello world", file)
        assertEquals(encrypt, secret.cipherTextBase64)
    }

    @Test
    fun testCreateFromCipherText() {
        val handler = DESede()
        val encrypt = Base64.getEncoder().encode(handler.encrypt("hello world".toByteArray(), key))
        val secret = Secret(encrypt, file)
        assertEquals("hello world", secret.value)
    }
}