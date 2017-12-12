package io.codestream.util.rest

import io.codestream.TestSettings
import org.junit.Test
import java.io.IOException

class RequestTest {


    val settings: TestSettings = TestSettings.get()





    @Test
    fun testHeader() {
        val request = Request("https://api.iextrading.com", path = "1.0/stock/aapl/batch", validateHostName = false, validateSSL = false)
        settings.setProxy(request)
        request.headers(
                "test1" to "1",
                "test2" to "2"
        )
    }

    @Test
    fun testGet() {
        val request = Request("https://api.iextrading.com", path = "1.0/stock/aapl/batch", validateHostName = false, validateSSL = false)
        settings.setProxy(request)
        try {
            val result = request
                    .parm("types", "quote,news,chart")
                    .parm("range", "1m")
                    .parm("last", "10")
                    .get()

            println(result)
        } catch (e: IOException) {
            //ignore it did go through and fire off the request
            e.printStackTrace()
        }
    }

    @Test
    fun testPost() {
        val request = Request("https://api.iextrading.com", path = "1.0/stock/aapl/batch", validateHostName = false, validateSSL = false)
        settings.setProxy(request)
        request.parms(
                "types" to "quote,news,chart",
                "range" to "1m",
                "last" to "10"
        )
        try {
            println(request.post())
        } catch (e: IOException) {
            //ignore it did go through and fire off the request
            e.printStackTrace()
        }

    }
}