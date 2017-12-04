package io.codestream.util

import org.junit.Test
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class StringsTest {
    @Test
    fun testIsEmpty() {
        assertTrue(Strings.isEmpty(""))
        assertTrue(Strings.isEmpty(" "))
        assertTrue(Strings.isEmpty(null))
        assertFalse(Strings.isEmpty("hello"))
    }

    @Test
    fun testExec() {
        if (OS.os().unixVariant) {
            val result = "ls -l".exec()
            assertEquals(0, result.first)
            assertNotNull(result.second)
            println(result.second)
        } else {
            val result = "C:\\Windows\\System32\\where.exe".exec()
            assertEquals(2, result.first)
            assertNotNull(result.second)
            println(result.second)
        }

    }


    @Test
    fun testExecTimeout() {
        if (OS.os().unixVariant) {
            val result = "sleep 2".exec(File(System.getProperty("user.dir")), 1, TimeUnit.SECONDS)
            assertEquals(-1, result.first)
            assertNotNull(result.second)
            println(result.second)
        }
    }
}