
package com.kispoko.tome.model.sheet.group


import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.PaintDrawable
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import com.kispoko.tome.activity.sheet.SheetActivityGlobal
import com.kispoko.tome.db.*
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.ProdType
import com.kispoko.tome.lib.orm.RowValue2
import com.kispoko.tome.lib.orm.RowValue3
import com.kispoko.tome.lib.orm.schema.CollValue
import com.kispoko.tome.lib.orm.schema.MaybeProdValue
import com.kispoko.tome.lib.orm.schema.PrimValue
import com.kispoko.tome.lib.orm.schema.ProdValue
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.asSQLValue
import com.kispoko.tome.lib.ui.LinearLayoutBuilder
import com.kispoko.tome.model.sheet.style.*
import com.kispoko.tome.rts.entity.EntityId
import com.kispoko.tome.rts.entity.colorOrBlack
import com.kispoko.tome.rts.entity.sheet.SheetComponent
import com.kispoko.tome.util.Util
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueError
import lulo.value.ValueParser
import maybe.Just
import maybe.Maybe
import maybe.Nothing
import java.io.Serializable
import java.util.*



/**
 * Group
 */
data class Group(override val id : UUID,
                 private val format : GroupFormat,
                 private var index : GroupIndex,
                 private val rows : MutableList<GroupRow>)
                  : ToDocument, ProdType, SheetComponent, Comparable<Group>, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(format : GroupFormat,
                index : GroupIndex,
                rows : List<GroupRow>)
        : this(UUID.randomUUID(),
               format,
               index,
               rows.toMutableList())


    companion object
    {
        fun fromDocument(doc : SchemaDoc, index : Int) : ValueParser<Group> = when (doc)
        {
            is DocDict ->
            {
                apply(::Group,
                      // Format
                      split(doc.maybeAt("format"),
                            effValue(GroupFormat.default()),
                            { GroupFormat.fromDocument(it)}),
                      // Index
                      effValue(GroupIndex(index)),
                      // Groups
                      doc.list("rows") ap { docList ->
                          docList.mapIndexed {
                              itemDoc, itemIndex -> GroupRow.fromDocument(itemDoc, itemIndex) }
                      })
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

    fun format() : GroupFormat = this.format

    fun indexInt() : Int = this.index.value

    fun rows() : List<GroupRow> = this.rows


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_GroupValue =
        RowValue3(groupTable, ProdValue(this.format),
                              PrimValue(this.index),
                              CollValue(this.rows))


    // SHEET COMPONENT
    // -----------------------------------------------------------------------------------------

    override fun onSheetComponentActive(entityId : EntityId, context : Context)
    {
        this.rows.forEach { it.onSheetComponentActive(entityId, context) }
    }


    // -----------------------------------------------------------------------------------------
    // COMPARABLE
    // -----------------------------------------------------------------------------------------

    override fun compareTo(other : Group) = compareValuesBy(this, other, { it.indexInt() })


    // -----------------------------------------------------------------------------------------
    // VIEW
    // -----------------------------------------------------------------------------------------

    fun view(entityId : EntityId, context : Context) = groupView(this, entityId, context)

}


/**
 * Group Index
 */
data class GroupIndex(val value : Int) : SQLSerializable, Serializable
{

    companion object : Factory<GroupIndex>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<GroupIndex> = when (doc)
        {
            is DocNumber -> effValue(GroupIndex(doc.number.toInt()))
            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
        }
    }

    override fun asSQLValue() = this.value.asSQLValue()

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

    group.rows().forEach { layout.addView(it.view(entityId, context)) }

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


