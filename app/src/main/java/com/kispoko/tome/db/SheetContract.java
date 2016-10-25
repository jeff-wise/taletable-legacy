
package com.kispoko.tome.db;


import android.provider.BaseColumns;

/**
 * Define structure of database and constants.
 */
public class SheetContract
{

    public static final String DATABASE_NAME = "Sheet.db";
    public static final int DATABASE_VERSION = 1;


    private static final String TYPE_TEXT = "TEXT";
    private static final String TYPE_INTEGER = "INTEGER";
    private static final String TYPE_BLOB = "BLOB";


    private SheetContract() {}


    // > TABLES
    // ------------------------------------------------------------------------------------------

    /**
     * Sheet Table
     */
    public static class Sheet implements BaseColumns
    {
        // Table: Sheet
        public static final String TABLE_NAME = "sheet";

        // Column: Sheet Id
        public static final String COLUMN_SHEET_ID_NAME = "sheet_id";
        public static final String COLUMN_SHEET_ID_TYPE = TYPE_TEXT;

        // Column: Last Used
        public static final String COLUMN_LAST_USED_NAME = "last_used";
        public static final String COLUMN_LAST_USED_TYPE = TYPE_INTEGER;

        // Column: Game Id
        public static final String COLUMN_GAME_ID_NAME = "game_id";
        public static final String COLUMN_GAME_ID_TYPE = TYPE_TEXT;
    }


    /**
     * Game
     */
    public static class Game implements BaseColumns
    {
        // Table: Sheet
        public static final String TABLE_NAME = "game";

        // Column: Game Id
        public static final String COLUMN_GAME_ID_NAME = "game_id";
        public static final String COLUMN_GAME_ID_TYPE = TYPE_TEXT;

        // Column: Label
        public static final String COLUMN_LABEL_NAME = "label";
        public static final String COLUMN_LABEL_TYPE = TYPE_TEXT;

        // Column: Description
        public static final String COLUMN_DESCRIPTION_NAME = "description";
        public static final String COLUMN_DESCRIPTION_TYPE = TYPE_TEXT;
    }


    /**
     * Page Table
     */
    public static class Page implements BaseColumns
    {
        // Table: Page
        public static final String TABLE_NAME = "page";

        // Column: Page Id
        public static final String COLUMN_PAGE_ID_NAME = "page_id";
        public static final String COLUMN_PAGE_ID_TYPE = TYPE_TEXT;

        // Column: Sheet Id
        public static final String COLUMN_SHEET_ID_NAME = "sheet_id";
        public static final String COLUMN_SHEET_ID_TYPE = TYPE_TEXT;

        // Column: Section Id
        public static final String COLUMN_SECTION_ID_NAME = "section_id";
        public static final String COLUMN_SECTION_ID_TYPE = TYPE_TEXT;

        // Column: Page Index
        public static final String COLUMN_PAGE_INDEX_NAME = "page_index";
        public static final String COLUMN_PAGE_INDEX_TYPE = TYPE_INTEGER;

        // Column: Label
        public static final String COLUMN_LABEL_NAME = "label";
        public static final String COLUMN_LABEL_TYPE = TYPE_TEXT;
    }


    /**
     * Group Table
     */
    public static class Group implements BaseColumns
    {
        // Table: Group
        public static final String TABLE_NAME = "_group";

        // Column: Group Id
        public static final String COLUMN_GROUP_ID_NAME = "group_id";
        public static final String COLUMN_GROUP_ID_TYPE = TYPE_TEXT;

        // Column: Page Id
        public static final String COLUMN_PAGE_ID_NAME = "page_id";
        public static final String COLUMN_PAGE_ID_TYPE = TYPE_TEXT;

        // Column: Group Index
        public static final String COLUMN_GROUP_INDEX_NAME = "group_index";
        public static final String COLUMN_GROUP_INDEX_TYPE = TYPE_INTEGER;

        // Column: Group Label
        public static final String COLUMN_LABEL_NAME = "label";
        public static final String COLUMN_LABEL_TYPE = TYPE_TEXT;

        // Column: Number Of Rows
        public static final String COLUMN_NUMBER_OF_ROWS_NAME = "number_of_rows";
        public static final String COLUMN_NUMBER_OF_ROWS_TYPE = TYPE_INTEGER;
    }


    /**
     * Component Table
     */
    public static class Component implements BaseColumns
    {
        // Table: Component Info
        public static final String TABLE_NAME = "component";

        // Column: Component Id
        public static final String COLUMN_COMPONENT_ID_NAME = "component_id";
        public static final String COLUMN_COMPONENT_ID_TYPE = TYPE_TEXT;

        // Column: Group Id
        public static final String COLUMN_GROUP_ID_NAME = "group_id";
        public static final String COLUMN_GROUP_ID_TYPE = TYPE_TEXT;

        // Column: Data Type
        public static final String COLUMN_DATA_TYPE_NAME = "data_type";
        public static final String COLUMN_DATA_TYPE_TYPE = TYPE_TEXT;

        // Column: Label
        public static final String COLUMN_LABEL_NAME = "label";
        public static final String COLUMN_LABEL_TYPE = TYPE_TEXT;

        // Column: Row
        public static final String COLUMN_ROW_NAME = "row";
        public static final String COLUMN_ROW_TYPE = TYPE_INTEGER;

        // Column: Column
        public static final String COLUMN_COLUMN_NAME = "column";
        public static final String COLUMN_COLUMN_TYPE = TYPE_INTEGER;

        // Column: Width
        public static final String COLUMN_WIDTH_NAME = "width";
        public static final String COLUMN_WIDTH_TYPE = TYPE_INTEGER;

        // Column: Key Stat
        public static final String COLUMN_KEY_STAT_NAME = "key_stat";
        public static final String COLUMN_KEY_STAT_TYPE = TYPE_INTEGER;

        // Column: Actions
        public static final String COLUMN_ACTIONS_NAME = "actions";
        public static final String COLUMN_ACTIONs_TYPE = TYPE_TEXT;

        // Column: Type Kind
        public static final String COLUMN_TYPE_KIND_NAME = "type_kind";
        public static final String COLUMN_TYPE_KIND_TYPE = TYPE_TEXT;

        // Column: Type Id
        public static final String COLUMN_TYPE_ID_NAME = "type_id";
        public static final String COLUMN_TYPE_ID_TYPE = TYPE_TEXT;

        // Column: Text Value (for easier querying in some cases)
        public static final String COLUMN_TEXT_VALUE_NAME = "text_value";
        public static final String COLUMN_TEXT_VALUE_TYPE = TYPE_TEXT;
    }


    /**
     * Text Component
     */
    public static class ComponentText implements BaseColumns
    {
        // Table: Component Text
        public static final String TABLE_NAME = "component_text";

        // Column: Text Id
        public static final String COLUMN_COMPONENT_ID_NAME = "component_id";
        public static final String COLUMN_COMPONENT_ID_TYPE = TYPE_TEXT;

        // Column: Value
        public static final String COLUMN_VALUE_NAME = "value";
        public static final String COLUMN_VALUE_TYPE = TYPE_TEXT;

        // Column: Size
        public static final String COLUMN_SIZE_NAME = "size";
        public static final String COLUMN_SIZE_TYPE = TYPE_TEXT;
    }


    /**
     * Integer Component
     */
    public static class ComponentInteger implements BaseColumns
    {
        // Table: Integer
        public static final String TABLE_NAME = "component_integer";

        // Column: Component Id
        public static final String COLUMN_COMPONENT_ID_NAME = "component_id";
        public static final String COLUMN_COMPONENT_ID_TYPE = TYPE_TEXT;

        // Column: Value
        public static final String COLUMN_VALUE_NAME = "value";
        public static final String COLUMN_VALUE_TYPE = TYPE_INTEGER;

        // Column: Prefix
        public static final String COLUMN_PREFIX_NAME = "prefix";
        public static final String COLUMN_PREFIX_TYPE = TYPE_TEXT;
    }


    /**
     * Table Component
     */
    public static class ComponentTable implements BaseColumns
    {
        // Table: Data Table
        public static final String TABLE_NAME = "component_table";

        // Column: Component Id
        public static final String COLUMN_COMPONENT_ID_NAME = "component_id";
        public static final String COLUMN_COMPONENT_ID_TYPE = TYPE_TEXT;

        // Column: Column 1
        public static final String COLUMN_COLUMN_1_NAME_NAME = "column1_name";
        public static final String COLUMN_COLUMN_1_NAME_TYPE = TYPE_TEXT;

        // Column: Column 2
        public static final String COLUMN_COLUMN_2_NAME_NAME = "column2_name";
        public static final String COLUMN_COLUMN_2_NAME_TYPE = TYPE_TEXT;

        // Column: Column 3
        public static final String COLUMN_COLUMN_3_NAME_NAME = "column3_name";
        public static final String COLUMN_COLUMN_3_NAME_TYPE = TYPE_TEXT;

        // Column: Column 4
        public static final String COLUMN_COLUMN_4_NAME_NAME = "column4_name";
        public static final String COLUMN_COLUMN_4_NAME_TYPE = TYPE_TEXT;

        // Column: Column 5
        public static final String COLUMN_COLUMN_5_NAME_NAME = "column5_name";
        public static final String COLUMN_COLUMN_5_NAME_TYPE = TYPE_TEXT;

        // Column: Column 6
        public static final String COLUMN_COLUMN_6_NAME_NAME = "column6_name";
        public static final String COLUMN_COLUMN_6_NAME_TYPE = TYPE_TEXT;
    }


    /**
     * Table Component
     */
    public static class ComponentTableRow implements BaseColumns
    {
        // Table: Data Table Row
        public static final String TABLE_NAME = "component_table_row";

        // Column: Table Row Id
        public static final String COLUMN_TABLE_ROW_ID_NAME = "table_row_id";
        public static final String COLUMN_TABLE_ROW_ID_TYPE = TYPE_INTEGER;

        // Column: Table Id
        public static final String COLUMN_TABLE_ID_NAME = "table_id";
        public static final String COLUMN_TABLE_ID_TYPE = TYPE_TEXT;

        // Column: Index
        public static final String COLUMN_ROW_INDEX_NAME = "row_index";
        public static final String COLUMN_ROW_INDEX_TYPE = TYPE_INTEGER;

        // Column: Column 1
        public static final String COLUMN_COLUMN_1_NAME = "column1";
        public static final String COLUMN_COLUMN_1_TYPE = TYPE_TEXT;

        // Column: Column 2
        public static final String COLUMN_COLUMN_2_NAME = "column2";
        public static final String COLUMN_COLUMN_2_TYPE = TYPE_TEXT;

        // Column: Column 3
        public static final String COLUMN_COLUMN_3_NAME = "column3";
        public static final String COLUMN_COLUMN_3_TYPE = TYPE_TEXT;

        // Column: Column 4
        public static final String COLUMN_COLUMN_4_NAME = "column4";
        public static final String COLUMN_COLUMN_4_TYPE = TYPE_TEXT;

        // Column: Column 5
        public static final String COLUMN_COLUMN_5_NAME = "column5";
        public static final String COLUMN_COLUMN_5_TYPE = TYPE_TEXT;

        // Column: Column 6
        public static final String COLUMN_COLUMN_6_NAME = "column6";
        public static final String COLUMN_COLUMN_6_TYPE = TYPE_TEXT;
    }


    /**
     * Image Component
     */
    public static class ComponentImage implements BaseColumns
    {
        // Table: Integer
        public static final String TABLE_NAME = "component_image";

        // Column: Component Id
        public static final String COLUMN_COMPONENT_ID_NAME = "component_id";
        public static final String COLUMN_COMPONENT_ID_TYPE = TYPE_TEXT;

        // Column: Image
        public static final String COLUMN_IMAGE_NAME = "image";
        public static final String COLUMN_IMAGE_TYPE = TYPE_BLOB;
    }


    /**
     * ListType Type
     */
    public static class TypeList implements BaseColumns
    {
        // Table: Data Table
        public static final String TABLE_NAME = "type_list";

        // Column: Component Id
        public static final String COLUMN_LIST_ID_NAME = "list_id";
        public static final String COLUMN_LIST_ID_TYPE = TYPE_TEXT;

        // Column: Sheet Id
        public static final String COLUMN_SHEET_ID_NAME = "sheet_id";
        public static final String COLUMN_SHEET_ID_TYPE = TYPE_TEXT;

        // Column: Value
        public static final String COLUMN_VALUE_NAME = "value";
        public static final String COLUMN_VALUE_TYPE = TYPE_TEXT;
    }

}
