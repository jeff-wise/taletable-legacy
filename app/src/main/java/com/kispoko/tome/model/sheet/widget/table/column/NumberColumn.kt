
package com.kispoko.tome.model.sheet.widget.table.column


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.*
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.model.sheet.style.NumberFormat
import com.kispoko.tome.model.sheet.style.NumericEditorType
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
                              val valuePrefix : Maybe<Prim<ValuePrefix>>,
                              val numberFormat : Prim<NumberFormat>,
                              val editorType : Prim<NumericEditorType>)
                               : ToDocument, Model, Serializable
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

        this.numberFormat.name                      = "number_format"

        this.editorType.name                        = "editor_type"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(columnFormat : ColumnFormat,
                valuePrefix : Maybe<ValuePrefix>,
                numberFormat : NumberFormat,
                editorType : NumericEditorType)
        : this(UUID.randomUUID(),
               Comp(columnFormat),
               maybeLiftPrim(valuePrefix),
               Prim(numberFormat),
               Prim(editorType))


    companion object : Factory<NumberColumnFormat>
    {

        private val defaultColumnFormat = ColumnFormat.default()
        private val defaultNumberFormat = NumberFormat.Normal
        private val defaultEditorType   = NumericEditorType.Calculator


        override fun fromDocument(doc: SchemaDoc): ValueParser<NumberColumnFormat> = when (doc)
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
                                { effApply(::Just, ValuePrefix.fromDocument(it)) }),
                        // Number Format
                        split(doc.maybeAt("number_format"),
                                effValue<ValueError,NumberFormat>(defaultNumberFormat),
                                { NumberFormat.fromDocument(it) }),
                        // Editor Type
                        split(doc.maybeAt("editor_type"),
                              effValue<ValueError,NumericEditorType>(defaultEditorType),
                              { NumericEditorType.fromDocument(it) })
                        )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = NumberColumnFormat(defaultColumnFormat,
                                          Nothing(),
                                          defaultNumberFormat,
                                          defaultEditorType)
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "column_format" to this.columnFormat().toDocument(),
        "editor_type" to this.editorType().toDocument()
    ))
    .maybeMerge(this.valuePrefix().apply {
        Just(Pair("default_value_prefix", it.toDocument() as SchemaDoc)) })


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun columnFormat() : ColumnFormat = this.columnFormat.value

    fun valuePrefix() : Maybe<ValuePrefix> = _getMaybePrim(this.valuePrefix)

    fun valuePrefixString() : String? = getMaybePrim(this.valuePrefix)?.value

    fun numberFormat() : NumberFormat = this.numberFormat.value

    fun editorType() : NumericEditorType = this.editorType.value


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
data class ValuePrefix(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ValuePrefix>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<ValuePrefix> = when (doc)
        {
            is DocText -> effValue(ValuePrefix(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocText(this.value)


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() = SQLText({ this.value })
}
