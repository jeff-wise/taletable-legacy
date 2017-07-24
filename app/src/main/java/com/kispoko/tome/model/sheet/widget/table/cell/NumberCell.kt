
package com.kispoko.tome.model.sheet.widget.table.cell


import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.kispoko.tome.activity.sheet.dialog.openNumberVariableEditorDialog
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.*
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.ui.TextViewBuilder
import com.kispoko.tome.model.sheet.style.NumericEditorType
import com.kispoko.tome.model.sheet.style.TextStyle
import com.kispoko.tome.model.sheet.widget.table.*
import com.kispoko.tome.model.sheet.widget.table.column.NumberColumnFormat
import com.kispoko.tome.rts.sheet.SheetContext
import com.kispoko.tome.rts.sheet.SheetUIContext
import com.kispoko.tome.util.Util
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueError
import lulo.value.ValueParser
import java.io.Serializable
import java.util.*



/**
 * Number Cell Format
 */
data class NumberCellFormat(override val id : UUID,
                            val cellFormat : Comp<CellFormat>,
                            val valuePrefix : Maybe<Prim<NumberCellValuePrefix>>)
                            : Model, Serializable
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.cellFormat.name                        = "cell_format"

        when (this.valuePrefix) {
            is Just -> this.valuePrefix.value.name  = "value_prefix"
        }
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<NumberCellFormat>
    {

        private val defaultCellFormat  = CellFormat.default


        override fun fromDocument(doc : SpecDoc) : ValueParser<NumberCellFormat> = when (doc)
        {
            is DocDict ->
            {
                effApply(::NumberCellFormat,
                         // Model Id
                         effValue(UUID.randomUUID()),
                         // Cell Format
                         split(doc.maybeAt("cell_format"),
                               effValue(Comp.default(defaultCellFormat)),
                               { effApply(::Comp, CellFormat.fromDocument(it)) }),
                         // Value Prefix
                         split(doc.maybeAt("value_prefix"),
                               effValue<ValueError,Maybe<Prim<NumberCellValuePrefix>>>(Nothing()),
                               { effApply({x -> Just(Prim(x))}, NumberCellValuePrefix.fromDocument(it)) })
                         )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = NumberCellFormat(UUID.randomUUID(),
                                         Comp(defaultCellFormat),
                                         Nothing())
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun cellFormat() : CellFormat = this.cellFormat.value

    fun valuePrefixString() : String? = getMaybePrim(this.valuePrefix)?.value


    // -----------------------------------------------------------------------------------------
    // RESOLVERS
    // -----------------------------------------------------------------------------------------

    fun resolveTextStyle(columnFormat : NumberColumnFormat) : TextStyle =
        if (this.cellFormat().textStyle.isDefault())
            columnFormat.columnFormat().textStyle()
        else
            this.cellFormat().textStyle()


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name = "number_cell_format"

    override val modelObject = this

}


/**
 * Value Prefix
 */
data class NumberCellValuePrefix(val value : String) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<NumberCellValuePrefix>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<NumberCellValuePrefix> = when (doc)
        {
            is DocText -> effValue(NumberCellValuePrefix(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() = SQLText({ this.value })

}


object NumberCellView
{


    fun view(cell : TableWidgetNumberCell,
             rowFormat : TableWidgetRowFormat,
             column : TableWidgetNumberColumn,
             cellFormat : NumberCellFormat,
             sheetUIContext: SheetUIContext) : View
    {
        val layout = TableWidgetCellView.layout(rowFormat,
                                                column.format().columnFormat(),
                                                cellFormat.cellFormat(),
                sheetUIContext)

        layout.addView(this.valueTextView(cell, column.format(), cellFormat, sheetUIContext))


        layout.setOnClickListener {
            Log.d("***NUMBERCELL", "on click")
            openNumberVariableEditorDialog(cell.valueVariable(),
                                           cell.resolveEditorType(column),
                                           sheetUIContext)
        }

        return layout
    }


    private fun valueTextView(cell : TableWidgetNumberCell,
                              columnFormat : NumberColumnFormat,
                              cellFormat : NumberCellFormat,
                              sheetUIContext: SheetUIContext) : TextView
    {
        val value = TextViewBuilder()

        // > VIEW ID
        val viewId = Util.generateViewId()
        cell.viewId = Just(viewId)
        value.id    = viewId

        // > LAYOUT
        value.width      = LinearLayout.LayoutParams.WRAP_CONTENT
        value.height     = LinearLayout.LayoutParams.WRAP_CONTENT

        // > STYLE
        val valueStyle = cellFormat.resolveTextStyle(columnFormat)
        valueStyle.styleTextViewBuilder(value, sheetUIContext)

        // > VALUE
        val maybeValue = cell.valueString(sheetUIContext)
        when (maybeValue)
        {
            is Just    -> value.text = maybeValue.value
        }

        val valueString = cell.valueVariable().valueString(SheetContext(sheetUIContext))
        when (valueString) {
            is Val -> value.text = valueString.value
            is Err -> ApplicationLog.error(valueString.error)
        }

        return value.textView(sheetUIContext.context)
    }


}


//
//
//    /**
//     * Get the cell's integer value as a string.
//     * @return The cell's value as a string.
//     */
//    public String valueString()
//    {
//        Integer integerValue = this.value();
//
//        if (integerValue != null)
//        {
//            String integerString = integerValue.toString();
//
//            String valuePrefix = null;
//            if (this.column != null)
//                valuePrefix = this.format().resolveValuePrefix(column.format().valuePrefix());
//            if (valuePrefix != null)
//                integerString = valuePrefix + integerString;
//
//            return integerString;
//        }
//
//        return "";
//    }
//
//
//    // ** Format
//    // -----------------------------------------------------------------------------------------
//
//    /**
//     * The number cell formatting options.
//     * @return The format.
//     */
//    public NumberCellFormat format()
//    {
//        return this.format.getValue();
//    }
//
//
//    // ** Edit Dialog Type
//    // -----------------------------------------------------------------------------------------
//
//    /**
//     * The type of edit dialog.
//     * @return The edit dialog type.
//     */
//    public ArithmeticDialogType editDialogType()
//    {
//        return this.editDialogType.getValue();
//    }
//
//
//    public void setEditDialogType(ArithmeticDialogType editDialogType)
//    {
//        if (editDialogType != null)
//            this.editDialogType.setValue(editDialogType);
//        else
//            this.editDialogType.setValue(ArithmeticDialogType.INCREMENTAL);
//    }
//
//

//    // > Clicks
//    // -----------------------------------------------------------------------------------------
//
//    /**
//     * On a short click, open the appropriate editor.
//     */
//    private void onNumberCellShortClick(Context context)
//    {
//        SheetActivityOld sheetActivity = (SheetActivityOld) context;
//
//        switch (this.editDialogType())
//        {
//            case INCREMENTAL:
//                ArrayList<DialogOptionButton> dialogButtons = new ArrayList<>();
//
//                DialogOptionButton addRowButton =
//                        new DialogOptionButton(R.string.add_row,
//                                               R.drawable.ic_dialog_table_widget_add_row,
//                                               null);
//
//                DialogOptionButton editRowButton =
//                        new DialogOptionButton(R.string.edit_row,
//                                               R.drawable.ic_dialog_table_widget_edit_row,
//                                               null);
//
//                DialogOptionButton editTableButton =
//                        new DialogOptionButton(R.string.edit_table,
//                                               R.drawable.ic_dialog_table_widget_widget,
//                                               null);
//
//                dialogButtons.add(addRowButton);
//                dialogButtons.add(editRowButton);
//                dialogButtons.add(editTableButton);
//
//                CalculatorDialogFragment dialog =
//                            CalculatorDialogFragment.newInstance(valueVariable(), dialogButtons);
//                dialog.show(sheetActivity.getSupportFragmentManager(), "");
//                break;
//        }
//    }
//
//
//    // INTERNAL
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * Initialize the text cell state.
//     */
//    private void initializeNumberCell()
//    {
//        // [1] The boolean cell's value view ID. It is null until the view is created.
//        // --------------------------------------------------------------------------------------
//
//        this.valueViewId = null;
//
//        // [2] Initialize the value variable
//        // --------------------------------------------------------------------------------------
//
//        // [3] Widget Container
//        // --------------------------------------------------------------------------------------
//
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
//            if (this.value() != null && textView != null)
//                textView.setText(this.valueString());
//        }
//    }

