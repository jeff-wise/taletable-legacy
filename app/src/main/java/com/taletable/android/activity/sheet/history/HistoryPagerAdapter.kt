
package com.taletable.android.activity.sheet.history


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.taletable.android.rts.entity.EntityId



data class HistoryPagerAdapter(val fragmentManager : FragmentManager,
                               val sheetId : EntityId)
                                : FragmentStatePagerAdapter(fragmentManager)
{

    // -----------------------------------------------------------------------------------------
    // PAGER ADAPTER
    // -----------------------------------------------------------------------------------------

    override fun getItem(position: Int) : Fragment = when (position)
    {
        0    -> DecisionsFragment.newInstance(sheetId)
        else -> HistoryFragment.newInstance()

    }


    override fun getCount() : Int = 3


    override fun getPageTitle(position : Int) : CharSequence = when (position)
    {
        0    -> "Decisions"
        1    -> "Events"
        2    -> "Stats"
        else -> "Stats"
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

