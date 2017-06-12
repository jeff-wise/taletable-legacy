
package com.kispoko.tome.model.sheet.widget


import android.view.View
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.*
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.model.game.engine.mechanic.MechanicCategory
import com.kispoko.tome.model.game.engine.value.ValueSetId
import com.kispoko.tome.model.game.engine.variable.BooleanVariable
import com.kispoko.tome.model.game.engine.variable.NumberVariable
import com.kispoko.tome.model.game.engine.variable.TextVariable
import com.kispoko.tome.model.game.engine.variable.Variable
import com.kispoko.tome.model.sheet.SheetId
import com.kispoko.tome.model.sheet.group.Group
import com.kispoko.tome.model.sheet.widget.table.TableWidgetColumn
import com.kispoko.tome.model.sheet.widget.table.TableWidgetRow
import com.kispoko.tome.rts.sheet.SheetComponent
import com.kispoko.tome.rts.sheet.SheetContext
import com.kispoko.tome.rts.sheet.SheetDoesNotExist
import com.kispoko.tome.rts.sheet.SheetManager
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
sealed class Widget : Model, SheetComponent, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<Widget>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<Widget> = when (doc)
        {
            is DocDict ->
            {
                // TODO avoid hard coding this
                when (doc.case())
                {
                    "widget_action"   -> ActionWidget.fromDocument(doc)
                    "widget_boolean"  -> BooleanWidget.fromDocument(doc)
                    "widget_button"   -> ButtonWidget.fromDocument(doc)
                    "widget_expander" -> ExpanderWidget.fromDocument(doc)
                    "widget_image"    -> ImageWidget.fromDocument(doc)
                    "widget_list"     -> ListWidget.fromDocument(doc)
                    "widget_log"      -> LogWidget.fromDocument(doc)
                                            as ValueParser<Widget>
                    "widget_mechanic" -> MechanicWidget.fromDocument(doc)
                                            as ValueParser<Widget>
                    "widget_number"   -> NumberWidget.fromDocument(doc)
                                            as ValueParser<Widget>
                    "widget_option"   -> OptionWidget.fromDocument(doc)
                                            as ValueParser<Widget>
                    "widget_quote"    -> QuoteWidget.fromDocument(doc)
                                            as ValueParser<Widget>
                    "widget_table"    -> TableWidget.fromDocument(doc)
                                            as ValueParser<Widget>
                    "widget_tab"      -> TabWidget.fromDocument(doc)
                                            as ValueParser<Widget>
                    "widget_text"     -> TextWidget.fromDocument(doc)
                                            as ValueParser<Widget>
                    else       -> effError<ValueError,Widget>(UnknownCase(doc.case(), doc.path))
                }
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // WIDGET API
    // -----------------------------------------------------------------------------------------

    abstract fun widgetFormat() : WidgetFormat


    // -----------------------------------------------------------------------------------------
    // INTERNAL
    // -----------------------------------------------------------------------------------------

    protected fun addVariableToState(sheetId : SheetId, variable : Variable)
    {
        val sheetState = SheetManager.state(sheetId)

        if (sheetState != null)
            sheetState.addVariable(variable)
        else
            ApplicationLog.error(
                    SheetDoesNotExist(sheetId, Thread.currentThread().stackTrace.toString()))
    }

}


/**
 * Widget Name
 */
data class WidgetId(val value : String)
{

    companion object : Factory<WidgetId>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<WidgetId> = when (doc)
        {
            is DocText -> effValue(WidgetId(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }
}


/**
 * Widget Label
 */
data class WidgetLabel(val value : String?)
{

    companion object : Factory<WidgetLabel>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<WidgetLabel> = when (doc)
        {
            is DocText -> effValue(WidgetLabel(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }
}


/**
 * Action Widget
 */
data class ActionWidget(override val id : UUID,
                        val name : Prim<WidgetId>,
                        val format : Comp<ActionWidgetFormat>,
                        val modifier : Comp<NumberVariable>,
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
                effApply(::ActionWidget,
                         // Model Id
                         effValue(UUID.randomUUID()),
                         // Widget Name
                         doc.at("name") ap {
                             effApply(::Prim, WidgetId.fromDocument(it))
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

            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun name() : WidgetId = this.name.value

    fun format() : ActionWidgetFormat = this.format.value

    fun modifier() : NumberVariable = this.modifier.value


    // -----------------------------------------------------------------------------------------
    // WIDGET
    // -----------------------------------------------------------------------------------------

    override fun widgetFormat() : WidgetFormat = this.format().widgetFormat()

    override fun view(sheetContext: SheetContext): View {
        TODO("not implemented")
    }


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    // SHEET COMPONENT
    // -----------------------------------------------------------------------------------------

    override fun onSheetComponentActive(sheetContext: SheetContext) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}


/**
 * Boolean Widget
 */
data class BooleanWidget(override val id : UUID,
                         val widgetId : Prim<WidgetId>,
                         val format : Comp<BooleanWidgetFormat>,
                         val valueVariable : Comp<BooleanVariable>) : Widget()
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<Widget>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<Widget>  = when (doc)
        {
            is DocDict ->
            {
                effApply(::BooleanWidget,
                         // Model Id
                         effValue(UUID.randomUUID()),
                         // Widget Name
                         doc.at("name") ap {
                             effApply(::Prim, WidgetId.fromDocument(it))
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
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun widgetId() : WidgetId = this.widgetId.value

    fun format() : BooleanWidgetFormat = this.format.value

    fun valueVariable() : BooleanVariable = this.valueVariable.value


    // -----------------------------------------------------------------------------------------
    // WIDGET
    // -----------------------------------------------------------------------------------------

    override fun widgetFormat() : WidgetFormat = this.format().widgetFormat()

    override fun view(sheetContext: SheetContext): View {
        TODO("not implemented")
    }


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    // -----------------------------------------------------------------------------------------
    // SHEET COMPONENT
    // -----------------------------------------------------------------------------------------

    override fun onSheetComponentActive(sheetContext: SheetContext) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}


/**
 * Button Widget
 */
data class ButtonWidget(override val id : UUID,
                        val widgetId : Prim<WidgetId>,
                        val format : Comp<ButtonWidgetFormat>,
                        val viewType : Func<ButtonViewType>,
                        val label : Func<ButtonLabel>,
                        val description : Func<ButtonDescription>,
                        val icon : Func<ButtonIcon>) : Widget()
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<Widget>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<Widget>  = when (doc)
        {
            is DocDict ->
            {
                effApply(::ButtonWidget,
                         // Model Id
                         effValue(UUID.randomUUID()),
                         // Widget Name
                         doc.at("name") ap {
                             effApply(::Prim, WidgetId.fromDocument(it))
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

            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun widgetId() : WidgetId = this.widgetId.value

    fun format() : ButtonWidgetFormat = this.format.value


    // -----------------------------------------------------------------------------------------
    // WIDGET
    // -----------------------------------------------------------------------------------------

    override fun widgetFormat(): WidgetFormat = this.format().widgetFormat()

    override fun view(sheetContext: SheetContext): View {
        TODO("not implemented")
    }

    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    // SHEET COMPONENT
    // -----------------------------------------------------------------------------------------

    override fun onSheetComponentActive(sheetContext: SheetContext) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}


/**
 * Expander Widget
 */
data class ExpanderWidget(override val id : UUID,
                          val widgetId : Prim<WidgetId>,
                          val format : Comp<ExpanderWidgetFormat>,
                          val label : Func<ExpanderLabel>,
                          val groups: Coll<Group>) : Widget()
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<Widget>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<Widget>  = when (doc)
        {
            is DocDict ->
            {
                effApply(::ExpanderWidget,
                         // Model Id
                         effValue(UUID.randomUUID()),
                         // Widget Name
                         doc.at("name") ap {
                             effApply(::Prim, WidgetId.fromDocument(it))
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
                             effApply(::Coll, docList.mapIndexed {
                                 doc,index -> Group.fromDocument(doc,index) })
                         })
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }

    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun widgetId() : WidgetId = this.widgetId.value

    fun format() : ExpanderWidgetFormat = this.format.value


    // -----------------------------------------------------------------------------------------
    // WIDGET
    // -----------------------------------------------------------------------------------------

    override fun widgetFormat() : WidgetFormat = this.format().widgetFormat()

    override fun view(sheetContext: SheetContext): View {
        TODO("not implemented")
    }

    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    // -----------------------------------------------------------------------------------------
    // SHEET COMPONENT
    // -----------------------------------------------------------------------------------------

    override fun onSheetComponentActive(sheetContext: SheetContext) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}


/**
 * Image Widget
 */
data class ImageWidget(override val id : UUID,
                       val widgetId : Prim<WidgetId>,
                       val format : Comp<ImageWidgetFormat>) : Widget()
{

    companion object : Factory<Widget>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<Widget>  = when (doc)
        {
            is DocDict ->
            {
                effApply(::ImageWidget,
                         // Model Id
                         effValue(UUID.randomUUID()),
                         // Widget Name
                         doc.at("name") ap {
                             effApply(::Prim, WidgetId.fromDocument(it))
                         },
                         // Format
                         doc.at("format") ap {
                             effApply(::Comp, ImageWidgetFormat.fromDocument(it))
                         })
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun widgetId() : WidgetId = this.widgetId.value

    fun format() : ImageWidgetFormat = this.format.value


    // -----------------------------------------------------------------------------------------
    // WIDGET
    // -----------------------------------------------------------------------------------------

    override fun widgetFormat() : WidgetFormat = this.format().widgetFormat()

    override fun view(sheetContext: SheetContext): View {
        TODO("not implemented")
    }

    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    // -----------------------------------------------------------------------------------------
    // SHEET COMPONENT
    // -----------------------------------------------------------------------------------------

    override fun onSheetComponentActive(sheetContext: SheetContext) {
        TODO("not implemented")
    }

}


/**
 * List Widget
 */
data class ListWidget(override val id : UUID,
                      val widgetId : Prim<WidgetId>,
                      val format : Comp<ListWidgetFormat>,
                      val valueSetId: Func<ValueSetId>,
                      val values : Coll<Variable>) : Widget()
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<Widget>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<Widget>  = when (doc)
        {
            is DocDict ->
            {
                effApply(::ListWidget,
                         // Model Id
                         effValue(UUID.randomUUID()),
                         // Widget Name
                         doc.at("name") ap {
                             effApply(::Prim, WidgetId.fromDocument(it))
                         },
                         // Format
                         doc.at("format") ap {
                             effApply(::Comp, ListWidgetFormat.fromDocument(it))
                         },
                         // ValueSet Name
                         doc.at("value_set_name") ap {
                             effApply(::Prim, ValueSetId.fromDocument(it))
                         },
                         // Groups
                         doc.list("values") ap { docList ->
                             effApply(::Coll,
                                 docList.map { Variable.fromDocument(it) })
                         })
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun widgetId() : WidgetId = this.widgetId.value

    fun format() : ListWidgetFormat = this.format.value


    // -----------------------------------------------------------------------------------------
    // WIDGET
    // -----------------------------------------------------------------------------------------

    override fun widgetFormat() : WidgetFormat = this.format().widgetFormat()

    override fun view(sheetContext: SheetContext): View {
        TODO("not implemented")
    }


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    // -----------------------------------------------------------------------------------------
    // SHEET COMPONENT
    // -----------------------------------------------------------------------------------------

    override fun onSheetComponentActive(sheetContext: SheetContext) {
        TODO("not implemented")
    }

}


/**
 * Log Widget
 */
data class LogWidget(override val id : UUID,
                     val widgetId : Prim<WidgetId>,
                     val format : Comp<LogWidgetFormat>,
                     val entries : Coll<LogEntry>) : Widget()
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<LogWidget>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<LogWidget>  = when (doc)
        {
            is DocDict ->
            {
                effApply(::LogWidget,
                         // Model Id
                         effValue(UUID.randomUUID()),
                         // Widget Name
                         doc.at("name") ap {
                             effApply(::Prim, WidgetId.fromDocument(it))
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
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }

    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun widgetId() : WidgetId = this.widgetId.value

    fun format() : LogWidgetFormat = this.format.value


    // -----------------------------------------------------------------------------------------
    // WIDGET
    // -----------------------------------------------------------------------------------------

    override fun widgetFormat() : WidgetFormat = this.format().widgetFormat()

    override fun view(sheetContext: SheetContext): View {
        TODO("not implemented")
    }


    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    // SHEET COMPONENT
    // -----------------------------------------------------------------------------------------

    override fun onSheetComponentActive(sheetContext: SheetContext) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}


/**
 * Mechanic Widget
 */
data class MechanicWidget(override val id : UUID,
                          val widgetId : Prim<WidgetId>,
                          val format : Comp<MechanicWidgetFormat>,
                          val category : Func<MechanicCategory>) : Widget()
{

    companion object : Factory<MechanicWidget>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<MechanicWidget>  = when (doc)
        {
            is DocDict ->
            {
                effApply(::MechanicWidget,
                         // Model Id
                         effValue(UUID.randomUUID()),
                         // Widget Id
                         doc.at("id") ap {
                             effApply(::Prim, WidgetId.fromDocument(it))
                         },
                         // Format
                         doc.at("format") ap {
                             effApply(::Comp, MechanicWidgetFormat.fromDocument(it))
                         },
                         // Category
                         doc.at("category") ap {
                             effApply(::Prim, MechanicCategory.fromDocument(it))
                         })
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun widgetId() : WidgetId = this.widgetId.value

    fun format() : MechanicWidgetFormat = this.format.value


    // -----------------------------------------------------------------------------------------
    // WIDGET
    // -----------------------------------------------------------------------------------------

    override fun widgetFormat() : WidgetFormat = this.format().widgetFormat()

    override fun view(sheetContext: SheetContext): View {
        TODO("not implemented")
    }


    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    // SHEET COMPONENT
    // -----------------------------------------------------------------------------------------

    override fun onSheetComponentActive(sheetContext: SheetContext) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}


/**
 * Number Widget
 */
data class NumberWidget(override val id : UUID,
                        val widgetId : Prim<WidgetId>,
                        val format : Comp<NumberWidgetFormat>,
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
                effApply(::NumberWidget,
                         // Model Id
                         effValue(UUID.randomUUID()),
                         // Widget Id
                         doc.at("id") ap {
                             effApply(::Prim, WidgetId.fromDocument(it))
                         },
                         // Format
                         split(doc.maybeAt("format"),
                               effValue(Comp(NumberWidgetFormat.default())),
                               { effApply(::Comp, NumberWidgetFormat.fromDocument(it))}),
                         // Value
                         doc.at("value") ap {
                             effApply(::Comp, NumberVariable.fromDocument(it))
                         },
                         // Value Prefix
                         split(doc.maybeText("value_prefix"),
                               nullEff<String>(),
                               { effValue(Prim(it)) }),
                         // Value Prefix
                         split(doc.maybeText("value_postfix"),
                               nullEff<String>(),
                               { effValue(Prim(it)) }),
                         // Description
                         split(doc.maybeText("description"),
                               nullEff<String>(),
                               { effValue(Prim(it)) }),
                         // Variables
                         doc.list("variables") ap { docList ->
                             effApply(::Coll,
                                 docList.map { Variable.fromDocument(it) })
                         })
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun widgetId() : WidgetId = this.widgetId.value

    fun format() : NumberWidgetFormat = this.format.value


    // -----------------------------------------------------------------------------------------
    // WIDGET
    // -----------------------------------------------------------------------------------------

    override fun widgetFormat() : WidgetFormat = this.format().widgetFormat()

    override fun view(sheetContext: SheetContext): View {
        TODO("not implemented")
    }


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    // -----------------------------------------------------------------------------------------
    // SHEET COMPONENT
    // -----------------------------------------------------------------------------------------

    override fun onSheetComponentActive(sheetContext: SheetContext) {
        TODO("not implemented")
    }

}


/**
 * Option Widget
 */
data class OptionWidget(override val id : UUID,
                        val name : Func<WidgetId>,
                        val format : Func<OptionWidgetFormat>,
                        val viewType : Func<OptionViewType>,
                        val description : Func<OptionDescription>,
                        val valueSet : Func<ValueSetId>) : Widget()
{

    companion object : Factory<OptionWidget>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<OptionWidget>  = when (doc)
        {
            is DocDict ->
            {
                effApply(::OptionWidget,
                         // Model Id
                         effValue(UUID.randomUUID()),
                         // Widget Name
                         doc.at("name") ap {
                             effApply(::Prim, WidgetId.fromDocument(it))
                         },
                         // Format
                         split(doc.maybeAt("format"),
                               nullEff<OptionWidgetFormat>(),
                               { effApply(::Comp, OptionWidgetFormat.fromDocument(it)) }),
                         // View Type
                         split(doc.maybeEnum<OptionViewType>("view_type"),
                               nullEff<OptionViewType>(),
                               { effValue(Prim(it)) }),
                         // Description
                         split(doc.maybeAt("description"),
                               nullEff<OptionDescription>(),
                                 { effApply(::Prim, OptionDescription.fromDocument(it))}),
                         // ValueSet Name
                         split(doc.maybeAt("value_set_name"),
                               nullEff<ValueSetId>(),
                               { effApply(::Prim, ValueSetId.fromDocument(it))})
                        )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun widgetId() : WidgetId = this.widgetId.value

    fun format() : NumberWidgetFormat = this.format.value


    // -----------------------------------------------------------------------------------------
    // WIDGET
    // -----------------------------------------------------------------------------------------

    override fun widgetFormat() : WidgetFormat = this.format().widgetFormat()

    override fun view(sheetContext: SheetContext): View {
        TODO("not implemented")
    }


    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    // SHEET COMPONENT
    // -----------------------------------------------------------------------------------------

    override fun onSheetComponentActive(sheetContext: SheetContext) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}


/**
 * Quote Widget
 */
data class QuoteWidget(override val id : UUID,
                       val widgetId : Prim<WidgetId>,
                       val format : Comp<QuoteWidgetFormat>,
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
                effApply(::QuoteWidget,
                         // Model Id
                         effValue(UUID.randomUUID()),
                         // Widget Name
                         doc.at("name") ap {
                             effApply(::Prim, WidgetId.fromDocument(it))
                         },
                         // Format
                         split(doc.maybeAt("format"),
                               nullEff<QuoteWidgetFormat>(),
                               { effApply(::Comp, QuoteWidgetFormat.fromDocument(it)) }),
                         // View Type
                         split(doc.maybeEnum<QuoteViewType>("view_type"),
                               nullEff<QuoteViewType>(),
                               { effValue(Prim(it)) }),
                         // Quote
                         split(doc.maybeAt("quote"),
                               nullEff<Quote>(),
                               { effApply(::Prim, Quote.fromDocument(it)) }),
                         // Quote Source
                         split(doc.maybeAt("source"),
                               nullEff<QuoteSource>(),
                               { effApply(::Prim, QuoteSource.fromDocument(it)) })
                        )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun widgetId() : WidgetId = this.widgetId.value

    fun format() : QuoteWidgetFormat = this.format.value


    // -----------------------------------------------------------------------------------------
    // WIDGET
    // -----------------------------------------------------------------------------------------

    override fun widgetFormat() : WidgetFormat = this.format().widgetFormat()


    override fun view(sheetContext: SheetContext): View {
        TODO("not implemented")
    }


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    // -----------------------------------------------------------------------------------------
    // SHEET COMPONENT
    // -----------------------------------------------------------------------------------------

    override fun onSheetComponentActive(sheetContext: SheetContext) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}


/**
 * Table Widget
 */
data class TableWidget(override val id : UUID,
                       val name : Func<WidgetId>,
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
                effApply(::TableWidget,
                         // Model Id
                         effValue(UUID.randomUUID()),
                         // Widget Name
                         doc.at("name") ap {
                             effApply(::Prim, WidgetId.fromDocument(it))
                         },
                         // Format
                         split(doc.maybeAt("format"),
                               nullEff<TableWidgetFormat>(),
                               { effApply(::Comp, TableWidgetFormat.fromDocument(it)) }),
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
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    // SHEET COMPONENT
    // -----------------------------------------------------------------------------------------

    override fun onSheetComponentActive(sheetContext: SheetContext) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}


/**
 * Tab Widget
 */
data class TabWidget(override val id : UUID,
                     val name : Func<WidgetId>,
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
                effApply(::TabWidget,
                         // Model Id
                         effValue(UUID.randomUUID()),
                         // Widget Name
                         doc.at("name") ap {
                             effApply(::Prim, WidgetId.fromDocument(it))
                         },
                         // Format
                         split(doc.maybeAt("format"),
                               nullEff<TabWidgetFormat>(),
                               { effApply(::Comp, TabWidgetFormat.fromDocument(it)) }),
                         // Tabs
                         doc.list("tabs") ap { docList ->
                             effApply(::Coll,
                                     docList.map { Tab.fromDocument(it) })
                         },
                         // Default Selected
                         split(doc.maybeInt("default_selected"),
                               nullEff<Int>(),
                               { effValue(Prim(it)) })
                         )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    // SHEET COMPONENT
    // -----------------------------------------------------------------------------------------

    override fun onSheetComponentActive(sheetContext: SheetContext) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}


/**
 * Text Widget
 */
data class TextWidget(override val id : UUID,
                      val widgetId : Prim<WidgetId>,
                      val format : Comp<TextWidgetFormat>,
                      val description : Prim<TextDescription>,
                      val valueVariable : Comp<TextVariable>,
                      val variables : Coll<Variable>) : Widget()
{

    companion object : Factory<TextWidget>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<TextWidget>  = when (doc)
        {
            is DocDict ->
            {
                effApply(::TextWidget,
                         // Model Id
                         effValue(UUID.randomUUID()),
                         // Widget Id
                         doc.at("id") ap {
                             effApply(::Prim, WidgetId.fromDocument(it))
                         },
                         // Format
                         split(doc.maybeAt("format"),
                               nullEff<TextWidgetFormat>(),
                               { effApply(::Comp, TextWidgetFormat.fromDocument(it)) }),
                         // Description
                         split(doc.maybeAt("description"),
                               nullEff<TextDescription>(),
                               { effApply(::Prim, TextDescription.fromDocument(it)) }),
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
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun widgetId() : WidgetId = this.widgetId.value

    fun format() : TextWidgetFormat = this.format.value

    fun description() : TextDescription = this.description.value

    fun valueVariable() : TextVariable = this.valueVariable.value

    fun variables() : List<Variable> = this.variables.list



    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    // -----------------------------------------------------------------------------------------
    // SHEET COMPONENT
    // -----------------------------------------------------------------------------------------

    override fun onSheetComponentActive(sheetContext : SheetContext) =
        this.addVariableToState(sheetContext.sheetId, this.valueVariable())

}



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
