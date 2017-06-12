
package com.kispoko.tome.activity.sheet.page;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.kispoko.tome.lib.ui.ScrollViewBuilder;
import com.kispoko.tome.model.game.engine.Engine;
import com.kispoko.tome.model.sheet.page.Page;



/**
 * Page Fragment
 */
public class PageFragment extends Fragment
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private Page page;
    private Engine rulesEngine;


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
    public static PageFragment newInstance(Page page, Engine rulesEngine)
    {
        PageFragment fragment = new PageFragment();
        Bundle args = new Bundle();
//        args.putSerializable("PAGE", page);
//        args.putSerializable("RULES", rulesEngine);
        fragment.setArguments(args);
        return fragment;
    }


    // API
    // ------------------------------------------------------------------------------------------

}
