package io.codestream.util


fun <T, K> mapOf(func: MapBuilder<T, K>.() -> Unit): Map<T, K> {
    val theMap = MapBuilder<T, K>()
    theMap.func()
    return theMap.toMap()
}

class MapBuilder<T, K> {

    var map: MutableMap<T, K> = mutableMapOf()


    infix fun put(value: Pair<T, K>): MapBuilder<T, K> {
        map[value.first] = value.second
        return this
    }

    fun toMap(): Map<T, K> {
        return emptyMap()
    }


}

