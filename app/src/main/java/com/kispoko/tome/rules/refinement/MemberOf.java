
package com.kispoko.tome.rules.refinement;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.util.Util;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.PrimitiveValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;



/**
 * Refinement: MemberOf
 */
public class MemberOf implements Model, Serializable
{

    // PROPERTIES
    // -------------------------------------------------------------------------------------------

    private UUID id;

    private PrimitiveValue<String>   name;
    private PrimitiveValue<String>   label;
    private PrimitiveValue<String[]> values;


    // CONSTRUCTORS
    // -------------------------------------------------------------------------------------------

    public MemberOf()
    {
        this.id     = null;

        this.name   = new PrimitiveValue<>(null, String.class);
        this.label  = new PrimitiveValue<>(null, String.class);

        this.values = new PrimitiveValue<>(null, String[].class);

    }


    public MemberOf(UUID id, String name, String label, List<String> values)
    {
        this.id     = id;

        this.name   = new PrimitiveValue<>(name, String.class);
        this.label  = new PrimitiveValue<>(label, String.class);

        String[] valueArray = values.toArray(new String[values.size()]);
        this.values = new PrimitiveValue<>(valueArray, String[].class);
    }


    public static MemberOf fromYaml(Yaml yaml)
                  throws YamlException
    {
        UUID id = UUID.randomUUID();

        String       name      = yaml.atKey("name").getString();
        String       label     = yaml.atKey("label").getString();
        List<String> valueList = yaml.atKey("values").getStringList();

        return new MemberOf(id, name, label, valueList);
    }


    // API
    // -------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    // ** Id
    // ------------------------------------------------------------------------------------------

    /**
     * Get the model identifier.
     * @return The model UUID.
     */
    public UUID getId()
    {
        return this.id;
    }


    /**
     * Set the model identifier.
     * @param id The new model UUID.
     */
    public void setId(UUID id)
    {
        this.id = id;
    }


    // ** On Load
    // ------------------------------------------------------------------------------------------

    /**
     * This method is called when the MemberOf is completely loaded for the first time.
     */
    public void onLoad() { }


    // > State
    // ------------------------------------------------------------------------------------------

    /**
     * Get the name of the MemberOf refinement.
     * @return The refinement name.
     */
    public String getName()
    {
        return this.name.getValue();
    }


    /**
     * Get the values that the MemberOf refinement restricts over.
     * @return The MemberOf value set.
     */
    public List<String> getValues()
    {
        return Arrays.asList(this.values.getValue());
    }



    // > Views
    // -------------------------------------------------------------------------------------------


    public View getItemView(Context context)
    {
        RelativeLayout layout = new RelativeLayout(context);



        // Layout
        LinearLayout itemLayout = new LinearLayout(context);
        itemLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.sheet_medium));

        RelativeLayout.LayoutParams itemLayoutParams =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                                                RelativeLayout.LayoutParams.WRAP_CONTENT);
        itemLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        itemLayout.setLayoutParams(itemLayoutParams);

        int itemLayoutVertPadding = (int) Util.getDim(context, R.dimen.type_list_item_vert_padding);
        int itemLayoutHorzPadding = (int) Util.getDim(context, R.dimen.type_list_item_padding_horz);
        itemLayout.setPadding(0, itemLayoutVertPadding,
                              0, itemLayoutVertPadding);

        // Status Icon
        ImageView statusIcon = new ImageView(context);
        LinearLayout.LayoutParams statusIconLayoutParams = Util.linearLayoutParamsWrap();
        int statusIconWidth = (int) Util.getDim(context,
                                        R.dimen.type_list_item_select_icon_width);
        //selectIconLayoutParams.setMargins(0, 0, selectIconMarginRight, 0);
        statusIconLayoutParams.width = statusIconWidth;
        statusIconLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        statusIcon.setLayoutParams(statusIconLayoutParams);
        int selectIconPaddingTop = (int) Util.getDim(context,
                                             R.dimen.type_list_item_select_icon_padding_top);
        statusIcon.setPadding(0, selectIconPaddingTop, 0, 0);
        statusIcon.setId(R.id.type_list_item_icon);
//        statusIcon.setImageDrawable(
//                ContextCompat.getDrawable(context, R.drawable.ic_list_item_selected));



        // Item Name
        TextView itemNameView = new TextView(context);
        itemNameView.setId(R.id.type_list_item_name);

        LinearLayout.LayoutParams itemNameLayoutParams = Util.linearLayoutParamsWrap();
        itemNameView.setLayoutParams(itemNameLayoutParams);

        itemNameView.setTextColor(ContextCompat.getColor(context, R.color.text_medium));

        float itemNameTextSize = Util.getDim(context, R.dimen.type_list_item_name_text_size);
        itemNameView.setTextSize(itemNameTextSize);

        itemNameView.setTypeface(Util.serifFontBold(context));


        // Info Button
        ImageView infoButton = new ImageView(context);
        RelativeLayout.LayoutParams infoButtonLayoutParams =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                                                RelativeLayout.LayoutParams.WRAP_CONTENT);
        int infoButtonMarginRight = (int) Util.getDim(context,
                                            R.dimen.type_list_item_info_button_margin_right);
        infoButtonLayoutParams.rightMargin = infoButtonMarginRight;
        infoButtonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        infoButtonLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        infoButton.setLayoutParams(infoButtonLayoutParams);
        infoButton.setImageDrawable(
                ContextCompat.getDrawable(context, R.drawable.ic_list_item_info));


        // Define structure
        itemLayout.addView(statusIcon);
        itemLayout.addView(itemNameView);

        layout.addView(itemLayout);
        layout.addView(infoButton);

        return layout;
    }


}
