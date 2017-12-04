package io.codestream.util

abstract  class Credentials(val user:String)

class UserPassword(user:String, val password:String) : Credentials(user)

class SSHKey(user:String, val keyFile:String) : Credentials(user)