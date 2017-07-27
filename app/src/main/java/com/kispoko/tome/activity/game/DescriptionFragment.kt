
package com.kispoko.tome.activity.game


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import com.kispoko.tome.lib.ui.ScrollViewBuilder
import com.kispoko.tome.model.game.GameDescription



class DescriptionFragment : Fragment()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var gameDescription : GameDescription? = null

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun newInstance(gameDescription : GameDescription) : DescriptionFragment
        {
            val fragment = DescriptionFragment()

            val args = Bundle()
            args.putSerializable("game_description", gameDescription)

            return fragment
        }
    }


    // -----------------------------------------------------------------------------------------
    // FRAGMENT
    // -----------------------------------------------------------------------------------------

    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)

//        if (arguments != null)
//        {
//            this.page         = arguments.getSerializable("page") as Page
//            this.sheetContext = arguments.getSerializable("sheet_context") as SheetContext
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
        val scrollView = ScrollViewBuilder()

        scrollView.width        = LinearLayout.LayoutParams.MATCH_PARENT
        scrollView.height       = LinearLayout.LayoutParams.MATCH_PARENT

        return scrollView.scrollView(context)
    }

}

