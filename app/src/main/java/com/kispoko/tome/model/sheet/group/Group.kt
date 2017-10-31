
package com.kispoko.tome.model.sheet.group


import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.PaintDrawable
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import com.kispoko.tome.activity.sheet.SheetActivityGlobal
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.*
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.lib.orm.sql.SQLInt
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.lib.ui.LinearLayoutBuilder
import com.kispoko.tome.model.sheet.style.Corners
import com.kispoko.tome.model.sheet.style.DividerMargin
import com.kispoko.tome.model.sheet.style.DividerThickness
import com.kispoko.tome.model.sheet.style.Spacing
import com.kispoko.tome.model.theme.ColorTheme
import com.kispoko.tome.rts.sheet.SheetComponent
import com.kispoko.tome.rts.sheet.SheetContext
import com.kispoko.tome.rts.sheet.SheetUIContext
import com.kispoko.tome.rts.sheet.SheetManager
import com.kispoko.tome.util.Util
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueError
import lulo.value.ValueParser
import java.io.Serializable
import java.util.*



/**
 * Group
 */
data class Group(override val id : UUID,
                 val format : Comp<GroupFormat>,
                 val index : Prim<GroupIndex>,
                 val rows : CollS<GroupRow>)
                  : ToDocument, Model, SheetComponent, Comparable<Group>, Serializable
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.format.name    = "format"
        this.index.name     = "index"
        this.rows.name      = "rows"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(format : GroupFormat,
                index : GroupIndex,
                rows : MutableList<GroupRow>)
        : this(UUID.randomUUID(),
               Comp(format),
               Prim(index),
               CollS(rows))


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
                              doc, index -> GroupRow.fromDocument(doc, index) }
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

    fun format() : GroupFormat = this.format.value

    fun indexInt() : Int = this.index.value.value

    fun rows() : MutableList<GroupRow> = this.rows.list


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name : String = "group"

    override val modelObject = this


    // SHEET COMPONENT
    // -----------------------------------------------------------------------------------------

    override fun onSheetComponentActive(sheetUIContext : SheetUIContext)
    {
        this.rows.list.forEach { it.onSheetComponentActive(sheetUIContext) }
    }


    // -----------------------------------------------------------------------------------------
    // COMPARABLE
    // -----------------------------------------------------------------------------------------

    override fun compareTo(other : Group) = compareValuesBy(this, other, { it.indexInt() })


    // -----------------------------------------------------------------------------------------
    // VIEW
    // -----------------------------------------------------------------------------------------

    fun view(sheetUIContext: SheetUIContext) = groupView(this, sheetUIContext)

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

    override fun asSQLValue() : SQLValue = SQLInt({this.value})

}


/**
 * Group Format
 */
data class GroupFormat(override val id : UUID,
                       val backgroundColorTheme : Prim<ColorTheme>,
                       val margins : Comp<Spacing>,
                       val padding : Comp<Spacing>,
                       val corners : Comp<Corners>,
                       val showDivider : Prim<ShowGroupDivider>,
                       val dividerColorTheme: Prim<ColorTheme>,
                       val dividerMargins : Prim<DividerMargin>,
                       val dividerThickness : Prim<DividerThickness>)
                        : ToDocument, Model, Serializable
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.backgroundColorTheme.name  = "background_color_theme"
        this.margins.name               = "margins"
        this.padding.name               = "padding"
        this.corners.name               = "corners"
        this.showDivider.name           = "show_divider"
        this.dividerColorTheme.name     = "divider_color_theme"
        this.dividerMargins.name        = "divider_margins"
        this.dividerThickness.name      = "divider_thickness"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(backgroundColorTheme : ColorTheme,
                margins : Spacing,
                padding : Spacing,
                corners : Corners,
                showDivider : ShowGroupDivider,
                dividerColorTheme : ColorTheme,
                dividerMargins : DividerMargin,
                dividerThickness: DividerThickness)
        : this(UUID.randomUUID(),
               Prim(backgroundColorTheme),
               Comp(margins),
               Comp(padding),
               Comp(corners),
               Prim(showDivider),
               Prim(dividerColorTheme),
               Prim(dividerMargins),
               Prim(dividerThickness))


    companion object : Factory<GroupFormat>
    {

        private val defaultBackgroundColorTheme = ColorTheme.transparent
        private val defaultMargins              = Spacing.default()
        private val defaultPadding              = Spacing.default()
        private val defaultCorners              = Corners.default()
        private val defaultShowDivider          = ShowGroupDivider(false)
        private val defaultDividerColorTheme    = ColorTheme.black
        private val defaultDividerMargins       = DividerMargin.default()
        private val defaultDividerThickness     = DividerThickness.default()


        override fun fromDocument(doc: SchemaDoc): ValueParser<GroupFormat> = when (doc)
        {
            is DocDict ->
            {
                effApply(::GroupFormat,
                         // Background Color
                         split(doc.maybeAt("background_color_theme"),
                               effValue(defaultBackgroundColorTheme),
                               { ColorTheme.fromDocument(it)} ),
                         // Margins
                         split(doc.maybeAt("margins"),
                               effValue(defaultMargins),
                               { Spacing.fromDocument(it) }),
                         // Padding
                         split(doc.maybeAt("padding"),
                               effValue(defaultPadding),
                               { Spacing.fromDocument(it) }),
                         // Corners
                         split(doc.maybeAt("corners"),
                               effValue<ValueError,Corners>(defaultCorners),
                               { Corners.fromDocument(it) }),
                         // Show Divider?
                         split(doc.maybeAt("show_divider"),
                               effValue(defaultShowDivider),
                               { ShowGroupDivider.fromDocument(it) }),
                         // Divider Color Theme
                         split(doc.maybeAt("divider_color_theme"),
                               effValue(defaultDividerColorTheme),
                               { ColorTheme.fromDocument(it) }),
                         // Divider Margins
                         split(doc.maybeAt("divider_margins"),
                               effValue(defaultDividerMargins),
                               { DividerMargin.fromDocument(it) }),
                         // Divider Thickness
                         split(doc.maybeAt("divider_thickness"),
                               effValue(defaultDividerThickness),
                               { DividerThickness.fromDocument(it) })
                         )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = GroupFormat(defaultBackgroundColorTheme,
                                    defaultMargins,
                                    defaultPadding,
                                    defaultCorners,
                                    defaultShowDivider,
                                    defaultDividerColorTheme,
                                    defaultDividerMargins,
                                    defaultDividerThickness)

    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
            "background_color_theme" to this.backgroundColorTheme().toDocument(),
            "margins" to this.margins().toDocument(),
            "padding" to this.padding().toDocument(),
            "corners" to this.corners().toDocument(),
            "show_divider" to this.showDivider.value.toDocument(),
            "divider_color_theme" to this.dividerColorTheme().toDocument(),
            "divider_margins" to this.dividerMargins.value.toDocument(),
            "divider_thickness" to this.dividerThickness.value.toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun backgroundColorTheme() : ColorTheme = this.backgroundColorTheme.value

    fun margins() : Spacing = this.margins.value

    fun padding() : Spacing = this.padding.value

    fun corners() : Corners = this.corners.value

    fun showDividerBool() : Boolean = this.showDivider.value.value

    fun dividerColorTheme() : ColorTheme = this.dividerColorTheme.value

    fun dividerMargins() : Float? = this.dividerMargins.value.value

    fun dividerThickness() : Int? = this.dividerThickness.value.value


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name : String = "group_format"

    override val modelObject = this

}


/**
 * Show Divider
 */
data class ShowGroupDivider(val value : Boolean) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ShowGroupDivider>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<ShowGroupDivider> = when (doc)
        {
            is DocBoolean -> effValue(ShowGroupDivider(doc.boolean))
            else          -> effError(UnexpectedType(DocType.BOOLEAN, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocBoolean(this.value)


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLInt({ if (value) 1 else 0})

}


// ---------------------------------------------------------------------------------------------
// GROUP VIEWS
// ---------------------------------------------------------------------------------------------


fun groupView(group : Group, sheetUIContext: SheetUIContext) : View
{
    val layout = viewLayout(group.format(), sheetUIContext)

    // > Rows
    layout.addView(rowsView(group, sheetUIContext))

    // > Divider
    if (group.format().showDividerBool())
        layout.addView(dividerView(group.format(), sheetUIContext))

    return layout
}


private fun viewLayout(format : GroupFormat, sheetUIContext: SheetUIContext) : LinearLayout
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


    val layout = GroupTouchView(sheetUIContext.context)

    layout.orientation = LinearLayout.VERTICAL

    val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                                 LinearLayout.LayoutParams.WRAP_CONTENT)


    val margins = format.margins()
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

    val corners = format.corners()
    val topLeft  = Util.dpToPixel(corners.topLeftCornerRadiusDp()).toFloat()
    val topRight : Float   = Util.dpToPixel(corners.topRightCornerRadiusDp()).toFloat()
    val bottomRight : Float = Util.dpToPixel(corners.bottomRightCornerRadiusDp()).toFloat()
    val bottomLeft :Float = Util.dpToPixel(corners.bottomLeftCornerRadiusDp()).toFloat()

    val radii = floatArrayOf(topLeft, topLeft, topRight, topRight,
                     bottomRight, bottomRight, bottomLeft, bottomLeft)

    bgDrawable.setCornerRadii(radii)

    val bgColor = SheetManager.color(sheetUIContext.sheetId,
                                     format.backgroundColorTheme())

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



private fun rowsView(group : Group, sheetUIContext: SheetUIContext) : View
{
    val layout = rowsViewLayout(group.format(), sheetUIContext.context)

    group.rows().forEach { layout.addView(it.view(sheetUIContext)) }

    return layout
}


private fun rowsViewLayout(format : GroupFormat, context : Context) : LinearLayout
{
    val layout = LinearLayoutBuilder()

    layout.orientation      = LinearLayout.VERTICAL
    layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
    layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

    layout.paddingSpacing   = format.padding()

    return layout.linearLayout(context)
}


private fun dividerView(format : GroupFormat, sheetUIContext: SheetUIContext) : LinearLayout
{
    val divider = LinearLayoutBuilder()

    divider.width               = LinearLayout.LayoutParams.MATCH_PARENT
    divider.heightDp            = format.dividerThickness()

    divider.backgroundColor     = SheetManager.color(sheetUIContext.sheetId,
                                                     format.dividerColorTheme())

    divider.margin.leftDp       = format.dividerMargins()
    divider.margin.rightDp      = format.dividerMargins()

    return divider.linearLayout(sheetUIContext.context)
}


