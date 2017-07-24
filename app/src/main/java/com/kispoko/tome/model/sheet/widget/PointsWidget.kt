
package com.kispoko.tome.model.sheet.widget


import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Comp
import com.kispoko.tome.lib.functor.Prim
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.lib.orm.sql.SQLInt
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.lib.ui.*
import com.kispoko.tome.model.sheet.style.TextFormat
import com.kispoko.tome.model.theme.ColorTheme
import com.kispoko.tome.rts.sheet.SheetContext
import com.kispoko.tome.rts.sheet.SheetManager
import com.kispoko.tome.rts.sheet.SheetUIContext
import effect.effApply
import effect.effError
import effect.effValue
import effect.split
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import java.io.Serializable
import java.util.*



/**
 * Points Widget Format
 */
data class PointsWidgetFormat(override val id : UUID,
                              val widgetFormat : Comp<WidgetFormat>,
                              val limitTextFormat : Comp<TextFormat>,
                              val currentTextFormat : Comp<TextFormat>,
                              val limitColorTheme : Prim<ColorTheme>,
                              val currentColorTheme : Prim<ColorTheme>,
                              val barHeight : Prim<PointsBarHeight>)
                               : Model, Serializable
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
        this.barHeight.name         = "bar_height"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(widgetFormat : WidgetFormat,
                limitTextFormat : TextFormat,
                currentTextFormat : TextFormat,
                limitColorTheme : ColorTheme,
                currentColorTheme : ColorTheme,
                barHeight : PointsBarHeight)
        : this(UUID.randomUUID(),
               Comp(widgetFormat),
               Comp(limitTextFormat),
               Comp(currentTextFormat),
               Prim(limitColorTheme),
               Prim(currentColorTheme),
               Prim(barHeight))


    companion object : Factory<PointsWidgetFormat>
    {

        val defaultWidgetFormat         = WidgetFormat.default()
        val defaultLimitTextFormat      = TextFormat.default()
        val defaultCurrentTextFormat    = TextFormat.default()
        val defaultLimitColorTheme      = ColorTheme.black
        val defaultCurrentColorTheme    = ColorTheme.white
        val defaultBarHeight            = PointsBarHeight(20)


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
                                         { ColorTheme.fromDocument(it) }),
                                   // Bar Height
                                   split(doc.maybeAt("bar_height"),
                                         effValue(defaultBarHeight),
                                         { PointsBarHeight.fromDocument(it) })
                                   )
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = PointsWidgetFormat(defaultWidgetFormat,
                                           defaultLimitTextFormat,
                                           defaultCurrentTextFormat,
                                           defaultLimitColorTheme,
                                           defaultCurrentColorTheme,
                                           defaultBarHeight)

    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun widgetFormat() : WidgetFormat = this.widgetFormat.value

    fun limitTextFormat() : TextFormat = this.limitTextFormat.value

    fun currentTextFormat() : TextFormat = this.currentTextFormat.value

    fun limitColorTheme() : ColorTheme = this.limitColorTheme.value

    fun currentColorTheme() : ColorTheme = this.currentColorTheme.value

    fun barHeight() : Int  = this.barHeight.value.value


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name : String = "points_widget_format"

    override val modelObject = this

}


/**
 * Points Bar Height
 */
data class PointsBarHeight(val value : Int) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<PointsBarHeight>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<PointsBarHeight> = when (doc)
        {
            is DocNumber -> effValue(PointsBarHeight(doc.number.toInt()))
            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLInt( {this.value} )

}


object PointsWidgetView
{



    fun view(pointsWidget : PointsWidget, sheetUIContext : SheetUIContext) : View
    {
        val layout = WidgetView.layout(pointsWidget.widgetFormat(), sheetUIContext)

        layout.addView(this.standardView(pointsWidget, sheetUIContext))

        return layout
    }


    // -----------------------------------------------------------------------------------------
    // STANDARD VIEW
    // -----------------------------------------------------------------------------------------

    private fun standardView(pointsWidget : PointsWidget,
                             sheetUIContext : SheetUIContext) : View
    {
        val layout = this.standardViewLayout(pointsWidget.format().barHeight(),
                                             sheetUIContext.context)

        layout.addView(this.standardBarView(pointsWidget, sheetUIContext))

        val currentPointsString = pointsWidget.currentValueString(SheetContext(sheetUIContext))
        layout.addView(this.standardCurrentPointsView(currentPointsString ?: "",
                                                      pointsWidget.format().currentTextFormat(),
                                                      sheetUIContext))

        val limitPointsString = pointsWidget.limitValueString(SheetContext(sheetUIContext))
        layout.addView(this.standardLimitPointsView(limitPointsString ?: "",
                                                    pointsWidget.format().limitTextFormat(),
                                                    sheetUIContext))

        return layout
    }


    private fun standardViewLayout(barHeight : Int, context : Context) : RelativeLayout
    {
        val layout = RelativeLayoutBuilder()

        layout.layoutType           = LayoutType.LINEAR
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.heightDp             = barHeight

        return layout.relativeLayout(context)
    }


    private fun standardBarView(pointsWidget : PointsWidget,
                                sheetUIContext : SheetUIContext) : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout          = LinearLayoutBuilder()
        val current         = LinearLayoutBuilder()
        val limit           = LinearLayoutBuilder()

        val limitValue      = pointsWidget.limitValue(SheetContext(sheetUIContext))
        val currentValue    = pointsWidget.currentValue(SheetContext(sheetUIContext))

        var currentWeight   = 1f
        var limitWeight     = 1f

        if (limitValue != null && currentValue != null &&
            currentValue <= limitValue && limitValue > 0)
        {
            val ratio = (currentValue / limitValue) * 100
            currentWeight = ratio.toFloat()
            limitWeight   = (100.0 - ratio).toFloat()
        }

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.layoutType           = LayoutType.RELATIVE
        layout.width                = RelativeLayout.LayoutParams.MATCH_PARENT
        layout.height               = RelativeLayout.LayoutParams.MATCH_PARENT

        layout.orientation          = LinearLayout.HORIZONTAL

        layout.child(current)
              .child(limit)

        // (3 A) Current
        // -------------------------------------------------------------------------------------

        current.width               = 0
        current.height              = LinearLayout.LayoutParams.MATCH_PARENT
        current.weight              = currentWeight

        current.backgroundColor     = SheetManager.color(sheetUIContext.sheetId,
                                            pointsWidget.format().currentColorTheme())

        // (3 B) Limit
        // -------------------------------------------------------------------------------------

        limit.width                 = 0
        limit.height                = LinearLayout.LayoutParams.MATCH_PARENT
        limit.weight                = limitWeight

        limit.backgroundColor       = SheetManager.color(sheetUIContext.sheetId,
                                                         pointsWidget.format().limitColorTheme())

        return layout.linearLayout(sheetUIContext.context)
    }


    private fun standardCurrentPointsView(currentString : String,
                                          textFormat : TextFormat,
                                          sheetUIContext : SheetUIContext) : TextView
    {
        val current                 = TextViewBuilder()

        current.layoutType          = LayoutType.RELATIVE

        current.width               = RelativeLayout.LayoutParams.WRAP_CONTENT
        current.height              = RelativeLayout.LayoutParams.WRAP_CONTENT

        current.text                = currentString

        current.color               = SheetManager.color(sheetUIContext.sheetId,
                                                         textFormat.style().colorTheme())

        current.sizeSp              = textFormat.style().sizeSp()

        current.font                = Font.typeface(textFormat.style().font(),
                                                    textFormat.style().fontStyle(),
                                                    sheetUIContext.context)

        current.paddingSpacing      = textFormat.padding()
        current.marginSpacing       = textFormat.margins()

        current.addRule(RelativeLayout.ALIGN_PARENT_START)
        current.addRule(textFormat.verticalAlignment().relativeLayoutRule())

        return current.textView(sheetUIContext.context)
    }


    private fun standardLimitPointsView(limitString : String,
                                        textFormat : TextFormat,
                                        sheetUIContext : SheetUIContext) : TextView
    {
        val limit                   = TextViewBuilder()

        limit.layoutType            = LayoutType.RELATIVE

        limit.width                 = RelativeLayout.LayoutParams.WRAP_CONTENT
        limit.height                = RelativeLayout.LayoutParams.WRAP_CONTENT

        limit.text                  = limitString

        limit.color                 = SheetManager.color(sheetUIContext.sheetId,
                                                         textFormat.style().colorTheme())

        limit.sizeSp                = textFormat.style().sizeSp()

        limit.font                  = Font.typeface(textFormat.style().font(),
                                                    textFormat.style().fontStyle(),
                                                    sheetUIContext.context)

        limit.paddingSpacing        = textFormat.padding()
        limit.marginSpacing         = textFormat.margins()

        limit.addRule(RelativeLayout.ALIGN_PARENT_END)
        limit.addRule(textFormat.verticalAlignment().relativeLayoutRule())

        return limit.textView(sheetUIContext.context)
    }


}
