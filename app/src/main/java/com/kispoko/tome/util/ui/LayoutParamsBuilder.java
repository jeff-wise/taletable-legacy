
package com.kispoko.tome.util.ui;


import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

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
    private TableLayout.LayoutParams    tableLayoutParams;
    private TableRow.LayoutParams       tableRowLayoutParams;

    private Context                     context;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public LayoutParamsBuilder(LayoutType layoutType, Context context)
    {
        this.layoutType           = layoutType;

        this.linearLayoutParams   = null;
        this.relativeLayoutParams = null;
        this.tableLayoutParams    = null;
        this.tableRowLayoutParams = null;

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
            case TABLE:
                this.tableLayoutParams = new TableLayout.LayoutParams(
                                                TableLayout.LayoutParams.MATCH_PARENT,
                                                TableLayout.LayoutParams.MATCH_PARENT);
            case TABLE_ROW:
                this.tableRowLayoutParams = new TableRow.LayoutParams(
                                                TableRow.LayoutParams.MATCH_PARENT,
                                                TableRow.LayoutParams.MATCH_PARENT);
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


    public TableLayout.LayoutParams tableLayoutParams()
    {
        return this.tableLayoutParams;
    }


    public TableRow.LayoutParams tableRowLayoutParams()
    {
        return this.tableRowLayoutParams;
    }


    public ViewGroup.LayoutParams layoutParams()
    {
        switch (this.layoutType)
        {
            case LINEAR:
                return this.linearLayoutParams;
            case RELATIVE:
                return this.relativeLayoutParams;
            case TABLE:
                return this.tableLayoutParams;
            case TABLE_ROW:
                return this.tableRowLayoutParams;
        }

        return null;
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
            case TABLE:
                this.tableLayoutParams.width = widthValue;
                break;
            case TABLE_ROW:
                this.tableRowLayoutParams.width = widthValue;
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
            case TABLE:
                this.tableLayoutParams.height = heightValue;
                break;
            case TABLE_ROW:
                this.tableRowLayoutParams.height = heightValue;
                break;
        }
    }


    public void setWeight(Float weight)
    {
        switch (this.layoutType)
        {
            case LINEAR:
                this.linearLayoutParams.weight = weight;
                break;
            case TABLE:
                this.tableLayoutParams.weight = weight;
                break;
            case TABLE_ROW:
                this.tableRowLayoutParams.weight = weight;
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
                this.linearLayoutParams.setMargins(margins.left(context),
                                                   margins.top(context),
                                                   margins.right(context),
                                                   margins.bottom(context));
                break;
            case RELATIVE:
                this.relativeLayoutParams.setMargins(margins.left(context),
                                                     margins.top(context),
                                                     margins.right(context),
                                                     margins.bottom(context));
                break;
            case TABLE:
                this.tableLayoutParams.setMargins(margins.left(context),
                                                  margins.top(context),
                                                  margins.right(context),
                                                  margins.bottom(context));
                break;
            case TABLE_ROW:
                this.tableRowLayoutParams.setMargins(margins.left(context),
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
            constant == LinearLayout.LayoutParams.WRAP_CONTENT ||
            constant == 0) {
            return true;
        }
        else {
            return false;
        }
    }

}
