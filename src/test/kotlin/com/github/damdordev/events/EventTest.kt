package com.github.damdordev.events

import org.junit.Test

import org.junit.Assert.*
import java.lang.IllegalStateException

class EventTest {

    @Test
    fun testAdd() {
        var value = 0

        val evt = Event<Int>()
        evt.add({ value = it })

        evt(11)
        assertEquals(11, value)
    }

    @Test
    fun testPriority(){
        var value = 0

        val evt = Event<Int>()
        evt.add( { value = 19 }, Event.DEFAULT_PRIORITY + 1)
        evt.add( { value = 5 }, Event.DEFAULT_PRIORITY)

        evt(9)

        assertEquals(19, value)
    }

    @Test
    fun testAddMultipleListeners(){
        var value1 = 0
        var value2 = 0

        val evt = Event<Int>()
        evt.add( { value1 = it })
        evt.add( { value2 = it })
        evt(5)

        assertEquals(5, value1)
        assertEquals(5, value2)
    }

    @Test
    fun testOperatorPlus(){
        var value = 0

        val evt = Event<Int>()
        evt += { value = it }
        evt(90)

        assertEquals(90, value)
    }

    @Test
    fun testOperatorPlusMultiple(){
        var value1 = 0
        var value2 = 0

        val evt = Event<Int>()
        evt += { value1 = it }
        evt += { value2 = it }

        evt(4)
        assertEquals(4, value1)
        assertEquals(4, value2)
    }

    @Test
    fun testAddAndRemove(){
        var value = 0

        val evt = Event<Int>()
        val callback = { it:Int -> value = it }
        evt.add(callback)
        evt.remove(callback)
        evt(11)
        assertEquals(0, value)
    }

    @Test
    fun testAddAndOperatorMinus(){
        var value = 0

        val evt = Event<Int>()
        val callback = { it: Int -> value = it }
        evt.add(callback)
        evt -= callback
        evt(9)

        assertEquals(0, value)
    }

    @Test
    fun testOperatorPlusAndRemove(){
        var value = 0

        val evt = Event<Int>()
        val callback = { it: Int -> value = it}
        evt += callback
        evt.remove(callback)
        evt(5)

        assertEquals(0, value)
    }

    @Test
    fun testOperatorPlusAndOperatorMinus(){
        var value = 0

        val evt = Event<Int>()
        val callback = { it: Int -> value = it }
        evt += callback
        evt -= callback
        evt(3)

        assertEquals(0, value)
    }

    @Test
    fun testRemoveOnlyOneCallback(){
        var value1 = 0
        var value2 = 0

        val evt = Event<Int>()
        val callback1 = { it: Int -> value1 = it }
        val callback2 = { it: Int -> value2 = it }
        evt += callback1
        evt += callback2
        evt -= callback2
        evt(98)

        assertEquals(98, value1)
        assertEquals(0, value2)
    }

    @Test
    fun testAddTwiceTheSameCallback(){
        var value = 0

        val evt = Event<Int>()
        val callback = { it: Int -> value += 1 }
        evt += callback
        evt += callback

        evt(3)
        assertEquals(1, value)
    }

    @Test
    fun testClear() {
        var value = 0

        val evt = Event<Int>()
        evt += { value += 3 }
        evt += { value += 2 }
        evt += { value += 11 }
        evt.clear()
        evt(3)

        assertEquals(0, value)
    }

    @Test
    fun testIfInvokeThrowsExceptionWhenCalledDuringInvoke(){
        val evt = Event<Int>()
        var exceptionCatch = false

        evt += { evt(3) }
        try {
            evt(11)
        } catch (e: IllegalStateException){
            exceptionCatch = true
        }

        assertTrue(exceptionCatch)
    }
}