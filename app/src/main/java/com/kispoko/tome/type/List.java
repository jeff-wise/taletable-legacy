
package com.kispoko.tome.type;


import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.util.Util;

import java.util.ArrayList;
import java.util.Map;



/**
 * List Type
 */
public class List extends Type
{

    // > PROPERTIES
    // -------------------------------------------------------------------------------------------

    private ArrayList<String> values;


    // > CONSTRUCTORS
    // -------------------------------------------------------------------------------------------

    public List(String name, ArrayList<String> values)
    {
        super(name);
        this.values = values;
    }


    @SuppressWarnings("unchecked")
    public static List fromYaml(Map<String,Object> listYaml)
    {
        String name = (String) listYaml.get("name");

        ArrayList<String> valueList = (ArrayList<String>) listYaml.get("values");

        return new List(name, valueList);
    }




    // > API
    // -------------------------------------------------------------------------------------------

    public List asClone()
    {
        return new List(this.getName(), new ArrayList<>(this.values));
    }


    public int size()
    {
        return this.values.size();
    }


    public String getValue(int position)
    {
        // TODO verify
        return this.values.get(position);
    }

    public ArrayList<String> getValueList()
    {
        return this.values;
    }



    // >> Views
    // -------------------------------------------------------------------------------------------


    public View getItemView(Context context)
    {
        // Layout
        LinearLayout itemLayout = new LinearLayout(context);
        itemLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.grey_350));

        LinearLayout.LayoutParams itemLayoutParams =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                              LinearLayout.LayoutParams.WRAP_CONTENT);
        itemLayout.setLayoutParams(itemLayoutParams);

        Typeface font = Typeface.createFromAsset(context.getAssets(),
                "fonts/DavidLibre-Bold.ttf");

        int itemLayoutVertPadding = (int) Util.getDim(context, R.dimen.type_list_item_vert_padding);
        int itemLayoutLeftPadding = (int) Util.getDim(context, R.dimen.type_list_item_left_padding);
        itemLayout.setPadding(itemLayoutLeftPadding, itemLayoutVertPadding,
                              itemLayoutLeftPadding, itemLayoutVertPadding);

        // Item Name
        TextView itemNameView = new TextView(context);
        itemNameView.setId(R.id.type_list_item_name);

        LinearLayout.LayoutParams itemNameLayoutParams =
                new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        itemNameView.setLayoutParams(itemNameLayoutParams);

        itemNameView.setTextColor(ContextCompat.getColor(context, R.color.text_medium));

        float itemNameTextSize = Util.getDim(context, R.dimen.type_list_item_name_text_size);
        itemNameView.setTextSize(itemNameTextSize);

        itemNameView.setTypeface(font);

        itemLayout.addView(itemNameView);


        // Selection Icon
        ImageView selectIcon = new ImageView(context);
        selectIcon.setLayoutParams(Util.linearLayoutParamsWrap());
        int selectIconPaddingTop = (int) Util.getDim(context,
                                             R.dimen.type_list_item_select_icon_padding_top);
        selectIcon.setPadding(0, selectIconPaddingTop, 0, 0);
        selectIcon.setId(R.id.type_list_item_icon);
        selectIcon.setImageDrawable(
                ContextCompat.getDrawable(context, R.drawable.ic_select_item_24dp));

        itemLayout.addView(selectIcon);

        return itemLayout;
    }


}
