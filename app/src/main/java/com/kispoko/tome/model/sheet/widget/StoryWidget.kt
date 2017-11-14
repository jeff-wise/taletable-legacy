
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
import com.kispoko.tome.lib.model.ProdType
import com.kispoko.tome.lib.ui.*
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
import com.kispoko.tome.db.*
import com.kispoko.tome.lib.functor.*
import com.kispoko.tome.lib.functor.Val
import com.kispoko.tome.lib.orm.sql.*
import com.kispoko.tome.model.game.engine.variable.NumberVariable



/**
 * Story Widget Format
 */
data class StoryWidgetFormat(override val id : UUID,
                             val widgetFormat : WidgetFormat,
                             val lineSpacing : LineSpacing)
                              : ToDocument, ProdType, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(widgetFormat : WidgetFormat,
                lineSpacing : LineSpacing)
        : this(UUID.randomUUID(),
               widgetFormat,
               lineSpacing)


    companion object : Factory<StoryWidgetFormat>
    {

        private fun defaultWidgetFormat()      = WidgetFormat.default()
        private fun defaultLineSpacing()       = LineSpacing.default()


        override fun fromDocument(doc : SchemaDoc) : ValueParser<StoryWidgetFormat> = when (doc)
        {
            is DocDict ->
            {
                apply(::StoryWidgetFormat,
                      // Widget Format
                      split(doc.maybeAt("widget_format"),
                            effValue(defaultWidgetFormat()),
                            { WidgetFormat.fromDocument(it) }),
                      // Line Spacing
                      split(doc.maybeAt("line_spacing"),
                            effValue(defaultLineSpacing()),
                            { LineSpacing.fromDocument(it) })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = StoryWidgetFormat(defaultWidgetFormat(),
                                          defaultLineSpacing())

    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "widget_format" to this.widgetFormat().toDocument(),
        "line_spacing" to this.lineSpacing().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun widgetFormat() : WidgetFormat = this.widgetFormat


    fun lineSpacing() : LineSpacing = this.lineSpacing


    fun lineSpacingFloat() : Float = this.lineSpacing.value


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun row() : DB_WidgetStoryFormat =
            dbWidgetStoryFormat(this.widgetFormat, this.lineSpacing)

}


@Suppress("UNCHECKED_CAST")
sealed class StoryPart : ToDocument, ProdType, Serializable
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

    open fun variable(sheetContext : SheetContext) : Variable? = when (this)
    {
        is StoryPartVariable -> {
            val variable = this.valueVariable(sheetContext)
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
 * Story Part Span
 */
data class StoryPartSpan(override val id : UUID,
                         val textFormat : TextFormat,
                         val text : StoryPartText) : StoryPart(), Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(textFormat : TextFormat,
                text : StoryPartText)
        : this(UUID.randomUUID(),
               textFormat,
               text)


    companion object : Factory<StoryPartSpan>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<StoryPartSpan> = when (doc)
        {
            is DocDict ->
            {
                apply(::StoryPartSpan,
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
        "text_format" to this.textFormat.toDocument(),
        "text" to this.text().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

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


    override fun row() : DB_WidgetStoryPartSpan =
            dbWidgetStoryPartSpan(this.textFormat, this.text)

}


/**
 * Story Part Variable
 */
data class StoryPartVariable(override val id : UUID,
                             val textFormat : TextFormat,
                             val variableId : VariableId,
                             val numericEditorType : NumericEditorType)
                              : StoryPart(), Serializable
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var viewId : Int? = null


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(textFormat : TextFormat,
                variableId : VariableId,
                numericEditorType : NumericEditorType)
        : this(UUID.randomUUID(),
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
        "text_format" to this.textFormat.toDocument(),
        "variable_id" to this.variableId.toDocument(),
        "numeric_editor_type" to this.numericEditorType().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun textFormat() : TextFormat = this.textFormat


    fun variableId() : VariableId = this.variableId


    fun valueVariable(sheetContext : SheetContext) : AppEff<Variable> =
        SheetManager.sheetState(sheetContext.sheetId)
                .apply { it.variableWithId(this.variableId()) }


    fun numericEditorType() : NumericEditorType = this.numericEditorType


    fun valueString(sheetContext : SheetContext) : String
    {
        val str = this.variable(sheetContext)?.valueString(sheetContext)
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


    override fun row() : DB_WidgetStoryPartVariable =
            dbWidgetStoryPartVariable(this.textFormat, this.variableId, this.numericEditorType)

}


/**
 * Story Part Icon
 */
data class StoryPartIcon(override val id : UUID,
                         val icon : Icon,
                         val iconFormat : IconFormat) : StoryPart(), Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(icon : Icon,
                format : IconFormat)
        : this(UUID.randomUUID(),
               icon,
               format)


    companion object : Factory<StoryPartIcon>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<StoryPartIcon> = when (doc)
        {
            is DocDict -> apply(::StoryPartIcon,
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

    fun icon() : Icon = this.icon

    fun iconFormat() : IconFormat = this.iconFormat


    // -----------------------------------------------------------------------------------------
    // STORY PART
    // -----------------------------------------------------------------------------------------

    override fun wordCount() = 0


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun row() : DB_WidgetStoryPartIcon =
            dbWidgetStoryPartIcon(this.icon, this.iconFormat)

}


/**
 * Story Part Roll
 */
data class StoryPartAction(override val id : UUID,
                           val text : StoryPartText,
                           val action : Action,
                           val textFormat : TextFormat,
                           val iconFormat : IconFormat,
                           val showProcedureDialog : ShowProcedureDialog)
                            : StoryPart(), Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(text : StoryPartText,
                action : Action,
                textFormat : TextFormat,
                iconFormat : IconFormat,
                showProcedureDialog : ShowProcedureDialog)
        : this(UUID.randomUUID(),
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
        "text" to this.text.toDocument(),
        "action" to this.action().toDocument(),
        "text_format" to this.textFormat.toDocument(),
        "icon_format" to this.iconFormat().toDocument(),
        "show_procedure_dialog" to this.showProcedureDialog().toDocument()
        ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------


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


    override fun row() : DB_WidgetStoryPartAction =
            dbWidgetStoryPartAction(this.text,
                                    this.action,
                                    this.textFormat,
                                    this.iconFormat,
                                    this.showProcedureDialog)

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
                        is effect.Val ->
                        {
                            val valueVar = valueVariableEff.value
                            when (valueVar)
                            {
                                is NumberVariable -> {
                                    val number = valueVar.valueOrZero(sheetContext)
                                    storyPart.textFormat().numberFormat().formattedString(number)
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
                    this.formatSpans(storyPart.textFormat(), sheetUIContext).forEach {
                        builder.setSpan(it, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                    }
                }
                is StoryPartVariable ->
                {
                    this.formatSpans(storyPart.textFormat(), sheetUIContext).forEach {
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

                    this.formatSpans(storyPart.textFormat(), sheetUIContext).forEach {
                        builder.setSpan(it, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                    }

                    val procedureId = storyPart.action().procedureId()
                    val summationId = storyPart.action().rollSummationId()
                    Log.d("***STORYWIDGET", "here???")
//                    if (procedureId != null) {
//                        val clickSpan = object : ClickableSpan() {
//                            override fun onClick(view: View?) {
//                            }
//
//                            override fun updateDrawState(ds: TextPaint?) {
//                            }
//                        }
//
//                        builder.setSpan(clickSpan, start + 1, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
//                    }
//                    else if (summationId != null)
//                    {

                    when (summationId)
                    {
                        is Just ->
                        {
                            val diceRoll = SheetManager.summation(summationId.value, sheetContext)
                                                        .apply { it.diceRoll(sheetContext) }

                            when (diceRoll)
                            {
                                is effect.Val ->
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
        }

        return builder
    }


    fun formatSpans(textStyle : TextFormat, sheetUIContext : SheetUIContext) : List<Any>
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
                        is effect.Val ->
                        {
                            val valueVar = valueVariableEff.value
                            when (valueVar)
                            {
                                is NumberVariable -> {
                                    val number = valueVar.valueOrZero(sheetContext)
                                    storyPart.textFormat().numberFormat().formattedString(number)
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

        val formatPadding   = storyPartSpan.textFormat().elementFormat().padding()
        val padding         = Spacing(formatPadding.leftDp().toDouble(),
                                      formatPadding.topDp().toDouble() + format.lineSpacingFloat(),
                                      formatPadding.rightDp().toDouble(),
                                      formatPadding.bottomDp().toDouble())

        text.paddingSpacing = padding
        text.marginSpacing  = storyPartSpan.textFormat().elementFormat().margins()

        storyPartSpan.textFormat().styleTextViewBuilder(text, sheetUIContext)

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

        val formatPadding   = storyPart.textFormat().elementFormat().padding()
        val padding         = Spacing(formatPadding.leftDp().toDouble(),
                                    formatPadding.topDp().toDouble() + format.lineSpacingFloat(),
                                    formatPadding.rightDp().toDouble(),
                                    formatPadding.bottomDp().toDouble())

        text.paddingSpacing = padding
        text.marginSpacing  = storyPart.textFormat().elementFormat().margins()

        storyPart.textFormat().styleTextViewBuilder(text, sheetUIContext)

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


