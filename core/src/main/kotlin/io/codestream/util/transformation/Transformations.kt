package io.codestream.util.transformation


object Transformations {


    fun toBoolean(o: String?): Boolean {
        return if (o == null) {
            false
        } else o.toString().equals("1", ignoreCase = true) ||
                o.toString().equals("yes", ignoreCase = true) ||
                o.toString().equals("true", ignoreCase = true) ||
                o.toString().equals("y", ignoreCase = true)

    }

    fun toBoolean(o: Char?): Boolean {
        return if (o == null) {
            false
        } else o == '1' || o == 'y' || o == 'Y'
    }

    fun toBoolean(o: Number?): Boolean {
        return if (o == null) {
            false
        } else o.toInt() == 1
    }


}