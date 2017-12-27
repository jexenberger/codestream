package io.codestream.util.crypto

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class DESedeTest {


    @Test
    fun testEncrypt() {
        val key = DESede.generateKey()
        val aes = DESede()
        val encrypt = aes.encrypt("hello world".toByteArray(), key)
        assertNotNull(encrypt)
        val decrypt = aes.decrypt(encrypt, key)
        assertEquals("hello world", String(decrypt))

    }
}