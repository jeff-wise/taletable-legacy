
package com.kispoko.tome.activity.program;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.kispoko.tome.R;
import com.kispoko.tome.engine.programming.program.Program;
import com.kispoko.tome.util.ui.Form;
import com.kispoko.tome.util.ui.LinearLayoutBuilder;



/**
 * Data Fragment
 */
public class DataFragment extends Fragment
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private Program program;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public static DataFragment newInstance(Program program)
    {
        DataFragment dataFragment = new DataFragment();

        Bundle args = new Bundle();
        args.putSerializable("program", program);
        dataFragment.setArguments(args);

        return dataFragment;
    }


    // FRAGMENT API
    // ------------------------------------------------------------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.program = (Program) getArguments().getSerializable("program");
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
                    R.string.program_field_name_label,
                    R.string.program_field_name_description,
                    Form.textInput(this.program.name(), null, getContext()),
                    getContext());

        // > Label Field
        LinearLayout labelField = Form.field(
                    R.string.program_field_label_label,
                    R.string.program_field_label_description,
                    Form.textInput(this.program.label(), null, getContext()),
                    getContext());

        // > Description Field
        LinearLayout descriptionField =
                Form.field(R.string.program_field_description_label,
                           R.string.program_field_description_description,
                           Form.textInput(this.program.description(), null, getContext()),
                           getContext());

        // [2] Add Fields
        // -------------------------------------------------------------------------------------

        layout.addView(nameField);
        layout.addView(labelField);
        layout.addView(descriptionField);

        return layout;
    }


    private LinearLayout viewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.VERTICAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.padding.left         = R.dimen.program_data_padding_horz;
        layout.padding.right        = R.dimen.program_data_padding_horz;
        layout.padding.top          = R.dimen.program_data_padding_vert;
        layout.padding.bottom       = R.dimen.program_data_padding_vert;

        return layout.linearLayout(context);
    }

}
