package io.codestream.util.git.mockserver

import io.codestream.util.crypto.SSL
import io.codestream.util.system
import io.undertow.Handlers
import io.undertow.Undertow
import io.undertow.security.api.AuthenticationMechanism
import io.undertow.security.api.AuthenticationMode
import io.undertow.security.handlers.AuthenticationCallHandler
import io.undertow.security.handlers.AuthenticationConstraintHandler
import io.undertow.security.handlers.AuthenticationMechanismsHandler
import io.undertow.security.handlers.SecurityInitialHandler
import io.undertow.security.idm.Account
import io.undertow.security.idm.Credential
import io.undertow.security.idm.IdentityManager
import io.undertow.security.idm.PasswordCredential
import io.undertow.security.impl.BasicAuthenticationMechanism
import io.undertow.server.HttpHandler
import io.undertow.servlet.Servlets
import io.undertow.servlet.Servlets.deployment
import io.undertow.servlet.Servlets.servlet
import io.undertow.servlet.api.InstanceFactory
import io.undertow.servlet.api.InstanceHandle
import io.undertow.servlet.api.ServletContainer
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.http.server.GitServlet
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import java.io.File
import java.io.IOException
import java.security.Principal
import java.util.*
import java.util.Collections.singletonList


/*
 converted from JGIt sources:
 https://github.com/centic9/jgit-cookbook/blob/master/httpserver/src/main/java/org/dstadler/jgit/server/Main.java
 */
object MockGitServer {


    private val container: ServletContainer = Servlets.defaultContainer()
    private var server: Undertow? = null


    private val PATH = "/git"

    fun start() {

        val servletBuilder = deployment()
                .setClassLoader(MockGitServer::class.java.classLoader)
                .setContextPath(PATH)
                .setDeploymentName("test.war")
                .addServlets(
                        servlet("GitServlet", GitServlet::class.java, GitServletInstanceHandle())
                                .addMapping("/*")
                        )
        val manager =  this.container.addDeployment(servletBuilder)
        manager.deploy()

        val users = HashMap<String, CharArray>(2)
        users.put("pass", "word".toCharArray())
        users.put("userTwo", "passwordTwo".toCharArray())

        val identityManager = MapIdentityManager(users)


        val file = "${system.tempDir}/${UUID.randomUUID()}"
        File(file).deleteOnExit()
        val storeFile = SSL.defaultStoreAndKey("localhost", file)
        val ctx = SSL.ctx(storeFile.store, storeFile.store)
        val servletHandler = manager.start()
        val path = Handlers.path(Handlers.redirect(PATH))
                .addPrefixPath(PATH, servletHandler)
        server = Undertow.builder()
                .addHttpsListener(54321, "localhost", ctx)
                .setHandler(addSecurity(path, identityManager))
                .setWorkerThreads(5)
                .build()
        server?.start()
    }

    internal fun addSecurity(toWrap: HttpHandler, identityManager: MapIdentityManager) : HttpHandler {
        var handler = toWrap
        handler = AuthenticationCallHandler(handler)
        handler = AuthenticationConstraintHandler(handler)
        val mechanisms = singletonList<AuthenticationMechanism>(BasicAuthenticationMechanism("My Realm"))
        handler = AuthenticationMechanismsHandler(handler, mechanisms)
        handler = SecurityInitialHandler(AuthenticationMode.PRO_ACTIVE, identityManager, handler)
        return handler
    }

    fun stop() {
        server?.stop()
    }
}


class GitServletInstanceHandle : InstanceFactory<GitServlet>, InstanceHandle<GitServlet> {
    override fun createInstance(): InstanceHandle<GitServlet> {
        return this
    }

    val gs: GitServlet

    init {
        val repository = createRepository()
        repository.config.setString("http",null,"receivepack", "true")
        repository.config.setBoolean("http",null, "receivepack", true)

        populateRepository(repository)

        // Create the JGit Servlet which handles the Git protocol
        gs = GitServlet()
        gs.setRepositoryResolver { req, name ->
            repository.incrementOpen()
            repository
        }
    }

    override fun release() {
    }

    override fun getInstance(): GitServlet  {
        return this.gs
    }

    fun populateRepository(repository: Repository) {
        Git(repository).use { git ->
            val myfile = File(repository.directory.parent, "testfile")
            if (!myfile.createNewFile()) {
                throw IOException("Could not create file " + myfile)
            }

            git.add().addFilepattern("testfile").call()

            println("Added file " + myfile + " to repository at " + repository.directory)

            git.commit().setMessage("Test-Checkin").call()
        }
    }

    fun createRepository(): Repository {
        // prepare a new folder
        val localPath = File.createTempFile("TestGitRepository", "")
        if (!localPath.delete()) {
            throw IOException("Could not delete temporary file " + localPath)
        }

        if (!localPath.mkdirs()) {
            throw IOException("Could not create directory " + localPath)
        }

        // create the directory
        val repository = FileRepositoryBuilder.create(File(localPath, ".git"))
        repository.create()

        return repository
    }

}

internal class UserPrincipal(name:String):Principal {

    val _name:String = name

    override fun getName(): String {
        return _name
    }

}

internal class MapIdentityManager(private val users: Map<String, CharArray>) : IdentityManager {

    override fun verify(account: Account): Account {
        // An existing account so for testing assume still valid.
        return account
    }

    override fun verify(id: String, credential: Credential): Account? {
        val account = getAccount(id)
        return if (account != null && verifyCredential(account, credential)) {
            account
        } else null

    }

    override
    fun verify(credential: Credential): Account? {
        // TODO Auto-generated method stub
        return null
    }

    private fun verifyCredential(account: Account, credential: Credential): Boolean {
        if (credential is PasswordCredential) {
            val password = credential.password
            val expectedPassword = users[account.principal.name]

            return Arrays.equals(password, expectedPassword)
        }
        return false
    }

    private fun getAccount(id: String): Account? {
        return if (users.containsKey(id)) {
            object : Account {

                private val principal = UserPrincipal(id)

                override fun getPrincipal(): Principal {
                    return principal
                }

                override fun getRoles(): Set<String> {
                    return Collections.emptySet()
                }

            }
        } else null
    }

}