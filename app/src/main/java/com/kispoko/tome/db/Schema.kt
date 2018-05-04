
package com.kispoko.tome.db


import com.kispoko.tome.lib.orm.*
import com.kispoko.tome.lib.orm.schema.*
import com.kispoko.tome.model.book.*
import com.kispoko.tome.model.campaign.CampaignId
import com.kispoko.tome.model.campaign.CampaignName
import com.kispoko.tome.model.campaign.CampaignSummary
import com.kispoko.tome.model.game.*
import com.kispoko.tome.model.engine.*
import com.kispoko.tome.model.engine.dice.*
import com.kispoko.tome.model.engine.function.*
import com.kispoko.tome.model.engine.function.Function
import com.kispoko.tome.model.engine.mechanic.*
import com.kispoko.tome.model.engine.message.*
import com.kispoko.tome.model.engine.procedure.*
import com.kispoko.tome.model.engine.program.*
import com.kispoko.tome.model.engine.reference.BooleanReference
import com.kispoko.tome.model.engine.reference.NumberReference
import com.kispoko.tome.model.engine.summation.Summation
import com.kispoko.tome.model.engine.summation.SummationId
import com.kispoko.tome.model.engine.summation.SummationName
import com.kispoko.tome.model.engine.summation.term.TermName
import com.kispoko.tome.model.engine.value.*
import com.kispoko.tome.model.engine.variable.*
import com.kispoko.tome.model.engine.constraint.ConstraintNumber
import com.kispoko.tome.model.sheet.Settings
import com.kispoko.tome.model.sheet.SheetId
import com.kispoko.tome.model.sheet.SheetName
import com.kispoko.tome.model.sheet.SheetSummary
import com.kispoko.tome.model.sheet.group.*
import com.kispoko.tome.model.sheet.page.Page
import com.kispoko.tome.model.sheet.page.PageFormat
import com.kispoko.tome.model.sheet.page.PageIndex
import com.kispoko.tome.model.sheet.page.PageName
import com.kispoko.tome.model.sheet.section.Section
import com.kispoko.tome.model.sheet.section.SectionName
import com.kispoko.tome.model.sheet.style.*
import com.kispoko.tome.model.sheet.widget.*
import com.kispoko.tome.model.sheet.widget.table.*
import com.kispoko.tome.model.sheet.widget.table.cell.BooleanCellFormat
import com.kispoko.tome.model.sheet.widget.table.cell.NumberCellFormat
import com.kispoko.tome.model.sheet.widget.table.cell.TextCellFormat
import com.kispoko.tome.model.sheet.widget.table.column.*
import com.kispoko.tome.model.theme.*
import com.kispoko.tome.model.user.UserName


//*********************************************************************************************//
//                                Application Database Schema                                  //
//*********************************************************************************************//

// ACTION
// ---------------------------------------------------------------------------------------------

val actionTable = Table3("action",
                         "action_name",
                         "roll_group",
                         "procedure_id")

typealias DB_ActionValue =
    RowValue3<PrimValue<ActionName>,
              MaybeProdValue<DiceRollGroup>,
              MaybePrimValue<ProcedureId>>


// APP SETTINGS
// ---------------------------------------------------------------------------------------------

val appSettingsTable = Table1("app_settings", "theme_id")

typealias DB_AppSettingsValue = RowValue1<PrimValue<ThemeId>>


// AUTHOR
// ---------------------------------------------------------------------------------------------

val authorTable = Table3("author",
                         "author_name",
                         "organization",
                         "user_name")

typealias DB_AuthorValue =
    RowValue3<PrimValue<AuthorName>,
              MaybePrimValue<AuthorOrganization>,
              MaybePrimValue<UserName>>


// BOOK
// ---------------------------------------------------------------------------------------------

val bookTable =
    Table6("book",
           "book_id",
           "info",
           "settings",
           "engine",
           "variables",
           "chapters")


typealias DB_BookValue =
    RowValue6<PrimValue<BookId>,
              ProdValue<BookInfo>,
              ProdValue<BookSettings>,
              ProdValue<Engine>,
              CollValue<Variable>,
              CollValue<BookChapter>>


// BOOK FORMAT
// ---------------------------------------------------------------------------------------------

val bookFormatTable =
    Table3("book_format",
           "element_format",
           "chapter_index_format",
           "chapter_button_format")

typealias DB_BookFormatValue =
    RowValue3<ProdValue<ElementFormat>,
              ProdValue<ElementFormat>,
              ProdValue<ChapterButtonFormat>>

// BOOK INFO
// ---------------------------------------------------------------------------------------------

val bookInfoTable =
    Table4("book_info",
           "title",
           "summary",
           "authors",
           "abstract")


typealias DB_BookInfoValue =
    RowValue4<PrimValue<BookTitle>,
              PrimValue<BookSummary>,
              CollValue<Author>,
              PrimValue<BookAbstract>>

// BOOK SETTINGS
// ---------------------------------------------------------------------------------------------

val bookSettingsTable =
    Table1("book_settings",
           "theme_id")


typealias DB_BookSettingsValue =
    RowValue1<PrimValue<ThemeId>>


// BOOK CONTENT
// ---------------------------------------------------------------------------------------------
//
//val bookContentTable =
//    Table1("book_content",
//           "groups")
//
//typealias DB_BookContentValue =
//    RowValue1<CollValue<Group>>


// BOOK CHAPTER
// ---------------------------------------------------------------------------------------------

val bookChapterTable =
    Table3("book_chapter",
           "chapter_id",
           "title",
           "sections")

typealias DB_BookChapterValue =
    RowValue3<PrimValue<BookChapterId>,
              PrimValue<BookChapterTitle>,
              CollValue<BookSection>>


// BOOK SECTION
// ---------------------------------------------------------------------------------------------

val bookSectionTable =
    Table3("book_section",
           "section_id",
           "title",
           "subsections")

typealias DB_BookSectionValue =
    RowValue3<PrimValue<BookSectionId>,
              PrimValue<BookSectionTitle>,
              CollValue<BookSubsection>>


// BOOK SECTION > FORMAT
// ---------------------------------------------------------------------------------------------

val bookSectionFormatTable =
    Table1("book_section_format",
           "page_header_format")


typealias DB_BookSectionFormatValue =
    RowValue1<ProdValue<BookSectionPageHeaderFormat>>


// BOOK SECTION > FORMAT > PAGE HEADER FORMAT
// ---------------------------------------------------------------------------------------------

val bookSectionPageHeaderFormatTable =
    Table3("book_section_page_header_format",
           "element_format",
           "chapter_name_format",
           "section_name_format")


typealias DB_BookSectionPageHeaderFormatValue =
    RowValue3<ProdValue<ElementFormat>,
              ProdValue<TextFormat>,
              ProdValue<TextFormat>>


// BOOK SUBSECTION
// ---------------------------------------------------------------------------------------------

val bookSubsectionTable =
    Table2("book_subsection",
           "subsection_id",
           "title")

typealias DB_BookSubsectionValue =
    RowValue2<PrimValue<BookSubsectionId>,
              PrimValue<BookSubsectionTitle>>


// BOOK REFERENCE
// ---------------------------------------------------------------------------------------------

val bookReferenceTable =
    Table4("book_reference",
           "book_id",
           "chapter_id",
           "section_id",
           "subsection_id")

typealias DB_BookReferenceValue =
    RowValue4<PrimValue<BookId>,
              PrimValue<BookChapterId>,
              MaybePrimValue<BookSectionId>,
              MaybePrimValue<BookSubsectionId>>


// BORDER
// ---------------------------------------------------------------------------------------------

val borderTable = Table4("border",
                         "top",
                         "right",
                         "bottom",
                         "left")

typealias DB_BorderValue =
    RowValue4<MaybePrimValue<BorderEdge>,
              MaybePrimValue<BorderEdge>,
              MaybePrimValue<BorderEdge>,
              MaybePrimValue<BorderEdge>>


// CAMPAIGN
// ---------------------------------------------------------------------------------------------

val campaignTable = Table5("campaign",
                           "campaign_id",
                           "engine",
                           "name",
                           "summary",
                           "game_id")

typealias DB_CampaignValue =
    RowValue5<PrimValue<CampaignId>,
              ProdValue<Engine>,
              PrimValue<CampaignName>,
              PrimValue<CampaignSummary>,
              PrimValue<GameId>>


// CHAPTER BUTTON FORMAT
// ---------------------------------------------------------------------------------------------

val chapterButtonFormatTable =
    Table4("chapter_button_format",
           "element_format",
           "index_format",
           "title_format",
           "summary_format")

typealias DB_ChapterButtonFormatValue =
    RowValue4<ProdValue<ElementFormat>,
              ProdValue<TextFormat>,
              ProdValue<TextFormat>,
              ProdValue<TextFormat>>


// DICE ROLL
// ---------------------------------------------------------------------------------------------

val diceRollTable = Table3("dice_roll",
                           "quantities",
                           "modifiers",
                           "roll_name")

typealias DB_DiceRollValue =
    RowValue3<PrimValue<DiceQuantitySet>,
              CollValue<RollModifier>,
              MaybePrimValue<DiceRollName>>


// DICE ROLL GROUP
// ---------------------------------------------------------------------------------------------

val diceRollGroupTable = Table2("dice_roll_group",
                                "roll_references",
                                "roll_name")

typealias DB_DiceRollGroupValue =
    RowValue2<PrimValue<DiceRollReferences>,
              MaybePrimValue<DiceRollGroupName>>


// DIVIDER
// ---------------------------------------------------------------------------------------------

val dividerTable = Table3("divider",
                          "color_theme",
                          "margins",
                          "thickness")

typealias DB_DividerValue =
    RowValue3<PrimValue<ColorTheme>,
              PrimValue<Spacing>,
              PrimValue<DividerThickness>>

// ENGINE
// ---------------------------------------------------------------------------------------------

val engineTable = Table7("engine",
                         "value_sets",
                         "mechanics",
                         "mechanic_categories",
                         "functions",
                         "programs",
                         "summations",
                         "procedures")

typealias DB_EngineValue =
    RowValue7<CollValue<ValueSet>,
              CollValue<Mechanic>,
              CollValue<MechanicCategory>,
              CollValue<Function>,
              CollValue<Program>,
              CollValue<Summation>,
              CollValue<Procedure>>


// ELEMENT FORMAT
// ---------------------------------------------------------------------------------------------

val elementFormatTable =
    Table9("element_format",
           "position",
           "height",
           "width",
           "padding",
           "margins",
           "background_color_theme",
           "corners",
           "alignment",
           "vertical_alignment")

typealias DB_ElementFormatValue =
    RowValue9<PrimValue<Position>,
              PrimValue<Height>,
              PrimValue<Width>,
              PrimValue<Spacing>,
              PrimValue<Spacing>,
              PrimValue<ColorTheme>,
              PrimValue<Corners>,
              PrimValue<Alignment>,
              PrimValue<VerticalAlignment>>


// FUNCTION
// ---------------------------------------------------------------------------------------------

val functionTable =
    Table5("function",
           "function_id",
           "label",
           "description",
           "type_signature",
           "tuples")

typealias DB_FunctionValue =
    RowValue5<PrimValue<FunctionId>,
              PrimValue<FunctionLabel>,
              PrimValue<FunctionDescription>,
              ProdValue<FunctionTypeSignature>,
              CollValue<Tuple>>


// FUNCTION TYPE SIGNATURE
// ---------------------------------------------------------------------------------------------

val functionTypeSignatureTable =
    Table6("function_type_signature",
           "parameter1_type",
           "parameter2_type",
           "parameter3_type",
           "parameter4_type",
           "parameter5_type",
           "result_type")

typealias DB_FunctionTypeSignatureValue =
    RowValue6<PrimValue<EngineValueType>,
              MaybePrimValue<EngineValueType>,
              MaybePrimValue<EngineValueType>,
              MaybePrimValue<EngineValueType>,
              MaybePrimValue<EngineValueType>,
              PrimValue<EngineValueType>>


// INVOCATION
// ---------------------------------------------------------------------------------------------

val invocationTable =
    Table2("invocation",
           "program_id",
           "parameters")

typealias DB_InvocationValue =
    RowValue2<PrimValue<ProgramId>,
              PrimValue<InvocationParameters>>


// GAME
// ---------------------------------------------------------------------------------------------

val gameTable =
    Table6("game",
           "game_id",
           "name",
           "summary",
           "authors",
           "engine",
           "book_ids")

typealias DB_GameValue =
    RowValue6<PrimValue<GameId>,
              PrimValue<GameName>,
              PrimValue<GameSummary>,
              CollValue<Author>,
              ProdValue<Engine>,
              PrimValue<GameBookIds>>


// GROUP
// ---------------------------------------------------------------------------------------------

//val groupTable =
//    Table3("group",
//           "format",
//           "index",
//           "rows")
//
//typealias DB_GroupValue =
//    RowValue3<ProdValue<GroupFormat>,
//              PrimValue<GroupIndex>,
//              CollValue<GroupRow>>


// GROUP FORMAT
// ---------------------------------------------------------------------------------------------

val groupFormatTable =
    Table2("group_format",
           "element_format",
           "border")

typealias DB_GroupFormatValue =
    RowValue2<ProdValue<ElementFormat>,
              MaybeProdValue<Border>>


// GROUP ROW
// ---------------------------------------------------------------------------------------------

val groupRowTable =
    Table2("group_row",
           "format",
           "index")

typealias DB_GroupRowValue =
    RowValue2<ProdValue<GroupRowFormat>,
              PrimValue<GroupRowIndex>>


// GROUP ROW FORMAT
// ---------------------------------------------------------------------------------------------

val groupRowFormatTable =
    Table3("group_row_format",
           "element_format",
           "has_columns",
           "border")

typealias DB_GroupRowFormatValue =
    RowValue3<ProdValue<ElementFormat>,
              PrimValue<GroupRowHasColumns>,
              MaybeProdValue<Border>>


// ICON
// ---------------------------------------------------------------------------------------------

val iconTable =
    Table3("icon",
           "icon_type",
           "element_format",
           "icon_format")


typealias DB_IconValue =
    RowValue3<PrimValue<IconType>,
              ProdValue<ElementFormat>,
              ProdValue<IconFormat>>


// ICON FORMAT
// ---------------------------------------------------------------------------------------------

val iconFormatTable =
    Table2("icon_format",
           "color_theme",
           "size")

typealias DB_IconFormatValue =
    RowValue2<PrimValue<ColorTheme>,
              PrimValue<IconSize>>


// MECHANIC
// ---------------------------------------------------------------------------------------------

val mechanicTable =
    Table9("mechanic",
           "mechanic_id",
           "label",
           "description",
           "summary",
           "annotation",
           "category_id",
           "mechanic_type",
           "requirements",
           "variables")

typealias DB_MechanicValue =
    RowValue9<PrimValue<MechanicId>,
              PrimValue<MechanicLabel>,
              PrimValue<MechanicDescription>,
              PrimValue<MechanicSummary>,
              MaybePrimValue<MechanicAnnotation>,
              PrimValue<MechanicCategoryId>,
              PrimValue<MechanicType>,
              PrimValue<MechanicRequirements>,
              CollValue<Variable>>


// MECHANIC CATEGORY
// ---------------------------------------------------------------------------------------------

val mechanicCategoryTable =
    Table3("mechanic_category",
           "category_id",
           "label",
           "description")

typealias DB_MechanicCategoryValue =
    RowValue3<PrimValue<MechanicCategoryId>,
              PrimValue<MechanicCategoryLabel>,
              PrimValue<MechanicCategoryDescription>>


// MESSAGE
// ---------------------------------------------------------------------------------------------

val messageTable =
    Table3("message",
           "template",
           "parts",
           "format")

typealias DB_MessageValue =
    RowValue3<PrimValue<MessageTemplate>,
              PrimValue<MessageParts>,
              ProdValue<TextFormat>>

// PAGE
// ---------------------------------------------------------------------------------------------

val pageTable =
    Table3("page",
           "page_name",
           "format",
           "index")

typealias DB_PageValue =
    RowValue3<PrimValue<PageName>,
              ProdValue<PageFormat>,
              PrimValue<PageIndex>>


// PAGE FORMAT
// ---------------------------------------------------------------------------------------------

val pageFormatTable =
    Table1("page_format",
           "element_format")

typealias DB_PageFormatValue =
    RowValue1<ProdValue<ElementFormat>>


// PROCEDURE
// ---------------------------------------------------------------------------------------------

val procedureTable =
    Table5("procedure",
           "procedure_id",
           "procedure_name",
           "updates",
           "description",
           "action_label")

typealias DB_ProcedureValue =
    RowValue5<PrimValue<ProcedureId>,
              PrimValue<ProcedureName>,
              PrimValue<ProcedureUpdates>,
              MaybeProdValue<Message>,
              PrimValue<ProcedureActionLabel>>


// PROGRAM
// ---------------------------------------------------------------------------------------------

val programTable =
    Table5("program",
           "program_id",
           "label",
           "description",
           "type_signature",
           "result_binding_name")

typealias DB_ProgramValue =
    RowValue5<PrimValue<ProgramId>,
              PrimValue<ProgramLabel>,
              PrimValue<ProgramDescription>,
              ProdValue<ProgramTypeSignature>,
//              CollValue<Statement>,
              PrimValue<StatementBindingName>>


// PROGRAM PARAMETER: BOOLEAN
// ---------------------------------------------------------------------------------------------

val programParameterBooleanTable =
    Table4("program_parameter_boolean",
           "name",
           "default_value",
           "label",
           "input_message")

typealias DB_ProgramParameterBooleanValue =
    RowValue4<PrimValue<ProgramParameterName>,
              MaybePrimValue<EngineValueBoolean>,
              PrimValue<ProgramParameterLabel>,
              ProdValue<Message>>


// PROGRAM PARAMETER: NUMBER
// ---------------------------------------------------------------------------------------------

val programParameterNumberTable =
    Table4("program_parameter_number",
           "name",
           "default_value",
           "label",
           "input_message")

typealias DB_ProgramParameterNumberValue =
    RowValue4<PrimValue<ProgramParameterName>,
              MaybePrimValue<EngineValueNumber>,
              PrimValue<ProgramParameterLabel>,
              ProdValue<Message>>


// PROGRAM PARAMETER: TEXT
// ---------------------------------------------------------------------------------------------

val programParameterTextTable =
    Table4("program_parameter_text",
           "name",
           "default_value",
           "label",
           "input_message")

typealias DB_ProgramParameterTextValue =
    RowValue4<PrimValue<ProgramParameterName>,
              MaybePrimValue<EngineValueText>,
              PrimValue<ProgramParameterLabel>,
              ProdValue<Message>>


// PROGRAM TYPE SIGNATURE
// ---------------------------------------------------------------------------------------------

val programTypeSignatureTable =
    Table2("program_type_signature",
           "parameters",
           "result")

typealias DB_ProgramTypeSignatureValue =
    RowValue2<CollValue<ProgramParameter>,
              PrimValue<EngineValueType>>


// ROLL MODIFIER
// ---------------------------------------------------------------------------------------------

val rollModifierTable =
    Table2("roll_modifier",
           "value",
           "modifier_name")

typealias DB_RollModifierValue =
    RowValue2<PrimValue<RollModifierValue>,
              MaybePrimValue<RollModifierName>>



// SECTION
// ---------------------------------------------------------------------------------------------

val sectionTable =
    Table3("section",
           "section_name",
           "pages",
           "icon")

typealias DB_SectionValue =
    RowValue3<PrimValue<SectionName>,
              CollValue<Page>,
              PrimValue<IconType>>


// SESSION
// ---------------------------------------------------------------------------------------------

//val sheetSessionTable =
//    Table4("sheet_session",
//           "name",
//           "time_last_active",
//           "active_sheet_id",
//           "sheet_records")
//
//typealias DB_SheetSessionValue =
//    RowValue4<PrimValue<SessionName>,
//              PrimValue<SessionLastActiveTime>,
//              MaybePrimValue<SheetId>,
//              CollValue<SheetSessionRecord>>
//

// SESSION SHEET RECORD
// ---------------------------------------------------------------------------------------------
//val sessionSheetRecordTable =
//    Table3("session_sheet_record",
//           "sheet_id",
//           "session_index",
//           "time_last_active")
//
//typealias DB_SessionSheetRecordValue =
//    RowValue3<PrimValue<SheetId>,
//              PrimValue<SessionRecordIndex>,
//              PrimValue<SheetLastActiveTime>>


// SETTINGS
// ---------------------------------------------------------------------------------------------

val sheetSettingsTable =
    Table3("sheet_settings",
            "theme_id",
            "sheet_name",
            "sheet_summary")

typealias DB_SheetSettingsValue =
    RowValue3<PrimValue<ThemeId>,
              PrimValue<SheetName>,
              PrimValue<SheetSummary>>


// SHEET
// ---------------------------------------------------------------------------------------------

val sheetTable =
    Table6("sheet",
           "sheet_id",
           "campaign_id",
           "sections",
           "engine",
           "variables",
           "settings")

typealias DB_SheetValue =
    RowValue6<PrimValue<SheetId>,
              PrimValue<CampaignId>,
              CollValue<Section>,
              ProdValue<Engine>,
              CollValue<Variable>,
              ProdValue<Settings>>


// STATEMENT
// ---------------------------------------------------------------------------------------------

val statementTable =
    Table7("statement",
           "binding_name",
           "function_id",
           "parameter_1",
           "parameter_2",
           "parameter_3",
           "parameter_4",
           "parameter_5")

//typealias DB_StatementValue =
//    RowValue7<PrimValue<StatementBindingName>,
//              PrimValue<FunctionId>,
//              MaybeSumValue<StatementParameter>,
//              MaybeSumValue<StatementParameter>,
//              MaybeSumValue<StatementParameter>,
//              MaybeSumValue<StatementParameter>,
//              MaybeSumValue<StatementParameter>>


// SUMMATION
// ---------------------------------------------------------------------------------------------

val summationTable =
    Table2("summationWithId",
           "summation_id",
           "summation_name")

typealias DB_SummationValue =
    RowValue2<PrimValue<SummationId>,
              PrimValue<SummationName>>


// TERM: NUMBER
// ---------------------------------------------------------------------------------------------

val summationTermNumberTable =
    Table2("summation_term_number",
           "term_name",
           "value_reference")

typealias DB_SummationTermNumberValue =
    RowValue2<MaybePrimValue<TermName>,
             SumValue<NumberReference>>

// TERM: LINEAR COMBINATION
// ---------------------------------------------------------------------------------------------

val summationTermLinearCombinationTable =
    Table5("summation_term_linear_combination",
           "term_name",
           "variable_tag",
           "value_relation",
           "weight_relation",
           "filter_relation")

typealias DB_SummationTermLinearCombinationValue =
    RowValue5<MaybePrimValue<TermName>,
              PrimValue<VariableTag>,
              MaybePrimValue<VariableRelation>,
              MaybePrimValue<VariableRelation>,
              MaybePrimValue<VariableRelation>>


// TERM: DICE ROLL
// ---------------------------------------------------------------------------------------------

val summationTermDiceRollTable =
    Table2("summation_term_dice_roll",
            "term_name",
            "value_reference")

//typealias DB_SummationTermDiceRollValue =
//    RowValue2<MaybePrimValue<TermName>>
//              SumValue<DiceRollReference>>


// TERM: CONDITIONAL
// ---------------------------------------------------------------------------------------------

val summationTermConditionalTable =
    Table4("summation_term_conditional",
           "term_name",
           "conditional_reference",
           "true_reference",
           "false_reference")

typealias DB_SummationTermConditionalValue =
    RowValue4<MaybePrimValue<TermName>,
              SumValue<BooleanReference>,
              SumValue<NumberReference>,
              SumValue<NumberReference>>


// TEXT FORMAT
// ---------------------------------------------------------------------------------------------

val textFormatTable =
    Table9("text_format",
           "color_theme",
           "size",
           "font",
           "font_style",
           "is_underlined",
           "number_format",
           "roll_format",
           "icon_format",
           "element_format")

typealias DB_TextFormatValue =
    RowValue9<PrimValue<ColorTheme>,
              PrimValue<TextSize>,
              PrimValue<TextFont>,
              PrimValue<TextFontStyle>,
              PrimValue<IsUnderlined>,
              PrimValue<NumberFormat>,
              PrimValue<RollFormat>,
              ProdValue<IconFormat>,
              ProdValue<ElementFormat>>


//// projection
//// restriction
//
//
////class Query
////{
////
////}
////
////
////typealias MyRow = Row2<Prim<TextValue>, TextValue, Prim<NumberValue>, NumberValue>
////
////
////fun query(f : Query.() -> Unit) : Query {
////    val query = Query()
////    query.f()
////    return query
////}
////
////
////fun query(myRow : MyRow) : Query Row = query {
////    val (_, x, _) = myRow
////
////}
//
//
//
//
//
////val query = Query { row : MyRow -> row
////
////}
//
//
// THEME
// ---------------------------------------------------------------------------------------------

val themeTable =
    Table3("theme",
           "theme_id",
           "palette",
           "ui_colors")

typealias DB_ThemeValue =
    RowValue3<PrimValue<ThemeId>,
              PrimValue<ThemeColorSet>,
              ProdValue<UIColors>>


// TUPLE
// ---------------------------------------------------------------------------------------------

val tupleTable =
    Table6("tuple",
           "parameter1",
           "parameter2",
           "parameter3",
           "parameter4",
           "parameter5",
           "result")

typealias DB_TupleValue =
    RowValue6<SumValue<EngineValue>,
              MaybeSumValue<EngineValue>,
              MaybeSumValue<EngineValue>,
              MaybeSumValue<EngineValue>,
              MaybeSumValue<EngineValue>,
              SumValue<EngineValue>>


// UI COLORS
// ---------------------------------------------------------------------------------------------

val uiColorsTable =
    Table9("ui_colors",
            "toolbar_background_color_id",
            "toolbar_icons_color_id",
            "toolbar_title_color_id",
            "tab_bar_background_color_id",
            "tab_text_normal_color_id",
            "tab_text_selected_color_id",
            "tab_underline_color_id",
            "bottom_bar_background_color_id",
            "bottom_bar_nav_color_id")


typealias DB_UIColorsValue =
    RowValue9<PrimValue<ColorId>,
               PrimValue<ColorId>,
               PrimValue<ColorId>,
               PrimValue<ColorId>,
               PrimValue<ColorId>,
               PrimValue<ColorId>,
               PrimValue<ColorId>,
               PrimValue<ColorId>,
               PrimValue<ColorId>>

// WIDGET: ACTION
// ---------------------------------------------------------------------------------------------

val widgetActionTable =
    Table4("widget_action",
           "widget_id",
           "format",
           "procedure_id",
           "description")

typealias DB_WidgetActionValue =
    RowValue4<PrimValue<WidgetId>,
              ProdValue<ActionWidgetFormat>,
              PrimValue<ProcedureId>,
              MaybePrimValue<ActionWidgetDescription>>


// WIDGET: ACTION > FORMAT
// ---------------------------------------------------------------------------------------------

val widgetActionFormatTable =
    Table5("widget_roll_format",
           "widget_format",
           "view_type",
           "description_format",
           "button_format",
           "button_icon")

typealias DB_WidgetActionFormatValue =
    RowValue5<ProdValue<WidgetFormat>,
              PrimValue<ActionWidgetViewType>,
              ProdValue<TextFormat>,
              ProdValue<TextFormat>,
              MaybePrimValue<IconType>>


// WIDGET: BOOLEAN
// ---------------------------------------------------------------------------------------------

val widgetBooleanTable =
    Table2("widget_boolean",
           "widget_id",
           "value_variables_reference")

typealias DB_WidgetBooleanValue =
    RowValue2<PrimValue<WidgetId>,
              PrimValue<VariableReference>>


// WIDGET: BOOLEAN > FORMAT
// ---------------------------------------------------------------------------------------------

val widgetBooleanFormatTable =
    Table8("widget_boolean_format",
           "widget_format",
           "view_type",
           "true_format",
           "false_format",
           "true_text",
           "false_text",
           "true_icon",
           "false_icon")

typealias DB_WidgetBooleanFormatValue =
    RowValue8<ProdValue<WidgetFormat>,
              PrimValue<BooleanWidgetViewType>,
              ProdValue<TextFormat>,
              ProdValue<TextFormat>,
              PrimValue<TrueText>,
              PrimValue<FalseText>,
              MaybeProdValue<Icon>,
              MaybeProdValue<Icon>>

// WIDGET: EXPANDER
// ---------------------------------------------------------------------------------------------
//
//val widgetExpanderTable =
//    Table3("widget_expander",
//           "widget_id",
//           "header",
//           "groups")
//
//
//typealias DB_WidgetExpanderValue =
//    RowValue3<PrimValue<WidgetId>,
//              PrimValue<ExpanderWidgetLabel>,
//              CollValue<Group>>


// WIDGET: EXPANDER > FORMAT
// ---------------------------------------------------------------------------------------------

val widgetExpanderFormatTable =
    Table8("widget_expander_format",
           "widget_format",
           "view_type",
           "header_open_format",
           "header_closed_format",
           "header_label_open_format",
           "header_label_closed_format",
           "header_icon_open_format",
           "header_icon_closed_format")


typealias DB_WidgetExpanderFormatValue =
    RowValue8<ProdValue<WidgetFormat>,
              PrimValue<ExpanderWidgetViewType>,
              ProdValue<TextFormat>,
              ProdValue<TextFormat>,
              ProdValue<TextFormat>,
              ProdValue<TextFormat>,
              ProdValue<TextFormat>,
              ProdValue<TextFormat>>


// WIDGET: IMAGE
// ---------------------------------------------------------------------------------------------

val widgetImageTable =
    Table3("widget_image",
           "widget_id",
           "format",
           "official_images")

typealias DB_WidgetImageValue =
    RowValue3<PrimValue<WidgetId>,
              ProdValue<ImageWidgetFormat>,
              PrimValue<OfficialImageIdList>>


// WIDGET: IMAGE FORMAT
// ---------------------------------------------------------------------------------------------

val widgetImageFormatTable =
    Table1("widget_image_format",
           "widget_format")

typealias DB_WidgetImageFormatValue =
    RowValue1<ProdValue<WidgetFormat>>


// WIDGET: LIST
// ---------------------------------------------------------------------------------------------

val widgetListTable =
    Table3("widget_list",
           "widget_id",
           "format",
           "values_variable_id")

typealias DB_WidgetListValue =
    RowValue3<PrimValue<WidgetId>,
              ProdValue<ListWidgetFormat>,
              PrimValue<VariableId>>


// WIDGET: LIST > FORMAT
// ---------------------------------------------------------------------------------------------

val widgetListFormatTable =
    Table5("widget_list_format",
           "widget_format",
           "view_type",
           "item_format",
           "description_format",
           "annotation_format")

typealias DB_WidgetListFormatValue =
    RowValue5<ProdValue<WidgetFormat>,
              PrimValue<ListViewType>,
              ProdValue<TextFormat>,
              ProdValue<TextFormat>,
              ProdValue<TextFormat>>



// WIDGET: LOG
// ---------------------------------------------------------------------------------------------

val widgetLogTable =
    Table3("widget_log",
           "widget_id",
           "format",
           "entries")

typealias DB_WidgetLogValue =
    RowValue3<PrimValue<WidgetId>,
              ProdValue<LogWidgetFormat>,
              CollValue<LogEntry>>


// WIDGET: LOG > ENTRY
// ---------------------------------------------------------------------------------------------

val widgetLogEntryTable =
    Table5("widget_log_entry",
           "title",
           "date",
           "author",
           "summary",
           "text")

typealias DB_WidgetLogEntryValue =
    RowValue5<PrimValue<EntryTitle>,
              PrimValue<EntryDate>,
              PrimValue<EntryAuthor>,
              MaybePrimValue<EntrySummary>,
              PrimValue<EntryText>>


// WIDGET: LOG > FORMAT
// ---------------------------------------------------------------------------------------------

val widgetLogFormatTable =
    Table3("widget_log_format",
           "format",
           "entry_format",
           "entry_view_type")

typealias DB_WidgetLogFormatValue =
    RowValue3<ProdValue<WidgetFormat>,
              ProdValue<LogEntryFormat>,
              PrimValue<EntryViewType>>


// WIDGET: LOG > ENTRY FORMAT
// ---------------------------------------------------------------------------------------------

val widgetLogEntryFormatTable =
    Table5("widget_log_entry_format",
           "title_format",
           "author_format",
           "summary_format",
           "body_format",
           "entry_format")

typealias DB_WidgetLogEntryFormatValue =
    RowValue5<ProdValue<TextFormat>,
              ProdValue<TextFormat>,
              ProdValue<TextFormat>,
              ProdValue<TextFormat>,
              ProdValue<TextFormat>>


// WIDGET: MECHANIC
// ---------------------------------------------------------------------------------------------

val widgetMechanicTable =
    Table3("widget_mechanic",
           "widget_id",
           "format",
           "category_reference")

typealias DB_WidgetMechanicValue =
    RowValue3<PrimValue<WidgetId>,
              ProdValue<MechanicWidgetFormat>,
              PrimValue<MechanicCategoryReference>>


// WIDGET: MECHANIC > FORMAT
// ---------------------------------------------------------------------------------------------

val widgetMechanicFormatTable =
    Table8("widget_mechanic_format",
           "widget_format",
           "view_type",
           "mechanic_format",
           "header_format",
           "mechanic_header_format",
           "mechanic_summary_format",
           "option_element_format",
           "option_label_format")

typealias DB_WidgetMechanicFormatValue =
    RowValue8<ProdValue<WidgetFormat>,
              PrimValue<MechanicWidgetViewType>,
              ProdValue<ElementFormat>,
              ProdValue<TextFormat>,
              ProdValue<TextFormat>,
              ProdValue<TextFormat>,
              ProdValue<ElementFormat>,
              ProdValue<TextFormat>>


// WIDGET: NUMBER
// ---------------------------------------------------------------------------------------------

val widgetNumberTable =
    Table4("widget_number",
           "widget_id",
           "format",
           "value_variable_id",
           "inside_label")

typealias DB_WidgetNumberValue =
    RowValue4<PrimValue<WidgetId>,
              ProdValue<NumberWidgetFormat>,
              PrimValue<VariableId>,
              MaybePrimValue<NumberWidgetLabel>>


// WIDGET: NUMBER > FORMAT
// ---------------------------------------------------------------------------------------------

val widgetNumberFormatTable =
    Table5("widget_number_format",
           "widget_format",
           "inside_label_format",
           "outside_label_format",
           "value_format",
           "number_format")

typealias DB_WidgetNumberFormatValue =
    RowValue5<ProdValue<WidgetFormat>,
              ProdValue<TextFormat>,
              ProdValue<TextFormat>,
              ProdValue<TextFormat>,
              PrimValue<NumberFormat>>


// WIDGET FORMAT
// ---------------------------------------------------------------------------------------------

val widgetFormatTable =
    Table3("widget_format",
           "width",
           "column",
           "element_format")

typealias DB_WidgetFormatValue =
    RowValue3<PrimValue<WidgetWidth>,
              PrimValue<RowColumn>,
              ProdValue<ElementFormat>>


// WIDGET: POINTS
// ---------------------------------------------------------------------------------------------

val widgetPointsTable =
    Table5("widget_points",
           "widget_id",
           "format",
           "limit_value_variable_id",
           "current_value_variable_id",
           "label")

typealias DB_WidgetPointsValue =
    RowValue5<PrimValue<WidgetId>,
              ProdValue<PointsWidgetFormat>,
              PrimValue<VariableId>,
              PrimValue<VariableId>,
              MaybePrimValue<PointsWidgetLabel>>


// WIDGET: POINTS > FORMAT
// ---------------------------------------------------------------------------------------------

val widgetPointsFormatTable =
    Table6("widget_points_format",
           "widget_format",
           "limit_text_format",
           "current_text_format",
           "label_text_format",
           "info_format",
           "bar_format")

typealias DB_WidgetPointsFormatValue =
    RowValue6<ProdValue<WidgetFormat>,
              ProdValue<TextFormat>,
              ProdValue<TextFormat>,
              ProdValue<TextFormat>,
              ProdValue<PointsWidgetInfoFormat>,
              ProdValue<PointsWidgetBarFormat>>

// WIDGET: POINTS > INFO FORMAT
// ---------------------------------------------------------------------------------------------

val widgetPointsInfoFormatTable =
    Table2("widget_points_info_format",
           "style",
           "format")

typealias DB_WidgetPointsInfoFormatValue =
    RowValue2<PrimValue<PointsInfoStyle>,
              ProdValue<TextFormat>>


// WIDGET: POINTS > BAR FORMAT
// ---------------------------------------------------------------------------------------------

val widgetPointsBarFormatTable =
    Table8("widget_points_bar_format",
           "element_format",
           "style",
           "height",
           "limit_format",
           "current_format",
           "counter_active_icon",
           "counter_active_text",
           "counter_inactive_text")

typealias DB_WidgetPointsBarFormatValue =
    RowValue8<ProdValue<ElementFormat>,
              PrimValue<PointsBarStyle>,
              PrimValue<PointsBarHeight>,
              ProdValue<TextFormat>,
              ProdValue<TextFormat>,
              MaybePrimValue<IconType>,
              MaybePrimValue<PointsWidgetCounterActiveText>,
              MaybePrimValue<PointsWidgetCounterActiveText>>


// WIDGET: QUOTE
// ---------------------------------------------------------------------------------------------

val widgetQuoteTable =
    Table4("widget_quote",
           "widget_id",
           "format",
           "quote_variable_id",
           "source_variable_id")

typealias DB_WidgetQuoteValue =
    RowValue4<PrimValue<WidgetId>,
              ProdValue<QuoteWidgetFormat>,
              PrimValue<VariableId>,
              MaybePrimValue<VariableId>>


// WIDGET: QUOTE > FORMAT
// ---------------------------------------------------------------------------------------------

val widgetQuoteFormatTable =
    Table5("widget_quote_format",
           "widget_format",
           "view_type",
           "quote_format",
           "source_format",
           "icon_format")

typealias DB_WidgetQuoteFormatValue =
    RowValue5<ProdValue<WidgetFormat>,
              PrimValue<QuoteViewType>,
              ProdValue<TextFormat>,
              ProdValue<TextFormat>,
              ProdValue<IconFormat>>


// WIDGET: ROLL
// ---------------------------------------------------------------------------------------------

val widgetRollTable =
    Table5("widget_roll",
           "widget_id",
           "format",
           "roll_group",
           "description",
           "result_description")

typealias DB_WidgetRollValue =
    RowValue5<PrimValue<WidgetId>,
              ProdValue<RollWidgetFormat>,
              ProdValue<DiceRollGroup>,
              MaybePrimValue<RollWidgetDescription>,
              MaybePrimValue<RollWidgetResultDescription>>


// WIDGET: ROLL > FORMAT
// ---------------------------------------------------------------------------------------------

val widgetRollFormatTable =
    Table6("widget_roll_format",
           "widget_format",
           "view_type",
           "description_format",
           "button_format",
           "roll_text_location",
           "roll_text_format")

typealias DB_WidgetRollFormatValue =
    RowValue6<ProdValue<WidgetFormat>,
              PrimValue<RollWidgetViewType>,
              ProdValue<TextFormat>,
              ProdValue<TextFormat>,
              PrimValue<RollTextLocation>,
              ProdValue<TextFormat>>


// WIDGET: STORY
// ---------------------------------------------------------------------------------------------

val widgetStoryTable =
    Table3("widget_story",
           "widget_id",
           "format",
           "story")

typealias DB_WidgetStoryValue =
    RowValue3<PrimValue<WidgetId>,
              ProdValue<StoryWidgetFormat>,
              CollValue<StoryPart>>


// WIDGET: STORY > FORMAT
// ---------------------------------------------------------------------------------------------

val widgetStoryFormatTable =
    Table4("widget_story_format",
           "widget_format",
           "line_height",
           "line_spaceing",
           "text_format")

typealias DB_WidgetStoryFormatValue =
    RowValue4<ProdValue<WidgetFormat>,
              MaybePrimValue<LineHeight>,
              MaybePrimValue<LineSpacing>,
              ProdValue<TextFormat>>


// WIDGET: STORY > PART > FORMAT
// ---------------------------------------------------------------------------------------------

val widgetStoryPartFormatTable =
    Table2("widget_story_part_format",
           "highlight_skew",
           "highlight_corner_radius")


typealias DB_WidgetStoryPartFormatValue =
    RowValue2<PrimValue<HighlightSkew>,
              PrimValue<HighlightCornerRadius>>


// WIDGET: STORY > PART: SPAN
// ---------------------------------------------------------------------------------------------

val widgetStoryPartSpanTable =
    Table3("widget_story_part_span",
            "format",
            "text_format",
            "text")


typealias DB_WidgetStoryPartSpanValue =
    RowValue3<ProdValue<StoryPartFormat>,
              ProdValue<TextFormat>,
              PrimValue<StoryPartText>>


// WIDGET: STORY > PART: VARIABLE
// ---------------------------------------------------------------------------------------------

val widgetStoryPartVariableTable =
    Table4("widget_story_part_variable",
           "format",
           "text_format",
           "variable_id",
           "numeric_editor_type")

typealias DB_WidgetStoryPartVariableValue =
    RowValue4<ProdValue<StoryPartFormat>,
              ProdValue<TextFormat>,
              PrimValue<VariableId>,
              PrimValue<NumericEditorType>>


// WIDGET: STORY > PART: ICON
// ---------------------------------------------------------------------------------------------

val widgetStoryPartIconTable =
    Table2("widget_story_part_icon",
           "format",
           "icon")

typealias DB_WidgetStoryPartIconValue =
    RowValue2<ProdValue<StoryPartFormat>,
              ProdValue<Icon>>


// WIDGET: STORY > PART: ACTION
// ---------------------------------------------------------------------------------------------

val widgetStoryPartActionTable =
    Table6("widget_story_part_action",
           "format",
           "text",
           "action",
           "text_format",
           "icon_format",
           "show_procedure_dialog")


typealias DB_WidgetStoryPartActionValue =
    RowValue6<ProdValue<StoryPartFormat>,
              PrimValue<StoryPartText>,
              ProdValue<Action>,
              ProdValue<TextFormat>,
              ProdValue<IconFormat>,
              PrimValue<ShowProcedureDialog>>


// WIDGET: TABLE
// ---------------------------------------------------------------------------------------------

val widgetTableTable =
    Table4("widget_table",
           "widget_id",
           "columns",
           "rows",
           "sort")

typealias DB_WidgetTableValue =
    RowValue4<PrimValue<WidgetId>,
              CollValue<TableWidgetColumn>,
              CollValue<TableWidgetRow>,
              MaybePrimValue<TableSort>>


// WIDGET: TABLE > CELL: BOOLEAN
// ---------------------------------------------------------------------------------------------

val widgetTableCellBooleanTable =
    Table2("widget_table_cell_boolean",
           "format",
           "variable_value")

typealias DB_WidgetTableCellBooleanValue =
    RowValue2<ProdValue<BooleanCellFormat>,
              SumValue<BooleanVariableValue>>


// WIDGET: TABLE > CELL: BOOLEAN > FORMAT
// ---------------------------------------------------------------------------------------------

val widgetTableCellBooleanFormatTable =
    Table5("widget_table_cell_boolean_format",
           "element_format",
           "true_format",
           "false_format",
           "show_true_icon",
           "show_false_icon")

typealias DB_WidgetTableCellBooleanFormatValue =
    RowValue5<MaybeProdValue<ElementFormat>,
              MaybeProdValue<TextFormat>,
              MaybeProdValue<TextFormat>,
              MaybePrimValue<ShowTrueIcon>,
              MaybePrimValue<ShowFalseIcon>>


// WIDGET: TABLE > CELL: NUMBER
// ---------------------------------------------------------------------------------------------

val widgetTableCellNumberTable =
    Table4("widget_table_cell_number",
           "format",
           "variable_value",
           "editor_type",
           "action")

typealias DB_WidgetTableCellNumberValue =
    RowValue4<ProdValue<NumberCellFormat>,
              SumValue<NumberVariableValue>,
              MaybePrimValue<NumericEditorType>,
              MaybeProdValue<Action>>


// WIDGET: TABLE > CELL: NUMBER > FORMAT
// ---------------------------------------------------------------------------------------------

val widgetTableCellNumberFormatTable =
        Table1("widget_table_cell_number_format",
                "text_format")

typealias DB_WidgetTableCellNumberFormatValue =
    RowValue1<MaybeProdValue<TextFormat>>


// WIDGET: TABLE > CELL: TEXT
// ---------------------------------------------------------------------------------------------

val widgetTableCellTextTable =
        Table3("widget_table_cell_text",
               "format",
               "variable_value",
               "action")

typealias DB_WidgetTableCellTextValue =
    RowValue3<ProdValue<TextCellFormat>,
              SumValue<TextVariableValue>,
              MaybeProdValue<Action>>


// WIDGET: TABLE > CELL: TEXT > FORMAT
// ---------------------------------------------------------------------------------------------

val widgetTableCellTextFormatTable =
        Table1("widget_table_cell_text_format",
               "text_format")

typealias DB_WidgetTableCellTextFormatValue =
    RowValue1<MaybeProdValue<TextFormat>>


// WIDGET: TABLE > COLUMN: BOOLEAN
// ---------------------------------------------------------------------------------------------

val widgetTableColumnBooleanTable =
    Table5("widget_table_column_boolean",
           "column_name",
           "variable_prefix",
           "is_column_namespaced",
           "default_value",
           "format")

typealias DB_WidgetTableColumnBooleanValue =
    RowValue5<PrimValue<ColumnName>,
              PrimValue<ColumnVariablePrefix>,
              PrimValue<IsColumnNamespaced>,
              SumValue<BooleanVariableValue>,
              ProdValue<BooleanColumnFormat>>


// WIDGET: TABLE > COLUMN: BOOLEAN > FORMAT
// ---------------------------------------------------------------------------------------------

val widgetTableColumnBooleanFormatTable =
    Table7("widget_table_column_boolean_format",
           "column_name",
           "true_format",
           "false_format",
           "true_text",
           "false_text",
           "show_true_icon",
           "show_false_icon")

typealias DB_WidgetTableColumnBooleanFormatValue =
    RowValue7<ProdValue<ColumnFormat>,
              MaybeProdValue<TextFormat>,
              MaybeProdValue<TextFormat>,
              PrimValue<ColumnTrueText>,
              PrimValue<ColumnFalseText>,
              PrimValue<ShowTrueIcon>,
              PrimValue<ShowFalseIcon>>


// WIDGET: TABLE > COLUMN: NUMBER
// ---------------------------------------------------------------------------------------------

val widgetTableColumnNumberTable =
    Table7("widget_table_column_number",
           "column_name",
           "variable_prefix",
           "is_column_namespaced",
           "default_value",
           "format",
           "action",
           "editor_type")

typealias DB_WidgetTableColumnNumberValue =
    RowValue7<PrimValue<ColumnName>,
              PrimValue<ColumnVariablePrefix>,
              PrimValue<IsColumnNamespaced>,
              SumValue<NumberVariableValue>,
              ProdValue<NumberColumnFormat>,
              MaybeProdValue<Action>,
              PrimValue<NumericEditorType>>


// WIDGET: TABLE > COLUMN: NUMBER > FORMAT
// ---------------------------------------------------------------------------------------------

val widgetTableColumnNumberFormatTable =
    Table1("widget_table_column_number_format",
           "column_format")

typealias DB_WidgetTableColumnNumberFormatValue =
    RowValue1<ProdValue<ColumnFormat>>


// WIDGET: TABLE > COLUMN: TEXT
// ---------------------------------------------------------------------------------------------

val widgetTableColumnTextTable =
    Table7("widget_table_column_text",
           "column_name",
           "variable_prefix",
           "is_column_namespaced",
           "default_value",
           "format",
           "action",
           "defines_namespace")

typealias DB_WidgetTableColumnTextValue =
    RowValue7<PrimValue<ColumnName>,
              PrimValue<ColumnVariablePrefix>,
              PrimValue<IsColumnNamespaced>,
              SumValue<TextVariableValue>,
              ProdValue<TextColumnFormat>,
              MaybeProdValue<Action>,
              PrimValue<DefinesNamespace>>


// WIDGET: TABLE > COLUMN: TEXT > FORMAT
// ---------------------------------------------------------------------------------------------

val widgetTableColumnTextFormatTable =
    Table1("widget_table_column_text_format",
           "column_format")

typealias DB_WidgetTableColumnTextFormatValue =
    RowValue1<ProdValue<ColumnFormat>>


// WIDGET: TABLE > COLUMN > FORMAT
// ---------------------------------------------------------------------------------------------

val widgetTableColumnFormatTable =
    Table2("widget_table_column_format",
           "text_format",
           "width")

typealias DB_WidgetTableColumnFormatValue =
    RowValue2<ProdValue<TextFormat>,
              PrimValue<ColumnWidth>>


// WIDGET: TABLE > FORMAT
// ---------------------------------------------------------------------------------------------

val widgetTableFormatTable =
    Table4("widget_table_format",
           "widget_format",
           "header_format",
           "row_format",
           "cell_height")

typealias DB_WidgetTableFormatValue =
    RowValue4<ProdValue<WidgetFormat>,
              ProdValue<TableWidgetRowFormat>,
              ProdValue<TableWidgetRowFormat>,
              PrimValue<Height>>


// WIDGET: TABLE > ROW
// ---------------------------------------------------------------------------------------------

val widgetTableRowTable =
    Table2("widget_table_row",
           "format",
           "cells")

typealias DB_WidgetTableRowValue =
    RowValue2<ProdValue<TableWidgetRowFormat>,
              CollValue<TableWidgetCell>>


// WIDGET: TABLE > ROW > FORMAT
// ---------------------------------------------------------------------------------------------

val widgetTableRowFormatTable =
    Table1("widget_table_row_format",
           "text_format")

typealias DB_WidgetTableRowFormatValue =
    RowValue1<ProdValue<TextFormat>>


// WIDGET: TEXT
// ---------------------------------------------------------------------------------------------

val widgetTextTable =
    Table3("widget_text",
           "widget_id",
           "format",
           "value_variable_id")

typealias DB_WidgetTextValue =
    RowValue3<PrimValue<WidgetId>,
              ProdValue<TextWidgetFormat>,
              PrimValue<VariableId>>


// WIDGET: TEXT > FORMAT
// ---------------------------------------------------------------------------------------------

val widgetTextFormatTable =
    Table4("widget_text_format",
           "widget_format",
           "inside_label_format",
           "outside_label_format",
           "value_format")

typealias DB_WidgetTextFormat =
    RowValue4<ProdValue<WidgetFormat>,
              ProdValue<TextFormat>,
              ProdValue<TextFormat>,
              ProdValue<TextFormat>>


// VALUE: NUMBER
// ---------------------------------------------------------------------------------------------

val valueNumberTable =
    Table4("value_number",
           "value_id",
           "description",
           "variables",
           "value")

typealias DB_ValueNumberValue =
    RowValue4<PrimValue<ValueId>,
              PrimValue<ValueDescription>,
              CollValue<Variable>,
              PrimValue<NumberValue>>


// VALUE: TEXT
// ---------------------------------------------------------------------------------------------

val valueTextTable =
    Table4("value_text",
           "value_id",
           "description",
           "variables",
           "value")

typealias DB_ValueTextValue =
    RowValue4<PrimValue<ValueId>,
              PrimValue<ValueDescription>,
              CollValue<Variable>,
              PrimValue<TextValue>>


// VALUE SET: BASE
// ---------------------------------------------------------------------------------------------

val valueSetBaseTable =
    Table6("value_set_base",
           "value_set_id",
           "label",
           "label_singular",
           "description",
           "value_type",
           "values")

typealias DB_ValueSetBaseValue =
    RowValue6<PrimValue<ValueSetId>,
              PrimValue<ValueSetLabel>,
              PrimValue<ValueSetLabelSingular>,
              PrimValue<ValueSetDescription>,
              PrimValue<ValueType>,
              CollValue<Value>>


// VALUE SET: COMPOUND
// ---------------------------------------------------------------------------------------------

val valueSetCompoundTable =
    Table6("value_set_compound",
           "value_set_id",
           "label",
           "label_singular",
           "description",
           "value_type",
           "value_set_ids")

typealias DB_ValueSetCompoundValue =
    RowValue6<PrimValue<ValueSetId>,
              PrimValue<ValueSetLabel>,
              PrimValue<ValueSetLabelSingular>,
              PrimValue<ValueSetDescription>,
              PrimValue<ValueType>,
              PrimValue<ValueSetIdSet>>


// VARIABLE: BOOLEAN
// ---------------------------------------------------------------------------------------------

val variableBooleanTable =
    Table5("variable_boolean",
           "variable_id",
           "label",
           "description",
           "tags",
           "variable_value")

typealias DB_VariableBooleanValue =
    RowValue5<PrimValue<VariableId>,
              PrimValue<VariableLabel>,
              PrimValue<VariableDescription>,
              PrimValue<VariableTagSet>,
              SumValue<BooleanVariableValue>>


// VARIABLE: DICE ROLL
// ---------------------------------------------------------------------------------------------

val variableDiceRollTable =
    Table4("variable_dice_roll",
           "variable_id",
           "label",
           "description",
           "tags")

typealias DB_VariableDiceRollValue =
    RowValue4<PrimValue<VariableId>,
              PrimValue<VariableLabel>,
              PrimValue<VariableDescription>,
              PrimValue<VariableTagSet>>


// VARIABLE: NUMBER
// ---------------------------------------------------------------------------------------------

val variableNumberTable =
    Table5("variable_number",
           "variable_id",
           "label",
           "description",
           "tags",
           "variable_value")

typealias DB_VariableNumberValue =
    RowValue5<PrimValue<VariableId>,
              PrimValue<VariableLabel>,
              PrimValue<VariableDescription>,
              PrimValue<VariableTagSet>,
              SumValue<NumberVariableValue>>


// VARIABLE: NUMBER LIST
// ---------------------------------------------------------------------------------------------

val variableNumberListTable =
    Table6("variable_list_number",
           "variable_id",
           "label",
           "description",
           "tags",
           "variable_value",
           "value_set_id")

typealias DB_VariableNumberListValue =
    RowValue6<PrimValue<VariableId>,
              PrimValue<VariableLabel>,
              PrimValue<VariableDescription>,
              PrimValue<VariableTagSet>,
              SumValue<NumberListVariableValue>,
              MaybePrimValue<ValueSetId>>


// VARIABLE: TEXT
// ---------------------------------------------------------------------------------------------

val variableTextTable =
    Table5("variable_text",
           "variable_id",
           "label",
           "description",
           "tags",
           "variable_value")

typealias DB_VariableTextValue =
    RowValue5<PrimValue<VariableId>,
              PrimValue<VariableLabel>,
              PrimValue<VariableDescription>,
              PrimValue<VariableTagSet>,
              SumValue<TextVariableValue>>

// VARIABLE: TEXT LIST
// ---------------------------------------------------------------------------------------------

val variableTextListTable =
    Table6("variable_list_text",
           "variable_id",
           "label",
           "description",
           "tags",
           "variable_value",
           "value_set_id")

typealias DB_VariableTextListValue =
    RowValue6<PrimValue<VariableId>,
              PrimValue<VariableLabel>,
              PrimValue<VariableDescription>,
              PrimValue<VariableTagSet>,
              SumValue<TextListVariableValue>,
              MaybePrimValue<ValueSetId>>

