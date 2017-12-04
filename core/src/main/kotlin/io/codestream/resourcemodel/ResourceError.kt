package io.codestream.resourcemodel

import io.codestream.util.Error

data class ResourceError(val errors: List<String>) : RuntimeException(errors.toString()), Error