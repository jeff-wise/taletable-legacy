
package com.kispoko.tome.sheet;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.kispoko.tome.activity.SheetActivity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

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

    public static Map<Integer,AsyncConstructor> asyncConstructorMap = new HashMap<>();


    // > CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Roleplay(ArrayList<Page> pages)
    {
        this.pages = pages;
    }


    @SuppressWarnings("unchecked")
    public static Roleplay fromYaml(Map<String, Object> roleplayYaml)
    {
        // Roleplay pages
        ArrayList<Map<String,Object>> pagesYaml =
                (ArrayList<Map<String,Object>>) roleplayYaml.get("pages");
        ArrayList<Page> pages = new ArrayList<>();

        for (Map<String,Object> pageYaml : pagesYaml)
        {
            Page page = Page.fromYaml(pageYaml);
            pages.add(page);
        }

        return new Roleplay(pages);
    }


    public static Integer fromAysnc(Integer sheetConsId, int numberOfPages)
    {
        Random randGen = new Random();
        Integer constructorId = randGen.nextInt();

        Roleplay.asyncConstructorMap.put(constructorId,
                                         new AsyncConstructor(numberOfPages, sheetConsId));

        return constructorId;
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



    public static void load(final SQLiteDatabase database,
                            final int sheetConstructorId,
                            final int sheetId)
    {
        new AsyncTask<Void,Void,ArrayList<Integer>>()
        {

            protected ArrayList<Integer> doInBackground(Void... args)
            {
                String pagesOfSheetQuery =
                    "SELECT page_id " +
                    "FROM Page " +
                    "WHERE Page.sheet_id = " + Integer.toString(sheetId);

                Cursor cursor = database.rawQuery(pagesOfSheetQuery, null);

                ArrayList<Integer> pageIds = new ArrayList<Integer>();
                try {
                    while (cursor.moveToNext()) {
                        pageIds.add(cursor.getInt(0));
                    }
                }
                // TODO log
                finally {
                    cursor.close();
                }

                return pageIds;
            }

            protected void onPostExecute(ArrayList<Integer> pageIds)
            {
                Integer roleplayConstructorId = Roleplay.fromAysnc(sheetConstructorId,
                                                                   pageIds.size());

                for (Integer pageId : pageIds)
                {
                    Page.load(database, roleplayConstructorId, pageId);
                }
            }

        }.execute();
    }


    // > NESTED CLASSES
    // ------------------------------------------------------------------------------------------

    public static class AsyncConstructor
    {
        private Integer sheetConsId;
        private int numberOfPages;
        private ArrayList<Page> pages;

        public AsyncConstructor(int numberOfPages, Integer sheetConsId)
        {
            this.numberOfPages = numberOfPages;
            this.sheetConsId = sheetConsId;

            pages = new ArrayList<>();
        }

        synchronized public void addPage(Page page)
        {
            this.pages.add(page);

            if (pages.size() == numberOfPages) ready();
        }

        private void ready()
        {
            Roleplay roleplay = new Roleplay(pages);
            Sheet.asyncConstructorMap.get(sheetConsId).addRoleplay(roleplay);
        }

    }

}
