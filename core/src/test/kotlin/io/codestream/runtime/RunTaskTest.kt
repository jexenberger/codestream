package io.codestream.runtime

import io.codestream.core.*
import io.codestream.module.coremodule.createTaskContext
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class RunTaskTest {

    @Before
    fun setUp() {
        Module += MockModule()
    }

    @Test
    fun testRun() {
        val (ctx, defn) = createTaskContext<MockTask>(MockModule(), "mockTask",
                mapOf("testSet" to "A Value"), defaultCondition())
        val runner = RunTask(defn)
        val result = runner.run(ctx)
        assertNull(result)
        assertEquals(RunExecutableState.Complete, runner.state)
    }

    @Test
    fun testRunFailBinding() {
        val (ctx, defn) = createTaskContext<MockTask>(MockModule(), "mockTask", condition = defaultCondition())
        val runner = RunTask(defn)
        val result = runner.run(ctx)
        assertNotNull(result)
        assertEquals("TaskValidationError", result?.code)
        assertEquals(RunExecutableState.Failed, runner.state)
    }

    @Test
    fun testRunWithConditionFalse() {
        var called = false
        val (ctx, defn) = createTaskContext<MockTask>(MockModule(), "mockTask", condition = { _, _ ->
            called = true
            false
        })
        val runner = RunTask(defn)
        val result = runner.run(ctx)
        assertTrue { called }
        assertNull(result)
        assertEquals(RunExecutableState.Skipped, runner.state)
    }

    @Test
    fun testRunFailRunningOfTask() {
        val (ctx, defn) = createTaskContext<MockTask>(MockModule(), "mockTask",
                mapOf("testSet" to "A Value",
                        "willFail" to "true"), defaultCondition())
        val runner = RunTask(defn)
        val result = runner.run(ctx)
        assertNotNull(result)
        assertEquals("TestFail", result?.code)
        assertEquals(RunExecutableState.Failed, runner.state)
    }

    @Test
    fun testRunFailRunningOfTaskWithThrownException() {
        val (ctx, defn) = createTaskContext<MockTask>(MockModule(), "mockTask",
                mapOf("testSet" to "A Value",
                        "throwException" to "true"), defaultCondition())
        val runner = RunTask(defn)
        val result = runner.run(ctx)
        assertNotNull(result)
        assertEquals("TaskFailedWithException", result?.code)
        assertEquals(RunExecutableState.ThrewException, runner.state)
    }
}

