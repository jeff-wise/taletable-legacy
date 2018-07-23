
package com.taletable.android.activity.sheet.widget.table;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.taletable.android.model.sheet.widget.TableWidget;


/**
 * Table Widget Pager Adapter
 */
public class TableWidgetPagerAdapter extends FragmentStatePagerAdapter
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private static final int TABS = 2;

    private TableWidget tableWidget;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public TableWidgetPagerAdapter(FragmentManager fragmentManager, TableWidget tableWidget)
    {
        super(fragmentManager);

        this.tableWidget = tableWidget;
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
//            case 0:
//                return SettingsFragment.newInstance(this.tableWidget);
//            case 1:
//                return ColumnListFragment.newInstance(this.tableWidget);
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
                return "Settings";
            case 1:
                return "Columns";
            default:
                return "Table Widget";
        }
    }


}
