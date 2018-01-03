
package com.kispoko.tome.model.sheet


import android.content.Context
import android.view.View
import com.kispoko.tome.db.DB_SheetValue
import com.kispoko.tome.db.sheetTable
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.ProdType
import com.kispoko.tome.lib.orm.RowValue6
import com.kispoko.tome.lib.orm.schema.CollValue
import com.kispoko.tome.lib.orm.schema.PrimValue
import com.kispoko.tome.lib.orm.schema.ProdValue
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.model.campaign.CampaignId
import com.kispoko.tome.model.game.engine.Engine
import com.kispoko.tome.model.game.engine.variable.Variable
import com.kispoko.tome.model.game.engine.variable.VariableId
import com.kispoko.tome.model.sheet.section.Section
import com.kispoko.tome.model.sheet.section.SectionName
import com.kispoko.tome.model.sheet.widget.*
import com.kispoko.tome.rts.sheet.*
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import java.io.Serializable
import java.util.*



/**
 * Sheet
 */
data class Sheet(override val id : UUID,
                 private var sheetId : SheetId,
                 private var campaignId : CampaignId,
                 private var sections : MutableList<Section>,
                 private var engine : Engine,
                 private var variables : MutableList<Variable>,
                 private var settings : Settings)
                  : ProdType, ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // INDEXES
    // -----------------------------------------------------------------------------------------

    private val variableById : Map<VariableId,Variable> =
                                this.variables().associateBy { it.variableId() }

    // Widgets
    // -----------------------------------------------------------------------------------------

    private val listWidgetById : MutableMap<UUID,ListWidget> = mutableMapOf()

    private val pointsWidgetById : MutableMap<UUID,PointsWidget> = mutableMapOf()

    private val storyWidgetById : MutableMap<UUID,StoryWidget> = mutableMapOf()

    private val tableWidgetById : MutableMap<UUID,TableWidget> = mutableMapOf()

    private val textWidgetById : MutableMap<UUID,TextWidget> = mutableMapOf()


    // Index Widgets
    // -----------------------------------------------------------------------------------------

    init {
        this.forEachWidget {
            when (it) {
                is ListWidget   -> listWidgetById.put(it.id, it)
                is PointsWidget -> pointsWidgetById.put(it.id, it)
                is StoryWidget  -> storyWidgetById.put(it.id, it)
                is TableWidget  -> tableWidgetById.put(it.id, it)
                is TextWidget   -> textWidgetById.put(it.id, it)
            }
        }
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(sheetId : SheetId,
                campaignId : CampaignId,
                sections : List<Section>,
                engine : Engine,
                variables : List<Variable>,
                settings : Settings)
        : this(UUID.randomUUID(),
               sheetId,
               campaignId,
               sections.toMutableList(),
               engine,
               variables.toMutableList(),
               settings)


    companion object : Factory<Sheet>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<Sheet> = when (doc)
        {
            is DocDict ->
            {
                apply(::Sheet,
                      // Sheet Id
                      doc.at("id") ap { SheetId.fromDocument(it) },
                      // Campaign Id
                      doc.at("campaign_id") ap { CampaignId.fromDocument(it) },
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

    fun sheetId() : SheetId = this.sheetId


    fun campaignId() : CampaignId = this.campaignId


    fun sections() : List<Section> = this.sections


    fun engine() : Engine = this.engine


    fun sectionWithName(sectionName : SectionName) : Section? =
        this.sections().filter { it.name().equals(sectionName) }
                       .firstOrNull()


    fun sectionWithIndex(index : Int) : Section? = this.sections()[index]


    fun settings() : Settings = this.settings


    fun variables() : List<Variable> = this.variables


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_SheetValue =
            RowValue6(sheetTable, PrimValue(sheetId),
                                  PrimValue(campaignId),
                                  CollValue(sections),
                                  ProdValue(engine),
                                  CollValue(variables),
                                  ProdValue(settings))


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "id" to this.sheetId().toDocument(),
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
                        row.widgets().forEach { f(it) }
                    }
                }
            }
        }
    }


    // -----------------------------------------------------------------------------------------
    // ON ACTIVE
    // -----------------------------------------------------------------------------------------

    fun onActive(sheetUIContext : SheetUIContext)
    {
        this.variables().forEach {
            SheetManager.addVariable(sheetUIContext.sheetId, it)
        }

        this.sections.forEach { it.onActive(sheetUIContext) }
    }


    // -----------------------------------------------------------------------------------------
    // UPDATE
    // -----------------------------------------------------------------------------------------

    fun update(widgetUpdate : WidgetUpdate,
               sheetContext : SheetContext,
               rootView : View,
               context : Context) =
        when (widgetUpdate)
        {
            is WidgetUpdateListWidget ->
            {
                val listWidget = this.listWidgetById[widgetUpdate.widgetId]
                listWidget?.update(widgetUpdate, SheetUIContext(sheetContext, context), rootView)
            }
            is WidgetUpdatePointsWidget ->
            {
                val pointsWidget = this.pointsWidgetById[widgetUpdate.widgetId]
                pointsWidget?.update(widgetUpdate, SheetUIContext(sheetContext, context), rootView)
            }
            is WidgetUpdateStoryWidget ->
            {
                val storyWidget = this.storyWidgetById[widgetUpdate.widgetId]
                storyWidget?.update(widgetUpdate, SheetUIContext(sheetContext, context), rootView)
            }
            is WidgetUpdateTableWidget ->
            {
                val tableWidget = this.tableWidgetById[widgetUpdate.widgetId]
                tableWidget?.update(widgetUpdate, SheetUIContext(sheetContext, context), rootView)
            }
            is WidgetUpdateTextWidget ->
            {
                val textWidget = this.textWidgetById[widgetUpdate.widgetId]
                textWidget?.update(widgetUpdate, sheetContext, rootView)
            }
        }


}


/**
 * Sheet Id
 */
data class SheetId(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<SheetId>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<SheetId> = when (doc)
        {
            is DocText -> effValue(SheetId(doc.text))
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



