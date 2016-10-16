
package com.kispoko.tome.activity.sheet;


import android.content.Context;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.kispoko.tome.R;

/**
 */
public class ChooseImageAction
{

    private LinearLayout imageLayout;
    private ImageView imageView;
    private Button chooseImageButton;

    public ChooseImageAction(LinearLayout imageLayout, ImageView imageView, Button chooseImageButton)
    {
        this.imageLayout = imageLayout;
        this.imageView = imageView;
        this.chooseImageButton = chooseImageButton;
    }

    public void setImage(Context context, Uri uri)
    {
        this.imageLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.sheet_dark));
        this.imageView.setVisibility(View.VISIBLE);
        this.chooseImageButton.setVisibility(View.GONE);
        this.imageView.setImageURI(uri);
    }

}
