
package com.kispoko.tome.activity.program;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.kispoko.tome.engine.programming.program.Program;



/**
 * Program Pager Adatper
 */
public class ProgramPagerAdapter extends FragmentStatePagerAdapter
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private static final int TABS = 2;

    private Program program;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public ProgramPagerAdapter(FragmentManager fragmentManager, Program program)
    {
        super(fragmentManager);

        this.program = program;
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
                return DataFragment.newInstance(this.program);
            case 1:
                return StatementListFragment.newInstance(this.program);
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
                return "Statements";
            default:
                return "Program";
        }
    }


}
