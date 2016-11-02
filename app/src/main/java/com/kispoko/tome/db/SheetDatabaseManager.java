
package com.kispoko.tome.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



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
            "CREATE TABLE IF NOT EXISTS " + SheetContract.Sheet.TABLE_NAME + " (" +
            SheetContract.Sheet.COLUMN_SHEET_ID_NAME + " " + SheetContract.Sheet.COLUMN_SHEET_ID_TYPE + " PRIMARY KEY, " +
            SheetContract.Sheet.COLUMN_LAST_USED_NAME + " " + SheetContract.Sheet.COLUMN_LAST_USED_TYPE + "," +
            SheetContract.Sheet.COLUMN_GAME_ID_NAME + " " + SheetContract.Sheet.COLUMN_GAME_ID_TYPE + ")";

        final String CREATE_GAME_TABLE =
            "CREATE TABLE IF NOT EXISTS " + SheetContract.Game.TABLE_NAME + " (" +
            SheetContract.Game.COLUMN_GAME_ID_NAME + " " + SheetContract.Game.COLUMN_GAME_ID_TYPE + " PRIMARY KEY, " +
            SheetContract.Game.COLUMN_LABEL_NAME + " " + SheetContract.Game.COLUMN_LABEL_TYPE + "," +
            SheetContract.Game.COLUMN_DESCRIPTION_NAME + " " + SheetContract.Game.COLUMN_DESCRIPTION_TYPE + ")";

        final String CREATE_PAGE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + SheetContract.Page.TABLE_NAME + " (" +
            SheetContract.Page.COLUMN_PAGE_ID_NAME + " " + SheetContract.Page.COLUMN_PAGE_ID_TYPE + " PRIMARY KEY, " +
            SheetContract.Page.COLUMN_SHEET_ID_NAME + " " + SheetContract.Page.COLUMN_SHEET_ID_TYPE + ", " +
            SheetContract.Page.COLUMN_SECTION_ID_NAME + " " + SheetContract.Page.COLUMN_SECTION_ID_TYPE + ", " +
            SheetContract.Page.COLUMN_PAGE_INDEX_NAME + " " + SheetContract.Page.COLUMN_PAGE_INDEX_TYPE + ", " +
            SheetContract.Page.COLUMN_LABEL_NAME + " " + SheetContract.Page.COLUMN_LABEL_TYPE + ", " +
            "FOREIGN KEY (" + SheetContract.Page.COLUMN_SHEET_ID_NAME + ") REFERENCES " +
                    SheetContract.Sheet.TABLE_NAME + "(" + SheetContract.Sheet.COLUMN_SHEET_ID_NAME + ") )";

        final String CREATE_GROUP_TABLE =
            "CREATE TABLE IF NOT EXISTS " + SheetContract.Group.TABLE_NAME + " (" +
            SheetContract.Group.COLUMN_GROUP_ID_NAME + " " + SheetContract.Group.COLUMN_GROUP_ID_TYPE + " PRIMARY KEY, " +
            SheetContract.Group.COLUMN_PAGE_ID_NAME + " " + SheetContract.Group.COLUMN_PAGE_ID_TYPE + ", " +
            SheetContract.Group.COLUMN_GROUP_INDEX_NAME + " " + SheetContract.Group.COLUMN_GROUP_INDEX_TYPE + ", " +
            SheetContract.Group.COLUMN_LABEL_NAME + " " + SheetContract.Group.COLUMN_LABEL_TYPE + ", " +
            SheetContract.Group.COLUMN_NUMBER_OF_ROWS_NAME + " " + SheetContract.Group.COLUMN_NUMBER_OF_ROWS_TYPE + ", " +
            "FOREIGN KEY (" + SheetContract.Group.COLUMN_PAGE_ID_NAME + ") REFERENCES " +
                SheetContract.Page.TABLE_NAME + "(" + SheetContract.Page.COLUMN_PAGE_ID_NAME + ") )";

        final String CREATE_COMPONENT_TABLE =
            "CREATE TABLE IF NOT EXISTS " + SheetContract.Component.TABLE_NAME + " (" +
            SheetContract.Component.COLUMN_COMPONENT_ID_NAME + " " + SheetContract.Component.COLUMN_COMPONENT_ID_TYPE + " PRIMARY KEY," +
            SheetContract.Component.COLUMN_NAME_NAME + " " + SheetContract.Component.COLUMN_NAME_TYPE + "," +
            SheetContract.Component.COLUMN_GROUP_ID_NAME + " " + SheetContract.Component.COLUMN_GROUP_ID_TYPE + "," +
            SheetContract.Component.COLUMN_DATA_TYPE_NAME + " " + SheetContract.Component.COLUMN_DATA_TYPE_TYPE + "," +
            SheetContract.Component.COLUMN_LABEL_NAME + " " + SheetContract.Component.COLUMN_LABEL_TYPE + "," +
            SheetContract.Component.COLUMN_SHOW_LABEL_NAME + " " + SheetContract.Component.COLUMN_SHOW_LABEL_TYPE + "," +
            SheetContract.Component.COLUMN_ROW_NAME + " " + SheetContract.Component.COLUMN_ROW_TYPE + "," +
            SheetContract.Component.COLUMN_COLUMN_NAME + " " + SheetContract.Component.COLUMN_COLUMN_TYPE + "," +
            SheetContract.Component.COLUMN_WIDTH_NAME + " " + SheetContract.Component.COLUMN_WIDTH_TYPE + "," +
            SheetContract.Component.COLUMN_ALIGNMENT_NAME + " " + SheetContract.Component.COLUMN_ALIGNMENT_TYPE + "," +
            SheetContract.Component.COLUMN_KEY_STAT_NAME + " " + SheetContract.Component.COLUMN_KEY_STAT_TYPE + "," +
            SheetContract.Component.COLUMN_ACTIONS_NAME + " " + SheetContract.Component.COLUMN_ACTIONs_TYPE + "," +
            SheetContract.Component.COLUMN_TYPE_KIND_NAME + " " + SheetContract.Component.COLUMN_TYPE_KIND_TYPE + "," +
            SheetContract.Component.COLUMN_TYPE_ID_NAME + " " + SheetContract.Component.COLUMN_TYPE_ID_TYPE + "," +
            SheetContract.Component.COLUMN_TEXT_VALUE_NAME + " " + SheetContract.Component.COLUMN_TEXT_VALUE_TYPE + "," +
            "FOREIGN KEY (" + SheetContract.Component.COLUMN_GROUP_ID_NAME + ") REFERENCES " +
                SheetContract.Group.TABLE_NAME + "(" + SheetContract.Group.COLUMN_GROUP_ID_NAME + ") )";

        final String CREATE_COMPONENT_TEXT_TABLE =
            "CREATE TABLE IF NOT EXISTS " + SheetContract.ComponentText.TABLE_NAME + " (" +
            SheetContract.ComponentText.COLUMN_COMPONENT_ID_NAME + " " + SheetContract.ComponentText.COLUMN_COMPONENT_ID_TYPE + " PRIMARY KEY, " +
            SheetContract.ComponentText.COLUMN_SIZE_NAME + " " + SheetContract.ComponentText.COLUMN_SIZE_TYPE + ", " +
            SheetContract.ComponentText.COLUMN_VALUE_NAME + " " + SheetContract.ComponentText.COLUMN_VALUE_TYPE + ", " +
            "FOREIGN KEY (" + SheetContract.ComponentText.COLUMN_COMPONENT_ID_NAME + ") REFERENCES " +
                SheetContract.Component.TABLE_NAME + "(" + SheetContract.Component.COLUMN_COMPONENT_ID_NAME + ") )";

        final String CREATE_COMPONENT_INTEGER_TABLE =
            "CREATE TABLE IF NOT EXISTS " + SheetContract.ComponentInteger.TABLE_NAME + " (" +
            SheetContract.ComponentInteger.COLUMN_COMPONENT_ID_NAME + " " + SheetContract.ComponentInteger.COLUMN_COMPONENT_ID_TYPE + " PRIMARY KEY, " +
            SheetContract.ComponentInteger.COLUMN_PREFIX_NAME + " " + SheetContract.ComponentInteger.COLUMN_PREFIX_TYPE + ", " +
            SheetContract.ComponentInteger.COLUMN_VALUE_NAME + " " + SheetContract.ComponentInteger.COLUMN_VALUE_TYPE + ", " +
            "FOREIGN KEY (" + SheetContract.ComponentInteger.COLUMN_COMPONENT_ID_NAME + ") REFERENCES " +
                SheetContract.Component.TABLE_NAME + "(" + SheetContract.Component.COLUMN_COMPONENT_ID_NAME + ") )";

        final String CREATE_COMPONENT_BOOLEAN_TABLE =
            "CREATE TABLE IF NOT EXISTS " + SheetContract.ComponentBoolean.TABLE_NAME + " (" +
            SheetContract.ComponentBoolean.COLUMN_COMPONENT_ID_NAME + " " + SheetContract.ComponentBoolean.COLUMN_COMPONENT_ID_TYPE + " PRIMARY KEY, " +
            SheetContract.ComponentBoolean.COLUMN_VALUE_NAME + " " + SheetContract.ComponentBoolean.COLUMN_VALUE_TYPE + ", " +
            "FOREIGN KEY (" + SheetContract.ComponentBoolean.COLUMN_COMPONENT_ID_NAME + ") REFERENCES " +
                SheetContract.Component.TABLE_NAME + "(" + SheetContract.Component.COLUMN_COMPONENT_ID_NAME + ") )";

        final String CREATE_COMPONENT_TABLE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + SheetContract.ComponentTable.TABLE_NAME + " (" +
            SheetContract.ComponentTable.COLUMN_COMPONENT_ID_NAME + " " + SheetContract.ComponentTable.COLUMN_COMPONENT_ID_TYPE + " PRIMARY KEY, " +
            SheetContract.ComponentTable.COLUMN_WIDTH_NAME + " " + SheetContract.ComponentTable.COLUMN_WIDTH_TYPE + ", " +
            SheetContract.ComponentTable.COLUMN_HEIGHT_NAME + " " + SheetContract.ComponentTable.COLUMN_HEIGHT_TYPE + ", " +
            SheetContract.ComponentTable.COLUMN_COLUMN_1_NAME_NAME + " " + SheetContract.ComponentTable.COLUMN_COLUMN_1_NAME_TYPE + ", " +
            SheetContract.ComponentTable.COLUMN_COLUMN_2_NAME_NAME + " " + SheetContract.ComponentTable.COLUMN_COLUMN_2_NAME_TYPE + ", " +
            SheetContract.ComponentTable.COLUMN_COLUMN_3_NAME_NAME + " " + SheetContract.ComponentTable.COLUMN_COLUMN_3_NAME_TYPE + ", " +
            SheetContract.ComponentTable.COLUMN_COLUMN_4_NAME_NAME + " " + SheetContract.ComponentTable.COLUMN_COLUMN_4_NAME_TYPE + ", " +
            SheetContract.ComponentTable.COLUMN_COLUMN_5_NAME_NAME + " " + SheetContract.ComponentTable.COLUMN_COLUMN_5_NAME_TYPE + ", " +
            SheetContract.ComponentTable.COLUMN_COLUMN_6_NAME_NAME + " " + SheetContract.ComponentTable.COLUMN_COLUMN_6_NAME_TYPE + ", " +
            "FOREIGN KEY (" + SheetContract.ComponentTable.COLUMN_COMPONENT_ID_NAME + ") REFERENCES " +
                SheetContract.Component.TABLE_NAME + "(" + SheetContract.Component.COLUMN_COMPONENT_ID_NAME + ") )";

        final String CREATE_COMPONENT_TABLE_CELL_TABLE =
            "CREATE TABLE IF NOT EXISTS " + SheetContract.ComponentTableCell.TABLE_NAME + " (" +
            SheetContract.ComponentTableCell.COLUMN_TABLE_ID_NAME + " " + SheetContract.ComponentTableCell.COLUMN_TABLE_ID_TYPE + ", " +
            SheetContract.ComponentTableCell.COLUMN_ROW_INDEX_NAME + " " + SheetContract.ComponentTableCell.COLUMN_ROW_INDEX_TYPE + ", " +
            SheetContract.ComponentTableCell.COLUMN_COLUMN_INDEX_NAME + " " + SheetContract.ComponentTableCell.COLUMN_COLUMN_INDEX_TYPE + ", " +
            SheetContract.ComponentTableCell.COLUMN_IS_TEMPLATE_NAME + " " + SheetContract.ComponentTableCell.COLUMN_IS_TEMPLATE_TYPE + ", " +
            SheetContract.ComponentTableCell.COLUMN_COMPONENT_ID_NAME + " " + SheetContract.ComponentTableCell.COLUMN_COMPONENT_ID_TYPE + ", " +
            "UNIQUE (" +
                    SheetContract.ComponentTableCell.COLUMN_TABLE_ID_NAME + ", " +
                    SheetContract.ComponentTableCell.COLUMN_ROW_INDEX_NAME + ", " +
                    SheetContract.ComponentTableCell.COLUMN_COLUMN_INDEX_NAME + ", " +
                    SheetContract.ComponentTableCell.COLUMN_IS_TEMPLATE_NAME + ") )";

        final String CREATE_COMPONENT_IMAGE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + SheetContract.ComponentImage.TABLE_NAME + " (" +
            SheetContract.ComponentImage.COLUMN_COMPONENT_ID_NAME + " " + SheetContract.ComponentImage.COLUMN_COMPONENT_ID_TYPE + " PRIMARY KEY, " +
            SheetContract.ComponentImage.COLUMN_IMAGE_NAME + " " + SheetContract.ComponentImage.COLUMN_IMAGE_TYPE + ", " +
            "FOREIGN KEY (" + SheetContract.ComponentImage.COLUMN_COMPONENT_ID_NAME + ") REFERENCES " +
                SheetContract.Component.TABLE_NAME + "(" + SheetContract.Component.COLUMN_COMPONENT_ID_NAME + ") )";

        final String CREATE_TYPE_LIST_TABLE =
            "CREATE TABLE IF NOT EXISTS " + SheetContract.TypeList.TABLE_NAME + " (" +
            SheetContract.TypeList.COLUMN_LIST_ID_NAME + " " + SheetContract.TypeList.COLUMN_LIST_ID_TYPE + ", " +
            SheetContract.TypeList.COLUMN_SHEET_ID_NAME + " " + SheetContract.TypeList.COLUMN_SHEET_ID_TYPE + "," +
            SheetContract.TypeList.COLUMN_VALUE_NAME + " " + SheetContract.TypeList.COLUMN_VALUE_TYPE + ")";


        // > RUN Create Table Queries
        // --------------------------------------------------------------------------------------
        db.execSQL(CREATE_SHEET_TABLE);
        db.execSQL(CREATE_GAME_TABLE);
        db.execSQL(CREATE_PAGE_TABLE);
        db.execSQL(CREATE_GROUP_TABLE);
        db.execSQL(CREATE_COMPONENT_TABLE);
        db.execSQL(CREATE_COMPONENT_TEXT_TABLE);
        db.execSQL(CREATE_COMPONENT_INTEGER_TABLE);
        db.execSQL(CREATE_COMPONENT_BOOLEAN_TABLE);
        db.execSQL(CREATE_COMPONENT_TABLE_TABLE);
        db.execSQL(CREATE_COMPONENT_TABLE_CELL_TABLE);
        db.execSQL(CREATE_COMPONENT_IMAGE_TABLE);
        db.execSQL(CREATE_TYPE_LIST_TABLE);
    }


    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        onCreate(db);
    }


}
