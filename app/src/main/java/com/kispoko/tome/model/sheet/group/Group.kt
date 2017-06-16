
package com.kispoko.tome.model.sheet.group


import android.content.Context
import android.view.View
import android.widget.LinearLayout
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.*
import com.kispoko.tome.lib.model.Model
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
                 val index : Prim<Int>,
                 val rows : CollS<GroupRow>)
                  : Model, SheetComponent, Comparable<Group>, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun fromDocument(doc : SpecDoc, index : Int) : ValueParser<Group> = when (doc)
        {
            is DocDict -> effApply(::Group,
                                   // Model Id
                                   effValue(UUID.randomUUID()),
                                   // Format
                                   split(doc.maybeAt("format"),
                                         effValue(Comp(GroupFormat.default())),
                                         { effApply(::Comp, GroupFormat.fromDocument(it))}),
                                   // Index
                                   effValue(Prim(index)),
                                   // Groups
                                   doc.list("rows") ap { docList ->
                                       effApply(::CollS, docList.mapIndexed {
                                           doc, index -> GroupRow.fromDocument(doc, index) })
                                   })
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun format() : GroupFormat = this.format.value

    fun index() : Int = this.index.value

    fun rows() : MutableList<GroupRow> = this.rows.list


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    // SHEET COMPONENT
    // -----------------------------------------------------------------------------------------

    override fun onSheetComponentActive(sheetContext : SheetContext)
    {
        this.rows.list.forEach { it.onSheetComponentActive(sheetContext) }
    }


    // -----------------------------------------------------------------------------------------
    // COMPARABLE
    // -----------------------------------------------------------------------------------------

    override fun compareTo(other : Group) = compareValuesBy(this, other, { it.index() })


    // -----------------------------------------------------------------------------------------
    // VIEW
    // -----------------------------------------------------------------------------------------

    override fun view(sheetContext : SheetContext) : View
    {
        val layout = this.viewLayout(sheetContext)

        // > Rows
        layout.addView(this.rowsView(sheetContext))

        // > Divider
        if (this.format.value.showDivider())
            layout.addView(this.dividerView(sheetContext))

        return layout
    }


    private fun viewLayout(sheetContext : SheetContext) : LinearLayout
    {
        val layout = LinearLayoutBuilder()

        layout.orientation          = LinearLayout.VERTICAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.marginSpacing        = this.format().margins()

        layout.backgroundColor      = SheetManager.color(sheetContext.sheetId,
                                                         this.format().backgroundColorTheme())
        layout.backgroundResource   = this.format().corners().resourceId()

        return layout.linearLayout(sheetContext.context)
    }


    private fun rowsView(sheetContext : SheetContext) : View
    {
        val layout = this.rowsViewLayout(sheetContext.context)

        this.rows.list.forEach { layout.addView(it.view(sheetContext)) }

        return layout
    }


    private fun rowsViewLayout(context : Context) : LinearLayout
    {
        val layout = LinearLayoutBuilder()

        layout.orientation      = LinearLayout.VERTICAL
        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.paddingSpacing   = this.format().padding()

        return layout.linearLayout(context)
    }


    private fun dividerView(sheetContext : SheetContext) : LinearLayout
    {
        val divider = LinearLayoutBuilder()

        divider.width               = LinearLayout.LayoutParams.MATCH_PARENT
        divider.heightDp            = this.format().dividerThickness()

        divider.backgroundColor     = SheetManager.color(sheetContext.sheetId,
                                                         this.format().dividerColorTheme())

        divider.margin.leftDp       = this.format().dividerMargins()
        divider.margin.rightDp      = this.format().dividerMargins()

        return divider.linearLayout(sheetContext.context)
    }

}


/**
 * Group Format
 */
data class GroupFormat(override val id : UUID,
                       val backgroundColorTheme : Prim<ColorTheme>,
                       val margins : Comp<Spacing>,
                       val padding : Comp<Spacing>,
                       val corners : Prim<Corners>,
                       val showDivider : Prim<Boolean>,
                       val dividerColorTheme: Prim<ColorTheme>,
                       val dividerMargins : Prim<DividerMargin>,
                       val dividerThickness : Prim<DividerThickness>) : Model, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(backgroundColorTheme : ColorTheme,
                margins : Spacing,
                padding : Spacing,
                corners : Corners,
                showDivider : Boolean,
                dividerColorTheme : ColorTheme,
                dividerMargins : DividerMargin,
                dividerThickness: DividerThickness)
        : this(UUID.randomUUID(),
               Prim(backgroundColorTheme),
               Comp(margins),
               Comp(padding),
               Prim(corners),
               Prim(showDivider),
               Prim(dividerColorTheme),
               Prim(dividerMargins),
               Prim(dividerThickness))


    companion object : Factory<GroupFormat>
    {

        override fun fromDocument(doc : SpecDoc) : ValueParser<GroupFormat> = when (doc)
        {
            is DocDict ->
            {
                effApply(::GroupFormat,
                         // Model Id
                         effValue(UUID.randomUUID()),
                         // Background Color
                         split(doc.maybeAt("background_color_theme"),
                               effValue(Prim(ColorTheme.transparent)),
                               { effApply(::Prim, ColorTheme.fromDocument(it))}),
                         // Margins
                         split(doc.maybeAt("margins"),
                               effValue(Comp(Spacing.default)),
                               { effApply(::Comp, Spacing.fromDocument(it))}),
                         // Padding
                         split(doc.maybeAt("padding"),
                               effValue(Comp(Spacing.default)),
                               { effApply(::Comp, Spacing.fromDocument(it))}),
                         // Corners
                         split(doc.maybeAt("corners"),
                               effValue<ValueError,Prim<Corners>>(Prim(Corners.None)),
                               { effApply(::Prim, Corners.fromDocument(it))}),
                         // Show Divider?
                         split(doc.maybeBoolean("show_divider"),
                               effValue(Prim(false)),
                               { effValue(Prim(it)) }),
                         // Divider Color Theme
                         split(doc.maybeAt("divider_color_them"),
                               effValue(Prim(ColorTheme.black)),
                               { effApply(::Prim, ColorTheme.fromDocument(it))}),
                         // Divider Margins
                         split(doc.maybeAt("divider_margins"),
                               effValue(Prim(DividerMargin.default())),
                               { effApply(::Prim, DividerMargin.fromDocument(it))}),
                         // Divider Thickness
                         split(doc.maybeAt("divider_thickness"),
                               effValue(Prim(DividerThickness.default())),
                               { effApply(::Prim, DividerThickness.fromDocument(it))})
                         )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() : GroupFormat =
                GroupFormat(ColorTheme.transparent,
                            Spacing.default,
                            Spacing.default,
                            Corners.None,
                            false,
                            ColorTheme.black,
                            DividerMargin.default(),
                            DividerThickness.default())
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun backgroundColorTheme() : ColorTheme = this.backgroundColorTheme.value

    fun margins() : Spacing = this.margins.value

    fun padding() : Spacing = this.padding.value

    fun corners() : Corners = this.corners.value

    fun showDivider() : Boolean = this.showDivider.value

    fun dividerColorTheme() : ColorTheme = this.dividerColorTheme.value

    fun dividerMargins() : Float? = this.dividerMargins.value.value

    fun dividerThickness() : Int? = this.dividerThickness.value.value


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

}

