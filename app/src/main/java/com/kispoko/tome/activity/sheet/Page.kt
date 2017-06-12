
package com.kispoko.tome.activity.sheet


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import com.kispoko.tome.lib.ui.ScrollViewBuilder
import com.kispoko.tome.model.sheet.page.Page



/**
 * Page Pager Adapter
 *
 * An adapter that manages displaying the pages of a sheet under a tab view.
 */
class PagePagerAdapter(fragmentManager : FragmentManager,
                       val pages : List<Page>) : FragmentStatePagerAdapter(fragmentManager)
{

    // PAGER ADAPTER
    // -----------------------------------------------------------------------------------------

    override fun getItem(position: Int) : Fragment
    {
    }


    override fun getCount() : Int = this.pages.size


    override fun getPageTitle(position : Int) : CharSequence =
            this.pages[position].name.value.name


    override fun getItemPosition(obj : Any?) : Int
    {
        if (this.pages.contains(obj))
            return this.pages.indexOf(obj)
        else
            return PagerAdapter.POSITION_NONE
    }

}



class PageFragment : Fragment()
{

    var page : Page? = null

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun newInstance(page : Page) : PageFragment
        {
            val pageFragment = PageFragment()

            val args = Bundle()
            args.putSerializable("page", page)

            pageFragment.arguments = args

            return pageFragment
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
            this.page = arguments.getSerializable("page") as Page
        }
    }


    override fun onCreateView(inflater: LayoutInflater?,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    {
        val fragmentView = this.view()
        fragmentView.addView(this.page?.view())
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
