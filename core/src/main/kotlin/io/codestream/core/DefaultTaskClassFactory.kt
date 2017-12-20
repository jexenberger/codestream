package io.codestream.core

import kotlin.reflect.KClass


fun taskFromClass(taskClass: KClass<out Task>): DefaultTaskClassFactory {
    return DefaultTaskClassFactory(taskClass)
}

class DefaultTaskClassFactory(taskClass: KClass<out Task>) : DefaultClassFactory<Task>(taskClass)