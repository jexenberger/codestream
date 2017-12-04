package io.codestream.core

import io.codestream.util.events.Event
import io.codestream.util.events.EventProvider

class MockEventProvider : EventProvider {

    val firedEvents: MutableMap<String, Event> = mutableMapOf()

    override fun fire(event: Event) {
        firedEvents[event.id] = event
    }

}