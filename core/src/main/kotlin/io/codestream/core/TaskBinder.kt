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
                ctx.log.debug(id, "BEGIN ---> Binding '$input' with '$value@${value?.let { it::class.qualifiedName }}'")
                val type = taskParameters[input] ?: return taskParameterValidation(id, msg = "property '${input}' does not exist on task ${taskType.fqn}")
                try {
                    val typeHint = type.property.returnType.jvmErasure
                    ctx.log.debug(id, "Binding to type => '${typeHint.qualifiedName}'")
                    val valueToSet = if (type.disableEvaluation)
                        TransformerService.convertWithNull<Any?>(value, typeHint)
                    else {
                        ctx.log.debug(id, "Evaluating '$value'")
                        ctx.evalTo(value, typeHint)
                    }
                    if (!type.property.returnType.isMarkedNullable && valueToSet == null) {
                        return taskParameterValidation(id, msg = "property '${input}' is required and does not allow null values")
                    }
                    ctx.log.debug(id, "resolved binding value to -> '$valueToSet' of type '${valueToSet?.let { it::class.qualifiedName }}' to property type ${type.property.returnType}")
                    type.property.setter.call(task, valueToSet)
                    ctx.log.debug(id, "Property '$input' successfully been bound")
                } catch (e: RuntimeException) {
                    ctx.log.error(id, "Unable to bind '$input' to '$value'", e)
                    parentError += taskParameterValidation(id, msg = "property '${input}' failed binding with -> ${e.message}")
                } finally {
                    ctx.log.debug(id, "END --->")
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