package io.codestream.util


fun <T, K : RuntimeException> returnError(error: K) {
    throw error
}

class Try<out T, out K> constructor(val value: T?, val error: K?) {


    fun ok(): Boolean {
        return value != null
    }

    fun <Z> map(okHandler: (value: T) -> Z, errorHandler: (error: K) -> Z): Z {
        when {
            ok() -> return okHandler(value!!)
            else -> return errorHandler(error!!)
        }
    }

    fun <U, Z> map(handler: (current: Try<T, K>) -> Try<U, Z>): Try<U, Z> {
        return handler(this)
    }

    infix fun <Z> isOk(okHandler: (value: T) -> Z): Try<Z, K> {
        return if (ok()) ok(okHandler(value!!)) else Try.error(error!!)
    }

    infix fun <Z> isError(errorHandler: (value: K) -> Z): Try<T, Z> {
        return if (!this) Try.error(errorHandler(error!!)) else ok(value!!)
    }

    operator fun not(): Boolean {
        return error != null;
    }

    companion object {


        infix fun <Z, Y> ok(value: Z): Try<Z, Y> {
            return Try<Z, Y>(value, null)
        }

        infix fun <Z, Y> error(error: Y): Try<Z, Y> {
            return Try<Z, Y>(null, error)
        }

        fun <Z, Y, U> onException(handler: () -> Z, onError: (U) -> Y): Try<Z, Y> {
            try {
                return Try.ok<Z, Y>(handler())
            } catch (e: Exception) {
                return Try.error<Z, Y>(onError(e as U))
            }
        }

        fun <Z, Y> onException(handler: () -> Z): Try<Z, Y> {
            return onException<Z, Y, Y>(handler, { it })
        }

        fun <Y> ok(): Try<Unit, Y> {
            return Try<Unit, Y>(Unit, null)
        }

    }

}