package io.codestream.core

data class ModuleError(val code: String, val msg: String, val failedModules: Map<String, String> = mapOf())