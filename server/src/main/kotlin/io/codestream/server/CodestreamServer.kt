package io.codestream.server

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import spark.Spark.path
import spark.Spark.post


fun main(args: Array<String>) {
    val mapper = ObjectMapper().registerKotlinModule()

    val path = "workspace"

    path("/stream-runs/:group/:name") {
        post("/") { req, res ->
            val body = req.body()
            val data = mapper.readValue<StreamRun>(body)
            val group = req.params("group")
            val name = req.params("name")
            val result = StreamApi().runStream(path, group, name, data)
            res.header("content-type","application/json")
            result.map(
                    {
                        res.status(201)
                        res.header("location", "/stream-run/${it.id}")
                        res.body(mapper.writeValueAsString(it))
                    }
                    ,
                    {
                        res.status(500)
                        mapper.writeValueAsString(it)
                    }
            )
        }
    }

}

