package io.codestream.runtime

data class ParseError(val code: String, val msg: String, val line: Int = 0) : Exception(msg)