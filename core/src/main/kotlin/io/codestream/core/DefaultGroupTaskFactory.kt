package io.codestream.core

import kotlin.reflect.KClass

fun groupFromClass(groupTaskClass: KClass<out GroupTask>): DefaultClassFactory<GroupTask> {
    return DefaultGroupTaskFactory(groupTaskClass)
}

class DefaultGroupTaskFactory(groupClass: KClass<out GroupTask>) : DefaultClassFactory<GroupTask>(groupClass)