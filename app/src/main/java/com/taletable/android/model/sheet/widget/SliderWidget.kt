
package com.taletable.android.model.sheet.widget


import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.taletable.android.R
import com.taletable.android.lib.Factory
import com.taletable.android.lib.orm.sql.SQLSerializable
import com.taletable.android.lib.orm.sql.SQLText
import com.taletable.android.lib.orm.sql.SQLValue
import com.taletable.android.lib.ui.*
import com.taletable.android.model.sheet.style.BorderEdge
import com.taletable.android.model.sheet.style.ElementFormat
import com.taletable.android.model.sheet.style.TextFormat
import com.taletable.android.rts.entity.EntityId
import com.taletable.android.rts.entity.colorOrBlack
import effect.apply
import effect.effError
import effect.effValue
import effect.split
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.UnexpectedValue
import lulo.value.ValueError
import lulo.value.ValueParser
import java.io.Serializable
import java.util.*



/**
 * Slider Widget Format
 */
data class SliderWidgetFormat(val id : UUID,
                              val widgetFormat : WidgetFormat,
                              val viewType : SliderWidgetViewType,
                              val groupFormat : ElementFormat,
                              val controlsFormat : ElementFormat,
                              val changeButtonFormat : TextFormat,
                              val counterFormat : SliderCounterFormat)
                               : ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(widgetFormat: WidgetFormat,
                viewType : SliderWidgetViewType,
                groupFormat : ElementFormat,
                controlsFormat : ElementFormat,
                changeButtonFormat : TextFormat,
                counterFormat : SliderCounterFormat)
        : this(UUID.randomUUID(),
               widgetFormat,
               viewType,
               groupFormat,
               controlsFormat,
               changeButtonFormat,
               counterFormat)


    companion object : Factory<SliderWidgetFormat>
    {

        private fun defaultWidgetFormat()       = WidgetFormat.default()
        private fun defaultViewType()           = SliderWidgetViewType.ButtonsOppositeCountMiddle
        private fun defaultGroupFormat()        = ElementFormat.default()
        private fun defaultControlsFormat()     = ElementFormat.default()
        private fun defaultChangeButtonFormat() = TextFormat.default()
        private fun defaultCounterFormat()      = SliderCounterFormat.default()


        override fun fromDocument(doc : SchemaDoc) : ValueParser<SliderWidgetFormat> = when (doc)
        {
            is DocDict ->
            {
                apply(::SliderWidgetFormat,
                      // Widget Format
                      split(doc.maybeAt("widget_format"),
                            effValue(defaultWidgetFormat()),
                            { WidgetFormat.fromDocument(it) }),
                      // View Type
                      split(doc.maybeAt("view_type"),
                            effValue<ValueError,SliderWidgetViewType>(defaultViewType()),
                            { SliderWidgetViewType.fromDocument(it) }),
                      // Group Format
                      split(doc.maybeAt("group_format"),
                            effValue(defaultGroupFormat()),
                            { ElementFormat.fromDocument(it) }),
                      // Controls Format
                      split(doc.maybeAt("controls_format"),
                            effValue(defaultControlsFormat()),
                            { ElementFormat.fromDocument(it) }),
                      // Change Button Format
                      split(doc.maybeAt("change_button_format"),
                            effValue(defaultChangeButtonFormat()),
                            { TextFormat.fromDocument(it) }),
                      // Counter Format
                      split(doc.maybeAt("counter_format"),
                            effValue(defaultCounterFormat()),
                            { SliderCounterFormat.fromDocument(it) }))
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = SliderWidgetFormat(defaultWidgetFormat(),
                                           defaultViewType(),
                                           defaultGroupFormat(),
                                           defaultControlsFormat(),
                                           defaultChangeButtonFormat(),
                                           defaultCounterFormat())
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "widget_format" to this.widgetFormat().toDocument(),
        "view_type" to this.viewType.toDocument(),
        "group_format" to this.groupFormat.toDocument(),
        "controls_format" to this.controlsFormat.toDocument(),
        "change_button_format" to this.changeButtonFormat.toDocument(),
        "counter_format" to this.counterFormat.toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun widgetFormat() : WidgetFormat = this.widgetFormat


    fun viewType() : SliderWidgetViewType = this.viewType


    fun groupFormat() : ElementFormat = this.groupFormat


    fun controlsFormat() : ElementFormat = this.controlsFormat


    fun changeButtonFormat() : TextFormat = this.changeButtonFormat


    fun counterFormat() : SliderCounterFormat = this.counterFormat

}


/**
 * Slider Widget View Type
 */
sealed class SliderWidgetViewType : ToDocument, SQLSerializable, Serializable
{

    object ButtonsOppositeCountMiddle : SliderWidgetViewType()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "buttons_opposite_count_middle" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("buttons_opposite_count_middle")

    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun fromDocument(doc : SchemaDoc) : ValueParser<SliderWidgetViewType> = when (doc)
        {
            is DocText -> when (doc.text)
            {
                "buttons_opposite_count_middle" -> effValue<ValueError,SliderWidgetViewType>(
                                                        SliderWidgetViewType.ButtonsOppositeCountMiddle)
                else                            -> effError<ValueError,SliderWidgetViewType>(
                                                        UnexpectedValue("SliderWidgetViewType", doc.text, doc.path))
            }
            else       -> effError(lulo.value.UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }

}


/**
 * Slider Counter Format
 */
data class SliderCounterFormat(val id : UUID,
                              val elementFormat : ElementFormat,
                              val currentValueFormat : TextFormat,
                              val slashFormat : TextFormat,
                              val limitValueFormat : TextFormat)
                               : ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(elementFormat : ElementFormat,
                currentValueFormat : TextFormat,
                slashFormat : TextFormat,
                limitValueFormat : TextFormat)
        : this(UUID.randomUUID(),
               elementFormat,
               currentValueFormat,
               slashFormat,
               limitValueFormat)


    companion object : Factory<SliderCounterFormat>
    {

        private fun defaultElementFormat()      = ElementFormat.default()
        private fun defaultCurrentValueFormat() = TextFormat.default()
        private fun defaultSlashFormat()        = TextFormat.default()
        private fun defaultLimitValueFormat()   = TextFormat.default()


        override fun fromDocument(doc : SchemaDoc) : ValueParser<SliderCounterFormat> = when (doc)
        {
            is DocDict ->
            {
                apply(::SliderCounterFormat,
                      // Element Format
                      split(doc.maybeAt("element_format"),
                            effValue(defaultElementFormat()),
                            { ElementFormat.fromDocument(it) }),
                      // Current Value Format
                      split(doc.maybeAt("current_value_format"),
                            effValue(defaultCurrentValueFormat()),
                            { TextFormat.fromDocument(it) }),
                      // Separator Format
                      split(doc.maybeAt("separator_format"),
                            effValue(defaultSlashFormat()),
                            { TextFormat.fromDocument(it) }),
                      // Limit Value Format
                      split(doc.maybeAt("limit_value_format"),
                            effValue(defaultLimitValueFormat()),
                            { TextFormat.fromDocument(it) })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = SliderCounterFormat(defaultElementFormat(),
                                            defaultCurrentValueFormat(),
                                            defaultSlashFormat(),
                                            defaultLimitValueFormat())
    }

    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "element_format" to this.elementFormat.toDocument(),
        "current_value_format" to this.currentValueFormat.toDocument(),
        "slash_format" to this.slashFormat.toDocument(),
        "limit_value_format" to this.limitValueFormat.toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun elementFormat() : ElementFormat = this.elementFormat


    fun currentValueFormat() : TextFormat = this.currentValueFormat


    fun slashFormat() : TextFormat = this.slashFormat


    fun limitValueFormat() : TextFormat = this.limitValueFormat

}


class SliderWidgetUI(val sliderWidget : WidgetSlider,
                     val entityId : EntityId,
                     val context : Context)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var currentIndex : Int = 0

    var groupViewLayout : LinearLayout? = null
    var counterCurrentValueView : TextView? = null

    // -----------------------------------------------------------------------------------------
    // METHODS
    // -----------------------------------------------------------------------------------------

    private fun showPreviousGroup()
    {
        if (currentIndex != 0)
        {
            currentIndex -= 1
            groupViewLayout?.removeAllViews()

            sliderWidget.groupAtIndex(currentIndex).doMaybe {
                groupViewLayout?.addView(it.view(entityId, context))
            }

            counterCurrentValueView?.text = (currentIndex + 1).toString()
        }
    }


    private fun showNextGroup()
    {
        if (currentIndex != (sliderWidget.groups().size - 1))
        {
            currentIndex += 1
            groupViewLayout?.removeAllViews()

            sliderWidget.groupAtIndex(currentIndex).doMaybe {
                groupViewLayout?.addView(it.view(entityId, context))
            }

            counterCurrentValueView?.text = (currentIndex + 1).toString()
        }
    }

    // -----------------------------------------------------------------------------------------
    // VIEWS
    // -----------------------------------------------------------------------------------------

    fun view() : View
    {
        val layout = this.viewLayout()

        // Group
        val groupViewLayout = this.groupViewLayout()
        this.groupViewLayout = groupViewLayout
        layout.addView(groupViewLayout)

        sliderWidget.groupAtIndex(0).doMaybe {
            groupViewLayout.addView(it.view(entityId, context))
        }


        // Controls
        if (sliderWidget.groups().size > 1)
            layout.addView(this.controlsView())

        return layout
    }


    private fun viewLayout() : LinearLayout
    {
        val layout          = LinearLayoutBuilder()

        layout.width        = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height       = LinearLayout.LayoutParams.MATCH_PARENT

        layout.orientation  = LinearLayout.VERTICAL

        return layout.linearLayout(context)
    }


    private fun borderView(format : BorderEdge) : LinearLayout
    {
        val border                  = LinearLayoutBuilder()

        border.width               = LinearLayout.LayoutParams.MATCH_PARENT
        border.heightDp            = format.thickness().value

        border.backgroundColor     = colorOrBlack(format.colorTheme(), entityId)

        return border.linearLayout(context)
    }


    // VIEWS > Group View
    // -----------------------------------------------------------------------------------------

    private fun groupViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        val format              = sliderWidget.format().groupFormat()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.marginSpacing    = format.margins()
        layout.paddingSpacing   = format.padding()

        layout.backgroundColor  = colorOrBlack(format.backgroundColorTheme(), entityId)

        return layout.linearLayout(context)
    }


    // VIEWS > Controls
    // -----------------------------------------------------------------------------------------

    private fun controlsView() : View = when (sliderWidget.format().viewType())
    {
        is SliderWidgetViewType.ButtonsOppositeCountMiddle ->
        {
            val previousButtonView = this.changeButtonView(
                                        R.drawable.icon_chevron_left,
                                        RelativeLayout.ALIGN_PARENT_START,
                                        View.OnClickListener { showPreviousGroup() })


            val counterView = this.counterView()

            val nextButtonView =    this.changeButtonView(
                                        R.drawable.icon_chevron_right,
                                        RelativeLayout.ALIGN_PARENT_END,
                                        View.OnClickListener { showNextGroup() })

            val views = listOf(previousButtonView, counterView, nextButtonView)

            this.controlsView(views)
        }
    }


    private fun controlsView(childViews : List<View>) : LinearLayout
    {
        val outerLayout = this.controlsOuterViewLayout()

        sliderWidget.format().controlsFormat().border().top().doMaybe {
            outerLayout.addView(this.borderView(it))
        }

        val mainLayout = this.controlsMainViewLayout()
        outerLayout.addView(mainLayout)

        childViews.forEach {
            mainLayout.addView(it)
        }

        return outerLayout
    }


    private fun controlsOuterViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        val format              = sliderWidget.format().controlsFormat()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        layout.marginSpacing    = format.margins()

        return layout.linearLayout(context)
    }

    private fun controlsMainViewLayout() : RelativeLayout
    {
        val layout              = RelativeLayoutBuilder()

        val format              = sliderWidget.format().controlsFormat()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.paddingSpacing   = format.padding()

        layout.gravity          = format.alignment().gravityConstant() or
                                   format.verticalAlignment().gravityConstant()

        layout.backgroundColor  = colorOrBlack(format.backgroundColorTheme(), entityId)

        return layout.relativeLayout(context)
    }


    // VIEWS > Controls > Change Button
    // -----------------------------------------------------------------------------------------

    private fun changeButtonView(iconId : Int,
                                 layoutRule : Int,
                                 onClick : View.OnClickListener) : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout              = LinearLayoutBuilder()

        val iconLayout          = LinearLayoutBuilder()
        val icon                = ImageViewBuilder()

        val format              = sliderWidget.format().changeButtonFormat()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.layoutType       = LayoutType.RELATIVE

        layout.width            = RelativeLayout.LayoutParams.WRAP_CONTENT
        layout.height           = RelativeLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.backgroundColor  = colorOrBlack(format.elementFormat().backgroundColorTheme(), entityId)

        layout.corners          = format.elementFormat().corners()

        layout.gravity          = Gravity.CENTER

        layout.paddingSpacing   = format.elementFormat().padding()
        layout.marginSpacing    = format.elementFormat().margins()

        layout.addRule(layoutRule)

        layout.addRule(RelativeLayout.CENTER_VERTICAL)

        layout.onClick          = onClick

        layout.child(iconLayout)

        // (3 A) Icon
        // -------------------------------------------------------------------------------------

        iconLayout.width        = LinearLayout.LayoutParams.WRAP_CONTENT
        iconLayout.height        = LinearLayout.LayoutParams.WRAP_CONTENT

        iconLayout.child(icon)

        val iconFormat          = sliderWidget.format().changeButtonFormat().iconFormat()

        icon.widthDp            = iconFormat.size().width
        icon.heightDp           = iconFormat.size().height

        icon.image              = iconId

        icon.color              = colorOrBlack(iconFormat.colorTheme(), entityId)

        return layout.linearLayout(context)
    }


    // VIEWS > Controls > Counter
    // -----------------------------------------------------------------------------------------

    private fun counterView() : LinearLayout
    {
        val layout = this.counterViewLayout(RelativeLayout.CENTER_HORIZONTAL)

        val currentValueView = this.counterCurrentValueView()
        this.counterCurrentValueView = currentValueView
        layout.addView(currentValueView)

        layout.addView(this.counterSlashTextView())
        layout.addView(this.counterLimitValueView())

        return layout
    }


    private fun counterViewLayout(layoutRule : Int) : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        val format              = sliderWidget.format().counterFormat().elementFormat()

        layout.layoutType       = LayoutType.RELATIVE

        layout.width            = RelativeLayout.LayoutParams.WRAP_CONTENT
        layout.height           = RelativeLayout.LayoutParams.WRAP_CONTENT

        layout.addRule(layoutRule)

        layout.addRule(RelativeLayout.CENTER_VERTICAL)

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.gravity          = format.alignment().gravityConstant() or
                                    format.verticalAlignment().gravityConstant()

        layout.marginSpacing    = format.margins()
        layout.paddingSpacing   = format.padding()

        layout.backgroundColor  = colorOrBlack(format.backgroundColorTheme(), entityId)

        return layout.linearLayout(context)
    }


    private fun counterCurrentValueView() : TextView
    {
        val limit                   = TextViewBuilder()
        val format                  = sliderWidget.format().counterFormat().currentValueFormat()

        limit.width                 = LinearLayout.LayoutParams.WRAP_CONTENT
        limit.height                = LinearLayout.LayoutParams.WRAP_CONTENT

        limit.text                  = (this.currentIndex + 1).toString()

        limit.color                 = colorOrBlack(format.colorTheme(), entityId)

        limit.sizeSp                = format.sizeSp()

        limit.font                  = Font.typeface(format.font(),
                                                    format.fontStyle(),
                                                    context)

        limit.paddingSpacing        = format.elementFormat().padding()
        limit.marginSpacing         = format.elementFormat().margins()

        return limit.textView(context)
    }


    private fun counterSlashTextView() : TextView
    {
        val current                 = TextViewBuilder()
        val format                  = sliderWidget.format().counterFormat().slashFormat()

        current.width               = LinearLayout.LayoutParams.WRAP_CONTENT
        current.height              = LinearLayout.LayoutParams.WRAP_CONTENT

        current.text                = "/"

        current.color               = colorOrBlack(format.colorTheme(), entityId)

        current.sizeSp              = format.sizeSp()

        current.font                = Font.typeface(format.font(),
                                                    format.fontStyle(),
                                                    context)

        current.paddingSpacing      = format.elementFormat().padding()
        current.marginSpacing       = format.elementFormat().margins()

        return current.textView(context)
    }


    private fun counterLimitValueView() : TextView
    {
        val limit                   = TextViewBuilder()
        val format                  = sliderWidget.format().counterFormat().limitValueFormat()

        limit.width                 = LinearLayout.LayoutParams.WRAP_CONTENT
        limit.height                = LinearLayout.LayoutParams.WRAP_CONTENT

        limit.text                  = sliderWidget.groups().size.toString()

        limit.color                 = colorOrBlack(format.colorTheme(), entityId)

        limit.sizeSp                = format.sizeSp()

        limit.font                  = Font.typeface(format.font(),
                                                    format.fontStyle(),
                                                    context)

        limit.paddingSpacing        = format.elementFormat().padding()
        limit.marginSpacing         = format.elementFormat().margins()

        return limit.textView(context)
    }


}
