package io.codestream.core

data class InputError(val id: String,
                      val code: String,
                      val msg: String) : Exception("$id::$code -> $msg") {

}