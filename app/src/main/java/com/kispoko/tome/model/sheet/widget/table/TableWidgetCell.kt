
package com.kispoko.tome.model.sheet.widget.table


import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TableRow
import com.kispoko.tome.app.AppEff
import com.kispoko.tome.app.AppSheetError
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.*
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.lib.ui.LayoutType
import com.kispoko.tome.lib.ui.LinearLayoutBuilder
import com.kispoko.tome.model.game.engine.variable.*
import com.kispoko.tome.model.sheet.style.Alignment
import com.kispoko.tome.model.sheet.style.NumericEditorType
import com.kispoko.tome.model.sheet.style.TextStyle
import com.kispoko.tome.model.sheet.widget.TableWidget
import com.kispoko.tome.model.sheet.widget.table.cell.*
import com.kispoko.tome.model.theme.ColorTheme
import com.kispoko.tome.rts.sheet.CellVariableUndefined
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
sealed class TableWidgetCell : ToDocument, Model, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<TableWidgetCell>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<TableWidgetCell> = when (doc)
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
                                  val variableValue : Sum<BooleanVariableValue>,
                                  var variableId : VariableId?)
                                  : TableWidgetCell(), Model
{

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

    constructor() : this(UUID.randomUUID(),
                         Comp(BooleanCellFormat.default()),
                         Sum(BooleanVariableLiteralValue(true)),
                         null)


    constructor(variableValue : BooleanVariableValue)
        : this(UUID.randomUUID(),
               Comp(BooleanCellFormat.default()),
               Sum(variableValue),
               null)


    constructor(format : BooleanCellFormat,
                variableValue : BooleanVariableValue)
        : this(UUID.randomUUID(),
               Comp(format),
               Sum(variableValue),
               null)


    companion object : Factory<TableWidgetBooleanCell>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<TableWidgetBooleanCell> = when (doc)
        {
            is DocDict ->
            {
                effApply(::TableWidgetBooleanCell,
                         // Format
                         split(doc.maybeAt("format"),
                               effValue(BooleanCellFormat.default()),
                               { BooleanCellFormat.fromDocument(it) }),
                         // Variable Value
                         doc.at("variable_value") ap { BooleanVariableValue.fromDocument(it) }
                         )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "format" to this.format().toDocument(),
        "variable_value" to this.variableValue().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun format() : BooleanCellFormat = this.format.value


    fun variableValue() : BooleanVariableValue = this.variableValue.value


    fun variableId() : AppEff<VariableId> =
            note(this.variableId, AppSheetError(CellVariableUndefined(this.id)))


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

    fun valueVariable(sheetContext : SheetContext) : AppEff<BooleanVariable> =
        this.variableId()                             ap { variableId ->
        SheetManager.sheetState(sheetContext.sheetId) ap { state ->
        state.booleanVariableWithId(variableId)
            } }


    fun value(sheetContext : SheetContext) : AppEff<Boolean> =
        this.valueVariable(sheetContext) ap { it.value() }


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
                                 val variableValue : Sum<NumberVariableValue>,
                                 val editorType : Prim<NumericEditorType>,
                                 var variableId : VariableId?)
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
        this.variableValue.name     = "variable_value"
        this.editorType.name        = "editor_type"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(variableValue : NumberVariableValue)
        : this(UUID.randomUUID(),
               Comp(NumberCellFormat.default()),
               Sum(variableValue),
               Prim(NumericEditorType.Simple),
               null)


    constructor(format : NumberCellFormat,
                variableValue : NumberVariableValue,
                editorType : NumericEditorType)
        : this(UUID.randomUUID(),
               Comp(format),
               Sum(variableValue),
               Prim(editorType),
               null)


    companion object : Factory<TableWidgetNumberCell>
    {

        private val defaultNumberCellFormat = NumberCellFormat.default()
        private val defaultEditorType       = NumericEditorType.Calculator

        override fun fromDocument(doc: SchemaDoc): ValueParser<TableWidgetNumberCell> = when (doc)
        {
            is DocDict ->
            {
                effApply(::TableWidgetNumberCell,
                         // Format
                         split(doc.maybeAt("format"),
                               effValue(defaultNumberCellFormat),
                               { NumberCellFormat.fromDocument(it) }),
                         // Variable Value
                         doc.at("variable_value") ap { NumberVariableValue.fromDocument(it) },
                         // Editor Type
                         split(doc.maybeAt("editor_type"),
                               effValue<ValueError,NumericEditorType>(defaultEditorType),
                               { NumericEditorType.fromDocument(it) })
                         )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "format" to this.format().toDocument(),
        "variable_value" to this.variableValue().toDocument(),
        "editor_type" to this.editorType().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun format() : NumberCellFormat = this.format.value

    fun variableValue() : NumberVariableValue = this.variableValue.value

    fun editorType() : NumericEditorType = this.editorType.value

    fun resolveEditorType(column : TableWidgetNumberColumn) : NumericEditorType =
        if (this.editorType.isDefault())
            column.editorType()
        else
            this.editorType()


    fun variableId() : AppEff<VariableId> =
        note(this.variableId, AppSheetError(CellVariableUndefined(this.id)))


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

    fun valueVariable(sheetContext : SheetContext) : AppEff<NumberVariable> =
        this.variableId()                             ap { variableId ->
        SheetManager.sheetState(sheetContext.sheetId) ap { state ->
        state.numberVariableWithId(variableId)
            } }


    fun valueString(sheetContext : SheetContext) : AppEff<String> =
         this.valueVariable(sheetContext) ap { it.valueString(sheetContext) }


    fun updateValue(newValue : Double, sheetContext : SheetContext) =
        this.valueVariable(sheetContext) apDo {
            it.updateValue(newValue, sheetContext.sheetId) }


    // -----------------------------------------------------------------------------------------
    // VIEW
    // -----------------------------------------------------------------------------------------

    fun view(rowFormat : TableWidgetRowFormat,
             column : TableWidgetNumberColumn,
             rowIndex : Int,
             tableWidget : TableWidget,
             sheetUIContext : SheetUIContext) : View
    {
        val viewBuilder = NumberCellViewBuilder(this,
                                                rowFormat,
                                                column,
                                                rowIndex,
                                                tableWidget,
                                                sheetUIContext)
        return viewBuilder.view()
    }

}


/**
 * Table Widget Text Cell
 */
data class TableWidgetTextCell(override val id : UUID,
                               val format : Comp<TextCellFormat>,
                               val variableValue : Sum<TextVariableValue>,
                               var variableId : VariableId?)
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

    constructor(variableValue : TextVariableValue)
        : this(UUID.randomUUID(),
               Comp(TextCellFormat.default()),
               Sum(variableValue),
               null)

    constructor(format : TextCellFormat,
                variableValue : TextVariableValue)
        : this(UUID.randomUUID(),
               Comp(format),
               Sum(variableValue),
               null)


    companion object : Factory<TableWidgetTextCell>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<TableWidgetTextCell> = when (doc)
        {
            is DocDict ->
            {
                effApply(::TableWidgetTextCell,
                         // Format
                         split(doc.maybeAt("format"),
                               effValue(TextCellFormat.default()),
                                { TextCellFormat.fromDocument(it) }),
                         // Variable Value
                         doc.at("variable_value") ap { TextVariableValue.fromDocument(it) }
                        )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "format" to this.format().toDocument(),
        "variable_value" to this.variableValue().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun format() : TextCellFormat = this.format.value


    fun variableValue() : TextVariableValue = this.variableValue.value


    fun variableId() : AppEff<VariableId> =
            note(this.variableId, AppSheetError(CellVariableUndefined(this.id)))


    fun valueVariable(sheetContext : SheetContext) : AppEff<TextVariable> =
        this.variableId()                             ap { variableId ->
        SheetManager.sheetState(sheetContext.sheetId) ap { state ->
        state.textVariableWithId(variableId)
    } }

//
//    fun valueVariable(sheetContext : SheetContext) : Maybe<TextVariable> =
//        this.variableId ap { variableId ->
//            val variable = SheetManager.sheetState(sheetContext.sheetId)
//                                           .apply { it.textVariableWithId(variableId) }
//            when (variable) {
//                is Val -> Just(variable.value)
//                is Err -> {
//                    ApplicationLog.error(variable.error)
//                    Nothing<TextVariable>()
//                }
//            }
//        }


    // -----------------------------------------------------------------------------------------
    // CELL
    // -----------------------------------------------------------------------------------------

    override fun type() : TableWidgetCellType = TableWidgetCellType.TEXT


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name : String = "table_widget_number_cell"

    override val modelObject = this


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    fun valueString(sheetContext : SheetContext) : AppEff<String> =
            this.valueVariable(sheetContext) ap { it.valueString(sheetContext) }
//
//    fun valueString(sheetContext : SheetContext) : Maybe<String> =
//        this.valueVariable(sheetContext) ap { variable ->
//            val str = variable.valueString(sheetContext)
//            when (str) {
//                is Val -> Just(str.value)
//                is Err -> {
//                    ApplicationLog.error(str.error)
//                    Nothing<String>()
//                }
//            }
//        }



    // -----------------------------------------------------------------------------------------
    // VIEW
    // -----------------------------------------------------------------------------------------

    fun view(rowFormat : TableWidgetRowFormat,
             column : TableWidgetTextColumn,
             rowIndex : Int,
             tableWidget : TableWidget,
             sheetUIContext: SheetUIContext) : View
    {
        val viewBuilder = TextCellViewBuilder(this,
                                              rowFormat,
                                              column,
                                              rowIndex,
                                              tableWidget,
                                              sheetUIContext)
        return viewBuilder.view()
    }

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
                      val backgroundColorTheme : Prim<ColorTheme>)
                       : ToDocument, Model, Serializable
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


        override fun fromDocument(doc: SchemaDoc): ValueParser<CellFormat> = when (doc)
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
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "text_style" to this.textStyle().toDocument(),
        "alignment" to this.alignment().toDocument(),
        "background_color_theme" to this.backgroundColorTheme().toDocument()
    ))


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
        layout.weight               = columnFormat.widthFloat()


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
