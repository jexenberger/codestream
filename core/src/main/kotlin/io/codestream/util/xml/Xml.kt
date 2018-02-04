package io.codestream.util.xml

import org.w3c.dom.Document
import java.io.File
import javax.xml.namespace.NamespaceContext
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

object Xml {

    private val factory: DocumentBuilderFactory = DocumentBuilderFactory.newInstance()
    private val xPathFactory: XPathFactory = XPathFactory.newInstance()


    fun parse(file: File): Document {
        return factory.newDocumentBuilder().parse(file)
    }

    fun parse(xml: String): Document {
        return factory.newDocumentBuilder().parse(xml.byteInputStream())
    }

    fun eval(document: Document, expr: String, nsMap: Map<String, String> = mapOf()): Any? {
        val xPath = xPathFactory.newXPath()
        xPath.namespaceContext = MapNamespaceContext(nsMap)
        val xPathExpression = xPath.compile(expr)
        return xPathExpression.evaluate(document, XPathConstants.STRING)
    }


}

private class MapNamespaceContext(val nsMap: Map<String, String>) : NamespaceContext {
    override fun getNamespaceURI(prefix: String): String? {
        return nsMap[prefix]
    }

    override fun getPrefix(namespaceURI: String): String? {
        return nsMap.entries.find { it.value.equals(namespaceURI) }?.key
    }

    override fun getPrefixes(namespaceURI: String): Iterator<Any?> {
        return nsMap.entries.filter { it.value.equals(namespaceURI) }.map { it.key }.iterator()
    }

}