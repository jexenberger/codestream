package io.codestream.core

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ModuleLoaderTest {

    @Test
    fun testLoadModuleFromPath() {
        val loader = ModuleLoader(arrayOf(ModuleLoader.defaultPath, "src/test/resources/modules"))
        val result = loader.loadModulesFromPath("src/test/resources/modules/testmodule")
        assertNotNull(result.left!!)
        val mockModule = result.left?.forEach {
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
        val loader = ModuleLoader(arrayOf(ModuleLoader.defaultPath, "src/test/resources/modules"))
        val result = loader.load()
        assertEquals(1, result.size)
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