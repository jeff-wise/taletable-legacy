
package com.kispoko.tome.model.sheet.widget.table.cell


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Comp
import com.kispoko.tome.lib.functor.Func
import com.kispoko.tome.lib.functor.Null
import com.kispoko.tome.lib.functor.Prim
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.model.sheet.style.TextStyle
import com.kispoko.tome.model.sheet.widget.table.CellFormat
import effect.Err
import effect.effApply
import effect.split
import lulo.document.DocDict
import lulo.document.DocType
import lulo.document.SpecDoc
import lulo.document.docType
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import lulo.value.valueResult
import java.util.*



/**
 * Boolean Cell Format
 */
data class BooleanCellFormat(override val id : UUID,
                             val cellFormat : Func<CellFormat>,
                             val trueStyle : Func<TextStyle>,
                             val falseStyle : Func<TextStyle>,
                             val showTrueIcon : Func<Boolean>,
                             val showFalseIcon : Func<Boolean>) : Model
{

    companion object : Factory<BooleanCellFormat>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<BooleanCellFormat> = when (doc)
        {
            is DocDict -> effApply(::BooleanCellFormat,
                                   // Model Id
                                   valueResult(UUID.randomUUID()),
                                   // Column Format
                                   split(doc.maybeAt("cell_format"),
                                         valueResult<Func<CellFormat>>(Null()),
                                         fun(d : SpecDoc) : ValueParser<Func<CellFormat>> =
                                             effApply(::Comp, CellFormat.fromDocument(d))),
                                   // True Style
                                   split(doc.maybeAt("true_style"),
                                         valueResult<Func<TextStyle>>(Null()),
                                         fun(d : SpecDoc) : ValueParser<Func<TextStyle>> =
                                             effApply(::Comp, TextStyle.fromDocument(d))),
                                   // False Style
                                   split(doc.maybeAt("false_style"),
                                         valueResult<Func<TextStyle>>(Null()),
                                         fun(d : SpecDoc) : ValueParser<Func<TextStyle>> =
                                             effApply(::Comp, TextStyle.fromDocument(d))),
                                   // Show True Icon?
                                   split(doc.maybeBoolean("show_true_icon"),
                                         valueResult<Func<Boolean>>(Null()),
                                         { valueResult(Prim(it))  }),
                                   // Show True Icon?
                                   split(doc.maybeBoolean("show_false_icon"),
                                         valueResult<Func<Boolean>>(Null()),
                                         { valueResult(Prim(it))  })
                                   )
            else       -> Err(UnexpectedType(DocType.DICT, docType(doc)), doc.path)
        }
    }

    override fun onLoad() { }

}


//
//
//
//    // > Internal
//    // ------------------------------------------------------------------------------------------
//
//    private Integer                         valueViewId;
//
//    private String                          trueText;
//    private String                          falseText;
//
//    // ** Column State
//
//    private TextStyle                       defaultStyle;
//    private TextStyle                       trueStyle;
//    private TextStyle                       falseStyle;
//
//    private UUID                            parentTableWidgetId;
//    private UUID                            unionId;
//
//
//    // CONSTRUCTORS
//    // ------------------------------------------------------------------------------------------
//
//    public BooleanCell()
//    {
//        this.id             = null;
//
//        this.valueVariable  = ModelFunctor.empty(BooleanVariable.class);
//        this.format         = ModelFunctor.empty(BooleanCellFormat.class);
//    }
//
//
//    public BooleanCell(UUID id,
//                       BooleanVariable valueVariable,
//                       BooleanCellFormat format)
//    {
//        // ** Id
//        this.id             = id;
//
//        // ** Value
//        this.valueVariable  = ModelFunctor.full(valueVariable, BooleanVariable.class);
//
//        // ** Format
//        this.format         = ModelFunctor.full(format, BooleanCellFormat.class);
//
//        initializeBooleanCell();
//    }
//
//
//    public static BooleanCell fromYaml(YamlParser yaml)
//                  throws YamlParseException
//    {
//        UUID              id     = UUID.randomUUID();
//
//        BooleanVariable   value  = BooleanVariable.fromYaml(yaml.atMaybeKey("value"));
//        BooleanCellFormat format = BooleanCellFormat.fromYaml(yaml.atMaybeKey("format"));
//
//        return new BooleanCell(id, value, format);
//    }
//
//
//
//    // API
//    // ------------------------------------------------------------------------------------------
//
//    // > Model
//    // ------------------------------------------------------------------------------------------
//
//    // ** Id
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * Get the model identifier.
//     * @return The model UUID.
//     */
//    public UUID getId()
//    {
//        return this.id;
//    }
//
//
//    /**
//     * Set the model identifier.
//     * @param id The new model UUID.
//     */
//    public void setId(UUID id)
//    {
//        this.id = id;
//    }
//
//
//    // ** On Load
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * This method is called when the Boolean Cell is completely loaded for the first time.
//     */
//    public void onLoad()
//    {
//        initializeBooleanCell();
//    }
//
//
//    // > To Yaml
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * The Boolean Cell's yaml representation.
//     * @return The Yaml Builder.
//     */
//    public YamlBuilder toYaml()
//    {
//        return YamlBuilder.map()
//                .putYaml("value", this.valueVariable())
//                .putYaml("format", this.format());
//    }
//
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
//
//
//    // > Cell
//    // ------------------------------------------------------------------------------------------
//
//    @Override
//    public Alignment alignment()
//    {
//        return this.format().alignment();
//    }
//
//
//    @Override
//    public BackgroundColor background()
//    {
//        return this.format().background();
//    }
//
//
//    @Override
//    public UUID parentTableWidgetId()
//    {
//        return this.parentTableWidgetId;
//    }
//
//
//    @Override
//    public void setUnionId(UUID unionId)
//    {
//        this.unionId = unionId;
//    }
//
//
//    @Override
//    public UUID unionId()
//    {
//        return this.unionId;
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
//    @Override
//    public void openEditor(AppCompatActivity activity) { }
//
//
//    // > State
//    // ------------------------------------------------------------------------------------------
//
//    // ** Value
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * Get the boolean variable that contains the value of the boolean cell.
//     * @return The Number Variable value.
//     */
//    public BooleanVariable valueVariable()
//    {
//        return this.valueVariable.getValue();
//    }
//
//
//    public Boolean value()
//    {
//        if (!this.valueVariable.isNull())
//            return this.valueVariable().value();
//        return null;
//    }
//
//
//    // ** Format
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * The cell's formatting options.
//     * @return The format.
//     */
//    public BooleanCellFormat format()
//    {
//        return this.format.getValue();
//    }
//
//
//    // > View
//    // ------------------------------------------------------------------------------------------
//
//    public View view(BooleanColumn column, TableRowFormat rowFormat, final Context context)
//    {
//        this.setColumnState(column);
//
//        final LinearLayout valueView = valueView(column, rowFormat, context);
//
//        return valueView;
//    }
//
//
//    private LinearLayout valueView(BooleanColumn column,
//                                   TableRowFormat rowFormat,
//                                   final Context context)
//    {
//        TextStyle textStyle = this.format().resolveStyle(column.format().style());
//
//        LinearLayout layout = this.layout(column, textStyle.size(),
//                                          rowFormat.cellHeight(), context);
//
//        if ((this.value() && this.format().showTrueIcon()) ||
//            (!this.value() && this.format().showFalseIcon()))
//        {
//            layout.addView(valueIconView(context));
//        }
//
//        // > Text View
//        // -------------------------------------------------------------------------------------
//
//        final TextView valueView = valueTextView(column, context);
//        layout.addView(valueView);
//
//        valueView.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                if (value())
//                {
//                    valueVariable().setValue(false);
//                    valueView.setText(falseText);
//
//                    // No false style, but need to undo true style
//                    if (falseStyle == null && trueStyle != null && defaultStyle != null) {
//                        defaultStyle.styleTextView(valueView, context);
//                    }
//                    // Set false style
//                    else if (falseStyle != null) {
//                        falseStyle.styleTextView(valueView, context);
//                    }
//
//                }
//                else
//                {
//                    valueVariable().setValue(true);
//                    valueView.setText(trueText);
//
//                    // No true style, but need to undo false style
//                    if (trueStyle == null && falseStyle != null && defaultStyle != null) {
//                        defaultStyle.styleTextView(valueView, context);
//                    }
//                    // Set true style
//                    else if (trueStyle != null) {
//                        trueStyle.styleTextView(valueView, context);
//                    }
//                }
//            }
//        });
//
//
//        return layout;
//    }
//
//
//    private ImageView valueIconView(Context context)
//    {
//        ImageViewBuilder icon = new ImageViewBuilder();
//
//        icon.width          = LinearLayout.LayoutParams.WRAP_CONTENT;
//        icon.height         = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        if (this.value())
//            icon.image      = R.drawable.ic_boolean_cell_true;
//        else
//            icon.image      = R.drawable.ic_boolean_cell_false;
//
//        icon.margin.right   = R.dimen.four_dp;
//
//        if (this.value() && this.format().trueStyle() != null) {
//            icon.color      = this.format().trueStyle().color().resourceId();
//        }
//        else if (!this.value() && this.format().falseStyle() != null) {
//            icon.color      = this.format().falseStyle().color().resourceId();
//        }
//        else {
//            icon.color      = this.format().style().color().resourceId();
//        }
//
//        return icon.imageView(context);
//    }
//
//
//    /**
//     * The cell's value text view.
//     * @param context The context.
//     * @return The value Text View.
//     */
//    private TextView valueTextView(BooleanColumn column, final Context context)
//    {
//        TextViewBuilder value = new TextViewBuilder();
//
//        value.layoutType        = LayoutType.TABLE_ROW;
//        value.width             = TableRow.LayoutParams.WRAP_CONTENT;
//        value.height            = TableRow.LayoutParams.WRAP_CONTENT;
//
//        // > Value
//        if (this.value())
//            value.text          = trueText;
//        else
//            value.text          = falseText;
//
//        // > Styles
//        // -------------------------------------------------------------------------------------
//
//        TextStyle defaultStyle  = this.format().resolveStyle(column.format().style());
//        TextStyle trueStyle     = this.format().resolveTrueStyle(column.format().trueStyle());
//        TextStyle falseStyle    = this.format().resolveFalseStyle(column.format().falseStyle());
//
//        if (this.value() && trueStyle != null) {
//            trueStyle.styleTextViewBuilder(value, context);
//        }
//        else if (!this.value() && falseStyle != null) {
//            falseStyle.styleTextViewBuilder(value, context);
//        }
//        else {
//            defaultStyle.styleTextViewBuilder(value, context);
//        }
//
//        return value.textView(context);
//    }
//
//
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
//        this.trueStyle     = this.format().resolveTrueStyle(column.format().trueStyle());
//        this.falseStyle    = this.format().resolveFalseStyle(column.format().falseStyle());
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


