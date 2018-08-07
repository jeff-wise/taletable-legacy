
package com.taletable.android.model.sheet.group


import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.PaintDrawable
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import com.taletable.android.activity.sheet.SheetActivityGlobal
import com.taletable.android.db.*
import com.taletable.android.lib.Factory
import com.taletable.android.lib.orm.ProdType
import com.taletable.android.lib.orm.RowValue2
import com.taletable.android.lib.orm.schema.MaybeProdValue
import com.taletable.android.lib.orm.schema.ProdValue
import com.taletable.android.lib.ui.LinearLayoutBuilder
import com.taletable.android.model.engine.constraint.Trigger
import com.taletable.android.model.engine.tag.Tag
import com.taletable.android.model.engine.tag.TagQuery
import com.taletable.android.model.engine.tag.TagQueryTag
import com.taletable.android.model.sheet.style.*
import com.taletable.android.rts.entity.EntityId
import com.taletable.android.rts.entity.colorOrBlack
import com.taletable.android.rts.entity.groupWithId
import com.taletable.android.rts.entity.sheet.SheetComponent
import com.taletable.android.util.Util
import effect.*
import lulo.document.*
import lulo.value.*
import lulo.value.UnexpectedType
import maybe.Just
import maybe.Maybe
import maybe.Nothing
import maybe.filterJust
import java.io.Serializable
import java.util.*



/**
 * Group
 */
data class Group(val id : GroupId,
                 private val name : GroupName,
                 private val summary : GroupSummary,
                 private val format : GroupFormat,
                 private var index : Int,
                 private val rows : MutableList<GroupRow>,
                 private val tags : List<Tag>,
                 private var context : Maybe<GroupContext>,
                 private val trigger : Maybe<Trigger>)
                  : ToDocument, SheetComponent, Comparable<Group>, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(name : GroupName,
                summary : GroupSummary,
                format : GroupFormat,
                index : Int,
                rows : List<GroupRow>,
                tags : List<Tag>,
                context : Maybe<GroupContext>,
                trigger : Maybe<Trigger>)
        : this(GroupId(UUID.randomUUID()),
               name,
               summary,
               format,
               index,
               rows.toMutableList(),
               tags,
               context,
               trigger)


    constructor(format : GroupFormat,
                rows : List<GroupRow>)
        : this(GroupId(UUID.randomUUID()),
               GroupName(""),
               GroupSummary(""),
               format,
               0,
               rows.toMutableList(),
               listOf(),
               Nothing(),
               Nothing())



    companion object
    {
        fun fromDocument(doc : SchemaDoc, index : Int) : ValueParser<Group> = when (doc)
        {
            is DocDict ->
            {
                apply(::Group,
                      // Group Id
                      split(doc.maybeAt("id"),
                            effValue(GroupId(UUID.randomUUID())),
                            { GroupId.fromDocument(it) }),
                      // Group Name
                      split(doc.maybeText("name"),
                            effValue(GroupName("")),
                            { effValue(GroupName(it)) }),
                      // Group Summary
                      split(doc.maybeText("summary"),
                            effValue(GroupSummary("")),
                            { effValue(GroupSummary(it)) }),
                      // Format
                      split(doc.maybeAt("format"),
                            effValue(GroupFormat.default()),
                            { GroupFormat.fromDocument(it)}),
                      // Index
                      effValue(index),
                      // Rows
                      doc.list("rows") ap { docList ->
                          docList.mapIndexed { itemDoc, itemIndex -> GroupRow.fromDocument(itemDoc, itemIndex) }
                      },
                      // Tags
                      split(doc.maybeList("tags"),
                            effValue(listOf()),
                             { it.map { Tag.fromDocument(it) } }),
                      // Context
                      split(doc.maybeAt("context"),
                            effValue<ValueError,Maybe<GroupContext>>(Nothing()),
                            { apply(::Just, GroupContext.fromDocument(it)) }),
                      // Trigger
                      split(doc.maybeList("trigger"),
                            effValue<ValueError,Maybe<Trigger>>(Nothing()),
                            { apply(::Just, Trigger.fromDocument(it)) })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "format" to this.format().toDocument(),
        "rows" to DocList(this.rows().map { it.toDocument() })
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun name() : GroupName = this.name


    fun summary() : GroupSummary = this.summary


    fun format() : GroupFormat = this.format


    fun index() : Int = this.index


    fun rows() : List<GroupRow> = this.rows


    fun tags() : List<Tag> = this.tags


    fun context() : Maybe<GroupContext> = this.context


    fun trigger() : Maybe<Trigger> = this.trigger


    // SHEET COMPONENT
    // -----------------------------------------------------------------------------------------

    override fun onSheetComponentActive(entityId : EntityId, context : Context)
    {
        this.rows.forEach { it.onSheetComponentActive(entityId, context) }
    }


    // -----------------------------------------------------------------------------------------
    // COMPARABLE
    // -----------------------------------------------------------------------------------------

    override fun compareTo(other : Group) = compareValuesBy(this, other, { it.index() })


    // -----------------------------------------------------------------------------------------
    // VIEW
    // -----------------------------------------------------------------------------------------

    fun view(entityId : EntityId,
             context : Context,
             groupContext : Maybe<GroupContext> = Nothing()) : View
    {
        when (groupContext) {
            is Just -> {
                when (this.context) {
                    is Nothing -> this.context = groupContext
                }
            }
        }

        return groupView(this, entityId, context)
    }

}


/**
 * Group Name
 */
data class GroupName(val value : String) : ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<GroupName>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<GroupName> = when (doc)
        {
            is DocText -> effValue(GroupName(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocText(this.value)

}


/**
 * Group Summary
 */
data class GroupSummary(val value : String) : ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<GroupSummary>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<GroupSummary> = when (doc)
        {
            is DocText -> effValue(GroupSummary(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocText(this.value)

}


/**
 * Group Context
 */
data class GroupContext(val value : String) : ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<GroupContext>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<GroupContext> = when (doc)
        {
            is DocText -> effValue(GroupContext(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocText(this.value)

}


/**
 * Group Id
 */
data class GroupId(val value : UUID) : Serializable
{

    companion object : Factory<GroupId>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<GroupId> = when (doc)
        {
            is DocText -> {
                try {
                    effValue<ValueError,GroupId>(GroupId(UUID.fromString(doc.text)))
                }
                catch (e : IllegalArgumentException) {
                    effError<ValueError,GroupId>(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
                }
            }
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }

    }

}


sealed class GroupReference : ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<GroupReference>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<GroupReference> =
            when (doc.case())
            {
                "group_id"     -> GroupReferenceId.fromDocument(doc) as ValueParser<GroupReference>
                "group_set_id" -> GroupReferenceSetId.fromDocument(doc) as ValueParser<GroupReference>
                "group"        -> GroupReferenceLiteral.fromDocument(doc) as ValueParser<GroupReference>
                else           -> effError(UnknownCase(doc.case(), doc.path))
            }
    }


    // -----------------------------------------------------------------------------------------
    // GROUP ID
    // -----------------------------------------------------------------------------------------

    abstract fun groupId() : GroupId?
}


data class GroupReferenceId(val groupId : GroupId) : GroupReference()
{
    companion object : Factory<GroupReferenceId>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<GroupReferenceId> = when (doc)
        {
            is DocText -> {
                try {
                    effValue<ValueError,GroupReferenceId>(
                            GroupReferenceId(GroupId(UUID.fromString(doc.text))))
                }
                catch (e : IllegalArgumentException) {
                    effError<ValueError,GroupReferenceId>(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
                }
            }
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    override fun toDocument() = DocText(this.groupId.value.toString()).withCase("group_id")


    override fun groupId() = this.groupId

}


data class GroupReferenceSetId(val groupSetId : GroupSetId) : GroupReference()
{
    companion object : Factory<GroupReferenceSetId>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<GroupReferenceSetId> =
                apply(::GroupReferenceSetId, GroupSetId.fromDocument(doc))

    }


    override fun toDocument() = DocText(this.groupSetId.value.toString()).withCase("group_set_id")


    override fun groupId() = null

}


data class GroupReferenceLiteral(val group : Group) : GroupReference()
{

    companion object : Factory<GroupReferenceLiteral>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<GroupReferenceLiteral> =
                effApply(::GroupReferenceLiteral, Group.fromDocument(doc, 0))
    }


    override fun toDocument() = this.group.toDocument()
                                    .withCase("group")


    override fun groupId() = this.group.id
}


/**
 * Group Index
 */
data class GroupIndex(val groups : MutableList<Group>,
                      val groupSets : MutableList<GroupSet>)
{

    // -----------------------------------------------------------------------------------------
    // | Properties
    // -----------------------------------------------------------------------------------------

    // | Properties > Indexes
    // -----------------------------------------------------------------------------------------

    private val groupById : MutableMap<GroupId,Group> =
                               groups.associateBy { it.id }
                                    as MutableMap<GroupId,Group>

    private val groupsWithTag : MutableMap<Tag,MutableList<Group>> = mutableMapOf()


    private val groupSetById : MutableMap<GroupSetId,GroupSet> =
                                   groupSets.associateBy { it.id }
                                        as MutableMap<GroupSetId,GroupSet>


    // -----------------------------------------------------------------------------------------
    // | Constructors
    // -----------------------------------------------------------------------------------------

    init {
        indexGroups(groups)
    }


    companion object
    {
        fun fromDocument(doc : SchemaDoc) : ValueParser<GroupIndex> = when (doc)
        {
            is DocDict ->
            {
                apply(::GroupIndex,
                      // Groups
                      doc.list("groups") ap { docList ->
                          docList.mapMut { Group.fromDocument(it, 0) } },
                      // Group Sets
                      split(doc.maybeList("group_sets"),
                            effValue(mutableListOf()),
                            { it.mapMut { GroupSet.fromDocument(it) } }))
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun empty() : GroupIndex = GroupIndex(mutableListOf(), mutableListOf())

    }


    // -----------------------------------------------------------------------------------------
    // | Methods
    // -----------------------------------------------------------------------------------------

    // | Methods > Public
    // -----------------------------------------------------------------------------------------

    fun groups() : List<Group> = this.groups


    fun groupSets() : List<GroupSet> = this.groupSets


    fun merge(groupIndex : GroupIndex)
    {
        this.addGroups(groupIndex.groups())
        this.addGroupSets(groupIndex.groupSets())
    }


    fun groupWithId(groupId : GroupId) : Maybe<Group>
    {
        val _group = this.groupById[groupId]
        return if (_group != null)
            Just(_group)
        else
            Nothing()
    }


    fun groupSetWithId(groupSetId : GroupSetId) : Maybe<GroupSet>
    {
        val groupSet = this.groupSetById[groupSetId]
        return if (groupSet != null)
            Just(groupSet)
        else
            Nothing()
    }


    fun groups(tagQuery : TagQuery) : List<Group> = when (tagQuery)
    {
        is TagQueryTag ->
        {
            groupsWithTag[tagQuery.tag] ?: listOf()
        }
        else -> listOf()
    }


    private fun addGroups(groups : List<Group>)
    {
        this.groups.addAll(groups)

        groups.forEach {
            this.groupById[it.id] = it
        }

        this.indexGroups(groups)
    }


    private fun addGroupSets(groupSets : List<GroupSet>)
    {
        this.groupSets.addAll(groupSets)

        groupSets.forEach {
            this.groupSetById[it.id] = it
        }
    }


    // | Methods > Private
    // -----------------------------------------------------------------------------------------

    private fun indexGroups(groups : List<Group>)
    {
        groups.forEach { group ->
            group.tags().forEach { tag ->
                if (!groupsWithTag.containsKey(tag))
                    groupsWithTag[tag] = mutableListOf()
                groupsWithTag[tag]!!.add(group)
            }
        }
    }


}



data class GroupSet(val id : GroupSetId,
                    val groupIds : List<GroupId>) : GroupReference()
{

    // -----------------------------------------------------------------------------------------
    // | Constructors
    // -----------------------------------------------------------------------------------------

    companion object : Factory<GroupSet>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<GroupSet> = when (doc)
        {
            is DocDict ->
            {
                apply(::GroupSet,
                      // Id
                      doc.at("id").apply { GroupSetId.fromDocument(it) },
                      // Group IDs
                      doc.list("group_ids") ap { docList ->
                          docList.map { GroupId.fromDocument(it) } }
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // | To Document
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocList(groupIds.map { DocText(it.value.toString()) })


    // -----------------------------------------------------------------------------------------
    // | Group Reference
    // -----------------------------------------------------------------------------------------

    override fun groupId() = this.groupIds.firstOrNull() ?: GroupId(UUID.randomUUID())


    // -----------------------------------------------------------------------------------------
    // | Groups
    // -----------------------------------------------------------------------------------------

    fun groups(entityId : EntityId) : List<Group> =
        this.groupIds.map { groupWithId(it, entityId) }.filterJust()


}


/**
 * Group Set Id
 */
data class GroupSetId(val value : UUID) : Serializable
{

    companion object : Factory<GroupSetId>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<GroupSetId> = when (doc)
        {
            is DocText -> {
                try {
                    effValue<ValueError,GroupSetId>(GroupSetId(UUID.fromString(doc.text)))
                }
                catch (e : IllegalArgumentException) {
                    effError<ValueError,GroupSetId>(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
                }
            }
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }

    }

}



/**
 * Group Format
 */
data class GroupFormat(override val id : UUID,
                       val elementFormat : ElementFormat,
                       val border : Maybe<Border>)
                        : ToDocument, ProdType, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(elementFormat : ElementFormat,
                border : Maybe<Border>)
        : this(UUID.randomUUID(),
               elementFormat,
               border)


    companion object : Factory<GroupFormat>
    {

        private fun defaultElementFormat() = ElementFormat.default()


        override fun fromDocument(doc : SchemaDoc): ValueParser<GroupFormat> = when (doc)
        {
            is DocDict ->
            {
                apply(::GroupFormat,
                      // Element Format
                      split(doc.maybeAt("element_format"),
                            effValue(defaultElementFormat()),
                            { ElementFormat.fromDocument(it)} ),
                      // Border
                      split(doc.maybeAt("border"),
                            effValue<ValueError, Maybe<Border>>(Nothing()),
                            { effApply(::Just, Border.fromDocument(it)) })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = GroupFormat(defaultElementFormat(), Nothing())

    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "element_format" to this.elementFormat.toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun elementFormat() : ElementFormat = this.elementFormat


    fun border() : Maybe<Border> = this.border


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_GroupFormatValue =
        RowValue2(groupFormatTable, ProdValue(this.elementFormat),
                                    MaybeProdValue(this.border))

}



// ---------------------------------------------------------------------------------------------
// GROUP VIEWS
// ---------------------------------------------------------------------------------------------


fun groupView(group : Group, entityId : EntityId, context : Context) : View
{
    val layout = viewLayout(group.format(), entityId, context)

    // Top Border
    val topBorder = group.format().border().apply { it.top() }
    when (topBorder) {
        is Just -> layout.addView(dividerView(topBorder.value, entityId, context))
    }

    // Rows
    layout.addView(rowsView(group, entityId, context))

    val bottomBorder = group.format().border().apply { it.bottom() }
    when (bottomBorder) {
        is Just -> layout.addView(dividerView(bottomBorder.value, entityId, context))
    }

    return layout
}


private fun viewLayout(format : GroupFormat,
                       entityId : EntityId,
                       context : Context) : LinearLayout
{
//    val layout = LinearLayoutBuilder()
//
//    layout.orientation          = LinearLayout.VERTICAL;
//    layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
//    layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT
//
//    layout.marginSpacing        = format.margins()
//
//    layout.backgroundColor      = SheetManager.color(sheetUIContext.sheetId,
//                                                     format.backgroundColorTheme())
//    layout.corners              = format.corners()
//
//    return layout.linearLayout(sheetUIContext.context)


    val layout = GroupTouchView(context)

    layout.orientation = LinearLayout.VERTICAL

    val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                                 LinearLayout.LayoutParams.WRAP_CONTENT)


    val margins = format.elementFormat().margins()
    layoutParams.leftMargin = margins.leftPx()
    layoutParams.rightMargin = margins.rightPx()
    layoutParams.topMargin = margins.topPx()
    layoutParams.bottomMargin = margins.bottomPx()

    layout.layoutParams = layoutParams

//        val padding = widgetFormat.padding()
//        layout.setPadding(padding.leftPx(),
//                          padding.topPx(),
//                          padding.rightPx(),
//                          padding.bottomPx())


    // Background
    val bgDrawable = PaintDrawable()

    val corners = format.elementFormat().corners()
    val topLeft  = Util.dpToPixel(corners.topLeftCornerRadiusDp()).toFloat()
    val topRight : Float   = Util.dpToPixel(corners.topRightCornerRadiusDp()).toFloat()
    val bottomRight : Float = Util.dpToPixel(corners.bottomRightCornerRadiusDp()).toFloat()
    val bottomLeft :Float = Util.dpToPixel(corners.bottomLeftCornerRadiusDp()).toFloat()

    val radii = floatArrayOf(topLeft, topLeft, topRight, topRight,
                     bottomRight, bottomRight, bottomLeft, bottomLeft)

    bgDrawable.setCornerRadii(radii)

    val bgColor = colorOrBlack(format.elementFormat().backgroundColorTheme(), entityId)

    bgDrawable.colorFilter = PorterDuffColorFilter(bgColor, PorterDuff.Mode.SRC_IN)

    layout.background = bgDrawable

    return layout
}


class GroupTouchView(context : Context) : LinearLayout(context)
{


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
                MotionEvent.ACTION_MOVE ->
                {
                    // Log.d("***GROUP", "x: ${ev.x} y: ${ev.y}")
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



private fun rowsView(group : Group, entityId : EntityId, context : Context) : View
{
    val layout = rowsViewLayout(group.format(), context)

    group.rows().forEach {
        layout.addView(it.view(group.context(), entityId, context))
    }

    return layout
}


private fun rowsViewLayout(format : GroupFormat, context : Context) : LinearLayout
{
    val layout = LinearLayoutBuilder()

    layout.orientation      = LinearLayout.VERTICAL
    layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
    layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

    layout.paddingSpacing   = format.elementFormat().padding()

    return layout.linearLayout(context)
}


private fun dividerView(format : BorderEdge, entityId : EntityId, context : Context) : LinearLayout
{
    val divider = LinearLayoutBuilder()

    divider.width               = LinearLayout.LayoutParams.MATCH_PARENT
    divider.heightDp            = format.thickness().value

    divider.backgroundColor     = colorOrBlack(format.colorTheme(), entityId)

    return divider.linearLayout(context)
}


