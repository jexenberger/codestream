package io.codestream.core

import io.codestream.module.coremodule.CoreModule
import io.codestream.module.gitmodule.GitModule
import io.codestream.module.iomodule.IOModule
import io.codestream.module.sshmodule.SSHModule
import io.codestream.module.templatemodule.TemplateModule
import io.codestream.util.*
import org.kevoree.kcl.api.FlexyClassLoader
import org.kevoree.kcl.api.FlexyClassLoaderFactory
import java.io.File
import java.nio.file.Paths


class ModuleLoader(private val paths: Array<String>) {

    val moduleScopes: MutableMap<Module, FlexyClassLoader> = mutableMapOf()

    val log = ConsoleLog()

    init {

        val defaultModules: Array<Module> = arrayOf(CoreModule(), TemplateModule(), IOModule(), GitModule(), SSHModule())
        log.debug("Loading core modules")
        defaultModules.forEach {
            log.debug("Loading module -> ${it.name}")
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
                            moduleThatCouldNotBeLoaded.add(it)
                        })
                    }
                }
            }
        }
        return moduleThatCouldNotBeLoaded
    }

    fun loadModulesFromPath(path: String): Either<Array<Module>, ModuleError> {
        val modulePath = File(path)
        val moduleScope = FlexyClassLoaderFactory.INSTANCE.create()
        moduleScope.load(modulePath)
        val pathList = modulePath.list()
        val file = pathList.find { it == "modules.conf" }
        return file?.let {
            val modules = mutableListOf<Module>()
            Paths.get(path, it).toFile().forEachLine { moduleClass ->
                val module = moduleScope.loadClass(moduleClass).newInstance() as Module
                modules.add(module)
                moduleScopes[module] = moduleScope
            }
            ok<Array<Module>, ModuleError>(modules.toTypedArray())
        } ?: fail(ModuleError("module.file.doesnt.exist", "module '$path' does not exist"))
    }

    companion object {
        val defaultPath = "${system.homeDir}/.cs/modules"
    }

}