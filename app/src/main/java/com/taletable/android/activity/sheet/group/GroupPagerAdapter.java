
package com.taletable.android.activity.sheet.group;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.taletable.android.model.sheet.group.Group;



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
