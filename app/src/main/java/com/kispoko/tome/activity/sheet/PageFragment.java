
package com.kispoko.tome.activity.sheet;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.kispoko.tome.engine.RulesEngine;
import com.kispoko.tome.lib.ui.ScrollViewBuilder;
import com.kispoko.tome.sheet.Page;



/**
 * Page Fragment
 */
public class PageFragment extends Fragment
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private Page page;
    private RulesEngine rulesEngine;


    // CONSTRUCTORS
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
    public static PageFragment newInstance(Page page, RulesEngine rulesEngine)
    {
        PageFragment fragment = new PageFragment();
        Bundle args = new Bundle();
        args.putSerializable("PAGE", page);
        args.putSerializable("RULES", rulesEngine);
        fragment.setArguments(args);
        return fragment;
    }


    // API
    // ------------------------------------------------------------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            page = (Page) getArguments().getSerializable("PAGE");
            rulesEngine = (RulesEngine) getArguments().getSerializable("RULES");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        ScrollView fragmentView = this.scrollView(getContext());

        View pageView = this.page.view();
        fragmentView.addView(pageView);

        return fragmentView;
    }


    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
    }


    @Override
    public void onDetach()
    {
        super.onDetach();
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    private ScrollView scrollView(Context context)
    {
        ScrollViewBuilder scrollView = new ScrollViewBuilder();

        scrollView.width        = LinearLayout.LayoutParams.MATCH_PARENT;
        scrollView.height       = LinearLayout.LayoutParams.MATCH_PARENT;

        return scrollView.scrollView(context);
    }

}
