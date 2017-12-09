package io.codestream.module.coremodule

import io.codestream.kts.KtExecutableDefinitionBuilder
import io.codestream.kts.KtStreamBuilder

fun KtStreamBuilder.echo(value: String): KtExecutableDefinitionBuilder {
    return this.task("core", "echo").parm("value", value)
}

fun KtStreamBuilder.foreach(value: String): KtExecutableDefinitionBuilder {
    return this.task("core", "echo").parm("value", value)
}

