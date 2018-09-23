
package com.taletable.android.model.sheet.widget.table.cell


import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.taletable.android.R
import com.taletable.android.activity.sheet.dialog.openNumberVariableEditorDialog
import com.taletable.android.app.AppEff
import com.taletable.android.app.ApplicationLog
import com.taletable.android.lib.Factory
import com.taletable.android.lib.ui.ImageViewBuilder
import com.taletable.android.lib.ui.LinearLayoutBuilder
import com.taletable.android.lib.ui.TextViewBuilder
import com.taletable.android.model.engine.variable.NumberVariableValue
import com.taletable.android.model.engine.variable.VariableRelation
import com.taletable.android.model.sheet.style.NumberFormat
import com.taletable.android.model.sheet.style.TextFormat
import com.taletable.android.model.sheet.widget.WidgetId
import com.taletable.android.model.sheet.widget.table.*
import com.taletable.android.model.sheet.widget.table.column.NumberColumnFormat
import com.taletable.android.model.theme.ColorId
import com.taletable.android.model.theme.ColorTheme
import com.taletable.android.model.theme.ThemeColorId
import com.taletable.android.model.theme.ThemeId
import com.taletable.android.rts.entity.EntityId
import com.taletable.android.rts.entity.colorOrBlack
import com.taletable.android.rts.entity.sheet.*
import com.taletable.android.util.Util
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.UnknownCase
import lulo.value.ValueError
import lulo.value.ValueParser
import maybe.Just
import maybe.Maybe
import maybe.Nothing
import java.io.Serializable



/**
 * Number Cell Format
 */
data class NumberCellFormat(val textFormat : Maybe<TextFormat>)
                            : ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<NumberCellFormat>
    {

        override fun fromDocument(doc: SchemaDoc): ValueParser<NumberCellFormat> = when (doc)
        {
            is DocDict ->
            {
                apply(::NumberCellFormat,
                      // Text Format
                      split(doc.maybeAt("text_format"),
                            effValue<ValueError, Maybe<TextFormat>>(Nothing()),
                            { apply(::Just, TextFormat.fromDocument(it)) })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = NumberCellFormat(Nothing())

    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf())
        .maybeMerge(this.textFormat.apply {
            Just(Pair("text_format", it.toDocument() as SchemaDoc)) })


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun textformat() : Maybe<TextFormat> = this.textFormat


    // -----------------------------------------------------------------------------------------
    // RESOLVERS
    // -----------------------------------------------------------------------------------------

    fun resolveTextFormat(columnFormat : NumberColumnFormat) : TextFormat =
        when (this.textFormat) {
            is Just -> this.textFormat.value
            else    -> columnFormat.columnFormat().textFormat()
        }

}


class NumberCellViewBuilder(val cell : TableWidgetNumberCell,
                            val column : TableWidgetNumberColumn,
                            val tableWidgetId : WidgetId,
                            val entityId : EntityId,
                            val context : Context)
{

    fun openEditorDialog()
    {
        val valueVariable = cell.valueVariable(entityId)
        when (valueVariable)
        {
            is effect.Val ->
            {
                val editorType = cell.resolveEditorType(column)
                openNumberVariableEditorDialog(valueVariable.value,
                                               editorType,
                                               UpdateTargetNumberCell(tableWidgetId, cell.id),
                                               entityId,
                                               context)
            }
            is Err -> ApplicationLog.error(valueVariable.error)
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    fun view() : View
    {
        val layout = TableWidgetCellView.layout(column.format().columnFormat(),
                                                entityId,
                                                context)

        layout.addView(this.valueView())


        var clickTime : Long = 0
        val CLICK_DURATION = 500

        layout.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action)
            {
                MotionEvent.ACTION_DOWN -> {
                    clickTime = System.currentTimeMillis()
                    Log.d("***NUMBERCELL", "action down")
                }
                MotionEvent.ACTION_UP -> {
                    Log.d("***NUMBERCELL", "action up")
                    val upTime = System.currentTimeMillis()
                    if ((upTime - clickTime) < CLICK_DURATION) {
                        this.openEditorDialog()
                        Log.d("***NUMBERCELL", "on single click")
                    }
                }
            }

            true
        }

        return layout
    }


    private fun valueView() : LinearLayout
    {
        val layout = this.valueViewLayout()

        when (this.cell.action()) {
            is Just -> layout.addView(this.rollIconView())
        }

        layout.addView(this.valueTextView())

        return layout
    }


    private fun valueViewLayout() : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        val format = this.cell.format().resolveTextFormat(column.format())

        layout.orientation          = LinearLayout.HORIZONTAL

        layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.backgroundColor      = colorOrBlack(format.elementFormat().backgroundColorTheme(), entityId)

        layout.corners              = format.elementFormat().corners()

        layout.paddingSpacing       = format.elementFormat().padding()

        //val valueStyle = this.cell.format().resolveTextStyle(this.column.format())

//        layout.gravity              = Gravity.CENTER_VERTICAL or
//                                        valueStyle.alignment().gravityConstant()

        return layout.linearLayout(context)
    }


    private fun rollIconView() : LinearLayout
    {
        // (1) Declarations
        // -------------------------------------------------------------------------------------

        val layout = LinearLayoutBuilder()
        val icon   = ImageViewBuilder()

        // (2) Layout
        // -------------------------------------------------------------------------------------

        layout.width        = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height       = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.child(icon)

        // (3) Icon
        // -------------------------------------------------------------------------------------

        icon.widthDp        = 19
        icon.heightDp       = 19

        icon.image          = R.drawable.icon_dice_roll_filled

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        icon.color          = colorOrBlack(colorTheme, entityId)

        return layout.linearLayout(context)
    }


    private fun valueTextView() : TextView
    {
        val value = TextViewBuilder()

        // > VIEW ID
        val viewId = Util.generateViewId()
        cell.viewId = viewId
        value.id    = viewId

        // > LAYOUT
        value.width      = LinearLayout.LayoutParams.WRAP_CONTENT
        value.height     = LinearLayout.LayoutParams.WRAP_CONTENT

        // > STYLE
        val valueStyle = this.cell.format().resolveTextFormat(this.column.format())
        valueStyle.styleTextViewBuilder(value, entityId, context)

        //value.layoutGravity = valueStyle.alignment().gravityConstant()

        // > VALUE
        val maybeValue = cell.value(entityId)
        when (maybeValue)
        {
            is effect.Val -> {
                val numberFormat = this.cell.format().resolveTextFormat(this.column.format()).numberFormat()
                when (numberFormat) {
                    is NumberFormat.Modifier -> {
                        value.text = numberFormat.formattedString(maybeValue.value)
                    }
                    else -> {
                        value.text = Util.doubleString(maybeValue.value)
                    }
                }
            }
        }

        return value.textView(context)
    }


}


@Suppress("UNCHECKED_CAST")
sealed class NumberCellValue : ToDocument, Serializable
{

    companion object : Factory<NumberCellValue>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<NumberCellValue> =
            when (doc.case())
            {
                "variable_number_value" -> NumberCellValueValue.fromDocument(doc.nextCase()) as ValueParser<NumberCellValue>
                "variable_relation"     -> NumberCellValueRelation.fromDocument(doc.nextCase()) as ValueParser<NumberCellValue>
                else                    -> effError(UnknownCase(doc.case(), doc.path))
            }
    }


    fun value(entityId : EntityId) : AppEff<Maybe<Double>> = when (this)
    {
        is NumberCellValueValue -> {
            this.value.value(entityId)
        }
        else -> effValue(Just(0.0))
    }
}


/**
 * Number Cell Value : Value
 */
data class NumberCellValueValue(val value : NumberVariableValue) : NumberCellValue(), Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<NumberCellValueValue>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<NumberCellValueValue> =
                apply(::NumberCellValueValue, NumberVariableValue.fromDocument(doc))

    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = this.value.toDocument()

}



/**
 * Number Cell Value : Relation
 */
data class NumberCellValueRelation(val relation : VariableRelation) : NumberCellValue(), Serializable
{

    // | Constructors
    // -----------------------------------------------------------------------------------------

    companion object : Factory<NumberCellValueRelation>
    {
        override fun fromDocument(doc : SchemaDoc): ValueParser<NumberCellValueRelation> =
                apply(::NumberCellValueRelation, VariableRelation.fromDocument(doc))
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = this.relation.toDocument()

}


