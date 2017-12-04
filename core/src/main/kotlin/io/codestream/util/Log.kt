package io.codestream.util

interface Log {
    fun log(msg: Any)
    fun info(msg: Any)
    fun error(msg: Any, vararg exception: Exception)
}