package io.codestream.util.transformation

import kotlin.reflect.KClass

annotation class TransformerHint(
        val name: String,
        val targetType: KClass<*>
)