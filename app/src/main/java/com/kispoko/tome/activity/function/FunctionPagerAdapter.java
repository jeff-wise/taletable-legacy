
package com.kispoko.tome.activity.function;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.kispoko.tome.engine.function.Function;



/**
 * Function Pager Adapter
 */
public class FunctionPagerAdapter extends FragmentStatePagerAdapter
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private static final int TABS = 2;

    private Function function;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public FunctionPagerAdapter(FragmentManager fragmentManager, Function function)
    {
        super(fragmentManager);

        this.function = function;
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
                return DataFragment.newInstance(this.function);
            case 1:
                return TuplesFragment.newInstance(this.function);
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
                return "Mappings";
            default:
                return "Function";
        }
    }

}
