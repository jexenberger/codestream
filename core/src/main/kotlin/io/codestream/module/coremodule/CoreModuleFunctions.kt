package io.codestream.module.coremodule

class CoreModuleFunctions {

    fun split(value: String) = value.split(",").map { it.trim() }

}