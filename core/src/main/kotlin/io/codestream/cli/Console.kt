package io.codestream.cli

object Console {

    val ANSI_RESET = "\u001B[39m"
    val ANSI_RED = "\u001B[31m"
    val ANSI_GREEN = "\u001B[32m"


    private fun decorate(pre: String, str: Any?, post: String): String {
        return "${pre}${str}${post}"
    }

    fun line(line: Any?) {
        println(line)
    }

    fun newLine() {
        println()
    }

    fun red(line: Any?) {
        println(decorate(ANSI_RED, line, ANSI_RESET))
    }

    fun green(line: Any?) {
        println(decorate(ANSI_GREEN, line, ANSI_RESET))
    }


}