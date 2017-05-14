package com.tl.serialization

import com.tl.serialization.core.TLDeserializer
import com.tl.serialization.core.TLParser
import com.tl.serialization.core.TLSerializer
import com.tl.serialization.stream.DataInputStream
import com.tl.serialization.stream.DataOutputStream
import java.io.IOException

abstract class TLObject {

    @Throws(IOException::class)
    protected fun load(data: ByteArray) {
        val values = TLDeserializer(TLParser.deserialize(DataInputStream(data, 0, data.size)))
        deserialize(values)
    }

    fun toBytes(): ByteArray {
        val outputStream = DataOutputStream()
        val writer = TLSerializer(outputStream)
        try {
            serialize(writer)
        } catch (e: IOException) {
            throw RuntimeException("Unexpected IO exception")
        }

        return outputStream.toBytes()
    }

    @Throws(IOException::class)
    abstract fun deserialize(deserializer: TLDeserializer)

    @Throws(IOException::class)
    abstract fun serialize(serializer: TLSerializer)
}
