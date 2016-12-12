
package com.kispoko.tome.util.ui;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.kispoko.tome.R;
import com.kispoko.tome.util.Util;



/**
 * Image View Builder
 */
public class ImageViewBuilder
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    public Integer id;

    public Integer height;
    public Integer width;

    public Padding padding;
    public Margins margin;

    public Integer image;



    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public ImageViewBuilder()
    {
        this.id      = null;

        this.height  = null;
        this.width   = null;

        this.padding = new Padding();
        this.margin  = new Margins();

        this.image   = null;
    }


    // API
    // ------------------------------------------------------------------------------------------

    public ImageView imageView(Context context)
    {
        ImageView imageView = new ImageView(context);

        // [1] Image View
        // --------------------------------------------------------------------------------------

        // > Id
        // --------------------------------------------------------------------------------------

        if (this.id != null)
            imageView.setId(this.id);

        // > Padding
        // --------------------------------------------------------------------------------------

        imageView.setPadding(this.padding.left(context),
                this.padding.top(context),
                this.padding.right(context),
                this.padding.bottom(context));

        // > Image
        // --------------------------------------------------------------------------------------

        if (this.image != null)
            imageView.setImageDrawable(ContextCompat.getDrawable(context, this.image));

        // [2] Layout
        // --------------------------------------------------------------------------------------

        LinearLayout.LayoutParams imageViewLayoutParams = Util.linearLayoutParamsMatch();
        imageView.setLayoutParams(imageViewLayoutParams);

        // > Height
        // --------------------------------------------------------------------------------------

        if (this.height != null) {
            if (isLayoutConstant(this.height)) {
                imageViewLayoutParams.height = this.height;
            } else {
                imageViewLayoutParams.height = (int) context.getResources()
                                                            .getDimension(this.height);
            }
        }

        // > Width
        // --------------------------------------------------------------------------------------

        if (this.width != null) {
            if (isLayoutConstant(this.width)) {
                imageViewLayoutParams.width = this.width;
            } else {
                imageViewLayoutParams.width = (int) context.getResources()
                        .getDimension(this.width);
            }
        }

        // > Margins
        // --------------------------------------------------------------------------------------

        imageViewLayoutParams.setMargins(this.margin.left(context),
                                         this.margin.top(context),
                                         this.margin.right(context),
                                         this.margin.bottom(context));

        return imageView;
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    private boolean isLayoutConstant(Integer constant)
    {
        if (constant == LinearLayout.LayoutParams.MATCH_PARENT ||
                constant == LinearLayout.LayoutParams.WRAP_CONTENT) {
            return true;
        } else {
            return false;
        }
    }


}
