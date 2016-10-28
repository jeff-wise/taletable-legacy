
package com.kispoko.tome.sheet;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.kispoko.tome.Global;
import com.kispoko.tome.util.SQL;
import com.kispoko.tome.util.TrackerId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;



/**
 * Character Sheet Format
 *
 * This class represents the structure and representation of character sheet. Character sheets
 * can therefore be customized for different roleplaying games or even different campaigns.
 */
public class Roleplay
{

    // > PROPERTIES
    // ------------------------------------------------------------------------------------------

    private UUID sheetId;
    private ArrayList<Page> pages;

    // >> STATIC
    //private static Map<UUID,AsyncConstructor> asyncConstructorMap = new HashMap<>();

    private static Map<UUID,AsyncTracker> asyncTrackerMap = new HashMap<>();


    // > CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Roleplay(UUID sheetId)
    {
        this.sheetId = sheetId;
    }


    public Roleplay(UUID sheetId, ArrayList<Page> pages)
    {
        this.sheetId = sheetId;
        this.pages = pages;

        // Make sure pages are sorted
        Collections.sort(this.pages, new Comparator<Page>() {
            @Override
            public int compare(Page page1, Page page2) {
                if (page1.getIndex() > page2.getIndex())
                    return 1;
                if (page1.getIndex() < page2.getIndex())
                    return -1;
                return 0;
            }
        });
    }


    @SuppressWarnings("unchecked")
    public static Roleplay fromYaml(UUID sheetId, Map<String, Object> roleplayYaml)
    {
        // Roleplay pages
        ArrayList<Map<String,Object>> pagesYaml =
                (ArrayList<Map<String,Object>>) roleplayYaml.get("pages");
        ArrayList<Page> pages = new ArrayList<>();

        Integer pageIndex = 0;
        for (Map<String,Object> pageYaml : pagesYaml)
        {
            Page page = Page.fromYaml(pageYaml);

            page.setIndex(pageIndex);

            pages.add(page);

            pageIndex += 1;
        }

        return new Roleplay(sheetId, pages);
    }


    // > API
    // ------------------------------------------------------------------------------------------

    // >> State
    // ------------------------------------------------------------------------------------------

    // >>> Sheet Id
    // ------------------------------------------------------------------------------------------

    public UUID getSheetId() {
        return this.sheetId;
    }


    // >>> Pages
    // ------------------------------------------------------------------------------------------

    /**
     * Returns the pages in the roleplay section.
     * @return The roleplay pages.
     */
    public List<Page> getPages()
    {
        return this.pages;
    }


    public void setPages(ArrayList<Page> pages) {
        this.pages = pages;
    }


    // >> Tracking
    // ------------------------------------------------------------------------------------------

    /**
     * Create a tracker for asynchronously tracking the state of a sheet.
     * @param sheetTrackerId ID of the sheet tracker.
     * @return The new tracker's ID.
     */
    private TrackerId addAsyncTracker(TrackerId sheetTrackerId)
    {
        UUID trackerCode = UUID.randomUUID();
        Roleplay.asyncTrackerMap.put(trackerCode, new AsyncTracker(this, sheetTrackerId));
        return new TrackerId(trackerCode, TrackerId.Target.ROLEPLAY);
    }


    public static AsyncTracker getAsyncTracker(UUID trackerCode)
    {
        return Roleplay.asyncTrackerMap.get(trackerCode);
    }


    // >> Async Constructor
    // ------------------------------------------------------------------------------------------

//    private static UUID addAsyncConstructor(Integer numberOfPages, UUID sheetConstructorId)
//    {
//        UUID constructorId = UUID.randomUUID();
//        Roleplay.asyncConstructorMap.put(constructorId, new AsyncConstructor(numberOfPages,
//                                                                         sheetConstructorId));
//        return constructorId;
//    }
//
//
//    public static AsyncConstructor getAsyncConstructor(UUID constructorId)
//    {
//        return Roleplay.asyncConstructorMap.get(constructorId);
//    }


    // >> I/O Methods
    // ------------------------------------------------------------------------------------------

    public void load(final TrackerId sheetTrackerId)
    {
        final Roleplay thisRoleplay = this;

        new AsyncTask<Void,Void,Boolean>()
        {

            @Override
            protected Boolean doInBackground(Void... args)
            {
                SQLiteDatabase database = Global.getDatabase();

                String pagesOfSheetQuery =
                    "SELECT page_id " +
                    "FROM Page " +
                    "WHERE Page.sheet_id = " + SQL.quoted(thisRoleplay.getSheetId().toString());

                Cursor cursor = database.rawQuery(pagesOfSheetQuery, null);

                ArrayList<Page> pages = new ArrayList<>();
                try {
                    while (cursor.moveToNext()) {
                        UUID pageId = UUID.fromString(cursor.getString(0));
                        pages.add(new Page(pageId));
                    }
                }
                finally {
                    cursor.close();
                }

                thisRoleplay.setPages(pages);

                return true;
            }

            @Override
            protected void onPostExecute(Boolean result)
            {
                // Load roleplay asynchronously
                TrackerId roleplayTrackerId = thisRoleplay.addAsyncTracker(sheetTrackerId);

                for (Page page : thisRoleplay.getPages()) {
                    page.load(roleplayTrackerId);
                }
            }

        }.execute();
    }


    /**
     * Save to the database.
     */
    public void save(TrackerId sheetTrackerId, boolean recursive)
    {
        if (!recursive) return;

        TrackerId roleplayTrackerId = this.addAsyncTracker(sheetTrackerId);

        // Save all the roleplay data
        for (Page page : this.getPages()) {
            page.save(roleplayTrackerId, sheetId, "roleplay", true);
        }
    }


    // > NESTED CLASSES
    // ------------------------------------------------------------------------------------------

    /**
     * Create a page class asynchronously.
     * Define required state of a new page, and allow that state to be filled in by other
     * application components at any time. When the constructor reaches the desired state, then
     * it executes a callback.
     */
    /*
    public static class AsyncConstructor
    {
        private UUID sheetConstructorId;
        private int numberOfPages;
        private ArrayList<Page> pages;

        public AsyncConstructor(int numberOfPages, UUID sheetConstructorId)
        {
            this.sheetConstructorId = sheetConstructorId;

            this.numberOfPages = numberOfPages;
            pages = new ArrayList<>();
        }

        synchronized public void addPage(Page page)
        {
            this.pages.add(page);

            if (pages.size() == numberOfPages) ready();
        }

        private void ready()
        {
            Roleplay roleplay = new Roleplay(this.pages);
            Sheet.getAsyncConstructor(sheetConstructorId).setRoleplay(roleplay);
        }

    }*/


    /**
     * Track the state of a Roleplay object. When the state reaches a desired configuration,
     * execute a callback.
     */
    public static class AsyncTracker
    {

        // > PROPERTIES
        // --------------------------------------------------------------------------------------

        private Roleplay roleplay;
        private TrackerId sheetTrackerId;

        private Map<UUID,Boolean> pageIdTracker;


        // > CONSTRUCTORS
        // --------------------------------------------------------------------------------------

        public AsyncTracker(Roleplay roleplay, TrackerId sheetTrackerId)
        {
            this.roleplay = roleplay;
            this.sheetTrackerId = sheetTrackerId;

            pageIdTracker = new HashMap<>();
            for (Page page : roleplay.getPages()) {
                pageIdTracker.put(page.getId(), false);
            }

            if (roleplay.getPages().size() == 0)  ready();
        }


        // > API
        // --------------------------------------------------------------------------------------

        synchronized public void markPageId(UUID pageId)
        {
            if (pageIdTracker.containsKey(pageId))
                pageIdTracker.put(pageId, true);
            if (isReady()) ready();
        }


        // > INTERNAL
        // --------------------------------------------------------------------------------------

        private boolean isReady()
        {
            for (boolean status : this.pageIdTracker.values()) {
                if (!status) return false;
            }
            return true;
        }

        private void ready()
        {
            Sheet.getAsyncTracker(this.sheetTrackerId.getCode())
                 .markRoleplay();
        }
    }

}
