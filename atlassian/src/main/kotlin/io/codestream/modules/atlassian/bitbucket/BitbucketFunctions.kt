package io.codestream.modules.atlassian.bitbucket

class BitbucketFunctions {

    fun branchFromJira(id: String, description: String): String {
        var description = description
                .trim()
                .toLowerCase()
                .split(" ")
                .take(3)
                .joinToString("-")
        return "$id-$description"
    }

    fun getTaskFromBranch(name: String): String? {
        val process = if (name.contains('/')) name.split("/")[1].trim() else name.trim()
        return process.split("-").take(2).joinToString("-")
    }
}