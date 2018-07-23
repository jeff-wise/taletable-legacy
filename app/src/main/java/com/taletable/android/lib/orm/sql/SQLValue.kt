
package com.taletable.android.lib.orm.sql


sealed class SQLValue
{
    abstract fun type() : SQLValueType


}


object SQLNull : SQLValue()
{
    override fun type() : SQLValueType = SQLValueType.NULL
}


class SQLInt(val lazyValue : () -> Long) : SQLValue()
{

    fun value() : Long = lazyValue()

    override fun type() : SQLValueType = SQLValueType.INTEGER

}


data class SQLReal(val lazyValue : () -> Double) : SQLValue()
{

    fun value() : Double = lazyValue()

    override fun type() : SQLValueType = SQLValueType.REAL
}


data class SQLText(val lazyValue : () -> String) : SQLValue()
{

    fun value() : String = lazyValue()

    override fun type() : SQLValueType = SQLValueType.TEXT

}


data class SQLBlob(val lazyValue : () -> ByteArray) : SQLValue()
{

    fun value() : ByteArray = lazyValue()

    override fun type() : SQLValueType = SQLValueType.BLOB


    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}


enum class SQLValueType
{
    NULL,
    INTEGER,
    REAL,
    TEXT,
    BLOB,
}


val SQL_NULL_TYPE_STRING    = "NULL"
val SQL_INTEGER_TYPE_STRING = "INTEGER"
val SQL_REAL_TYPE_STRING    = "REAL"
val SQL_TEXT_TYPE_STRING    = "Text"
val SQL_BLOB_TYPE_STRING    = "BLOB"


