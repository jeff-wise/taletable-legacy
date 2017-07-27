
package com.kispoko.tome.activity.game


import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.kispoko.tome.model.game.Game



/**
 * Game Pager Adapter
 */
class GamePagerAdapter(fragmentManager : FragmentManager, val game : Game)
                        : FragmentStatePagerAdapter(fragmentManager)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTEIS
    // -----------------------------------------------------------------------------------------

    private val pageCount = 2


    // -----------------------------------------------------------------------------------------
    // PAGER ADAPTER
    // -----------------------------------------------------------------------------------------

    override fun getItem(position : Int) : Fragment =
        when (position)
        {
            0    -> DescriptionFragment.newInstance(game.description())
            1    -> EngineFragment.newInstance(game.engine())
            else -> EngineFragment.newInstance(game.engine())
        }


    override fun getCount() : Int = this.pageCount


    override fun getPageTitle(position : Int) : CharSequence =
        when (position)
        {
            0    -> "Description"
            1    -> "Engine"
            else -> "Other"
        }

//
//    override fun getItemPosition(obj : Any?) : Int
//    {
//        if (this.pages.contains(obj))
//            return this.pages.indexOf(obj)
//        else
//            return PagerAdapter.POSITION_NONE
//    }

}


