package io.codestream.util.events

object Events {

    var eventProvider: EventProvider = DefaultEventProvider

    fun fire(event: Event) {
        eventProvider?.fire(event)
    }

    fun set(provider: EventProvider) {
        eventProvider = provider
    }


}