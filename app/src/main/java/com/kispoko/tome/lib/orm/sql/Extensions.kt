
package com.kispoko.tome.lib.orm.sql


import android.content.ContentValues
import com.kispoko.tome.lib.model.ProdType
import com.kispoko.tome.lib.orm.saveProdType
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.run



// ---------------------------------------------------------------------------------------------
// SQL SERIALIZABLE
// ---------------------------------------------------------------------------------------------

fun Int.asSQLValue() = SQLInt({this.toLong()})

fun Long.asSQLValue() = SQLInt({this})

fun Double.asSQLValue() = SQLReal({this})

fun String.asSQLValue() = SQLText({this})


fun ContentValues.addSQLValue(key : String, sqlValue : SQLValue)
{
    when (sqlValue)
    {
        is SQLInt  -> this.put(key, sqlValue.value())
        is SQLReal -> this.put(key, sqlValue.value())
        is SQLText -> this.put(key, sqlValue.value())
        is SQLBlob -> this.put(key, sqlValue.value())
        is SQLNull -> this.putNull(key)
    }
}


/**
 * Saves this product type. It runs non-recursive, saving the row only. It creates a
 * transaction for the update operation. It runs asynchronoulsy
 */
suspend fun <A : ProdType> MutableList<A>.save() = run(CommonPool, {
    this.forEach {
        saveProdType(it, listOf(), false, true)
    }
})
