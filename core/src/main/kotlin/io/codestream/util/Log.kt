package io.codestream.util

interface Log {
    fun debug(msg: Any?)
    fun log(msg: Any?)
    fun info(msg: Any?)
    fun error(msg: Any?, vararg exception: Exception)
}