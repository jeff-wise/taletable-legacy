
package com.kispoko.tome.model.sheet.style


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Comp
import com.kispoko.tome.lib.functor.Func
import com.kispoko.tome.lib.functor.Null
import com.kispoko.tome.lib.functor.Prim
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.model.sheet.VerticalAlignment
import com.kispoko.tome.model.theme.ColorId
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import lulo.value.valueResult
import java.util.*



/**
 * Text Format
 */
data class TextFormat(override val id : UUID,
                      val style : Func<TextStyle>,
                      val position : Func<Position>,
                      val height : Func<Height>,
                      val padding : Func<Spacing>,
                      val margins : Func<Spacing>,
                      val alignment: Func<Alignment>,
                      val verticalAlignment: Func<VerticalAlignment>) : Model
{

    companion object : Factory<TextFormat>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<TextFormat> = when (doc)
        {
            is DocDict ->
            {
                effApply8(::TextFormat,
                          // Model Id
                          valueResult(UUID.randomUUID()),
                          // Style
                          split(doc.maybeAt("style"),
                                valueResult<Func<TextStyle>>(Null()),
                                fun(d : SpecDoc) : ValueParser<Func<TextStyle>> =
                                    effApply(::Comp, TextStyle.fromDocument(d))),
                          // Position
                          split(doc.maybeEnum<Position>("position"),
                                valueResult<Func<Position>>(Null()),
                                { valueResult(Prim(it))  }),
                          // Height
                          split(doc.maybeEnum<Height>("height"),
                                valueResult<Func<Height>>(Null()),
                                { valueResult(Prim(it))  }),
                          // Padding
                          split(doc.maybeAt("padding"),
                                valueResult<Func<Spacing>>(Null()),
                                fun(d : SpecDoc) : ValueParser<Func<Spacing>> =
                                    effApply(::Comp, Spacing.fromDocument(d))),
                          // Margins
                          split(doc.maybeAt("margins"),
                                valueResult<Func<Spacing>>(Null()),
                                fun(d : SpecDoc) : ValueParser<Func<Spacing>> =
                                    effApply(::Comp, Spacing.fromDocument(d))),
                          // Alignment
                          split(doc.maybeEnum<Alignment>("alignment"),
                                valueResult<Func<Alignment>>(Null()),
                                { valueResult(Prim(it))  }),
                          // Vertical Alignment
                          split(doc.maybeEnum<VerticalAlignment>("vertical_alignment"),
                                valueResult<Func<VerticalAlignment>>(Null()),
                                { valueResult(Prim(it))  })
                          )
            }

            else       -> Err(UnexpectedType(DocType.DICT, docType(doc)), doc.path)
        }
    }

    override fun onLoad() { }

}


/**
 * Text Style
 */
data class TextStyle(override val id : UUID,
                     val color : Func<ColorId>,
                     val size : Func<TextSize>,
                     val font : Func<TextFont>,
                     val isUnderlined : Func<Boolean>,
                     val alignment: Func<Alignment>,
                     val backgroundColor : Func<ColorId>) : Model
{

    companion object : Factory<TextStyle>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<TextStyle> = when (doc)
        {
            is DocDict -> effApply7(::TextStyle,
                                    // Model Id
                                    valueResult(UUID.randomUUID()),
                                    // Color
                                    doc.at("color") ap {
                                        effApply(::Prim, ColorId.fromDocument(it))
                                    },
                                    // Size
                                    doc.at("size") ap {
                                        effApply(::Prim, TextSize.fromDocument(it))
                                    },
                                    // Font
                                    effApply(::Prim, doc.enum<TextFont>("font")),
                                    // Is Underlined?
                                    effApply(::Prim, doc.boolean("is_underlined")),
                                    // Alignment
                                    effApply(::Prim, doc.enum<Alignment>("alignment")),
                                    // Color
                                    doc.at("background_color") ap {
                                        effApply(::Prim, ColorId.fromDocument(it))
                                    })
            else       -> Err(UnexpectedType(DocType.DICT, docType(doc)), doc.path)
        }
    }

    override fun onLoad() { }

}


/**
 * Text Size
 */
data class TextSize(val value : Double)
{

    companion object : Factory<TextSize>
    {
        override fun fromDocument(doc: SpecDoc) : ValueParser<TextSize> = when (doc)
        {
            is DocNumber -> valueResult(TextSize(doc.number))
            else         -> Err(UnexpectedType(DocType.NUMBER, docType(doc)), doc.path)
        }
    }
}


/**
 * Text Font
 */
enum class TextFont
{
    REGULAR,
    BOLD,
    ITALIC,
    BOLD_ITALIC
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

//
//public class TextStyle extends Model
//                       implements ToYaml, Serializable
//{
//
//    // PROPERTIES
//    // -----------------------------------------------------------------------------------------
//
//    // > Model
//    // -----------------------------------------------------------------------------------------
//
//    private UUID                                id;
//
//
//    // > Functors
//    // -----------------------------------------------------------------------------------------
//
//    private PrimitiveFunctor<TextColor>         color;
//    private PrimitiveFunctor<TextSize>          size;
//    private PrimitiveFunctor<TextFont>          font;
//    private primitivefunctor<boolean>           isunderlined;
//    private primitivefunctor<alignment>         alignment;
//    private primitivefunctor<backgroundcolor>   backgroundcolor;
//
//
//    // constructors
//    // -----------------------------------------------------------------------------------------
//
//    public textstyle()
//    {
//        this.id                 = null;
//
//        this.color              = new primitivefunctor<>(null, textcolor.class);
//        this.size               = new primitivefunctor<>(null, textsize.class);
//        this.font               = new primitivefunctor<>(null, textfont.class);
//        this.isunderlined       = new primitivefunctor<>(null, boolean.class);
//        this.alignment          = new primitivefunctor<>(null, alignment.class);
//        this.backgroundcolor    = new primitivefunctor<>(null, backgroundcolor.class);
//    }
//
//
//    public textstyle(uuid id,
//                     textcolor color,
//                     textsize size,
//                     textfont font,
//                     boolean isunderlined,
//                     alignment alignment,
//                     backgroundcolor backgroundcolor)
//    {
//        this.id                 = id;
//
//        this.color              = new primitivefunctor<>(color, textcolor.class);
//        this.size               = new primitivefunctor<>(size, textsize.class);
//        this.font               = new primitivefunctor<>(font, textfont.class);
//        this.isunderlined       = new primitivefunctor<>(isunderlined, boolean.class);
//        this.alignment          = new primitivefunctor<>(alignment, alignment.class);
//        this.backgroundcolor    = new primitivefunctor<>(backgroundcolor, backgroundcolor.class);
//
//        this.setcolor(color);
//        this.setsize(size);
//        this.setfont(font);
//        this.setisunderlined(isunderlined);
//        this.setalignment(alignment);
//        this.setbackgroundcolor(backgroundcolor);
//    }
//
//
//    public textstyle(uuid id,
//                     textcolor color,
//                     textsize size)
//    {
//        this.id                 = id;
//
//        this.color              = new primitivefunctor<>(color, textcolor.class);
//        this.size               = new primitivefunctor<>(size, textsize.class);
//        this.font               = new primitivefunctor<>(null, textfont.class);
//        this.isunderlined       = new primitivefunctor<>(null, boolean.class);
//        this.alignment          = new primitivefunctor<>(null, alignment.class);
//        this.backgroundcolor    = new primitivefunctor<>(null, backgroundcolor.class);
//
//        this.setcolor(color);
//        this.setsize(size);
//        this.setfont(null);
//        this.setisunderlined(null);
//        this.setalignment(null);
//        this.setbackgroundcolor(null);
//    }
//
//
//    public textstyle(uuid id,
//                     textcolor color,
//                     textsize size,
//                     alignment alignment)
//    {
//        this.id                 = id;
//
//        this.color              = new primitivefunctor<>(color, textcolor.class);
//        this.size               = new primitivefunctor<>(size, textsize.class);
//        this.font               = new primitivefunctor<>(null, textfont.class);
//        this.isunderlined       = new primitivefunctor<>(null, boolean.class);
//        this.alignment          = new primitivefunctor<>(alignment, alignment.class);
//        this.backgroundcolor    = new primitivefunctor<>(null, backgroundcolor.class);
//
//        this.setcolor(color);
//        this.setsize(size);
//        this.setfont(null);
//        this.setisunderlined(null);
//        this.setalignment(alignment);
//        this.setbackgroundcolor(null);
//    }
//
//
//    public textstyle(uuid id,
//                     textcolor color,
//                     textsize size,
//                     textfont font)
//    {
//        this.id                 = id;
//
//        this.color              = new primitivefunctor<>(color, textcolor.class);
//        this.size               = new primitivefunctor<>(size, textsize.class);
//        this.font               = new primitivefunctor<>(font, textfont.class);
//        this.isunderlined       = new primitivefunctor<>(null, boolean.class);
//        this.alignment          = new primitivefunctor<>(null, alignment.class);
//        this.backgroundcolor    = new primitivefunctor<>(null, backgroundcolor.class);
//
//        this.setcolor(color);
//        this.setsize(size);
//        this.setfont(font);
//        this.setisunderlined(null);
//        this.setalignment(null);
//        this.setbackgroundcolor(null);
//    }
//
//
//    public textstyle(uuid id,
//                     textcolor color,
//                     textsize size,
//                     textfont font,
//                     alignment alignment)
//    {
//        this.id                 = id;
//
//        this.color              = new primitivefunctor<>(color, textcolor.class);
//        this.size               = new primitivefunctor<>(size, textsize.class);
//        this.font               = new primitivefunctor<>(font, textfont.class);
//        this.isunderlined       = new primitivefunctor<>(null, boolean.class);
//        this.alignment          = new primitivefunctor<>(alignment, alignment.class);
//        this.backgroundcolor    = new primitivefunctor<>(null, backgroundcolor.class);
//
//        this.setcolor(color);
//        this.setsize(size);
//        this.setfont(font);
//        this.setisunderlined(null);
//        this.setalignment(alignment);
//        this.setbackgroundcolor(null);
//    }
//
//
//    /**
//     * create a widget text style from its yaml representation. if the yaml is null, return a
//     * default text style.
//     * @param yaml the yaml parser.
//     * @return the parsed text style.
//     * @throws yamlparseexception
//     */
//    public static textstyle fromyaml(yamlparser yaml)
//                  throws yamlparseexception
//    {
//        return textstyle.fromyaml(yaml, false);
//    }
//
//
//    /**
//     * create a text style from its yaml representation. if the yaml is null, and usedefault is
//     * null, returns null.
//     * @param yaml the yaml parser.
//     * @param usedefault if true, return a default when the yaml is null.
//     * @return the parsed text style.
//     * @throws yamlparseexception
//     */
//    public static textstyle fromyaml(yamlparser yaml, boolean usedefault)
//                  throws yamlparseexception
//    {
//        if (yaml.isnull() && usedefault)
//            return textstyle.asdefault();
//        else if (yaml.isnull())
//            return null;
//
//        uuid            id           = uuid.randomuuid();
//
//        textcolor       color        = textcolor.fromyaml(yaml.atmaybekey("color"));
//        textsize        size         = textsize.fromyaml(yaml.atmaybekey("size"));
//        textfont        font         = textfont.fromyaml(yaml.atmaybekey("font"));
//        boolean         isunderlined = yaml.atmaybekey("is_underlined").getboolean();
//        alignment       alignment    = alignment.fromyaml(yaml.atmaybekey("alignment"));
//        backgroundcolor bgcolor      = backgroundcolor.fromyaml(
//                                                yaml.atmaybekey("background_color"));
//
//        return new textstyle(id, color, size, font, isunderlined, alignment, bgcolor);
//    }
//
//
//    public static textstyle asdefault()
//    {
//        textstyle style = new textstyle();
//
//        style.setid(uuid.randomuuid());
//        style.setcolor(null);
//        style.setsize(null);
//        style.setfont(null);
//        style.setisunderlined(null);
//        style.setalignment(null);
//        style.setbackgroundcolor(null);
//
//        return style;
//    }
//
//
//    // api
//    // -----------------------------------------------------------------------------------------
//
//    // > model
//    // --------------------------------------------------------------------------------------
//
//    // ** id
//    // --------------------------------------------------------------------------------------
//
//    public uuid getid()
//    {
//        return this.id;
//    }
//
//
//    public void setid(uuid id)
//    {
//        this.id = id;
//    }
//
//
//    // ** on load
//    // --------------------------------------------------------------------------------------
//
//    /**
//     * called when the text widget format is completely loaded.
//     */
//    public void onload() { }
//
//
//    // > to yaml
//    // -----------------------------------------------------------------------------------------
//
//    public yamlbuilder toyaml()
//    {
//        return yamlbuilder.map()
//                .putyaml("color", this.color())
//                .putyaml("size", this.size())
//                .putyaml("font", this.font())
//                .putboolean("is_underlined", this.isunderlined())
//                .putyaml("alignment", this.alignment())
//                .putyaml("background_color", this.backgroundcolor());
//    }
//
//
//    // > state
//    // -----------------------------------------------------------------------------------------
//
//    // ** color
//    // -----------------------------------------------------------------------------------------
//
//    /**
//     * the text color.
//     * @return the text color.
//     */
//    public textcolor color()
//    {
//        return this.color.getvalue();
//    }
//
//
//    public void setcolor(textcolor color)
//    {
//        if (color != null)
//            this.color.setvalue(color);
//        else
//            this.color.setvalue(textcolor.theme_medium);
//    }
//
//
//    // ** size
//    // -----------------------------------------------------------------------------------------
//
//    /**
//     * the text size.
//     * @return the size.
//     */
//    public textsize size()
//    {
//        return this.size.getvalue();
//    }
//
//
//    /**
//     * set the text size. if null, defaults to medium.
//     * @param size the text size.
//     */
//    public void setsize(textsize size)
//    {
//        if (size != null)
//            this.size.setvalue(size);
//        else
//            this.size.setvalue(textsize.medium);
//    }
//
//
//    // ** is underlined
//    // -----------------------------------------------------------------------------------------
//
//    /**
//     * true if the text should be underlined.
//     * @return is underlined?
//     */
//    public boolean isunderlined()
//    {
//        return this.isunderlined.getvalue();
//    }
//
//
//    /**
//     * set the text to be underlined or not underlined. defaults to non underlined if null.
//     * @param isunderlined is underlined?
//     */
//    public void setisunderlined(boolean isunderlined)
//    {
//        if (isunderlined != null)
//            this.isunderlined.setvalue(isunderlined);
//        else
//            this.isunderlined.setvalue(false);
//    }
//
//
//    // ** font
//    // -----------------------------------------------------------------------------------------
//
//    /**
//     * the font.
//     * @return the font.
//     */
//    public textfont font()
//    {
//        return this.font.getvalue();
//    }
//
//
//    /**
//     * set the text font
//     * @param font the font.
//     */
//    public void setfont(textfont font)
//    {
//        if (font != null)
//            this.font.setvalue(font);
//        else
//            this.font.setvalue(textfont.regular);
//    }
//
//
//    // ** alignment
//    // -----------------------------------------------------------------------------------------
//
//    /**
//     * the alignment.
//     * @return the alignment.
//     */
//    public alignment alignment()
//    {
//        return this.alignment.getvalue();
//    }
//
//
//    /**
//     * set the alignment. if null, defaults to left.
//     * @param alignment the alignment.
//     */
//    public void setalignment(alignment alignment)
//    {
//        if (alignment != null)
//            this.alignment.setvalue(alignment);
//        else
//            this.alignment.setvalue(alignment.left);
//    }
//
//
//    // ** background color
//    // -----------------------------------------------------------------------------------------
//
//    /**
//     * the background color.
//     * @return the background color.
//     */
//    public backgroundcolor backgroundcolor()
//    {
//        return this.backgroundcolor.getvalue();
//    }
//
//
//    /**
//     * set the background color. if null, defaults to medium.
//     * @param backgroundcolor the background color.
//     */
//    public void setbackgroundcolor(backgroundcolor backgroundcolor)
//    {
//        if (backgroundcolor != null)
//            this.backgroundcolor.setvalue(backgroundcolor);
//        else
//            this.backgroundcolor.setvalue(backgroundcolor.medium);
//    }
//
//
//    // > typeface
//    // -----------------------------------------------------------------------------------------
//
//    public typeface typeface(context context)
//    {
//        switch (this.font())
//        {
//            case regular:
//                return font.seriffontregular(context);
//            case bold:
//                return font.seriffontbold(context);
//            case italic:
//                return font.seriffontitalic(context);
//            case bold_italic:
//                return font.seriffontbolditalic(context);
//            default:
//                return font.seriffontregular(context);
//        }
//    }
//
//
//    // > style text view
//    // -----------------------------------------------------------------------------------------
//
//    /**
//     * format a text view with this style.
//     * @param textview the text view.
//     */
//    public void styletextview(textview textview, context context)
//    {
//        // ** color
//        textview.settextcolor(contextcompat.getcolor(context, this.color().resourceid()));
//
//        // ** size
//        textview.settextsize(context.getresources().getdimension(this.size().resourceid()));
//
//        // ** font
//        textview.settypeface(this.typeface(context));
//    }
//
//
//    // > style text view builder
//    // -----------------------------------------------------------------------------------------
//
//    /**
//     * set the text view builder style options to match this style.
//     * @param viewbuilder the text view builder.
//     */
//    public void styletextviewbuilder(textviewbuilder viewbuilder, context context)
//    {
//        // ** color
//        viewbuilder.color   = this.color().resourceid();
//
//        // ** size
//        viewbuilder.size    = this.size().resourceid();
//
//        // ** fton
//        viewbuilder.font    = this.typeface(context);
//    }
//
//}
