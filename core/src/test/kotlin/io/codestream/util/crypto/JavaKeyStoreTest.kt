package io.codestream.util.crypto

import io.codestream.util.system
import org.junit.Test
import java.io.File
import java.util.*
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class JavaKeyStoreTest {


    @Test
    fun testAddKeyPairCertificate() {
        val cert = SSLCertificate(
                cn="localhost",
                ou="codestream",
                org="codestream",
                locality ="nowhereia",
                state = "nowhereiastate"
        )
        val file = File("${system.tempDir}/${UUID.randomUUID()}")
        file.deleteOnExit()
        val store = JavaKeyStore(file, "changeit")
        val (keyPair, certificate) = cert.selfSigned()
        store.addKeyAndCertificate("test", keyPair, certificate)
        assertTrue { file.exists() }

        val newStore = JavaKeyStore(file, "changeit")
        assertNotNull(newStore["test"])
        println(newStore["test"])

    }
}