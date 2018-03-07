
package com.kispoko.tome.model.sheet.widget


import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.PaintDrawable
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.activity.sheet.SheetActivity
import com.kispoko.tome.activity.sheet.SheetActivityGlobal
import com.kispoko.tome.app.AppEff
import com.kispoko.tome.app.AppError
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.db.*
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.*
import com.kispoko.tome.lib.orm.schema.*
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.lib.ui.LinearLayoutBuilder
import com.kispoko.tome.model.book.BookReference
import com.kispoko.tome.model.game.engine.dice.DiceRollGroup
import com.kispoko.tome.model.game.engine.mechanic.MechanicCategoryReference
import com.kispoko.tome.model.game.engine.procedure.Procedure
import com.kispoko.tome.model.game.engine.procedure.ProcedureId
import com.kispoko.tome.model.game.engine.variable.*
import com.kispoko.tome.model.sheet.group.Group
import com.kispoko.tome.model.sheet.style.BorderEdge
import com.kispoko.tome.model.sheet.style.Height
import com.kispoko.tome.model.sheet.style.Width
import com.kispoko.tome.model.sheet.widget.table.*
import com.kispoko.tome.rts.entity.*
import com.kispoko.tome.rts.entity.sheet.*
import com.kispoko.tome.util.Util
import effect.*
import effect.Val
import lulo.document.*
import lulo.value.*
import lulo.value.UnexpectedType
import maybe.Just
import maybe.Maybe
import maybe.Nothing
import maybe.maybeValue
import java.io.Serializable
import java.util.*



/**
 * Widget
 */
@Suppress("UNCHECKED_CAST")
sealed class Widget : ToDocument, ProdType, SheetComponent, Serializable
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
//                    "widget_image"    -> ImageWidget.fromDocument(doc)
//                                            as ValueParser<Widget>
                    "widget_list"     -> ListWidget.fromDocument(doc)
                                            as ValueParser<Widget>
                    "widget_log"      -> LogWidget.fromDocument(doc)
                                            as ValueParser<Widget>
                    "widget_mechanic" -> MechanicWidget.fromDocument(doc)
                                            as ValueParser<Widget>
                    "widget_number"   -> NumberWidget.fromDocument(doc)
                                            as ValueParser<Widget>
//                    "widget_option"   -> OptionWidget.fromDocument(doc)
//                                            as ValueParser<Widget>
                    "widget_points"   -> PointsWidget.fromDocument(doc)
                                            as ValueParser<Widget>
                    "widget_quote"    -> QuoteWidget.fromDocument(doc)
                                            as ValueParser<Widget>
                    "widget_roll"    -> RollWidget.fromDocument(doc)
                                            as ValueParser<Widget>
                    "widget_story"    -> StoryWidget.fromDocument(doc)
                                            as ValueParser<Widget>
                    "widget_table"    -> TableWidget.fromDocument(doc)
                                            as ValueParser<Widget>
//                    "widget_tab"      -> TabWidget.fromDocument(doc)
//                                            as ValueParser<Widget>
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


    abstract fun view(entityId : EntityId, context : Context) : View

}


object WidgetView
{

    fun layout(widgetFormat : WidgetFormat,
               entityId : EntityId,
               context : Context) : LinearLayout
    {
        val layout = this.widgetLayout(widgetFormat, entityId, context)

        val contentLayout = this.contentLayout(widgetFormat, context)

        layout.addView(contentLayout)

        val rightBorder = widgetFormat.elementFormat().border().right()
        when (rightBorder) {
            is Just -> layout.addView(this.verticalBorderView(rightBorder.value, entityId, context))
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

        when (widgetFormat.elementFormat().border().right()) {
            is Just -> layout.orientation = LinearLayout.HORIZONTAL
        }


        val width = widgetFormat.elementFormat().width()
        when (width) {
            is Width.Justify -> {
                layout.width            = 0
                layout.weight           = widgetFormat.width().toFloat()
            }
            is Width.Wrap -> {
                layout.width = LinearLayout.LayoutParams.WRAP_CONTENT
            }
            is Width.Fixed -> {
                layout.widthDp  = width.value.toInt()
            }
        }

        val height = widgetFormat.elementFormat().height()
        when (height)
        {
            is Height.Wrap  -> layout.height   = LinearLayout.LayoutParams.WRAP_CONTENT
            is Height.Fixed -> layout.heightDp = height.value.toInt()
        }


        layout.marginSpacing    = widgetFormat.elementFormat().margins()

        layout.backgroundColor  = colorOrBlack(widgetFormat.elementFormat().backgroundColorTheme(),
                                               entityId)

        layout.corners          = widgetFormat.elementFormat().corners()

        return layout.linearLayout(context)
    }


    fun widgetTouchLayout(widgetFormat : WidgetFormat,
                          entityId : EntityId,
                          context : Context) : LinearLayout
    {
        val layout = WidgetTouchView(context)

        layout.orientation = LinearLayout.VERTICAL

        val layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.weight = widgetFormat.width().toFloat()

        val margins = widgetFormat.elementFormat().margins()
        layoutParams.leftMargin = margins.leftPx()
        layoutParams.rightMargin = margins.rightPx()
        layoutParams.topMargin = margins.topPx()
        layoutParams.bottomMargin = margins.bottomPx()

        layout.layoutParams = layoutParams

        val padding = widgetFormat.elementFormat().padding()
        layout.setPadding(padding.leftPx(),
                          padding.topPx(),
                          padding.rightPx(),
                          padding.bottomPx())


        // Background
        val bgDrawable = PaintDrawable()

        val corners = widgetFormat.elementFormat().corners()
        val topLeft  = Util.dpToPixel(corners.topLeftCornerRadiusDp()).toFloat()
        val topRight : Float   = Util.dpToPixel(corners.topRightCornerRadiusDp()).toFloat()
        val bottomRight : Float = Util.dpToPixel(corners.bottomRightCornerRadiusDp()).toFloat()
        val bottomLeft :Float = Util.dpToPixel(corners.bottomLeftCornerRadiusDp()).toFloat()

        val radii = floatArrayOf(topLeft, topLeft, topRight, topRight,
                         bottomRight, bottomRight, bottomLeft, bottomLeft)

        bgDrawable.setCornerRadii(radii)

        val bgColor = colorOrBlack(widgetFormat.elementFormat().backgroundColorTheme(), entityId)

        bgDrawable.colorFilter = PorterDuffColorFilter(bgColor, PorterDuff.Mode.SRC_IN)

        layout.background = bgDrawable

        return layout
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
data class WidgetId(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<WidgetId>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<WidgetId> = when (doc)
        {
            is DocText -> effValue(WidgetId(doc.text))
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

    override fun asSQLValue() : SQLValue = SQLText({this.value})

}


/**
 * Action Widget
 */
data class ActionWidget(override val id : UUID,
                       val widgetId : WidgetId,
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

    constructor(widgetId : WidgetId,
                format : ActionWidgetFormat,
                procedureId : ProcedureId,
                activeVariableId : Maybe<VariableId>,
                description : Maybe<ActionWidgetDescription>)
        : this(UUID.randomUUID(),
               widgetId,
               format,
               procedureId,
               activeVariableId,
               description)


    companion object : Factory<ActionWidget>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<ActionWidget> = when (doc)
        {
            is DocDict ->
            {
                apply(::ActionWidget,
                      // Widget Id
                      doc.at("id") ap { WidgetId.fromDocument(it) },
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

    fun widgetId() : WidgetId = this.widgetId


    fun format() : ActionWidgetFormat = this.format


    fun procedureId() : ProcedureId = this.procedureId


    fun activeVariableId() : Maybe<VariableId> = this.activeVariableId


    fun description() : Maybe<ActionWidgetDescription> = this.description


    // -----------------------------------------------------------------------------------------
    // WIDGET
    // -----------------------------------------------------------------------------------------

    override fun widgetFormat() : WidgetFormat = this.format().widgetFormat()


    override fun view(entityId : EntityId, context : Context) : View {
        val viewBuilder = ActionWidgetViewBuilder(this, entityId, context)
        return viewBuilder.view()
    }


    // -----------------------------------------------------------------------------------------
    // PROD TYPE
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_WidgetActionValue =
        RowValue4(widgetActionTable,
                  PrimValue(this.widgetId),
                  ProdValue(this.format),
                  PrimValue(this.procedureId),
                  MaybePrimValue(this.description))


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
                        variable.value.setOnUpdateListener {
                            rootView?.let {
                                this.updateView(it, entityId, context)
                            }
                        }
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
            val layout = rootView.findViewById(layoutViewId) as LinearLayout?
            layout?.removeAllViews()
            layout?.addView(ActionWidgetViewBuilder(this, entityId, context).inlineLeftButtonView())
        }
    }


}


/**
 * Boolean Widget
 */
data class BooleanWidget(override val id : UUID,
                         private val widgetId : WidgetId,
                         private val format : BooleanWidgetFormat,
                         private val valueVariablesReference : VariableReference) : Widget()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var layoutId : Int? = null


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(widgetId : WidgetId,
                format : BooleanWidgetFormat,
                valueVariablesReference : VariableReference)
        : this(UUID.randomUUID(),
               widgetId,
               format,
               valueVariablesReference)


    companion object : Factory<Widget>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<Widget> = when (doc)
        {
            is DocDict ->
            {
                apply(::BooleanWidget,
                      // Widget Id
                      doc.at("id") ap { WidgetId.fromDocument(it) },
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

    fun widgetId() : WidgetId = this.widgetId


    fun format() : BooleanWidgetFormat = this.format


    fun valueVariablesReference() : VariableReference = this.valueVariablesReference


    // -----------------------------------------------------------------------------------------
    // WIDGET
    // -----------------------------------------------------------------------------------------

    override fun widgetFormat() : WidgetFormat = this.format().widgetFormat()


    override fun view(entityId : EntityId, context : Context) : View
    {
        val viewBuilder = BooleanWidgetViewBuilder(this, entityId, context)
        return viewBuilder.view()
    }


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_WidgetBooleanValue =
        RowValue3(widgetBooleanTable,
                  PrimValue(this.widgetId),
                  ProdValue(this.format),
                  PrimValue(this.valueVariablesReference))


    // -----------------------------------------------------------------------------------------
    // SHEET COMPONENT
    // -----------------------------------------------------------------------------------------

    override fun onSheetComponentActive(entityId : EntityId, context : Context)
    {
        val sheetActivity = context as SheetActivity
        val rootView = sheetActivity.rootSheetView()

        this.variables(entityId) apDo {
            it.forEach {
                it.setOnUpdateListener {
                    rootView?.let {
                        this.updateView(it, entityId, context)
                    }
                }
            }
        }
    }


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    fun variables(entityId : EntityId) : AppEff<Set<BooleanVariable>> =
        variables(this.valueVariablesReference, entityId)
          .apply { effValue<AppError,Set<BooleanVariable>>(filterBooleanVariables(it)) }


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


//    fun variable(sheetContext : SheetContext) : AppEff<BooleanVariable>
//    {
//        val variables = this.variables(sheetContext)
//        return when (variables) {
//            is Val -> {
//                val first = variables.value.toList().firstOrNull()
//                if (first != null) {
//                    when (first) {
//                        is BooleanVariable -> effValue(first)
//                        else               -> effError<AppError,BooleanVariable>(AppStateError(VariableIsOfUnexpectedType(sheetContext.sheetId,
//                                                                                                first.variableId(),
//                                                                                                VariableType.BOOLEAN,
//                                                                                                first.type())))
//                    }
//                }
//                else {
//                    effError<AppError,BooleanVariable>(AppStateError(VariableDoesNotExist(sheetContext.sheetId, this.valueVariablesReference)))
//                }
//            }
//            is Err -> {
//                effError(variables.error)
//            }
//        }
//
//    }


    fun variableValue(entityId : EntityId) : AppEff<Boolean> =
        this.variables(entityId)
            .apply { if (it.isNotEmpty()) {
                        it.mapM { it.value() }
                     }
                     else {
                        effValue(setOf())
                     }
            }
            .apply { effValue<AppError,Boolean>(it.all { it }) }


    // -----------------------------------------------------------------------------------------
    // UPDATE
    // -----------------------------------------------------------------------------------------

    fun update(booleanWidgetUpdate : WidgetUpdateBooleanWidget,
               entityId: EntityId,
               rootView : View,
               context : Context) =
        when (booleanWidgetUpdate)
        {
            is BooleanWidgetUpdateToggle ->
            {
                this.toggleValues(entityId)
                this.updateView(rootView, entityId, context)
            }
        }


    private fun updateView(rootView : View, entityId : EntityId, context : Context)
    {
        val viewId = this.layoutId
        if (viewId != null) {
            val layout = rootView.findViewById(viewId) as LinearLayout?
            if (layout != null)
                BooleanWidgetViewBuilder(this, entityId, context).updateView(layout)
        }
    }


    fun toggleValues(entityId : EntityId)
    {
        val booleanVariables = this.variables(entityId)
        when (booleanVariables) {
            is Val -> {
                booleanVariables.value.forEach {
                    it.toggleValue(entityId)
                }
            }
            is Err -> ApplicationLog.error(booleanVariables.error)
        }
    }


}


/**
 * Expander Widget
 */
data class ExpanderWidget(override val id : UUID,
                          val widgetId : WidgetId,
                          val format : ExpanderWidgetFormat,
                          val header : ExpanderWidgetLabel,
                          val groups : List<Group>) : Widget()
{


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(widgetId : WidgetId,
                format : ExpanderWidgetFormat,
                header : ExpanderWidgetLabel,
                groups : MutableList<Group>)
        : this(UUID.randomUUID(),
               widgetId,
               format,
               header,
               groups)


    companion object : Factory<Widget>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<Widget> = when (doc)
        {
            is DocDict ->
            {
                apply(::ExpanderWidget,
                      // Widget Name
                      doc.at("id") ap { WidgetId.fromDocument(it) },
                      // Format
                     split(doc.maybeAt("format"),
                           effValue(ExpanderWidgetFormat.default()),
                           { ExpanderWidgetFormat.fromDocument(it) }),
                      // Label
                      doc.at("label") ap { ExpanderWidgetLabel.fromDocument(it) },
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
        "label" to this.label().toDocument(),
        "groups" to DocList(this.groups.map { it.toDocument() })
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun widgetId() : WidgetId = this.widgetId


    fun format() : ExpanderWidgetFormat = this.format


    fun label() : ExpanderWidgetLabel = this.header


    fun groups() : List<Group> = this.groups


    // -----------------------------------------------------------------------------------------
    // WIDGET
    // -----------------------------------------------------------------------------------------

    override fun widgetFormat() : WidgetFormat = this.format().widgetFormat()


    override fun view(entityId : EntityId, context : Context) : View {
        val viewBuilder = ExpanderWidgetViewBuilder(this, entityId, context)
        return viewBuilder.view()
    }


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_WidgetExpanderValue =
        RowValue4(widgetExpanderTable,
                  PrimValue(this.widgetId),
                  ProdValue(this.format),
                  PrimValue(this.header),
                  CollValue(this.groups))


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
 * Image Widget
 */
//data class ImageWidget(override val id : UUID,
//                       val widgetId : Prim<WidgetId>,
//                       val format : Prod<ImageWidgetFormat>) : Widget()
//{
//
//    // -----------------------------------------------------------------------------------------
//    // INIT
//    // -----------------------------------------------------------------------------------------
//
//    init
//    {
//        this.widgetId.name  = "widget_id"
//        this.format.name    = "format"
//    }
//
//
//    // -----------------------------------------------------------------------------------------
//    // CONSTRUCTORS
//    // -----------------------------------------------------------------------------------------
//
//    constructor(widgetId : WidgetId,
//                format : ImageWidgetFormat)
//        : this(UUID.randomUUID(),
//               Prim(widgetId),
//               Prod(format))
//
//
//    companion object : Factory<Widget>
//    {
//        override fun fromDocument(doc : SchemaDoc) : ValueParser<Widget> = when (doc)
//        {
//            is DocDict ->
//            {
//                effApply(::ImageWidget,
//                         // Widget Name
//                         doc.at("id") ap { WidgetId.fromDocument(it) },
//                         // Format
//                         doc.at("format") ap { ImageWidgetFormat.fromDocument(it) }
//                        )
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
//        "id" to this.widgetId().toDocument(),
//        "format" to this.format().toDocument()
//    ))
//
//
//    // -----------------------------------------------------------------------------------------
//    // GETTERS
//    // -----------------------------------------------------------------------------------------
//
//    fun widgetId() : WidgetId = this.widgetId.value
//
//    fun format() : ImageWidgetFormat = this.format.value
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
//    override val name : String = "widget_image"
//
//    override val prodTypeObject = this
//
//    override fun persistentFunctors() : List<com.kispoko.tome.lib.functor.Val<*>> =
//            listOf(this.widgetId,
//                   this.format)
//
//
//    // -----------------------------------------------------------------------------------------
//    // SHEET COMPONENT
//    // -----------------------------------------------------------------------------------------
//
//    override fun onSheetComponentActive(sheetUIContext: SheetUIContext) {
//        TODO("not implemented")
//    }
//
//}


/**
 * List Widget
 */
data class ListWidget(override val id : UUID,
                      val widgetId : WidgetId,
                      val format : ListWidgetFormat,
                      val valuesVariableId : VariableId,
                      val description : Maybe<ListWidgetDescription>) : Widget()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var layoutViewId : Int? = null


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(widgetId : WidgetId,
                format : ListWidgetFormat,
                valuesVariableId : VariableId,
                description : Maybe<ListWidgetDescription>)
        : this(UUID.randomUUID(),
               widgetId,
               format,
               valuesVariableId,
               description)


    companion object : Factory<ListWidget>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<ListWidget> = when (doc)
        {
            is DocDict ->
            {
                apply(::ListWidget,
                    // Widget Name
                    doc.at("id") ap { WidgetId.fromDocument(it) },
                    // Format
                    split(doc.maybeAt("format"),
                          effValue(ListWidgetFormat.default()),
                          { ListWidgetFormat.fromDocument(it) }),
                    // Values Variable Id
                    doc.at("values_variable_id") ap { VariableId.fromDocument(it) },
                    // Description
                    split(doc.maybeAt("description"),
                          effValue<ValueError,Maybe<ListWidgetDescription>>(Nothing()),
                          { apply(::Just, ListWidgetDescription.fromDocument(it)) })
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

    fun widgetId() : WidgetId = this.widgetId


    fun format() : ListWidgetFormat = this.format


    fun valuesVariableId() : VariableId = this.valuesVariableId


    fun description() : Maybe<ListWidgetDescription> = this.description


    // -----------------------------------------------------------------------------------------
    // WIDGET
    // -----------------------------------------------------------------------------------------

    override fun widgetFormat() : WidgetFormat = this.format().widgetFormat()


    override fun view(entityId : EntityId, context : Context) : View
    {
        val viewBuilder = ListWidgetViewBuilder(this, entityId, context)
        return viewBuilder.view()
    }


    // -----------------------------------------------------------------------------------------
    // PROD TYPE
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_WidgetListValue =
        RowValue3(widgetListTable,
                  PrimValue(this.widgetId),
                  ProdValue(this.format),
                  PrimValue(this.valuesVariableId))


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


    // -----------------------------------------------------------------------------------------
    // UPDATE
    // -----------------------------------------------------------------------------------------

    fun update(listWidgetUpdate : WidgetUpdateListWidget,
               rootView : View,
               entityId : EntityId,
               context : Context) =
        when (listWidgetUpdate)
        {
            is ListWidgetUpdateSetCurrentValue ->
            {
                this.updateCurrentValue(listWidgetUpdate.newCurrentValue, entityId)
                this.updateView(rootView, entityId, context)
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


    private fun updateView(rootView : View, entityId : EntityId, context : Context)
    {
        val layoutViewId = this.layoutViewId
        if (layoutViewId != null) {
            val layout = rootView.findViewById(layoutViewId) as LinearLayout?
            layout?.removeAllViews()
            layout?.addView(ListWidgetViewBuilder(this, entityId, context).inlineView())
        }
    }

}


/**
 * Log Widget
 */
data class LogWidget(override val id : UUID,
                     private val widgetId : WidgetId,
                     private val format : LogWidgetFormat,
                     private val entries : MutableList<LogEntry>) : Widget()
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(widgetId : WidgetId,
                format : LogWidgetFormat,
                entries : List<LogEntry>)
        : this(UUID.randomUUID(),
               widgetId,
               format,
               entries.toMutableList())


    companion object : Factory<LogWidget>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<LogWidget> = when (doc)
        {
            is DocDict ->
            {
                apply(::LogWidget,
                      // Widget Name
                      doc.at("id") ap { WidgetId.fromDocument(it) },
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

    fun widgetId() : WidgetId = this.widgetId

    fun format() : LogWidgetFormat = this.format

    fun entries() : List<LogEntry> = this.entries


    // -----------------------------------------------------------------------------------------
    // WIDGET
    // -----------------------------------------------------------------------------------------

    override fun widgetFormat() : WidgetFormat = this.format().widgetFormat()


    override fun view(entityId : EntityId, context : Context) : View {
        val viewBuilder = LogViewBuilder(this, entityId, context)
        return viewBuilder.view()
    }


    // PROD TYPE
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_WidgetLogValue =
        RowValue3(widgetLogTable,
                  PrimValue(this.widgetId),
                  ProdValue(this.format),
                  CollValue(this.entries))


    // -----------------------------------------------------------------------------------------
    // SHEET COMPONENT
    // -----------------------------------------------------------------------------------------

    override fun onSheetComponentActive(entityId : EntityId, context : Context) {
    }

}


/**
 * Mechanic Widget
 */
data class MechanicWidget(override val id : UUID,
                          private val widgetId : WidgetId,
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

    constructor(widgetId : WidgetId,
                format : MechanicWidgetFormat,
                categoryReference : MechanicCategoryReference)
        : this(UUID.randomUUID(),
               widgetId,
               format,
               categoryReference)


    companion object : Factory<MechanicWidget>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<MechanicWidget> = when (doc)
        {
            is DocDict ->
            {
                apply(::MechanicWidget,
                     // Widget Id
                     doc.at("id") ap { WidgetId.fromDocument(it) },
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

    fun widgetId() : WidgetId = this.widgetId


    fun format() : MechanicWidgetFormat = this.format


    fun categoryReference() : MechanicCategoryReference = this.categoryReference


    // -----------------------------------------------------------------------------------------
    // WIDGET
    // -----------------------------------------------------------------------------------------

    override fun widgetFormat() : WidgetFormat = this.format().widgetFormat()


    override fun view(entityId : EntityId, context : Context) : View
    {
        val viewBuilder = MechanicWidgetViewBuilder(this, entityId, context)
        return viewBuilder.view()
    }


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_WidgetMechanicValue =
        RowValue3(widgetMechanicTable,
                  PrimValue(this.widgetId),
                  ProdValue(this.format),
                  PrimValue(this.categoryReference))


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
            val layout = rootView.findViewById(viewId) as LinearLayout?
            if (layout != null) {
                MechanicWidgetViewBuilder(this, entityId, context).updateView(layout)
            }
        }
    }

}


/**
 * Number Widget
 */
data class NumberWidget(override val id : UUID,
                        val widgetId : WidgetId,
                        val format : NumberWidgetFormat,
                        val valueVariableId : VariableId,
                        val insideLabel : Maybe<NumberWidgetLabel>,
                        val rulebookReference : Maybe<BookReference>)
                         : Widget()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var textViewId : Int? = null


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(widgetId : WidgetId,
                format : NumberWidgetFormat,
                valueVariableId : VariableId,
                insideLabel : Maybe<NumberWidgetLabel>,
                rulebookReference: Maybe<BookReference>)
        : this(UUID.randomUUID(),
               widgetId,
               format,
               valueVariableId,
               insideLabel,
               rulebookReference)


    companion object : Factory<NumberWidget>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<NumberWidget> = when (doc)
        {
            is DocDict ->
            {
                apply(::NumberWidget,
                      // Widget Id
                      doc.at("id") ap { WidgetId.fromDocument(it) },
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
                      split(doc.maybeAt("rulebook_reference"),
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

    fun widgetId() : WidgetId = this.widgetId


    fun format() : NumberWidgetFormat = this.format


    fun valueVariableId() : VariableId = this.valueVariableId


    fun insideLabel() : Maybe<NumberWidgetLabel> = this.insideLabel


    fun rulebookReference() : Maybe<BookReference> = this.rulebookReference


    // -----------------------------------------------------------------------------------------
    // WIDGET
    // -----------------------------------------------------------------------------------------

    override fun widgetFormat() : WidgetFormat = this.format().widgetFormat()


    override fun view(entityId: EntityId, context : Context): View =
            NumberWidgetView.view(this, this.format(), entityId, context)


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_WidgetNumberValue =
        RowValue5(widgetNumberTable,
                  PrimValue(this.widgetId),
                  ProdValue(this.format),
                  PrimValue(this.valueVariableId),
                  MaybePrimValue(this.insideLabel),
                  MaybeProdValue(this.rulebookReference))


    // -----------------------------------------------------------------------------------------
    // SHEET COMPONENT
    // -----------------------------------------------------------------------------------------

    override fun onSheetComponentActive(entityId : EntityId, context : Context)
    {
        val sheetActivity = context as SheetActivity
        val rootView = sheetActivity.rootSheetView()

        this.valueVariable(entityId) apDo { currentValueVar ->
            currentValueVar.setOnUpdateListener {
                rootView?.let {
                    this.updateView(it, entityId, context)
                }
            }
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
            is Err -> ApplicationLog.error(numberString.error)
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

    private fun updateView(rootView : View, entityId : EntityId, context : Context)
    {
        val viewId = this.textViewId
        if (viewId != null)
        {
            val textView = rootView.findViewById(viewId) as TextView?
            textView?.text = this.valueString(entityId)
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
//    override fun persistentFunctors() : List<com.kispoko.tome.lib.functor.Val<*>>
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
data class PointsWidget(override val id : UUID,
                        val widgetId : WidgetId,
                        val format : PointsWidgetFormat,
                        val limitValueVariableId : VariableId,
                        val currentValueVariableId : VariableId,
                        val label : Maybe<PointsWidgetLabel>) : Widget()
{


    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var layoutViewId : Int? = null


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(widgetId : WidgetId,
                format : PointsWidgetFormat,
                limitValueVariableId : VariableId,
                currentValueVariableId : VariableId,
                label : Maybe<PointsWidgetLabel>)
        : this(UUID.randomUUID(),
               widgetId,
               format,
               limitValueVariableId,
               currentValueVariableId,
               label)


    companion object : Factory<PointsWidget>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<PointsWidget> = when (doc)
        {
            is DocDict ->
            {
                apply(::PointsWidget,
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

    fun widgetId() : WidgetId = this.widgetId

    fun format() : PointsWidgetFormat = this.format

    fun limitValueVariableId() : VariableId = this.limitValueVariableId

    fun currentValueVariableId() : VariableId = this.currentValueVariableId

    fun label() : Maybe<PointsWidgetLabel> = this.label


    // -----------------------------------------------------------------------------------------
    // WIDGET
    // -----------------------------------------------------------------------------------------

    override fun widgetFormat() : WidgetFormat = this.format().widgetFormat()


    override fun view(entityId : EntityId, context : Context) : View
    {
        val viewBuilder = PointsWidgetViewBuilder(this, entityId, context)
        return viewBuilder.view()
    }


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    fun currentValueVariable(entityId: EntityId) : AppEff<NumberVariable> =
        numberVariable(this.currentValueVariableId(), entityId)


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
               rootView : View,
               entityId : EntityId,
               context : Context) =
        when (pointsWidgetUpdate)
        {
            is PointsWidgetUpdateSetCurrentValue ->
            {
                this.updateCurrentValue(pointsWidgetUpdate.newCurrentValue, entityId)
                this.updateView(rootView, entityId, context)
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
            val layout = rootView.findViewById(layoutViewId) as LinearLayout?
            if (layout != null) {
                PointsWidgetViewBuilder(this, entityId, context).updateView(layout)
            }
        }
    }


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_WidgetPointsValue =
        RowValue5(widgetPointsTable,
                  PrimValue(this.widgetId),
                  ProdValue(this.format),
                  PrimValue(this.limitValueVariableId),
                  PrimValue(this.currentValueVariableId),
                  MaybePrimValue(this.label))


    // -----------------------------------------------------------------------------------------
    // SHEET COMPONENT
    // -----------------------------------------------------------------------------------------

    override fun onSheetComponentActive(entityId : EntityId, context : Context)
    {
        val sheetActivity = context as SheetActivity
        val rootView = sheetActivity.rootSheetView()

        this.currentValueVariable(entityId) apDo { currentValueVar ->
            currentValueVar.setOnUpdateListener {
                rootView?.let {
                    this.updateView(it, entityId, context)
                }
            }
        }
    }

}


/**
 * Quote Widget
 */
data class QuoteWidget(override val id : UUID,
                       val widgetId : WidgetId,
                       val format : QuoteWidgetFormat,
                       val quoteVariableId : VariableId,
                       val sourceVariableId : Maybe<VariableId>) : Widget()
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(widgetId : WidgetId,
                format   : QuoteWidgetFormat,
                quote    : VariableId,
                source   : Maybe<VariableId>)
        : this(UUID.randomUUID(),
               widgetId,
               format,
               quote,
               source)


    companion object : Factory<QuoteWidget>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<QuoteWidget> = when (doc)
        {
            is DocDict ->
            {
                apply(::QuoteWidget,
                      // Widget Id
                      doc.at("id") ap { WidgetId.fromDocument(it) },
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

    fun widgetId() : WidgetId = this.widgetId

    fun format() : QuoteWidgetFormat = this.format

    fun quoteVariableId() : VariableId = this.quoteVariableId

    fun sourceVariableId() : Maybe<VariableId> = this.sourceVariableId


    // -----------------------------------------------------------------------------------------
    // WIDGET
    // -----------------------------------------------------------------------------------------

    override fun widgetFormat() : WidgetFormat = this.format().widgetFormat()


    override fun view(entityId : EntityId, context : Context) : View
    {
        val viewBuilder = QuoteWidgetViewBuilder(this, entityId, context)
        return viewBuilder.view()
    }


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

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_WidgetQuoteValue =
        RowValue4(widgetQuoteTable,
                  PrimValue(this.widgetId),
                  ProdValue(this.format),
                  PrimValue(this.quoteVariableId),
                  MaybePrimValue(this.sourceVariableId))


    // -----------------------------------------------------------------------------------------
    // SHEET COMPONENT
    // -----------------------------------------------------------------------------------------

    override fun onSheetComponentActive(entityId : EntityId, context : Context) { }

}


/**
 * Roll Widget
 */
data class RollWidget(override val id : UUID,
                      val widgetId : WidgetId,
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

    constructor(widgetId : WidgetId,
                format : RollWidgetFormat,
                rollGroup : DiceRollGroup,
                description : Maybe<RollWidgetDescription>)
        : this(UUID.randomUUID(),
               widgetId,
               format,
               rollGroup,
               description,
               Nothing())


    constructor(widgetId : WidgetId,
                format : RollWidgetFormat,
                rollGroup : DiceRollGroup,
                description : Maybe<RollWidgetDescription>,
                resultDescription : Maybe<RollWidgetResultDescription>)
        : this(UUID.randomUUID(),
               widgetId,
               format,
               rollGroup,
               description,
               resultDescription)


    companion object : Factory<RollWidget>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<RollWidget> = when (doc)
        {
            is DocDict ->
            {
                apply(::RollWidget,
                    // Widget Id
                    doc.at("id") ap { WidgetId.fromDocument(it) },
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

    fun widgetId() : WidgetId = this.widgetId


    fun format() : RollWidgetFormat = this.format


    fun rollGroup() : DiceRollGroup = this.rollGroup


    fun description() : Maybe<RollWidgetDescription> = this.description


    fun resultDescription() : Maybe<RollWidgetResultDescription> = this.resultDescription


    // -----------------------------------------------------------------------------------------
    // WIDGET
    // -----------------------------------------------------------------------------------------

    override fun widgetFormat() : WidgetFormat = this.format().widgetFormat()


    override fun view(entityId : EntityId, context : Context) : View {
        val viewBuilder = RollWidgetViewBuilder(this, entityId, context)
        return viewBuilder.view()
    }


    // -----------------------------------------------------------------------------------------
    // PROD TYPE
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_WidgetRollValue =
        RowValue5(widgetRollTable,
                  PrimValue(this.widgetId),
                  ProdValue(this.format),
                  ProdValue(this.rollGroup),
                  MaybePrimValue(this.description),
                  MaybePrimValue(this.resultDescription))


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
               variable.setOnUpdateListener {
                   rootView?.let {
                       updateView(it, entityId, context)
                   }
               }
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
            val layout = rootView.findViewById(layoutId) as LinearLayout?
            if (layout != null) {
                RollWidgetViewBuilder(this, entityId, context).updateContentView(layout)
            }
        }
    }




}


/**
 * Story Widget
 */
data class StoryWidget(override val id : UUID,
                       val widgetId : WidgetId,
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

    constructor(widgetId : WidgetId,
                format : StoryWidgetFormat,
                story : List<StoryPart>)
        : this(UUID.randomUUID(),
               widgetId,
               format,
               story)


    companion object : Factory<StoryWidget>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<StoryWidget> = when (doc)
        {
            is DocDict ->
            {
                apply(::StoryWidget,
                      // Widget Id
                      doc.at("id") ap { WidgetId.fromDocument(it) },
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

    fun widgetId() : WidgetId = this.widgetId


    fun format() : StoryWidgetFormat = this.format


    fun story() : List<StoryPart> = this.story


    // -----------------------------------------------------------------------------------------
    // WIDGET
    // -----------------------------------------------------------------------------------------

    override fun widgetFormat() : WidgetFormat = this.format().widgetFormat()


    override fun view(entityId : EntityId, context : Context) : View
    {
        val viewBuilder = StoryWidgetViewBuilder(this, entityId, context)
        return viewBuilder.view()
    }


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

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_WidgetStoryValue =
        RowValue3(widgetStoryTable,
                  PrimValue(this.widgetId),
                  ProdValue(this.format),
                  CollValue(this.story))


    // -----------------------------------------------------------------------------------------
    // UPDATE
    // -----------------------------------------------------------------------------------------

    fun update(storyWidgetUpdate : WidgetUpdateStoryWidget,
               rootView : View,
               entityId : EntityId,
               context : Context)
    {
        when (storyWidgetUpdate)
        {
            is StoryWidgetUpdateNumberPart ->
                this.updateNumberPart(storyWidgetUpdate, rootView, entityId, context)
            is StoryWidgetUpdateTextValuePart ->
                this.updateTextValuePart(storyWidgetUpdate, rootView, entityId, context)
        }
    }


    private fun updateNumberPart(partUpdate : StoryWidgetUpdateNumberPart,
                                 rootView : View,
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

                // Update View
//                val layoutViewId = this.layoutViewId
//                if (layoutViewId == null)
//                {
//                    val viewId = part.viewId
//                    if (viewId != null) {
//                        val textView = rootView.findViewById(viewId) as TextView
//                        textView?.text = Util.doubleString(partUpdate.newNumber)
//                    }
//                }
//                else
//                {
//                    val layout = rootView.findViewById(layoutViewId) as LinearLayout
//                    layout.removeAllViews()
//                    layout.addView(storySpannableView(this, sheetUIContext))
//                }
            }
        }
    }


    private fun updateTextValuePart(partUpdate : StoryWidgetUpdateTextValuePart,
                                    rootView : View,
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

                // Update View
//                val layoutViewId = this.layoutViewId
//                if (layoutViewId == null)
//                {
//                    val viewId = part.viewId
//                    if (viewId != null) {
//                        val textView = rootView.findViewById(viewId) as TextView
//                        textView?.text = newValue
//                    }
//                }
//                else
//                {
//                    val layout = rootView.findViewById(layoutViewId) as LinearLayout
//                    layout.removeAllViews()
//                    layout.addView(storySpannableView(this, sheetUIContext))
//                }
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
            it.setOnUpdateListener {
                rootView?.let {
                    this.updateView(it, entityId, context)
                }
            }
        }
    }


    private fun updateView(rootView : View, entityId : EntityId, context : Context)
    {
        val viewId = this.viewId
        if (viewId != null) {
            val layout = rootView.findViewById(viewId) as LinearLayout?
            if (layout != null)
                StoryWidgetViewBuilder(this, entityId, context).updateView(layout)
        }
    }



}


/**
 * Tab Widget
 */
//data class TabWidget(override val id : UUID,
//                     val widgetId : Prim<WidgetId>,
//                     val format : Prod<TabWidgetFormat>,
//                     val tabs : Coll<Tab>,
//                     val defaultSelected : Prim<DefaultSelected>) : Widget()
//{
//
//    // -----------------------------------------------------------------------------------------
//    // INIT
//    // -----------------------------------------------------------------------------------------
//
//    init
//    {
//        this.widgetId.name          = "widget_id"
//        this.format.name            = "format"
//        this.tabs.name              = "tabs"
//        this.defaultSelected.name   = "default_selected"
//    }
//
//
//    // -----------------------------------------------------------------------------------------
//    // CONSTRUCTORS
//    // -----------------------------------------------------------------------------------------
//
//    constructor(widgetId : WidgetId,
//                format : TabWidgetFormat,
//                tabs : MutableList<Tab>,
//                defaultSelected : DefaultSelected)
//        : this(UUID.randomUUID(),
//               Prim(widgetId),
//               Prod(format),
//               Coll(tabs),
//               Prim(defaultSelected))
//
//
//    companion object : Factory<TabWidget>
//    {
//        override fun fromDocument(doc: SchemaDoc): ValueParser<TabWidget> = when (doc)
//        {
//            is DocDict ->
//            {
//                effApply(::TabWidget,
//                         // Widget Id
//                         doc.at("id") ap { WidgetId.fromDocument(it) },
//                         // Format
//                         split(doc.maybeAt("format"),
//                               effValue(TabWidgetFormat.default()),
//                               { TabWidgetFormat.fromDocument(it) }),
//                         // Tabs
//                         doc.list("tabs") ap { docList ->
//                             docList.mapMut { Tab.fromDocument(it) }
//                         },
//                         // Default Selected
//                         split(doc.maybeAt("default_selected"),
//                               effValue(DefaultSelected(1)),
//                               { DefaultSelected.fromDocument(it) })
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
//            "id" to this.widgetId().toDocument(),
//            "format" to this.format().toDocument(),
//            "tabs" to DocList(this.tabs().map { it.toDocument() }),
//            "default_selected" to this.defaultSelected().toDocument()
//    ))
//
//
//    // -----------------------------------------------------------------------------------------
//    // GETTERS
//    // -----------------------------------------------------------------------------------------
//
//    fun widgetId() : WidgetId = this.widgetId.value
//
//    fun format() : TabWidgetFormat = this.format.value
//
//    fun tabs() : List<Tab> = this.tabs.value
//
//    fun defaultSelected() : DefaultSelected = this.defaultSelected.value
//
//
//    // -----------------------------------------------------------------------------------------
//    // WIDGET
//    // -----------------------------------------------------------------------------------------
//
//    override fun widgetFormat() : WidgetFormat = this.format().widgetFormat()
//
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
//    override val name : String = "widget_tab"
//
//    override val prodTypeObject = this
//
//    override fun persistentFunctors() : List<com.kispoko.tome.lib.functor.Val<*>> =
//            listOf(this.widgetId,
//                   this.format,
//                   this.tabs,
//                   this.defaultSelected)
//
//
//    // -----------------------------------------------------------------------------------------
//    // SHEET COMPONENT
//    // -----------------------------------------------------------------------------------------
//
//    override fun onSheetComponentActive(sheetUIContext : SheetUIContext) {
//        TODO("not implemented")
//    }
//
//}


/**
 * Table Widget
 */
data class TableWidget(override val id : UUID,
                       private val widgetId : WidgetId,
                       private val format : TableWidgetFormat,
                       private val columns : MutableList<TableWidgetColumn>,
                       private val rows : MutableList<TableWidgetRow>,
                       private val sort : Maybe<TableSort>) : Widget()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var tableLayoutId : Int? = null

    var selectedRow : Int? = null


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

    constructor(widgetId: WidgetId,
                format : TableWidgetFormat,
                columns: List<TableWidgetColumn>,
                rows: List<TableWidgetRow>,
                sort : Maybe<TableSort>)
        : this(UUID.randomUUID(),
               widgetId,
               format,
               columns.toMutableList(),
               rows.toMutableList(),
               sort)


    companion object : Factory<TableWidget>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<TableWidget> = when (doc)
        {
            is DocDict ->
            {
                apply(::TableWidget,
                     // Widget Id
                     doc.at("id") ap { WidgetId.fromDocument(it) },
                     // Format
                     split(doc.maybeAt("format"),
                             effValue(TableWidgetFormat.default()),
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
                             effValue<ValueError, Maybe<TableSort>>(Nothing()),
                             { effApply(::Just, TableSort.fromDocument(it)) })
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

    fun widgetId() : WidgetId = this.widgetId


    fun format() : TableWidgetFormat = this.format


    fun columns( ): List<TableWidgetColumn> = this.columns


    fun rows() : List<TableWidgetRow> = this.rows


    fun sort() : Maybe<TableSort> = this.sort


    // -----------------------------------------------------------------------------------------
    // WIDGET
    // -----------------------------------------------------------------------------------------

    override fun widgetFormat(): WidgetFormat = this.format().widgetFormat()


    // -----------------------------------------------------------------------------------------
    // UPDATE VIEW
    // -----------------------------------------------------------------------------------------

    fun update(tableWidgetUpdate : WidgetUpdateTableWidget,
               rootView : View,
               entityId : EntityId,
               context : Context) =
        when (tableWidgetUpdate)
        {
            is TableWidgetUpdateSetNumberCell ->
            {
                this.updateNumberCellView(tableWidgetUpdate, rootView)
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
                this.addRow(tableWidgetUpdate.selectedRow, rootView, entityId, context)
            }
            is TableWidgetUpdateInsertRowAfter ->
            {
                this.addRow(tableWidgetUpdate.selectedRow + 1, rootView, entityId, context)
            }
        }


    private fun updateNumberCellView(numberCellUpdate : TableWidgetUpdateSetNumberCell,
                                     rootView: View) {
        val numberCell = this.numberCellById[numberCellUpdate.cellId]

        numberCell?.viewId?.let {
            val textView = rootView.findViewById(it) as TextView?
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
                                         rootView : View,
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
            val textView = rootView.findViewById(it) as TextView?
            textView?.text = newValue
        }
    }


    private fun addRow(rowIndex : Int, rootView : View, entityId : EntityId, context : Context)
    {
        val tableLayoutId = this.tableLayoutId

        if (tableLayoutId != null)
        {
            val tableLayout = rootView.findViewById(tableLayoutId) as TableLayout?
            if (tableLayout != null)
            {
                val newTableRow = this.defaultTableRow()
                this.rows.add(rowIndex, newTableRow)

                this.updateTableVariables(rowIndex + 1 , entityId)

                this.addRowToState(rowIndex, entityId, context)
                // need to update all variables
                val rowView = newTableRow.view(this, rowIndex, entityId, context)
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
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_WidgetTableValue =
        RowValue5(widgetTableTable,
                  PrimValue(this.widgetId),
                  ProdValue(this.format),
                  CollValue(this.columns),
                  CollValue(this.rows),
                  MaybePrimValue(this.sort))


    // -----------------------------------------------------------------------------------------
    // SHEET COMPONENT
    // -----------------------------------------------------------------------------------------

    override fun onSheetComponentActive(entityId : EntityId, context : Context)
    {
        this.rows().forEachIndexed { rowIndex, _ ->
            this.addRowToState(rowIndex, entityId, context)
        }
    }


    // -----------------------------------------------------------------------------------------
    // VIEW
    // -----------------------------------------------------------------------------------------

    override fun view(entityId : EntityId, context : Context) : View =
            TableWidgetView.view(this, this.format(), entityId, context)


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
                                       booleanCell.variableValue())
        variable.setOnUpdateListener {
            booleanCell.updateView(entityId, context)
        }
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
        variable.setOnUpdateListener {
            numberCell.updateView(entityId, context)
        }
        addVariable(variable, entityId)
        numberCell.variableId = variableId

        return variable
    }


    private fun addTextCellVariable(textCell : TableWidgetTextCell,
                                    rowIndex : Int,
                                    cellIndex : Int,
                                    entityId : EntityId,
                                    context : Context) : Variable
    {
        val column = this.columns()[cellIndex] as TableWidgetTextColumn
        val variableId = this.cellVariableId(column.variablePrefixString(), rowIndex) //, namespace)
        val variable = TextVariable(variableId,
                                    VariableLabel(column.nameString()),
                                    VariableDescription(column.nameString()),
                                    listOf(),
                                    column.variableRelation(),
                                    textCell.variableValue())

        variable.addTags(column.tags().toSet())

        variable.setOnUpdateListener {
            textCell.updateView(entityId, context)
        }
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

    private fun defaultTableRow() : TableWidgetRow
    {
        val cells : MutableList<TableWidgetCell> = mutableListOf()

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

        return TableWidgetRow(cells)
    }


    private fun addRowToState(rowIndex : Int, entityId : EntityId, context : Context)
    {
        if (rowIndex >= 0 && rowIndex < this.rows().size)
        {
            val row = this.rows()[rowIndex]

            val rowVariables : MutableList<Variable> = mutableListOf()

            row.cells().forEachIndexed { cellIndex, cell ->
                when (cell) {
                    is TableWidgetBooleanCell ->
                    {
                        val booleanVar = addBooleanCellVariable(cell, rowIndex, cellIndex, entityId, context)
                        rowVariables.add(booleanVar)
                    }
                    is TableWidgetNumberCell ->
                    {
                        this.numberCellById.put(cell.id, cell)
                        val numberVar = addNumberCellVariable(cell, rowIndex, cellIndex, entityId, context)
                        rowVariables.add(numberVar)
                    }
                    is TableWidgetTextCell ->
                    {
                        this.textCellById.put(cell.id, cell)
                        val textVar = addTextCellVariable(cell, rowIndex, cellIndex, entityId, context)
                        rowVariables.add(textVar)
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
data class TextWidget(override val id : UUID,
                      val widgetId : WidgetId,
                      val format : TextWidgetFormat,
                      val valueVariableId : VariableId,
                      val rulebookReference : Maybe<BookReference>) : Widget()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var viewId : Int? = null


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(widgetId: WidgetId,
                format : TextWidgetFormat,
                valueVariableId : VariableId,
                rulebookReference : Maybe<BookReference>)
        : this(UUID.randomUUID(),
               widgetId,
               format,
               valueVariableId,
               rulebookReference)


    companion object : Factory<TextWidget>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<TextWidget> = when (doc)
        {
            is DocDict ->
            {
                apply(::TextWidget,
                      // Widget Id
                      doc.at("id") ap { WidgetId.fromDocument(it) },
                      // Format
                      split(doc.maybeAt("format"),
                            effValue(TextWidgetFormat.default()),
                            { TextWidgetFormat.fromDocument(it) }),
                      // Value
                      doc.at("value_variable_id") ap { VariableId.fromDocument(it) },
                      // Rulebook Reference
                      split(doc.maybeAt("rulebook_reference"),
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
        "value_variable_id" to this.valueVariableId().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun widgetId() : WidgetId = this.widgetId


    fun format() : TextWidgetFormat = this.format


    fun valueVariableId() : VariableId = this.valueVariableId


    fun rulebookReference() : Maybe<BookReference> = this.rulebookReference


    // -----------------------------------------------------------------------------------------
    // WIDGET
    // -----------------------------------------------------------------------------------------

    override fun widgetFormat() : WidgetFormat = this.format().widgetFormat()


    override fun view(entityId : EntityId, context : Context) : View =
        TextWidgetView.view(this, this.format(), entityId, context)


    fun update(textWidgetUpdate : WidgetUpdateTextWidget,
               rootView : View,
               entityId : EntityId) =
        when (textWidgetUpdate)
        {
            is TextWidgetUpdateSetText ->
            {
                this.updateTextValue(textWidgetUpdate.newText, entityId)
                this.updateTextView(this.valueString(entityId), rootView)
            }
        }


    private fun updateTextView(newText : String, rootView : View)
    {
        val viewId = this.viewId
        if (viewId != null) {
            val textView = rootView.findViewById(viewId) as TextView?
            textView?.text = newText
        }
    }


    fun updateTextValue(newText : String, entityId : EntityId)
    {
        val textVariable = this.valueVariable(entityId)
        when (textVariable) {
            is Val -> textVariable.value.updateValue(newText, entityId)
            is Err -> ApplicationLog.error(textVariable.error)
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

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_WidgetTextValue =
        RowValue4(widgetTextTable,
                  PrimValue(this.widgetId),
                  ProdValue(this.format),
                  PrimValue(this.valueVariableId),
                  MaybeProdValue(this.rulebookReference))


    // -----------------------------------------------------------------------------------------
    // SHEET COMPONENT
    // -----------------------------------------------------------------------------------------

    override fun onSheetComponentActive(entityId : EntityId, context : Context)
    {
    //     SheetManager.addVariable(sheetContext.sheetId, this.valueVariableId())
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



