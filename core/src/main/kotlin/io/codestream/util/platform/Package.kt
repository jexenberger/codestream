package io.codestream.util.platform

interface Package {

    val name: String
    val version: String
    val installed: Boolean

    fun remove()
    fun update()

}