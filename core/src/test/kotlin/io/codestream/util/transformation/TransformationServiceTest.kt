package io.codestream.util.transformation

import org.junit.Assert
import org.junit.Assert.assertArrayEquals
import org.junit.Test
import kotlin.test.assertEquals


class TransformerServiceTest {


    val test: Array<String> = arrayOf()


    @Test
    @Throws(Exception::class)
    fun testTransform() {


        assertEquals(true, TransformerService.convert(true, Boolean::class))
        assertEquals("1", TransformerService.convert(1))
        assertEquals(1.0, TransformerService.convert(1))
        assertEquals(1.0, TransformerService.convert(1))
        assertEquals(1.toShort(), TransformerService.convert(1))
        assertEquals(1.toByte(), TransformerService.convert(1))
        assertEquals("1", TransformerService.convert(1))
        assertEquals("1.0", TransformerService.convert(1.0))
        assertEquals("1.0", TransformerService.convert(1.0f))
        assertEquals("1", TransformerService.convert(1))
        assertEquals(true, TransformerService.convert(1))
        assertEquals(false, TransformerService.convert(0))
        assertEquals(true, TransformerService.convert('1'))
        assertEquals(false, TransformerService.convert('0'))
        assertEquals(true, TransformerService.convert('y'))
        assertEquals(false, TransformerService.convert('n'))
        assertEquals(true, TransformerService.convert("yes"))
        assertEquals(false, TransformerService.convert("no"))
        Assert.assertArrayEquals(arrayOf("1", "2"), TransformerService.convert("1,2", Array<String>::class))
        val testS: Array<String> = TransformerService.convert("1, 2, 3")
        Assert.assertArrayEquals(arrayOf("1", "2", "3"), testS)
        val testI: Array<Int> = TransformerService.convert("1, 2, 3")
        Assert.assertArrayEquals(arrayOf(1, 2, 3), testI)
        val testL: Array<Long> = TransformerService.convert("1, 2, 3")
        Assert.assertArrayEquals(arrayOf(1L, 2L, 3L), testL)
        val testF: Array<Float> = TransformerService.convert("1, 2, 3")
        Assert.assertArrayEquals(arrayOf(1.0f, 2.0f, 3.0f), testF)
        val testD: Array<Double> = TransformerService.convert("1, 2, 3")
        Assert.assertArrayEquals(arrayOf(1.0, 2.0, 3.0), testD)
        val testB: Array<Boolean> = TransformerService.convert("yes, no, true, false, 1, 0")
        Assert.assertArrayEquals(arrayOf(true, false, true, false, true, false), testB)

        var testCS: Collection<String> = TransformerService.convert("1, 2, 3")
        assertArrayEquals(arrayOf("1", "2", "3"), testCS.toTypedArray())
    }

    @Test
    fun testMap() {
        val convert = mapOf<String, Any?>("1" to "1", "2" to 2, "3" to false)
        val result: Map<String, Any?> = TransformerService.convert(convert)
        assertEquals(convert, result)
    }
}