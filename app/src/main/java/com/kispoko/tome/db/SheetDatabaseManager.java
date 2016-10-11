
package com.kispoko.tome.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.kispoko.tome.db.SheetContract.ComponentImage.COLUMN_IMAGE_ID_NAME;


/**
 * Sheet Database Manager
 */
public class SheetDatabaseManager extends SQLiteOpenHelper
{


    public SheetDatabaseManager(Context context)
    {
        super(context, SheetContract.DATABASE_NAME, null, SheetContract.DATABASE_VERSION);
    }


    public void onCreate(SQLiteDatabase db)
    {

        // > DEFINE Create Table Queries
        // --------------------------------------------------------------------------------------

        final String CREATE_SHEET_TABLE =
            "CREATE_TABLE IF NOT EXISTS " + SheetContract.Sheet.TABLE_NAME + " (" +
            SheetContract.Sheet.COLUMN_SHEET_ID_NAME + " " + SheetContract.Sheet.COLUMN_SHEET_ID_TYPE + " PRIMARY KEY," +
            SheetContract.Sheet.COLUMN_LAST_USED_NAME + " " + SheetContract.Sheet.COLUMN_LAST_USED_TYPE + ")";

        final String CREATE_PAGE_TABLE =
            "CREATE_TABLE IF NOT EXISTS " + SheetContract.Page.TABLE_NAME + " (" +
            SheetContract.Page.COLUMN_PAGE_ID_NAME + " " + SheetContract.Page.COLUMN_PAGE_ID_TYPE + " PRIMARY KEY," +
            SheetContract.Page.COLUMN_SHEET_ID_NAME + " " + SheetContract.Page.COLUMN_SHEET_ID_TYPE + "," +
            SheetContract.Page.COLUMN_SECTION_ID_NAME + " " + SheetContract.Page.COLUMN_SECTION_ID_TYPE + "," +
            SheetContract.Page.COLUMN_LABEL_NAME + " " + SheetContract.Page.COLUMN_LABEL_TYPE + ")";

        final String CREATE_GROUP_TABLE =
            "CREATE_TABLE IF NOT EXISTS " + SheetContract.Group.TABLE_NAME + " (" +
            SheetContract.Group.COLUMN_GROUP_ID_NAME + " " + SheetContract.Group.COLUMN_GROUP_ID_TYPE + " PRIMARY KEY," +
            SheetContract.Group.COLUMN_PAGE_ID_NAME + " " + SheetContract.Group.COLUMN_PAGE_ID_TYPE + "," +
            SheetContract.Group.COLUMN_LABEL_NAME + " " + SheetContract.Group.COLUMN_LABEL_TYPE + ")";

        final String CREATE_GROUP_LAYOUT_TABLE =
            "CREATE_TABLE IF NOT EXISTS " + SheetContract.GroupLayout.TABLE_NAME + " (" +
            SheetContract.GroupLayout.COLUMN_GROUP_LAYOUT_ID_NAME + " " + SheetContract.GroupLayout.COLUMN_GROUP_LAYOUT_ID_TYPE + " PRIMARY KEY," +
            SheetContract.GroupLayout.COLUMN_GROUP_ID_NAME + " " + SheetContract.GroupLayout.COLUMN_GROUP_ID_TYPE + ")";

        final String CREATE_GROUP_LAYOUT_ROW_TABLE =
            "CREATE_TABLE IF NOT EXISTS " + SheetContract.GroupLayoutRow.TABLE_NAME + " (" +
            SheetContract.GroupLayoutRow.COLUMN_GROUP_LAYOUT_ROW_ID_NAME + " " + SheetContract.GroupLayoutRow.COLUMN_GROUP_LAYOUT_ROW_ID_TYPE + " PRIMARY KEY," +
            SheetContract.GroupLayoutRow.COLUMN_GROUP_LAYOUT_ID_NAME + " " + SheetContract.GroupLayoutRow.COLUMN_GROUP_LAYOUT_ID_TYPE + "," +
            SheetContract.GroupLayoutRow.COLUMN_INDEX_NAME + " " + SheetContract.GroupLayoutRow.COLUMN_INDEX_TYPE + "," +
            SheetContract.GroupLayoutRow.COLUMN_FRAME_1_NAME + " " + SheetContract.GroupLayoutRow.COLUMN_FRAME_1_TYPE + "," +
            SheetContract.GroupLayoutRow.COLUMN_FRAME_2_NAME + " " + SheetContract.GroupLayoutRow.COLUMN_FRAME_2_TYPE + "," +
            SheetContract.GroupLayoutRow.COLUMN_FRAME_3_NAME + " " + SheetContract.GroupLayoutRow.COLUMN_FRAME_3_TYPE + ")";

        final String CREATE_GROUP_LAYOUT_FRAME_TABLE =
            "CREATE_TABLE IF NOT EXISTS " + SheetContract.GroupLayoutFrame.TABLE_NAME + " (" +
            SheetContract.GroupLayoutFrame.COLUMN_GROUP_LAYOUT_FRAME_ID_NAME + " " + SheetContract.GroupLayoutFrame.COLUMN_GROUP_LAYOUT_FRAME_ID_TYPE + " PRIMARY KEY," +
            SheetContract.GroupLayoutFrame.COLUMN_COMPONENT_ID_NAME + " " + SheetContract.GroupLayoutFrame.COLUMN_COMPONENT_ID_TYPE + "," +
            SheetContract.GroupLayoutFrame.COLUMN_SIZE_NAME + " " + SheetContract.GroupLayoutFrame.COLUMN_SIZE_TYPE + ")";

        final String CREATE_COMPONENT_TABLE =
            "CREATE_TABLE IF NOT EXISTS " + SheetContract.Component.TABLE_NAME + " (" +
            SheetContract.Component.COLUMN_COMPONENT_ID_NAME + " " + SheetContract.Component.COLUMN_COMPONENT_ID_TYPE + " PRIMARY KEY," +
            SheetContract.Component.COLUMN_GROUP_ID_NAME + " " + SheetContract.Component.COLUMN_GROUP_ID_TYPE + "," +
            SheetContract.Component.COLUMN_DATA_TYPE_NAME + " " + SheetContract.Component.COLUMN_DATA_TYPE_TYPE + "," +
            SheetContract.Component.COLUMN_LABEL_NAME + " " + SheetContract.Component.COLUMN_LABEL_TYPE + "," +
            SheetContract.Component.COLUMN_TYPE_KIND_NAME + " " + SheetContract.Component.COLUMN_TYPE_KIND_TYPE + "," +
            SheetContract.Component.COLUMN_TYPE_ID_NAME + " " + SheetContract.Component.COLUMN_TYPE_ID_TYPE + ")";

        final String CREATE_COMPONENT_TEXT_TABLE =
            "CREATE_TABLE IF NOT EXISTS " + SheetContract.ComponentText.TABLE_NAME + " (" +
            SheetContract.ComponentText.COLUMN_TEXT_ID_NAME + " " + SheetContract.ComponentText.COLUMN_TEXT_ID_TYPE + " PRIMARY KEY," +
            SheetContract.ComponentText.COLUMN_COMPONENT_ID_NAME + " " + SheetContract.ComponentText.COLUMN_COMPONENT_ID_TYPE + "," +
            SheetContract.ComponentText.COLUMN_SIZE_NAME + " " + SheetContract.ComponentText.COLUMN_SIZE_TYPE + "," +
            SheetContract.ComponentText.COLUMN_VALUE_NAME + " " + SheetContract.ComponentText.COLUMN_VALUE_TYPE + ")";

        final String CREATE_COMPONENT_INTEGER_TABLE =
            "CREATE_TABLE IF NOT EXISTS " + SheetContract.ComponentInteger.TABLE_NAME + " (" +
            SheetContract.ComponentInteger.COLUMN_INTEGER_ID_NAME + " " + SheetContract.ComponentInteger.COLUMN_INTEGER_ID_TYPE + " PRIMARY KEY," +
            SheetContract.ComponentInteger.COLUMN_COMPONENT_ID_NAME + " " + SheetContract.ComponentInteger.COLUMN_COMPONENT_ID_TYPE + "," +
            SheetContract.ComponentInteger.COLUMN_PREFIX_NAME + " " + SheetContract.ComponentInteger.COLUMN_PREFIX_TYPE + "," +
            SheetContract.ComponentInteger.COLUMN_VALUE_NAME + " " + SheetContract.ComponentInteger.COLUMN_VALUE_TYPE + ")";

        final String CREATE_COMPONENT_TABLE_TABLE =
            "CREATE_TABLE IF NOT EXISTS " + SheetContract.ComponentTable.TABLE_NAME + " (" +
            SheetContract.ComponentTable.COLUMN_TABLE_ID_NAME + " " + SheetContract.ComponentTable.COLUMN_TABLE_ID_TYPE + " PRIMARY KEY," +
            SheetContract.ComponentTable.COLUMN_COMPONENT_ID_NAME + " " + SheetContract.ComponentTable.COLUMN_COMPONENT_ID_TYPE + "," +
            SheetContract.ComponentTable.COLUMN_ROW_NAME + " " + SheetContract.ComponentTable.COLUMN_ROW_TYPE + "," +
            SheetContract.ComponentTable.COLUMN_COLUMN_1_NAME + " " + SheetContract.ComponentTable.COLUMN_COLUMN_1_TYPE + "," +
            SheetContract.ComponentTable.COLUMN_COLUMN_2_NAME + " " + SheetContract.ComponentTable.COLUMN_COLUMN_2_TYPE + "," +
            SheetContract.ComponentTable.COLUMN_COLUMN_3_NAME + " " + SheetContract.ComponentTable.COLUMN_COLUMN_3_TYPE + "," +
            SheetContract.ComponentTable.COLUMN_COLUMN_4_NAME + " " + SheetContract.ComponentTable.COLUMN_COLUMN_4_TYPE + "," +
            SheetContract.ComponentTable.COLUMN_COLUMN_5_NAME + " " + SheetContract.ComponentTable.COLUMN_COLUMN_5_TYPE + "," +
            SheetContract.ComponentTable.COLUMN_COLUMN_6_NAME + " " + SheetContract.ComponentTable.COLUMN_COLUMN_6_TYPE + ")";

        final String CREATE_COMPONENT_IMAGE_TABLE =
            "CREATE_TABLE IF NOT EXISTS " + SheetContract.ComponentImage.TABLE_NAME + " (" +
            SheetContract.ComponentImage.COLUMN_IMAGE_ID_NAME + " " + SheetContract.ComponentImage.COLUMN_IMAGE_ID_TYPE + " PRIMARY KEY," +
            SheetContract.ComponentImage.COLUMN_COMPONENT_ID_NAME + " " + SheetContract.ComponentImage.COLUMN_COMPONENT_ID_TYPE + "," +
            SheetContract.ComponentImage.COLUMN_IMAGE_NAME + " " + SheetContract.ComponentImage.COLUMN_IMAGE_TYPE + ")";

        final String CREATE_TYPE_LIST_TABLE =
            "CREATE_TABLE IF NOT EXISTS " + SheetContract.TypeList.TABLE_NAME + " (" +
            SheetContract.TypeList.COLUMN_LIST_ID_NAME + " " + SheetContract.TypeList.COLUMN_LIST_ID_TYPE + " PRIMARY KEY," +
            SheetContract.TypeList.COLUMN_ITEM_NAME + " " + SheetContract.TypeList.COLUMN_ITEM_TYPE + "," +
            SheetContract.TypeList.COLUMN_VALUE_NAME + " " + SheetContract.TypeList.COLUMN_VALUE_TYPE + ")";


        // > RUN Create Table Queries
        // --------------------------------------------------------------------------------------
        db.execSQL(CREATE_SHEET_TABLE);
        db.execSQL(CREATE_PAGE_TABLE);
        db.execSQL(CREATE_GROUP_TABLE);
        db.execSQL(CREATE_GROUP_LAYOUT_TABLE);
        db.execSQL(CREATE_GROUP_LAYOUT_ROW_TABLE);
        db.execSQL(CREATE_GROUP_LAYOUT_FRAME_TABLE);
        db.execSQL(CREATE_COMPONENT_TABLE);
        db.execSQL(CREATE_COMPONENT_TEXT_TABLE);
        db.execSQL(CREATE_COMPONENT_INTEGER_TABLE);
        db.execSQL(CREATE_COMPONENT_TABLE_TABLE);
        db.execSQL(CREATE_COMPONENT_IMAGE_TABLE);
        db.execSQL(CREATE_TYPE_LIST_TABLE);
    }


    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        onCreate(db);
    }


}
