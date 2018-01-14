package io.codestream.util

import io.codestream.util.log.BufferedStreamLog
import org.junit.Test
import java.io.File
import kotlin.test.assertTrue

class BufferedStreamLogTest {


    fun log(check: String, handler: (BufferedStreamLog) -> Unit) {
        val log = BufferedStreamLog("target", "testGrp", "testName", "id")
        handler(log)
        val file = File("target/testGrp/testName/id")
        assertTrue { file.exists() }
        val readText = file.readText()
        println(readText)
        assertTrue { readText.contains(check) }
    }

    @Test
    fun testInfo() {
        log("\"hello\"", { it.info("hello") })
    }

    @Test
    fun testError() {
        log("error", { it.error("error", Exception("exception")) })
        log("exception", { it.error("error", Exception("exception")) })
    }

    @Test
    fun testFromFile() {
        log("\"hello\"", { it.info("hello") })
        val log = BufferedStreamLog.fromFile("target", "testGrp", "testName", "id")
        log?.let { /*do nothing test passed*/ } ?: throw RuntimeException("should have existed and been read")
    }
}