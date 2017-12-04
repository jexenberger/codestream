package io.codestream.util

fun <T, K> ok(value: T): Either<T, K> = Either.left(value)
fun <K> ok(): Either<Unit, K> = Either.left(Unit)
fun <T, K> ok(value: () -> T): Either<T, K> = Either.left(value())
fun <T, K> fail(value: K): Either<T, K> = Either.right(value)
fun <T, K> fail(value: () -> K): Either<T, K> = Either.right(value())

sealed class Either<out L, out R> {


    val left: L?
        get() = when (this) {
            is Either.Left -> this.value
            is Either.Right -> null
        }


    val right: R?
        get() = when (this) {
            is Either.Left -> null
            is Either.Right -> this.value
        }


    class Left<out L, out R>(val value: L) : Either<L, R>() {
        override fun toString(): String = "Left: $value"
    }

    class Right<out L, out R>(val value: R) : Either<L, R>() {
        override fun toString(): String = "Right: $value"
    }







    fun <U, Z> apply(l: (L) -> Either<U, Z>, r: (R) -> Either<U, Z>): Either<U, Z> {
        return when (this) {
            is Either.Left -> l(this.value)
            is Either.Right -> r(this.value)
        }
    }



    fun <Z> map(l: (value: L) -> Z, r: (error: R) -> Z): Z {
        return when (this) {
            is Either.Left -> l(this.value)
            is Either.Right -> r(this.value)
        }
    }

    fun <Z> mapL(l: (value: L) -> Z): Either<Z, R> {
        return when (this) {
            is Either.Left -> left<Z, R>(l(this.value))
            is Either.Right -> this as Either<Z, R>
        }
    }

    fun <Z> mapR(r: (value: R) -> Z): Either<L, Z> {
        return when (this) {
            is Either.Left -> this as Either<L, Z>
            is Either.Right -> right<L, Z>(r(this.value))
        }
    }

    operator fun component1() : L? {
        return left
    }

    operator fun component2() : R? {
        return right
    }



    companion object {
        fun <Z, K> left(left: Z): Either<Z, K> {
            return Either.Left<Z, K>(left)
        }

        fun <Z, K> right(right: K): Either<Z, K> {
            return Either.Right<Z, K>(right)
        }

        fun <Z, Y : Exception> onException(handler: () -> Z): Either<Z, Y> {
            return Either.onException<Z, Y, Y>(handler, { it })
        }

        fun <Z, Y, U : Exception> onException(handler: () -> Z, onError: (U) -> Y): Either<Z, Y> {
            try {
                return ok(handler())
            } catch (e: Exception) {
                return fail(onError(e as U))
            }
        }
    }

}



inline infix fun <Z,L,R> Either<L,R>.bind(f: (L) -> Either<Z, R>): Either<Z, R> {
    return when (this) {
        is Either.Left -> f(this.value)
        is Either.Right -> Either.Right(this.value)
    }
}



inline infix fun <Z,L,R> Either<L,R>.bindR(f: (R) -> Either<L, Z>): Either<L, Z> {
    return when (this) {
        is Either.Left -> Either.Left(this.value)
        is Either.Right -> f(this.value)
    }
}