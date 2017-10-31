
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
import com.kispoko.tome.model.sheet.style.Alignment
import com.kispoko.tome.model.sheet.style.Corners
import com.kispoko.tome.model.sheet.style.Spacing
import com.kispoko.tome.model.sheet.style.VerticalAlignment
import com.kispoko.tome.model.sheet.widget.Widget
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
 * Group Row
 */
data class GroupRow(override val id : UUID,
                    val format : Comp<GroupRowFormat>,
                    val index : Prim<GroupRowIndex>,
                    val widgets : Coll<Widget>)
                      : ToDocument, Model, SheetComponent, Comparable<GroupRow>, Serializable
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
        fun fromDocument(doc : SchemaDoc, index : Int) : ValueParser<GroupRow> = when (doc)
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
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "format" to this.format().toDocument(),
        "widgets" to DocList(this.widgets().map { it.toDocument() })
    ))


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

    override fun onSheetComponentActive(sheetUIContext : SheetUIContext)
    {
        this.widgets.list.forEach { it.onSheetComponentActive(sheetUIContext) }
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

//        val layout              = LinearLayoutBuilder()
//
//        layout.orientation      = LinearLayout.VERTICAL
//        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
//        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT
//
//        layout.marginSpacing    = this.format().margins()
//
//        layout.backgroundColor  = SheetManager.color(sheetUIContext.sheetId,
//                                                     this.format().backgroundColortheme())
//
//        layout.gravity          = this.format().alignment().gravityConstant() or
//                                    this.format().verticalAlignment().gravityConstant()
//
//        layout.corners          = this.format().corners()
//
//        return layout.linearLayout(sheetUIContext.context)

        val layout = GroupRowTouchView(sheetUIContext.context)


        layout.orientation = LinearLayout.VERTICAL

        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                                     LinearLayout.LayoutParams.WRAP_CONTENT)

        layout.gravity = this.format().alignment().gravityConstant() or
                                    this.format().verticalAlignment().gravityConstant()

        val margins = this.format().margins()
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

        val corners = this.format().corners()
        val topLeft  = Util.dpToPixel(corners.topLeftCornerRadiusDp()).toFloat()
        val topRight : Float   = Util.dpToPixel(corners.topRightCornerRadiusDp()).toFloat()
        val bottomRight : Float = Util.dpToPixel(corners.bottomRightCornerRadiusDp()).toFloat()
        val bottomLeft :Float = Util.dpToPixel(corners.bottomLeftCornerRadiusDp()).toFloat()

        val radii = floatArrayOf(topLeft, topLeft, topRight, topRight,
                         bottomRight, bottomRight, bottomLeft, bottomLeft)

        bgDrawable.setCornerRadii(radii)

        val bgColor = SheetManager.color(sheetUIContext.sheetId,
                                         this.format().backgroundColorTheme())

        bgDrawable.colorFilter = PorterDuffColorFilter(bgColor, PorterDuff.Mode.SRC_IN)

        layout.background = bgDrawable

        return layout
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


class GroupRowTouchView(context : Context) : LinearLayout(context)
{

    override fun onTouchEvent(ev: MotionEvent?) : Boolean
    {
        if (ev != null)
        {
            Log.d("***GROUPROW", ev.action.toString())
            when (ev.action)
            {
                MotionEvent.ACTION_DOWN ->
                {
                    SheetActivityGlobal.cancelLongPressRunnable()
                    //return true
                }
                MotionEvent.ACTION_UP ->
                {
                    Log.d("***GROUPROW", "action up")
                    SheetActivityGlobal.cancelLongPressRunnable()
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


/**
 * Group Row Format
 */
data class GroupRowFormat(override val id : UUID,
                          val alignment : Prim<Alignment>,
                          val verticalAlignment : Prim<VerticalAlignment>,
                          val backgroundColorTheme : Prim<ColorTheme>,
                          val margins : Comp<Spacing>,
                          val padding : Comp<Spacing>,
                          val corners : Comp<Corners>,
                          val showDivider : Prim<ShowGroupRowDivider>,
                          val dividerColorTheme : Prim<ColorTheme>)
                           : ToDocument, Model, Serializable
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
        this.corners.name               = "corners"
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
                corners : Corners,
                showDivider : ShowGroupRowDivider,
                dividerColorTheme : ColorTheme)
        : this(UUID.randomUUID(),
               Prim(alignment),
               Prim(verticalAlignment),
               Prim(backgroundColorTheme),
               Comp(margins),
               Comp(padding),
               Comp(corners),
               Prim(showDivider),
               Prim(dividerColorTheme))


    companion object : Factory<GroupRowFormat>
    {

        private val defaultAlignment            = Alignment.Center
        private val defaultVerticalAlignment    = VerticalAlignment.Middle
        private val defaultBackgroundColorTheme = ColorTheme.transparent
        private val defaultMargins              = Spacing.default()
        private val defaultPadding              = Spacing.default()
        private val defaultCorners              = Corners.default()
        private val defaultShowDivider          = ShowGroupRowDivider(false)
        private val defaultDividerColorTheme    = ColorTheme.black


        override fun fromDocument(doc: SchemaDoc): ValueParser<GroupRowFormat> = when (doc)
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
                        // Corners
                        split(doc.maybeAt("corners"),
                                effValue(defaultCorners),
                                { Corners.fromDocument(it) }),
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
                               defaultCorners,
                               defaultShowDivider,
                               defaultDividerColorTheme)

    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "alignment" to this.alignment().toDocument(),
        "vertical_alignment" to this.alignment().toDocument(),
        "background_color_theme" to this.backgroundColorTheme().toDocument(),
        "margins" to this.margins().toDocument(),
        "padding" to this.padding().toDocument(),
        "show_divider" to this.showDivider.value.toDocument(),
        "divider_color_theme" to this.dividerColorTheme().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun alignment() : Alignment = this.alignment.value

    fun verticalAlignment() : VerticalAlignment = this.verticalAlignment.value

    fun backgroundColorTheme() : ColorTheme = this.backgroundColorTheme.value

    fun margins() : Spacing = this.margins.value

    fun padding() : Spacing = this.padding.value

    fun corners() : Corners = this.corners.value

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
        override fun fromDocument(doc: SchemaDoc): ValueParser<GroupRowIndex> = when (doc)
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
data class ShowGroupRowDivider(val value : Boolean) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ShowGroupRowDivider>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<ShowGroupRowDivider> = when (doc)
        {
            is DocBoolean -> effValue(ShowGroupRowDivider(doc.boolean))
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

