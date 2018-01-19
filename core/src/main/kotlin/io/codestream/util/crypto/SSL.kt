package io.codestream.util.crypto

import io.codestream.util.system
import java.io.File
import java.security.KeyStore
import javax.net.ssl.*


object SSL {


    fun defaultStoreAndKey(host:String, path:String) : JavaKeyStore {
        val (key, certificate) = SSLCertificate(
                cn=host,
                ou="org-unit",
                org="organisation",
                locality ="na",
                state = "na"
        ).selfSigned()
        val store = JavaKeyStore(File(path))
        store.addKeyAndCertificate(host, key, certificate)
        return store
    }

    fun ctx(keyStore: KeyStore, trustStore: KeyStore, password: String = "changeit"): SSLContext {
        val keyManagers: Array<KeyManager>
        val keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
        keyManagerFactory.init(keyStore, password.toCharArray())
        keyManagers = keyManagerFactory.getKeyManagers()

        val trustManagers: Array<TrustManager>
        val trustManagerFactory = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
        trustManagerFactory.init(trustStore)
        trustManagers = trustManagerFactory.getTrustManagers()

        val sslContext: SSLContext = SSLContext.getInstance("TLS")
        sslContext.init(keyManagers, trustManagers, null)
        return sslContext
    }
}