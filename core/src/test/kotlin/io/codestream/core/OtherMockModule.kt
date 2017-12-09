package io.codestream.core

class OtherMockModule(override val name: String = "other-mock",
                      override val factories: MutableMap<TaskType, Pair<Module.AllowedTypes, Factory<out Executable>>> = mutableMapOf()) : Module