
package com.taletable.android.model.sheet.widget


import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TableLayout
import android.widget.TextView
import com.taletable.android.R
import com.taletable.android.activity.entity.book.BookActivity
import com.taletable.android.activity.sheet.SheetActivity
import com.taletable.android.activity.sheet.SheetActivityGlobal
import com.taletable.android.activity.sheet.dialog.openNumberVariableEditorDialog
import com.taletable.android.activity.sheet.dialog.openTextVariableEditorDialog
import com.taletable.android.app.*
import com.taletable.android.lib.Factory
import com.taletable.android.lib.orm.sql.SQLSerializable
import com.taletable.android.lib.orm.sql.SQLText
import com.taletable.android.lib.orm.sql.SQLValue
import com.taletable.android.lib.ui.LayoutType
import com.taletable.android.lib.ui.LinearLayoutBuilder
import com.taletable.android.model.book.BookReference
import com.taletable.android.model.engine.EngineValueNumber
import com.taletable.android.model.engine.dice.DiceRollGroup
import com.taletable.android.model.engine.mechanic.MechanicCategoryReference
import com.taletable.android.model.engine.procedure.Procedure
import com.taletable.android.model.engine.procedure.ProcedureId
import com.taletable.android.model.engine.reference.TextReferenceLiteral
import com.taletable.android.model.engine.tag.TagQuery
import com.taletable.android.model.engine.tag.TagQueryAll
import com.taletable.android.model.engine.value.Value
import com.taletable.android.model.engine.value.ValueReference
import com.taletable.android.model.engine.value.ValueSetId
import com.taletable.android.model.engine.variable.*
import com.taletable.android.model.entity.*
import com.taletable.android.model.sheet.group.*
import com.taletable.android.model.sheet.style.*
import com.taletable.android.model.sheet.widget.table.*
import com.taletable.android.model.sheet.widget.table.cell.TextCellValueRelation
import com.taletable.android.model.sheet.widget.table.cell.TextCellValueValue
import com.taletable.android.rts.entity.*
import com.taletable.android.rts.entity.sheet.*
import com.taletable.android.util.Util
import effect.*
import effect.Val
import lulo.document.*
import lulo.value.*
import lulo.value.UnexpectedType
import maybe.*
import maybe.Nothing
import java.io.Serializable
import java.util.*



/**
 * Widget
 */
@Suppress("UNCHECKED_CAST")
sealed class Widget : ToDocument, SheetComponent, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<Widget>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<Widget> = when (doc)
        {
            is DocDict ->
            {
                // TODO avoid hard coding this
                when (doc.case())
                {
                    "widget_action"  -> ActionWidget.fromDocument(doc)
                                            as ValueParser<Widget>
                    "widget_boolean"  -> BooleanWidget.fromDocument(doc)
                                            // as ValueParser<Widget>
                    "widget_expander" -> ExpanderWidget.fromDocument(doc)
                                           //  as ValueParser<Widget>
                    "widget_group"    -> WidgetGroup.fromDocument(doc)
                                            //  as ValueParser<Widget>
                    "widget_image"    -> ImageWidget.fromDocument(doc)
//                                            as ValueParser<Widget>
                    "widget_list"     -> ListWidget.fromDocument(doc)
                                            as ValueParser<Widget>
                    "widget_log"      -> LogWidget.fromDocument(doc)
                                            as ValueParser<Widget>
                    "widget_mechanic" -> MechanicWidget.fromDocument(doc)
                                            as ValueParser<Widget>
                    "widget_number"   -> NumberWidget.fromDocument(doc)
                                            as ValueParser<Widget>
                    "widget_points"   -> PointsWidget.fromDocument(doc)
                                            as ValueParser<Widget>
                    "widget_quote"    -> QuoteWidget.fromDocument(doc)
                                            as ValueParser<Widget>
                    "widget_roll"     -> RollWidget.fromDocument(doc)
                                            as ValueParser<Widget>
                    "widget_slider"   -> WidgetSlider.fromDocument(doc)
                                                as ValueParser<Widget>
                    "widget_story"    -> StoryWidget.fromDocument(doc)
                                            as ValueParser<Widget>
                    "widget_table"    -> TableWidget.fromDocument(doc)
                                            as ValueParser<Widget>
                    "widget_tab"      -> WidgetTab.fromDocument(doc)
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


    abstract fun view(groupContext : Maybe<GroupContext>,
                      rowLayoutType : RowLayoutType,
                      entityId : EntityId,
                      context : Context) : View


    abstract fun widgetId() : WidgetId


    open fun primaryAction(entityId : EntityId, context : Context) { }


    open fun secondaryAction(entityId : EntityId, context : Context) { }

}


object WidgetView
{

    fun layout(widgetFormat : WidgetFormat,
               entityId : EntityId,
               context : Context,
               rowLayoutType : RowLayoutType = RowLayoutTypeLinear) : LinearLayout
    {
        val layout = when (rowLayoutType) {
            is RowLayoutTypeLinear -> this.widgetLayout(widgetFormat, entityId, context)
            is RowLayoutTypeRelative -> this.widgetLayoutRelative(widgetFormat, entityId, context)
        }

        val contentLayout = this.contentLayout(widgetFormat, context)

        widgetFormat.elementFormat().border().left().doMaybe {
            layout.addView(this.verticalBorderView(it, entityId, context))
        }

        layout.addView(contentLayout)

        widgetFormat.elementFormat().border().right().doMaybe {
            layout.addView(this.verticalBorderView(it, entityId, context))
        }

        return layout
    }


    private fun contentLayout(widgetFormat : WidgetFormat,
                              context : Context) : LinearLayout
    {
        val layout          = LinearLayoutBuilder()

        layout.id           = R.id.widget_content_layout

        layout.width        = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height       = LinearLayout.LayoutParams.MATCH_PARENT

        layout.gravity      = widgetFormat.elementFormat().alignment().gravityConstant() or
                                    widgetFormat.elementFormat().verticalAlignment().gravityConstant()

        layout.paddingSpacing   = widgetFormat.elementFormat().padding()

        return layout.linearLayout(context)
    }


    private fun widgetLayout(widgetFormat : WidgetFormat,
                             entityId : EntityId,
                             context : Context) : LinearLayout
    {
        val layout = LinearLayoutBuilder()

        layout.orientation      = LinearLayout.VERTICAL

        widgetFormat.elementFormat().border().right().doMaybe {
            layout.orientation = LinearLayout.HORIZONTAL
        }

        widgetFormat.elementFormat().border().left().doMaybe {
            layout.orientation = LinearLayout.HORIZONTAL
        }


        val width = widgetFormat.elementFormat().width()
        when (width) {
            is Width.Fill -> {
                layout.width        = LinearLayout.LayoutParams.MATCH_PARENT
            }
            is Width.Justify -> {
                layout.width        = 0
                layout.weight       = widgetFormat.width().toFloat()
            }
            is Width.Wrap -> {
                layout.width        = LinearLayout.LayoutParams.WRAP_CONTENT
            }
            is Width.Fixed -> {
                layout.widthDp      = width.value.toInt()
            }
        }

        val height = widgetFormat.elementFormat().height()
        when (height)
        {
            is Height.Wrap  -> {
                layout.height   = LinearLayout.LayoutParams.WRAP_CONTENT
                layout.layoutGravity      = widgetFormat.elementFormat().verticalAlignment().gravityConstant()
            }
            is Height.Fixed -> layout.heightDp = height.value.toInt()
        }


        layout.marginSpacing    = widgetFormat.elementFormat().margins()

        layout.backgroundColor  = colorOrBlack(widgetFormat.elementFormat().backgroundColorTheme(),
                                               entityId)

        layout.corners          = widgetFormat.elementFormat().corners()

        return layout.linearLayout(context)
    }


    private fun widgetLayoutRelative(widgetFormat : WidgetFormat,
                                     entityId : EntityId,
                                     context : Context) : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.layoutType       = LayoutType.RELATIVE

        layout.orientation      = LinearLayout.VERTICAL

        widgetFormat.elementFormat().border().right().doMaybe {
            layout.orientation = LinearLayout.HORIZONTAL
        }

        widgetFormat.elementFormat().border().left().doMaybe {
            layout.orientation = LinearLayout.HORIZONTAL
        }


        val width = widgetFormat.elementFormat().width()
        when (width) {
            is Width.Fill -> {
                layout.width        = RelativeLayout.LayoutParams.MATCH_PARENT
            }
            is Width.Justify -> {
                layout.width        = 0
                layout.weight       = widgetFormat.width().toFloat()
            }
            is Width.Wrap -> {
                layout.width        = RelativeLayout.LayoutParams.WRAP_CONTENT
            }
            is Width.Fixed -> {
                layout.widthDp      = width.value.toInt()
            }
        }

        val height = widgetFormat.elementFormat().height()
        when (height)
        {
            is Height.Wrap  -> {
                layout.height   = LinearLayout.LayoutParams.WRAP_CONTENT
                layout.layoutGravity      = widgetFormat.elementFormat().verticalAlignment().gravityConstant()
            }
            is Height.Fixed -> layout.heightDp = height.value.toInt()
        }


        layout.marginSpacing    = widgetFormat.elementFormat().margins()

        layout.backgroundColor  = colorOrBlack(widgetFormat.elementFormat().backgroundColorTheme(),
                                               entityId)

        layout.corners          = widgetFormat.elementFormat().corners()

        when (widgetFormat.elementFormat().alignment()) {
            is Alignment.Left   -> {
                layout.addRule(RelativeLayout.ALIGN_PARENT_START)
            }
            is Alignment.Right  -> {
                layout.addRule(RelativeLayout.ALIGN_PARENT_END)
            }
        }

        layout.addRule(RelativeLayout.CENTER_VERTICAL)


        return layout.linearLayout(context)
    }


    private fun verticalBorderView(format : BorderEdge,
                                   entityId : EntityId,
                                   context : Context) : LinearLayout
    {
        val divider = LinearLayoutBuilder()

        divider.widthDp             = format.thickness().value
        divider.height              = LinearLayout.LayoutParams.MATCH_PARENT

        divider.backgroundColor     = colorOrBlack(format.colorTheme(), entityId)

        return divider.linearLayout(context)
    }


    class WidgetTouchView(context : Context) : LinearLayout(context)
    {


        var clickTime : Long = 0
        var CLICK_DURATION = 500


        override fun onInterceptTouchEvent(ev: MotionEvent?) : Boolean
        {
            if (ev != null)
            {
                when (ev.action)
                {
                    MotionEvent.ACTION_UP ->
                    {
                        SheetActivityGlobal.cancelLongPressRunnable()
                    }
                    MotionEvent.ACTION_OUTSIDE ->
                    {
                        //SheetActivityGlobal.touchHandler.removeCallbacks(runnable)
                        SheetActivityGlobal.cancelLongPressRunnable()
                    }
                    MotionEvent.ACTION_SCROLL ->
                    {
                        SheetActivityGlobal.cancelLongPressRunnable()
                    }
                    MotionEvent.ACTION_CANCEL ->
                    {
                        SheetActivityGlobal.cancelLongPressRunnable()
                    }
                }
            }
            return false
        }


    }



}


/**
 * Widget Name
 */
data class WidgetId(val value : UUID) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<WidgetId>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<WidgetId> = when (doc)
        {
            is DocText -> {
                try {
                    effValue<ValueError,WidgetId>(WidgetId(UUID.fromString(doc.text)))
                }
                catch (e : IllegalArgumentException) {
                    effError<ValueError,WidgetId>(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
                }
            }
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }


        fun random() = WidgetId(UUID.randomUUID())
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocText(this.value.toString())


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value.toString()})

}


/**
 * Action Widget
 */
data class ActionWidget(val widgetId : WidgetId,
                        val format : ActionWidgetFormat,
                        val procedureId : ProcedureId,
                        val activeVariableId : Maybe<VariableId>,
                        val description : Maybe<ActionWidgetDescription>) : Widget()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var layoutViewId : Int? = null


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------
//
//    constructor(widgetId : WidgetId,
//                format : ActionWidgetFormat,
//                procedureId : ProcedureId,
//                activeVariableId : Maybe<VariableId>,
//                description : Maybe<ActionWidgetDescription>)
//        : this(UUID.randomUUID(),
//               widgetId,
//               format,
//               procedureId,
//               activeVariableId,
//               description)
//

    companion object : Factory<ActionWidget>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<ActionWidget> = when (doc)
        {
            is DocDict ->
            {
                apply(::ActionWidget,
                      // Widget Id
                      split(doc.maybeAt("id"),
                            effValue(WidgetId.random()),
                            { WidgetId.fromDocument(it) }),
                      // Format
                      split(doc.maybeAt("format"),
                            effValue(ActionWidgetFormat.default()),
                            { ActionWidgetFormat.fromDocument(it) }),
                      // Procedure Id
                      doc.at("procedure_id") ap { ProcedureId.fromDocument(it) },
                      // Active Variable Id
                      split(doc.maybeAt("active_variable_id"),
                            effValue<ValueError,Maybe<VariableId>>(Nothing()),
                            { apply(::Just, VariableId.fromDocument(it)) }),
                      // Description
                      split(doc.maybeAt("description"),
                            effValue<ValueError,Maybe<ActionWidgetDescription>>(Nothing()),
                            { apply(::Just, ActionWidgetDescription.fromDocument(it)) })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "id" to this.widgetId().toDocument(),
        "format" to this.format().toDocument(),
        "procedure_id" to this.procedureId().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun format() : ActionWidgetFormat = this.format


    fun procedureId() : ProcedureId = this.procedureId


    fun activeVariableId() : Maybe<VariableId> = this.activeVariableId


    fun description() : Maybe<ActionWidgetDescription> = this.description


    // -----------------------------------------------------------------------------------------
    // WIDGET
    // -----------------------------------------------------------------------------------------

    override fun widgetFormat() : WidgetFormat = this.format().widgetFormat()


    override fun view(groupContext : Maybe<GroupContext>,
                      rowLayoutType : RowLayoutType,
                      entityId : EntityId,
                      context : Context) : View {
        val viewBuilder = ActionWidgetViewBuilder(this, entityId, context)
        return viewBuilder.view()
    }


    override fun widgetId() = this.widgetId


    // -----------------------------------------------------------------------------------------
    // PROD TYPE
    // -----------------------------------------------------------------------------------------

//    override fun onLoad() { }
//
//
//    override val prodTypeObject = this
//
//
//    override fun rowValue() : DB_WidgetActionValue =
//        RowValue4(widgetActionTable,
//                  PrimValue(this.widgetId),
//                  ProdValue(this.format),
//                  PrimValue(this.procedureId),
//                  MaybePrimValue(this.description))
//

    // -----------------------------------------------------------------------------------------
    // SHEET COMPONENT
    // -----------------------------------------------------------------------------------------

    override fun onSheetComponentActive(entityId : EntityId, context : Context)
    {
        val sheetActivity = context as SheetActivity
        val rootView = sheetActivity.rootSheetView()

        val activeVariableId = this.activeVariableId()
        when (activeVariableId)
        {
            is Just ->
            {
                val variable = booleanVariable(activeVariableId.value, entityId)
                when (variable) {
                    is Val -> {
                       val listener = VariableChangeListener({
                            rootView?.let {
                                this.updateView(it, entityId, context)
                            }
                       }, {})

                       addVariableChangeListener(variable.value.variableId(),
                                                   listener,
                                                   entityId)
                    }
                }
            }
        }
    }


    // -----------------------------------------------------------------------------------------
    // PROCEDURE
    // -----------------------------------------------------------------------------------------

    fun procedure(entityId : EntityId) : AppEff<Procedure> =
            procedure(this.procedureId, entityId)


    // -----------------------------------------------------------------------------------------
    // ACTIVE
    // -----------------------------------------------------------------------------------------

    fun isActive(entityId : EntityId) : Boolean
    {
        val variableId = this.activeVariableId()
        return when (variableId)
        {
            is Just ->
            {
                val variableValue = booleanVariable(variableId.value, entityId)
                                      .apply { it.value() }
                when (variableValue)
                {
                    is Val -> {
                        variableValue.value
                    }
                    is Err -> {
                        ApplicationLog.error(variableValue.error)
                        true
                    }
                }

            }
            is Nothing -> {
                true
            }
        }
    }


    fun setActive(entityId : EntityId)
    {
        val variableId = this.activeVariableId()
        when (variableId)
        {
            is Just ->
            {
                val variable = booleanVariable(variableId.value, entityId)
                when (variable)
                {
                    is Val -> variable.value.updateValue(true, entityId)
                    is Err -> ApplicationLog.error(variable.error)
                }
            }
        }
    }


    // -----------------------------------------------------------------------------------------
    // UPDATE
    // -----------------------------------------------------------------------------------------

    fun update(actionWidgetUpdate : WidgetUpdateActionWidget,
               rootView : View,
               entityId : EntityId,
               context : Context) =
        when (actionWidgetUpdate)
        {
            is ActionWidgetUpdate ->
            {
                this.updateView(rootView, entityId, context)
            }
        }


    private fun updateView(rootView : View, entityId : EntityId, context : Context)
    {
        val layoutViewId = this.layoutViewId
        if (layoutViewId != null) {
            val layout = rootView.findViewById<LinearLayout>(layoutViewId)
            layout?.removeAllViews()
            layout?.addView(ActionWidgetViewBuilder(this, entityId, context).inlineLeftButtonView())
        }
    }


}


/**
 * Boolean Widget
 */
data class BooleanWidget(private val widgetId : WidgetId,
                         private val format : BooleanWidgetFormat,
                         private var valueVariablesReference : VariableReference) : Widget()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var layoutId : Int? = null


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

//    constructor(widgetId : WidgetId,
//                format : BooleanWidgetFormat,
//                valueVariablesReference : VariableReference)
//        : this(UUID.randomUUID(),
//               widgetId,
//               format,
//               valueVariablesReference)


    companion object : Factory<Widget>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<Widget> = when (doc)
        {
            is DocDict ->
            {
                apply(::BooleanWidget,
                      // Widget Id
                      split(doc.maybeAt("id"),
                            effValue(WidgetId.random()),
                            { WidgetId.fromDocument(it) }),
                      // Format
                      split(doc.maybeAt("format"),
                            effValue(BooleanWidgetFormat.default()),
                            { BooleanWidgetFormat.fromDocument(it) }),
                      // Value Variables Reference
                      doc.at("value_variables_reference") ap { VariableReference.fromDocument(it) }
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "id" to this.widgetId().toDocument(),
        "format" to this.format().toDocument(),
        "value_variables_reference" to this.valueVariablesReference().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun format() : BooleanWidgetFormat = this.format


    fun valueVariablesReference() : VariableReference = this.valueVariablesReference


    // -----------------------------------------------------------------------------------------
    // WIDGET
    // -----------------------------------------------------------------------------------------

    override fun widgetFormat() : WidgetFormat = this.format().widgetFormat()


    override fun view(groupContext : Maybe<GroupContext>,
                      rowLayoutType : RowLayoutType,
                      entityId : EntityId,
                      context : Context) : View
    {
        val viewBuilder = BooleanWidgetViewBuilder(this, entityId, context)

        groupContext.doMaybe { groupContext ->
            val variableRef = this.valueVariablesReference()
            when (variableRef) {
                is VariableReferenceContextual -> {
                    entityEngineState(entityId).apDo {
                        val context = VariableContext(groupContext.value)
                        setContextualVariable(variableRef.id(), context, entityId)
                        this.valueVariablesReference = VariableReferenceContextual(variableRef.id(), Just(context))
                    }
                }
            }
        }

        return viewBuilder.view()
    }


    override fun widgetId() = this.widgetId


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------
//
//    override fun onLoad() { }
//
//
//    override val prodTypeObject = this
//
//
//    override fun rowValue() : DB_WidgetBooleanValue =
//        RowValue3(widgetBooleanTable,
//                  PrimValue(this.widgetId),
//                  ProdValue(this.format),
//                  PrimValue(this.valueVariablesReference))


    // -----------------------------------------------------------------------------------------
    // SHEET COMPONENT
    // -----------------------------------------------------------------------------------------

    override fun onSheetComponentActive(entityId : EntityId, context : Context)
    {
        val sheetActivity = context as SheetActivity
        val rootView = sheetActivity.rootSheetView()

        this.variables(entityId).forEach {
            val listener = VariableChangeListener({
                rootView?.let {
                    this.updateView(it, entityId, context)
                }
            }, {})

            addVariableChangeListener(it.variableId(), listener, entityId)
        }
    }


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    fun variables(entityId : EntityId) : Set<BooleanVariable> =
        filterBooleanVariables(variables(this.valueVariablesReference, entityId))


    private fun filterBooleanVariables(variableSet : Set<Variable>) : Set<BooleanVariable>
    {
        val booleanVariables : MutableSet<BooleanVariable> = mutableSetOf()

        variableSet.forEach {
            when (it) {
                is BooleanVariable -> booleanVariables.add(it)
            }
        }

        return booleanVariables
    }


    fun variableValue(entityId : EntityId) : Boolean
    {
        val _variables = this.variables(entityId)

        val variableValues : MutableList<Boolean> = mutableListOf()

        _variables.forEach {
            it.value().apDo {
                variableValues.add(it)
            }
        }

        return variableValues.all { it }
    }



    // -----------------------------------------------------------------------------------------
    // UPDATE
    // -----------------------------------------------------------------------------------------

    fun update(booleanWidgetUpdate : WidgetUpdateBooleanWidget,
               entityId : EntityId,
               rootView : View?,
               context : Context)
    {
        when (booleanWidgetUpdate)
        {
            is BooleanWidgetUpdateToggle ->
            {
                this.toggleValues(entityId)
                rootView?.let { this.updateView(it, entityId, context) }

            }
            is BooleanWidgetUpdateSetValue ->
            {
                this.updateValues(booleanWidgetUpdate.newValue, entityId)
                rootView?.let { this.updateView(it, entityId, context) }
            }
        }
    }



    private fun updateView(rootView : View, entityId : EntityId, context : Context)
    {
        val viewId = this.layoutId
        if (viewId != null) {
            val layout = rootView.findViewById<LinearLayout>(viewId)
            if (layout != null) {
                BooleanWidgetViewBuilder(this, entityId, context).updateView(layout)
            }
        }
    }


    fun toggleValues(entityId : EntityId)
    {
        val booleanVariables = this.variables(entityId)
        booleanVariables.forEach {
            it.toggleValue(entityId)
        }
    }


    fun updateValues(newValue : Boolean, entityId : EntityId)
    {
        val booleanVariables = this.variables(entityId)
        booleanVariables.forEach {
            it.updateValue(newValue, entityId)
        }
    }


}


/**
 * Expander Widget
 */
data class ExpanderWidget(val widgetId : WidgetId,
                          val format : ExpanderWidgetFormat,
                          val header : ExpanderWidgetLabel,
                          val headerGroupReferences : List<GroupReference>,
                          val groupReferences : List<GroupReference>,
                          val bookReference : Maybe<BookReference>,
                          val checkboxVariableReference : Maybe<VariableReference>)
                           : Widget()
{

    // | Properties
    // -----------------------------------------------------------------------------------------

    var groupContext : Maybe<GroupContext> = Nothing()

    var layoutId : Int? = null
    var checkboxLayoutId : Int? = null

    private var headerGroupsCache : Maybe<List<Group>> = Nothing()

    private var contentGroupsCache : Maybe<List<Group>> = Nothing()


    // | Constructors
    // -----------------------------------------------------------------------------------------

    companion object : Factory<Widget>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<Widget> = when (doc)
        {
            is DocDict ->
            {
                apply(::ExpanderWidget,
                      // Widget Id
                      split(doc.maybeAt("id"),
                            effValue(WidgetId.random()),
                            { WidgetId.fromDocument(it) }),
                      // Format
                      split(doc.maybeAt("format"),
                           effValue(ExpanderWidgetFormat.default()),
                           { ExpanderWidgetFormat.fromDocument(it) }),
                      // Label
                      doc.at("label") ap { ExpanderWidgetLabel.fromDocument(it) },
                      // Header Groups
                      split(doc.maybeList("header_group_references"),
                            effValue(listOf()),
                            { it.map { GroupReference.fromDocument(it) } }),
                      // Group References
                      split(doc.maybeList("group_references"),
                            effValue(listOf()),
                            { it.map { GroupReference.fromDocument(it) } }),
                      // Book Reference
                      split(doc.maybeAt("book_reference"),
                            effValue<ValueError,Maybe<BookReference>>(Nothing()),
                            { apply(::Just, BookReference.fromDocument(it)) }),
                      // Checkbox Variable Reference
                      split(doc.maybeAt("checkbox_variable_reference"),
                            effValue<ValueError,Maybe<VariableReference>>(Nothing()),
                            { apply(::Just, VariableReference.fromDocument(it)) })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "id" to this.widgetId().toDocument(),
        "format" to this.format().toDocument(),
        "label" to this.label().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun format() : ExpanderWidgetFormat = this.format


    fun label() : ExpanderWidgetLabel = this.header


    fun headerGroupReferences() : List<GroupReference> = this.headerGroupReferences


    fun groupReferences() : List<GroupReference> = this.groupReferences


    fun bookReference() : Maybe<BookReference> = this.bookReference


    fun checkboxVariableReference() : Maybe<VariableReference> = this.checkboxVariableReference


    // | Widget
    // -----------------------------------------------------------------------------------------

    override fun widgetFormat() : WidgetFormat = this.format().widgetFormat()


    override fun view(groupContext : Maybe<GroupContext>,
                      rowLayoutType : RowLayoutType,
                      entityId : EntityId,
                      context : Context) : View {
        val viewBuilder = ExpanderWidgetUI(this, entityId, context)

        this.groupContext = groupContext

        return viewBuilder.view()
    }


    override fun widgetId() = this.widgetId


    // | Sheet Component
    // -----------------------------------------------------------------------------------------

    override fun onSheetComponentActive(entityId : EntityId, context : Context)
    {
        this.contentGroups(entityId).forEach {
            it.onSheetComponentActive(entityId, context)
        }

        sheet(entityId).doMaybe {
            it.indexWidget(this)
        }
    }


    // | Groups
    // -----------------------------------------------------------------------------------------

    fun headerGroups(entityId : EntityId) : List<Group>
    {
        val headerGroupsCache = this.headerGroupsCache
        return when (headerGroupsCache) {
            is Just    -> headerGroupsCache.value
            is Nothing -> {
                val _groups = groups(this.headerGroupReferences, entityId)
                _groups.forEach {
                    it.rows().forEach {
                        it.widgets().forEach { widget ->
                            sheetOrError(entityId) apDo { it.indexWidget(widget)  }
                        }
                    }
                }
                _groups
            }
        }
    }


    fun contentGroups(entityId : EntityId) : List<Group>
    {
        val contentGroupsCache = this.contentGroupsCache
        return when (contentGroupsCache) {
            is Just    -> contentGroupsCache.value
            is Nothing -> {
                val _groups = groups(this.groupReferences, entityId)
                _groups.forEach {
                    it.rows().forEach {
                        it.widgets().forEach { widget ->
                            sheetOrError(entityId) apDo { it.indexWidget(widget)  }
                        }
                    }
                }
                _groups
            }
        }
    }


    // | Checkbox Variable
    // -----------------------------------------------------------------------------------------

    fun checkboxVariable(entityId : EntityId) : AppEff<BooleanVariable>
    {
        val variableReference = this.checkboxVariableReference

        return when (variableReference) {
            is Just -> {
                booleanVariable(variableReference.value, entityId)
            }
            is Nothing -> effError(AppSheetError(ExpanderWidgetDoesNotHaveCheckboxVariable(this.widgetId)))
        }

    }


    fun isSelected(entityId : EntityId) : Boolean
    {
        val checkboxValue = this.checkboxVariable(entityId).apply { it.value() }
        return when (checkboxValue) {
            is Val -> checkboxValue.value
            is Err -> false
        }
    }


    // | Update
    // -----------------------------------------------------------------------------------------

    fun update(expanderWidgetUpdate : WidgetUpdateExpanderWidget,
               entityId : EntityId,
               rootView : View?,
               context : Context)
    {
        when (expanderWidgetUpdate)
        {
            is ExpanderWidgetUpdateToggle ->
            {
                this.checkboxVariable(entityId).apDo {
                    it.toggleValue(entityId)
                }

                rootView?.let { this.updateCheckboxView(it, entityId, context) }
            }
        }
    }


    private fun updateCheckboxView(rootView : View, entityId : EntityId, context : Context)
    {
        val viewId = this.checkboxLayoutId
        if (viewId != null) {
            val layout = rootView.findViewById<LinearLayout>(viewId)
            if (layout != null) {
                ExpanderWidgetUI(this, entityId, context).updateCheckboxView(layout)
            }
        }
    }

}


/**
 * Group Widget
 */
data class WidgetGroup(val widgetId : WidgetId,
                       val format : GroupWidgetFormat,
                       var groupReferences : List<GroupReference>,
                       val titleVariableId : Maybe<VariableId>,
                       val groupQuery : TagQuery) : Widget()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var groupsCache : Maybe<List<Group>> = Nothing()

    var contentLayoutId : Int? = null

    var groupContext : Maybe<GroupContext> = Nothing()


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<Widget>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<Widget> = when (doc)
        {
            is DocDict ->
            {
                apply(::WidgetGroup,
                      // Widget Id
                      split(doc.maybeAt("id"),
                            effValue(WidgetId.random()),
                            { WidgetId.fromDocument(it) }),
                      // Format
                      split(doc.maybeAt("format"),
                           effValue(GroupWidgetFormat.default()),
                           { GroupWidgetFormat.fromDocument(it) }),
                      // Group References
                      doc.list("group_references") ap { docList ->
                          docList.map { GroupReference.fromDocument(it) }
                      },
                      // Title Variable Id
                      split(doc.maybeAt("title_variable_id"),
                            effValue<ValueError,Maybe<VariableId>>(Nothing()),
                            { apply(::Just, VariableId.fromDocument(it)) }),
                      // Group Query
                      split(doc.maybeAt("group_query"),
                            effValue<ValueError,TagQuery>(TagQueryAll()),
                            { TagQuery.fromDocument(it) })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "id" to this.widgetId().toDocument(),
        "format" to this.format().toDocument(),
        "group_references" to DocList(this.groupReferences.map { it.toDocument() })
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun format() : GroupWidgetFormat = this.format


    fun groupReferences() : List<GroupReference> = this.groupReferences


    fun groupQuery() : TagQuery = this.groupQuery


    // -----------------------------------------------------------------------------------------
    // TITLE
    // -----------------------------------------------------------------------------------------

    fun title(entityId : EntityId) : Maybe<String> =
        this.titleVariableId ap {
            val value = textVariable(it, entityId).apply { it.value(entityId) }
            when (value) {
                is Val -> value.value
                else    -> Nothing()
            }
        }


    // -----------------------------------------------------------------------------------------
    // GROUP
    // -----------------------------------------------------------------------------------------

    fun groupAtIndex(index : Int, entityId : EntityId) : Maybe<Group>
    {
        val groups = this.groups(entityId)

        return if (groups.size > index)
            Just(groups[index]!!)
        else
            Nothing()
    }


    fun groups(entityId : EntityId) : List<Group>
    {
        val groupsCache = this.groupsCache
        return when (groupsCache) {
            is Just    -> groupsCache.value
            is Nothing -> groups(this.groupReferences, entityId)
        }
    }


    // -----------------------------------------------------------------------------------------
    // WIDGET
    // -----------------------------------------------------------------------------------------

    override fun widgetFormat() : WidgetFormat = this.format().widgetFormat()


    override fun view(groupContext : Maybe<GroupContext>,
                      rowLayoutType : RowLayoutType,
                      entityId : EntityId,
                      context : Context) : View {
        val widgetUI = GroupWidgetUI(this, entityId, context)

        this.groupContext = groupContext

        return widgetUI.view()
    }


    override fun widgetId() = this.widgetId


    // -----------------------------------------------------------------------------------------
    // SHEET COMPONENT
    // -----------------------------------------------------------------------------------------

    override fun onSheetComponentActive(entityId : EntityId, context : Context)
    {
        this.groups(entityId).forEach {
            it.onSheetComponentActive(entityId, context)
        }
    }


    // -----------------------------------------------------------------------------------------
    // UPDATE
    // -----------------------------------------------------------------------------------------

    fun update(groupWidgetUpdate : WidgetUpdateGroupWidget,
               entityId : EntityId,
               rootView : View?,
               context : Context) =
        when (groupWidgetUpdate)
        {
            is GroupWidgetUpdateSetReferences ->
            {
                this.groupReferences = groupWidgetUpdate.newReferenceList
                rootView?.let { this.updateContentView(rootView, entityId, context) }
            }
        }


    private fun updateContentView(rootView : View?, entityId : EntityId, context : Context)
    {
        this.contentLayoutId?.let { layoutId ->
            val layout = rootView?.findViewById<LinearLayout>(layoutId)
            layout?.let {
                it.removeAllViews()
                it.addView(GroupWidgetUI(this, entityId, context).groupsView())
            }
        }
    }

}


/**
 * Image Widget
 */
data class ImageWidget(val widgetId : WidgetId,
                       val format : ImageWidgetFormat,
                       val officialImageIds : MutableList<OfficialImageId>,
                       val icon : Maybe<Icon>) : Widget()
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<Widget>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<Widget> = when (doc)
        {
            is DocDict ->
            {
                apply(::ImageWidget,
                      // Widget Id
                      split(doc.maybeAt("id"),
                            effValue(WidgetId.random()),
                            { WidgetId.fromDocument(it) }),
                      // Format
                      split(doc.maybeAt("format"),
                            effValue(ImageWidgetFormat.default()),
                            { ImageWidgetFormat.fromDocument(it) }),
                      // Official Image Id
                      split(doc.maybeList("official_images"),
                            effValue(mutableListOf()),
                            { it.mapMut { OfficialImageId.fromDocument(it) } }),
                      // Icon
                      split(doc.maybeAt("icon"),
                            effValue<ValueError,Maybe<Icon>>(Nothing()),
                            { apply(::Just, Icon.fromDocument(it)) })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "id" to this.widgetId().toDocument(),
        "format" to this.format().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun format() : ImageWidgetFormat = this.format


    fun officialImageIds() : List<OfficialImageId> = this.officialImageIds


    fun icon() : Maybe<Icon> = this.icon


    // -----------------------------------------------------------------------------------------
    // WIDGET
    // -----------------------------------------------------------------------------------------

    override fun widgetFormat() : WidgetFormat = this.format().widgetFormat()


    override fun widgetId() : WidgetId = this.widgetId


    override fun view(groupContext : Maybe<GroupContext>,
                      rowLayoutType : RowLayoutType,
                      entityId : EntityId,
                      context : Context) : View
    {
        val viewBuilder = ImageWidgetUI(this, entityId, context)
        return viewBuilder.view()
    }


    // -----------------------------------------------------------------------------------------
    // PROD TYPE
    // -----------------------------------------------------------------------------------------
//
//    override fun onLoad() { }
//
//
//    override val prodTypeObject = this
//
//
//    override fun rowValue() : DB_WidgetImageValue =
//        RowValue3(widgetImageTable,
//                  PrimValue(this.widgetId),
//                  ProdValue(this.format),
//                  PrimValue(OfficialImageIdList(this.officialImageIds)))


    // -----------------------------------------------------------------------------------------
    // SHEET COMPONENT
    // -----------------------------------------------------------------------------------------

    override fun onSheetComponentActive(entityId : EntityId, context : Context) {
    }

}


/**
 * List Widget
 */
data class ListWidget(val widgetId : WidgetId,
                      val format : ListWidgetFormat,
                      val valuesVariableId : VariableId,
                      val titleVariableId : Maybe<VariableId>,
                      val description : Maybe<ListWidgetDescription>,
                      val bookReference : Maybe<BookReference>) : Widget()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var layoutViewId : Int? = null


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ListWidget>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<ListWidget> = when (doc)
        {
            is DocDict ->
            {
                apply(::ListWidget,
                      // Widget Id
                      split(doc.maybeAt("id"),
                            effValue(WidgetId.random()),
                            { WidgetId.fromDocument(it) }),
                      // Format
                      split(doc.maybeAt("format"),
                            effValue(ListWidgetFormat.default()),
                            { ListWidgetFormat.fromDocument(it) }),
                      // Values Variable Id
                      doc.at("values_variable_id") ap { VariableId.fromDocument(it) },
                      // Title Variable Id
                      split(doc.maybeAt("title_variable_id"),
                            effValue<ValueError,Maybe<VariableId>>(Nothing()),
                            { apply(::Just, VariableId.fromDocument(it)) }),
                      // Description
                      split(doc.maybeAt("description"),
                            effValue<ValueError,Maybe<ListWidgetDescription>>(Nothing()),
                            { apply(::Just, ListWidgetDescription.fromDocument(it)) }),
                      // Book Reference
                      split(doc.maybeAt("book_reference"),
                            effValue<ValueError,Maybe<BookReference>>(Nothing()),
                            { apply(::Just, BookReference.fromDocument(it)) })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "id" to this.widgetId().toDocument(),
        "format" to this.format().toDocument(),
        "values_variable_id" to this.valuesVariableId.toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun format() : ListWidgetFormat = this.format


    fun valuesVariableId() : VariableId = this.valuesVariableId


    fun titleVariableId() : Maybe<VariableId> = this.titleVariableId


    fun description() : Maybe<ListWidgetDescription> = this.description


    fun bookReference() : Maybe<BookReference> = this.bookReference


    // -----------------------------------------------------------------------------------------
    // WIDGET
    // -----------------------------------------------------------------------------------------

    override fun widgetFormat() : WidgetFormat = this.format().widgetFormat()


    override fun view(groupContext : Maybe<GroupContext>,
                      rowLayoutType : RowLayoutType,
                      entityId : EntityId,
                      context : Context) : View
    {
        val listWidgetUI = ListWidgetUI(this, entityId, context)
        return listWidgetUI.view()
    }


    override fun widgetId() = this.widgetId


    // -----------------------------------------------------------------------------------------
    // SHEET COMPONENT
    // -----------------------------------------------------------------------------------------

    override fun onSheetComponentActive(entityId : EntityId, context : Context) {
    }


    // -----------------------------------------------------------------------------------------
    // VALUES
    // -----------------------------------------------------------------------------------------

    fun variable(entityId : EntityId) : AppEff<TextListVariable> =
        textListVariable(this.valuesVariableId(), entityId)


    fun value(entityId : EntityId) : AppEff<List<String>> =
        textListVariable(this.valuesVariableId(), entityId)
          .apply { it.value(entityId) }


    fun valueIdStrings(entityId : EntityId) : AppEff<List<String>> =
            this.variable(entityId)
            .apply {
                note<AppError, ValueSetId>(it.valueSetId().toNullable(),
                        AppStateError(VariableDoesNotHaveValueSet(it.variableId())))
            }
            .apply { valueSetId ->
                this.value(entityId) ap { valueIds ->
                    valueIds.mapM { valueId ->
                        val valueRef = ValueReference(TextReferenceLiteral(valueSetId.value),
                                                      TextReferenceLiteral(valueId))
                        value(valueRef, entityId)
                    }
                }
            }
            .apply { values ->
                effValue<AppError,List<String>>(values.map { it.valueString() })
            }


//    fun baseValueSets(entityId : EntityId) : List<ValueSetBase> =
//        this.variable(entityId)
//        .apply {
//            note<AppError, ValueSetId>(it.valueSetId().toNullable(),
//                    AppStateError(VariableDoesNotHaveValueSet(it.variableId())))
//        }
//        .apply {
//            valueSet(it, entityId)
//        }


    // -----------------------------------------------------------------------------------------
    // TITLE
    // -----------------------------------------------------------------------------------------

    fun title(entityId : EntityId) : Maybe<String> =
        this.titleVariableId ap {
            val value = textVariable(it, entityId).apply { it.value(entityId) }
            when (value) {
                is Val -> value.value
                else    -> Nothing()
            }
        }


    // -----------------------------------------------------------------------------------------
    // UPDATE
    // -----------------------------------------------------------------------------------------

    fun update(listWidgetUpdate : WidgetUpdateListWidget,
               entityId : EntityId,
               rootView : View?,
               context : Context) =
        when (listWidgetUpdate)
        {
            is ListWidgetUpdateSetCurrentValue ->
            {
                this.updateCurrentValue(listWidgetUpdate.newCurrentValue, entityId)
                rootView?.let { this.updateView(rootView, entityId, context) }
            }
            is ListWidgetUpdateAddValue ->
            {
                this.updateAddValue(listWidgetUpdate.newValue, entityId)
                rootView?.let { this.updateView(rootView, entityId, context) }
            }
        }


    private fun updateCurrentValue(newCurrentValue : List<String>, entityId : EntityId)
    {
        val currentValueVariable = this.variable(entityId)
        when (currentValueVariable) {
            is Val -> currentValueVariable.value.updateLiteralValue(newCurrentValue, entityId)
            is Err -> ApplicationLog.error(currentValueVariable.error)
        }
    }


    private fun updateAddValue(newValue : String, entityId : EntityId)
    {
        val currentValueVariable = this.variable(entityId)
        when (currentValueVariable) {
            is Val -> currentValueVariable.value.addValue(newValue, entityId)
            is Err -> ApplicationLog.error(currentValueVariable.error)
        }
    }



    private fun updateView(rootView : View, entityId : EntityId, context : Context)
    {
        val layoutViewId = this.layoutViewId
        if (layoutViewId != null) {
            rootView.findViewById<LinearLayout>(layoutViewId)?.let {
                ListWidgetUI(this, entityId, context).updateView(it)
            }
        }
    }

}


/**
 * Log Widget
 */
data class LogWidget(private val widgetId : WidgetId,
                     private val format : LogWidgetFormat,
                     private val entries : MutableList<LogEntry>) : Widget()
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

//    constructor(widgetId : WidgetId,
//                format : LogWidgetFormat,
//                entries : List<LogEntry>)
//        : this(UUID.randomUUID(),
//               widgetId,
//               format,
//               entries.toMutableList())


    companion object : Factory<LogWidget>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<LogWidget> = when (doc)
        {
            is DocDict ->
            {
                apply(::LogWidget,
                      // Widget Id
                      split(doc.maybeAt("id"),
                            effValue(WidgetId.random()),
                            { WidgetId.fromDocument(it) }),
                      // Format
                      split(doc.maybeAt("format"),
                            effValue(LogWidgetFormat.default()),
                            { LogWidgetFormat.fromDocument(it) }),
                      // Entries
                      doc.list("entries") ap { docList ->
                          docList.mapMut { LogEntry.fromDocument(it) }
                      })
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "id" to this.widgetId().toDocument(),
        "format" to this.format().toDocument(),
        "entries" to DocList(this.entries.map { it.toDocument() })
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------


    fun format() : LogWidgetFormat = this.format


    fun entries() : List<LogEntry> = this.entries


    // -----------------------------------------------------------------------------------------
    // WIDGET
    // -----------------------------------------------------------------------------------------

    override fun widgetFormat() : WidgetFormat = this.format().widgetFormat()


    override fun view(groupContext : Maybe<GroupContext>,
                      rowLayoutType : RowLayoutType,
                      entityId : EntityId,
                      context : Context) : View {
        val viewBuilder = LogViewBuilder(this, entityId, context)
        return viewBuilder.view()
    }


    override fun widgetId() = this.widgetId


    // PROD TYPE
    // -----------------------------------------------------------------------------------------
//
//    override fun onLoad() { }
//
//
//    override val prodTypeObject = this
//
//
//    override fun rowValue() : DB_WidgetLogValue =
//        RowValue3(widgetLogTable,
//                  PrimValue(this.widgetId),
//                  ProdValue(this.format),
//                  CollValue(this.entries))
//

    // -----------------------------------------------------------------------------------------
    // SHEET COMPONENT
    // -----------------------------------------------------------------------------------------

    override fun onSheetComponentActive(entityId : EntityId, context : Context) {
    }

}


/**
 * Mechanic Widget
 */
data class MechanicWidget(private val widgetId : WidgetId,
                          private val format : MechanicWidgetFormat,
                          private val categoryReference : MechanicCategoryReference) : Widget()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var viewId : Int? = null


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

//    constructor(widgetId : WidgetId,
//                format : MechanicWidgetFormat,
//                categoryReference : MechanicCategoryReference)
//        : this(UUID.randomUUID(),
//               widgetId,
//               format,
//               categoryReference)


    companion object : Factory<MechanicWidget>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<MechanicWidget> = when (doc)
        {
            is DocDict ->
            {
                apply(::MechanicWidget,
                      // Widget Id
                      split(doc.maybeAt("id"),
                            effValue(WidgetId.random()),
                            { WidgetId.fromDocument(it) }),
                     // Format
                     split(doc.maybeAt("format"),
                           effValue(MechanicWidgetFormat.default()),
                           { MechanicWidgetFormat.fromDocument(it) }),
                     // Category Reference
                     doc.at("category_reference") ap {
                         MechanicCategoryReference.fromDocument(it) }
                     )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "id" to this.widgetId().toDocument(),
        "format" to this.format().toDocument(),
        "category_reference" to this.categoryReference.toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun format() : MechanicWidgetFormat = this.format


    fun categoryReference() : MechanicCategoryReference = this.categoryReference


    // -----------------------------------------------------------------------------------------
    // WIDGET
    // -----------------------------------------------------------------------------------------

    override fun widgetFormat() : WidgetFormat = this.format().widgetFormat()


    override fun view(groupContext : Maybe<GroupContext>,
                      rowLayoutType : RowLayoutType,
                      entityId : EntityId,
                      context : Context) : View
    {
        val viewBuilder = MechanicWidgetViewBuilder(this, entityId, context)
        return viewBuilder.view()
    }


    override fun widgetId() = this.widgetId


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------
//
//    override fun onLoad() { }
//
//
//    override val prodTypeObject = this
//
//
//    override fun rowValue() : DB_WidgetMechanicValue =
//        RowValue3(widgetMechanicTable,
//                  PrimValue(this.widgetId),
//                  ProdValue(this.format),
//                  PrimValue(this.categoryReference))


    // -----------------------------------------------------------------------------------------
    // SHEET COMPONENT
    // -----------------------------------------------------------------------------------------

    override fun onSheetComponentActive(entityId : EntityId, context : Context)
    {
        val sheetActivity = context as SheetActivity
        val rootView = sheetActivity.rootSheetView()

//        val mechanics = mechanicsInCategory(this.categoryId(), entityId)
//
//        when (mechanics) {
//            is Val -> {
//                mechanics.value.forEach { mechanic ->
//                    when (mechanic.mechanicType()) {
//                        is MechanicType.Option -> {
//                            mechanic.variables().forEach { optVar ->
//                                optVar.setOnUpdateListener {
//                                    rootView?.let {
//                                        this.updateView(it, entityId, context)
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//
//            }
//            is Err -> ApplicationLog.error(mechanics.error)
//        }
    }


    // -----------------------------------------------------------------------------------------
    // UPDATE
    // -----------------------------------------------------------------------------------------

    fun updateView(rootView : View, entityId : EntityId, context : Context)
    {
        val viewId = this.viewId
        if (viewId != null)
        {
            val layout = rootView.findViewById<LinearLayout>(viewId)
            if (layout != null) {
                MechanicWidgetViewBuilder(this, entityId, context).updateView(layout)
            }
        }
    }

}


/**
 * Number Widget
 */
data class NumberWidget(val widgetId : WidgetId,
                        val format : NumberWidgetFormat,
                        val valueVariableId : VariableId,
                        val insideLabel : Maybe<NumberWidgetLabel>,
                        val bookReference : Maybe<BookReference>)
                         : Widget()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var layoutId : Int? = null


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

//    constructor(widgetId : WidgetId,
//                format : NumberWidgetFormat,
//                valueVariableId : VariableId,
//                insideLabel : Maybe<NumberWidgetLabel>,
//                bookReference: Maybe<BookReference>)
//        : this(UUID.randomUUID(),
//               widgetId,
//               format,
//               valueVariableId,
//               insideLabel,
//               bookReference)


    companion object : Factory<NumberWidget>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<NumberWidget> = when (doc)
        {
            is DocDict ->
            {
                apply(::NumberWidget,
                      // Widget Id
                      split(doc.maybeAt("id"),
                            effValue(WidgetId.random()),
                            { WidgetId.fromDocument(it) }),
                      // Format
                      split(doc.maybeAt("format"),
                            effValue(NumberWidgetFormat.default()),
                            { NumberWidgetFormat.fromDocument(it) }),
                      // Value Variable Id
                      doc.at("value_variable_id") ap { VariableId.fromDocument(it) },
                      // Inside Label
                      split(doc.maybeAt("inside_label"),
                            effValue<ValueError,Maybe<NumberWidgetLabel>>(Nothing()),
                            { apply(::Just, NumberWidgetLabel.fromDocument(it)) }),
                      // Rulebook Referenece
                      split(doc.maybeAt("book_reference"),
                            effValue<ValueError,Maybe<BookReference>>(Nothing()),
                            { apply(::Just, BookReference.fromDocument(it)) })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "id" to this.widgetId().toDocument(),
        "format" to this.format().toDocument(),
        "value_variable_id" to this.valueVariableId().toDocument()))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun format() : NumberWidgetFormat = this.format


    fun valueVariableId() : VariableId = this.valueVariableId


    fun insideLabel() : Maybe<NumberWidgetLabel> = this.insideLabel


    fun bookReference() : Maybe<BookReference> = this.bookReference


    // -----------------------------------------------------------------------------------------
    // WIDGET
    // -----------------------------------------------------------------------------------------

    override fun widgetFormat() : WidgetFormat = this.format().widgetFormat()


    override fun view(groupContext : Maybe<GroupContext>,
                      rowLayoutType : RowLayoutType,
                      entityId : EntityId,
                      context : Context): View =
            NumberWidgetView.view(this, entityId, context)


    override fun widgetId() = this.widgetId


    override fun primaryAction(entityId : EntityId, context : Context)
    {
        val valueVariable = this.valueVariable(entityId)
        when (valueVariable)
        {
            is Val ->
            {
                openNumberVariableEditorDialog(valueVariable.value,
                                               UpdateTargetNumberWidget(this.widgetId),
                                               entityId,
                                               context)
            }
            is Err -> ApplicationLog.error(valueVariable.error)
        }
    }


    override fun secondaryAction(entityId : EntityId, context : Context)
    {
        when (this.bookReference) {
            is Just -> {
                val activity = context as AppCompatActivity
                val intent = Intent(activity, BookActivity::class.java)
                intent.putExtra("book_reference", bookReference.value)
                activity.startActivity(intent)
            }
        }
    }


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

//    override fun onLoad() { }
//
//
//    override val prodTypeObject = this
//
//
//    override fun rowValue() : DB_WidgetNumberValue =
//        RowValue5(widgetNumberTable,
//                  PrimValue(this.widgetId),
//                  ProdValue(this.format),
//                  PrimValue(this.valueVariableId),
//                  MaybePrimValue(this.insideLabel),
//                  MaybeProdValue(this.bookReference))
//

    // -----------------------------------------------------------------------------------------
    // SHEET COMPONENT
    // -----------------------------------------------------------------------------------------

    override fun onSheetComponentActive(entityId : EntityId, context : Context)
    {
        val sheetActivity = context as SheetActivity
        val rootView = sheetActivity.rootSheetView()

        this.valueVariable(entityId) apDo { currentValueVar ->
            val listener = VariableChangeListener({
                rootView?.let {
                    this.updateView(it, entityId, context)
                }
            }, {})

            addVariableChangeListener(currentValueVar.variableId(), listener, entityId)
        }
    }


    // -----------------------------------------------------------------------------------------
    // VARIABLE
    // -----------------------------------------------------------------------------------------

    fun valueVariable(entityId : EntityId) : AppEff<NumberVariable> =
        numberVariable(this.valueVariableId(), entityId)


    /**
     * The string representation of the widget's current value. This method returns 0 when the
     * value is null for some reason.
     */
    fun valueString(entityId : EntityId) : String
    {
        val numberString  = this.valueVariable(entityId)
                             .apply { it.valueString(entityId) }

        when (numberString)
        {
            is Val ->
            {
                return if (numberString.value == "")
                    "0"
                else
                    numberString.value
            }
            is Err -> {
                ApplicationLog.error(numberString.error)
            }
        }

        return "0"
    }


    /**
     * The string representation of the widget's current value. This method returns 0 when the
     * value is null for some reason.
     */
    fun value(entityId : EntityId) : Double
    {
        val num  = this.valueVariable(entityId)
                       .apply { it.value(entityId) }

        when (num)
        {
            is Val -> {
                val maybeNum = num.value
                when (maybeNum) {
                    is Just -> return maybeNum.value
                }
            }
            is Err -> ApplicationLog.error(num.error)
        }

        return 0.0
    }


    // -----------------------------------------------------------------------------------------
    // UPDATE
    // -----------------------------------------------------------------------------------------

    fun update(update : WidgetUpdateNumberWidget,
               entityId: EntityId,
               rootView : View?,
               context : Context) =
        when (update)
        {
            is NumberWidgetUpdateValue ->
            {
                val newValue = EngineValueNumber(update.newValue)
                updateVariable(this.valueVariableId(), newValue, entityId)

                rootView?.let { this.updateView(rootView, entityId, context) }
            }
        }


    private fun updateView(rootView : View, entityId : EntityId, context : Context)
    {
        this.layoutId?.let { layoutId ->
            rootView.findViewById<LinearLayout>(layoutId)?.let { layout ->
                NumberWidgetView.updateView(this, entityId, layout, context)
            }
        }
    }

}


/**
 * Option Widget
 */
//data class OptionWidget(override val id : UUID,
//                        val widgetId : Prim<WidgetId>,
//                        val format : Prod<OptionWidgetFormat>,
//                        val viewType : Prim<OptionViewType>,
//                        val description : Maybe<Prim<OptionDescription>>,
//                        val valueSetId : Prim<ValueSetId>) : Widget()
//{
//
//    // -----------------------------------------------------------------------------------------
//    // INIT
//    // -----------------------------------------------------------------------------------------
//
//    init
//    {
//        this.widgetId.name                          = "widget_id"
//        this.format.name                            = "format"
//        this.viewType.name                          = "view_type"
//
//        when (this.description) {
//            is Just -> this.description.value.name  = "description"
//        }
//
//        this.valueSetId.name                          = "value_set"
//    }
//
//
//    // -----------------------------------------------------------------------------------------
//    // CONSTRUCTORS
//    // -----------------------------------------------------------------------------------------
//
//    constructor(widgetId : WidgetId,
//                format : OptionWidgetFormat,
//                viewType : OptionViewType,
//                description : Maybe<OptionDescription>,
//                valueSet : ValueSetId)
//        : this(UUID.randomUUID(),
//                Prim(widgetId),
//                Prod(format),
//                Prim(viewType),
//                maybeLiftPrim(description),
//                Prim(valueSet))
//
//
//    companion object : Factory<OptionWidget>
//    {
//        override fun fromDocument(doc: SchemaDoc): ValueParser<OptionWidget> = when (doc)
//        {
//            is DocDict ->
//            {
//                effApply(::OptionWidget,
//                         // Widget Id
//                         doc.at("id") ap { WidgetId.fromDocument(it) },
//                         // Format
//                         split(doc.maybeAt("format"),
//                               effValue(OptionWidgetFormat.default()),
//                               { OptionWidgetFormat.fromDocument(it) }),
//                         // View Type
//                         split(doc.maybeAt("view_type"),
//                               effValue<ValueError,OptionViewType>(OptionViewType.NoArrows),
//                               { OptionViewType.fromDocument(it) }),
//                         // Description
//                         split(doc.maybeAt("description"),
//                               effValue<ValueError,Maybe<OptionDescription>>(Nothing()),
//                               { effApply(::Just, OptionDescription.fromDocument(it)) }),
//                         // ValueSet Name
//                         doc.at("value_set_id") ap { ValueSetId.fromDocument(it) }
//                         )
//            }
//            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
//        }
//    }
//
//
//    // -----------------------------------------------------------------------------------------
//    // TO DOCUMENT
//    // -----------------------------------------------------------------------------------------
//
//    override fun toDocument() = DocDict(mapOf(
//            "id" to this.widgetFormat().toDocument(),
//            "format" to this.format().toDocument(),
//            "view_type" to this.viewType().toDocument(),
//            "value_set_id" to this.valueSetId().toDocument()
//        ))
//        .maybeMerge(this.description().apply {
//            Just(Pair("description", it.toDocument() as SchemaDoc)) })
//
//
//    // -----------------------------------------------------------------------------------------
//    // GETTERS
//    // -----------------------------------------------------------------------------------------
//
//    fun widgetId() : WidgetId = this.widgetId.value
//
//    fun format() : OptionWidgetFormat = this.format.value
//
//    fun viewType() : OptionViewType = this.viewType.value
//
//    fun description() : Maybe<OptionDescription> = _getMaybePrim(this.description)
//
//    fun valueSetId() : ValueSetId = this.valueSetId.value
//
//
//    // -----------------------------------------------------------------------------------------
//    // WIDGET
//    // -----------------------------------------------------------------------------------------
//
//    override fun widgetFormat() : WidgetFormat = this.format().widgetFormat()
//
//    override fun view(sheetUIContext: SheetUIContext): View {
//        TODO("not implemented")
//    }
//
//
//    // -----------------------------------------------------------------------------------------
//    // MODEL
//    // -----------------------------------------------------------------------------------------
//
//    override fun onLoad() { }
//
//    override val name : String = "widget_option"
//
//    override val prodTypeObject = this
//
//    override fun persistentFunctors() : List<com.taletable.tome.lib.functor.Val<*>>
//    {
//        val l = mutableListOf(this.widgetId,
//                              this.format,
//                              this.viewType,
//                              this.valueSetId)
//
//        when (this.description) {
//            is Just -> l.add(this.description.value)
//        }
//
//        return l
//    }
//
//
//    // -----------------------------------------------------------------------------------------
//    // SHEET COMPONENT
//    // -----------------------------------------------------------------------------------------
//
//    override fun onSheetComponentActive(sheetUiContext : SheetUIContext) {
//        TODO("not implemented")
//    }
//
//}


/**
 * Points Widget
 */
data class PointsWidget(val widgetId : WidgetId,
                        val format : PointsWidgetFormat,
                        val limitValueVariableId : VariableId,
                        val currentValueVariableId : VariableId,
                        val label : Maybe<PointsWidgetLabel>) : Widget()
{


    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var layoutViewId : Int? = null

    // | Constructors
    // -----------------------------------------------------------------------------------------

    companion object : Factory<PointsWidget>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<PointsWidget> = when (doc)
        {
            is DocDict ->
            {
                apply(::PointsWidget,
                      // Widget Id
                      split(doc.maybeAt("id"),
                            effValue(WidgetId.random()),
                            { WidgetId.fromDocument(it) }),
                      // Format
                      split(doc.maybeAt("format"),
                            effValue(PointsWidgetFormat.default()),
                            { PointsWidgetFormat.fromDocument(it) }),
                      // Limit Value Variable Id
                      doc.at("limit_value_variable_id") ap { VariableId.fromDocument(it) },
                      // Current Value Variable Id
                      doc.at("current_value_variable_id") ap { VariableId.fromDocument(it) },
                      // Label
                      split(doc.maybeAt("label"),
                            effValue<ValueError,Maybe<PointsWidgetLabel>>(Nothing()),
                            { effApply(::Just, PointsWidgetLabel.fromDocument(it)) })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "id" to this.widgetId().toDocument(),
        "format" to this.format().toDocument(),
        "limit_value_variable_id" to this.limitValueVariableId().toDocument(),
        "current_value_variable_id" to this.limitValueVariableId().toDocument()
    ))
    .maybeMerge(this.label().apply {
        Just(Pair("labelString", it.toDocument() as SchemaDoc)) })


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun format() : PointsWidgetFormat = this.format


    fun limitValueVariableId() : VariableId = this.limitValueVariableId


    fun currentValueVariableId() : VariableId = this.currentValueVariableId


    fun label() : Maybe<PointsWidgetLabel> = this.label


    // -----------------------------------------------------------------------------------------
    // WIDGET
    // -----------------------------------------------------------------------------------------

    override fun widgetFormat() : WidgetFormat = this.format().widgetFormat()


    override fun view(groupContext : Maybe<GroupContext>,
                      rowLayoutType : RowLayoutType,
                      entityId : EntityId,
                      context : Context) : View
    {
        val viewBuilder = PointsWidgetViewBuilder(this, entityId, context)
        return viewBuilder.view()
    }


    override fun widgetId() = this.widgetId


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    fun currentValueVariable(entityId: EntityId) : AppEff<NumberVariable> =
        numberVariable(this.currentValueVariableId(), entityId)


    fun limitValueVariable(entityId: EntityId) : AppEff<NumberVariable> =
            numberVariable(this.limitValueVariableId(), entityId)

    fun limitValue(entityId : EntityId) : Double?
    {
        val mDouble = numberVariable(this.limitValueVariableId(), entityId)
                       .apply { it.value(entityId) }

        when (mDouble)
        {
            is Val ->
            {
                val dbl = mDouble.value
                return when (dbl)
                {
                    is Just    -> dbl.value
                    is Nothing -> null
                }
            }
            is Err -> ApplicationLog.error(mDouble.error)
        }

        return null
    }


    fun limitValueString(entityId : EntityId) : String?
    {
        val valueString = variable(this.limitValueVariableId(), entityId)
                            .apply { it.valueString(entityId) }

        when (valueString) {
            is Val -> return valueString.value
            is Err -> ApplicationLog.error(valueString.error)
        }

        return null
    }


    fun currentValue(entityId : EntityId) : Double?
    {
        val mDouble = numberVariable(this.currentValueVariableId(), entityId)
                       .apply { it.value(entityId) }

        when (mDouble)
        {
            is Val ->
            {
                val dbl = mDouble.value
                when (dbl) {
                    is Just    -> return dbl.value
                    is Nothing -> return null
                }
            }
            is Err -> ApplicationLog.error(mDouble.error)
        }

        return null
    }


    fun currentValueString(entityId : EntityId) : String?
    {
        val valueString = variable(this.currentValueVariableId(), entityId)
                            .ap { it.valueString(entityId) }

        when (valueString) {
            is Val -> return valueString.value
            is Err -> ApplicationLog.error(valueString.error)
        }

        return null
    }


    // -----------------------------------------------------------------------------------------
    // UPDATE
    // -----------------------------------------------------------------------------------------

    fun update(pointsWidgetUpdate : WidgetUpdatePointsWidget,
               entityId : EntityId,
               rootView : View?,
               context : Context) =
        when (pointsWidgetUpdate)
        {
            is PointsWidgetUpdateSetCurrentValue ->
            {
                this.updateCurrentValue(pointsWidgetUpdate.newCurrentValue, entityId)
                rootView?.let { this.updateView(rootView, entityId, context) }
            }
        }


    private fun updateCurrentValue(newCurrentValue : Double, entityId : EntityId)
    {
        val currentValueVariable = this.currentValueVariable(entityId)
        when (currentValueVariable) {
            is Val -> currentValueVariable.value.updateValue(newCurrentValue, entityId)
            is Err -> ApplicationLog.error(currentValueVariable.error)
        }
    }


    private fun updateView(rootView : View, entityId : EntityId, context : Context)
    {
        val layoutViewId = this.layoutViewId
        if (layoutViewId != null) {
            val layout = rootView.findViewById<LinearLayout>(layoutViewId)
            if (layout != null) {
                PointsWidgetViewBuilder(this, entityId, context).updateView(layout)
            }
        }
    }


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

//    override fun onLoad() { }
//
//
//    override val prodTypeObject = this
//
//
//    override fun rowValue() : DB_WidgetPointsValue =
//        RowValue5(widgetPointsTable,
//                  PrimValue(this.widgetId),
//                  ProdValue(this.format),
//                  PrimValue(this.limitValueVariableId),
//                  PrimValue(this.currentValueVariableId),
//                  MaybePrimValue(this.label))
//

    // -----------------------------------------------------------------------------------------
    // SHEET COMPONENT
    // -----------------------------------------------------------------------------------------

    override fun onSheetComponentActive(entityId : EntityId, context : Context)
    {
        val sheetActivity = context as SheetActivity
        val rootView = sheetActivity.rootSheetView()

        this.currentValueVariable(entityId) apDo { currentValueVar ->
            val listener = VariableChangeListener({
                rootView?.let {
                    this.updateView(it, entityId, context)
                }
            }, {})

            addVariableChangeListener(currentValueVar.variableId(), listener, entityId)
        }

        this.limitValueVariable(entityId) apDo { variable ->
            val listener = VariableChangeListener({
                rootView?.let {
                    this.updateView(it, entityId, context)
                }
            }, {})

            addVariableChangeListener(variable.variableId(), listener, entityId)
        }
    }

}


/**
 * Quote Widget
 */
data class QuoteWidget(val widgetId : WidgetId,
                       val format : QuoteWidgetFormat,
                       val quoteVariableId : VariableId,
                       val sourceVariableId : Maybe<VariableId>) : Widget()
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

//    constructor(widgetId : WidgetId,
//                format   : QuoteWidgetFormat,
//                quote    : VariableId,
//                source   : Maybe<VariableId>)
//        : this(UUID.randomUUID(),
//               widgetId,
//               format,
//               quote,
//               source)
//

    companion object : Factory<QuoteWidget>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<QuoteWidget> = when (doc)
        {
            is DocDict ->
            {
                apply(::QuoteWidget,
                      // Widget Id
                      split(doc.maybeAt("id"),
                            effValue(WidgetId.random()),
                            { WidgetId.fromDocument(it) }),
                      // Format
                      split(doc.maybeAt("format"),
                            effValue(QuoteWidgetFormat.default()),
                            { QuoteWidgetFormat.fromDocument(it) }),
                      // Quote Variable Id
                      doc.at("quote_variable_id") ap { VariableId.fromDocument(it) },
                      // Source Variable Id
                      split(doc.maybeAt("source_variable_id"),
                            effValue<ValueError,Maybe<VariableId>>(Nothing()),
                            { effApply(::Just, VariableId.fromDocument(it)) })
                     )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "id" to this.widgetId().toDocument(),
        "format" to this.format().toDocument(),
        "quote_variable_id" to this.quoteVariableId().toDocument()
        ))
        .maybeMerge(this.sourceVariableId().apply {
            Just(Pair("source_variable_id", it.toDocument() as SchemaDoc)) })


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun format() : QuoteWidgetFormat = this.format


    fun quoteVariableId() : VariableId = this.quoteVariableId


    fun sourceVariableId() : Maybe<VariableId> = this.sourceVariableId


    // -----------------------------------------------------------------------------------------
    // WIDGET
    // -----------------------------------------------------------------------------------------

    override fun widgetFormat() : WidgetFormat = this.format().widgetFormat()


    override fun view(groupContext : Maybe<GroupContext>,
                      rowLayoutType : RowLayoutType,
                      entityId : EntityId,
                      context : Context) : View
    {
        val viewBuilder = QuoteWidgetViewBuilder(this, entityId, context)
        return viewBuilder.view()
    }


    override fun widgetId() : WidgetId = this.widgetId


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    fun quoteVariable(entityId : EntityId) : AppEff<TextVariable> =
        textVariable(this.quoteVariableId(), entityId)


    fun source(entityId : EntityId) : Maybe<String>
    {
        val sourceVarId = this.sourceVariableId()
        when (sourceVarId)
        {
            is Just ->
            {
                val sourceString = textVariable(sourceVarId.value, entityId)
                                     .apply { it.valueString(entityId)  }
                when (sourceString)
                {
                    is Val -> return Just(sourceString.value)
                    is Err -> ApplicationLog.error(sourceString.error)
                }
            }
            is Nothing -> return Nothing()
        }

        return Nothing()
    }


    fun quote(entityId : EntityId) : String
    {
        val quoteString = this.quoteVariable(entityId)
                              .apply { it.valueString(entityId) }

        when (quoteString)
        {
            is Val -> return quoteString.value
            is Err -> ApplicationLog.error(quoteString.error)
        }

        return ""
    }


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

//    override fun onLoad() { }
//
//
//    override val prodTypeObject = this
//
//
//    override fun rowValue() : DB_WidgetQuoteValue =
//        RowValue4(widgetQuoteTable,
//                  PrimValue(this.widgetId),
//                  ProdValue(this.format),
//                  PrimValue(this.quoteVariableId),
//                  MaybePrimValue(this.sourceVariableId))


    // -----------------------------------------------------------------------------------------
    // SHEET COMPONENT
    // -----------------------------------------------------------------------------------------

    override fun onSheetComponentActive(entityId : EntityId, context : Context) { }

}


/**
 * Roll Widget
 */
data class RollWidget(val widgetId : WidgetId,
                      val format : RollWidgetFormat,
                      val rollGroup : DiceRollGroup,
                      val description : Maybe<RollWidgetDescription>,
                      val resultDescription : Maybe<RollWidgetResultDescription>) : Widget()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var layoutId : Int? = null


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<RollWidget>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<RollWidget> = when (doc)
        {
            is DocDict ->
            {
                apply(::RollWidget,
                      // Widget Id
                      split(doc.maybeAt("id"),
                            effValue(WidgetId.random()),
                            { WidgetId.fromDocument(it) }),
                      // Format
                      split(doc.maybeAt("format"),
                            effValue(RollWidgetFormat.default()),
                            { RollWidgetFormat.fromDocument(it) }),
                      // Roll Group
                      doc.at("roll_group") ap { DiceRollGroup.fromDocument(it) },
                      // Description
                      split(doc.maybeAt("description"),
                            effValue<ValueError,Maybe<RollWidgetDescription>>(Nothing()),
                            { apply(::Just, RollWidgetDescription.fromDocument(it)) }),
                      // Result Description
                      split(doc.maybeAt("result_description"),
                            effValue<ValueError,Maybe<RollWidgetResultDescription>>(Nothing()),
                            { apply(::Just, RollWidgetResultDescription.fromDocument(it)) })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "id" to this.widgetId().toDocument(),
        "format" to this.format().toDocument(),
        "roll_group" to this.rollGroup().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun format() : RollWidgetFormat = this.format


    fun rollGroup() : DiceRollGroup = this.rollGroup


    fun description() : Maybe<RollWidgetDescription> = this.description


    fun resultDescription() : Maybe<RollWidgetResultDescription> = this.resultDescription


    // -----------------------------------------------------------------------------------------
    // WIDGET
    // -----------------------------------------------------------------------------------------

    override fun widgetFormat() : WidgetFormat = this.format().widgetFormat()


    override fun view(groupContext : Maybe<GroupContext>,
                      rowLayoutType : RowLayoutType,
                      entityId : EntityId,
                      context : Context) : View {
        val viewBuilder = RollWidgetViewBuilder(this, entityId, context)
        return viewBuilder.view()
    }


    override fun widgetId() : WidgetId = this.widgetId


    // -----------------------------------------------------------------------------------------
    // SHEET COMPONENT
    // -----------------------------------------------------------------------------------------

    override fun onSheetComponentActive(entityId : EntityId, context : Context)
    {
        val sheetActivity = context as SheetActivity
        val rootView = sheetActivity.rootSheetView()

        val deps : MutableSet<VariableReference> = mutableSetOf()
        this.rollGroup().rollReferences().forEach {
            deps.addAll(it.dependencies(entityId))
        }

        deps.forEach { varRef ->
            variable(varRef, entityId) apDo { variable ->
                val listener = VariableChangeListener({
                    rootView?.let {
                        this.updateView(it, entityId, context)
                    }
                }, {})

                addVariableChangeListener(variable.variableId(), listener, entityId)
            }
        }

    }


    // -----------------------------------------------------------------------------------------
    // UPDATE
    // -----------------------------------------------------------------------------------------

    private fun updateView(rootView : View, entityId : EntityId, context : Context)
    {
        val layoutId = this.layoutId
        if (layoutId != null)
        {
            val layout = rootView.findViewById<LinearLayout>(layoutId)
            if (layout != null) {
                RollWidgetViewBuilder(this, entityId, context).updateContentView(layout)
            }
        }
    }

}


/**
 * Slider Widget
 */
data class WidgetSlider(val widgetId : WidgetId,
                        val format : SliderWidgetFormat,
                        val groups : List<Group>) : Widget()
{


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

//    constructor(widgetId : WidgetId,
//                format : SliderWidgetFormat,
//                groups : MutableList<Group>)
//        : this(UUID.randomUUID(),
//               widgetId,
//               format,
//               groups)


    companion object : Factory<Widget>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<Widget> = when (doc)
        {
            is DocDict ->
            {
                apply(::WidgetSlider,
                      // Widget Id
                      split(doc.maybeAt("id"),
                            effValue(WidgetId.random()),
                            { WidgetId.fromDocument(it) }),
                      // Format
                     split(doc.maybeAt("format"),
                           effValue(SliderWidgetFormat.default()),
                           { SliderWidgetFormat.fromDocument(it) }),
                      // Groups
                      doc.list("groups") ap { docList ->
                          docList.mapIndexed { d,index -> Group.fromDocument(d,index) }
                      })
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "id" to this.widgetId().toDocument(),
        "format" to this.format().toDocument(),
        "groups" to DocList(this.groups.map { it.toDocument() })
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun format() : SliderWidgetFormat = this.format


    fun groups() : List<Group> = this.groups


    // -----------------------------------------------------------------------------------------
    // GROUP
    // -----------------------------------------------------------------------------------------

    fun groupAtIndex(index : Int) : Maybe<Group> =
        if (this.groups.size > index)
            Just(this.groups[index]!!)
        else
            Nothing()


    // -----------------------------------------------------------------------------------------
    // WIDGET
    // -----------------------------------------------------------------------------------------

    override fun widgetFormat() : WidgetFormat = this.format().widgetFormat()


    override fun view(groupContext : Maybe<GroupContext>,
                      rowLayoutType : RowLayoutType,
                      entityId : EntityId,
                      context : Context) : View {
        val widgetUI = SliderWidgetUI(this, entityId, context)
        return widgetUI.view()
    }


    override fun widgetId() = this.widgetId


    // -----------------------------------------------------------------------------------------
    // SHEET COMPONENT
    // -----------------------------------------------------------------------------------------

    override fun onSheetComponentActive(entityId : EntityId, context : Context)
    {
        this.groups().forEach {
            it.onSheetComponentActive(entityId, context)
        }
    }

}


/**
 * Story Widget
 */
data class StoryWidget(val widgetId : WidgetId,
                       val format : StoryWidgetFormat,
                       val story : List<StoryPart>) : Widget()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var viewId : Int? = null


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------
//
//    constructor(widgetId : WidgetId,
//                format : StoryWidgetFormat,
//                story : List<StoryPart>)
//        : this(UUID.randomUUID(),
//               widgetId,
//               format,
//               story)


    companion object : Factory<StoryWidget>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<StoryWidget> = when (doc)
        {
            is DocDict ->
            {
                apply(::StoryWidget,
                      // Widget Id
                      split(doc.maybeAt("id"),
                            effValue(WidgetId.random()),
                            { WidgetId.fromDocument(it) }),
                      // Format
                      split(doc.maybeAt("format"),
                            effValue(StoryWidgetFormat.default()),
                            { StoryWidgetFormat.fromDocument(it) }),
                      // Story
                      doc.list("story") ap {
                          it.mapMut { StoryPart.fromDocument(it) }
                      })
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "id" to this.widgetId().toDocument(),
        "format" to this.format().toDocument(),
        "story" to DocList(this.story().map { it.toDocument() })
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun format() : StoryWidgetFormat = this.format


    fun story() : List<StoryPart> = this.story


    // -----------------------------------------------------------------------------------------
    // WIDGET
    // -----------------------------------------------------------------------------------------

    override fun widgetFormat() : WidgetFormat = this.format().widgetFormat()


    override fun view(groupContext : Maybe<GroupContext>,
                      rowLayoutType : RowLayoutType,
                      entityId : EntityId,
                      context : Context) : View
    {
        val viewBuilder = StoryWidgetViewBuilder(this, entityId, context)
        return viewBuilder.view()
    }


    override fun widgetId() : WidgetId = this.widgetId


    fun variables(entityId : EntityId) : Set<Variable> =
        this.variableParts().mapNotNull { part ->
            val variable = variable(part.variableId(), entityId)
            when (variable) {
                is Val -> variable.value
                is Err -> {
                    ApplicationLog.error(variable.error)
                    null
                }
            }
        }.toSet()


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    @Suppress("UNCHECKED_CAST")
    fun variableParts() : List<StoryPartVariable> =
            this.story().filter { it is StoryPartVariable } as List<StoryPartVariable>

    @Suppress("UNCHECKED_CAST")
    fun actionParts() : List<StoryPartAction> =
            this.story().filter { it is StoryPartAction } as List<StoryPartAction>


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

//    override fun onLoad() { }
//
//
//    override val prodTypeObject = this
//
//
//    override fun rowValue() : DB_WidgetStoryValue =
//        RowValue3(widgetStoryTable,
//                  PrimValue(this.widgetId),
//                  ProdValue(this.format),
//                  CollValue(this.story))


    // -----------------------------------------------------------------------------------------
    // UPDATE
    // -----------------------------------------------------------------------------------------

    fun update(storyWidgetUpdate : WidgetUpdateStoryWidget,
               entityId : EntityId,
               rootView : View?,
               context : Context)
    {
        when (storyWidgetUpdate)
        {
            is StoryWidgetUpdateNumberPart -> {
                this.updateNumberPart(storyWidgetUpdate, rootView, entityId, context)
            }
            is StoryWidgetUpdateTextPart ->
                this.updateTextPart(storyWidgetUpdate, rootView, entityId, context)
            is StoryWidgetUpdateTextValuePart ->
                this.updateTextValuePart(storyWidgetUpdate, rootView, entityId, context)
        }
    }


    private fun updateNumberPart(partUpdate : StoryWidgetUpdateNumberPart,
                                 rootView : View?,
                                 entityId : EntityId,
                                 context : Context)
    {
        val part = this.story()[partUpdate.partIndex]
        when (part)
        {
            is StoryPartVariable ->
            {
                // Update Value
                val variable = part.partVariable(entityId)
                when (variable) {
                    is NumberVariable ->
                    {
                        variable.updateValue(partUpdate.newNumber, entityId)
                    }
                }

                rootView?.let { this.updateView(rootView, entityId, context) }
            }
        }
    }


    private fun updateTextPart(partUpdate : StoryWidgetUpdateTextPart,
                               rootView : View?,
                               entityId : EntityId,
                               context : Context)
    {
        val part = this.story()[partUpdate.partIndex]
        when (part)
        {
            is StoryPartVariable ->
            {
                // Update Value
                val variable = part.partVariable(entityId)
                when (variable) {
                    is TextVariable ->
                    {
                        variable.updateValue(partUpdate.newValue, entityId)
                    }
                }

                rootView?.let { this.updateView(rootView, entityId, context) }
            }
        }
    }


    private fun updateTextValuePart(partUpdate : StoryWidgetUpdateTextValuePart,
                                    rootView : View?,
                                    entityId : EntityId,
                                    context : Context)
    {
        val part = this.story()[partUpdate.partIndex]
        when (part)
        {
            is StoryPartVariable ->
            {
                // Update Value
                val variable = part.partVariable(entityId)
                var newValue : String? = null
                when (variable) {
                    is TextVariable -> {
                        variable.updateValue(partUpdate.newValueId.value, entityId)
                        val updatedValue = variable.value(entityId)
                        when (updatedValue) {
                            is Val -> newValue = maybeValue("", updatedValue.value)
                        }
                    }
                }

                rootView?.let { this.updateView(rootView, entityId, context) }
            }
        }
    }


    // -----------------------------------------------------------------------------------------
    // SHEET COMPONENT
    // -----------------------------------------------------------------------------------------

    override fun onSheetComponentActive(entityId : EntityId, context : Context)
    {
        val sheetActivity = context as SheetActivity
        val rootView = sheetActivity.rootSheetView()


        this.variables(entityId).forEach {
            val listener = VariableChangeListener({
                rootView?.let {
                    this.updateView(it, entityId, context)
                }
            }, {})

            addVariableChangeListener(it.variableId(), listener, entityId)
        }
    }


    private fun updateView(rootView : View, entityId : EntityId, context : Context)
    {
        val viewId = this.viewId
        if (viewId != null) {
            val layout = rootView.findViewById<LinearLayout>(viewId)
            if (layout != null)
                StoryWidgetViewBuilder(this, entityId, context).updateView(layout)
        }
    }

}


/**
 * Tab Widget
 */
data class WidgetTab(val widgetId : WidgetId,
                     val format : TabWidgetFormat,
                     val tabs : List<Tab>) : Widget()
{

    // -----------------------------------------------------------------------------------------
    // | Properties
    // -----------------------------------------------------------------------------------------

    var groupContext : Maybe<GroupContext> = Nothing()


    // -----------------------------------------------------------------------------------------
    // | Constructors
    // -----------------------------------------------------------------------------------------

    companion object : Factory<Widget>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<Widget> = when (doc)
        {
            is DocDict ->
            {
                apply(::WidgetTab,
                      // Widget Id
                      split(doc.maybeAt("id"),
                            effValue(WidgetId.random()),
                            { WidgetId.fromDocument(it) }),
                      // Format
                      split(doc.maybeAt("format"),
                            effValue(TabWidgetFormat.default()),
                            { TabWidgetFormat.fromDocument(it) }),
                      // Tabs
                      doc.list("tabs") ap { docList ->
                         docList.mapMut { Tab.fromDocument(it) }
                     })
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "id" to this.widgetId().toDocument(),
        "format" to this.format().toDocument(),
        "tabs" to DocList(this.tabs.map { it.toDocument() })
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun format() : TabWidgetFormat = this.format


    fun tabs() : List<Tab> = this.tabs


    // -----------------------------------------------------------------------------------------
    // TABS
    // -----------------------------------------------------------------------------------------

    fun tabAtIndex(index : Int) : Maybe<Tab> =
        if (this.tabs.size > index)
            Just(this.tabs[index]!!)
        else
            Nothing()


    // -----------------------------------------------------------------------------------------
    // WIDGET
    // -----------------------------------------------------------------------------------------

    override fun widgetFormat() : WidgetFormat = this.format().widgetFormat()


    override fun view(groupContext : Maybe<GroupContext>,
                      rowLayoutType : RowLayoutType,
                      entityId : EntityId,
                      context : Context) : View {
        val widgetUI = TabWidgetUI(this, entityId, context)

        this.groupContext = groupContext

        return widgetUI.view()
    }


    override fun widgetId() = this.widgetId


    // -----------------------------------------------------------------------------------------
    // SHEET COMPONENT
    // -----------------------------------------------------------------------------------------

    override fun onSheetComponentActive(entityId : EntityId, context : Context)
    {
        this.tabs().forEach {
            it.groups(entityId).forEach {
                it.onSheetComponentActive(entityId, context)
            }
        }
    }

}


/**
 * Table Widget
 */
data class TableWidget(private val widgetId : WidgetId,
                       private val format : TableWidgetFormat,
                       private val columns : MutableList<TableWidgetColumn>,
                       private val rows : MutableList<TableWidgetRow>,
                       private val sort : Maybe<TableSort>,
                       private val titleVariableId : Maybe<VariableId>,
                       private val primaryColumnIndex : Maybe<PrimaryColumnIndex>,
                       private val bookReference : Maybe<BookReference>) : Widget()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var tableLayoutId : Int? = null
    var layoutId : Int? = null

    var selectedRow : Int? = null

    var editMode : Boolean = false

    var cachedRows : List<TableWidgetRow> = this.rows


    // -----------------------------------------------------------------------------------------
    // INDEXES
    // -----------------------------------------------------------------------------------------

    //private var namespaceColumn : Int? = null

    private val numberCellById : MutableMap<UUID, TableWidgetNumberCell> = mutableMapOf()

    private val textCellById : MutableMap<UUID,TableWidgetTextCell> = mutableMapOf()


    init {
        this.rows().forEach { row ->
            row.cells().forEach { cell ->
                when (cell) {
                    is TableWidgetNumberCell -> this.numberCellById.put(cell.id, cell)
                    is TableWidgetTextCell   -> {
                        this.textCellById.put(cell.id, cell)
                    }
                }
            }
        }
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<TableWidget>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<TableWidget> = when (doc)
        {
            is DocDict ->
            {
                apply(::TableWidget,
                      // Widget Id
                      split(doc.maybeAt("id"),
                            effValue(WidgetId.random()),
                            { WidgetId.fromDocument(it) }),
                     // Format
                     split(doc.maybeAt("format"),
                             effValue(TableWidgetFormat.default()),
                             { TableWidgetFormat.fromDocument(it) }),
                     // Columns
                     doc.list("columns") ap { docList ->
                         docList.mapMut { TableWidgetColumn.fromDocument(it) }
                     },
                     // Rows
                     split(doc.maybeList("rows"),
                           effValue(mutableListOf()),
                           { it.mapMut { TableWidgetRow.fromDocument(it) } }),
                     // Table Sort
                     split(doc.maybeAt("sort"),
                           effValue<ValueError, Maybe<TableSort>>(Nothing()),
                           { apply(::Just, TableSort.fromDocument(it)) }),
                     // Title Variable Id
                     split(doc.maybeAt("title_variable_id"),
                           effValue<ValueError,Maybe<VariableId>>(Nothing()),
                           { apply(::Just, VariableId.fromDocument(it)) }),
                     // Primary Column Index
                     split(doc.maybeAt("primary_column_index"),
                           effValue<ValueError,Maybe<PrimaryColumnIndex>>(Nothing()),
                           { apply(::Just, PrimaryColumnIndex.fromDocument(it)) }),
                     // Book Reference
                     split(doc.maybeAt("book_reference"),
                           effValue<ValueError,Maybe<BookReference>>(Nothing()),
                           { apply(::Just, BookReference.fromDocument(it)) })
                     )
            }
            else -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "id" to this.widgetId().toDocument(),
        "format" to this.format().toDocument(),
        "columns" to DocList(this.columns().map { it.toDocument() }),
        "rows" to DocList(this.rows().map { it.toDocument() })
        ))
        .maybeMerge(this.sort().apply {
            Just(Pair("sort", it.toDocument() as SchemaDoc)) })


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun format() : TableWidgetFormat = this.format


    fun columns( ): List<TableWidgetColumn> = this.columns


    fun rows() : List<TableWidgetRow> = this.rows


    fun cachedRows() : List<TableWidgetRow> = this.cachedRows


    fun sort() : Maybe<TableSort> = this.sort


    fun titleVariableId() : Maybe<VariableId> = this.titleVariableId


    fun primaryColumnIndex() : Maybe<PrimaryColumnIndex> =
            this.primaryColumnIndex.ap { Just(PrimaryColumnIndex(it.value - 1)) }


    fun bookReference() : Maybe<BookReference> = this.bookReference


    // -----------------------------------------------------------------------------------------
    // WIDGET
    // -----------------------------------------------------------------------------------------

    override fun widgetFormat(): WidgetFormat = this.format().widgetFormat()


    override fun widgetId() = this.widgetId


    // -----------------------------------------------------------------------------------------
    // SET
    // -----------------------------------------------------------------------------------------

    fun rowSetVariable(entityId: EntityId) : AppEff<TextListVariable>
    {
        val maybeSetVariableId = this.primaryColumnIndex()
                                     .apply { maybe(this.columns.getOrNull(it.value)) }
                                     .apply { it.textColumn() }
                                     .apply { it.columnVariableId() }

        return when (maybeSetVariableId) {
            is Just    -> textListVariable(maybeSetVariableId.value, entityId)
            is Nothing -> effError(AppSheetError(TableWidgetDoesNotHaveColumnVariableId(this.widgetId)))
        }
    }


    private fun valueSetId(entityId : EntityId) : AppEff<Maybe<ValueSetId>> =
        this.rowSetVariable(entityId).apply {
            effValue<AppError,Maybe<ValueSetId>>(it.valueSetId())
        }


    fun rowsInSet(values : List<Value>, valueSetId : Maybe<ValueSetId>) : List<TableWidgetRow>
    {

        Log.d("***WIDGET", "rows in set: ${valueSetId}")

        val rows : MutableList<TableWidgetRow> = mutableListOf()

        val rowByValueId : MutableMap<String,TableWidgetRow> = mutableMapOf()

        // Index rows
        for (row in this.cachedRows())
        {
            Log.d("***WIDGET", "${valueSetId} CACHED row")
            val maybePrimaryCell = this.primaryColumnIndex()
                                       .apply { maybe(row.cells().getOrNull(it.value)) }
            maybePrimaryCell.doMaybe { cell ->
                when (cell) {
                    is TableWidgetTextCell -> {
                        val cellValueId = cell.valueId
                        if (cellValueId != null)
                        {
                            rowByValueId[cellValueId.value] = row
                        }
                        else
                        {
                            val cellVariableValue = cell.value()
                            when (cellVariableValue) {
                                is TextCellValueValue -> {
                                    val variableValue = cellVariableValue.value
                                    when (variableValue) {
                                        is TextVariableLiteralValue -> {
                                            rowByValueId[variableValue.value] = row
                                        }
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }

        for (value in values)
        {
            val valueIdString = value.valueId().value

            if (rowByValueId.containsKey(valueIdString))
            {
                val row = rowByValueId[valueIdString]!!
                this.primaryColumnIndex().doMaybe {
                    row.cells().getOrNull(it.value)?.let { cell ->
                        when (cell) {
                            is TableWidgetTextCell -> {
                                cell.valueId = value.valueId()

                                when (valueSetId) {
                                    is Just -> {
                                        Log.d("***WIDGET", "creating dynamic cell with value reference for value set: ${valueSetId}")
                                        val valueReference = ValueReference(valueSetId.value, value.valueId())
                                        cell.value = TextCellValueValue(TextVariableValueValue(valueReference))
                                    }
                                    is Nothing -> {
                                        cell.value = TextCellValueValue(TextVariableLiteralValue(value.valueString()))
                                    }
                                }
                            }
                        }
                    }
                }
                rows.add(row)
            }
            else
            {
                val defaultRow = when (valueSetId) {
                    is Just -> {
                        val valueReference = ValueReference(valueSetId.value, value.valueId())
                        this.defaultTableRow(value.valueString(), valueReference)
                    }
                    else -> {
                        this.defaultTableRow(value.valueString())
                    }
                }

                rows.add(defaultRow)
            }
        }

        return rows
    }


    // -----------------------------------------------------------------------------------------
    // TITLE
    // -----------------------------------------------------------------------------------------

    fun title(entityId : EntityId) : Maybe<String> =
        this.titleVariableId ap {
            val value = textVariable(it, entityId).apply { it.value(entityId) }
            when (value) {
                is Val -> value.value
                else    -> Nothing()
            }
        }


    // -----------------------------------------------------------------------------------------
    // UPDATE VIEW
    // -----------------------------------------------------------------------------------------

    fun update(tableWidgetUpdate : WidgetUpdateTableWidget,
               entityId : EntityId,
               rootView : View?,
               context : Context) =
        when (tableWidgetUpdate)
        {
            is TableWidgetUpdateSetNumberCell ->
            {
                rootView?.let { this.updateNumberCellView(tableWidgetUpdate, it) }
                this.updateNumberCellValue(tableWidgetUpdate, entityId)
            }
            is TableWidgetUpdateSetTextCellValue ->
            {
                this.updateTextCellValueValue(tableWidgetUpdate,
                                              rootView,
                                              entityId)
            }
            is TableWidgetUpdateInsertRowBefore ->
            {
                rootView?.let {
                    this.addRow(tableWidgetUpdate.selectedRow, rootView, entityId, context)
                }
            }
            is TableWidgetUpdateInsertRowAfter ->
            {
                rootView?.let {
                    this.addRow(tableWidgetUpdate.selectedRow + 1, rootView, entityId, context)
                }
            }
            is TableWidgetUpdateSubset ->
            {
                // Update row set variable
                this.rowSetVariable(entityId).apDo {
                    it.updateLiteralValue(tableWidgetUpdate.values, entityId)
                }

                // Reset state
                this.removeTableFromState(entityId)
                this.addTableToState(entityId, context)

                rootView?.let { this.updateView(rootView, entityId, context) }
            }
        }


    private fun updateView(rootView : View, entityId : EntityId, context : Context)
    {
        val layoutId = this.layoutId
        if (layoutId != null)
        {
            rootView.findViewById<LinearLayout>(layoutId)?.let { layout ->
                TableWidgetUI(this, entityId, context).updateView(layout)
            }
        }
    }


    private fun updateNumberCellView(numberCellUpdate : TableWidgetUpdateSetNumberCell,
                                     rootView: View) {
        val numberCell = this.numberCellById[numberCellUpdate.cellId]

        numberCell?.viewId?.let {
            val textView = rootView.findViewById<TextView>(it)
            textView?.text = Util.doubleString(numberCellUpdate.newNumber)
        }
    }


    private fun updateNumberCellValue(numberCellUpdate : TableWidgetUpdateSetNumberCell,
                                      entityId : EntityId)
    {
        val numberCell = this.numberCellById[numberCellUpdate.cellId]
        numberCell?.updateValue(numberCellUpdate.newNumber, entityId)
    }


    private fun updateTextCellValueValue(cellUpdate : TableWidgetUpdateSetTextCellValue,
                                         rootView : View?,
                                         entityId : EntityId)
    {
        val cell = this.textCellById[cellUpdate.cellId]


        // Update Variable
        val variable = cell?.valueVariable(entityId)
        var newValue : String? = null
        when (variable)
        {
            is Val ->
            {
                val textVariable = variable.value
                textVariable.updateValue(cellUpdate.newValueId.value, entityId)
                val updatedValue = textVariable.value(entityId)
                when (updatedValue) {
                    is Val -> newValue = maybeValue("", updatedValue.value)
                }
            }
            is Err -> ApplicationLog.error(variable.error)
        }

        // Update View
        cell?.viewId?.let {
            val textView = rootView?.findViewById<TextView>(it)
            textView?.text = newValue
        }
    }


    private fun addRow(rowIndex : Int, rootView : View, entityId : EntityId, context : Context)
    {
        val tableLayoutId = this.tableLayoutId

        if (tableLayoutId != null)
        {
            val tableLayout = rootView.findViewById<TableLayout>(tableLayoutId)
            if (tableLayout != null)
            {
                val newTableRow = this.defaultTableRow(null)
                this.rows.add(rowIndex, newTableRow)

                this.updateTableVariables(rowIndex + 1 , entityId)

                this.cachedRows().getOrNull(rowIndex)?.let {
                    this.addRowToState(it, rowIndex, entityId, context)
                }
                // need to update all variables
                val rowView = newTableRow.view(this, entityId, context)
                tableLayout.addView(rowView, rowIndex + 1)

                val selectedRowIndex = this.selectedRow
                if (selectedRowIndex != null) {
                    if (rowIndex <= selectedRowIndex)
                        this.selectedRow = selectedRowIndex + 1
                }
            }
        }
    }


    // -----------------------------------------------------------------------------------------
    // SHEET COMPONENT
    // -----------------------------------------------------------------------------------------

    override fun onSheetComponentActive(entityId : EntityId, context : Context)
    {
        this.addTableToState(entityId, context)
    }


    // -----------------------------------------------------------------------------------------
    // STATE
    // -----------------------------------------------------------------------------------------

    private fun addTableToState(entityId : EntityId, context : Context)
    {
        val rows = this.dynamicRows(entityId)
        rows.forEachIndexed { index, row ->
            this.addRowToState(row, index, entityId, context)
        }

        this.cachedRows = rows
    }


    private fun removeTableFromState(entityId : EntityId)
    {
        for (row in this.cachedRows()) {
            removeRowFromState(row, entityId)
        }
    }


    // -----------------------------------------------------------------------------------------
    // VIEW
    // -----------------------------------------------------------------------------------------

    override fun view(groupContext : Maybe<GroupContext>,
                      rowLayoutType : RowLayoutType,
                      entityId : EntityId,
                      context : Context) : View
    {
        val tableWidgetUI = TableWidgetUI(this, entityId, context)
        return tableWidgetUI.view()
    }


    // -----------------------------------------------------------------------------------------
    // CELLS
    // -----------------------------------------------------------------------------------------

    private fun addBooleanCellVariable(booleanCell : TableWidgetBooleanCell,
                                       rowIndex : Int,
                                       cellIndex : Int,
                                       entityId : EntityId,
                                       context : Context) : Variable
    {
        val column = this.columns()[cellIndex]
        val variableId = this.cellVariableId(column.variablePrefixString(), rowIndex)
        val variable = BooleanVariable(variableId,
                                       VariableLabel(column.nameString()),
                                       VariableDescription(column.nameString()),
                                       listOf(),
                                       column.variableRelation(),
                                       booleanCell.variableValue(),
                                       VariableIsContextual(false))

        val listener = VariableChangeListener({
            booleanCell.updateView(entityId, context)
        }, {})

        addVariableChangeListener(variableId, listener, entityId)

        addVariable(variable, entityId)
        booleanCell.variableId = variableId

        return variable
    }


    // Number Cell
    // -----------------------------------------------------------------------------------------

    private fun addNumberCellVariable(numberCell : TableWidgetNumberCell,
                                      rowIndex : Int,
                                      cellIndex : Int,
                                      entityId : EntityId,
                                      context : Context) : Variable
    {
        val column = this.columns()[cellIndex]
        val variableId = this.cellVariableId(column.variablePrefixString(), rowIndex)
        val variable = NumberVariable(variableId,
                                      VariableLabel(column.nameString()),
                                      VariableDescription(column.nameString()),
                                      listOf(),
                                      column.variableRelation(),
                                      numberCell.variableValue())

        val listener = VariableChangeListener({
            numberCell.updateView(entityId, context)
        }, {})

        addVariableChangeListener(variableId, listener, entityId)

        addVariable(variable, entityId)
        numberCell.variableId = variableId

        return variable
    }


    private fun addTextCellVariable(textCell : TableWidgetTextCell,
                                    rowIndex : Int,
                                    cellIndex : Int,
                                    primaryVariable : Variable?,
                                    entityId : EntityId,
                                    context : Context) : Variable
    {
        val column = this.columns()[cellIndex] as TableWidgetTextColumn
        val variableId = this.cellVariableId(column.variablePrefixString(), rowIndex) //, namespace)

        val textCellValue = textCell.value()
        val variable = when (textCellValue) {
            is TextCellValueValue -> {
                TextVariable(variableId,
                             VariableLabel(column.nameString()),
                             VariableDescription(column.nameString()),
                             listOf(),
                             column.variableRelation(),
                             textCellValue.value)
            }
            is TextCellValueRelation -> {

                if (primaryVariable != null)
                {
                    Log.d("***WIDGET", "primary variable: ${primaryVariable}")
                    val value = primaryVariable.relatedVariableIdOrError(textCellValue.relation) apply { relVarId ->
                        Log.d("***WIDGET", "found related var id")
                    variable(relVarId, entityId)                        apply { variable ->
                        variable.valueString(entityId)
                    } }

                    when (value) {
                        is Val -> {
                            Log.d("***WIDGET", "found related value")
                            TextVariable(variableId,
                                 VariableLabel(column.nameString()),
                                 VariableDescription(column.nameString()),
                                 listOf(),
                                 column.variableRelation(),
                                 TextVariableLiteralValue(value.value))
                        }
                        is Err -> {
                            Log.d("***WIDGET", "could not find related value")
                            TextVariable(variableId)
                        }
                    }
                }
                else {
                    Log.d("***WIDGET", "could not find primary variable")
                    TextVariable(variableId)
                }
            }
        }


        variable.addTags(column.tags().toSet())

        val listener = VariableChangeListener({
            textCell.updateView(entityId, context)
        }, {})

        addVariableChangeListener(variableId, listener, entityId)

        addVariable(variable, entityId)
        textCell.variableId = variableId

        return variable
    }


    private fun cellVariableId(variablePrefix : String,
                               rowIndex : Int) : VariableId =
            VariableId(variablePrefix + "_row_" + rowIndex.toString())


    // -----------------------------------------------------------------------------------------
    // ROWS
    // -----------------------------------------------------------------------------------------

    fun dynamicRows(entityId : EntityId) : List<TableWidgetRow>
    {
        val rowSetVariableEff = this.rowSetVariable(entityId)
        return when (rowSetVariableEff)
        {
            is Val ->
            {
                val rowSetVariable = rowSetVariableEff.value
                val setValues = rowSetVariable.values(entityId)
                this.rowsInSet(setValues, rowSetVariable.valueSetId())
            }
            is Err ->
            {
                this.rows()
            }
        }
    }


    private fun defaultTableRow(primaryValue : String?, valueReference : ValueReference? = null) : TableWidgetRow
    {
        val cells : MutableList<TableWidgetCell> = mutableListOf()

        val primaryColumnIndex = this.primaryColumnIndex()
        when (primaryColumnIndex)
        {
            is Just ->
            {
                this.columns().forEachIndexed { index, column ->

                    if (index == primaryColumnIndex.value.value)
                    {
                        if (valueReference != null)
                        {
                            val variableValue = TextVariableValueValue(valueReference)
                            cells.add(TableWidgetTextCell(TextCellValueValue(variableValue)))
                        }
                        else if (primaryValue != null)
                        {
                            val variableValue = TextVariableLiteralValue(primaryValue)
                            cells.add(TableWidgetTextCell(TextCellValueValue(variableValue)))
                        }
                    }
                    else
                    {
                        when (column)
                        {
                            is TableWidgetBooleanColumn ->
                                cells.add(TableWidgetBooleanCell(column.defaultValue()))
                            is TableWidgetNumberColumn ->
                                cells.add(TableWidgetNumberCell(column.defaultValue()))
                            is TableWidgetTextColumn ->
                                cells.add(TableWidgetTextCell(column.defaultValue()))
                        }
                    }
                }
            }
            is Nothing ->
            {
                this.columns().forEach {
                    when (it)
                    {
                        is TableWidgetBooleanColumn ->
                            cells.add(TableWidgetBooleanCell(it.defaultValue()))
                        is TableWidgetNumberColumn ->
                            cells.add(TableWidgetNumberCell(it.defaultValue()))
                        is TableWidgetTextColumn ->
                            cells.add(TableWidgetTextCell(it.defaultValue()))
                    }
                }
            }
        }

        return TableWidgetRow(cells)
    }


    private fun addRowToState(row : TableWidgetRow,
                              rowIndex : Int,
                              entityId : EntityId,
                              context : Context)
    {
        val rowVariables : MutableList<Variable> = mutableListOf()

        if (row.cells().size != this.columns().size) return

        var primaryVariable : Variable? = null

        val primaryColumnIndex : Int? = primaryColumnIndex().toNullable()?.value

        primaryColumnIndex?.let { index ->
            row.cells().getOrNull(index)?.let { cell ->
                when (cell) {
                    is TableWidgetBooleanCell ->
                    {
                        val booleanVar = addBooleanCellVariable(cell, rowIndex, index, entityId, context)
                        rowVariables.add(booleanVar)
                        primaryVariable = booleanVar
                    }
                    is TableWidgetNumberCell ->
                    {
                        this.numberCellById[cell.id] = cell
                        val numberVar = addNumberCellVariable(cell, rowIndex, index, entityId, context)
                        rowVariables.add(numberVar)
                        primaryVariable = numberVar
                    }
                    is TableWidgetTextCell ->
                    {
                        this.textCellById[cell.id] = cell
                        val textVar = addTextCellVariable(cell, rowIndex, index, null, entityId, context)
                        rowVariables.add(textVar)
                        primaryVariable = textVar
                    }
                    else -> { }
                }
            }
        }

        row.cells().forEachIndexed { cellIndex, cell ->
            if (!(primaryColumnIndex != null && primaryColumnIndex == cellIndex)) {
                when (cell) {
                    is TableWidgetBooleanCell ->
                    {
                        val booleanVar = addBooleanCellVariable(cell, rowIndex, cellIndex, entityId, context)
                        rowVariables.add(booleanVar)
                    }
                    is TableWidgetNumberCell ->
                    {
                        this.numberCellById[cell.id] = cell
                        val numberVar = addNumberCellVariable(cell, rowIndex, cellIndex, entityId, context)
                        rowVariables.add(numberVar)
                    }
                    is TableWidgetTextCell ->
                    {
                        this.textCellById[cell.id] = cell
                        val textVar = addTextCellVariable(cell, rowIndex, cellIndex, primaryVariable, entityId, context)
                        rowVariables.add(textVar)
                    }
                }
            }

        }

        rowVariables.forEachIndexed { i, iVar ->
            rowVariables.forEachIndexed { j, jVar ->
                val relation = jVar.relation()
                when (relation) {
                    is Just -> {
                        iVar.setRelation(relation.value, jVar.variableId(), entityId)
                    }
                }
            }
        }
    }


    private fun removeRowFromState(row : TableWidgetRow, entityId : EntityId)
    {
        for (cell in row.cells())
        {
            entityEngineState(entityId).apDo { entityState ->
                cell.variableId()?.let { cellVarId ->
                    entityState.removeVariable(cellVarId)
                }
            }
        }
    }


    private fun updateTableVariables(fromIndex : Int, entityId : EntityId)
    {
        for (rowIndex in (this.rows().size - 1) downTo fromIndex)
        {
            val row = this.rows()[rowIndex]
            row.cells().forEachIndexed { cellIndex, cell ->
                val column = this.columns()[cellIndex]
                when (cell) {
                    is TableWidgetBooleanCell ->
                    {
                        val newVarId = cellVariableId(column.variablePrefixString(), rowIndex) //, null)

                        cell.valueVariable(entityId)              apDo { boolVar ->
                            updateVariableId(boolVar.variableId(), newVarId, entityId)
                        }

                        cell.variableId = newVarId
                    }
                    is TableWidgetNumberCell ->
                    {
                        val newVarId = cellVariableId(column.variablePrefixString(), rowIndex)
                        cell.valueVariable(entityId) apDo { numVar ->
                            updateVariableId(numVar.variableId(), newVarId, entityId)
                        }
                        cell.variableId = newVarId
                    }
                    is TableWidgetTextCell ->
                    {
                        val newVarId = cellVariableId(column.variablePrefixString(), rowIndex)
                        cell.valueVariable(entityId) apDo { textVar ->
                            updateVariableId(textVar.variableId(), newVarId, entityId)
                        }
                        cell.variableId = newVarId
                    }
                }
            }
        }
    }

}


/**
 * Text Widget
 */
data class TextWidget(val widgetId : WidgetId,
                      val format : TextWidgetFormat,
                      val valueVariableId : VariableId,
                      val bookReference : Maybe<BookReference>,
                      val primaryActionWidgetId : Maybe<WidgetId>,
                      val secondaryActionWigdetId : Maybe<WidgetId>)
                       : Widget()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var layoutId : Int? = null


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(format : TextWidgetFormat,
                valueVariableId : VariableId)
        : this(WidgetId(UUID.randomUUID()),
               format,
               valueVariableId,
               Nothing(),
               Nothing(),
               Nothing())


    companion object : Factory<TextWidget>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<TextWidget> = when (doc)
        {
            is DocDict ->
            {
                apply(::TextWidget,
                      // Widget Id
                      split(doc.maybeAt("id"),
                            effValue(WidgetId.random()),
                            { WidgetId.fromDocument(it) }),
                      // Format
                      split(doc.maybeAt("format"),
                            effValue(TextWidgetFormat.default()),
                            { TextWidgetFormat.fromDocument(it) }),
                      // Value
                      doc.at("value_variable_id") ap { VariableId.fromDocument(it) },
                      // Book Reference
                      split(doc.maybeAt("book_reference"),
                            effValue<ValueError,Maybe<BookReference>>(Nothing()),
                            { apply(::Just, BookReference.fromDocument(it)) }),
                      // Primary Action Widget Id
                      split(doc.maybeAt("primary_action_widget_id"),
                            effValue<ValueError,Maybe<WidgetId>>(Nothing()),
                            { apply(::Just, WidgetId.fromDocument(it)) }),
                      // Secondary Action Widget Id
                      split(doc.maybeAt("secondary_action_widget_id"),
                            effValue<ValueError,Maybe<WidgetId>>(Nothing()),
                            { apply(::Just, WidgetId.fromDocument(it)) })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "id" to this.widgetId().toDocument(),
        "format" to this.format().toDocument(),
        "value_variable_id" to this.valueVariableId().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun format() : TextWidgetFormat = this.format


    fun valueVariableId() : VariableId = this.valueVariableId


    fun rulebookReference() : Maybe<BookReference> = this.bookReference


    fun primaryActionWidgetId() : Maybe<WidgetId> = this.primaryActionWidgetId


    fun secondaryActionWidgetId() : Maybe<WidgetId> = this.secondaryActionWigdetId


    // -----------------------------------------------------------------------------------------
    // WIDGET
    // -----------------------------------------------------------------------------------------

    override fun widgetFormat() : WidgetFormat = this.format().widgetFormat()


    override fun view(groupContext : Maybe<GroupContext>,
                      rowLayoutType : RowLayoutType,
                      entityId : EntityId,
                      context : Context) : View =
        TextWidgetView.view(this, this.format(), rowLayoutType, entityId, context)


    override fun widgetId() : WidgetId = this.widgetId


    override fun primaryAction(entityId : EntityId, context : Context)
    {
        val valueVar = this.valueVariable(entityId)
        when (valueVar) {
            is effect.Val ->
            {
                openTextVariableEditorDialog(valueVar.value,
                                             UpdateTargetTextWidget(this.widgetId),
                                             entityId,
                                             context)
//                val viewId = this.viewId
//                if (viewId != null)
//                {
//                    val widgetReference = WidgetReference(this.id, viewId)
                //}
            }
            is Err -> ApplicationLog.error(valueVar.error)
        }
    }


    override fun secondaryAction(entityId : EntityId, context : Context)
    {
        when (this.bookReference) {
            is Just -> {
                val activity = context as AppCompatActivity
                val intent = Intent(activity, BookActivity::class.java)
                intent.putExtra("book_reference", bookReference.value)
                activity.startActivity(intent)
            }
        }
    }


    // -----------------------------------------------------------------------------------------
    // UPDATE
    // -----------------------------------------------------------------------------------------

    fun update(update : WidgetUpdateTextWidget,
               entityId : EntityId,
               rootView : View?,
               context : Context) =
        when (update)
        {
            is TextWidgetUpdateSetText ->
            {
                this.updateValue(update.newText, entityId)

                rootView?.let {
                    this.updateView(rootView, entityId, context)
                }
            }
        }


    fun updateValue(newText : String, entityId : EntityId)
    {
        val textVariable = this.valueVariable(entityId)
        when (textVariable) {
            is Val -> textVariable.value.updateValue(newText, entityId)
            is Err -> ApplicationLog.error(textVariable.error)
        }
    }


    private fun updateView(rootView : View, entityId : EntityId, context : Context)
    {
        val layoutId = this.layoutId
        if (layoutId != null)
        {
            try {
                val layout = rootView.findViewById<LinearLayout>(layoutId)
                TextWidgetView.updateView(this, entityId, layout, context)
            }
            catch (e: TypeCastException) {
            }
        }
    }



    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    fun valueVariable(entityId : EntityId) : AppEff<TextVariable> =
        textVariable(this.valueVariableId(), entityId)


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

//    override fun onLoad() { }
//
//
//    override val prodTypeObject = this
//
//
//    override fun rowValue() : DB_WidgetTextValue =
//        RowValue4(widgetTextTable,
//                  PrimValue(this.widgetId),
//                  ProdValue(this.format),
//                  PrimValue(this.valueVariableId),
//                  MaybeProdValue(this.bookReference))
//

    // -----------------------------------------------------------------------------------------
    // SHEET COMPONENT
    // -----------------------------------------------------------------------------------------

    override fun onSheetComponentActive(entityId : EntityId, context : Context)
    {
        val sheetActivity = context as SheetActivity
        val rootView = sheetActivity.rootSheetView()

        this.valueVariable(entityId) apDo { currentValueVar ->

            val listener = VariableChangeListener({
                rootView?.let {
                    this.updateView(it, entityId, context)
                }
            }, {})

            addVariableChangeListener(currentValueVar.variableId(), listener, entityId)
        }
    }


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    /**
     * The string representation of the widget's current value.
     */
    fun valueString(entityId : EntityId) : String
    {
        val str = this.valueVariable(entityId)
                      .apply { it.valueString(entityId) }

        when (str)
        {
            is Val -> return str.value
            is Err -> ApplicationLog.error(str.error)
        }

        return ""
    }




}



