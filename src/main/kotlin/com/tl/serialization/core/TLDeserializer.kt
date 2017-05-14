package com.tl.serialization.core

import com.tl.serialization.TLContext
import com.tl.serialization.TLObject
import java.io.IOException
import java.nio.charset.Charset
import java.util.*

class TLDeserializer(private val fields: HashMap<Int, Any>) {

    private val serialized = HashMap<Int, Boolean>()

    @Throws(IOException::class)
    fun optLong(id: Int): Long {
        return getLong(id, 0)
    }

    @Throws(IOException::class)
    fun getLong(id: Int): Long {
        if (!fields.containsKey(id)) {
            throw IOException("Unable to find field #" + id)
        }
        return getLong(id, 0)
    }

    @Throws(IOException::class)
    fun getLong(id: Int, defValue: Long): Long {
        if (fields.containsKey(id)) {
            serialized.put(id, true)
            val obj = fields[id]
            if (obj is Long) {
                return obj
            }
            throw IOException("Unexpected type:" + obj)
        }
        return defValue
    }

    @Throws(IOException::class)
    fun nullSafeInt(id: Int): Int {
        return convertInt(optLong(id))
    }

    @Throws(IOException::class)
    fun getInt(id: Int): Int {
        return convertInt(getLong(id))
    }

    @Throws(IOException::class)
    fun getInt(id: Int, defValue: Int): Int {
        return convertInt(getLong(id, defValue.toLong()))
    }

    @Throws(IOException::class)
    fun nullSafeDouble(id: Int): Double {
        return java.lang.Double.longBitsToDouble(optLong(id))
    }

    @Throws(IOException::class)
    fun getDouble(id: Int): Double {
        return java.lang.Double.longBitsToDouble(getLong(id))
    }

    @Throws(IOException::class)
    fun getDouble(id: Int, defValue: Double): Double {
        return java.lang.Double.longBitsToDouble(getLong(id, java.lang.Double.doubleToLongBits(defValue)))
    }

    @Throws(IOException::class)
    fun nullSafeBool(id: Int): Boolean {
        return optLong(id) != 0L
    }

    @Throws(IOException::class)
    fun getBool(id: Int): Boolean {
        return getLong(id) != 0L
    }

    @Throws(IOException::class)
    fun getBool(id: Int, defValue: Boolean): Boolean {
        return getLong(id, (if (defValue) 1 else 0).toLong()) != 0L
    }

    @Throws(IOException::class)
    fun nullSafeBytes(id: Int): ByteArray? {
        return getBytes(id, null)
    }

    @Throws(IOException::class)
    fun getBytes(id: Int): ByteArray? {
        if (!fields.containsKey(id)) {
            throw IOException("Unable to find field #" + id)
        }
        return getBytes(id, null)
    }

    @Throws(IOException::class)
    fun getBytes(id: Int, defValue: ByteArray?): ByteArray? {
        if (fields.containsKey(id)) {
            serialized.put(id, true)
            val obj = fields[id]
            if (obj is ByteArray) {
                return obj
            }
            throw IOException("Unexpected type: " + obj)
        }
        return defValue
    }

    @Throws(IOException::class)
    fun nullSafeString(id: Int): String? {
        return convertString(nullSafeBytes(id))
    }

    @Throws(IOException::class)
    fun getString(id: Int): String? {
        return convertString(getBytes(id))
    }

    @Throws(IOException::class)
    fun getString(id: Int, defValue: String): String? {
        return convertString(getBytes(id, defValue.toByteArray(charset("UTF-8"))))
    }

    @Throws(IOException::class)
    fun <T : TLObject> nullSafeObj(id: Int, obj: T): T? {
        val data = nullSafeBytes(id) ?: return null
        return TLContext.deserialize(obj, data)
    }

    @Throws(IOException::class)
    fun <T : TLObject> getObject(id: Int, obj: T): T? {
        val data = nullSafeBytes(id) ?: throw IOException("Unable to find field #" + id)
        return null
    }

    @Throws(IOException::class)
    fun getListCount(id: Int): Int {
        if (fields.containsKey(id)) {
            serialized.put(id, true)
            val value = fields[id]
            return (value as? List<*>)?.size ?: 1
        }
        return 0
    }

    @Throws(IOException::class)
    fun getListOfLongs(id: Int): List<Long> {
        val res = ArrayList<Long>()
        if (fields.containsKey(id)) {
            serialized.put(id, true)
            val value = fields[id]
            if (value is Long) {
                res.add(value)
            } else if (value is List<*>) {
                for (val2 in value) {
                    if (val2 is Long) {
                        res.add(val2)
                    } else {
                        throw IOException("Unexpected type: " + val2)
                    }
                }
            } else {
                throw IOException("Unexpected type: " + value)
            }
        }
        return res
    }

    @Throws(IOException::class)
    fun getListOfInts(id: Int): List<Int> {
        val src = getListOfLongs(id)
        val res = ArrayList<Int>()
        for (l in src) {
            res.add(convertInt(l))
        }
        return res
    }

    @Throws(IOException::class)
    fun getListOfBytes(id: Int): List<ByteArray> {
        val res = ArrayList<ByteArray>()
        if (fields.containsKey(id)) {
            serialized.put(id, true)
            val value = fields[id]
            if (value is ByteArray) {
                res.add(value)
            } else if (value is List<*>) {
                for (val2 in value) {
                    if (val2 is ByteArray) {
                        res.add(val2)
                    } else {
                        throw IOException("Unexpected type " + val2)
                    }
                }
            } else {
                throw IOException("Unexpected type: " + value)
            }
        }
        return res
    }

    @Throws(IOException::class)
    fun getListOfString(id: Int): List<String> {
        val src = getListOfBytes(id)
        val res = ArrayList<String>()
        for (l in src) {
            res.add(convertString(l)!!)
        }
        return res
    }

    companion object {

        @Throws(IOException::class)
        fun convertInt(value: Long): Int {
            if (value < Integer.MIN_VALUE)
                throw IOException("Too small value")
             else if (value > Integer.MAX_VALUE)
                throw IOException("Too big value")

            return value.toInt()
        }

        @Throws(IOException::class)
        fun convertString(data: ByteArray?): String? {
            return if (data == null) null else String(data, Charset.defaultCharset())
        }
    }
}
