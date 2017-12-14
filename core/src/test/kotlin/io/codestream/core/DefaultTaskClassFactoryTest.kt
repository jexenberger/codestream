package io.codestream.core

import io.codestream.runtime.StreamContext
import io.codestream.util.Either
import io.codestream.util.error
import io.codestream.util.ok
import org.junit.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class DefaultTaskClassFactoryTest {


    @Test
    fun testTaskFromClass() {
        val factory = taskFromClass(MockTask::class)
        assertNotNull(factory)
    }

    @Test
    fun testCreate() {
        val factory = taskFromClass(MockTask::class)
        assertNotNull(factory)
        val type = TaskType("mock", "mock")
        val id = TaskId("test", "test", type)
        val binding = MapBinding(id, type, params = mapOf(
                "testSet" to "hello",
                "list" to "hello, world",
                //"willFail" to "yes",
                "testTwo" to "1"
        ))
        val defn = ExecutableDefinition(
                type,
                id,
                binding.toBinding(),
                defaultCondition())

        val ctx = StreamContext()
        @Suppress("UNCHECKED_CAST")
        val task = factory.create(
                defn,
                ctx,
                MockModule()
        ) as Either<MockTask, TaskError>
        assertNotNull(task)
        assertTrue(task.ok(), task.error().toString())
    }
}