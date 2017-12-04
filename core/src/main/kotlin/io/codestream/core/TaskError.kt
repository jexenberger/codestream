package io.codestream.core


fun taskParameterValidation(id: TaskId, msg: String) = TaskError(id, "TaskValidationError", msg)
fun taskCreationFailed(id: TaskId, msg: String) = TaskError(id, "TaskCreationFailed", msg)
fun invalidModule(id: TaskId, msg: String) = TaskError(id, "InvalidModule", msg)
fun invalidParameter(id: TaskId, msg: String) = TaskError(id, "InvalidTaskParameter", msg)
fun taskFailed(id: TaskId, msg: String, errors: Array<TaskError> = emptyArray()) = TaskError(id, "TaskFailed", msg, errors)
fun taskFailedWithException(id: TaskId, msg: String, errors: Array<TaskError> = emptyArray()) = TaskError(id, "TaskFailedWithException", msg, errors)
fun isGroupTask(id: TaskId, type: TaskType) = TaskError(id, "IsGroupTask", "${type.fqn} is a group task")



fun ifTask(id:TaskId,type:TaskType, task:Executable, f: (Task)-> TaskError?) : TaskError? {
    if (task !is Task) {
        return isGroupTask(id,type)
    } else {
        return f(task)
    }
}

data class TaskError(val id: TaskId,
                     val code: String,
                     val msg: String,
                     var errors: Array<TaskError> = arrayOf()) : Exception("${id.fqid}::$code -> $msg") {


    override fun toString(): String {
        return "ERROR[$id]:$code -> $msg ${if (errors.isNotEmpty()) "[" + errors.joinToString() + "]" else ""}"
    }

    operator fun plusAssign(error: TaskError) {
        errors += error
    }

    fun toMap(): Map<String, String> {
        if (errors.isEmpty()) {
            return mapOf(code to msg)
        } else {
            return errors.map { it.code to it.msg }.toMap()
        }
    }

}