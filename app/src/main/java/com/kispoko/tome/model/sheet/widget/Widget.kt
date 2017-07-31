
package com.kispoko.tome.model.sheet.widget


import android.view.View
import android.widget.LinearLayout
import com.kispoko.tome.app.AppEff
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
import com.kispoko.tome.model.sheet.widget.table.TableWidgetTextCell
import com.kispoko.tome.rts.sheet.SheetComponent
import com.kispoko.tome.rts.sheet.SheetContext
import com.kispoko.tome.rts.sheet.SheetUIContext
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
sealed class Widget(open val variables : Conj<Variable>) : Model, SheetComponent, Serializable
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
                    "widget_points"   -> PointsWidget.fromDocument(doc)
                                            as ValueParser<Widget>
                    "widget_quote"    -> QuoteWidget.fromDocument(doc)
                                            as ValueParser<Widget>
                    "widget_story"    -> StoryWidget.fromDocument(doc)
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

    abstract fun view(sheetUIContext: SheetUIContext) : View

    fun variables() : Set<Variable> = this.variables.value


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

    fun layout(widgetFormat : WidgetFormat, sheetUIContext: SheetUIContext) : LinearLayout
    {
        val layout = LinearLayoutBuilder()

        layout.orientation      = LinearLayout.VERTICAL
        layout.width            = 0
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.weight           = widgetFormat.width().toFloat()

        layout.marginSpacing    = widgetFormat.margins()
        layout.paddingSpacing   = widgetFormat.padding()

        layout.backgroundColor  = SheetManager.color(sheetUIContext.sheetId,
                                                     widgetFormat.backgroundColorTheme())

        layout.corners          = widgetFormat.corners()

        return layout.linearLayout(sheetUIContext.context)
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
                        val actionResult : Prim<ActionResult>,
                        override val variables : Conj<Variable>) : Widget(variables)
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
        this.variables.name             = "variables"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(widgetId : WidgetId,
                format : ActionWidgetFormat,
                modifier : NumberVariable,
                description : ActionDescription,
                descriptionHighlight : ActionDescriptionHighlight,
                actionName : ActionName,
                actionResult : ActionResult,
                variables : MutableSet<Variable>)
        : this(UUID.randomUUID(),
               Prim(widgetId),
               Comp(format),
               Comp(modifier),
               Prim(description),
               Prim(descriptionHighlight),
               Prim(actionName),
               Prim(actionResult),
               Conj(variables))


    companion object : Factory<Widget>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<Widget>  = when (doc)
        {
            is DocDict ->
            {
                effApply(::ActionWidget,
                         // Widget Name
                         doc.at("name") ap { WidgetId.fromDocument(it) },
                         // Format
                         doc.at("format") ap { ActionWidgetFormat.fromDocument(it) },
                         // Modifier
                         doc.at("modifier") ap { NumberVariable.fromDocument(it) },
                         // Description
                         doc.at("description") ap { ActionDescription.fromDocument(it) },
                         // Description Highlight
                         doc.at("description_highlight") ap {
                             ActionDescriptionHighlight.fromDocument(it)
                         },
                         // Action Name
                         doc.at("action_name") ap { ActionName.fromDocument(it) },
                         // Action Result
                         doc.at("action_result") ap { ActionResult.fromDocument(it) },
                         // Variables
                         split(doc.maybeList("variables"),
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

    fun format() : ActionWidgetFormat = this.format.value

    fun modifier() : NumberVariable = this.modifier.value


    // -----------------------------------------------------------------------------------------
    // WIDGET
    // -----------------------------------------------------------------------------------------

    override fun widgetFormat() : WidgetFormat = this.format().widgetFormat()

    override fun view(sheetUIContext: SheetUIContext) : View {
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

    override fun onSheetComponentActive(sheetContext : SheetContext) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}


/**
 * Boolean Widget
 */
data class BooleanWidget(override val id : UUID,
                         val widgetId : Prim<WidgetId>,
                         val format : Comp<BooleanWidgetFormat>,
                         val valueVariable : Comp<BooleanVariable>,
                         override val variables : Conj<Variable>) : Widget(variables)
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.widgetId.name      = "widget_id"
        this.format.name        = "format"
        this.valueVariable.name = "value_variable"
        this.variables.name     = "variables"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(widgetId : WidgetId,
                format : BooleanWidgetFormat,
                valueVariable : BooleanVariable,
                variables : MutableSet<Variable>)
        : this(UUID.randomUUID(),
               Prim(widgetId),
               Comp(format),
               Comp(valueVariable),
               Conj(variables))


    companion object : Factory<Widget>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<Widget>  = when (doc)
        {
            is DocDict ->
            {
                effApply(::BooleanWidget,
                         // Widget Name
                         doc.at("name") ap { WidgetId.fromDocument(it) },
                         // Format
                         doc.at("format") ap { BooleanWidgetFormat.fromDocument(it) },
                         // Value
                         doc.at("value") ap { BooleanVariable.fromDocument(it) },
                         // Variables
                         split(doc.maybeList("variables"),
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

    fun format() : BooleanWidgetFormat = this.format.value

    fun valueVariable() : BooleanVariable = this.valueVariable.value


    // -----------------------------------------------------------------------------------------
    // WIDGET
    // -----------------------------------------------------------------------------------------

    override fun widgetFormat() : WidgetFormat = this.format().widgetFormat()

    override fun view(sheetUIContext: SheetUIContext): View {
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

    override fun onSheetComponentActive(sheetContext : SheetContext) {
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
                        val icon : Prim<ButtonIcon>,
                        override val variables : Conj<Variable>) : Widget(variables)
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
        this.icon.name          = "icon"
        this.variables.name     = "variables"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(widgetId : WidgetId,
                format : ButtonWidgetFormat,
                viewType : ButtonViewType,
                label : ButtonLabel,
                description : ButtonDescription,
                icon : ButtonIcon,
                variables : MutableSet<Variable>)
        : this(UUID.randomUUID(),
               Prim(widgetId),
               Comp(format),
               Prim(viewType),
               Prim(label),
               Prim(description),
               Prim(icon),
               Conj(variables))


    companion object : Factory<Widget>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<Widget>  = when (doc)
        {
            is DocDict ->
            {
                effApply(::ButtonWidget,
                         // Widget Name
                         doc.at("name") ap { WidgetId.fromDocument(it) },
                         // Format
                         doc.at("format") ap { ButtonWidgetFormat.fromDocument(it) },
                         // View Type
                         doc.at("view_type") ap { ButtonViewType.fromDocument(it) },
                         // Label
                         doc.at("label") ap { ButtonLabel.fromDocument(it) },
                         // Description
                         doc.at("description") ap { ButtonDescription.fromDocument(it) },
                         // Icon
                         doc.at("icon") ap { ButtonIcon.fromDocument(it) },
                         // Variables
                         split(doc.maybeList("variables"),
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

    fun format() : ButtonWidgetFormat = this.format.value


    // -----------------------------------------------------------------------------------------
    // WIDGET
    // -----------------------------------------------------------------------------------------

    override fun widgetFormat(): WidgetFormat = this.format().widgetFormat()

    override fun view(sheetUIContext: SheetUIContext): View {
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
                          val groups: Coll<Group>,
                          override val variables : Conj<Variable>) : Widget(variables)
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
        this.variables.name     = "variables"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(widgetId : WidgetId,
                format : ExpanderWidgetFormat,
                label : ExpanderLabel,
                groups : MutableList<Group>,
                variables : MutableSet<Variable>)
        : this(UUID.randomUUID(),
               Prim(widgetId),
               Comp(format),
               Prim(label),
               Coll(groups),
               Conj(variables))


    companion object : Factory<Widget>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<Widget>  = when (doc)
        {
            is DocDict ->
            {
                effApply(::ExpanderWidget,
                         // Widget Name
                         doc.at("name") ap { WidgetId.fromDocument(it) },
                         // Format
                         doc.at("format") ap { ExpanderWidgetFormat.fromDocument(it) },
                         // Label
                         doc.at("label") ap { ExpanderLabel.fromDocument(it) },
                         // Groups
                         doc.list("groups") ap { docList ->
                             docList.mapIndexed { d,index -> Group.fromDocument(d,index) }
                         },
                         // Variables
                         split(doc.maybeList("variables"),
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

    fun format() : ExpanderWidgetFormat = this.format.value


    // -----------------------------------------------------------------------------------------
    // WIDGET
    // -----------------------------------------------------------------------------------------

    override fun widgetFormat() : WidgetFormat = this.format().widgetFormat()

    override fun view(sheetUIContext: SheetUIContext): View {
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

    override fun onSheetComponentActive(sheetContext : SheetContext) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}


/**
 * Image Widget
 */
data class ImageWidget(override val id : UUID,
                       val widgetId : Prim<WidgetId>,
                       val format : Comp<ImageWidgetFormat>,
                       override val variables : Conj<Variable>) : Widget(variables)
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.widgetId.name  = "widget_id"
        this.format.name    = "format"
        this.variables.name = "variables"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(widgetId : WidgetId,
                format : ImageWidgetFormat,
                variables : MutableSet<Variable>)
        : this(UUID.randomUUID(),
               Prim(widgetId),
               Comp(format),
               Conj(variables))


    companion object : Factory<Widget>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<Widget>  = when (doc)
        {
            is DocDict ->
            {
                effApply(::ImageWidget,
                         // Widget Name
                         doc.at("name") ap { WidgetId.fromDocument(it) },
                         // Format
                         doc.at("format") ap { ImageWidgetFormat.fromDocument(it) },
                         // Variables
                         split(doc.maybeList("variables"),
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

    fun format() : ImageWidgetFormat = this.format.value


    // -----------------------------------------------------------------------------------------
    // WIDGET
    // -----------------------------------------------------------------------------------------

    override fun widgetFormat() : WidgetFormat = this.format().widgetFormat()

    override fun view(sheetUIContext: SheetUIContext): View {
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
                      val values : Coll<Variable>,
                      override val variables : Conj<Variable>) : Widget(variables)
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
        this.variables.name     = "variables"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(widgetId : WidgetId,
                foramt : ListWidgetFormat,
                valueSetId : ValueSetId,
                values : MutableList<Variable>,
                variables : MutableSet<Variable>)
        : this(UUID.randomUUID(),
               Prim(widgetId),
               Comp(foramt),
               Prim(valueSetId),
               Coll(values),
               Conj(variables))


    companion object : Factory<Widget>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<Widget>  = when (doc)
        {
            is DocDict ->
            {
                effApply(::ListWidget,
                         // Widget Name
                         doc.at("name") ap { WidgetId.fromDocument(it) },
                         // Format
                         doc.at("format") ap { ListWidgetFormat.fromDocument(it) },
                         // ValueSet Name
                         doc.at("value_set_name") ap { ValueSetId.fromDocument(it) },
                         // Groups
                         doc.list("values") ap { docList ->
                             docList.mapMut { Variable.fromDocument(it) }
                         },
                         // Variables
                         split(doc.maybeList("variables"),
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

    fun format() : ListWidgetFormat = this.format.value


    // -----------------------------------------------------------------------------------------
    // WIDGET
    // -----------------------------------------------------------------------------------------

    override fun widgetFormat() : WidgetFormat = this.format().widgetFormat()

    override fun view(sheetUIContext: SheetUIContext): View {
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
                     val entries : Coll<LogEntry>,
                     override val variables : Conj<Variable>) : Widget(variables)
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.widgetId.name      = "widget_id"
        this.format.name        = "format"
        this.entries.name       = "entries"
        this.variables.name     = "variables"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(widgetId : WidgetId,
                format : LogWidgetFormat,
                entries : MutableList<LogEntry>,
                variables : MutableSet<Variable>)
        : this(UUID.randomUUID(),
               Prim(widgetId),
               Comp(format),
               Coll(entries),
               Conj(variables))


    companion object : Factory<LogWidget>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<LogWidget>  = when (doc)
        {
            is DocDict ->
            {
                effApply(::LogWidget,
                         // Widget Name
                         doc.at("name") ap { WidgetId.fromDocument(it) },
                         // Format
                         doc.at("format") ap { LogWidgetFormat.fromDocument(it) },
                         // Entries
                         doc.list("entries") ap { docList ->
                             docList.mapMut { LogEntry.fromDocument(it) }
                         },
                         // Variables
                         split(doc.maybeList("variables"),
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

    fun format() : LogWidgetFormat = this.format.value


    // -----------------------------------------------------------------------------------------
    // WIDGET
    // -----------------------------------------------------------------------------------------

    override fun widgetFormat() : WidgetFormat = this.format().widgetFormat()

    override fun view(sheetUIContext: SheetUIContext): View {
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
                          val category : Func<MechanicCategory>,
                          override val variables : Conj<Variable>) : Widget(variables)
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.widgetId.name      = "widget_id"
        this.format.name        = "format"
        this.category.name      = "category"
        this.variables.name     = "variables"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(widgetId : WidgetId,
                format : MechanicWidgetFormat,
                category : MechanicCategory,
                variables : MutableSet<Variable>)
        : this(UUID.randomUUID(),
               Prim(widgetId),
               Comp(format),
               Prim(category),
               Conj(variables))


    companion object : Factory<MechanicWidget>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<MechanicWidget>  = when (doc)
        {
            is DocDict ->
            {
                effApply(::MechanicWidget,
                         // Widget Id
                         doc.at("id") ap { WidgetId.fromDocument(it) },
                         // Format
                         doc.at("format") ap { MechanicWidgetFormat.fromDocument(it) },
                         // Category
                         doc.at("category") ap { MechanicCategory.fromDocument(it) },
                         // Variables
                         split(doc.maybeList("variables"),
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

    fun format() : MechanicWidgetFormat = this.format.value


    // -----------------------------------------------------------------------------------------
    // WIDGET
    // -----------------------------------------------------------------------------------------

    override fun widgetFormat() : WidgetFormat = this.format().widgetFormat()

    override fun view(sheetUIContext: SheetUIContext): View {
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
                        override val variables : Conj<Variable>) : Widget(variables)
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
                variables : MutableSet<Variable>)
        : this(UUID.randomUUID(),
               Prim(widgetId),
               Comp(format),
               Comp(valueVariable),
               maybeLiftPrim(description),
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
                         // Variables
                         split(doc.maybeList("variables"),
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


    // -----------------------------------------------------------------------------------------
    // WIDGET
    // -----------------------------------------------------------------------------------------

    override fun widgetFormat() : WidgetFormat = this.format().widgetFormat()

    override fun view(sheetUIContext: SheetUIContext): View =
            NumberWidgetView.view(this, this.format(), sheetUIContext)


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
    fun valueString(sheetUIContext: SheetUIContext) : String
    {
        val numberEff = this.valueVariable().value(SheetContext(sheetUIContext))
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
                        val valueSet : Prim<ValueSetId>,
                        override val variables : Conj<Variable>) : Widget(variables)
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
        this.variables.name                         = "variables"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(widgetId : WidgetId,
                format : OptionWidgetFormat,
                viewType : OptionViewType,
                description : Maybe<OptionDescription>,
                valueSet : ValueSetId,
                variables : MutableSet<Variable>)
        : this(UUID.randomUUID(),
                Prim(widgetId),
                Comp(format),
                Prim(viewType),
                maybeLiftPrim(description),
                Prim(valueSet),
                Conj(variables))


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
                         doc.at("value_set_id") ap { ValueSetId.fromDocument(it) },
                         // Variables
                         split(doc.maybeList("variables"),
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

    fun format() : OptionWidgetFormat = this.format.value


    // -----------------------------------------------------------------------------------------
    // WIDGET
    // -----------------------------------------------------------------------------------------

    override fun widgetFormat() : WidgetFormat = this.format().widgetFormat()

    override fun view(sheetUIContext: SheetUIContext): View {
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

    override fun onSheetComponentActive(sheetContext : SheetContext) {
        TODO("not implemented")
    }

}


/**
 * Points Widget
 */
data class PointsWidget(override val id : UUID,
                        val widgetId : Prim<WidgetId>,
                        val format : Comp<PointsWidgetFormat>,
                        val limitValueVariableId : Prim<VariableId>,
                        val currentValueVariableId : Prim<VariableId>,
                        override val variables : Conj<Variable>) : Widget(variables)
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.widgetId.name                  = "widget_id"
        this.format.name                    = "format"
        this.limitValueVariableId.name      = "limit_value_variable"
        this.currentValueVariableId.name    = "current_value_variable"
        this.variables.name                 = "variables"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(widgetId : WidgetId,
                format : PointsWidgetFormat,
                limitValueVariableId : VariableId,
                currentValueVariableId : VariableId,
                variables : MutableSet<Variable>)
        : this(UUID.randomUUID(),
               Prim(widgetId),
               Comp(format),
               Prim(limitValueVariableId),
               Prim(currentValueVariableId),
               Conj(variables))


    companion object : Factory<PointsWidget>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<PointsWidget>  = when (doc)
        {
            is DocDict ->
            {
                effApply(::PointsWidget,
                         // Widget Id
                         doc.at("id") ap { WidgetId.fromDocument(it) },
                         // Format
                         split(doc.maybeAt("format"),
                               effValue(PointsWidgetFormat.default()),
                               { PointsWidgetFormat.fromDocument(it) }),
                         // Limit Value Variable Id
                         doc.at("limit_value_variable_id") ap { VariableId.fromDocument(it) },
                         // Current Value Variable Id
                         doc.at("current_value_variable_id") ap { VariableId.fromDocument(it) },
                         // Variables
                         split(doc.maybeList("variables"),
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

    fun format() : PointsWidgetFormat = this.format.value

    fun limitValueVariableId() : VariableId = this.limitValueVariableId.value

    fun currentValueVariableId() : VariableId = this.currentValueVariableId.value


    // -----------------------------------------------------------------------------------------
    // WIDGET
    // -----------------------------------------------------------------------------------------

    override fun widgetFormat() : WidgetFormat = this.format().widgetFormat()

    override fun view(sheetUIContext : SheetUIContext) : View =
            PointsWidgetView.view(this, sheetUIContext)



    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------


    fun limitValue(sheetContext : SheetContext) : Double?
    {
        val valueString = SheetManager.sheetState(sheetContext.sheetId)
                            .apply { it.numberVariableWithId(this.limitValueVariableId()) }
                            .apply { it.value(sheetContext) }

        when (valueString) {
            is Val -> return valueString.value
            is Err -> ApplicationLog.error(valueString.error)
        }

        return null
    }


    fun limitValueString(sheetContext : SheetContext) : String?
    {
        val valueString = SheetManager.sheetState(sheetContext.sheetId)
                            .apply { it.variableWithId(this.limitValueVariableId()) }
                            .apply { it.valueString(sheetContext) }

        when (valueString) {
            is Val -> return valueString.value
            is Err -> ApplicationLog.error(valueString.error)
        }

        return null
    }


    fun currentValue(sheetContext : SheetContext) : Double?
    {
        val valueString = SheetManager.sheetState(sheetContext.sheetId)
                            .apply { it.numberVariableWithId(this.currentValueVariableId()) }
                            .ap { it.value(sheetContext) }

        when (valueString) {
            is Val -> return valueString.value
            is Err -> ApplicationLog.error(valueString.error)
        }

        return null
    }


    fun currentValueString(sheetContext : SheetContext) : String?
    {
        val valueString = SheetManager.sheetState(sheetContext.sheetId)
                            .apply { it.variableWithId(this.currentValueVariableId()) }
                            .ap { it.valueString(sheetContext) }

        when (valueString) {
            is Val -> return valueString.value
            is Err -> ApplicationLog.error(valueString.error)
        }

        return null
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

    override fun onSheetComponentActive(sheetContext : SheetContext) {
        SheetManager.addVariable(sheetContext.sheetId, this.limitValueVariableId())
        SheetManager.addVariable(sheetContext.sheetId, this.currentValueVariableId())
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
                       val source : Maybe<Prim<QuoteSource>>,
                       override val variables : Conj<Variable>) : Widget(variables)
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

        this.variables.name                     = "variables"

    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(widgetId : WidgetId,
                format   : QuoteWidgetFormat,
                viewType : QuoteViewType,
                quote    : Quote,
                source   : Maybe<QuoteSource>,
                variables : MutableSet<Variable>)
        : this(UUID.randomUUID(),
               Prim(widgetId),
               Comp(format),
               Prim(viewType),
               Prim(quote),
               maybeLiftPrim(source),
               Conj(variables))


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
                               { effApply(::Just, QuoteSource.fromDocument(it)) }),
                         // Variables
                         split(doc.maybeList("variables"),
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

    fun format() : QuoteWidgetFormat = this.format.value

    fun viewType() : QuoteViewType = this.viewType.value

    fun quoteString() : String = this.quote.value.value

    fun sourceString() : String? = getMaybePrim(this.source)?.value


    // -----------------------------------------------------------------------------------------
    // WIDGET
    // -----------------------------------------------------------------------------------------

    override fun widgetFormat() : WidgetFormat = this.format().widgetFormat()


    override fun view(sheetUIContext: SheetUIContext) : View =
            QuoteWidgetView.widgetView(this, sheetUIContext)


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name : String = "widget_quote"

    override val modelObject = this


    // -----------------------------------------------------------------------------------------
    // SHEET COMPONENT
    // -----------------------------------------------------------------------------------------

    override fun onSheetComponentActive(sheetContext: SheetContext) { }

}


/**
 * Story Widget
 */
data class StoryWidget(override val id : UUID,
                       val widgetId : Prim<WidgetId>,
                       val format : Comp<StoryWidgetFormat>,
                       val story : Coll<StoryPart>,
                       override val variables : Conj<Variable>) : Widget(variables)
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.widgetId.name      = "widget_id"
        this.format.name        = "format"
        this.story.name         = "story"
        this.variables.name     = "variables"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(widgetId : WidgetId,
                format : StoryWidgetFormat,
                story : MutableList<StoryPart>,
                variables : MutableSet<Variable>)
        : this(UUID.randomUUID(),
               Prim(widgetId),
               Comp(format),
               Coll(story),
               Conj(variables))


    companion object : Factory<StoryWidget>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<StoryWidget>  = when (doc)
        {
            is DocDict ->
            {
                effApply(::StoryWidget,
                         // Widget Id
                         doc.at("id") ap { WidgetId.fromDocument(it) },
                         // Format
                         split(doc.maybeAt("format"),
                               effValue(StoryWidgetFormat.default()),
                               { StoryWidgetFormat.fromDocument(it) }),
                         // Story
                         doc.list("story") ap {
                             it.mapMut { StoryPart.fromDocument(it) }
                         },
                         // Variables
                         split(doc.maybeList("variables"),
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

    fun format() : StoryWidgetFormat = this.format.value

    fun story() : List<StoryPart> = this.story.list


    // -----------------------------------------------------------------------------------------
    // WIDGET
    // -----------------------------------------------------------------------------------------

    override fun widgetFormat() : WidgetFormat = this.format().widgetFormat()


    override fun view(sheetUIContext: SheetUIContext) : View =
            StoryWidgetView.view(this, sheetUIContext)


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name : String = "widget_story"

    override val modelObject = this


    // -----------------------------------------------------------------------------------------
    // SHEET COMPONENT
    // -----------------------------------------------------------------------------------------

    override fun onSheetComponentActive(sheetContext : SheetContext)
    {
        this.story().mapNotNull { it.variable() }
                    .forEach { this.addVariableToState(sheetContext.sheetId, it) }

        this.variables().forEach { this.addVariableToState(sheetContext.sheetId, it) }
    }

}


/**
 * Tab Widget
 */
data class TabWidget(override val id : UUID,
                     val widgetId : Prim<WidgetId>,
                     val format : Comp<TabWidgetFormat>,
                     val tabs : Coll<Tab>,
                     val defaultSelected : Prim<DefaultSelected>,
                     override val variables : Conj<Variable>) : Widget(variables)
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
                defaultSelected : DefaultSelected,
                variables : MutableSet<Variable>)
        : this(UUID.randomUUID(),
               Prim(widgetId),
               Comp(format),
               Coll(tabs),
               Prim(defaultSelected),
               Conj(variables))


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
                               { DefaultSelected.fromDocument(it) }),
                         // Variables
                         split(doc.maybeList("variables"),
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

    fun format() : TabWidgetFormat = this.format.value


    // -----------------------------------------------------------------------------------------
    // WIDGET
    // -----------------------------------------------------------------------------------------

    override fun widgetFormat() : WidgetFormat = this.format().widgetFormat()


    override fun view(sheetUIContext: SheetUIContext): View {
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
 * Table Widget
 */
data class TableWidget(override val id : UUID,
                       val widgetId : Prim<WidgetId>,
                       val format : Comp<TableWidgetFormat>,
                       val columns : Coll<TableWidgetColumn>,
                       val rows : Coll<TableWidgetRow>,
                       val sort : Maybe<Prim<TableSort>>,
                       override val variables : Conj<Variable>) : Widget(variables)
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

        when (this.sort) {
            is Just -> this.sort.value.name = "sort"
        }

    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(widgetId : WidgetId,
                format : TableWidgetFormat,
                columns : MutableList<TableWidgetColumn>,
                rows : MutableList<TableWidgetRow>,
                sort : Maybe<TableSort>,
                variables : MutableSet<Variable>)
        : this(UUID.randomUUID(),
               Prim(widgetId),
               Comp(format),
               Coll(columns),
               Coll(rows),
               maybeLiftPrim(sort),
               Conj(variables))


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
                         },
                         // Table Sort
                         split(doc.maybeAt("sort"),
                               effValue<ValueError,Maybe<TableSort>>(Nothing()),
                               { effApply(::Just, TableSort.fromDocument(it)) }),
                         // Variables
                         split(doc.maybeList("variables"),
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

    override fun onSheetComponentActive(sheetContext : SheetContext)
    {
        this.rows().forEachIndexed { rowIndex, row ->
            row.cells().forEachIndexed { cellIndex, cell ->
                when (cell) {
                    is TableWidgetTextCell -> {
                        val column = this.columns()[cellIndex]
                        val variableId = VariableId(column.variablePrefix() + "_row_" + rowIndex.toString())
                        val variable = TextVariable(variableId,
                                                    VariableLabel(column.nameString()),
                                                    VariableDescription(column.nameString()),
                                                    VariableTagSet(mutableSetOf()),
                                                    cell.variableValue(),
                                                    DefinesNamespace(false))
                        SheetManager.addVariable(sheetContext.sheetId, variable)
                        cell.setVariableId(variableId)
                    }
                }

            }
        }
    }


    override fun view(sheetUIContext: SheetUIContext) : View =
            TableWidgetView.view(this, this.format(), sheetUIContext)


}



/**
 * Text Widget
 */
data class TextWidget(override val id : UUID,
                      val widgetId : Prim<WidgetId>,
                      val format : Comp<TextWidgetFormat>,
                      val description : Maybe<Prim<TextWidgetDescription>>,
                      val valueVariableId : Prim<VariableId>,
                      override val variables : Conj<Variable>) : Widget(variables)
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

        this.valueVariableId.name                   = "value_variable_id"

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
                valueVariableId : VariableId,
                variables : MutableSet<Variable>)
        : this(UUID.randomUUID(),
               Prim(widgetId),
               Comp(format),
               maybeLiftPrim(description),
               Prim(valueVariableId),
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
                         doc.at("value_variable_id") ap { VariableId.fromDocument(it) },
                         // Variables
                         split(doc.maybeList("variables"),
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

    fun valueVariableId() : VariableId = this.valueVariableId.value


    // -----------------------------------------------------------------------------------------
    // WIDGET
    // -----------------------------------------------------------------------------------------

    override fun widgetFormat() : WidgetFormat = this.format().widgetFormat()


    override fun view(sheetUIContext: SheetUIContext) : View =
        TextWidgetView.view(this, this.format(), sheetUIContext)


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    fun valueVariable(sheetContext : SheetContext) : AppEff<TextVariable> =
        SheetManager.sheetState(sheetContext.sheetId)
                    .apply { it.textVariableWithId(this.valueVariableId()) }


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name : String = "widget_text"

    override val modelObject = this


    // -----------------------------------------------------------------------------------------
    // SHEET COMPONENT
    // -----------------------------------------------------------------------------------------

    override fun onSheetComponentActive(sheetContext : SheetContext)
    {
        SheetManager.addVariable(sheetContext.sheetId, this.valueVariableId())
    }


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    /**
     * The string representation of the widget's current value. This method returns 0 when the
     * value is null for some reason.
     */
    fun valueString(sheetContext : SheetContext) : String
    {
        val str = this.valueVariable(sheetContext)
                      .apply { it.value(sheetContext) }

        when (str)
        {
            is Val -> return str.value
            is Err -> ApplicationLog.error(str.error)
        }

        return ""
    }

}



