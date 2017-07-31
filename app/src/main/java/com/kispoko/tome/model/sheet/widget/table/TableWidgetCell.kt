
package com.kispoko.tome.model.sheet.widget.table


import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TableRow
import com.kispoko.tome.app.AppEff
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.*
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.lib.ui.LayoutType
import com.kispoko.tome.lib.ui.LinearLayoutBuilder
import com.kispoko.tome.model.game.engine.variable.*
import com.kispoko.tome.model.sheet.style.Alignment
import com.kispoko.tome.model.sheet.style.NumericEditorType
import com.kispoko.tome.model.sheet.style.TextStyle
import com.kispoko.tome.model.sheet.widget.table.cell.*
import com.kispoko.tome.model.theme.ColorTheme
import com.kispoko.tome.rts.sheet.SheetContext
import com.kispoko.tome.rts.sheet.SheetUIContext
import com.kispoko.tome.rts.sheet.SheetManager
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.UnknownCase
import lulo.value.ValueError
import lulo.value.ValueParser
import java.io.Serializable
import java.util.*



/**
 * Table Widget Cell
 */
@Suppress("UNCHECKED_CAST")
sealed class TableWidgetCell : Model, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<TableWidgetCell>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<TableWidgetCell> = when (doc)
        {
            is DocDict ->
            {
                when (doc.case())
                {
                    "table_widget_boolean_cell" -> TableWidgetBooleanCell.fromDocument(doc)
                                                    as ValueParser<TableWidgetCell>
                    "table_widget_number_cell"  -> TableWidgetNumberCell.fromDocument(doc)
                                                    as ValueParser<TableWidgetCell>
                    "table_widget_text_cell"    -> TableWidgetTextCell.fromDocument(doc)
                                                    as ValueParser<TableWidgetCell>
                    else                        -> effError<ValueError, TableWidgetCell>(
                                                    UnknownCase(doc.case(), doc.path))
                }
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // CELL
    // -----------------------------------------------------------------------------------------

    abstract fun type() : TableWidgetCellType

}


/**
 * Table Widget Boolean Cell
 */
data class TableWidgetBooleanCell(override val id : UUID,
                                  val format : Comp<BooleanCellFormat>,
                                  val valueVariable : Comp<BooleanVariable>)
                                  : TableWidgetCell(), Model
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.format.name        = "format"
        this.valueVariable.name = "value_variable"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(format : BooleanCellFormat, valueVariable : BooleanVariable)
        : this(UUID.randomUUID(), Comp(format), Comp(valueVariable))


    companion object : Factory<TableWidgetBooleanCell>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<TableWidgetBooleanCell> = when (doc)
        {
            is DocDict ->
            {
                effApply(::TableWidgetBooleanCell,
                         // Format
                         split(doc.maybeAt("format"),
                               effValue(BooleanCellFormat.default),
                               { BooleanCellFormat.fromDocument(it) }),
                         // Value
                         doc.at("value_variable") ap { BooleanVariable.fromDocument(it) }
                        )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun format() : BooleanCellFormat = this.format.value

    fun valueVariable() : BooleanVariable = this.valueVariable.value


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name : String = "table_widget_boolean_cell"

    override val modelObject = this


    // -----------------------------------------------------------------------------------------
    // CELL
    // -----------------------------------------------------------------------------------------

    override fun type() : TableWidgetCellType = TableWidgetCellType.BOOLEAN


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    fun value() : Boolean
    {
        val booleanEff = this.valueVariable().value()

        when (booleanEff)
        {
            is Val -> return booleanEff.value
            is Err -> ApplicationLog.error(booleanEff.error)
        }

        return true
    }


    // -----------------------------------------------------------------------------------------
    // VIEW
    // -----------------------------------------------------------------------------------------

    fun view(rowFormat : TableWidgetRowFormat,
             column : TableWidgetBooleanColumn,
             sheetUIContext: SheetUIContext) : View
        = BooleanCellView.view(this, rowFormat, column, this.format(), sheetUIContext)

}


/**
 * Table Widget Number Cell
 */
data class TableWidgetNumberCell(override val id : UUID,
                                 val format : Comp<NumberCellFormat>,
                                 val valueVariable : Comp<NumberVariable>,
                                 val editorType : Prim<NumericEditorType>)
                                  : TableWidgetCell(), Model
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var viewId : Maybe<Int> = Nothing()


    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.format.name            = "format"
        this.valueVariable.name     = "value_variable"
        this.editorType.name        = "editor_type"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------


    companion object : Factory<TableWidgetNumberCell>
    {

        private val defaultNumberCellFormat = NumberCellFormat.default()
        private val defaultEditorType       = NumericEditorType.Calculator

        override fun fromDocument(doc : SpecDoc) : ValueParser<TableWidgetNumberCell> = when (doc)
        {
            is DocDict ->
            {
                effApply(::TableWidgetNumberCell,
                         // Model Id
                         effValue(UUID.randomUUID()),
                         // Format
                         split(doc.maybeAt("format"),
                               effValue(Comp.default(defaultNumberCellFormat)),
                               { effApply(::Comp, NumberCellFormat.fromDocument(it)) }),
                         // Value
                         doc.at("value_variable") ap {
                             effApply(::Comp, NumberVariable.fromDocument(it))
                         },
                         // Editor Type
                         split(doc.maybeAt("editor_type"),
                               effValue<ValueError,Prim<NumericEditorType>>(Prim.default(defaultEditorType)),
                               { effApply(::Prim, NumericEditorType.fromDocument(it)) })
                        )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun format() : NumberCellFormat = this.format.value

    fun valueVariable() : NumberVariable = this.valueVariable.value

    fun editorType() : NumericEditorType = this.editorType.value


    fun resolveEditorType(column : TableWidgetNumberColumn) : NumericEditorType =
        if (this.editorType.isDefault())
            column.editorType()
        else
            this.editorType()


    // -----------------------------------------------------------------------------------------
    // CELL
    // -----------------------------------------------------------------------------------------

    override fun type() : TableWidgetCellType = TableWidgetCellType.NUMBER


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name : String = "table_widget_number_cell"

    override val modelObject = this


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    fun valueString(sheetUIContext: SheetUIContext) : Maybe<String>
    {
        val numberEff = this.valueVariable().value(SheetContext(sheetUIContext))

        when (numberEff)
        {
            is Val -> return Just(numberEff.value.toString())
            is Err -> return Nothing()
        }
    }


    // -----------------------------------------------------------------------------------------
    // VIEW
    // -----------------------------------------------------------------------------------------

    fun view(rowFormat : TableWidgetRowFormat,
             column : TableWidgetNumberColumn,
             sheetUIContext: SheetUIContext) : View
            = NumberCellView.view(this, rowFormat, column, this.format(), sheetUIContext)

}


/**
 * Table Widget Text Cell
 */
data class TableWidgetTextCell(override val id : UUID,
                               val format : Comp<TextCellFormat>,
                               val variableValue : Sum<TextVariableValue>,
                               var variableId : Maybe<VariableId>)
                                : TableWidgetCell(), Model
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var viewId : Maybe<Int> = Nothing()


    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.format.name        = "format"
        this.variableValue.name = "variable_value"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(format : TextCellFormat,
                variableValue : TextVariableValue)
        : this(UUID.randomUUID(),
               Comp(format),
               Sum(variableValue),
               Nothing())


    companion object : Factory<TableWidgetTextCell>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<TableWidgetTextCell> = when (doc)
        {
            is DocDict ->
            {
                effApply(::TableWidgetTextCell,
                         // Format
                         split(doc.maybeAt("format"),
                               effValue(TextCellFormat.default),
                                { TextCellFormat.fromDocument(it) }),
                         // Variable Value
                         doc.at("variable_value") ap { TextVariableValue.fromDocument(it) }
                        )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun format() : TextCellFormat = this.format.value


    fun variableValue() : TextVariableValue = this.variableValue.value


    fun valueVariable(sheetContext : SheetContext) : Maybe<TextVariable> =
        this.variableId ap { variableId ->
            val variable = SheetManager.sheetState(sheetContext.sheetId)
                                           .apply { it.textVariableWithId(variableId) }
            when (variable) {
                is Val -> Just(variable.value)
                is Err -> {
                    ApplicationLog.error(variable.error)
                    Nothing<TextVariable>()
                }
            }
        }


    // -----------------------------------------------------------------------------------------
    // CELL
    // -----------------------------------------------------------------------------------------

    override fun type() : TableWidgetCellType = TableWidgetCellType.TEXT


    /**
     * Update the id of the variable that holds the cell data.
     */
    fun setVariableId(variableId : VariableId)
    {
        this.variableId = Just(variableId)
    }


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name : String = "table_widget_number_cell"

    override val modelObject = this


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    fun valueString(sheetContext : SheetContext) : Maybe<String> =
        this.valueVariable(sheetContext) ap { variable ->
            val str = variable.valueString(sheetContext)
            when (str) {
                is Val -> Just(str.value)
                is Err -> {
                    ApplicationLog.error(str.error)
                    Nothing<String>()
                }
            }
        }



    // -----------------------------------------------------------------------------------------
    // VIEW
    // -----------------------------------------------------------------------------------------

    fun view(rowFormat : TableWidgetRowFormat,
             column : TableWidgetTextColumn,
             sheetUIContext: SheetUIContext) : View
            = TextCellView.view(this, rowFormat, column, this.format(), sheetUIContext)

}


enum class TableWidgetCellType
{
    BOOLEAN,
    NUMBER,
    TEXT
}


/**
 * Table Widget Cell Format
 */
data class CellFormat(override val id : UUID,
                      val textStyle : Comp<TextStyle>,
                      val alignment : Prim<Alignment>,
                      val backgroundColorTheme : Prim<ColorTheme>) : Model, Serializable
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.textStyle.name             = "text_style"
        this.alignment.name             = "alignment"
        this.backgroundColorTheme.name  = "background_color_theme"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(textStyle: TextStyle,
                alignment : Alignment,
                backgroundColorTheme : ColorTheme)
        : this(UUID.randomUUID(),
               Comp(textStyle),
               Prim(alignment),
               Prim(backgroundColorTheme))


    companion object : Factory<CellFormat>
    {

        private val defaultTextStyle            = TextStyle.default()
        private val defaultAlignment            = Alignment.Center
        private val defaultBackgroundColorTheme = ColorTheme.transparent


        override fun fromDocument(doc : SpecDoc) : ValueParser<CellFormat> = when (doc)
        {
            is DocDict ->
            {
                effApply(::CellFormat,
                         // Model Id
                         effValue(UUID.randomUUID()),
                         // Text Style
                         split(doc.maybeAt("text_style"),
                               effValue(Comp.default(defaultTextStyle)),
                               { effApply(::Comp, TextStyle.fromDocument(it)) }),
                         // Alignment
                         split(doc.maybeAt("alignment"),
                               effValue<ValueError,Prim<Alignment>>(Prim.default(defaultAlignment)),
                               { effApply(::Prim, Alignment.fromDocument(it)) }),
                         // Background Color
                         split(doc.maybeAt("background_color_theme"),
                               effValue(Prim(defaultBackgroundColorTheme)),
                               { effApply(::Prim, ColorTheme.fromDocument(it)) })
                       )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        val default : CellFormat =
                CellFormat(UUID.randomUUID(),
                           Comp.default(defaultTextStyle),
                           Prim.default(defaultAlignment),
                           Prim.default(defaultBackgroundColorTheme))

    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun textStyle() : TextStyle = this.textStyle.value

    fun alignment() : Alignment = this.alignment.value

    fun backgroundColorTheme() : ColorTheme = this.backgroundColorTheme.value


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name : String = "table_widget_cell_format"

    override val modelObject = this

}


object TableWidgetCellView
{

    fun layout(tableRowFormat : TableWidgetRowFormat,
               columnFormat : ColumnFormat,
               cellFormat : CellFormat,
               sheetUIContext: SheetUIContext) : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.layoutType           = LayoutType.TABLE_ROW
        layout.orientation          = LinearLayout.HORIZONTAL
        layout.width                = 0
        layout.height               = TableRow.LayoutParams.WRAP_CONTENT
        layout.weight               = columnFormat.width()


        // > Gravity
        if (cellFormat.alignment.isDefault()) {
            layout.gravity          = columnFormat.alignment().gravityConstant() or
                                        Gravity.CENTER_VERTICAL
        } else {
            layout.gravity          = cellFormat.alignment().gravityConstant() or
                                        Gravity.CENTER_VERTICAL
            layout.layoutGravity          = cellFormat.alignment().gravityConstant() or
                                                Gravity.CENTER_VERTICAL
        }

        if (cellFormat.backgroundColorTheme.isDefault()) {
            layout.backgroundColor  = SheetManager.color(sheetUIContext.sheetId,
                                        columnFormat.backgroundColorTheme())
        } else {
            layout.backgroundColor  = SheetManager.color(sheetUIContext.sheetId,
                                        cellFormat.backgroundColorTheme())
        }

        // layout.backgroundResource   = tableRowFormat.cellHeight().resourceId(Corners.None)

        return layout.linearLayout(sheetUIContext.context)
    }


}
