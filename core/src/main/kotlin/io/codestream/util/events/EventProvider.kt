package io.codestream.util.events

interface EventProvider {

    fun fire(event: Event)

}