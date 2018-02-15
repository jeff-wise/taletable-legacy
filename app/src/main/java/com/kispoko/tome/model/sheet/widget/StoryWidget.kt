
package com.kispoko.tome.model.sheet.widget


import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.support.graphics.drawable.VectorDrawableCompat
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.AbsoluteSizeSpan
import android.text.style.BackgroundColorSpan
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
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
import com.kispoko.tome.lib.orm.*
import com.kispoko.tome.lib.orm.schema.MaybePrimValue
import com.kispoko.tome.lib.orm.schema.PrimValue
import com.kispoko.tome.lib.orm.schema.ProdValue
import com.kispoko.tome.lib.orm.sql.*
import com.kispoko.tome.model.game.engine.variable.NumberVariable
import com.kispoko.tome.model.theme.ColorTheme
import maybe.Just
import maybe.Maybe
import maybe.Nothing


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
        override fun fromDocument(doc : SchemaDoc): ValueParser<StoryPartSpan> = when (doc)
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


    override fun rowValue() : DB_WidgetStoryPartSpanValue =
        RowValue2(widgetStoryPartSpanTable,
                  ProdValue(this.textFormat),
                  PrimValue(this.text))

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


    override fun rowValue() : DB_WidgetStoryPartVariableValue =
        RowValue3(widgetStoryPartVariableTable,
                  ProdValue(this.textFormat),
                  PrimValue(this.variableId),
                  PrimValue(this.numericEditorType))

}


/**
 * Story Part Icon
 */
data class StoryPartIcon(override val id : UUID,
                         val icon : Icon)
                          : StoryPart(), Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(icon : Icon) : this(UUID.randomUUID(), icon)


    companion object : Factory<StoryPartIcon>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<StoryPartIcon> = when (doc)
        {
            is DocDict -> apply(::StoryPartIcon,
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
        "icon" to this.icon().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

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
        RowValue1(widgetStoryPartIconTable,
                  ProdValue(this.icon))

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


    override fun rowValue() : DB_WidgetStoryPartActionValue =
        RowValue5(widgetStoryPartActionTable,
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



class StoryWidgetViewBuilder(val storyWidget : StoryWidget, val sheetUIContext : SheetUIContext)
{

    fun view() : View
    {
        val layout = WidgetView.layout(storyWidget.widgetFormat(), sheetUIContext)

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
        val contentLayout = layout.findViewById(R.id.widget_content_layout) as LinearLayout

        contentLayout.removeAllViews()

        val spanView = storySpannableView(storyWidget, sheetUIContext)
        contentLayout.addView(spanView)
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
                    words(storyPart.textString()).forEach {
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

                    words(text).forEach {
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
                                      formatPadding.topDp().toDouble(), // + format.lineSpacingFloat(),
                                      formatPadding.rightDp().toDouble(),
                                      formatPadding.bottomDp().toDouble())// + format.lineSpacingFloat())

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

        text.backgroundColor = SheetManager.color(
                                sheetUIContext.sheetId,
                                wordFormat.elementFormat().backgroundColorTheme())

        text.corners        = wordFormat.elementFormat().corners()

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

        val iconFormat      = storyPart.icon().iconFormat()

        icon.layoutType     = LayoutType.FLEXBOX

        icon.widthDp        = iconFormat.size().width
        icon.heightDp       = iconFormat.size().height

        icon.image          = storyPart.icon().iconType().drawableResId()

        icon.iconSize       = iconFormat.size()

        icon.color          = SheetManager.color(sheetUIContext.sheetId,
                                                 iconFormat.colorTheme())

        return icon.imageView(sheetUIContext.context)
    }

}


fun storySpannableView(storyWidget : StoryWidget,
                       sheetUIContext : SheetUIContext) : TextView
{
    val story           = TextViewBuilder()

    story.width         = LinearLayout.LayoutParams.MATCH_PARENT
//        story.widthDp       = 300
    story.height        = LinearLayout.LayoutParams.WRAP_CONTENT

    story.textSpan      = spannableStringBuilder(storyWidget.story(),
                                                 storyWidget.id,
                                                 storyWidget.format().lineHeight(),
                                                 storyWidget.format().lineSpacing(),
                                                 sheetUIContext)

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




private fun spannableStringBuilder(storyParts : List<StoryPart>,
                                   storyWidgetId : UUID,
                                   lineHeight : Maybe<LineHeight>,
                                   lineSpacing : Maybe<LineSpacing>,
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
                val valueVariableEff = storyPart.valueVariable(sheetContext)
                // TODO move this logic in variable
                var text = when (valueVariableEff)
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
                formatSpans(storyPart.textFormat(), lineHeight, lineSpacing, sheetUIContext).forEach {
                    builder.setSpan(it, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                }
            }
            is StoryPartVariable ->
            {

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
                        //super.updateDrawState(ds)
                    }
                }

                builder.setSpan(clickSpan, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE)

                formatSpans(storyPart.textFormat(), lineHeight, lineSpacing, sheetUIContext).forEach {
                    builder.setSpan(it, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                }
            }
            is StoryPartIcon ->
            {
                val vectorDrawable =
                        VectorDrawableCompat.create(sheetUIContext.context.resources,
                                                    storyPart.icon().iconType().drawableResId(), null)

                vectorDrawable?.setBounds(
                        0,
                        0,
                        Util.dpToPixel(storyPart.icon().iconFormat().size().width.toFloat()),
                        Util.dpToPixel(storyPart.icon().iconFormat().size().height.toFloat()))

                val color = SheetManager.color(sheetUIContext.sheetId,
                                               storyPart.icon().iconFormat().colorTheme())
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
                formatSpans(storyPart.textFormat(), lineHeight, lineSpacing, sheetUIContext, vectorDrawable?.mutate(), storyPart.iconFormat()).forEach {
                    builder.setSpan(it, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                }

                //builder.setSpan(imageSpan, start, start + 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE)

                val procedureId = storyPart.action().procedureId()
                val rollGroup = storyPart.action().rollGroup()

                when (rollGroup)
                {
                    is Just ->
                    {
                        val sheetActivity = sheetUIContext.context as SheetActivity
                        val clickSpan = object : ClickableSpan() {
                            override fun onClick(view: View?) {
                                val dialog = DiceRollDialog.newInstance(
                                                            rollGroup.value,
                                                            SheetContext(sheetUIContext))
                                dialog.show(sheetActivity.supportFragmentManager, "")
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
                        sheetUIContext : SheetUIContext,
                        drawable : Drawable? = null,
                        iconFormat : IconFormat? = null) : List<Any>
{
    val sizePx = Util.spToPx(textStyle.sizeSp(), sheetUIContext.context)
    val sizeSpan = AbsoluteSizeSpan(sizePx)

    val typeface = Font.typeface(textStyle.font(), textStyle.fontStyle(), sheetUIContext.context)

    val typefaceSpan = CustomTypefaceSpan(typeface)

    var color = SheetManager.color(sheetUIContext.sheetId, textStyle.colorTheme())
    val colorSpan = ForegroundColorSpan(color)

    var bgColor = SheetManager.color(sheetUIContext.sheetId,
                                     textStyle.elementFormat().backgroundColorTheme())
    val bgColorSpan = BackgroundColorSpan(bgColor)

    val iconColor : Int? = iconFormat?.let {
        SheetManager.color(sheetUIContext.sheetId, it.colorTheme())
    }


    if (iconColor != null) {
        drawable?.colorFilter = PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_IN)
    }

    return when (lineHeight) {
        is Just -> when (lineSpacing) {
            is Just -> {
                val lineSpacingPx = Util.spToPx(lineSpacing.value.value, sheetUIContext.context)
                val lineHeightPx = Util.spToPx(lineHeight.value.value, sheetUIContext.context)
                val bgSpan = RoundedBackgroundHeightSpan(lineHeightPx,
                                                         lineSpacingPx,
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


