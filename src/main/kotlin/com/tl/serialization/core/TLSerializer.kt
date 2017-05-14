package com.tl.serialization.core

import com.tl.serialization.Const
import com.tl.serialization.TLObject
import com.tl.serialization.stream.DataOutputStream
import java.io.IOException
import java.util.*

class TLSerializer(private val stream: DataOutputStream?) {

    private val writtenFields = HashMap<Int, Boolean>()

    init {
        if (stream == null) {
            throw IllegalArgumentException("Stream can not be null")
        }
    }

    @Throws(IOException::class)
    fun serializeBytes(fieldNumber: Int, value: ByteArray) {
        if (value == null) {
            throw IllegalArgumentException("Value can not be null")
        }
        if (value.size > Const.MAX_SIZE) {
            throw IllegalArgumentException("Unable to write more than 1 MB")
        }
        writtenFields.put(fieldNumber, true)
        serializeBytesField(fieldNumber, value)
    }

    @Throws(IOException::class)
    fun serializeString(fieldNumber: Int, value: String) {
        if (value == null) {
            throw IllegalArgumentException("Value can not be null")
        }
        writtenFields.put(fieldNumber, true)
        serializeBytesField(fieldNumber, value.toByteArray())
    }

    @Throws(IOException::class)
    fun serializeBool(fieldNumber: Int, value: Boolean) {
        serializeVarIntField(fieldNumber, (if (value) 1 else 0).toLong())
    }

    @Throws(IOException::class)
    fun serializeInt(fieldNumber: Int, value: Int) {
        serializeVarIntField(fieldNumber, value.toLong())
    }

    @Throws(IOException::class)
    fun serializeDouble(fieldNumber: Int, value: Double) {
        serializeVar64Fixed(fieldNumber, java.lang.Double.doubleToLongBits(value))
    }

    @Throws(IOException::class)
    fun serializeLong(fieldNumber: Int, value: Long) {
        serializeVarIntField(fieldNumber, value)
    }

    @Throws(IOException::class)
    fun serializeListOfLongs(fieldNumber: Int, values: List<Long>) {
        if (values == null) {
            throw IllegalArgumentException("Values can not be null")
        }
        if (values.size > Const.MAX_SIZE) {
            throw IllegalArgumentException("Too many values")
        }
        writtenFields.put(fieldNumber, true)
        for (l in values) {
            if (l == null) {
                throw IllegalArgumentException("Value can not be null")
            }
            serializeVarIntField(fieldNumber, l)
        }
    }

    @Throws(IOException::class)
    fun serializeListOfInts(fieldNumber: Int, values: List<Int>) {
        if (values == null) {
            throw IllegalArgumentException("Values can not be null")
        }
        if (values.size > Const.MAX_SIZE) {
            throw IllegalArgumentException("Too many values")
        }
        writtenFields.put(fieldNumber, true)
        for (l in values) {
            if (l == null) {
                throw IllegalArgumentException("Value can not be null")
            }
            serializeVarIntField(fieldNumber, l.toLong())
        }
    }

    @Throws(IOException::class)
    fun serializeListOfBools(fieldNumber: Int, values: List<Boolean>) {
        if (values == null) {
            throw IllegalArgumentException("Values can not be null")
        }
        if (values.size > Const.MAX_SIZE) {
            throw IllegalArgumentException("Too many values")
        }
        writtenFields.put(fieldNumber, true)
        for (l in values) {
            if (l == null) {
                throw IllegalArgumentException("Value can not be null")
            }
            serializeBool(fieldNumber, l)
        }
    }

    @Throws(IOException::class)
    fun serializeListOfStrings(fieldNumber: Int, values: List<String>) {
        if (values == null) {
            throw IllegalArgumentException("Values can not be null")
        }
        if (values.size > Const.MAX_SIZE) {
            throw IllegalArgumentException("Too many values")
        }
        writtenFields.put(fieldNumber, true)
        for (l in values) {
            if (l == null) {
                throw IllegalArgumentException("Value can not be null")
            }
            serializeString(fieldNumber, l)
        }
    }

    @Throws(IOException::class)
    fun serializeListOfBytes(fieldNumber: Int, values: List<ByteArray>) {
        if (values == null) {
            throw IllegalArgumentException("Values can not be null")
        }
        if (values.size > Const.MAX_SIZE) {
            throw IllegalArgumentException("Too many values")
        }
        writtenFields.put(fieldNumber, true)
        for (l in values) {
            if (l == null) {
                throw IllegalArgumentException("Value can not be null")
            }
            serializeBytes(fieldNumber, l)
        }
    }

    @Throws(IOException::class)
    fun <T : TLObject> serializeListOfObj(fieldNumber: Int, values: List<T>) {
        if (values == null) {
            throw IllegalArgumentException("Values can not be null")
        }
        if (values.size > Const.MAX_SIZE) {
            throw IllegalArgumentException("Too many values")
        }
        writtenFields.put(fieldNumber, true)
        for (l in values) {
            if (l == null) {
                throw IllegalArgumentException("Value can not be null")
            }
            serializeObject(fieldNumber, l)
        }
    }

    @Throws(IOException::class)
    fun serializeObject(fieldNumber: Int, value: TLObject) {
        if (value == null) {
            throw IllegalArgumentException("Value can not be null")
        }
        writtenFields.put(fieldNumber, true)
        serializeTag(fieldNumber, Const.OUTLINE_LENGTH)
        val outputStream = DataOutputStream()
        val writer = TLSerializer(outputStream)
        value.serialize(writer)
        serializeBytes(outputStream.toBytes())
    }

    @Throws(IOException::class)
    private fun serializeTag(fieldNumber: Int, wireType: Int) {
        var fieldNumber = fieldNumber
        fieldNumber = fieldNumber and 0xFFFF
        if (fieldNumber <= 0) {
            throw IllegalArgumentException("Field Number must greater than zero")
        }

        val tag = (fieldNumber shl 3).toLong() or wireType.toLong()
        stream!!.writeVarInt(tag)
    }

    @Throws(IOException::class)
    private fun serializeVarIntField(fieldNumber: Int, value: Long) {
        serializeTag(fieldNumber, 0)
        serializeVarInt(value)
    }

    @Throws(IOException::class)
    private fun serializeBytesField(fieldNumber: Int, value: ByteArray) {
        serializeTag(fieldNumber, Const.OUTLINE_LENGTH)
        serializeBytes(value)
    }

    @Throws(IOException::class)
    private fun serializeVar64Fixed(fieldNumber: Int, value: Long) {
        serializeTag(fieldNumber, Const.BIT_64)
        serializeLong(value)
    }

    @Throws(IOException::class)
    private fun serializeVarInt(value: Long) {
        stream!!.writeVarInt(value and 0xFFFFFFFF.toInt().toLong())
    }

    @Throws(IOException::class)
    private fun serializeLong(v: Long) {
        stream!!.writeLong(v and 0xFFFFFFFF.toInt().toLong())
    }

    @Throws(IOException::class)
    private fun serializeBytes(data: ByteArray) {
        stream!!.writeProtoBytes(data, 0, data.size)
    }
}
