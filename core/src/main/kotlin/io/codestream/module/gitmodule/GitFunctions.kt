package io.codestream.module.gitmodule

import io.codestream.util.UserPassword
import io.codestream.util.git.GitRepository
import io.codestream.util.system

class GitFunctions {

    fun repo(workingPath: String, remote: String, user: String, pwd: String): GitRepository {
        return GitRepository(workingPath, remote, UserPassword(user, pwd))
    }

    fun repo(workingPath: String): GitRepository {
        return GitRepository(workingPath, "origin", null)
    }

    fun repo(): GitRepository {
        return GitRepository(system.pwd, "origin", null)
    }


}