
package com.kispoko.tome.component;


import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.kispoko.tome.ChooseImageAction;
import com.kispoko.tome.MainActivity;
import com.kispoko.tome.R;
import com.kispoko.tome.util.UI;

import java.io.Serializable;
import java.util.Map;



/**
 * Image
 */
public class Image extends Component implements Serializable
{

    // > PROPERTIES
    // ------------------------------------------------------------------------------------------

    private String name;


    // > CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Image(String name)
    {
        super(name);
    }


    public static Image fromYaml(Map<String, Object> imageYaml)
    {
        //Map<String, Object> dataYaml = (Map<String, Object>) imageYaml.get("data");

        String name = (String) imageYaml.get("name");

        return new Image(name);
    }


    // > API
    // ------------------------------------------------------------------------------------------


    /**
     * Get the view for the image component. Depending on the mode, it will either display an
     * image or a button that allows the user to choose an image.
     * Use setMode to change the view dynamically.
     * @param context
     * @return
     */
    public View getView(final Context context)
    {
        // Layout
        LinearLayout imageLayout = Component.linearLayout(context);
        imageLayout.setGravity(Gravity.CENTER);
        imageLayout.setLayoutParams(UI.linearLayoutParamsMatch());

        // Views
        final ImageView imageView = this.imageView(context);
        final Button chooseImageButton = this.chooseImageButton(context);

        final Image thisImage = this;

        chooseImageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity) context;

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

//                intent.setType("image/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);

                mainActivity.setChooseImageAction(
                        new ChooseImageAction(imageView, chooseImageButton));

                mainActivity.startActivityForResult(intent, MainActivity.CHOOSE_IMAGE_FROM_FILE);

//                mainActivity.startActivityForResult(
//                        Intent.createChooser(intent, "Complete action using"),
//                        MainActivity.CHOOSE_IMAGE_FROM_FILE);
            }
        });

        chooseImageButton.setVisibility(View.VISIBLE);

        // Add views to layout
        imageLayout.addView(imageView);
        imageLayout.addView(chooseImageButton);

        return imageLayout;
    }


    // > INTERNAL
    // ------------------------------------------------------------------------------------------


    private ImageView imageView(Context context)
    {
        ImageView imageView = new ImageView(context);
        imageView.setVisibility(View.GONE);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setAdjustViewBounds(true);

        LinearLayout.LayoutParams imageViewLayoutParams = UI.linearLayoutParamsWrap();
        int imageViewHeight = (int) context.getResources()
                .getDimension(R.dimen.comp_image_image_height);
        imageViewLayoutParams.height = imageViewHeight;
        imageView.setLayoutParams(imageViewLayoutParams);

        return imageView;
    }


    private Button chooseImageButton(Context context)
    {
        final Button button = new Button(context);

        button.setVisibility(View.GONE);

        // Button text appearance
        button.setText("Choose a Picture");
        int textSize = (int) context.getResources()
                .getDimension(R.dimen.comp_image_button_text_size);
        button.setTextSize(textSize);
        button.setTextColor(ContextCompat.getColor(context, R.color.text));

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
        LinearLayout.LayoutParams buttonLayoutParams = UI.linearLayoutParamsWrap();

        // >> Button margins
        int buttonVertMargins = (int) context.getResources()
                                             .getDimension(R.dimen.comp_image_button_vert_margins);
        buttonLayoutParams.setMargins(0, buttonVertMargins, 0, buttonVertMargins);

        button.setLayoutParams(buttonLayoutParams);

        return button;
    }


}
