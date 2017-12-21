package io.codestream.util

import kotlin.reflect.KClass

val mapping = mapOf<KClass<*>, (Array<*>) -> Array<*>>(
        String::class to { array -> array.map { it as String }.toTypedArray() },
        Byte::class to { array -> array.map { it as Byte }.toTypedArray() },
        Short::class to { array -> array.map { it as Short }.toTypedArray() },
        Int::class to { array -> array.map { it as Int }.toTypedArray() },
        Long::class to { array -> array.map { it as Long }.toTypedArray() },
        Float::class to { array -> array.map { it as Float }.toTypedArray() },
        Double::class to { array -> array.map { it as Double }.toTypedArray() },
        Boolean::class to { array -> array.map { it as Boolean }.toTypedArray() },
        Char::class to { array -> array.map { it as Char }.toTypedArray() }
)

@SuppressWarnings("UNCHECKED_CAST")
fun <T> coerce(arr: Array<*>, type: KClass<*>): Array<T> {
    val res = mapping[type]?.let {
        it(arr)
    } ?: arr
    return res as Array<T>
}