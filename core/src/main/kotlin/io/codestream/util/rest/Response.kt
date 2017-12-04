package io.codestream.util.rest

data class Response(val status: Int, val responseMessage: String, val body: String = "", val headers: Map<String, List<String>> = mapOf())