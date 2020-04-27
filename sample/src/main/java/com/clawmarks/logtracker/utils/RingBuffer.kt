package com.clawmarks.logtracker.utils

import java.util.*


/**
 * A simple ring buffer structure with bounded capacity backed by an array.
 * Events can always be added at the logical end of the buffer. If the buffer is
 * full, oldest events are dropped when new events are added.
 * {@hide}
 */
class RingBuffer<T>(capacity: Int):Iterable<T> {
    private var buffer : Array<T?>
    private var count = 0 // number of elements on queue

    private var indexOut = 0 // index of first element of queue

    private var indexIn = 0 // index of next available slot


    init {
        val checkedCapacity = if (capacity>0) capacity else 0
        @Suppress("UNCHECKED_CAST") // cast needed since no generic array creation in Java
        buffer = arrayOfNulls<Any>(checkedCapacity) as Array<T?>
    }

    fun isEmpty(): Boolean {
        return count == 0
    }

    fun size(): Int {
        return count
    }

    fun push(item: T) {
        if (count == buffer.size) {
            throw RuntimeException("Ring buffer overflow")
        }
        buffer[indexIn] = item
        indexIn = (indexIn + 1) % buffer.size // wrap-around
        count++
    }

    fun pop(): T {
        if (isEmpty()) {
            throw RuntimeException("Ring buffer underflow")
        }
        val item = buffer[indexOut]
        buffer[indexOut] = null // to help with garbage collection
        count--
        indexOut = (indexOut + 1) % buffer.size // wrap-around
        return item!!
    }

    override fun iterator(): Iterator<T> {
        return RingBufferIterator()
    }

    private inner class RingBufferIterator : Iterator<T> {
        private var i = 0
        override fun hasNext(): Boolean {
            return i < count
        }

        override fun next(): T {
            if (!hasNext()) {
                throw NoSuchElementException()
            }
            return buffer[i++]!!
        }
    }
}