package io.codestream.util.transformation

class LambdaTransformer<T, K>(val converter: (a: T) -> K) : TypeTransformer<T, K> {
    override fun transform(instance: T): K {
        return converter(instance)
    }

}