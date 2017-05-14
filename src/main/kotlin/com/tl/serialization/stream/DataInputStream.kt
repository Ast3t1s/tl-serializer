package com.tl.serialization.stream

import com.tl.serialization.Const
import java.io.IOException

class DataInputStream {

    var data: ByteArray? = null
        private set
    var offset: Int = 0
        private set
    var maxOffset: Int = 0
        private set

    constructor(data: ByteArray?) {
        if (data == null) {
            throw IllegalArgumentException("can't be null")
        }
        this.data = data
        this.offset = 0
        this.maxOffset = data.size
    }

    constructor(data: ByteArray?, offset: Int, len: Int) {
        if (data == null) {
            throw IllegalArgumentException("data can't be null")
        }
        if (offset < 0) {
            throw IllegalArgumentException("Offset can't be negative")
        }
        if (len < 0) {
            throw IllegalArgumentException("Length can't be negative")
        }
        if (data.size < offset + len) {
            throw IllegalArgumentException("Conflicting lengths, total: " + data.size + ", offset: " + offset + ", len: " + len)
        }

        this.data = data
        this.offset = offset
        this.maxOffset = offset + len
    }

    val isEOF: Boolean get() = maxOffset <= offset

    @Throws(IOException::class)
    fun readLong(): Long {
        if (offset + 8 > maxOffset) {
            throw IOException()
        }

        val a1 = (data!![offset + 3]).toLong() and 0xFF
        val a2 = (data!![offset + 2]).toLong() and 0xFF
        val a3 = (data!![offset + 1]).toLong() and 0xFF
        val a4 = (data!![offset + 0]).toLong() and 0xFF

        val res1 = a1 + (a2 shl 8) + (a3 shl 16) + (a4 shl 24)
        offset += 4

        val b1 = (data!![offset + 3]).toLong() and 0xFF
        val b2 = (data!![offset + 2]).toLong() and 0xFF
        val b3 = (data!![offset + 1]).toLong() and 0xFF
        val b4 = (data!![offset + 0]).toLong() and 0xFF

        val res2 = b1 + (b2 shl 8) + (b3 shl 16) + (b4 shl 24)
        offset += 4

        return res2 + (res1 shl 32)
    }

    @Throws(IOException::class)
    fun readUInt(): Long {
        if (offset + 4 > maxOffset) {
            throw IOException()
        }

        val a1 = (data!![offset + 3]).toLong() and 0xFF
        val a2 = (data!![offset + 2]).toLong() and 0xFF
        val a3 = (data!![offset + 1]).toLong() and 0xFF
        val a4 = (data!![offset + 0]).toLong() and 0xFF
        offset += 4
        return a1 + (a2 shl 8) + (a3 shl 16) + (a4 shl 24)
    }

    @Throws(IOException::class)
    fun readBytes(count: Int): ByteArray {

        if (count < 0) {
            throw IOException("Count can't be negative")
        }

        if (count > Const.MAX_SIZE) {
            throw IOException("Unable to read more than 1 MB")
        }

        if (offset + count > maxOffset) {
            throw IOException("Too many to read, max len: " + maxOffset + ", required len: " + (offset + count))
        }

        val res = ByteArray(count)
        for (i in 0..count - 1) {
            res[i] = data!![offset++]
        }
        return res
    }

    @Throws(IOException::class)
    fun readVarInt(): Long {
        var value: Long = 0
        var i: Long = 0
        var b: Long

        do {
            if (offset == maxOffset) {
                throw IOException()
            }

            b = (data!![offset++]).toLong() and 0xFF

            if (b and 0x80 != 0L) {
                value = value or (b and 0x7F shl i.toInt())
                i += 7
                if (i > 70) throw IOException()

            } else {
                break
            }
        } while (true)

        return value or (b shl i.toInt())
    }

}
