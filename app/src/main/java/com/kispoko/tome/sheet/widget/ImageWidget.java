
package com.kispoko.tome.sheet.widget;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.kispoko.tome.activity.sheet.ChooseImageAction;
import com.kispoko.tome.activity.SheetActivity;
import com.kispoko.tome.R;
import com.kispoko.tome.sheet.Alignment;
import com.kispoko.tome.sheet.SheetManager;
import com.kispoko.tome.sheet.group.GroupParent;
import com.kispoko.tome.sheet.BackgroundColor;
import com.kispoko.tome.sheet.widget.util.WidgetData;
import com.kispoko.tome.util.SerialBitmap;
import com.kispoko.tome.util.Util;
import com.kispoko.tome.lib.ui.Font;
import com.kispoko.tome.lib.ui.ImageViewBuilder;
import com.kispoko.tome.lib.ui.LinearLayoutBuilder;
import com.kispoko.tome.lib.ui.TextViewBuilder;
import com.kispoko.tome.lib.functor.ModelFunctor;
import com.kispoko.tome.lib.functor.PrimitiveFunctor;
import com.kispoko.tome.lib.yaml.ToYaml;
import com.kispoko.tome.lib.yaml.YamlBuilder;
import com.kispoko.tome.lib.yaml.YamlParser;
import com.kispoko.tome.lib.yaml.YamlParseException;

import java.io.Serializable;
import java.util.UUID;



/**
 * ImageWidget
 */
public class ImageWidget extends Widget
                         implements ToYaml, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID                            id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private ModelFunctor<WidgetData>        widgetData;
    private PrimitiveFunctor<SerialBitmap>  bitmap;


    // > Internal
    // ------------------------------------------------------------------------------------------

    private int                             imageViewId;
    private int                             chooseImageButtonId;

    private GroupParent                     groupParent;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public ImageWidget()
    {
        this.id         = null;

        this.widgetData = ModelFunctor.empty(WidgetData.class);
        this.bitmap     = new PrimitiveFunctor<>(null, SerialBitmap.class);
    }


    public ImageWidget(UUID id, WidgetData widgetData, Bitmap bitmap)
    {
        this.id         = id;

        this.widgetData = ModelFunctor.full(widgetData, WidgetData.class);

        if (this.bitmap != null) {
            SerialBitmap serialBitmap = new SerialBitmap(bitmap);
            this.bitmap     = new PrimitiveFunctor<>(serialBitmap, SerialBitmap.class);
        }
        else {
            this.bitmap     = new PrimitiveFunctor<>(null, SerialBitmap.class);
        }

        this.initializeImageWidget();
    }


    /**
     * Create an ImageWidget from its yaml representation.
     * @param yaml The Yaml parser object.
     * @return A new ImageWidget.
     * @throws YamlParseException
     */
    public static ImageWidget fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        UUID       id         = UUID.randomUUID();

        WidgetData widgetData = WidgetData.fromYaml(yaml.atKey("data"));

        return new ImageWidget(id, widgetData, null);
    }


    // API
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    // ** Id
    // ------------------------------------------------------------------------------------------

    /**
     * Get the model identifier.
     * @return The model UUID.
     */
    public UUID getId()
    {
        return this.id;
    }


    /**
     * Set the model identifier.
     * @param id The new model UUID.
     */
    public void setId(UUID id)
    {
        this.id = id;
    }


    // ** Data
    // ------------------------------------------------------------------------------------------

    /**
     * Get the image widget's WidgetData object.
     * @return The WidgetData.
     */
    public WidgetData data()
    {
        return this.widgetData.getValue();
    }


    // ** On Load
    // ------------------------------------------------------------------------------------------

    /**
     * This method is called when the Image Widget is completely loaded for the first time.
     */
    public void onLoad()
    {
        this.initializeImageWidget();
    }


    // > To Yaml
    // ------------------------------------------------------------------------------------------

    /**
     * The Image Widget's yaml representation.
     * @return The Yaml Builder.
     */
    public YamlBuilder toYaml()
    {
        return YamlBuilder.map()
                .putYaml("data", this.data());
    }


    // > Widget
    // ------------------------------------------------------------------------------------------

    @Override
    public void initialize(GroupParent groupParent, Context context)
    {
        this.groupParent = groupParent;
    }


    // > Image
    // ------------------------------------------------------------------------------------------

    public void setImageFromURI(Activity activity, Uri uri)
    {
        ImageView imageView = (ImageView) activity.findViewById(this.imageViewId);
        Button chooseImageButton = (Button) activity.findViewById(this.chooseImageButtonId);

        imageView.setVisibility(View.VISIBLE);
        chooseImageButton.setVisibility(View.GONE);
        imageView.setImageURI(uri);

        SerialBitmap serialBitmap =
                new SerialBitmap(((BitmapDrawable)imageView.getDrawable()).getBitmap());
        this.bitmap.setValue(serialBitmap);

        // > Save the Image Widget with the new bitmap
    }


    // > Views
    // ------------------------------------------------------------------------------------------

    /**
     * Get the view for the image component. Depending on the mode, it will either display an
     * image or a button that allows the user to choose an image.
     * Use setMode to change the view dynamically.
     * @return
     */
    public View view(boolean rowHasLabel, Context context)
    {
        LinearLayout layout = viewLayout(context);


        LinearLayout chooseImageView = this.chooseImageView(context);
        ImageView    imageView       = this.imageView(context);

        // Add views to layout
        layout.addView(chooseImageView);
        layout.addView(imageView);

        // No stored picture, give user upload button
        if (this.bitmap.isNull())
        {
            chooseImageView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
        }
        // Have a picture, show it
        else
        {
            chooseImageView.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageBitmap(this.bitmap.getValue().getBitmap());
        }


        return layout;
    }


    // INTERNAL
    // -----------------------------------------------------------------------------------------

    // > Initialize
    // -----------------------------------------------------------------------------------------

    private void initializeImageWidget()
    {
        // [1] Set default format values
        // -------------------------------------------------------------------------------------

        // ** Alignment
        if (this.data().format().alignmentIsDefault())
            this.data().format().setAlignment(Alignment.CENTER);

        // ** Background
        if (this.data().format().backgroundIsDefault())
            this.data().format().setBackground(BackgroundColor.NONE);

    }


    // > Choose Image SheetDialog
    // -----------------------------------------------------------------------------------------

    private void chooseImageDialog()
    {
        SheetActivity sheetActivity = (SheetActivity) SheetManager.currentSheetContext();

        Intent intent;

        if (Build.VERSION.SDK_INT < 19)
        {
            intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
        }
        else
        {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
        }

        sheetActivity.setChooseImageAction(new ChooseImageAction(this));

        sheetActivity.startActivityForResult(intent, SheetActivity.CHOOSE_IMAGE_FROM_FILE);
    }


    // > Views
    // ------------------------------------------------------------------------------------------

    private LinearLayout viewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.VERTICAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.gravity              = Gravity.CENTER_HORIZONTAL;

        layout.backgroundColor      = this.data().format().background().colorId();

        return layout.linearLayout(context);
    }


    private ImageView imageView(Context context)
    {
        ImageViewBuilder imageView = new ImageViewBuilder();
        this.imageViewId = Util.generateViewId();

        imageView.id                = this.imageViewId;
        imageView.width             = LinearLayout.LayoutParams.WRAP_CONTENT;
        imageView.height            = R.dimen.widget_image_view_height;
        imageView.scaleType         = ImageView.ScaleType.FIT_XY;
        imageView.adjustViewBounds  = true;

        return imageView.imageView(context);
    }


    /**
     * The choose image view that is displayed on the sheet if the image widget is present, but no
     * image is currently chosen or loaded.
     * @param context The context.
     * @return The choose image view.
     */
    private LinearLayout chooseImageView(Context context)
    {
        // [1] Declarations
        // -------------------------------------------------------------------------------------

        LinearLayoutBuilder layout   = new LinearLayoutBuilder();
        ImageViewBuilder    iconView = new ImageViewBuilder();
        TextViewBuilder     textView = new TextViewBuilder();

        this.chooseImageButtonId = Util.generateViewId();

        // [2] Layout
        // --------------------------------------------------------------------------------------

        layout.id                   = this.chooseImageButtonId;

        layout.orientation          = LinearLayout.VERTICAL;
        layout.width                = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;
        layout.gravity              = Gravity.CENTER_HORIZONTAL;

//        layout.padding.top      = R.dimen.widget_image_choose_layout_padding_vert;
//        layout.padding.bottom   = R.dimen.widget_image_choose_layout_padding_vert;

        layout.backgroundResource   = R.drawable.bg_choose_image_button;

        layout.child(iconView)
              .child(textView);

        // [3 A] Icon View
        // --------------------------------------------------------------------------------------

        iconView.width          = LinearLayout.LayoutParams.WRAP_CONTENT;
        iconView.height         = LinearLayout.LayoutParams.WRAP_CONTENT;

        iconView.image          = R.drawable.ic_choose_a_picture;

        iconView.margin.bottom  = R.dimen.widget_image_choose_icon_margin_bottom;

        // [3 B] Text View
        // --------------------------------------------------------------------------------------

        textView.width  = LinearLayout.LayoutParams.WRAP_CONTENT;
        textView.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        textView.text   = "Choose a Picture";
        textView.size   = R.dimen.widget_image_choose_text_size;
        textView.color  = R.color.dark_blue_2;
        textView.font   = Font.sansSerifFontRegular(context);


        return layout.linearLayout(context);
    }



    // > Clicks
    // -----------------------------------------------------------------------------------------

    /**
     * On image widget click, go to choose image dialog.
     */
    private void onImageWidgetShortClick()
    {
        this.chooseImageDialog();
    }


}
