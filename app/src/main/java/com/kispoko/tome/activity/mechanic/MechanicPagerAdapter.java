
package com.kispoko.tome.activity.mechanic;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.kispoko.tome.activity.program.StatementListFragment;
import com.kispoko.tome.activity.variable.VariableListFragment;
import com.kispoko.tome.engine.programming.mechanic.Mechanic;
import com.kispoko.tome.engine.programming.program.Program;

import static com.kispoko.tome.activity.mechanic.DataFragment.newInstance;


/**
 * Mechanic Pager Adapter
 */
public class MechanicPagerAdapter extends FragmentStatePagerAdapter
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private static final int TABS = 2;

    private Mechanic mechanic;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public MechanicPagerAdapter(FragmentManager fragmentManager, Mechanic mechanic)
    {
        super(fragmentManager);

        this.mechanic = mechanic;
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
                return newInstance(this.mechanic);
            case 1:
                return VariableListFragment.newInstance(this.mechanic);
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
                return "Variables";
            default:
                return "Mechanic";
        }
    }


}
