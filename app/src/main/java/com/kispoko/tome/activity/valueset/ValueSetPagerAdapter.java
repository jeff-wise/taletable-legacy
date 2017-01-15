
package com.kispoko.tome.activity.valueset;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.kispoko.tome.engine.value.ValueSet;



/**
 * Value Set Pager Adapter
 */
public class ValueSetPagerAdapter extends FragmentStatePagerAdapter
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private static final int TABS = 2;

    private ValueSet valueSet;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public ValueSetPagerAdapter(FragmentManager fragmentManager, ValueSet valueset)
    {
        super(fragmentManager);

        this.valueSet = valueset;
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
                return DataFragment.newInstance(valueSet);
            case 1:
                return ValueListFragment.newInstance(valueSet);
            default:
                return null;
        }
    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position)
    {
        switch (position)
        {
            case 0:
                return "Data";
            case 1:
                return "Values";
            default:
                return "Classes";
        }
    }


    @Override
    public int getItemPosition(Object object)
    {
        if (this.valueSet.values().contains(object)) {
            return this.valueSet.values().indexOf(object);
        } else {
            return POSITION_NONE;
        }
    }

}
