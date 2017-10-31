
package com.kispoko.tome.model.sheet.widget


import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.support.graphics.drawable.VectorDrawableCompat
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.AbsoluteSizeSpan
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.flexbox.*
import com.kispoko.tome.R
import com.kispoko.tome.activity.sheet.SheetActivity
import com.kispoko.tome.app.AppEff
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.lib.ui.*
import com.kispoko.tome.model.game.engine.procedure.ProcedureId
import com.kispoko.tome.model.game.engine.summation.SummationId
import com.kispoko.tome.model.game.engine.variable.Variable
import com.kispoko.tome.model.game.engine.variable.VariableId
import com.kispoko.tome.model.sheet.style.*
import com.kispoko.tome.rts.sheet.*
import com.kispoko.tome.util.Util
import effect.*
import lulo.document.*
import lulo.value.*
import lulo.value.UnexpectedType
import java.io.Serializable
import java.util.*
import com.kispoko.tome.activity.sheet.dialog.*
import com.kispoko.tome.lib.functor.*
import com.kispoko.tome.lib.orm.sql.*
import com.kispoko.tome.model.game.engine.variable.NumberVariable



/**
 * Story Widget Format
 */
data class StoryWidgetFormat(override val id : UUID,
                             val widgetFormat : Comp<WidgetFormat>,
                             val verticalAlignment : Prim<VerticalAlignment>,
                             val lineSpacing : Prim<LineSpacing>)
                              : ToDocument, Model, Serializable
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.widgetFormat.name      = "widget_format"
        this.verticalAlignment.name = "vertical_alignment"
        this.lineSpacing.name       = "line_spacing"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(widgetFormat : WidgetFormat,
                verticalAlignment : VerticalAlignment,
                lineSpacing : LineSpacing)
        : this(UUID.randomUUID(),
               Comp(widgetFormat),
               Prim(verticalAlignment),
               Prim(lineSpacing))


    companion object : Factory<StoryWidgetFormat>
    {

        private val defaultWidgetFormat         = WidgetFormat.default()
        private val defaultVerticalAlignment    = VerticalAlignment.Bottom
        private val defaultLineSpacing          = LineSpacing.default()


        override fun fromDocument(doc: SchemaDoc): ValueParser<StoryWidgetFormat> = when (doc)
        {
            is DocDict ->
            {
                effApply(::StoryWidgetFormat,
                         // Widget Format
                         split(doc.maybeAt("widget_format"),
                               effValue(defaultWidgetFormat),
                               { WidgetFormat.fromDocument(it) }),
                         // Vertical Alignment
                         split(doc.maybeAt("vertical_alignment"),
                               effValue<ValueError,VerticalAlignment>(defaultVerticalAlignment),
                               { VerticalAlignment.fromDocument(it) }),
                         // Line Spacing
                         split(doc.maybeAt("line_spacing"),
                               effValue(defaultLineSpacing),
                               { LineSpacing.fromDocument(it) })
                         )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = StoryWidgetFormat(defaultWidgetFormat,
                                          defaultVerticalAlignment,
                                          defaultLineSpacing)

    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "widget_format" to this.widgetFormat().toDocument(),
        "vertical_alignment" to this.verticalAlignment().toDocument(),
        "line_spacing" to this.lineSpacing().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun widgetFormat() : WidgetFormat = this.widgetFormat.value

    fun verticalAlignment() : VerticalAlignment = this.verticalAlignment.value

    fun lineSpacing() : LineSpacing = this.lineSpacing.value

    fun lineSpacingFloat() : Float = this.lineSpacing.value.value


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name : String = "story_widget_format"

    override val modelObject = this

}


@Suppress("UNCHECKED_CAST")
sealed class StoryPart : ToDocument, Model, Serializable
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
                else                  -> effError<ValueError,StoryPart>(
                                            UnknownCase(doc.case(), doc.path))
            }

    }


    // -----------------------------------------------------------------------------------------
    // APi
    // -----------------------------------------------------------------------------------------

    open fun variable(sheetContext : SheetContext) : Variable? = when (this)
    {
        is StoryPartVariable -> {
            val variable = this.valueVariable(sheetContext)
            when (variable) {
                is Val -> variable.value
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
 * Story Part Span
 */
data class StoryPartSpan(override val id : UUID,
                         val format : Comp<ElementFormat>,
                         val textStyle : Comp<TextStyle>,
                         val text : Prim<StoryPartText>) : StoryPart(), Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(format : ElementFormat,
                textStyle : TextStyle,
                text : StoryPartText)
        : this(UUID.randomUUID(),
               Comp(format),
               Comp(textStyle),
               Prim(text))


    companion object : Factory<StoryPartSpan>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<StoryPartSpan> = when (doc)
        {
            is DocDict ->
            {
                apply(::StoryPartSpan,
                      // Element Format
                      split(doc.maybeAt("format"),
                            effValue(ElementFormat.default()),
                            { ElementFormat.fromDocument(it) }),
                      // Text Style
                      split(doc.maybeAt("text_style"),
                            effValue(TextStyle.default()),
                            { TextStyle.fromDocument(it) }),
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
        "format" to this.format().toDocument(),
        "text_style" to this.textStyle().toDocument(),
        "text" to this.text().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun text() : StoryPartText = this.text.value

    fun textString() : String = this.text.value.value

    fun format() : ElementFormat = this.format.value

    fun textStyle() : TextStyle = this.textStyle.value


    // -----------------------------------------------------------------------------------------
    // STORY PART
    // -----------------------------------------------------------------------------------------

    override fun wordCount() = this.textString().split(' ').size

    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val modelObject = this

    override val name = "story_part_span"

}


/**
 * Story Part Variable
 */
data class StoryPartVariable(override val id : UUID,
                             val format : Comp<ElementFormat>,
                             val textStyle : Comp<TextStyle>,
                             val variableId : Prim<VariableId>,
                             val numericEditorType : Maybe<Prim<NumericEditorType>>,
                             val numberFormat : Prim<NumberFormat>)
                              : StoryPart(), Serializable
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var viewId : Int? = null


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(format : ElementFormat,
                textStyle : TextStyle,
                variableId : VariableId,
                numericEditorType : Maybe<NumericEditorType>,
                numberFormat : NumberFormat)
        : this(UUID.randomUUID(),
               Comp(format),
               Comp(textStyle),
               Prim(variableId),
               maybeLiftPrim(numericEditorType),
               Prim(numberFormat))


    companion object : Factory<StoryPartVariable>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<StoryPartVariable> = when (doc)
        {
            is DocDict ->
            {
                apply(::StoryPartVariable,
                      // Format
                      split(doc.maybeAt("format"),
                            effValue(ElementFormat.default()),
                            { ElementFormat.fromDocument(it) }),
                      // Text Styel
                      split(doc.maybeAt("text_style"),
                             effValue(TextStyle.default()),
                             { TextStyle.fromDocument(it) }),
                      // Variable Id
                      doc.at("variable_id") ap { VariableId.fromDocument(it) },
                      // Numeric Editor Type
                      split(doc.maybeAt("numeric_editor_type"),
                            effValue<ValueError,Maybe<NumericEditorType>>(Nothing()),
                            { effApply(::Just, NumericEditorType.fromDocument(it)) }),
                      // Number Format
                      split(doc.maybeAt("number_format"),
                            effValue<ValueError,NumberFormat>(NumberFormat.Normal),
                            { NumberFormat.fromDocument(it) })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "format" to this.format().toDocument(),
        "text_style" to this.textStyle().toDocument(),
        "variable_id" to this.variableId().toDocument()
    ))
    .maybeMerge(this.numericEditorTypeMaybe().apply {
        Just(Pair("numeric_editor_type", it.toDocument())) })


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun format() : ElementFormat = this.format.value

    fun textStyle() : TextStyle = this.textStyle.value

    fun variableId() : VariableId = this.variableId.value

    fun valueVariable(sheetContext : SheetContext) : AppEff<Variable> =
        SheetManager.sheetState(sheetContext.sheetId)
                .apply { it.variableWithId(this.variableId()) }

    fun numericEditorTypeMaybe() : Maybe<NumericEditorType> = _getMaybePrim(this.numericEditorType)

    fun numericEditorType() : NumericEditorType? = getMaybePrim(this.numericEditorType)

    fun numberFormat() : NumberFormat = this.numberFormat.value


    fun valueString(sheetContext : SheetContext) : String
    {
        val str = this.variable(sheetContext)?.valueString(sheetContext)
        when (str)
        {
            is Val -> return str.value
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

    override val modelObject = this

    override val name = "story_part_variable"

}


/**
 * Story Part Icon
 */
data class StoryPartIcon(override val id : UUID,
                         val icon : Prim<Icon>,
                         val iconFormat : Comp<IconFormat>) : StoryPart(), Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(icon : Icon,
                format : IconFormat)
        : this(UUID.randomUUID(),
               Prim(icon),
               Comp(format))


    companion object : Factory<StoryPartIcon>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<StoryPartIcon> = when (doc)
        {
            is DocDict -> effApply(::StoryPartIcon,
                                   // Icon
                                   doc.at("icon") ap { Icon.fromDocument(it) },
                                   // Format
                                   split(doc.maybeAt("icon_format"),
                                         effValue(IconFormat.default()),
                                         { IconFormat.fromDocument(it) })
                                   )
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "icon" to this.icon().toDocument(),
        "icon_format" to this.iconFormat().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun icon() : Icon = this.icon.value

    fun iconFormat() : IconFormat = this.iconFormat.value


    // -----------------------------------------------------------------------------------------
    // STORY PART
    // -----------------------------------------------------------------------------------------

    override fun wordCount() = 0


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val modelObject = this

    override val name = "story_part_variable"

}


/**
 * Story Part Roll
 */
data class StoryPartAction(override val id : UUID,
                           val text : Prim<StoryPartText>,
                           val rollSummationId : Maybe<Prim<SummationId>>,
                           val procedureId : Maybe<Prim<ProcedureId>>,
                           val format : Comp<ElementFormat>,
                           val textStyle : Comp<TextStyle>,
                           val iconFormat : Comp<IconFormat>,
                           val showProcedureDialog : Prim<ShowProcedureDialog>)
                            : StoryPart(), Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(text : StoryPartText,
                rollSummationId : Maybe<SummationId>,
                procedureId : Maybe<ProcedureId>,
                format : ElementFormat,
                textStyle : TextStyle,
                iconFormat : IconFormat,
                showProcedureDialog : ShowProcedureDialog)
        : this(UUID.randomUUID(),
               Prim(text),
               maybeLiftPrim(rollSummationId),
               maybeLiftPrim(procedureId),
               Comp(format),
               Comp(textStyle),
               Comp(iconFormat),
               Prim(showProcedureDialog))


    companion object : Factory<StoryPartAction>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<StoryPartAction> = when (doc)
        {
            is DocDict ->
            {
                apply(::StoryPartAction,
                      // Text
                      doc.at("text") ap { StoryPartText.fromDocument(it) },
                      // Roll Summation Id
                      split(doc.maybeAt("roll_summation_id"),
                            effValue<ValueError,Maybe<SummationId>>(Nothing()),
                            { effApply(::Just, SummationId.fromDocument(it)) }),
                      // Procedure Id
                      split(doc.maybeAt("procedure_id"),
                            effValue<ValueError,Maybe<ProcedureId>>(Nothing()),
                            { effApply(::Just, ProcedureId.fromDocument(it)) }),
                      // Format
                      split(doc.maybeAt("format"),
                            effValue(ElementFormat.default()),
                            { ElementFormat.fromDocument(it) }),
                      // Text Style
                      split(doc.maybeAt("text_style"),
                            effValue(TextStyle.default()),
                            { TextStyle.fromDocument(it) }),
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
        "text" to this.text().toDocument(),
        "format" to this.format().toDocument(),
        "text_style" to this.textStyle().toDocument(),
        "icon_format" to this.iconFormat().toDocument(),
        "show_procedure_dialog" to this.showProcedureDialog().toDocument()
        ))
        .maybeMerge(this.rollSummationIdMaybe().apply {
            Just(Pair("roll_summation_id", it.toDocument() as SchemaDoc)) })
        .maybeMerge(this.procedureIdMaybe().apply {
            Just(Pair("procedure_id", it.toDocument() as SchemaDoc)) })


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun rollSummationIdMaybe() : Maybe<SummationId> = _getMaybePrim(this.rollSummationId)

    fun rollSummationId() : SummationId? = getMaybePrim(this.rollSummationId)

    fun procedureIdMaybe() : Maybe<ProcedureId> = _getMaybePrim(this.procedureId)

    fun procedureId() : ProcedureId? = getMaybePrim(this.procedureId)

    fun text() : StoryPartText = this.text.value

    fun textString() : String = this.text.value.value

    fun format() : ElementFormat = this.format.value

    fun textStyle() : TextStyle = this.textStyle.value

    fun iconFormat() : IconFormat = this.iconFormat.value

    fun showProcedureDialog() : ShowProcedureDialog = this.showProcedureDialog.value


    // -----------------------------------------------------------------------------------------
    // STORY PART
    // -----------------------------------------------------------------------------------------

    override fun wordCount() = this.textString().split(' ').size


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val modelObject = this

    override val name = "story_part_action"

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


object StoryWidgetView
{


    fun view(storyWidget : StoryWidget,
             sheetUIContext : SheetUIContext) : View
    {
        val layout = WidgetView.layout(storyWidget.widgetFormat(), sheetUIContext)

        val wc = storyWidget.story().map { it.wordCount() }.sum()
        if (wc <= 5 && storyWidget.actionParts().isEmpty())
        {
            layout.addView(this.storyFlexView(storyWidget, sheetUIContext))
        }
        else
        {
            val layoutViewId = Util.generateViewId()
            storyWidget.layoutViewId = layoutViewId
            layout.id                = layoutViewId
            val spanView = this.storySpannableView(storyWidget, sheetUIContext)


            layout.addView(spanView)
        }

        // Layout on click
        val variableParts = storyWidget.variableParts()
        if (variableParts.size == 1)
        {
            var variablePartIndex = 0
            storyWidget.story().forEachIndexed { index, storyPart ->
                when (storyPart) {
                    is StoryPartVariable -> variablePartIndex = index
                }
            }
            val variable = variableParts.first().variable(SheetContext(sheetUIContext))
            if (variable != null) {
//                layout.setOnClickListener {
//                    openVariableEditorDialog(
//                            variable,
//                            UpdateTargetStoryWidgetPart(storyWidget.id, variablePartIndex),
//                            sheetUIContext)
//                }
            }

        }


        return layout
    }


    fun storySpannableView(storyWidget : StoryWidget,
                           sheetUIContext : SheetUIContext) : TextView
    {
        val story           = TextViewBuilder()

        story.width         = LinearLayout.LayoutParams.MATCH_PARENT
        story.height        = LinearLayout.LayoutParams.WRAP_CONTENT

        story.textSpan      = this.spannableStringBuilder(storyWidget.story(),
                                                          storyWidget.id,
                                                          sheetUIContext)

        story.lineSpacingAdd    = 0f
        story.lineSpacingMult   = storyWidget.format().lineSpacingFloat()

        story.gravity       = storyWidget.widgetFormat().alignment().gravityConstant()
        story.layoutGravity = storyWidget.widgetFormat().alignment().gravityConstant()

        when (storyWidget.format().verticalAlignment()) {
            is VerticalAlignment.Middle -> {
                story.layoutGravity = storyWidget.widgetFormat().alignment().gravityConstant() or
                                        Gravity.CENTER_VERTICAL
                story.gravity       = storyWidget.widgetFormat().alignment().gravityConstant() or
                                        Gravity.CENTER_VERTICAL
            }
        }

        story.movementMethod    = LinkMovementMethod.getInstance()

        val gestureDetector = openWidgetOptionsDialogOnDoubleTap(
                sheetUIContext.context as SheetActivity,
                storyWidget,
                SheetContext(sheetUIContext))
        story.onTouch   = View.OnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            false
        }

        return story.textView(sheetUIContext.context)
    }


    data class Phrase(val storyPart : StoryPart,
                      val text : String,
                      val partIndex : Int,
                      val start : Int,
                      val end : Int)


    fun spannableStringBuilder(storyParts : List<StoryPart>,
                               storyWidgetId : UUID,
                               sheetUIContext : SheetUIContext) : SpannableStringBuilder
    {

        val builder = SpannableStringBuilder()

        val sheetContext = SheetContext(sheetUIContext)

        val phrases : MutableList<Phrase> = mutableListOf()

        var index = 0
        storyParts.forEachIndexed { partIndex, storyPart ->

            when (storyPart)
            {
                is StoryPartSpan ->
                {
                    val text = storyPart.textString()
                    builder.append(text)
                    val textLen = text.length
                    phrases.add(Phrase(storyPart, text, partIndex, index, index + textLen))
                    index += textLen
                }
                is StoryPartVariable ->
                {
                    val valueVariableEff = storyPart.valueVariable(sheetContext)
                    // TODO move this logic in variable
                    val text = when (valueVariableEff)
                    {
                        is Val ->
                        {
                            val valueVar = valueVariableEff.value
                            when (valueVar)
                            {
                                is NumberVariable -> {
                                    val number = valueVar.valueOrZero(sheetContext)
                                    storyPart.numberFormat().formattedString(number)
                                }
                                else -> storyPart.valueString(SheetContext(sheetUIContext))
                            }
                        }
                        is Err -> {
                            ApplicationLog.error(valueVariableEff.error)
                            ""
                        }

                    }

                    builder.append(text)

                    val textLen = text.length
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
                    builder.append("  " + text)
                    val textLen = text.length + 2
                    phrases.add(Phrase(storyPart, text, partIndex, index, index + textLen))
                    index += textLen
                }
            }
        }

        phrases.forEach { (storyPart, _, partIndex, start, end) ->

            when (storyPart)
            {
                is StoryPartSpan ->
                {
                    this.formatSpans(storyPart.textStyle(), sheetUIContext).forEach {
                        builder.setSpan(it, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                    }
                }
                is StoryPartVariable ->
                {
                    this.formatSpans(storyPart.textStyle(), sheetUIContext).forEach {
                        builder.setSpan(it, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                    }

                    val clickSpan = object: ClickableSpan()
                    {
                        override fun onClick(view : View?)
                        {
                            val variable = storyPart.variable(SheetContext(sheetUIContext))
                            if (variable != null) {
                                openVariableEditorDialog(
                                        variable,
                                        storyPart.numericEditorType(),
                                        UpdateTargetStoryWidgetPart(storyWidgetId, partIndex),
                                        sheetUIContext)
                            }

                        }

                        override fun updateDrawState(ds: TextPaint?) {
                           // super.updateDrawState(ds)
                        }
                    }

                    builder.setSpan(clickSpan, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                }
                is StoryPartIcon ->
                {
                    val vectorDrawable =
                            VectorDrawableCompat.create(sheetUIContext.context.resources,
                                                        storyPart.icon().drawableResId(), null)

                    vectorDrawable?.setBounds(
                            0,
                            0,
                            Util.dpToPixel(storyPart.iconFormat().size().width.toFloat()),
                            Util.dpToPixel(storyPart.iconFormat().size().height.toFloat()))

                    val color = SheetManager.color(sheetUIContext.sheetId,
                                                   storyPart.iconFormat().colorTheme())
                    vectorDrawable?.colorFilter = PorterDuffColorFilter(color,
                                                                        PorterDuff.Mode.SRC_IN)

                    val imageSpan = CenteredImageSpan(vectorDrawable)
                    builder.setSpan(imageSpan, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                }
                is StoryPartAction ->
                {
                    val vectorDrawable =
                            VectorDrawableCompat.create(sheetUIContext.context.resources,
                                                        R.drawable.icon_dice_roll_filled,
                                                        null)

                    vectorDrawable?.setBounds(
                            0,
                            0,
                            Util.dpToPixel(storyPart.iconFormat().size().width.toFloat()),
                            Util.dpToPixel(storyPart.iconFormat().size().height.toFloat()))

                    val color = SheetManager.color(sheetUIContext.sheetId,
                                                   storyPart.iconFormat().colorTheme())
                    vectorDrawable?.colorFilter = PorterDuffColorFilter(color,
                                                                        PorterDuff.Mode.SRC_IN)

                    val imageSpan = CenteredImageSpan(vectorDrawable)
                    builder.setSpan(imageSpan, start, start + 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE)

                    this.formatSpans(storyPart.textStyle(), sheetUIContext).forEach {
                        builder.setSpan(it, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                    }

                    val procedureId = storyPart.procedureId()
                    val summationId = storyPart.rollSummationId()
                    Log.d("***STORYWIDGET", "here???")
                    if (procedureId != null) {
                        val clickSpan = object : ClickableSpan() {
                            override fun onClick(view: View?) {
                            }

                            override fun updateDrawState(ds: TextPaint?) {
                            }
                        }

                        builder.setSpan(clickSpan, start + 1, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                    }
                    else if (summationId != null)
                    {
                        Log.d("***STORYWIDGET", "summation id is not null")

                        val sheetContext = SheetContext(sheetUIContext)

                        val diceRoll = SheetManager.summation(summationId, sheetContext)
                                                    .apply { it.diceRoll(sheetContext) }

                        when (diceRoll)
                        {
                            is Val ->
                            {
                                val sheetActivity = sheetUIContext.context as SheetActivity
                                val clickSpan = object : ClickableSpan() {
                                    override fun onClick(view: View?) {
                                        val dialog = DiceRollDialog.newInstance(
                                                                    diceRoll.value,
                                                                    Nothing(),
                                                                    SheetContext(sheetUIContext))
                                        dialog.show(sheetActivity.supportFragmentManager, "")
                                    }

                                    override fun updateDrawState(ds: TextPaint?) {
                                    }
                                }

                                builder.setSpan(clickSpan, start + 1, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                            }
                            is Err -> ApplicationLog.error(diceRoll.error)
                        }
                    }

                }
            }
        }

        return builder
    }


    fun formatSpans(textStyle : TextStyle, sheetUIContext : SheetUIContext) : List<Any>
    {
        val sizePx = Util.spToPx(textStyle.sizeSp(), sheetUIContext.context)
        val sizeSpan = AbsoluteSizeSpan(sizePx)

        val typeface = Font.typeface(textStyle.font(), textStyle.fontStyle(), sheetUIContext.context)

        val typefaceSpan = CustomTypefaceSpan(typeface)

        var color = SheetManager.color(sheetUIContext.sheetId, textStyle.colorTheme())
        val colorSpan = ForegroundColorSpan(color)

        return listOf(sizeSpan, typefaceSpan, colorSpan)
    }


    fun storyFlexView(storyWidget : StoryWidget,
                      sheetUIContext : SheetUIContext) : FlexboxLayout
    {
        val layout = this.storyViewLayout(storyWidget.format(), sheetUIContext)

        val sheetContext = SheetContext(sheetUIContext)

        val storyParts = storyWidget.story()

        storyParts.forEachIndexed { partIndex, storyPart ->
            when (storyPart)
            {
                is StoryPartSpan ->
                {
                    this.words(storyPart.textString()).forEach {
                        layout.addView(this.wordView(it, storyWidget.format(),
                                                     storyPart, sheetUIContext))
                    }
                }
                is StoryPartVariable ->
                {
                    val valueVariableEff = storyPart.valueVariable(sheetContext)
                    // TODO move this logic in variable
                    val text = when (valueVariableEff)
                    {
                        is Val ->
                        {
                            val valueVar = valueVariableEff.value
                            when (valueVar)
                            {
                                is NumberVariable -> {
                                    val number = valueVar.valueOrZero(sheetContext)
                                    storyPart.numberFormat().formattedString(number)
                                }
                                else -> storyPart.valueString(SheetContext(sheetUIContext))
                            }
                        }
                        is Err -> {
                            ApplicationLog.error(valueVariableEff.error)
                            ""
                        }

                    }

                    this.words(text).forEach {
                        layout.addView(this.wordVariableView(it,
                                                             storyWidget.format(),
                                                             storyPart,
                                                             partIndex,
                                                             storyWidget.id,
                                                             sheetUIContext))
                    }
                }
                is StoryPartIcon ->
                {
                    layout.addView(this.iconView(storyPart, sheetUIContext))
                }
            }
        }

        return layout
    }


    fun words(valueString : String) : List<String> =
        valueString.split(" ").mapIndexed { index, word ->
            if (index != 0)
                " " + word
            else
                word
        }


    fun storyViewLayout(format : StoryWidgetFormat, sheetUIContext : SheetUIContext) : FlexboxLayout
    {
        val layout = FlexboxLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT

//        layout.gravity          = format.alignment().gravityConstant() or
//                                        format.alignment().gravityConstant()

        when (format.widgetFormat().alignment()) {
            is Alignment.Center -> layout.justification = JustifyContent.CENTER
        }

//        layout.backgroundColor  = SheetManager.color(sheetUIContext.sheetId,
//                                                     format.backgroundColorTheme())

//        layout.corners          = format.corners()

        layout.direction        = FlexDirection.ROW
        layout.wrap             = FlexWrap.WRAP

        when (format.verticalAlignment()) {
            is VerticalAlignment.Middle -> layout.itemAlignment = AlignItems.CENTER
        }

        return layout.flexboxLayout(sheetUIContext.context)

    }


    fun wordView(word : String,
                 format : StoryWidgetFormat,
                 storyPartSpan : StoryPartSpan,
                 sheetUIContext: SheetUIContext) : View
    {
        val text = TextViewBuilder()

        text.layoutType     = LayoutType.FLEXBOX
        text.width          = FlexboxLayout.LayoutParams.WRAP_CONTENT
        text.height         = FlexboxLayout.LayoutParams.WRAP_CONTENT

        text.text           = word

        val formatPadding   = storyPartSpan.format().padding()
        val padding         = Spacing(LeftSpacing(formatPadding.leftDp()),
                                      TopSpacing(formatPadding.topDp() + format.lineSpacingFloat()),
                                      RightSpacing(formatPadding.rightDp()),
                                      BottomSpacing(formatPadding.bottomDp()))

        text.paddingSpacing = padding
        text.marginSpacing  = storyPartSpan.format().margins()

        storyPartSpan.textStyle().styleTextViewBuilder(text, sheetUIContext)

        return text.textView(sheetUIContext.context)
    }


    fun wordVariableView(word : String,
                         format : StoryWidgetFormat,
                         storyPart : StoryPartVariable,
                         partIndex : Int,
                         widgetId : UUID,
                         sheetUIContext : SheetUIContext) : View
    {
        val text = TextViewBuilder()

        text.layoutType     = LayoutType.FLEXBOX
        text.width          = FlexboxLayout.LayoutParams.WRAP_CONTENT
        text.height         = FlexboxLayout.LayoutParams.WRAP_CONTENT

        text.text           = word

        storyPart.viewId   = Util.generateViewId()
        text.id            = storyPart.viewId

        val formatPadding   = storyPart.format().padding()
        val padding         = Spacing(LeftSpacing(formatPadding.leftDp()),
                                      TopSpacing(formatPadding.topDp() + format.lineSpacingFloat()),
                                      RightSpacing(formatPadding.rightDp()),
                                      BottomSpacing(formatPadding.bottomDp()))

        text.paddingSpacing = padding
        text.marginSpacing  = storyPart.format().margins()

        storyPart.textStyle().styleTextViewBuilder(text, sheetUIContext)

        text.onClick        = View.OnClickListener {
            val variable = storyPart.variable(SheetContext(sheetUIContext))
            if (variable != null) {
                openVariableEditorDialog(variable,
                        storyPart.numericEditorType(),
                        UpdateTargetStoryWidgetPart(widgetId, partIndex),
                        sheetUIContext)
            }
        }

        return text.textView(sheetUIContext.context)
    }


    fun iconView(storyPart : StoryPartIcon,
                 sheetUIContext : SheetUIContext) : View
    {
        val icon            = ImageViewBuilder()

        icon.layoutType     = LayoutType.FLEXBOX

        icon.widthDp        = storyPart.iconFormat().size().width
        icon.heightDp       = storyPart.iconFormat().size().height

        icon.image          = storyPart.icon().drawableResId()

        icon.iconSize       = storyPart.iconFormat().size()

        icon.color          = SheetManager.color(sheetUIContext.sheetId,
                                                 storyPart.iconFormat().colorTheme())

        return icon.imageView(sheetUIContext.context)
    }
    }


