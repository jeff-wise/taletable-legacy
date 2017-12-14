
package com.kispoko.tome.db


import com.kispoko.tome.lib.orm.*
import com.kispoko.tome.lib.orm.schema.*
import com.kispoko.tome.model.campaign.CampaignId
import com.kispoko.tome.model.campaign.CampaignName
import com.kispoko.tome.model.campaign.CampaignSummary
import com.kispoko.tome.model.game.*
import com.kispoko.tome.model.game.engine.Engine
import com.kispoko.tome.model.game.engine.EngineValue
import com.kispoko.tome.model.game.engine.EngineValueType
import com.kispoko.tome.model.game.engine.dice.*
import com.kispoko.tome.model.game.engine.function.*
import com.kispoko.tome.model.game.engine.function.Function
import com.kispoko.tome.model.game.engine.mechanic.*
import com.kispoko.tome.model.game.engine.procedure.ProcedureId
import com.kispoko.tome.model.game.engine.program.*
import com.kispoko.tome.model.game.engine.reference.BooleanReference
import com.kispoko.tome.model.game.engine.reference.DataReference
import com.kispoko.tome.model.game.engine.reference.DiceRollReference
import com.kispoko.tome.model.game.engine.reference.NumberReference
import com.kispoko.tome.model.game.engine.summation.Summation
import com.kispoko.tome.model.game.engine.summation.SummationId
import com.kispoko.tome.model.game.engine.summation.SummationName
import com.kispoko.tome.model.game.engine.summation.term.SummationTerm
import com.kispoko.tome.model.game.engine.summation.term.TermName
import com.kispoko.tome.model.game.engine.value.*
import com.kispoko.tome.model.game.engine.variable.*
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
import com.kispoko.tome.rts.sheet.*
import effect.Maybe
import lulo.schema.Prim
import lulo.schema.Sum



//*********************************************************************************************//
//                                Application Database Schema                                  //
//*********************************************************************************************//

// ACTION
// ---------------------------------------------------------------------------------------------

val actionTable = Table3("action",
                         "action_name",
                         "roll_summation_id",
                         "procedure_id")

typealias DB_ActionValue =
    RowValue3<PrimValue<ActionName>,
              MaybePrimValue<SummationId>,
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


// CAMPAIGN
// ---------------------------------------------------------------------------------------------

val campaignTable = Table4("campaign",
                           "campaign_id",
                           "name",
                           "summary",
                           "game_id")

typealias DB_CampaignValue =
    RowValue4<PrimValue<CampaignId>,
              PrimValue<CampaignName>,
              PrimValue<CampaignSummary>,
              PrimValue<GameId>>


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

val engineTable = Table6("engine",
                         "value_sets",
                         "mechanics",
                         "mechanic_categories",
                         "functions",
                         "programs",
                         "summations")

typealias DB_EngineValue =
    RowValue6<CollValue<ValueSet>,
              CollValue<Mechanic>,
              CollValue<MechanicCategory>,
              CollValue<Function>,
              CollValue<Program>,
              CollValue<Summation>>


// ELEMENT FORMAT
// ---------------------------------------------------------------------------------------------

val elementFormatTable =
    Table8("element_format",
           "position",
           "height",
           "padding",
           "margins",
           "background_color_theme",
           "corners",
           "alignment",
           "vertical_alignment")

typealias DB_ElementFormatValue =
    RowValue8<PrimValue<Position>,
              PrimValue<Height>,
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
    Table6("invocation",
           "parameter1_type",
           "parameter2_type",
           "parameter3_type",
           "parameter4_type",
           "parameter5_type",
           "result_type")

typealias DB_Invocation =
    RowValue6<PrimValue<ProgramId>,
              SumValue<DataReference>,
              MaybeSumValue<DataReference>,
              MaybeSumValue<DataReference>,
              MaybeSumValue<DataReference>,
              MaybeSumValue<DataReference>>


// GAME
// ---------------------------------------------------------------------------------------------

val gameTable =
    Table6("game",
           "game_id",
           "name",
           "summary",
           "authors",
           "engine",
           "rulebook")

typealias DB_GameValue =
    RowValue6<PrimValue<GameId>,
              PrimValue<GameName>,
              PrimValue<GameSummary>,
              CollValue<Author>,
              ProdValue<Engine>,
              ProdValue<Rulebook>>


// GROUP
// ---------------------------------------------------------------------------------------------

val groupTable =
    Table3("group",
           "format",
           "index",
           "rows")

typealias DB_GroupValue =
    RowValue3<ProdValue<GroupFormat>,
              PrimValue<GroupIndex>,
              CollValue<GroupRow>>


// GROUP FORMAT
// ---------------------------------------------------------------------------------------------

val groupFormatTable =
    Table2("group_format",
           "element_format",
           "divider")

typealias DB_GroupFormatValue =
    RowValue2<ProdValue<ElementFormat>,
              ProdValue<Divider>>


// GROUP ROW
// ---------------------------------------------------------------------------------------------

val groupRowTable =
    Table3("group_row",
            "format",
            "index",
            "widgets")

typealias DB_GroupRowValue =
    RowValue3<ProdValue<GroupRowFormat>,
              PrimValue<GroupRowIndex>,
              CollValue<Widget>>


// GROUP ROW FORMAT
// ---------------------------------------------------------------------------------------------

val groupRowFormatTable =
    Table2("group_row_format",
           "element_format",
           "divider")

typealias DB_GroupRowFormatValue =
    RowValue2<ProdValue<ElementFormat>,
              ProdValue<Divider>>


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
    Table7("mechanic",
           "mechanic_id",
           "label",
           "description",
           "summary",
           "category_id",
           "requirements",
           "variables")

typealias DB_Mechanic =
    RowValue7<PrimValue<MechanicId>,
              PrimValue<MechanicLabel>,
              PrimValue<MechanicDescription>,
              PrimValue<MechanicSummary>,
              PrimValue<MechanicCategoryId>,
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


// PAGE
// ---------------------------------------------------------------------------------------------

val pageTable =
    Table4("page",
           "page_name",
           "format",
           "index",
           "groups")

typealias DB_PageValue =
    RowValue4<PrimValue<PageName>,
              ProdValue<PageFormat>,
              PrimValue<PageIndex>,
              CollValue<Group>>


// PAGE FORMAT
// ---------------------------------------------------------------------------------------------

val pageFormatTable =
    Table1("page_format",
           "element_format")

typealias DB_PageFormatValue =
    RowValue1<ProdValue<ElementFormat>>


// PROGRAM
// ---------------------------------------------------------------------------------------------

val programTable =
    Table6("program",
           "program_id",
           "label",
           "description",
           "type_signature",
           "statements",
           "result_binding_name")

typealias DB_ProgramValue =
    RowValue6<PrimValue<ProgramId>,
              PrimValue<ProgramLabel>,
              PrimValue<ProgramDescription>,
              ProdValue<ProgramTypeSignature>,
              CollValue<Statement>,
              PrimValue<StatementBindingName>>


// PROGRAM TYPE SIGNATURE
// ---------------------------------------------------------------------------------------------

val programTypeSignatureTable =
    Table6("program_type_signature",
           "parameter1_type",
           "parameter2_type",
           "parameter3_type",
           "parameter4_type",
           "parameter5_type",
           "result_type")

typealias DB_ProgramTypeSignatureValue =
    RowValue6<PrimValue<EngineValueType>,
              MaybePrimValue<EngineValueType>,
              MaybePrimValue<EngineValueType>,
              MaybePrimValue<EngineValueType>,
              MaybePrimValue<EngineValueType>,
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


// RULEBOOK
// ---------------------------------------------------------------------------------------------

val rulebookTable =
    Table3("rulebook",
           "title",
           "abstract",
           "chapters")

typealias DB_RulebookValue =
    RowValue3<PrimValue<RulebookTitle>,
              PrimValue<RulebookAbstract>,
              CollValue<RulebookChapter>>


// RULEBOOK CHAPTER
// ---------------------------------------------------------------------------------------------

val rulebookChapterTable =
    Table3("rulebook_chapter",
           "chapter_id",
           "title",
           "sections")

typealias DB_RulebookChapterValue =
    RowValue3<PrimValue<RulebookChapterId>,
              PrimValue<RulebookChapterTitle>,
              CollValue<RulebookSection>>


// RULEBOOK SECTION
// ---------------------------------------------------------------------------------------------

val rulebookSectionTable =
    Table4("rulebook_section",
           "section_id",
           "title",
           "body",
           "subsections")

typealias DB_RulebookSectionValue =
    RowValue4<PrimValue<RulebookSectionId>,
              PrimValue<RulebookSectionTitle>,
              PrimValue<RulebookSectionBody>,
              CollValue<RulebookSubsection>>


// RULEBOOK SUBSECTION
// ---------------------------------------------------------------------------------------------

val rulebookSubsectionTable =
    Table3("rulebook_subsection",
           "subsection_id",
           "title",
           "body")

typealias DB_RulebookSubsectionValue =
    RowValue3<PrimValue<RulebookSubsectionId>,
              PrimValue<RulebookSubsectionTitle>,
              PrimValue<RulebookSubsectionBody>>


// RULEBOOK REFERENCE
// ---------------------------------------------------------------------------------------------

val rulebookReferenceTable =
    Table3("rulebook_reference",
           "chapter_id",
           "section_id",
           "subsection_id")

typealias DB_RulebookReferenceValue =
    RowValue3<PrimValue<RulebookChapterId>,
              MaybePrimValue<RulebookSectionId>,
              MaybePrimValue<RulebookSubsectionId>>


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
              PrimValue<Icon>>


// SESSION
// ---------------------------------------------------------------------------------------------

val sessionTable =
    Table4("session",
           "name",
           "time_last_active",
           "active_sheet_id",
           "sheet_records")

typealias DB_SessionValue =
    RowValue4<PrimValue<SessionName>,
              PrimValue<SessionLastActiveTime>,
              MaybePrimValue<SheetId>,
              CollValue<SessionSheetRecord>>


// SESSION SHEET RECORD
// ---------------------------------------------------------------------------------------------
val sessionSheetRecordTable =
    Table3("session_sheet_record",
           "sheet_id",
           "session_index",
           "time_last_active")

typealias DB_SessionSheetRecordValue =
    RowValue3<PrimValue<SheetId>,
              PrimValue<SessionRecordIndex>,
              PrimValue<SheetLastActiveTime>>


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

typealias DB_Statement =
    RowValue7<PrimValue<StatementBindingName>,
              PrimValue<FunctionId>,
              SumValue<StatementParameter>,
              MaybeSumValue<StatementParameter>,
              MaybeSumValue<StatementParameter>,
              MaybeSumValue<StatementParameter>,
              MaybeSumValue<StatementParameter>>


// SUMMATION
// ---------------------------------------------------------------------------------------------

val summationTable =
    Table3("summation",
           "summation_id",
           "summation_name",
           "terms")

typealias DB_SummationValue =
    RowValue3<PrimValue<SummationId>,
              PrimValue<SummationName>,
              CollValue<SummationTerm>>


// TERM: NUMBER
// ---------------------------------------------------------------------------------------------

val summationTermNumberTable =
    Table2("summation_term_number",
           "term_name",
           "value_reference")

typealias DB_SummationTermNumberValue =
    RowValue2<MaybePrimValue<TermName>,
             SumValue<NumberReference>>


// TERM: DICE ROLL
// ---------------------------------------------------------------------------------------------

val summationTermDiceRollTable =
    Table2("summation_term_dice_roll",
            "term_name",
            "value_reference")

typealias DB_SummationTermDiceRollValue =
    RowValue2<MaybePrimValue<TermName>,
              SumValue<DiceRollReference>>


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
    Table7("text_format",
           "color_theme",
           "size",
           "font",
           "font_style",
           "is_underlined",
           "number_format",
           "element_format")

typealias DB_TextFormatValue =
    RowValue7<PrimValue<ColorTheme>,
              PrimValue<TextSize>,
              PrimValue<TextFont>,
              PrimValue<TextFontStyle>,
              PrimValue<IsUnderlined>,
              PrimValue<NumberFormat>,
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
    Table10("ui_colors",
            "toolbar_background_color_id",
            "toolbar_icons_color_id",
            "toolbar_title_color_id",
            "tab_bar_background_color_id",
            "tab_text_normal_color_id",
            "tab_text_selected_color_id",
            "tab_underline_color_id",
            "bottom_bar_background_color_id",
            "bottom_bar_active_color_id",
            "bottom_bar_inactive_color_id")


typealias DB_UIColors =
    RowValue10<PrimValue<ColorId>,
               PrimValue<ColorId>,
               PrimValue<ColorId>,
               PrimValue<ColorId>,
               PrimValue<ColorId>,
               PrimValue<ColorId>,
               PrimValue<ColorId>,
               PrimValue<ColorId>,
               PrimValue<ColorId>,
               PrimValue<ColorId>>


// WIDGET: LOG
// ---------------------------------------------------------------------------------------------

val widgetLogTable =
    Table3("widget_log",
           "widget_id",
           "format",
           "entries")

typealias DB_WidgetLog =
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

val widgetLogFormat =
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

val widgetLogEntryFormat =
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
           "entries")

typealias DB_WidgetMechanicValue =
    RowValue3<PrimValue<WidgetId>,
              ProdValue<MechanicWidgetFormat>,
              PrimValue<MechanicCategoryId>>


// WIDGET: MECHANIC > FORMAT
// ---------------------------------------------------------------------------------------------

val widgetMechanicFormatTable =
    Table7("widget_mechanic_format",
           "widget_id",
           "widget_format",
           "view_type",
           "mechanic_format",
           "header_format",
           "mechanic_header_format",
           "mechanic_summary_format")

typealias DB_WidgetMechanicFormatValue =
    RowValue7<PrimValue<WidgetId>,
              ProdValue<WidgetFormat>,
              PrimValue<MechanicWidgetViewType>,
              ProdValue<TextFormat>,
              ProdValue<TextFormat>,
              ProdValue<TextFormat>,
              ProdValue<TextFormat>>


// WIDGET: NUMBER
// ---------------------------------------------------------------------------------------------

val widgetNumberTable =
    Table3("widget_number",
           "widget_id",
           "format",
           "value_variable_id")

typealias DB_WidgetNumber =
    RowValue3<PrimValue<WidgetId>,
              ProdValue<NumberWidgetFormat>,
              PrimValue<VariableId>>


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
              ProdValue<TextFormat>>


// WIDGET FORMAT
// ---------------------------------------------------------------------------------------------

val widgetFormatTable =
    Table2("widget_format",
           "width",
           "element_format")

typealias DB_WidgetFormatValue =
    RowValue2<PrimValue<WidgetWidth>,
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
    Table5("widget_points_format",
           "widget_format",
           "limit_text_format",
           "current_text_format",
           "label_text_format",
           "bar_format")

typealias DB_WidgetPointsFormatValue =
    RowValue5<ProdValue<WidgetFormat>,
              ProdValue<TextFormat>,
              ProdValue<TextFormat>,
              ProdValue<TextFormat>,
              ProdValue<PointsBarFormat>>


// WIDGET: POINTS > BAR FORMAT
// ---------------------------------------------------------------------------------------------

val widgetPointsBarFormatTable =
    Table5("widget_points_bar_format",
           "style",
           "above_style",
           "height",
           "limit_color_theme",
           "current_color_theme")

typealias DB_WidgetPointsBarFormatValue =
    RowValue5<PrimValue<PointsBarStyle>,
              PrimValue<PointsAboveBarStyle>,
              PrimValue<PointsBarHeight>,
              PrimValue<ColorTheme>,
              PrimValue<ColorTheme>>


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
    Table2("widget_story_format",
           "widget_format",
           "line_spaceing")

typealias DB_WidgetStoryFormatValue =
    RowValue2<ProdValue<WidgetFormat>,
              PrimValue<LineSpacing>>


// WIDGET: STORY > PART: SPAN
// ---------------------------------------------------------------------------------------------

val widgetStoryPartSpanTable =
    Table2("widget_story_part_span",
            "text_format",
            "text")

typealias DB_WidgetStoryPartSpanValue =
    RowValue2<ProdValue<TextFormat>,
              PrimValue<StoryPartText>>


// WIDGET: STORY > PART: VARIABLE
// ---------------------------------------------------------------------------------------------

val widgetStoryPartVariableTable =
    Table3("widget_story_part_variable",
           "text_format",
           "variable_id",
           "numeric_editor_type")

typealias DB_WidgetStoryPartVariableValue =
    RowValue3<ProdValue<TextFormat>,
              PrimValue<VariableId>,
              PrimValue<NumericEditorType>>


// WIDGET: STORY > PART: ICON
// ---------------------------------------------------------------------------------------------

val widgetStoryPartIconTable =
    Table2("widget_story_part_icon",
           "icon",
           "icon_format")

typealias DB_WidgetStoryPartIconValue =
    RowValue2<PrimValue<Icon>,
              ProdValue<IconFormat>>


// WIDGET: STORY > PART: ACTION
// ---------------------------------------------------------------------------------------------

val widgetStoryPartActionTable =
    Table5("widget_story_part_action",
           "text",
           "action",
           "text_format",
           "icon_format",
           "show_procedure_dialog")

typealias DB_WidgetStoryPartActionValue =
    RowValue5<PrimValue<StoryPartText>,
              ProdValue<Action>,
              ProdValue<TextFormat>,
              ProdValue<IconFormat>,
              PrimValue<ShowProcedureDialog>>


// WIDGET: TABLE
// ---------------------------------------------------------------------------------------------

val widgetTableTable =
    Table5("widget_table",
           "widget_id",
           "format",
           "columns",
           "rows",
           "sort")

typealias DB_WidgetTableValue =
    RowValue5<PrimValue<WidgetId>,
              ProdValue<TableWidgetFormat>,
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
    RowValue1<MaybeProdValue<ElementFormat>>



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
    RowValue1<MaybeProdValue<ElementFormat>>


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
              ProdValue<TextFormat>,
              ProdValue<TextFormat>,
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
    Table3("widget_table_column_format",
           "text_format",
           "element_format",
           "width")

typealias DB_WidgetTableColumnFormatValue =
    RowValue3<ProdValue<TextFormat>,
              ProdValue<ElementFormat>,
              PrimValue<ColumnWidth>>


// WIDGET: TABLE > FORMAT
// ---------------------------------------------------------------------------------------------

val widgetTableFormatTable =
    Table5("widget_table_format",
           "widget_format",
           "header_format",
           "row_format",
           "divider",
           "cell_height")

typealias DB_WidgetTableFormatValue =
    RowValue5<ProdValue<WidgetFormat>,
              ProdValue<TableWidgetRowFormat>,
              ProdValue<TableWidgetRowFormat>,
              MaybeProdValue<Divider>,
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

val valueNumber =
    Table5("value_number",
           "value_id",
           "description",
           "rulebook_reference",
           "variables",
           "value")

typealias DB_ValueNumberValue =
    RowValue5<PrimValue<ValueId>,
              PrimValue<ValueDescription>,
              MaybeProdValue<RulebookReference>,
              CollValue<Variable>,
              PrimValue<NumberValue>>


// VALUE: TEXT
// ---------------------------------------------------------------------------------------------

val valueText =
    Table5("value_text",
           "value_id",
           "description",
           "rulebook_reference",
           "variables",
           "value")

typealias DB_ValueTextValue =
    RowValue5<PrimValue<ValueId>,
              PrimValue<ValueDescription>,
              MaybeProdValue<RulebookReference>,
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
    Table5("variable_dice_roll",
           "variable_id",
           "label",
           "description",
           "tags",
           "variable_value")

typealias DB_VariableDiceRollValue =
    RowValue5<PrimValue<VariableId>,
              PrimValue<VariableLabel>,
              PrimValue<VariableDescription>,
              PrimValue<VariableTagSet>,
              SumValue<DiceRollVariableValue>>


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
