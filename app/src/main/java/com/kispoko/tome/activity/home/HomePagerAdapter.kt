
package com.kispoko.tome.activity.home


import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter



data class HomePagerAdapter(val fragmentManager : FragmentManager)
                             : FragmentStatePagerAdapter(fragmentManager)
{

    // -----------------------------------------------------------------------------------------
    // PAGER ADAPTER
    // -----------------------------------------------------------------------------------------

    override fun getItem(position: Int) : Fragment = when (position)
    {
        0    -> FeedFragment.newInstance()
        1    -> PlayFragment.newInstance()
        2    -> ShareFragment.newInstance()
        else -> ShareFragment.newInstance()
    }


    override fun getCount() : Int = 3


    override fun getPageTitle(position : Int) : CharSequence = when (position)
    {
        0    -> "HOME"
        1    -> "PLAY"
        2    -> "SHARE"
        else -> "HOME"
    }

}

