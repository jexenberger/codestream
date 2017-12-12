package io.codestream.util.template

import com.github.jknack.handlebars.Context
import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.cache.ConcurrentMapTemplateCache
import com.github.jknack.handlebars.context.MapValueResolver
import com.github.jknack.handlebars.io.FileTemplateLoader
import java.io.StringWriter
import java.io.Writer

object TemplateEngine {

    lateinit var handlebars: Handlebars

    init {
        reinit("templates")
    }

    fun reinit(newBasePath: String) {
        handlebars = Handlebars(FileTemplateLoader(newBasePath))
                .with(ConcurrentMapTemplateCache())
    }

    fun render(template: String, data: Map<String, Any?>): String {
        val writer = StringWriter()
        render(template, writer, data)
        return writer.toString()
    }

    fun render(src: String, target: Writer, data: Map<String, Any?>) {
        val ctx = Context.newBuilder(data).resolver(MapValueResolver.INSTANCE).build()
        val template = handlebars.compile(src)
        template.apply(ctx, target)
    }

    fun renderInline(src: String, data: Map<String, Any?>): String {
        val ctx = Context.newBuilder(data).resolver(MapValueResolver.INSTANCE).build()
        val template = handlebars.compileInline(src)
        return template.apply(ctx)
    }

}