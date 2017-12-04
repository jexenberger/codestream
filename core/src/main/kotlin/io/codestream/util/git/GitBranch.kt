package io.codestream.util.git

data class GitBranch(val name:String) {
    val shortName:String
    get() = name.substring(name.lastIndexOf('/')+1)

}