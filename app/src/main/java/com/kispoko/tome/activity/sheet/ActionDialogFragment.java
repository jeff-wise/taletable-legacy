
package com.kispoko.tome.activity.sheet;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.kispoko.tome.R;
import com.kispoko.tome.sheet.SheetManager;
import com.kispoko.tome.sheet.widget.Widget;
import com.kispoko.tome.sheet.widget.action.Action;
import com.kispoko.tome.util.ui.LinearLayoutBuilder;



/**
 * Bottom Sheet Fragment for Action Dialog
 */
public class ActionDialogFragment extends DialogFragment
{

    private Widget widget;


    public static final ActionDialogFragment newInstance(Widget widget)
    {
        ActionDialogFragment actionDialogFragment = new ActionDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("COMPONENT", widget);
        actionDialogFragment.setArguments(bundle);
        return actionDialogFragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        this.widget = (Widget) getArguments().getSerializable("COMPONENT");
    }


    @Override
    public View onCreateView(LayoutInflater layoutInflater,
                             ViewGroup container,
                             Bundle savedInstanceState)
    {
        // [1] Setup / Declarations
        // --------------------------------------------------------------------------------------

        Context context = SheetManager.currentSheetContext();

        // [2] Layout
        // --------------------------------------------------------------------------------------

        LinearLayout layout = this.dialogLayout(context);

        for (final Action action : this.widget.data().getActions())
        {
            View actionView = Action.view(action, this.widget, this);
            layout.addView(actionView);
        }


        return layout;
    }


    @Override
    public void onResume()
    {
        super.onResume();

        Context context = SheetManager.currentSheetContext();

        int width = (int) context.getResources().getDimension(R.dimen.action_dialog_width);
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setLayout(width, height);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }

//
//    @Override
//    public void setupDialog(final Dialog dialog, int style)
//    {
//        super.setupDialog(dialog, style);
//
//
//    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    // > Views
    // ------------------------------------------------------------------------------------------

    private LinearLayout dialogLayout(Context context)
    {
        LinearLayoutBuilder layout = new LinearLayoutBuilder();

        layout.height          = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.width           = LinearLayout.LayoutParams.MATCH_PARENT;
        layout.orientation     = LinearLayout.VERTICAL;
        layout.backgroundColor = R.color.dark_grey_8;
        layout.padding.top     = R.dimen.action_dialog_padding_vert;
        layout.padding.bottom  = R.dimen.action_dialog_padding_vert;

        return layout.linearLayout(context);
    }


}
