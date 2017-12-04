package io.codestream.core

import java.util.*

data class TaskId(val group: String,
                  val stream: String,
                  val id: String = UUID.randomUUID().toString()) {

    val fqid: String
        get() = "$group::$stream::$id"


    companion object {
        fun fromString(str: String): TaskId {
            val parts = str.trim().split("::")
            if (parts.size != 3) throw IllegalStateException("'$str' is not in valid format '[group]::[stream]::[id]'")
            return TaskId(parts[0], parts[1], parts[2])
        }
    }

    override fun toString(): String {
        return fqid
    }

}