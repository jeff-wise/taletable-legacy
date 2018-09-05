
package com.taletable.android.model.sheet.widget


import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.taletable.android.R
import com.taletable.android.lib.Factory
import com.taletable.android.lib.orm.ProdType
import com.taletable.android.lib.orm.sql.SQLBlob
import com.taletable.android.lib.orm.sql.SQLSerializable
import com.taletable.android.lib.orm.sql.SQLText
import com.taletable.android.lib.orm.sql.SQLValue
import com.taletable.android.lib.ui.ImageViewBuilder
import com.taletable.android.lib.ui.LinearLayoutBuilder
import com.taletable.android.model.sheet.style.Icon
import com.taletable.android.rts.entity.EntityId
import com.taletable.android.rts.entity.colorOrBlack
import effect.apply
import effect.effError
import effect.effValue
import effect.split
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import maybe.Just
import org.apache.commons.lang3.SerializationUtils
import java.io.InputStream
import java.io.Serializable
import java.util.*



/**
 * Image Widget Format
 */
data class ImageWidgetFormat(val widgetFormat : WidgetFormat)
                             : ToDocument, Serializable
{


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ImageWidgetFormat>
    {

        private fun defaultWidgetFormat()  = WidgetFormat.default()


        override fun fromDocument(doc : SchemaDoc) : ValueParser<ImageWidgetFormat> = when (doc)
        {
            is DocDict ->
            {
                apply(::ImageWidgetFormat,
                     // Widget Format
                     split(doc.maybeAt("widget_format"),
                           effValue(defaultWidgetFormat()),
                           { WidgetFormat.fromDocument(it) })
                     )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = ImageWidgetFormat(defaultWidgetFormat())
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "widget_format" to this.widgetFormat().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun widgetFormat() : WidgetFormat = this.widgetFormat

}


/**
 * Official Image Id
 */
data class OfficialImageId(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<OfficialImageId>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<OfficialImageId> = when (doc)
        {
            is DocText -> effValue(OfficialImageId(doc.text))
            else       -> effError(lulo.value.UnexpectedType(DocType.TEXT, docType(doc), doc.path))
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


/**
 * Official Image Id List
 */
data class OfficialImageIdList(val imageIds : List<OfficialImageId>) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLBlob({ SerializationUtils.serialize(this)})

}


class ImageWidgetUI(val imageWidget : ImageWidget,
                    val entityId : EntityId,
                    val context : Context)
{


    fun view() : View
    {
        val layout = WidgetView.layout(imageWidget.widgetFormat(), entityId, context)


//        val layoutId = Util.generateViewId()
//        contentLayout.id = layoutId
//        imageWidget.layoutViewId = layoutId

//        val layout = this.viewLayout()
//
//        if (imageWidget.officialImageIds().isNotEmpty())
//            layout.addView(this.officialImageView(imageWidget.officialImageIds().first()))

        this.updateView(layout)

        return layout
    }


    private fun updateView(layout : LinearLayout)
    {
        val contentLayout = layout.findViewById<LinearLayout>(R.id.widget_content_layout)
        contentLayout.removeAllViews()

        val maybeIcon = imageWidget.icon
        when (maybeIcon) {
            is Just -> {
                Log.d("***IMAGE WIDGET", "rendering icon view")
                contentLayout.addView(this.iconView(maybeIcon.value))
                return
            }
        }

        if (imageWidget.officialImageIds().isNotEmpty())
            contentLayout.addView(this.officialImageView(imageWidget.officialImageIds().first()))
    }


    private fun viewLayout() : LinearLayout
    {
        val layout          = LinearLayoutBuilder()

        layout.width        = LinearLayout.LayoutParams.MATCH_PARENT
        layout.height       = LinearLayout.LayoutParams.WRAP_CONTENT

        return layout.linearLayout(context)
    }


    private fun iconView(icon : Icon) : ImageView
    {
        val image               = ImageViewBuilder()

        val iconFormat = icon.iconFormat()

        image.widthDp           = iconFormat.size().width
        image.heightDp          = iconFormat.size().height

//        image.width             = LinearLayout.LayoutParams.WRAP_CONTENT
//        image.height            = LinearLayout.LayoutParams.WRAP_CONTENT

//        image.scaleType         = ImageView.ScaleType.FIT_XY
//        image.adjustViewBounds  = true

        image.image             = icon.iconType().drawableResId()
//        image.image             = R.drawable.icon_planet

        image.color             = colorOrBlack(iconFormat.colorTheme(), entityId)

        return image.imageView(context)
    }


    private fun officialImageView(officialImageId : OfficialImageId) : ImageView
    {
        val image               = ImageViewBuilder()

        image.width             = LinearLayout.LayoutParams.WRAP_CONTENT
        image.height            = LinearLayout.LayoutParams.WRAP_CONTENT

        image.scaleType         = ImageView.ScaleType.FIT_XY
        image.adjustViewBounds  = true

        val imagePath = "images/${officialImageId.value}.png"

        var stream : InputStream? = null
        try
        {
            stream = context.assets.open(imagePath)
            if (stream != null)
                image.bitmap = BitmapFactory.decodeStream(stream)
        }
        finally {
            try
            {
                if(stream != null)
                {
                    stream.close();
                }
            } catch (e : Exception) {}
        }

        return image.imageView(context)
    }

}


//
//    // > Image
//    // ------------------------------------------------------------------------------------------
//
//    public void setImageFromURI(Activity activity, Uri uri)
//    {
//        ImageView imageView = (ImageView) activity.findViewById(this.imageViewId);
//        Button chooseImageButton = (Button) activity.findViewById(this.chooseImageButtonId);
//
//        imageView.setVisibility(View.VISIBLE);
//        chooseImageButton.setVisibility(View.GONE);
//        imageView.setImageURI(uri);
//
//        SerialBitmap serialBitmap =
//                new SerialBitmap(((BitmapDrawable)imageView.getDrawable()).getBitmap());
//        this.bitmap.setValue(serialBitmap);
//
//        // > Save the Image Widget with the new bitmap
//    }
//
//
//    // > Views
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * Get the view for the image component. Depending on the mode, it will either display an
//     * image or a button that allows the user to choose an image.
//     * Use setMode to change the view dynamically.
//     * @return
//     */
//    public View view(boolean rowHasLabel, Context context)
//    {
//        LinearLayout layout = viewLayout(context);
//
//
//        LinearLayout chooseImageView = this.chooseImageView(context);
//        ImageView    imageView       = this.imageView(context);
//
//        // Add views to layout
//        layout.addView(chooseImageView);
//        layout.addView(imageView);
//
//        // No stored picture, give user upload button
//        if (this.bitmap.isNull())
//        {
//            chooseImageView.setVisibility(View.VISIBLE);
//            imageView.setVisibility(View.GONE);
//        }
//        // Have a picture, show it
//        else
//        {
//            chooseImageView.setVisibility(View.GONE);
//            imageView.setVisibility(View.VISIBLE);
//            imageView.setImageBitmap(this.bitmap.getValue().getBitmap());
//        }
//
//
//        return layout;
//    }
//
//
//    // INTERNAL
//    // -----------------------------------------------------------------------------------------
//
//    // > Initialize
//    // -----------------------------------------------------------------------------------------
//
//    private void initializeImageWidget()
//    {
//        // [1] Set default format values
//        // -------------------------------------------------------------------------------------
//
//        // ** Alignment
//        if (this.data().format().alignmentIsDefault())
//            this.data().format().setAlignment(Alignment.CENTER);
//
//        // ** Background
//        if (this.data().format().backgroundIsDefault())
//            this.data().format().setBackground(BackgroundColor.NONE);
//
//    }
//
//
//    // > Choose Image SheetDialog
//    // -----------------------------------------------------------------------------------------
//
//    private void chooseImageDialog()
//    {
//        SheetActivityOld sheetActivity = (SheetActivityOld) SheetManagerOld.currentSheetContext();
//
//        Intent intent;
//
//        if (Build.VERSION.SDK_INT < 19)
//        {
//            intent = new Intent();
//            intent.setAction(Intent.ACTION_GET_CONTENT);
//            intent.setType("image/*");
//        }
//        else
//        {
//            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//            intent.addCategory(Intent.CATEGORY_OPENABLE);
//            intent.setType("image/*");
//        }
//
//        sheetActivity.setChooseImageAction(new ChooseImageAction(this));
//
//        sheetActivity.startActivityForResult(intent, SheetActivityOld.CHOOSE_IMAGE_FROM_FILE);
//    }
//

