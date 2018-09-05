
package com.taletable.android.model.sheet.widget.table.column


import com.taletable.android.lib.Factory
import com.taletable.android.lib.orm.ProdType
import com.taletable.android.lib.orm.RowValue7
import com.taletable.android.lib.orm.schema.MaybeProdValue
import com.taletable.android.lib.orm.schema.PrimValue
import com.taletable.android.lib.orm.schema.ProdValue
import com.taletable.android.lib.orm.sql.SQLInt
import com.taletable.android.lib.orm.sql.SQLSerializable
import com.taletable.android.lib.orm.sql.SQLText
import com.taletable.android.lib.orm.sql.SQLValue
import com.taletable.android.model.sheet.style.TextFormat
import com.taletable.android.model.sheet.widget.table.ColumnFormat
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueError
import lulo.value.ValueParser
import maybe.Just
import maybe.Maybe
import maybe.Nothing
import java.io.Serializable
import java.util.*



/**
 * Boolean Column Format
 */
data class BooleanColumnFormat(val columnFormat : ColumnFormat,
                               val trueFormat : Maybe<TextFormat>,
                               val falseFormat : Maybe<TextFormat>,
                               val trueText : ColumnTrueText,
                               val falseText : ColumnFalseText,
                               val showTrueIcon : ShowTrueIcon,
                               val showFalseIcon : ShowFalseIcon)
                                : ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<BooleanColumnFormat>
    {

        private fun defaultColumnFormat()  = ColumnFormat.default()
        private fun defaultTrueText()      = ColumnTrueText("True")
        private fun defaultFalseText()     = ColumnFalseText("False")
        private fun defaultShowTrueIcon()  = ShowTrueIcon(false)
        private fun defaultShowFalseIcon() = ShowFalseIcon(false)


        override fun fromDocument(doc: SchemaDoc): ValueParser<BooleanColumnFormat> = when (doc)
        {
            is DocDict ->
            {
                apply(::BooleanColumnFormat,
                      // Column Format
                      split(doc.maybeAt("column_format"),
                            effValue(defaultColumnFormat()),
                            { ColumnFormat.fromDocument(it) }),
                      // True Format
                      split(doc.maybeAt("true_format"),
                            effValue<ValueError,Maybe<TextFormat>>(Nothing()),
                            { apply(::Just, TextFormat.fromDocument(it)) }),
                      // False Format
                      split(doc.maybeAt("false_format"),
                            effValue<ValueError,Maybe<TextFormat>>(Nothing()),
                            { apply(::Just, TextFormat.fromDocument(it)) }),
                      // True Text
                      split(doc.maybeAt("true_text"),
                            effValue(defaultTrueText()),
                            { ColumnTrueText.fromDocument(it) }),
                      // False Text
                      split(doc.maybeAt("false_text"),
                            effValue(defaultFalseText()),
                            { ColumnFalseText.fromDocument(it) }),
                      // Show True Icon?
                      split(doc.maybeAt("show_true_icon"),
                            effValue(defaultShowTrueIcon()),
                            { ShowTrueIcon.fromDocument(it) }),
                      // Show False Icon?
                      split(doc.maybeAt("show_false_icon"),
                            effValue(defaultShowFalseIcon()),
                            { ShowFalseIcon.fromDocument(it) })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = BooleanColumnFormat(defaultColumnFormat(),
                                            Nothing(),
                                            Nothing(),
                                            defaultTrueText(),
                                            defaultFalseText(),
                                            defaultShowTrueIcon(),
                                            defaultShowFalseIcon())
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "column_format" to this.columnFormat().toDocument(),
        "true_text" to this.trueText().toDocument(),
        "false_text" to this.falseText().toDocument(),
        "show_true_icon" to this.showTrueIcon().toDocument(),
        "show_false_icon" to this.showFalseIcon().toDocument()
        ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun columnFormat() : ColumnFormat = this.columnFormat


    fun trueFormat() : Maybe<TextFormat> = this.trueFormat


    fun falseFormat() : Maybe<TextFormat> = this.falseFormat


    fun trueText() : ColumnTrueText = this.trueText


    fun trueTextString() : String = this.trueText.value


    fun falseText() : ColumnFalseText = this.falseText


    fun falseTextString() : String = this.falseText.value


    fun showTrueIcon() : ShowTrueIcon = this.showTrueIcon


    fun showTrueIconBoolean() : Boolean = this.showTrueIcon.value


    fun showFalseIcon() : ShowFalseIcon = this.showFalseIcon


    fun showFalseIconBoolean() : Boolean = this.showFalseIcon.value


    fun resolveTrueFormat() : TextFormat =
        when (this.trueFormat) {
            is Just -> this.trueFormat.value
            else    -> this.columnFormat.textFormat()
        }


    fun resolveFalseFormat() : TextFormat =
            when (this.falseFormat) {
                is Just -> this.falseFormat.value
                else    -> this.columnFormat.textFormat()
            }

}



/**
 * Show True Icon
 */
data class ShowTrueIcon(val value : Boolean) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ShowTrueIcon>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<ShowTrueIcon> = when (doc)
        {
            is DocBoolean -> effValue(ShowTrueIcon(doc.boolean))
            else          -> effError(UnexpectedType(DocType.BOOLEAN, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocBoolean(this.value)


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLInt({ if(this.value) 1 else 0 })

}


/**
 * Show False Icon
 */
data class ShowFalseIcon(val value : Boolean) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ShowFalseIcon>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<ShowFalseIcon> = when (doc)
        {
            is DocBoolean -> effValue(ShowFalseIcon(doc.boolean))
            else          -> effError(UnexpectedType(DocType.BOOLEAN, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocBoolean(this.value)


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLInt({ if(this.value) 1 else 0 })

}


/**
 * Column True Text
 */
data class ColumnTrueText(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ColumnTrueText>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<ColumnTrueText> = when (doc)
        {
            is DocText -> effValue(ColumnTrueText(doc.text))
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

    override fun asSQLValue(): SQLValue = SQLText({this.value})

}


/**
 * Column False Text
 */
data class ColumnFalseText(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ColumnFalseText>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<ColumnFalseText> = when (doc)
        {
            is DocText -> effValue(ColumnFalseText(doc.text))
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

    override fun asSQLValue(): SQLValue = SQLText({this.value})

}



/**
 * Default Boolean Column Value
 */
//data class DefaultBooleanColumnValue(val value : Boolean) : SQLSerializable, Serializable
//{
//
//    // -----------------------------------------------------------------------------------------
//    // CONSTRUCTORS
//    // -----------------------------------------------------------------------------------------
//
//    companion object : Factory<DefaultBooleanColumnValue>
//    {
//        override fun fromDocument(doc: SchemaDoc): ValueParser<DefaultBooleanColumnValue> = when (doc)
//        {
//            is DocBoolean -> effValue(DefaultBooleanColumnValue(doc.boolean))
//            else          -> effError(UnexpectedType(DocType.BOOLEAN, docType(doc), doc.path))
//        }
//    }
//
//
//    // -----------------------------------------------------------------------------------------
//    // SQL SERIALIZABLE
//    // -----------------------------------------------------------------------------------------
//
//    override fun asSQLValue(): SQLValue = SQLInt({ if(this.value) 1 else 0 })
//
//}

