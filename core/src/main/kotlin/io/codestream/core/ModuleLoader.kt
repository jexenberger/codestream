package io.codestream.core

import io.codestream.module.coremodule.CoreModule
import io.codestream.module.gitmodule.GitModule
import io.codestream.module.iomodule.IOModule
import io.codestream.module.templatemodule.TemplateModule
import io.codestream.util.ConsoleLog
import io.codestream.util.Either
import io.codestream.util.fail
import io.codestream.util.ok
import org.kevoree.kcl.api.FlexyClassLoader
import org.kevoree.kcl.api.FlexyClassLoaderFactory
import java.io.File
import java.nio.file.Paths


class ModuleLoader(val paths: Array<String>) {

    val moduleScopes: MutableMap<Module, FlexyClassLoader> = mutableMapOf()

    val log = ConsoleLog()

    init {
        log.log("Loading core modules")
        Module += CoreModule()
        log.log("Core module...")
        Module += TemplateModule()
        log.log("Template module...")
        Module += IOModule()
        log.log("I/O module...")
        Module += GitModule()
        log.log("Git module...")
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
                            modules.forEach { Module += it }
                        }, {
                            moduleThatCouldNotBeLoaded.add(it)
                            it
                        })
                    }
                }
            }
        }
        return moduleThatCouldNotBeLoaded;
    }

    fun loadModulesFromPath(path: String): Either<Array<Module>, ModuleError> {
        val modulePath = File(path)
        val moduleScope = FlexyClassLoaderFactory.INSTANCE.create()
        moduleScope.load(modulePath)
        val pathList = modulePath.list()
        val file = pathList.find { it.equals("modules.conf") }
        return file?.let {
            val modules = mutableListOf<Module>()
            Paths.get(path, it).toFile().forEachLine { moduleClass ->
                val module = moduleScope.loadClass(moduleClass).newInstance() as Module
                modules.add(module)
                moduleScopes[module] = moduleScope
            }
            ok<Array<Module>, ModuleError>(modules.toTypedArray())
        } ?: fail(ModuleError("module.file.doesnt.exist", "module '${path}' does not exist"))
    }

    companion object {
        val defaultPath = "${System.getProperty("user.home")}/.cs/modules"
    }

}