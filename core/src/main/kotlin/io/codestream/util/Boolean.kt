package io.codestream.util

fun Boolean.whenTrue(handler: () -> Unit): Boolean {
    if (this) {
        handler()
    }
    return this
}

fun <T> Boolean.returnIfTrue(handler: () -> T): T? {
    if (this) {
        return handler()
    }
    return null
}