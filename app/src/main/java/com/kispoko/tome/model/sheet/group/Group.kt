
package com.kispoko.tome.model.sheet.group


import android.content.Context
import android.view.View
import android.widget.LinearLayout
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
import com.kispoko.tome.rts.sheet.SheetManager
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
                  : Model, SheetComponent, Comparable<Group>, Serializable
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
        fun fromDocument(doc : SpecDoc, index : Int) : ValueParser<Group> = when (doc)
        {
            is DocDict -> effApply(::Group,
                                   // Format
                                   split(doc.maybeAt("format"),
                                         effValue(GroupFormat.default),
                                         { GroupFormat.fromDocument(it)}),
                                   // Index
                                   effValue(GroupIndex(index)),
                                   // Groups
                                   doc.list("rows") ap { docList ->
                                       docList.mapIndexed {
                                           doc, index -> GroupRow.fromDocument(doc, index) }
                                   })
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }



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

    override fun onSheetComponentActive(sheetContext : SheetContext)
    {
        this.rows.list.forEach { it.onSheetComponentActive(sheetContext) }
    }


    // -----------------------------------------------------------------------------------------
    // COMPARABLE
    // -----------------------------------------------------------------------------------------

    override fun compareTo(other : Group) = compareValuesBy(this, other, { it.indexInt() })


    // -----------------------------------------------------------------------------------------
    // VIEW
    // -----------------------------------------------------------------------------------------

    fun view(sheetContext : SheetContext) = groupView(this, sheetContext)

}


/**
 * Group Index
 */
data class GroupIndex(val value : Int) : SQLSerializable, Serializable
{

    companion object : Factory<GroupIndex>
    {
        override fun fromDocument(doc: SpecDoc) : ValueParser<GroupIndex> = when (doc)
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
                       val dividerThickness : Prim<DividerThickness>) : Model, Serializable
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


        override fun fromDocument(doc : SpecDoc) : ValueParser<GroupFormat> = when (doc)
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
                         split(doc.maybeAt("divider_color_them"),
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


        val default : GroupFormat =
                GroupFormat(defaultBackgroundColorTheme,
                            defaultMargins,
                            defaultPadding,
                            defaultCorners,
                            defaultShowDivider,
                            defaultDividerColorTheme,
                            defaultDividerMargins,
                            defaultDividerThickness)

    }


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
data class ShowGroupDivider(val value : Boolean) : SQLSerializable, Serializable
{

    companion object : Factory<ShowGroupDivider>
    {
        override fun fromDocument(doc: SpecDoc) : ValueParser<ShowGroupDivider> = when (doc)
        {
            is DocBoolean -> effValue(ShowGroupDivider(doc.boolean))
            else          -> effError(UnexpectedType(DocType.BOOLEAN, docType(doc), doc.path))
        }
    }

    override fun asSQLValue() : SQLValue = SQLInt({ if (value) 1 else 0})

}


// ---------------------------------------------------------------------------------------------
// GROUP VIEWS
// ---------------------------------------------------------------------------------------------


fun groupView(group : Group, sheetContext : SheetContext) : View
{
    val layout = viewLayout(group.format(), sheetContext)

    // > Rows
    layout.addView(rowsView(group, sheetContext))

    // > Divider
    if (group.format().showDividerBool())
        layout.addView(dividerView(group.format(), sheetContext))

    return layout
}


private fun viewLayout(format : GroupFormat, sheetContext : SheetContext) : LinearLayout
{
    val layout = LinearLayoutBuilder()

    layout.orientation          = LinearLayout.VERTICAL;
    layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
    layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

    layout.marginSpacing        = format.margins()

    layout.backgroundColor      = SheetManager.color(sheetContext.sheetId,
                                                     format.backgroundColorTheme())
    layout.corners              = format.corners()

    return layout.linearLayout(sheetContext.context)
}


private fun rowsView(group : Group, sheetContext : SheetContext) : View
{
    val layout = rowsViewLayout(group.format(), sheetContext.context)

    group.rows().forEach { layout.addView(it.view(sheetContext)) }

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


private fun dividerView(format : GroupFormat, sheetContext : SheetContext) : LinearLayout
{
    val divider = LinearLayoutBuilder()

    divider.width               = LinearLayout.LayoutParams.MATCH_PARENT
    divider.heightDp            = format.dividerThickness()

    divider.backgroundColor     = SheetManager.color(sheetContext.sheetId,
                                                     format.dividerColorTheme())

    divider.margin.leftDp       = format.dividerMargins()
    divider.margin.rightDp      = format.dividerMargins()

    return divider.linearLayout(sheetContext.context)
}


