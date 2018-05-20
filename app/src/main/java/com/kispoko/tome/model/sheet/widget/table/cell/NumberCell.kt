
package com.kispoko.tome.model.sheet.widget.table.cell


import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.activity.sheet.dialog.openNumberVariableEditorDialog
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.db.*
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.ProdType
import com.kispoko.tome.lib.orm.RowValue1
import com.kispoko.tome.lib.orm.schema.MaybeProdValue
import com.kispoko.tome.lib.ui.ImageViewBuilder
import com.kispoko.tome.lib.ui.LinearLayoutBuilder
import com.kispoko.tome.lib.ui.TextViewBuilder
import com.kispoko.tome.model.sheet.style.NumberFormat
import com.kispoko.tome.model.sheet.style.TextFormat
import com.kispoko.tome.model.sheet.widget.TableWidget
import com.kispoko.tome.model.sheet.widget.WidgetId
import com.kispoko.tome.model.sheet.widget.table.*
import com.kispoko.tome.model.sheet.widget.table.column.NumberColumnFormat
import com.kispoko.tome.model.theme.ColorId
import com.kispoko.tome.model.theme.ColorTheme
import com.kispoko.tome.model.theme.ThemeColorId
import com.kispoko.tome.model.theme.ThemeId
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
 * Number Cell Format
 */
data class NumberCellFormat(override val id : UUID,
                            val textFormat : Maybe<TextFormat>)
                            : ToDocument, ProdType, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(textFormat : Maybe<TextFormat>)
            : this(UUID.randomUUID(), textFormat)


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


        fun default() = NumberCellFormat(UUID.randomUUID(), Nothing())

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


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_WidgetTableCellNumberFormatValue =
        RowValue1(widgetTableCellNumberFormatTable,
                  MaybeProdValue(this.textFormat))

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

        layout.orientation          = LinearLayout.HORIZONTAL
        layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT


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

