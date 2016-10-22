
package com.kispoko.tome.activity.sheet;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.kispoko.tome.activity.SheetActivity;
import com.kispoko.tome.sheet.Component;
import com.kispoko.tome.sheet.Page;
import com.kispoko.tome.sheet.Sheet;
import com.kispoko.tome.util.Util;


/**
 * Page Fragment
 */
public class PageFragment extends Fragment
{

    // > PROPERTIES
    // ------------------------------------------------------------------------------------------

    // Saved property names
    private static final String PAGE_PROPERTY_NAME = "page";

    private Page page;


    // > CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public PageFragment() {
        // Required empty public constructor
    }


    /**
     * Create a new instance of a page fragment, loading serialized state that was saved
     * if the fragment had been destroyed.
     * @param page The sheet page represented by the fragment.
     * @return A new instance of ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PageFragment newInstance(Page page)
    {
        PageFragment fragment = new PageFragment();
        Bundle args = new Bundle();
        args.putSerializable(PAGE_PROPERTY_NAME, page);
        fragment.setArguments(args);
        return fragment;
    }


    // > API
    // ------------------------------------------------------------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            page = (Page) getArguments().getSerializable(PAGE_PROPERTY_NAME);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        ScrollView fragmentView = new ScrollView(getContext());
        fragmentView.setLayoutParams(Util.linearLayoutParamsMatch());

        View pageView = this.page.getView(getContext());
        fragmentView.addView(pageView);

        return fragmentView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof EventListener) {
//            mListener = (EventListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement EventListener");
//        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }


    /**
     * This interface represents the messages that this fragment may send to the activity.
     * The activity must therefore implement the EventListener interface.
     */
    public interface EventListener
    {
        void setChooseImageAction(ChooseImageAction chooseImageAction);
        void openEditActivity(Component component);
    }

}
