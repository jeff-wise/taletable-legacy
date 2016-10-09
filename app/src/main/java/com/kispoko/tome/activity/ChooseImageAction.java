
package com.kispoko.tome.activity;


import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

/**
 */
public class ChooseImageAction
{

    private ImageView imageView;
    private Button chooseImageButton;

    public ChooseImageAction(ImageView imageView, Button chooseImageButton)
    {
        this.imageView = imageView;
        this.chooseImageButton = chooseImageButton;
    }

    public void setImage(Uri uri)
    {
        this.imageView.setVisibility(View.VISIBLE);
        this.chooseImageButton.setVisibility(View.GONE);
        this.imageView.setImageURI(uri);
    }

}
