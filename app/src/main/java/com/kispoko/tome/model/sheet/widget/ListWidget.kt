
package com.kispoko.tome.model.sheet.widget


import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned.SPAN_INCLUSIVE_EXCLUSIVE
import android.text.style.AbsoluteSizeSpan
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.kispoko.tome.R
import com.kispoko.tome.R.string.engine
import com.kispoko.tome.activity.sheet.dialog.openVariableEditorDialog
import com.kispoko.tome.app.AppError
import com.kispoko.tome.app.AppStateError
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.db.DB_WidgetListFormatValue
import com.kispoko.tome.db.widgetListFormatTable
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.ProdType
import com.kispoko.tome.lib.orm.RowValue5
import com.kispoko.tome.lib.orm.schema.PrimValue
import com.kispoko.tome.lib.orm.schema.ProdValue
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.lib.ui.CustomTypefaceSpan
import com.kispoko.tome.lib.ui.Font
import com.kispoko.tome.lib.ui.TextViewBuilder
import com.kispoko.tome.model.game.engine.reference.TextReferenceLiteral
import com.kispoko.tome.model.game.engine.value.ValueReference
import com.kispoko.tome.model.game.engine.value.ValueSetId
import com.kispoko.tome.model.sheet.style.TextFormat
import com.kispoko.tome.rts.entity.EntityId
import com.kispoko.tome.rts.entity.colorOrBlack
import com.kispoko.tome.rts.entity.game.GameManager
import com.kispoko.tome.rts.entity.sheet.*
import com.kispoko.tome.rts.entity.value
import com.kispoko.tome.util.Util
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.UnexpectedValue
import lulo.value.ValueError
import lulo.value.ValueParser
import maybe.Just
import java.io.Serializable
import java.util.*



/**
 * List Widget Format
 */
data class ListWidgetFormat(override val id : UUID,
                            val widgetFormat : WidgetFormat,
                            val viewType : ListViewType,
                            val itemFormat : TextFormat,
                            val descriptionFormat : TextFormat,
                            val annotationFormat : TextFormat)
                             : ToDocument, ProdType, Serializable
{


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(widgetFormat : WidgetFormat,
                viewType : ListViewType,
                itemFormat : TextFormat,
                descriptionFormat : TextFormat,
                annotationFormat : TextFormat)
        : this(UUID.randomUUID(),
               widgetFormat,
               viewType,
               itemFormat,
               descriptionFormat,
               annotationFormat)


    companion object : Factory<ListWidgetFormat>
    {

        private fun defaultWidgetFormat()       = WidgetFormat.default()
        private fun defaultViewType()           = ListViewType.ParagraphCommas
        private fun defaultItemFormat()         = TextFormat.default()
        private fun defaultDescriptionFormat()  = TextFormat.default()
        private fun defaultAnnoationFormat()    = TextFormat.default()


        override fun fromDocument(doc : SchemaDoc) : ValueParser<ListWidgetFormat> = when (doc)
        {
            is DocDict ->
            {
                apply(::ListWidgetFormat,
                     // Widget Format
                     split(doc.maybeAt("widget_format"),
                           effValue(defaultWidgetFormat()),
                           { WidgetFormat.fromDocument(it) }),
                     // View Type
                     split(doc.maybeAt("view_type"),
                           effValue<ValueError,ListViewType>(defaultViewType()),
                           { ListViewType.fromDocument(it) }),
                     // Item Format
                     split(doc.maybeAt("item_format"),
                           effValue(defaultItemFormat()),
                           { TextFormat.fromDocument(it) }),
                     // Description Format
                     split(doc.maybeAt("description_format"),
                           effValue(defaultDescriptionFormat()),
                           { TextFormat.fromDocument(it) }),
                     // Annotation Format
                     split(doc.maybeAt("annotation_format"),
                           effValue(defaultAnnoationFormat()),
                           { TextFormat.fromDocument(it) })
                     )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = ListWidgetFormat(defaultWidgetFormat(),
                                         defaultViewType(),
                                         defaultItemFormat(),
                                         defaultDescriptionFormat(),
                                         defaultAnnoationFormat())
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "widget_format" to this.widgetFormat().toDocument(),
        "item_format" to this.itemFormat().toDocument(),
        "description_format" to this.descriptionFormat().toDocument(),
        "annotation_format" to this.annotationFormat().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun widgetFormat() : WidgetFormat = this.widgetFormat


    fun itemFormat() : TextFormat = this.itemFormat


    fun descriptionFormat() : TextFormat = this.descriptionFormat


    fun annotationFormat() : TextFormat = this.annotationFormat


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_WidgetListFormatValue =
        RowValue5(widgetListFormatTable,
                  ProdValue(this.widgetFormat),
                  PrimValue(this.viewType),
                  ProdValue(this.itemFormat),
                  ProdValue(this.descriptionFormat),
                  ProdValue(this.annotationFormat))

}


/**
 * List View Type
 */
sealed class ListViewType : ToDocument, SQLSerializable, Serializable
{

    object ParagraphCommas : ListViewType()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({ "paragraph_commas" })

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("paragraph_commas")

    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun fromDocument(doc : SchemaDoc) : ValueParser<ListViewType> = when (doc)
        {
            is DocText -> when (doc.text)
            {
                "paragraph_commas" -> effValue<ValueError,ListViewType>(ListViewType.ParagraphCommas)
                else               -> effError<ValueError,ListViewType>(
                                            UnexpectedValue("ListViewType", doc.text, doc.path))
            }
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }

}



/**
 * List Widget Description
 */
data class ListWidgetDescription(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ListWidgetDescription>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<ListWidgetDescription> = when (doc)
        {
            is DocText -> effValue(ListWidgetDescription(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocText(this.value)


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({ this.value })

}



class ListWidgetViewBuilder(val listWidget : ListWidget,
                            val entityId : EntityId,
                            val context : Context)
{

    fun view() : View
    {
        val layout = WidgetView.layout(listWidget.widgetFormat(), entityId, context)

        val contentLayout = layout.findViewById(R.id.widget_content_layout) as LinearLayout

        contentLayout.addView(this.inlineView())

        val layoutId = Util.generateViewId()
        contentLayout.id = layoutId
        listWidget.layoutViewId = layoutId

        return layout
    }


    fun inlineView() : TextView
    {
        val paragraph           = TextViewBuilder()
        val format              = listWidget.format().descriptionFormat()

        paragraph.width         = LinearLayout.LayoutParams.MATCH_PARENT
        paragraph.height        = LinearLayout.LayoutParams.WRAP_CONTENT

        val description = listWidget.description()
        val valueSetId = listWidget.variable(entityId).apply {
                            note<AppError,ValueSetId>(it.valueSetId().toNullable(),
                                                      AppStateError(VariableDoesNotHaveValueSet(it.variableId())))
                         }
        when (description) {
            is Just -> {
                when (valueSetId) {
                    is Val -> {
                        val values = listWidget.value(entityId) ap { valueIds ->
                                            valueIds.mapM { valueId ->
                                                val valueRef = ValueReference(TextReferenceLiteral(valueSetId.value.value),
                                                                              TextReferenceLiteral(valueId))
                                                value(valueRef, entityId)
                                            }
                                     }
                        when (values) {
                            is Val -> {
                                val valueStrings = values.value.map { it.valueString() }
                                paragraph.textSpan = this.spannableString(description.value.value, valueStrings)
                            }
                            is Err -> ApplicationLog.error(values.error)
                        }
                    }
                }
            }
        }

        paragraph.onClick       = View.OnClickListener {

            val textListVariable =  listWidget.variable(entityId)
            when (textListVariable) {
                is Val -> {
                    openVariableEditorDialog(textListVariable.value,
                                             null,
                                             UpdateTargetListWidget(listWidget.id),
                                             entityId,
                                             context)
                }
            }

        }

        return paragraph.textView(context)
    }


    private fun spannableString(description : String, valueStrings : List<String>) : SpannableStringBuilder
    {
        val builder = SpannableStringBuilder()
        var currentIndex = 0

        val parts = description.split("$$$")
        val part1 : String = parts[0]

        // > Part 1
        builder.append(part1)

        this.formatSpans(listWidget.format().descriptionFormat()).forEach {
            builder.setSpan(it, 0, part1.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        }

        currentIndex += part1.length

        val items = valueStrings.take(valueStrings.size - 1)
        val lastItem = valueStrings.elementAt(valueStrings.size - 1)

        // > Items
        items.forEach { item ->
            builder.append(item)

            this.formatSpans(listWidget.format().itemFormat()).forEach {
                builder.setSpan(it, currentIndex, currentIndex + item.length, SPAN_INCLUSIVE_EXCLUSIVE)
            }

            currentIndex += item.length

            if (items.size > 1) {
                builder.append(", ")

                this.formatSpans(listWidget.format().descriptionFormat()).forEach {
                    builder.setSpan(it, currentIndex, currentIndex + 2, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
                }

                currentIndex += 2
            }
        }

        if (items.size == 1)
        {
            builder.append(" and ")

            this.formatSpans(listWidget.format().descriptionFormat()).forEach {
                builder.setSpan(it, currentIndex, currentIndex + 5, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
            }

            currentIndex += 5
        }
        else if (items.size > 1)
        {
            builder.append("and ")

            this.formatSpans(listWidget.format().descriptionFormat()).forEach {
                builder.setSpan(it, currentIndex, currentIndex + 4, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
            }

            currentIndex += 4

        }

        builder.append(lastItem)

        this.formatSpans(listWidget.format().itemFormat()).forEach {
            builder.setSpan(it, currentIndex, currentIndex + lastItem.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        }

        return builder
    }


    private fun formatSpans(textFormat : TextFormat) : List<Any>
    {
        val sizePx = Util.spToPx(textFormat.sizeSp(), context)
        val sizeSpan = AbsoluteSizeSpan(sizePx)

        val typeface = Font.typeface(textFormat.font(), textFormat.fontStyle(), context)

        val typefaceSpan = CustomTypefaceSpan(typeface)

        var color = colorOrBlack(textFormat.colorTheme(), entityId)
        val colorSpan = ForegroundColorSpan(color)

        var bgColor = colorOrBlack(textFormat.elementFormat().backgroundColorTheme(), entityId)
        val bgColorSpan = BackgroundColorSpan(bgColor)

        return listOf(sizeSpan, typefaceSpan, colorSpan, bgColorSpan)
    }



}

