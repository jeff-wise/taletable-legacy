
package com.kispoko.tome.model.sheet.style


import android.widget.TextView
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.*
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.lib.orm.sql.*
import com.kispoko.tome.lib.ui.Font
import com.kispoko.tome.lib.ui.TextViewBuilder
import com.kispoko.tome.model.theme.ColorTheme
import com.kispoko.tome.rts.sheet.SheetUIContext
import com.kispoko.tome.rts.sheet.SheetManager
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
 * Text Format
 */
data class TextFormat(override val id : UUID,
                      val style : Comp<TextStyle>,
                      val position : Prim<Position>,
                      val height : Prim<Height>,
                      val padding : Comp<Spacing>,
                      val margins : Comp<Spacing>,
                      val alignment: Prim<Alignment>,
                      val verticalAlignment: Prim<VerticalAlignment>) : Model, Serializable
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.style.name             = "style"
        this.position.name          = "position"
        this.height.name            = "height"
        this.padding.name           = "padding"
        this.margins.name           = "margins"
        this.alignment.name         = "alignment"
        this.verticalAlignment.name = "vertical_alignment"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(style : TextStyle,
                position : Position,
                height : Height,
                padding : Spacing,
                margins : Spacing,
                alignment : Alignment,
                verticalAlignment : VerticalAlignment)
        : this(UUID.randomUUID(),
               Comp(style),
               Prim(position),
               Prim(height),
               Comp(padding),
               Comp(margins),
               Prim(alignment),
               Prim(verticalAlignment))


    companion object : Factory<TextFormat>
    {

        private val defaultStyle             = TextStyle.default()
        private val defaultPosition          = Position.Top
        private val defaultHeight            = Height.Wrap
        private val defaultPadding           = Spacing.default()
        private val defaultMargins           = Spacing.default()
        private val defaultAlignment         = Alignment.Center
        private val defaultVerticalAlignment = VerticalAlignment.Middle

        override fun fromDocument(doc: SchemaDoc): ValueParser<TextFormat> = when (doc)
        {
            is DocDict ->
            {
                effApply(::TextFormat,
                         // Style
                         split(doc.maybeAt("style"),
                               effValue(defaultStyle),
                               { TextStyle.fromDocument(it) }),
                         // Position
                         split(doc.maybeAt("position"),
                               effValue<ValueError,Position>(defaultPosition),
                               { Position.fromDocument(it) }),
                         // Height
                         split(doc.maybeAt("height"),
                               effValue<ValueError,Height>(defaultHeight),
                               { Height.fromDocument(it) }),
                         // Padding
                         split(doc.maybeAt("padding"),
                               effValue(defaultPadding),
                               { Spacing.fromDocument(it) }),
                         // Margins
                         split(doc.maybeAt("margins"),
                               effValue(defaultMargins),
                               { Spacing.fromDocument(it) }),
                         // Alignment
                         split(doc.maybeAt("alignment"),
                               effValue<ValueError,Alignment>(defaultAlignment),
                               { Alignment.fromDocument(it) }),
                         // Vertical Alignment
                         split(doc.maybeAt("vertical_alignment"),
                               effValue<ValueError,VerticalAlignment>(defaultVerticalAlignment),
                               { VerticalAlignment.fromDocument(it) })
                         )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = TextFormat(defaultStyle,
                                   defaultPosition,
                                   defaultHeight,
                                   defaultPadding,
                                   defaultMargins,
                                   defaultAlignment,
                                   defaultVerticalAlignment)

    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun style() : TextStyle = this.style.value

    fun position() : Position = this.position.value

    fun height() : Height = this.height.value

    fun padding() : Spacing = this.padding.value

    fun margins() : Spacing = this.margins.value

    fun alignment() : Alignment = this.alignment.value

    fun verticalAlignment() : VerticalAlignment = this.verticalAlignment.value


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name = "text_format"

    override val modelObject = this

}


/**
 * Text Style
 */
data class TextStyle(override val id : UUID,
                     val colorTheme : Prim<ColorTheme>,
                     val size : Prim<TextSize>,
                     val font : Prim<TextFont>,
                     val fontStyle : Prim<TextFontStyle>,
                     val isUnderlined : Prim<IsUnderlined>,
                     val alignment: Prim<Alignment>,
                     val backgroundColorTheme : Prim<ColorTheme>) : Model, Serializable
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.colorTheme.name            = "color_theme"
        this.size.name                  = "size"
        this.font.name                  = "font"
        this.fontStyle.name             = "font_style"
        this.isUnderlined.name          = "is_underlined"
        this.alignment.name             = "alignment"
        this.backgroundColorTheme.name  = "background_color_theme"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(colorTheme : ColorTheme,
                size : TextSize,
                font : TextFont,
                fontStyle : TextFontStyle,
                isUnderlined : IsUnderlined,
                alignment : Alignment,
                backgroundColorTheme: ColorTheme)
        : this(UUID.randomUUID(),
               Prim(colorTheme),
               Prim(size),
               Prim(font),
               Prim(fontStyle),
               Prim(isUnderlined),
               Prim(alignment),
               Prim(backgroundColorTheme))


    companion object : Factory<TextStyle>
    {

        private val defaultColorTheme           = ColorTheme.black
        private val defaultTextSize             = TextSize(16.0f)
        private val defaultFont                 = TextFont.FiraSans
        private val defaultFontStyle            = TextFontStyle.Regular
        private val defaultIsUnderlined         = IsUnderlined(false)
        private val defaultAlignment            = Alignment.Center
        private val defaultBackgroundColorTheme = ColorTheme.transparent


        override fun fromDocument(doc: SchemaDoc): ValueParser<TextStyle> = when (doc)
        {
            is DocDict ->
            {
                effApply(::TextStyle,
                         // Color Theme
                         split(doc.maybeAt("color_theme"),
                               effValue(defaultColorTheme),
                               { ColorTheme.fromDocument(it) }),
                         // Size
                         split(doc.maybeAt("size"),
                               effValue(defaultTextSize),
                               { TextSize.fromDocument(it) }),
                         // Font
                         split(doc.maybeAt("font"),
                                effValue<ValueError,TextFont>(defaultFont),
                                { TextFont.fromDocument(it) }),
                         // Font Style
                         split(doc.maybeAt("font_style"),
                               effValue<ValueError,TextFontStyle>(defaultFontStyle),
                              { TextFontStyle.fromDocument(it) }),
                         // Is Underlined?
                         split(doc.maybeAt("is_underlined"),
                               effValue(defaultIsUnderlined),
                               { IsUnderlined.fromDocument(it) }),
                         // Alignment
                         split(doc.maybeAt("alignment"),
                               effValue<ValueError,Alignment>(defaultAlignment),
                               { Alignment.fromDocument(it) }),
                         // Color
                         split(doc.maybeAt("background_color_theme"),
                               effValue(defaultBackgroundColorTheme),
                               { ColorTheme.fromDocument(it) })
                         )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = TextStyle(defaultColorTheme,
                                  defaultTextSize,
                                  defaultFont,
                                  defaultFontStyle,
                                  defaultIsUnderlined,
                                  defaultAlignment,
                                  defaultBackgroundColorTheme)

    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun colorTheme() : ColorTheme = this.colorTheme.value

    fun sizeSp() : Float = this.size.value.sp

    fun font() : TextFont = this.font.value

    fun fontStyle() : TextFontStyle = this.fontStyle.value

    fun isUnderlined() : Boolean = this.isUnderlined.value.value

    fun alignment() : Alignment = this.alignment.value

    fun backgroundColorTheme() : ColorTheme = this.backgroundColorTheme.value


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name = "text_style"

    override val modelObject = this


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    /**
     * Set the TextViewBuilder style options according the values in the TextStyle.
     */
    fun styleTextViewBuilder(textViewBuilder : TextViewBuilder, sheetUIContext: SheetUIContext)
    {
        textViewBuilder.color   = SheetManager.color(sheetUIContext.sheetId, this.colorTheme())
        textViewBuilder.sizeSp  = this.sizeSp()
        textViewBuilder.font    = Font.typeface(this.font(),
                                                this.fontStyle(),
                                                sheetUIContext.context)
    }


    /**
     * format a text view with this style.
     * @param textview the text view.
     */
    fun styleTextView(textView : TextView, sheetUIContext : SheetUIContext)
    {
        textView.setTextColor(SheetManager.color(sheetUIContext.sheetId, this.colorTheme()))
        textView.textSize = Util.spToPx(this.sizeSp(), sheetUIContext.context).toFloat()
        textView.typeface = Font.typeface(this.font(), this.fontStyle(), sheetUIContext.context)
    }





}



/**
 * Text Size
 */
data class TextSize(val sp : Float) : SQLSerializable, Serializable
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
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() = SQLReal({ this.sp.toDouble() })

}


/**
 * Is Underlined
 */
data class IsUnderlined(val value : Boolean) : SQLSerializable, Serializable
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
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() = SQLInt({ if (this.value) 1 else 0 })

}


/**
 * Text Font
 */
sealed class TextFont : SQLSerializable, Serializable
{

    object FiraSans : TextFont()
    {
        override fun asSQLValue() = SQLText({ "roboto "})
    }


    object Merriweather : TextFont()
    {
        override fun asSQLValue() = SQLText({ "merriweather "})
    }


    companion object
    {
        fun fromDocument(doc : SchemaDoc) : ValueParser<TextFont> = when (doc)
        {
            is DocText -> when (doc.text)
            {
                "roboto"       -> effValue<ValueError,TextFont>(TextFont.FiraSans)
                "merriweather" -> effValue<ValueError,TextFont>(TextFont.Merriweather)
                else           -> effError<ValueError,TextFont>(
                                      UnexpectedValue("TextFont", doc.text, doc.path))
            }
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }


        fun default() = TextFont.FiraSans

    }


}


/**
 * Text Font
 */
sealed class TextFontStyle : SQLSerializable, Serializable
{

    object Regular : TextFontStyle()
    {
        override fun asSQLValue() = SQLText({ "regular "})
    }


    object Bold : TextFontStyle()
    {
        override fun asSQLValue() = SQLText({ "bold "})
    }


    object Italic : TextFontStyle()
    {
        override fun asSQLValue() = SQLText({ "italic "})
    }


    object BoldItalic : TextFontStyle()
    {
        override fun asSQLValue() = SQLText({ "bold_italic "})
    }


    object Light : TextFontStyle()
    {
        override fun asSQLValue() = SQLText({ "light "})
    }


    companion object
    {
        fun fromDocument(doc : SchemaDoc) : ValueParser<TextFontStyle> = when (doc)
        {
            is DocText -> when (doc.text)
            {
                "regular"     -> effValue<ValueError,TextFontStyle>(TextFontStyle.Regular)
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


