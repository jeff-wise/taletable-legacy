
package com.kispoko.tome.model.sheet


import android.content.Context
import android.view.View
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.*
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.model.campaign.CampaignId
import com.kispoko.tome.model.game.engine.variable.Variable
import com.kispoko.tome.model.game.engine.variable.VariableId
import com.kispoko.tome.model.sheet.section.Section
import com.kispoko.tome.model.sheet.section.SectionName
import com.kispoko.tome.model.sheet.widget.StoryWidget
import com.kispoko.tome.model.sheet.widget.TableWidget
import com.kispoko.tome.model.sheet.widget.TextWidget
import com.kispoko.tome.model.sheet.widget.Widget
import com.kispoko.tome.rts.sheet.*
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueError
import lulo.value.ValueParser
import java.io.Serializable
import java.util.*



/**
 * Sheet
 */
data class Sheet(override val id : UUID,
                 val sheetId : Prim<SheetId>,
                 val campaignId : Prim<CampaignId>,
                 val sections : Coll<Section>,
                 val variables : Conj<Variable>,
                 val settings : Comp<Settings>) : Model, Serializable
{

    // -----------------------------------------------------------------------------------------
    // SCHEMA
    // -----------------------------------------------------------------------------------------

    init
    {
        this.sheetId.name       = "sheet_id"
        this.campaignId.name    = "campaign_id"
        this.sections.name      = "sections"
        this.variables.name     = "variables"
        this.settings.name      = "settings"
    }


    // -----------------------------------------------------------------------------------------
    // INDEXES
    // -----------------------------------------------------------------------------------------

    private val variableById : Map<VariableId,Variable> =
                                this.variables().associateBy { it.variableId() }

    // Widgets
    // -----------------------------------------------------------------------------------------

    private val storyWidgetById : MutableMap<UUID,StoryWidget> = mutableMapOf()

    private val tableWidgetById : MutableMap<UUID,TableWidget> = mutableMapOf()

    private val textWidgetById : MutableMap<UUID,TextWidget> = mutableMapOf()


    // Index Widgets
    // -----------------------------------------------------------------------------------------

    init {
        this.forEachWidget {
            when (it) {
                is StoryWidget -> storyWidgetById.put(it.id, it)
                is TableWidget -> tableWidgetById.put(it.id, it)
                is TextWidget  -> textWidgetById.put(it.id, it)
            }
        }
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(sheetId : SheetId,
                campaignId : CampaignId,
                sections : MutableList<Section>,
                variables : MutableSet<Variable>,
                settings : Settings)
        : this(UUID.randomUUID(),
               Prim(sheetId),
               Prim(campaignId),
               Coll(sections),
               Conj(variables),
               Comp(settings))


    companion object : Factory<Sheet>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<Sheet> = when (doc)
        {
            is DocDict ->
            {
                effApply(::Sheet,
                         // Sheet Id
                         doc.at("id") ap { SheetId.fromDocument(it) },
                         // Campaign Id
                         doc.at("campaign_id") ap { CampaignId.fromDocument(it) },
                         // Section List
                         doc.list("sections") ap { docList ->
                             docList.mapMut { Section.fromDocument(it) }
                         },
                         // Variables
                         split(doc.maybeList("variables"),
                               effValue<ValueError,MutableSet<Variable>>(mutableSetOf()),
                               { it.mapSetMut { Variable.fromDocument(it) } }),
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

    fun sheetId() : SheetId = this.sheetId.value

    fun campaignId() : CampaignId = this.campaignId.value

    fun sections() : List<Section> = this.sections.list

    fun sectionWithName(sectionName : SectionName) : Section? =
        this.sections().filter { it.name().equals(sectionName) }
                       .firstOrNull()

    fun sectionWithIndex(index : Int) : Section? = this.sections()[index]

    fun settings() : Settings = this.settings.value

    fun variables() : Set<Variable> = this.variables.set


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name : String = "sheet"

    override val modelObject = this


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

    fun onActive(sheetContext : SheetContext)
    {
        sections.list.forEach { it.onActive(sheetContext) }
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
            is WidgetUpdateStoryWidget ->
            {
                val storyWidget = this.storyWidgetById[widgetUpdate.widgetId]
                storyWidget?.update(widgetUpdate, SheetUIContext(sheetContext, context), rootView)
            }
            is WidgetUpdateTableWidget ->
            {
                val tableWidget = this.tableWidgetById[widgetUpdate.widgetId]
                tableWidget?.update(widgetUpdate, sheetContext, rootView)
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
data class SheetId(val value : String) : SQLSerializable, Serializable
{

    companion object : Factory<SheetId>
    {
        override fun fromDocument(doc: SpecDoc) : ValueParser<SheetId> = when (doc)
        {
            is DocText -> effValue(SheetId(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }

    override fun asSQLValue() : SQLValue = SQLText({this.value})

}



