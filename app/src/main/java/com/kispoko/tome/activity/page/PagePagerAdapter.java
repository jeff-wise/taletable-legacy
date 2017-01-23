
package com.kispoko.tome.activity.page;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.kispoko.tome.activity.function.TuplesFragment;
import com.kispoko.tome.sheet.Page;



/**
 * Pager Pager Adapter
 */
public class PagePagerAdapter extends FragmentStatePagerAdapter
{


    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private static final int TABS = 2;

    private Page page;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public PagePagerAdapter(FragmentManager fragmentManager, Page page)
    {
        super(fragmentManager);

        this.page = page;
    }


    // PAGER ADAPTER API
    // ------------------------------------------------------------------------------------------

    @Override
    public int getCount()
    {
        return TABS;
    }


    @Override
    public Fragment getItem(int position)
    {
        switch (position)
        {
            case 0:
                return DataFragment.newInstance(this.page);
            case 1:
                return GroupListFragment.newInstance(this.page);
            default:
                return null;
        }
    }


    @Override
    public CharSequence getPageTitle(int position)
    {
        switch (position)
        {
            case 0:
                return "Data";
            case 1:
                return "Groups";
            default:
                return "Page";
        }
    }


}
