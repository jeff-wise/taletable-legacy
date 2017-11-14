
package com.kispoko.tome.model.sheet.widget


import android.content.Context
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.activity.sheet.dialog.openNumberVariableEditorDialog
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.db.DB_WidgetPointsBarFormat
import com.kispoko.tome.db.DB_WidgetPointsFormat
import com.kispoko.tome.db.dbWidgetPointsBarFormat
import com.kispoko.tome.db.dbWidgetPointsFormat
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Val
import com.kispoko.tome.lib.model.ProdType
import com.kispoko.tome.lib.orm.sql.*
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
                              val widgetFormat : WidgetFormat,
                              val limitTextFormat : TextFormat,
                              val currentTextFormat : TextFormat,
                              val labelTextFormat : TextFormat,
                              val barFormat : PointsBarFormat)
                               : ToDocument, ProdType, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(widgetFormat : WidgetFormat,
                limitTextFormat : TextFormat,
                currentTextFormat : TextFormat,
                labelTextFormat : TextFormat,
                barFormat : PointsBarFormat)
        : this(UUID.randomUUID(),
               widgetFormat,
               limitTextFormat,
               currentTextFormat,
               labelTextFormat,
               barFormat)


    companion object : Factory<PointsWidgetFormat>
    {

        private fun defaultWidgetFormat()      = WidgetFormat.default()
        private fun defaultLimitTextFormat()   = TextFormat.default()
        private fun defaultCurrentTextFormat() = TextFormat.default()
        private fun defaultLabelTextFormat()   = TextFormat.default()
        private fun defaultBarFormat()         = PointsBarFormat.default()


        override fun fromDocument(doc: SchemaDoc): ValueParser<PointsWidgetFormat> = when (doc)
        {
            is DocDict ->
            {
                apply(::PointsWidgetFormat,
                      // Widget Format
                      split(doc.maybeAt("widget_format"),
                            effValue(defaultWidgetFormat()),
                            { WidgetFormat.fromDocument(it) }),
                      // Limit Text Format
                      split(doc.maybeAt("limit_text_format"),
                            effValue(defaultLimitTextFormat()),
                            { TextFormat.fromDocument(it) }),
                      // Current Text Format
                      split(doc.maybeAt("current_text_format"),
                            effValue(defaultCurrentTextFormat()),
                            { TextFormat.fromDocument(it) }),
                      // Label Text Format
                      split(doc.maybeAt("label_text_format"),
                            effValue(defaultLabelTextFormat()),
                            { TextFormat.fromDocument(it) }),
                      // Bar Format
                      split(doc.maybeAt("bar_format"),
                            effValue(defaultBarFormat()),
                            { PointsBarFormat.fromDocument(it) })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = PointsWidgetFormat(defaultWidgetFormat(),
                                           defaultLimitTextFormat(),
                                           defaultCurrentTextFormat(),
                                           defaultLabelTextFormat(),
                                           defaultBarFormat())

    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "widget_format" to this.widgetFormat.toDocument(),
        "limit_text_format" to this.limitTextFormat.toDocument(),
        "current_text_format" to this.currentTextFormat.toDocument(),
        "label_text_format" to this.labelTextFormat.toDocument(),
        "bar_format" to this.barFormat.toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun widgetFormat() : WidgetFormat = this.widgetFormat

    fun limitTextFormat() : TextFormat = this.limitTextFormat

    fun currentTextFormat() : TextFormat = this.currentTextFormat

    fun labelTextFormat() : TextFormat = this.labelTextFormat

    fun barFormat() : PointsBarFormat  = this.barFormat


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun row() : DB_WidgetPointsFormat =
            dbWidgetPointsFormat(this.widgetFormat,
                                 this.limitTextFormat,
                                 this.currentTextFormat,
                                 this.labelTextFormat,
                                 this.barFormat)

}


/**
 * Points Widget Label
 */
data class PointsWidgetLabel(val value : String) : ToDocument, SQLSerializable, Serializable
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
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocText(this.value)


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({ this.value })

}


/**
 * Points Bar Height
 */
data class PointsBarHeight(val value : Int) : ToDocument, SQLSerializable, Serializable
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
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocNumber(this.value.toDouble())


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() = this.value.asSQLValue()

}


/**
 * Points Bar Style
 */
sealed class PointsBarStyle : ToDocument, SQLSerializable, Serializable
{

    object Simple : PointsBarStyle()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "simple" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("simple")

    }


    object OppositeLabels : PointsBarStyle()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "opposite_labels" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("opposite_labels")

    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

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
sealed class PointsAboveBarStyle : ToDocument, SQLSerializable, Serializable
{

    object LimitLabelMaxRight : PointsAboveBarStyle()
    {
        override fun asSQLValue() : SQLValue = SQLText({ "limit_label_max_right" })

        override fun toDocument() = DocText("limit_label_max_right")
    }


    object LabelLeftSlashRight : PointsAboveBarStyle()
    {
        override fun asSQLValue() : SQLValue = SQLText({ "label_left_slash_right" })

        override fun toDocument() = DocText("label_left_slash_right")
    }


    companion object
    {
        fun fromDocument(doc : SchemaDoc) : ValueParser<PointsAboveBarStyle> = when (doc)
        {
            is DocText -> when (doc.text)
            {
                "limit_label_max_right" -> effValue<ValueError,PointsAboveBarStyle>(
                                             PointsAboveBarStyle.LimitLabelMaxRight)
                "label_left_slash_right" -> effValue<ValueError,PointsAboveBarStyle>(
                                                PointsAboveBarStyle.LabelLeftSlashRight)
                else                  -> effError<ValueError,PointsAboveBarStyle>(
                                             UnexpectedValue("PointsAboveBarStyle", doc.text, doc.path))
            }
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }

}


// Bar Format


//val limitColorTheme : ColorTheme,
//                              val currentColorTheme : ColorTheme,
//                              val pointsBarStyle : PointsBarStyle,
//                              val pointsAboveBarStyle : PointsAboveBarStyle,
//                              val barHeight : PointsBarHeight)



/**
 * Bar Format
 */
data class PointsBarFormat(override val id : UUID,
                           val barStyle : PointsBarStyle,
                           val barAboveStyle : PointsAboveBarStyle,
                           val barHeight : PointsBarHeight,
                           val limitColorTheme : ColorTheme,
                           val currentColorTheme : ColorTheme)
                            : ToDocument, ProdType, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(barStyle : PointsBarStyle,
                barAboveStyle : PointsAboveBarStyle,
                barHeight : PointsBarHeight,
                limitColorTheme : ColorTheme,
                currentColorTheme : ColorTheme)
        : this(UUID.randomUUID(),
               barStyle,
               barAboveStyle,
               barHeight,
               limitColorTheme,
               currentColorTheme)


    companion object : Factory<PointsBarFormat>
    {

        private fun defaultBarStyle()           = PointsBarStyle.Simple
        private fun defaultBarAboveStyle()      = PointsAboveBarStyle.LimitLabelMaxRight
        private fun defaultBarHeight()          = PointsBarHeight(8)
        private fun defaultLimitColorTheme()    = ColorTheme.black
        private fun defaultCurrentColorTheme()  = ColorTheme.white


        override fun fromDocument(doc : SchemaDoc) : ValueParser<PointsBarFormat> = when (doc)
        {
            is DocDict ->
            {
                apply(::PointsBarFormat,
                      // Bar Style
                      split(doc.maybeAt("bar_style"),
                            effValue<ValueError,PointsBarStyle>(defaultBarStyle()),
                            { PointsBarStyle.fromDocument(it) }),
                      // Above Bar Style
                      split(doc.maybeAt("bar_above_style"),
                            effValue<ValueError,PointsAboveBarStyle>(defaultBarAboveStyle()),
                            { PointsAboveBarStyle.fromDocument(it) }),
                      // Bar Height
                      split(doc.maybeAt("bar_height"),
                            effValue(defaultBarHeight()),
                            { PointsBarHeight.fromDocument(it) }),
                      // Limit Color Theme
                      split(doc.maybeAt("limit_color_theme"),
                            effValue(defaultLimitColorTheme()),
                            { ColorTheme.fromDocument(it) }),
                      // Current Color Theme
                      split(doc.maybeAt("current_color_theme"),
                            effValue(defaultCurrentColorTheme()),
                            { ColorTheme.fromDocument(it) })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = PointsBarFormat(defaultBarStyle(),
                                        defaultBarAboveStyle(),
                                        defaultBarHeight(),
                                        defaultLimitColorTheme(),
                                        defaultCurrentColorTheme())

    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "bar_style" to this.barStyle().toDocument(),
        "above_bar_style" to this.aboveBarStyle().toDocument(),
        "bar_height" to this.barHeight().toDocument(),
        "limit_color_theme" to this.limitColorTheme().toDocument(),
        "current_color_theme" to this.currentColorTheme().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------


    fun barStyle() : PointsBarStyle = this.barStyle

    fun aboveBarStyle() : PointsAboveBarStyle = this.barAboveStyle

    fun barHeight() : PointsBarHeight = this.barHeight

    fun limitColorTheme() : ColorTheme = this.limitColorTheme

    fun currentColorTheme() : ColorTheme = this.currentColorTheme


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun row() : DB_WidgetPointsBarFormat =
            dbWidgetPointsBarFormat(this.barStyle,
                                    this.barAboveStyle,
                                    this.barHeight,
                                    this.limitColorTheme,
                                    this.currentColorTheme)

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
                is effect.Val ->
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

        when (pointsWidget.format().barFormat().aboveBarStyle())
        {
            is PointsAboveBarStyle.LimitLabelMaxRight ->
            {
                if (currentPointsString != null) {
                    layout.addView(this.currentPointsView(currentPointsString,
                                                          pointsWidget.format().currentTextFormat(),
                                                          sheetUIContext))
                }

                val label = pointsWidget.label()
                when (label) {
                    is Just -> {
                        val labelView = this.labelView(label.value.value,
                                                  pointsWidget.format().labelTextFormat(),
                                                  sheetUIContext)
                        val layoutParams = labelView.layoutParams as RelativeLayout.LayoutParams
                        layoutParams.addRule(RelativeLayout.RIGHT_OF, R.id.points_bar_current)
                        labelView.layoutParams = layoutParams
                        layout.addView(labelView)
                    }
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
        val layout = this.standardViewLayout(pointsWidget.format().barFormat().barHeight().value,
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
                                            pointsWidget.format().barFormat().currentColorTheme())

        // (3 B) Limit
        // -------------------------------------------------------------------------------------

        limit.width                 = 0
        limit.height                = LinearLayout.LayoutParams.MATCH_PARENT
        limit.weight                = limitWeight

        limit.backgroundColor       = SheetManager.color(sheetUIContext.sheetId,
                                                         pointsWidget.format().barFormat().limitColorTheme())

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
                                                         textFormat.colorTheme())

        current.sizeSp              = textFormat.sizeSp()

        current.font                = Font.typeface(textFormat.font(),
                                                    textFormat.fontStyle(),
                                                    sheetUIContext.context)

        current.paddingSpacing      = textFormat.elementFormat().padding()
        current.marginSpacing       = textFormat.elementFormat().margins()

//        current.addRule(RelativeLayout.ALIGN_PARENT_START)
        Log.d("***POINTSWIDGET", "current vert align: ${textFormat.elementFormat().verticalAlignment()}")
        current.addRule(textFormat.elementFormat().verticalAlignment().relativeLayoutRule())

//        current.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)

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
                                                         textFormat.colorTheme())

        limit.sizeSp                = textFormat.sizeSp()

        limit.font                  = Font.typeface(textFormat.font(),
                                                    textFormat.fontStyle(),
                                                    sheetUIContext.context)

        limit.paddingSpacing        = textFormat.elementFormat().padding()
        limit.marginSpacing         = textFormat.elementFormat().margins()

//        limit.addRule(RelativeLayout.ALIGN_PARENT_END)

        Log.d("***POINTSWIDGET", "label vert align: ${textFormat.elementFormat().verticalAlignment()}")
        limit.addRule(textFormat.elementFormat().verticalAlignment().relativeLayoutRule())
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
                                                         textFormat.colorTheme())

        label.sizeSp                = textFormat.sizeSp()

        label.font                  = Font.typeface(textFormat.font(),
                                                    textFormat.fontStyle(),
                                                    sheetUIContext.context)

        label.paddingSpacing        = textFormat.elementFormat().padding()
        label.marginSpacing         = textFormat.elementFormat().margins()
//
//        label.addRule(RelativeLayout.ALIGN_PARENT_END)
        Log.d("***POINTSWIDGET", "label vert align: ${textFormat.elementFormat().verticalAlignment()}")
        label.addRule(textFormat.elementFormat().verticalAlignment().relativeLayoutRule())

//        label.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)

        return label.textView(sheetUIContext.context)
    }


}
