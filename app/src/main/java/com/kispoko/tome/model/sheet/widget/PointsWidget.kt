
package com.kispoko.tome.model.sheet.widget


import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.activity.sheet.dialog.openNumberVariableEditorDialog
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Comp
import com.kispoko.tome.lib.functor.Prim
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.lib.orm.sql.SQLInt
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.lib.ui.*
import com.kispoko.tome.model.sheet.style.NumericEditorType
import com.kispoko.tome.model.sheet.style.TextFormat
import com.kispoko.tome.model.theme.ColorTheme
import com.kispoko.tome.rts.sheet.SheetContext
import com.kispoko.tome.rts.sheet.SheetManager
import com.kispoko.tome.rts.sheet.SheetUIContext
import com.kispoko.tome.rts.sheet.UpdateTargetPointsWidget
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
 * Points Widget Format
 */
data class PointsWidgetFormat(override val id : UUID,
                              val widgetFormat : Comp<WidgetFormat>,
                              val limitTextFormat : Comp<TextFormat>,
                              val currentTextFormat : Comp<TextFormat>,
                              val labelTextFormat : Comp<TextFormat>,
                              val limitColorTheme : Prim<ColorTheme>,
                              val currentColorTheme : Prim<ColorTheme>,
                              val pointsBarStyle : Prim<PointsBarStyle>,
                              val pointsAboveBarStyle : Prim<PointsAboveBarStyle>,
                              val barHeight : Prim<PointsBarHeight>)
                               : Model, Serializable
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.widgetFormat.name          = "widget_format"
        this.limitTextFormat.name       = "limit_text_format"
        this.currentTextFormat.name     = "current_text_format"
        this.labelTextFormat.name       = "label_text_format"
        this.limitColorTheme.name       = "limit_color_theme"
        this.currentColorTheme.name     = "current_color_theme"
        this.pointsBarStyle.name        = "points_bar_style"
        this.pointsAboveBarStyle.name   = "points_bar_style"
        this.barHeight.name             = "bar_height"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(widgetFormat : WidgetFormat,
                limitTextFormat : TextFormat,
                currentTextFormat : TextFormat,
                labelTextFormat : TextFormat,
                limitColorTheme : ColorTheme,
                currentColorTheme : ColorTheme,
                pointsBarStyle : PointsBarStyle,
                pointsAboveBarStyle : PointsAboveBarStyle,
                barHeight : PointsBarHeight)
        : this(UUID.randomUUID(),
               Comp(widgetFormat),
               Comp(limitTextFormat),
               Comp(currentTextFormat),
               Comp(labelTextFormat),
               Prim(limitColorTheme),
               Prim(currentColorTheme),
               Prim(pointsBarStyle),
               Prim(pointsAboveBarStyle),
               Prim(barHeight))


    companion object : Factory<PointsWidgetFormat>
    {

        val defaultWidgetFormat         = WidgetFormat.default()
        val defaultLimitTextFormat      = TextFormat.default()
        val defaultCurrentTextFormat    = TextFormat.default()
        val defaultLabelTextFormat      = TextFormat.default()
        val defaultLimitColorTheme      = ColorTheme.black
        val defaultCurrentColorTheme    = ColorTheme.white
        val defaultPointsBarStyle       = PointsBarStyle.Simple
        val defaultPointsAboveBarStyle  = PointsAboveBarStyle.OppositeLeftLabel
        val defaultBarHeight            = PointsBarHeight(20)


        override fun fromDocument(doc: SchemaDoc): ValueParser<PointsWidgetFormat> = when (doc)
        {
            is DocDict ->
            {
                effApply(::PointsWidgetFormat,
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
                         // Label Text Format
                         split(doc.maybeAt("label_text_format"),
                               effValue(defaultLabelTextFormat),
                               { TextFormat.fromDocument(it) }),
                         // Limit Color Theme
                         split(doc.maybeAt("limit_color_theme"),
                               effValue(defaultLimitColorTheme),
                               { ColorTheme.fromDocument(it) }),
                         // Current Color Theme
                         split(doc.maybeAt("current_color_theme"),
                               effValue(defaultCurrentColorTheme),
                               { ColorTheme.fromDocument(it) }),
                         // Bar Style
                         split(doc.maybeAt("bar_style"),
                               effValue<ValueError,PointsBarStyle>(defaultPointsBarStyle),
                               { PointsBarStyle.fromDocument(it) }),
                         // Bar Style
                         split(doc.maybeAt("above_bar_style"),
                               effValue<ValueError,PointsAboveBarStyle>(defaultPointsAboveBarStyle),
                               { PointsAboveBarStyle.fromDocument(it) }),
                         // Bar Height
                         split(doc.maybeAt("bar_height"),
                               effValue(defaultBarHeight),
                               { PointsBarHeight.fromDocument(it) })
                         )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = PointsWidgetFormat(defaultWidgetFormat,
                                           defaultLimitTextFormat,
                                           defaultCurrentTextFormat,
                                           defaultLabelTextFormat,
                                           defaultLimitColorTheme,
                                           defaultCurrentColorTheme,
                                           defaultPointsBarStyle,
                                           defaultPointsAboveBarStyle,
                                           defaultBarHeight)

    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun widgetFormat() : WidgetFormat = this.widgetFormat.value

    fun limitTextFormat() : TextFormat = this.limitTextFormat.value

    fun currentTextFormat() : TextFormat = this.currentTextFormat.value

    fun labelTextFormat() : TextFormat = this.labelTextFormat.value

    fun limitColorTheme() : ColorTheme = this.limitColorTheme.value

    fun currentColorTheme() : ColorTheme = this.currentColorTheme.value

    fun barStyle() : PointsBarStyle = this.pointsBarStyle.value

    fun aboveBarStyle() : PointsAboveBarStyle = this.pointsAboveBarStyle.value

    fun barHeight() : Int  = this.barHeight.value.value


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name : String = "points_widget_format"

    override val modelObject = this

}


/**
 * Points Widget Label
 */
data class PointsWidgetLabel(val value : String) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<PointsWidgetLabel>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<PointsWidgetLabel> = when (doc)
        {
            is DocText -> effValue(PointsWidgetLabel(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({ this.value })

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
        override fun fromDocument(doc: SchemaDoc): ValueParser<PointsBarHeight> = when (doc)
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


/**
 * Points Bar Style
 */
sealed class PointsBarStyle : SQLSerializable, Serializable
{

    object Simple : PointsBarStyle()
    {
        override fun asSQLValue() : SQLValue = SQLText({ "simple" })
    }


    object OppositeLabels : PointsBarStyle()
    {
        override fun asSQLValue() : SQLValue = SQLText({ "opposite_labels" })
    }


    companion object
    {
        fun fromDocument(doc : SchemaDoc) : ValueParser<PointsBarStyle> = when (doc)
        {
            is DocText -> when (doc.text)
            {
                "simple"          -> effValue<ValueError,PointsBarStyle>(PointsBarStyle.Simple)
                "opposite_labels" -> effValue<ValueError,PointsBarStyle>(
                                        PointsBarStyle.OppositeLabels)
                else              -> effError<ValueError,PointsBarStyle>(
                                    UnexpectedValue("PointsBarStyle", doc.text, doc.path))
            }
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }

}


/**
 * Points Above Bar Style
 */
sealed class PointsAboveBarStyle : SQLSerializable, Serializable
{

    object OppositeLeftLabel : PointsAboveBarStyle()
    {
        override fun asSQLValue() : SQLValue = SQLText({ "opposite_left_label" })
    }


    companion object
    {
        fun fromDocument(doc : SchemaDoc) : ValueParser<PointsAboveBarStyle> = when (doc)
        {
            is DocText -> when (doc.text)
            {
                "opposite_left_label" -> effValue<ValueError,PointsAboveBarStyle>(
                                             PointsAboveBarStyle.OppositeLeftLabel)
                else                  -> effError<ValueError,PointsAboveBarStyle>(
                                             UnexpectedValue("PointsAboveBarStyle", doc.text, doc.path))
            }
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }

}


object PointsWidgetView
{



    fun view(pointsWidget : PointsWidget, sheetUIContext : SheetUIContext) : View
    {
        val layout = WidgetView.layout(pointsWidget.widgetFormat(), sheetUIContext)

        val layoutViewId = Util.generateViewId()
        pointsWidget.layoutViewId = layoutViewId
        layout.id = layoutViewId

        // Above Bar
        layout.addView(this.aboveBarView(pointsWidget, sheetUIContext))

        // Bar
        layout.addView(this.barView(pointsWidget, sheetUIContext))

        layout.setOnClickListener {
            val currentValueVariable =
                    pointsWidget.currentValueVariable(SheetContext(sheetUIContext))

            when (currentValueVariable)
            {
                is Val ->
                {
                    openNumberVariableEditorDialog(currentValueVariable.value,
                                                   NumericEditorType.Adder,
                                                   UpdateTargetPointsWidget(pointsWidget.id),
                                                   sheetUIContext)
                }
                is Err -> ApplicationLog.error(currentValueVariable.error)
            }
        }

        return layout
    }


    // -----------------------------------------------------------------------------------------
    // ABOVE BAR VIEW
    // -----------------------------------------------------------------------------------------

    fun aboveBarView(pointsWidget : PointsWidget, sheetUIContext : SheetUIContext) : View
    {
        val layout = this.aboveBarLayout(sheetUIContext)

        val currentPointsString = pointsWidget.currentValueString(SheetContext(sheetUIContext))
        val limitPointsString = pointsWidget.limitValueString(SheetContext(sheetUIContext))

        when (pointsWidget.format().aboveBarStyle())
        {
            is PointsAboveBarStyle.OppositeLeftLabel ->
            {
                if (currentPointsString != null) {
                    layout.addView(this.currentPointsView(currentPointsString,
                                                          pointsWidget.format().currentTextFormat(),
                                                          sheetUIContext))
                }

                val label = pointsWidget.label()
                if (label != null) {
                    val labelView = this.labelView(label,
                                                  pointsWidget.format().labelTextFormat(),
                                                  sheetUIContext)
                    val layoutParams = labelView.layoutParams as RelativeLayout.LayoutParams
                    layoutParams.addRule(RelativeLayout.RIGHT_OF, R.id.points_bar_current)
                    labelView.layoutParams = layoutParams
                    layout.addView(labelView)
                }

                if (limitPointsString != null) {
                    val limitPointsView = this.limitPointsView(
                                                        limitPointsString,
                                                        pointsWidget.format().limitTextFormat(),
                                                        sheetUIContext)
                    val layoutParams = limitPointsView.layoutParams as RelativeLayout.LayoutParams
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END)
                    limitPointsView.layoutParams = layoutParams
                    layout.addView(limitPointsView)
                }
            }
        }

        return layout
    }


    private fun aboveBarLayout(sheetUIContext : SheetUIContext) : RelativeLayout
    {
        val layout              = RelativeLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.padding.bottomDp = 4f

        return layout.relativeLayout(sheetUIContext.context)
    }


    // -----------------------------------------------------------------------------------------
    // BAR VIEW
    // -----------------------------------------------------------------------------------------

    fun barView(pointsWidget : PointsWidget, sheetUIContext : SheetUIContext) : View
    {
        val layout = this.standardViewLayout(pointsWidget.format().barHeight(),
                                             sheetUIContext.context)

        layout.addView(this.standardBarView(pointsWidget, sheetUIContext))
//
//        val currentPointsString = pointsWidget.currentValueString(SheetContext(sheetUIContext))
//        layout.addView(this.currentPointsView(currentPointsString ?: "",
//                                                      pointsWidget.format().currentTextFormat(),
//                                                      sheetUIContext))
//
//        val limitPointsString = pointsWidget.limitValueString(SheetContext(sheetUIContext))
//        layout.addView(this.limitPointsView(limitPointsString ?: "",
//                                                    pointsWidget.format().limitTextFormat(),
//                                                    sheetUIContext))

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



    // -----------------------------------------------------------------------------------------
    // SHARED VIEWS
    // -----------------------------------------------------------------------------------------

    private fun currentPointsView(currentString : String,
                                  textFormat : TextFormat,
                                  sheetUIContext : SheetUIContext) : TextView
    {
        val current                 = TextViewBuilder()

        current.layoutType          = LayoutType.RELATIVE

        current.width               = RelativeLayout.LayoutParams.WRAP_CONTENT
        current.height              = RelativeLayout.LayoutParams.WRAP_CONTENT

        current.id                  = R.id.points_bar_current

        current.text                = currentString

        current.color               = SheetManager.color(sheetUIContext.sheetId,
                                                         textFormat.style().colorTheme())

        current.sizeSp              = textFormat.style().sizeSp()

        current.font                = Font.typeface(textFormat.style().font(),
                                                    textFormat.style().fontStyle(),
                                                    sheetUIContext.context)

        current.paddingSpacing      = textFormat.padding()
        current.marginSpacing       = textFormat.margins()

//        current.addRule(RelativeLayout.ALIGN_PARENT_START)
//        current.addRule(textFormat.verticalAlignment().relativeLayoutRule())

        current.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)

        return current.textView(sheetUIContext.context)
    }


    private fun limitPointsView(limitString : String,
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

//        limit.addRule(RelativeLayout.ALIGN_PARENT_END)
        limit.addRule(textFormat.verticalAlignment().relativeLayoutRule())
//        limit.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)

        return limit.textView(sheetUIContext.context)
    }


    private fun labelView(labelString : String,
                          textFormat : TextFormat,
                          sheetUIContext : SheetUIContext) : TextView
    {
        val label                   = TextViewBuilder()

        label.layoutType            = LayoutType.RELATIVE

        label.width                 = RelativeLayout.LayoutParams.WRAP_CONTENT
        label.height                = RelativeLayout.LayoutParams.WRAP_CONTENT

        label.id                    = R.id.points_bar_label

        label.text                  = labelString

        label.color                 = SheetManager.color(sheetUIContext.sheetId,
                                                         textFormat.style().colorTheme())

        label.sizeSp                = textFormat.style().sizeSp()

        label.font                  = Font.typeface(textFormat.style().font(),
                                                    textFormat.style().fontStyle(),
                                                    sheetUIContext.context)

        label.paddingSpacing        = textFormat.padding()
        label.marginSpacing         = textFormat.margins()
//
//        label.addRule(RelativeLayout.ALIGN_PARENT_END)
        label.addRule(textFormat.verticalAlignment().relativeLayoutRule())

//        label.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)

        return label.textView(sheetUIContext.context)
    }


}
