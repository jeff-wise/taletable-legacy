
package com.kispoko.tome.activity.sheet.history


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import com.kispoko.tome.activity.sheet.SheetActivity
import com.kispoko.tome.activity.sheet.task.TaskFragment
import com.kispoko.tome.activity.sheet.task.TaskUI
import com.kispoko.tome.model.sheet.SheetId
import com.kispoko.tome.model.theme.official.officialThemeLight
import com.kispoko.tome.rts.entity.EntitySheetId



class HistoryFragment : Fragment()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    // Must be used from a sheet activity
    var sheetId : SheetId? = null


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun newInstance() : HistoryFragment
        {
            val taskFragment = HistoryFragment()

//            val args = Bundle()
//
//            taskFragment.arguments = args

            return taskFragment
        }
    }


    // -----------------------------------------------------------------------------------------
    // FRAGMENT
    // -----------------------------------------------------------------------------------------

    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)

//        if (arguments != null) {
//            this.sheetId = arguments.getSerializable("sheet_id") as SheetId
//
//        }
    }


    override fun onCreateView(inflater: LayoutInflater?,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    {
        val fragmentView = this.view()

        return fragmentView
    }


    // -----------------------------------------------------------------------------------------
    // INTERNAL
    // -----------------------------------------------------------------------------------------

    fun view() : ScrollView
    {
        val scrollView = ScrollView(context)

        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                                     LinearLayout.LayoutParams.MATCH_PARENT)

        scrollView.layoutParams = layoutParams

        return scrollView
    }

}