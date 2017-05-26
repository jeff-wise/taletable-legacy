
package com.kispoko.tome.model.sheet.widget.table


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Comp
import com.kispoko.tome.lib.functor.Func
import com.kispoko.tome.lib.functor.Null
import com.kispoko.tome.lib.functor.Prim
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.model.engine.variable.DefinesNamespace
import com.kispoko.tome.model.sheet.style.Alignment
import com.kispoko.tome.model.sheet.style.TextStyle
import com.kispoko.tome.model.sheet.widget.table.column.BooleanColumnFormat
import com.kispoko.tome.model.sheet.widget.table.column.NumberColumnFormat
import com.kispoko.tome.model.sheet.widget.table.column.TextColumnFormat
import com.kispoko.tome.model.theme.ColorId
import effect.*
import lulo.document.*
import lulo.value.*
import lulo.value.UnexpectedType
import java.util.*



/**
 * Table Widget Column
 */
@Suppress("UNCHECKED_CAST")
sealed class TableWidgetColumn(open val name : Func<ColumnName>,
                               open val defaultValueLabel : Func<DefaultValueLabel>,
                               open val isColumnNamespaced: Func<IsColumnNamespaced>) : Model
{

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
                    else      -> Err<ValueError, DocPath, TableWidgetColumn>(
                                            UnknownCase(doc.case()), doc.path)
                }
            }
            else       -> Err(UnexpectedType(DocType.DICT, docType(doc)), doc.path)
        }
    }

}


/**
 * Table Widget Boolean Column
 */
data class TableWidgetBooleanColumn(
                    override val id : UUID,
                    override val name : Func<ColumnName>,
                    override val defaultValueLabel : Func<DefaultValueLabel>,
                    override val isColumnNamespaced: Func<IsColumnNamespaced>,
                    val defaultValue : Func<Boolean>,
                    val format : Func<BooleanColumnFormat>,
                    val trueText : Func<String>,
                    val falseText : Func<String>)
                    : TableWidgetColumn(name, defaultValueLabel, isColumnNamespaced)
{

    companion object : Factory<TableWidgetBooleanColumn>
    {
        override fun fromDocument(doc : SpecDoc)
                        : ValueParser<TableWidgetBooleanColumn> = when (doc)
        {
            is DocDict ->
            {
                effApply8(::TableWidgetBooleanColumn,
                          // Model Id
                          valueResult(UUID.randomUUID()),
                          // Name
                          doc.at("name") ap {
                              effApply(::Prim, ColumnName.fromDocument(it))
                          },
                          // Default Value Label
                          split(doc.maybeAt("default_value_label"),
                                valueResult<Func<DefaultValueLabel>>(Null()),
                                 fun(d : SpecDoc) : ValueParser<Func<DefaultValueLabel>> =
                                     effApply(::Prim, DefaultValueLabel.fromDocument(d))),
                          // Is Column Namespaced
                          split(doc.maybeAt("is_namespaced"),
                                valueResult<Func<IsColumnNamespaced>>(Null()),
                                fun(d : SpecDoc) : ValueParser<Func<IsColumnNamespaced>> =
                                    effApply(::Prim, IsColumnNamespaced.fromDocument(d))),
                          // Default Value
                          split(doc.maybeBoolean("default_value"),
                                valueResult<Func<Boolean>>(Null()),
                                { valueResult(Prim(it)) }),
                          // Format
                          split(doc.maybeAt("format"),
                                valueResult<Func<BooleanColumnFormat>>(Null()),
                                fun(d : SpecDoc) : ValueParser<Func<BooleanColumnFormat>> =
                                    effApply(::Comp, BooleanColumnFormat.fromDocument(d))),
                          // True Text
                          split(doc.maybeText("true_text"),
                                valueResult<Func<String>>(Null()),
                                { valueResult(Prim(it)) }),
                          // Default Value
                          split(doc.maybeText("false_text"),
                                valueResult<Func<String>>(Null()),
                                { valueResult(Prim(it)) })
                        )
            }
            else       -> Err(UnexpectedType(DocType.DICT, docType(doc)), doc.path)
        }
    }


    override fun onLoad() {}

}


/**
 * Table Widget Number Column
 */
data class TableWidgetNumberColumn(
                        override val id : UUID,
                        override val name : Func<ColumnName>,
                        override val defaultValueLabel : Func<DefaultValueLabel>,
                        override val isColumnNamespaced: Func<IsColumnNamespaced>,
                        val defaultValue : Func<Double>,
                        val format : Func<NumberColumnFormat>)
                        : TableWidgetColumn(name, defaultValueLabel, isColumnNamespaced)
{

    companion object : Factory<TableWidgetNumberColumn>
    {
        override fun fromDocument(doc : SpecDoc)
                        : ValueParser<TableWidgetNumberColumn> = when (doc)
        {
            is DocDict ->
            {
                effApply6(::TableWidgetNumberColumn,
                          // Model Id
                          valueResult(UUID.randomUUID()),
                          // Name
                          doc.at("name") ap {
                              effApply(::Prim, ColumnName.fromDocument(it))
                          },
                          // Default Value Label
                          split(doc.maybeAt("default_value_label"),
                                valueResult<Func<DefaultValueLabel>>(Null()),
                                 fun(d : SpecDoc) : ValueParser<Func<DefaultValueLabel>> =
                                     effApply(::Prim, DefaultValueLabel.fromDocument(d))),
                          // Is Column Namespaced
                          split(doc.maybeAt("is_namespaced"),
                                valueResult<Func<IsColumnNamespaced>>(Null()),
                                fun(d : SpecDoc) : ValueParser<Func<IsColumnNamespaced>> =
                                    effApply(::Prim, IsColumnNamespaced.fromDocument(d))),
                          // Default Value
                          split(doc.maybeDouble("default_value"),
                                valueResult<Func<Double>>(Null()),
                                { valueResult(Prim(it)) }),
                          // Format
                          split(doc.maybeAt("format"),
                                valueResult<Func<NumberColumnFormat>>(Null()),
                                fun(d : SpecDoc) : ValueParser<Func<NumberColumnFormat>> =
                                    effApply(::Comp, NumberColumnFormat.fromDocument(d)))
                        )
            }
            else       -> Err(UnexpectedType(DocType.DICT, docType(doc)), doc.path)
        }
    }

    override fun onLoad() {}

}


/**
 * Table Widget Text Column
 */
data class TableWidgetTextColumn(
                        override val id : UUID,
                        override val name : Func<ColumnName>,
                        override val defaultValueLabel : Func<DefaultValueLabel>,
                        override val isColumnNamespaced: Func<IsColumnNamespaced>,
                        val defaultValue : Func<String>,
                        val format : Func<TextColumnFormat>,
                        val definesNamespace : Func<ColumnDefinesNamespace>)
                        : TableWidgetColumn(name, defaultValueLabel, isColumnNamespaced)
{

    companion object : Factory<TableWidgetTextColumn>
    {
        override fun fromDocument(doc : SpecDoc)
                        : ValueParser<TableWidgetTextColumn> = when (doc)
        {
            is DocDict ->
            {
                effApply7(::TableWidgetTextColumn,
                          // Model Id
                          valueResult(UUID.randomUUID()),
                          // Name
                          doc.at("name") ap {
                              effApply(::Prim, ColumnName.fromDocument(it))
                          },
                          // Default Value Label
                          split(doc.maybeAt("default_value_label"),
                                valueResult<Func<DefaultValueLabel>>(Null()),
                                 fun(d : SpecDoc) : ValueParser<Func<DefaultValueLabel>> =
                                     effApply(::Prim, DefaultValueLabel.fromDocument(d))),
                          // Is Column Namespaced
                          split(doc.maybeAt("is_namespaced"),
                                valueResult<Func<IsColumnNamespaced>>(Null()),
                                fun(d : SpecDoc) : ValueParser<Func<IsColumnNamespaced>> =
                                    effApply(::Prim, IsColumnNamespaced.fromDocument(d))),
                          // Default Value
                          split(doc.maybeText("default_value"),
                                valueResult<Func<String>>(Null()),
                                { valueResult(Prim(it)) }),
                          // Format
                          split(doc.maybeAt("format"),
                                valueResult<Func<TextColumnFormat>>(Null()),
                                fun(d : SpecDoc) : ValueParser<Func<TextColumnFormat>> =
                                    effApply(::Comp, TextColumnFormat.fromDocument(d))),
                          // Defines Namespace?
                          split(doc.maybeAt("defines_namespace"),
                                valueResult<Func<ColumnDefinesNamespace>>(Null()),
                                fun(d : SpecDoc) : ValueParser<Func<ColumnDefinesNamespace>> =
                                        effApply(::Prim, ColumnDefinesNamespace.fromDocument(d)))
                        )
            }
            else       -> Err(UnexpectedType(DocType.DICT, docType(doc)), doc.path)
        }
    }

    override fun onLoad() {}

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
            is DocText -> valueResult(ColumnName(doc.text))
            else -> Err(UnexpectedType(DocType.TEXT, docType(doc)), doc.path)
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
            is DocText -> valueResult(DefaultValueLabel(doc.text))
            else -> Err(UnexpectedType(DocType.TEXT, docType(doc)), doc.path)
        }
    }
}


/**
 * Is Column Namespaced?
 */
data class IsColumnNamespaced(val value : Boolean)
{

    companion object : Factory<IsColumnNamespaced>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<IsColumnNamespaced> = when (doc)
        {
            is DocBoolean -> valueResult(IsColumnNamespaced(doc.boolean))
            else          -> Err(UnexpectedType(DocType.BOOLEAN, docType(doc)), doc.path)
        }
    }
}


/**
 * Column Defines Namespace?
 */
data class ColumnDefinesNamespace(val value : Boolean)
{

    companion object : Factory<ColumnDefinesNamespace>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<ColumnDefinesNamespace> = when (doc)
        {
            is DocBoolean -> valueResult(ColumnDefinesNamespace(doc.boolean))
            else          -> Err(UnexpectedType(DocType.BOOLEAN, docType(doc)), doc.path)
        }
    }
}


/**
 * Table Widget Column Format
 */
data class ColumnFormat(override val id : UUID,
                        val textStyle : Func<TextStyle>,
                        val alignment : Func<Alignment>,
                        val width : Func<Int>,
                        val backgroundColor : Func<ColorId>) : Model
{
    companion object : Factory<ColumnFormat>
    {
        override fun fromDocument(doc : SpecDoc)
                      : ValueParser<ColumnFormat> = when (doc)
        {
            is DocDict -> effApply5(::ColumnFormat,
                                    // Model Id
                                    valueResult(UUID.randomUUID()),
                                    // Text Style
                                    split(doc.maybeAt("text_style"),
                                          valueResult<Func<TextStyle>>(Null()),
                                          fun(d : SpecDoc) : ValueParser<Func<TextStyle>> =
                                              effApply(::Comp, TextStyle.fromDocument(d))),
                                    // Alignment
                                    split(doc.maybeEnum<Alignment>("alignment"),
                                          valueResult<Func<Alignment>>(Null()),
                                          { valueResult(Prim(it)) }),
                                    // Width
                                    split(doc.maybeInt("width"),
                                          valueResult<Func<Int>>(Null()),
                                          { valueResult(Prim(it)) }),
                                    // Background Color
                                    split(doc.maybeAt("background_color"),
                                          valueResult<Func<ColorId>>(Null()),
                                          fun(d : SpecDoc) : ValueParser<Func<ColorId>> =
                                              effApply(::Prim, ColorId.fromDocument(d)))
                                    )
            else       -> Err(UnexpectedType(DocType.DICT, docType(doc)), doc.path)
        }
    }

    override fun onLoad() { }

}

