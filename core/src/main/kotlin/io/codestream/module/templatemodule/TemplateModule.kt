package io.codestream.module.templatemodule

import io.codestream.core.*

class TemplateModule(override val name: String = "template",
                     override val factories: MutableMap<TaskType, Pair<Module.AllowedTypes, Factory<Executable>>> = mutableMapOf()) : Module {
    init {
        define {
            task("render" to taskFromClass(RenderTask::class))
            task("renderfile" to taskFromClass(RenderToFileTask::class))
            task("basepath" to taskFromClass(SetBasePath::class))
        }
    }

}