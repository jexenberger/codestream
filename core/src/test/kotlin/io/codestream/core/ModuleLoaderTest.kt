package io.codestream.core

import io.codestream.runtime.ModuleLoader
import io.codestream.util.log.ConsoleLog
import org.junit.Test
import kotlin.test.assertNotNull

class ModuleLoaderTest {

    @Test
    fun testLoadModuleFromPath() {
        val loader = ModuleLoader(arrayOf(ModuleLoader.defaultPath, "src/test/resources/modules"))
        val result = loader.loadModulesFromPath("src/test/resources/modules/testmodule")
        assertNotNull(result.left!!)
        result.left?.forEach {
            val cl = loader.moduleScopes[it]
            assertNotNull(cl)
            val clazz = cl?.loadClass("com.jcraft.jsch.JSch")
            assertNotNull(clazz)
            //test we loaded the jar
            clazz!!.newInstance()
        }
    }


    @Test
    fun testLoad() {
        val log = ConsoleLog()
        log.enableDebug = true
        val loader = ModuleLoader(arrayOf(ModuleLoader.defaultPath, "src/test/resources/modules"), log)
        val result = loader.load()
        val mockModule = Module["mock"]
        assertNotNull(mockModule)
        val cl = loader.moduleScopes[mockModule]
        assertNotNull(cl)
        //test we loaded the jar
        val clazz = cl?.loadClass("com.jcraft.jsch.JSch")
        assertNotNull(clazz)
        clazz!!.newInstance()
    }


}