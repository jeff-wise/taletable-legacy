
package com.kispoko.tome.model.sheet.widget.table.cell

import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Comp
import com.kispoko.tome.lib.functor.Func
import com.kispoko.tome.lib.functor.Null
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
 * Text Cell Format
 */
data class TextCellFormat(override val id : UUID,
                          val cellFormat : Func<CellFormat>) : Model
{

    companion object : Factory<TextCellFormat>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<TextCellFormat> = when (doc)
        {
            is DocDict -> effApply2(::TextCellFormat,
                                    // Model Id
                                    valueResult(UUID.randomUUID()),
                                    // Cell Format
                                    split(doc.maybeAt("cell_format"),
                                          valueResult<Func<CellFormat>>(Null()),
                                          fun(d : SpecDoc) : ValueParser<Func<CellFormat>> =
                                              effApply(::Comp, CellFormat.fromDocument(d)))
                                    )
            else       -> Err(UnexpectedType(DocType.DICT, docType(doc)), doc.path)
        }
    }

    override fun onLoad() { }

}



//
//
//    /**
//     * Set the cells widget container (which is the parent Table Row).
//     * @param widgetContainer The widget container.
//     */
//    public void initialize(TextColumn column,
//                           WidgetContainer widgetContainer,
//                           UUID parentTableWidgetId)
//    {
//        // [1] Set properties
//        // --------------------------------------------------------------------------------------
//
//        this.widgetContainer     = widgetContainer;
//        this.parentTableWidgetId = parentTableWidgetId;
//
//        // [2] Inherit column properties
//        // --------------------------------------------------------------------------------------
//
//        if (this.valueVariable() != null)
//        {
//            this.valueVariable().setDefinesNamespace(column.definesNamespace());
//            this.valueVariable().setIsNamespaced(column.isNamespaced());
//
//            if (column.defaultLabel() != null && this.valueVariable().label() == null)
//                this.valueVariable().setLabel(column.defaultLabel());
//        }
//
//        // [3] Initialize value variable
//        // --------------------------------------------------------------------------------------
//
//        // > If null, set default value
//        if (this.valueVariable.isNull()) {
//            valueVariable.setValue(TextVariable.asText(UUID.randomUUID(),
//                                                       column.defaultValue()));
//        }
//
//        this.valueVariable().initialize();
//
//        this.valueVariable().setOnUpdateListener(new Variable.OnUpdateListener() {
//                                             @Override
//                                             public void onUpdate() {
//                onValueUpdate();
//        }
//    });
//
//        State.addVariable(this.valueVariable());
//    }
//
//
//    /**
//     * The cell's variables that may be in a namespace.
//     * @return The variable list.
//     */
//    public List<Variable> namespacedVariables()
//    {
//        List<Variable> variables = new ArrayList<>();
//
//        if (this.valueVariable().isNamespaced())
//            variables.add(this.valueVariable());
//
//        return variables;
//    }
//
//
//    // > State
//    // ------------------------------------------------------------------------------------------
//
//    // ** Value
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * Get the value of this text cell which is a text variable.
//     * @return The Text Variable value.
//     */
//    public TextVariable valueVariable()
//    {
//        return this.valueVariable.getValue();
//    }
//
//
//    public String value()
//    {
//        if (valueVariable() != null)
//        {
//            try {
//                return this.valueVariable().value();
//            }
//            catch (NullVariableException exception) {
//                ApplicationFailure.nullVariable(exception);
//            }
//        }
//
//        return "N/A";
//    }
//
//
//    /**
//     * Update the text cell's literal value.
//     * @param value
//     */
//    public void setLiteralValue(String value, Activity activity)
//    {
//        this.valueVariable().setLiteralValue(value);
//
//        if (activity != null && this.valueViewId != null)
//        {
//            TextView textView = (TextView) activity.findViewById(this.valueViewId);
//
//            try
//            {
//                textView.setText(this.valueVariable().value());
//
//                // > SAVE the new value
//                this.valueVariable.saveAsync();
//            }
//            catch (NullVariableException exception)
//            {
//                ApplicationFailure.nullVariable(exception);
//            }
//        }
//
//    }
//
//
//    // ** Format
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * The text cell formatting options.
//     * @return The format.
//     */
//    public TextCellFormat format()
//    {
//        return this.format.getValue();
//    }
//
//
//    // > View
//    // ------------------------------------------------------------------------------------------
//
//    public LinearLayout view(TextColumn column, TableRowFormat rowFormat, final Context context)
//    {
//        this.column = column;
//
//        TextStyle valuestyle = this.format().resolveStyle(column.style());
//
//        LinearLayout layout = this.layout(column, valuestyle.size(),
//                                          rowFormat.cellHeight(), context);
//
//        layout.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
//                onTextCellShortClick(context);
//            }
//        });
//
//        // > Text
//        layout.addView(valueTextView(column, context));
//
//        return layout;
//    }
//
//
//    private TextView valueTextView(TextColumn column, Context context)
//    {
//        TextViewBuilder value = new TextViewBuilder();
//        this.valueViewId = Util.generateViewId();
//
//
//        value.id         = this.valueViewId;
//        value.width      = LinearLayout.LayoutParams.WRAP_CONTENT;
//        value.height     = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        TextStyle valuestyle = this.format().resolveStyle(column.style());
//        valuestyle.styleTextViewBuilder(value, context);
//
//        // > Value
//        if (this.value() != null)
//            value.text = this.value();
//        else
//            value.text = column.defaultValue();
//
//        return value.textView(context);
//    }
//
//
//    // > Dialog
//    // ------------------------------------------------------------------------------------------
//
//    public void openEditor(AppCompatActivity activity)
//    {
//        switch (this.valueVariable().kind())
//        {
//            case LITERAL:
//                // If the string is short, edit in DIALOG
//                if (this.value().length() < 145)
//                {
//                    TextEditorDialogFragment textDialog =
//                            TextEditorDialogFragment.forTextCell(this);
//                    textDialog.show(activity.getSupportFragmentManager(), "");
//                }
//                // ...otherwise, edit in ACTIVITY
//                else
//                {
//                    Intent intent = new Intent(activity, TextEditorActivity.class);
//                    intent.putExtra("text_widget", this);
//                    activity.startActivity(intent);
//                }
//                break;
//
//            case VALUE:
//                Dictionary dictionary = SheetManagerOld.dictionary();
//
//                if (this.valueVariable() == null || dictionary == null)
//                    break;
//
//                ValueReference valueReference = this.valueVariable().valueReference();
//                String         valueSetName   = this.valueVariable().valueSetName();
//
//                ValueSetUnion valueSetUnion  = dictionary.lookup(valueSetName);
//                ValueUnion valueUnion     = dictionary.valueUnion(valueReference);
//
//                if (valueSetUnion == null || valueUnion == null)
//                    break;
//
//                ChooseValueDialogFragment valueDialog =
//                            ChooseValueDialogFragment.newInstance(valueSetUnion, valueUnion);
//                valueDialog.show(activity.getSupportFragmentManager(), "");
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
//    private void initializeTextCell()
//    {
//        this.valueViewId = null;
//        this.widgetContainer = null;
//    }
//
//
//    /**
//     * Configure the container's namespace. If the text cell's value is a variable that defines
//     * a namespace, then update the container namespace.
//     */
//    private void configureNamespace()
//    {
//        if (this.valueVariable().definesNamespace())
//        {
//            try {
//                Namespace namespace = this.valueVariable().namespace();
//                this.widgetContainer.setNamespace(namespace);
//            }
//            catch (NullVariableException exception) {
//                ApplicationFailure.nullVariable(exception);
//            }
//        }
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
//            if (this.value() != null)
//                textView.setText(this.value());
//        }
//        else if (!this.valueVariable.isNull()) {
//            this.configureNamespace();
//        }
//    }
//
//
//    // > Clicks
//    // ------------------------------------------------------------------------------------------
//
//    private void onTextCellShortClick(Context context)
//    {
//        AppCompatActivity activity = (AppCompatActivity) context;
//
//        TableActionDialogFragment dialog =
//                TableActionDialogFragment.newInstance(this.parentTableWidgetId,
//                                                      this.unionId(),
//                                                      this.column.name());
//        dialog.show(activity.getSupportFragmentManager(), "");
//    }
//
//
//    // UPDATE EVENT
//    // -----------------------------------------------------------------------------------------
//
//    public static class UpdateLiteralEvent
//    {
//
//        // PROPERTIES
//        // -------------------------------------------------------------------------------------
//
//        private UUID   tableWidgetId;
//        private UUID   cellId;
//        private String newValue;
//
//
//        // CONSTRUCTORS
//        // -------------------------------------------------------------------------------------
//
//        public UpdateLiteralEvent(UUID tableWidgetId, UUID cellId, String newValue)
//        {
//            this.tableWidgetId  = tableWidgetId;
//            this.cellId         = cellId;
//            this.newValue       = newValue;
//        }
//
//
//        // API
//        // -------------------------------------------------------------------------------------
//
//        public UUID tableWidgetId()
//        {
//            return this.tableWidgetId;
//        }
//
//
//        public UUID cellId()
//        {
//            return this.cellId;
//        }
//
//
//        public String newValue()
//        {
//            return this.newValue;
//        }
//
//    }

