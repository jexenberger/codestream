package io.codestream.util.crypto

import sun.security.tools.keytool.CertAndKeyGen
import sun.security.x509.X500Name
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.cert.X509Certificate
import java.util.*
import sun.security.pkcs10.PKCS10
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.security.Signature


class SSLCertificate(
        val cn: String,
        val ou: String,
        val org: String,
        val locality: String,
        val state: String,
        val country: String = Locale.getDefault().isO3Country,
        val algorithm: String = "RSA",
        val signature: String = "SHA256WithRSA",
        val validity: Long = 730,
        val keySize: Int = 2048

) {


    var key: PrivateKey? = null
    var certificate: X509Certificate? = null

    fun selfSigned(): Pair<PrivateKey, X509Certificate> {
        val keypair = CertAndKeyGen(algorithm, signature, null)
        val x500Name = X500Name(cn, ou, org, locality, state, country)
        keypair.generate(keySize)
        val privateKey = keypair.privateKey
        this.key = privateKey
        val x509Certificate = keypair.getSelfCertificate(x500Name, Date(), validity * 24 * 60 * 60)
        this.certificate = x509Certificate
        return privateKey to x509Certificate
    }

    fun pkcs10CSR(keyPair:KeyPair = generateKeyPair(algorithm, keySize)) : Pair<KeyPair, String> {
        val x500Name = X500Name(cn, ou, org, locality, state, country)
        val sig = Signature.getInstance(signature)
        sig.initSign(keyPair.getPrivate())
        val pkcs10 = PKCS10(keyPair.getPublic())
        pkcs10.encodeAndSign(x500Name, sig)
        val bos = ByteArrayOutputStream()
        pkcs10.print(PrintStream(bos))
        return keyPair to String(bos.toByteArray())
    }

    fun generateKeyPair(alg: String, keySize: Int): KeyPair {
        var keyPairGenerator: KeyPairGenerator? = null
        keyPairGenerator = KeyPairGenerator.getInstance(alg)
        keyPairGenerator.initialize(keySize)
        return keyPairGenerator.generateKeyPair()
    }

}