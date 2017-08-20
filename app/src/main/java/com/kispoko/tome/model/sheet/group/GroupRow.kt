
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
import com.kispoko.tome.model.sheet.style.Alignment
import com.kispoko.tome.model.sheet.style.Spacing
import com.kispoko.tome.model.sheet.style.VerticalAlignment
import com.kispoko.tome.model.sheet.widget.Widget
import com.kispoko.tome.model.theme.ColorTheme
import com.kispoko.tome.rts.sheet.SheetComponent
import com.kispoko.tome.rts.sheet.SheetContext
import com.kispoko.tome.rts.sheet.SheetUIContext
import com.kispoko.tome.rts.sheet.SheetManager
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueError
import lulo.value.ValueParser
import java.io.Serializable
import java.util.*



/**
 * Group Row
 */
data class GroupRow(override val id : UUID,
                    val format : Comp<GroupRowFormat>,
                    val index : Prim<GroupRowIndex>,
                    val widgets : Coll<Widget>)
                      : Model, SheetComponent, Comparable<GroupRow>, Serializable
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.format.name    = "format"
        this.index.name     = "index"
        this.widgets.name   = "widgets"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(format : GroupRowFormat,
                index : GroupRowIndex,
                widgets : MutableList<Widget>)
        : this(UUID.randomUUID(),
               Comp(format),
               Prim(index),
               Coll(widgets))


    companion object
    {
        fun fromDocument(doc : SpecDoc, index : Int) : ValueParser<GroupRow> = when (doc)
        {
            is DocDict -> effApply(::GroupRow,
                                   // Format
                                   split(doc.maybeAt("format"),
                                         effValue(GroupRowFormat.default()),
                                         { GroupRowFormat.fromDocument(it) }),
                                   // Index
                                   effValue(GroupRowIndex(index)),
                                   // Widgets
                                   doc.list("widgets") ap { docList ->
                                       docList.mapMut { Widget.fromDocument(it) }
                                   })
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun format() : GroupRowFormat = this.format.value

    fun indexInt() : Int = this.index.value.value

    fun widgets() : List<Widget> = this.widgets.list


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name : String = "group_row"

    override val modelObject = this


    // -----------------------------------------------------------------------------------------
    // COMPARABLE
    // -----------------------------------------------------------------------------------------

    override fun compareTo(other : GroupRow) = compareValuesBy(this, other, { it.indexInt() })


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

    fun view(sheetUIContext: SheetUIContext) : View
    {
        val layout = this.viewLayout(sheetUIContext)

        // > Widgets
        layout.addView(widgetsView(sheetUIContext))

        // > Divider
        if (this.format().showDividerBool())
            layout.addView(dividerView(sheetUIContext))

        return layout
    }



    private fun viewLayout(sheetUIContext : SheetUIContext) : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.orientation      = LinearLayout.VERTICAL
        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.marginSpacing    = this.format().margins()

        layout.backgroundColor  = SheetManager.color(sheetUIContext.sheetId,
                                                     this.format().backgroundColortheme())

        layout.gravity          = this.format().alignment().gravityConstant() or
                                    this.format().verticalAlignment().gravityConstant()

        return layout.linearLayout(sheetUIContext.context)
    }


    private fun widgetsView(sheetUIContext: SheetUIContext) : LinearLayout
    {
        val layout = this.widgetsViewLayout(sheetUIContext.context)

//        var rowHasTopLabel = false
//
//        for (widget in this.widgets())
//
//            if (widget.widgetFormat().label() != null)
//                rowHasTopLabel = true
//            }
//        }

        this.widgets().forEach { layout.addView(it.view(sheetUIContext)) }

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


    private fun dividerView(sheetUIContext: SheetUIContext) : LinearLayout
    {
        val divider = LinearLayoutBuilder()

        divider.width           = LinearLayout.LayoutParams.MATCH_PARENT
        divider.heightDp        = 1

        divider.backgroundColor = SheetManager.color(sheetUIContext.sheetId,
                                                     this.format().dividerColorTheme())

        return divider.linearLayout(sheetUIContext.context)
    }

}


/**
 * Group Row Format
 */
data class GroupRowFormat(override val id : UUID,
                          val alignment : Prim<Alignment>,
                          val verticalAlignment : Prim<VerticalAlignment>,
                          val backgroundColorTheme : Prim<ColorTheme>,
                          val margins : Comp<Spacing>,
                          val padding : Comp<Spacing>,
                          val showDivider : Prim<ShowGroupRowDivider>,
                          val dividerColorTheme : Prim<ColorTheme>) : Model, Serializable
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.alignment.name             = "alignment"
        this.verticalAlignment.name     = "vertical_alignment"
        this.backgroundColorTheme.name  = "background_color_theme"
        this.margins.name               = "margins"
        this.padding.name               = "padding"
        this.showDivider.name           = "show_divider"
        this.dividerColorTheme.name     = "divider_color_theme"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(alignment : Alignment,
                verticalAlignment : VerticalAlignment,
                backgroundColorTheme : ColorTheme,
                margins : Spacing,
                padding : Spacing,
                showDivider : ShowGroupRowDivider,
                dividerColorTheme : ColorTheme)
        : this(UUID.randomUUID(),
               Prim(alignment),
               Prim(verticalAlignment),
               Prim(backgroundColorTheme),
               Comp(margins),
               Comp(padding),
               Prim(showDivider),
               Prim(dividerColorTheme))


    companion object : Factory<GroupRowFormat>
    {

        private val defaultAlignment            = Alignment.Center
        private val defaultVerticalAlignment    = VerticalAlignment.Middle
        private val defaultBackgroundColorTheme = ColorTheme.transparent
        private val defaultMargins              = Spacing.default()
        private val defaultPadding              = Spacing.default()
        private val defaultShowDivider          = ShowGroupRowDivider(false)
        private val defaultDividerColorTheme    = ColorTheme.black


        override fun fromDocument(doc : SpecDoc) : ValueParser<GroupRowFormat> = when (doc)
        {
            is DocDict ->
            {
                effApply(::GroupRowFormat,
                         // Alignment
                         split(doc.maybeAt("alignment"),
                               effValue<ValueError,Alignment>(defaultAlignment),
                               { Alignment.fromDocument(it) }),
                         // Vertical Alignment
                         split(doc.maybeAt("vertical_alignment"),
                               effValue<ValueError,VerticalAlignment>(defaultVerticalAlignment),
                               { VerticalAlignment.fromDocument(it) }),
                         // Background Color
                         split(doc.maybeAt("background_color_theme"),
                               effValue(defaultBackgroundColorTheme),
                               { ColorTheme.fromDocument(it) }),
                         // Margins
                         split(doc.maybeAt("margins"),
                               effValue(defaultMargins),
                               { Spacing.fromDocument(it) }),
                         // Padding
                         split(doc.maybeAt("padding"),
                               effValue(defaultPadding),
                               { Spacing.fromDocument(it) }),
                         // Show Divider?
                         split(doc.maybeAt("show_divider"),
                               effValue(defaultShowDivider),
                               { ShowGroupRowDivider.fromDocument(it) }),
                         // Divider Color
                         split(doc.maybeAt("divider_color_theme"),
                               effValue(defaultDividerColorTheme),
                               { ColorTheme.fromDocument(it) })
                         )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() : GroupRowFormat =
                GroupRowFormat(defaultAlignment,
                               defaultVerticalAlignment,
                               defaultBackgroundColorTheme,
                               defaultMargins,
                               defaultPadding,
                               defaultShowDivider,
                               defaultDividerColorTheme)

    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun alignment() : Alignment = this.alignment.value

    fun verticalAlignment() : VerticalAlignment = this.verticalAlignment.value

    fun backgroundColortheme() : ColorTheme = this.backgroundColorTheme.value

    fun margins() : Spacing = this.margins.value

    fun padding() : Spacing = this.padding.value

    fun showDividerBool() : Boolean = this.showDivider.value.value

    fun dividerColorTheme() : ColorTheme = this.dividerColorTheme.value


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name : String = "group_row_format"

    override val modelObject = this

}


/**
 * Group Row Index
 */
data class GroupRowIndex(val value : Int) : SQLSerializable, Serializable
{

    companion object : Factory<GroupRowIndex>
    {
        override fun fromDocument(doc: SpecDoc) : ValueParser<GroupRowIndex> = when (doc)
        {
            is DocNumber -> effValue(GroupRowIndex(doc.number.toInt()))
            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
        }
    }

    override fun asSQLValue() : SQLValue = SQLInt({this.value})

}


/**
 * Show Divider
 */
data class ShowGroupRowDivider(val value : Boolean) : SQLSerializable, Serializable
{

    companion object : Factory<ShowGroupRowDivider>
    {
        override fun fromDocument(doc: SpecDoc) : ValueParser<ShowGroupRowDivider> = when (doc)
        {
            is DocBoolean -> effValue(ShowGroupRowDivider(doc.boolean))
            else          -> effError(UnexpectedType(DocType.BOOLEAN, docType(doc), doc.path))
        }
    }

    override fun asSQLValue() : SQLValue = SQLInt({ if (value) 1 else 0})

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

