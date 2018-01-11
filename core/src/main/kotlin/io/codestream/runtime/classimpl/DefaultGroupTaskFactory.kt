package io.codestream.runtime.classimpl

import io.codestream.core.GroupTask
import kotlin.reflect.KClass

fun groupFromClass(groupTaskClass: KClass<out GroupTask>): DefaultGroupTaskFactory {
    return DefaultGroupTaskFactory(groupTaskClass)
}

class DefaultGroupTaskFactory(groupClass: KClass<out GroupTask>) : DefaultClassFactory<GroupTask>(groupClass)