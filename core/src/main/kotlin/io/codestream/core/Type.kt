package io.codestream.core

import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty

data class Type(val id: String,
                val defn: TaskProperty,
                var property: KMutableProperty<*>,
                val typeHint: KClass<*>? = null,
                val values: Array<Any> = arrayOf(),
                val disableEvaluation: Boolean = false) {


}