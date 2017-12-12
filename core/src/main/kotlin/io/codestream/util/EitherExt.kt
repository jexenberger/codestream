package io.codestream.util

fun <L, R> Either<L, R>.ok(): Boolean = this.left != null
fun <L, R> Either<L, R>.error(): Boolean = this.right != null
