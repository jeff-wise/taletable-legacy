package com.kispoko.tome.model.sheet.widget

import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Comp
import com.kispoko.tome.lib.functor.Prim
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.model.sheet.style.TextFormat
import com.kispoko.tome.model.sheet.style.TextStyle
import com.kispoko.tome.model.theme.ColorTheme
import effect.effApply
import effect.effError
import effect.effValue
import effect.split
import lulo.document.DocDict
import lulo.document.DocType
import lulo.document.SpecDoc
import lulo.document.docType
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import java.util.*

/**
 * Created by jeff on 7/23/17.
 */
/**
 * Points Widget Format
 */
data class PointsWidgetFormat(override val id : UUID,
                              val widgetFormat : Comp<WidgetFormat>,
                              val limitTextFormat : Comp<TextFormat>,
                              val currentTextFormat : Comp<TextFormat>,
                              val limitColorTheme : Prim<ColorTheme>,
                              val currentColorTheme : Prim<ColorTheme> ) : Model
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.widgetFormat.name      = "widget_format"
        this.limitTextFormat.name   = "limit_text_format"
        this.currentTextFormat.name = "current_text_format"
        this.limitColorTheme.name   = "limit_color_theme"
        this.currentColorTheme.name = "current_color_theme"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(widgetFormat : WidgetFormat,
                limitTextFormat : TextFormat,
                currentTextFormat : TextFormat,
                limitColorTheme : ColorTheme,
                currentColorTheme : ColorTheme)
        : this(UUID.randomUUID(),
               Comp(widgetFormat),
               Comp(limitTextFormat),
               Comp(currentTextFormat),
               Prim(limitColorTheme),
               Prim(currentColorTheme))


    companion object : Factory<PointsWidgetFormat>
    {

        val defaultWidgetFormat         = WidgetFormat.default()
        val defaultLimitTextFormat      = TextFormat.default()
        val defaultCurrentTextFormat    = TextFormat.default()
        val defaultLimitColorTheme      = ColorTheme.black
        val defaultCurrentColorTheme    = ColorTheme.white


        override fun fromDocument(doc : SpecDoc) : ValueParser<PointsWidgetFormat> = when (doc)
        {
            is DocDict -> effApply(::PointsWidgetFormat,
                                   // Widget Format
                                   split(doc.maybeAt("widget_format"),
                                         effValue(defaultWidgetFormat),
                                         { WidgetFormat.fromDocument(it) }),
                                   // Limit Text Format
                                   split(doc.maybeAt("limit_text_format"),
                                         effValue(defaultLimitTextFormat),
                                         { TextFormat.fromDocument(it) }),
                                   // Current Text Format
                                   split(doc.maybeAt("current_text_format"),
                                         effValue(defaultCurrentTextFormat),
                                         { TextFormat.fromDocument(it) }),
                                   // Limit Color Theme
                                   split(doc.maybeAt("limit_color_theme"),
                                         effValue(defaultLimitColorTheme),
                                         { ColorTheme.fromDocument(it) }),
                                   // Current Color Theme
                                   split(doc.maybeAt("current_color_theme"),
                                         effValue(defaultCurrentColorTheme),
                                         { ColorTheme.fromDocument(it) })
                                   )
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = PointsWidgetFormat(defaultWidgetFormat,
                                           defaultLimitTextFormat,
                                           defaultCurrentTextFormat,
                                           defaultLimitColorTheme,
                                           defaultCurrentColorTheme)

    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun widgetFormat() : WidgetFormat = this.widgetFormat.value

    fun limitTextFormat() : TextFormat = this.limitTextFormat.value

    fun currentTextFormat() : TextFormat = this.currentTextFormat.value

    fun limitColorTheme() : ColorTheme = this.limitColorTheme.value

    fun currentColorTheme() : ColorTheme = this.currentColorTheme.value


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name : String = "points_widget_format"

    override val modelObject = this

}
