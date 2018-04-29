
package com.kispoko.tome.model.sheet.widget


import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.support.graphics.drawable.VectorDrawableCompat
import android.support.v7.app.AppCompatActivity
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.AbsoluteSizeSpan
import android.text.style.BackgroundColorSpan
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.flexbox.*
import com.kispoko.tome.R
import com.kispoko.tome.activity.entity.book.BookActivity
import com.kispoko.tome.activity.sheet.SheetActivity
import com.kispoko.tome.app.AppEff
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.ui.*
import com.kispoko.tome.model.game.engine.variable.Variable
import com.kispoko.tome.model.game.engine.variable.VariableId
import com.kispoko.tome.model.sheet.style.*
import com.kispoko.tome.rts.entity.sheet.*
import com.kispoko.tome.util.Util
import effect.*
import lulo.document.*
import lulo.value.*
import lulo.value.UnexpectedType
import java.io.Serializable
import java.util.*
import com.kispoko.tome.activity.sheet.dialog.*
import com.kispoko.tome.db.*
import com.kispoko.tome.lib.orm.*
import com.kispoko.tome.lib.orm.schema.MaybePrimValue
import com.kispoko.tome.lib.orm.schema.PrimValue
import com.kispoko.tome.lib.orm.schema.ProdValue
import com.kispoko.tome.lib.orm.sql.*
import com.kispoko.tome.model.game.engine.variable.NumberVariable
import com.kispoko.tome.model.theme.ColorTheme
import com.kispoko.tome.rts.entity.EntityId
import com.kispoko.tome.rts.entity.colorOrBlack
import com.kispoko.tome.rts.entity.variable
import com.kispoko.tome.util.LongClickLinkMovementMethod
import com.kispoko.tome.util.LongClickableSpan
import maybe.Just
import maybe.Maybe
import maybe.Nothing
import java.text.Format



/**
 * Story Widget Format
 */
data class StoryWidgetFormat(override val id : UUID,
                             val widgetFormat : WidgetFormat,
                             val lineHeight : Maybe<LineHeight>,
                             val lineSpacing : Maybe<LineSpacing>,
                             val textFormat : TextFormat)
                              : ToDocument, ProdType, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(widgetFormat : WidgetFormat,
                lineHeight : Maybe<LineHeight>,
                lineSpacing : Maybe<LineSpacing>,
                textFormat : TextFormat)
        : this(UUID.randomUUID(),
               widgetFormat,
               lineHeight,
               lineSpacing,
               textFormat)


    companion object : Factory<StoryWidgetFormat>
    {

        private fun defaultWidgetFormat()   = WidgetFormat.default()
        private fun defaultLineHeight()     = Nothing<LineHeight>()
        private fun defaultLineSpacing()    = Nothing<LineSpacing>()
        private fun defaultTextFormat()     = TextFormat.default()


        override fun fromDocument(doc : SchemaDoc) : ValueParser<StoryWidgetFormat> = when (doc)
        {
            is DocDict ->
            {
                apply(::StoryWidgetFormat,
                      // Widget Format
                      split(doc.maybeAt("widget_format"),
                            effValue(defaultWidgetFormat()),
                            { WidgetFormat.fromDocument(it) }),
                      // Line Height
                      split(doc.maybeAt("line_height"),
                            effValue<ValueError,Maybe<LineHeight>>(defaultLineHeight()),
                            { apply(::Just, LineHeight.fromDocument(it)) }),
                      // Line Spacing
                      split(doc.maybeAt("line_spacing"),
                            effValue<ValueError,Maybe<LineSpacing>>(defaultLineSpacing()),
                            { apply(::Just, LineSpacing.fromDocument(it)) }),
                      // Text Format
                      split(doc.maybeAt("text_format"),
                            effValue(defaultTextFormat()),
                            { TextFormat.fromDocument(it) })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = StoryWidgetFormat(defaultWidgetFormat(),
                                          defaultLineHeight(),
                                          defaultLineSpacing(),
                                          defaultTextFormat())

    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "widget_format" to this.widgetFormat().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun widgetFormat() : WidgetFormat = this.widgetFormat


    fun lineSpacing() : Maybe<LineSpacing> = this.lineSpacing


    fun lineHeight() : Maybe<LineHeight> = this.lineHeight


    fun textFormat() : TextFormat = this.textFormat


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_WidgetStoryFormatValue =
        RowValue4(widgetStoryFormatTable,
                  ProdValue(this.widgetFormat),
                  MaybePrimValue(this.lineHeight),
                  MaybePrimValue(this.lineSpacing),
                  ProdValue(this.textFormat))

}


@Suppress("UNCHECKED_CAST")
sealed class StoryPart(open val format : StoryPartFormat) : ToDocument, ProdType, Serializable
{

    companion object : Factory<StoryPart>
    {

        override fun fromDocument(doc: SchemaDoc): ValueParser<StoryPart> =
            when (doc.case())
            {
                "story_part_span"     -> StoryPartSpan.fromDocument(doc) as ValueParser<StoryPart>
                "story_part_variable" -> StoryPartVariable.fromDocument(doc) as ValueParser<StoryPart>
                "story_part_icon"     -> StoryPartIcon.fromDocument(doc) as ValueParser<StoryPart>
                "story_part_action"   -> StoryPartAction.fromDocument(doc) as ValueParser<StoryPart>
                else                  -> effError(UnknownCase(doc.case(), doc.path))
            }
    }


    // -----------------------------------------------------------------------------------------
    // APi
    // -----------------------------------------------------------------------------------------

    open fun partVariable(entityId : EntityId) : Variable? = when (this)
    {
        is StoryPartVariable -> {
            val variable = this.valueVariable(entityId)
            when (variable) {
                is effect.Val -> variable.value
                is Err -> {
                    ApplicationLog.error(variable.error)
                    null
                }
            }
        }
        else                 ->  null
    }


    abstract fun wordCount() : Int

}


/**
 * Story Part Format
 */
data class StoryPartFormat(override val id : UUID,
                           val highlightSkew : HighlightSkew,
                           val highlightCornerRadius : HighlightCornerRadius)
                            : ProdType, ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(highlightSkew : HighlightSkew,
                highlightCornerRadius : HighlightCornerRadius)
        : this(UUID.randomUUID(),
               highlightSkew,
               highlightCornerRadius)


    companion object : Factory<StoryPartFormat>
    {
        override fun fromDocument(doc : SchemaDoc): ValueParser<StoryPartFormat> = when (doc)
        {
            is DocDict ->
            {
                apply(::StoryPartFormat,
                      // Highlight Skew
                      split(doc.maybeAt("highlight_skew"),
                            effValue(HighlightSkew.default()),
                            { HighlightSkew.fromDocument(it) }),
                      // Highlight Corner Radius
                      split(doc.maybeAt("highlight_corner_radius"),
                            effValue(HighlightCornerRadius.default()),
                            { HighlightCornerRadius.fromDocument(it) })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = StoryPartFormat(HighlightSkew.default(),
                                        HighlightCornerRadius.default())
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "highlight_skew" to this.highlightSkew.toDocument(),
        "highlight_corner_radius" to this.highlightCornerRadius.toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun highlightSkew() : HighlightSkew = this.highlightSkew


    fun highlightCornerRadius() : HighlightCornerRadius = this.highlightCornerRadius


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject : ProdType = this


    override fun rowValue() : DB_WidgetStoryPartFormatValue =
        RowValue2(widgetStoryPartFormatTable,
                  PrimValue(this.highlightSkew),
                  PrimValue(this.highlightCornerRadius))

}


/**
 * Story Part Span
 */
data class StoryPartSpan(override val id : UUID,
                         override val format : StoryPartFormat,
                         val textFormat : TextFormat,
                         val text : StoryPartText) : StoryPart(format), Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(format : StoryPartFormat,
                textFormat : TextFormat,
                text : StoryPartText)
        : this(UUID.randomUUID(),
               format,
               textFormat,
               text)


    companion object : Factory<StoryPartSpan>
    {
        override fun fromDocument(doc : SchemaDoc): ValueParser<StoryPartSpan> = when (doc)
        {
            is DocDict ->
            {
                apply(::StoryPartSpan,
                      // Format
                      split(doc.maybeAt("format"),
                            effValue(StoryPartFormat.default()),
                            { StoryPartFormat.fromDocument(it) }),
                      // Text Format
                      split(doc.maybeAt("text_format"),
                            effValue(TextFormat.default()),
                            { TextFormat.fromDocument(it) }),
                      // Text
                      doc.at("text") ap { StoryPartText.fromDocument(it) }
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "format" to this.format.toDocument(),
        "text_format" to this.textFormat.toDocument(),
        "text" to this.text().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun format() : StoryPartFormat = this.format


    fun text() : StoryPartText = this.text


    fun textString() : String = this.text.value


    fun textFormat() : TextFormat = this.textFormat


    // -----------------------------------------------------------------------------------------
    // STORY PART
    // -----------------------------------------------------------------------------------------

    override fun wordCount() = this.textString().split(' ').size


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject : ProdType = this


    override fun rowValue() : DB_WidgetStoryPartSpanValue =
        RowValue3(widgetStoryPartSpanTable,
                  ProdValue(this.format),
                  ProdValue(this.textFormat),
                  PrimValue(this.text))

}


/**
 * Story Part Variable
 */
data class StoryPartVariable(override val id : UUID,
                             override val format : StoryPartFormat,
                             val textFormat : TextFormat,
                             val variableId : VariableId,
                             val numericEditorType : NumericEditorType)
                              : StoryPart(format), Serializable
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var viewId : Int? = null


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(format : StoryPartFormat,
                textFormat : TextFormat,
                variableId : VariableId,
                numericEditorType : NumericEditorType)
        : this(UUID.randomUUID(),
               format,
               textFormat,
               variableId,
               numericEditorType)


    companion object : Factory<StoryPartVariable>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<StoryPartVariable> = when (doc)
        {
            is DocDict ->
            {
                apply(::StoryPartVariable,
                      // Format
                      split(doc.maybeAt("format"),
                            effValue(StoryPartFormat.default()),
                            { StoryPartFormat.fromDocument(it) }),
                      // Text Format
                      split(doc.maybeAt("text_format"),
                             effValue(TextFormat.default()),
                             { TextFormat.fromDocument(it) }),
                      // Variable Id
                      doc.at("variable_id") ap { VariableId.fromDocument(it) },
                      // Numeric Editor Type
                      split(doc.maybeAt("numeric_editor_type"),
                            effValue<ValueError,NumericEditorType>(NumericEditorType.Adder),
                            { NumericEditorType.fromDocument(it) })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "format" to this.format.toDocument(),
        "text_format" to this.textFormat.toDocument(),
        "variable_id" to this.variableId.toDocument(),
        "numeric_editor_type" to this.numericEditorType().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun format() : StoryPartFormat = this.format


    fun textFormat() : TextFormat = this.textFormat


    fun variableId() : VariableId = this.variableId


    fun valueVariable(entityId : EntityId) : AppEff<Variable> =
            variable(this.variableId(), entityId)

    fun numericEditorType() : NumericEditorType = this.numericEditorType


    fun valueString(entityId : EntityId) : String
    {
        val str = this.partVariable(entityId)?.valueString(entityId)
        when (str)
        {
            is effect.Val -> return str.value
            is Err -> {
                ApplicationLog.error(str.error)
            }
        }

        return ""
    }


    // -----------------------------------------------------------------------------------------
    // STORY PART
    // -----------------------------------------------------------------------------------------

    override fun wordCount() = 0


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_WidgetStoryPartVariableValue =
        RowValue4(widgetStoryPartVariableTable,
                  ProdValue(this.format),
                  ProdValue(this.textFormat),
                  PrimValue(this.variableId),
                  PrimValue(this.numericEditorType))

}


/**
 * Story Part Icon
 */
data class StoryPartIcon(override val id : UUID,
                         override val format : StoryPartFormat,
                         val icon : Icon)
                          : StoryPart(format), Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(format : StoryPartFormat,
                icon : Icon)
                 : this(UUID.randomUUID(), format, icon)


    companion object : Factory<StoryPartIcon>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<StoryPartIcon> = when (doc)
        {
            is DocDict -> apply(::StoryPartIcon,
                                // Format
                                split(doc.maybeAt("format"),
                                      effValue(StoryPartFormat.default()),
                                      { StoryPartFormat.fromDocument(it) }),
                                // Icon
                                doc.at("icon") ap { Icon.fromDocument(it) }
                                )
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "format" to this.icon().toDocument(),
        "icon" to this.icon().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun format() : StoryPartFormat = this.format


    fun icon() : Icon = this.icon


    // -----------------------------------------------------------------------------------------
    // STORY PART
    // -----------------------------------------------------------------------------------------

    override fun wordCount() = 0


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_WidgetStoryPartIconValue =
        RowValue2(widgetStoryPartIconTable,
                  ProdValue(this.format),
                  ProdValue(this.icon))

}


/**
 * Story Part Roll
 */
data class StoryPartAction(override val id : UUID,
                           override val format : StoryPartFormat,
                           val text : StoryPartText,
                           val action : Action,
                           val textFormat : TextFormat,
                           val iconFormat : IconFormat,
                           val showProcedureDialog : ShowProcedureDialog)
                            : StoryPart(format), Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(format : StoryPartFormat,
                text : StoryPartText,
                action : Action,
                textFormat : TextFormat,
                iconFormat : IconFormat,
                showProcedureDialog : ShowProcedureDialog)
        : this(UUID.randomUUID(),
               format,
               text,
               action,
               textFormat,
               iconFormat,
               showProcedureDialog)


    companion object : Factory<StoryPartAction>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<StoryPartAction> = when (doc)
        {
            is DocDict ->
            {
                apply(::StoryPartAction,
                      // Format
                      split(doc.maybeAt("format"),
                            effValue(StoryPartFormat.default()),
                            { StoryPartFormat.fromDocument(it) }),
                      // Text
                      doc.at("text") ap { StoryPartText.fromDocument(it) },
                      // Action
                      doc.at("action") ap { Action.fromDocument(it) },
                      // Text Format
                      split(doc.maybeAt("text_format"),
                            effValue(TextFormat.default()),
                            { TextFormat.fromDocument(it) }),
                      // Icon Format
                      split(doc.maybeAt("icon_format"),
                            effValue(IconFormat.default()),
                            { IconFormat.fromDocument(it) }),
                      // Show Procedure Dialog
                      split(doc.maybeAt("show_procedure_dialog"),
                            effValue(ShowProcedureDialog(false)),
                            { ShowProcedureDialog.fromDocument(it) })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "format" to this.format.toDocument(),
        "text" to this.text.toDocument(),
        "action" to this.action().toDocument(),
        "text_format" to this.textFormat.toDocument(),
        "icon_format" to this.iconFormat().toDocument(),
        "show_procedure_dialog" to this.showProcedureDialog().toDocument()
        ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------


    fun format() : StoryPartFormat = this.format


    fun text() : StoryPartText = this.text


    fun textString() : String = this.text.value


    fun action() : Action = this.action


    fun textFormat() : TextFormat = this.textFormat


    fun iconFormat() : IconFormat = this.iconFormat


    fun showProcedureDialog() : ShowProcedureDialog = this.showProcedureDialog


    // -----------------------------------------------------------------------------------------
    // STORY PART
    // -----------------------------------------------------------------------------------------

    override fun wordCount() = this.textString().split(' ').size


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_WidgetStoryPartActionValue =
        RowValue6(widgetStoryPartActionTable,
                  ProdValue(this.format),
                  PrimValue(this.text),
                  ProdValue(this.action),
                  ProdValue(this.textFormat),
                  ProdValue(this.iconFormat),
                  PrimValue(this.showProcedureDialog))

}


/**
 * Story Part Action - Show Procedure Dialog
 */
data class ShowProcedureDialog(val value : Boolean) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ShowProcedureDialog>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<ShowProcedureDialog> = when (doc)
        {
            is DocBoolean -> effValue(ShowProcedureDialog(doc.boolean))
            else          -> effError(UnexpectedType(DocType.BOOLEAN, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocBoolean(this.value)


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLInt({ if (this.value) 1 else 0 })

}


/**
 * Story Part Text
 */
data class StoryPartText(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<StoryPartText>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<StoryPartText> = when (doc)
        {
            is DocText -> effValue(StoryPartText(doc.text))
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

    override fun asSQLValue() : SQLValue = SQLText({this.value})

}


/**
 * Line Spacing
 */
data class LineSpacing(val value : Float) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<LineSpacing>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<LineSpacing> = when (doc)
        {
            is DocNumber -> effValue(LineSpacing(doc.number.toFloat()))
            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
        }

        fun default() = LineSpacing(1.0f)
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocNumber(this.value.toDouble())


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLReal({this.value.toDouble()})

}


/**
 * Line Height
 */
data class LineHeight(val value : Float) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<LineHeight>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<LineHeight> = when (doc)
        {
            is DocNumber -> effValue(LineHeight(doc.number.toFloat()))
            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
        }

        fun default() = LineHeight(1.0f)
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocNumber(this.value.toDouble())


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLReal({this.value.toDouble()})

}


/**
 * Highlight Skew
 */
data class HighlightSkew(val value : Float) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<HighlightSkew>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<HighlightSkew> = when (doc)
        {
            is DocNumber -> effValue(HighlightSkew(doc.number.toFloat()))
            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
        }

        fun default() = HighlightSkew(0.75f)
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocNumber(this.value.toDouble())


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLReal({this.value.toDouble()})

}


/**
 * Highlight Corner Radius
 */
data class HighlightCornerRadius(val value : Int) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<HighlightCornerRadius>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<HighlightCornerRadius> = when (doc)
        {
            is DocNumber -> effValue(HighlightCornerRadius(doc.number.toInt()))
            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
        }

        fun default() = HighlightCornerRadius(12)
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocNumber(this.value.toDouble())


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLReal({this.value.toDouble()})

}



class StoryWidgetViewBuilder(val storyWidget : StoryWidget,
                             val entityId : EntityId,
                             val context : Context)
{

    fun view() : View
    {
        val layout = WidgetView.layout(storyWidget.widgetFormat(), entityId, context)

        val viewId = Util.generateViewId()
        storyWidget.viewId  = viewId
        layout.id           = viewId

        this.updateView(layout)

//        val wc = storyWidget.story().map { it.wordCount() }.sum()
//        if (wc <= 4 && storyWidget.actionParts().isEmpty())
//        {
//            contentLayout.addView(this.storyFlexView(storyWidget, sheetUIContext))
//        }
//        else
//        {

        //}

        // Layout on click
//        val variableParts = storyWidget.variableParts()
//        if (variableParts.size == 1)
//        {
//            var variablePartIndex = 0
//            storyWidget.story().forEachIndexed { index, storyPart ->
//                when (storyPart) {
//                    is StoryPartVariable -> variablePartIndex = index
//                }
//            }
//            val variable = variableParts.first().variable(SheetContext(sheetUIContext))
//            if (variable != null) {
////                layout.setOnClickListener {
////                    openVariableEditorDialog(
////                            variable,
////                            UpdateTargetStoryWidgetPart(storyWidget.id, variablePartIndex),
////                            sheetUIContext)
////                }
//            }
//
//        }


        return layout
    }


    fun updateView(layout : LinearLayout)
    {
        val contentLayout = layout.findViewById<LinearLayout>(R.id.widget_content_layout)

        contentLayout.removeAllViews()

        val spanView = storySpannableView(storyWidget, entityId, context)
        contentLayout.addView(spanView)
    }


    fun storyFlexView(storyWidget : StoryWidget) : FlexboxLayout
    {
        val layout = this.storyViewLayout(storyWidget.format())

        val storyParts = storyWidget.story()

        storyParts.forEachIndexed { partIndex, storyPart ->
            when (storyPart)
            {
                is StoryPartSpan ->
                {
                    words(storyPart.textString()).forEach {
                        layout.addView(this.wordView(it, storyWidget.format(), storyPart))
                    }
                }
                is StoryPartVariable ->
                {
                    val valueVariableEff = storyPart.valueVariable(entityId)
                    // TODO move this logic in variable
                    val text = when (valueVariableEff)
                    {
                        is effect.Val ->
                        {
                            val valueVar = valueVariableEff.value
                            when (valueVar)
                            {
                                is NumberVariable -> {
                                    val number = valueVar.valueOrZero(entityId)
                                    storyPart.textFormat().numberFormat().formattedString(number)
                                }
                                else -> storyPart.valueString(entityId)
                            }
                        }
                        is Err -> {
                            ApplicationLog.error(valueVariableEff.error)
                            ""
                        }

                    }

                    words(text).forEach {
                        layout.addView(this.wordVariableView(it,
                                                             storyWidget.format(),
                                                             storyPart,
                                                             partIndex,
                                                             storyWidget.widgetId()))
                    }
                }
                is StoryPartIcon ->
                {
                    layout.addView(this.iconView(storyPart))
                }
            }
        }

        return layout
    }


    fun storyViewLayout(format : StoryWidgetFormat) : FlexboxLayout
    {
        val layout = FlexboxLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT

//        layout.gravity          = format.alignment().gravityConstant() or
//                                        format.alignment().gravityConstant()

        when (format.widgetFormat().elementFormat().alignment()) {
            is Alignment.Center -> layout.justification = JustifyContent.CENTER
        }

//        layout.backgroundColor  = SheetManager.color(sheetUIContext.sheetId,
//                                                     format.backgroundColorTheme())

//        layout.corners          = format.corners()

        layout.direction        = FlexDirection.ROW
        layout.wrap             = FlexWrap.WRAP

        when (format.widgetFormat().elementFormat().verticalAlignment()) {
            is VerticalAlignment.Middle -> layout.itemAlignment = AlignItems.CENTER
        }

        return layout.flexboxLayout(context)

    }


    fun wordView(word : String,
                 format : StoryWidgetFormat,
                 storyPartSpan : StoryPartSpan) : View
    {
        val text = TextViewBuilder()

        text.layoutType     = LayoutType.FLEXBOX
        text.width          = FlexboxLayout.LayoutParams.WRAP_CONTENT
        text.height         = FlexboxLayout.LayoutParams.WRAP_CONTENT

        text.text           = word

        val formatPadding   = storyPartSpan.textFormat().elementFormat().padding()
        val padding         = Spacing(formatPadding.leftDp().toDouble(),
                                      formatPadding.topDp().toDouble(), // + format.lineSpacingFloat(),
                                      formatPadding.rightDp().toDouble(),
                                      formatPadding.bottomDp().toDouble())// + format.lineSpacingFloat())

        text.paddingSpacing = padding
        text.marginSpacing  = storyPartSpan.textFormat().elementFormat().margins()

        storyPartSpan.textFormat().styleTextViewBuilder(text, entityId, context)

        return text.textView(context)
    }


    fun wordVariableView(word : String,
                         format : StoryWidgetFormat,
                         storyPart : StoryPartVariable,
                         partIndex : Int,
                         widgetId : WidgetId) : View
    {
        val text = TextViewBuilder()

        val wordFormat = storyPart.textFormat()

        text.layoutType     = LayoutType.FLEXBOX
        text.width          = FlexboxLayout.LayoutParams.WRAP_CONTENT
        text.height         = FlexboxLayout.LayoutParams.WRAP_CONTENT

        text.text           = word

        storyPart.viewId   = Util.generateViewId()
        text.id            = storyPart.viewId

        val formatPadding   = storyPart.textFormat().elementFormat().padding()
//        val padding         = Spacing(formatPadding.leftDp().toDouble(),
//                                    formatPadding.topDp().toDouble(), // + format.lineSpacingFloat(),
//                                    formatPadding.rightDp().toDouble(),
//                                    formatPadding.bottomDp().toDouble()) //  + format.lineSpacingFloat())

        text.paddingSpacing = wordFormat.elementFormat().padding()
        text.marginSpacing  = wordFormat.elementFormat().margins()

        text.backgroundColor = colorOrBlack(wordFormat.elementFormat().backgroundColorTheme(), entityId)

        text.corners        = wordFormat.elementFormat().corners()

        storyPart.textFormat().styleTextViewBuilder(text, entityId, context)

        text.onClick        = View.OnClickListener {
            val variable = storyPart.partVariable(entityId)
            if (variable != null) {
                openVariableEditorDialog(variable,
                        storyPart.numericEditorType(),
                        UpdateTargetStoryWidgetPart(widgetId, partIndex),
                        entityId,
                        context)
            }
        }

        return text.textView(context)
    }


    fun iconView(storyPart : StoryPartIcon) : View
    {
        val icon            = ImageViewBuilder()

        val iconFormat      = storyPart.icon().iconFormat()

        icon.layoutType     = LayoutType.FLEXBOX

        icon.widthDp        = iconFormat.size().width
        icon.heightDp       = iconFormat.size().height

        icon.image          = storyPart.icon().iconType().drawableResId()

        icon.iconSize       = iconFormat.size()

        icon.color          = colorOrBlack(iconFormat.colorTheme(), entityId)

        return icon.imageView(context)
    }

}


fun storySpannableView(storyWidget : StoryWidget,
                       entityId : EntityId,
                       context : Context) : TextView
{
    val story           = TextViewBuilder()

    story.width         = LinearLayout.LayoutParams.MATCH_PARENT
//        story.widthDp       = 300
    story.height        = LinearLayout.LayoutParams.WRAP_CONTENT

    story.textSpan      = spannableStringBuilder(storyWidget.story(),
                                                 storyWidget.widgetId(),
                                                 storyWidget.format().lineHeight(),
                                                 storyWidget.format().lineSpacing(),
                                                 entityId,
                                                 context)

    //story.lineSpacingAdd    = 1f
    //story.lineSpacingMult   = 1.4f

    story.paddingSpacing    = storyWidget.format().textFormat().elementFormat().padding()


    story.sizeSp = storyWidget.format().textFormat().sizeSp()

    story.gravity       = storyWidget.widgetFormat().elementFormat().alignment().gravityConstant()
    story.layoutGravity = storyWidget.widgetFormat().elementFormat().alignment().gravityConstant()

    when (storyWidget.format().widgetFormat().elementFormat().verticalAlignment()) {
        is VerticalAlignment.Middle -> {
            story.layoutGravity = storyWidget.widgetFormat().elementFormat().alignment().gravityConstant() or
                                    Gravity.CENTER_VERTICAL
            story.gravity       = storyWidget.widgetFormat().elementFormat().alignment().gravityConstant() or
                                    Gravity.CENTER_VERTICAL
        }
    }

//    story.movementMethod    = LinkMovementMethod.getInstance()
    story.movementMethod    = LongClickLinkMovementMethod.getInstance()

//    val gestureDetector = openWidgetOptionsDialogOnDoubleTap(
//            context as SheetActivity,
//            storyWidget,
//            SheetContext(sheetUIContext))
//    story.onTouch   = View.OnTouchListener { _, event ->
//        gestureDetector.onTouchEvent(event)
//        false
//    }

    return story.textView(context)
}


data class Phrase(val storyPart : StoryPart,
                  val text : String,
                  val partIndex : Int,
                  val start : Int,
                  val end : Int)




private fun spannableStringBuilder(storyParts : List<StoryPart>,
                                   storyWidgetId : WidgetId,
                                   lineHeight : Maybe<LineHeight>,
                                   lineSpacing : Maybe<LineSpacing>,
                                   entityId : EntityId,
                                   context : Context)
                                    : SpannableStringBuilder
{

    val builder = SpannableStringBuilder()

    val phrases : MutableList<Phrase> = mutableListOf()

    var index = 0
    storyParts.forEachIndexed { partIndex, storyPart ->

        when (storyPart)
        {
            is StoryPartSpan ->
            {
                //val text = storyPart.textString()
                words(storyPart.textString()).forEach {
                    builder.append(it)
                    val textLen = it.length
                    phrases.add(Phrase(storyPart, it, partIndex, index, index + textLen))
                    index += textLen
                }
            }
            is StoryPartVariable ->
            {
                val valueVariableEff = storyPart.valueVariable(entityId)
                // TODO move this logic in variable
                var text = when (valueVariableEff)
                {
                    is effect.Val ->
                    {
                        val valueVar = valueVariableEff.value
                        when (valueVar)
                        {
                            is NumberVariable -> {
                                val number = valueVar.valueOrZero(entityId)
                                storyPart.textFormat().numberFormat().formattedString(number)
                            }
                            else -> storyPart.valueString(entityId)
                        }
                    }
                    is Err -> {
                        ApplicationLog.error(valueVariableEff.error)
                        ""
                    }

                }

                val nbspSpacing = "\u202F\u202F";
                if (storyPart.textFormat.elementFormat().backgroundColorTheme() != ColorTheme.transparent) {
                    text = "$nbspSpacing$text$nbspSpacing"
                }

                val textLen = text.length
                builder.append(text)

                phrases.add(Phrase(storyPart, text, partIndex, index, index + textLen))
                index += textLen
            }
            is StoryPartIcon ->
            {
                builder.append(" ")
                phrases.add(Phrase(storyPart, " ", partIndex, index, index + 1))
                index += 1
            }
            is StoryPartAction ->
            {
                val text = storyPart.textString()
                builder.append("        " + text + "  ")
                val textLen = text.length + 10
                phrases.add(Phrase(storyPart, text, partIndex, index, index + textLen))
                index += textLen
            }
        }
    }

    phrases.forEach { (storyPart, _, partIndex, start, end) ->

        //Log.d("***STORY WIDGET", "start -> end: " + start.toString() + " " + end.toString())

        when (storyPart)
        {
            is StoryPartSpan ->
            {
                formatSpans(storyPart.textFormat(), lineHeight, lineSpacing, storyPart.format(), entityId, context).forEach {
                    builder.setSpan(it, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                }
            }
            is StoryPartVariable ->
            {

                val clickSpan = object : LongClickableSpan()
                {
                    override fun onClick(view : View?)
                    {
                        Log.d("***STORY WIDGET", "on click")
                        val variable = storyPart.partVariable(entityId)
                        if (variable != null) {
                            openVariableEditorDialog(
                                    variable,
                                    storyPart.numericEditorType(),
                                    UpdateTargetStoryWidgetPart(storyWidgetId, partIndex),
                                    entityId,
                                    context)
                        }

                    }

                    override fun onLongClick(view: View?)
                    {
                        Log.d("***STORY WIDGET", "on long click")
                        val activity = context as AppCompatActivity

                        val vibrator = activity.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                        // Vibrate for 500 milliseconds
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
                        } else {
                            //deprecated in API 26
                            vibrator.vibrate(50)
                        }

                        val variable = storyPart.partVariable(entityId)
                        if (variable != null) {
                            variable.bookReference(entityId).doMaybe {
                                val intent = Intent(activity, BookActivity::class.java)
                                intent.putExtra("book_reference", it)
                                activity.startActivity(intent)
                            }
                        }
                    }

                    override fun updateDrawState(ds: TextPaint?) {
                        //super.updateDrawState(ds)
                    }
                }

                builder.setSpan(clickSpan, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE)

                formatSpans(storyPart.textFormat(), lineHeight, lineSpacing, storyPart.format(), entityId, context).forEach {
                    builder.setSpan(it, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                }
            }
            is StoryPartIcon ->
            {
                val vectorDrawable =
                        VectorDrawableCompat.create(context.resources,
                                                    storyPart.icon().iconType().drawableResId(), null)

                vectorDrawable?.setBounds(
                        0,
                        0,
                        Util.dpToPixel(storyPart.icon().iconFormat().size().width.toFloat()),
                        Util.dpToPixel(storyPart.icon().iconFormat().size().height.toFloat()))

                val color = colorOrBlack(storyPart.icon().iconFormat().colorTheme(), entityId)
                vectorDrawable?.colorFilter = PorterDuffColorFilter(color,
                                                                    PorterDuff.Mode.SRC_IN)

                val imageSpan = CenteredImageSpan(vectorDrawable)
                builder.setSpan(imageSpan, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            }
            is StoryPartAction ->
            {
                val vectorDrawable =
                        VectorDrawableCompat.create(context.resources,
                                                    R.drawable.icon_dice_roll_filled,
                                                    null)

//                vectorDrawable?.setBounds(
//                        0,
//                        0,
//                        Util.dpToPixel(storyPart.iconFormat().size().width.toFloat()),
//                        Util.dpToPixel(storyPart.iconFormat().size().height.toFloat()))

//                val color = SheetManager.color(sheetUIContext.sheetId,
//                                               storyPart.iconFormat().colorTheme())
//                vectorDrawable?.colorFilter = PorterDuffColorFilter(color,
//                                                                    PorterDuff.Mode.SRC_IN)

                val imageSpan = CenteredImageSpan(vectorDrawable)

                // TODO android tip mutate
                formatSpans(storyPart.textFormat(), lineHeight, lineSpacing, storyPart.format(), entityId, context,
                            vectorDrawable?.mutate(), storyPart.iconFormat()).forEach {
                    builder.setSpan(it, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                }

                //builder.setSpan(imageSpan, start, start + 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE)

                val procedureId = storyPart.action().procedureId()
                val rollGroup = storyPart.action().rollGroup()

                when (rollGroup)
                {
                    is Just ->
                    {
                        val sheetActivity = context as SheetActivity
                        val clickSpan = object : ClickableSpan() {
                            override fun onClick(view: View?) {
//                                val dialog = DiceRollDialog.newInstance(
//                                                            rollGroup.value,
//                                                            entityId)
//                                dialog.show(sheetActivity.supportFragmentManager, "")
                            }

                            override fun updateDrawState(ds: TextPaint?) {
                            }
                        }

                        builder.setSpan(clickSpan, start + 1, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                    }
                }

            }
        }
    }

    return builder
}


private fun formatSpans(textStyle : TextFormat,
                        lineHeight : Maybe<LineHeight>,
                        lineSpacing : Maybe<LineSpacing>,
                        partFormat : StoryPartFormat,
                        entityId : EntityId,
                        context : Context,
                        drawable : Drawable? = null,
                        iconFormat : IconFormat? = null) : List<Any>
{
    val sizePx = Util.spToPx(textStyle.sizeSp(), context)
    val sizeSpan = AbsoluteSizeSpan(sizePx)

    val typeface = Font.typeface(textStyle.font(), textStyle.fontStyle(), context)

    val typefaceSpan = CustomTypefaceSpan(typeface)

    var color = colorOrBlack(textStyle.colorTheme(), entityId)
    val colorSpan = ForegroundColorSpan(color)

    var bgColor = colorOrBlack(textStyle.elementFormat().backgroundColorTheme(), entityId)
    val bgColorSpan = BackgroundColorSpan(bgColor)

    val iconColor : Int? = iconFormat?.let {
        colorOrBlack( it.colorTheme(), entityId)
    }


    if (iconColor != null) {
        drawable?.colorFilter = PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_IN)
    }

    return when (lineHeight) {
        is Just -> when (lineSpacing) {
            is Just -> {
                val lineSpacingPx = Util.spToPx(lineSpacing.value.value, context)
                val lineHeightPx = Util.spToPx(lineHeight.value.value, context)
                val bgSpan = RoundedBackgroundHeightSpan(lineHeightPx,
                                                         lineSpacingPx,
                                                         partFormat.highlightSkew().value,
                                                         partFormat.highlightCornerRadius().value,
                                                         color,
                                                         bgColor,
                                                         drawable,
                                                         iconFormat?.size(),
                                                         iconColor)
                listOf(bgSpan, typefaceSpan, sizeSpan)
            }
            else -> listOf(sizeSpan, typefaceSpan, colorSpan, bgColorSpan)
        }
        else -> listOf(sizeSpan, typefaceSpan, colorSpan, bgColorSpan)
    }

//    return if (lineHeight.isJust() && lineSpacing.isJust()) {
//    }
//

}



fun words(valueString : String) : List<String> =
    valueString.split(" ").map { word -> "$word " }


