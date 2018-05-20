
package com.kispoko.tome.model.sheet.widget.table.cell


import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.activity.entity.book.BookActivity
import com.kispoko.tome.activity.sheet.dialog.openTextVariableEditorDialog
import com.kispoko.tome.app.AppError
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.db.*
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.ProdType
import com.kispoko.tome.lib.orm.RowValue1
import com.kispoko.tome.lib.orm.schema.MaybeProdValue
import com.kispoko.tome.lib.ui.ImageViewBuilder
import com.kispoko.tome.lib.ui.LinearLayoutBuilder
import com.kispoko.tome.lib.ui.TextViewBuilder
import com.kispoko.tome.model.book.BookReference
import com.kispoko.tome.model.sheet.style.TextFormat
import com.kispoko.tome.model.sheet.widget.TableWidget
import com.kispoko.tome.model.sheet.widget.WidgetId
import com.kispoko.tome.model.sheet.widget.table.*
import com.kispoko.tome.model.sheet.widget.table.column.TextColumnFormat
import com.kispoko.tome.rts.entity.EntityId
import com.kispoko.tome.rts.entity.colorOrBlack
import com.kispoko.tome.rts.entity.sheet.*
import com.kispoko.tome.util.Util
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueError
import lulo.value.ValueParser
import maybe.Just
import maybe.Maybe
import maybe.Nothing
import java.io.Serializable
import java.util.*



/**
 * Text Cell Format
 */
data class TextCellFormat(override val id : UUID,
                          val textFormat : Maybe<TextFormat>)
                           : ToDocument, ProdType, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(textFormat: Maybe<TextFormat>)
        : this(UUID.randomUUID(), textFormat)

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


        fun default() = TextCellFormat(UUID.randomUUID(), Nothing())

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


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_WidgetTableCellTextFormatValue =
        RowValue1(widgetTableCellTextFormatTable,
                  MaybeProdValue(this.textFormat))


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

            val bookReference = cell.variable(entityId).apply {
                effValue<AppError,Maybe<BookReference>>(it.bookReference(entityId))
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

