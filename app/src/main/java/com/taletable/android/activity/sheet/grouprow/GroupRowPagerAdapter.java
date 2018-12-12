
package com.taletable.android.activity.sheet.grouprow;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.taletable.android.model.sheet.group.GroupRow;



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
