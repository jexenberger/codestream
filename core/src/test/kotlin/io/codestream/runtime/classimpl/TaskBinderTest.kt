package io.codestream.runtime.classimpl

import io.codestream.core.MockModule
import io.codestream.core.MockTask
import io.codestream.core.defaultCondition
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
        val map = mapOf(
                "testSet" to "hello",
                "list" to "hello, world",
                "willFail" to "yes",
                "testTwo" to "1"
        )
        val (ctx, defn) = createTaskContext<MockTask>(bindingParams = map)
        ctx.log.debug = true
        val task = MockTask()
        val validation = TaskBinder.bind(defn.id, task, ctx, map)
        assertNull(validation, validation.toString())
        Assert.assertArrayEquals(arrayOf("hello", "world"), task.list)
        assertEquals(1, task.testTwo)
        task.execute(defn.id, ctx)
    }


    @Test
    fun testCreateBadValidation() {
        val factory = taskFromClass(MockTask::class)
        assertNotNull(factory)
        val (ctx, defn) = createTaskContext<MockTask>(MockModule(), "mockTask", condition = defaultCondition())
        val task = MockTask()
        val validation = TaskBinder.bind(defn.id, task, ctx, mapOf())
        assertNotNull(validation, validation?.toString())
    }

    @Test
    fun testBindResource() {
        val factory = taskFromClass(MockTask::class)
        CodestreamRuntime.init(emptyArray())

        assertNotNull(factory)
        val params = mapOf("server" to "server1")
        val (ctx, defn) = createTaskContext<MockTask>(MockModule(), "mockTask",
                condition = defaultCondition(),
                bindingParams = params)
        DefaultYamlResourceDefinitions("src/test/resources/resourcemodel/resourcedefinitions.yaml").load()
        val registry = DefaultYamlResourceRegistry("src/test/resources/resourcemodel/resources.yaml")
        registry.load()?.let { fail(it.errors.joinToString()) }
        CodestreamRuntime.resourceRegistry = registry
        ctx.resources = registry
        val task = MockTask()
        TaskBinder.bind(defn.id, task, ctx, params)
        assertNotNull(task.server)

    }


}