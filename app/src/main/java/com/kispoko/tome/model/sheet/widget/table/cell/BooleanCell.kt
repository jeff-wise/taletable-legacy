
package com.kispoko.tome.model.sheet.widget.table.cell


import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TableRow
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.db.*
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.ProdType
import com.kispoko.tome.lib.orm.RowValue5
import com.kispoko.tome.lib.orm.schema.MaybePrimValue
import com.kispoko.tome.lib.orm.schema.MaybeProdValue
import com.kispoko.tome.lib.ui.ImageViewBuilder
import com.kispoko.tome.lib.ui.LayoutType
import com.kispoko.tome.lib.ui.TextViewBuilder
import com.kispoko.tome.model.sheet.style.ElementFormat
import com.kispoko.tome.model.sheet.style.TextFormat
import com.kispoko.tome.model.sheet.widget.table.*
import com.kispoko.tome.model.sheet.widget.table.column.BooleanColumnFormat
import com.kispoko.tome.model.sheet.widget.table.column.ShowFalseIcon
import com.kispoko.tome.model.sheet.widget.table.column.ShowTrueIcon
import com.kispoko.tome.rts.entity.EntityId
import effect.*
import effect.Val
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
 * Boolean Cell Format
 */
data class BooleanCellFormat(override val id : UUID,
                             val elementFormat : Maybe<ElementFormat>,
                             val trueFormat : Maybe<TextFormat>,
                             val falseFormat : Maybe<TextFormat>,
                             val showTrueIcon : Maybe<ShowTrueIcon>,
                             val showFalseIcon : Maybe<ShowFalseIcon>)
                              : ToDocument, ProdType, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(elementFormat : Maybe<ElementFormat>,
                trueFormat : Maybe<TextFormat>,
                falseFormat : Maybe<TextFormat>,
                showTrueIcon : Maybe<ShowTrueIcon>,
                showFalseIcon : Maybe<ShowFalseIcon>)
        : this(UUID.randomUUID(),
               elementFormat,
               trueFormat,
               falseFormat,
               showTrueIcon,
               showFalseIcon)


    companion object : Factory<BooleanCellFormat>
    {

        override fun fromDocument(doc : SchemaDoc) : ValueParser<BooleanCellFormat> = when (doc)
        {
            is DocDict ->
            {
                apply(::BooleanCellFormat,
                      // Element Format
                      split(doc.maybeAt("element_format"),
                            effValue<ValueError, Maybe<ElementFormat>>(Nothing()),
                            { apply(::Just, ElementFormat.fromDocument(it)) }),
                      // True Format
                      split(doc.maybeAt("true_format"),
                            effValue<ValueError,Maybe<TextFormat>>(Nothing()),
                            { apply(::Just, TextFormat.fromDocument(it)) }),
                      // False Format
                      split(doc.maybeAt("false_format"),
                            effValue<ValueError,Maybe<TextFormat>>(Nothing()),
                            { apply(::Just, TextFormat.fromDocument(it)) }),
                      // Show True Icon?
                      split(doc.maybeAt("show_true_icon"),
                            effValue<ValueError,Maybe<ShowTrueIcon>>(Nothing()),
                            { apply(::Just, ShowTrueIcon.fromDocument(it)) }),
                      // Show False Icon?
                      split(doc.maybeAt("show_false_icon"),
                            effValue<ValueError,Maybe<ShowFalseIcon>>(Nothing()),
                            { apply(::Just, ShowFalseIcon.fromDocument(it)) })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = BooleanCellFormat(UUID.randomUUID(),
                                          Nothing(),
                                          Nothing(),
                                          Nothing(),
                                          Nothing(),
                                          Nothing())

    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf())
        .maybeMerge(this.elementFormat.apply {
            Just(Pair("element_format", it.toDocument() as SchemaDoc)) })
        .maybeMerge(this.trueFormat.apply {
            Just(Pair("true_format", it.toDocument() as SchemaDoc)) })
        .maybeMerge(this.falseFormat.apply {
            Just(Pair("true_format", it.toDocument() as SchemaDoc)) })
        .maybeMerge(this.showTrueIcon.apply {
            Just(Pair("show_true_icon", it.toDocument() as SchemaDoc)) })
        .maybeMerge(this.showFalseIcon.apply {
            Just(Pair("show_false_icon", it.toDocument() as SchemaDoc)) })


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun elementFormat() : Maybe<ElementFormat> = this.elementFormat


    fun trueFormat() : Maybe<TextFormat> = this.trueFormat


    fun falseFormat() : Maybe<TextFormat> = this.falseFormat


    fun showTrueIcon() : Maybe<ShowTrueIcon> = this.showTrueIcon


    fun showFalseIcon() : Maybe<ShowFalseIcon> = this.showFalseIcon


    // -----------------------------------------------------------------------------------------
    // RESOLVERS
    // -----------------------------------------------------------------------------------------

//    fun resolveTextStyle(columnFormat : BooleanColumnFormat) : TextFormat =
//        if (this.cellFormat().textStyle.isDefault())
//            columnFormat.columnFormat().textStyle()
//        else
//            this.cellFormat().textStyle()


    fun resolveTrueFormat(columnFormat : BooleanColumnFormat) : TextFormat =
        when (this.trueFormat) {
            is Just -> this.trueFormat.value
            is Nothing -> columnFormat.resolveTrueFormat()
        }


    fun resolveFalseFormat(columnFormat : BooleanColumnFormat) : TextFormat =
        when (this.falseFormat) {
            is Just -> this.falseFormat.value
            is Nothing -> columnFormat.resolveFalseFormat()
        }


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_WidgetTableCellBooleanFormatValue =
        RowValue5(widgetTableCellBooleanFormatTable,
                  MaybeProdValue(this.elementFormat),
                  MaybeProdValue(this.trueFormat),
                  MaybeProdValue(this.falseFormat),
                  MaybePrimValue(this.showTrueIcon),
                  MaybePrimValue(this.showFalseIcon))

}


object BooleanCellView
{

    fun view(cell : TableWidgetBooleanCell,
             rowFormat : TableWidgetRowFormat,
             column : TableWidgetBooleanColumn,
             cellFormat : BooleanCellFormat,
             entityId : EntityId,
             context : Context) : View
    {

        val layout = TableWidgetCellView.layout(column.format().columnFormat(),
                                                entityId,
                                                context)

        // Text View
        // -------------------------------------------------------------------------------------

        val cellValue = cell.value(entityId)
        when (cellValue)
        {
            is Val ->
            {
                this.addValueViews(layout, cellValue.value, cell, column, entityId, context)

            }
            is Err -> ApplicationLog.error(cellValue.error)
        }

        // On Click
        // -------------------------------------------------------------------------------------


        return layout
    }


    private fun addValueViews(layout : LinearLayout,
                              value : Boolean,
                              cell : TableWidgetBooleanCell,
                              column : TableWidgetBooleanColumn,
                              entityId : EntityId,
                              context : Context)
    {
        // Value Text View
        // -------------------------------------------------------------------------------------

        val valueView = this.valueTextView(value,
                                           column,
                                           cell.format(),
                                           entityId,
                                           context)
        layout.addView(valueView)

        // On Click Listener
        // -------------------------------------------------------------------------------------

        layout.setOnClickListener {

            val cellValue = cell.value(entityId)
            when (cellValue)
            {
                is Val -> toggleCellValue(cellValue.value, cell, column, valueView, entityId, context)
                is Err -> ApplicationLog.error(cellValue.error)
            }
        }
    }


    private fun toggleCellValue(value : Boolean,
                                cell : TableWidgetBooleanCell,
                                column : TableWidgetBooleanColumn,
                                valueView : TextView,
                                entityId : EntityId,
                                context : Context)
    {
        val cellFormat = cell.format()

        val trueFormat  = cellFormat.resolveTrueFormat(column.format())
        val falseFormat = cellFormat.resolveFalseFormat(column.format())

        if (value)
        {
            cell.valueVariable(entityId) apDo { it.updateValue(false, entityId) }

            valueView.text = column.format().falseTextString()

            falseFormat.styleTextView(valueView, entityId, context)
        }
        else
        {
            cell.valueVariable(entityId) apDo { it.updateValue(true, entityId) }

            valueView.text = column.format().trueTextString()
            trueFormat.styleTextView(valueView, entityId, context)
        }

    }


//    private fun valueIconView(cellValue : Boolean,
//                              columnFormat : BooleanColumnFormat,
//                              cellFormat : BooleanCellFormat,
//                              sheetUIContext: SheetUIContext) : ImageView
//    {
//        val icon = ImageViewBuilder()
//
//        // > LAYOUT
//        icon.width          = LinearLayout.LayoutParams.WRAP_CONTENT
//        icon.height         = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        // > IMAGE
//        if (cellValue)
//            icon.image      = R.drawable.ic_boolean_cell_true
//        else
//            icon.image      = R.drawable.ic_boolean_cell_false
//
//        // > MARGINS
//        icon.margin.rightDp = 4f
//
//        // > COLOR
//        val trueStyle   = cellFormat.resolveTrueFormat(columnFormat)
//        val falseStyle  = cellFormat.resolveFalseFormat(columnFormat)
//        //val normalStyle = cellFormat.resolveTextStyle(columnFormat)
//        if (cellValue)
//            icon.color      = SheetManager.color(sheetUIContext.sheetId, trueStyle.colorTheme())
//        else
//            icon.color      = SheetManager.color(sheetUIContext.sheetId, falseStyle.colorTheme())
////        else
////        {
////            icon.color      = SheetManager.color(sheetUIContext.sheetId, normalStyle.colorTheme())
////        }
//
//        return icon.imageView(sheetUIContext.context)
//    }


    // TODO remove cell format param
    // TODO resolve true and false text
    private fun valueTextView(cellValue : Boolean,
                              column : TableWidgetBooleanColumn,
                              cellFormat : BooleanCellFormat,
                              entityId : EntityId,
                              context : Context) : TextView
    {
        val value = TextViewBuilder()

        value.layoutType        = LayoutType.TABLE_ROW
        value.width             = TableRow.LayoutParams.WRAP_CONTENT
        value.height            = TableRow.LayoutParams.WRAP_CONTENT

        // > VALUE
        if (cellValue)
            value.text          = column.format().trueTextString()
        else
            value.text          = column.format().falseTextString()

        // > STYLE
        //val defaultStyle  = cellFormat.resolveTextStyle(column.format())
        val trueStyle     = cellFormat.resolveTrueFormat(column.format())
        val falseStyle    = cellFormat.resolveFalseFormat(column.format())

        if (cellValue)
            trueStyle.styleTextViewBuilder(value, entityId, context)
        else
            falseStyle.styleTextViewBuilder(value, entityId, context)
//        else
//            defaultStyle.styleTextViewBuilder(value, sheetUIContext)

        return value.textView(context)
    }


}

//
//    // > Initialize
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * Set the cells widget container (which is the parent Table Row).
//     * @param parentTableWidgetId The parent table Widget's UUID.
//     */
//    public void initialize(BooleanColumn column, UUID parentTableWidgetId)
//    {
//        // [1] Set properties
//        // --------------------------------------------------------------------------------------
//
//        this.parentTableWidgetId = parentTableWidgetId;
//
//        // [2] Inherit column properites
//        // --------------------------------------------------------------------------------------
//
//        this.valueVariable().setIsNamespaced(column.isNamespaced());
//
//        if (column.defaultLabel() != null && this.valueVariable().label() == null)
//            this.valueVariable().setLabel(column.defaultLabel());
//
//
//        // [3] Initialize the value variable
//        // --------------------------------------------------------------------------------------
//
//        if (this.valueVariable.isNull()) {
//            valueVariable.setValue(BooleanVariable.asBoolean(UUID.randomUUID(),
//                                                             column.defaultValue()));
//        }
//
//        this.valueVariable().initialize();
//
//        this.valueVariable().setOnUpdateListener(new Variable.OnUpdateListener()
//        {
//             @Override
//             public void onUpdate() {
//                onValueUpdate();
//            }
//        });
//
//        State.addVariable(this.valueVariable());
//
//        // [4] Save Column Data
//        // --------------------------------------------------------------------------------------
//
//        this.trueText           = column.trueText();
//        this.falseText          = column.falseText();
//    }

//    // INTERNAL
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * Initialize the text cell state.
//     */
//    private void initializeBooleanCell()
//    {
//        this.valueViewId        = null;
//    }
//
//
//    /**
//     * Saves state about the parent column. This is refreshed whenever a new view is created. When
//     * the column state is changed, it will request a new view to update the table, so the cell
//     * state needs to match the state of the most recent view owner.
//     */
//    private void setColumnState(BooleanColumn column)
//    {
//        this.defaultStyle  = this.format().resolveStyle(column.format().style());
//        this.trueStyle     = this.format().resolveTrueFormat(column.format().trueStyle());
//        this.falseStyle    = this.format().resolveFalseFormat(column.format().falseStyle());
//    }
//
//
//    /**
//     * When the text widget's value is updated.
//     */
//    private void onValueUpdate()
//    {
//        if (this.valueViewId != null && !this.valueVariable.isNull())
//        {
//            Activity activity = (Activity) SheetManagerOld.currentSheetContext();
//            TextView textView = (TextView) activity.findViewById(this.valueViewId);
//
//            Boolean value = this.value();
//
//            // TODO can value be null
//            if (value != null)
//                textView.setText(Boolean.toString(value));
//        }
//    }


