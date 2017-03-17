
package com.kispoko.tome.activity.valueset;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kispoko.tome.R;
import com.kispoko.tome.engine.value.ValueSet;
import com.kispoko.tome.lib.ui.EditTextBuilder;
import com.kispoko.tome.lib.ui.Font;
import com.kispoko.tome.lib.ui.LinearLayoutBuilder;
import com.kispoko.tome.lib.ui.TextViewBuilder;



/**
 * Valuse Set Data Fragment
 */
public class DataFragment extends Fragment
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private ValueSet valueSet;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public static DataFragment newInstance(ValueSet valueSet)
    {
        DataFragment fragmentFirst = new DataFragment();

        Bundle args = new Bundle();
        args.putSerializable("value_set", valueSet);
        fragmentFirst.setArguments(args);

        return fragmentFirst;
    }


    // FRAGMENT API
    // ------------------------------------------------------------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        valueSet = (ValueSet) getArguments().getSerializable("value_set");
    }


    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState)
    {
        return view();
    }


    // VIEWS
    // ------------------------------------------------------------------------------------------

    private LinearLayout view()
    {
        LinearLayout layout = viewLayout();

        // > Name
        layout.addView(nameField(getContext()));

        // > Label
        layout.addView(labelField(getContext()));

        // > Description
        layout.addView(descriptionField(getContext()));

        return layout;
    }


    private LinearLayout viewLayout()
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.VERTICAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.MATCH_PARENT;

        layout.backgroundColor      = R.color.dark_blue_5;

        layout.padding.left         = R.dimen.value_set_data_padding_horz;
        layout.padding.right        = R.dimen.value_set_data_padding_horz;
        layout.padding.top          = R.dimen.value_set_data_padding_vert;
        layout.padding.bottom       = R.dimen.value_set_data_padding_vert;

        return layout.linearLayout(getContext());
    }


    // > Fields
    // ------------------------------------------------------------------------------------------

    private LinearLayout nameField(Context context)
    {
        LinearLayout layout = fieldLayout(context);

        // > Header
        layout.addView(fieldHeaderView(R.string.value_set_field_name_label, context));

        // > Description
        layout.addView(fieldDescriptionView(R.string.value_set_field_name_description, context));

        // > Input
        layout.addView(inputView(this.valueSet.name(), context));

        return layout;
    }


    private LinearLayout labelField(Context context)
    {
        LinearLayout layout = fieldLayout(context);

        // > Header
        layout.addView(fieldHeaderView(R.string.value_set_field_label_label, context));

        // > Description
        layout.addView(fieldDescriptionView(R.string.value_set_field_label_description, context));

        // > Input
        layout.addView(inputView(this.valueSet.label(), context));

        return layout;
    }


    private LinearLayout descriptionField(Context context)
    {
        LinearLayout layout = fieldLayout(context);

        // > Header
        layout.addView(fieldHeaderView(R.string.value_set_field_description_label, context));

        // > Description
        layout.addView(fieldDescriptionView(R.string.value_set_field_description_description,
                                            context));

        // > Input
        layout.addView(inputView(this.valueSet.description(), context));

        return layout;
    }


    private LinearLayout fieldLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.VERTICAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        return layout.linearLayout(context);
    }


    private TextView fieldHeaderView(int headerTextId, Context context)
    {
        TextViewBuilder header = new TextViewBuilder();

        header.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        header.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        header.textId               = headerTextId;
        header.font                 = Font.sansSerifFontBold(context);
        header.color                = R.color.gold_light;
        header.size                 = R.dimen.value_set_data_input_header_text_size;

        header.margin.bottom        = R.dimen.value_set_data_input_header_margin_bottom;

        return header.textView(context);
    }


    private TextView fieldDescriptionView(int descriptionTextId, Context context)
    {
        TextViewBuilder description = new TextViewBuilder();

        description.width               = LinearLayout.LayoutParams.MATCH_PARENT;
        description.height              = LinearLayout.LayoutParams.WRAP_CONTENT;

        description.textId              = descriptionTextId;
        description.font                = Font.sansSerifFontRegular(context);
        description.color               = R.color.dark_blue_hl_8;
        description.size                = R.dimen.value_set_data_input_description_text_size;

        description.margin.bottom       = R.dimen.value_set_data_input_description_margin_bottom;

        return description.textView(context);
    }


    private LinearLayout inputView(String value, Context context)
    {
        // [1] Declarations
        // --------------------------------------------------------------------------------------

        LinearLayoutBuilder layout    = new LinearLayoutBuilder();
        EditTextBuilder     inputText = new EditTextBuilder();

        // [2] Layout
        // --------------------------------------------------------------------------------------

        layout.orientation          = LinearLayout.HORIZONTAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.margin.bottom        = R.dimen.value_set_data_input_margin_bottom;

        layout.child(inputText);

        // [3] Input Text
        // --------------------------------------------------------------------------------------

        inputText.width                 = LinearLayout.LayoutParams.MATCH_PARENT;
        inputText.height                = LinearLayout.LayoutParams.WRAP_CONTENT;

        inputText.font                  = Font.sansSerifFontRegular(context);
        inputText.color                 = R.color.dark_blue_hlx_9;
        inputText.size                  = R.dimen.value_set_data_input_value_text_size;
        inputText.text                  = value;

        inputText.backgroundResource    = R.drawable.bg_edit_text;

        return layout.linearLayout(context);
    }

}


