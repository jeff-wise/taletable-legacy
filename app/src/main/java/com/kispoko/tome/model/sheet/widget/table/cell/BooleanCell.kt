
package com.kispoko.tome.model.sheet.widget.table.cell


import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TableRow
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.*
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.lib.orm.sql.SQLInt
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.lib.ui.ImageViewBuilder
import com.kispoko.tome.lib.ui.LayoutType
import com.kispoko.tome.lib.ui.TextViewBuilder
import com.kispoko.tome.model.sheet.style.TextStyle
import com.kispoko.tome.model.sheet.widget.table.*
import com.kispoko.tome.model.sheet.widget.table.column.BooleanColumnFormat
import com.kispoko.tome.rts.sheet.SheetContext
import com.kispoko.tome.rts.sheet.SheetManager
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueError
import lulo.value.ValueParser
import java.io.Serializable
import java.util.*



/**
 * Boolean Cell Format
 */
data class BooleanCellFormat(override val id : UUID,
                             val cellFormat : Comp<CellFormat>,
                             val trueStyle : Maybe<Comp<TextStyle>>,
                             val falseStyle : Maybe<Comp<TextStyle>>,
                             val trueText : Prim<TrueText>,
                             val falseText : Prim<FalseText>,
                             val showTrueIcon : Prim<ShowTrueIcon>,
                             val showFalseIcon : Prim<ShowFalseIcon>) : Model, Serializable
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.cellFormat.name                        = "cell_format"

        when (this.trueStyle) {
            is Just -> this.trueStyle.value.name    = "true_style"
        }

        when (this.falseStyle) {
            is Just -> this.falseStyle.value.name   = "false_style"
        }

        this.trueText.name                          = "true_text"

        this.falseText.name                         = "false_text"

        this.showTrueIcon.name                      = "show_true_icon"

        this.showFalseIcon.name                     = "show_false_icon"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<BooleanCellFormat>
    {

        private val defaultCellFormat    = CellFormat.default
        private val defaultTrueText      = TrueText("True")
        private val defaultFalseText     = FalseText("False")
        private val defaultShowTrueIcon  = ShowTrueIcon(false)
        private val defaultShowFalseIcon = ShowFalseIcon(false)


        override fun fromDocument(doc : SpecDoc) : ValueParser<BooleanCellFormat> = when (doc)
        {
            is DocDict ->
            {
                effApply(::BooleanCellFormat,
                         // Model Id
                         effValue(UUID.randomUUID()),
                         // Cell Format
                         split(doc.maybeAt("cell_format"),
                               effValue(Comp.default(defaultCellFormat)),
                               { effApply(::Comp, CellFormat.fromDocument(it)) }),
                         // True Style
                         split(doc.maybeAt("true_style"),
                               effValue(Nothing()),
                               { TextStyle.fromDocument(it) ap {
                                   effValue<ValueError,Maybe<Comp<TextStyle>>>(Just(Comp(it)))} }),
                         // False Style
                         split(doc.maybeAt("false_style"),
                               effValue(Nothing()),
                               { TextStyle.fromDocument(it) ap {
                                    effValue<ValueError,Maybe<Comp<TextStyle>>>(Just(Comp(it)))} }),
                         // True Text
                         split(doc.maybeAt("true_text"),
                               effValue(Prim.default(defaultTrueText)),
                               { effApply(::Prim, TrueText.fromDocument(it)) }),
                         // False Text
                         split(doc.maybeAt("false_text"),
                               effValue(Prim.default(defaultFalseText)),
                               { effApply(::Prim, FalseText.fromDocument(it)) }),
                         // Show True Icon?
                         split(doc.maybeAt("show_true_icon"),
                               effValue(Prim.default(defaultShowTrueIcon)),
                               { effApply(::Prim, ShowTrueIcon.fromDocument(it)) }),
                         // Show False Icon?
                         split(doc.maybeAt("show_false_icon"),
                               effValue(Prim.default(defaultShowFalseIcon)),
                               { effApply(::Prim, ShowFalseIcon.fromDocument(it)) })
                         )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        val default : BooleanCellFormat =
                BooleanCellFormat(UUID.randomUUID(),
                                  Comp.default(defaultCellFormat),
                                  Nothing<Comp<TextStyle>>(),
                                  Nothing<Comp<TextStyle>>(),
                                  Prim.default(defaultTrueText),
                                  Prim.default(defaultFalseText),
                                  Prim.default(defaultShowTrueIcon),
                                  Prim.default(defaultShowFalseIcon))

    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun cellFormat() : CellFormat = this.cellFormat.value

    fun trueStyle() : Maybe<TextStyle> = getMaybeComp(this.trueStyle)

    fun falseStyle() : Maybe<TextStyle> = getMaybeComp(this.falseStyle)

    fun trueText() : String = this.trueText.value.value

    fun falseText() : String = this.falseText.value.value

    fun showTrueIcon() : Boolean = this.showTrueIcon.value.value

    fun showFalseIcon() : Boolean = this.showFalseIcon.value.value


    // -----------------------------------------------------------------------------------------
    // RESOLVERS
    // -----------------------------------------------------------------------------------------

    fun resolveTextStyle(columnFormat : BooleanColumnFormat) : TextStyle
    {
        if (this.cellFormat().textStyle.isDefault())
            return this.cellFormat().textStyle()

        return columnFormat.columnFormat().textStyle()
    }


    fun resolveTrueStyle(columnFormat : BooleanColumnFormat) : TextStyle?
    {
        when (this.trueStyle) {
            is Just -> return this.trueStyle.value.value
        }

        when (columnFormat.trueStyle) {
            is Just -> return columnFormat.trueStyle.value.value
        }

        return null
    }


    fun resolveFalseStyle(columnFormat : BooleanColumnFormat) : TextStyle?
    {
        when (this.falseStyle) {
            is Just -> return this.falseStyle.value.value
        }

        when (columnFormat.falseStyle) {
            is Just -> return columnFormat.falseStyle.value.value
        }

        return null
    }


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name = "boolean_cell_format"

    override val modelObject = this

}


/**
 * Show True Icon
 */
data class ShowTrueIcon(val value : Boolean) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ShowTrueIcon>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<ShowTrueIcon> = when (doc)
        {
            is DocBoolean -> effValue(ShowTrueIcon(doc.boolean))
            else          -> effError(UnexpectedType(DocType.BOOLEAN, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLInt({ if(this.value) 1 else 0 })

}


/**
 * Show False Icon
 */
data class ShowFalseIcon(val value : Boolean) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ShowFalseIcon>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<ShowFalseIcon> = when (doc)
        {
            is DocBoolean -> effValue(ShowFalseIcon(doc.boolean))
            else          -> effError(UnexpectedType(DocType.BOOLEAN, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLInt({ if(this.value) 1 else 0 })

}


/**
 * True Text
 */
data class TrueText(val value : String) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<TrueText>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<TrueText> = when (doc)
        {
            is DocText -> effValue(TrueText(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue(): SQLValue = SQLText({this.value})

}


/**
 * False Text
 */
data class FalseText(val value : String) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<FalseText>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<FalseText> = when (doc)
        {
            is DocText -> effValue(FalseText(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue(): SQLValue = SQLText({this.value})

}


object BooleanCellView
{

    fun view(cell : TableWidgetBooleanCell,
             rowFormat : TableWidgetRowFormat,
             column : TableWidgetBooleanColumn,
             cellFormat : BooleanCellFormat,
             sheetContext : SheetContext) : View
    {

        val layout = TableWidgetCellView.layout(rowFormat,
                                                column.format().columnFormat(),
                                                cellFormat.cellFormat(),
                                                sheetContext)

//        if (cellFormat.showTrueIcon() || columnFormat.showTrueIcon())
//        {
//            layout.addView(this.valueIconView(context))
//        }

        // > Text View
        // -------------------------------------------------------------------------------------

        val valueView = this.valueTextView(cell, column, cellFormat, sheetContext)
        layout.addView(valueView)

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


        return layout
    }



    private fun valueIconView(cell : TableWidgetBooleanCell,
                              columnFormat : BooleanColumnFormat,
                              cellFormat : BooleanCellFormat,
                              sheetContext : SheetContext) : ImageView
    {
        val icon = ImageViewBuilder()

        // > LAYOUT
        icon.width          = LinearLayout.LayoutParams.WRAP_CONTENT
        icon.height         = LinearLayout.LayoutParams.WRAP_CONTENT

        // > IMAGE
        if (cell.value())
            icon.image      = R.drawable.ic_boolean_cell_true
        else
            icon.image      = R.drawable.ic_boolean_cell_false

        // > MARGINS
        icon.margin.rightDp = 4f

        // > COLOR
        val trueStyle   = cellFormat.resolveTrueStyle(columnFormat)
        val falseStyle  = cellFormat.resolveFalseStyle(columnFormat)
        val normalStyle = cellFormat.resolveTextStyle(columnFormat)
        if (cell.value() && trueStyle != null)
        {
            icon.color      = SheetManager.color(sheetContext.sheetId, trueStyle.colorTheme())
        }
        else if (!cell.value() && falseStyle != null)
        {
            icon.color      = SheetManager.color(sheetContext.sheetId, falseStyle.colorTheme())
        }
        else
        {
            icon.color      = SheetManager.color(sheetContext.sheetId, normalStyle.colorTheme())
        }

        return icon.imageView(sheetContext.context)
    }


    // TODO remove cell format param
    // TODO resolve true and false text
    private fun valueTextView(cell : TableWidgetBooleanCell,
                              column : TableWidgetBooleanColumn,
                              cellFormat : BooleanCellFormat,
                              sheetContext : SheetContext) : TextView
    {
        val value = TextViewBuilder()

        value.layoutType        = LayoutType.TABLE_ROW
        value.width             = TableRow.LayoutParams.WRAP_CONTENT
        value.height            = TableRow.LayoutParams.WRAP_CONTENT

        // > VALUE
        if (cell.value())
            value.text          = column.format().trueText()
        else
            value.text          = column.format().falseText()

        // > STYLE
        val defaultStyle  = cellFormat.resolveTextStyle(column.format())
        val trueStyle     = cellFormat.resolveTrueStyle(column.format())
        val falseStyle    = cellFormat.resolveFalseStyle(column.format())

        if (cell.value() && trueStyle != null)
            trueStyle.styleTextViewBuilder(value, sheetContext)
        else if (!cell.value() && falseStyle != null)
            falseStyle.styleTextViewBuilder(value, sheetContext)
        else
            defaultStyle.styleTextViewBuilder(value, sheetContext)

        return value.textView(sheetContext.context)
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


