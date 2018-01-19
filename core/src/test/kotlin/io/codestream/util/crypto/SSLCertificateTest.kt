package io.codestream.util.crypto

import org.junit.Test

class SSLCertificateTest {




    @Test
    fun testGeneratePKCS10CSR() {
        val cert = SSLCertificate(
                cn="localhost",
                ou="codestream",
                org="codestream",
                locality ="nowhereia",
                state = "nowhereiastate",
                country = "SA"
        )

        val (keyPair, csr) = cert.pkcs10CSR()
        println(csr)
        println(keyPair.private)
        println(keyPair.public)

    }

    @Test
    fun testSelfSigned() {
        val cert = SSLCertificate(
                cn="localhost",
                ou="codestream",
                org="codestream",
                locality ="nowhereia",
                state = "nowhereiastate",
                country = "SA"
        )
        val (keyPair, certificate) = cert.selfSigned()
        println(certificate)
        println(keyPair)
    }
}