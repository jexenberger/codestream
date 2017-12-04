package io.codestream.modules.atlassian.jira

import io.codestream.core.*

class JiraModule(
        override val name: String = "jira",
        override val factories: MutableMap<TaskType, Pair<Module.AllowedTypes, Factory<out Executable>>>) : Module {

    init {
        define {
            task("assign" to taskFromClass(AssignIssueTask::class))
            task("get-issue" to taskFromClass(GetIssueTask::class))
            task("set-status" to taskFromClass(SetStatusTask::class))
            task("get-issue-transitions" to taskFromClass(GetIssueTransitionsTask::class))
        }
    }


}