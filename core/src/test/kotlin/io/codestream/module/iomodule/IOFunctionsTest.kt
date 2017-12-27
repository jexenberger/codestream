package io.codestream.module.iomodule

import io.codestream.util.system
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File
import java.nio.file.Files
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class IOFunctionsTest {
    lateinit var one: String
    lateinit var two: String
    lateinit var dir1: File
    lateinit var dir2: File
    lateinit var files: List<String>
    lateinit var testDir: String
    lateinit var copyDir: String

    @Before
    fun setUp() {
        one = UUID.randomUUID().toString()
        two = UUID.randomUUID().toString()
        testDir = "${system.homeDir}/.cstest"
        files = listOf(
                "$testDir/$one/a.txt",
                "$testDir/$two/a.txt",
                "$testDir/$one/a.xxx",
                "$testDir/$two/a.xxx"
        )
        dir1 = File("$testDir", one)
        dir2 = File("$testDir", two)
        dir1.mkdirs()
        dir2.mkdirs()

        files.forEach {
            File(it).writeText("test")
        }
        copyDir = "$testDir/copytarget"
    }

    @Test
    fun testFileList() {
        val funcs = IOFunctions()

        val find = funcs.find(testDir, "**/a.txt")
        assertEquals(2, find.size)
        find.forEach {
            assertTrue { it.endsWith("a.txt") }
        }
    }

    @Test
    fun testCopyDir() {
        val funcs = IOFunctions()
        funcs.copyDir(testDir, copyDir, "**/a.txt")
        val find = funcs.find(copyDir, "**/*.*")
        assertEquals(2, find.size)
        find.forEach {
            assertTrue { it.endsWith("a.txt") }
        }
    }

    @After
    fun tearDown() {
        dir1.deleteRecursively()
        dir2.deleteRecursively()

        files.forEach {
            Files.deleteIfExists(File(it).toPath())
        }
        Files.deleteIfExists(dir1.toPath())
        Files.deleteIfExists(dir2.toPath())

        val copyPath = File(copyDir)
        copyPath.deleteRecursively()
        Files.deleteIfExists(copyPath.toPath())
    }
}