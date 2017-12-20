package io.codestream.doc

data class ParameterDocumentation(
        val name: String,
        val description: String,
        val type: String,
        val examples: Array<String> = arrayOf())