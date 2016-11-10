
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
     * Sheet TableWidget
     */
    public static class Sheet implements BaseColumns
    {
        // TableWidget: Sheet
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
        // TableWidget: Game
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
     * Page TableWidget
     */
    public static class Page implements BaseColumns
    {
        // TableWidget: Page
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
     * Group TableWidget
     */
    public static class Group implements BaseColumns
    {
        // TableWidget: Group
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

        // Column: NumberWidget Of Rows
        public static final String COLUMN_NUMBER_OF_ROWS_NAME = "number_of_rows";
        public static final String COLUMN_NUMBER_OF_ROWS_TYPE = TYPE_INTEGER;
    }


    /**
     * WidgetData TableWidget
     */
    public static class Component implements BaseColumns
    {
        // TableWidget: WidgetData
        public static final String TABLE_NAME = "component";

        // Column: WidgetData Id
        public static final String COLUMN_COMPONENT_ID_NAME = "component_id";
        public static final String COLUMN_COMPONENT_ID_TYPE = TYPE_TEXT;

        // Column: Name
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_TYPE = TYPE_TEXT;

        // Column: Value
        public static final String COLUMN_VALUE_NAME = "value";
        public static final String COLUMN_VALUE_TYPE = TYPE_TEXT;

        // Column: Group Id
        public static final String COLUMN_GROUP_ID_NAME = "group_id";
        public static final String COLUMN_GROUP_ID_TYPE = TYPE_TEXT;

        // Column: Type Id
        public static final String COLUMN_TYPE_ID_NAME = "type_id";
        public static final String COLUMN_TYPE_ID_TYPE = TYPE_TEXT;

        // Column: Key Stat
        public static final String COLUMN_KEY_STAT_NAME = "key_stat";
        public static final String COLUMN_KEY_STAT_TYPE = TYPE_INTEGER;

        // Column: Actions
        public static final String COLUMN_ACTIONS_NAME = "actions";
        public static final String COLUMN_ACTIONs_TYPE = TYPE_TEXT;

        // Column: TextWidget Value (for easier querying in some cases)
        public static final String COLUMN_TEXT_VALUE_NAME = "text_value";
        public static final String COLUMN_TEXT_VALUE_TYPE = TYPE_TEXT;
    }


    /**
     * WidgetData Format
     */
    public static class ComponentFormat implements BaseColumns
    {
        // TableWidget: WidgetData
        public static final String TABLE_NAME = "component_format";

        // Column: WidgetData Id
        public static final String COLUMN_COMPONENT_ID_NAME = "component_id";
        public static final String COLUMN_COMPONENT_ID_TYPE = TYPE_TEXT;

        // Column: Label
        public static final String COLUMN_LABEL_NAME = "label";
        public static final String COLUMN_LABEL_TYPE = TYPE_TEXT;

        // Column: Show Label
        public static final String COLUMN_SHOW_LABEL_NAME = "show_label";
        public static final String COLUMN_SHOW_LABEL_TYPE = TYPE_INTEGER;

        // Column: Row
        public static final String COLUMN_ROW_NAME = "row";
        public static final String COLUMN_ROW_TYPE = TYPE_INTEGER;

        // Column: Column
        public static final String COLUMN_COLUMN_NAME = "column";
        public static final String COLUMN_COLUMN_TYPE = TYPE_INTEGER;

        // Column: Width
        public static final String COLUMN_WIDTH_NAME = "width";
        public static final String COLUMN_WIDTH_TYPE = TYPE_INTEGER;

        // Column: Alignment
        public static final String COLUMN_ALIGNMENT_NAME = "alignment";
        public static final String COLUMN_ALIGNMENT_TYPE = TYPE_TEXT;
    }


    /**
     * Variable TableWidget
     */
    public static class Variable implements BaseColumns
    {
        // TableWidget: WidgetData
        public static final String TABLE_NAME = "variable";

        // Column: WidgetData Id
        public static final String COLUMN_VARIABLE_ID_NAME = "variable_id";
        public static final String COLUMN_VARIABLE_ID_TYPE = TYPE_TEXT;

        // Column: Value
        public static final String COLUMN_VALUE_NAME = "value";
        public static final String COLUMN_VALUE_TYPE = TYPE_TEXT;

        // Column: Value Type
        public static final String COLUMN_VALUE_TYPE_NAME = "value_type";
        public static final String COLUMN_VALUE_TYPE_TYPE = TYPE_TEXT;
    }


    /**
     * TextWidget WidgetData TableWidget
     */
    public static class ComponentText implements BaseColumns
    {
        // TableWidget: WidgetData TextWidget
        public static final String TABLE_NAME = "component_text";

        // Column: TextWidget Id
        public static final String COLUMN_COMPONENT_ID_NAME = "component_id";
        public static final String COLUMN_COMPONENT_ID_TYPE = TYPE_TEXT;

        // Column: Value
//        public static final String COLUMN_VALUE_NAME = "value";
//        public static final String COLUMN_VALUE_TYPE = TYPE_TEXT;

        // Column: Size
        public static final String COLUMN_SIZE_NAME = "size";
        public static final String COLUMN_SIZE_TYPE = TYPE_TEXT;
    }


    /**
     * Integer WidgetData TableWidget
     */
    public static class ComponentInteger implements BaseColumns
    {
        // TableWidget: WidgetData Integer
        public static final String TABLE_NAME = "component_integer";

        // Column: WidgetData Id
        public static final String COLUMN_COMPONENT_ID_NAME = "component_id";
        public static final String COLUMN_COMPONENT_ID_TYPE = TYPE_TEXT;

        // Column: Value
//        public static final String COLUMN_VALUE_NAME = "value";
//        public static final String COLUMN_VALUE_TYPE = TYPE_INTEGER;

        // Column: Prefix
        public static final String COLUMN_PREFIX_NAME = "prefix";
        public static final String COLUMN_PREFIX_TYPE = TYPE_TEXT;

        // Column: Postfix
        public static final String COLUMN_POSTFIX_NAME = "postfix";
        public static final String COLUMN_POSTFIX_TYPE = TYPE_TEXT;
    }


    /**
     * Boolean WidgetData TableWidget
     */
    public static class ComponentBoolean implements BaseColumns
    {
        // TableWidget: WidgetData Boolean
        public static final String TABLE_NAME = "component_boolean";

        // Column: WidgetData Id
        public static final String COLUMN_COMPONENT_ID_NAME = "component_id";
        public static final String COLUMN_COMPONENT_ID_TYPE = TYPE_TEXT;

        // Column: Value
//        public static final String COLUMN_VALUE_NAME = "value";
//        public static final String COLUMN_VALUE_TYPE = TYPE_INTEGER;
    }


    /**
     * TableWidget WidgetData TableWidget
     */
    public static class ComponentTable implements BaseColumns
    {
        // TableWidget: WidgetData TableWidget
        public static final String TABLE_NAME = "component_table";

        // Column: WidgetData Id
        public static final String COLUMN_COMPONENT_ID_NAME = "component_id";
        public static final String COLUMN_COMPONENT_ID_TYPE = TYPE_TEXT;

        // Column: Width
        public static final String COLUMN_WIDTH_NAME = "width";
        public static final String COLUMN_WIDTH_TYPE = TYPE_INTEGER;

        // Column: Height
        public static final String COLUMN_HEIGHT_NAME = "height";
        public static final String COLUMN_HEIGHT_TYPE = TYPE_INTEGER;

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
     * TableWidget WidgetData Cell TableWidget
     */
    public static class ComponentTableCell implements BaseColumns
    {
        // TableWidget: WidgetData TableWidget Cell
        public static final String TABLE_NAME = "component_table_cell";

        // Column: TableWidget Id
        public static final String COLUMN_TABLE_ID_NAME = "table_id";
        public static final String COLUMN_TABLE_ID_TYPE = TYPE_TEXT;

        // Column: Row Index
        public static final String COLUMN_ROW_INDEX_NAME = "row_index";
        public static final String COLUMN_ROW_INDEX_TYPE = TYPE_INTEGER;

        // Column: Column Index
        public static final String COLUMN_COLUMN_INDEX_NAME = "column_index";
        public static final String COLUMN_COLUMN_INDEX_TYPE = TYPE_INTEGER;

        // Column: Is Template?
        public static final String COLUMN_IS_TEMPLATE_NAME = "is_template";
        public static final String COLUMN_IS_TEMPLATE_TYPE = TYPE_INTEGER;

        // Column: WidgetData Id
        public static final String COLUMN_COMPONENT_ID_NAME = "component_id";
        public static final String COLUMN_COMPONENT_ID_TYPE = TYPE_TEXT;
    }


    /**
     * ImageWidget WidgetData TableWidget
     */
    public static class ComponentImage implements BaseColumns
    {
        // TableWidget: ImageWidget WidgetData
        public static final String TABLE_NAME = "component_image";

        // Column: WidgetData Id
        public static final String COLUMN_COMPONENT_ID_NAME = "component_id";
        public static final String COLUMN_COMPONENT_ID_TYPE = TYPE_TEXT;

        // Column: ImageWidget
        public static final String COLUMN_IMAGE_NAME = "image";
        public static final String COLUMN_IMAGE_TYPE = TYPE_BLOB;
    }


    /**
     * Type TableWidget
     */
    public static class Type implements BaseColumns
    {
        // TableWidget: Type
        public static final String TABLE_NAME = "type";

        // Column: Type Id
        public static final String COLUMN_TYPE_ID_NAME = "type_id";
        public static final String COLUMN_TYPE_ID_TYPE = TYPE_TEXT;

        // Column: Type Id
        public static final String COLUMN_TYPE_VALUE_ID_NAME = "type_id";
        public static final String COLUMN_TYPE_VALUE_ID_TYPE = TYPE_TEXT;

        // Column: Type Kind
        public static final String COLUMN_TYPE_KIND_NAME = "type_kind";
        public static final String COLUMN_TYPE_KIND_TYPE = TYPE_TEXT;
    }


    /**
     * ListType Type TableWidget
     */
    public static class TypeList implements BaseColumns
    {
        // TableWidget: List Type
        public static final String TABLE_NAME = "type_list";

        // Column: WidgetData Id
        public static final String COLUMN_LIST_ID_NAME = "list_id";
        public static final String COLUMN_LIST_ID_TYPE = TYPE_TEXT;

        // Column: Sheet Id
        public static final String COLUMN_SHEET_ID_NAME = "sheet_id";
        public static final String COLUMN_SHEET_ID_TYPE = TYPE_TEXT;

        // Column: Value
        public static final String COLUMN_VALUE_NAME = "value";
        public static final String COLUMN_VALUE_TYPE = TYPE_TEXT;
    }


    /**
     * Function TableWidget
     */
    public static class Function implements BaseColumns
    {
        // TableWidget: Function
        public static final String TABLE_NAME = "function";

        // Column: Function Id
        public static final String COLUMN_FUNCTION_ID_NAME = "function_id";
        public static final String COLUMN_FUNCTION_ID_TYPE = TYPE_TEXT;

        // Column: Name
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_TYPE = TYPE_TEXT;

        // Column: Sheet Id
        public static final String COLUMN_SHEET_ID_NAME = "sheet_id";
        public static final String COLUMN_SHEET_ID_TYPE = TYPE_TEXT;

        // Column: Result Type
        public static final String COLUMN_RESULT_TYPE_NAME = "result_type";
        public static final String COLUMN_RESULT_TYPE_TYPE = TYPE_TEXT;

        // Column: NumberWidget of Parameters
        public static final String COLUMN_NUMBER_OF_PARAMETERS_NAME = "number_of_parameters";
        public static final String COLUMN_NUMBER_OF_PARAMETERS_TYPE = TYPE_INTEGER;

        // Column: Parameter Type (1)
        public static final String COLUMN_PARAMETER_TYPE_1_NAME = "parameter_type_1";
        public static final String COLUMN_PARAMETER_TYPE_1_TYPE = TYPE_TEXT;

        // Column: Parameter Type (2)
        public static final String COLUMN_PARAMETER_TYPE_2_NAME = "parameter_type_2";
        public static final String COLUMN_PARAMETER_TYPE_2_TYPE = TYPE_TEXT;

        // Column: Parameter Type (3)
        public static final String COLUMN_PARAMETER_TYPE_3_NAME = "parameter_type_3";
        public static final String COLUMN_PARAMETER_TYPE_3_TYPE = TYPE_TEXT;
    }


    /**
     * Tuple TableWidget
     */
    public static class Tuple implements BaseColumns
    {
        // TableWidget: Tuple
        public static final String TABLE_NAME = "tuple";

        // Column: Function Name
        public static final String COLUMN_FUNCTION_ID_NAME = "function_id";
        public static final String COLUMN_FUNCTION_ID_TYPE = TYPE_TEXT;

        // Column: Parameter 1 Value
        public static final String COLUMN_PARAMETER_1_VALUE_NAME = "parameter1";
        public static final String COLUMN_PARAMETER_1_VALUE_TYPE = TYPE_TEXT;

        // Column: Parameter 2 Value
        public static final String COLUMN_PARAMETER_2_VALUE_NAME = "parameter2";
        public static final String COLUMN_PARAMETER_2_VALUE_TYPE = TYPE_TEXT;

        // Column: Parameter 3 Value
        public static final String COLUMN_PARAMETER_3_VALUE_NAME = "parameter3";
        public static final String COLUMN_PARAMETER_3_VALUE_TYPE = TYPE_TEXT;

        // Column: Result Value
        public static final String COLUMN_RESULT_VALUE_NAME = "result";
        public static final String COLUMN_RESULT_VALUE_TYPE = TYPE_TEXT;
    }


    /**
     * Program TableWidget
     */
    public static class Program implements BaseColumns
    {
        // TableWidget: Program
        public static final String TABLE_NAME = "program";

        // Column: Program Id
        public static final String COLUMN_PROGRAM_ID_NAME = "program_id";
        public static final String COLUMN_PROGRAM_ID_TYPE = TYPE_TEXT;

        // Column: Program Name
        public static final String COLUMN_PROGRAM_NAME = "program_name";
        public static final String COLUMN_PROGRAM_TYPE = TYPE_TEXT;

        // Column: Sheet Id
        public static final String COLUMN_SHEET_ID_NAME = "sheet_id";
        public static final String COLUMN_SHEET_ID_TYPE = TYPE_TEXT;

        // Column: Result Type
        public static final String COLUMN_RESULT_TYPE_NAME = "result_type";
        public static final String COLUMN_RESULT_TYPE_TYPE = TYPE_TEXT;

        // Column: NumberWidget of Parameters
        public static final String COLUMN_NUMBER_OF_PARAMETERS_NAME = "number_of_parameters";
        public static final String COLUMN_NUMBER_OF_PARAMETERS_TYPE = TYPE_INTEGER;

        // Column: Parameter Type (1)
        public static final String COLUMN_PARAMETER_TYPE_1_NAME = "parameter_type_1";
        public static final String COLUMN_PARAMETER_TYPE_1_TYPE = TYPE_TEXT;

        // Column: Parameter Type (2)
        public static final String COLUMN_PARAMETER_TYPE_2_NAME = "parameter_type_2";
        public static final String COLUMN_PARAMETER_TYPE_2_TYPE = TYPE_TEXT;

        // Column: Parameter Type (3)
        public static final String COLUMN_PARAMETER_TYPE_3_NAME = "parameter_type_3";
        public static final String COLUMN_PARAMETER_TYPE_3_TYPE = TYPE_TEXT;

        // Column: Result Variable Name
        public static final String COLUMN_VARIABLE_NAME_NAME = "variable_name";
        public static final String COLUMN_VARIABLE_NAME_TYPE = TYPE_TEXT;
    }


    /**
     * Statement TableWidget
     */
    public static class Statement implements BaseColumns
    {
        // TableWidget: Statement
        public static final String TABLE_NAME = "statement";

        // Column: Program Id
        public static final String COLUMN_PROGRAM_ID_NAME = "program_id";
        public static final String COLUMN_PROGRAM_ID_TYPE = TYPE_TEXT;

        // Column: Variable Name
        public static final String COLUMN_VARIABLE_NAME_NAME = "variable_name";
        public static final String COLUMN_VARIABLE_NAME_TYPE = TYPE_TEXT;

        // Column: Function Name
        public static final String COLUMN_FUNCTION_NAME_NAME = "function_name";
        public static final String COLUMN_FUNCTION_NAME_TYPE = TYPE_TEXT;

        // Column: NumberWidget of Parameters Name
        public static final String COLUMN_NUMBER_OF_PARAMETERS_NAME = "number_of_parameters";
        public static final String COLUMN_NUMBER_OF_PARAMETERS_TYPE = TYPE_INTEGER;

        // Column: Parameter 1 Value
        public static final String COLUMN_PARAMETER_VALUE_1_NAME = "parameter_value_1";
        public static final String COLUMN_PARAMETER_VALUE_1_TYPE = TYPE_TEXT;

        // Column: Parameter 1 Type
        public static final String COLUMN_PARAMETER_TYPE_1_NAME = "parameter_type_1";
        public static final String COLUMN_PARAMETER_TYPE_1_TYPE = TYPE_TEXT;

        // Column: Parameter 2 Value
        public static final String COLUMN_PARAMETER_VALUE_2_NAME = "parameter_value_2";
        public static final String COLUMN_PARAMETER_VALUE_2_TYPE = TYPE_TEXT;

        // Column: Parameter 2 Type
        public static final String COLUMN_PARAMETER_TYPE_2_NAME = "parameter_type_2";
        public static final String COLUMN_PARAMETER_TYPE_2_TYPE = TYPE_TEXT;

        // Column: Parameter 3 Value
        public static final String COLUMN_PARAMETER_VALUE_3_NAME = "parameter_value_3";
        public static final String COLUMN_PARAMETER_VALUE_3_TYPE = TYPE_TEXT;

        // Column: Parameter 3 Type
        public static final String COLUMN_PARAMETER_TYPE_3_NAME = "parameter_type_3";
        public static final String COLUMN_PARAMETER_TYPE_3_TYPE = TYPE_TEXT;
    }


    /**
     * Statement TableWidget
     */
    public static class ProgramInvocation implements BaseColumns
    {
        // TableWidget: Program Invocation
        public static final String TABLE_NAME = "program_invocation";

        // Column: Program Invocation Id
        public static final String COLUMN_PROGRAM_INVOCATION_ID_NAME = "program_invocation_id";
        public static final String COLUMN_PROGRAM_INVOCATION_ID_TYPE = TYPE_TEXT;

        // Column: Program Name
        public static final String COLUMN_PROGRAM_NAME_NAME = "program_name";
        public static final String COLUMN_PROGRAM_NAME_TYPE = TYPE_TEXT;

        // Column: NumberWidget of Parameters Name
        public static final String COLUMN_NUMBER_OF_PARAMETERS_NAME = "number_of_parameters";
        public static final String COLUMN_NUMBER_OF_PARAMETERS_TYPE = TYPE_INTEGER;

        // Column: Parameter 1 Value
        public static final String COLUMN_PARAMETER_VALUE_1_NAME = "parameter_value_1";
        public static final String COLUMN_PARAMETER_VALUE_1_TYPE = TYPE_TEXT;

        // Column: Parameter 1 Type
        public static final String COLUMN_PARAMETER_TYPE_1_NAME = "parameter_type_1";
        public static final String COLUMN_PARAMETER_TYPE_1_TYPE = TYPE_TEXT;

        // Column: Parameter 2 Value
        public static final String COLUMN_PARAMETER_VALUE_2_NAME = "parameter_value_2";
        public static final String COLUMN_PARAMETER_VALUE_2_TYPE = TYPE_TEXT;

        // Column: Parameter 2 Type
        public static final String COLUMN_PARAMETER_TYPE_2_NAME = "parameter_type_2";
        public static final String COLUMN_PARAMETER_TYPE_2_TYPE = TYPE_TEXT;

        // Column: Parameter 3 Value
        public static final String COLUMN_PARAMETER_VALUE_3_NAME = "parameter_value_3";
        public static final String COLUMN_PARAMETER_VALUE_3_TYPE = TYPE_TEXT;

        // Column: Parameter 3 Type
        public static final String COLUMN_PARAMETER_TYPE_3_NAME = "parameter_type_3";
        public static final String COLUMN_PARAMETER_TYPE_3_TYPE = TYPE_TEXT;
    }

}
