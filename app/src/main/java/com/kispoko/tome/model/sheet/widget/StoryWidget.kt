
package com.kispoko.tome.model.sheet.widget


import android.view.View
import android.widget.LinearLayout
import com.google.android.flexbox.*
import com.kispoko.tome.activity.sheet.dialog.openVariableEditorDialog
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Comp
import com.kispoko.tome.lib.functor.Prim
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.lib.orm.sql.SQLReal
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.lib.ui.FlexboxLayoutBuilder
import com.kispoko.tome.lib.ui.LayoutType
import com.kispoko.tome.lib.ui.TextViewBuilder
import com.kispoko.tome.model.game.engine.variable.Variable
import com.kispoko.tome.model.sheet.style.*
import com.kispoko.tome.rts.sheet.SheetUIContext
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
                             val partAlignment : Prim<PartAlignment>,
                             val lineSpacing : Prim<LineSpacing>)
                              : Model, Serializable
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.widgetFormat.name      = "widget_format"
        this.partAlignment.name     = "part_alignment"
        this.lineSpacing.name       = "line_spacing"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(widgetFormat : WidgetFormat,
                partAlignment : PartAlignment,
                lineSpacing : LineSpacing)
        : this(UUID.randomUUID(),
               Comp(widgetFormat),
               Prim(partAlignment),
               Prim(lineSpacing))


    companion object : Factory<StoryWidgetFormat>
    {

        private val defaultWidgetFormat     = WidgetFormat.default()
        private val defaultPartAlignment    = PartAlignment.Stretch
        private val defaultLineSpacing      = LineSpacing(0.0f)


        override fun fromDocument(doc : SpecDoc) : ValueParser<StoryWidgetFormat> = when (doc)
        {
            is DocDict ->
            {
                effApply(::StoryWidgetFormat,
                         // Widget Format
                         split(doc.maybeAt("widget_format"),
                               effValue(defaultWidgetFormat),
                               { WidgetFormat.fromDocument(it) }),
                         // Part Alignment
                         split(doc.maybeAt("part_alignment"),
                               effValue<ValueError,PartAlignment>(defaultPartAlignment),
                               { PartAlignment.fromDocument(it) }),
                         // Line Spacing
                         split(doc.maybeAt("line_spacing"),
                               effValue(defaultLineSpacing),
                               { LineSpacing.fromDocument(it) })
                         )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = StoryWidgetFormat(defaultWidgetFormat,
                                          defaultPartAlignment,
                                          defaultLineSpacing)

    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun widgetFormat() : WidgetFormat = this.widgetFormat.value

    fun partAlignment() : PartAlignment = this.partAlignment.value

    fun lineSpacing() : Float = this.lineSpacing.value.value


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name : String = "story_widget_format"

    override val modelObject = this

}



/**
 * Part Alignment
 *
 * Based off of Align Items property in the Flexbox Layout
 */
sealed class PartAlignment : SQLSerializable, Serializable
{

    object Stretch : PartAlignment()
    {
        override fun asSQLValue() : SQLValue = SQLText({"stretch"})
    }


    object FlexStart : PartAlignment()
    {
        override fun asSQLValue() : SQLValue = SQLText({"flex_start"})
    }


    object FlexEnd : PartAlignment()
    {
        override fun asSQLValue() : SQLValue = SQLText({"flex_end"})
    }


    object Center : PartAlignment()
    {
        override fun asSQLValue() : SQLValue = SQLText({"center"})
    }


    object Baseline : PartAlignment()
    {
        override fun asSQLValue() : SQLValue = SQLText({"baseline"})
    }


    companion object
    {
        fun fromDocument(doc : SpecDoc) : ValueParser<PartAlignment> = when (doc)
        {
            is DocText -> when (doc.text)
            {
                "stretch"    -> effValue<ValueError,PartAlignment>(PartAlignment.Stretch)
                "flex_start" -> effValue<ValueError,PartAlignment>(PartAlignment.FlexStart)
                "flex_end"   -> effValue<ValueError,PartAlignment>(PartAlignment.FlexEnd)
                "center"     -> effValue<ValueError,PartAlignment>(PartAlignment.Center)
                "baseline"   -> effValue<ValueError,PartAlignment>(PartAlignment.Baseline)
                else         -> effError<ValueError,PartAlignment>(
                                    UnexpectedValue("PartAlignment", doc.text, doc.path))
            }
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    fun alignItems() : Int = when (this)
    {
        is Stretch   -> AlignItems.STRETCH
        is FlexStart -> AlignItems.FLEX_START
        is FlexEnd   -> AlignItems.FLEX_END
        is Center    -> AlignItems.CENTER
        is Baseline  -> AlignItems.BASELINE
    }



}


@Suppress("UNCHECKED_CAST")
sealed class StoryPart(open val format : Comp<TextFormat>) : Model, Serializable
{

    companion object : Factory<StoryPart>
    {

        override fun fromDocument(doc : SpecDoc) : ValueParser<StoryPart> =
            when (doc.case())
            {
                "story_part_span"     -> StoryPartSpan.fromDocument(doc) as ValueParser<StoryPart>
                "story_part_variable" -> StoryPartVariable.fromDocument(doc) as ValueParser<StoryPart>
                else                  -> effError<ValueError,StoryPart>(
                                            UnknownCase(doc.case(), doc.path))
            }

    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun format() : TextFormat = this.format.value


    fun valueString(sheetUIContext: SheetUIContext) : String = when (this)
    {
        is StoryPartSpan     -> this.text()
        is StoryPartVariable ->
        {
            val str = this.variable().valueString(sheetUIContext)
            when (str)
            {
                is Val -> str.value
                is Err -> {
                    ApplicationLog.error(str.error)
                    ""
                }
            }
        }
    }


    // -----------------------------------------------------------------------------------------
    // VARIABLE
    // -----------------------------------------------------------------------------------------

    open fun variable() : Variable? = when (this)
    {
        is StoryPartSpan     -> null
        is StoryPartVariable -> this.variable()
    }

}


/**
 * Story Part Span
 */
data class StoryPartSpan(override val id : UUID,
                         override val format : Comp<TextFormat>,
                         val text : Prim<StoryPartText>) : StoryPart(format), Serializable
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
                             override val format : Comp<TextFormat>,
                             val variable : Comp<Variable>) : StoryPart(format), Serializable
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

    override fun variable() : Variable = this.variable.value


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val modelObject = this

    override val name = "story_part_variable"

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
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLReal({this.value.toDouble()})

}


object StoryWidgetView
{


    fun view(storyWidget : StoryWidget, sheetUIContext: SheetUIContext) : View
    {
        val layout = WidgetView.layout(storyWidget.widgetFormat(), sheetUIContext)

        layout.addView(this.storyView(storyWidget, sheetUIContext))

        return layout
    }


    fun storyView(storyWidget : StoryWidget, sheetUIContext: SheetUIContext) : FlexboxLayout
    {
        val layout = this.storyViewLayout(storyWidget.format(), sheetUIContext)

        val storyParts = storyWidget.story()

        storyParts.forEach { storyPart ->
            val words = storyPart.valueString(sheetUIContext).split(" ")
            words.forEachIndexed { index, word ->
                var w = word
                if (index != 0)
                    w = " " + word
                val partView = this.storyPartView(w, storyWidget.format(), storyPart, sheetUIContext)
                layout.addView(partView)
            }
        }


        return layout
    }


    fun storyViewLayout(format : StoryWidgetFormat, sheetUIContext: SheetUIContext) : FlexboxLayout
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
        layout.itemAlignment    = format.partAlignment().alignItems()


        return layout.flexboxLayout(sheetUIContext.context)

    }


    fun storyPartView(word : String,
                      format : StoryWidgetFormat,
                      storyPart : StoryPart,
                      sheetUIContext: SheetUIContext) : View
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
        if (variable != null)
        {
            text.onClick        = View.OnClickListener {
                openVariableEditorDialog(variable, sheetUIContext)
            }
        }

        return text.textView(sheetUIContext.context)
    }

}

