package io.codestream.util.transformation

@FunctionalInterface
interface TypeTransformer<T, K> {

    fun transform(instance: T): K

}