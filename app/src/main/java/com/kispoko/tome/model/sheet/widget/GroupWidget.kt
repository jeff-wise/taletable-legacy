
package com.kispoko.tome.model.sheet.widget


import android.content.Context
import android.util.Log
import android.widget.LinearLayout
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.lib.ui.LinearLayoutBuilder
import com.kispoko.tome.rts.entity.EntityId
import effect.effError
import effect.effValue
import effect.split
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.UnexpectedValue
import lulo.value.ValueError
import lulo.value.ValueParser
import java.io.Serializable



/**
 * Group Widget Format
 */
data class GroupWidgetFormat(val widgetFormat : WidgetFormat,
                             val viewType : GroupWidgetViewType)
                              : ToDocument, Serializable
{

    companion object : Factory<GroupWidgetFormat>
    {

        private fun defaultWidgetFormat()       = WidgetFormat.default()
        private fun defaultViewType()           = GroupWidgetViewType.Normal


        override fun fromDocument(doc : SchemaDoc) : ValueParser<GroupWidgetFormat> = when (doc)
        {
            is DocDict ->
            {
                effect.apply(::GroupWidgetFormat,
                      // Widget Format
                      split(doc.maybeAt("widget_format"),
                            effValue(defaultWidgetFormat()),
                            { WidgetFormat.fromDocument(it) }),
                      // View Type
                      split(doc.maybeAt("view_type"),
                            effValue<ValueError,GroupWidgetViewType>(defaultViewType()),
                            { GroupWidgetViewType.fromDocument(it) })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = GroupWidgetFormat(defaultWidgetFormat(),
                                          defaultViewType())
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "widget_format" to this.widgetFormat().toDocument(),
        "view_type" to this.viewType.toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun widgetFormat() : WidgetFormat = this.widgetFormat


    fun viewType() : GroupWidgetViewType = this.viewType

}



/**
 * Group Widget View Type
 */
sealed class GroupWidgetViewType : ToDocument, SQLSerializable, Serializable
{

    object Normal : GroupWidgetViewType()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "normal" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("normal")

    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun fromDocument(doc : SchemaDoc) : ValueParser<GroupWidgetViewType> = when (doc)
        {
            is DocText -> when (doc.text)
            {
                "normal" -> effValue<ValueError,GroupWidgetViewType>(
                                    GroupWidgetViewType.Normal)
                else     -> effError<ValueError,GroupWidgetViewType>(
                                    UnexpectedValue("GroupWidgetViewType", doc.text, doc.path))
            }
            else       -> effError(lulo.value.UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }

}



class GroupWidgetUI(val groupWidget : WidgetGroup,
                    val entityId : EntityId,
                    val context : Context)
{

    // -----------------------------------------------------------------------------------------
    // VIEWS
    // -----------------------------------------------------------------------------------------

    fun view() : LinearLayout
    {
        val layout = this.viewLayout()


        Log.d("***GROUP WIDGET", "rendering view")
        groupWidget.groups(entityId).forEach {
            Log.d("***GROUP WIDGET", "group view")
            layout.addView(it.view(entityId, context))
        }

        return layout
    }


    private fun viewLayout() : LinearLayout
    {
        val layout              = LinearLayoutBuilder()

        layout.width            = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height           = LinearLayout.LayoutParams.WRAP_CONTENT

        layout.orientation      = LinearLayout.VERTICAL

        return layout.linearLayout(context)
    }

}
