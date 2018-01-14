package io.codestream.runtime

import io.codestream.core.Module
import io.codestream.module.coremodule.CoreModule
import io.codestream.module.gitmodule.GitModule
import io.codestream.module.httpmodule.HttpModule
import io.codestream.module.iomodule.IOModule
import io.codestream.module.sshmodule.SSHModule
import io.codestream.module.templatemodule.TemplateModule
import io.codestream.module.utilmodule.UtilModule
import io.codestream.util.Either
import io.codestream.util.fail
import io.codestream.util.log.ConsoleLog
import io.codestream.util.log.Log
import io.codestream.util.ok
import org.kevoree.kcl.api.FlexyClassLoader
import org.kevoree.kcl.api.FlexyClassLoaderFactory
import java.io.File
import java.nio.file.Paths


class ModuleLoader(private val paths: Array<String>, val log: Log = ConsoleLog()) {

    val moduleScopes: MutableMap<Module, FlexyClassLoader> = mutableMapOf()

    init {

        val defaultModules: Array<Module> = arrayOf(
                CoreModule(),
                UtilModule(),
                TemplateModule(),
                IOModule(),
                GitModule(),
                SSHModule(),
                HttpModule()
        )
        log.debug("Loading default modules")
        defaultModules.forEach {
            log.debug("Loading default module -> ${it.name}")
            Module += it
        }
    }

    fun load(): List<ModuleError> {
        val moduleThatCouldNotBeLoaded = mutableListOf<ModuleError>()
        paths.forEach {
            val dir = File(it)
            if (dir.isDirectory) {
                val entries = dir.list()
                entries.forEach { entry ->
                    val targetFile = Paths.get(dir.absolutePath, entry).toFile()
                    if (targetFile.isDirectory) {
                        loadModulesFromPath(targetFile.absolutePath).map({ modules ->
                            modules.forEach {
                                log.debug("Loading module -> ${it.name}")
                                Module += it
                            }
                        }, {
                            log.debug("Unable to load module -> ${it}")
                            moduleThatCouldNotBeLoaded.add(it)
                        })
                    }
                }
            }
        }
        return moduleThatCouldNotBeLoaded
    }

    fun loadModulesFromPath(path: String): Either<Array<Module>, ModuleError> {
        log.debug("Loading modules from path -> $path")
        val modulePath = File(path)
        val moduleScope = FlexyClassLoaderFactory.INSTANCE.create()
        moduleScope.load(modulePath)
        val pathList = modulePath.list()
        val file = pathList.find { it == "modules.conf" }
        return file?.let {
            val modules = mutableListOf<Module>()
            val failedModules = mutableMapOf<String, String>()
            Paths.get(path, it).toFile().forEachLine { moduleClass ->
                try {
                    val clazz = moduleScope.loadClass(moduleClass)
                    log.debug("Loaded module class -> ${clazz.name}")
                    //val constructor = clazz.getConstructor(String::class.java, MutableMap::class.java)
                    //constructor.newInstance()
                    //println(constructor.parameters.forEach {
                    //    println(it.name)
                    //})
                    val module = clazz.newInstance() as Module
                    modules.add(module)
                    moduleScopes[module] = moduleScope
                } catch (e: ClassNotFoundException) {
                    failedModules[modulePath.name] = e.message!!
                }
            }
            if (failedModules.isEmpty())
                ok<Array<Module>, ModuleError>(modules.toTypedArray())
            else
                fail<Array<Module>, ModuleError>(ModuleError("error.loading", "Unable to load modules", failedModules))
        } ?: fail(ModuleError("module.file.doesnt.exist", "module '$path' does not exist"))
    }

    companion object {
        val defaultPath = "${CodestreamRuntime.homeFolder}/modules"
    }

}