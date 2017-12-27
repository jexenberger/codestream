package io.codestream.core

import io.codestream.resourcemodel.Resource
import io.codestream.util.Either
import io.codestream.util.fail
import io.codestream.util.ok
import io.codestream.util.transformation.TransformerService
import kotlin.reflect.KClass

data class Parameter(val required: Boolean,
                     val name: String,
                     val desc: String,
                     val type: KClass<*>,
                     val defaultValue: Any? = null,
                     val values: Array<*> = emptyArray<Any>()) {


    constructor(name: String,
                desc: String,
                stringType: String,
                defaultString: String? = null,
                required: Boolean = true,
                allowedValuesList: String? = null) :
            this(required,
                    name,
                    desc,
                    resolveType(stringType),
                    resolveDefaultString(defaultString),
                    resolveAllowedValues(stringType, allowedValuesList))


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
            return fail(listOf("$value is not in ${values.joinToString(", ")}"))
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

        fun stringType(type: KClass<*>): String? = types.filterValues { it == type }.keys.singleOrNull()

        private fun resolveAllowedValues(stringType: String, allowedValuesList: String?): Array<Any> {
            val isArray = isArrayType(stringType)
            val conversionType = if (isArray) typeFor(stringType) else typeFor("[$stringType]")
            if (allowedValuesList != null && !allowedValuesList.trim().isBlank() && !isArray) {
                return conversionType?.let { TransformerService.convert<Array<Any>>(allowedValuesList, it) } ?: throw SystemException("${stringType} is not support, supported types are '$typeNames'")
            } else {
                return arrayOf()
            }
        }

        private fun resolveType(stringType: String)
                = typeFor(stringType) ?: throw IllegalStateException("${stringType} is not supported, supported types are '$typeNames'")

        private fun resolveDefaultString(defaultString: String?): Any? {
            return defaultString?.let { TransformerService.convert(it) }
        }


        private fun isArrayType(arrayType: String) = arrayType.startsWith("[") && arrayType.endsWith("]")

    }
}