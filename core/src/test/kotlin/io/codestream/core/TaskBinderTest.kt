package io.codestream.core

import io.codestream.module.coremodule.createTaskContext
import io.codestream.resourcemodel.DefaultYamlResourceDefinitions
import io.codestream.resourcemodel.DefaultYamlResourceRegistry
import io.codestream.runtime.CodestreamRuntime
import org.junit.Assert
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.fail

class TaskBinderTest {

    @Test
    fun testBind() {
        val (ctx, defn) = createTaskContext(MockModule(), "mockTask", mapOf(
                "testSet" to "hello",
                "list" to "hello, world",
                "willFail" to "yes",
                "testTwo" to "1"
        ), defaultCondition())
        val task = MockTask()
        val validation = task.bind(defn, ctx)
        assertNull(validation, validation?.let { it.toString() })
        Assert.assertArrayEquals(arrayOf("hello", "world"), task.list)
        assertEquals(1, task.testTwo)
        task.execute(defn.id, ctx)
    }


    @Test
    fun testCreateBadValidation() {
        val factory = taskFromClass(MockTask::class)
        assertNotNull(factory)
        val (ctx, defn) = createTaskContext(MockModule(), "mockTask", condition = defaultCondition())
        val task = MockTask()
        val validation = task.bind(defn, ctx)
        assertNotNull(validation, validation?.let { it.toString() })
    }

    @Test
    fun testBindResource() {
        val factory = taskFromClass(MockTask::class)
        CodestreamRuntime.init(emptyArray())

        assertNotNull(factory)
        val (ctx, defn) = createTaskContext(MockModule(), "mockTask",
                condition = defaultCondition(),
                bindingParams = mapOf("server" to "server1"))
        DefaultYamlResourceDefinitions("src/test/resources/resourcemodel/resourcedefinitions.yaml").load()
        val registry = DefaultYamlResourceRegistry("src/test/resources/resourcemodel/resources.yaml")
        registry.load()?.let { fail(it.errors.joinToString()) }
        CodestreamRuntime.resourceRegistry = registry
        ctx.resources = registry
        val task = MockTask()
        task.bind(defn, ctx)
        assertNotNull(task.server)

    }
}