
package com.kispoko.tome.model.sheet.widget


import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.activity.sheet.dialog.openNumberVariableEditorDialog
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.db.*
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.ProdType
import com.kispoko.tome.lib.orm.RowValue7
import com.kispoko.tome.lib.orm.RowValue8
import com.kispoko.tome.lib.orm.schema.MaybePrimValue
import com.kispoko.tome.lib.orm.schema.PrimValue
import com.kispoko.tome.lib.orm.schema.ProdValue
import com.kispoko.tome.lib.orm.sql.*
import com.kispoko.tome.lib.ui.*
import com.kispoko.tome.model.sheet.style.*
import com.kispoko.tome.rts.entity.sheet.SheetContext
import com.kispoko.tome.rts.entity.sheet.SheetManager
import com.kispoko.tome.rts.entity.sheet.SheetUIContext
import com.kispoko.tome.rts.entity.sheet.UpdateTargetPointsWidget
import com.kispoko.tome.util.Util
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.UnexpectedValue
import lulo.value.ValueError
import lulo.value.ValueParser
import maybe.Just
import maybe.Maybe
import maybe.Nothing
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
                              val infoStyle : PointsInfoStyle,
                              val infoFormat : TextFormat,
                              val barFormat : PointsWidgetBarFormat)
                               : ToDocument, ProdType, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(widgetFormat : WidgetFormat,
                limitTextFormat : TextFormat,
                currentTextFormat : TextFormat,
                labelTextFormat : TextFormat,
                infoStyle : PointsInfoStyle,
                infoFormat : TextFormat,
                barFormat : PointsWidgetBarFormat)
        : this(UUID.randomUUID(),
               widgetFormat,
               limitTextFormat,
               currentTextFormat,
               labelTextFormat,
               infoStyle,
               infoFormat,
               barFormat)


    companion object : Factory<PointsWidgetFormat>
    {

        private fun defaultWidgetFormat()      = WidgetFormat.default()
        private fun defaultLimitTextFormat()   = TextFormat.default()
        private fun defaultCurrentTextFormat() = TextFormat.default()
        private fun defaultLabelTextFormat()   = TextFormat.default()
        private fun defaultInfoStyle()         = PointsInfoStyle.CenterLabelRight
        private fun defaultInfoFormat()        = TextFormat.default()
        private fun defaultBarFormat()         = PointsWidgetBarFormat.default()


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
                      // Info Style
                      split(doc.maybeAt("info_style"),
                            effValue<ValueError,PointsInfoStyle>(defaultInfoStyle()),
                            { PointsInfoStyle.fromDocument(it) }),
                      // Info Format
                      split(doc.maybeAt("info_format"),
                            effValue(defaultInfoFormat()),
                            { TextFormat.fromDocument(it) }),
                      // Bar Format
                      split(doc.maybeAt("bar_format"),
                            effValue(defaultBarFormat()),
                            { PointsWidgetBarFormat.fromDocument(it) })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = PointsWidgetFormat(defaultWidgetFormat(),
                                           defaultLimitTextFormat(),
                                           defaultCurrentTextFormat(),
                                           defaultLabelTextFormat(),
                                           defaultInfoStyle(),
                                           defaultInfoFormat(),
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
        "info_style" to this.infoStyle.toDocument(),
        "info_format" to this.infoFormat.toDocument(),
        "bar_format" to this.barFormat.toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun widgetFormat() : WidgetFormat = this.widgetFormat


    fun limitTextFormat() : TextFormat = this.limitTextFormat


    fun currentTextFormat() : TextFormat = this.currentTextFormat


    fun labelTextFormat() : TextFormat = this.labelTextFormat


    fun infoStyle() : PointsInfoStyle = this.infoStyle


    fun infoFormat() : TextFormat = this.infoFormat


    fun barFormat() : PointsWidgetBarFormat = this.barFormat


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_WidgetPointsFormatValue =
        RowValue7(widgetPointsFormatTable,
                  ProdValue(this.widgetFormat),
                  ProdValue(this.limitTextFormat),
                  ProdValue(this.currentTextFormat),
                  ProdValue(this.labelTextFormat),
                  PrimValue(this.infoStyle),
                  ProdValue(this.infoFormat),
                  ProdValue(this.barFormat))

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


    object Counter : PointsBarStyle()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "counter" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("counter")

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
                "counter"         -> effValue<ValueError,PointsBarStyle>(PointsBarStyle.Counter)
                else              -> effError<ValueError,PointsBarStyle>(
                                    UnexpectedValue("PointsBarStyle", doc.text, doc.path))
            }
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }

}


/**
 * Points Info Style
 */
sealed class PointsInfoStyle : ToDocument, SQLSerializable, Serializable
{

    object LimitLabelMaxRight : PointsInfoStyle()
    {
        override fun asSQLValue() : SQLValue = SQLText({ "limit_label_max_right" })

        override fun toDocument() = DocText("limit_label_max_right")
    }


    object LabelLeftSlashRight : PointsInfoStyle()
    {
        override fun asSQLValue() : SQLValue = SQLText({ "label_left_slash_right" })

        override fun toDocument() = DocText("label_left_slash_right")
    }


    object CenterLabelRight : PointsInfoStyle()
    {
        override fun asSQLValue() : SQLValue = SQLText({ "center_label_right" })

        override fun toDocument() = DocText("center_label_right")
    }


    companion object
    {
        fun fromDocument(doc : SchemaDoc) : ValueParser<PointsInfoStyle> = when (doc)
        {
            is DocText -> when (doc.text)
            {
                "limit_label_max_right"  -> effValue<ValueError, PointsInfoStyle>(
                                                PointsInfoStyle.LimitLabelMaxRight)
                "label_left_slash_right" -> effValue<ValueError, PointsInfoStyle>(
                                                PointsInfoStyle.LabelLeftSlashRight)
                "center_label_right"     -> effValue<ValueError, PointsInfoStyle>(
                                                PointsInfoStyle.CenterLabelRight)
                else                  -> effError<ValueError, PointsInfoStyle>(
                                             UnexpectedValue("PointsInfoStyle", doc.text, doc.path))
            }
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }

}


/**
 * Points Widget Counter Active Text
 */
data class PointsWidgetCounterActiveText(val value : String)
                : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<PointsWidgetCounterActiveText>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<PointsWidgetCounterActiveText> = when (doc)
        {
            is DocText -> effValue(PointsWidgetCounterActiveText(doc.text))
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
 * Bar Format
 */
data class PointsWidgetBarFormat(override val id : UUID,
                                 val elementFormat : ElementFormat,
                                 val barStyle : PointsBarStyle,
                                 val barHeight : PointsBarHeight,
                                 val limitFormat : TextFormat,
                                 val currentFormat : TextFormat,
                                 val counterActiveIcon : Maybe<IconType>,
                                 val counterActiveText : Maybe<PointsWidgetCounterActiveText>,
                                 val counterInactiveText : Maybe<PointsWidgetCounterActiveText>)
                                  : ToDocument, ProdType, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(elementFormat : ElementFormat,
                barStyle : PointsBarStyle,
                barHeight : PointsBarHeight,
                limitFormat : TextFormat,
                currentFormat : TextFormat,
                counterActiveIcon : Maybe<IconType>,
                counterActiveText : Maybe<PointsWidgetCounterActiveText>,
                counterInactiveText : Maybe<PointsWidgetCounterActiveText>)
        : this(UUID.randomUUID(),
               elementFormat,
               barStyle,
               barHeight,
               limitFormat,
               currentFormat,
               counterActiveIcon,
               counterActiveText,
               counterInactiveText)


    companion object : Factory<PointsWidgetBarFormat>
    {

        private fun defaultElementFormat()  = ElementFormat.default()
        private fun defaultBarStyle()       = PointsBarStyle.Simple
        private fun defaultBarHeight()      = PointsBarHeight(8)
        private fun defaultLimitFormat()    = TextFormat.default()
        private fun defaultCurrentFormat()  = TextFormat.default()


        override fun fromDocument(doc : SchemaDoc) : ValueParser<PointsWidgetBarFormat> = when (doc)
        {
            is DocDict ->
            {
                apply(::PointsWidgetBarFormat,
                      // Element Format
                      split(doc.maybeAt("element_format"),
                            effValue(defaultElementFormat()),
                            { ElementFormat.fromDocument(it) }),
                      // Bar Style
                      split(doc.maybeAt("bar_style"),
                            effValue<ValueError,PointsBarStyle>(defaultBarStyle()),
                            { PointsBarStyle.fromDocument(it) }),
                      // Bar Height
                      split(doc.maybeAt("bar_height"),
                            effValue(defaultBarHeight()),
                            { PointsBarHeight.fromDocument(it) }),
                      // Limit Format
                      split(doc.maybeAt("limit_format"),
                            effValue(defaultLimitFormat()),
                            { TextFormat.fromDocument(it) }),
                      // Current Format
                      split(doc.maybeAt("current_format"),
                            effValue(defaultCurrentFormat()),
                            { TextFormat.fromDocument(it) }),
                      // Counter Active Icon
                      split(doc.maybeAt("counter_active_icon"),
                            effValue<ValueError,Maybe<IconType>>(Nothing()),
                            { apply(::Just, IconType.fromDocument(it)) }),
                      // Counter Active Text
                      split(doc.maybeAt("counter_active_text"),
                            effValue<ValueError,Maybe<PointsWidgetCounterActiveText>>(Nothing()),
                            { apply(::Just, PointsWidgetCounterActiveText.fromDocument(it)) }),
                      // Counter Inactive Text
                      split(doc.maybeAt("counter_inactive_text"),
                            effValue<ValueError,Maybe<PointsWidgetCounterActiveText>>(Nothing()),
                            { apply(::Just, PointsWidgetCounterActiveText.fromDocument(it)) })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = PointsWidgetBarFormat(defaultElementFormat(),
                                        defaultBarStyle(),
                                        defaultBarHeight(),
                                        defaultLimitFormat(),
                                        defaultCurrentFormat(),
                                        Nothing(),
                                        Nothing(),
                                        Nothing())

    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "element_format" to this.elementFormat().toDocument(),
        "bar_style" to this.barStyle().toDocument(),
        "bar_height" to this.barHeight().toDocument(),
        "limit_format" to this.limitFormat().toDocument(),
        "current_format" to this.currentFormat().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun elementFormat() : ElementFormat = this.elementFormat


    fun barStyle() : PointsBarStyle = this.barStyle


    fun barHeight() : PointsBarHeight = this.barHeight


    fun limitFormat() : TextFormat = this.limitFormat


    fun currentFormat() : TextFormat = this.currentFormat


    fun counterActiveText() : Maybe<PointsWidgetCounterActiveText> = this.counterActiveText


    fun counterInactiveText() : Maybe<PointsWidgetCounterActiveText> = this.counterInactiveText


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_WidgetPointsBarFormatValue =
        RowValue8(widgetPointsBarFormatTable,
                  ProdValue(this.elementFormat),
                  PrimValue(this.barStyle),
                  PrimValue(this.barHeight),
                  ProdValue(this.limitFormat),
                  ProdValue(this.currentFormat),
                  MaybePrimValue(this.counterActiveIcon),
                  MaybePrimValue(this.counterActiveText),
                  MaybePrimValue(this.counterInactiveText))

}



class PointsWidgetViewBuilder(val pointsWidget : PointsWidget,
                              val sheetUIContext : SheetUIContext)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    val sheetContext = SheetContext(sheetUIContext)


    // -----------------------------------------------------------------------------------------
    // VIEWS
    // -----------------------------------------------------------------------------------------

    fun view() : View
    {
        val layout = WidgetView.layout(pointsWidget.widgetFormat(), sheetUIContext)

        val layoutViewId = Util.generateViewId()
        pointsWidget.layoutViewId = layoutViewId
        layout.id = layoutViewId

        updateView(layout)

        return layout
    }


    fun updateView(layout : LinearLayout)
    {
        val contentLayout = layout.findViewById(R.id.widget_content_layout) as LinearLayout

        contentLayout.removeAllViews()

        // Above Bar
        val infoPosition = pointsWidget.format().infoFormat().elementFormat().position()
        when (infoPosition)
        {
            is Position.Top -> {
                contentLayout.orientation = LinearLayout.VERTICAL
                contentLayout.addView(this.infoView())
            }
            is Position.Left -> {
                contentLayout.orientation = LinearLayout.HORIZONTAL
                contentLayout.addView(this.infoView())
            }
        }

        // Bar
        when (pointsWidget.format().barFormat().barStyle)
        {
            is PointsBarStyle.Counter ->
            {
                contentLayout.addView(this.counterBarView())
            }
            else ->
            {
                contentLayout.addView(this.barView())

                contentLayout.setOnClickListener {
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
            }
        }


    }


    // -----------------------------------------------------------------------------------------
    // ABOVE BAR VIEW
    // -----------------------------------------------------------------------------------------

    fun infoView() : View
    {
        var layout : ViewGroup = this.infoViewLayout(sheetUIContext)

        val currentPointsString = pointsWidget.currentValueString(SheetContext(sheetUIContext))
        val limitPointsString = pointsWidget.limitValueString(SheetContext(sheetUIContext))

        when (pointsWidget.format().infoStyle())
        {
            is PointsInfoStyle.LimitLabelMaxRight ->
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
            is PointsInfoStyle.LabelLeftSlashRight ->
            {
                layout = this.infoViewLinearLayout(pointsWidget.format().infoFormat())
                val infoContentLayout = layout.findViewById(R.id.content) as LinearLayout

                val border = pointsWidget.format().infoFormat().elementFormat().border()

                if (currentPointsString != null) {
                    infoContentLayout.addView(this.currentPointsView(currentPointsString,
                                                          pointsWidget.format().currentTextFormat(),
                                                          sheetUIContext))
                }

                val slashView = this.slashTextView(pointsWidget.format().limitTextFormat(), sheetUIContext)
                infoContentLayout.addView(slashView)


                if (limitPointsString != null) {
                    val limitPointsView = this.limitPointsView(
                                                        limitPointsString,
                                                        pointsWidget.format().limitTextFormat(),
                                                        sheetUIContext)
                    val layoutParams = limitPointsView.layoutParams as RelativeLayout.LayoutParams
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END)
                    limitPointsView.layoutParams = layoutParams
                    infoContentLayout.addView(limitPointsView)
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
                        infoContentLayout.addView(labelView)
                    }
                }

//                val rightBorder = border.right()
//                when (rightBorder) {
//                    is Just -> {
//                        layout.addView(infoViewVerticalBorder(rightBorder.value))
//                    }
//                }

            }
            is PointsInfoStyle.CenterLabelRight ->
            {
                layout = this.infoViewLinearLayout(pointsWidget.format().infoFormat())

                if (currentPointsString != null) {
                    val currView = this.currentPointsLinearView(currentPointsString,
                                                          pointsWidget.format().currentTextFormat(),
                                                          sheetUIContext)
                    layout.addView(currView)
                }

                val slashView = this.slashTextView(pointsWidget.format().limitTextFormat(), sheetUIContext)
                layout.addView(slashView)

                if (limitPointsString != null) {
                    val limitPointsView = this.limitPointsLinearView(
                                                        limitPointsString,
                                                        pointsWidget.format().limitTextFormat(),
                                                        sheetUIContext)
                    layout.addView(limitPointsView)
                }

                val label = pointsWidget.label()
                when (label) {
                    is Just -> {
                        val labelView = this.labelLinearView(label.value.value,
                                                  pointsWidget.format().labelTextFormat(),
                                                  sheetUIContext)
                        layout.addView(labelView)
                    }
                }

            }
        }

        return layout
    }


    private fun infoViewLayout(sheetUIContext : SheetUIContext) : RelativeLayout
    {
        val layout              = RelativeLayoutBuilder()

        layout.id               = R.id.points_above_bar_layout

        layout.layoutType       = LayoutType.LINEAR
        layout.weight           = 1f
        layout.height           = 0

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT

        layout.gravity          = Gravity.TOP

        return layout.relativeLayout(sheetUIContext.context)
    }


    private fun infoViewVerticalBorder(border : BorderEdge) : LinearLayout
    {
        val layout              = LinearLayoutBuilder()


        return layout.linearLayout(sheetUIContext.context)
    }


    private fun infoViewLinearLayout(format : TextFormat) : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout              = LinearLayoutBuilder()
        val contentLayout       = LinearLayoutBuilder()
        val borderView          = LinearLayoutBuilder()

        // (2) Main Layout
        // -------------------------------------------------------------------------------------

        when (format.elementFormat().position())
        {
            is Position.Top -> {
                layout.weight           = 1f
                layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
                layout.height           = 0
            }
            is Position.Bottom -> {
                layout.weight           = 1f
                layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
                layout.height           = 0
            }
            is Position.Left -> {
                layout.width            = LinearLayout.LayoutParams.WRAP_CONTENT
                layout.height           = LinearLayout.LayoutParams.MATCH_PARENT
            }
            is Position.Right -> {
                layout.width            = LinearLayout.LayoutParams.WRAP_CONTENT
                layout.height           = LinearLayout.LayoutParams.MATCH_PARENT
            }
        }

        layout.marginSpacing            = format.elementFormat().margins()

        layout.child(contentLayout)

        // (3) Content Layout
        // -------------------------------------------------------------------------------------

        contentLayout.id        = R.id.content

        contentLayout.width     = LinearLayout.LayoutParams.MATCH_PARENT
        contentLayout.height    = LinearLayout.LayoutParams.MATCH_PARENT

        contentLayout.gravity   = format.elementFormat().alignment().gravityConstant() or
                                    format.elementFormat().verticalAlignment().gravityConstant()

        contentLayout.paddingSpacing   = format.elementFormat().padding()

        // (4) Border
        // -------------------------------------------------------------------------------------

        val rightBorder = format.elementFormat().border().right()
        when (rightBorder) {
            is Just -> {
                borderView.widthDp          = rightBorder.value.thickness().value
                borderView.height           = LinearLayout.LayoutParams.MATCH_PARENT

                borderView.backgroundColor  = SheetManager.color(sheetUIContext.sheetId, rightBorder.value.colorTheme())

                layout.child(borderView)
            }
        }

        return layout.linearLayout(sheetUIContext.context)
    }



    // -----------------------------------------------------------------------------------------
    // BAR VIEW
    // -----------------------------------------------------------------------------------------

    fun barView() : View
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


    private fun standardViewLayout(barHeight : Int, context : Context) : LinearLayout
    {
        val layout = LinearLayoutBuilder()

        layout.layoutType           = LayoutType.LINEAR
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.heightDp             = barHeight

        return layout.linearLayout(context)
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
        var currentValue    = pointsWidget.currentValue(SheetContext(sheetUIContext))


        var currentWeight   = 1f
        var limitWeight     = 1f

        if (limitValue != null && currentValue != null && limitValue > 0)
        {
            if (currentValue > limitValue)
                currentValue = limitValue

            val ratio = (currentValue / limitValue) * 100
            currentWeight = ratio.toFloat()
            limitWeight   = (100.0 - ratio).toFloat()
        }

        // (2) Layout
        // -------------------------------------------------------------------------------------

//        layout.layoutType           = LayoutType.RELATIVE
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT

        layout.orientation          = LinearLayout.HORIZONTAL

        layout.child(current)
              .child(limit)

        // (3 A) Current
        // -------------------------------------------------------------------------------------

        current.width               = 0
        current.height              = LinearLayout.LayoutParams.MATCH_PARENT
        current.weight              = currentWeight

        current.backgroundColor     = SheetManager.color(sheetUIContext.sheetId,
                                            pointsWidget.format().barFormat().currentFormat().colorTheme())

        // (3 B) Limit
        // -------------------------------------------------------------------------------------

        limit.width                 = 0
        limit.height                = LinearLayout.LayoutParams.MATCH_PARENT
        limit.weight                = limitWeight

        limit.backgroundColor       = SheetManager.color(sheetUIContext.sheetId,
                                                         pointsWidget.format().barFormat().limitFormat().colorTheme())

        return layout.linearLayout(sheetUIContext.context)
    }


    private fun counterBarView() : LinearLayout
    {
        val layout          = this.counterBarViewLayout()

        val limitValue      = pointsWidget.limitValue(SheetContext(sheetUIContext))?.toInt()
        var currentValue    = pointsWidget.currentValue(SheetContext(sheetUIContext))?.toInt()

        if (limitValue != null && currentValue != null && limitValue > 0)
        {
            for (i in 1..limitValue) {
                if (i <= currentValue) {
                    layout.addView(this.counterBarActiveView(i))
                }
                else {
                    layout.addView(this.counterBarInactiveView(i))
                }
            }
        }

        return layout
    }


    private fun counterBarViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.paddingSpacing   = pointsWidget.format().barFormat().elementFormat().padding()
        layout.marginSpacing   = pointsWidget.format().barFormat().elementFormat().margins()

        return layout.linearLayout(sheetUIContext.context)
    }


    private fun counterBarActiveView(index : Int) : LinearLayout
    {
        val layout              = LinearLayoutBuilder()
        val format              = pointsWidget.format().barFormat().currentFormat()

        val width = format.elementFormat().width()
        when (width) {
            is Width.Fixed -> layout.widthDp = width.value.toInt()
            else           -> layout.width = LinearLayout.LayoutParams.WRAP_CONTENT
        }

        val height = format.elementFormat().height()
        when (height) {
            is Height.Fixed -> layout.heightDp = height.value.toInt()
            else            -> layout.height = LinearLayout.LayoutParams.WRAP_CONTENT
        }

        layout.backgroundColor  = SheetManager.color(sheetUIContext.sheetId,
                                                     format.elementFormat().backgroundColorTheme())

        layout.corners          = format.elementFormat().corners()

        layout.gravity          = Gravity.CENTER

        layout.paddingSpacing   = format.elementFormat().padding()
        layout.marginSpacing    = format.elementFormat().margins()

        val activeIcon = pointsWidget.format().barFormat().counterActiveIcon
        when (activeIcon) {
            is Just -> {
                val icon            = ImageViewBuilder()

                icon.widthDp        = format.iconFormat().size().width
                icon.heightDp       = format.iconFormat().size().height

                icon.image          = activeIcon.value.drawableResId()

                layout.child(icon)
            }
        }


        val activeText = pointsWidget.format().barFormat().counterActiveText()
        when (activeText) {
            is Just -> {
                val textView      = TextViewBuilder()

                textView.width        = LinearLayout.LayoutParams.WRAP_CONTENT
                textView.height       = LinearLayout.LayoutParams.WRAP_CONTENT

                textView.text         = activeText.value.value

                pointsWidget.format().barFormat().currentFormat().styleTextViewBuilder(textView, sheetUIContext)

                layout.child(textView)
            }
        }


        layout.onClick = View.OnClickListener {
            pointsWidget.currentValueVariable(sheetContext) apDo { valueVariable ->
            valueVariable.valueOrError(sheetContext)        apDo { value ->
                if (value.toInt() != index) {
                    valueVariable.updateValue(index.toDouble(), sheetContext)
                }
                else {
                    valueVariable.updateValue((index - 1).toDouble(), sheetContext)
                }
            } }
        }

        return layout.linearLayout(sheetUIContext.context)
    }


    private fun counterBarInactiveView(index : Int) : LinearLayout
    {
        val layout              = LinearLayoutBuilder()
        val format              = pointsWidget.format().barFormat().limitFormat()

//        layout.width            = LinearLayout.LayoutParams.WRAP_CONTENT
//        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT
//
        val width = format.elementFormat().width()
        when (width) {
            is Width.Fixed -> layout.widthDp = width.value.toInt()
            else           -> layout.width = LinearLayout.LayoutParams.WRAP_CONTENT
        }

        val height = format.elementFormat().height()
        when (height) {
            is Height.Fixed -> layout.heightDp = height.value.toInt()
            else            -> layout.height = LinearLayout.LayoutParams.WRAP_CONTENT
        }

        layout.backgroundColor  = SheetManager.color(sheetUIContext.sheetId,
                                                     format.elementFormat().backgroundColorTheme())

        layout.corners          = format.elementFormat().corners()

        layout.paddingSpacing   = format.elementFormat().padding()
        layout.marginSpacing    = format.elementFormat().margins()

        layout.gravity          = format.elementFormat().alignment().gravityConstant() or
                                    format.elementFormat().verticalAlignment().gravityConstant()

        val activeIcon = pointsWidget.format().barFormat().counterActiveIcon
        when (activeIcon) {
            is Just -> {
                val icon            = ImageViewBuilder()

                icon.widthDp        = format.iconFormat().size().width
                icon.heightDp       = format.iconFormat().size().height

                icon.image          = activeIcon.value.drawableResId()

                icon.color          = SheetManager.color(sheetUIContext.sheetId,
                                                         format.iconFormat().colorTheme())

                layout.child(icon)
            }
        }

        val inactiveText = pointsWidget.format().barFormat().counterInactiveText()
        when (inactiveText) {
            is Just -> {
                val textView      = TextViewBuilder()

                textView.width        = LinearLayout.LayoutParams.WRAP_CONTENT
                textView.height       = LinearLayout.LayoutParams.WRAP_CONTENT

                textView.text         = inactiveText.value.value

                pointsWidget.format().barFormat().limitFormat().styleTextViewBuilder(textView, sheetUIContext)

                layout.child(textView)
            }
        }

        layout.onClick = View.OnClickListener {
            pointsWidget.currentValueVariable(sheetContext) apDo { valueVariable ->
            valueVariable.valueOrError(sheetContext)        apDo { value ->
                if (value.toInt() != index) {
                    valueVariable.updateValue(index.toDouble(), sheetContext)
                }
            } }
        }


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

//        current.gravity             = Gravity.BOTTOM

//        current.addRule(RelativeLayout.ALIGN_PARENT_START)
//        current.addRule(textFormat.elementFormat().verticalAlignment().relativeLayoutRule())

//        current.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
//        current.addRule(RelativeLayout.ALIGN_BASELINE)

        return current.textView(sheetUIContext.context)
    }



    private fun limitPointsView(limitString : String,
                                textFormat : TextFormat,
                                sheetUIContext : SheetUIContext) : TextView
    {
        val limit                   = TextViewBuilder()

        limit.id                    = R.id.points_bar_limit

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

//        limit.addRule(textFormat.elementFormat().verticalAlignment().relativeLayoutRule())
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
//        label.addRule(textFormat.elementFormat().verticalAlignment().relativeLayoutRule())

//        label.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)

        return label.textView(sheetUIContext.context)
    }



    // -----------------------------------------------------------------------------------------
    // SHARED LINEAR VIEWS
    // -----------------------------------------------------------------------------------------

    private fun currentPointsLinearView(currentString : String,
                                        textFormat : TextFormat,
                                        sheetUIContext : SheetUIContext) : TextView
    {
        val current                 = TextViewBuilder()

        current.layoutType          = LayoutType.LINEAR
        current.width               = LinearLayout.LayoutParams.WRAP_CONTENT
        current.height              = LinearLayout.LayoutParams.WRAP_CONTENT

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

//        current.layoutGravity       = textFormat.elementFormat().alignment().gravityConstant() or
//                                        textFormat.elementFormat().verticalAlignment().gravityConstant()

//        current.addRule(RelativeLayout.ALIGN_PARENT_START)
//        current.addRule(textFormat.elementFormat().verticalAlignment().relativeLayoutRule())

//        current.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
//        current.addRule(RelativeLayout.ALIGN_BASELINE)

        return current.textView(sheetUIContext.context)
    }



    private fun limitPointsLinearView(limitString : String,
                                      textFormat : TextFormat,
                                      sheetUIContext : SheetUIContext) : TextView
    {
        val limit                   = TextViewBuilder()

        limit.id                    = R.id.points_bar_limit

        limit.layoutType            = LayoutType.LINEAR

        limit.width                 = LinearLayout.LayoutParams.WRAP_CONTENT
        limit.height                = LinearLayout.LayoutParams.WRAP_CONTENT

        limit.text                  = limitString

        limit.color                 = SheetManager.color(sheetUIContext.sheetId,
                                                         textFormat.colorTheme())

        limit.sizeSp                = textFormat.sizeSp()

        limit.font                  = Font.typeface(textFormat.font(),
                                                    textFormat.fontStyle(),
                                                    sheetUIContext.context)

        limit.paddingSpacing        = textFormat.elementFormat().padding()
        limit.marginSpacing         = textFormat.elementFormat().margins()

//        limit.layoutGravity       = textFormat.elementFormat().alignment().gravityConstant() or
//                                    textFormat.elementFormat().verticalAlignment().gravityConstant()


        return limit.textView(sheetUIContext.context)
    }



    private fun labelLinearView(labelString : String,
                                textFormat : TextFormat,
                                sheetUIContext : SheetUIContext) : TextView
    {
        val label                   = TextViewBuilder()

        label.layoutType            = LayoutType.LINEAR

        label.width                 = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height                = LinearLayout.LayoutParams.WRAP_CONTENT

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

//        label.layoutGravity       = textFormat.elementFormat().alignment().gravityConstant() or
//                                        textFormat.elementFormat().verticalAlignment().gravityConstant()

//        label.layoutGravity         = Gravity.BOTTOM
//
//        label.gravity       = textFormat.elementFormat().alignment().gravityConstant() or
//                                textFormat.elementFormat().verticalAlignment().gravityConstant()

        return label.textView(sheetUIContext.context)
    }



    private fun slashTextView(textFormat : TextFormat,
                              sheetUIContext : SheetUIContext) : TextView
    {
        val current                 = TextViewBuilder()

        current.layoutType          = LayoutType.LINEAR

        current.id                  = R.id.points_bar_slash

        current.width               = LinearLayout.LayoutParams.WRAP_CONTENT
        current.height              = LinearLayout.LayoutParams.WRAP_CONTENT

        current.text                = "/"

        current.color               = SheetManager.color(sheetUIContext.sheetId,
                                                         textFormat.colorTheme())

        current.sizeSp              = textFormat.sizeSp()

        current.font                = Font.typeface(textFormat.font(),
                                                    textFormat.fontStyle(),
                                                    sheetUIContext.context)

        current.paddingSpacing      = textFormat.elementFormat().padding()
        current.marginSpacing       = textFormat.elementFormat().margins()

//        current.layoutGravity       = textFormat.elementFormat().alignment().gravityConstant() or
//                textFormat.elementFormat().verticalAlignment().gravityConstant()


//        current.addRule(RelativeLayout.ALIGN_PARENT_START)
//        current.addRule(textFormat.elementFormat().verticalAlignment().relativeLayoutRule())

//        current.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)

        return current.textView(sheetUIContext.context)
    }

}
