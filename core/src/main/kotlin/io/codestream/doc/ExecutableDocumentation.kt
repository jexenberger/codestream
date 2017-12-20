package io.codestream.doc

data class ExecutableDocumentation(
        val name: String,
        val description: String,
        val params: Array<ParameterDocumentation> = arrayOf(),
        val examples: Array<String> = arrayOf())