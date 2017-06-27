
package com.kispoko.tome.model.sheet.widget.table.column


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.*
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.lib.orm.sql.SQLInt
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.model.sheet.style.TextStyle
import com.kispoko.tome.model.sheet.widget.table.ColumnFormat
import com.kispoko.tome.model.sheet.widget.table.cell.TrueText
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueError
import lulo.value.ValueParser
import java.io.Serializable
import java.util.*



/**
 * Boolean Column Format
 */
data class BooleanColumnFormat(override val id : UUID,
                               val columnFormat : Comp<ColumnFormat>,
                               val trueStyle : Maybe<Comp<TextStyle>>,
                               val falseStyle : Maybe<Comp<TextStyle>>,
                               val trueText : Prim<ColumnTrueText>,
                               val falseText : Prim<ColumnFalseText>,
                               val showTrueIcon : Prim<ShowTrueIcon>,
                               val showFalseIcon : Prim<ShowFalseIcon>)
                                : Model, Serializable
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.columnFormat.name                      = "column_format"

        when (this.trueStyle) {
            is Just -> this.trueStyle.value.name    = "true_style"
        }

        when (this.falseStyle) {
            is Just -> this.falseStyle.value.name   = "false_style"
        }
        this.trueText.name                          = "true_text"
        this.falseText.name                         = "false_text"
        this.showTrueIcon.name                      = "show_true_icon"
        this.showFalseIcon.name                     = "show_false_icon"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(format : ColumnFormat,
                trueStyle : Maybe<TextStyle>,
                falseStyle : Maybe<TextStyle>,
                trueText : ColumnTrueText,
                falseText : ColumnFalseText,
                showTrueIcon : ShowTrueIcon,
                showFalseIcon : ShowFalseIcon)
        : this(UUID.randomUUID(),
               Comp(format),
               maybeLiftComp(trueStyle),
               maybeLiftComp(falseStyle),
               Prim(trueText),
               Prim(falseText),
               Prim(showTrueIcon),
               Prim(showFalseIcon))


    companion object : Factory<BooleanColumnFormat>
    {

        private val defaultColumnFormat  = ColumnFormat.default
        private val defaultTrueText      = ColumnTrueText("True")
        private val defaultFalseText     = ColumnFalseText("False")
        private val defaultTrueStyle     = TextStyle.default
        private val defaultFalseStyle    = TextStyle.default
        private val defaultShowTrueIcon  = ShowTrueIcon(false)
        private val defaultShowFalseIcon = ShowFalseIcon(false)


        override fun fromDocument(doc : SpecDoc) : ValueParser<BooleanColumnFormat> = when (doc)
        {
            is DocDict -> effApply(::BooleanColumnFormat,
                                   // Column Format
                                   split(doc.maybeAt("column_format"),
                                         effValue(defaultColumnFormat),
                                         { ColumnFormat.fromDocument(it) }),
                                   // True Style
                                   split(doc.maybeAt("true_style"),
                                         effValue<ValueError,Maybe<TextStyle>>(Nothing()),
                                         { effApply(::Just, TextStyle.fromDocument(it)) }),
                                   // False Style
                                   split(doc.maybeAt("false_style"),
                                         effValue<ValueError,Maybe<TextStyle>>(Nothing()),
                                         { effApply(::Just, TextStyle.fromDocument(it)) }),
                                   // True Text
                                   split(doc.maybeAt("true_text"),
                                         effValue(defaultTrueText),
                                         { ColumnTrueText.fromDocument(it) }),
                                   // False Text
                                   split(doc.maybeAt("false_text"),
                                         effValue(defaultFalseText),
                                         { ColumnFalseText.fromDocument(it) }),
                                   // Show True Icon?
                                   split(doc.maybeAt("show_true_icon"),
                                         effValue(defaultShowTrueIcon),
                                         { ShowTrueIcon.fromDocument(it) }),
                                   // Show False Icon?
                                   split(doc.maybeAt("show_false_icon"),
                                         effValue(defaultShowFalseIcon),
                                         { ShowFalseIcon.fromDocument(it) })
                                   )
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        val default : BooleanColumnFormat =
                BooleanColumnFormat(defaultColumnFormat,
                                    Nothing(),
                                    Nothing(),
                                    defaultTrueText,
                                    defaultFalseText,
                                    defaultShowTrueIcon,
                                    defaultShowFalseIcon)
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun columnFormat() : ColumnFormat = this.columnFormat.value

    fun trueStyle() : Maybe<TextStyle> = getMaybeComp(this.trueStyle)

    fun falseStyle() : Maybe<TextStyle> = getMaybeComp(this.falseStyle)

    fun trueText() : String = this.trueText.value.value

    fun falseText() : String = this.falseText.value.value

    fun showTrueIcon() : Boolean = this.showTrueIcon.value.value

    fun showFalseIcon() : Boolean = this.showFalseIcon.value.value


    // -----------------------------------------------------------------------------------------
    // MODELS
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name : String = "boolean_column_format"

    override val modelObject = this

}



/**
 * Show True Icon
 */
data class ShowTrueIcon(val value : Boolean) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ShowTrueIcon>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<ShowTrueIcon> = when (doc)
        {
            is DocBoolean -> effValue(ShowTrueIcon(doc.boolean))
            else          -> effError(UnexpectedType(DocType.BOOLEAN, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLInt({ if(this.value) 1 else 0 })

}


/**
 * Show False Icon
 */
data class ShowFalseIcon(val value : Boolean) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ShowFalseIcon>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<ShowFalseIcon> = when (doc)
        {
            is DocBoolean -> effValue(ShowFalseIcon(doc.boolean))
            else          -> effError(UnexpectedType(DocType.BOOLEAN, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLInt({ if(this.value) 1 else 0 })

}


/**
 * Column True Text
 */
data class ColumnTrueText(val value : String) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ColumnTrueText>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<ColumnTrueText> = when (doc)
        {
            is DocText -> effValue(ColumnTrueText(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue(): SQLValue = SQLText({this.value})

}


/**
 * Column False Text
 */
data class ColumnFalseText(val value : String) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ColumnFalseText>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<ColumnFalseText> = when (doc)
        {
            is DocText -> effValue(ColumnFalseText(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue(): SQLValue = SQLText({this.value})

}



/**
 * Default Boolean Column Value
 */
data class DefaultBooleanColumnValue(val value : Boolean) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<DefaultBooleanColumnValue>
    {
        override fun fromDocument(doc : SpecDoc)
                        : ValueParser<DefaultBooleanColumnValue> = when (doc)
        {
            is DocBoolean -> effValue(DefaultBooleanColumnValue(doc.boolean))
            else          -> effError(UnexpectedType(DocType.BOOLEAN, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue(): SQLValue = SQLInt({ if(this.value) 1 else 0 })

}

