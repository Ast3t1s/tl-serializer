package com.tl.serialization

import com.tl.serialization.core.TLDeserializer
import com.tl.serialization.core.TLParser
import com.tl.serialization.stream.DataInputStream
import java.io.IOException

object TLContext {

    @Throws(IOException::class)
    fun <T : TLObject> deserialize(creator: TLWrapper<T>, data: ByteArray): T {
        return deserialize(creator.wrap(), DataInputStream(data, 0, data.size))
    }

    @Throws(IOException::class)
    fun <T : TLObject> deserialize(res: T, data: ByteArray): T {
        return deserialize(res, DataInputStream(data, 0, data.size))
    }

    @Throws(IOException::class)
    fun <T : TLObject> deserialize(res: T, inputStream: DataInputStream): T {
        val reader = TLDeserializer(TLParser.deserialize(inputStream))
        res.deserialize(reader)
        return res
    }
}
