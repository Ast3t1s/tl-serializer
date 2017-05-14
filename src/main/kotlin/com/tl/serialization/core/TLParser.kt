package com.tl.serialization.core

import com.tl.serialization.Const
import com.tl.serialization.stream.DataInputStream
import java.io.IOException
import java.util.*

object TLParser {

    @Throws(IOException::class)
    fun deserialize(inputStream: DataInputStream): HashMap<Int, Any> {
        val hashMap = HashMap<Int, Any>()
        while (!inputStream.isEOF) {
            val currentTag = inputStream.readVarInt()

            val id = (currentTag shr 3).toInt()
            val type = (currentTag and 0x7).toInt()

            when (type) {
                0 -> put(id, inputStream.readVarInt(), hashMap)
                Const.OUTLINE_LENGTH -> {
                    val size = inputStream.readVarInt().toInt()
                    put(id, inputStream.readBytes(size), hashMap)
                }
                Const.BIT_64 -> put(id, inputStream.readLong(), hashMap)
                Const.BIT_32 -> put(id, inputStream.readUInt(), hashMap)
                else -> throw IOException("Unknown Type #" + type)
            }
        }
        return hashMap
    }

    private fun put(id: Int, res: Any, hashMap: HashMap<Int, Any>) {
        if (hashMap[id] != null) {
            if (hashMap[id] is List<*>) {
                (hashMap[id] as List<*>).toMutableList().add(res)
            } else {
                val list = ArrayList<Any>()
                list.add(hashMap[id] as Any)
                list.add(res)
                hashMap.put(id, list)
            }
        } else {
            hashMap.put(id, res)
        }
    }
}
