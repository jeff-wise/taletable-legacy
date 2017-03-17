
package com.kispoko.tome.activity.mechanic;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.kispoko.tome.R;
import com.kispoko.tome.engine.mechanic.Mechanic;
import com.kispoko.tome.lib.ui.Form;
import com.kispoko.tome.lib.ui.LinearLayoutBuilder;
import com.kispoko.tome.lib.ui.ScrollViewBuilder;



/**
 * Mechanic Data Fragment
 */
public class DataFragment extends Fragment
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private Mechanic mechanic;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public static DataFragment newInstance(Mechanic mechanic)
    {
        DataFragment dataFragment = new DataFragment();

        Bundle args = new Bundle();
        args.putSerializable("mechanic", mechanic);
        dataFragment.setArguments(args);

        return dataFragment;
    }


    // FRAGMENT API
    // ------------------------------------------------------------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.mechanic = (Mechanic) getArguments().getSerializable("mechanic");
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
        ScrollView scrollView = dataScrollView(getContext());
        LinearLayout layout = formLayout(getContext());

        scrollView.addView(layout);

        // [1] Define Fields
        // -------------------------------------------------------------------------------------

        // > Name Field
        LinearLayout nameField = Form.field(
                    R.string.mechanic_field_name_label,
                    R.string.mechanic_field_name_description,
                    Form.textInput(this.mechanic.name(), getContext()),
                    getContext());

        // > Label Field
        LinearLayout labelField = Form.field(
                    R.string.mechanic_field_label_label,
                    R.string.mechanic_field_label_description,
                    Form.textInput(this.mechanic.label(), getContext()),
                    getContext());

        // > Type Field
        LinearLayout typeField =
                Form.field(R.string.mechanic_field_type_label,
                           R.string.mechanic_field_type_description,
                           Form.textInput(this.mechanic.type(), getContext()),
                           getContext());

        // > Requirements Field
        LinearLayout reqsField =
                Form.field(R.string.mechanic_field_reqs_label,
                           R.string.mechanic_field_reqs_description,
                           Form.listInput("REQUIREMENT",
                                          this.mechanic.requirements(),
                                          getContext()),
                           getContext());

        // [2] Add Fields
        // -------------------------------------------------------------------------------------

        layout.addView(nameField);
        layout.addView(labelField);
        layout.addView(typeField);
        layout.addView(reqsField);

        return scrollView;
    }


    private LinearLayout formLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.VERTICAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.padding.left         = R.dimen.mechanic_data_padding_horz;
        layout.padding.right        = R.dimen.mechanic_data_padding_horz;
        layout.padding.top          = R.dimen.mechanic_data_padding_vert;
        layout.padding.bottom       = R.dimen.mechanic_data_padding_vert;

        return layout.linearLayout(context);
    }


    private ScrollView dataScrollView(Context context)
    {
        ScrollViewBuilder scrollView = new ScrollViewBuilder();

        scrollView.width    = LinearLayout.LayoutParams.MATCH_PARENT;
        scrollView.height   = LinearLayout.LayoutParams.MATCH_PARENT;

        return scrollView.scrollView(context);
    }


}
