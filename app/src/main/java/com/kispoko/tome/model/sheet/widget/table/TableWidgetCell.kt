
package com.kispoko.tome.model.sheet.widget.table


import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TableRow
import android.widget.TextView
import com.kispoko.tome.app.AppEff
import com.kispoko.tome.app.AppError
import com.kispoko.tome.app.AppSheetError
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.*
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.lib.ui.LayoutType
import com.kispoko.tome.lib.ui.LinearLayoutBuilder
import com.kispoko.tome.model.game.engine.variable.*
import com.kispoko.tome.model.sheet.style.Alignment
import com.kispoko.tome.model.sheet.style.NumberFormat
import com.kispoko.tome.model.sheet.style.NumericEditorType
import com.kispoko.tome.model.sheet.style.TextStyle
import com.kispoko.tome.model.sheet.widget.Action
import com.kispoko.tome.model.sheet.widget.ActionName
import com.kispoko.tome.model.sheet.widget.TableWidget
import com.kispoko.tome.model.sheet.widget.table.cell.*
import com.kispoko.tome.model.theme.ColorTheme
import com.kispoko.tome.rts.sheet.CellVariableUndefined
import com.kispoko.tome.rts.sheet.SheetContext
import com.kispoko.tome.rts.sheet.SheetUIContext
import com.kispoko.tome.rts.sheet.SheetManager
import com.kispoko.tome.util.Util
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

    abstract fun updateView(sheetUIContext : SheetUIContext)

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

    var namespace : VariableNamespace? = null

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


    override fun updateView(sheetUIContext : SheetUIContext) {
    }


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
                                 val action : Maybe<Comp<Action>>,
                                 var variableId : VariableId?)
                                  : TableWidgetCell(), Model
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var viewId : Int? = null
    var column : TableWidgetNumberColumn? = null
    var namespace : VariableNamespace? = null


    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.format.name            = "format"
        this.variableValue.name     = "variable_value"
        this.editorType.name        = "editor_type"

        when (this.action) {
            is Just -> this.action.value.name       = "action"
        }
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(variableValue : NumberVariableValue)
        : this(UUID.randomUUID(),
               Comp.default(NumberCellFormat.default()),
               Sum(variableValue),
               Prim.default(NumericEditorType.Simple),
               Nothing(),
               null)


    constructor(format : NumberCellFormat,
                variableValue : NumberVariableValue,
                editorType : NumericEditorType)
        : this(UUID.randomUUID(),
               Comp(format),
               Sum(variableValue),
               Prim(editorType),
               Nothing(),
               null)


    companion object : Factory<TableWidgetNumberCell>
    {

        private val defaultNumberCellFormat = NumberCellFormat.default()
        private val defaultEditorType       = NumericEditorType.Calculator
        private val defaultAction           = Nothing<Action>()

        override fun fromDocument(doc: SchemaDoc): ValueParser<TableWidgetNumberCell> = when (doc)
        {
            is DocDict ->
            {
                apply(::TableWidgetNumberCell,
                      // Model Id
                      effValue(UUID.randomUUID()),
                      // Format
                      split(doc.maybeAt("format"),
                            effValue(Comp.default(defaultNumberCellFormat)),
                            { apply(::Comp, NumberCellFormat.fromDocument(it)) }),
                      // Variable Value
                      doc.at("variable_value") ap {
                          apply(::Sum, NumberVariableValue.fromDocument(it))
                      },
                      // Editor Type
                      split(doc.maybeAt("editor_type"),
                            effValue<ValueError,Prim<NumericEditorType>>(Prim.default(defaultEditorType)),
                            { apply(::Prim, NumericEditorType.fromDocument(it)) }),
                      // Action
                      split(doc.maybeAt("action"),
                            effValue<ValueError,Maybe<Comp<Action>>>(Nothing()),
                            { effApply({x -> Just(Comp(x))}, Action.fromDocument(it)) }),
                      effValue(null)
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
    .maybeMerge(this.action().apply {
        Just(Pair("action", it.toDocument() as SchemaDoc)) })


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


    fun action() : Maybe<Action> = getMaybeComp(this.action)


    // -----------------------------------------------------------------------------------------
    // CELL
    // -----------------------------------------------------------------------------------------

    override fun type() : TableWidgetCellType = TableWidgetCellType.NUMBER


    override fun updateView(sheetUIContext : SheetUIContext) {
        this.viewId?.let {
            val activity = sheetUIContext.context as AppCompatActivity
            val valueTextView = activity.findViewById(it) as TextView?
            val maybeValue = this.value(SheetContext(sheetUIContext))
            when (maybeValue)
            {
                is Val    -> {
                    val numberFormat = this.column?.format()?.let {
                                           this.format().resolveNumberFormat(it)
                                       } ?: NumberFormat.Normal
                    when (numberFormat) {
                        is NumberFormat.Modifier -> {
                            valueTextView?.text = numberFormat.formattedString(maybeValue.value)
                        }
                        else -> {
                            valueTextView?.text = Util.doubleString(maybeValue.value)
                        }
                    }
                }
                is Err -> ApplicationLog.error(maybeValue.error)
            }
        }
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

    fun valueVariable(sheetContext : SheetContext) : AppEff<NumberVariable> =
        this.variableId()                             ap { variableId ->
        SheetManager.sheetState(sheetContext.sheetId) ap { state ->
        state.numberVariableWithId(variableId)
            } }


    fun valueString(sheetContext : SheetContext) : AppEff<String> =
         this.valueVariable(sheetContext) ap { it.valueString(sheetContext) }


    fun value(sheetContext : SheetContext) : AppEff<Double> =
            this.valueVariable(sheetContext)
                .apply { it.value(sheetContext) }
                .apply { effValue<AppError,Double>(maybe(0.0, it)) }


    fun updateValue(newValue : Double, sheetContext : SheetContext) =
        this.valueVariable(sheetContext) apDo {
            it.updateValue(newValue, sheetContext.sheetId) }


    // -----------------------------------------------------------------------------------------
    // VIEW
    // -----------------------------------------------------------------------------------------

    fun view(row : TableWidgetRow,
             column : TableWidgetNumberColumn,
             rowIndex : Int,
             tableWidget : TableWidget,
             sheetUIContext : SheetUIContext) : View
    {
        val viewBuilder = NumberCellViewBuilder(this,
                                                row,
                                                column,
                                                rowIndex,
                                                tableWidget,
                                                sheetUIContext)
        this.column = column
        return viewBuilder.view()
    }


}


/**
 * Table Widget Text Cell
 */
data class TableWidgetTextCell(override val id : UUID,
                               val format : Comp<TextCellFormat>,
                               val variableValue : Sum<TextVariableValue>,
                               val action : Maybe<Comp<Action>>,
                               val actionName : Maybe<Prim<ActionName>>,
                               var variableId : VariableId?)
                                : TableWidgetCell(), Model
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var viewId : Int? = null

    var namespace : VariableNamespace? = null


    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.format.name        = "format"
        this.variableValue.name = "variable_value"

        when (this.action) {
            is Just -> this.action.value.name       = "action"
        }

        when (this.actionName) {
            is Just -> this.actionName.value.name = "action_name"
        }
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(variableValue : TextVariableValue)
        : this(UUID.randomUUID(),
               Comp(TextCellFormat.default()),
               Sum(variableValue),
               Nothing(),
               Nothing(),
               null)

    constructor(format : TextCellFormat,
                variableValue : TextVariableValue,
                action : Maybe<Action>,
                actionName : Maybe<ActionName>)
        : this(UUID.randomUUID(),
               Comp(format),
               Sum(variableValue),
               maybeLiftComp(action),
               maybeLiftPrim(actionName),
               null)


    companion object : Factory<TableWidgetTextCell>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<TableWidgetTextCell> = when (doc)
        {
            is DocDict ->
            {
                apply(::TableWidgetTextCell,
                      // Model Id
                      effValue(UUID.randomUUID()),
                      // Format
                      split(doc.maybeAt("format"),
                            effValue(Comp.default(TextCellFormat.default())),
                             { apply(::Comp, TextCellFormat.fromDocument(it)) }),
                      // Variable Value
                      doc.at("variable_value") ap {
                          apply(::Sum, TextVariableValue.fromDocument(it))
                      },
                      // Action
                      split(doc.maybeAt("action"),
                            effValue<ValueError,Maybe<Comp<Action>>>(Nothing()),
                            { effApply({x -> Just(Comp(x))}, Action.fromDocument(it)) } ),
                       // Action Name
                       split(doc.maybeAt("action_name"),
                             effValue<ValueError,Maybe<Prim<ActionName>>>(Nothing()),
                             { effApply({x -> Just(Prim(x))}, ActionName.fromDocument(it)) } ),
                      effValue(null)
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
    .maybeMerge(this.action().apply {
        Just(Pair("action", it.toDocument() as SchemaDoc)) })


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


    fun action() : Maybe<Action> = getMaybeComp(this.action)


    fun actionName() : Maybe<ActionName> = _getMaybePrim(this.actionName)


    fun actionNameString() : Maybe<String> = this.actionName() ap { Just(it.value) }

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


    fun resolveAction(column : TableWidgetTextColumn) : Maybe<Action> =
        when (this.action)
        {
            is Just -> this.action()
            else    -> column.action()
        }


    // -----------------------------------------------------------------------------------------
    // CELL
    // -----------------------------------------------------------------------------------------

    override fun type() : TableWidgetCellType = TableWidgetCellType.TEXT


    override fun updateView(sheetUIContext : SheetUIContext) {
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


        fun default() = CellFormat(UUID.randomUUID(),
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
//            layout.layoutGravity          = cellFormat.alignment().gravityConstant() or
//                                                Gravity.CENTER_VERTICAL
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
