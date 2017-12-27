package io.codestream.util.git

import io.codestream.util.Credentials
import org.eclipse.jgit.api.CreateBranchCommand
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ListBranchCommand
import org.eclipse.jgit.transport.RefSpec
import java.io.File

class GitRepository(val repo: String,
                    private val remote: String? = "origin",
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

    val branch: String
        get() = git.repository.branch

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

    val commitID: String
        get() = git.repository.findRef("HEAD").objectId.name


    fun branchExists(brn: String): Boolean {
        val gitBranch = allBranches
                .find { brn.equals(it.shortName) }
        return gitBranch?.let { true } ?: false
    }

    fun commit(message: String, all: Boolean = true): GitRepository {
        git.commit()
                .setAll(all)
                .setMessage(message)
                .call()
        return this
    }

    fun fetch(remote: String? = this.remote, credentials: Credentials? = this.credentials): GitRepository {
        if (remote == null) {
            throw IllegalStateException("No remote defined")
        }
        val fetchCommand = git.fetch()
        credentials?.let { GitServer.setup(it, fetchCommand, true, true) }
        fetchCommand
                .setCheckFetchedObjects(true)
                .setRemote(remote)
                .call()
        return this
    }

    fun checkout(branch: String): GitRepository {
        git.checkout()
                .setName(branch)
                .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK)
                .call()
        return this
    }

    fun branch(name: String, force: Boolean = false): GitBranch {
        git.branchCreate()
                .setName(name)
                .setForce(force)
                .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK)
                .call()
        return GitBranch(name)
    }


    fun pull(branch: String = this.branch,
             remote: String? = this.remote,
             credentials: Credentials? = this.credentials): Boolean {
        if (remote == null) {
            throw IllegalStateException("No remote defined")
        }
        val pullCommand = git.pull()
        credentials?.let { GitServer.setup(it, pullCommand, true, true) }
        val pullResult = pullCommand
                .setRemote(remote)
                .setRemoteBranchName(branch)
                .call()
        return pullResult.isSuccessful
    }

    fun push(branch: String,
             remote: String? = this.remote,
             credentials: Credentials? = this.credentials,
             force: Boolean = false,
             pushTags: Boolean = false): GitRepository {
        if (remote == null) {
            throw IllegalStateException("No remote defined")
        }

        val pushCommand = git.push()
        credentials?.let { GitServer.setup(it, pushCommand, true, true) }
        if (pushTags) {
            pushCommand.setPushTags()
        }
        pushCommand
                .setRefSpecs(RefSpec(branch))
                .setAtomic(true)
                .setRemote(remote)
                .setForce(force)
                .setPushTags()
                .call()
        return this
    }


    fun add(vararg file: String): GitRepository {
        val addCommand = git.add()
        file.forEach {
            addCommand.addFilepattern(it)
        }
        addCommand.call()
        return this
    }
}

