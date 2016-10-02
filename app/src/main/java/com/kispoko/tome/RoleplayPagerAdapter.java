
package com.kispoko.tome;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.kispoko.tome.sheet.Profile;
import com.kispoko.tome.sheet.Sheet;
import com.kispoko.tome.fragment.roleplay.AbilitiesFragment;
import com.kispoko.tome.fragment.roleplay.BackpackFragment;
import com.kispoko.tome.fragment.roleplay.ProfileFragment;
import com.kispoko.tome.fragment.roleplay.StatsFragment;


/**
 * This class manages the roleplay pages by assigning a numeric index to each
 * roleplay page Fragment.
 */
public class RoleplayPagerAdapter extends FragmentStatePagerAdapter
{

    // > PROPERTIES
    // ------------------------------------------------------------------------------------------

    private static int ROLEPLAY_PAGES = 4;

    private Sheet sheet;


    // > CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public RoleplayPagerAdapter(FragmentManager fragmentManager, Sheet sheet)
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
        return ROLEPLAY_PAGES;
    }


    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int position)
    {
        switch (position)
        {
            case 0:
                Profile profile = sheet.getRoleplay().getProfile();
                return ProfileFragment.newInstance(profile);
            case 1:
                return StatsFragment.newInstance();
            case 2:
                return AbilitiesFragment.newInstance();
            case 3:
                return BackpackFragment.newInstance();
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
                return "Profile";
            case 1:
                return "Stats";
            case 2:
                return "Abilities";
            case 3:
                return "Pack";
            default:
                return "Error";
        }
    }

}
