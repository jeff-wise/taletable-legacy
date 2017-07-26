
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
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.flexbox.*
import com.kispoko.tome.R
import com.kispoko.tome.activity.sheet.SheetActivity
import com.kispoko.tome.activity.sheet.dialog.DiceRollerDialogFragment
import com.kispoko.tome.activity.sheet.dialog.openVariableEditorDialog
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Comp
import com.kispoko.tome.lib.functor.Prim
import com.kispoko.tome.lib.functor.getMaybePrim
import com.kispoko.tome.lib.functor.maybeLiftPrim
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.lib.orm.sql.SQLReal
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.lib.ui.*
import com.kispoko.tome.model.game.engine.dice.DiceRollName
import com.kispoko.tome.model.game.engine.procedure.ProcedureId
import com.kispoko.tome.model.game.engine.summation.SummationId
import com.kispoko.tome.model.game.engine.variable.Variable
import com.kispoko.tome.model.sheet.style.*
import com.kispoko.tome.rts.game.GameManager
import com.kispoko.tome.rts.sheet.SheetContext
import com.kispoko.tome.rts.sheet.SheetManager
import com.kispoko.tome.rts.sheet.SheetUIContext
import com.kispoko.tome.util.Util
import effect.*
import lulo.document.*
import lulo.value.*
import lulo.value.UnexpectedType
import java.io.Serializable
import java.util.*



/**
 * Story Widget Format
 */
data class StoryWidgetFormat(override val id : UUID,
                             val widgetFormat : Comp<WidgetFormat>,
                             val verticalAlignment : Prim<VerticalAlignment>,
                             val lineSpacing : Prim<LineSpacing>)
                              : Model, Serializable
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


        override fun fromDocument(doc : SpecDoc) : ValueParser<StoryWidgetFormat> = when (doc)
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
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun widgetFormat() : WidgetFormat = this.widgetFormat.value

    fun verticalAlignment() : VerticalAlignment = this.verticalAlignment.value

    fun lineSpacing() : Float = this.lineSpacing.value.value


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name : String = "story_widget_format"

    override val modelObject = this

}


@Suppress("UNCHECKED_CAST")
sealed class StoryPart : Model, Serializable
{

    companion object : Factory<StoryPart>
    {

        override fun fromDocument(doc : SpecDoc) : ValueParser<StoryPart> =
            when (doc.case())
            {
                "story_part_span"     -> StoryPartSpan.fromDocument(doc) as ValueParser<StoryPart>
                "story_part_variable" -> StoryPartVariable.fromDocument(doc) as ValueParser<StoryPart>
                "story_part_icon"     -> StoryPartIcon.fromDocument(doc) as ValueParser<StoryPart>
                "story_part_roll"     -> StoryPartAction.fromDocument(doc) as ValueParser<StoryPart>
                else                  -> effError<ValueError,StoryPart>(
                                            UnknownCase(doc.case(), doc.path))
            }

    }


    // -----------------------------------------------------------------------------------------
    // APi
    // -----------------------------------------------------------------------------------------

    open fun variable() : Variable? = when (this)
    {
        is StoryPartVariable -> this.variable()
        else                 -> null
    }


    abstract fun wordCount() : Int

}


/**
 * Story Part Span
 */
data class StoryPartSpan(override val id : UUID,
                         val format : Comp<TextFormat>,
                         val text : Prim<StoryPartText>) : StoryPart(), Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(format : TextFormat, text : StoryPartText)
        : this(UUID.randomUUID(), Comp(format), Prim(text))


    companion object : Factory<StoryPartSpan>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<StoryPartSpan> = when (doc)
        {
            is DocDict -> effApply(::StoryPartSpan,
                                   // Text Format
                                   split(doc.maybeAt("format"),
                                         effValue(TextFormat.default()),
                                         { TextFormat.fromDocument(it) }),
                                   // Text
                                   doc.at("text") ap { StoryPartText.fromDocument(it) }
                                   )
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun text() : String = this.text.value.value

    fun format() : TextFormat = this.format.value


    // -----------------------------------------------------------------------------------------
    // STORY PART
    // -----------------------------------------------------------------------------------------

    override fun wordCount() = this.text().split(' ').size

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
                             val format : Comp<TextFormat>,
                             val variable : Comp<Variable>) : StoryPart(), Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(format : TextFormat, variable : Variable)
        : this(UUID.randomUUID(), Comp(format), Comp(variable))

    companion object : Factory<StoryPartVariable>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<StoryPartVariable> = when (doc)
        {
            is DocDict -> effApply(::StoryPartVariable,
                                   // Format
                                   split(doc.maybeAt("format"),
                                         effValue(TextFormat.default()),
                                         { TextFormat.fromDocument(it) }),
                                   // Style
                                   doc.at("variable") ap { Variable.fromDocument(it) }
                                   )
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun format() : TextFormat = this.format.value


    override fun variable() : Variable = this.variable.value


    fun valueString(sheetUIContext : SheetUIContext) : String
    {
        val str = this.variable().valueString(SheetContext(sheetUIContext))
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
        override fun fromDocument(doc : SpecDoc) : ValueParser<StoryPartIcon> = when (doc)
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
                           val textFormat : Comp<TextFormat>,
                           val iconFormat : Comp<IconFormat>) : StoryPart(), Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(text : StoryPartText,
                rollSummationId : Maybe<SummationId>,
                procedureId : Maybe<ProcedureId>,
                textFormat : TextFormat,
                iconFormat : IconFormat)
        : this(UUID.randomUUID(),
               Prim(text),
               maybeLiftPrim(rollSummationId),
               maybeLiftPrim(procedureId),
               Comp(textFormat),
               Comp(iconFormat))


    companion object : Factory<StoryPartAction>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<StoryPartAction> = when (doc)
        {
            is DocDict ->
            {
                effApply(::StoryPartAction,
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
                         // Text Format
                         split(doc.maybeAt("text_format"),
                               effValue(TextFormat.default()),
                               { TextFormat.fromDocument(it) }),
                         // Icon Format
                         split(doc.maybeAt("icon_format"),
                               effValue(IconFormat.default()),
                               { IconFormat.fromDocument(it) })
                         )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun rollSummationId() : SummationId? = getMaybePrim(this.rollSummationId)

    fun procedureId() : ProcedureId? = getMaybePrim(this.procedureId)

    fun text() : String = this.text.value.value

    fun textFormat() : TextFormat = this.textFormat.value

    fun iconFormat() : IconFormat = this.iconFormat.value


    // -----------------------------------------------------------------------------------------
    // STORY PART
    // -----------------------------------------------------------------------------------------

    override fun wordCount() = this.text().split(' ').size


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val modelObject = this

    override val name = "story_part_action"

}


/**
 * Story Part Text
 */
data class StoryPartText(val value : String) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<StoryPartText>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<StoryPartText> = when (doc)
        {
            is DocText -> effValue(StoryPartText(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value})

}


/**
 * Line Spacing
 */
data class LineSpacing(val value : Float) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<LineSpacing>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<LineSpacing> = when (doc)
        {
            is DocNumber -> effValue(LineSpacing(doc.number.toFloat()))
            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
        }

        fun default() = LineSpacing(1.0f)
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLReal({this.value.toDouble()})

}


object StoryWidgetView
{


    fun view(storyWidget : StoryWidget, sheetUIContext : SheetUIContext) : View
    {
        val layout = WidgetView.layout(storyWidget.widgetFormat(), sheetUIContext)

        val wc = storyWidget.story().map { it.wordCount() }.sum()
        if (wc <= 5)
            layout.addView(this.storyFlexView(storyWidget, sheetUIContext))
        else
            layout.addView(this.storySpannableView(storyWidget, sheetUIContext))

        return layout
    }


    fun storySpannableView(storyWidget : StoryWidget, sheetUIContext : SheetUIContext) : TextView
    {
        val story = TextViewBuilder()

        story.width         = LinearLayout.LayoutParams.MATCH_PARENT
        story.height        = LinearLayout.LayoutParams.WRAP_CONTENT

        story.textSpan      = this.spannableStringBuilder(storyWidget.story(), sheetUIContext)

        story.lineSpacingAdd    = 0f
        story.lineSpacingMult   = storyWidget.format().lineSpacing()

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

        return story.textView(sheetUIContext.context)
    }


    data class Phrase(val storyPart : StoryPart,
                      val text : String,
                      val start : Int,
                      val end : Int)


    fun spannableStringBuilder(storyParts : List<StoryPart>,
                               sheetUIContext : SheetUIContext) : SpannableStringBuilder
    {

        val builder = SpannableStringBuilder()

        val phrases : MutableList<Phrase> = mutableListOf()

        var index = 0
        storyParts.forEach { storyPart ->

            when (storyPart)
            {
                is StoryPartSpan ->
                {
                    val text = storyPart.text()
                    builder.append(text)
                    val textLen = text.length
                    phrases.add(Phrase(storyPart, text, index, index + textLen))
                    index += textLen
                }
                is StoryPartVariable ->
                {
                    val text = storyPart.valueString(sheetUIContext)
                    builder.append(text)
                    val textLen = text.length
                    phrases.add(Phrase(storyPart, text, index, index + textLen))
                    index += textLen
                }
                is StoryPartIcon ->
                {
                    builder.append(" ")
                    phrases.add(Phrase(storyPart, " ", index, index + 1))
                    index += 1
                }
                is StoryPartAction ->
                {
                    val text = storyPart.text()
                    builder.append(" " + text)
                    val textLen = text.length + 1
                    phrases.add(Phrase(storyPart, text, index, index + textLen))
                    index += textLen
                }
            }
        }

        phrases.forEach { (storyPart, _, start, end) ->

            when (storyPart)
            {
                is StoryPartSpan ->
                {
                    this.formatSpans(storyPart.format(), sheetUIContext).forEach {
                        builder.setSpan(it, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                    }
                }
                is StoryPartVariable ->
                {
                    this.formatSpans(storyPart.format(), sheetUIContext).forEach {
                        builder.setSpan(it, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                    }

                    val clickSpan = object: ClickableSpan()
                    {
                        override fun onClick(view : View?)
                        {
                            openVariableEditorDialog(storyPart.variable(), sheetUIContext)
                        }

                        override fun updateDrawState(ds: TextPaint?) {
//                            val color = SheetManager.color(sheetUIContext.sheetId,
//                                            storyPart.textFormat().style().colorTheme())
//                            ds?.linkColor = color
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

                    val summation = GameManager.engine(sheetUIContext.gameId)
                                               .apply{ it.summation(storyPart.summationId())}
                    when (summation)
                    {
                        is Val ->
                        {
                            val diceRoll = summation.value.diceRoll(storyPart.rollName(),
                                                                    SheetContext(sheetUIContext))

                            if (diceRoll != null)
                            {
                                val clickSpan = object: ClickableSpan() {
                                    override fun onClick(view : View?) {
                                        val sheetActivity = sheetUIContext.context as SheetActivity
                                        val dialog = DiceRollerDialogFragment.newInstance(diceRoll,
                                                                              SheetContext(sheetUIContext))
                                        dialog.show(sheetActivity.supportFragmentManager, "")
                                    }

                                    override fun updateDrawState(ds: TextPaint?) {
//                                        val color = SheetManager.color(sheetUIContext.sheetId,
//                                                        storyPart.textFormat().style().colorTheme())
//                                        ds?.linkColor = color
                                    }
                                }

                                builder.setSpan(clickSpan, start + 1, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                            }


                        }
                        is Err -> ApplicationLog.error(summation.error)
                    }


                }
            }
        }

        return builder
    }


    fun formatSpans(textFormat : TextFormat, sheetUIContext : SheetUIContext) : List<Any>
    {
        val style = textFormat.style()

        val sizePx = Util.spToPx(textFormat.style().sizeSp(), sheetUIContext.context)
        val sizeSpan = AbsoluteSizeSpan(sizePx)

        val typeface = Font.typeface(style.font(), style.fontStyle(), sheetUIContext.context)

        val typefaceSpan = CustomTypefaceSpan(typeface)

        var color = SheetManager.color(sheetUIContext.sheetId, textFormat.style().colorTheme())
        val colorSpan = ForegroundColorSpan(color)

        return listOf(sizeSpan, typefaceSpan, colorSpan)
    }


    fun storyFlexView(storyWidget : StoryWidget, sheetUIContext : SheetUIContext) : FlexboxLayout
    {
        val layout = this.storyViewLayout(storyWidget.format(), sheetUIContext)

        val storyParts = storyWidget.story()

        storyParts.forEach { storyPart ->
            when (storyPart)
            {
                is StoryPartSpan ->
                {
                    this.words(storyPart.text()).forEach {
                        layout.addView(this.wordView(it, storyWidget.format(),
                                                     storyPart, sheetUIContext))
                    }
                }
                is StoryPartVariable ->
                {
                    this.words(storyPart.valueString(sheetUIContext)).forEach {
                        layout.addView(this.wordVariableView(it, storyWidget.format(),
                                                            storyPart, sheetUIContext))
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
                                      TopSpacing(formatPadding.topDp() + format.lineSpacing()),
                                      RightSpacing(formatPadding.rightDp()),
                                      BottomSpacing(formatPadding.bottomDp()))

        text.paddingSpacing = padding
        text.marginSpacing  = storyPartSpan.format().margins()

        storyPartSpan.format().style().styleTextViewBuilder(text, sheetUIContext)

        return text.textView(sheetUIContext.context)
    }


    fun wordVariableView(word : String,
                         format : StoryWidgetFormat,
                         storyPart : StoryPartVariable,
                         sheetUIContext : SheetUIContext) : View
    {
        val text = TextViewBuilder()

        text.layoutType     = LayoutType.FLEXBOX
        text.width          = FlexboxLayout.LayoutParams.WRAP_CONTENT
        text.height         = FlexboxLayout.LayoutParams.WRAP_CONTENT

        text.text           = word


        val formatPadding   = storyPart.format().padding()
        val padding         = Spacing(LeftSpacing(formatPadding.leftDp()),
                                      TopSpacing(formatPadding.topDp() + format.lineSpacing()),
                                      RightSpacing(formatPadding.rightDp()),
                                      BottomSpacing(formatPadding.bottomDp()))

        text.paddingSpacing = padding
        text.marginSpacing  = storyPart.format().margins()

        storyPart.format().style().styleTextViewBuilder(text, sheetUIContext)

        val variable = storyPart.variable()
        text.onClick        = View.OnClickListener {
            openVariableEditorDialog(variable, sheetUIContext)
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


