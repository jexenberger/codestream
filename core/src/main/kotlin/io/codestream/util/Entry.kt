package io.codestream.util

data class Entry<out K, out V>(override val key: K, override val value: V) : Map.Entry<K, V>