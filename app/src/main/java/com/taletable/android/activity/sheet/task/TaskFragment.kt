
package com.taletable.android.activity.sheet.task


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import com.taletable.android.activity.session.SessionActivity
import com.taletable.android.model.theme.official.officialThemeLight
import com.taletable.android.rts.entity.EntityId



class TaskFragment : Fragment()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    // Must be used from a sheet activity
    var sheetId : EntityId? = null

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun newInstance(sheetId : EntityId?) : TaskFragment
        {
            val taskFragment = TaskFragment()

            val args = Bundle()
            args.putSerializable("sheet_id", sheetId)

            taskFragment.arguments = args

            return taskFragment
        }
    }


    // -----------------------------------------------------------------------------------------
    // FRAGMENT
    // -----------------------------------------------------------------------------------------

    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)

        this.sheetId = arguments?.getSerializable("sheet_id") as EntityId
    }


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    {
        val fragmentView = this.view()


        this.sheetId?.let {
            val sheetActivity = context as SessionActivity
            val taskUI = TaskUI(it, officialThemeLight, sheetActivity)
            fragmentView.addView(taskUI.view())
        }

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
