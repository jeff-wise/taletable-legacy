
package com.kispoko.tome.model.sheet.widget.table.cell


import android.annotation.SuppressLint
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.activity.sheet.SheetActivity
import com.kispoko.tome.activity.sheet.dialog.DiceRollDialog
import com.kispoko.tome.activity.sheet.dialog.openTextVariableEditorDialog
import com.kispoko.tome.app.AppError
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.db.DB_WidgetTableCellTextFormat
import com.kispoko.tome.db.dbWidgetTableCellTextFormat
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Prod
import com.kispoko.tome.lib.functor.Val
import com.kispoko.tome.lib.model.ProdType
import com.kispoko.tome.lib.ui.ImageViewBuilder
import com.kispoko.tome.lib.ui.LinearLayoutBuilder
import com.kispoko.tome.lib.ui.TextViewBuilder
import com.kispoko.tome.model.game.engine.variable.VariableNamespace
import com.kispoko.tome.model.sheet.style.ElementFormat
import com.kispoko.tome.model.sheet.style.TextFormat
import com.kispoko.tome.model.sheet.widget.TableWidget
import com.kispoko.tome.model.sheet.widget.table.*
import com.kispoko.tome.model.sheet.widget.table.column.TextColumnFormat
import com.kispoko.tome.rts.sheet.*
import com.kispoko.tome.util.Util
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueError
import lulo.value.ValueParser
import java.io.Serializable
import java.util.*



/**
 * Text Cell Format
 */
data class TextCellFormat(override val id : UUID,
                          val elementFormat : Maybe<ElementFormat>,
                          val textFormat : Maybe<TextFormat>)
                           : ToDocument, ProdType, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(elementFormat: Maybe<ElementFormat>,
                textFormat: Maybe<TextFormat>)
        : this(UUID.randomUUID(),
              elementFormat,
               textFormat)

    companion object : Factory<TextCellFormat>
    {

        override fun fromDocument(doc: SchemaDoc): ValueParser<TextCellFormat> = when (doc)
        {
            is DocDict ->
            {
                apply(::TextCellFormat,
                      // Element Format
                      split(doc.maybeAt("element_format"),
                            effValue<ValueError,Maybe<ElementFormat>>(Nothing()),
                            { apply(::Just, ElementFormat.fromDocument(it)) }),
                      // Text Format
                      split(doc.maybeAt("text_format"),
                            effValue<ValueError,Maybe<TextFormat>>(Nothing()),
                            { apply(::Just, TextFormat.fromDocument(it)) })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = TextCellFormat(UUID.randomUUID(), Nothing(), Nothing())

    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf())
        .maybeMerge(this.elementFormat.apply {
            Just(Pair("element_format", it.toDocument() as SchemaDoc)) })
        .maybeMerge(this.textFormat.apply {
            Just(Pair("text_format", it.toDocument() as SchemaDoc)) })


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun elementFormat() : Maybe<ElementFormat> = this.elementFormat


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


    override fun row() : DB_WidgetTableCellTextFormat =
            dbWidgetTableCellTextFormat(this.elementFormat, this.textFormat)

}


class TextCellViewBuilder(val cell : TableWidgetTextCell,
                          val rowFormat : TableWidgetRowFormat,
                          val column : TableWidgetTextColumn,
                          val rowIndex : Int,
                          val tableWidget : TableWidget,
                          val sheetUIContext : SheetUIContext)
{

    val sheetContext = SheetContext(sheetUIContext)
    val sheetActivity = sheetUIContext.context as SheetActivity


    private fun openEditorDialog()
    {
        val valueVariable = cell.valueVariable(SheetContext(sheetUIContext))
        when (valueVariable)
        {
            is effect.Val -> openTextVariableEditorDialog(
                                        valueVariable.value,
                                        UpdateTargetTextCell(tableWidget.id, cell.id),
                                        sheetUIContext)
            is Err -> ApplicationLog.error(valueVariable.error)
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    fun view() : View
    {
        val layout = TableWidgetCellView.layout(column.format().columnFormat(),
                                                sheetUIContext)

        layout.addView(this.valueView())


        var clickTime : Long = 0
        val CLICK_DURATION = 500


        layout.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action)
            {
                MotionEvent.ACTION_DOWN -> {
                    clickTime = System.currentTimeMillis()
                }
                MotionEvent.ACTION_UP -> {

                    val upTime = System.currentTimeMillis()
                    if ((upTime - clickTime) < CLICK_DURATION) {
                        Log.d("***TEXTCELL", "on click")
                        val cellAction = cell.resolveAction(column)
                        Log.d("***TEXTCELL", "cell action: $cellAction")
                        val cellActionRollSummationId =  cellAction ap { it.rollSummationId() }
                        Log.d("***TEXTCELL", "cell action summation id: $cellActionRollSummationId")
                        when (cellActionRollSummationId) {
                            is Just -> {
                                Log.d("***TEXTCELL", "is summation id")
                                val cellNS = cell.namespace
                                val rowContext = if (cellNS != null) Just(cellNS)
                                                    else Nothing<VariableNamespace>()
                                val summation =
                                        SheetManager.summation(cellActionRollSummationId.value,
                                                               SheetContext(sheetUIContext))
                                                .apply { it.diceRoll(sheetContext, rowContext) }

                                when (summation) {
                                    is effect.Val -> {
                                        val dialog = DiceRollDialog.newInstance(summation.value,
                                                                    cell.action() ap { Just(it.name().value) },
                                                                SheetContext(sheetUIContext))
                                        dialog.show(sheetActivity.supportFragmentManager, "")
                                    }
                                    is Err -> ApplicationLog.error(summation.error)
                                }
                            }
                            else -> {
                                Log.d("***TEXTCELL", "open editor")
                                this.openEditorDialog()
                            }
                        }
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


        when (this.cell.resolveAction(column)) {
            is Just -> layout.addView(this.rollIconView())
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

        return layout.linearLayout(sheetUIContext.context)
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

//        val colorTheme = ColorTheme(setOf(
//                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
//                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))

        val valueStyle      = this.cell.format().resolveTextFormat(column.format())
        icon.color          = SheetManager.color(sheetUIContext.sheetId, valueStyle.colorTheme())

        icon.margin.rightDp = 5f

        return layout.linearLayout(sheetUIContext.context)
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
        valueStyle.styleTextViewBuilder(value, sheetUIContext)

//        value.layoutGravity = valueStyle.alignment().gravityConstant()

        // > VALUE
        val cellValue = cell.valueString(SheetContext(sheetUIContext))
        when (cellValue)
        {
            is effect.Val -> value.text = cellValue.value
            is Err -> ApplicationLog.error(cellValue.error)
        }

        return value.textView(sheetUIContext.context)
    }


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
//                DataReference valueReference = this.valueVariable().valueReference();
//                String         valueSetId   = this.valueVariable().valueSetId();
//
//                ValueSetUnion valueSetUnion  = dictionary.lookup(valueSetId);
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

