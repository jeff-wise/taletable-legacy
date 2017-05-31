
package com.kispoko.tome.model.sheet.widget.table


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.*
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.model.game.engine.variable.BooleanVariable
import com.kispoko.tome.model.game.engine.variable.NumberVariable
import com.kispoko.tome.model.game.engine.variable.TextVariable
import com.kispoko.tome.model.sheet.style.Alignment
import com.kispoko.tome.model.sheet.style.TextStyle
import com.kispoko.tome.model.sheet.widget.table.cell.BooleanCellFormat
import com.kispoko.tome.model.sheet.widget.table.cell.NumberCellFormat
import com.kispoko.tome.model.sheet.widget.table.cell.TextCellFormat
import com.kispoko.tome.model.theme.ColorId
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.UnknownCase
import lulo.value.ValueError
import lulo.value.ValueParser
import java.util.*



/**
 * Table Widget Cell
 */
@Suppress("UNCHECKED_CAST")
sealed class TableWidgetCell : Model
{

    companion object : Factory<TableWidgetCell>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<TableWidgetCell> = when (doc)
        {
            is DocDict ->
            {
                when (doc.case())
                {
                    "boolean" -> TableWidgetBooleanCell.fromDocument(doc)
                                    as ValueParser<TableWidgetCell>
                    "number"  -> TableWidgetNumberCell.fromDocument(doc)
                                    as ValueParser<TableWidgetCell>
                    "text"    -> TableWidgetTextCell.fromDocument(doc)
                                    as ValueParser<TableWidgetCell>
                    else      -> effError<ValueError, TableWidgetCell>(
                                            UnknownCase(doc.case(), doc.path))
                }
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }

}


/**
 * Table Widget Boolean Cell
 */
data class TableWidgetBooleanCell(override val id : UUID,
                                  val format : Func<BooleanCellFormat>,
                                  val value : Func<BooleanVariable>) : Model
{

    companion object : Factory<TableWidgetBooleanCell>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<TableWidgetBooleanCell> = when (doc)
        {
            is DocDict ->
            {
                effApply(::TableWidgetBooleanCell,
                         // Model Id
                         effValue(UUID.randomUUID()),
                         // Format
                         split(doc.maybeAt("format"),
                               nullEff<BooleanCellFormat>(),
                               { effApply(::Comp, BooleanCellFormat.fromDocument(it)) }),
                         // Value
                         doc.at("value") ap {
                             effApply(::Comp, BooleanVariable.fromDocument(it))
                         })
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }

    override fun onLoad() { }

}


/**
 * Table Widget Number Cell
 */
sealed class TableWidgetNumberCell(override val id : UUID,
                                   val format : Func<NumberCellFormat>,
                                   val value : Func<NumberVariable>) : Model
{

    companion object : Factory<TableWidgetNumberCell>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<TableWidgetNumberCell> = when (doc)
        {
            is DocDict ->
            {
                effApply(::TableWidgetNumberCell,
                         // Model Id
                         effValue(UUID.randomUUID()),
                         // Format
                         split(doc.maybeAt("format"),
                               nullEff<NumberCellFormat>(),
                               { effApply(::Comp, NumberCellFormat.fromDocument(it)) }),
                         // Value
                         doc.at("value") ap {
                             effApply(::Comp, NumberVariable.fromDocument(it))
                         })
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }

}


/**
 * Table Widget Text Cell
 */
sealed class TableWidgetTextCell(override val id : UUID,
                                 val format : Func<TextCellFormat>,
                                 val value : Func<TextVariable>) : Model
{

    companion object : Factory<TableWidgetTextCell>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<TableWidgetTextCell> = when (doc)
        {
            is DocDict ->
            {
                effApply(::TableWidgetTextCell,
                         // Model Id
                         effValue(UUID.randomUUID()),
                         // Format
                         split(doc.maybeAt("format"),
                               nullEff<TextCellFormat>(),
                                { effApply(::Comp, TextCellFormat.fromDocument(it)) }),
                         // Value
                         doc.at("value") ap {
                             effApply(::Comp, TextVariable.fromDocument(it))
                         })
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }

}


/**
 * Table Widget Cell Format
 */
data class CellFormat(override val id : UUID,
                      val textStyle : Func<TextStyle>,
                      val alignment : Func<Alignment>,
                      val backgroundColor : Func<ColorId>) : Model
{
    companion object : Factory<CellFormat>
    {
        override fun fromDocument(doc : SpecDoc)
                      : ValueParser<CellFormat> = when (doc)
        {
            is DocDict -> effApply(::CellFormat,
                                   // Model Id
                                   effValue(UUID.randomUUID()),
                                   // Text Style
                                   split(doc.maybeAt("text_style"),
                                         nullEff<TextStyle>(),
                                         { effApply(::Comp, TextStyle.fromDocument(it)) }),
                                   // Alignment
                                   split(doc.maybeEnum<Alignment>("alignment"),
                                         nullEff<Alignment>(),
                                         { effValue(Prim(it)) }),
                                   // Background Color
                                   split(doc.maybeAt("background_color"),
                                         nullEff<ColorId>(),
                                         { effApply(::Prim, ColorId.fromDocument(it)) })
                                   )
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }

    override fun onLoad() { }

}


//
//
//    protected LinearLayout layout(Column column,
//                                  TextSize textSize,
//                                  Height cellHeight,
//                                  Context context)
//    {
//        LinearLayoutBuilder layout = new LinearLayoutBuilder();
//
//        layout.layoutType       = LayoutType.TABLE_ROW;
//        layout.orientation      = LinearLayout.HORIZONTAL;
//        layout.width            = 0;
//        layout.height           = TableRow.LayoutParams.WRAP_CONTENT;
//        layout.weight           = column.width().floatValue();
//
//        // > Alignment
//        Alignment cellAlignment = this.alignment();
//
//        if (column.alignment() != null)
//            cellAlignment = column.alignment();
//
//        layout.gravity          = cellAlignment.gravityConstant() | Gravity.CENTER_VERTICAL;
//
//
//        if (cellHeight == null)
//        {
//            switch (textSize)
//            {
//                case VERY_SMALL:
//                    cellHeight = Height.VERY_SMALL;
//                    break;
//                case SMALL:
//                    cellHeight = Height.SMALL;
//                    break;
//                case MEDIUM_SMALL:
//                    cellHeight = Height.MEDIUM_SMALL;
//                    break;
//                case MEDIUM:
//                    cellHeight = Height.MEDIUM;
//                    break;
//                case MEDIUM_LARGE:
//                    cellHeight = Height.MEDIUM_LARGE;
//                    break;
//                case LARGE:
//                    cellHeight = Height.LARGE;
//                    break;
//                default:
//                    cellHeight = Height.MEDIUM_SMALL;
//            }
//
//        }
//
//        layout.backgroundColor      = this.background().colorId();
//        layout.backgroundResource   = cellHeight.cellBackgroundResourceId();
//
//
//        return layout.linearLayout(context);
//    }


