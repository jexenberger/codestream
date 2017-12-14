package io.codestream.core

import org.junit.Test
import kotlin.test.assertEquals

class TaskIdTest {

    @Test
    fun testFromString() {
        val id = TaskId.fromString("hello::world::test::type::task")
        assertEquals("hello", id.group)
        assertEquals("world", id.stream)
        assertEquals(TaskType("test", "type"), id.type)
        assertEquals("task", id.id)
    }

    @Test
    fun testFqn() {
        val id = TaskId("grp", "strm", TaskType("test", "task"), "1")
        assertEquals("grp::strm::test::task::1", id.fqid)
    }
}