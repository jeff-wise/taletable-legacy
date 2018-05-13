
package com.kispoko.tome.model.sheet.widget.table.cell


import android.content.Context
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.ui.ImageViewBuilder
import com.kispoko.tome.lib.ui.LinearLayoutBuilder
import com.kispoko.tome.model.sheet.style.TextFormat
import com.kispoko.tome.model.sheet.widget.table.TableWidgetCellView
import com.kispoko.tome.model.sheet.widget.table.TableWidgetImageCell
import com.kispoko.tome.model.sheet.widget.table.TableWidgetImageColumn
import com.kispoko.tome.model.sheet.widget.table.column.ImageColumnFormat
import com.kispoko.tome.rts.entity.EntityId
import com.kispoko.tome.rts.entity.colorOrBlack
import effect.apply
import effect.effError
import effect.effValue
import effect.split
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
 * Icon Cell Format
 */
data class ImageCellFormat(val textFormat : Maybe<TextFormat>)
                            : ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ImageCellFormat>
    {

        override fun fromDocument(doc : SchemaDoc) : ValueParser<ImageCellFormat> = when (doc)
        {
            is DocDict ->
            {
                effect.apply(::ImageCellFormat,
                      // Text Format
                      split(doc.maybeAt("text_format"),
                            effValue<ValueError,Maybe<TextFormat>>(maybe.Nothing()),
                            { apply(::Just, TextFormat.fromDocument(it)) })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = ImageCellFormat(Nothing())

    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf())
        .maybeMerge(this.textFormat.apply {
            Just(Pair("text_format", it.toDocument() as SchemaDoc)) })


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun textFormat() : Maybe<TextFormat> = this.textFormat


    // -----------------------------------------------------------------------------------------
    // RESOLVERS
    // -----------------------------------------------------------------------------------------

    fun resolveTextFormat(columnFormat : ImageColumnFormat) : TextFormat =
        when (this.textFormat) {
            is Just -> this.textFormat.value
            else    -> columnFormat.columnFormat().textFormat()
        }

}



data class ImageCellUI(val imageCell : TableWidgetImageCell,
                       val column : TableWidgetImageColumn,
                       val entityId : EntityId,
                       val context : Context)
{


    // -----------------------------------------------------------------------------------------
    // VIEWS
    // -----------------------------------------------------------------------------------------

    fun view() : View
    {
        val layout = TableWidgetCellView.layout(column.format().columnFormat(),
                                                entityId,
                                                context)

        layout.addView(this.imageView())

        return layout
    }


    private fun imageView() : ImageView
    {
        val image               = ImageViewBuilder()

        val format              = imageCell.format().resolveTextFormat(column.format())

        image.image             = column.defaultIconType().drawableResId()

        image.widthDp           = format.iconFormat().size.width
        image.heightDp          = format.iconFormat().size.height

        image.color             = colorOrBlack(format.iconFormat().colorTheme, entityId)

        return image.imageView(context)
    }

}
