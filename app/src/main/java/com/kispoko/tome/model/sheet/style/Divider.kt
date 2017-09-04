
package com.kispoko.tome.model.sheet.style


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.sql.SQLInt
import com.kispoko.tome.lib.orm.sql.SQLReal
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLValue
import effect.effError
import effect.effValue
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import java.io.Serializable



/**
 * Divider Margin
 */
data class DividerMargin(val value : Float) : SQLSerializable, Serializable
{

    companion object : Factory<DividerMargin>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<DividerMargin> = when (doc)
        {
            is DocNumber -> effValue(DividerMargin(doc.number.toFloat()))
            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
        }

        fun default() : DividerMargin = DividerMargin(0f)
    }


    override fun asSQLValue() : SQLValue = SQLReal({this.value.toDouble()})

}


/**
 * Divider Thickness
 */
data class DividerThickness(val value : Int) : SQLSerializable, Serializable
{

    companion object : Factory<DividerThickness>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<DividerThickness> = when (doc)
        {
            is DocNumber -> effValue(DividerThickness(doc.number.toInt()))
            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
        }

        fun default() : DividerThickness = DividerThickness(0)
    }


    override fun asSQLValue() : SQLValue = SQLInt({this.value})

}




