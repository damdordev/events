package com.github.damdordev.events

import java.lang.IllegalStateException

class Event<TEvent> {

    companion object {
        const val DEFAULT_PRIORITY = 0
    }

    private inner class ListenerData(val listener: (TEvent) -> Unit,
                                     val priority: Int)

    private val listenerData = mutableListOf<ListenerData>()
    private val toInvokeCached = mutableListOf<ListenerData>()
    private var toInvokeCacheValid = false
    private var invocationGuard = false

    operator fun plusAssign(listener: (TEvent) -> Unit){
        add(listener)
    }

    operator fun minusAssign(listener: (TEvent) -> Unit){
        remove(listener)
    }

    fun add(listener: (TEvent) -> Unit, priority: Int = DEFAULT_PRIORITY){
        if(!listenerData.any { it.listener == listener }){
            listenerData.add(ListenerData(listener, priority))
        }
        toInvokeCacheValid = false
    }

    fun remove(listener: (TEvent) -> Unit){
        if(listenerData.removeAll { it.listener == listener }) {
            toInvokeCacheValid = false
        }
    }

    fun clear() {
        listenerData.clear()
        toInvokeCacheValid = false
    }

    operator fun invoke(evt: TEvent){
        if(invocationGuard){
            throw IllegalStateException("Cannot invoke event during invocation")
        }

        invocationGuard = true
        cacheToInvoke()
        toInvokeCached.sortedBy { it.priority }
                      .forEach { it.listener(evt) }
        invocationGuard = false
    }

    private fun cacheToInvoke(){
        if(toInvokeCacheValid){
            return
        }

        toInvokeCached.clear()
        toInvokeCached.addAll(listenerData)
        toInvokeCacheValid = true
    }

}