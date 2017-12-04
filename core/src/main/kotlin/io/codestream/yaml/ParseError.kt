package io.codestream.yaml

data class ParseError(val code: String, val msg: String) : Exception(msg) {

}