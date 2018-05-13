
package com.kispoko.tome.model.sheet.widget.table


import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TableRow
import android.widget.TextView
import com.kispoko.tome.app.AppEff
import com.kispoko.tome.app.AppError
import com.kispoko.tome.app.AppSheetError
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.db.*
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.ProdType
import com.kispoko.tome.lib.orm.RowValue2
import com.kispoko.tome.lib.orm.RowValue3
import com.kispoko.tome.lib.orm.RowValue4
import com.kispoko.tome.lib.orm.schema.*
import com.kispoko.tome.lib.ui.LayoutType
import com.kispoko.tome.lib.ui.LinearLayoutBuilder
import com.kispoko.tome.model.book.BookReference
import com.kispoko.tome.model.engine.variable.*
import com.kispoko.tome.model.sheet.style.*
import com.kispoko.tome.model.sheet.widget.Action
import com.kispoko.tome.model.sheet.widget.TableWidget
import com.kispoko.tome.model.sheet.widget.table.cell.*
import com.kispoko.tome.rts.entity.*
import com.kispoko.tome.rts.entity.sheet.CellVariableUndefined
import com.kispoko.tome.util.Util
import effect.*
import effect.Val
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.UnknownCase
import lulo.value.ValueError
import lulo.value.ValueParser
import maybe.*
import maybe.Nothing
import java.io.Serializable
import java.util.*



/**
 * Table Widget Cell
 */
@Suppress("UNCHECKED_CAST")
sealed class TableWidgetCell : ToDocument, Serializable
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
                    "table_widget_image_cell"   -> TableWidgetImageCell.fromDocument(doc)
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


    abstract fun updateView(entityId : EntityId, context : Context)


    open fun variableId() : VariableId? = null


    abstract fun variableIdOrError() : AppEff<VariableId>

}


/**
 * Table Widget Boolean Cell
 */
data class TableWidgetBooleanCell(override val id : UUID,
                                  val format : BooleanCellFormat,
                                  val variableValue : BooleanVariableValue,
                                  var variableId : VariableId?)
                                  : TableWidgetCell(), ProdType
{

    var namespace : VariableNamespace? = null


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor() : this(UUID.randomUUID(),
                         BooleanCellFormat.default(),
                         BooleanVariableLiteralValue(true),
                         null)


    constructor(variableValue : BooleanVariableValue)
        : this(UUID.randomUUID(),
               BooleanCellFormat.default(),
               variableValue,
               null)


    constructor(format : BooleanCellFormat,
                variableValue : BooleanVariableValue)
        : this(UUID.randomUUID(),
               format,
               variableValue,
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

    fun format() : BooleanCellFormat = this.format


    fun variableValue() : BooleanVariableValue = this.variableValue


    override fun variableIdOrError() : AppEff<VariableId> =
            note(this.variableId, AppSheetError(CellVariableUndefined(this.id)))


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_WidgetTableCellBooleanValue =
        RowValue2(widgetTableCellBooleanTable,
                  ProdValue(this.format),
                  SumValue(this.variableValue))


    // -----------------------------------------------------------------------------------------
    // CELL
    // -----------------------------------------------------------------------------------------

    override fun type() : TableWidgetCellType = TableWidgetCellType.BOOLEAN


    override fun updateView(entityId : EntityId, context : Context) {
    }


    override fun variableId() = this.variableId


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    fun valueVariable(entityId : EntityId) : AppEff<BooleanVariable> =
        this.variableIdOrError().apply { booleanVariable(it, entityId) }


    fun value(entityId : EntityId) : AppEff<Boolean> =
        this.valueVariable(entityId) ap { it.value() }


    // -----------------------------------------------------------------------------------------
    // VIEW
    // -----------------------------------------------------------------------------------------

    fun view(rowFormat : TableWidgetRowFormat,
             column : TableWidgetBooleanColumn,
             entityId : EntityId,
             context : Context) : View
        = BooleanCellView.view(this, rowFormat, column, this.format(), entityId, context)

}


/**
 * Table Widget Icon Cell
 */
data class TableWidgetImageCell(private val id : UUID,
                                private val format : ImageCellFormat,
                                private val iconType : Maybe<IconType>,
                                private val bookReference : Maybe<BookReference>)
                                : TableWidgetCell()
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<TableWidgetImageCell>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<TableWidgetImageCell> = when (doc)
        {
            is DocDict ->
            {
                apply(::TableWidgetImageCell,
                      // Cell Id
                      effValue(UUID.randomUUID()) ,
                      // Format
                      split(doc.maybeAt("format"),
                            effValue(ImageCellFormat.default()),
                            { ImageCellFormat.fromDocument(it) }),
                      // Icon Type
                      split(doc.maybeAt("icon_type"),
                            effValue<ValueError,Maybe<IconType>>(Nothing()),
                            { apply(::Just, IconType.fromDocument(it)) }),
                      // Book Reference
                      split(doc.maybeAt("book_reference"),
                            effValue<ValueError,Maybe<BookReference>>(Nothing()),
                            { apply(::Just, BookReference.fromDocument(it)) })

                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun format() : ImageCellFormat = this.format


    fun iconType() : Maybe<IconType> = this.iconType


    fun bookReference() : Maybe<BookReference> = this.bookReference


    // -----------------------------------------------------------------------------------------
    // CELL
    // -----------------------------------------------------------------------------------------

    override fun type() : TableWidgetCellType = TableWidgetCellType.BOOLEAN


    override fun updateView(entityId : EntityId, context : Context) {
    }


    override fun variableId() = null


    // -----------------------------------------------------------------------------------------
    // VARIABLE
    // -----------------------------------------------------------------------------------------

    override fun variableIdOrError() : AppEff<VariableId> =
        effError(AppSheetError(CellVariableUndefined(this.id)))


    // -----------------------------------------------------------------------------------------
    // VIEW
    // -----------------------------------------------------------------------------------------

    fun view(entityId : EntityId,
             column : TableWidgetImageColumn,
             context : Context) : View
        = ImageCellUI(this, column, entityId, context).view()

}


/**
 * Table Widget Number Cell
 */
data class TableWidgetNumberCell(override val id : UUID,
                                 val format : NumberCellFormat,
                                 val variableValue : NumberVariableValue,
                                 val editorType : Maybe<NumericEditorType>,
                                 val action : Maybe<Action>,
                                 var variableId : VariableId?)
                                  : TableWidgetCell(), ProdType
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var viewId : Int? = null
    var column : TableWidgetNumberColumn? = null
    var namespace : VariableNamespace? = null


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(variableValue : NumberVariableValue)
        : this(UUID.randomUUID(),
               NumberCellFormat.default(),
               variableValue,
               Nothing(),
               Nothing(),
               null)


    constructor(format : NumberCellFormat,
                variableValue : NumberVariableValue,
                editorType : Maybe<NumericEditorType>,
                action : Maybe<Action>)
        : this(UUID.randomUUID(),
               format,
               variableValue,
               editorType,
               action,
               null)


    constructor(format : NumberCellFormat,
                variableValue : NumberVariableValue,
                editorType : Maybe<NumericEditorType>)
        : this(UUID.randomUUID(),
               format,
               variableValue,
               editorType,
               Nothing(),
               null)


    companion object : Factory<TableWidgetNumberCell>
    {

        private fun defaultNumberCellFormat() = NumberCellFormat.default()

        override fun fromDocument(doc: SchemaDoc): ValueParser<TableWidgetNumberCell> = when (doc)
        {
            is DocDict ->
            {
                apply(::TableWidgetNumberCell,
                      // Format
                      split(doc.maybeAt("format"),
                            effValue(defaultNumberCellFormat()),
                            { NumberCellFormat.fromDocument(it) }),
                      // Variable Value
                      doc.at("variable_value") ap {
                          NumberVariableValue.fromDocument(it)
                      },
                      // Editor Type
                      split(doc.maybeAt("editor_type"),
                            effValue<ValueError,Maybe<NumericEditorType>>(Nothing()),
                            { apply(::Just, NumericEditorType.fromDocument(it)) }),
                      // Action
                      split(doc.maybeAt("action"),
                            effValue<ValueError,Maybe<Action>>(Nothing()),
                            { apply(::Just, Action.fromDocument(it)) })
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
    .maybeMerge(this.editorType.apply {
        Just(Pair("editor_type", it.toDocument() as SchemaDoc)) })
    .maybeMerge(this.action().apply {
        Just(Pair("action", it.toDocument() as SchemaDoc)) })


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun format() : NumberCellFormat = this.format


    fun variableValue() : NumberVariableValue = this.variableValue


    fun editorType() : Maybe<NumericEditorType> = this.editorType


    fun resolveEditorType(column : TableWidgetNumberColumn) : NumericEditorType =
        when (this.editorType) {
            is Just -> this.editorType.value
            else -> column.editorType()
        }


    override fun variableIdOrError() : AppEff<VariableId> =
        note(this.variableId, AppSheetError(CellVariableUndefined(this.id)))


    fun action() : Maybe<Action> = this.action


    // -----------------------------------------------------------------------------------------
    // CELL
    // -----------------------------------------------------------------------------------------

    override fun type() : TableWidgetCellType = TableWidgetCellType.NUMBER


    override fun updateView(entityId : EntityId, context : Context)
    {
        this.viewId?.let {
            val activity = context as AppCompatActivity
            val valueTextView = activity.findViewById<TextView>(it)
            val maybeValue = this.value(entityId)
            when (maybeValue)
            {
                is Val -> {
                    val numberFormat = this.column?.format()?.let {
                                           this.format().resolveTextFormat(it).numberFormat()
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


    override fun variableId() = this.variableId


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_WidgetTableCellNumberValue =
        RowValue4(widgetTableCellNumberTable,
                  ProdValue(this.format),
                  SumValue(this.variableValue),
                  MaybePrimValue(this.editorType),
                  MaybeProdValue(this.action))



    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    fun valueVariable(entityId : EntityId) : AppEff<NumberVariable> =
        this.variableIdOrError().apply { numberVariable(it, entityId) }


    fun valueString(entityId : EntityId) : AppEff<String> =
         this.valueVariable(entityId) ap { it.valueString(entityId) }


    fun value(entityId : EntityId) : AppEff<Double> =
            this.valueVariable(entityId)
                .apply { it.value(entityId) }
                .apply { effValue<AppError,Double>(maybeValue(0.0, it)) }


    fun updateValue(newValue : Double, entityId : EntityId) =
        this.valueVariable(entityId) apDo {
            it.updateValue(newValue, entityId) }


    // -----------------------------------------------------------------------------------------
    // VIEW
    // -----------------------------------------------------------------------------------------

    fun view(row : TableWidgetRow,
             column : TableWidgetNumberColumn,
             rowIndex : Int,
             tableWidget : TableWidget,
             entityId : EntityId,
             context : Context) : View
    {
        val viewBuilder = NumberCellViewBuilder(this,
                                                row,
                                                column,
                                                rowIndex,
                                                tableWidget,
                                                entityId,
                                                context)
        this.column = column
        return viewBuilder.view()
    }


}


/**
 * Table Widget Text Cell
 */
data class TableWidgetTextCell(override val id : UUID,
                               val format : TextCellFormat,
                               val variableValue : TextVariableValue,
                               val action : Maybe<Action>,
                               var variableId : VariableId?)
                                : TableWidgetCell(), ProdType
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var viewId : Int? = null

    var namespace : VariableNamespace? = null


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(variableValue : TextVariableValue)
        : this(UUID.randomUUID(),
               TextCellFormat.default(),
               variableValue,
               Nothing(),
               null)

    constructor(format : TextCellFormat,
                variableValue : TextVariableValue,
                action : Maybe<Action>)
        : this(UUID.randomUUID(),
               format,
               variableValue,
               action,
               null)


    companion object : Factory<TableWidgetTextCell>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<TableWidgetTextCell> = when (doc)
        {
            is DocDict ->
            {
                apply(::TableWidgetTextCell,
                      // Format
                      split(doc.maybeAt("format"),
                            effValue(TextCellFormat.default()),
                             { TextCellFormat.fromDocument(it) }),
                      // Variable Value
                      doc.at("variable_value") ap {
                          TextVariableValue.fromDocument(it)
                      },
                      // Action
                      split(doc.maybeAt("action"),
                            effValue<ValueError,Maybe<Action>>(Nothing()),
                            { apply(::Just, Action.fromDocument(it)) } )
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

    fun format() : TextCellFormat = this.format


    fun variableValue() : TextVariableValue = this.variableValue


    override fun variableIdOrError() : AppEff<VariableId> =
            note(this.variableId, AppSheetError(CellVariableUndefined(this.id)))


    fun valueVariable(entityId : EntityId) : AppEff<TextVariable> =
        this.variableIdOrError().apply { textVariable(it, entityId) }


    fun action() : Maybe<Action> = this.action


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


    override fun updateView(entityId : EntityId, context : Context) {
    }


    override fun variableId() = this.variableId


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_WidgetTableCellTextValue =
        RowValue3(widgetTableCellTextTable,
                  ProdValue(this.format),
                  SumValue(this.variableValue),
                  MaybeProdValue(this.action))


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    fun variable(entityId : EntityId) : AppEff<TextVariable> =
        this.variableIdOrError() apply { textVariable(it, entityId) }


    fun valueString(entityId : EntityId) : AppEff<String> =
            this.valueVariable(entityId) ap { it.valueString(entityId) }
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
             entityId : EntityId,
             context : Context) : View
    {
        val viewBuilder = TextCellUI(this,
                                     column,
                                     tableWidget,
                                     entityId,
                                     context)
        return viewBuilder.view()
    }

}


enum class TableWidgetCellType
{
    BOOLEAN,
    IMAGE,
    NUMBER,
    TEXT
}


/**
 * Table Widget Cell Format
 */
//data class CellFormat(override val id : UUID,
//                      val textStyle : Prod<TextFormat>,
//                      val alignment : Prim<Alignment>,
//                      val backgroundColorTheme : Prim<ColorTheme>)
//                       : ToDocument, ProdType, Serializable
//{
//
//    // -----------------------------------------------------------------------------------------
//    // INIT
//    // -----------------------------------------------------------------------------------------
//
//    init
//    {
//        this.textStyle.name             = "text_style"
//        this.alignment.name             = "alignment"
//        this.backgroundColorTheme.name  = "background_color_theme"
//    }
//
//
//    // -----------------------------------------------------------------------------------------
//    // CONSTRUCTORS
//    // -----------------------------------------------------------------------------------------
//
//    constructor(textStyle: TextFormat,
//                alignment : Alignment,
//                backgroundColorTheme : ColorTheme)
//        : this(UUID.randomUUID(),
//               Prod(textStyle),
//               Prim(alignment),
//               Prim(backgroundColorTheme))
//
//
//    companion object : Factory<CellFormat>
//    {
//
//        private fun defaultTextStyle()            = TextFormat.default()
//        private fun defaultAlignment()            = Alignment.Center
//        private fun defaultBackgroundColorTheme() = ColorTheme.transparent
//
//
//        override fun fromDocument(doc: SchemaDoc): ValueParser<CellFormat> = when (doc)
//        {
//            is DocDict ->
//            {
//                apply(::CellFormat,
//                      // ProdType Id
//                      effValue(UUID.randomUUID()),
//                      // Text Style
//                      split(doc.maybeAt("text_style"),
//                            effValue(Prod.default(defaultTextStyle())),
//                            { effApply(::Prod, TextFormat.fromDocument(it)) }),
//                      // Alignment
//                      split(doc.maybeAt("alignment"),
//                            effValue<ValueError,Prim<Alignment>>(Prim.default(defaultAlignment())),
//                            { effApply(::Prim, Alignment.fromDocument(it)) }),
//                      // Background Color
//                      split(doc.maybeAt("background_color_theme"),
//                            effValue(Prim(defaultBackgroundColorTheme())),
//                            { effApply(::Prim, ColorTheme.fromDocument(it)) })
//                    )
//            }
//            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
//        }
//
//
//        fun default() = CellFormat(UUID.randomUUID(),
//                                   Prod.default(defaultTextStyle()),
//                                   Prim.default(defaultAlignment()),
//                                   Prim.default(defaultBackgroundColorTheme()))
//
//    }
//
//
//    // -----------------------------------------------------------------------------------------
//    // TO DOCUMENT
//    // -----------------------------------------------------------------------------------------
//
//    override fun toDocument() = DocDict(mapOf(
//        "text_style" to this.textStyle().toDocument(),
//        "alignment" to this.alignment().toDocument(),
//        "background_color_theme" to this.backgroundColorTheme().toDocument()
//    ))
//
//
//    // -----------------------------------------------------------------------------------------
//    // GETTERS
//    // -----------------------------------------------------------------------------------------
//
//    fun textStyle() : TextFormat = this.textStyle.value
//
//    fun alignment() : Alignment = this.alignment.value
//
//    fun backgroundColorTheme() : ColorTheme = this.backgroundColorTheme.value
//
//
//    // -----------------------------------------------------------------------------------------
//    // MODEL
//    // -----------------------------------------------------------------------------------------
//
//    override fun onLoad() { }
//
//    override val name : String = "table_widget_cell_format"
//
//    override val prodTypeObject = this
//
//    override fun persistentFunctors() : List<com.kispoko.tome.lib.functor.Val<*>> =
//            listOf(this.textStyle,
//                   this.alignment,
//                   this.backgroundColorTheme)
//
//}


object TableWidgetCellView
{

    fun layout(columnFormat : ColumnFormat,
               entityId : EntityId,
               context : Context) : LinearLayout
    {
        val layout                  = LinearLayoutBuilder()

        layout.layoutType           = LayoutType.TABLE_ROW
        layout.orientation          = LinearLayout.HORIZONTAL
        layout.width                = 0
        layout.height               = TableRow.LayoutParams.WRAP_CONTENT
        layout.weight               = columnFormat.widthFloat()

        // > Gravity
        // TODO
        //if (cellFormat.alignment().isDefault()) {
        layout.gravity          = columnFormat.textFormat().elementFormat().alignment().gravityConstant() or
                                    Gravity.CENTER_VERTICAL
//        } else {
//            layout.gravity          = cellFormat.alignment().gravityConstant() or
//                                        Gravity.CENTER_VERTICAL
////            layout.layoutGravity          = cellFormat.alignment().gravityConstant() or
////                                                Gravity.CENTER_VERTICAL
//        }

        //if (cellFormat.backgroundColorTheme.isDefault()) {
        layout.backgroundColor  = colorOrBlack(columnFormat.textFormat().elementFormat().backgroundColorTheme(),
                                        entityId)
//        } else {
//            layout.backgroundColor  = SheetManager.color(sheetUIContext.sheetId,
//                                        cellFormat.backgroundColorTheme())
//        }

        // layout.backgroundResource   = tableRowFormat.cellHeight().resourceId(Corners.None)

        return layout.linearLayout(context)
    }


}
