package io.codestream.core

annotation class TaskProperty(
        val alias: String = "",
        val description: String,
        val disableEvaluation: Boolean = false
)