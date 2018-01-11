
package com.kispoko.tome.model.sheet.widget


import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.PaintDrawable
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TextView
import com.kispoko.tome.activity.sheet.SheetActivity
import com.kispoko.tome.activity.sheet.SheetActivityGlobal
import com.kispoko.tome.app.AppEff
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.db.*
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.*
import com.kispoko.tome.lib.orm.schema.CollValue
import com.kispoko.tome.lib.orm.schema.MaybePrimValue
import com.kispoko.tome.lib.orm.schema.PrimValue
import com.kispoko.tome.lib.orm.schema.ProdValue
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.lib.ui.LinearLayoutBuilder
import com.kispoko.tome.model.game.engine.mechanic.MechanicCategoryId
import com.kispoko.tome.model.game.engine.procedure.ProcedureId
import com.kispoko.tome.model.game.engine.summation.SummationId
import com.kispoko.tome.model.game.engine.variable.*
import com.kispoko.tome.model.sheet.SheetId
import com.kispoko.tome.model.sheet.style.Height
import com.kispoko.tome.model.sheet.widget.table.*
import com.kispoko.tome.rts.sheet.*
import com.kispoko.tome.util.Util
import effect.*
import effect.Nothing
import effect.Val
import lulo.document.*
import lulo.value.*
import lulo.value.UnexpectedType
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
                                            as ValueParser<Widget>
//                    "widget_expander" -> ExpanderWidget.fromDocument(doc)
//                                            as ValueParser<Widget>
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

    abstract fun view(sheetUIContext : SheetUIContext) : View

    open fun variables(sheetContext : SheetContext) : Set<Variable> = setOf()


    // -----------------------------------------------------------------------------------------
    // INTERNAL
    // -----------------------------------------------------------------------------------------

    protected fun addVariableToState(sheetId : SheetId, variable : Variable)
    {
        val stateEff = SheetManager.sheetState(sheetId)

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
        val layout = this.widgetLayout(widgetFormat, sheetUIContext)

        return layout
    }


    private fun widgetLayout(widgetFormat : WidgetFormat,
                             sheetUIContext: SheetUIContext) : LinearLayout
    {
        val layout = LinearLayoutBuilder()

        layout.orientation      = LinearLayout.VERTICAL
        layout.width            = 0

        val height = widgetFormat.elementFormat().height()
        when (height)
        {
            is Height.Wrap  -> layout.height   = LinearLayout.LayoutParams.WRAP_CONTENT
            is Height.Fixed -> layout.heightDp = height.value.toInt()
        }

        layout.weight           = widgetFormat.width().toFloat()

        layout.marginSpacing    = widgetFormat.elementFormat().margins()
        layout.paddingSpacing   = widgetFormat.elementFormat().padding()

        layout.backgroundColor  = SheetManager.color(sheetUIContext.sheetId,
                                                     widgetFormat.elementFormat().backgroundColorTheme())

        layout.corners          = widgetFormat.elementFormat().corners()

        layout.gravity          = widgetFormat.elementFormat().alignment().gravityConstant() or
                                    widgetFormat.elementFormat().verticalAlignment().gravityConstant()

        return layout.linearLayout(sheetUIContext.context)
    }


    fun widgetTouchLayout(widgetFormat : WidgetFormat,
                                  sheetUIContext: SheetUIContext) : LinearLayout
    {
        val layout = WidgetTouchView(sheetUIContext.context)

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

        val bgColor = SheetManager.color(sheetUIContext.sheetId,
                                         widgetFormat.elementFormat().backgroundColorTheme())

        bgDrawable.colorFilter = PorterDuffColorFilter(bgColor, PorterDuff.Mode.SRC_IN)

        layout.background = bgDrawable

        return layout
    }


    class WidgetTouchView(context : Context) : LinearLayout(context)
    {


        var clickTime : Long = 0
        var CLICK_DURATION = 500


        override fun onInterceptTouchEvent(ev: MotionEvent?) : Boolean
        {
            if (ev != null)
            {
                //Log.d("***WIDGET", ev.action.toString())
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

    private var viewBuilder : ActionWidgetViewBuilder? = null
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


    override fun view(sheetUIContext : SheetUIContext) : View {
        Log.d("***WIDGET", "action widget view")
        val viewBuilder = ActionWidgetViewBuilder(this, sheetUIContext)
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

    override fun onSheetComponentActive(sheetUIContext: SheetUIContext)
    {
        val sheetActivity = sheetUIContext.context as SheetActivity
        val rootView = sheetActivity.rootSheetView()
        val sheetContext = SheetContext(sheetUIContext)

        val activeVariableId = this.activeVariableId()
        when (activeVariableId)
        {
            is Just ->
            {
                val variable = SheetManager.sheetState(sheetContext.sheetId)
                                 .apply { it.booleanVariableWithId(activeVariableId.value) }
                when (variable) {
                    is Val -> {
                        variable.value.setOnUpdateListener {
                            rootView?.let {
                                this.updateView(it, sheetUIContext)
                            }
                        }
                    }
                }
            }
        }
    }


    // -----------------------------------------------------------------------------------------
    // VIEW
    // -----------------------------------------------------------------------------------------

    private fun viewBuilder(sheetUIContext : SheetUIContext) =
        this.viewBuilder ?: ActionWidgetViewBuilder(this, sheetUIContext)


    // -----------------------------------------------------------------------------------------
    // ACTIVE
    // -----------------------------------------------------------------------------------------

    fun isActive(sheetContext : SheetContext) : Boolean
    {
        val variableId = this.activeVariableId()
        return when (variableId)
        {
            is Just ->
            {
                val variableValue = SheetManager.sheetState(sheetContext.sheetId)
                                .apply { it.booleanVariableWithId(variableId.value) }
                                .apply { it.value() }
                when (variableValue)
                {
                    is Val ->
                    {
                        variableValue.value
                    }
                    is Err -> {
                        ApplicationLog.error(variableValue.error)
                        true
                    }
                }

            }
            is Nothing -> {
                Log.d("***WIDGET", "active variable is NOTHING")
                true
            }
        }
    }


    fun setActive(sheetContext : SheetContext)
    {
        val variableId = this.activeVariableId()
        when (variableId)
        {
            is Just ->
            {
                val variable = SheetManager.sheetState(sheetContext.sheetId)
                                 .apply { it.booleanVariableWithId(variableId.value) }
                when (variable)
                {
                    is Val -> variable.value.updateValue(true, sheetContext.sheetId)
                    is Err -> ApplicationLog.error(variable.error)
                }
            }
        }
    }


    // -----------------------------------------------------------------------------------------
    // UPDATE
    // -----------------------------------------------------------------------------------------

    fun update(actionWidgetUpdate : WidgetUpdateActionWidget,
               sheetUIContext : SheetUIContext,
               rootView : View) =
        when (actionWidgetUpdate)
        {
            is ActionWidgetUpdate ->
            {
                Log.d("***WIDGET", "updating action widget")
                this.updateView(rootView, sheetUIContext)
            }
        }


    private fun updateView(rootView : View, sheetUIContext : SheetUIContext)
    {
        val layoutViewId = this.layoutViewId
        if (layoutViewId != null) {
            val layout = rootView.findViewById(layoutViewId) as LinearLayout?
            layout?.removeAllViews()
            layout?.addView(viewBuilder(sheetUIContext).inlineLeftButtonView())
        }
    }


}


/**
 * Boolean Widget
 */
data class BooleanWidget(override val id : UUID,
                         private val widgetId : WidgetId,
                         private val format : BooleanWidgetFormat,
                         private val valueVariableId : VariableId) : Widget()
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(widgetId : WidgetId,
                format : BooleanWidgetFormat,
                valueVariableId : VariableId)
        : this(UUID.randomUUID(),
               widgetId,
               format,
               valueVariableId)


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
                      // Value Variable Id
                      doc.at("value_variable_id") ap { VariableId.fromDocument(it) }
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


    fun format() : BooleanWidgetFormat = this.format


    fun valueVariableId() : VariableId = this.valueVariableId


    // -----------------------------------------------------------------------------------------
    // WIDGET
    // -----------------------------------------------------------------------------------------

    override fun widgetFormat() : WidgetFormat = this.format().widgetFormat()


    override fun view(sheetUIContext : SheetUIContext) : View
    {
        val viewBuilder = BooleanWidgetViewBuilder(this, sheetUIContext)
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
                  PrimValue(this.valueVariableId))


    // -----------------------------------------------------------------------------------------
    // SHEET COMPONENT
    // -----------------------------------------------------------------------------------------

    override fun onSheetComponentActive(sheetUIContext : SheetUIContext)
    {
    }


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------


    fun variable(sheetContext : SheetContext) : AppEff<BooleanVariable> =
        SheetManager.sheetState(sheetContext.sheetId)
                    .apply { it.booleanVariableWithId(this.valueVariableId()) }


    fun variableValue(sheetContext : SheetContext) : AppEff<Boolean> =
            this.variable(sheetContext)
                .apply { it.value() }

}


/**
 * Expander Widget
 */
//data class ExpanderWidget(override val id : UUID,
//                          val widgetId : Prim<WidgetId>,
//                          val format : Prod<ExpanderWidgetFormat>,
//                          val label : Prim<ExpanderLabel>,
//                          val groups: Coll<Group>) : Widget()
//{
//
//    // -----------------------------------------------------------------------------------------
//    // INIT
//    // -----------------------------------------------------------------------------------------
//
//    init
//    {
//        this.widgetId.name      = "widget_id"
//        this.format.name        = "format"
//        this.label.name         = "labelString"
//        this.groups.name        = "groups"
//    }
//
//
//    // -----------------------------------------------------------------------------------------
//    // CONSTRUCTORS
//    // -----------------------------------------------------------------------------------------
//
//    constructor(widgetId : WidgetId,
//                format : ExpanderWidgetFormat,
//                label : ExpanderLabel,
//                groups : MutableList<Group>)
//        : this(UUID.randomUUID(),
//               Prim(widgetId),
//               Prod(format),
//               Prim(label),
//               Coll(groups))
//
//
//    companion object : Factory<Widget>
//    {
//        override fun fromDocument(doc: SchemaDoc): ValueParser<Widget> = when (doc)
//        {
//            is DocDict ->
//            {
//                effApply(::ExpanderWidget,
//                         // Widget Name
//                         doc.at("id") ap { WidgetId.fromDocument(it) },
//                         // Format
//                         doc.at("format") ap { ExpanderWidgetFormat.fromDocument(it) },
//                         // Label
//                         doc.at("label") ap { ExpanderLabel.fromDocument(it) },
//                         // Groups
//                         doc.list("groups") ap { docList ->
//                             docList.mapIndexed { d,index -> Group.fromDocument(d,index) }
//                         })
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
//        "format" to this.format().toDocument(),
//        "label" to this.label().toDocument(),
//        "groups" to DocList(this.groups.value.map { it.toDocument() })
//    ))
//
//
//    // -----------------------------------------------------------------------------------------
//    // GETTERS
//    // -----------------------------------------------------------------------------------------
//
//    fun widgetId() : WidgetId = this.widgetId.value
//
//    fun format() : ExpanderWidgetFormat = this.format.value
//
//    fun label() : ExpanderLabel = this.label.value
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
//    // -----------------------------------------------------------------------------------------
//    // MODEL
//    // -----------------------------------------------------------------------------------------
//
//    override fun onLoad() { }
//
//    override val name : String = "widget_expander"
//
//    override val prodTypeObject = this
//
//    override fun persistentFunctors() : List<com.kispoko.tome.lib.functor.Val<*>> =
//            listOf(this.widgetId,
//                   this.format,
//                   this.label,
//                   this.groups)
//
//
//    // -----------------------------------------------------------------------------------------
//    // SHEET COMPONENT
//    // -----------------------------------------------------------------------------------------
//
//    override fun onSheetComponentActive(sheetUIContext : SheetUIContext) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//}


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
    var viewBuilder : ListWidgetViewBuilder? = null


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


    override fun view(sheetUIContext : SheetUIContext) : View =
            this.viewBuilder(sheetUIContext).view()


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

    override fun onSheetComponentActive(sheetUIContext: SheetUIContext) {
    }


    // -----------------------------------------------------------------------------------------
    // VALUES
    // -----------------------------------------------------------------------------------------

    fun variable(sheetContext : SheetContext) : AppEff<TextListVariable> =
        SheetManager.sheetState(sheetContext.sheetId)
                    .apply { it.textListVariableWithId(this.valuesVariableId()) }


    fun value(sheetContext : SheetContext) : AppEff<List<String>> =
        SheetManager.sheetState(sheetContext.sheetId)
                    .apply { it.textListVariableWithId(this.valuesVariableId()) }
                    .apply { it.value(sheetContext) }


    // -----------------------------------------------------------------------------------------
    // VIEW
    // -----------------------------------------------------------------------------------------

    private fun viewBuilder(sheetUIContext : SheetUIContext) =
        this.viewBuilder ?: ListWidgetViewBuilder(this, sheetUIContext)


    // -----------------------------------------------------------------------------------------
    // UPDATE
    // -----------------------------------------------------------------------------------------

    fun update(listWidgetUpdate : WidgetUpdateListWidget,
               sheetUIContext : SheetUIContext,
               rootView : View) =
        when (listWidgetUpdate)
        {
            is ListWidgetUpdateSetCurrentValue ->
            {
                this.updateCurrentValue(listWidgetUpdate.newCurrentValue,
                                        SheetContext(sheetUIContext))
                this.updateView(rootView, sheetUIContext)
            }
        }


    private fun updateCurrentValue(newCurrentValue : List<String>, sheetContext : SheetContext)
    {
        val currentValueVariable = this.variable(sheetContext)
        when (currentValueVariable) {
            is Val -> currentValueVariable.value.updateLiteralValue(newCurrentValue,
                                                                    sheetContext.sheetId)
            is Err -> ApplicationLog.error(currentValueVariable.error)
        }
    }


    private fun updateView(rootView : View, sheetUIContext : SheetUIContext)
    {
        val layoutViewId = this.layoutViewId
        if (layoutViewId != null) {
            val layout = rootView.findViewById(layoutViewId) as LinearLayout?
            layout?.removeAllViews()
            layout?.addView(viewBuilder(sheetUIContext).inlineView())
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


    override fun view(sheetUIContext : SheetUIContext) : View {
        val viewBuilder = LogViewBuilder(this, sheetUIContext)
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

    override fun onSheetComponentActive(sheetUIContext : SheetUIContext) {
    }

}


/**
 * Mechanic Widget
 */
data class MechanicWidget(override val id : UUID,
                          private val widgetId : WidgetId,
                          private val format : MechanicWidgetFormat,
                          private val categoryId : MechanicCategoryId) : Widget()
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(widgetId : WidgetId,
                format : MechanicWidgetFormat,
                categoryId : MechanicCategoryId)
        : this(UUID.randomUUID(),
               widgetId,
               format,
               categoryId)


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
                     // Category Id
                     doc.at("category_id") ap { MechanicCategoryId.fromDocument(it) }
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
        "category_id" to this.categoryId().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun widgetId() : WidgetId = this.widgetId

    fun format() : MechanicWidgetFormat = this.format

    fun categoryId() : MechanicCategoryId = this.categoryId


    // -----------------------------------------------------------------------------------------
    // WIDGET
    // -----------------------------------------------------------------------------------------

    override fun widgetFormat() : WidgetFormat = this.format().widgetFormat()


    override fun view(sheetUIContext: SheetUIContext) : View
    {
        val viewBuilder = MechanicWidgetViewBuilder(this, sheetUIContext)
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
                  PrimValue(this.categoryId))


    // -----------------------------------------------------------------------------------------
    // SHEET COMPONENT
    // -----------------------------------------------------------------------------------------

    override fun onSheetComponentActive(sheetUIContext: SheetUIContext) { }

}


/**
 * Number Widget
 */
data class NumberWidget(override val id : UUID,
                        val widgetId : WidgetId,
                        val format : NumberWidgetFormat,
                        val valueVariableId : VariableId,
                        val insideLabel : Maybe<NumberWidgetLabel>)
                         : Widget()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var viewId : Int? = null


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(widgetId : WidgetId,
                format : NumberWidgetFormat,
                valueVariableId : VariableId,
                insideLabel : Maybe<NumberWidgetLabel>)
        : this(UUID.randomUUID(),
               widgetId,
               format,
               valueVariableId,
               insideLabel)


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
                      // Format
                      split(doc.maybeAt("inside_label"),
                            effValue<ValueError,Maybe<NumberWidgetLabel>>(Nothing()),
                            { apply(::Just, NumberWidgetLabel.fromDocument(it)) })
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


    override val prodTypeObject = this


    override fun rowValue() : DB_WidgetNumberValue =
        RowValue4(widgetNumberTable,
                  PrimValue(this.widgetId),
                  ProdValue(this.format),
                  PrimValue(this.valueVariableId),
                  MaybePrimValue(this.insideLabel))


    // -----------------------------------------------------------------------------------------
    // SHEET COMPONENT
    // -----------------------------------------------------------------------------------------

    override fun onSheetComponentActive(sheetUIContext : SheetUIContext) {  }


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    fun valueVariable(sheetContext : SheetContext) : AppEff<NumberVariable> =
        SheetManager.sheetState(sheetContext.sheetId)
                    .apply { it.numberVariableWithId(this.valueVariableId()) }


    /**
     * The string representation of the widget's current value. This method returns 0 when the
     * value is null for some reason.
     */
    fun valueString(sheetContext : SheetContext) : String
    {
        val numberString  = this.valueVariable(sheetContext)
                             .apply { it.valueString(sheetContext) }

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
    fun value(sheetContext : SheetContext) : Double
    {
        val num  = this.valueVariable(sheetContext)
                       .apply { it.value(sheetContext) }

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

    override fun view(sheetUIContext : SheetUIContext) : View =
            PointsWidgetView.view(this, sheetUIContext)


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    fun currentValueVariable(sheetContext : SheetContext) : AppEff<NumberVariable> =
            SheetManager.sheetState(sheetContext.sheetId)
                    .apply { it.numberVariableWithId(this.currentValueVariableId()) }


    fun limitValue(sheetContext : SheetContext) : Double?
    {
        val mDouble = SheetManager.sheetState(sheetContext.sheetId)
                            .apply { it.numberVariableWithId(this.limitValueVariableId()) }
                            .apply { it.value(sheetContext) }

        when (mDouble)
        {
            is Val ->
            {
                val dbl = mDouble.value
                when (dbl)
                {
                    is Just    -> return dbl.value
                    is Nothing -> return null
                }
            }
            is Err -> ApplicationLog.error(mDouble.error)
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
        val mDouble = SheetManager.sheetState(sheetContext.sheetId)
                            .apply { it.numberVariableWithId(this.currentValueVariableId()) }
                            .ap { it.value(sheetContext) }

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
    // UPDATE
    // -----------------------------------------------------------------------------------------

    fun update(pointsWidgetUpdate : WidgetUpdatePointsWidget,
               sheetUIContext : SheetUIContext,
               rootView : View) =
        when (pointsWidgetUpdate)
        {
            is PointsWidgetUpdateSetCurrentValue ->
            {
                this.updateCurrentValue(pointsWidgetUpdate.newCurrentValue,
                                        SheetContext(sheetUIContext))
                this.updateView(rootView, sheetUIContext)
            }
        }


    private fun updateCurrentValue(newCurrentValue : Double, sheetContext : SheetContext)
    {
        val currentValueVariable = this.currentValueVariable(sheetContext)
        when (currentValueVariable) {
            is Val -> currentValueVariable.value.updateValue(newCurrentValue,
                                                             sheetContext.sheetId)
            is Err -> ApplicationLog.error(currentValueVariable.error)
        }
    }


    private fun updateView(rootView : View, sheetUIContext : SheetUIContext)
    {
        val layoutViewId = this.layoutViewId
        if (layoutViewId != null) {
            val layout = rootView.findViewById(layoutViewId) as LinearLayout?
            layout?.removeAllViews()
            layout?.addView(PointsWidgetView.aboveBarView(this, sheetUIContext))
            layout?.addView(PointsWidgetView.barView(this, sheetUIContext))
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

    override fun onSheetComponentActive(sheetUIContext : SheetUIContext)
    {
        val sheetActivity = sheetUIContext.context as SheetActivity
        val rootView = sheetActivity.rootSheetView()
        val sheetContext = SheetContext(sheetUIContext)

        this.currentValueVariable(sheetContext) apDo { currentValueVar ->
            currentValueVar.setOnUpdateListener {
                rootView?.let {
                    this.updateView(it, sheetUIContext)
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


    override fun view(sheetUIContext: SheetUIContext) : View
    {
        val viewBuilder = QuoteWidgetViewBuilder(this, sheetUIContext)
        return viewBuilder.view()
    }


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    fun quoteVariable(sheetContext : SheetContext) : AppEff<TextVariable> =
            SheetManager.sheetState(sheetContext.sheetId)
                    .apply { it.textVariableWithId(this.quoteVariableId()) }


    fun source(sheetContext : SheetContext) : Maybe<String>
    {
        val sourceVarId = this.sourceVariableId()
        when (sourceVarId)
        {
            is Just ->
            {
                val sourceString = SheetManager.sheetState(sheetContext.sheetId)
                                    .apply { it.textVariableWithId(sourceVarId.value) }
                                    .apply { it.valueString(sheetContext)  }
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


    fun quote(sheetContext : SheetContext) : String
    {
        val quoteString = this.quoteVariable(sheetContext)
                              .apply { it.valueString(sheetContext) }

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

    override fun onSheetComponentActive(sheetUIContext : SheetUIContext) { }

}


/**
 * Roll Widget
 */
data class RollWidget(override val id : UUID,
                      val widgetId : WidgetId,
                      val format : RollWidgetFormat,
                      val rollSummationId : SummationId,
                      val description : Maybe<RollWidgetDescription>) : Widget()
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(widgetId : WidgetId,
                format : RollWidgetFormat,
                rollSummationId : SummationId,
                description : Maybe<RollWidgetDescription>)
        : this(UUID.randomUUID(),
               widgetId,
               format,
               rollSummationId,
               description)


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
                    // Roll Summation Id
                    doc.at("roll_summation_id") ap { SummationId.fromDocument(it) },
                    // Description
                    split(doc.maybeAt("description"),
                          effValue<ValueError,Maybe<RollWidgetDescription>>(Nothing()),
                          { apply(::Just, RollWidgetDescription.fromDocument(it)) })
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
        "roll_summation_id" to this.rollSummationId().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun widgetId() : WidgetId = this.widgetId


    fun format() : RollWidgetFormat = this.format


    fun rollSummationId() : SummationId = this.rollSummationId


    fun description() : Maybe<RollWidgetDescription> = this.description


    // -----------------------------------------------------------------------------------------
    // WIDGET
    // -----------------------------------------------------------------------------------------

    override fun widgetFormat() : WidgetFormat = this.format().widgetFormat()


    override fun view(sheetUIContext : SheetUIContext) : View {
        val viewBuilder = RollWidgetViewBuilder(this, sheetUIContext)
        return viewBuilder.view()
    }


    // -----------------------------------------------------------------------------------------
    // PROD TYPE
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_WidgetRollValue =
        RowValue4(widgetRollTable,
                  PrimValue(this.widgetId),
                  ProdValue(this.format),
                  PrimValue(this.rollSummationId),
                  MaybePrimValue(this.description))


    // -----------------------------------------------------------------------------------------
    // SHEET COMPONENT
    // -----------------------------------------------------------------------------------------

    override fun onSheetComponentActive(sheetUIContext: SheetUIContext) {
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

    var layoutViewId : Int? = null


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


    override fun view(sheetUIContext : SheetUIContext) : View
    {
        val viewBuilder = StoryWidgetViewBuilder(this, sheetUIContext)
        return viewBuilder.view()
    }


    override fun variables(sheetContext : SheetContext) : Set<Variable> =
        this.variableParts().mapNotNull { part ->
            val variable = SheetManager.sheetState(sheetContext.sheetId)
                                       .apply { it.variableWithId(part.variableId()) }
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
               sheetUIContext : SheetUIContext,
               rootView : View)
    {
        when (storyWidgetUpdate)
        {
            is StoryWidgetUpdateNumberPart ->
                this.updateNumberPart(storyWidgetUpdate, sheetUIContext, rootView)
            is StoryWidgetUpdateTextValuePart ->
                this.updateTextValuePart(storyWidgetUpdate, sheetUIContext, rootView)
        }
    }


    private fun updateNumberPart(partUpdate : StoryWidgetUpdateNumberPart,
                                 sheetUIContext : SheetUIContext,
                                 rootView : View)
    {
        val part = this.story()[partUpdate.partIndex]
        when (part)
        {
            is StoryPartVariable ->
            {
                // Update Value
                val variable = part.variable(SheetContext(sheetUIContext))
                when (variable) {
                    is NumberVariable ->
                    {
                        variable.updateValue(partUpdate.newNumber, sheetUIContext.sheetId)
                    }
                }

                // Update View
                val layoutViewId = this.layoutViewId
                if (layoutViewId == null)
                {
                    val viewId = part.viewId
                    if (viewId != null) {
                        val textView = rootView.findViewById(viewId) as TextView
                        textView?.text = Util.doubleString(partUpdate.newNumber)
                    }
                }
                else
                {
                    val layout = rootView.findViewById(layoutViewId) as LinearLayout
                    layout.removeAllViews()
                    layout.addView(storySpannableView(this, sheetUIContext))
                }
            }
        }
    }


    private fun updateTextValuePart(partUpdate : StoryWidgetUpdateTextValuePart,
                                    sheetUIContext : SheetUIContext,
                                    rootView : View)
    {
        val sheetContext = SheetContext(sheetUIContext)
        val part = this.story()[partUpdate.partIndex]
        when (part)
        {
            is StoryPartVariable ->
            {
                // Update Value
                val variable = part.variable(sheetContext)
                var newValue : String? = null
                when (variable) {
                    is TextVariable -> {
                        variable.updateValue(partUpdate.newValueId, sheetUIContext.sheetId)
                        val updatedValue = variable.value(sheetContext)
                        when (updatedValue) {
                            is Val -> newValue = maybe("", updatedValue.value)
                        }
                    }
                }

                // Update View
                val layoutViewId = this.layoutViewId
                if (layoutViewId == null)
                {
                    val viewId = part.viewId
                    if (viewId != null) {
                        val textView = rootView.findViewById(viewId) as TextView
                        textView?.text = newValue
                    }
                }
                else
                {
                    val layout = rootView.findViewById(layoutViewId) as LinearLayout
                    layout.removeAllViews()
                    layout.addView(storySpannableView(this, sheetUIContext))
                }
            }
        }
    }


    // -----------------------------------------------------------------------------------------
    // SHEET COMPONENT
    // -----------------------------------------------------------------------------------------

    override fun onSheetComponentActive(sheetUIContext : SheetUIContext)
    {
//        this.story().mapNotNull { it.variable(sheetContext) }
//                    .forEach { this.addVariableToState(sheetContext.sheetId, it) }
//
//        this.variables().forEach { this.addVariableToState(sheetContext.sheetId, it) }
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

    private var namespaceColumn : Int? = null

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

        this.columns().forEachIndexed { index, column ->
            when (column) {
                is TableWidgetTextColumn -> {
                    if (column.definesNamespaceBoolean() && namespaceColumn == null)
                        namespaceColumn = index
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
               sheetUIContext : SheetUIContext,
               rootView : View) =
        when (tableWidgetUpdate)
        {
            is TableWidgetUpdateSetNumberCell ->
            {
                this.updateNumberCellView(tableWidgetUpdate, rootView)
                this.updateNumberCellValue(tableWidgetUpdate, SheetContext(sheetUIContext))
            }
            is TableWidgetUpdateSetTextCellValue ->
            {
                this.updateTextCellValueValue(tableWidgetUpdate,
                                              SheetContext(sheetUIContext),
                                              rootView)
            }
            is TableWidgetUpdateInsertRowBefore ->
            {
                this.addRow(tableWidgetUpdate.selectedRow, rootView, sheetUIContext)
            }
            is TableWidgetUpdateInsertRowAfter ->
            {
                this.addRow(tableWidgetUpdate.selectedRow + 1, rootView, sheetUIContext)
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
                                      sheetContext : SheetContext)
    {
        val numberCell = this.numberCellById[numberCellUpdate.cellId]
        numberCell?.updateValue(numberCellUpdate.newNumber, sheetContext)
    }


    private fun updateTextCellValueValue(cellUpdate : TableWidgetUpdateSetTextCellValue,
                                         sheetContext : SheetContext,
                                         rootView : View)
    {
        Log.d("***WIDGET", "updating text cell value")
        val cell = this.textCellById[cellUpdate.cellId]

        if (cell == null)
            Log.d("***WIDGET", "text cell is null")

        // Update Variable
        val variable = cell?.valueVariable(sheetContext)
        var newValue : String? = null
        when (variable)
        {
            is Val -> {
                Log.d("***WIDGET", "cell text variable found")
                val textVariable = variable.value
                textVariable.updateValue(cellUpdate.newValueId, sheetContext.sheetId)
                val updatedValue = textVariable.value(sheetContext)
                when (updatedValue) {
                    is Val -> newValue = maybe("", updatedValue.value)
                }
            }
            is Err -> ApplicationLog.error(variable.error)
        }

        // Update View
        cell?.viewId?.let {
            val textView = rootView.findViewById(it) as TextView?
            textView?.text = newValue
            Log.d("***WIDGET", "cell text updated: $newValue")
        }
    }


    private fun addRow(rowIndex : Int, rootView : View, sheetUIContext : SheetUIContext)
    {
        val tableLayoutId = this.tableLayoutId

        Log.d("***WIDGET", "row index: $rowIndex")

        if (tableLayoutId != null)
        {
            val tableLayout = rootView.findViewById(tableLayoutId) as TableLayout?
            if (tableLayout != null)
            {
                val newTableRow = this.defaultTableRow()
                this.rows.add(rowIndex, newTableRow)

                this.updateTableVariables(rowIndex + 1 , SheetContext(sheetUIContext))

                this.addRowToState(rowIndex, sheetUIContext)
                // need to update all variables
                Log.d("***WIDGET", "add table row view")
                val rowView = newTableRow.view(this, rowIndex, sheetUIContext)
                tableLayout.addView(rowView, rowIndex + 1)

                val selectedRowIndex = this.selectedRow
                if (selectedRowIndex != null) {
                    Log.d("***WIDGET", "selected row index: $selectedRowIndex")
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

    override fun onSheetComponentActive(sheetUIContext : SheetUIContext)
    {
        this.rows().forEachIndexed { rowIndex, _ ->
            this.addRowToState(rowIndex, sheetUIContext)
        }
    }


    // -----------------------------------------------------------------------------------------
    // VIEW
    // -----------------------------------------------------------------------------------------

    override fun view(sheetUIContext: SheetUIContext) : View =
            TableWidgetView.view(this, this.format(), sheetUIContext)


    // -----------------------------------------------------------------------------------------
    // CELLS
    // -----------------------------------------------------------------------------------------

    private fun addBooleanCellVariable(booleanCell : TableWidgetBooleanCell,
                                       rowIndex : Int,
                                       cellIndex : Int,
                                       namespace : VariableNamespace?,
                                       sheetUIContext : SheetUIContext)
    {
        val column = this.columns()[cellIndex]
        val variableId = this.cellVariableId(column.variablePrefixString(), rowIndex, namespace)
        val variable = BooleanVariable(variableId,
                                       VariableLabel(column.nameString()),
                                       VariableDescription(column.nameString()),
                                       listOf(),
                                       booleanCell.variableValue())
        variable.setOnUpdateListener {
            booleanCell.updateView(sheetUIContext)
        }
        SheetManager.addVariable(sheetUIContext.sheetId, variable)
        booleanCell.variableId = variableId
        booleanCell.namespace = namespace
    }


    // Number Cell
    // -----------------------------------------------------------------------------------------

    private fun addNumberCellVariable(numberCell : TableWidgetNumberCell,
                                      rowIndex : Int,
                                      cellIndex : Int,
                                      namespace : VariableNamespace?,
                                      sheetUIContext : SheetUIContext)
    {
        val column = this.columns()[cellIndex]
        val variableId = this.cellVariableId(column.variablePrefixString(), rowIndex, namespace)
        val variable = NumberVariable(variableId,
                                      VariableLabel(column.nameString()),
                                      VariableDescription(column.nameString()),
                                      listOf(),
                                      numberCell.variableValue())
        variable.setOnUpdateListener {
            numberCell.updateView(sheetUIContext)
        }
        SheetManager.addVariable(sheetUIContext.sheetId, variable)
        numberCell.variableId = variableId
        numberCell.namespace = namespace
    }


    private fun addTextCellVariable(textCell : TableWidgetTextCell,
                                    rowIndex : Int,
                                    cellIndex : Int,
                                    namespace : VariableNamespace?,
                                    sheetUIContext : SheetUIContext)
    {
        val column = this.columns()[cellIndex]
        val variableId = this.cellVariableId(column.variablePrefixString(), rowIndex, namespace)
        val variable = TextVariable(variableId,
                                    VariableLabel(column.nameString()),
                                    VariableDescription(column.nameString()),
                                    listOf(),
                                    textCell.variableValue())
        variable.setOnUpdateListener {
            textCell.updateView(sheetUIContext)
        }
        SheetManager.addVariable(sheetUIContext.sheetId, variable)
        textCell.variableId = variableId
        textCell.namespace = namespace
    }


    private fun cellVariableId(variablePrefix : String,
                               rowIndex : Int,
                               namespace : VariableNamespace?) : VariableId =
        if (namespace != null)
            VariableId(namespace, VariableName(variablePrefix))
        else
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


    private fun addRowToState(rowIndex : Int, sheetUIContext : SheetUIContext)
    {
        if (rowIndex >= 0 && rowIndex < this.rows().size)
        {
            val row = this.rows()[rowIndex]

            var namespace : VariableNamespace? = null
            val nsCol = this.namespaceColumn
            if (nsCol != null)
            {
                val cell = row.cells()[nsCol]
                when (cell) {
                    is TableWidgetTextCell -> {
                        val valueStringEff = cell.variableValue().value(SheetContext(sheetUIContext))
                        when (valueStringEff) {
                            is Val -> {
                                val valueString = valueStringEff.value
                                when (valueString) {
                                    is Just ->
                                    {
                                        namespace = VariableNamespace(valueString.value.toLowerCase())
                                    }
                                }
                            }
                            is Err -> ApplicationLog.error(valueStringEff.error)
                        }
                    }
                }
            }

            row.cells().forEachIndexed { cellIndex, cell ->
                when (cell) {
                    is TableWidgetBooleanCell ->
                    {
                        addBooleanCellVariable(cell, rowIndex, cellIndex, namespace, sheetUIContext)
                    }
                    is TableWidgetNumberCell ->
                    {
                        this.numberCellById.put(cell.id, cell)
                        addNumberCellVariable(cell, rowIndex, cellIndex, namespace, sheetUIContext)
                    }
                    is TableWidgetTextCell ->
                    {
                        this.textCellById.put(cell.id, cell)
                        addTextCellVariable(cell, rowIndex, cellIndex, namespace, sheetUIContext)
                    }
                }
            }
        }
    }


    private fun updateTableVariables(fromIndex : Int, sheetContext : SheetContext)
    {
        Log.d("***WIDGET", "update table vars: $fromIndex")
        for (rowIndex in (this.rows().size - 1) downTo fromIndex)
        {
            Log.d("***WIDGET", "update table row: $rowIndex")
            val row = this.rows()[rowIndex]
            row.cells().forEachIndexed { cellIndex, cell ->
                val column = this.columns()[cellIndex]
                when (cell) {
                    is TableWidgetBooleanCell ->
                    {
                        val newVarId = cellVariableId(column.variablePrefixString(), rowIndex, null)
                        cell.valueVariable(sheetContext)              apDo { boolVar ->
                        SheetManager.sheetState(sheetContext.sheetId) apDo { sheetState ->
                            sheetState.updateVariableId(boolVar.variableId(), newVarId)
                        } }
                        cell.variableId = newVarId
                    }
                    is TableWidgetNumberCell ->
                    {
                        val newVarId = cellVariableId(column.variablePrefixString(), rowIndex, null)
                        cell.valueVariable(sheetContext)              apDo { numVar ->
                        SheetManager.sheetState(sheetContext.sheetId) apDo { sheetState ->
                            sheetState.updateVariableId(numVar.variableId(), newVarId)
                        } }
                        cell.variableId = newVarId
                    }
                    is TableWidgetTextCell ->
                    {
                        val newVarId = cellVariableId(column.variablePrefixString(), rowIndex, null)
                        cell.valueVariable(sheetContext)              apDo { textVar ->
                        SheetManager.sheetState(sheetContext.sheetId) apDo { sheetState ->
                            sheetState.updateVariableId(textVar.variableId(), newVarId)
                        } }
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
                      val valueVariableId : VariableId) : Widget()
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
                valueVariableId : VariableId)
        : this(UUID.randomUUID(),
               widgetId,
               format,
               valueVariableId)


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
                      doc.at("value_variable_id") ap { VariableId.fromDocument(it) }
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


    // -----------------------------------------------------------------------------------------
    // WIDGET
    // -----------------------------------------------------------------------------------------

    override fun widgetFormat() : WidgetFormat = this.format().widgetFormat()


    override fun view(sheetUIContext: SheetUIContext) : View =
        TextWidgetView.view(this, this.format(), sheetUIContext)


    fun update(textWidgetUpdate : WidgetUpdateTextWidget,
               sheetContext : SheetContext,
               rootView : View) =
        when (textWidgetUpdate)
        {
            is TextWidgetUpdateSetText ->
            {
                this.updateTextView(textWidgetUpdate.newText, rootView)
                this.updateTextValue(textWidgetUpdate.newText, sheetContext)
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


    fun updateTextValue(newText : String, sheetContext : SheetContext)
    {
        val textVariable = this.valueVariable(sheetContext)
        when (textVariable) {
            is Val -> textVariable.value.updateLiteralValue(newText, sheetContext.sheetId)
            is Err -> ApplicationLog.error(textVariable.error)
        }
    }


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


    override val prodTypeObject = this


    override fun rowValue() : DB_WidgetTextValue =
        RowValue3(widgetTextTable,
                  PrimValue(this.widgetId),
                  ProdValue(this.format),
                  PrimValue(this.valueVariableId))


    // -----------------------------------------------------------------------------------------
    // SHEET COMPONENT
    // -----------------------------------------------------------------------------------------

    override fun onSheetComponentActive(sheetUIContext : SheetUIContext)
    {
    //     SheetManager.addVariable(sheetContext.sheetId, this.valueVariableId())
    }


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    /**
     * The string representation of the widget's current value.
     */
    fun valueString(sheetContext : SheetContext) : String
    {
        val str = this.valueVariable(sheetContext)
                      .apply { it.valueString(sheetContext) }

        when (str)
        {
            is Val -> return str.value
            is Err -> ApplicationLog.error(str.error)
        }

        return ""
    }

}



