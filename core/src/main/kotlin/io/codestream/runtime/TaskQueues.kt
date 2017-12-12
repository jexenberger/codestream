package io.codestream.runtime

import java.util.concurrent.*

object TaskQueues : Thread() {

    val taskQueue: ExecutorService
    val eventQueue: ExecutorService
    val trackingMap: MutableMap<String, MutableList<Future<Any>>> = ConcurrentHashMap()

    init {
        taskQueue = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 5)
        eventQueue = Executors.newSingleThreadExecutor()
        Runtime.getRuntime().addShutdownHook(this)
    }

    @Synchronized
    @Suppress("UNCHECKED_CAST")
    fun <T> run(grpId: String, handler: () -> T): Future<T> {
        val future = taskQueue.submit(Callable<T> {
            Thread.yield()
            handler()
        })
        if (!trackingMap.containsKey(grpId)) {
            trackingMap[grpId] = mutableListOf<Future<Any>>()
        }
        trackingMap[grpId]?.add(future as Future<Any>)
        return future
    }


    fun waitFor(grpId: String) {
        //we do it this way over using an iterative approach
        //as that either causes a deadlock with concurrent writes to
        //the trackingMap if we synchronize or throw concurrent modification
        //exceptions if we don't synchronize
        trackingMap[grpId]?.let {
            do {
                var allDone = true
                for (i in it.indices) {
                    Thread.yield()
                    val done = it[i].isDone
                    allDone = done and allDone
                }
            } while (!allDone)
        }
    }

    fun shutdown() {
        taskQueue.shutdown()
        eventQueue.shutdown()
    }

    override fun run() {
        shutdown()
    }
}