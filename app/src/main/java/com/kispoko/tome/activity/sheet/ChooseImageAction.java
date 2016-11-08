
package com.kispoko.tome.activity.sheet;


import android.net.Uri;

import com.kispoko.tome.activity.SheetActivity;
import com.kispoko.tome.sheet.component.type.Image;



/**
 */
public class ChooseImageAction
{

    private Image image;

    public ChooseImageAction(Image image)
    {
        this.image = image;
    }

    public void setImage(SheetActivity sheetActivity, Uri uri)
    {
        this.image.setImageFromURI(sheetActivity, uri);
    }

}
