
package com.kispoko.tome.type;


import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.component.Text;
import com.kispoko.tome.util.UI;

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

    public int size()
    {
        return this.values.size();
    }

    public String getValue(int position)
    {
        // TODO verify
        return this.values.get(position);
    }


    // >> Views
    // -------------------------------------------------------------------------------------------


    public View getItemView(Context context)
    {
        LinearLayout itemLayout = new LinearLayout(context);
        itemLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.grey_300));

        LinearLayout.LayoutParams itemLayoutParams =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                              LinearLayout.LayoutParams.WRAP_CONTENT);
        itemLayout.setLayoutParams(itemLayoutParams);

        Typeface font = Typeface.createFromAsset(context.getAssets(),
                "fonts/DavidLibre-Bold.ttf");

        int itemLayoutVertPadding = (int) UI.getDim(context, R.dimen.type_list_item_vert_padding);
        int itemLayoutLeftPadding = (int) UI.getDim(context, R.dimen.type_list_item_left_padding);
        itemLayout.setPadding(itemLayoutLeftPadding, itemLayoutVertPadding,
                              0, itemLayoutVertPadding);

        TextView itemNameView = new TextView(context);
        itemNameView.setId(R.id.type_list_item_name);
        itemNameView.setTextColor(ContextCompat.getColor(context, R.color.text_medium));
        float itemNameTextSize = UI.getDim(context, R.dimen.type_list_item_name_text_size);
        itemNameView.setTextSize(itemNameTextSize);
        itemNameView.setTypeface(font);

        itemLayout.addView(itemNameView);

        return itemLayout;
    }


}
