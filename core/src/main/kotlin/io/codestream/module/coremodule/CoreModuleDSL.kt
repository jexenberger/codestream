package io.codestream.module.coremodule

import io.codestream.kts.KtExecutableDefinitionBuilder
import io.codestream.kts.KtStreamBuilder

fun KtStreamBuilder.echo(echoVal: String): KtExecutableDefinitionBuilder<EchoTask> {
    return this.task<EchoTask>("core", "echo")
            .bind {
                value = echoVal
            }
}

fun KtStreamBuilder.foreach(itemStr: String, builder: KtExecutableDefinitionBuilder<ForEachTask>.() -> Unit): KtExecutableDefinitionBuilder<ForEachTask> {
    return this.groupTask("core", "foreach", builder)
            .bindWithCtx { ctx ->
                items = ctx.evalTo<Collection<Any>>(itemStr)?.let { it } ?: emptyList<Any>()
            }
}

fun KtStreamBuilder.foreach(itemsArr: Collection<*>, builder: KtExecutableDefinitionBuilder<ForEachTask>.() -> Unit): KtExecutableDefinitionBuilder<ForEachTask> {
    return this.groupTask("core", "foreach", builder)
            .bind {
                items = itemsArr
            }
}

fun KtStreamBuilder.sleep(time: Long = 0): KtExecutableDefinitionBuilder<SleepTask> {
    return this.task<SleepTask>("core", "sleep").bind {
        duration = time
    }
}




