package io.codestream.core

data class TaskType(val namespace: String, val name: String) {

    val fqn: String
        get() = "$namespace::$name"

    val module: Module?
        get() = Module[namespace]

    override fun toString(): String {
        return fqn
    }


    companion object {
        fun fromString(task: String): TaskType {
            val candidate = if (task.contains("::")) task else "core::$task"
            val parts = candidate.split("::")
            if (parts.size != 2) throw IllegalArgumentException("$task is not in the format [namespace]::[type]")
            return TaskType(parts[0], parts[1])
        }
    }


}