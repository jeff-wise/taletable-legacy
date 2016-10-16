
package com.kispoko.tome.sheet;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.kispoko.tome.util.SQL;

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

    private ArrayList<Page> pages;

    // >> STATIC
    private static Map<UUID,AsyncConstructor> asyncConstructorMap = new HashMap<>();

    private static Map<UUID,SaveTracker> trackerMap = new HashMap<>();


    // > CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Roleplay(ArrayList<Page> pages)
    {
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
    public static Roleplay fromYaml(Map<String, Object> roleplayYaml)
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

        return new Roleplay(pages);
    }


    // > API
    // ------------------------------------------------------------------------------------------

    /**
     * Returns the pages in the roleplay section.
     * @return The roleplay pages.
     */
    public List<Page> getPages()
    {
        return this.pages;
    }


    // >> Tracking
    // ------------------------------------------------------------------------------------------

    /**
     * Create a tracker for asynchronously tracking the state of a sheet.
     * @param sheetTrackerId ID of the sheet tracker.
     * @return The new tracker's ID.
     */
    private static UUID addTracker(UUID sheetTrackerId, ArrayList<UUID> pageIds)
    {
        UUID trackerId = UUID.randomUUID();
        Roleplay.trackerMap.put(trackerId, new SaveTracker(sheetTrackerId, pageIds));
        return trackerId;
    }


    public static SaveTracker getTracker(UUID trackerId)
    {
        return Roleplay.trackerMap.get(trackerId);
    }


    // >> Async Constructor
    // ------------------------------------------------------------------------------------------

    private static UUID addAsyncConstructor(Integer numberOfPages, UUID sheetConstructorId)
    {
        UUID constructorId = UUID.randomUUID();
        Roleplay.asyncConstructorMap.put(constructorId, new AsyncConstructor(numberOfPages,
                                                                         sheetConstructorId));
        return constructorId;
    }


    public static AsyncConstructor getAsyncConstructor(UUID constructorId)
    {
        return Roleplay.asyncConstructorMap.get(constructorId);
    }



    // >> I/O Methods
    // ------------------------------------------------------------------------------------------

    public static void load(final SQLiteDatabase database,
                            final UUID sheetConstructorId,
                            final UUID sheetId)
    {
        new AsyncTask<Void,Void,ArrayList<UUID>>()
        {

            @Override
            protected ArrayList<UUID> doInBackground(Void... args)
            {
                String pagesOfSheetQuery =
                    "SELECT page_id " +
                    "FROM Page " +
                    "WHERE Page.sheet_id = " + SQL.quoted(sheetId.toString());

                Cursor cursor = database.rawQuery(pagesOfSheetQuery, null);

                ArrayList<UUID> pageIds = new ArrayList<>();
                try {
                    while (cursor.moveToNext()) {
                        pageIds.add(UUID.fromString(cursor.getString(0)));
                    }
                }
                // TODO log
                finally {
                    cursor.close();
                }

                return pageIds;
            }

            @Override
            protected void onPostExecute(ArrayList<UUID> pageIds)
            {
                // Load roleplay asynchronously
                UUID roleplayConstructorId = Roleplay.addAsyncConstructor(pageIds.size(),
                                                                          sheetConstructorId);

                for (UUID pageId : pageIds)
                {
                    Page.load(database, roleplayConstructorId, pageId);
                }
            }

        }.execute();
    }


    /**
     * Save to the database.
     * @param database The SQLite database object.
     * @param recursive If true, save all child objects as well.
     */
    public void save(SQLiteDatabase database, UUID sheetId, UUID sheetTrackerId, boolean recursive)
    {
        if (!recursive) return;

        ArrayList<UUID> pageIds = new ArrayList<>();
        for (Page page : pages) {
            pageIds.add(page.getId());
        }
        UUID roleplayTrackerId = Roleplay.addTracker(sheetTrackerId, pageIds);


        // Save all the roleplay data
        for (Page page : pages)
        {
            page.save(database, roleplayTrackerId, sheetId, "roleplay", true);
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
            Sheet.getAsyncConstructor(sheetConstructorId).addRoleplay(roleplay);
        }

    }


    /**
     * Track the state of a Roleplay object. When the state reaches a desired configuration,
     * execute a callback.
     */
    public static class SaveTracker
    {

        // > PROPERTIES
        // --------------------------------------------------------------------------------------

        private Map<UUID,Boolean> pageIdTracker;
        private Integer pageIdsRemaining;

        private UUID sheetTrackerId;


        // > CONSTRUCTORS
        // --------------------------------------------------------------------------------------

        public SaveTracker(UUID sheetTrackerId, ArrayList<UUID> pageIds)
        {
            this.sheetTrackerId = sheetTrackerId;

            pageIdTracker = new HashMap<>();
            for (UUID pageId : pageIds)
            {
                pageIdTracker.put(pageId, false);
            }
            this.pageIdsRemaining = pageIds.size();

            if (pageIds.size() == 0)
                ready();
        }


        // > API
        // --------------------------------------------------------------------------------------

        synchronized public void setPageId(UUID pageId)
        {
            if (pageIdTracker.containsKey(pageId)) {
                boolean currentStatus = pageIdTracker.get(pageId);
                if (!currentStatus) {
                    pageIdTracker.put(pageId, true);
                    pageIdsRemaining -= 1;
                }
            }

            if (isReady()) ready();
        }


        // > INTERNAL
        // --------------------------------------------------------------------------------------

        private boolean isReady()
        {
            return pageIdsRemaining == 0;
        }

        private void ready()
        {
            Log.d("***Roleplay", "roleplay is ready");
            Sheet.getTracker(this.sheetTrackerId).setRoleplay();
        }
    }

}
