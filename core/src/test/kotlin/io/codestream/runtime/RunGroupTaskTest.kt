package io.codestream.runtime

import io.codestream.core.MockModule
import io.codestream.core.Module
import io.codestream.core.RunExecutableState
import io.codestream.core.defaultCondition
import io.codestream.module.coremodule.createTaskContext
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class RunGroupTaskTest {

    private val module = MockModule()

    @Before
    fun setUp() {
        Module += module
    }


    @Test
    fun testRun() {
        val (_, taskDefn) = createTaskContext(module, "mockTask", mapOf("testSet" to "hello world"), defaultCondition())
        val taskRunner = RunTask(taskDefn)

        val (ctx, defn) = createTaskContext(module, "mockGroupTask", condition = defaultCondition())
        val runner = RunGroupTask(defn, arrayOf(taskRunner))
        val result = runner.run(ctx)
        assertNull(result, result?.toString())
        assertEquals(9, ctx["theMockCounter"])
        assertTrue(ctx["isRan"] as Boolean)
        assertEquals(RunExecutableState.Complete, runner.state)
    }

    @Test
    fun testRunWithException() {
        val (_, taskDefn) = createTaskContext(module, "mockTask", mapOf("testSet" to "hello world", "throwException" to "true"), defaultCondition())
        val taskRunner = RunTask(taskDefn)

        val (ctx, defn) = createTaskContext(module, "mockGroupTask", condition = defaultCondition())
        ctx["throwError"] = true
        val runner = RunGroupTask(defn, arrayOf(taskRunner))
        val result = runner.run(ctx)
        assertNotNull(result)
        assertTrue(result?.toString()!!.contains("java.lang.RuntimeException"))
        assertNotNull(ctx["theMockException"])
        assertEquals(RunExecutableState.ThrewException, runner.state)
    }

    @Test
    fun testRunWithConditionFalse() {
        var called = false
        val (_, taskDefn) = createTaskContext(MockModule(), "mockTask",mapOf("testSet" to "hello world", "willFail" to "true"), condition = defaultCondition())
        val taskRunner = RunTask(taskDefn)
        val (ctx, defn) = createTaskContext(module, "mockGroupTask", condition = {
            d,c ->
            called = true
            false
        })
        val runner = RunGroupTask(defn, arrayOf(taskRunner))
        val result = runner.run(ctx)
        assertTrue { called }
        assertNull(result)
        assertEquals(RunExecutableState.Skipped, runner.state)
    }

    @Test
    fun testRunWithFail() {
        val (_, taskDefn) = createTaskContext(module, "mockTask", mapOf("testSet" to "hello world", "willFail" to "true"), defaultCondition())
        val taskRunner = RunTask(taskDefn)

        val (ctx, defn) = createTaskContext(module, "mockGroupTask", condition = defaultCondition())
        val runner = RunGroupTask(defn, arrayOf(taskRunner))
        val result = runner.run(ctx)
        assertNotNull(result)
        assertEquals(RunExecutableState.Failed, runner.state)
    }
}
