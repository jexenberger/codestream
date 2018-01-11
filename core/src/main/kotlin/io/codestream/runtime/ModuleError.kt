package io.codestream.runtime

data class ModuleError(val code: String, val msg: String, val failedModules: Map<String, String> = mapOf())