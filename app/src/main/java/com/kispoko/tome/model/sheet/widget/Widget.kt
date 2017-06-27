
package com.kispoko.tome.model.sheet.widget


import android.content.Context
import android.view.View
import android.widget.LinearLayout
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.*
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.lib.ui.LinearLayoutBuilder
import com.kispoko.tome.model.game.engine.mechanic.MechanicCategory
import com.kispoko.tome.model.game.engine.value.ValueSetId
import com.kispoko.tome.model.game.engine.variable.*
import com.kispoko.tome.model.sheet.SheetId
import com.kispoko.tome.model.sheet.group.Group
import com.kispoko.tome.model.sheet.widget.table.TableWidgetColumn
import com.kispoko.tome.model.sheet.widget.table.TableWidgetRow
import com.kispoko.tome.rts.sheet.SheetComponent
import com.kispoko.tome.rts.sheet.SheetContext
import com.kispoko.tome.rts.sheet.SheetManager
import effect.*
import effect.Nothing
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

    abstract fun view(sheetContext : SheetContext) : View


    // -----------------------------------------------------------------------------------------
    // INTERNAL
    // -----------------------------------------------------------------------------------------

    protected fun addVariableToState(sheetId : SheetId, variable : Variable)
    {
        val stateEff = SheetManager.state(sheetId)

        when (stateEff)
        {
            is Val -> stateEff.value.addVariable(variable)
            is Err -> ApplicationLog.error(stateEff.error)
        }
    }

}


object WidgetView
{

    fun layout(widgetFormat : WidgetFormat, context : Context) : LinearLayout
    {
        val layout = LinearLayoutBuilder()

        layout.orientation      = LinearLayout.VERTICAL
        layout.width            = 0
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.weight           = widgetFormat.width().toFloat()

//        layout.margin.left      = R.dimen.widget_margin_horz;
//        layout.margin.right     = R.dimen.widget_margin_horz;

        return layout.linearLayout(context)
    }

}


/**
 * Widget Name
 */
data class WidgetId(val value : String) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<WidgetId>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<WidgetId> = when (doc)
        {
            is DocText -> effValue(WidgetId(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value})

}


/**
 * Widget Label
 */
data class WidgetLabel(val value : String) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<WidgetLabel>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<WidgetLabel> = when (doc)
        {
            is DocText -> effValue(WidgetLabel(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value})

}


/**
 * Action Widget
 */
data class ActionWidget(override val id : UUID,
                        val widgetId : Prim<WidgetId>,
                        val format : Comp<ActionWidgetFormat>,
                        val modifier : Comp<NumberVariable>,
                        val description : Prim<ActionDescription>,
                        val descriptionHighlight : Prim<ActionDescriptionHighlight>,
                        val actionName : Prim<ActionName>,
                        val actionResult : Prim<ActionResult>) : Widget()
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.widgetId.name              = "widget_id"
        this.format.name                = "format"
        this.modifier.name              = "modifier"
        this.description.name           = "description"
        this.descriptionHighlight.name  = "description_highlight"
        this.actionName.name            = "action_name"
        this.actionResult.name          = "action_result"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

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

    fun widgetId() : WidgetId = this.widgetId.value

    fun format() : ActionWidgetFormat = this.format.value

    fun modifier() : NumberVariable = this.modifier.value


    // -----------------------------------------------------------------------------------------
    // WIDGET
    // -----------------------------------------------------------------------------------------

    override fun widgetFormat() : WidgetFormat = this.format().widgetFormat()

    override fun view(sheetContext : SheetContext) : View {
        TODO("not implemented")
    }


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name : String = "widget_action"

    override val modelObject = this


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
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.widgetId.name      = "widget_id"
        this.format.name        = "format"
        this.valueVariable.name = "value_variable"
    }


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

    override val name : String = "widget_boolean"

    override val modelObject = this


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
                        val viewType : Prim<ButtonViewType>,
                        val label : Prim<ButtonLabel>,
                        val description : Prim<ButtonDescription>,
                        val icon : Prim<ButtonIcon>) : Widget()
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.widgetId.name      = "widget_id"
        this.format.name        = "format"
        this.viewType.name      = "view_type"
        this.label.name         = "label"
        this.description.name   = "description"
        this.icon.name          = "icion"
    }


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
                         doc.at("view_type") ap {
                             effApply(::Prim, ButtonViewType.fromDocument(it))
                         },
                         // Label
                         doc.at("label") ap {
                             effApply(::Prim, ButtonLabel.fromDocument(it))
                         },
                         // Description
                         doc.at("description") ap {
                             effApply(::Prim, ButtonDescription.fromDocument(it))
                         },
                         // Icon
                         doc.at("icon") ap {
                             effApply(::Prim, ButtonIcon.fromDocument(it))
                         })
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

    override val name : String = "widget_button"

    override val modelObject = this


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
                          val label : Prim<ExpanderLabel>,
                          val groups: Coll<Group>) : Widget()
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.widgetId.name      = "widget_id"
        this.format.name        = "format"
        this.label.name         = "label"
        this.groups.name        = "groups"
    }


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

    override val name : String = "widget_expander"

    override val modelObject = this


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

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.widgetId.name  = "widget_id"
        this.format.name    = "format"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

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

    override val name : String = "widget_image"

    override val modelObject = this


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
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.widgetId.name      = "widget_id"
        this.format.name        = "format"
        this.valueSetId.name    = "value_set_id"
        this.values.name        = "values"
    }


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
                                 docList.mapMut { Variable.fromDocument(it) })
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

    override val name : String = "widget_list"

    override val modelObject = this


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
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.widgetId.name      = "widget_id"
        this.format.name        = "format"
        this.entries.name       = "entries"
    }


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
                                 docList.mapMut { LogEntry.fromDocument(it) })
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

    override val name : String = "widget_log"

    override val modelObject = this


    // -----------------------------------------------------------------------------------------
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

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.widgetId.name      = "widget_id"
        this.format.name        = "format"
        this.category.name      = "category"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

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


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name : String = "widget_mechanic"

    override val modelObject = this


    // -----------------------------------------------------------------------------------------
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
                        val valueVariable : Comp<NumberVariable>,
                        val description : Maybe<Prim<NumberWidgetDescription>>,
                        val valuePrefix : Maybe<Prim<NumberWidgetValuePrefix>>,
                        val valuePostfix : Maybe<Prim<NumberWidgetValuePostfix>>,
                        val variables : Conj<Variable>) : Widget()
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.widgetId.name                          = "widget_id"
        this.format.name                            = "format"
        this.valueVariable.name                     = "value_variable"

        when (this.description) {
            is Just -> this.description.value.name = "description"
        }

        when (this.valuePrefix) {
            is Just -> this.valuePrefix.value.name = "value_prefix"
        }

        when (this.valuePostfix) {
            is Just -> this.valuePostfix.value.name = "value_postfix"
        }

        this.variables.name                         = "variables"
    }


    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var viewId : Int? = null


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(widgetId : WidgetId,
                format : NumberWidgetFormat,
                valueVariable : NumberVariable,
                description : Maybe<NumberWidgetDescription>,
                valuePrefix : Maybe<NumberWidgetValuePrefix>,
                valuePostfix : Maybe<NumberWidgetValuePostfix>,
                variables : MutableSet<Variable>)
        : this(UUID.randomUUID(),
               Prim(widgetId),
               Comp(format),
               Comp(valueVariable),
               maybeLiftPrim(description),
               maybeLiftPrim(valuePrefix),
               maybeLiftPrim(valuePostfix),
               Conj(variables))


    companion object : Factory<NumberWidget>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<NumberWidget>  = when (doc)
        {
            is DocDict ->
            {
                effApply(::NumberWidget,
                         // Widget Id
                         doc.at("id") ap { WidgetId.fromDocument(it) },
                         // Format
                         split(doc.maybeAt("format"),
                               effValue(NumberWidgetFormat.default()),
                               { NumberWidgetFormat.fromDocument(it) }),
                         // Value
                         doc.at("value_variable") ap { NumberVariable.fromDocument(it) },
                         // Description
                         split(doc.maybeAt("description"),
                               effValue<ValueError,Maybe<NumberWidgetDescription>>(Nothing()),
                               { effApply(::Just, NumberWidgetDescription.fromDocument(it)) }),
                         // Value Prefix
                         split(doc.maybeAt("value_prefix"),
                               effValue<ValueError,Maybe<NumberWidgetValuePrefix>>(Nothing()),
                               { effApply(::Just, NumberWidgetValuePrefix.fromDocument(it)) }),
                         // Value Postfix
                        split(doc.maybeAt("value_postfix"),
                                effValue<ValueError,Maybe<NumberWidgetValuePostfix>>(Nothing()),
                                { effApply(::Just, NumberWidgetValuePostfix.fromDocument(it)) }),
                         // Variables
                         split(doc.maybeList("tags"),
                               effValue<ValueError,MutableSet<Variable>>(mutableSetOf()),
                               { it.mapSetMut { Variable.fromDocument(it)} })
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

    fun valueVariable() : NumberVariable = this.valueVariable.value

    fun description() : String? = getMaybePrim(this.description)?.value

    fun valuePrefix() : String? = getMaybePrim(this.valuePrefix)?.value

    fun valuePostfix() : String? = getMaybePrim(this.valuePostfix)?.value

    fun variables() : Set<Variable> = this.variables.set


    // -----------------------------------------------------------------------------------------
    // WIDGET
    // -----------------------------------------------------------------------------------------

    override fun widgetFormat() : WidgetFormat = this.format().widgetFormat()

    override fun view(sheetContext : SheetContext): View =
            NumberWidgetView.view(this, this.format(), sheetContext)


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name : String = "widget_number"

    override val modelObject = this


    // -----------------------------------------------------------------------------------------
    // SHEET COMPONENT
    // -----------------------------------------------------------------------------------------

    override fun onSheetComponentActive(sheetContext : SheetContext) =
        this.addVariableToState(sheetContext.sheetId, this.valueVariable())


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    /**
     * The string representation of the widget's current value. This method returns 0 when the
     * value is null for some reason.
     */
    fun valueString(sheetContext : SheetContext) : String
    {
        val numberEff = this.valueVariable().value(sheetContext)
        when (numberEff)
        {
            is Val ->
            {
                val number = numberEff.value
                if ((number == Math.floor(number)))
                    return number.toInt().toString()
                return number.toString()
            }
            is Err -> return "0"
        }
    }

}


/**
 * Option Widget
 */
data class OptionWidget(override val id : UUID,
                        val widgetId : Prim<WidgetId>,
                        val format : Comp<OptionWidgetFormat>,
                        val viewType : Prim<OptionViewType>,
                        val description : Maybe<Prim<OptionDescription>>,
                        val valueSet : Prim<ValueSetId>) : Widget()
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.widgetId.name                          = "widget_id"
        this.format.name                            = "format"
        this.viewType.name                          = "view_type"

        when (this.description) {
            is Just -> this.description.value.name  = "description"
        }

        this.valueSet.name                          = "value_set"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(widgetId : WidgetId,
                format : OptionWidgetFormat,
                viewType : OptionViewType,
                description : Maybe<OptionDescription>,
                valueSet : ValueSetId)
        : this(UUID.randomUUID(),
                Prim(widgetId),
                Comp(format),
                Prim(viewType),
                maybeLiftPrim(description),
                Prim(valueSet))



    companion object : Factory<OptionWidget>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<OptionWidget>  = when (doc)
        {
            is DocDict ->
            {
                effApply(::OptionWidget,
                         // Widget Id
                         doc.at("name") ap { WidgetId.fromDocument(it) },
                         // Format
                         split(doc.maybeAt("format"),
                               effValue(OptionWidgetFormat.default),
                               { OptionWidgetFormat.fromDocument(it) }),
                         // View Type
                         split(doc.maybeAt("view_type"),
                               effValue<ValueError,OptionViewType>(OptionViewType.NoArrows),
                               { OptionViewType.fromDocument(it) }),
                         // Description
                         split(doc.maybeAt("description"),
                               effValue<ValueError,Maybe<OptionDescription>>(Nothing()),
                               { effApply(::Just, OptionDescription.fromDocument(it)) }),
                         // ValueSet Name
                         doc.at("value_set_id") ap { ValueSetId.fromDocument(it) }
                         )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun widgetId() : WidgetId = this.widgetId.value

    fun format() : OptionWidgetFormat = this.format.value


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

    override val name : String = "widget_option"

    override val modelObject = this


    // -----------------------------------------------------------------------------------------
    // SHEET COMPONENT
    // -----------------------------------------------------------------------------------------

    override fun onSheetComponentActive(sheetContext: SheetContext) {
        TODO("not implemented")
    }

}


/**
 * Quote Widget
 */
data class QuoteWidget(override val id : UUID,
                       val widgetId : Prim<WidgetId>,
                       val format : Comp<QuoteWidgetFormat>,
                       val viewType : Prim<QuoteViewType>,
                       val quote : Prim<Quote>,
                       val source : Maybe<Prim<QuoteSource>>) : Widget()
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.widgetId.name                      = "widget_id"
        this.format.name                        = "format"
        this.viewType.name                      = "view_type"
        this.quote.name                         = "quote"

        when (this.source) {
            is Just -> this.source.value.name   = "source"
        }

    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(widgetId : WidgetId,
                format   : QuoteWidgetFormat,
                viewType : QuoteViewType,
                quote    : Quote,
                source   : Maybe<QuoteSource>)
        : this(UUID.randomUUID(),
               Prim(widgetId),
               Comp(format),
               Prim(viewType),
               Prim(quote),
               maybeLiftPrim(source))


    companion object : Factory<QuoteWidget>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<QuoteWidget>  = when (doc)
        {
            is DocDict ->
            {
                effApply(::QuoteWidget,
                         // Widget Id
                         doc.at("id") ap { WidgetId.fromDocument(it) },
                         // Format
                         split(doc.maybeAt("format"),
                               effValue(QuoteWidgetFormat.default),
                               { QuoteWidgetFormat.fromDocument(it) }),
                         // View Type
                         split(doc.maybeAt("view_type"),
                               effValue<ValueError,QuoteViewType>(QuoteViewType.NoIcon),
                               { QuoteViewType.fromDocument(it) }),
                         // Quote
                         doc.at("quote") ap { Quote.fromDocument(it) },
                         // Quote Source
                         split(doc.maybeAt("source"),
                               effValue<ValueError,Maybe<QuoteSource>>(Nothing()),
                               { effApply(::Just, QuoteSource.fromDocument(it)) })
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

    override val name : String = "widget_quote"

    override val modelObject = this


    // -----------------------------------------------------------------------------------------
    // SHEET COMPONENT
    // -----------------------------------------------------------------------------------------

    override fun onSheetComponentActive(sheetContext: SheetContext) {
        TODO("not implemented")
    }

}


/**
 * Table Widget
 */
data class TableWidget(override val id : UUID,
                       val widgetId : Prim<WidgetId>,
                       val format : Comp<TableWidgetFormat>,
                       val columns : Coll<TableWidgetColumn>,
                       val rows : Coll<TableWidgetRow>) : Widget()
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.widgetId.name      = "widget_id"
        this.format.name        = "format"
        this.columns.name       = "columns"
        this.rows.name          = "rows"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(widgetId : WidgetId,
                format : TableWidgetFormat,
                columns : MutableList<TableWidgetColumn>,
                rows : MutableList<TableWidgetRow>)
        : this(UUID.randomUUID(),
               Prim(widgetId),
               Comp(format),
               Coll(columns),
               Coll(rows))


    companion object : Factory<TableWidget>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<TableWidget>  = when (doc)
        {
            is DocDict ->
            {
                effApply(::TableWidget,
                         // Widget Id
                         doc.at("id") ap { WidgetId.fromDocument(it) },
                         // Format
                         split(doc.maybeAt("format"),
                               effValue(TableWidgetFormat.default),
                               { TableWidgetFormat.fromDocument(it) }),
                         // Columns
                         doc.list("columns") ap { docList ->
                             docList.mapMut { TableWidgetColumn.fromDocument(it) }
                         },
                         // Rows
                         doc.list("rows") ap { docList ->
                             docList.mapMut { TableWidgetRow.fromDocument(it) }
                         })
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun widgetId() : WidgetId = this.widgetId.value

    fun format() : TableWidgetFormat = this.format.value

    fun columns() : List<TableWidgetColumn> = this.columns.list

    fun rows() : List<TableWidgetRow> = this.rows.list


    // -----------------------------------------------------------------------------------------
    // WIDGET
    // -----------------------------------------------------------------------------------------

    override fun widgetFormat() : WidgetFormat = this.format().widgetFormat()


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name : String = "widget_table"

    override val modelObject = this


    // -----------------------------------------------------------------------------------------
    // SHEET COMPONENT
    // -----------------------------------------------------------------------------------------

    override fun onSheetComponentActive(sheetContext: SheetContext) {
        // TODO("not implemented")

        // this.addVariableToState(sheetContext.sheetId, this.valueVariable())
    }


    override fun view(sheetContext : SheetContext) : View =
            TableWidgetView.view(this, this.format(), sheetContext)


}


/**
 * Tab Widget
 */
data class TabWidget(override val id : UUID,
                     val widgetId : Prim<WidgetId>,
                     val format : Comp<TabWidgetFormat>,
                     val tabs : Coll<Tab>,
                     val defaultSelected : Prim<DefaultSelected>) : Widget()
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.widgetId.name          = "widget_id"
        this.format.name            = "format"
        this.tabs.name              = "tabs"
        this.defaultSelected.name   = "default_selected"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(widgetId : WidgetId,
                format : TabWidgetFormat,
                tabs : MutableList<Tab>,
                defaultSelected : DefaultSelected)
        : this(UUID.randomUUID(),
               Prim(widgetId),
               Comp(format),
               Coll(tabs),
               Prim(defaultSelected))


    companion object : Factory<TabWidget>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<TabWidget>  = when (doc)
        {
            is DocDict ->
            {
                effApply(::TabWidget,
                         // Widget Id
                         doc.at("id") ap { WidgetId.fromDocument(it) },
                         // Format
                         split(doc.maybeAt("format"),
                               effValue(TabWidgetFormat.default),
                               { TabWidgetFormat.fromDocument(it) }),
                         // Tabs
                         doc.list("tabs") ap { docList ->
                             docList.mapMut { Tab.fromDocument(it) }
                         },
                         // Default Selected
                         split(doc.maybeAt("default_selected"),
                               effValue(DefaultSelected(1)),
                               { DefaultSelected.fromDocument(it) })
                         )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun widgetId() : WidgetId = this.widgetId.value

    fun format() : TabWidgetFormat = this.format.value


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

    override val name : String = "widget_tab"

    override val modelObject = this


    // -----------------------------------------------------------------------------------------
    // SHEET COMPONENT
    // -----------------------------------------------------------------------------------------

    override fun onSheetComponentActive(sheetContext: SheetContext) {
        TODO("not implemented")
    }

}


/**
 * Text Widget
 */
data class TextWidget(override val id : UUID,
                      val widgetId : Prim<WidgetId>,
                      val format : Comp<TextWidgetFormat>,
                      val description : Maybe<Prim<TextWidgetDescription>>,
                      val valueVariable : Comp<TextVariable>,
                      val variables : Conj<Variable>) : Widget()
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.widgetId.name                          = "widget_id"
        this.format.name                            = "format"

        when (this.description) {
            is Just -> this.description.value.name  = "description"
        }

        this.valueVariable.name                     = "value_variable"

        this.variables.name                         = "variables"
    }


    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var viewId : Int? = null


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(widgetId: WidgetId,
                format : TextWidgetFormat,
                description : Maybe<TextWidgetDescription>,
                valueVariable : TextVariable,
                variables : MutableSet<Variable>)
        : this(UUID.randomUUID(),
               Prim(widgetId),
               Comp(format),
               maybeLiftPrim(description),
               Comp(valueVariable),
               Conj(variables))


    companion object : Factory<TextWidget>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<TextWidget>  = when (doc)
        {
            is DocDict ->
            {
                effApply(::TextWidget,
                         // Widget Id
                         doc.at("id") ap { WidgetId.fromDocument(it) },
                         // Format
                         split(doc.maybeAt("format"),
                               effValue(TextWidgetFormat.default),
                               { TextWidgetFormat.fromDocument(it) }),
                         // Description
                         split(doc.maybeAt("description"),
                               effValue<ValueError,Maybe<TextWidgetDescription>>(Nothing()),
                               { effApply(::Just, TextWidgetDescription.fromDocument(it)) }),
                         // Value
                         doc.at("value_variable") ap { TextVariable.fromDocument(it) },
                         // Variables
                         split(doc.maybeList("tags"),
                               effValue<ValueError,MutableSet<Variable>>(mutableSetOf()),
                               { it.mapSetMut { Variable.fromDocument(it)} })
                         )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun widgetId() : WidgetId = this.widgetId.value

    fun format() : TextWidgetFormat = this.format.value

    fun description() : TextWidgetDescription? = getMaybePrim(this.description)

    fun valueVariable() : TextVariable = this.valueVariable.value

    fun variables() : Set<Variable> = this.variables.set


    // -----------------------------------------------------------------------------------------
    // WIDGET
    // -----------------------------------------------------------------------------------------

    override fun widgetFormat() : WidgetFormat = this.format().widgetFormat()


    override fun view(sheetContext : SheetContext) : View =
        TextWidgetView.view(this, this.format(), sheetContext)


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name : String = "widget_text"

    override val modelObject = this


    // -----------------------------------------------------------------------------------------
    // SHEET COMPONENT
    // -----------------------------------------------------------------------------------------

    override fun onSheetComponentActive(sheetContext : SheetContext) =
        this.addVariableToState(sheetContext.sheetId, this.valueVariable())


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    /**
     * The string representation of the widget's current value. This method returns 0 when the
     * value is null for some reason.
     */
    fun valueString(sheetContext : SheetContext) : String
    {
        val stringEff = this.valueVariable().value(sheetContext)
        when (stringEff)
        {
            is Val -> return stringEff.value
            is Err -> return ""
        }
    }

}



