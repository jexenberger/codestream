package io.codestream

import io.codestream.util.UserPassword
import io.codestream.util.git.GitRepository
import io.codestream.util.git.GitServer
import io.codestream.util.system
import java.io.File


object TestSettings {

    val gitPassword = "word"

    val gitUser = "pass"
    val repoName = "TestGitRepository"
    val gitUrl = "https://localhost:54321/git/$repoName"
    val server = GitServer(
            uri = gitUrl,
            credentials = UserPassword(gitUser, gitPassword),
            disableHostNameCheck = true,
            disableSSL = true)

    val dir = File("${system.tempDir}/git_repos")
    val gitWorkingDir = dir.absolutePath
    val gitBranch = "master"
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

    fun getRepo(remoteName: String = "origin") = GitRepository(repoPath.absolutePath, remoteName, UserPassword(gitUser, gitPassword))
    val sshUser: String = "test"
    val sshHost: String = "localhost"
    val sshPassword: String = "test!"
    val sshPrivateKey: String = "${system.homeDir}/.ssh/id_rsa"
    val sshKnownHosts: String = "${system.homeDir}/.ssh/known_hosts"
    val sshScpFile: String = ""

}