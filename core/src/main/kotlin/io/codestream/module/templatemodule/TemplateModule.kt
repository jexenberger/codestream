package io.codestream.module.templatemodule

import io.codestream.core.*

class TemplateModule(override val name: String = "template",
                     override val factories: MutableMap<TaskType, Pair<Module.AllowedTypes, Factory<out Executable>>> = mutableMapOf()) : Module {
    override val description: String
        get() = "Templating module for working with Handlebars templates"

    init {
        define {
            task(taskFromClass(RenderTask::class))
            task(taskFromClass(RenderToFileTask::class))
            task(taskFromClass(SetBasePath::class))
        }
    }

}