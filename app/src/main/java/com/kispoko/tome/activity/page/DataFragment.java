
package com.kispoko.tome.activity.page;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.kispoko.tome.R;
import com.kispoko.tome.sheet.Page;
import com.kispoko.tome.util.ui.Form;
import com.kispoko.tome.util.ui.LinearLayoutBuilder;



/**
 * Page Data Fragment
 */
public class DataFragment extends Fragment
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private Page page;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public static DataFragment newInstance(Page page)
    {
        DataFragment dataFragment = new DataFragment();

        Bundle args = new Bundle();
        args.putSerializable("page", page);
        dataFragment.setArguments(args);

        return dataFragment;
    }


    // FRAGMENT API
    // ------------------------------------------------------------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.page = (Page) getArguments().getSerializable("page");
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
        LinearLayout nameField = Form.field(
                    R.string.function_field_name_label,
                    R.string.function_field_name_description,
                    Form.textInput(this.page.name(), null, getContext()),
                    getContext());

        // [2] Add Fields
        // -------------------------------------------------------------------------------------

        layout.addView(nameField);

        return layout;
    }


    private LinearLayout viewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.VERTICAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.padding.left         = R.dimen.page_data_padding_horz;
        layout.padding.right        = R.dimen.page_data_padding_horz;
        layout.padding.top          = R.dimen.page_data_padding_vert;
        layout.padding.bottom       = R.dimen.page_data_padding_vert;

        return layout.linearLayout(context);
    }


}
