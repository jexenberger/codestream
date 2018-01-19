package io.codestream.util.crypto

import io.codestream.util.system
import org.junit.Test
import java.io.File
import java.util.*
import kotlin.test.assertNotNull

class SSLTest {

    @Test
    fun testCtx() {
        val f = "${system.tempDir}/${UUID.randomUUID()}"
        File(f).deleteOnExit()
        val store = SSL.defaultStoreAndKey("localhost", f)
        val ctx = SSL.ctx(store.store, store.store)
        assertNotNull(ctx)
        assertNotNull(store["localhost"])
    }
}