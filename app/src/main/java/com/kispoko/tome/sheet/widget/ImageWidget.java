
package com.kispoko.tome.sheet.widget;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.kispoko.tome.activity.sheet.ChooseImageAction;
import com.kispoko.tome.activity.SheetActivity;
import com.kispoko.tome.R;
import com.kispoko.tome.rules.Rules;
import com.kispoko.tome.sheet.widget.util.WidgetData;
import com.kispoko.tome.sheet.widget.util.WidgetUI;
import com.kispoko.tome.util.SerialBitmap;
import com.kispoko.tome.util.Util;
import com.kispoko.tome.util.model.Modeler;
import com.kispoko.tome.util.value.ModelValue;
import com.kispoko.tome.util.value.PrimitiveValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.io.Serializable;
import java.util.UUID;



/**
 * ImageWidget
 */
public class ImageWidget extends Widget implements Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // ** Model Values
    private UUID                         id;

    private ModelValue<WidgetData>       widgetData;
    private PrimitiveValue<SerialBitmap> bitmap;

    // ** Internal
    private int imageViewId;
    private int chooseImageButtonId;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public ImageWidget() { }


    public ImageWidget(UUID id, WidgetData widgetData, Bitmap bitmap)
    {
        this.id = id;

        this.widgetData = new ModelValue<>(widgetData, this, WidgetData.class);

        SerialBitmap serialBitmap = new SerialBitmap(bitmap);
        this.bitmap     = new PrimitiveValue<>(serialBitmap, this, SerialBitmap.class);
    }


    /**
     * Create an ImageWidget from its yaml representation.
     * @param yaml The Yaml parser object.
     * @return A new ImageWidget.
     * @throws YamlException
     */
    public static ImageWidget fromYaml(Yaml yaml)
                  throws YamlException
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


    // ** On Update
    // ------------------------------------------------------------------------------------------

    public void onModelUpdate(String valueName) { }


    // > Widget
    // ------------------------------------------------------------------------------------------

    /**
     * The widget type as a string.
     * @return The widget's type as a string.
     */
    public String name() {
        return "text";
    }


    public void runAction(String actionName, Context context, Rules rules) { }


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
        Modeler.saveValuePromise(this).run(null);
    }


    // > Views
    // ------------------------------------------------------------------------------------------

    /**
     * Get the view for the image component. Depending on the mode, it will either display an
     * image or a button that allows the user to choose an image.
     * Use setMode to change the view dynamically.
     * @param context
     * @return
     */
    public View getDisplayView(final Context context, Rules rules)
    {
        // Layout
        final LinearLayout imageLayout = WidgetUI.linearLayout(this, context, rules);
        imageLayout.setGravity(Gravity.CENTER);
        //imageLayout.setLayoutParams(com.kispoko.tome.util.Util.linearLayoutParamsMatch());

        // Views
        final ImageView imageView = this.imageView(context);
        this.imageViewId = Util.generateViewId();
        imageView.setId(this.imageViewId);

        final Button chooseImageButton = this.chooseImageButton(context);
        this.chooseImageButtonId = Util.generateViewId();
        chooseImageButton.setId(this.chooseImageButtonId);

        final ImageWidget thisImageWidget = this;

        chooseImageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SheetActivity sheetActivity = (SheetActivity) context;

                Intent intent;
                if (Build.VERSION.SDK_INT < 19) {
                    intent = new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                } else {
                    intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("image/*");
                }

                sheetActivity.setChooseImageAction(new ChooseImageAction(thisImageWidget));

                sheetActivity.startActivityForResult(intent, SheetActivity.CHOOSE_IMAGE_FROM_FILE);

            }
        });


        // Add views to layout
        imageLayout.addView(imageView);
        imageLayout.addView(chooseImageButton);

        // Have a picture, show it
        if (this.bitmap.getValue() != null)
        {
            chooseImageButton.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageBitmap(this.bitmap.getValue().getBitmap());
            Log.d("***IMAGE", "set image bitmap");
        }
        // No stored picture, give user upload button
        else {
            chooseImageButton.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
        }


        return imageLayout;
    }


    public View getEditorView(Context context, Rules rules)
    {
        return new LinearLayout(context);
    }


    // > INTERNAL
    // ------------------------------------------------------------------------------------------


    private ImageView imageView(Context context)
    {
        ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setAdjustViewBounds(true);

        LinearLayout.LayoutParams imageViewLayoutParams = Util.linearLayoutParamsWrap();
        int imageViewHeight = (int) context.getResources()
                .getDimension(R.dimen.comp_image_image_height);
        imageViewLayoutParams.height = imageViewHeight;
        imageView.setLayoutParams(imageViewLayoutParams);

        return imageView;
    }


    private Button chooseImageButton(Context context)
    {
        final Button button = new Button(context);


        // Button text appearance
        button.setText("Choose a Picture");
        int textSize = (int) context.getResources()
                .getDimension(R.dimen.comp_image_button_text_size);
        button.setTextSize(textSize);
        button.setTextColor(ContextCompat.getColor(context, R.color.text_medium));

        // Set button icon
        button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add_a_photo_17dp, 0, 0, 0);

        // Configure button padding
        int buttonPadding = (int) context.getResources()
                .getDimension(R.dimen.comp_image_button_padding);
        int buttonIconPadding = (int) context.getResources()
                .getDimension(R.dimen.comp_image_button_icon_padding);
        button.setPadding(buttonPadding, 0, buttonPadding, 0);
        button.setCompoundDrawablePadding(buttonIconPadding);

        // Configure button layout params
        LinearLayout.LayoutParams buttonLayoutParams = Util.linearLayoutParamsWrap();

        // >> Button margins
        int buttonVertMargins = (int) context.getResources()
                                             .getDimension(R.dimen.comp_image_button_vert_margins);
        buttonLayoutParams.setMargins(0, buttonVertMargins, 0, buttonVertMargins);

        button.setLayoutParams(buttonLayoutParams);

        return button;
    }


}
