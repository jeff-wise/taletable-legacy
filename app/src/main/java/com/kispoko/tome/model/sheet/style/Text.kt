
package com.kispoko.tome.model.sheet.style


import android.content.Context
import android.util.Log
import android.util.TypedValue
import android.widget.TextView
import com.kispoko.tome.db.*
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.ProdType
import com.kispoko.tome.lib.orm.RowValue9
import com.kispoko.tome.lib.orm.schema.PrimValue
import com.kispoko.tome.lib.orm.schema.ProdValue
import com.kispoko.tome.lib.orm.sql.*
import com.kispoko.tome.lib.ui.Font
import com.kispoko.tome.lib.ui.TextViewBuilder
import com.kispoko.tome.model.theme.ColorTheme
import com.kispoko.tome.rts.entity.EntityId
import com.kispoko.tome.rts.entity.colorOrBlack
import com.kispoko.tome.util.Util
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.UnexpectedValue
import lulo.value.ValueError
import lulo.value.ValueParser
import java.io.Serializable
import java.util.*



/**
 * Text Style
 */
data class TextFormat(override val id : UUID,
                      private val colorTheme : ColorTheme,
                      private val size : TextSize,
                      private val font : TextFont,
                      private val fontStyle : TextFontStyle,
                      private val isUnderlined : IsUnderlined,
                      private val numberFormat : NumberFormat,
                      private val rollFormat : RollFormat,
                      private val iconFormat : IconFormat,
                      private val elementFormat : ElementFormat)
                      : ToDocument, ProdType, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(colorTheme : ColorTheme,
                size : TextSize,
                font : TextFont,
                fontStyle : TextFontStyle,
                isUnderlined : IsUnderlined,
                numberFormat : NumberFormat,
                rollFormat : RollFormat,
                iconFormat : IconFormat,
                elementFormat : ElementFormat)
        : this(UUID.randomUUID(),
               colorTheme,
               size,
               font,
               fontStyle,
               isUnderlined,
               numberFormat,
               rollFormat,
               iconFormat,
               elementFormat)


    companion object : Factory<TextFormat>
    {

        private fun defaultColorTheme()     = ColorTheme.black
        private fun defaultTextSize()       = TextSize(16.0f)
        private fun defaultFont()           = TextFont.Cabin
        private fun defaultFontStyle()      = TextFontStyle.Regular
        private fun defaultIsUnderlined()   = IsUnderlined(false)
        private fun defaultNumberFormat()   = NumberFormat.Normal
        private fun defaultRollFormat()     = RollFormat.Normal
        private fun defaultIconFormat()     = IconFormat.default()
        private fun defaultElementFormat()  = ElementFormat.default()


        override fun fromDocument(doc: SchemaDoc): ValueParser<TextFormat> = when (doc)
        {
            is DocDict ->
            {
                apply(::TextFormat,
                      // Color Theme
                      split(doc.maybeAt("color_theme"),
                            effValue(defaultColorTheme()),
                            { ColorTheme.fromDocument(it) }),
                      // Size
                      split(doc.maybeAt("size"),
                            effValue(defaultTextSize()),
                            { TextSize.fromDocument(it) }),
                      // Font
                      split(doc.maybeAt("font"),
                             effValue<ValueError,TextFont>(defaultFont()),
                             { TextFont.fromDocument(it) }),
                      // Font Style
                      split(doc.maybeAt("font_style"),
                            effValue<ValueError,TextFontStyle>(defaultFontStyle()),
                            { TextFontStyle.fromDocument(it) }),
                      // Is Underlined?
                      split(doc.maybeAt("is_underlined"),
                            effValue(defaultIsUnderlined()),
                            { IsUnderlined.fromDocument(it) }),
                      // Number Format
                      split(doc.maybeAt("number_format"),
                            effValue<ValueError,NumberFormat>(defaultNumberFormat()),
                            { NumberFormat.fromDocument(it) }),
                      // Roll Format
                      split(doc.maybeAt("roll_format"),
                            effValue<ValueError,RollFormat>(defaultRollFormat()),
                            { RollFormat.fromDocument(it) }),
                      // Icon Format
                      split(doc.maybeAt("icon_format"),
                            effValue<ValueError,IconFormat>(defaultIconFormat()),
                            { IconFormat.fromDocument(it) }),
                      // Element Format
                      split(doc.maybeAt("element_format"),
                            effValue(defaultElementFormat()),
                            { ElementFormat.fromDocument(it) })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = TextFormat(defaultColorTheme(),
                                  defaultTextSize(),
                                  defaultFont(),
                                  defaultFontStyle(),
                                  defaultIsUnderlined(),
                                  defaultNumberFormat(),
                                  defaultRollFormat(),
                                  defaultIconFormat(),
                                  defaultElementFormat())

    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "color_theme" to this.colorTheme().toDocument(),
        "size" to this.size.toDocument(),
        "font" to this.font.toDocument(),
        "font_style" to this.fontStyle.toDocument(),
        "is_underlined" to this.isUnderlined.toDocument(),
        "number_format" to this.numberFormat.toDocument(),
        "roll_format" to this.rollFormat.toDocument(),
        "icon_format" to this.iconFormat.toDocument(),
        "element_format" to this.elementFormat.toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun colorTheme() : ColorTheme = this.colorTheme


    fun sizeSp() : Float = this.size.sp


    fun font() : TextFont = this.font


    fun fontStyle() : TextFontStyle = this.fontStyle


    fun isUnderlined() : Boolean = this.isUnderlined.value


    fun numberFormat() : NumberFormat = this.numberFormat


    fun rollFormat() : RollFormat = this.rollFormat


    fun iconFormat() : IconFormat = this.iconFormat


    fun elementFormat() : ElementFormat = this.elementFormat


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_TextFormatValue =
        RowValue9(textFormatTable, PrimValue(this.colorTheme),
                                   PrimValue(this.size),
                                   PrimValue(this.font),
                                   PrimValue(this.fontStyle),
                                   PrimValue(this.isUnderlined),
                                   PrimValue(this.numberFormat),
                                   PrimValue(this.rollFormat),
                                   ProdValue(this.iconFormat),
                                   ProdValue(this.elementFormat))


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    /**
     * Set the TextViewBuilder style options according the values in the TextFormat.
     */
    fun styleTextViewBuilder(textViewBuilder : TextViewBuilder,
                             entityId : EntityId,
                             context : Context)
    {
        textViewBuilder.color   = colorOrBlack(this.colorTheme(), entityId)
        textViewBuilder.sizeSp  = this.sizeSp()
        textViewBuilder.font    = Font.typeface(this.font(),
                                                this.fontStyle(),
                                                context)
    }


    /**
     * format a text view with this style.
     * @param textview the text view.
     */
    fun styleTextView(textView : TextView, entityId : EntityId, context : Context)
    {
        textView.setTextColor(colorOrBlack(this.colorTheme(), entityId))
        Log.d("***TEXT", "size: " + this.sizeSp().toString())
        //textView.textSize = Util.spToPx(this.sizeSp(), sheetUIContext.context).toFloat()

        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, this.sizeSp())
        textView.typeface = Font.typeface(this.font(), this.fontStyle(), context)
    }


    // -----------------------------------------------------------------------------------------
    // MODIFY
    // -----------------------------------------------------------------------------------------

    fun withColorTheme(newColorTheme : ColorTheme) : TextFormat =
        this.copy(colorTheme = newColorTheme)

    fun withFontStyle(newFontStyle : TextFontStyle) : TextFormat =
            this.copy(fontStyle = newFontStyle)

    fun withSize(newSize : TextSize) : TextFormat =
            this.copy(size = newSize)

    fun withElementFormat(newElementFormat : ElementFormat) : TextFormat =
            this.copy(elementFormat = newElementFormat)

}


/**
 * Number Format
 */
sealed class NumberFormat : ToDocument, SQLSerializable, Serializable
{

    object Modifier : NumberFormat()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({"modifier"})

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("modifier")
    }


    object Normal : NumberFormat()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({"normal"})

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("normal")
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun fromDocument(doc : SchemaDoc) : ValueParser<NumberFormat> = when (doc)
        {
            is DocText -> when (doc.text)
            {
                "normal"   -> effValue<ValueError,NumberFormat>(NumberFormat.Normal)
                "modifier" -> effValue<ValueError,NumberFormat>(NumberFormat.Modifier)
                else       -> effError<ValueError,NumberFormat>(
                                  UnexpectedValue("NumberFormat", doc.text, doc.path))
            }
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------


    fun formattedString(n : Double) : String =
        when (this)
        {
            is NumberFormat.Modifier -> {
                if (n >= 0)
                    "+${Util.doubleString(n)}"
                else
                    Util.doubleString(n)
            }
            is NumberFormat.Normal -> Util.doubleString(n)
        }
}


/**
 * Text Size
 */
data class TextSize(val sp : Float) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<TextSize>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<TextSize> = when (doc)
        {
            is DocNumber -> effValue(TextSize(doc.number.toFloat()))
            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocNumber(this.sp.toDouble())


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() = SQLReal({ this.sp.toDouble() })

}


/**
 * Is Underlined
 */
data class IsUnderlined(val value : Boolean) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<IsUnderlined>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<IsUnderlined> = when (doc)
        {
            is DocBoolean -> effValue(IsUnderlined(doc.boolean))
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

    override fun asSQLValue() = SQLInt({ if (this.value) 1 else 0 })

}


/**
 * Text Font
 */
sealed class TextFont : ToDocument, SQLSerializable, Serializable
{

    object Cabin : TextFont()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() = SQLText({ "cabin "})

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("cabin")

    }


    object FiraSans : TextFont()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() = SQLText({ "fira_sans "})

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("fira_sans")

    }


    object Merriweather : TextFont()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() = SQLText({ "merriweather "})

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("merriweather")

    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun fromDocument(doc : SchemaDoc) : ValueParser<TextFont> = when (doc)
        {
            is DocText -> when (doc.text)
            {
                "cabin"        -> effValue<ValueError,TextFont>(TextFont.Cabin)
                "fira_sans"    -> effValue<ValueError,TextFont>(TextFont.FiraSans)
                "merriweather" -> effValue<ValueError,TextFont>(TextFont.Merriweather)
                else           -> effError<ValueError,TextFont>(
                                      UnexpectedValue("TextFont", doc.text, doc.path))
            }
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }


        fun default() = TextFont.Cabin

    }


}


/**
 * Text Font
 */
sealed class TextFontStyle : ToDocument, SQLSerializable, Serializable
{

    object Regular : TextFontStyle()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() = SQLText({ "regular "})

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("regular")

    }


    object Medium : TextFontStyle()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() = SQLText({ "medium "})

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("medium")

    }


    object SemiBold : TextFontStyle()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() = SQLText({ "semi_bold "})

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("semi_bold")

    }


    object Bold : TextFontStyle()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() = SQLText({ "bold "})

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("bold")

    }


    object Italic : TextFontStyle()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() = SQLText({ "italic "})

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("italic")

    }


    object BoldItalic : TextFontStyle()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() = SQLText({ "bold_italic "})

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("bold_italic")

    }


    object Light : TextFontStyle()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() = SQLText({ "light "})

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("light")

    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun fromDocument(doc : SchemaDoc) : ValueParser<TextFontStyle> = when (doc)
        {
            is DocText -> when (doc.text)
            {
                "regular"     -> effValue<ValueError,TextFontStyle>(TextFontStyle.Regular)
                "medium"      -> effValue<ValueError,TextFontStyle>(TextFontStyle.Medium)
                "semi_bold"   -> effValue<ValueError,TextFontStyle>(TextFontStyle.SemiBold)
                "bold"        -> effValue<ValueError,TextFontStyle>(TextFontStyle.Bold)
                "italic"      -> effValue<ValueError,TextFontStyle>(TextFontStyle.Italic)
                "bold_italic" -> effValue<ValueError,TextFontStyle>(TextFontStyle.BoldItalic)
                "light"       -> effValue<ValueError,TextFontStyle>(TextFontStyle.Light)
                else          -> effError<ValueError,TextFontStyle>(
                                    UnexpectedValue("TextFontStyle", doc.text, doc.path))
            }
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }

        fun default() = TextFontStyle.Regular
    }

}




//
//    public float size()
//    {
//        switch (this)
//        {
//            case SUPER_SMALL:
//                return 3f;
//            case VERY_SMALL:
//                return 3.3f;
//            case SMALL:
//                return 3.7f;
//            case MEDIUM_SMALL:
//                return 4.2f;
//            case MEDIUM:
//                return 4.6f;
//            case MEDIUM_LARGE:
//                return 5f;
//            case LARGE:
//                return 6.2f;
//            case VERY_LARGE:
//                return 7.5f;
//            case HUGE:
//                return 9f;
//            case GARGANTUAN:
//                return 11;
//            case COLOSSAL:
//                return 13f;
//            default:
//                return 4.2f;
//        }
//    }

//
//    public Integer resourceId()
//    {
//        switch (this)
//        {
//            case THEME_VERY_LIGHT:
//                return R.color.dark_blue_hlx_5;
//            case THEME_LIGHT:
//                return R.color.dark_blue_hlx_7;
//            case THEME_MEDIUM_LIGHT:
//                return R.color.dark_blue_hlx_9;
//            case THEME_MEDIUM:
//                return R.color.dark_blue_hl_2;
//            case THEME_MEDIUM_DARK:
//                return R.color.dark_blue_hl_4;
//            case THEME_DARK:
//                return R.color.dark_blue_hl_6;
//            case THEME_VERY_DARK:
//                return R.color.dark_blue_hl_8;
//            case THEME_SUPER_DARK:
//                return R.color.dark_blue_1;
//            case THEME_BACKGROUND_LIGHT:
//                return R.color.dark_blue_5;
//            case THEME_BACKGROUND_MEDIUM_LIGHT:
//                return R.color.dark_blue_6;
//            case THEME_BACKGROUND_MEDIUM:
//                return R.color.dark_blue_7;
//            case THEME_BACKGROUND_MEDIUM_DARK:
//                return R.color.dark_blue_8;
//            case THEME_BACKGROUND_DARK:
//                return R.color.dark_blue_9;
//            case GOLD_VERY_LIGHT:
//                return R.color.gold_very_light;
//            case GOLD_LIGHT:
//                return R.color.gold_light;
//            case GOLD_MEDIUM_LIGHT:
//                return R.color.gold_medium_light;
//            case GOLD_MEDIUM:
//                return R.color.gold_medium;
//            case GOLD_MEDIUM_DARK:
//                return R.color.gold_medium_dark;
//            case GOLD_DARK:
//                return R.color.gold_dark;
//            case GOLD_VERY_DARK:
//                return R.color.gold_very_dark;
//            case PURPLE:
//                return R.color.purple_light;
//            case PURPLE_VERY_LIGHT:
//                return R.color.purple_very_light;
//            case PURPLE_LIGHT:
//                return R.color.purple_light;
//            case PURPLE_MEDIUM_LIGHT:
//                return R.color.purple_medium_light;
//            case PURPLE_MEDIUM:
//                return R.color.purple_medium;
//            case PURPLE_MEDIUM_DARK:
//                return R.color.purple_medium_dark;
//            case RED_LIGHT:
//                return R.color.red_light;
//            case RED_ORANGE_LIGHT:
//                return R.color.red_orange_light;
//            case ORANGE_LIGHT:
//                return R.color.orange_light;
//            case BLUE_LIGHT:
//                return R.color.blue_light;
//            case GREEN_VERY_LIGHT:
//                return R.color.green_very_light;
//            case GREEN_LIGHT:
//                return R.color.green_light;
//            case GREEN_MEDIUM_LIGHT:
//                return R.color.green_medium_light;
//            default:
//                return R.color.dark_blue_hl_5;
//        }
//
//    }


