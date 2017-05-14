package com.tl.serialization.stream

import com.tl.serialization.Const

class DataOutputStream {

    private var data = ByteArray(16)
    private var offset: Int = 0

    private fun expand(size: Int) {
        var nSize = data.size
        while (nSize < size) {
            nSize = getEnthropySize(nSize)
        }

        val nData = ByteArray(nSize)
        System.arraycopy(data, 0, nData, 0, offset)
        data = nData
    }

    fun writeLong(v: Long) {
        var v = v
        if (data.size <= offset + 8) {
            expand(offset + 8)
        }

        v = v and 0xFFFFFFFFFFFFFFFL

        data[offset++] = (v shr 56 and 0xFF).toByte()
        data[offset++] = (v shr 48 and 0xFF).toByte()
        data[offset++] = (v shr 40 and 0xFF).toByte()
        data[offset++] = (v shr 32 and 0xFF).toByte()
        data[offset++] = (v shr 24 and 0xFF).toByte()
        data[offset++] = (v shr 16 and 0xFF).toByte()
        data[offset++] = (v shr 8 and 0xFF).toByte()
        data[offset++] = (v and 0xFF).toByte()
    }

    fun writeByte(v: Int) {
        if (v < 0) {
            throw IllegalArgumentException("Value can't be negative")
        }
        if (v > 255) {
            throw IllegalArgumentException("Value can't be more than 255")
        }
        if (data.size <= offset + 1) {
            expand(offset + 1)
        }
        data[offset++] = v.toByte()
    }

    fun writeVarInt(v: Long) {
        var v = v
        while (v and 0xfffffffffffff80L != 0L) {
            writeByte((v and 0x7f or 0x80).toInt())
            v = v ushr 7
        }

        writeByte((v and 0x7f).toInt())
    }

    fun writeProtoBytes(v: ByteArray, ofs: Int, len: Int) {
        writeVarInt(len.toLong())
        writeBytes(v, ofs, len)
    }

    fun writeBytes(v: ByteArray, ofs: Int, len: Int) {
        if (len > Const.MAX_SIZE) {
            throw IllegalArgumentException("Unable to write more than 1 MB")
        }
        if (len < 0) {
            throw IllegalArgumentException("Length can't be negative")
        }
        if (ofs < 0) {
            throw IllegalArgumentException("Offset can't be negative")
        }
        if (ofs + len > v.size) {
            throw IllegalArgumentException("Conflicting sizes")
        }

        if (data.size < offset + v.size) {
            expand(offset + v.size)
        }
        for (i in 0..len - 1) {
            data[offset++] = v[i + ofs]
        }
    }

    fun toBytes(): ByteArray {
        val res = ByteArray(offset)
        for (i in 0..offset - 1) {
            res[i] = data[i]
        }
        return res
    }

    companion object {

        fun getEnthropySize(currentSize: Int): Int {
            return if (currentSize <= 4) 8 else currentSize * 2
        }
    }
}
