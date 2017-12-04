package io.codestream.util.platform

class HomeBrewPackage(override val name: String, override val version: String) : Package {
    override val installed: Boolean
        get() = false

    override fun remove() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun update() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}