package io.codestream.module.xmlmodule

import io.codestream.core.*
import io.codestream.runtime.StreamContext
import javax.xml.parsers.DocumentBuilderFactory

class ParseXmlTask : Task, SetOutput {


    var xml: String = ""

    override var outputVar: String = "\$xmldoc"

    override fun execute(id: TaskId, ctx: StreamContext): TaskError? {
        ctx[outputVar] = DocumentBuilderFactory
                .newInstance()
                .newDocumentBuilder()
                .parse(xml.byteInputStream())
        return done()
    }

}