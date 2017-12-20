
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
import com.kispoko.tome.lib.orm.schema.PrimValue
import com.kispoko.tome.lib.orm.schema.ProdValue
import com.kispoko.tome.lib.orm.sql.SQLInt
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.lib.orm.sql.asSQLValue
import com.kispoko.tome.lib.ui.LinearLayoutBuilder
import com.kispoko.tome.model.sheet.style.*
import com.kispoko.tome.model.sheet.widget.Widget
import com.kispoko.tome.rts.sheet.SheetComponent
import com.kispoko.tome.rts.sheet.SheetUIContext
import com.kispoko.tome.rts.sheet.SheetManager
import com.kispoko.tome.util.Util
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import java.io.Serializable
import java.util.*



/**
 * Group Row
 */
data class GroupRow(override val id : UUID,
                    private val format : GroupRowFormat,
                    private var index : GroupRowIndex,
                    private val widgets : MutableList<Widget>)
                      : ToDocument, ProdType, SheetComponent, Comparable<GroupRow>, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(format : GroupRowFormat,
                index : GroupRowIndex,
                widgets : List<Widget>)
        : this(UUID.randomUUID(),
               format,
               index,
               widgets.toMutableList())


    companion object
    {
        fun fromDocument(doc : SchemaDoc, index : Int) : ValueParser<GroupRow> = when (doc)
        {
            is DocDict ->
            {
                apply(::GroupRow,
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
            }

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

    fun format() : GroupRowFormat = this.format

    fun indexInt() : Int = this.index.value

    fun widgets() : List<Widget> = this.widgets


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_GroupRowValue =
        RowValue3(groupRowTable, ProdValue(this.format),
                                 PrimValue(this.index),
                                 CollValue(this.widgets))


    // -----------------------------------------------------------------------------------------
    // COMPARABLE
    // -----------------------------------------------------------------------------------------

    override fun compareTo(other : GroupRow) = compareValuesBy(this, other, { it.indexInt() })


    // -----------------------------------------------------------------------------------------
    // SHEET COMPONENT
    // -----------------------------------------------------------------------------------------

    override fun onSheetComponentActive(sheetUIContext : SheetUIContext)
    {
        this.widgets.forEach { it.onSheetComponentActive(sheetUIContext) }
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
//        if (this.format().showDividerBool())
//            layout.addView(dividerView(sheetUIContext))

        return layout
    }



    private fun viewLayout(sheetUIContext : SheetUIContext) : LinearLayout
    {
        val layout = GroupRowTouchView(sheetUIContext.context)
        val elementFormat = this.format().elementFormat()


        layout.orientation = LinearLayout.VERTICAL

        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                                     LinearLayout.LayoutParams.WRAP_CONTENT)

        layout.gravity = elementFormat.alignment().gravityConstant() or
                                    elementFormat.verticalAlignment().gravityConstant()

        val margins = elementFormat.margins()
        layoutParams.leftMargin = margins.leftPx()
        layoutParams.rightMargin = margins.rightPx()
        layoutParams.topMargin = margins.topPx()
        layoutParams.bottomMargin = margins.bottomPx()

        layout.layoutParams = layoutParams

        // Background
        val bgDrawable = PaintDrawable()

        val corners = elementFormat.corners()
        val topLeft  = Util.dpToPixel(corners.topLeftCornerRadiusDp()).toFloat()
        val topRight : Float   = Util.dpToPixel(corners.topRightCornerRadiusDp()).toFloat()
        val bottomRight : Float = Util.dpToPixel(corners.bottomRightCornerRadiusDp()).toFloat()
        val bottomLeft :Float = Util.dpToPixel(corners.bottomLeftCornerRadiusDp()).toFloat()

        val radii = floatArrayOf(topLeft, topLeft, topRight, topRight,
                         bottomRight, bottomRight, bottomLeft, bottomLeft)

        bgDrawable.setCornerRadii(radii)

        val bgColor = SheetManager.color(sheetUIContext.sheetId,
                                         elementFormat.backgroundColorTheme())

        bgDrawable.colorFilter = PorterDuffColorFilter(bgColor, PorterDuff.Mode.SRC_IN)

        layout.background = bgDrawable

        return layout
    }


    private fun widgetsView(sheetUIContext: SheetUIContext) : LinearLayout
    {
        val layout = this.widgetsViewLayout(sheetUIContext.context)

        this.widgets().forEach { layout.addView(it.view(sheetUIContext)) }

        return layout
    }


    private fun widgetsViewLayout(context : Context) : LinearLayout
    {
        val layout = LinearLayoutBuilder()

        layout.orientation      = LinearLayout.HORIZONTAL
        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.paddingSpacing   = this.format().elementFormat().padding()

        return layout.linearLayout(context)
    }

//
//    private fun dividerView(sheetUIContext: SheetUIContext) : LinearLayout
//    {
//        val divider = LinearLayoutBuilder()
//
//        divider.width           = LinearLayout.LayoutParams.MATCH_PARENT
//        divider.heightDp        = 1
//
//        divider.backgroundColor = SheetManager.color(sheetUIContext.sheetId,
//                                                     this.format().elementFormat().dividerColorTheme())
//
//        return divider.linearLayout(sheetUIContext.context)
//    }

}


class GroupRowTouchView(context : Context) : LinearLayout(context)
{

    override fun onTouchEvent(ev: MotionEvent?) : Boolean
    {
        if (ev != null)
        {
        //    Log.d("***GROUPROW", ev.action.toString())
            when (ev.action)
            {
                MotionEvent.ACTION_DOWN ->
                {
                    SheetActivityGlobal.cancelLongPressRunnable()
                    //return true
                }
                MotionEvent.ACTION_UP ->
                {
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
                          private val elementFormat : ElementFormat,
                          private val divider : Divider)
                           : ToDocument, ProdType, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(elementFormat : ElementFormat,
                divider : Divider)
        : this(UUID.randomUUID(),
               elementFormat,
               divider)


    companion object : Factory<GroupRowFormat>
    {

        private fun defaultElementFormat() = ElementFormat.default()
        private fun defaultDivider()       = Divider.default()


        override fun fromDocument(doc : SchemaDoc) : ValueParser<GroupRowFormat> = when (doc)
        {
            is DocDict ->
            {
                apply(::GroupRowFormat,
                      // Element Format
                      split(doc.maybeAt("element_format"),
                            effValue(defaultElementFormat()),
                            { ElementFormat.fromDocument(it) }),
                      // Divider
                      split(doc.maybeAt("divider"),
                            effValue(defaultDivider()),
                            { Divider.fromDocument(it) })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = GroupRowFormat(defaultElementFormat(),
                                       defaultDivider())

    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "element_format" to this.elementFormat.toDocument(),
        "divider" to this.divider.toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun elementFormat() : ElementFormat = this.elementFormat

    fun divider() : Divider = this.divider


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_GroupRowFormatValue =
        RowValue2(groupRowFormatTable, ProdValue(this.elementFormat),
                                       ProdValue(this.divider))

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

    override fun asSQLValue() = this.value.asSQLValue()

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

