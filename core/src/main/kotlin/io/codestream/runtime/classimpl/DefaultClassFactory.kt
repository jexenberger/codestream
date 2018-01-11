package io.codestream.runtime.classimpl

import io.codestream.core.*
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

    override fun getBinding(parms: Map<String, Any?>): Binding<T> {
        return { id: TaskId, ctx: StreamContext, instance: T ->
            TaskBinder.bind(id, instance, ctx, parms)
        }
    }


    override val documentation: ExecutableDocumentation
        get() {
            val instance = newInstance() ?: throw IllegalStateException("Unable to create instance of '$executableType', not default constructor")
            val parameters = TaskBinder.extractTaskParameters(executableType)
            val parmDoc: Array<ParameterDocumentation> = parameters.map { (k, v) ->
                val typeToUser = v.property.returnType.classifier as KClass<*>
                val default = v.property.getter.call(instance).takeIf {
                    when (it) {
                        is String -> it.isNotBlank()
                        is Array<*> -> it.isNotEmpty()
                        is Collection<*> -> it.isNotEmpty()
                        is Map<*, *> -> it.isNotEmpty()
                        else -> true
                    }
                }
                ParameterDocumentation(
                        k,
                        v.defn.description,
                        Parameter.stringType(typeToUser) ?: "string",
                        default?.toString()
                )
            }.toTypedArray()
            return ExecutableDocumentation(
                    description.name,
                    description.description,
                    parmDoc
            )
        }

    override fun create(defn: ExecutableDefinition<T>, ctx: StreamContext, module: Module): Either<T, TaskError> {
        val task = newInstance() ?: return fail(TaskError(defn.id, "UnableToCreateTask", "${defn.type.fqn} does not have a primary constructor"))
        return ok(task)
    }

    private fun newInstance() = executableType.primaryConstructor?.call()

}