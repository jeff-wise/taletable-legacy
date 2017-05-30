
package com.kispoko.tome.model.sheet.widget.table.cell


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Comp
import com.kispoko.tome.lib.functor.Func
import com.kispoko.tome.lib.functor.Null
import com.kispoko.tome.lib.functor.Prim
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.model.sheet.widget.table.CellFormat
import effect.*
import lulo.document.DocDict
import lulo.document.DocType
import lulo.document.SpecDoc
import lulo.document.docType
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import lulo.value.valueResult
import java.util.*



/**
 * Number Cell Format
 */
data class NumberCellFormat(override val id : UUID,
                            val cellFormat : Func<CellFormat>,
                            val valuePrefix : Func<String>) : Model
{

    companion object : Factory<NumberCellFormat>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<NumberCellFormat> = when (doc)
        {
            is DocDict -> effApply(::NumberCellFormat,
                                   // Model Id
                                   valueResult(UUID.randomUUID()),
                                   // Cell Format
                                   split(doc.maybeAt("cell_format"),
                                         valueResult<Func<CellFormat>>(Null()),
                                         fun(d : SpecDoc) : ValueParser<Func<CellFormat>> =
                                             effApply(::Comp, CellFormat.fromDocument(d))),
                                   // Value Prefix
                                   split(doc.maybeText("value_prefix"),
                                         valueResult<Func<String>>(Null()),
                                         { valueResult(Prim(it)) })
                                   )
            else       -> Err(UnexpectedType(DocType.DICT, docType(doc)), doc.path)
        }
    }

    override fun onLoad() { }

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
//    // > View
//    // -----------------------------------------------------------------------------------------
//
//    public LinearLayout view(NumberColumn column, TableRowFormat format, final Context context)
//    {
//        this.column = column;
//
//        TextStyle valueStyle = this.format().resolveStyle(column.style());
//        LinearLayout layout = this.layout(column, valueStyle.size(), format.cellHeight(), context);
//
//        layout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onNumberCellShortClick(context);
//            }
//        });
//
//        layout.addView(valueTextView(column, context));
//
//        return layout;
//    }
//
//
//    private TextView valueTextView(NumberColumn column, final Context context)
//    {
//        TextViewBuilder value = new TextViewBuilder();
//
//        this.valueViewId = Util.generateViewId();
//
//        value.id         = this.valueViewId;
//        value.width      = LinearLayout.LayoutParams.WRAP_CONTENT;
//        value.height     = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        TextStyle valueStyle = this.format().resolveStyle(column.style());
//        valueStyle.styleTextViewBuilder(value, context);
//
//        // > Value
//        String valueString = this.valueString();
//        if (valueString != null)
//            value.text = valueString;
//        else
//            value.text = Integer.toString(column.defaultValue());
//
//        return value.textView(context);
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

