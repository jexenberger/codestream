stream(name = "MyCoolTestStream") {

    inputs {
        input("aValue", "A value to enter", required = true, optionsList = arrayOf("one", "two"))
        input<String>("aValueTwo", "A second value to enter")
    }

    task("core", "echo").apply {
        parm("value", "\${aValue}")
    }

    task("core", "echo")
            .onlyIf { defn, ctx -> true }
            .apply {
                parm("value", "\${aValue}")
            }

}
