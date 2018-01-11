package io.codestream.runtime

data class ParseError(val code: String, val msg: String) : Exception(msg)