package io.codestream.core

import io.codestream.doc.ExecutableDocumentation
import io.codestream.doc.ParameterDocumentation
import io.codestream.runtime.StreamContext
import io.codestream.util.Either
import io.codestream.util.fail
import io.codestream.util.ok
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.primaryConstructor

open class DefaultClassFactory<T : Executable>(val executableType: KClass<out T>) : Factory<T> {

    lateinit var description: TaskDescriptor

    init {
        executableType.findAnnotation<TaskDescriptor>()?.let {
            description = it
        } ?: throw IllegalStateException("No ${TaskDescriptor::class.qualifiedName} annotation on ${executableType}")
    }


    override val documentation: ExecutableDocumentation
        get() {

            val parameters = TaskBinder.extractTaskParameters(executableType)
            val parmDoc: Array<ParameterDocumentation> = parameters.map { (k, v) ->
                ParameterDocumentation(k, v.defn.description, v.typeHint.toString())
            }.toTypedArray()
            return ExecutableDocumentation(
                    description.name,
                    description.description,
                    parmDoc
            )
        }

    override fun create(defn: ExecutableDefinition<T>, ctx: StreamContext, module: Module): Either<T, TaskError> {
        val task = executableType.primaryConstructor?.call() ?: return fail(TaskError(defn.id, "UnableToCreateTask", "${defn.type.fqn} does not have a primary constructor"))
        return ok(task)
    }

}