package io.codestream.util.crypto

import java.io.File
import java.util.*

data class SimpleKeyStore(val file: String) {

    val store: Properties = Properties()

    init {
        loadFromFile()
    }

    private fun loadFromFile() {
        val storeFile = File(file)
        if (storeFile.exists() && storeFile.isFile) {
            store.load(storeFile.inputStream())
        }
    }

    @Synchronized
    fun store(name: String, key: ByteArray) {
        store.setProperty(name, String(Base64.getEncoder().encode(key)))
        store.store(File(file).outputStream(), "Written by keystore")
    }

    @Synchronized
    fun load(name: String): ByteArray? {
        return store.getProperty(name)?.let { Base64.getDecoder().decode(it) }
    }

}