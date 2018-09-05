
package com.taletable.android.model.sheet.widget.table.cell


import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.taletable.android.R
import com.taletable.android.activity.entity.book.BookActivity
import com.taletable.android.activity.sheet.dialog.openTextVariableEditorDialog
import com.taletable.android.app.AppEff
import com.taletable.android.app.AppError
import com.taletable.android.app.AppSheetError
import com.taletable.android.app.ApplicationLog
import com.taletable.android.lib.Factory
import com.taletable.android.lib.ui.ImageViewBuilder
import com.taletable.android.lib.ui.LinearLayoutBuilder
import com.taletable.android.lib.ui.TextViewBuilder
import com.taletable.android.model.book.BookReference
import com.taletable.android.model.engine.value.ValueReference
import com.taletable.android.model.engine.variable.TextVariableValue
import com.taletable.android.model.engine.variable.VariableRelation
import com.taletable.android.model.sheet.style.TextFormat
import com.taletable.android.model.sheet.widget.WidgetId
import com.taletable.android.model.sheet.widget.table.*
import com.taletable.android.model.sheet.widget.table.column.TextColumnFormat
import com.taletable.android.rts.entity.*
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
 * Text Cell Format
 */
data class TextCellFormat(val textFormat : Maybe<TextFormat>)
                           : ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<TextCellFormat>
    {

        override fun fromDocument(doc: SchemaDoc): ValueParser<TextCellFormat> = when (doc)
        {
            is DocDict ->
            {
                apply(::TextCellFormat,
                      // Text Format
                      split(doc.maybeAt("text_format"),
                            effValue<ValueError,Maybe<TextFormat>>(Nothing()),
                            { apply(::Just, TextFormat.fromDocument(it)) })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = TextCellFormat(Nothing())

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

    fun textFormat() : Maybe<TextFormat> = this.textFormat


    // -----------------------------------------------------------------------------------------
    // RESOLVERS
    // -----------------------------------------------------------------------------------------

    fun resolveTextFormat(columnFormat : TextColumnFormat) : TextFormat =
        when (this.textFormat) {
            is Just -> this.textFormat.value
            else    -> columnFormat.columnFormat().textFormat()
        }


}


@Suppress("UNCHECKED_CAST")
sealed class TextCellValue : ToDocument, Serializable
{

    companion object : Factory<TextCellValue>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<TextCellValue> =
            when (doc.case())
            {
                "variable_text_value" -> TextCellValueValue.fromDocument(doc.nextCase()) as ValueParser<TextCellValue>
                "variable_relation"   -> TextCellValueRelation.fromDocument(doc.nextCase()) as ValueParser<TextCellValue>
                else                  -> effError(UnknownCase(doc.case(), doc.path))
            }
    }


    fun value(entityId : EntityId) : AppEff<Maybe<String>> = when (this)
    {
        is TextCellValueValue -> {
            this.value.value(entityId)
        }
        else -> effValue(Just(""))
    }
}


/**
 * Text Cell DValue : Value
 */
data class TextCellValueValue(val value : TextVariableValue) : TextCellValue(), Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<TextCellValueValue>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<TextCellValueValue> =
                apply(::TextCellValueValue, TextVariableValue.fromDocument(doc))

    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = this.value.toDocument()

}



/**
 * Text Cell DValue : Value
 */
data class TextCellValueRelation(val relation : VariableRelation) : TextCellValue(), Serializable
{

    // | Constructors
    // -----------------------------------------------------------------------------------------

    companion object : Factory<TextCellValueRelation>
    {
        override fun fromDocument(doc : SchemaDoc): ValueParser<TextCellValueRelation> =
                apply(::TextCellValueRelation, VariableRelation.fromDocument(doc))
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = this.relation.toDocument()

}




class TextCellUI(val cell : TableWidgetTextCell,
                 val column : TableWidgetTextColumn,
                 val tableWidgetId : WidgetId,
                 val entityId : EntityId,
                 val context : Context)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    val sheetActivity = context as AppCompatActivity


    // -----------------------------------------------------------------------------------------
    // METHODS
    // -----------------------------------------------------------------------------------------

    private fun openEditorDialog()
    {
        val valueVariable = cell.valueVariable(entityId)
        when (valueVariable)
        {
            is effect.Val -> openTextVariableEditorDialog(
                                        valueVariable.value,
                                        UpdateTargetTextCell(tableWidgetId, cell.id),
                                        entityId,
                                        context)
            is Err -> ApplicationLog.error(valueVariable.error)
        }
    }


    // -----------------------------------------------------------------------------------------
    // VIEWS
    // -----------------------------------------------------------------------------------------

    fun view() : View
    {
        val layout = TableWidgetCellView.layout(column.format().columnFormat(),
                                                entityId,
                                                context)

        layout.addView(this.valueView())

        layout.setOnClickListener {
            when (column.columnVariableId())
            {
                is Just    -> { }
                is Nothing -> this.openEditorDialog()
            }
        }

        layout.setOnLongClickListener {

            var bookReference : Eff<AppError,Identity,Maybe<BookReference>> =
                    effError(AppSheetError(CellDoesNotHaveBookReference(cell.id)))

            bookReference = cell.variable(entityId).apply {
                effValue<AppError,Maybe<BookReference>>(it.bookReference(entityId))
            }

            Log.d("***TEXT CELL", "book reference: ${bookReference}")

            // This is interesting...
            // TODO need more functions to do parts of this.
            column.columnVariableId().doMaybe { colVarId ->
                textListVariable(colVarId, entityId).apDo { colVar ->
                    colVar.valueSetId().doMaybe { valueSetId ->
                        valueSet(valueSetId, entityId).apDo {
                            cell.valueId?.let { valueId ->
                                value(ValueReference(valueSetId, valueId), entityId).apDo { value ->
                                    bookReference = effValue(value.bookReference)
                                }
                            }
                        }
                    }
                }
            }

            bookReference.apDo { maybeBookRef ->
                maybeBookRef.doMaybe { bookRef ->
                    val intent = Intent(sheetActivity, BookActivity::class.java)
                    intent.putExtra("book_reference", bookRef)
                    sheetActivity.startActivity(intent)
                }
            }

            true
        }

        return layout
    }


    private fun valueView() : LinearLayout
    {
        val layout = this.valueViewLayout()


        when (this.cell.resolveAction(column)) {
            is Just<*> -> layout.addView(this.rollIconView())
        }

        layout.addView(this.valueTextView())

        return layout
    }


    private fun valueViewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.orientation      = LinearLayout.HORIZONTAL
        layout.width            = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        val valueStyle          = this.cell.format().resolveTextFormat(column.format())
        layout.gravity          = Gravity.CENTER_VERTICAL or valueStyle.elementFormat().alignment().gravityConstant()

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

        val valueStyle      = this.cell.format().resolveTextFormat(column.format())
        icon.color          = colorOrBlack(valueStyle.colorTheme(), entityId)

        icon.margin.rightDp = 5f

        return layout.linearLayout(context)
    }



    private fun valueTextView() : TextView
    {
        val value           = TextViewBuilder()

        // > VIEW ID
        val viewId          = Util.generateViewId()
        cell.viewId         = viewId
        value.id            = viewId

        // > LAYOUT
        value.width         = LinearLayout.LayoutParams.WRAP_CONTENT
        value.height        = LinearLayout.LayoutParams.WRAP_CONTENT

        val valueStyle      = this.cell.format().resolveTextFormat(column.format())
        valueStyle.styleTextViewBuilder(value, entityId, context)

        // > VALUE
        val cellValue = cell.valueString(entityId)
        when (cellValue)
        {
            is effect.Val -> value.text = cellValue.value
            is Err -> ApplicationLog.error(cellValue.error)
        }

        return value.textView(context)
    }


}

