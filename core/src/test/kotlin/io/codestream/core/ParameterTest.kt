package io.codestream.core

import io.codestream.util.error
import io.codestream.util.ok
import org.junit.Assert
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ParameterTest {

    @Test
    fun testTryConvert() {
        val parm = Parameter(
                name = "test",
                desc = "test parm",
                stringType = "int",
                required = true,
                defaultString = "1",
                allowedValuesList = "1, 2, 3"
        )
        val tryConvert = parm.tryConvert("2")
        assertTrue(tryConvert.ok(), tryConvert.error()?.toString())
        assertEquals(2, tryConvert.left!!)
    }

    @Test
    fun testTryConvertRequired() {
        val parm = Parameter(
                name = "test",
                desc = "test parm",
                stringType = "int",
                required = true,
                allowedValuesList = "1, 2, 3"
        )
        val tryConvert = parm.tryConvert(null)
        assertFalse(tryConvert.ok())
        assertEquals("required", tryConvert.right?.get(0))
    }

    @Test
    fun testTryConvertIsInList() {
        val parm = Parameter(
                name = "test",
                desc = "test parm",
                stringType = "int",
                required = true,
                allowedValuesList = "1, 2, 3"
        )
        val tryConvert = parm.tryConvert("9")
        assertFalse(tryConvert.ok(), tryConvert.error()?.toString())
        assertEquals("9 is not in 1, 2, 3", tryConvert.right?.get(0))
    }

    @Test
    fun testIsIn() {
        val parm = Parameter(
                name = "test",
                desc = "test parm",
                stringType = "string",
                defaultString = "hello",
                allowedValuesList = "hello, world"
        )

        val shouldExist = parm.isIn("hello")
        assertTrue(shouldExist)
        val shouldntExist = parm.isIn("qwerty")
        assertFalse(shouldntExist)
    }

    @Test
    fun testInt() {
        val parm = Parameter(
                name = "test",
                desc = "test parm",
                stringType = "int",
                defaultString = "0",
                allowedValuesList = "1, 2"
        )

        val shouldExist = parm.isIn(1)
        assertTrue(shouldExist)
        val shouldntExist = parm.isIn(3)
        assertFalse(shouldntExist)
        assertEquals(99, parm.fromString("99"))
    }

    @Test
    fun testLong() {
        val parm = Parameter(
                name = "test",
                desc = "test parm",
                stringType = "long",
                defaultString = "0",
                allowedValuesList = "1, 2"
        )

        val shouldExist = parm.isIn(1L)
        assertTrue(shouldExist)
        val shouldntExist = parm.isIn(3L)
        assertFalse(shouldntExist)
        assertEquals(99L, parm.fromString("99"))
    }

    @Test
    fun testFloat() {
        val parm = Parameter(
                name = "test",
                desc = "test parm",
                stringType = "float",
                defaultString = "1.0",
                allowedValuesList = "1.0, 2.0"
        )

        val shouldExist = parm.isIn(1.0f)
        assertTrue(shouldExist)
        val shouldntExist = parm.isIn(3.0f)
        assertFalse(shouldntExist)
        assertEquals(99.0f, parm.fromString("99.0"))
    }

    @Test
    fun testBoolean() {
        val parm = Parameter(
                name = "test",
                desc = "test parm",
                stringType = "boolean",
                defaultString = "yes"
        )

        val shouldExist = parm.isIn(true)
        assertTrue(shouldExist)
        val shouldntExist = parm.isIn(false)
        assertTrue(shouldntExist)
        assertTrue(parm.fromString("yes") as Boolean)
        assertFalse(parm.fromString("no") as Boolean)
    }

    @Test
    fun testStringArray() {
        val parm = Parameter(
                name = "test",
                desc = "test parm",
                stringType = "[string]",
                defaultString = "1,2,3"
        )

        val shouldExist = parm.isIn(arrayOf("1", "2", "3"))
        assertTrue(shouldExist)
        val shouldntExist = parm.isIn(arrayOf("5", "6", "7"))
        assertTrue(shouldntExist)
        Assert.assertArrayEquals(arrayOf("1", "2", "3"), parm.fromString("1, 2, 3") as Array<String>)
    }

    @Test
    fun testIntArray() {
        val parm = Parameter(
                name = "test",
                desc = "test parm",
                stringType = "[int]"
        )

        val shouldExist = parm.isIn(1)
        assertTrue(shouldExist)
        val shouldntExist = parm.isIn(99)
        assertTrue(shouldntExist)
        Assert.assertArrayEquals(arrayOf(99, 88, 66), parm.fromString("99,88,66") as Array<Int>)
    }

    @Test
    fun testLongArray() {
        val parm = Parameter(
                name = "test",
                desc = "test parm",
                stringType = "[long]"
        )

        val shouldExist = parm.isIn(1L)
        assertTrue(shouldExist)
        val shouldntExist = parm.isIn(99L)
        assertTrue(shouldntExist)
        Assert.assertArrayEquals(arrayOf(99L, 88L, 66L), parm.fromString("99,88,66") as Array<Long>)
    }

    @Test
    fun testFloatArray() {
        val parm = Parameter(
                name = "test",
                desc = "test parm",
                stringType = "[float]"
        )

        val shouldExist = parm.isIn(1.0f)
        assertTrue(shouldExist)
        val shouldntExist = parm.isIn(99.0f)
        assertTrue(shouldntExist)
        Assert.assertArrayEquals(arrayOf(99.0f, 88.0f, 66.0f), parm.fromString("99.0,88.0,66.0") as Array<Float>)
    }

    @Test
    fun testFromString() {
        val parm = Parameter(
                name = "test",
                desc = "test parm",
                stringType = "boolean",
                defaultString = "yes"
        )

        val result = parm.fromString("1") as Boolean
        assertTrue(result)
        val notTrue = parm.fromString("0") as Boolean
        assertFalse(notTrue)
    }
}