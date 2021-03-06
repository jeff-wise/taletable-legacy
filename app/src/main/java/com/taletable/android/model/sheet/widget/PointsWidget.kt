
package com.taletable.android.model.sheet.widget


import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.taletable.android.R
import com.taletable.android.activity.sheet.dialog.openNumberVariableEditorDialog
import com.taletable.android.app.ApplicationLog
import com.taletable.android.lib.Factory
import com.taletable.android.lib.orm.sql.*
import com.taletable.android.lib.ui.*
import com.taletable.android.model.sheet.style.*
import com.taletable.android.model.theme.ColorId
import com.taletable.android.model.theme.ColorTheme
import com.taletable.android.model.theme.ThemeColorId
import com.taletable.android.model.theme.ThemeId
import com.taletable.android.rts.entity.EntityId
import com.taletable.android.rts.entity.colorOrBlack
import com.taletable.android.rts.entity.sheet.UpdateTargetPointsWidget
import com.taletable.android.util.Util
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



/**
 * Points Widget Format
 */
data class PointsWidgetFormat(val widgetFormat : WidgetFormat,
                              val limitTextFormat : TextFormat,
                              val currentTextFormat : TextFormat,
                              val labelTextFormat : TextFormat,
                              val infoFormat : PointsWidgetInfoFormat,
                              val barFormat : PointsWidgetBarFormat)
                               : ToDocument, Serializable
{

    // | Constructors
    // -----------------------------------------------------------------------------------------

    companion object : Factory<PointsWidgetFormat>
    {

        private fun defaultWidgetFormat()      = WidgetFormat.default()
        private fun defaultLimitTextFormat()   = TextFormat.default()
        private fun defaultCurrentTextFormat() = TextFormat.default()
        private fun defaultLabelTextFormat()   = TextFormat.default()
        private fun defaultInfoFormat()        = PointsWidgetInfoFormat.default()
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
                      // Info Format
                      split(doc.maybeAt("info_format"),
                            effValue(defaultInfoFormat()),
                            { PointsWidgetInfoFormat.fromDocument(it) }),
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


    fun infoFormat() : PointsWidgetInfoFormat = this.infoFormat


    fun barFormat() : PointsWidgetBarFormat = this.barFormat

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
sealed class PointsBarStyle : ToDocument, Serializable
{

    object None : PointsBarStyle()
    {
        override fun toDocument() = DocText("none")
    }

    object Simple : PointsBarStyle()
    {
        override fun toDocument() = DocText("simple")
    }

    object OppositeLabels : PointsBarStyle()
    {
        override fun toDocument() = DocText("opposite_labels")
    }

    object Counter : PointsBarStyle()
    {
        override fun toDocument() = DocText("counter")
    }

    object SetCounter : PointsBarStyle()
    {
        override fun toDocument() = DocText("set_counter")
    }

    object Levels : PointsBarStyle()
    {
        override fun toDocument() = DocText("levels")
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
                "none"            -> effValue<ValueError,PointsBarStyle>(PointsBarStyle.None)
                "simple"          -> effValue<ValueError,PointsBarStyle>(PointsBarStyle.Simple)
                "opposite_labels" -> effValue<ValueError,PointsBarStyle>(
                                        PointsBarStyle.OppositeLabels)
                "counter"         -> effValue<ValueError,PointsBarStyle>(PointsBarStyle.Counter)
                "set_counter"     -> effValue<ValueError,PointsBarStyle>(PointsBarStyle.SetCounter)
                "levels"          -> effValue<ValueError,PointsBarStyle>(PointsBarStyle.Levels)
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
sealed class PointsInfoStyle : ToDocument, Serializable
{

    object CurrentSlashLimit : PointsInfoStyle()
    {
        override fun toDocument() = DocText("current_slash_limit")
    }

    object LimitLabelMaxRight : PointsInfoStyle()
    {
        override fun toDocument() = DocText("limit_label_max_right")
    }


    object LabelLeftSlashRight : PointsInfoStyle()
    {
        override fun toDocument() = DocText("label_left_slash_right")
    }


    object CenterLabelRight : PointsInfoStyle()
    {
        override fun toDocument() = DocText("center_label_right")
    }


    object LabelTopSlashBottom : PointsInfoStyle()
    {
        override fun toDocument() = DocText("label_top_slash_bottom")
    }


    object LabelOnly : PointsInfoStyle()
    {
        override fun toDocument() = DocText("label_only")
    }

    object None : PointsInfoStyle()
    {
        override fun toDocument() = DocText("none")
    }


    companion object
    {
        fun fromDocument(doc : SchemaDoc) : ValueParser<PointsInfoStyle> = when (doc)
        {
            is DocText -> when (doc.text)
            {
                "current_slash_limit"    -> effValue<ValueError, PointsInfoStyle>(
                                                PointsInfoStyle.CurrentSlashLimit)
                "limit_label_max_right"  -> effValue<ValueError, PointsInfoStyle>(
                                                PointsInfoStyle.LimitLabelMaxRight)
                "label_left_slash_right" -> effValue<ValueError, PointsInfoStyle>(
                                                PointsInfoStyle.LabelLeftSlashRight)
                "center_label_right"     -> effValue<ValueError, PointsInfoStyle>(
                                                PointsInfoStyle.CenterLabelRight)
                "label_top_slash_bottom" -> effValue<ValueError, PointsInfoStyle>(
                                                PointsInfoStyle.LabelTopSlashBottom)
                "label_only"             -> effValue<ValueError, PointsInfoStyle>(
                                                PointsInfoStyle.LabelOnly)
                "none"                   -> effValue<ValueError, PointsInfoStyle>(PointsInfoStyle.None)
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
 * Points Widget Info Format
 */
data class PointsWidgetInfoFormat(val style : PointsInfoStyle,
                                  val format : TextFormat)
                                  : ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<PointsWidgetInfoFormat>
    {

        private fun defaultStyle()  = PointsInfoStyle.LabelLeftSlashRight
        private fun defaultFormat() = TextFormat.default()


        override fun fromDocument(doc : SchemaDoc) : ValueParser<PointsWidgetInfoFormat> = when (doc)
        {
            is DocDict ->
            {
                apply(::PointsWidgetInfoFormat,
                      // Style
                      split(doc.maybeAt("style"),
                            effValue<ValueError,PointsInfoStyle>(defaultStyle()),
                            { PointsInfoStyle.fromDocument(it) }),
                      // Format
                      split(doc.maybeAt("format"),
                            effValue(defaultFormat()),
                            { TextFormat.fromDocument(it) })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = PointsWidgetInfoFormat(defaultStyle(),
                                               defaultFormat())

    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "style" to this.style.toDocument(),
        "format" to this.format.toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun style() : PointsInfoStyle = this.style


    fun format() : TextFormat = this.format

}


/**
 * Bar Format
 */
data class PointsWidgetBarFormat(val elementFormat : ElementFormat,
                                 val barStyle : PointsBarStyle,
                                 val barHeight : PointsBarHeight,
                                 val limitFormat : TextFormat,
                                 val currentFormat : TextFormat,
                                 val levels : List<String>,
                                 val counterActiveIcon : Maybe<IconType>,
                                 val counterActiveText : Maybe<PointsWidgetCounterActiveText>,
                                 val counterInactiveText : Maybe<PointsWidgetCounterActiveText>)
                                  : ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // | Constructors
    // -----------------------------------------------------------------------------------------

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
                      // Levels
                      split(doc.maybeList("levels"),
                            effValue(listOf()),
                            { it.stringList() }),
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
                                        listOf(),
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


    fun levels() : List<String> = this.levels


    fun counterActiveText() : Maybe<PointsWidgetCounterActiveText> = this.counterActiveText


    fun counterInactiveText() : Maybe<PointsWidgetCounterActiveText> = this.counterInactiveText

}



class PointsWidgetViewBuilder(val pointsWidget : PointsWidget,
                              val entityId : EntityId,
                              val context : Context)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------


    // -----------------------------------------------------------------------------------------
    // VIEWS
    // -----------------------------------------------------------------------------------------

    fun view() : View
    {
        val layout = WidgetView.layout(pointsWidget.widgetFormat(), entityId, context)

        val layoutViewId = Util.generateViewId()
        pointsWidget.layoutViewId = layoutViewId
        layout.id = layoutViewId

        updateView(layout)

        return layout
    }


    fun updateView(layout : LinearLayout)
    {
        val contentLayout = layout.findViewById<LinearLayout>(R.id.widget_content_layout)

        contentLayout.removeAllViews()

        // Above Bar
        val infoPosition = pointsWidget.format().infoFormat().format().elementFormat().position()
        when (infoPosition)
        {
            is Position.Top -> {
                contentLayout.orientation = LinearLayout.VERTICAL
                contentLayout.addView(this.infoView())
            }
            is Position.Left -> {
                contentLayout.orientation = LinearLayout.HORIZONTAL
                contentLayout.addView(this.infoView())

                contentLayout.gravity   = Gravity.CENTER_VERTICAL
            }
        }

        // Bar
        when (pointsWidget.format().barFormat().barStyle)
        {
            is PointsBarStyle.Counter ->
            {
                contentLayout.addView(this.counterBarView())
            }
            is PointsBarStyle.SetCounter ->
            {
                contentLayout.addView(this.counterBarView())
            }
            is PointsBarStyle.Levels ->
            {
                contentLayout.addView(this.levelsBarView())
            }
            is PointsBarStyle.None ->
            {
            }
            else ->
            {
                contentLayout.addView(this.barView())

                contentLayout.setOnClickListener {
                    val currentValueVariable =
                            pointsWidget.currentValueVariable(entityId)

                    when (currentValueVariable)
                    {
                        is effect.Val ->
                        {
                            openNumberVariableEditorDialog(currentValueVariable.value,
                                                           NumericEditorType.Adder,
                                                           UpdateTargetPointsWidget(pointsWidget.widgetId()),
                                                           entityId,
                                                           context)
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
        var layout : ViewGroup = this.infoViewLayout(pointsWidget.format().infoFormat().format())

        val currentPointsString = pointsWidget.currentValueString(entityId)
        val limitPointsString = pointsWidget.limitValueString(entityId)

        when (pointsWidget.format().infoFormat().style())
        {
            is PointsInfoStyle.LabelOnly ->
            {
                layout = this.infoViewLinearLayout(pointsWidget.format().infoFormat().format())
                val infoContentLayout = layout.findViewById<LinearLayout>(R.id.content)

                val label = pointsWidget.label()
                when (label) {
                    is Just -> {
                        val labelView = this.labelView(label.value.value,
                                                       pointsWidget.format().labelTextFormat())
                        infoContentLayout.addView(labelView)
                    }
                }
            }
            is PointsInfoStyle.LabelTopSlashBottom ->
            {
                layout = this.infoViewLinearLayout(pointsWidget.format().infoFormat().format())
                val infoContentLayout = layout.findViewById<LinearLayout>(R.id.content)
                infoContentLayout.orientation = LinearLayout.VERTICAL

                val label = pointsWidget.label()
                when (label) {
                    is Just -> {
                        val labelView = this.labelView(label.value.value,
                                                       pointsWidget.format().labelTextFormat())
                        infoContentLayout.addView(labelView)
                    }
                }

                infoContentLayout.addView(this.pointsSlashView())
            }
            is PointsInfoStyle.LimitLabelMaxRight ->
            {
                if (currentPointsString != null) {
                    layout.addView(this.currentPointsView(currentPointsString,
                                                          pointsWidget.format().currentTextFormat()))
                }

                val label = pointsWidget.label()
                when (label) {
                    is Just -> {
                        val labelView = this.labelView(label.value.value,
                                                       pointsWidget.format().labelTextFormat())
                        val layoutParams = labelView.layoutParams as RelativeLayout.LayoutParams
                        layoutParams.addRule(RelativeLayout.RIGHT_OF, R.id.points_bar_current)
                        labelView.layoutParams = layoutParams
                        layout.addView(labelView)
                    }
                }

                if (limitPointsString != null) {
                    val limitPointsView = this.limitPointsView(
                                                        limitPointsString,
                                                        pointsWidget.format().limitTextFormat())
                    val layoutParams = limitPointsView.layoutParams as RelativeLayout.LayoutParams
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END)
                    limitPointsView.layoutParams = layoutParams
                    layout.addView(limitPointsView)
                }
            }
            is PointsInfoStyle.LabelLeftSlashRight ->
            {
                layout = this.infoViewLinearLayout(pointsWidget.format().infoFormat().format())
                val infoContentLayout = layout.findViewById<LinearLayout>(R.id.content)

                val border = pointsWidget.format().infoFormat().format().elementFormat().border()

                if (currentPointsString != null) {
                    infoContentLayout.addView(this.currentPointsView(currentPointsString,
                                                          pointsWidget.format().currentTextFormat()))
                }

                val slashView = this.slashTextView(pointsWidget.format().limitTextFormat())
                infoContentLayout.addView(slashView)


                if (limitPointsString != null) {
                    val limitPointsView = this.limitPointsView(
                                                        limitPointsString,
                                                        pointsWidget.format().limitTextFormat())
                    val layoutParams = limitPointsView.layoutParams as RelativeLayout.LayoutParams
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END)
                    limitPointsView.layoutParams = layoutParams
                    infoContentLayout.addView(limitPointsView)
                }

                val label = pointsWidget.label()
                when (label) {
                    is Just -> {
                        val labelView = this.labelView(label.value.value,
                                                       pointsWidget.format().labelTextFormat())
                        val layoutParams = labelView.layoutParams as RelativeLayout.LayoutParams
                        layoutParams.addRule(RelativeLayout.RIGHT_OF, R.id.points_bar_current)
                        labelView.layoutParams = layoutParams
                        infoContentLayout.addView(labelView)
                    }
                }

            }
            is PointsInfoStyle.CurrentSlashLimit ->
            {
                layout = this.infoViewLinearLayout(pointsWidget.format().infoFormat().format())
                val infoContentLayout = layout.findViewById<LinearLayout>(R.id.content)

//                if (currentPointsString != null && limitPointsString != null)
//                {
//                    infoContentLayout.addView(this.currentSlashLimitView(currentPointsString,
//                                                              limitPointsString,
//                                                              pointsWidget.format().infoFormat().format()))
//                    Log.d("***POINTS WIDGET", "current slash limit")
//                }


                if (currentPointsString != null) {
                    val currView = this.currentPointsLinearView(currentPointsString,
                                                          pointsWidget.format().currentTextFormat())
                    infoContentLayout.addView(currView)
                }

                val slashView = this.slashTextView(pointsWidget.format().limitTextFormat())
                infoContentLayout.addView(slashView)

                if (limitPointsString != null) {
                    val limitPointsView = this.limitPointsLinearView(
                                                        limitPointsString,
                                                        pointsWidget.format().limitTextFormat())
                    infoContentLayout.addView(limitPointsView)
                }
            }
            is PointsInfoStyle.CenterLabelRight ->
            {
                layout = this.infoViewLinearLayout(pointsWidget.format().infoFormat().format())


                if (currentPointsString != null) {
                    val currView = this.currentPointsLinearView(currentPointsString,
                                                          pointsWidget.format().currentTextFormat())
                    layout.addView(currView)
                }

                val slashView = this.slashTextView(pointsWidget.format().limitTextFormat())
                layout.addView(slashView)

                if (limitPointsString != null) {
                    val limitPointsView = this.limitPointsLinearView(
                                                        limitPointsString,
                                                        pointsWidget.format().limitTextFormat())
                    layout.addView(limitPointsView)
                }

                val label = pointsWidget.label()
                when (label) {
                    is Just -> {
                        val labelView = this.labelLinearView(label.value.value,
                                                  pointsWidget.format().labelTextFormat())
                        layout.addView(labelView)
                    }
                }

            }
        }

        return layout
    }


    private fun infoViewLayout(format : TextFormat) : RelativeLayout
    {
        val layout              = RelativeLayoutBuilder()

        layout.id               = R.id.points_above_bar_layout

        layout.layoutType       = LayoutType.LINEAR
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT

        layout.gravity          = format.elementFormat().alignment().gravityConstant() or
                                    format.elementFormat().verticalAlignment().gravityConstant()

        return layout.relativeLayout(context)
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

        if (pointsWidget.format().barFormat().barStyle() != PointsBarStyle.None)
        {
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
        }
        else
        {
            layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
            layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT
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

                borderView.backgroundColor  = colorOrBlack(rightBorder.value.colorTheme(), entityId)

                layout.child(borderView)
            }
        }

        return layout.linearLayout(context)
    }



    // -----------------------------------------------------------------------------------------
    // BAR VIEW
    // -----------------------------------------------------------------------------------------

    fun barView() : View
    {
        val layout = this.standardViewLayout(pointsWidget.format().barFormat().barHeight().value)

        layout.addView(this.standardBarView(pointsWidget))
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


    private fun standardViewLayout(barHeight : Int) : LinearLayout
    {
        val layout = LinearLayoutBuilder()

        layout.layoutType           = LayoutType.LINEAR
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.heightDp             = barHeight

        return layout.linearLayout(context)
    }


    private fun standardBarView(pointsWidget : PointsWidget) : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout          = LinearLayoutBuilder()
        val current         = LinearLayoutBuilder()
        val limit           = LinearLayoutBuilder()

        val limitValue      = pointsWidget.limitValue(entityId)
        var currentValue    = pointsWidget.currentValue(entityId)


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

        current.backgroundColor     = colorOrBlack(
                                        pointsWidget.format().barFormat().currentFormat().colorTheme(),
                                        entityId)

        // (3 B) Limit
        // -------------------------------------------------------------------------------------

        limit.width                 = 0
        limit.height                = LinearLayout.LayoutParams.MATCH_PARENT
        limit.weight                = limitWeight

        limit.backgroundColor       = colorOrBlack(
                                          pointsWidget.format().barFormat().limitFormat().colorTheme(),
                                          entityId)

        return layout.linearLayout(context)
    }


    // -----------------------------------------------------------------------------------------
    // COUNTER VIEW
    // -----------------------------------------------------------------------------------------

    private fun counterBarView() : LinearLayout
    {
        val layout          = this.counterBarViewLayout()

        val limitValue      = pointsWidget.limitValue(entityId)?.toInt()
        var currentValue    = pointsWidget.currentValue(entityId)?.toInt()

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

        return layout.linearLayout(context)
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

        layout.backgroundColor  = colorOrBlack(format.elementFormat().backgroundColorTheme(),
                                               entityId)

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

                pointsWidget.format().barFormat().currentFormat()
                            .styleTextViewBuilder(textView, entityId, context)

                layout.child(textView)
            }
        }


        layout.onClick = View.OnClickListener {
            pointsWidget.currentValueVariable(entityId) apDo { valueVariable ->
            valueVariable.valueOrError(entityId)        apDo { value ->
                if (value.toInt() != index) {
                    valueVariable.updateValue(index.toDouble(), entityId)
                }
                else {
                    valueVariable.updateValue((index - 1).toDouble(), entityId)
                }
            } }
        }

        return layout.linearLayout(context)
    }


    private fun counterBarInactiveView(index : Int) : LinearLayout
    {
        val layout              = LinearLayoutBuilder()
        val format              = pointsWidget.format().barFormat().limitFormat()
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

        layout.backgroundColor  = colorOrBlack(format.elementFormat().backgroundColorTheme(),
                                               entityId)

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

                icon.color          = colorOrBlack(format.iconFormat().colorTheme(), entityId)

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

                pointsWidget.format().barFormat().limitFormat()
                            .styleTextViewBuilder(textView, entityId, context)

                layout.child(textView)
            }
        }

        layout.onClick = View.OnClickListener {
            pointsWidget.currentValueVariable(entityId) apDo { valueVariable ->
            valueVariable.valueOrError(entityId)        apDo { value ->
                if (value.toInt() != index) {
                    valueVariable.updateValue(index.toDouble(), entityId)
                }
            } }
        }


        return layout.linearLayout(context)
    }


    // -----------------------------------------------------------------------------------------
    // LEVELS VIEW
    // -----------------------------------------------------------------------------------------

    private fun levelsBarView() : LinearLayout
    {
        val layout  = this.levelsBarViewLayout()

        val limitValue      = pointsWidget.limitValue(entityId)?.toInt()
        var currentValue    = pointsWidget.currentValue(entityId)?.toInt()

        if (limitValue != null && currentValue != null && limitValue > 0)
        {
            for (i in 1..limitValue) {
                val levelString = pointsWidget.format().barFormat().levels().getOrElse(i - 1, {"X"})

                val isFirst = i == 1
                val isLast  = i == limitValue
                val barFormat = pointsWidget.format().barFormat().elementFormat()

                if (i <= currentValue)
                {
                    val format = pointsWidget.format().barFormat().currentFormat()
                    val activeView = this.levelsBarActiveView(i, format, barFormat, isFirst, isLast)
                    activeView.addView(levelTextView(levelString, format))
                    layout.addView(activeView)
                }
                else
                {
                    val format = pointsWidget.format().barFormat().limitFormat()
                    val inactiveView = this.levelsBarInactiveView(i, format, barFormat, isFirst, isLast)
                    inactiveView.addView(levelTextView(levelString, format))
                    layout.addView(inactiveView)
                }

                if (i != limitValue)
                    layout.addView(this.levelsDividerView())
            }
        }

        return layout
    }


    private fun levelsDividerView() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.widthDp          = 1
        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_23")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("light_blue_grey_5"))))
        layout.backgroundColor  = colorOrBlack(colorTheme, entityId)

        return layout.linearLayout(context)
    }


    private fun levelsBarViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        val barFormat = pointsWidget.format().barFormat()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.backgroundColor  = colorOrBlack(barFormat.elementFormat().backgroundColorTheme(),
                                               entityId)

        layout.corners          = barFormat.elementFormat().corners()

        layout.paddingSpacing   = barFormat.elementFormat().padding()
        layout.marginSpacing    = barFormat.elementFormat().margins()

        return layout.linearLayout(context)
    }


    private fun levelsBarActiveView(index : Int,
                                    activeFormat : TextFormat,
                                    barFormat : ElementFormat,
                                    isFirst : Boolean,
                                    isLast : Boolean) : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        val width = activeFormat.elementFormat().width()
        when (width) {
            is Width.Fixed -> layout.widthDp = width.value.toInt()
            else           -> layout.width = LinearLayout.LayoutParams.WRAP_CONTENT
        }

        val height = activeFormat.elementFormat().height()
        when (height) {
            is Height.Fixed -> layout.heightDp = height.value.toInt()
            else            -> layout.height = LinearLayout.LayoutParams.WRAP_CONTENT
        }

        layout.backgroundColor  = colorOrBlack(activeFormat.elementFormat().backgroundColorTheme(),
                                               entityId)

        if (isFirst) {
            layout.corners          = Corners(barFormat.corners().topLeftRadius,
                                              0.0,
                                              0.0,
                                              barFormat.corners().bottomLeftRadius)
        }
        else if (isLast) {
            layout.corners          = Corners(0.0,
                                              barFormat.corners().topRightRadius,
                                              barFormat.corners().bottomRightRadius,
                                              0.0)
        }
        else {
            layout.corners          = activeFormat.elementFormat().corners()
        }

        layout.gravity          = Gravity.CENTER

        layout.paddingSpacing   = activeFormat.elementFormat().padding()
        layout.marginSpacing    = activeFormat.elementFormat().margins()

        layout.onClick = View.OnClickListener {
            pointsWidget.currentValueVariable(entityId) apDo { valueVariable ->
            valueVariable.valueOrError(entityId)        apDo { value ->
                if (value.toInt() != index) {
                    valueVariable.updateValue(index.toDouble(), entityId)
                }
                else {
                    valueVariable.updateValue((index - 1).toDouble(), entityId)
                }
            } }
        }

        return layout.linearLayout(context)
    }


    private fun levelsBarInactiveView(index : Int,
                                      format : TextFormat,
                                      barFormat: ElementFormat,
                                      isFirst : Boolean,
                                      isLast : Boolean) : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

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

        layout.backgroundColor  = colorOrBlack(format.elementFormat().backgroundColorTheme(),
                                               entityId)

        if (isFirst) {
            layout.corners          = Corners(barFormat.corners().topLeftRadius,
                                              0.0,
                                              0.0,
                                              barFormat.corners().bottomLeftRadius)
        }
        else if (isLast) {
            layout.corners          = Corners(0.0,
                                              barFormat.corners().topRightRadius,
                                              barFormat.corners().bottomRightRadius,
                                              0.0)
        }
        else {
            layout.corners          = format.elementFormat().corners()
        }

        layout.paddingSpacing   = format.elementFormat().padding()
        layout.marginSpacing    = format.elementFormat().margins()

        layout.gravity          = format.elementFormat().alignment().gravityConstant() or
                                    format.elementFormat().verticalAlignment().gravityConstant()

        layout.onClick = View.OnClickListener {
            pointsWidget.currentValueVariable(entityId) apDo { valueVariable ->
            valueVariable.valueOrError(entityId)        apDo { value ->
                if (value.toInt() != index) {
                    valueVariable.updateValue(index.toDouble(), entityId)
                }
            } }
        }


        return layout.linearLayout(context)
    }


    private fun levelTextView(level : String, textFormat : TextFormat) : TextView
    {
        val current                 = TextViewBuilder()

        current.width               = LinearLayout.LayoutParams.WRAP_CONTENT
        current.height              = LinearLayout.LayoutParams.WRAP_CONTENT

        current.text                = level

        current.color               = colorOrBlack(textFormat.colorTheme(), entityId)

        current.sizeSp              = textFormat.sizeSp()

        current.font                = Font.typeface(textFormat.font(),
                                                    textFormat.fontStyle(),
                                                    context)

        current.paddingSpacing      = textFormat.elementFormat().padding()
        current.marginSpacing       = textFormat.elementFormat().margins()

        return current.textView(context)
    }



    // -----------------------------------------------------------------------------------------
    // SHARED VIEWS
    // -----------------------------------------------------------------------------------------

    private fun currentPointsView(currentString : String,
                                  textFormat : TextFormat) : TextView
    {
        val current                 = TextViewBuilder()

        current.layoutType          = LayoutType.RELATIVE

        current.width               = RelativeLayout.LayoutParams.WRAP_CONTENT
        current.height              = RelativeLayout.LayoutParams.WRAP_CONTENT

        current.id                  = R.id.points_bar_current

        current.text                = currentString

        current.color               = colorOrBlack(textFormat.colorTheme(), entityId)

        current.sizeSp              = textFormat.sizeSp()

        current.font                = Font.typeface(textFormat.font(),
                                                    textFormat.fontStyle(),
                                                    context)

        current.paddingSpacing      = textFormat.elementFormat().padding()
        current.marginSpacing       = textFormat.elementFormat().margins()

        return current.textView(context)
    }



    private fun limitPointsView(limitString : String,
                                textFormat : TextFormat) : TextView
    {
        val limit                   = TextViewBuilder()

        limit.id                    = R.id.points_bar_limit

        limit.layoutType            = LayoutType.RELATIVE

        limit.width                 = RelativeLayout.LayoutParams.WRAP_CONTENT
        limit.height                = RelativeLayout.LayoutParams.WRAP_CONTENT

        limit.text                  = limitString

        limit.color                 = colorOrBlack(textFormat.colorTheme(), entityId)

        limit.sizeSp                = textFormat.sizeSp()

        limit.font                  = Font.typeface(textFormat.font(),
                                                    textFormat.fontStyle(),
                                                    context)

        limit.paddingSpacing        = textFormat.elementFormat().padding()
        limit.marginSpacing         = textFormat.elementFormat().margins()

        return limit.textView(context)
    }



    private fun labelView(labelString : String,
                          textFormat : TextFormat) : TextView
    {
        val label                   = TextViewBuilder()

        label.layoutType            = LayoutType.RELATIVE

        label.width                 = RelativeLayout.LayoutParams.WRAP_CONTENT
        label.height                = RelativeLayout.LayoutParams.WRAP_CONTENT

        label.id                    = R.id.points_bar_label

        label.text                  = labelString

        label.color                 = colorOrBlack(textFormat.colorTheme(), entityId)

        label.sizeSp                = textFormat.sizeSp()

        label.font                  = Font.typeface(textFormat.font(),
                                                    textFormat.fontStyle(),
                                                    context)

        label.paddingSpacing        = textFormat.elementFormat().padding()
        label.marginSpacing         = textFormat.elementFormat().margins()

        return label.textView(context)
    }



    // -----------------------------------------------------------------------------------------
    // SHARED LINEAR VIEWS
    // -----------------------------------------------------------------------------------------

    private fun pointsSlashView() : LinearLayout
    {
        val layout = this.pointsSlashViewLayout()

        val currentPointsString = pointsWidget.currentValueString(entityId)
        val limitPointsString = pointsWidget.limitValueString(entityId)

        val currentTextFormat = pointsWidget.format().currentTextFormat()
        val limitTextFormat = pointsWidget.format().limitTextFormat()

        if (currentPointsString != null)
            layout.addView(this.currentPointsLinearView(currentPointsString, currentTextFormat))

        layout.addView(this.slashTextView(limitTextFormat))

        if (limitPointsString != null)
            layout.addView(this.limitPointsLinearView(limitPointsString, limitTextFormat))

        return layout
    }


    private fun pointsSlashViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.HORIZONTAL

        return layout.linearLayout(context)
    }

    private fun currentPointsLinearView(currentString : String,
                                        textFormat : TextFormat) : TextView
    {
        val current                 = TextViewBuilder()

        current.layoutType          = LayoutType.LINEAR
        current.width               = LinearLayout.LayoutParams.WRAP_CONTENT
        current.height              = LinearLayout.LayoutParams.WRAP_CONTENT

        current.id                  = R.id.points_bar_current

        current.text                = currentString

        current.color               = colorOrBlack(textFormat.colorTheme(), entityId)

        current.sizeSp              = textFormat.sizeSp()

        current.font                = Font.typeface(textFormat.font(),
                                                    textFormat.fontStyle(),
                                                    context)

        current.paddingSpacing      = textFormat.elementFormat().padding()
        current.marginSpacing       = textFormat.elementFormat().margins()

        return current.textView(context)
    }



    private fun limitPointsLinearView(limitString : String,
                                      textFormat : TextFormat) : TextView
    {
        val limit                   = TextViewBuilder()

        limit.id                    = R.id.points_bar_limit

        limit.layoutType            = LayoutType.LINEAR

        limit.width                 = LinearLayout.LayoutParams.WRAP_CONTENT
        limit.height                = LinearLayout.LayoutParams.WRAP_CONTENT

        limit.text                  = limitString

        limit.color                 = colorOrBlack(textFormat.colorTheme(), entityId)

        limit.sizeSp                = textFormat.sizeSp()

        limit.font                  = Font.typeface(textFormat.font(),
                                                    textFormat.fontStyle(),
                                                    context)

        limit.paddingSpacing        = textFormat.elementFormat().padding()
        limit.marginSpacing         = textFormat.elementFormat().margins()

        return limit.textView(context)
    }



    private fun labelLinearView(labelString : String,
                                textFormat : TextFormat) : TextView
    {
        val label                   = TextViewBuilder()

        label.layoutType            = LayoutType.LINEAR

        label.width                 = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height                = LinearLayout.LayoutParams.WRAP_CONTENT

        label.id                    = R.id.points_bar_label

        label.text                  = labelString

        label.color                 = colorOrBlack(textFormat.colorTheme(), entityId)

        label.sizeSp                = textFormat.sizeSp()

        label.font                  = Font.typeface(textFormat.font(),
                                                    textFormat.fontStyle(),
                                                    context)


        label.paddingSpacing        = textFormat.elementFormat().padding()
        label.marginSpacing         = textFormat.elementFormat().margins()

        return label.textView(context)
    }



    private fun slashTextView(textFormat : TextFormat) : TextView
    {
        val current                 = TextViewBuilder()

        current.layoutType          = LayoutType.LINEAR

        current.id                  = R.id.points_bar_slash

        current.width               = LinearLayout.LayoutParams.WRAP_CONTENT
        current.height              = LinearLayout.LayoutParams.WRAP_CONTENT

        current.text                = "/"

        current.color               = colorOrBlack(textFormat.colorTheme(), entityId)

        current.sizeSp              = textFormat.sizeSp()

        current.font                = Font.typeface(textFormat.font(),
                                                    textFormat.fontStyle(),
                                                    context)

        current.paddingSpacing      = textFormat.elementFormat().padding()
        current.marginSpacing       = textFormat.elementFormat().margins()

        return current.textView(context)
    }


    private fun currentSlashLimitView(currentString : String,
                                      limitString : String,
                                      textFormat : TextFormat) : TextView
    {
        val current                 = TextViewBuilder()

        current.width               = LinearLayout.LayoutParams.WRAP_CONTENT
        current.height              = LinearLayout.LayoutParams.WRAP_CONTENT

        current.text                = "$currentString / $limitString"

        current.color               = colorOrBlack(textFormat.colorTheme(), entityId)

        current.sizeSp              = textFormat.sizeSp()

        current.font                = Font.typeface(textFormat.font(),
                                                    textFormat.fontStyle(),
                                                    context)

        current.paddingSpacing      = textFormat.elementFormat().padding()
        current.marginSpacing       = textFormat.elementFormat().margins()

        return current.textView(context)
    }

}
