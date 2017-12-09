package io.codestream.core

import io.codestream.runtime.StreamContext
import io.codestream.util.transformation.TransformerService
import io.codestream.util.validator
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.jvm.jvmErasure

interface TaskBinder {

    companion object {
        fun extractTaskParameters(clazz: KClass<*>): Map<String, Type> {
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

        fun bind(id: TaskId, taskType: TaskType, task: Any, ctx: StreamContext, params: Map<String, Any?>): TaskError? {
            val taskParameters = extractTaskParameters(task::class)
            val parentError = taskParameterValidation(id, msg = "There was a problem binding '${taskType.fqn}'")
            for ((input, value) in params) {
                val type = taskParameters[input] ?: return taskParameterValidation(id, msg = "property '${input}' does not exist on task ${taskType.fqn}")
                try {
                    val typeHint = type.property.returnType.jvmErasure
                    val valueToSet = if (type.disableEvaluation)
                        TransformerService.convertWithNull<Any?>(value, typeHint)
                    else {
                        ctx.evalTo(value, typeHint)
                    }
                    type.property.setter.call(task, valueToSet)
                } catch (e: RuntimeException) {
                    parentError += taskParameterValidation(id, msg = "property '${input}' failed binding with -> ${e.message}")
                }
            }
            val result = validator().validate(task)
            for (error in result) {
                parentError += taskParameterValidation(id, msg = "${error.propertyPath} ${error.message}")
            }
            return parentError.takeIf { it.errors.isNotEmpty() }
        }
    }
}