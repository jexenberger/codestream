package io.codestream.util.git

data class GitBranch(val name: String) {
    val shortName: String
        get() {
            val parts = name.split("/")
            return if (name.startsWith("refs/heads")) {
                parts.drop(2).joinToString("/")
            } else {
                parts.drop(3).joinToString("/")
            }
        }

    val remote: Boolean
        get() = name.startsWith("refs/remote")



}