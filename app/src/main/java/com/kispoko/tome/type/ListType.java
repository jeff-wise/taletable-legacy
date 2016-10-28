
package com.kispoko.tome.type;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kispoko.tome.Global;
import com.kispoko.tome.R;
import com.kispoko.tome.db.SheetContract;
import com.kispoko.tome.rules.Types;
import com.kispoko.tome.util.SQL;
import com.kispoko.tome.util.TrackerId;
import com.kispoko.tome.util.Util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;



/**
 * ListType Type
 */
public class ListType extends Type implements Serializable
{

    // > PROPERTIES
    // -------------------------------------------------------------------------------------------

    private UUID sheetId;
    private ArrayList<String> values;


    // > CONSTRUCTORS
    // -------------------------------------------------------------------------------------------

    public ListType(String id, UUID sheetId, ArrayList<String> values)
    {
        super(new Type.Id("list", id));
        this.sheetId = sheetId;
        this.values = values;
    }


    @SuppressWarnings("unchecked")
    public static ListType fromYaml(Map<String,Object> listYaml)
    {
        String id = (String) listYaml.get("id");

        ArrayList<String> valueList = (ArrayList<String>) listYaml.get("values");

        return new ListType(id, null, valueList);
    }


    // > API
    // -------------------------------------------------------------------------------------------

    public int size()
    {
        return this.values.size();
    }


    public String getValue(int position)
    {
        return this.values.get(position);
    }


    public List<String> getValueList()
    {
        return this.values;
    }


    // >> Database
    // -------------------------------------------------------------------------------------------


    public static void loadAll(final TrackerId typesTrackerId, final Types types,
                               final UUID sheetId)
    {
        new AsyncTask<Void,Void,Boolean>()
        {

            protected Boolean doInBackground(Void... args)
            {
                SQLiteDatabase database = Global.getDatabase();

                String listTypesQuery =
                    "SELECT list.list_id, list.value " +
                    "FROM type_list list " +
                    "WHERE list.sheet_id = " + SQL.quoted(sheetId.toString()) + " " +
                    "ORDER BY list.list_id ASC ";

                Cursor cursor = database.rawQuery(listTypesQuery, null);

                ArrayList<ListType> listTypes = new ArrayList<>();
                try {
                    String currentId = null;
                    ArrayList<String> currentValues = new ArrayList<>();
                    String id = null;
                    String value;
                    while (cursor.moveToNext()) {
                        id = cursor.getString(0);
                        value = cursor.getString(1);

                        if (!id.equals(currentId) && currentId != null) {
                            listTypes.add(new ListType(id, sheetId, currentValues));
                            currentValues = new ArrayList<>();
                        }

                        currentId = id;
                        currentValues.add(value);
                    }
                    listTypes.add(new ListType(id, sheetId, currentValues));
                }
                finally {
                    cursor.close();
                }

                for (ListType listType : listTypes) {
                    types.addType(listType);
                }

                return true;
            }

            protected void onPostExecute(Boolean result)
            {
                Types.getAsyncTracker(typesTrackerId.getCode()).markListTypes();
            }

        }.execute();
    }


    public void save(final TrackerId typesTrackerId, final UUID sheetId)
    {
        final ListType thisListType = this;

        new AsyncTask<Void,Void,Boolean>()
        {

            @Override
            protected Boolean doInBackground(Void... args)
            {
                SQLiteDatabase database = Global.getDatabase();

                // Delete current values, before insert new ones, since that's a lot easier than
                // tracking which values were deleted and which were changed.
                String isListIdRow = "list_id = " + SQL.quoted(thisListType.getId().getId());
                database.delete(SheetContract.TypeList.TABLE_NAME, isListIdRow, null);


                for (String value : thisListType.getValueList())
                {
                    ContentValues row = new ContentValues();
                    row.put("list_id", thisListType.getId().getId());
                    row.put("sheet_id", sheetId.toString());
                    row.put("value", value);

                    database.insert(SheetContract.TypeList.TABLE_NAME, null, row);
                }

                return null;
            }

            protected void onPostExecute(Boolean result)
            {
                Types.getAsyncTracker(typesTrackerId.getCode()).markListTypes();
            }

        }.execute();

    }



    // >> Views
    // -------------------------------------------------------------------------------------------


    public View getItemView(Context context)
    {
        RelativeLayout layout = new RelativeLayout(context);



        // Layout
        LinearLayout itemLayout = new LinearLayout(context);
        itemLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.sheet_medium));

        RelativeLayout.LayoutParams itemLayoutParams =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                                                RelativeLayout.LayoutParams.WRAP_CONTENT);
        itemLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        itemLayout.setLayoutParams(itemLayoutParams);

        int itemLayoutVertPadding = (int) Util.getDim(context, R.dimen.type_list_item_vert_padding);
        int itemLayoutHorzPadding = (int) Util.getDim(context, R.dimen.type_list_item_padding_horz);
        itemLayout.setPadding(0, itemLayoutVertPadding,
                              0, itemLayoutVertPadding);

        // Status Icon
        ImageView statusIcon = new ImageView(context);
        LinearLayout.LayoutParams statusIconLayoutParams = Util.linearLayoutParamsWrap();
        int statusIconWidth = (int) Util.getDim(context,
                                        R.dimen.type_list_item_select_icon_width);
        //selectIconLayoutParams.setMargins(0, 0, selectIconMarginRight, 0);
        statusIconLayoutParams.width = statusIconWidth;
        statusIconLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        statusIcon.setLayoutParams(statusIconLayoutParams);
        int selectIconPaddingTop = (int) Util.getDim(context,
                                             R.dimen.type_list_item_select_icon_padding_top);
        statusIcon.setPadding(0, selectIconPaddingTop, 0, 0);
        statusIcon.setId(R.id.type_list_item_icon);
//        statusIcon.setImageDrawable(
//                ContextCompat.getDrawable(context, R.drawable.ic_list_item_selected));



        // Item Name
        TextView itemNameView = new TextView(context);
        itemNameView.setId(R.id.type_list_item_name);

        LinearLayout.LayoutParams itemNameLayoutParams = Util.linearLayoutParamsWrap();
        itemNameView.setLayoutParams(itemNameLayoutParams);

        itemNameView.setTextColor(ContextCompat.getColor(context, R.color.text_medium));

        float itemNameTextSize = Util.getDim(context, R.dimen.type_list_item_name_text_size);
        itemNameView.setTextSize(itemNameTextSize);

        itemNameView.setTypeface(Util.serifFontBold(context));


        // Info Button
        ImageView infoButton = new ImageView(context);
        RelativeLayout.LayoutParams infoButtonLayoutParams =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                                                RelativeLayout.LayoutParams.WRAP_CONTENT);
        int infoButtonMarginRight = (int) Util.getDim(context,
                                            R.dimen.type_list_item_info_button_margin_right);
        infoButtonLayoutParams.rightMargin = infoButtonMarginRight;
        infoButtonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        infoButtonLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        infoButton.setLayoutParams(infoButtonLayoutParams);
        infoButton.setImageDrawable(
                ContextCompat.getDrawable(context, R.drawable.ic_list_item_info));


        // Define structure
        itemLayout.addView(statusIcon);
        itemLayout.addView(itemNameView);

        layout.addView(itemLayout);
        layout.addView(infoButton);

        return layout;
    }


}
