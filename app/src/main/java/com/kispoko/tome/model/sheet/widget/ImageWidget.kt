
package com.kispoko.tome.model.sheet.widget


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Comp
import com.kispoko.tome.lib.functor.Func
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.model.sheet.style.Height
import com.kispoko.tome.model.sheet.style.Position
import com.kispoko.tome.model.sheet.style.TextStyle
import com.kispoko.tome.model.theme.ColorId
import effect.effApply
import effect.effError
import effect.effValue
import lulo.document.DocDict
import lulo.document.DocType
import lulo.document.SpecDoc
import lulo.document.docType
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import java.util.*



/**
 * Image Widget Format
 */
data class ImageWidgetFormat(override val id : UUID,
                             val widgetFormat : Comp<WidgetFormat>) : Model
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.widgetFormat.name      = "widget_format"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ImageWidgetFormat>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<ImageWidgetFormat> = when (doc)
        {
            is DocDict -> effApply(::ImageWidgetFormat,
                                   // Model Id
                                   effValue(UUID.randomUUID()),
                                   // Widget Format
                                   doc.at("widget_format") ap {
                                       effApply(::Comp, WidgetFormat.fromDocument(it))
                                   })
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun widgetFormat() : WidgetFormat = this.widgetFormat.value


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name : String = "image_widget_format"

    override val modelObject = this

}


//
//
//
//    // > Widget
//    // ------------------------------------------------------------------------------------------
//
//    @Override
//    public void initialize(GroupParent groupParent, Context context)
//    {
//        this.groupParent = groupParent;
//    }
//
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
//
//    // > Views
//    // ------------------------------------------------------------------------------------------
//
//    private LinearLayout viewLayout(Context context)
//    {
//        LinearLayoutBuilder layout = new LinearLayoutBuilder();
//
//        layout.orientation          = LinearLayout.VERTICAL;
//        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
//        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        layout.gravity              = Gravity.CENTER_HORIZONTAL;
//
//        layout.backgroundColor      = this.data().format().background().colorId();
//
//        return layout.linearLayout(context);
//    }
//
//
//    private ImageView imageView(Context context)
//    {
//        ImageViewBuilder imageView = new ImageViewBuilder();
//        this.imageViewId = Util.generateViewId();
//
//        imageView.id                = this.imageViewId;
//        imageView.width             = LinearLayout.LayoutParams.WRAP_CONTENT;
//        imageView.height            = R.dimen.widget_image_view_height;
//        imageView.scaleType         = ImageView.ScaleType.FIT_XY;
//        imageView.adjustViewBounds  = true;
//
//        return imageView.imageView(context);
//    }
//
//
//    /**
//     * The choose image view that is displayed on the sheet if the image widget is present, but no
//     * image is currently chosen or loaded.
//     * @param context The context.
//     * @return The choose image view.
//     */
//    private LinearLayout chooseImageView(Context context)
//    {
//        // [1] Declarations
//        // -------------------------------------------------------------------------------------
//
//        LinearLayoutBuilder layout   = new LinearLayoutBuilder();
//        ImageViewBuilder    iconView = new ImageViewBuilder();
//        TextViewBuilder     textView = new TextViewBuilder();
//
//        this.chooseImageButtonId = Util.generateViewId();
//
//        // [2] Layout
//        // --------------------------------------------------------------------------------------
//
//        layout.id                   = this.chooseImageButtonId;
//
//        layout.orientation          = LinearLayout.VERTICAL;
//        layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT;
//        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;
//        layout.gravity              = Gravity.CENTER_HORIZONTAL;
//
////        layout.padding.top      = R.dimen.widget_image_choose_layout_padding_vert;
////        layout.padding.bottom   = R.dimen.widget_image_choose_layout_padding_vert;
//
//        layout.backgroundResource   = R.drawable.bg_choose_image_button;
//
//        layout.child(iconView)
//              .child(textView);
//
//        // [3 A] Icon View
//        // --------------------------------------------------------------------------------------
//
//        iconView.width          = LinearLayout.LayoutParams.WRAP_CONTENT;
//        iconView.height         = LinearLayout.LayoutParams.WRAP_CONTENT;
//
//        iconView.image          = R.drawable.ic_choose_a_picture;
//
//        iconView.margin.bottom  = R.dimen.widget_image_choose_icon_margin_bottom;
//
//        // [3 B] Text View
//        // --------------------------------------------------------------------------------------
//
//        textView.width  = LinearLayout.LayoutParams.WRAP_CONTENT;
//        textView.height = LinearLayout.LayoutParams.WRAP_CONTENT;
//        textView.text   = "Choose a Picture";
//        textView.size   = R.dimen.widget_image_choose_text_size;
//        textView.color  = R.color.dark_blue_2;
//        textView.font   = Font.sansSerifFontRegular(context);
//
//
//        return layout.linearLayout(context);
//    }
//

