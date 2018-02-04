package io.codestream.module.iomodule

import io.codestream.util.returnIfTrue
import java.io.File
import java.io.IOException
import java.nio.charset.Charset
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes

class IOFunctions {


    fun readFileWithEncoding(src: String, encoding: String): String {
        return File(src).readText(Charset.forName(encoding))
    }

    fun readFile(src: String): String {
        return File(src).readText()
    }

    fun append(src: String, text: String) {
        File(src).appendText(text)
    }

    fun copy(src: String, target: String, overwrite: Boolean): Boolean {
        val srcP = Paths.get(src)
        val targetP = Paths.get(target)
        val srcFile = srcP.toFile()
        val targetFile = targetP.toFile()
        if (!overwrite && targetFile.exists()) {
            return false
        }
        if (!srcFile.exists() && !srcFile.isFile) {
            return false
        }
        if (!targetFile.exists()) {
            return (targetP.parent != null && targetP.parent.toFile().mkdirs()).returnIfTrue {
                val resolved = Paths.get(targetP.parent.toFile().absolutePath, srcFile.name)
                Files.copy(
                        srcP,
                        resolved,
                        StandardCopyOption.COPY_ATTRIBUTES,
                        StandardCopyOption.ATOMIC_MOVE
                )
                true
            } ?: false
        }
        Files.copy(
                srcP,
                targetP,
                StandardCopyOption.COPY_ATTRIBUTES,
                StandardCopyOption.ATOMIC_MOVE,
                StandardCopyOption.REPLACE_EXISTING
        )
        return true
    }

    fun find(startingDir: String, pattern: String?): List<String> {
        val fileSystem = FileSystems.getDefault()
        //done this way for interoperability with JS, ?.let wont work
        val patternToUse = if (pattern != null) pattern else "**/*.*"
        val globPattern = "glob:$patternToUse"
        val matcher = fileSystem.getPathMatcher(globPattern)
        val visitor = BufferFileVisitor(matcher)
        Files.walkFileTree(Paths.get(startingDir), visitor)
        return visitor.fileList

    }

    fun copyDir(src: String, target: String, pattern: String): Boolean {
        val srcDir = File(src)
        val targetDir = File(target)
        if (!srcDir.isDirectory) {
            return false
        }
        if (!targetDir.exists()) {
            targetDir.mkdirs()
        }
        //done this way for interoperability with JS, ?.let wont work
        val patternToUse = if (pattern != null) pattern else "**/*.*"
        val matcher = FileSystems.getDefault().getPathMatcher("glob:$patternToUse")
        Files.walkFileTree(srcDir.toPath(), CopyFileVisitor(srcDir, matcher, targetDir))
        return true
    }


}

class CopyFileVisitor(val basePath: File, val pathMatcher: PathMatcher, val target: File) : SimpleFileVisitor<Path>() {
    override fun visitFile(file: Path?, attrs: BasicFileAttributes?): FileVisitResult {
        if (pathMatcher.matches(file!!)) {
            //exclude files in a possible nested directory which is also the target
            val srcAbsolutePath = file.toFile().absolutePath
            if (srcAbsolutePath.startsWith(target.absolutePath)) {
                return FileVisitResult.CONTINUE
            }
            val targetName = srcAbsolutePath.substring(basePath.absolutePath.length + 1)
            val targetFile = File("${target.absolutePath}/$targetName")
            if (!targetFile.exists()) {
                File(targetFile.parent).mkdirs()
            }
            Files.copy(file, targetFile.toPath())
        }
        return FileVisitResult.CONTINUE
    }
}

class BufferFileVisitor(val pathMatcher: PathMatcher) : SimpleFileVisitor<Path>() {

    private val internalFileList = mutableListOf<String>()
    val fileList: List<String>
        get() = internalFileList

    override fun visitFileFailed(file: Path?, exc: IOException?): FileVisitResult {
        //can happen but continue, list findByAttributes would on standard unix
        return FileVisitResult.CONTINUE
    }

    override fun visitFile(file: Path?, attrs: BasicFileAttributes?): FileVisitResult {
        if (pathMatcher.matches(file!!)) {
            val path = file.toFile().absolutePath
            internalFileList += path
        }
        return FileVisitResult.CONTINUE
    }
}