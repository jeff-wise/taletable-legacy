package com.kispoko.tome;

/**
 * Created by jeff on 11/14/16.
 */

public class SheetSummary
{

    /**
     * Stores summary information about a sheet for the user to browse.
     */
    /*
    public static class SummaryInfo implements Serializable
    {
        private String name;
        private Calendar lastUsed;

        private String stat1Name;
        private String stat1Value;
        private String stat2Name;
        private String stat2Value;
        private String stat3Name;
        private String stat3Value;

        public SummaryInfo(String name, Calendar lastUsed, String stat1Name, String stat1Value,
                           String stat2Name, String stat2Value, String stat3Name, String stat3Value)
        {
            this.name = name;
            this.lastUsed = lastUsed;
        }

        public String getName() {
            return this.name;
        }

        public Calendar getLastUsed() {
            return this.lastUsed;
        }

        public String getStat1Name() {
            return this.stat1Name;
        }

        public String getStat2Name() {
            return this.stat2Name;
        }

        public String getStat2Value() {
            return this.stat2Value;
        }

        public String getStat3Name() {
            return this.stat3Name;
        }

        public String getStat3Value() {
            return this.stat3Value;
        }
    }

    */



    /**
     * ModelQuery basic information about the stored sheets.
     * @param database The SQLite database object.
     * @return Array of sheet summary info objects.
     */
    /*
    public static void summaryInfo(final SQLiteDatabase database,
                                   final ManageSheetsActivity manageSheetsActivity)
    {

        final String summaryInfoQuery =
            "SELECT sh.last_used, cname.text_value, cstat1.label, cstat1.text_value, cstat2.label, " +
                   "cstat2.text_value, cstat3.label, cstat3.text_value " +
            "FROM sheet sh " +
            "INNER JOIN page p ON p.sheet_id = sh.sheet_id " +
            "INNER JOIN _group g ON g.page_id = p.page_id " +
            "INNER JOIN component cname ON (cname.group_id = g.group_id and cname.label = 'Name') " +
            "LEFT JOIN component cstat1 ON (cstat1.group_id = g.group_id and cstat1.key_stat = 1) " +
            "LEFT JOIN component cstat2 ON (cstat2.group_id = g.group_id and cstat2.key_stat = 2) " +
            "LEFT JOIN component cstat3 ON (cstat3.group_id = g.group_id and cstat3.key_stat = 3) " +
            "ORDER BY sh.last_used DESC ";


        new AsyncTask<Void,Void,List<SummaryInfo>>()
        {

            @Override
            protected List<SummaryInfo> doInBackground(Void... args)
            {
                Cursor summaryInfoCursor = database.rawQuery(summaryInfoQuery, null);

                ArrayList<SummaryInfo> summaryInfos = new ArrayList<>();
                try {
                    while (summaryInfoCursor.moveToNext())
                    {
                        Calendar lastUsed = Calendar.getInstance();
                        lastUsed.setTimeInMillis(summaryInfoCursor.getLong(0));
                        String name       = summaryInfoCursor.getString(1);
                        String stat1Name  = summaryInfoCursor.getString(2);
                        String stat1Value = summaryInfoCursor.getString(3);
                        String stat2Name  = summaryInfoCursor.getString(4);
                        String stat2Value = summaryInfoCursor.getString(5);
                        String stat3Name  = summaryInfoCursor.getString(6);
                        String stat3Value = summaryInfoCursor.getString(7);

                        summaryInfos.add(new SummaryInfo(name, lastUsed, stat1Name, stat1Value, stat2Name,
                                                         stat2Value, stat3Name, stat3Value));
                    }
                }
                finally {
                    summaryInfoCursor.close();
                }

                return summaryInfos;
            }

            @Override
            protected void onPostExecute(List<SummaryInfo> summaryInfos)
            {
                manageSheetsActivity.renderSheetSummaries(summaryInfos);
            }

        }.execute();

    }

    */



}
