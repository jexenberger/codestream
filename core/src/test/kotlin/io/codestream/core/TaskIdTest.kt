package io.codestream.core

import org.junit.Test
import kotlin.test.assertEquals

class TaskIdTest {

    @Test
    fun testFromString() {
        val id = TaskId.fromString("hello::world::task")
        assertEquals("hello", id.group)
        assertEquals("world", id.stream)
        assertEquals("task", id.id)
    }

    @Test
    fun testFqn() {
        val id = TaskId("grp", "strm", "1")
        assertEquals("grp::strm::1", id.fqid)
    }
}