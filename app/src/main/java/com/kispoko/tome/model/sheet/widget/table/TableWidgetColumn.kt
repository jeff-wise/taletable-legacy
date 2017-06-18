
package com.kispoko.tome.model.sheet.widget.table


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.*
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.model.sheet.style.Alignment
import com.kispoko.tome.model.sheet.style.TextStyle
import com.kispoko.tome.model.sheet.widget.TableWidgetFormat
import com.kispoko.tome.model.sheet.widget.table.column.BooleanColumnFormat
import com.kispoko.tome.model.sheet.widget.table.column.NumberColumnFormat
import com.kispoko.tome.model.sheet.widget.table.column.TextColumnFormat
import com.kispoko.tome.model.theme.ColorTheme
import effect.*
import lulo.document.*
import lulo.value.*
import lulo.value.UnexpectedType
import java.util.*



/**
 * Table Widget Column
 */
@Suppress("UNCHECKED_CAST")
sealed class TableWidgetColumn(open val name : Prim<ColumnName>,
                               open val defaultValueLabel : Prim<DefaultValueLabel>,
                               open val isColumnNamespaced: Prim<Boolean>) : Model
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
                    "boolean" -> TableWidgetBooleanColumn.fromDocument(doc)
                                    as ValueParser<TableWidgetColumn>
                    "number"  -> TableWidgetNumberColumn.fromDocument(doc)
                                    as ValueParser<TableWidgetColumn>
                    "text"    -> TableWidgetTextColumn.fromDocument(doc)
                                    as ValueParser<TableWidgetColumn>
                    else      -> effError<ValueError, TableWidgetColumn>(
                                            UnknownCase(doc.case(), doc.path))
                }
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun nameString() : String = this.name.value.value

    fun defaultValueLabelString() : String = this.defaultValueLabel.value.value

    fun isColumnNamespaced() : Boolean = this.isColumnNamespaced.value


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
                    override val name : Prim<ColumnName>,
                    override val defaultValueLabel : Prim<DefaultValueLabel>,
                    override val isColumnNamespaced: Prim<Boolean>,
                    val defaultValue : Prim<Boolean>,
                    val format : Comp<BooleanColumnFormat>,
                    val trueText : Prim<String>,
                    val falseText : Prim<String>)
                    : TableWidgetColumn(name, defaultValueLabel, isColumnNamespaced)
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<TableWidgetBooleanColumn>
    {
        override fun fromDocument(doc : SpecDoc)
                        : ValueParser<TableWidgetBooleanColumn> = when (doc)
        {
            is DocDict ->
            {
                effApply(::TableWidgetBooleanColumn,
                         // Model Id
                         effValue(UUID.randomUUID()),
                         // Name
                         doc.at("name") ap {
                             effApply(::Prim, ColumnName.fromDocument(it))
                         },
                         // Default Value Label
                         doc.at("default_value_label") ap {
                             effApply(::Prim, DefaultValueLabel.fromDocument(it))
                         },
                         // Is Column Namespaced
                         split(doc.maybeBoolean("is_namespaced"),
                               effValue(Prim(false)),
                               {  effValue(Prim(it)) }),
                         // Default Value
                         split(doc.maybeBoolean("default_value"),
                               effValue((Prim(true))),
                               { effValue(Prim(it)) }),
                         // Format
                         split(doc.maybeAt("format"),
                               effValue(Comp(BooleanColumnFormat.default)),
                               { effApply(::Comp, BooleanColumnFormat.fromDocument(it)) }),
                         // True Text
                         doc.text("true_text") ap { effValue<ValueError,Prim<String>>(Prim(it)) },
                         // False Text
                         doc.text("false_text") ap { effValue<ValueError,Prim<String>>(Prim(it)) }
                         )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun defaultValue() : Boolean = this.defaultValue.value

    fun format() : BooleanColumnFormat = this.format.value

    fun trueText() : String = this.trueText.value

    fun falseText() : String = this.falseText.value


    // -----------------------------------------------------------------------------------------
    // COLUMN
    // -----------------------------------------------------------------------------------------

    override fun type() : TableWidgetColumnType = TableWidgetColumnType.BOOLEAN

    override fun columnFormat(): ColumnFormat = this.format().columnFormat()


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() {}

}


/**
 * Table Widget Number Column
 */
data class TableWidgetNumberColumn(
                        override val id : UUID,
                        override val name : Prim<ColumnName>,
                        override val defaultValueLabel : Prim<DefaultValueLabel>,
                        override val isColumnNamespaced: Prim<Boolean>,
                        val defaultValue : Prim<Double>,
                        val format : Comp<NumberColumnFormat>)
                        : TableWidgetColumn(name, defaultValueLabel, isColumnNamespaced)
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<TableWidgetNumberColumn>
    {
        override fun fromDocument(doc : SpecDoc)
                        : ValueParser<TableWidgetNumberColumn> = when (doc)
        {
            is DocDict ->
            {
                effApply(::TableWidgetNumberColumn,
                         // Model Id
                         effValue(UUID.randomUUID()),
                         // Name
                         doc.at("name") ap {
                             effApply(::Prim, ColumnName.fromDocument(it))
                         },
                         // Default Value Label
                         doc.at("default_value_label") ap {
                             effApply(::Prim, DefaultValueLabel.fromDocument(it))
                         },
                         // Is Column Namespaced
                         split(doc.maybeBoolean("is_namespaced"),
                               effValue(Prim(false)),
                               { effValue(Prim(it)) }),
                         // Default Value
                         split(doc.maybeDouble("default_value"),
                               effValue(Prim(0.0)),
                               { effValue(Prim(it)) }),
                         // Format
                         split(doc.maybeAt("format"),
                               effValue(Comp(NumberColumnFormat.default)),
                               { effApply(::Comp, NumberColumnFormat.fromDocument(it)) })
                        )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun defaultValue() : Double = this.defaultValue.value

    fun format() : NumberColumnFormat = this.format.value


    // -----------------------------------------------------------------------------------------
    // COLUMN
    // -----------------------------------------------------------------------------------------

    override fun type() : TableWidgetColumnType = TableWidgetColumnType.NUMBER


    override fun columnFormat(): ColumnFormat = this.format().columnFormat()


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() {}

}


/**
 * Table Widget Text Column
 */
data class TableWidgetTextColumn(
                        override val id : UUID,
                        override val name : Prim<ColumnName>,
                        override val defaultValueLabel : Prim<DefaultValueLabel>,
                        override val isColumnNamespaced: Prim<Boolean>,
                        val defaultValue : Prim<String>,
                        val format : Comp<TextColumnFormat>,
                        val definesNamespace : Prim<Boolean>)
                        : TableWidgetColumn(name, defaultValueLabel, isColumnNamespaced)
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<TableWidgetTextColumn>
    {
        override fun fromDocument(doc : SpecDoc)
                        : ValueParser<TableWidgetTextColumn> = when (doc)
        {
            is DocDict ->
            {
                effApply(::TableWidgetTextColumn,
                         // Model Id
                         effValue(UUID.randomUUID()),
                         // Name
                         doc.at("name") ap {
                             effApply(::Prim, ColumnName.fromDocument(it))
                         },
                         // Default Value Label
                         doc.at("default_value_label") ap {
                             effApply(::Prim, DefaultValueLabel.fromDocument(it))
                         },
                         // Is Column Namespaced
                         split(doc.maybeBoolean("is_namespaced"),
                               effValue(Prim(false)),
                               { effValue(Prim(it)) }),
                         // Default Value
                         split(doc.maybeText("default_value"),
                               effValue(Prim("")),
                               { effValue(Prim(it)) }),
                         // Format
                         split(doc.maybeAt("format"),
                               effValue(Comp(TextColumnFormat.default)),
                               { effApply(::Comp, TextColumnFormat.fromDocument(it)) }),
                         // Defines Namespace?
                         split(doc.maybeBoolean("defines_namespace"),
                               effValue(Prim(false)),
                               { effValue(Prim(it)) })
                        )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun defaultValue() : String = this.defaultValue.value

    fun format() : TextColumnFormat = this.format.value

    fun definesNamespace() : Boolean = this.definesNamespace.value


    // -----------------------------------------------------------------------------------------
    // COLUMN
    // -----------------------------------------------------------------------------------------

    override fun type() : TableWidgetColumnType = TableWidgetColumnType.TEXT


    override fun columnFormat(): ColumnFormat = this.format().columnFormat()


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() {}

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
data class ColumnName(val value : String)
{

    companion object : Factory<ColumnName>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<ColumnName> = when (doc)
        {
            is DocText -> effValue(ColumnName(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }
}


/**
 * Default Value Label
 */
data class DefaultValueLabel(val value : String)
{

    companion object : Factory<DefaultValueLabel>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<DefaultValueLabel> = when (doc)
        {
            is DocText -> effValue(DefaultValueLabel(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }
}


/**
 * Table Widget Column Format
 */
data class ColumnFormat(override val id : UUID,
                        val textStyle : Comp<TextStyle>,
                        val alignment : Prim<Alignment>,
                        val width : Prim<Float>,
                        val backgroundColorTheme : Prim<ColorTheme>) : Model
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(textStyle : TextStyle,
                alignment : Alignment,
                width : Float,
                backgroundColorTheme : ColorTheme)
        : this(UUID.randomUUID(),
               Comp(textStyle),
               Prim(alignment),
               Prim(width),
               Prim(backgroundColorTheme))


    companion object : Factory<ColumnFormat>
    {

        private val defaultTextStyle            = TextStyle.default
        private val defaultAlignment            = Alignment.Center()
        private val defaultWidth                = 1.0f
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
                                   split(doc.maybeDouble("width"),
                                         effValue(defaultWidth),
                                         { effValue(it.toFloat()) }),
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

    fun width() : Float = this.width.value

    fun backgroundColorTheme() : ColorTheme = this.backgroundColorTheme.value


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

}

