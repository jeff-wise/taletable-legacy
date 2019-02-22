
package com.taletable.android.model.sheet.widget


import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.taletable.android.R
import com.taletable.android.lib.Factory
import com.taletable.android.lib.ui.LinearLayoutBuilder
import com.taletable.android.lib.ui.TextViewBuilder
import com.taletable.android.model.sheet.group.GroupContext
import com.taletable.android.model.sheet.group.RowLayoutType
import com.taletable.android.model.sheet.style.TextFormat
import com.taletable.android.rts.entity.EntityId
import com.taletable.android.rts.entity.EntityTypeSheet
import com.taletable.android.rts.entity.entityType
import com.taletable.android.rts.entity.sheetOrError
import com.taletable.android.util.Util
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import maybe.Just
import maybe.Maybe
import maybe.Nothing
import java.io.Serializable



/**
 * Text Widget Format
 */
data class TextWidgetFormat(val widgetFormat : WidgetFormat,
                            val insideLabelFormat : TextFormat,
                            val outsideLabelFormat : TextFormat,
                            val valueFormat : TextFormat)
                             : ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(widgetFormat : WidgetFormat, valueFormat : TextFormat)
      : this(widgetFormat, TextFormat.default(), TextFormat.default(), valueFormat)


    companion object : Factory<TextWidgetFormat>
    {

        private fun defaultWidgetFormat()       = WidgetFormat.default()
        private fun defaultInsideLabelFormat()  = TextFormat.default()
        private fun defaultOutsideLabelFormat() = TextFormat.default()
        private fun defaultValueFormat()        = TextFormat.default()


        override fun fromDocument(doc : SchemaDoc) : ValueParser<TextWidgetFormat> = when (doc)
        {
            is DocDict ->
            {
                apply(::TextWidgetFormat,
                      // Widget Format
                      split(doc.maybeAt("widget_format"),
                            effValue(defaultWidgetFormat()),
                            { WidgetFormat.fromDocument(it) }),
                      // Inside Label Format
                      split(doc.maybeAt("inside_label_format"),
                            effValue(defaultInsideLabelFormat()),
                            { TextFormat.fromDocument(it) }),
                      // Outside Label Format
                      split(doc.maybeAt("outside_label_format"),
                            effValue(defaultOutsideLabelFormat()),
                            { TextFormat.fromDocument(it) }),
                      // Value Format
                      split(doc.maybeAt("value_format"),
                            effValue(defaultValueFormat()),
                            { TextFormat.fromDocument(it) })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = TextWidgetFormat(defaultWidgetFormat(),
                                         defaultInsideLabelFormat(),
                                         defaultOutsideLabelFormat(),
                                         defaultValueFormat())

    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "widget_format" to this.widgetFormat().toDocument(),
        "inside_label_format" to this.insideLabelFormat().toDocument(),
        "outside_label_format" to this.outsideLabelFormat().toDocument(),
        "value_format" to this.valueFormat().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun widgetFormat() : WidgetFormat = this.widgetFormat


    fun insideLabelFormat() : TextFormat = this.insideLabelFormat


    fun outsideLabelFormat() : TextFormat = this.outsideLabelFormat


    fun valueFormat() : TextFormat = this.valueFormat

}


object TextWidgetView
{

    // -----------------------------------------------------------------------------------------
    // VIEW
    // -----------------------------------------------------------------------------------------

    fun view(textWidget : TextWidget,
             format : TextWidgetFormat,
             rowLayoutType : RowLayoutType,
             entityId : EntityId,
             context : Context,
             groupContext : Maybe<GroupContext> = Nothing()) : View
    {
        val layout = WidgetView.layout(format.widgetFormat(), entityId, context, rowLayoutType)

        val layoutId = Util.generateViewId()
        textWidget.layoutId = layoutId
        layout.id  = layoutId

        this.updateView(textWidget, entityId, layout, context, groupContext)

        // On Click
        // -------------------------------------------------------------------------------------

        entityType(entityId).apDo { entityType ->
            when (entityType) {
                is EntityTypeSheet -> {
                    layout.setOnClickListener {
                        val primaryActionWidgetId = textWidget.primaryActionWidgetId
                        when (primaryActionWidgetId) {
                            is Just -> {
                                sheetOrError(entityId)                 apDo {
                                it.widget(primaryActionWidgetId.value) apDo {
                                it.primaryAction(entityId, context)
                                } }
                            }
                            else -> textWidget.primaryAction(entityId, context)
                        }
                    }

                    layout.setOnLongClickListener {
                        val secondaryActionWidgetId = textWidget.secondaryActionWidgetId()
                        when (secondaryActionWidgetId) {
                            is Just -> {
                                sheetOrError(entityId)                 apDo {
                                it.widget(secondaryActionWidgetId.value) apDo {
                                    it.secondaryAction(entityId, context)
                                } }
                            }
                            else -> textWidget.secondaryAction(entityId, context)
                        }
                        true
                    }
                }
            }

        }


//        val rulebookReference = textWidget.rulebookReference()
//        when (rulebookReference) {
//            is Just -> {
//                layout.setOnLongClickListener {
//                    val sheetActivity = context as SessionActivity
//                    val dialog = RulebookExcerptDialog.newInstance(rulebookReference.value,
//                                                                   entityId)
//                    dialog.show(sheetActivity.supportFragmentManager, "")
//                    true
//                }
//            }
//        }


        return layout
    }


    fun updateView(textWidget : TextWidget,
                   entityId : EntityId,
                   layout : LinearLayout,
                   context : Context,
                   groupContext : Maybe<GroupContext>)
    {

        val contentLayout = layout.findViewById<LinearLayout>(R.id.widget_content_layout)
        contentLayout.removeAllViews()
        contentLayout.addView(this.mainView(textWidget, entityId, context, groupContext))
    }




    /**
     * The outermost view that holds the outside labels and the value view.
     *
     *                      top label
     *             --------------------------
     *             |                         |
     *  left label |        Value View       | right label
     *             |                         |
     *             ---------------------------
     *                    bottom label
     *
     */
    private fun mainView(textWidget : TextWidget,
                         entityId : EntityId,
                         context : Context,
                         groupContext : Maybe<GroupContext>) : LinearLayout
    {
        val layout = this.mainLayout(textWidget, context)

        val format = textWidget.format()

        // > Outside Top/Left Label View
//        if (format.outsideLabel() != null) {
//            if (format.outsideLabelFormat().position().isTop() ||
//                format.outsideLabelFormat().position().isLeft()) {
//                layout.addView(this.outsideLabelView(format, sheetUIContext))
//            }
//        }

        // > Value
        layout.addView(this.valueMainView(textWidget, format, entityId, context, groupContext))

        // > Outside Bottom/Right Label View
//        if (format.outsideLabel() != null) {
//            if (format.outsideLabelFormat().position().isBottom() ||
//                format.outsideLabelFormat().position().isRight()) {
//                layout.addView(this.outsideLabelView(format, sheetUIContext))
//            }
//        }


        return layout
    }


    private fun mainLayout(textWidget : TextWidget,
                           context : Context) : LinearLayout
    {
        val layout = LinearLayoutBuilder()

        layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

//        layout.orientation          = textWidget.format().outsideLabelFormat()
//                                            .position().linearLayoutOrientation()

//        layout.gravity              = textWidget.widgetFormat().elementFormat().alignment().gravityConstant()
        //Log.d("***TEXTWIDGET", "${textWidget.widgetFormat().elementFormat().alignment()}" )

        // layout.marginSpacing        = textWidget.widgetFormat().elementFormat().margins()


        return layout.linearLayout(context)
    }


    /**
     * The view that holds the value as well as the inside labels around the value.
     */
    private fun valueMainView(textWidget : TextWidget,
                              format : TextWidgetFormat,
                              entityId : EntityId,
                              context : Context,
                              groupContext : Maybe<GroupContext>) : LinearLayout
    {
        val layout = this.valueMainViewLayout(format, entityId, context)

        // > Inside Top/Left Label View
//        if (format.insideLabel() != null && textWidget.description() == null) {
//            if (format.insideLabelFormat().position().isTop() ||
//                format.insideLabelFormat().position().isLeft()) {
//                layout.addView(this.insideLabelView(format, sheetUIContext))
//            }
//        }

        val insideLabel = textWidget.insideLabelValue(entityId)

        // > Inside Top/Left Label View
        when (insideLabel) {
            is Val -> {
                val position = format.insideLabelFormat().elementFormat().position()
                if (position.isTop() || position.isLeft()) {
                    layout.addView(this.insideLabelView(format, insideLabel.value, entityId, context))
                }
            }
        }

        val valueString = textWidget.valueString(entityId, groupContext)
        val paragraphs = valueString.split("\n")
        paragraphs.forEachIndexed { index, s ->
            val isParagraph = index < paragraphs.size - 1
            layout.addView(valueTextView(s, isParagraph, textWidget, format, entityId, context, groupContext))
        }

        // > Inside Bottom/Right Label View
//        if (format.insideLabel() != null && textWidget.description() == null) {
//            if (format.insideLabelFormat().position().isBottom() ||
//                format.insideLabelFormat().position().isRight()) {
//                layout.addView(this.insideLabelView(format, sheetUIContext))
//            }
//        }

        return layout
    }


    private fun valueMainViewLayout(format : TextWidgetFormat,
                                    entityId : EntityId,
                                    context : Context) : LinearLayout
    {
        val layout = LinearLayoutBuilder()

//        layout.orientation          = format.insideLabelFormat()
//                                            .position().linearLayoutOrientation()

        layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation          = LinearLayout.VERTICAL

//        val height = format.widgetFormat().elementFormat().height()
//        when (height)
//        {
//            is Height.Wrap  -> layout.height   = LinearLayout.LayoutParams.WRAP_CONTENT
//            is Height.Fixed -> layout.heightDp = height.value.toInt()
//        }
        //layout.height               = LinearLayout.LayoutParams.MATCH_PARENT

//        layout.backgroundColor      = SheetManager.color(
//                                                sheetUIContext.sheetId,
//                                                format.widgetFormat().backgroundColorTheme())

        layout.gravity              = format.valueFormat().elementFormat().alignment().gravityConstant() or
                                        Gravity.CENTER_VERTICAL

//        layout.backgroundResource   = format.valueFormat().height()
//                                            .resourceId(format.widgetFormat().corners())

        if (format.valueFormat().elementFormat().height().isWrap())
        {
            layout.padding.topDp    = format.valueFormat().elementFormat().padding().topDp()
            layout.padding.bottomDp = format.valueFormat().elementFormat().padding().bottomDp()
        }

//        if (format.widgetFormat.background() == BackgroundColor.EMPTY)
//            layout.width            = LinearLayout.LayoutParams.WRAP_CONTENT

//        if (this.data().format().underlineThickness() > 0)
//        {
//            layout.backgroundColor    = this.data().format().underlineColor().resourceId();
//            layout.backgroundResource = R.drawable.bg_widget_bottom_border;
//        }

//        else if (this.data().format().background() != BackgroundColor.EMPTY &&
//                 this.data().format().background() != BackgroundColor.NONE)
//        {

        return layout.linearLayout(context)
    }


    private fun valueTextView(paragraph : String,
                              isParagraph : Boolean,
                              textWidget : TextWidget,
                              format : TextWidgetFormat,
                              entityId : EntityId,
                              context : Context,
                              groupContext : Maybe<GroupContext>) : TextView
    {
        val value = TextViewBuilder()

//        textWidget.viewId   = Util.generateViewId()
//        value.id            = textWidget.viewId

        value.width         = LinearLayout.LayoutParams.WRAP_CONTENT
        value.height        = LinearLayout.LayoutParams.WRAP_CONTENT

        if (isParagraph) {
            Log.d("***TEXT WIDGET", "is paragraph")
            value.margin.bottomDp = format.valueFormat().paragraphSpacing().value
        }

        value.layoutGravity = format.valueFormat().elementFormat().alignment().gravityConstant() or
                                Gravity.CENTER_VERTICAL
        value.gravity       = format.valueFormat().elementFormat().alignment().gravityConstant()

        //value.text          = textWidget.valueString(entityId, groupContext)
        value.text          = paragraph

        format.valueFormat().styleTextViewBuilder(value, entityId, context)

        return value.textView(context)
    }


//    private fun outsideLabelView(format : TextWidgetFormat,
//                                 sheetUIContext: SheetUIContext) : TextView
//    {
//        val label = TextViewBuilder()
//
//        label.width             = LinearLayout.LayoutParams.WRAP_CONTENT;
//        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        label.layoutGravity     = format.outsideLabelFormat().elementFormat().alignment().gravityConstant()
//
//        //label.text              = format.outsideLabel()
//
//        format.outsideLabelFormat().styleTextViewBuilder(label, sheetUIContext)
//
//        label.marginSpacing     = format.outsideLabelFormat().elementFormat().margins()
//
//        return label.textView(sheetUIContext.context)
//    }
//


    private fun insideLabelView(format : TextWidgetFormat,
                                labelString : String,
                                entityId : EntityId,
                                context : Context) : TextView
    {
        val label               = TextViewBuilder()

        label.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        label.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        label.text              = labelString

        label.layoutGravity     = format.insideLabelFormat().elementFormat().alignment().gravityConstant() or
                                      Gravity.CENTER_VERTICAL;

        format.insideLabelFormat().styleTextViewBuilder(label, entityId, context)

        label.marginSpacing     = format.insideLabelFormat().elementFormat().margins()

        return label.textView(context)
    }



}


