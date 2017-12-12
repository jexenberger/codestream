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
                items = ctx.evalTo<Array<Any>>(itemStr)?.let { it } ?: emptyArray<Any>()
            }
}

fun KtStreamBuilder.foreach(itemsArr: Array<*>, builder: KtExecutableDefinitionBuilder<ForEachTask>.() -> Unit): KtExecutableDefinitionBuilder<ForEachTask> {
    return this.groupTask("core", "foreach", builder)
            .bind {
                items = itemsArr
            }
}

fun KtStreamBuilder.runStream(streamPath: String, inputs: Map<String, Any?> = mapOf()): KtExecutableDefinitionBuilder<StreamTask> {
    return this.task<StreamTask>("core", "stream")
            .bindWithCtx { ctx ->
                ctx.evalTo<String>(streamPath)?.let {
                    path = it
                }
                inputParameters = inputs.mapValues {
                    when (it.value) {
                        is String -> ctx.evalTo<Any?>(it)
                        else -> it
                    }
                }
            }
}

fun KtStreamBuilder.runStream(streamPath: String, inputs: () -> Map<String, Any?>): KtExecutableDefinitionBuilder<StreamTask> {
    return this.runStream(streamPath, inputs())
}

fun KtStreamBuilder.sleep(time: Long = 0): KtExecutableDefinitionBuilder<SleepTask> {
    return this.task<SleepTask>("core", "sleep").bind {
        duration = time
    }
}




