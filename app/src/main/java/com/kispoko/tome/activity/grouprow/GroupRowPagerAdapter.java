
package com.kispoko.tome.activity.grouprow;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.kispoko.tome.sheet.group.GroupRow;



/**
 * Group Row Pager Adapter
 */
public class GroupRowPagerAdapter extends FragmentStatePagerAdapter
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private static final int TABS = 2;

    private GroupRow groupRow;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public GroupRowPagerAdapter(FragmentManager fragmentManager, GroupRow groupRow)
    {
        super(fragmentManager);

        this.groupRow = groupRow;
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
                return DataFragment.newInstance(this.groupRow);
            case 1:
                return WidgetListFragment.newInstance(this.groupRow);
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
                return "Widgets";
            default:
                return "Row";
        }
    }


}
