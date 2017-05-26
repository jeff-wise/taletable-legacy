
package com.kispoko.tome.model.sheet.widget


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.*
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.model.engine.mechanic.MechanicCategory
import com.kispoko.tome.model.engine.value.ValueSetName
import com.kispoko.tome.model.engine.variable.BooleanVariable
import com.kispoko.tome.model.engine.variable.NumberVariable
import com.kispoko.tome.model.engine.variable.TextVariable
import com.kispoko.tome.model.engine.variable.Variable
import com.kispoko.tome.model.sheet.group.Group
import com.kispoko.tome.model.sheet.widget.table.TableWidgetColumn
import com.kispoko.tome.model.sheet.widget.table.TableWidgetRow
import effect.*
import lulo.document.*
import lulo.value.*
import lulo.value.UnexpectedType
import java.io.Serializable
import java.util.*



/**
 * Widget
 */
@Suppress("UNCHECKED_CAST")
sealed class Widget : Model, Serializable
{
    companion object : Factory<Widget>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<Widget> = when (doc)
        {
            is DocDict ->
            {
                when (doc.case())
                {
                    "action"   -> ActionWidget.fromDocument(doc)
                    "boolean"  -> BooleanWidget.fromDocument(doc)
                    "button"   -> ButtonWidget.fromDocument(doc)
                    "expander" -> ExpanderWidget.fromDocument(doc)
                    "image"    -> ImageWidget.fromDocument(doc)
                    "list"     -> ListWidget.fromDocument(doc)
                    "log"      -> LogWidget.fromDocument(doc) as ValueParser<Widget>
                    "mechanic" -> ActionWidget.fromDocument(doc)
                    "number"   -> ActionWidget.fromDocument(doc)
                    "option"   -> ActionWidget.fromDocument(doc)
                    "quote"    -> ActionWidget.fromDocument(doc)
                    "table"    -> ActionWidget.fromDocument(doc)
                    "tab"      -> ActionWidget.fromDocument(doc)
                    "text"     -> ActionWidget.fromDocument(doc)
                    else       -> Err<ValueError, DocPath, Widget>(
                                        UnknownCase(doc.case()), doc.path)
                }
            }
            else       -> Err(UnexpectedType(DocType.DICT, docType(doc)), doc.path)
        }
    }

}


/**
 * Widget Name
 */
data class WidgetName(val value : String)
{

    companion object : Factory<WidgetName>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<WidgetName> = when (doc)
        {
            is DocText -> valueResult(WidgetName(doc.text))
            else -> Err(UnexpectedType(DocType.TEXT, docType(doc)), doc.path)
        }
    }
}


/**
 * Action Widget
 */
data class ActionWidget(override val id : UUID,
                        val name : Func<WidgetName>,
                        val format : Func<ActionWidgetFormat>,
                        val modifier : Func<NumberVariable>,
                        val description : Func<ActionDescription>,
                        val descriptionHighlight : Func<ActionDescriptionHighlight>,
                        val actionName : Func<ActionName>,
                        val actionResult : Func<ActionResult>) : Widget()
{

    companion object : Factory<Widget>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<Widget>  = when (doc)
        {
            is DocDict ->
            {
                effApply8(::ActionWidget,
                          // Model Id
                          valueResult(UUID.randomUUID()),
                          // Widget Name
                          doc.at("name") ap {
                              effApply(::Prim, WidgetName.fromDocument(it))
                          },
                          // Format
                          doc.at("format") ap {
                              effApply(::Comp, ActionWidgetFormat.fromDocument(it))
                          },
                          // Modifier
                          doc.at("modifier") ap {
                              effApply(::Comp, NumberVariable.fromDocument(it))
                          },
                          // Description
                          doc.at("description") ap {
                              effApply(::Prim, ActionDescription.fromDocument(it))
                          },
                          // Description Highlight
                          doc.at("description_highlight") ap {
                              effApply(::Prim, ActionDescriptionHighlight.fromDocument(it))
                          },
                          // Action Name
                          doc.at("action_name") ap {
                              effApply(::Prim, ActionName.fromDocument(it))
                          },
                          // Action Result
                          doc.at("action_result") ap {
                              effApply(::Prim, ActionResult.fromDocument(it))
                          })
            }

            else       -> Err(UnexpectedType(DocType.DICT, docType(doc)), doc.path)
        }
    }

    override fun onLoad() { }

}


/**
 * Boolean Widget
 */
data class BooleanWidget(override val id : UUID,
                         val name : Func<WidgetName>,
                         val format : Func<BooleanWidgetFormat>,
                         val value : Func<BooleanVariable>) : Widget()
{

    companion object : Factory<Widget>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<Widget>  = when (doc)
        {
            is DocDict ->
            {
                effApply4(::BooleanWidget,
                          // Model Id
                          valueResult(UUID.randomUUID()),
                          // Widget Name
                          doc.at("name") ap {
                              effApply(::Prim, WidgetName.fromDocument(it))
                          },
                          // Format
                          doc.at("format") ap {
                              effApply(::Comp, BooleanWidgetFormat.fromDocument(it))
                          },
                          // Value
                          doc.at("value") ap {
                              effApply(::Comp, BooleanVariable.fromDocument(it))
                          })
            }

            else       -> Err(UnexpectedType(DocType.DICT, docType(doc)), doc.path)
        }
    }

    override fun onLoad() { }

}


/**
 * Button Widget
 */
data class ButtonWidget(override val id : UUID,
                        val name : Func<WidgetName>,
                        val format : Func<ButtonWidgetFormat>,
                        val viewType : Func<ButtonViewType>,
                        val label : Func<ButtonLabel>,
                        val description : Func<ButtonDescription>,
                        val icon : Func<ButtonIcon>) : Widget()
{

    companion object : Factory<Widget>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<Widget>  = when (doc)
        {
            is DocDict ->
            {
                effApply7(::ButtonWidget,
                          // Model Id
                          valueResult(UUID.randomUUID()),
                          // Widget Name
                          doc.at("name") ap {
                              effApply(::Prim, WidgetName.fromDocument(it))
                          },
                          // Format
                          doc.at("format") ap {
                              effApply(::Comp, ButtonWidgetFormat.fromDocument(it))
                          },
                          // View Type
                          effApply(::Prim, doc.enum<ButtonViewType>("view_type")),
                          // Label
                          doc.at("label") ap {
                              effApply(::Prim, ButtonLabel.fromDocument(it))
                          },
                          // Description
                          doc.at("description") ap {
                              effApply(::Prim, ButtonDescription.fromDocument(it))
                          },
                          // Icon
                          effApply(::Prim, doc.enum<ButtonIcon>("icon")))
            }

            else       -> Err(UnexpectedType(DocType.DICT, docType(doc)), doc.path)
        }
    }

    override fun onLoad() { }
}


/**
 * Expander Widget
 */
data class ExpanderWidget(override val id : UUID,
                          val name : Func<WidgetName>,
                          val format : Func<ExpanderWidgetFormat>,
                          val label : Func<ExpanderLabel>,
                          val groups: Coll<Group>) : Widget()
{

    companion object : Factory<Widget>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<Widget>  = when (doc)
        {
            is DocDict ->
            {
                effApply5(::ExpanderWidget,
                          // Model Id
                          valueResult(UUID.randomUUID()),
                          // Widget Name
                          doc.at("name") ap {
                              effApply(::Prim, WidgetName.fromDocument(it))
                          },
                          // Format
                          doc.at("format") ap {
                              effApply(::Comp, ExpanderWidgetFormat.fromDocument(it))
                          },
                          // Label
                          doc.at("label") ap {
                              effApply(::Prim, ExpanderLabel.fromDocument(it))
                          },
                          // Groups
                          doc.list("groups") ap { docList ->
                              effApply(::Coll,
                                  docList.map { Group.fromDocument(it) })
                          })
            }

            else       -> Err(UnexpectedType(DocType.DICT, docType(doc)), doc.path)
        }
    }

    override fun onLoad() { }
}


/**
 * Image Widget
 */
data class ImageWidget(override val id : UUID,
                       val name : Func<WidgetName>) : Widget()
{

    companion object : Factory<Widget>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<Widget>  = when (doc)
        {
            is DocDict ->
            {
                effApply2(::ImageWidget,
                          // Model Id
                          valueResult(UUID.randomUUID()),
                          // Widget Name
                          doc.at("name") ap {
                              effApply(::Prim, WidgetName.fromDocument(it))
                          })
            }

            else       -> Err(UnexpectedType(DocType.DICT, docType(doc)), doc.path)
        }
    }

    override fun onLoad() { }
}


/**
 * List Widget
 */
data class ListWidget(override val id : UUID,
                      val name : Func<WidgetName>,
                      val format : Func<ListWidgetFormat>,
                      val valueSetName : Func<ValueSetName>,
                      val values : Coll<Variable>) : Widget()
{

    companion object : Factory<Widget>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<Widget>  = when (doc)
        {
            is DocDict ->
            {
                effApply5(::ListWidget,
                          // Model Id
                          valueResult(UUID.randomUUID()),
                          // Widget Name
                          doc.at("name") ap {
                              effApply(::Prim, WidgetName.fromDocument(it))
                          },
                          // Format
                          doc.at("format") ap {
                              effApply(::Comp, ListWidgetFormat.fromDocument(it))
                          },
                          // ValueSet Name
                          doc.at("value_set_name") ap {
                              effApply(::Prim, ValueSetName.fromDocument(it))
                          },
                          // Groups
                          doc.list("values") ap { docList ->
                              effApply(::Coll,
                                  docList.map { Variable.fromDocument(it) })
                          })
            }

            else       -> Err(UnexpectedType(DocType.DICT, docType(doc)), doc.path)
        }
    }

    override fun onLoad() { }
}


/**
 * Log Widget
 */
data class LogWidget(override val id : UUID,
                     val name : Func<WidgetName>,
                     val format : Func<LogWidgetFormat>,
                     val entries : Coll<LogEntry>) : Widget()
{

    companion object : Factory<LogWidget>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<LogWidget>  = when (doc)
        {
            is DocDict ->
            {
                effApply4(::LogWidget,
                          // Model Id
                          valueResult(UUID.randomUUID()),
                          // Widget Name
                          doc.at("name") ap {
                              effApply(::Prim, WidgetName.fromDocument(it))
                          },
                          // Format
                          doc.at("format") ap {
                              effApply(::Comp, LogWidgetFormat.fromDocument(it))
                          },
                          // Entries
                          doc.list("entries") ap { docList ->
                              effApply(::Coll,
                                  docList.map { LogEntry.fromDocument(it) })
                          })
            }

            else       -> Err(UnexpectedType(DocType.DICT, docType(doc)), doc.path)
        }
    }

    override fun onLoad() { }

}


/**
 * Mechanic Widget
 */
data class MechanicWidget(override val id : UUID,
                          val name : Func<WidgetName>,
                          val category : Func<MechanicCategory>) : Widget()
{

    companion object : Factory<MechanicWidget>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<MechanicWidget>  = when (doc)
        {
            is DocDict ->
            {
                effApply3(::MechanicWidget,
                          // Model Id
                          valueResult(UUID.randomUUID()),
                          // Widget Name
                          doc.at("name") ap {
                              effApply(::Prim, WidgetName.fromDocument(it))
                          },
                          // Category
                          doc.at("category") ap {
                              effApply(::Prim, MechanicCategory.fromDocument(it))
                          })
            }

            else       -> Err(UnexpectedType(DocType.DICT, docType(doc)), doc.path)
        }
    }

    override fun onLoad() { }

}


/**
 * Number Widget
 */
data class NumberWidget(override val id : UUID,
                        val name : Func<WidgetName>,
                        val format : Func<NumberWidgetFormat>,
                        val value : Func<NumberVariable>,
                        val valuePrefix : Func<String>,
                        val valuePostfix : Func<String>,
                        val description : Func<String>,
                        val variables : Coll<Variable>) : Widget()
{

    companion object : Factory<NumberWidget>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<NumberWidget>  = when (doc)
        {
            is DocDict ->
            {
                effApply8(::NumberWidget,
                          // Model Id
                          valueResult(UUID.randomUUID()),
                          // Widget Name
                          doc.at("name") ap {
                              effApply(::Prim, WidgetName.fromDocument(it))
                          },
                          // Format
                          split(doc.maybeAt("format"),
                                valueResult<Func<NumberWidgetFormat>>(Null()),
                                fun(d : SpecDoc) : ValueParser<Func<NumberWidgetFormat>> =
                                        effApply(::Comp, NumberWidgetFormat.fromDocument(d))),
                          // Value
                          doc.at("value") ap {
                              effApply(::Comp, NumberVariable.fromDocument(it))
                          },
                          // Value Prefix
                          split(doc.maybeText("value_prefix"),
                                valueResult<Func<String>>(Null()),
                                { valueResult(Prim(it)) }),
                          // Value Prefix
                          split(doc.maybeText("value_postfix"),
                                valueResult<Func<String>>(Null()),
                                { valueResult(Prim(it)) }),
                          // Description
                          split(doc.maybeText("description"),
                                valueResult<Func<String>>(Null()),
                                { valueResult(Prim(it)) }),
                          // Variables
                          doc.list("variables") ap { docList ->
                              effApply(::Coll,
                                  docList.map { Variable.fromDocument(it) })
                          })
            }

            else       -> Err(UnexpectedType(DocType.DICT, docType(doc)), doc.path)
        }
    }

    override fun onLoad() { }
}


/**
 * Option Widget
 */
data class OptionWidget(override val id : UUID,
                        val name : Func<WidgetName>,
                        val format : Func<OptionWidgetFormat>,
                        val viewType : Func<OptionViewType>,
                        val description : Func<OptionDescription>,
                        val valueSet : Func<ValueSetName>) : Widget()
{

    companion object : Factory<OptionWidget>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<OptionWidget>  = when (doc)
        {
            is DocDict ->
            {
                effApply6(::OptionWidget,
                          // Model Id
                          valueResult(UUID.randomUUID()),
                          // Widget Name
                          doc.at("name") ap {
                              effApply(::Prim, WidgetName.fromDocument(it))
                          },
                          // Format
                          split(doc.maybeAt("format"),
                                valueResult<Func<OptionWidgetFormat>>(Null()),
                                fun(d : SpecDoc) : ValueParser<Func<OptionWidgetFormat>> =
                                        effApply(::Comp, OptionWidgetFormat.fromDocument(d))),
                          // View Type
                          split(doc.maybeEnum<OptionViewType>("view_type"),
                                valueResult<Func<OptionViewType>>(Null()),
                                { valueResult(Prim(it)) }),
                          // Description
                          split(doc.maybeAt("description"),
                                valueResult<Func<OptionDescription>>(Null()),
                                fun(d : SpecDoc) : ValueParser<Func<OptionDescription>> =
                                        effApply(::Prim, OptionDescription.fromDocument(d))),
                          // ValueSet Name
                          split(doc.maybeAt("value_set_name"),
                                valueResult<Func<ValueSetName>>(Null()),
                                fun(d : SpecDoc) : ValueParser<Func<ValueSetName>> =
                                        effApply(::Prim, ValueSetName.fromDocument(d)))
                        )
            }
            else       -> Err(UnexpectedType(DocType.DICT, docType(doc)), doc.path)
        }
    }

    override fun onLoad() { }

}


/**
 * Quote Widget
 */
data class QuoteWidget(override val id : UUID,
                       val name : Func<WidgetName>,
                       val format : Func<QuoteWidgetFormat>,
                       val viewType : Func<QuoteViewType>,
                       val quote : Func<Quote>,
                       val source : Func<QuoteSource>) : Widget()
{

    companion object : Factory<QuoteWidget>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<QuoteWidget>  = when (doc)
        {
            is DocDict ->
            {
                effApply6(::QuoteWidget,
                          // Model Id
                          valueResult(UUID.randomUUID()),
                          // Widget Name
                          doc.at("name") ap {
                              effApply(::Prim, WidgetName.fromDocument(it))
                          },
                          // Format
                          split(doc.maybeAt("format"),
                                valueResult<Func<QuoteWidgetFormat>>(Null()),
                                fun(d : SpecDoc) : ValueParser<Func<QuoteWidgetFormat>> =
                                        effApply(::Comp, QuoteWidgetFormat.fromDocument(d))),
                          // View Type
                          split(doc.maybeEnum<QuoteViewType>("view_type"),
                                valueResult<Func<QuoteViewType>>(Null()),
                                { valueResult(Prim(it)) }),
                          // Quote
                          split(doc.maybeAt("quote"),
                                valueResult<Func<Quote>>(Null()),
                                fun(d : SpecDoc) : ValueParser<Func<Quote>> =
                                        effApply(::Prim, Quote.fromDocument(d))),
                          // Quote Source
                          split(doc.maybeAt("source"),
                                valueResult<Func<QuoteSource>>(Null()),
                                fun(d : SpecDoc) : ValueParser<Func<QuoteSource>> =
                                        effApply(::Prim, QuoteSource.fromDocument(d)))
                        )
            }
            else       -> Err(UnexpectedType(DocType.DICT, docType(doc)), doc.path)
        }
    }

    override fun onLoad() { }

}


/**
 * Table Widget
 */
data class TableWidget(override val id : UUID,
                       val name : Func<WidgetName>,
                       val format : Func<TableWidgetFormat>,
                       val columns : Coll<TableWidgetColumn>,
                       val rows : Coll<TableWidgetRow>) : Widget()
{

    companion object : Factory<TableWidget>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<TableWidget>  = when (doc)
        {
            is DocDict ->
            {
                effApply5(::TableWidget,
                          // Model Id
                          valueResult(UUID.randomUUID()),
                          // Widget Name
                          doc.at("name") ap {
                              effApply(::Prim, WidgetName.fromDocument(it))
                          },
                          // Format
                          split(doc.maybeAt("format"),
                                valueResult<Func<TableWidgetFormat>>(Null()),
                                fun(d : SpecDoc) : ValueParser<Func<TableWidgetFormat>> =
                                        effApply(::Comp, TableWidgetFormat.fromDocument(d))),
                          // Columns
                          doc.list("columns") ap { docList ->
                              effApply(::Coll,
                                      docList.map { TableWidgetColumn.fromDocument(it) })
                          },
                          // Rows
                          doc.list("rows") ap { docList ->
                              effApply(::Coll,
                                  docList.map { TableWidgetRow.fromDocument(it) })
                          })
            }
            else       -> Err(UnexpectedType(DocType.DICT, docType(doc)), doc.path)
        }
    }

    override fun onLoad() { }
}


/**
 * Tab Widget
 */
data class TabWidget(override val id : UUID,
                     val name : Func<WidgetName>,
                     val format : Func<TabWidgetFormat>,
                     val tabs : Coll<Tab>,
                     val defaultSelected : Func<Int>) : Widget()
{

    companion object : Factory<TabWidget>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<TabWidget>  = when (doc)
        {
            is DocDict ->
            {
                effApply5(::TabWidget,
                          // Model Id
                          valueResult(UUID.randomUUID()),
                          // Widget Name
                          doc.at("name") ap {
                              effApply(::Prim, WidgetName.fromDocument(it))
                          },
                          // Format
                          split(doc.maybeAt("format"),
                                valueResult<Func<TabWidgetFormat>>(Null()),
                                fun(d : SpecDoc) : ValueParser<Func<TabWidgetFormat>> =
                                        effApply(::Comp, TabWidgetFormat.fromDocument(d))),
                          // Tabs
                          doc.list("tabs") ap { docList ->
                              effApply(::Coll,
                                      docList.map { Tab.fromDocument(it) })
                          },
                          // Default Selected
                          split(doc.maybeInt("default_selected"),
                                valueResult<Func<Int>>(Null()),
                                { valueResult(Prim(it)) })
                          )
            }
            else       -> Err(UnexpectedType(DocType.DICT, docType(doc)), doc.path)
        }
    }

    override fun onLoad() { }

}


/**
 * Text Widget
 */
data class TextWidget(override val id : UUID,
                      val name : Func<WidgetName>,
                      val format : Func<TextWidgetFormat>,
                      val description : Func<TextDescription>,
                      val value : Func<TextVariable>,
                      val variables : Coll<Variable>) : Widget()
{

    companion object : Factory<TextWidget>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<TextWidget>  = when (doc)
        {
            is DocDict ->
            {
                effApply6(::TextWidget,
                          // Model Id
                          valueResult(UUID.randomUUID()),
                          // Widget Name
                          doc.at("name") ap {
                              effApply(::Prim, WidgetName.fromDocument(it))
                          },
                          // Format
                          split(doc.maybeAt("format"),
                                valueResult<Func<TextWidgetFormat>>(Null()),
                                fun(d : SpecDoc) : ValueParser<Func<TextWidgetFormat>> =
                                        effApply(::Comp, TextWidgetFormat.fromDocument(d))),
                          // Description
                          split(doc.maybeAt("description"),
                                valueResult<Func<TextDescription>>(Null()),
                                fun(d : SpecDoc) : ValueParser<Func<TextDescription>> =
                                        effApply(::Prim, TextDescription.fromDocument(d))),
                          // Value
                          doc.at("value") ap {
                              effApply(::Comp, TextVariable.fromDocument(it))
                          },
                          // Variables
                          doc.list("variables") ap { docList ->
                              effApply(::Coll,
                                 docList.map { Variable.fromDocument(it) })
                          })
            }
            else       -> Err(UnexpectedType(DocType.DICT, docType(doc)), doc.path)
        }
    }

    override fun onLoad() { }

}


// sealed class Widget // : Model, Serializable

//



//
//public abstract class Widget extends Model
//                             implements ToYaml, Serializable
//{
//
//    // INTERFACE
//    // ------------------------------------------------------------------------------------------
//
//    abstract public View view(boolean rowhasLabel, Context context);
//
//    abstract public WidgetData data();
//
//    abstract public void initialize(GroupParent groupParent, Context context);
//
//
//    // > State
//    // ------------------------------------------------------------------------------------------
//
//
//    // > Views
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * Widget layout.
//     *
//     * @return A LinearLayout that represents the outer-most container of a component view.
//     */
//    public LinearLayout layout(boolean rowHasLabel, final Context context)
//    {
//        LinearLayoutBuilder layout = new LinearLayoutBuilder();
//
//        layout.orientation      = LinearLayout.VERTICAL;
//        layout.width            = 0;
//        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        layout.weight       = this.data().format().width().floatValue();
//
////        layout.margin.left      = R.dimen.widget_margin_horz;
////        layout.margin.right     = R.dimen.widget_margin_horz;
//
//        return layout.linearLayout(context);
//    }
//
//
//    // STATIC METHODS
//    // ------------------------------------------------------------------------------------------
//
//    public static Widget fromYaml(YamlParser yaml)
//                  throws YamlParseException
//    {
//        WidgetType widgetType = WidgetType.fromYaml(yaml.atKey("type"));
//
//        switch (widgetType) {
//            case TEXT:
//                return TextWidget.fromYaml(yaml);
//            case NUMBER:
//                return NumberWidget.fromYaml(yaml);
//            case BOOLEAN:
//                return BooleanWidget.fromYaml(yaml);
//            case IMAGE:
//                return ImageWidget.fromYaml(yaml);
//            case TABLE:
//                return TableWidget.fromYaml(yaml);
//            case ACTION:
//                return ActionWidget.fromYaml(yaml);
//            default:
//                ApplicationFailure.union(
//                        UnionException.unknownVariant(
//                                new UnknownVariantError(WidgetType.class.getName())));
//        }
//
//        return null;
//    }
//
//
//}
