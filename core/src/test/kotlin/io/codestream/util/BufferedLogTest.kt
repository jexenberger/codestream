package io.codestream.util

import org.junit.Test
import java.io.File
import kotlin.test.assertTrue

class BufferedLogTest {


    fun log(check:String, handler:(BufferedLog)->Unit) {
        val log = BufferedLog("target","testGrp","testName","id")
        handler(log)
        val file = File("target/testGrp/testName/id")
        assertTrue { file.exists() }
        val readText = file.readText()
        println(readText)
        assertTrue { readText.contains(check) }
    }

    @Test
    fun testInfo() {
        log("\"messages\" : [ \"hello\" ]", {it.info("hello")})
    }

    @Test
    fun testError() {
        log("error", {it.error("error", Exception("exception"))})
        log("exception", {it.error("error", Exception("exception"))})
    }

    @Test
    fun testFromFile() {
        log("\"messages\" : [ \"hello\" ]", {it.info("hello")})
        val log = BufferedLog.fromFile("target", "testGrp", "testName", "id")
        log?.let { /*do nothing test passed*/ } ?: throw RuntimeException("should have existed and been read")
    }
}