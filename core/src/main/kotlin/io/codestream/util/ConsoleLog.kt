package io.codestream.util

class ConsoleLog : Log {


    val ANSI_RESET = "\u001B[39m"
    val ANSI_RED = "\u001B[31m"
    val ANSI_GREEN = "\u001B[32m"

    override fun log(msg: Any) {
        println(msg.toString())
    }

    override fun info(msg: Any) {
        println(decorate(ANSI_GREEN, msg, ANSI_RESET))
    }

    private fun decorate(pre: String, str: Any, post: String): String {
        return "${pre}${str}${post}"
    }

    override fun error(msg: Any, vararg exception: Exception) {
        println(decorate(ANSI_RED, msg, ANSI_RESET))
    }


}