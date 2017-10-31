
package com.kispoko.tome.model.sheet.widget.table.cell


import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.activity.sheet.dialog.openNumberVariableEditorDialog
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.*
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.ui.ImageViewBuilder
import com.kispoko.tome.lib.ui.LinearLayoutBuilder
import com.kispoko.tome.lib.ui.TextViewBuilder
import com.kispoko.tome.model.sheet.style.NumberFormat
import com.kispoko.tome.model.sheet.style.TextStyle
import com.kispoko.tome.model.sheet.widget.TableWidget
import com.kispoko.tome.model.sheet.widget.table.*
import com.kispoko.tome.model.sheet.widget.table.column.NumberColumnFormat
import com.kispoko.tome.model.theme.ColorId
import com.kispoko.tome.model.theme.ColorTheme
import com.kispoko.tome.model.theme.ThemeColorId
import com.kispoko.tome.model.theme.ThemeId
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
 * Number Cell Format
 */
data class NumberCellFormat(override val id : UUID,
                            val cellFormat : Comp<CellFormat>,
                            val valuePrefix : Maybe<Prim<NumberCellValuePrefix>>,
                            val numberFormat : Prim<NumberFormat>)
                            : ToDocument, Model, Serializable
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


        this.numberFormat.name                      = "number_format"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<NumberCellFormat>
    {

        private val defaultCellFormat   = CellFormat.default()
        private val defaultNumberFormat = NumberFormat.Normal


        override fun fromDocument(doc: SchemaDoc): ValueParser<NumberCellFormat> = when (doc)
        {
            is DocDict ->
            {
                apply(::NumberCellFormat,
                      // Model Id
                      effValue(UUID.randomUUID()),
                      // Cell Format
                      split(doc.maybeAt("cell_format"),
                            effValue(Comp.default(defaultCellFormat)),
                            { effApply(::Comp, CellFormat.fromDocument(it)) }),
                      // Value Prefix
                      split(doc.maybeAt("value_prefix"),
                            effValue<ValueError,Maybe<Prim<NumberCellValuePrefix>>>(Nothing()),
                            { effApply({x -> Just(Prim(x))}, NumberCellValuePrefix.fromDocument(it)) }),
                      // Number Format
                      split(doc.maybeAt("number_format"),
                            effValue<ValueError,Prim<NumberFormat>>(Prim.default(defaultNumberFormat)),
                            { effApply(::Prim, NumberFormat.fromDocument(it)) })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = NumberCellFormat(UUID.randomUUID(),
                                         Comp.default(defaultCellFormat),
                                         Nothing(),
                                         Prim.default(defaultNumberFormat))

    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "cell_format" to this.cellFormat().toDocument()
        ))
        .maybeMerge(this.valuePrefix().apply {
            Just(Pair("value_prefix", it.toDocument() as SchemaDoc)) })


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun cellFormat() : CellFormat = this.cellFormat.value

    fun valuePrefix() : Maybe<NumberCellValuePrefix> = _getMaybePrim(this.valuePrefix)

    fun valuePrefixString() : String? = getMaybePrim(this.valuePrefix)?.value

    fun numberFormat() : NumberFormat = this.numberFormat.value


    // -----------------------------------------------------------------------------------------
    // RESOLVERS
    // -----------------------------------------------------------------------------------------

    fun resolveTextStyle(columnFormat : NumberColumnFormat) : TextStyle =
        if (this.cellFormat().textStyle.isDefault())
            columnFormat.columnFormat().textStyle()
        else
            this.cellFormat().textStyle()


    fun resolveNumberFormat(columnFormat : NumberColumnFormat) : NumberFormat =
        if (this.numberFormat.isDefault())
            columnFormat.numberFormat()
        else
            this.numberFormat()


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
data class NumberCellValuePrefix(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<NumberCellValuePrefix>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<NumberCellValuePrefix> = when (doc)
        {
            is DocText -> effValue(NumberCellValuePrefix(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocText(this.value)


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() = SQLText({ this.value })

}


class NumberCellViewBuilder(val cell : TableWidgetNumberCell,
                            val row : TableWidgetRow,
                            val column : TableWidgetNumberColumn,
                            val rowIndex : Int,
                            val tableWidget : TableWidget,
                            val sheetUIContext : SheetUIContext)
{

    fun openEditorDialog()
    {
        val valueVariable = cell.valueVariable(SheetContext(sheetUIContext))
        when (valueVariable)
        {
            is Val ->
            {
                val editorType = cell.resolveEditorType(column)
                openNumberVariableEditorDialog(valueVariable.value,
                                               editorType,
                                               UpdateTargetNumberCell(tableWidget.id, cell.id),
                                               sheetUIContext)
            }
            is Err -> ApplicationLog.error(valueVariable.error)
        }
    }


    fun view() : View
    {
        val layout = TableWidgetCellView.layout(row.format(),
                                                column.format().columnFormat(),
                                                cell.format().cellFormat(),
                                                sheetUIContext)

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

        layout.orientation          = LinearLayout.HORIZONTAL
        layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT


        //val valueStyle = this.cell.format().resolveTextStyle(this.column.format())

//        layout.gravity              = Gravity.CENTER_VERTICAL or
//                                        valueStyle.alignment().gravityConstant()

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

        val colorTheme = ColorTheme(setOf(
                ThemeColorId(ThemeId.Dark, ColorId.Theme("light_grey_22")),
                ThemeColorId(ThemeId.Light, ColorId.Theme("dark_grey_12"))))
        icon.color          = SheetManager.color(sheetUIContext.sheetId, colorTheme)

        return layout.linearLayout(sheetUIContext.context)
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
        val valueStyle = this.cell.format().resolveTextStyle(this.column.format())
        valueStyle.styleTextViewBuilder(value, sheetUIContext)

        //value.layoutGravity = valueStyle.alignment().gravityConstant()

        // > VALUE
        val maybeValue = cell.value(SheetContext(sheetUIContext))
        when (maybeValue)
        {
            is Val    -> {
                val numberFormat = this.cell.format().resolveNumberFormat(this.column.format())
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
//            String valuePrefixString = null;
//            if (this.column != null)
//                valuePrefixString = this.format().resolveValuePrefix(column.format().valuePrefixString());
//            if (valuePrefixString != null)
//                integerString = valuePrefixString + integerString;
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

