package io.codestream.core

import io.codestream.runtime.StreamContext
import io.codestream.util.Either
import io.codestream.util.fail
import io.codestream.util.ok
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

open class DefaultClassFactory<T : Executable>(val executableType: KClass<out T>) : Factory<T> {

    override fun create(defn: ExecutableDefinition<T>, ctx: StreamContext, module: Module): Either<T, TaskError> {
        val task = executableType.primaryConstructor?.call() ?: return fail(TaskError(defn.id, "UnableToCreateTask", "${defn.type.fqn} does not have a primary constructor"))
        return ok(task)
    }

}