
package com.kispoko.tome.model.sheet.widget.table


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.*
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.lib.orm.sql.*
import com.kispoko.tome.model.sheet.style.Alignment
import com.kispoko.tome.model.sheet.style.NumericEditorType
import com.kispoko.tome.model.sheet.style.TextStyle
import com.kispoko.tome.model.sheet.widget.table.column.BooleanColumnFormat
import com.kispoko.tome.model.sheet.widget.table.column.DefaultBooleanColumnValue
import com.kispoko.tome.model.sheet.widget.table.column.NumberColumnFormat
import com.kispoko.tome.model.sheet.widget.table.column.TextColumnFormat
import com.kispoko.tome.model.theme.ColorTheme
import effect.*
import lulo.document.*
import lulo.value.*
import lulo.value.UnexpectedType
import java.io.Serializable
import java.util.*



/**
 * Table Widget Column
 */
@Suppress("UNCHECKED_CAST")
sealed class TableWidgetColumn(open val columnName : Prim<ColumnName>,
                               open val defaultValueLabel : Prim<DefaultValueLabel>,
                               open val isColumnNamespaced: Prim<IsColumnNamespaced>)
                                : Model, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<TableWidgetColumn>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<TableWidgetColumn> = when (doc)
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

    fun nameString() : String = this.columnName.value.value

    fun defaultValueLabelString() : String = this.defaultValueLabel.value.value

    fun isColumnNamespaced() : Boolean = this.isColumnNamespaced.value.value


    // -----------------------------------------------------------------------------------------
    // COLUMN
    // -----------------------------------------------------------------------------------------

    abstract fun type() : TableWidgetColumnType

    abstract fun columnFormat() : ColumnFormat

}


/**
 * Table Widget Boolean Column
 */
data class TableWidgetBooleanColumn(
        override val id : UUID,
        override val columnName : Prim<ColumnName>,
        override val defaultValueLabel : Prim<DefaultValueLabel>,
        override val isColumnNamespaced: Prim<IsColumnNamespaced>,
        val defaultValue : Prim<DefaultBooleanColumnValue>,
        val format : Comp<BooleanColumnFormat>)
                    : TableWidgetColumn(columnName, defaultValueLabel, isColumnNamespaced)
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.columnName.name            = "column_name"
        this.defaultValueLabel.name     = "default_value_label"
        this.isColumnNamespaced.name    = "is_column_namespaced"
        this.defaultValue.name          = "default_value"
        this.format.name                = "format"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(columnName : ColumnName,
                defaultValueLabel : DefaultValueLabel,
                isColumnNamespaced : IsColumnNamespaced,
                defaultValue : DefaultBooleanColumnValue,
                format : BooleanColumnFormat)
        : this(UUID.randomUUID(),
               Prim(columnName),
               Prim(defaultValueLabel),
               Prim(isColumnNamespaced),
               Prim(defaultValue),
               Comp(format))


    companion object : Factory<TableWidgetBooleanColumn>
    {
        override fun fromDocument(doc : SpecDoc)
                        : ValueParser<TableWidgetBooleanColumn> = when (doc)
        {
            is DocDict ->
            {
                effApply(::TableWidgetBooleanColumn,
                         // Name
                         doc.at("name") ap { ColumnName.fromDocument(it) },
                         // Default Value Label
                         doc.at("default_value_label") ap { DefaultValueLabel.fromDocument(it) },
                         // Is Column Namespaced
                         split(doc.maybeAt("is_namespaced"),
                               effValue(IsColumnNamespaced(false)),
                               { IsColumnNamespaced.fromDocument(it) }),
                         // Default Value
                         split(doc.maybeAt("default_value"),
                               effValue(DefaultBooleanColumnValue(true)),
                               { DefaultBooleanColumnValue.fromDocument(it) }),
                         // Format
                         split(doc.maybeAt("format"),
                               effValue(BooleanColumnFormat.default),
                               { BooleanColumnFormat.fromDocument(it) })
                         )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun defaultValueBoolean() : Boolean = this.defaultValue.value.value

    fun format() : BooleanColumnFormat = this.format.value


    // -----------------------------------------------------------------------------------------
    // COLUMN
    // -----------------------------------------------------------------------------------------

    override fun type() : TableWidgetColumnType = TableWidgetColumnType.BOOLEAN

    override fun columnFormat(): ColumnFormat = this.format().columnFormat()


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() {}

    override val name : String = "table_widget_boolean_column"

    override val modelObject = this

}


/**
 * Table Widget Number Column
 */
data class TableWidgetNumberColumn(
                        override val id : UUID,
                        override val columnName : Prim<ColumnName>,
                        override val defaultValueLabel : Prim<DefaultValueLabel>,
                        override val isColumnNamespaced: Prim<IsColumnNamespaced>,
                        val defaultValue : Prim<DefaultNumberColumnValue>,
                        val format : Comp<NumberColumnFormat>,
                        val editorType : Prim<NumericEditorType>)
                        : TableWidgetColumn(columnName, defaultValueLabel, isColumnNamespaced)
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.columnName.name            = "column_name"
        this.defaultValueLabel.name     = "default_value_label"
        this.isColumnNamespaced.name    = "is_column_namespaced"
        this.defaultValue.name          = "default_value"
        this.format.name                = "format"
        this.editorType.name            = "editor_type"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(columnName : ColumnName,
                defaultValueLabel : DefaultValueLabel,
                isColumnNamespaced : IsColumnNamespaced,
                defaultValue : DefaultNumberColumnValue,
                format : NumberColumnFormat,
                editorType : NumericEditorType)
        : this(UUID.randomUUID(),
               Prim(columnName),
               Prim(defaultValueLabel),
               Prim(isColumnNamespaced),
               Prim(defaultValue),
               Comp(format),
               Prim(editorType))


    companion object : Factory<TableWidgetNumberColumn>
    {

        private val defaultEditorType = NumericEditorType.Adder


        override fun fromDocument(doc : SpecDoc)
                        : ValueParser<TableWidgetNumberColumn> = when (doc)
        {
            is DocDict ->
            {
                effApply(::TableWidgetNumberColumn,
                         // Name
                         doc.at("name") ap { ColumnName.fromDocument(it) },
                         // Default Value Label
                         doc.at("default_value_label") ap { DefaultValueLabel.fromDocument(it) },
                         // Is Column Namespaced
                         split(doc.maybeAt("is_namespaced"),
                               effValue(IsColumnNamespaced(false)),
                               { IsColumnNamespaced.fromDocument(it) }),
                         // Default Value
                         split(doc.maybeAt("default_value"),
                               effValue(DefaultNumberColumnValue(0.0)),
                               { DefaultNumberColumnValue.fromDocument(it) }),
                         // Format
                         split(doc.maybeAt("format"),
                               effValue(NumberColumnFormat.default),
                               { NumberColumnFormat.fromDocument(it) }),
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
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun defaultValueDouble() : Double = this.defaultValue.value.value

    fun format() : NumberColumnFormat = this.format.value

    fun editorType() : NumericEditorType = this.editorType.value


    // -----------------------------------------------------------------------------------------
    // COLUMN
    // -----------------------------------------------------------------------------------------

    override fun type() : TableWidgetColumnType = TableWidgetColumnType.NUMBER


    override fun columnFormat(): ColumnFormat = this.format().columnFormat()


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() {}

    override val name : String = "table_widget_number_column"

    override val modelObject = this

}


/**
 * Table Widget Text Column
 */
data class TableWidgetTextColumn(
                        override val id : UUID,
                        override val columnName : Prim<ColumnName>,
                        override val defaultValueLabel : Prim<DefaultValueLabel>,
                        override val isColumnNamespaced: Prim<IsColumnNamespaced>,
                        val defaultValue : Prim<DefaultTextColumnValue>,
                        val format : Comp<TextColumnFormat>,
                        val definesNamespace : Prim<DefinesNamespace>)
                        : TableWidgetColumn(columnName, defaultValueLabel, isColumnNamespaced)
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.columnName.name            = "column_name"
        this.defaultValueLabel.name     = "default_value_label"
        this.isColumnNamespaced.name    = "is_column_namespaced"
        this.defaultValue.name          = "default_value"
        this.format.name                = "format"
        this.definesNamespace.name      = "defines_namespace"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(columnName : ColumnName,
                defaultValueLabel : DefaultValueLabel,
                isColumnNamespaced : IsColumnNamespaced,
                defaultValue : DefaultTextColumnValue,
                format : TextColumnFormat,
                definesNamespace: DefinesNamespace)
        : this(UUID.randomUUID(),
               Prim(columnName),
               Prim(defaultValueLabel),
               Prim(isColumnNamespaced),
               Prim(defaultValue),
               Comp(format),
               Prim(definesNamespace))


    companion object : Factory<TableWidgetTextColumn>
    {

        override fun fromDocument(doc : SpecDoc)
                        : ValueParser<TableWidgetTextColumn> = when (doc)
        {
            is DocDict ->
            {
                effApply(::TableWidgetTextColumn,
                         // Name
                         doc.at("name") ap { ColumnName.fromDocument(it) },
                         // Default Value Label
                         doc.at("default_value_label") ap { DefaultValueLabel.fromDocument(it) },
                         // Is Column Namespaced
                         split(doc.maybeAt("is_namespaced"),
                               effValue(IsColumnNamespaced(false)),
                               { IsColumnNamespaced.fromDocument(it) }),
                         // Default Value
                         split(doc.maybeAt("default_value"),
                               effValue(DefaultTextColumnValue("")),
                               { DefaultTextColumnValue.fromDocument(it) }),
                         // Format
                         split(doc.maybeAt("format"),
                               effValue(TextColumnFormat.default),
                               { TextColumnFormat.fromDocument(it) }),
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
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun defaultValueString() : String = this.defaultValue.value.value

    fun format() : TextColumnFormat = this.format.value

    fun definesNamespaceBool() : Boolean = this.definesNamespace.value.value


    // -----------------------------------------------------------------------------------------
    // COLUMN
    // -----------------------------------------------------------------------------------------

    override fun type() : TableWidgetColumnType = TableWidgetColumnType.TEXT


    override fun columnFormat(): ColumnFormat = this.format().columnFormat()


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() {}

    override val name : String = "table_widget_text_column"

    override val modelObject = this

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
data class ColumnName(val value : String) : SQLSerializable, Serializable
{

    companion object : Factory<ColumnName>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<ColumnName> = when (doc)
        {
            is DocText -> effValue(ColumnName(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }

    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue(): SQLValue = SQLText({this.value})

}


/**
 * Default Value Label
 */
data class DefaultValueLabel(val value : String) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<DefaultValueLabel>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<DefaultValueLabel> = when (doc)
        {
            is DocText -> effValue(DefaultValueLabel(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue(): SQLValue = SQLText({this.value})

}


/**
 * Is Column Namespaced?
 */
data class IsColumnNamespaced(val value : Boolean) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<IsColumnNamespaced>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<IsColumnNamespaced> = when (doc)
        {
            is DocBoolean -> effValue(IsColumnNamespaced(doc.boolean))
            else          -> effError(UnexpectedType(DocType.BOOLEAN, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue(): SQLValue = SQLInt({if (this.value) 1 else 0})

}



/**
 * Defines Namespace?
 */
data class DefinesNamespace(val value : Boolean) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<DefinesNamespace>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<DefinesNamespace> = when (doc)
        {
            is DocBoolean -> effValue(DefinesNamespace(doc.boolean))
            else          -> effError(UnexpectedType(DocType.BOOLEAN, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue(): SQLValue = SQLInt({if (this.value) 1 else 0})

}


/**
 * Table Widget Column Format
 */
data class ColumnFormat(override val id : UUID,
                        val textStyle : Comp<TextStyle>,
                        val alignment : Prim<Alignment>,
                        val width : Prim<ColumnWidth>,
                        val backgroundColorTheme : Prim<ColorTheme>)
                         : Model, Serializable
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.textStyle.name             = "text_style"
        this.alignment.name             = "alignment"
        this.width.name                 = "width"
        this.backgroundColorTheme.name  = "background_color_theme"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(textStyle : TextStyle,
                alignment : Alignment,
                width : ColumnWidth,
                backgroundColorTheme : ColorTheme)
        : this(UUID.randomUUID(),
               Comp(textStyle),
               Prim(alignment),
               Prim(width),
               Prim(backgroundColorTheme))


    companion object : Factory<ColumnFormat>
    {

        private val defaultTextStyle            = TextStyle.default()
        private val defaultAlignment            = Alignment.Center
        private val defaultWidth                = ColumnWidth(1.0f)
        private val defaultBackgroundColorTheme = ColorTheme.transparent


        override fun fromDocument(doc : SpecDoc)
                      : ValueParser<ColumnFormat> = when (doc)
        {
            is DocDict -> effApply(::ColumnFormat,
                                   // Text Style
                                   split(doc.maybeAt("text_style"),
                                         effValue(defaultTextStyle),
                                         { TextStyle.fromDocument(it) }),
                                   // Alignment
                                   split(doc.maybeAt("alignment"),
                                         effValue<ValueError,Alignment>(defaultAlignment),
                                         { Alignment.fromDocument(it) }),
                                   // Width
                                   split(doc.maybeAt("width"),
                                         effValue(defaultWidth),
                                         { ColumnWidth.fromDocument(it) }),
                                   // Background Color
                                   split(doc.maybeAt("background_color"),
                                         effValue(defaultBackgroundColorTheme),
                                         { ColorTheme.fromDocument(it) })
                                   )
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        val default : ColumnFormat =
                ColumnFormat(defaultTextStyle,
                             defaultAlignment,
                             defaultWidth,
                             defaultBackgroundColorTheme)

    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun textStyle() : TextStyle = this.textStyle.value

    fun alignment() : Alignment = this.alignment.value

    fun width() : Float = this.width.value.value

    fun backgroundColorTheme() : ColorTheme = this.backgroundColorTheme.value


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name : String = "table_widget_column_format"

    override val modelObject = this

}


/**
 * Column Widget
 */
data class ColumnWidth(val value : Float) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ColumnWidth>
    {
        override fun fromDocument(doc : SpecDoc): ValueParser<ColumnWidth> = when (doc)
        {
            is DocNumber -> effValue(ColumnWidth(doc.number.toFloat()))
            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue(): SQLValue = SQLReal({this.value.toDouble()})

}



/**
 * Default Number Column Value
 */
data class DefaultNumberColumnValue(val value : Double) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<DefaultNumberColumnValue>
    {
        override fun fromDocument(doc : SpecDoc)
                        : ValueParser<DefaultNumberColumnValue> = when (doc)
        {
            is DocNumber -> effValue(DefaultNumberColumnValue(doc.number))
            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue(): SQLValue = SQLReal({this.value})

}


/**
 * Default Text Column Value
 */
data class DefaultTextColumnValue(val value : String) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<DefaultTextColumnValue>
    {
        override fun fromDocument(doc : SpecDoc)
                        : ValueParser<DefaultTextColumnValue> = when (doc)
        {
            is DocText -> effValue(DefaultTextColumnValue(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue(): SQLValue = SQLText({this.value})

}

