package io.codestream.modules.atlassian.jira

import io.codestream.core.Executable
import io.codestream.core.Factory
import io.codestream.core.Module
import io.codestream.core.TaskType
import io.codestream.runtime.classimpl.taskFromClass

class JiraModule(override val name: String = "jira",
                 override val factories: MutableMap<TaskType, Pair<Module.AllowedTypes, Factory<out Executable>>> = mutableMapOf()) : Module {

    override val description: String
        get() = "Provides a set of tasks for interacting with Atlassian Jira"

    init {
        define {
            task(taskFromClass(AssignIssueTask::class))
            task(taskFromClass(GetIssueTask::class))
            task(taskFromClass(SetStatusTask::class))
            task(taskFromClass(GetIssueTransitionsTask::class))
        }
    }


}