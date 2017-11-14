
package com.kispoko.tome.model.sheet.widget.table.column


import com.kispoko.tome.db.DB_WidgetTableColumnBooleanFormat
import com.kispoko.tome.db.dbWidgetTableColumnBooleanFormat
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.model.ProdType
import com.kispoko.tome.lib.orm.sql.SQLInt
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.model.sheet.style.TextFormat
import com.kispoko.tome.model.sheet.widget.table.ColumnFormat
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import java.io.Serializable
import java.util.*



/**
 * Boolean Column Format
 */
data class BooleanColumnFormat(override val id : UUID,
                               val columnFormat : ColumnFormat,
                               val trueFormat : TextFormat,
                               val falseFormat : TextFormat,
                               val trueText : ColumnTrueText,
                               val falseText : ColumnFalseText,
                               val showTrueIcon : ShowTrueIcon,
                               val showFalseIcon : ShowFalseIcon)
                                : ToDocument, ProdType, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(format : ColumnFormat,
                trueStyle : TextFormat,
                falseStyle : TextFormat,
                trueText : ColumnTrueText,
                falseText : ColumnFalseText,
                showTrueIcon : ShowTrueIcon,
                showFalseIcon : ShowFalseIcon)
        : this(UUID.randomUUID(),
               format,
               trueStyle,
               falseStyle,
               trueText,
               falseText,
               showTrueIcon,
               showFalseIcon)


    companion object : Factory<BooleanColumnFormat>
    {

        private fun defaultColumnFormat()  = ColumnFormat.default()
        private fun defaultTrueFormat()    = TextFormat.default()
        private fun defaultFalseFormat()   = TextFormat.default()
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
                            effValue(defaultTrueFormat()),
                            { TextFormat.fromDocument(it) }),
                      // False Format
                      split(doc.maybeAt("false_format"),
                            effValue(defaultFalseFormat()),
                            { TextFormat.fromDocument(it) }),
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
                                            defaultTrueFormat(),
                                            defaultFalseFormat(),
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
        "true_format" to this.trueFormat.toDocument(),
        "false_format" to this.falseFormat.toDocument(),
        "true_text" to this.trueText().toDocument(),
        "false_text" to this.falseText().toDocument(),
        "show_true_icon" to this.showTrueIcon().toDocument(),
        "show_false_icon" to this.showFalseIcon().toDocument()
        ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun columnFormat() : ColumnFormat = this.columnFormat


    fun trueFormat() : TextFormat = this.trueFormat


    fun falseFormat() : TextFormat = this.falseFormat


    fun trueText() : ColumnTrueText = this.trueText


    fun trueTextString() : String = this.trueText.value


    fun falseText() : ColumnFalseText = this.falseText


    fun falseTextString() : String = this.falseText.value


    fun showTrueIcon() : ShowTrueIcon = this.showTrueIcon


    fun showTrueIconBoolean() : Boolean = this.showTrueIcon.value


    fun showFalseIcon() : ShowFalseIcon = this.showFalseIcon


    fun showFalseIconBoolean() : Boolean = this.showFalseIcon.value


    // -----------------------------------------------------------------------------------------
    // MODELS
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun row() : DB_WidgetTableColumnBooleanFormat =
            dbWidgetTableColumnBooleanFormat(this.columnFormat,
                                             this.trueFormat,
                                             this.falseFormat,
                                             this.trueText,
                                             this.falseText,
                                             this.showTrueIcon,
                                             this.showFalseIcon)

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

