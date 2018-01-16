
package com.kispoko.tome.model.sheet.widget.table


import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.db.*
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.*
import com.kispoko.tome.lib.orm.schema.MaybeProdValue
import com.kispoko.tome.lib.orm.schema.PrimValue
import com.kispoko.tome.lib.orm.schema.ProdValue
import com.kispoko.tome.lib.orm.schema.SumValue
import com.kispoko.tome.lib.orm.sql.*
import com.kispoko.tome.model.game.engine.variable.BooleanVariableValue
import com.kispoko.tome.model.game.engine.variable.NumberVariableValue
import com.kispoko.tome.model.game.engine.variable.TextVariableValue
import com.kispoko.tome.model.sheet.style.ElementFormat
import com.kispoko.tome.model.sheet.style.NumericEditorType
import com.kispoko.tome.model.sheet.style.TextFormat
import com.kispoko.tome.model.sheet.widget.Action
import com.kispoko.tome.model.sheet.widget.table.column.BooleanColumnFormat
import com.kispoko.tome.model.sheet.widget.table.column.NumberColumnFormat
import com.kispoko.tome.model.sheet.widget.table.column.TextColumnFormat
import com.kispoko.tome.rts.sheet.SheetContext
import com.kispoko.tome.util.Util
import effect.*
import lulo.document.*
import lulo.value.*
import lulo.value.UnexpectedType
import maybe.Just
import maybe.Maybe
import maybe.Nothing
import java.io.Serializable
import java.util.*



/**
 * Table Widget Column
 */
@Suppress("UNCHECKED_CAST")
sealed class TableWidgetColumn(open val columnName : ColumnName,
                               open val variablePrefix : ColumnVariablePrefix,
                               open val isColumnNamespaced : IsColumnNamespaced)
                                : ToDocument, ProdType, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<TableWidgetColumn>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<TableWidgetColumn> = when (doc)
        {
            is DocDict ->
            {
                when (doc.case())
                {
                    "table_widget_boolean_column" -> TableWidgetBooleanColumn.fromDocument(doc)
                                                        as ValueParser<TableWidgetColumn>
                    "table_widget_number_column"  -> TableWidgetNumberColumn.fromDocument(doc)
                                                        as ValueParser<TableWidgetColumn>
                    "table_widget_text_column"    -> TableWidgetTextColumn.fromDocument(doc)
                                                        as ValueParser<TableWidgetColumn>
                    else                          -> effError<ValueError, TableWidgetColumn>(
                                                        UnknownCase(doc.case(), doc.path))
                }
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun columnName() : ColumnName = this.columnName


    fun nameString() : String = this.columnName.value


    fun variablePrefix() : ColumnVariablePrefix = this.variablePrefix


    fun variablePrefixString() : String = this.variablePrefix.value


    fun isColumnNamespacedBoolean() : Boolean = this.isColumnNamespaced.value


    // -----------------------------------------------------------------------------------------
    // COLUMN
    // -----------------------------------------------------------------------------------------

    abstract fun type() : TableWidgetColumnType

    abstract fun columnFormat() : ColumnFormat

    abstract fun defaultValueString(sheetContext : SheetContext) : String

}


/**
 * Table Widget Boolean Column
 */
data class TableWidgetBooleanColumn(
        override val id : UUID,
        override val columnName : ColumnName,
        override val variablePrefix : ColumnVariablePrefix,
        override val isColumnNamespaced:  IsColumnNamespaced,
        val defaultValue : BooleanVariableValue,
        val format : BooleanColumnFormat)
          : TableWidgetColumn(columnName, variablePrefix, isColumnNamespaced)
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(columnName : ColumnName,
                variablePrefix : ColumnVariablePrefix,
                isColumnNamespaced : IsColumnNamespaced,
                defaultValue : BooleanVariableValue,
                format : BooleanColumnFormat)
        : this(UUID.randomUUID(),
               columnName,
               variablePrefix,
               isColumnNamespaced,
               defaultValue,
               format)


    companion object : Factory<TableWidgetBooleanColumn>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<TableWidgetBooleanColumn> = when (doc)
        {
            is DocDict ->
            {
                apply(::TableWidgetBooleanColumn,
                      // Name
                      doc.at("name") ap { ColumnName.fromDocument(it) },
                      // Variable Prefix
                      doc.at("variable_prefix") ap { ColumnVariablePrefix.fromDocument(it) },
                      // Is Column Namespaced
                      split(doc.maybeAt("is_namespaced"),
                            effValue(IsColumnNamespaced(false)),
                            { IsColumnNamespaced.fromDocument(it) }),
                      // Default Variable Value
                      doc.at("default_value") ap { BooleanVariableValue.fromDocument(it) },
                      // Format
                      split(doc.maybeAt("format"),
                            effValue(BooleanColumnFormat.default()),
                            { BooleanColumnFormat.fromDocument(it) })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "name" to this.columnName().toDocument(),
        "variable_prefix" to this.variablePrefix().toDocument(),
        "is_namespaced" to this.isColumnNamespaced.toDocument(),
        "default_value" to this.defaultValue().toDocument(),
        "format" to this.format().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun defaultValue() : BooleanVariableValue = this.defaultValue


    fun format() : BooleanColumnFormat = this.format


    // -----------------------------------------------------------------------------------------
    // COLUMN
    // -----------------------------------------------------------------------------------------

    override fun type() : TableWidgetColumnType = TableWidgetColumnType.BOOLEAN


    override fun columnFormat(): ColumnFormat = this.format().columnFormat()


    override fun defaultValueString(sheetContext : SheetContext) =
            this.defaultValue().toString()


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() {}


    override val prodTypeObject = this


    override fun rowValue() : DB_WidgetTableColumnBooleanValue =
        RowValue5(widgetTableColumnBooleanTable,
                  PrimValue(this.columnName),
                  PrimValue(this.variablePrefix),
                  PrimValue(this.isColumnNamespaced),
                  SumValue(this.defaultValue),
                  ProdValue(this.format))

}


/**
 * Table Widget Number Column
 */
data class TableWidgetNumberColumn(
                override val id : UUID,
                override val columnName : ColumnName,
                override val variablePrefix : ColumnVariablePrefix,
                override val isColumnNamespaced : IsColumnNamespaced,
                val defaultValue : NumberVariableValue,
                val format : NumberColumnFormat,
                val action : Maybe<Action>,
                val editorType : NumericEditorType)
                 : TableWidgetColumn(columnName, variablePrefix, isColumnNamespaced)
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(columnName : ColumnName,
                variablePrefix : ColumnVariablePrefix,
                isColumnNamespaced : IsColumnNamespaced,
                defaultValue : NumberVariableValue,
                format : NumberColumnFormat,
                action : Maybe<Action>,
                editorType : NumericEditorType)
        : this(UUID.randomUUID(),
               columnName,
               variablePrefix,
               isColumnNamespaced,
               defaultValue,
               format,
               action,
               editorType)


    companion object : Factory<TableWidgetNumberColumn>
    {

        private val defaultEditorType = NumericEditorType.Adder


        override fun fromDocument(doc: SchemaDoc): ValueParser<TableWidgetNumberColumn> = when (doc)
        {
            is DocDict ->
            {
                apply(::TableWidgetNumberColumn,
                      // Name
                      doc.at("name") ap { ColumnName.fromDocument(it) },
                      // Variable Prefix
                      doc.at("variable_prefix") ap { ColumnVariablePrefix.fromDocument(it) },
                      // Is Column Namespaced
                      split(doc.maybeAt("is_namespaced"),
                            effValue(IsColumnNamespaced(false)),
                            { IsColumnNamespaced.fromDocument(it) }),
                      // Default Value
                      doc.at("default_value") ap { NumberVariableValue.fromDocument(it) },
                      // Format
                      split(doc.maybeAt("format"),
                            effValue(NumberColumnFormat.default()),
                            { NumberColumnFormat.fromDocument(it) }),
                      // Action
                      split(doc.maybeAt("action"),
                            effValue<ValueError,Maybe<Action>>(Nothing()),
                            { apply(::Just, Action.fromDocument(it)) }),
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
        "name" to this.columnName().toDocument(),
        "variable_prefix" to this.variablePrefix().toDocument(),
        "is_namespaced" to this.isColumnNamespaced.toDocument(),
        "default_value" to this.defaultValue().toDocument(),
        "format" to this.format().toDocument(),
        "editor_type" to this.editorType().toDocument()
    ))
    .maybeMerge(this.action().apply {
        Just(Pair("action", it.toDocument() as SchemaDoc)) })


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun defaultValue() : NumberVariableValue = this.defaultValue

    fun format() : NumberColumnFormat = this.format

    fun editorType() : NumericEditorType = this.editorType

    fun action() : Maybe<Action> = this.action


    // -----------------------------------------------------------------------------------------
    // COLUMN
    // -----------------------------------------------------------------------------------------

    override fun type() : TableWidgetColumnType = TableWidgetColumnType.NUMBER


    override fun columnFormat(): ColumnFormat = this.format().columnFormat()


    override fun defaultValueString(sheetContext : SheetContext) : String
    {
        val maybeValue = this.defaultValue().value(sheetContext)
        when (maybeValue)
        {
            is effect.Val ->
            {
                val value = maybeValue.value
                when (value)
                {
                    is Just    -> return Util.doubleString(value.value)
                }

            }
            is Err -> ApplicationLog.error(maybeValue.error)
        }

        return ""
    }


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() {}


    override val prodTypeObject = this


    override fun rowValue() : DB_WidgetTableColumnNumberValue =
        RowValue7(widgetTableColumnNumberTable,
                  PrimValue(this.columnName),
                  PrimValue(this.variablePrefix),
                  PrimValue(this.isColumnNamespaced),
                  SumValue(this.defaultValue),
                  ProdValue(this.format),
                  MaybeProdValue(this.action),
                  PrimValue(this.editorType))

}


/**
 * Table Widget Text Column
 */
data class TableWidgetTextColumn(
        override val id : UUID,
        override val columnName : ColumnName,
        override val variablePrefix : ColumnVariablePrefix,
        override val isColumnNamespaced : IsColumnNamespaced,
        val defaultValue : TextVariableValue,
        val format : TextColumnFormat,
        val action : Maybe<Action>,
        val definesNamespace : DefinesNamespace)
         : TableWidgetColumn(columnName, variablePrefix, isColumnNamespaced)
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(columnName : ColumnName,
                variablePrefix : ColumnVariablePrefix,
                isColumnNamespaced : IsColumnNamespaced,
                defaultValue : TextVariableValue,
                format : TextColumnFormat,
                action : Maybe<Action>,
                definesNamespace: DefinesNamespace)
        : this(UUID.randomUUID(),
               columnName,
               variablePrefix,
               isColumnNamespaced,
               defaultValue,
               format,
               action,
               definesNamespace)


    companion object : Factory<TableWidgetTextColumn>
    {

        override fun fromDocument(doc: SchemaDoc): ValueParser<TableWidgetTextColumn> = when (doc)
        {
            is DocDict ->
            {
                apply(::TableWidgetTextColumn,
                      // Name
                      doc.at("name") ap { ColumnName.fromDocument(it) },
                      // Variable Prefix
                      doc.at("variable_prefix") ap { ColumnVariablePrefix.fromDocument(it) },
                      // Is Column Namespaced
                      split(doc.maybeAt("is_namespaced"),
                            effValue(IsColumnNamespaced(false)),
                            { IsColumnNamespaced.fromDocument(it) }),
                      // Default Value
                      doc.at("default_value") ap { TextVariableValue.fromDocument(it) },
                      // Format
                      split(doc.maybeAt("format"),
                            effValue(TextColumnFormat.default()),
                            { TextColumnFormat.fromDocument(it) }),
                      // Action
                      split(doc.maybeAt("action"),
                            effValue<ValueError,Maybe<Action>>(Nothing()),
                            { apply(::Just, Action.fromDocument(it)) }),
                      // Defines Namespace?
                      split(doc.maybeAt("defines_namespace"),
                            effValue(DefinesNamespace(false)),
                            { DefinesNamespace.fromDocument(it) })
                     )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "name" to this.columnName().toDocument(),
        "variable_prefix" to this.variablePrefix().toDocument(),
        "is_namespaced" to this.isColumnNamespaced.toDocument(),
        "default_value" to this.defaultValue().toDocument(),
        "format" to this.format().toDocument(),
        "defines_namespace" to this.definesNamespace().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun format() : TextColumnFormat = this.format


    fun definesNamespace() : DefinesNamespace = this.definesNamespace


    fun definesNamespaceBoolean() : Boolean = this.definesNamespace.value


    fun defaultValue() : TextVariableValue = this.defaultValue


    fun action() : Maybe<Action> = this.action


    // -----------------------------------------------------------------------------------------
    // COLUMN
    // -----------------------------------------------------------------------------------------

    override fun type() : TableWidgetColumnType = TableWidgetColumnType.TEXT


    override fun columnFormat(): ColumnFormat = this.format().columnFormat()


    override fun defaultValueString(sheetContext : SheetContext) : String
    {
        val maybeValue = this.defaultValue().value(sheetContext)
        when (maybeValue)
        {
            is effect.Val -> {
                val value = maybeValue.value
                when (value) {
                    is Just -> return value.value
                }
            }
            is Err -> ApplicationLog.error(maybeValue.error)
        }

        return ""
    }


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() {}


    override val prodTypeObject = this


    override fun rowValue() : DB_WidgetTableColumnTextValue =
        RowValue7(widgetTableColumnTextTable,
                  PrimValue(this.columnName),
                  PrimValue(this.variablePrefix),
                  PrimValue(this.isColumnNamespaced),
                  SumValue(this.defaultValue),
                  ProdValue(this.format),
                  MaybeProdValue(this.action),
                  PrimValue(this.definesNamespace))

}


enum class TableWidgetColumnType
{
    BOOLEAN,
    NUMBER,
    TEXT
}


/**
 * Table Widget Column Name
 */
data class ColumnName(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ColumnName>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<ColumnName> = when (doc)
        {
            is DocText -> effValue(ColumnName(doc.text))
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

    override fun asSQLValue(): SQLValue = SQLText({this.value})

}


/**
 * Column Variable Prefix
 */
data class ColumnVariablePrefix(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ColumnVariablePrefix>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<ColumnVariablePrefix> = when (doc)
        {
            is DocText -> effValue(ColumnVariablePrefix(doc.text))
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

    override fun asSQLValue(): SQLValue = SQLText({this.value})

}


/**
 * Default Value Label
 */
//data class DefaultValueLabel(val value : String) : ToDocument, SQLSerializable, Serializable
//{
//
//    // -----------------------------------------------------------------------------------------
//    // CONSTRUCTORS
//    // -----------------------------------------------------------------------------------------
//
//    companion object : Factory<DefaultValueLabel>
//    {
//        override fun fromDocument(doc: SchemaDoc): ValueParser<DefaultValueLabel> = when (doc)
//        {
//            is DocText -> effValue(DefaultValueLabel(doc.text))
//            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
//        }
//    }
//
//
//    // -----------------------------------------------------------------------------------------
//    // SQL SERIALIZABLE
//    // -----------------------------------------------------------------------------------------
//
//    override fun asSQLValue(): SQLValue = SQLText({this.value})
//
//}


/**
 * Is Column Namespaced?
 */
data class IsColumnNamespaced(val value : Boolean) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<IsColumnNamespaced>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<IsColumnNamespaced> = when (doc)
        {
            is DocBoolean -> effValue(IsColumnNamespaced(doc.boolean))
            else          -> effError(UnexpectedType(DocType.BOOLEAN, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocBoolean(this.value)


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue(): SQLValue = SQLInt({if (this.value) 1 else 0})

}



/**
 * Defines Namespace?
 */
data class DefinesNamespace(val value : Boolean) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<DefinesNamespace>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<DefinesNamespace> = when (doc)
        {
            is DocBoolean -> effValue(DefinesNamespace(doc.boolean))
            else          -> effError(UnexpectedType(DocType.BOOLEAN, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocBoolean(this.value)


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue(): SQLValue = SQLInt({if (this.value) 1 else 0})

}


/**
 * Table Widget Column Format
 */
data class ColumnFormat(override val id : UUID,
                        val textFormat : TextFormat,
                        val width : ColumnWidth)
                         : ToDocument, ProdType, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(textFormat : TextFormat,
                width : ColumnWidth)
        : this(UUID.randomUUID(),
               textFormat,
               width)


    companion object : Factory<ColumnFormat>
    {

        private fun defaultTextFormat()    = TextFormat.default()
        private fun defaultWidth()         = ColumnWidth(1.0f)


        override fun fromDocument(doc : SchemaDoc) : ValueParser<ColumnFormat> = when (doc)
        {
            is DocDict ->
            {
                apply(::ColumnFormat,
                      // Text Format
                      split(doc.maybeAt("text_format"),
                            effValue(defaultTextFormat()),
                            { TextFormat.fromDocument(it) }),
                      // Width
                      split(doc.maybeAt("width"),
                            effValue(defaultWidth()),
                            { ColumnWidth.fromDocument(it) })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = ColumnFormat(defaultTextFormat(),
                                     defaultWidth())

    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "text_format" to this.textFormat.toDocument(),
        "width" to this.width().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun textFormat() : TextFormat = this.textFormat


    fun width() : ColumnWidth = this.width


    fun widthFloat() : Float = this.width.value


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


     override fun rowValue() : DB_WidgetTableColumnFormatValue =
        RowValue2(widgetTableColumnFormatTable,
                  ProdValue(this.textFormat),
                  PrimValue(this.width))

}


/**
 * Column Widget
 */
data class ColumnWidth(val value : Float) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ColumnWidth>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<ColumnWidth> = when (doc)
        {
            is DocNumber -> effValue(ColumnWidth(doc.number.toFloat()))
            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocNumber(this.value.toDouble())


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue(): SQLValue = SQLReal({this.value.toDouble()})

}



/**
 * Default Number Column Value
 */
//data class DefaultNumberColumnValue(val value : Double) : SQLSerializable, Serializable
//{
//
//    // -----------------------------------------------------------------------------------------
//    // CONSTRUCTORS
//    // -----------------------------------------------------------------------------------------
//
//    companion object : Factory<DefaultNumberColumnValue>
//    {
//        override fun fromDocument(doc: SchemaDoc): ValueParser<DefaultNumberColumnValue> = when (doc)
//        {
//            is DocNumber -> effValue(DefaultNumberColumnValue(doc.number))
//            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
//        }
//    }
//
//
//    // -----------------------------------------------------------------------------------------
//    // SQL SERIALIZABLE
//    // -----------------------------------------------------------------------------------------
//
//    override fun asSQLValue(): SQLValue = SQLReal({this.value})
//
//}


/**
 * Default Text Column Value
 */
//data class DefaultTextColumnValue(val value : String) : SQLSerializable, Serializable
//{
//
//    // -----------------------------------------------------------------------------------------
//    // CONSTRUCTORS
//    // -----------------------------------------------------------------------------------------
//
//    companion object : Factory<DefaultTextColumnValue>
//    {
//        override fun fromDocument(doc: SchemaDoc): ValueParser<DefaultTextColumnValue> = when (doc)
//        {
//            is DocText -> effValue(DefaultTextColumnValue(doc.text))
//            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
//        }
//    }
//
//
//    // -----------------------------------------------------------------------------------------
//    // SQL SERIALIZABLE
//    // -----------------------------------------------------------------------------------------
//
//    override fun asSQLValue(): SQLValue = SQLText({this.value})
//
//}

