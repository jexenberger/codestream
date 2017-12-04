package io.codestream.util.git

import io.codestream.util.Credentials
import org.eclipse.jgit.api.CreateBranchCommand
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ListBranchCommand
import java.io.File

class GitRepository(val repo: String,
                    private val remote: String,
                    private val credentials: Credentials? = null) {

    private val repoFile = File(repo, ".git")
    private val git: Git by lazy {
        Git.open(repoFile)
    }

    val localBranches: Set<GitBranch>
        get() {
            val list = git.branchList().call()
            return list.map { GitBranch(it.name) }.toSet()
        }

    val allBranches: Set<GitBranch>
        get() {
            val list = git.branchList().setListMode(ListBranchCommand.ListMode.ALL).call()
            return list.map { GitBranch(it.name) }.toSet()
        }

    val remoteBranches: Set<GitBranch>
        get() {
            val list = git.branchList().setListMode(ListBranchCommand.ListMode.REMOTE).call()
            return list.map { GitBranch(it.name) }.toSet()
        }

    val remotes: Set<String>
        get() = git.remoteList()
                .call()
                .map { it.name }
                .toSet()


    fun commit(message: String, all: Boolean = true) {
        git.commit()
                .setAll(all)
                .setMessage(message)
                .call()
    }

    fun fetch(remote: String = this.remote, credentials: Credentials? = this.credentials) {
        val fetchCommand = git.fetch()
        credentials?.let { GitServer.setup(it, fetchCommand, true, true) }
        fetchCommand
                .setCheckFetchedObjects(true)
                .setRemote(remote)
                .call()
    }

    fun checkout(branch: String) {
        git.checkout()
                .setName(branch)
                .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK)
                .call()
    }

    fun branch(name: String, force: Boolean = false): GitBranch {
        git.branchCreate()
                .setName(name)
                .setForce(force)
                .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK)
                .call()
        return GitBranch(name)
    }

    fun add(vararg file: String) {
        val addCommand = git.add()
        file.forEach {
            addCommand.addFilepattern(it)
        }
        addCommand.call()
    }

    companion object {
        fun fromExistingRepository(path: String): GitRepository? {
            val git = Git.open(File(path))
            val remoteList = git.remoteList().call().map { it.name }
            println(remoteList)
            return null
        }
    }


}

