
package com.kispoko.tome.activity.sheet.grouprow;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.kispoko.tome.R;
import com.kispoko.tome.sheet.group.GroupRow;
import com.kispoko.tome.lib.ui.LinearLayoutBuilder;



/**
 * Group Row Data Fragment
 */
public class DataFragment extends Fragment
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private GroupRow groupRow;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public static DataFragment newInstance(GroupRow groupRow)
    {
        DataFragment dataFragment = new DataFragment();

        Bundle args = new Bundle();
        args.putSerializable("group_row", groupRow);
        dataFragment.setArguments(args);

        return dataFragment;
    }


    // FRAGMENT API
    // ------------------------------------------------------------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.groupRow = (GroupRow) getArguments().getSerializable("group_row");
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

        // > Alignment Field
//        LinearLayout alignmentField =
//                Form.field(
//                    R.string.group_row_field_alignment_label,
//                    R.string.group_row_field_alignment_description,
//                    Form.variantInput(Alignment.class,
//                                      this.groupRow.format().alignment(),
//                                      getContext()),
//                    getContext());

        // > Width Field
//        LinearLayout widthField =
//                Form.field(
//                        R.string.group_row_field_width_label,
//                        R.string.group_row_field_width_description,
//                        Form.variantInput(RowWidth.class,
//                                          this.groupRow.format().width(),
//                                          getContext()),
//                        getContext());

        // [2] Add Fields
        // -------------------------------------------------------------------------------------

        // layout.addView(alignmentField);
        //layout.addView(widthField);

        return layout;
    }


    private LinearLayout viewLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.orientation          = LinearLayout.VERTICAL;
        layout.width                = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.height               = LinearLayout.LayoutParams.WRAP_CONTENT;

        layout.padding.left         = R.dimen.group_row_data_padding_horz;
        layout.padding.right        = R.dimen.group_row_data_padding_horz;
        layout.padding.top          = R.dimen.group_row_data_padding_vert;
        layout.padding.bottom       = R.dimen.group_row_data_padding_vert;

        return layout.linearLayout(context);
    }


}
