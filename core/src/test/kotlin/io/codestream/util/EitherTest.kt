package io.codestream.util

import org.junit.Test

class EitherTest {

    @Test
    fun testBind() {
        ok<String, String> { "hello " }
                .bind { ok<String, String>(it.toUpperCase()) }
                .bind { ok<String, String>(it.toUpperCase()) }


    }
}