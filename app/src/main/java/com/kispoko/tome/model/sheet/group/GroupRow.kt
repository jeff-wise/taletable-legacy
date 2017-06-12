
package com.kispoko.tome.model.sheet.group


import android.content.Context
import android.view.View
import android.widget.LinearLayout
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.*
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.lib.ui.LinearLayoutBuilder
import com.kispoko.tome.model.sheet.style.Alignment
import com.kispoko.tome.model.sheet.style.Spacing
import com.kispoko.tome.model.sheet.widget.Widget
import com.kispoko.tome.model.theme.ColorTheme
import com.kispoko.tome.rts.sheet.SheetComponent
import com.kispoko.tome.rts.sheet.SheetContext
import com.kispoko.tome.rts.sheet.SheetManager
import effect.*
import lulo.document.DocDict
import lulo.document.DocType
import lulo.document.SpecDoc
import lulo.document.docType
import lulo.value.UnexpectedType
import lulo.value.ValueError
import lulo.value.ValueParser
import java.util.*



/**
 * Group Row
 */
data class GroupRow(override val id : UUID,
                    val format : Comp<GroupRowFormat>,
                    val index : Prim<Int>,
                    val widgets : Coll<Widget>) : Model, SheetComponent, Comparable<GroupRow>
{

    companion object
    {
        fun fromDocument(doc : SpecDoc, index : Int) : ValueParser<GroupRow> = when (doc)
        {
            is DocDict -> effApply(::GroupRow,
                                   // Model Id
                                   effValue(UUID.randomUUID()),
                                   // Format
                                   split(doc.maybeAt("format"),
                                         effValue(Comp(GroupRowFormat.default())),
                                         { effApply(::Comp, GroupRowFormat.fromDocument(it))}),
                                   // Index
                                   effValue(Prim(index)),
                                   // Widgets
                                   doc.list("widgets") ap { docList ->
                                       effApply(::Coll,
                                                docList.map { Widget.fromDocument(it) })
                                   })
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun format() : GroupRowFormat = this.format.value

    fun index() : Int = this.index.value

    fun widgets() : List<Widget> = this.widgets.list


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    // -----------------------------------------------------------------------------------------
    // COMPARABLE
    // -----------------------------------------------------------------------------------------

    override fun compareTo(other : GroupRow) = compareValuesBy(this, other, { it.index.value })


    // -----------------------------------------------------------------------------------------
    // SHEET COMPONENT
    // -----------------------------------------------------------------------------------------

    override fun onSheetComponentActive(sheetContext : SheetContext)
    {
        this.widgets.list.forEach { it.onSheetComponentActive(sheetContext) }
    }


    // -----------------------------------------------------------------------------------------
    // VIEW
    // -----------------------------------------------------------------------------------------

    override fun view(sheetContext : SheetContext) : View
    {
        val layout = this.viewLayout(sheetContext.context)

        // > Widgets
        layout.addView(widgetsView(sheetContext))

        // > Divider
        if (this.format().showDivider())
            layout.addView(dividerView(sheetContext))

        return layout
    }



    private fun viewLayout(context : Context) : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.orientation      = LinearLayout.VERTICAL
        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.marginSpacing    = this.format().margins()

        return layout.linearLayout(context)
    }


    private fun widgetsView(sheetContext : SheetContext) : LinearLayout
    {
        val layout = this.widgetsViewLayout(sheetContext.context)

//        var rowHasTopLabel = false
//
//        for (widget in this.widgets())
//
//            if (widget.widgetFormat().label() != null)
//                rowHasTopLabel = true
//            }
//        }

        this.widgets().forEach { layout.addView(it.view(sheetContext)) }

        return layout
    }


    private fun widgetsViewLayout(context : Context) : LinearLayout
    {
        val layout = LinearLayoutBuilder()

        layout.orientation      = LinearLayout.HORIZONTAL
        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.paddingSpacing   = this.format().padding()

        return layout.linearLayout(context)
    }


    private fun dividerView(sheetContext : SheetContext) : LinearLayout
    {
        val divider = LinearLayoutBuilder()

        divider.width           = LinearLayout.LayoutParams.MATCH_PARENT
        divider.heightDp        = 1

        divider.backgroundColor = SheetManager.color(sheetContext.sheetId,
                                                     this.format().dividerColorTheme())

        return divider.linearLayout(sheetContext.context)
    }

}


/**
 * Group Row Format
 */
data class GroupRowFormat(override val id : UUID,
                          val alignment : Prim<Alignment>,
                          val backgroundColorTheme : Prim<ColorTheme>,
                          val margins : Comp<Spacing>,
                          val padding : Comp<Spacing>,
                          val showDivider : Prim<Boolean>,
                          val dividerColorTheme : Prim<ColorTheme>) : Model
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(alignment : Alignment,
                backgroundColorTheme : ColorTheme,
                margins : Spacing,
                padding : Spacing,
                showDivider : Boolean,
                dividerColorTheme : ColorTheme)
        : this(UUID.randomUUID(),
               Prim(alignment),
               Prim(backgroundColorTheme),
               Comp(margins),
               Comp(padding),
               Prim(false),
               Prim(dividerColorTheme))


    companion object : Factory<GroupRowFormat>
    {


        override fun fromDocument(doc : SpecDoc) : ValueParser<GroupRowFormat> = when (doc)
        {
            is DocDict ->
            {
                effApply(::GroupRowFormat,
                         // Model Id
                         effValue(UUID.randomUUID()),
                         // Alignment
                         split(doc.maybeAt("alignment"),
                               effValue<ValueError,Prim<Alignment>>(Prim(Alignment.Center())),
                               { effApply(::Prim, Alignment.fromDocument(it))}),
                         // Background Color
                         split(doc.maybeAt("background_color_theme"),
                               effValue(Prim(ColorTheme.transparent)),
                               { effApply(::Prim, ColorTheme.fromDocument(it))}),
                         // Margins
                         split(doc.maybeAt("margins"),
                               effValue(Comp(Spacing.default())),
                               { effApply(::Comp, Spacing.fromDocument(it))}),
                         // Padding
                         split(doc.maybeAt("padding"),
                               effValue(Comp(Spacing.default())),
                               { effApply(::Comp, Spacing.fromDocument(it))}),
                        // Show Divider?
                        split(doc.maybeBoolean("show_divider"),
                              effValue(Prim(false)),
                              { effValue(Prim(it)) }),
                         // Divider Color
                         split(doc.maybeAt("divider_color_theme"),
                               effValue(Prim(ColorTheme.black)),
                               { effApply(::Prim, ColorTheme.fromDocument(it))})
                         )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() : GroupRowFormat =
                GroupRowFormat(Alignment.Center(),
                               ColorTheme.transparent,
                               Spacing.default(),
                               Spacing.default(),
                               false,
                               ColorTheme.black)

    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun alignment() : Alignment = this.alignment.value

    fun backgroundColortheme() : ColorTheme = this.backgroundColorTheme.value

    fun margins() : Spacing = this.margins.value

    fun padding() : Spacing = this.padding.value

    fun showDivider() : Boolean = this.showDivider.value

    fun dividerColorTheme() : ColorTheme = this.dividerColorTheme.value


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

}


//    // > Initialize
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * Initialize the group row.
//     */
//    public void initialize(GroupParent groupParent, Context context)
//    {
//        this.groupParent = groupParent;
//
//        // Initialize each widget
//        for (WidgetUnion widgetUnion : this.widgets())
//        {
//            widgetUnion.widget().initialize(groupParent, context);
//        }
//    }
//
//

