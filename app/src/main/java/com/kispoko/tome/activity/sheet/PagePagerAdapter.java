
package com.kispoko.tome.activity.sheet;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.kispoko.tome.sheet.Sheet;


/**
 * This class manages the roleplay pages by assigning a numeric index to each
 * roleplay page Fragment.
 */
public class PagePagerAdapter extends FragmentStatePagerAdapter
{

    // > PROPERTIES
    // ------------------------------------------------------------------------------------------

    private Sheet sheet;


    // > CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public PagePagerAdapter(FragmentManager fragmentManager, Sheet sheet)
    {
        super(fragmentManager);

        this.sheet = sheet;
    }


    // > API
    // ------------------------------------------------------------------------------------------

    // Returns total number of pages
    @Override
    public int getCount()
    {
        return sheet.getRoleplay().getPages().size();
    }


    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int position)
    {
        return PageFragment.newInstance(this.sheet.getRoleplay().getPages().get(position));
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        return this.sheet.getRoleplay().getPages().get(position).getName();
    }

}
