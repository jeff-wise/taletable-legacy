
package com.kispoko.tome.activity.sheet.group;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.kispoko.tome.sheet.group.Group;



/**
 * Group Pager Adapter
 */
public class GroupPagerAdapter extends FragmentStatePagerAdapter
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private static final int TABS = 2;

    private Group group;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public GroupPagerAdapter(FragmentManager fragmentManager, Group group)
    {
        super(fragmentManager);

        this.group = group;
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
                return DataFragment.newInstance(this.group);
            case 1:
                return GroupRowListFragment.newInstance(this.group);
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
