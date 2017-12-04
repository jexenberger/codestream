package io.codestream.resourcemodel

import io.codestream.core.Parameter

data class ResourceDefinition(var type: String,
                              var desc: String,
                              var attributes: Array<Parameter>) {


}