
package com.kispoko.tome.activity.engine;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;



/**
 * Engine Pager Adapter
 */
public class EnginePagerAdapter extends FragmentStatePagerAdapter
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private static final int TABS = 3;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public EnginePagerAdapter(FragmentManager fragmentManager)
    {
        super(fragmentManager);
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
                return StateFragment.newInstance();
            case 1:
                return MechanicsFragment.newInstance();
            case 2:
                return LogFragment.newInstance();
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
                return "State";
            case 1:
                return "Mechanics";
            case 2:
                return "Log";
            default:
                return "Engine";
        }
    }

}
