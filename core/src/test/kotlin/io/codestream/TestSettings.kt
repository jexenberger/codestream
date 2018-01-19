package io.codestream

import io.codestream.util.UserPassword
import io.codestream.util.git.GitRepository
import io.codestream.util.git.GitServer
import io.codestream.util.system
import java.io.File

class TestSettings {

    val repoName = "TestGitRepository"
    val server = GitServer(
            uri = "https://localhost:8080/git/$repoName",
            credentials = UserPassword("pass", "word"),
            disableHostNameCheck = true,
            disableSSL = true)

    val dir = File("${system.tempDir}/git_repos")
    val repoPath = File(dir, repoName)


    fun clone() {
        deleteExistingGitDir()
        server.clone(repoPath.absolutePath)
    }

    fun deleteExistingGitDir() {
        val workingDir = dir
        if (workingDir.exists()) {
            workingDir.deleteRecursively()
        }
    }

    fun getRepo(remoteName: String = "origin") = GitRepository(repoPath.absolutePath, remoteName, UserPassword("pass", "word"))

}