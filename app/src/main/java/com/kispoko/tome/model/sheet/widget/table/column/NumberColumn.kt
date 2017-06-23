
package com.kispoko.tome.model.sheet.widget.table.column


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.*
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.model.sheet.widget.table.ColumnFormat
import effect.*
import effect.Nothing
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueError
import lulo.value.ValueParser
import java.io.Serializable
import java.util.*



/**
 * Number Column Format
 */
data class NumberColumnFormat(override val id : UUID,
                              val columnFormat : Comp<ColumnFormat>,
                              val valuePrefix : Maybe<Prim<ValuePrefix>>) : Model, Serializable
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.columnFormat.name                      = "column_format"

        when (this.valuePrefix) {
            is Just -> this.valuePrefix.value.name  = "value_prefix"
        }
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(columnFormat : ColumnFormat,
                valuePrefix : Maybe<ValuePrefix>)
        : this(UUID.randomUUID(), Comp(columnFormat), maybeLiftPrim(valuePrefix))


    companion object : Factory<NumberColumnFormat>
    {

        private val defaultColumnFormat = ColumnFormat.default


        override fun fromDocument(doc : SpecDoc) : ValueParser<NumberColumnFormat> = when (doc)
        {
            is DocDict ->
            {
                effApply(::NumberColumnFormat,
                        // Column Format
                        split(doc.maybeAt("column_format"),
                                effValue(defaultColumnFormat),
                                { ColumnFormat.fromDocument(it) }),
                        // Default Value Prefix
                        split(doc.maybeAt("default_value_prefix"),
                                effValue<ValueError,Maybe<ValuePrefix>>(Nothing()),
                                { effApply(::Just, ValuePrefix.fromDocument(it)) })
                        )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        val default : NumberColumnFormat =
                NumberColumnFormat(defaultColumnFormat, Nothing<ValuePrefix>())
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun columnFormat() : ColumnFormat = this.columnFormat.value

    fun valuePrefixString() : String? = getMaybePrim(this.valuePrefix)?.value


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name = "number_column_format"

    override val modelObject = this

}


/**
 * Value Prefix
 */
data class ValuePrefix(val value : String) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ValuePrefix>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<ValuePrefix> = when (doc)
        {
            is DocText -> effValue(ValuePrefix(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() = SQLText({ this.value })
}
