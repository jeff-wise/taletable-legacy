
package com.kispoko.tome.activity.nav


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kispoko.tome.model.theme.ThemeId



/**
 * Game Navigation Fragment
 */
class GameNavigationFragment : Fragment()
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var themeId : ThemeId? = null


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun newInstance(themeId : ThemeId) : GameNavigationFragment
        {
            val fragment = GameNavigationFragment()

            val args = Bundle()
            args.putSerializable("theme_id", themeId)
            fragment.arguments = args

            return fragment
        }
    }


    // -----------------------------------------------------------------------------------------
    // FRAGMENT
    // -----------------------------------------------------------------------------------------

    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)

        if (arguments != null)
        {
            this.themeId = arguments.getSerializable("theme_id") as ThemeId
        }
    }


    override fun onCreateView(inflater : LayoutInflater?,
                              container : ViewGroup?,
                              savedInstanceState : Bundle?) : View?
    {
//        val themeId = this.themeId
//
//        if (themeId != null)
//            return this.view(themeId, context)
//        else
        return null
    }



}