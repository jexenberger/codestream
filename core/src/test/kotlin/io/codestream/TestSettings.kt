package io.codestream

import com.fasterxml.jackson.module.kotlin.readValue
import io.codestream.util.UserPassword
import io.codestream.util.YamlFactory
import io.codestream.util.git.GitRepository
import io.codestream.util.git.GitServer
import io.codestream.util.system
import java.io.File

data class TestSettings(
        val sshHost: String,
        val sshPassword: String,
        val sshUser: String,
        val sshPrivateKey: String,
        val sshKnownHosts: String,
        val sshScpFile: String,
        val gitUrl: String,
        val gitBranch: String,
        val gitUser: String,
        val gitPassword: String,
        val gitWorkingDir: String,
        val gitKeyFile: String,
        val gitSSHURL: String,
        val gitAlternateBranch: String
) {
    companion object {
        private val defaultTestPath = "${system.homeDir}/.cstest/test.yaml"

        fun get(): TestSettings {
            val file = File(defaultTestPath)
            if (!file.exists()) {
                throw RuntimeException("You need to create a test settings file @ $defaultTestPath")
            }
            return YamlFactory.mapper().readValue(file)
        }
    }


    fun clone() {
        deleteExistingGitDir()
        val server = GitServer(gitUrl, UserPassword(gitUser, gitPassword), true)
        server.clone(gitWorkingDir)
    }

    fun deleteExistingGitDir() {
        val workingDir = File(gitWorkingDir)
        if (workingDir.exists()) {
            workingDir.deleteRecursively()
        }
    }

    fun getRepo(remoteName: String = "origin") = GitRepository(gitWorkingDir, remoteName, UserPassword(gitUser, gitPassword))

}