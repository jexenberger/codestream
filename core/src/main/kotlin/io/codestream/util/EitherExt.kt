package io.codestream.util

inline  fun <L,R> Either<L,R>.ok() : Boolean =   this.left != null
inline  fun <L,R> Either<L,R>.error() : Boolean =   this.right != null
