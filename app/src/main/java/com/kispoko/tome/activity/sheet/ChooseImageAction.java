
package com.kispoko.tome.activity.sheet;


import android.net.Uri;

import com.kispoko.tome.activity.SheetActivity;


/**
 */
public class ChooseImageAction
{

    private ImageWidget imageWidget;

    public ChooseImageAction(ImageWidget imageWidget)
    {
        this.imageWidget = imageWidget;
    }

    public void setImage(SheetActivity sheetActivity, Uri uri)
    {
        this.imageWidget.setImageFromURI(sheetActivity, uri);
    }

}
