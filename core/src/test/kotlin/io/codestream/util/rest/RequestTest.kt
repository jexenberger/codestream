package io.codestream.util.rest

import io.fabric8.mockwebserver.DefaultMockServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.IOException


class RequestTest {


    var server: DefaultMockServer? = null

    @Before
    fun setUp() {
        server = DefaultMockServer()
        server?.start(4444)
    }

    @After
    fun tearDown() {
        server?.shutdown()
    }

    @Test
    fun testHeader() {
        val request = Request(
                url = "https://api.iextrading.com",
                path = "1.0/stock/aapl/batch",
                validateHostName = false,
                validateSSL = false,
                contentType = "application/json"
        )
        request.headers(
                "test1" to "1",
                "test2" to "2"
        )
    }

    @Test
    fun testGet() {

        server?.expect()?.get()?.withPath("/test/test")?.andReturn(200, "admin")?.once()
        val request = Request(
                url = "http://localhost:4444",
                path = "test/test",
                validateHostName = false,
                validateSSL = false,
                contentType = "application/json"
        )
        try {
            val result = request
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

        server?.expect()?.post()?.withPath("/test/test")?.andReturn(200, "admin")?.once()


        val request = Request(
                url = "http://localhost:4444",
                path = "test/test",
                contentType = "application/json",
                validateHostName = false,
                validateSSL = false)
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