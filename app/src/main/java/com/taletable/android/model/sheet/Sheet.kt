
package com.taletable.android.model.sheet


import android.content.Context
import android.util.Log
import android.view.View
import com.taletable.android.app.AppEff
import com.taletable.android.app.AppSheetError
import com.taletable.android.db.*
import com.taletable.android.lib.Factory
import com.taletable.android.model.engine.Engine
import com.taletable.android.model.engine.variable.Variable
import com.taletable.android.model.engine.variable.VariableId
import com.taletable.android.model.entity.*
import com.taletable.android.model.sheet.section.Section
import com.taletable.android.model.sheet.section.SectionName
import com.taletable.android.model.sheet.widget.*
import com.taletable.android.rts.entity.*
import com.taletable.android.rts.entity.sheet.*
import effect.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import java.io.Serializable



/**
 * Sheet
 */
data class Sheet(private var sheetId : EntityId,
                 private var campaignId : EntityId,
                 private var sections : MutableList<Section>,
                 private var engine : Engine,
                 private var variables : MutableList<Variable>,
                 private var category : SheetCategory,
                 private var settings : Settings)
                  : Entity, ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var isSavable : Boolean = true
    private var isSaved : Boolean = false


    // -----------------------------------------------------------------------------------------
    // INDEXES
    // -----------------------------------------------------------------------------------------

    private val variableById : Map<VariableId,Variable> =
                                this.variables().associateBy { it.variableId() }

    // Widgets
    // -----------------------------------------------------------------------------------------

    private val widgetById : MutableMap<WidgetId,Widget> = mutableMapOf()


    // Index Widgets
    // -----------------------------------------------------------------------------------------

    init {
        this.forEachWidget {
            this.indexWidget(it)
        }
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<Sheet>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<Sheet> = when (doc)
        {
            is DocDict ->
            {
                apply(::Sheet,
                      // Sheet Id
                      doc.at("id") ap { EntityId.fromDocument(it) },
                      // Campaign Id
                      doc.at("campaign_id") ap { EntityId.fromDocument(it) },
                      // Section List
                      doc.list("sections") ap { docList ->
                          docList.mapMut { Section.fromDocument(it) }
                      },
                      // Engine
                      doc.at("engine") ap { Engine.fromDocument(it) },
                      // Variables
                      split(doc.maybeList("variables"),
                            effValue(mutableListOf()),
                            { it.mapMut { Variable.fromDocument(it) } }),
                      // Category
                      doc.at("category") ap { SheetCategory.fromDocument(it) },
                      // Sheet Settings
                      split(doc.maybeAt("settings"),
                            effValue(Settings.default()),
                            { Settings.fromDocument(it) })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun campaignId() : EntityId = this.campaignId


    fun sections() : List<Section> = this.sections


    fun engine() : Engine = this.engine


    fun sectionWithName(sectionName : SectionName) : Section? =
        this.sections().filter { it.name().equals(sectionName) }
                       .firstOrNull()


    fun sectionWithIndex(index : Int) : Section? = this.sections()[index]


    fun settings() : Settings = this.settings


    fun variables() : List<Variable> = this.variables


    // -----------------------------------------------------------------------------------------
    // ENTITY
    // -----------------------------------------------------------------------------------------

    override fun name() : String = this.settings.sheetName()


    override fun summary() : String = this.settings.sheetSummary()


    override fun entityId() = this.sheetId


    override fun category() = this.category.value


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "campaign_id" to this.campaignId().toDocument(),
        "sections" to DocList(this.sections().map { it.toDocument() }),
        "variables" to DocList(this.variables().map { it.toDocument() }),
        "settings" to this.settings().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    fun variableWithId(variableId : VariableId) : Variable? = this.variableById[variableId]


    fun forEachWidget(f : (Widget) -> Unit)
    {
        this.sections().forEach { section ->
            section.pages().forEach { page ->
                page.groups().forEach { group ->
                    group.rows().forEach { row ->
                        row.widgets().forEach {
                            f(it)
                            when (it) {
                                is ExpanderWidget -> {
                                    it.headerGroups(this.entityId()).forEach {
                                        it.rows().forEach {
                                            it.widgets().forEach { f(it) }
                                        }
                                    }
                                    it.contentGroups(this.entityId()).map { it.group }.forEach {
                                        it.rows().forEach {
                                            it.widgets().forEach { f(it) }
                                        }
                                    }
                                }
                                is WidgetGroup -> {
                                    it.groups(this.entityId()).forEach {
                                        it.rows().forEach {
                                            it.widgets().forEach {
                                                Log.d("***SHEET", "indexing widget: ${it.widgetId()}")
                                                f(it)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    fun indexWidget(widget : Widget) {
        this.widgetById[widget.widgetId()] = widget
    }


    // -----------------------------------------------------------------------------------------
    // ON ACTIVE
    // -----------------------------------------------------------------------------------------

    fun onActive(entityId : EntityId, context : Context)
    {
        // Add game variables
        game(this.campaignId).doMaybe {
            Log.d("***SHEET", "found game ${it.gameId}")
            it.variables().forEach {
                addVariable(it, entityId)
                Log.d("***SHEET", "add game variable ${it.label()}")
            }
        }

        // Add sheet variables
        this.variables().forEach {
            addVariable(it, entityId)
        }

        // Initialize UI components
        this.sections.forEach { it.onActive(entityId, context) }
    }


    // -----------------------------------------------------------------------------------------
    // WIDGET
    // -----------------------------------------------------------------------------------------

    fun widget(widgetId : WidgetId) : AppEff<Widget>
    {
        val widgetOrNull = this.widgetById[widgetId]
        return if (widgetOrNull != null)
            effValue(widgetOrNull)
        else
            effError(AppSheetError(SheetDoesNotHaveWidget(widgetId)))
    }


    // -----------------------------------------------------------------------------------------
    // UPDATE
    // -----------------------------------------------------------------------------------------

    fun update(entityUpdate : EntityUpdate,
               rootView : View?,
               context : Context)
    {
        when (entityUpdate) {
            is EntityUpdateSheet -> this.update(entityUpdate, rootView, context)
        }
    }


    fun updateAndSave(sheetUpdate : EntityUpdateSheet,
                      rootView : View?,
                      context : Context)
    {
        this.update(sheetUpdate, rootView, context)

        // Save update
        if (this.isSavable) {
            launch(UI) {
                saveSheetUpdate(sheetUpdate, context)
            }
        }
    }


    fun update(sheetUpdate : EntityUpdateSheet,
               rootView: View?,
               context : Context)
    {
        when (sheetUpdate) {
            is SheetUpdateWidget -> this.updateWidget(sheetUpdate, rootView, context)
        }
    }


    fun saveSheetUpdate(sheetUpdate : EntityUpdateSheet, context : Context)
    {
        if (!this.isSaved) {
            writeEntity(this.entityId(), context)
            this.isSaved = true
        }

        writeEntityUpdate(this.entityId(), sheetUpdate, context)

//        val rowId = async(CommonPool) { writeEntity(entityId(), context) }.await()
//        when (rowId)
//        {
//            is Val ->
//            is Err -> ApplicationLog.error(rowId.error)
//        }
    }


    fun updateWidget(widgetUpdate : SheetUpdateWidget,
                     rootView : View?,
                     context : Context)
    {
        Log.d("***SHEET", "update widget")

        when (widgetUpdate)
        {
            is WidgetUpdateActionWidget ->
            {
//                val actionWidget = this.actionWidgetById[widgetUpdate.widgetId]
//                actionWidget?.update(widgetUpdate, SheetUIContext(sheetContext, context), rootView)
            }
            is WidgetUpdateBooleanWidget ->
            {
                Log.d("***SHEET", "update boolean widget")
                this.widget(widgetUpdate.widgetId) apDo {
                    when (it) {
                        is BooleanWidget -> {
                            it.update(widgetUpdate, this.entityId(), rootView, context)
                        }
                    }
                }
            }
            is WidgetUpdateNumberWidget ->
            {
                this.widget(widgetUpdate.widgetId) apDo {
                    when (it) {
                        is NumberWidget -> {
                            it.update(widgetUpdate, this.entityId(), rootView, context)
                        }
                    }
                }
            }
            is WidgetUpdateGroupWidget ->
            {
                this.widget(widgetUpdate.widgetId) apDo {
                    when (it) {
                        is WidgetGroup -> {
                            it.update(widgetUpdate, this.entityId(), rootView, context)
                        }
                    }
                }
            }
            is WidgetUpdateExpanderWidget ->
            {
                Log.d("***SHEET ", "update expander widget")
                this.widget(widgetUpdate.widgetId) apDo {
                    Log.d("***SHEET ", "found expander widget")
                    when (it) {
                        is ExpanderWidget -> {
                            Log.d("***SHEET ", "expander widget update")
                            it.update(widgetUpdate, this.entityId(), rootView, context)
                        }
                    }
                }
            }
            is WidgetUpdateListWidget ->
            {
                this.widget(widgetUpdate.widgetId) apDo {
                    when (it) {
                        is ListWidget -> {
                            it.update(widgetUpdate, this.entityId(), rootView, context)
                        }
                    }
                }
            }
            is WidgetUpdatePointsWidget ->
            {
                this.widget(widgetUpdate.widgetId) apDo {
                    when (it) {
                        is PointsWidget -> {
                            it.update(widgetUpdate, this.entityId(), rootView, context)
                        }
                    }
                }
            }
            is WidgetUpdateStoryWidget ->
            {
                this.widget(widgetUpdate.widgetId) apDo {
                    when (it) {
                        is StoryWidget -> {
                            it.update(widgetUpdate, this.entityId(), rootView, context)
                        }
                    }
                }
            }
            is WidgetUpdateTableWidget ->
            {
                this.widget(widgetUpdate.widgetId) apDo {
                    when (it) {
                        is TableWidget -> {
                            it.update(widgetUpdate, this.entityId(), rootView, context)
                        }
                    }
                }
            }
            is WidgetUpdateTextWidget ->
            {
                this.widget(widgetUpdate.widgetId) apDo {
                    when (it) {
                        is TextWidget -> {
                            it.update(widgetUpdate, this.entityId(), rootView, context)
                        }
                    }
                }
            }
        }

    }



}


/**
 * Sheet Id
 */
//data class SheetId(val value : String) : ToDocument, SQLSerializable, Serializable
//{
//
//    // -----------------------------------------------------------------------------------------
//    // CONSTRUCTORS
//    // -----------------------------------------------------------------------------------------
//
//    companion object : Factory<SheetId>
//    {
//        override fun fromDocument(doc: SchemaDoc): ValueParser<SheetId> = when (doc)
//        {
//            is DocText -> effValue(SheetId(doc.text))
//            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
//        }
//
//        fun fromYaml(yamlValue : YamlValue) : YamlParser<SheetId> =
//            when (yamlValue)
//            {
//                is YamlText -> effValue(SheetId(yamlValue.text))
//                else        -> error(UnexpectedTypeFound(YamlType.TEXT,
//                                                         yamlType(yamlValue),
//                                                         yamlValue.path))
//            }
//
//    }
//
//
//    // -----------------------------------------------------------------------------------------
//    // TO DOCUMENT
//    // -----------------------------------------------------------------------------------------
//
//    override fun toDocument() = DocText(this.value)
//
//
//    // -----------------------------------------------------------------------------------------
//    // SQL SERIALIZABLE
//    // -----------------------------------------------------------------------------------------
//
//    override fun asSQLValue() : SQLValue = SQLText({this.value})
//
//}
//
//


/**
 * Sheet Category
 */
data class SheetCategory(val value : String) : ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<SheetCategory>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<SheetCategory> = when (doc)
        {
            is DocText -> effValue(SheetCategory(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocText(this.value)

}

