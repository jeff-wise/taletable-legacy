
package com.taletable.android.activity.sheet.page


import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import com.taletable.android.activity.session.SheetActivityGlobal
import com.taletable.android.model.sheet.page.Page
import com.taletable.android.rts.entity.EntityId



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

    private var pages   : List<Page> = listOf()
    private var sheetId : EntityId?   = null


    // -----------------------------------------------------------------------------------------
    // PAGER ADAPTER
    // -----------------------------------------------------------------------------------------

    override fun getItem(position: Int) : Fragment =
            PageFragment.newInstance(this.pages[position], this.sheetId)


    override fun getCount() : Int = this.pages.size


    override fun getPageTitle(position : Int) : CharSequence =
            this.pages[position].nameString()


    override fun getItemPosition(obj : Any) : Int
    {
        return if (this.pages.contains(obj))
            this.pages.indexOf(obj)
        else
            PagerAdapter.POSITION_NONE
    }


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    fun setPages(pages : List<Page>, sheetId : EntityId)
    {
        this.pages = pages
        this.sheetId = sheetId

        this.notifyDataSetChanged()
    }

}



class PageFragment : Fragment()
{

    var page    : Page?    = null
    var sheetId : EntityId? = null

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun newInstance(page : Page, sheetId : EntityId?) : PageFragment
        {
            val pageFragment = PageFragment()

            val args = Bundle()
            args.putSerializable("page", page)
            args.putSerializable("sheet_id", sheetId)

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
            this.page    = arguments?.getSerializable("page") as Page
            this.sheetId = arguments?.getSerializable("sheet_id") as EntityId
        }
    }


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    {
        val fragmentView = this.view()

        fragmentView.viewTreeObserver.addOnScrollChangedListener {
//            val scrollY = rootScrollView.getScrollY() // For ScrollView
//            val scrollX = rootScrollView.getScrollX() // For HorizontalScrollView
            // DO SOMETHING WITH THE SCROLL COORDINATES
            // Log.d("***PAGE", "on scroll")
            SheetActivityGlobal.cancelLongPressRunnable()
        }

//        fragmentView.setOnTouchListener  { _, motionEvent ->
//            if (motionEvent != null)
//                Log.d("***PAGE", motionEvent.action.toString())
//            false
//        }

        val sheetId = this.sheetId
        val context = this.context

        if (sheetId != null && context != null)
            fragmentView.addView(this.page?.view(sheetId, context))

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

