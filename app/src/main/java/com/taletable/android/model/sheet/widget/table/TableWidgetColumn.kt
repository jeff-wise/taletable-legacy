
package com.taletable.android.model.sheet.widget.table


import com.taletable.android.app.ApplicationLog
import com.taletable.android.lib.Factory
import com.taletable.android.lib.orm.sql.*
import com.taletable.android.model.engine.variable.*
import com.taletable.android.model.sheet.style.IconType
import com.taletable.android.model.sheet.style.NumericEditorType
import com.taletable.android.model.sheet.style.TextFormat
import com.taletable.android.model.sheet.widget.Action
import com.taletable.android.model.sheet.widget.table.cell.TextCellValue
import com.taletable.android.model.sheet.widget.table.column.*
import com.taletable.android.rts.entity.EntityId
import com.taletable.android.util.Util
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
                               open val variableRelation : Maybe<VariableRelation>,
                               open val isColumnNamespaced : IsColumnNamespaced)
                                : ToDocument, Serializable
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
                    "table_widget_boolean_column" -> TableWidgetBooleanColumn.fromDocument(doc.nextCase())
                                                        as ValueParser<TableWidgetColumn>
                    "table_widget_image_column"   -> TableWidgetImageColumn.fromDocument(doc.nextCase())
                                                        as ValueParser<TableWidgetColumn>
                    "table_widget_number_column"  -> TableWidgetNumberColumn.fromDocument(doc.nextCase())
                                                        as ValueParser<TableWidgetColumn>
                    "table_widget_text_column"    -> TableWidgetTextColumn.fromDocument(doc.nextCase())
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


    fun variableRelation() : Maybe<VariableRelation> = this.variableRelation


    fun isColumnNamespacedBoolean() : Boolean = this.isColumnNamespaced.value


    // -----------------------------------------------------------------------------------------
    // COLUMN
    // -----------------------------------------------------------------------------------------

    abstract fun type() : TableWidgetColumnType


    abstract fun columnFormat() : ColumnFormat


    abstract fun defaultValueString(entityId : EntityId) : String


    // -----------------------------------------------------------------------------------------
    // COLUMN OF TYPE
    // -----------------------------------------------------------------------------------------

    fun textColumn() : Maybe<TableWidgetTextColumn> = when (this)
    {
        is TableWidgetTextColumn -> Just(this)
        else                     -> Nothing()
    }

}


/**
 * Table Widget Boolean Column
 */
data class TableWidgetBooleanColumn(
        val id : UUID,
        override val columnName : ColumnName,
        override val variablePrefix : ColumnVariablePrefix,
        override val variableRelation : Maybe<VariableRelation>,
        override val isColumnNamespaced:  IsColumnNamespaced,
        val defaultValue : BooleanVariableValue,
        val format : BooleanColumnFormat)
          : TableWidgetColumn(columnName, variablePrefix, variableRelation, isColumnNamespaced)
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(columnName : ColumnName,
                variablePrefix : ColumnVariablePrefix,
                variableRelation : Maybe<VariableRelation>,
                isColumnNamespaced : IsColumnNamespaced,
                defaultValue : BooleanVariableValue,
                format : BooleanColumnFormat)
        : this(UUID.randomUUID(),
               columnName,
               variablePrefix,
               variableRelation,
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
                      // Variable Relation
                      split(doc.maybeAt("relation"),
                            effValue<ValueError,Maybe<VariableRelation>>(Nothing()),
                            { apply(::Just, VariableRelation.fromDocument(it)) }),
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


    override fun defaultValueString(entityId : EntityId) =
            this.defaultValue().toString()


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

//    override fun onLoad() {}
//
//
//    override val prodTypeObject = this
//
//
//    override fun rowValue() : DB_WidgetTableColumnBooleanValue =
//        RowValue5(widgetTableColumnBooleanTable,
//                  PrimValue(this.columnName),
//                  PrimValue(this.variablePrefix),
//                  PrimValue(this.isColumnNamespaced),
//                  SumValue(this.defaultValue),
//                  ProdValue(this.format))

}


/**
 * Table Widget Image Column
 */
data class TableWidgetImageColumn(
        val id : UUID,
        override val columnName : ColumnName,
        override val variablePrefix : ColumnVariablePrefix,
        override val variableRelation : Maybe<VariableRelation>,
        override val isColumnNamespaced:  IsColumnNamespaced,
        val defaultIconType : IconType,
        val format : ImageColumnFormat)
          : TableWidgetColumn(columnName, variablePrefix, variableRelation, isColumnNamespaced)
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(columnName : ColumnName,
                variablePrefix : ColumnVariablePrefix,
                variableRelation : Maybe<VariableRelation>,
                isColumnNamespaced : IsColumnNamespaced,
                defaultIconType : IconType,
                format : ImageColumnFormat)
        : this(UUID.randomUUID(),
               columnName,
               variablePrefix,
               variableRelation,
               isColumnNamespaced,
               defaultIconType,
               format)


    companion object : Factory<TableWidgetImageColumn>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<TableWidgetImageColumn> = when (doc)
        {
            is DocDict ->
            {
                apply(::TableWidgetImageColumn,
                      // Name
                      doc.at("name") ap { ColumnName.fromDocument(it) },
                      // Variable Prefix
                      effValue(ColumnVariablePrefix("")),
                      // Variable Relation
                      split(doc.maybeAt("relation"),
                            effValue<ValueError,Maybe<VariableRelation>>(Nothing()),
                            { apply(::Just, VariableRelation.fromDocument(it)) }),
                      // Is Column Namespaced
                      split(doc.maybeAt("is_namespaced"),
                            effValue(IsColumnNamespaced(false)),
                            { IsColumnNamespaced.fromDocument(it) }),
                      // Default Icon Type
                      doc.at("default_icon_type") ap { IconType.fromDocument(it) },
                      // Format
                      split(doc.maybeAt("format"),
                            effValue(ImageColumnFormat.default()),
                            { ImageColumnFormat.fromDocument(it) })
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
        "default_icon_type" to this.defaultIconType().toDocument(),
        "format" to this.format().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun defaultIconType() : IconType = this.defaultIconType


    fun format() : ImageColumnFormat = this.format


    // -----------------------------------------------------------------------------------------
    // COLUMN
    // -----------------------------------------------------------------------------------------

    override fun type() : TableWidgetColumnType = TableWidgetColumnType.BOOLEAN


    override fun columnFormat(): ColumnFormat = this.format().columnFormat()


    override fun defaultValueString(entityId : EntityId) = ""


    // -----------------------------------------------------------------------------------------
    // MODEL
//    // -----------------------------------------------------------------------------------------
//
//    override fun onLoad() {}
//
//
//    override val prodTypeObject = this
//
//
//    override fun rowValue() : DB_WidgetTableColumnBooleanValue =
//        RowValue5(widgetTableColumnBooleanTable,
//                  PrimValue(this.columnName),
//                  PrimValue(this.variablePrefix),
//                  PrimValue(this.isColumnNamespaced),
//                  SumValue(this.defaultValue),
//                  ProdValue(this.format))

}


/**
 * Table Widget Number Column
 */
data class TableWidgetNumberColumn(
                val id : UUID,
                override val columnName : ColumnName,
                override val variablePrefix : ColumnVariablePrefix,
                override val variableRelation : Maybe<VariableRelation>,
                override val isColumnNamespaced : IsColumnNamespaced,
                val tags : List<VariableTag>,
                val defaultValue : NumberVariableValue,
                val format : NumberColumnFormat,
                val action : Maybe<Action>,
                val editorType : NumericEditorType)
                 : TableWidgetColumn(columnName, variablePrefix, variableRelation, isColumnNamespaced)
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(columnName : ColumnName,
                variablePrefix : ColumnVariablePrefix,
                variableRelation : Maybe<VariableRelation>,
                isColumnNamespaced : IsColumnNamespaced,
                tags : List<VariableTag>,
                defaultValue : NumberVariableValue,
                format : NumberColumnFormat,
                action : Maybe<Action>,
                editorType : NumericEditorType)
        : this(UUID.randomUUID(),
               columnName,
               variablePrefix,
               variableRelation,
               isColumnNamespaced,
               tags,
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
                      // Variable Relation
                      split(doc.maybeAt("relation"),
                            effValue<ValueError,Maybe<VariableRelation>>(Nothing()),
                            { apply(::Just, VariableRelation.fromDocument(it)) }),
                      // Is Column Namespaced
                      split(doc.maybeAt("is_namespaced"),
                            effValue(IsColumnNamespaced(false)),
                            { IsColumnNamespaced.fromDocument(it) }),
                      // Tags
                      split(doc.maybeList("tags"),
                            effValue(listOf()),
                            { it.map { VariableTag.fromDocument(it) } }),
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


    override fun defaultValueString(entityId : EntityId) : String
    {
        val maybeValue = this.defaultValue().value(entityId)
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


    fun tags() : List<VariableTag> = this.tags


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------
//
//    override fun onLoad() {}
//
//
//    override val prodTypeObject = this
//
//
//    override fun rowValue() : DB_WidgetTableColumnNumberValue =
//        RowValue7(widgetTableColumnNumberTable,
//                  PrimValue(this.columnName),
//                  PrimValue(this.variablePrefix),
//                  PrimValue(this.isColumnNamespaced),
//                  SumValue(this.defaultValue),
//                  ProdValue(this.format),
//                  MaybeProdValue(this.action),
//                  PrimValue(this.editorType))

}


/**
 * Table Widget Text Column
 */
data class TableWidgetTextColumn(
        val id : UUID,
        override val columnName : ColumnName,
        override val variablePrefix : ColumnVariablePrefix,
        override val variableRelation : Maybe<VariableRelation>,
        override val isColumnNamespaced : IsColumnNamespaced,
        val tags : List<VariableTag>,
        val defaultValue : TextCellValue,
        val format : TextColumnFormat,
        val action : Maybe<Action>,
        val columnVariableId : Maybe<VariableId>,
        val definesNamespace : DefinesNamespace)
         : TableWidgetColumn(columnName, variablePrefix, variableRelation, isColumnNamespaced)
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    // TODO remove namespace columns, use relations
    constructor(columnName : ColumnName,
                variablePrefix : ColumnVariablePrefix,
                variableRelation : Maybe<VariableRelation>,
                isColumnNamespaced : IsColumnNamespaced,
                tags : List<VariableTag>,
                defaultValue : TextCellValue,
                format : TextColumnFormat,
                action : Maybe<Action>,
                columnVariableId : Maybe<VariableId>,
                definesNamespace: DefinesNamespace)
        : this(UUID.randomUUID(),
               columnName,
               variablePrefix,
               variableRelation,
               isColumnNamespaced,
               tags,
               defaultValue,
               format,
               action,
               columnVariableId,
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
                      // Variable Relation
                      split(doc.maybeAt("relation"),
                            effValue<ValueError,Maybe<VariableRelation>>(Nothing()),
                            { apply(::Just, VariableRelation.fromDocument(it)) }),
                      // Is Column Namespaced
                      split(doc.maybeAt("is_namespaced"),
                            effValue(IsColumnNamespaced(false)),
                            { IsColumnNamespaced.fromDocument(it) }),
                      // Tags
                      split(doc.maybeList("tags"),
                            effValue(listOf()),
                            { it.map { VariableTag.fromDocument(it) } }),
                      // Default Value
                      doc.at("default_value") ap { TextCellValue.fromDocument(it) },
                      // Format
                      split(doc.maybeAt("format"),
                            effValue(TextColumnFormat.default()),
                            { TextColumnFormat.fromDocument(it) }),
                      // Action
                      split(doc.maybeAt("action"),
                            effValue<ValueError,Maybe<Action>>(Nothing()),
                            { apply(::Just, Action.fromDocument(it)) }),
                      // Column Variable Id
                      split(doc.maybeAt("column_variable_id"),
                            effValue<ValueError,Maybe<VariableId>>(Nothing()),
                            { apply(::Just, VariableId.fromDocument(it)) }),
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


    fun defaultValue() : TextCellValue = this.defaultValue


    fun columnVariableId() : Maybe<VariableId> = this.columnVariableId


    fun action() : Maybe<Action> = this.action


    // -----------------------------------------------------------------------------------------
    // COLUMN
    // -----------------------------------------------------------------------------------------

    override fun type() : TableWidgetColumnType = TableWidgetColumnType.TEXT


    override fun columnFormat(): ColumnFormat = this.format().columnFormat()


    override fun defaultValueString(entityId : EntityId) : String
    {
        val maybeValue = this.defaultValue().value(entityId)
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


    fun tags() : List<VariableTag> = this.tags

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
 * Primary Column Index
 */
data class PrimaryColumnIndex(val value : Int) : ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<PrimaryColumnIndex>
    {
        override fun fromDocument(doc : SchemaDoc): ValueParser<PrimaryColumnIndex> = when (doc)
        {
            is DocNumber -> effValue(PrimaryColumnIndex(doc.number.toInt()))
            else          -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
        }
    }

    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocNumber(this.value.toDouble())

}



/**
 * Table Widget Column Format
 */
data class ColumnFormat(val textFormat : TextFormat,
                        val width : ColumnWidth)
                         : ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

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

