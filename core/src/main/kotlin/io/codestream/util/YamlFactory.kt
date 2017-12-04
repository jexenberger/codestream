package io.codestream.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.yaml.snakeyaml.Yaml

object YamlFactory {

    private val yamlFactory = Yaml(LocalDateTimeConstructor(), LocalDateTimeRepresenter())

    fun yaml(): Yaml {
        return yamlFactory
    }

    fun mapper(): ObjectMapper {
        val jacksonYamlFactory = YAMLFactory()
        return ObjectMapper(jacksonYamlFactory).registerKotlinModule()
    }


}
