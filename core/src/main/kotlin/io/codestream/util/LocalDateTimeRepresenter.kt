package io.codestream.util

import org.yaml.snakeyaml.nodes.Node
import org.yaml.snakeyaml.nodes.Tag
import org.yaml.snakeyaml.representer.Represent
import org.yaml.snakeyaml.representer.Representer
import java.time.LocalDateTime


class LocalDateTimeRepresenter : Representer() {
    init {
        this.representers.put(LocalDateTime::class.java, RepresentLocalDateTime())
    }

    private inner class RepresentLocalDateTime : Represent {
        override fun representData(data: Any): Node {
            val localDateTime = data as LocalDateTime
            val value = localDateTime.toString()
            return representScalar(Tag("!localDateTime"), value)
        }
    }

}