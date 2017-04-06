
package com.kispoko.tome.activity.sheet.group;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.kispoko.tome.R;
import com.kispoko.tome.sheet.group.Group;
import com.kispoko.tome.lib.ui.LinearLayoutBuilder;



/**
 * Group Data Fragment
 */
public class DataFragment extends Fragment
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private Group group;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public static DataFragment newInstance(Group group)
    {
        DataFragment dataFragment = new DataFragment();

        Bundle args = new Bundle();
        args.putSerializable("group", group);
        dataFragment.setArguments(args);

        return dataFragment;
    }


    // FRAGMENT API
    // ------------------------------------------------------------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.group = (Group) getArguments().getSerializable("group");
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

    private View view()
    {
        LinearLayout layout = viewLayout(getContext());

        // [1] Define Fields
        // -------------------------------------------------------------------------------------

        // > Name Field
//        LinearLayout nameField = Form.field(
//                    R.string.group_field_name_label,
//                    R.string.group_field_name_description,
//                    Form.textInput(this.group.name(), null, getContext()),
//                    getContext());

        // [2] Add Fields
        // -------------------------------------------------------------------------------------

        // layout.addView(nameField);

        return layout;
    }


    private LinearLayout viewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.VERTICAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.padding.left         = R.dimen.group_data_padding_horz;
        layout.padding.right        = R.dimen.group_data_padding_horz;
        layout.padding.top          = R.dimen.group_data_padding_vert;
        layout.padding.bottom       = R.dimen.group_data_padding_vert;

        return layout.linearLayout(context);
    }


}
