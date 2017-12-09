package io.codestream.core

import io.codestream.runtime.StreamContext

interface ExecutableBinder<T : Executable> {

    fun bind(executable: T, ctx: StreamContext)

}