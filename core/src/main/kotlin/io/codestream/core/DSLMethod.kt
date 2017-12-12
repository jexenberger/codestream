package io.codestream.core

import kotlin.reflect.KClass

annotation class DSLMethod @JvmOverloads constructor(
        val description: String = "",
        val executable: KClass<Executable>
)