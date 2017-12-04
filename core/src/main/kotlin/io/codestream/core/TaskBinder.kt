package io.codestream.core

import io.codestream.runtime.StreamContext
import io.codestream.util.transformation.TransformerService
import io.codestream.util.validator
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.jvm.jvmErasure

interface TaskBinder : Executable {

    override fun bind(defn: ExecutableDefinition, ctx: StreamContext): TaskError? {
        return TaskBinder.bind(
                defn = defn,
                task = this,
                ctx = ctx
        )
    }

    companion object {
        fun <T : Executable> extractTaskParameters(clazz: KClass<out T>): Map<String, Type> {
            val properties = clazz.members.filterIsInstance<KMutableProperty<*>>()
            return properties
                    .filter {
                        !it.annotations.filterIsInstance<TaskProperty>().isEmpty()
                    }
                    .groupByTo(mutableMapOf(), { it.name })
                    .mapValues { it.value[0] }
                    .mapValues {
                        val decl = it.value.annotations.filterIsInstance<TaskProperty>()[0]
                        Type(id = it.key, property = it.value, defn = decl, disableEvaluation = decl.disableEvaluation)
                    }
        }

        fun <T : Executable> bind(defn: ExecutableDefinition, task: T, ctx: StreamContext): TaskError? {
            val taskParameters = extractTaskParameters(task::class)
            val parentError = taskParameterValidation(defn.id, msg = "There was a problem binding '${defn.type.fqn}'")
            for ((input, value) in defn.bindingParams) {
                val type = taskParameters[input] ?: return taskParameterValidation(defn.id, msg = "property '${input}' does not exist on task ${defn.type.fqn}")
                try {
                    val typeHint = type.property.returnType.jvmErasure
                    val valueToSet = if (type.disableEvaluation)
                        TransformerService.convertWithNull<Any?>(value, typeHint)
                    else {
                        ctx.evalTo<Any?>(value, typeHint)
                    }
                    type.property.setter.call(task, valueToSet)
                } catch (e: RuntimeException) {
                    parentError += taskParameterValidation(defn.id, msg = "property '${input}' failed binding with -> ${e.message}")
                }
            }
            val result = validator().validate(task)
            for (error in result) {
                parentError += taskParameterValidation(defn.id, msg = "${error.propertyPath} ${error.message}")
            }
            return parentError.takeIf { it.errors.isNotEmpty() }
        }
    }
}