
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
import com.kispoko.tome.rts.sheet.SheetUIContext
import com.kispoko.tome.rts.sheet.SheetContext



/**
 * Page Pager Adapter
 *
 * An adapter that manages displaying the pages of a sheet under a tab view.
 */
class PagePagerAdapter(fragmentManager : FragmentManager)
                        : FragmentStatePagerAdapter(fragmentManager)
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var pages            : List<Page> = listOf()
    private var sheetContext: SheetContext? = null


    // -----------------------------------------------------------------------------------------
    // PAGER ADAPTER
    // -----------------------------------------------------------------------------------------

    override fun getItem(position: Int) : Fragment =
        PageFragment.newInstance(this.pages[position], this.sheetContext)


    override fun getCount() : Int = this.pages.size


    override fun getPageTitle(position : Int) : CharSequence =
            this.pages[position].nameString()


    override fun getItemPosition(obj : Any?) : Int
    {
        if (this.pages.contains(obj))
            return this.pages.indexOf(obj)
        else
            return PagerAdapter.POSITION_NONE
    }


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    fun setPages(pages : List<Page>, sheetContext: SheetContext)
    {
        this.pages = pages
        this.sheetContext = sheetContext

        this.notifyDataSetChanged()
    }

}



class PageFragment : Fragment()
{

    var page         : Page?         = null
    var sheetContext : SheetContext? = null

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun newInstance(page : Page, sheetContext : SheetContext?) : PageFragment
        {
            val pageFragment = PageFragment()

            val args = Bundle()
            args.putSerializable("page", page)
            args.putSerializable("sheet_context", sheetContext)

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
            this.page         = arguments.getSerializable("page") as Page
            this.sheetContext = arguments.getSerializable("sheet_context") as SheetContext
        }
    }


    override fun onCreateView(inflater: LayoutInflater?,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    {
        val fragmentView = this.view()


        val currentSheetGameContext = this.sheetContext

        if (currentSheetGameContext != null)
        {
            val sheetContext = SheetUIContext(currentSheetGameContext.sheetId,
                                            currentSheetGameContext.campaignId,
                                            currentSheetGameContext.gameId,
                                            context)

            fragmentView.addView(this.page?.view(sheetContext))
        }

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
