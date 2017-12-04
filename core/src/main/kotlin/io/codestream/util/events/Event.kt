package io.codestream.util.events

import io.codestream.core.TaskId
import io.codestream.runtime.StreamContext
import java.time.LocalDateTime

data class Event(
        val id: String,
        val taskId: TaskId? = null,
        val ctx: StreamContext,
        val timeStamp: LocalDateTime = LocalDateTime.now(),
        var parameters: Map<String, String> = mapOf())