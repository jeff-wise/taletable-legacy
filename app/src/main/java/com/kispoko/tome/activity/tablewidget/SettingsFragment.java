package com.kispoko.tome.activity.tablewidget;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.kispoko.tome.R;
import com.kispoko.tome.sheet.widget.TableWidget;
import com.kispoko.tome.util.ui.Form;
import com.kispoko.tome.util.ui.LinearLayoutBuilder;



/**
 * Table Widget Settings Fragment
 */
public class SettingsFragment extends Fragment
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private TableWidget tableWidget;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public static SettingsFragment newInstance(TableWidget tableWidget)
    {
        SettingsFragment settingsFragment = new SettingsFragment();

        Bundle args = new Bundle();
        args.putSerializable("table_widget", tableWidget);
        settingsFragment.setArguments(args);

        return settingsFragment;
    }


    // FRAGMENT API
    // ------------------------------------------------------------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.tableWidget = (TableWidget) getArguments().getSerializable("table_widget");
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
        // -------------------------------------------------------------------------------------

        String name = this.tableWidget.data().format().label();

        LinearLayout nameField =
                Form.field(
                    R.string.boolean_widget_field_name_label,
                    R.string.boolean_widget_field_name_description,
                    Form.textInput(name, getContext()),
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

        layout.padding.left         = R.dimen.form_padding_horz;
        layout.padding.right        = R.dimen.form_padding_horz;
        layout.padding.top          = R.dimen.form_padding_vert;
        layout.padding.bottom       = R.dimen.form_padding_vert;

        return layout.linearLayout(context);
    }



}
