
package com.kispoko.tome.model.sheet.widget


import com.kispoko.tome.db.DB_WidgetFormatValue
import com.kispoko.tome.db.widgetFormatTable
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.ProdType
import com.kispoko.tome.lib.orm.RowValue2
import com.kispoko.tome.lib.orm.schema.PrimValue
import com.kispoko.tome.lib.orm.schema.ProdValue
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.asSQLValue
import com.kispoko.tome.model.sheet.style.ElementFormat
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import java.io.Serializable
import java.util.*



/**
 * Widget Format
 */
data class WidgetFormat(override val id : UUID,
                        val width : WidgetWidth,
                        val elementFormat : ElementFormat)
                         : ToDocument, ProdType, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(width : WidgetWidth,
                elmentFormat : ElementFormat)
        : this(UUID.randomUUID(),
               width,
               elmentFormat)

    companion object : Factory<WidgetFormat>
    {

        private fun defaultWidth()         = WidgetWidth.default()
        private fun defaultElementFormat() = ElementFormat.default()


        override fun fromDocument(doc : SchemaDoc) : ValueParser<WidgetFormat> = when (doc)
        {
            is DocDict ->
            {
                apply(::WidgetFormat,
                      // Width
                      split(doc.maybeAt("width"),
                            effValue(defaultWidth()),
                            { WidgetWidth.fromDocument(it) }),
                      // Element Format
                      split(doc.maybeAt("element_format"),
                            effValue(defaultElementFormat()),
                            { ElementFormat.fromDocument(it) })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = WidgetFormat(defaultWidth(), defaultElementFormat())
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "width" to this.width.toDocument(),
        "element_format" to this.elementFormat.toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun width() : Int = this.width.value

    fun elementFormat() : ElementFormat = this.elementFormat


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_WidgetFormatValue =
        RowValue2(widgetFormatTable,
                  PrimValue(this.width),
                  ProdValue(this.elementFormat))

}


/**
 * Widget Width
 */
data class WidgetWidth(val value : Int) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<WidgetWidth>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<WidgetWidth> = when (doc)
        {
            is DocNumber -> effValue(WidgetWidth(doc.number.toInt()))
            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
        }

        fun default() = WidgetWidth(1)
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocNumber(this.value.toDouble())


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() = this.value.asSQLValue()

}

