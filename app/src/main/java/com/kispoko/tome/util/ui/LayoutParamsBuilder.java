
package com.kispoko.tome.util.ui;


import android.content.Context;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.List;


/**
 * Layout Params Builder
 */
public class LayoutParamsBuilder
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private LayoutType                  layoutType;

    private LinearLayout.LayoutParams   linearLayoutParams;
    private RelativeLayout.LayoutParams relativeLayoutParams;

    private Context                     context;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public LayoutParamsBuilder(LayoutType layoutType, Context context)
    {
        this.layoutType           = layoutType;

        this.linearLayoutParams   = null;
        this.relativeLayoutParams = null;

        this.context              = context;

        switch (layoutType)
        {
            case LINEAR:
                this.linearLayoutParams = new LinearLayout.LayoutParams(
                                                LinearLayout.LayoutParams.MATCH_PARENT,
                                                LinearLayout.LayoutParams.MATCH_PARENT);
                break;
            case RELATIVE:
                this.relativeLayoutParams = new RelativeLayout.LayoutParams(
                                                RelativeLayout.LayoutParams.MATCH_PARENT,
                                                RelativeLayout.LayoutParams.MATCH_PARENT);
                break;
        }
    }


    // API
    // ------------------------------------------------------------------------------------------

    // > Layouts
    // ------------------------------------------------------------------------------------------

    public LinearLayout.LayoutParams linearLayoutParams()
    {
        return this.linearLayoutParams;
    }


    public RelativeLayout.LayoutParams relativeLayoutParams()
    {
        return this.relativeLayoutParams;
    }


    // > Attribute Setters
    // ------------------------------------------------------------------------------------------

    public void setWidth(int width)
    {
        int widthValue;

        if (isLayoutConstant(width)) {
            widthValue = width;
        }
        else {
            widthValue = (int) context.getResources().getDimension(width);
        }

        switch (this.layoutType)
        {
            case LINEAR:
                this.linearLayoutParams.width = widthValue;
                break;
            case RELATIVE:
                this.relativeLayoutParams.width = widthValue;
                break;
        }
    }


    public void setHeight(int height)
    {
        int heightValue;

        if (isLayoutConstant(height)) {
            heightValue = height;
        }
        else {
            heightValue = (int) context.getResources().getDimension(height);
        }

        switch (this.layoutType)
        {
            case LINEAR:
                this.linearLayoutParams.height = heightValue;
                break;
            case RELATIVE:
                this.relativeLayoutParams.height = heightValue;
                break;
        }
    }


    public void setGravity(int gravity)
    {
        switch (this.layoutType)
        {
            case LINEAR:
                this.linearLayoutParams.gravity = gravity;
                break;
        }
    }


    public void setMargins(Margins margins)
    {
        switch (this.layoutType)
        {
            case LINEAR:
                linearLayoutParams.setMargins(margins.left(context),
                                              margins.top(context),
                                              margins.right(context),
                                              margins.bottom(context));
                break;
            case RELATIVE:
                relativeLayoutParams.setMargins(margins.left(context),
                                                margins.top(context),
                                                margins.right(context),
                                                margins.bottom(context));
                break;
        }
    }


    public void setRules(List<Integer> rules)
    {
        if (this.layoutType != LayoutType.RELATIVE)
            return;

        for (Integer rule : rules) {
            this.relativeLayoutParams.addRule(rule);
        }
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    private boolean isLayoutConstant(Integer constant)
    {
        if (constant == LinearLayout.LayoutParams.MATCH_PARENT ||
            constant == LinearLayout.LayoutParams.WRAP_CONTENT) {
            return true;
        }
        else {
            return false;
        }
    }

}