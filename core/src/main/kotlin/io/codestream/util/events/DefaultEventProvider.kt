package io.codestream.util.events

object DefaultEventProvider : EventProvider {

    internal val handlers: MutableMap<String, MutableList<(event: Event) -> Unit>> = mutableMapOf()

    override fun fire(event: Event) {
        handlers[event.id]?.forEach {
            it(event)
        }
    }

    @Synchronized
    fun add(eventId: String, handler: (event: Event) -> Unit): DefaultEventProvider {
        val handlers: MutableList<(event: Event) -> Unit> = handlers[eventId]?.let {
            it.add(handler); it
        } ?: mutableListOf(handler)
        this.handlers[eventId] = handlers
        return this
    }
}