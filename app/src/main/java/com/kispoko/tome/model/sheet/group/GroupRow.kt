
package com.kispoko.tome.model.sheet.group


import android.content.Context
import android.content.Entity
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.PaintDrawable
import android.view.Gravity
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
import com.kispoko.tome.lib.orm.sql.SQLInt
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.lib.orm.sql.asSQLValue
import com.kispoko.tome.lib.ui.LinearLayoutBuilder
import com.kispoko.tome.model.sheet.style.*
import com.kispoko.tome.model.sheet.widget.Widget
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
        RowValue2(groupRowTable,
                  ProdValue(this.format),
                  PrimValue(this.index))


    // -----------------------------------------------------------------------------------------
    // COMPARABLE
    // -----------------------------------------------------------------------------------------

    override fun compareTo(other : GroupRow) = compareValuesBy(this, other, { it.indexInt() })


    // -----------------------------------------------------------------------------------------
    // SHEET COMPONENT
    // -----------------------------------------------------------------------------------------

    override fun onSheetComponentActive(entityId : EntityId, context : Context)
    {
        this.widgets.forEach { it.onSheetComponentActive(entityId, context) }
    }


    // -----------------------------------------------------------------------------------------
    // VIEW
    // -----------------------------------------------------------------------------------------

    fun view(entityId : EntityId, context : Context) : View
    {
        val layout = this.viewLayout(entityId, context)

        // Top Border
        val topBorder = this.format().border().apply { it.top() }
        when (topBorder) {
            is Just -> layout.addView(this.dividerView(topBorder.value, entityId, context))
        }

        // > Widgets
        layout.addView(widgetsView(entityId, context))

        val bottomBorder = this.format().border().apply { it.bottom() }
        when (bottomBorder) {
            is Just -> layout.addView(dividerView(bottomBorder.value, entityId, context))
        }

        return layout
    }



    private fun viewLayout(entityId : EntityId, context : Context) : LinearLayout
    {
        val layout = GroupRowTouchView(context)
        val elementFormat = this.format().elementFormat()


        layout.orientation = LinearLayout.VERTICAL

        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                                     LinearLayout.LayoutParams.WRAP_CONTENT)

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

        val bgColor = colorOrBlack(elementFormat.backgroundColorTheme(), entityId)

        bgDrawable.colorFilter = PorterDuffColorFilter(bgColor, PorterDuff.Mode.SRC_IN)

        layout.background = bgDrawable

        return layout
    }


    private fun widgetsView(entityId : EntityId, context : Context) : LinearLayout
    {
        val layout = this.widgetsViewLayout(context)

        if (this.format().hasColumns().value)
        {
            val colIndiceSet = this.widgets().map { it.widgetFormat().column() }
            val largestColIndex = colIndiceSet.max()

            if (largestColIndex == 1) {
                this.widgets().forEach { layout.addView(it.view(entityId, context)) }
            }
            else {
                val colToLayout : MutableMap<Int,LinearLayout> = mutableMapOf()
                colIndiceSet.forEach {
                    colToLayout.put(it, this.widgetsColumnLayout(context))
                }

                this.widgets().forEach {
                    val layout = colToLayout[it.widgetFormat().column()]
                    layout?.addView(it.view(entityId, context))
                }

                colToLayout.keys.sorted().forEach {
                    layout.addView(colToLayout[it])
                }
            }
        }
        else
        {
            this.widgets().forEach { layout.addView(it.view(entityId, context)) }
        }

        return layout
    }


    private fun widgetsColumnLayout(context : Context) : LinearLayout
    {
        val layout = LinearLayoutBuilder()

        layout.orientation      = LinearLayout.HORIZONTAL

        layout.weight           = 1f
        layout.width            = 0
        layout.height           = LinearLayout.LayoutParams.MATCH_PARENT

        layout.gravity          = Gravity.START

        return layout.linearLayout(context)
    }


    private fun widgetsViewLayout(context : Context) : LinearLayout
    {
        val layout = LinearLayoutBuilder()

        val elementFormat = this.format().elementFormat()

        layout.orientation      = LinearLayout.HORIZONTAL
        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.paddingSpacing   = this.format().elementFormat().padding()


        layout.gravity = elementFormat.alignment().gravityConstant() or
                            elementFormat.verticalAlignment().gravityConstant()

        return layout.linearLayout(context)
    }


    private fun dividerView(format : BorderEdge,
                            entityId : EntityId,
                            context : Context) : LinearLayout
    {
        val divider = LinearLayoutBuilder()

        divider.width               = LinearLayout.LayoutParams.MATCH_PARENT
        divider.heightDp            = format.thickness().value

        divider.backgroundColor     = colorOrBlack(format.colorTheme(), entityId)

        return divider.linearLayout(context)
    }



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
                          val hasColumns : GroupRowHasColumns,
                          val border : Maybe<Border>)
                           : ToDocument, ProdType, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(elementFormat : ElementFormat,
                hasColumns : GroupRowHasColumns,
                border : Maybe<Border>)
        : this(UUID.randomUUID(),
               elementFormat,
               hasColumns,
               border)


    companion object : Factory<GroupRowFormat>
    {

        private fun defaultElementFormat()  = ElementFormat.default()
        private fun defaultHasColumns()     = GroupRowHasColumns(false)
        private fun defaultDivider()        = Divider.default()


        override fun fromDocument(doc : SchemaDoc) : ValueParser<GroupRowFormat> = when (doc)
        {
            is DocDict ->
            {
                apply(::GroupRowFormat,
                      // Element Format
                      split(doc.maybeAt("element_format"),
                            effValue(defaultElementFormat()),
                            { ElementFormat.fromDocument(it) }),
                      // Has Columns
                      split(doc.maybeAt("has_columns"),
                            effValue(GroupRowHasColumns.default()),
                            { GroupRowHasColumns.fromDocument(it) }),
                      // Border
                      split(doc.maybeAt("border"),
                            effValue<ValueError,Maybe<Border>>(Nothing()),
                            { effApply(::Just, Border.fromDocument(it)) })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = GroupRowFormat(defaultElementFormat(),
                                       defaultHasColumns(),
                                       Nothing())

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


    fun hasColumns() : GroupRowHasColumns = this.hasColumns


    fun border() : Maybe<Border> = this.border


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_GroupRowFormatValue =
        RowValue3(groupRowFormatTable,
                  ProdValue(this.elementFormat),
                  PrimValue(this.hasColumns),
                  MaybeProdValue(this.border))

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


/**
 * Row Column
 */
data class RowColumn(val value : Int) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<RowColumn>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<RowColumn> = when (doc)
        {
            is DocNumber -> effValue(RowColumn(doc.number.toInt()))
            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
        }

        fun default() = RowColumn(1)
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocNumber(this.value.toDouble())


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() = this.value.asSQLValue()

}



/**
 * Group Row Has Columns
 */
data class GroupRowHasColumns(val value : Boolean) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<GroupRowHasColumns>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<GroupRowHasColumns> = when (doc)
        {
            is DocBoolean -> effValue(GroupRowHasColumns(doc.boolean))
            else          -> effError(UnexpectedType(DocType.BOOLEAN, docType(doc), doc.path))
        }

        fun default() = GroupRowHasColumns(false)
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocBoolean(this.value)


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() = this.value.asSQLValue()

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

