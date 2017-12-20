package io.codestream.core

import java.util.*

data class TaskId(val group: String,
                  val stream: String,
                  var type: TaskType,
                  val id: String = UUID.randomUUID().toString()) {

    val fqid: String
        get() = "$group::$stream::$type::$id"


    companion object {
        fun fromString(str: String): TaskId {
            val parts = str.trim().split("::")
            if (parts.size != 5) throw IllegalStateException("'$str' is not in valid format '[group]::[stream]::[id]'")
            return TaskId(parts[0], parts[1], TaskType(parts[2], parts[3]), parts[4])
        }
    }

    override fun toString(): String {
        return fqid
    }

}