package io.codestream.util.json

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule


val json = JsonFactory.mapper

object JsonFactory {

    val mapper by lazy {
        ObjectMapper()
                .registerKotlinModule()
                .findAndRegisterModules()
    }


}