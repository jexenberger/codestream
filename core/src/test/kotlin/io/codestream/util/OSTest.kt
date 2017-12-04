package io.codestream.util

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class OSTest {
    @Test
    fun testOs() {
        //flaky test, how do we check if the right is returned across different platforms
        val os = OS.os()
        assertNotNull(os)
    }

    @Test
    fun testProperties() {
        assertEquals(System.getProperty("os.version"), OS.os().version)
        assertEquals(System.getProperty("os.arch"), OS.os().architecture)
    }

    @Test
    fun testPwd() {
        assertEquals(System.getProperty("user.dir"), OS.os().pwd)
    }

    @Test
    fun testHomeDir() {
        assertEquals(System.getProperty("user.home"), OS.os().homeDir)
    }

    @Test
    fun testPathString() {
        val pathString = OS.os().pathString("hello", "world")
        assertEquals("hello:world", pathString)
    }

    @Test
    fun testFsString() {
        val pathString = OS.os().fsString("hello", "world")
        assertEquals("hello/world", pathString)
    }

    @Test
    fun testFsStringWithRoot() {
        val pathString = OS.os().fsString("hello", "world", absolute = true)
        assertEquals("/hello/world", pathString)
    }

    @Test
    fun testEnvironment() {
        assertNotNull(OS.os().env)
    }

    @Test
    fun testProps() {
        assertNotNull(OS.os().props)
    }

    @Test
    fun testRun() {
        if (OS.os().unixVariant) {
            var called = false
            val res = OS.os().shell("who") {
                called = true
            }
            assertTrue { called }
            assertEquals(0, res)
        }
    }

    @Test
    fun testRunStringOutput() {
        if (OS.os().unixVariant) {
            val (res,str) = OS.os().shell("who")
            assertTrue { str.length > 0 }
            assertEquals(0, res)
            println(str)
        }
    }

}

