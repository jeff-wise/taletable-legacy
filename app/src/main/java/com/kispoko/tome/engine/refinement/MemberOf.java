
package com.kispoko.tome.engine.refinement;


import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.ui.Font;
import com.kispoko.tome.util.ui.ImageViewBuilder;
import com.kispoko.tome.util.ui.LayoutType;
import com.kispoko.tome.util.ui.LinearLayoutBuilder;
import com.kispoko.tome.util.ui.RelativeLayoutBuilder;
import com.kispoko.tome.util.ui.TextViewBuilder;
import com.kispoko.tome.util.value.PrimitiveFunctor;
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

    private PrimitiveFunctor<String> name;
    private PrimitiveFunctor<String> label;
    private PrimitiveFunctor<String[]> values;


    // CONSTRUCTORS
    // -------------------------------------------------------------------------------------------

    public MemberOf()
    {
        this.id     = null;

        this.name   = new PrimitiveFunctor<>(null, String.class);
        this.label  = new PrimitiveFunctor<>(null, String.class);

        this.values = new PrimitiveFunctor<>(null, String[].class);

    }


    public MemberOf(UUID id, String name, String label, List<String> values)
    {
        this.id     = id;

        this.name   = new PrimitiveFunctor<>(name, String.class);
        this.label  = new PrimitiveFunctor<>(label, String.class);

        String[] valueArray = values.toArray(new String[values.size()]);
        this.values = new PrimitiveFunctor<>(valueArray, String[].class);
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
    // ------------------------------------------------------------------------------------------


    public View selectableItemView(Context context)
    {
        // [1] Views
        // --------------------------------------------------------------------------------------

        RelativeLayout  mainLayout    = this.selectableItemLayout(context);

        LinearLayout    innerLayout   = this.selectableItemInnerLayout(context);

        ImageView       statusIcon    = this.selectableItemStatusIcon(context);
        TextView        itemNameView  = this.selectableItemNameView(context);
        ImageView       infoButton    = this.selectableItemInfoButton(context);

        // [2] Structure
        // --------------------------------------------------------------------------------------

        innerLayout.addView(statusIcon);
        innerLayout.addView(itemNameView);

        mainLayout.addView(innerLayout);
        mainLayout.addView(infoButton);

        return mainLayout;
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    private RelativeLayout selectableItemLayout(Context context)
    {
        RelativeLayoutBuilder layout = new RelativeLayoutBuilder();

        layout.width     = RelativeLayout.LayoutParams.MATCH_PARENT;
        layout.height    = RelativeLayout.LayoutParams.WRAP_CONTENT;

        layout.backgroundColor = R.color.dark_grey_7;

        return layout.relativeLayout(context);
    }


    private LinearLayout selectableItemInnerLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.layoutType       = LayoutType.RELATIVE;
        layout.backgroundColor  = R.color.dark_grey_5;
        layout.width            = RelativeLayout.LayoutParams.MATCH_PARENT;
        layout.height           = RelativeLayout.LayoutParams.WRAP_CONTENT;

        layout.padding.top      = R.dimen.ref_member_of_sel_item_padding_vert;
        layout.padding.bottom   = R.dimen.ref_member_of_sel_item_padding_vert;

        layout.margin.left      = R.dimen.ref_member_of_sel_item_layout_margin_horz;
        layout.margin.right     = R.dimen.ref_member_of_sel_item_layout_margin_horz;
        layout.margin.top       = R.dimen.ref_member_of_sel_item_layout_margin_vert;
        layout.margin.bottom    = R.dimen.ref_member_of_sel_item_layout_margin_vert;

        layout.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

        return layout.linearLayout(context);
    }


    private ImageView selectableItemStatusIcon(Context context)
    {
        ImageViewBuilder statusIcon = new ImageViewBuilder();

        statusIcon.id               = R.id.type_list_item_icon;
        statusIcon.width            = R.dimen.ref_member_of_sel_item_sel_icon_width;
        statusIcon.height           = LinearLayout.LayoutParams.WRAP_CONTENT;
        statusIcon.layoutGravity    = Gravity.CENTER_HORIZONTAL;
        statusIcon.padding.top      = R.dimen.ref_member_of_sel_item_sel_icon_padding_top;

        return statusIcon.imageView(context);
    }


    private TextView selectableItemNameView(Context context)
    {
        TextViewBuilder nameView = new TextViewBuilder();

        nameView.id             = R.id.type_list_item_name;
        nameView.width          = LinearLayout.LayoutParams.WRAP_CONTENT;
        nameView.height         = LinearLayout.LayoutParams.WRAP_CONTENT;
        nameView.color          = R.color.light_grey_5;
        nameView.size           = R.dimen.ref_member_of_sel_item_name_text_size;
        nameView.font           = Font.serifFontBold(context);

        return nameView.textView(context);
    }


    private ImageView selectableItemInfoButton(Context context)
    {
        ImageViewBuilder infoButton = new ImageViewBuilder();

        infoButton.layoutType   = LayoutType.RELATIVE;
        infoButton.width        = RelativeLayout.LayoutParams.WRAP_CONTENT;
        infoButton.height       = RelativeLayout.LayoutParams.WRAP_CONTENT;
        infoButton.image        = R.drawable.ic_list_item_info;
        infoButton.margin.right = R.dimen.ref_member_of_sel_item_info_button_margin_right;

        infoButton.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                  .addRule(RelativeLayout.CENTER_VERTICAL);

        return infoButton.imageView(context);
    }



}
