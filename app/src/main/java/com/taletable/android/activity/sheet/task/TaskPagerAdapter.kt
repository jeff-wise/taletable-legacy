
package com.taletable.android.activity.sheet.task


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.taletable.android.rts.entity.EntityId



data class TaskPagerAdapter(val fragmentManager : FragmentManager, val sheetId : EntityId)
                        : FragmentStatePagerAdapter(fragmentManager)
{

    // -----------------------------------------------------------------------------------------
    // PAGER ADAPTER
    // -----------------------------------------------------------------------------------------

    override fun getItem(position: Int) : Fragment =
            TaskFragment.newInstance(sheetId)


    override fun getCount() : Int = 1


    override fun getPageTitle(position : Int) : CharSequence = "Tasks"

//
//    override fun getItemPosition(obj : Any?) : Int
//    {
//        return if (this.pages.contains(obj))
//            this.pages.indexOf(obj)
//        else
//            PagerAdapter.POSITION_NONE
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


