package io.codestream.core

import io.codestream.resourcemodel.Resource
import io.codestream.util.Either
import io.codestream.util.fail
import io.codestream.util.ok
import io.codestream.util.transformation.TransformerService
import kotlin.reflect.KClass

data class Parameter(val name: String,
                     val desc: String,
                     private val stringType: String,
                     private val defaultString: String? = null,
                     val required: Boolean = true,
                     val allowedValuesList: String? = null) {

    val type: KClass<*>
    val values: Array<Any>
    val defaultValue: Any?

    init {
        val targetType = typeFor(stringType)
        type = targetType ?: throw IllegalStateException("${stringType} is not supported, supported types are '$typeNames'")
        val isArray = isArrayType(stringType)
        val conversionType = if (isArray) targetType else typeFor("[$stringType]")
        if (allowedValuesList != null && !allowedValuesList.trim().isBlank() && !isArray) {
            values = conversionType?.let { TransformerService.convert<Array<Any>>(allowedValuesList, it) } ?: throw SystemException("${stringType} is not support, supported types are '$typeNames'")
        } else {
            values = arrayOf()
        }
        if (defaultString != null) {
            defaultValue = defaultString?.let { TransformerService.convert(it, type) }
        } else {
            defaultValue = null
        }

    }

    fun isIn(value: Any): Boolean {
        if (values.isEmpty()) {
            return true
        }
        return values.contains(value)
    }


    fun from(value: Any?): Any? {
        return value?.let {
            val result = TransformerService.convert<Any>(it, type)
            result
        } ?: defaultValue
    }


    fun fromString(value: String?): Any? {
        return from(value)
    }

    fun tryConvert(value: Any?): Either<Any?, List<String>> {
        if (required && value == null && defaultValue == null) {
            return fail(listOf("required"))
        }
        if (value == null && defaultValue != null) {
            return ok(from(value))
        }
        val targetValue = from(value!!)
        if (!isIn(targetValue!!)) {
            return fail(listOf("$value is not in $allowedValuesList"))
        }
        return ok(targetValue)
    }

    companion object {

        val types = mapOf<String, KClass<*>>(
                "string" to String::class,
                "int" to Int::class,
                "long" to Long::class,
                "boolean" to Boolean::class,
                "float" to Float::class,
                "resource" to Resource::class,
                "[resource]" to Array<Resource>::class,
                "[string]" to Array<String>::class,
                "[int]" to Array<Int>::class,
                "[float]" to Array<Float>::class,
                "[long]" to Array<Long>::class,
                "[keyValue]" to Map::class
        )

        val typeNames: Array<String>
            get() = types.map { it.key }.toTypedArray()

        fun typeFor(type: String): KClass<*>? {
            return types[type]
        }

        private fun isArrayType(arrayType: String) = arrayType.startsWith("[") && arrayType.endsWith("]")

    }
}