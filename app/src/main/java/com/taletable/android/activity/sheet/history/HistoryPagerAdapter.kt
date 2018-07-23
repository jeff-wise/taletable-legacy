
package com.taletable.android.activity.sheet.history


import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter



data class HistoryPagerAdapter(val fragmentManager : FragmentManager)
                                : FragmentStatePagerAdapter(fragmentManager)
{

    // -----------------------------------------------------------------------------------------
    // PAGER ADAPTER
    // -----------------------------------------------------------------------------------------

    override fun getItem(position: Int) : Fragment =
            HistoryFragment.newInstance()


    override fun getCount() : Int = 3


    override fun getPageTitle(position : Int) : CharSequence = when (position)
    {
        0    -> "EVENTS"
        1    -> "CHOICES"
        2    -> "STATS"
        else -> "STATS"
    }

//
//    override fun getItemPosition(obj : Any?) : Int
//    {
//        return if (this.pages.contains(obj))
//            this.pages.indexOf(obj)
//        else
//            T.POSITION_NONE
//    }


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

//    fun setPages(pages : List<Page>, sheetId : SheetId)
//    {
//        this.pages = pages
//        this.sheetId = sheetId
//
//        this.notifyDataSetChanged()
//    }

}

