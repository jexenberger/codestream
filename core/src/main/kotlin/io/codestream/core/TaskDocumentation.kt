package io.codestream.core

data class TaskDocumentation(
        val name: String,
        val description: String,
        val params: Array<ParameterDocumentation> = arrayOf(),
        val examples: Array<String> = arrayOf()) {
}