
package com.kispoko.tome.activity.sheet.page;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.kispoko.tome.model.sheet.page.Page;

import java.util.List;



/**
 * This class manages the roleplay pages by assigning a numeric index to each
 * roleplay page Fragment.
 */
public class PagePagerAdapter extends FragmentStatePagerAdapter
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private List<Page> pages;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public PagePagerAdapter(FragmentManager fragmentManager, List<Page> pages)
    {
        super(fragmentManager);

        this.pages = pages;
    }


    // API
    // ------------------------------------------------------------------------------------------

    // > Pages
    // ------------------------------------------------------------------------------------------

    public void setPages(List<Page> pages)
    {
        this.pages = pages;
    }


    // > Pager Adapter Methods
    // ------------------------------------------------------------------------------------------

    // Returns total number of pages
    @Override
    public int getCount()
    {
        return this.pages.size();
    }


    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int position)
    {
//        return PageFragment.newInstance(this.pages.get(position),
//                                        SheetManagerOld.currentSheet().engine());
        return null;
    }


    @Override
    public CharSequence getPageTitle(int position)
    {
//        String pageLabel = this.pages.get(position).name();
//        if (pageLabel != null)
//            return pageLabel;
//        else
            return "";
    }


    @Override
    public int getItemPosition(Object object) {
        if (this.pages.contains(object)) {
            return this.pages.indexOf(object);
        } else {
            return POSITION_NONE;
        }
    }

}
