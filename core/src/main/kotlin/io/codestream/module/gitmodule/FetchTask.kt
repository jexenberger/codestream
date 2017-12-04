package io.codestream.module.gitmodule

import io.codestream.core.*
import io.codestream.runtime.StreamContext
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import java.io.File
import javax.validation.constraints.NotBlank

class FetchTask : Task, TaskBinder {

    @TaskProperty
    @NotBlank
    var repoPath: String = ""


    @TaskProperty
    var user = ""

    @TaskProperty
    var password = ""

    @TaskProperty
    @NotBlank
    var remote = "origin"


    override fun execute(id: TaskId, ctx: StreamContext): TaskError? {
        val credentials = UsernamePasswordCredentialsProvider(user, password)
        val file = File(repoPath)
        val git = Git.open(File(file, ".git"))
        git.fetch()
                .setTransportConfigCallback {
                    it.credentialsProvider = credentials
                }
                .setCheckFetchedObjects(true)
                .setRemote(remote)
                .call()
        return done()
    }
}