package io.codestream.runtime

import io.codestream.core.*
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
        val (_, taskDefn) = createTaskContext<MockTask>(module, "mockTask", mapOf("testSet" to "hello world"), defaultCondition())
        val taskRunner = RunTask(taskDefn)

        val (ctx, defn) = createTaskContext<GroupTask>(module, "mockGroupTask", condition = defaultCondition())
        val runner = RunGroupTask<MockGroupTask>(defn, arrayOf(taskRunner))
        val result = runner.run(ctx)
        assertNull(result, result?.toString())
        assertEquals(9, ctx["theMockCounter"])
        assertTrue(ctx["isRan"] as Boolean)
        assertEquals(RunExecutableState.Complete, runner.state)
    }

    @Test
    fun testRunWithException() {
        val (_, taskDefn) = createTaskContext<MockTask>(module, "mockTask", mapOf("testSet" to "hello world", "throwException" to "true"), defaultCondition())
        val taskRunner = RunTask(taskDefn)

        val (ctx, defn) = createTaskContext<MockGroupTask>(module, "mockGroupTask", condition = defaultCondition())
        ctx["throwError"] = true
        val runner = RunGroupTask<MockGroupTask>(defn, arrayOf(taskRunner))
        val result = runner.run(ctx)
        assertNotNull(result)
        assertTrue(result?.toString()!!.contains("java.lang.RuntimeException"))
        assertNotNull(ctx["theMockException"])
        assertEquals(RunExecutableState.ThrewException, runner.state)
    }

    @Test
    fun testRunWithConditionFalse() {
        var called = false
        val (_, taskDefn) = createTaskContext<MockTask>(MockModule(), "mockTask", mapOf("testSet" to "hello world", "willFail" to "true"), condition = defaultCondition())
        val taskRunner = RunTask(taskDefn)
        val (ctx, defn) = createTaskContext<MockGroupTask>(module, "mockGroupTask", condition = { _, _ ->
            called = true
            false
        })
        val runner = RunGroupTask<MockGroupTask>(defn, arrayOf(taskRunner))
        val result = runner.run(ctx)
        assertTrue { called }
        assertNull(result)
        assertEquals(RunExecutableState.Skipped, runner.state)
    }

    @Test
    fun testRunWithFail() {
        val (_, taskDefn) = createTaskContext<MockTask>(module, "mockTask", mapOf("testSet" to "hello world", "willFail" to "true"), defaultCondition())
        val taskRunner = RunTask(taskDefn)

        val (ctx, defn) = createTaskContext<MockGroupTask>(module, "mockGroupTask", condition = defaultCondition())
        val runner = RunGroupTask<MockGroupTask>(defn, arrayOf(taskRunner))
        val result = runner.run(ctx)
        assertNotNull(result)
        assertEquals(RunExecutableState.Failed, runner.state)
    }
}
