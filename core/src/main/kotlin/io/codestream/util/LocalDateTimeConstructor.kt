package io.codestream.util

import org.yaml.snakeyaml.constructor.AbstractConstruct
import org.yaml.snakeyaml.constructor.Constructor
import org.yaml.snakeyaml.nodes.Node
import org.yaml.snakeyaml.nodes.ScalarNode
import org.yaml.snakeyaml.nodes.Tag
import java.time.LocalDateTime


class LocalDateTimeConstructor : Constructor() {

    init {
        this.yamlConstructors.put(Tag("!localDateTime"), ConstructLocalDateTime())
    }


    private inner class ConstructLocalDateTime : AbstractConstruct() {
        override fun construct(node: Node): Any {
            if (node is ScalarNode) {
                val value = constructScalar(node) as String
                return LocalDateTime.parse(value)
            } else {
                throw RuntimeException("node not instance of Scalar node:" + node::class)
            }
        }
    }
}