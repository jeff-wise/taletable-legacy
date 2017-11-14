
package com.kispoko.tome.db


import com.kispoko.tome.R.string.label
import com.kispoko.tome.lib.functor.*
import com.kispoko.tome.lib.orm.*
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



/**
 * Application Database Schema
 */

// ACTION
// ---------------------------------------------------------------------------------------------

typealias DB_Action = Row3<Prim<ActionName>, ActionName,
                           MaybePrim<SummationId>, SummationId,
                           MaybePrim<ProcedureId>, ProcedureId>

fun dbAction(actionName : ActionName,
            rollSummationId : Maybe<SummationId>,
            procedureId : Maybe<ProcedureId>) : DB_Action =
        Row3("action",
            Col("action_name", Prim(actionName)),
            Col("roll_summation_id", MaybePrim(rollSummationId)),
            Col("procedure_id", MaybePrim(procedureId)))

// APP SETTINGS
// ---------------------------------------------------------------------------------------------

typealias DB_AppSettings = Row1<Prim<ThemeId>, ThemeId>

fun dbAppSettings(themeId : ThemeId) : DB_AppSettings =
        Row1("app_settings",
            Col("theme_id", Prim(themeId)))

// AUTHOR
// ---------------------------------------------------------------------------------------------

typealias DB_Author = Row3<Prim<AuthorName>, AuthorName,
                           MaybePrim<AuthorOrganization>, AuthorOrganization,
                           MaybePrim<UserName>, UserName>

fun dbAuthor(authorName : AuthorName,
             organization : Maybe<AuthorOrganization>,
             userName : Maybe<UserName>) : DB_Author =
        Row3("author",
            Col("author_name", Prim(authorName)),
            Col("organization", MaybePrim(organization)),
            Col("user_name", MaybePrim(userName)))

// CAMPAIGN
// ---------------------------------------------------------------------------------------------

typealias DB_Campaign = Row4<Prim<CampaignId>, CampaignId,
                             Prim<CampaignName>, CampaignName,
                             Prim<CampaignSummary>, CampaignSummary,
                             Prim<GameId>, GameId>

fun dbCampaign(campaignId : CampaignId,
               campaignName : CampaignName,
               campaignSummary : CampaignSummary,
               gameId : GameId) : DB_Campaign =
        Row4("campaign",
            Col("campaign_id", Prim(campaignId)),
            Col("campaign_name", Prim(campaignName)),
            Col("campaign_summary", Prim(campaignSummary)),
            Col("game_id", Prim(gameId)))

// DICE ROLL
// ---------------------------------------------------------------------------------------------

typealias DB_DiceRoll = Row3<Prim<DiceQuantitySet>, DiceQuantitySet,
                             Coll<RollModifier>, RollModifier,
                             MaybePrim<DiceRollName>, DiceRollName>

fun dbDiceRoll(quantities : List<DiceQuantity>,
               modifiers : List<RollModifier>,
               rollName : Maybe<DiceRollName>) : DB_DiceRoll =
        Row3("dice_roll",
            Col("quantities", Prim(DiceQuantitySet(quantities))),
            Col("modifiers", Coll(modifiers)),
            Col("roll_name", MaybePrim(rollName)))

// DIVIDER
// ---------------------------------------------------------------------------------------------

typealias DB_Divider = Row3<Prim<ColorTheme>, ColorTheme,
                            Prim<Spacing>, Spacing,
                            Prim<DividerThickness>, DividerThickness>

fun dbDivider(colorTheme : ColorTheme,
              margins : Spacing,
              thickness : DividerThickness) : DB_Divider =
        Row3("divider",
            Col("color_theme", Prim(colorTheme)),
            Col("margins", Prim(margins)),
            Col("thickness", Prim(thickness)))

// ENGINE
// ---------------------------------------------------------------------------------------------

typealias DB_Engine = Row6<Coll<ValueSet>, ValueSet,
                           Coll<Mechanic>, Mechanic,
                           Coll<MechanicCategory>, MechanicCategory,
                           Coll<Function>, Function,
                           Coll<Program>, Program,
                           Coll<Summation>, Summation>

fun dbEngine(valueSets : List<ValueSet>,
             mechanics : List<Mechanic>,
             mechanicCategories : List<MechanicCategory>,
             functions : List<Function>,
             programs : List<Program>,
             summations : List<Summation>) : DB_Engine =
        Row6("engine",
            Col("value_sets", Coll(valueSets)),
            Col("mechanics", Coll(mechanics)),
            Col("mechanic_categories", Coll(mechanicCategories)),
            Col("functions", Coll(functions)),
            Col("programs", Coll(programs)),
            Col("summations", Coll(summations)))

// ELEMENT FORMAT
// ---------------------------------------------------------------------------------------------

typealias DB_ElementFormat = Row8<Prim<Position>, Position,
                                  Prim<Height>, Height,
                                  Prim<Spacing>, Spacing,
                                  Prim<Spacing>, Spacing,
                                  Prim<ColorTheme>, ColorTheme,
                                  Prim<Corners>, Corners,
                                  Prim<Alignment>, Alignment,
                                  Prim<VerticalAlignment>, VerticalAlignment>

fun dbElementFormat(position : Position,
                    height : Height,
                    padding : Spacing,
                    margins : Spacing,
                    backgroundColorTheme : ColorTheme,
                    corners : Corners,
                    alignment: Alignment,
                    verticalAlignment : VerticalAlignment) : DB_ElementFormat =
        Row8("element_format",
            Col("position", Prim(position)),
            Col("height", Prim(height)),
            Col("padding", Prim(padding)),
            Col("margins", Prim(margins)),
            Col("background_color_theme", Prim(backgroundColorTheme)),
            Col("corners", Prim(corners)),
            Col("alignment", Prim(alignment)),
            Col("vertical_alignment", Prim(verticalAlignment)))

// FUNCTION
// ---------------------------------------------------------------------------------------------

typealias DB_Function = Row5<Prim<FunctionId>, FunctionId,
                             Prim<FunctionLabel>, FunctionLabel,
                             Prim<FunctionDescription>, FunctionDescription,
                             Prod<FunctionTypeSignature>, FunctionTypeSignature,
                             Coll<Tuple>, Tuple>

fun dbFunction(functionId : FunctionId,
               label : FunctionLabel,
               description : FunctionDescription,
               typeSignature : FunctionTypeSignature,
               tuples : MutableList<Tuple>) : DB_Function =
        Row5("function",
            Col("function_id", Prim(functionId)),
            Col("label", Prim(label)),
            Col("description", Prim(description)),
            Col("type_signature", Prod(typeSignature)),
            Col("tuples", Coll(tuples)))

// FUNCTION TYPE SIGNATURE
// ---------------------------------------------------------------------------------------------

typealias DB_FunctionTypeSignature = Row6<Prim<EngineValueType>, EngineValueType,
                                          MaybePrim<EngineValueType>, EngineValueType,
                                          MaybePrim<EngineValueType>, EngineValueType,
                                          MaybePrim<EngineValueType>, EngineValueType,
                                          MaybePrim<EngineValueType>, EngineValueType,
                                          Prim<EngineValueType>, EngineValueType>

fun dbFunctionTypeSignature(parameter1Type : EngineValueType,
                            parameter2Type : Maybe<EngineValueType>,
                            parameter3Type : Maybe<EngineValueType>,
                            parameter4Type : Maybe<EngineValueType>,
                            parameter5Type : Maybe<EngineValueType>,
                            resultType : EngineValueType) : DB_FunctionTypeSignature =
        Row6("function_type_signature",
            Col("parameter1_type", Prim(parameter1Type)),
            Col("parameter2_type", MaybePrim(parameter2Type)),
            Col("parameter3_type", MaybePrim(parameter3Type)),
            Col("parameter4_type", MaybePrim(parameter4Type)),
            Col("parameter5_type", MaybePrim(parameter5Type)),
            Col("result_type", Prim(resultType)))

// INVOCATION
// ---------------------------------------------------------------------------------------------

typealias DB_Invocation = Row6<Prim<ProgramId>, ProgramId,
                               Sum<DataReference>, DataReference,
                               MaybeSum<DataReference>, DataReference,
                               MaybeSum<DataReference>, DataReference,
                               MaybeSum<DataReference>, DataReference,
                               MaybeSum<DataReference>, DataReference>

fun dbInvocation(programId : ProgramId,
                 parameter1 : DataReference,
                 parameter2 : Maybe<DataReference>,
                 parameter3 : Maybe<DataReference>,
                 parameter4 : Maybe<DataReference>,
                 parameter5 : Maybe<DataReference>) : DB_Invocation =
        Row6("invocation",
            Col("program_id", Prim(programId)),
            Col("parameter1", Sum(parameter1)),
            Col("parameter2", MaybeSum(parameter2)),
            Col("parameter3", MaybeSum(parameter3)),
            Col("parameter4", MaybeSum(parameter4)),
            Col("parameter5", MaybeSum(parameter5)))

// GAME
// ---------------------------------------------------------------------------------------------

typealias DB_Game = Row6<Prim<GameId>, GameId,
                         Prim<GameName>, GameName,
                         Prim<GameSummary>, GameSummary,
                         Coll<Author>, Author,
                         Prod<Engine>, Engine,
                         Prod<Rulebook>, Rulebook>

fun dbGame(gameId : GameId,
           gameName : GameName,
           gameSummary : GameSummary,
           authors : List<Author>,
           engine : Engine,
           rulebook : Rulebook) : DB_Game =
        Row6("game",
            Col("game_id", Prim(gameId)),
            Col("game_name", Prim(gameName)),
            Col("game_summary", Prim(gameSummary)),
            Col("authors", Coll(authors)),
            Col("engine", Prod(engine)),
            Col("rulebook", Prod(rulebook)))

// GROUP
// ---------------------------------------------------------------------------------------------

typealias DB_Group = Row3<Prod<GroupFormat>, GroupFormat,
                          Prim<GroupIndex>, GroupIndex,
                          Coll<GroupRow>, GroupRow>

fun dbGroup(format : GroupFormat,
            index : GroupIndex,
            rows : List<GroupRow>) : DB_Group =
        Row3("group",
            Col("format", Prod(format)),
            Col("index", Prim(index)),
            Col("rows", Coll(rows)))

// GROUP FORMAT
// ---------------------------------------------------------------------------------------------

typealias DB_GroupFormat = Row2<Prod<ElementFormat>, ElementFormat,
                                Prod<Divider>, Divider>

fun dbGroupFormat(elementFormat : ElementFormat,
                  divider : Divider) : DB_GroupFormat =
        Row2("group_format",
            Col("element_format", Prod(elementFormat)),
            Col("divider", Prod(divider)))

// GROUP ROW
// ---------------------------------------------------------------------------------------------

typealias DB_GroupRow = Row3<Prod<GroupRowFormat>, GroupRowFormat,
                             Prim<GroupRowIndex>, GroupRowIndex,
                             Coll<Widget>, Widget>

fun dbGroupRow(format : GroupRowFormat,
               index : GroupRowIndex,
               widgets : List<Widget>) : DB_GroupRow =
        Row3("group_row",
            Col("format", Prod(format)),
            Col("index", Prim(index)),
            Col("widgets", Coll(widgets)))

// GROUP ROW FORMAT
// ---------------------------------------------------------------------------------------------

typealias DB_GroupRowFormat = Row2<Prod<ElementFormat>, ElementFormat,
                                   Prod<Divider>, Divider>

fun dbGroupRowFromat(elementFormat : ElementFormat,
               divider : Divider) : DB_GroupRowFormat =
        Row2("group_row",
            Col("element_format", Prod(elementFormat)),
            Col("divider", Prod(divider)))

// ICON FORMAT
// ---------------------------------------------------------------------------------------------

typealias DB_IconFormat = Row2<Prim<ColorTheme>, ColorTheme,
                               Prim<IconSize>, IconSize>

fun dbIconFormat(colorTheme : ColorTheme,
                 size : IconSize) : DB_IconFormat =
        Row2("icon_format",
            Col("color_theme", Prim(colorTheme)),
            Col("size", Prim(size)))

// MECHANIC
// ---------------------------------------------------------------------------------------------

typealias DB_Mechanic = Row7<Prim<MechanicId>, MechanicId,
                             Prim<MechanicLabel>, MechanicLabel,
                             Prim<MechanicDescription>, MechanicDescription,
                             Prim<MechanicSummary>, MechanicSummary,
                             Prim<MechanicCategoryId>, MechanicCategoryId,
                             Prim<MechanicRequirements>, MechanicRequirements,
                             Coll<Variable>, Variable>

fun dbMechanic(mechanicId : MechanicId,
               label : MechanicLabel,
               description : MechanicDescription,
               summary : MechanicSummary,
               categoryId : MechanicCategoryId,
               requirements : List<VariableId>,
               variables : MutableList<Variable>) : DB_Mechanic =
        Row7("mechanic",
            Col("mechanic_id", Prim(mechanicId)),
            Col("label", Prim(label)),
            Col("description", Prim(description)),
            Col("summary", Prim(summary)),
            Col("category_id", Prim(categoryId)),
            Col("requirements", Prim(MechanicRequirements(requirements))),
            Col("variables", Coll(variables)))

// MECHANIC CATEGORY
// ---------------------------------------------------------------------------------------------

typealias DB_MechanicCategory = Row3<Prim<MechanicCategoryId>, MechanicCategoryId,
                                     Prim<MechanicCategoryLabel>, MechanicCategoryLabel,
                                     Prim<MechanicCategoryDescription>, MechanicCategoryDescription>

fun dbMechanicCategory(categoryId : MechanicCategoryId,
                       label : MechanicCategoryLabel,
                       description : MechanicCategoryDescription) : DB_MechanicCategory =
        Row3("mechanic_category",
            Col("category_id", Prim(categoryId)),
            Col("label", Prim(label)),
            Col("description", Prim(description)))

// PAGE
// ---------------------------------------------------------------------------------------------

typealias DB_Page = Row4<Prim<PageName>, PageName,
                         Prod<PageFormat>, PageFormat,
                         Prim<PageIndex>, PageIndex,
                         Coll<Group>, Group>

fun dbPage(pageName : PageName,
           format : PageFormat,
           index : PageIndex,
           groups : List<Group>) : DB_Page =
        Row4("page",
            Col("page_name", Prim(pageName)),
            Col("format", Prod(format)),
            Col("index", Prim(index)),
            Col("groups", Coll(groups)))

// PAGE FORMAT
// ---------------------------------------------------------------------------------------------

typealias DB_PageFormat = Row1<Prod<ElementFormat>, ElementFormat>

fun dbPageFormat(elementFormat : ElementFormat) : DB_PageFormat =
        Row1("element_format",
            Col("padding", Prod(elementFormat)))

// PROGRAM
// ---------------------------------------------------------------------------------------------

typealias DB_Program = Row6<Prim<ProgramId>, ProgramId,
                            Prim<ProgramLabel>, ProgramLabel,
                            Prim<ProgramDescription>, ProgramDescription,
                            Prod<ProgramTypeSignature>, ProgramTypeSignature,
                            Coll<Statement>, Statement,
                            Prim<StatementBindingName>, StatementBindingName>

fun dbProgram(programId : ProgramId,
              label : ProgramLabel,
              description : ProgramDescription,
              typeSignature : ProgramTypeSignature,
              statements : MutableList<Statement>,
              resultBindingName : StatementBindingName) : DB_Program =
        Row6("program",
            Col("program_id", Prim(programId)),
            Col("label", Prim(label)),
            Col("description", Prim(description)),
            Col("type_signature", Prod(typeSignature)),
            Col("statements", Coll(statements)),
            Col("result_binding_name", Prim(resultBindingName)))

// PROGRAM TYPE SIGNATURE
// ---------------------------------------------------------------------------------------------

typealias DB_ProgramTypeSignature = Row6<Prim<EngineValueType>, EngineValueType,
                                         MaybePrim<EngineValueType>, EngineValueType,
                                         MaybePrim<EngineValueType>, EngineValueType,
                                         MaybePrim<EngineValueType>, EngineValueType,
                                         MaybePrim<EngineValueType>, EngineValueType,
                                         Prim<EngineValueType>, EngineValueType>

fun dbProgramTypeSignature(parameter1Type : EngineValueType,
                            parameter2Type : Maybe<EngineValueType>,
                            parameter3Type : Maybe<EngineValueType>,
                            parameter4Type : Maybe<EngineValueType>,
                            parameter5Type : Maybe<EngineValueType>,
                            resultType : EngineValueType) : DB_ProgramTypeSignature =
        Row6("program_type_signature",
            Col("parameter1_type", Prim(parameter1Type)),
            Col("parameter2_type", MaybePrim(parameter2Type)),
            Col("parameter3_type", MaybePrim(parameter3Type)),
            Col("parameter4_type", MaybePrim(parameter4Type)),
            Col("parameter5_type", MaybePrim(parameter5Type)),
            Col("result_type", Prim(resultType)))

// ROLL MODIFIER
// ---------------------------------------------------------------------------------------------

typealias DB_RollModifier = Row2<Prim<RollModifierValue>, RollModifierValue,
                                 MaybePrim<RollModifierName>, RollModifierName>


fun dbRollModifier(value : RollModifierValue,
                   modifierName : Maybe<RollModifierName>) : DB_RollModifier =
        Row2("roll_modifier",
            Col("value", Prim(value)),
            Col("modifier_name", MaybePrim(modifierName)))

// RULEBOOK
// ---------------------------------------------------------------------------------------------

typealias DB_Rulebook = Row3<Prim<RulebookTitle>, RulebookTitle,
                             Prim<RulebookAbstract>, RulebookAbstract,
                             Coll<RulebookChapter>, RulebookChapter>


fun dbRulebook(title : RulebookTitle,
               abstract : RulebookAbstract,
               chapters : List<RulebookChapter>) : DB_Rulebook =
        Row3("rulebook",
            Col("title", Prim(title)),
            Col("abstract", Prim(abstract)),
            Col("chapters", Coll(chapters)))

// RULEBOOK CHAPTER
// ---------------------------------------------------------------------------------------------

typealias DB_RulebookChapter = Row3<Prim<RulebookChapterId>, RulebookChapterId,
                                    Prim<RulebookChapterTitle>, RulebookChapterTitle,
                                    Coll<RulebookSection>, RulebookSection>


fun dbRulebookChapter(chapterId : RulebookChapterId,
                      title : RulebookChapterTitle,
                      sections : MutableList<RulebookSection>) : DB_RulebookChapter =
        Row3("rulebook_chapter",
            Col("chapter_id", Prim(chapterId)),
            Col("title", Prim(title)),
            Col("sections", Coll(sections)))

// RULEBOOK SECTION
// ---------------------------------------------------------------------------------------------

typealias DB_RulebookSection = Row4<Prim<RulebookSectionId>, RulebookSectionId,
                                    Prim<RulebookSectionTitle>, RulebookSectionTitle,
                                    Prim<RulebookSectionBody>, RulebookSectionBody,
                                    Coll<RulebookSubsection>, RulebookSubsection>


fun dbRulebookSection(sectionId : RulebookSectionId,
                      title : RulebookSectionTitle,
                      body : RulebookSectionBody,
                      subsections : List<RulebookSubsection>) : DB_RulebookSection =
        Row4("rulebook_section",
            Col("section_id", Prim(sectionId)),
            Col("title", Prim(title)),
            Col("body", Prim(body)),
            Col("subsections", Coll(subsections)))

// RULEBOOK SUBSECTION
// ---------------------------------------------------------------------------------------------

typealias DB_RulebookSubsection = Row3<Prim<RulebookSubsectionId>, RulebookSubsectionId,
                                       Prim<RulebookSubsectionTitle>, RulebookSubsectionTitle,
                                       Prim<RulebookSubsectionBody>, RulebookSubsectionBody>


fun dbRulebookSubection(subsectionId : RulebookSubsectionId,
                        title : RulebookSubsectionTitle,
                        body : RulebookSubsectionBody) : DB_RulebookSubsection =
        Row3("rulebook_subsection",
            Col("subsection_id", Prim(subsectionId)),
            Col("title", Prim(title)),
            Col("body", Prim(body)))

// RULEBOOK REFERENCE
// ---------------------------------------------------------------------------------------------

typealias DB_RulebookReference = Row3<Prim<RulebookChapterId>, RulebookChapterId,
                                      MaybePrim<RulebookSectionId>, RulebookSectionId,
                                      MaybePrim<RulebookSubsectionId>, RulebookSubsectionId>


fun dbRulebookReference(chapterId : RulebookChapterId,
                        sectionId : Maybe<RulebookSectionId>,
                        subsectionId : Maybe<RulebookSubsectionId>) : DB_RulebookReference =
        Row3("rulebook_reference",
            Col("chapter_id", Prim(chapterId)),
            Col("section_id", MaybePrim(sectionId)),
            Col("subsection_id", MaybePrim(subsectionId)))

// SECTION
// ---------------------------------------------------------------------------------------------

typealias DB_Section = Row3<Prim<SectionName>, SectionName,
                            Coll<Page>, Page,
                            Prim<Icon>, Icon>


fun dbSection(sectionName : SectionName,
              pages : List<Page>,
              icon : Icon) : DB_Section =
        Row3("section",
            Col("section_name", Prim(sectionName)),
            Col("pages", Coll(pages)),
            Col("icon", Prim(icon)))

// SESSION
// ---------------------------------------------------------------------------------------------

typealias DB_Session = Row4<Prim<SessionName>, SessionName,
                            Prim<SessionLastActiveTime>, SessionLastActiveTime,
                            MaybePrim<SheetId>, SheetId,
                            Coll<SessionSheetRecord>, SessionSheetRecord>

fun dbSession(sessionName : SessionName,
              lastActiveTime : SessionLastActiveTime,
              activeSheetId : Maybe<SheetId>,
              sheetRecords : List<SessionSheetRecord>) : DB_Session =
        Row4("session",
            Col("session_name", Prim(sessionName)),
            Col("time_last_active", Prim(lastActiveTime)),
            Col("active_sheet_id", MaybePrim(activeSheetId)),
            Col("sheet_records", Coll(sheetRecords)))

// SESSION SHEET RECORD
// ---------------------------------------------------------------------------------------------

typealias DB_SessionSheetRecord = Row3<Prim<SheetId>, SheetId,
                                       Prim<SessionRecordIndex>, SessionRecordIndex,
                                       Prim<SheetLastActiveTime>, SheetLastActiveTime>

fun dbSessionSheetRecord(sheetId : SheetId,
                         sessionIndex : SessionRecordIndex,
                         lastActive : SheetLastActiveTime) : DB_SessionSheetRecord =
        Row3("session_sheet_record",
            Col("sheet_id", Prim(sheetId)),
            Col("session_index", Prim(sessionIndex)),
            Col("time_last_active", Prim(lastActive)))

// SETTINGS
// ---------------------------------------------------------------------------------------------

typealias DB_SheetSettings = Row3<Prim<ThemeId>, ThemeId,
                                  Prim<SheetName>, SheetName,
                                  Prim<SheetSummary>, SheetSummary>

fun dbSheetSettings(themeId : ThemeId,
                    sheetName : SheetName,
                    sheetSummary : SheetSummary) : DB_SheetSettings =
        Row3("sheet_settings",
            Col("theme_id", Prim(themeId)),
            Col("sheet_name", Prim(sheetName)),
            Col("sheet_summary", Prim(sheetSummary)))

// SHEET
// ---------------------------------------------------------------------------------------------

typealias DB_Sheet = Row6<Prim<SheetId>, SheetId,
                          Prim<CampaignId>, CampaignId,
                          Coll<Section>, Section,
                          Prod<Engine>, Engine,
                          Coll<Variable>, Variable,
                          Prod<Settings>, Settings>

fun dbSheet(sheetId : SheetId,
            campaignId : CampaignId,
            sections : List<Section>,
            engine : Engine,
            variables : List<Variable>,
            settings : Settings) : DB_Sheet =
        Row6("sheet",
            Col("sheet_id", Prim(sheetId)),
            Col("campaign_id", Prim(campaignId)),
            Col("sections", Coll(sections)),
            Col("engine", Prod(engine)),
            Col("variables", Coll(variables)),
            Col("settings", Prod(settings)))

// STATEMENT
// ---------------------------------------------------------------------------------------------

typealias DB_Statement = Row7<Prim<StatementBindingName>, StatementBindingName,
                              Prim<FunctionId>, FunctionId,
                              Sum<StatementParameter>, StatementParameter,
                              MaybeSum<StatementParameter>, StatementParameter,
                              MaybeSum<StatementParameter>, StatementParameter,
                              MaybeSum<StatementParameter>, StatementParameter,
                              MaybeSum<StatementParameter>, StatementParameter>

fun dbStatement(bindingName : StatementBindingName,
                functionId : FunctionId,
                parameter1 : StatementParameter,
                parameter2 : Maybe<StatementParameter>,
                parameter3 : Maybe<StatementParameter>,
                parameter4 : Maybe<StatementParameter>,
                parameter5 : Maybe<StatementParameter>) : DB_Statement =
        Row7("statement",
            Col("binding_name", Prim(bindingName)),
            Col("function_id", Prim(functionId)),
            Col("parameter_1", Sum(parameter1)),
            Col("parameter_2", MaybeSum(parameter2)),
            Col("parameter_3", MaybeSum(parameter3)),
            Col("parameter_4", MaybeSum(parameter4)),
            Col("parameter_5", MaybeSum(parameter5)))

// SUMMATION
// ---------------------------------------------------------------------------------------------

typealias DB_Summation = Row3<Prim<SummationId>, SummationId,
                              Prim<SummationName>, SummationName,
                              Coll<SummationTerm>, SummationTerm>

fun dbSummation(summationId : SummationId,
                summationName : SummationName,
                terms : List<SummationTerm>) : DB_Summation =
        Row3("summation",
            Col("summation_id", Prim(summationId)),
            Col("summation_name", Prim(summationName)),
            Col("terms", Coll(terms)))

// TERM: NUMBER
// ---------------------------------------------------------------------------------------------

typealias DB_TermNumber = Row2<MaybePrim<TermName>, TermName,
                               Sum<NumberReference>, NumberReference>

fun dbTermNumber(termName : Maybe<TermName>,
                 numberReference : NumberReference) : DB_TermNumber =
        Row2("summation_term_number",
            Col("term_name", MaybePrim(termName)),
            Col("value_reference", Sum(numberReference)))

// TERM: DICE ROLL
// ---------------------------------------------------------------------------------------------

typealias DB_TermDiceRoll = Row2<MaybePrim<TermName>, TermName,
                                 Sum<DiceRollReference>, DiceRollReference>

fun dbTermDiceRoll(termName : Maybe<TermName>,
                   diceRollReference : DiceRollReference) : DB_TermDiceRoll =
        Row2("summation_term_dice_roll",
            Col("term_name", MaybePrim(termName)),
            Col("value_reference", Sum(diceRollReference)))

// TERM: CONDITIONAL
// ---------------------------------------------------------------------------------------------

typealias DB_TermConditional = Row4<MaybePrim<TermName>, TermName,
                                    Sum<BooleanReference>, BooleanReference,
                                    Sum<NumberReference>, NumberReference,
                                    Sum<NumberReference>, NumberReference>

fun dbTermConditional(termName : Maybe<TermName>,
                      conditionalValueReference : BooleanReference,
                      trueValueReference : NumberReference,
                      falseValueReference: NumberReference) : DB_TermConditional =
        Row4("summation_term_conditional",
            Col("term_name", MaybePrim(termName)),
            Col("conditional_reference", Sum(conditionalValueReference)),
            Col("true_reference", Sum(trueValueReference)),
            Col("false_reference", Sum(falseValueReference)))

// TEXT FORMAT
// ---------------------------------------------------------------------------------------------

typealias DB_TextFormat = Row7<Prim<ColorTheme>, ColorTheme,
                              Prim<TextSize>, TextSize,
                              Prim<TextFont>, TextFont,
                              Prim<TextFontStyle>, TextFontStyle,
                              Prim<IsUnderlined>, IsUnderlined,
                              Prim<NumberFormat>, NumberFormat,
                              Prod<ElementFormat>, ElementFormat>

fun dbTextFormat(colorTheme : ColorTheme,
                 size : TextSize,
                 font : TextFont,
                 fontStyle : TextFontStyle,
                 isUnderlined : IsUnderlined,
                 numberFormat : NumberFormat,
                 elementFormat : ElementFormat) : DB_TextFormat =
        Row7("text_style",
            Col("color_theme", Prim(colorTheme)),
            Col("size", Prim(size)),
            Col("font", Prim(font)),
            Col("font_style", Prim(fontStyle)),
            Col("is_underlined", Prim(isUnderlined)),
            Col("number_format", Prim(numberFormat)),
            Col("element_format", Prod(elementFormat)))

// THEME
// ---------------------------------------------------------------------------------------------

typealias DB_Theme = Row3<Prim<ThemeId>, ThemeId,
                          Prim<ThemeColorSet>, ThemeColorSet,
                          Prod<UIColors>, UIColors>

fun dbTheme(themeId : ThemeId,
            palette : List<ThemeColor>,
            uiColors : UIColors) : DB_Theme =
        Row3("theme",
            Col("theme_id", Prim(themeId)),
            Col("palette", Prim(ThemeColorSet(palette))),
            Col("ui_colors", Prod(uiColors)))

// TUPLE
// ---------------------------------------------------------------------------------------------

typealias DB_Tuple = Row6<Sum<EngineValue>, EngineValue,
                          MaybeSum<EngineValue>, EngineValue,
                          MaybeSum<EngineValue>, EngineValue,
                          MaybeSum<EngineValue>, EngineValue,
                          MaybeSum<EngineValue>, EngineValue,
                          Sum<EngineValue>, EngineValue>

fun dbTuple(parameter1 : EngineValue,
            parameter2 : Maybe<EngineValue>,
            parameter3 : Maybe<EngineValue>,
            parameter4 : Maybe<EngineValue>,
            parameter5 : Maybe<EngineValue>,
            result : EngineValue) : DB_Tuple =
        Row6("tuple",
            Col("parameter1", Sum(parameter1)),
            Col("parameter2", MaybeSum(parameter2)),
            Col("parameter3", MaybeSum(parameter3)),
            Col("parameter4", MaybeSum(parameter4)),
            Col("parameter5", MaybeSum(parameter5)),
            Col("result", Sum(result)))

// UI COLORS
// ---------------------------------------------------------------------------------------------

typealias DB_UIColors = Row10<Prim<ColorId>, ColorId,
                              Prim<ColorId>, ColorId,
                              Prim<ColorId>, ColorId,
                              Prim<ColorId>, ColorId,
                              Prim<ColorId>, ColorId,
                              Prim<ColorId>, ColorId,
                              Prim<ColorId>, ColorId,
                              Prim<ColorId>, ColorId,
                              Prim<ColorId>, ColorId,
                              Prim<ColorId>, ColorId>

fun dbUIColors(toolbarBackgroundColorId : ColorId,
               toolbarIconsColorId : ColorId,
               toolbarTitleColorId : ColorId,
               tabBarBackgroundColorId : ColorId,
               tabTextNormalColorId : ColorId,
               tabTextSelectedColorId : ColorId,
               tabUnderlineColorId : ColorId,
               bottomBarBackgroundColorId : ColorId,
               bottomBarActiveColorId : ColorId,
               bottomBarInactiveColorId : ColorId) : DB_UIColors =
        Row10("ui_colors",
            Col("toolbar_background_color_id", Prim(toolbarBackgroundColorId)),
            Col("toolbar_icons_color_id", Prim(toolbarIconsColorId)),
            Col("toolbar_title_color_id", Prim(toolbarTitleColorId)),
            Col("tab_bar_background_color_id", Prim(tabBarBackgroundColorId)),
            Col("tab_text_normal_color_id", Prim(tabTextNormalColorId)),
            Col("tab_text_selected_color_id", Prim(tabTextSelectedColorId)),
            Col("tab_underline_color_id", Prim(tabUnderlineColorId)),
            Col("bottom_bar_background_color_id", Prim(bottomBarBackgroundColorId)),
            Col("bottom_bar_active_color_id", Prim(bottomBarActiveColorId)),
            Col("bottom_bar_inactive_color_id", Prim(bottomBarInactiveColorId)))

// WIDGET: LOG
// ---------------------------------------------------------------------------------------------

typealias DB_WidgetLog = Row3<Prim<WidgetId>, WidgetId,
                              Prod<LogWidgetFormat>, LogWidgetFormat,
                              Coll<LogEntry>, LogEntry>


fun dbWidgetLog(widgetId : WidgetId,
                format : LogWidgetFormat,
                entries : List<LogEntry>) : DB_WidgetLog =
        Row3("widget_log",
            Col("widget_id", Prim(widgetId)),
            Col("format", Prod(format)),
            Col("entries", Coll(entries)))

// WIDGET: LOG > ENTRY
// ---------------------------------------------------------------------------------------------

typealias DB_WidgetLogEntry = Row5<Prim<EntryTitle>, EntryTitle,
                                   Prim<EntryDate>, EntryDate,
                                   Prim<EntryAuthor>, EntryAuthor,
                                   MaybePrim<EntrySummary>, EntrySummary,
                                   Prim<EntryText>, EntryText>

fun dbWidgetLogEntry(title : EntryTitle,
                     date : EntryDate,
                     author : EntryAuthor,
                     summary : Maybe<EntrySummary>,
                     text : EntryText) : DB_WidgetLogEntry =
        Row5("widget_log_entry",
            Col("title", Prim(title)),
            Col("date", Prim(date)),
            Col("author", Prim(author)),
            Col("summary", MaybePrim(summary)),
            Col("text", Prim(text)))

// WIDGET: LOG > FORMAT
// ---------------------------------------------------------------------------------------------

typealias DB_WidgetLogFormat = Row3<Prod<WidgetFormat>, WidgetFormat,
                                    Prod<LogEntryFormat>, LogEntryFormat,
                                    Prim<EntryViewType>, EntryViewType>

fun dbWidgetLogFormat(format : WidgetFormat,
                      entryFormat : LogEntryFormat,
                      entryViewType : EntryViewType) : DB_WidgetLogFormat =
        Row3("widget_log_format",
            Col("format", Prod(format)),
            Col("entry_format", Prod(entryFormat)),
            Col("entry_view_type", Prim(entryViewType)))

// WIDGET: LOG > ENTRY FORMAT
// ---------------------------------------------------------------------------------------------

typealias DB_WidgetLogEntryFormat = Row9<Prod<ElementFormat>, ElementFormat,
                                         Prod<TextFormat>, TextFormat,
                                         Prod<ElementFormat>, ElementFormat,
                                         Prod<TextFormat>, TextFormat,
                                         Prod<ElementFormat>, ElementFormat,
                                         Prod<TextFormat>, TextFormat,
                                         Prod<ElementFormat>, ElementFormat,
                                         Prod<TextFormat>, TextFormat,
                                         Prod<ElementFormat>, ElementFormat>

fun dbWidgetLogEntryFormat(titleFormat : ElementFormat,
                           titleStyle : TextFormat,
                           authorFormat : ElementFormat,
                           authorStyle : TextFormat,
                           summaryFormat : ElementFormat,
                           summaryStyle : TextFormat,
                           bodyFormat : ElementFormat,
                           bodyStyle : TextFormat,
                           entryFormat : ElementFormat) : DB_WidgetLogEntryFormat =
        Row9("widget_log_entry_format",
            Col("title_format", Prod(titleFormat)),
            Col("title_style", Prod(titleStyle)),
            Col("author_format", Prod(authorFormat)),
            Col("author_style", Prod(authorStyle)),
            Col("summary_format", Prod(summaryFormat)),
            Col("summary_style", Prod(summaryStyle)),
            Col("body_format", Prod(bodyFormat)),
            Col("body_style", Prod(bodyStyle)),
            Col("entry_format", Prod(entryFormat)))

// WIDGET: MECHANIC
// ---------------------------------------------------------------------------------------------

typealias DB_WidgetMechanic = Row3<Prim<WidgetId>, WidgetId,
                                   Prod<MechanicWidgetFormat>, MechanicWidgetFormat,
                                   Prim<MechanicCategoryId>, MechanicCategoryId>


fun dbWidgetMechanic(widgetId : WidgetId,
                     format : MechanicWidgetFormat,
                     categoryId : MechanicCategoryId) : DB_WidgetMechanic =
        Row3("widget_mechanic",
            Col("widget_id", Prim(widgetId)),
            Col("format", Prod(format)),
            Col("entries", Prim(categoryId)))

// WIDGET: MECHANIC > FORMAT
// ---------------------------------------------------------------------------------------------

typealias DB_WidgetMechanicFormat = Row9<Prod<WidgetFormat>, WidgetFormat,
                                         Prim<MechanicWidgetViewType>, MechanicWidgetViewType,
                                         Prod<ElementFormat>, ElementFormat,
                                         Prod<ElementFormat>, ElementFormat,
                                         Prod<TextFormat>, TextFormat,
                                         Prod<ElementFormat>, ElementFormat,
                                         Prod<TextFormat>, TextFormat,
                                         Prod<ElementFormat>, ElementFormat,
                                         Prod<TextFormat>, TextFormat>


fun dbWidgetMechanicFormat(widgetFormat : WidgetFormat,
                           viewType : MechanicWidgetViewType,
                           mechanicFormat : ElementFormat,
                           headerFormat : ElementFormat,
                           headerStyle : TextFormat,
                           mechanicHeaderFormat : ElementFormat,
                           mechanicHeaderStyle : TextFormat,
                           mechanicSummaryFormat : ElementFormat,
                           mechanicSummaryStyle : TextFormat) : DB_WidgetMechanicFormat =
        Row9("widget_mechanic_format",
            Col("widget_format", Prod(widgetFormat)),
                Col("view_type", Prim(viewType)),
                Col("view_type", Prod(mechanicFormat)),
                Col("view_type", Prod(headerFormat)),
                Col("view_type", Prod(headerStyle)),
                Col("view_type", Prod(mechanicHeaderFormat)),
                Col("view_type", Prod(mechanicHeaderStyle)),
                Col("view_type", Prod(mechanicSummaryFormat)),
                Col("view_type", Prod(mechanicSummaryStyle)))

// WIDGET: NUMBER
// ---------------------------------------------------------------------------------------------

typealias DB_WidgetNumber = Row3<Prim<WidgetId>, WidgetId,
                                 Prod<NumberWidgetFormat>, NumberWidgetFormat,
                                 Prim<VariableId>, VariableId>


fun dbWidgetNumber(widgetId : WidgetId,
                   format : NumberWidgetFormat,
                   valueVariableId : VariableId) : DB_WidgetNumber =
        Row3("widget_number",
            Col("widget_id", Prim(widgetId)),
            Col("format", Prod(format)),
            Col("value_variable_id", Prim(valueVariableId)))

// WIDGET: NUMBER > FORMAT
// ---------------------------------------------------------------------------------------------

typealias DB_WidgetNumberFormat = Row8<Prod<WidgetFormat>, WidgetFormat,
                                       Prod<ElementFormat>, ElementFormat,
                                       Prod<TextFormat>, TextFormat,
                                       Prod<ElementFormat>, ElementFormat,
                                       Prod<TextFormat>, TextFormat,
                                       Prod<ElementFormat>, ElementFormat,
                                       Prod<TextFormat>, TextFormat,
                                       Prim<NumberFormat>, NumberFormat>

fun dbWidgetNumberFormat(widgetFormat : WidgetFormat,
                         insideLabelFormat : ElementFormat,
                         insideLabelStyle : TextFormat,
                         outsideLabelFormat : ElementFormat,
                         outsideLabelStyle : TextFormat,
                         valueFormat : ElementFormat,
                         valueStyle : TextFormat,
                         numberFormat : NumberFormat) : DB_WidgetNumberFormat =
        Row8("widget_number",
            Col("widget_format", Prod(widgetFormat)),
            Col("inside_label_format", Prod(insideLabelFormat)),
            Col("inside_label_style", Prod(insideLabelStyle)),
            Col("outside_label_format", Prod(insideLabelFormat)),
            Col("outside_label_style", Prod(insideLabelStyle)),
            Col("value_format", Prod(insideLabelFormat)),
            Col("value_style", Prod(insideLabelStyle)),
            Col("number_format", Prim(numberFormat)))

// WIDGET FORMAT
// ---------------------------------------------------------------------------------------------

typealias DB_WidgetFormat = Row2<Prim<WidgetWidth>, WidgetWidth,
                                 Prod<ElementFormat>, ElementFormat>


fun dbWidgetFormat(width : WidgetWidth,
                   elementFormat : ElementFormat) : DB_WidgetFormat =
        Row2("widget_number",
            Col("width", Prim(width)),
            Col("element_format", Prod(elementFormat)))

// WIDGET: POINTS
// ---------------------------------------------------------------------------------------------

typealias DB_WidgetPoints = Row5<Prim<WidgetId>, WidgetId,
                                 Prod<PointsWidgetFormat>, PointsWidgetFormat,
                                 Prim<VariableId>, VariableId,
                                 Prim<VariableId>, VariableId,
                                 MaybePrim<PointsWidgetLabel>, PointsWidgetLabel>

fun dbWidgetPoints(widgetId : WidgetId,
                   format : PointsWidgetFormat,
                   limitValueVariableId : VariableId,
                   currenttValueVariableId : VariableId,
                   label : Maybe<PointsWidgetLabel>) : DB_WidgetPoints =
        Row5("widget_number",
            Col("widget_id", Prim(widgetId)),
            Col("format", Prod(format)),
            Col("limit_value_variable_id", Prim(limitValueVariableId)),
            Col("current_value_variable_id", Prim(currenttValueVariableId)),
            Col("label", MaybePrim(label)))

// WIDGET: POINTS > FORMAT
// ---------------------------------------------------------------------------------------------

typealias DB_WidgetPointsFormat = Row5<Prod<WidgetFormat>, WidgetFormat,
                                       Prod<TextFormat>, TextFormat,
                                       Prod<TextFormat>, TextFormat,
                                       Prod<TextFormat>, TextFormat,
                                       Prod<PointsBarFormat>, PointsBarFormat>

fun dbWidgetPointsFormat(widgetFormat : WidgetFormat,
                         limitTextFormat : TextFormat,
                         currentTextFormat : TextFormat,
                         labelTextFormat : TextFormat,
                         barFormat : PointsBarFormat) : DB_WidgetPointsFormat =
        Row5("widget_points_format",
            Col("widget_format", Prod(widgetFormat)),
            Col("limit_text_format", Prod(limitTextFormat)),
            Col("current_text_format", Prod(currentTextFormat)),
            Col("label_text_format", Prod(labelTextFormat)),
            Col("bar_format", Prod(barFormat)))

// WIDGET: POINTS > BAR FORMAT
// ---------------------------------------------------------------------------------------------

typealias DB_WidgetPointsBarFormat = Row5<Prim<PointsBarStyle>, PointsBarStyle,
                                          Prim<PointsAboveBarStyle>, PointsAboveBarStyle,
                                          Prim<PointsBarHeight>, PointsBarHeight,
                                          Prim<ColorTheme>, ColorTheme,
                                          Prim<ColorTheme>, ColorTheme>

fun dbWidgetPointsBarFormat(barStyle : PointsBarStyle,
                            barAboveStyle : PointsAboveBarStyle,
                            barHeight : PointsBarHeight,
                            limitColorTheme : ColorTheme,
                            currentColorTheme : ColorTheme) : DB_WidgetPointsBarFormat =
        Row5("widget_points_bar_format",
            Col("style", Prim(barStyle)),
            Col("above_style", Prim(barAboveStyle)),
            Col("height", Prim(barHeight)),
            Col("limit_color_theme", Prim(limitColorTheme)),
            Col("current_color_theme", Prim(currentColorTheme)))

// WIDGET: QUOTE
// ---------------------------------------------------------------------------------------------

typealias DB_WidgetQuote = Row4<Prim<WidgetId>, WidgetId,
                                Prod<QuoteWidgetFormat>, QuoteWidgetFormat,
                                Prim<VariableId>, VariableId,
                                MaybePrim<VariableId>, VariableId>

fun dbWidgetQuote(widgetId : WidgetId,
                  format : QuoteWidgetFormat,
                  quoteVariableId : VariableId,
                  sourceVariableId : Maybe<VariableId>) : DB_WidgetQuote =
        Row4("widget_quote",
            Col("widget_id", Prim(widgetId)),
            Col("format", Prod(format)),
            Col("quote_variable_id", Prim(quoteVariableId)),
            Col("source_variable_id", MaybePrim(sourceVariableId)))

// WIDGET: QUOTE > FORMAT
// ---------------------------------------------------------------------------------------------

typealias DB_WidgetQuoteFormat = Row5<Prod<WidgetFormat>, WidgetFormat,
                                      Prim<QuoteViewType>, QuoteViewType,
                                      Prod<TextFormat>, TextFormat,
                                      Prod<TextFormat>, TextFormat,
                                      Prod<IconFormat>, IconFormat>

fun dbWidgetQuoteFormat(widgetFormat : WidgetFormat,
                        viewType : QuoteViewType,
                        quoteFormat : TextFormat,
                        sourceFormat : TextFormat,
                        iconFormat : IconFormat) : DB_WidgetQuoteFormat =
        Row5("widget_quote",
            Col("widget_format", Prod(widgetFormat)),
            Col("view_type", Prim(viewType)),
            Col("quote_format", Prod(quoteFormat)),
            Col("source_format", Prod(sourceFormat)),
            Col("icon_format", Prod(iconFormat)))

// WIDGET: STORY
// ---------------------------------------------------------------------------------------------

typealias DB_WidgetStory = Row3<Prim<WidgetId>, WidgetId,
                                Prod<StoryWidgetFormat>, StoryWidgetFormat,
                                Coll<StoryPart>, StoryPart>

fun dbWidgetStory(widgetId : WidgetId,
                  format : StoryWidgetFormat,
                  story : List<StoryPart>) : DB_WidgetStory =
        Row3("widget_story",
            Col("widget_id", Prim(widgetId)),
            Col("format", Prod(format)),
            Col("story", Coll(story)))

// WIDGET: STORY > FORMAT
// ---------------------------------------------------------------------------------------------

typealias DB_WidgetStoryFormat = Row2<Prod<WidgetFormat>, WidgetFormat,
                                      Prim<LineSpacing>, LineSpacing>

fun dbWidgetStoryFormat(widgetFormat : WidgetFormat,
                        lineSpacing : LineSpacing) : DB_WidgetStoryFormat =
        Row2("widget_quote",
            Col("widget_format", Prod(widgetFormat)),
            Col("line_spacing", Prim(lineSpacing)))

// WIDGET: STORY > PART: SPAN
// ---------------------------------------------------------------------------------------------

typealias DB_WidgetStoryPartSpan = Row2<Prod<TextFormat>, TextFormat,
                                        Prim<StoryPartText>, StoryPartText>

fun dbWidgetStoryPartSpan(textFormat : TextFormat,
                          text : StoryPartText) : DB_WidgetStoryPartSpan =
        Row2("widget_story_part_span",
            Col("text_format", Prod(textFormat)),
            Col("text", Prim(text)))

// WIDGET: STORY > PART: VARIABLE
// ---------------------------------------------------------------------------------------------

typealias DB_WidgetStoryPartVariable = Row3<Prod<TextFormat>, TextFormat,
                                            Prim<VariableId>, VariableId,
                                            Prim<NumericEditorType>, NumericEditorType>

fun dbWidgetStoryPartVariable(textFormat : TextFormat,
                              variableId : VariableId,
                              numEditorType : NumericEditorType) : DB_WidgetStoryPartVariable =
        Row3("widget_story_part_variable",
            Col("text_format", Prod(textFormat)),
            Col("variable_id", Prim(variableId)),
            Col("numeric_editor_type", Prim(numEditorType)))

// WIDGET: STORY > PART: ICON
// ---------------------------------------------------------------------------------------------

typealias DB_WidgetStoryPartIcon = Row2<Prim<Icon>, Icon,
                                        Prod<IconFormat>, IconFormat>

fun dbWidgetStoryPartIcon(icon : Icon,
                          iconFormat : IconFormat) : DB_WidgetStoryPartIcon =
        Row2("widget_story_part_icon",
            Col("icon", Prim(icon)),
            Col("icon_format", Prod(iconFormat)))

// WIDGET: STORY > PART: ACTION
// ---------------------------------------------------------------------------------------------

typealias DB_WidgetStoryPartAction = Row5<Prim<StoryPartText>, StoryPartText,
                                          Prod<Action>, Action,
                                          Prod<TextFormat>, TextFormat,
                                          Prod<IconFormat>, IconFormat,
                                          Prim<ShowProcedureDialog>, ShowProcedureDialog>

fun dbWidgetStoryPartAction(text : StoryPartText,
                            action : Action,
                            textFormat : TextFormat,
                            iconFormat : IconFormat,
                            showProcedureDialog : ShowProcedureDialog)
                             : DB_WidgetStoryPartAction =
        Row5("widget_story_part_action",
            Col("text", Prim(text)),
            Col("action", Prod(action)),
            Col("text_format", Prod(textFormat)),
            Col("icon_format", Prod(iconFormat)),
            Col("show_procedure_dialog", Prim(showProcedureDialog)))

// WIDGET: TABLE
// ---------------------------------------------------------------------------------------------

typealias DB_WidgetTable = Row5<Prim<WidgetId>, WidgetId,
                                Prod<TableWidgetFormat>, TableWidgetFormat,
                                Coll<TableWidgetColumn>, TableWidgetColumn,
                                Coll<TableWidgetRow>, TableWidgetRow,
                                MaybePrim<TableSort>, TableSort>

fun dbWidgetTable(widgetId : WidgetId,
                  format : TableWidgetFormat,
                  columns : MutableList<TableWidgetColumn>,
                  rows : MutableList<TableWidgetRow>,
                  sort : Maybe<TableSort>) : DB_WidgetTable =
        Row5("widget_table",
            Col("widget_id", Prim(widgetId)),
            Col("format", Prod(format)),
            Col("columns", Coll(columns)),
            Col("rows", Coll(rows)),
            Col("sort", MaybePrim(sort)))

// WIDGET: TABLE > CELL: BOOLEAN
// ---------------------------------------------------------------------------------------------

typealias DB_WidgetTableCellBoolean = Row2<Prod<BooleanCellFormat>, BooleanCellFormat,
                                           Sum<BooleanVariableValue>, BooleanVariableValue>

fun dbWidgetTableCellBoolean(format : BooleanCellFormat,
                             variableValue : BooleanVariableValue)
                              : DB_WidgetTableCellBoolean =
        Row2("widget_table_cell_boolean",
            Col("format", Prod(format)),
            Col("variable_value", Sum(variableValue)))

// WIDGET: TABLE > CELL: BOOLEAN > FORMAT
// ---------------------------------------------------------------------------------------------

typealias DB_WidgetTableCellBooleanFormat =
                        Row5<MaybeProd<ElementFormat>, ElementFormat,
                             MaybeProd<TextFormat>, TextFormat,
                             MaybeProd<TextFormat>, TextFormat,
                             MaybePrim<ShowTrueIcon>, ShowTrueIcon,
                             MaybePrim<ShowFalseIcon>, ShowFalseIcon>

fun dbWidgetTableCellBooleanFormat(elementFormat : Maybe<ElementFormat>,
                                   trueFormat : Maybe<TextFormat>,
                                   falseFormat : Maybe<TextFormat>,
                                   showTrueIcon : Maybe<ShowTrueIcon>,
                                   showFalseIcon : Maybe<ShowFalseIcon>)
                                    : DB_WidgetTableCellBooleanFormat =
        Row5("widget_table_cell_boolean_format",
            Col("element_format", MaybeProd(elementFormat)),
            Col("true_format", MaybeProd(trueFormat)),
            Col("false_format", MaybeProd(falseFormat)),
            Col("show_true_icon", MaybePrim(showTrueIcon)),
            Col("show_false_icon", MaybePrim(showFalseIcon)))

// WIDGET: TABLE > CELL: NUMBER
// ---------------------------------------------------------------------------------------------

typealias DB_WidgetTableCellNumber = Row4<Prod<NumberCellFormat>, NumberCellFormat,
                                          Sum<NumberVariableValue>, NumberVariableValue,
                                          MaybePrim<NumericEditorType>, NumericEditorType,
                                          MaybeProd<Action>, Action>

fun dbWidgetTableCellNumber(format : NumberCellFormat,
                            variableValue : NumberVariableValue,
                            editorType : Maybe<NumericEditorType>,
                            action : Maybe<Action>)
                              : DB_WidgetTableCellNumber =
        Row4("widget_table_cell_number",
            Col("format", Prod(format)),
            Col("variable_value", Sum(variableValue)),
            Col("editor_type", MaybePrim(editorType)),
            Col("action", MaybeProd(action)))

// WIDGET: TABLE > CELL: NUMBER > FORMAT
// ---------------------------------------------------------------------------------------------

typealias DB_WidgetTableCellNumberFormat =
                        Row2<MaybeProd<ElementFormat>, ElementFormat,
                             MaybeProd<TextFormat>, TextFormat>

fun dbWidgetTableCellNumberFormat(elementFormat : Maybe<ElementFormat>,
                                  textFormat : Maybe<TextFormat>)
                                   : DB_WidgetTableCellNumberFormat =
        Row2("widget_table_cell_number_format",
            Col("element_format", MaybeProd(elementFormat)),
            Col("text_format", MaybeProd(textFormat)))

// WIDGET: TABLE > CELL: TEXT
// ---------------------------------------------------------------------------------------------

typealias DB_WidgetTableCellText = Row3<Prod<TextCellFormat>, TextCellFormat,
                                        Sum<TextVariableValue>, TextVariableValue,
                                        MaybeProd<Action>, Action>

fun dbWidgetTableCellText(format : TextCellFormat,
                          variableValue : TextVariableValue,
                          action : Maybe<Action>)
                           : DB_WidgetTableCellText =
        Row3("widget_table_cell_text",
            Col("format", Prod(format)),
            Col("variable_value", Sum(variableValue)),
            Col("action", MaybeProd(action)))

// WIDGET: TABLE > CELL: TEXT > FORMAT
// ---------------------------------------------------------------------------------------------

typealias DB_WidgetTableCellTextFormat =
                        Row2<MaybeProd<ElementFormat>, ElementFormat,
                             MaybeProd<TextFormat>, TextFormat>

fun dbWidgetTableCellTextFormat(elementFormat : Maybe<ElementFormat>,
                                textFormat : Maybe<TextFormat>)
                                   : DB_WidgetTableCellTextFormat =
        Row2("widget_table_cell_text_format",
            Col("element_format", MaybeProd(elementFormat)),
            Col("text_format", MaybeProd(textFormat)))

// WIDGET: TABLE > COLUMN: BOOLEAN
// ---------------------------------------------------------------------------------------------

typealias DB_WidgetTableColumnBoolean = Row5<Prim<ColumnName>, ColumnName,
                                             Prim<ColumnVariablePrefix>, ColumnVariablePrefix,
                                             Prim<IsColumnNamespaced>, IsColumnNamespaced,
                                             Sum<BooleanVariableValue>, BooleanVariableValue,
                                             Prod<BooleanColumnFormat>, BooleanColumnFormat>

fun dbWidgetTableColumnBoolean(columnName : ColumnName,
                               variablePrefix : ColumnVariablePrefix,
                               isColumnNamespaced:  IsColumnNamespaced,
                               defaultValue : BooleanVariableValue,
                               format : BooleanColumnFormat) : DB_WidgetTableColumnBoolean =
        Row5("widget_table_column_boolean",
            Col("column_name", Prim(columnName)),
            Col("variable_prefix", Prim(variablePrefix)),
            Col("is_column_namespaced", Prim(isColumnNamespaced)),
            Col("default_value", Sum(defaultValue)),
            Col("format", Prod(format)))

// WIDGET: TABLE > COLUMN: BOOLEAN > FORMAT
// ---------------------------------------------------------------------------------------------

typealias DB_WidgetTableColumnBooleanFormat =
                        Row7<Prod<ColumnFormat>, ColumnFormat,
                             Prod<TextFormat>, TextFormat,
                             Prod<TextFormat>, TextFormat,
                             Prim<ColumnTrueText>, ColumnTrueText,
                             Prim<ColumnFalseText>, ColumnFalseText,
                             Prim<ShowTrueIcon>, ShowTrueIcon,
                             Prim<ShowFalseIcon>, ShowFalseIcon>

fun dbWidgetTableColumnBooleanFormat(columnFormat : ColumnFormat,
                                     trueFormat : TextFormat,
                                     falseFormat : TextFormat,
                                     trueText : ColumnTrueText,
                                     falseText : ColumnFalseText,
                                     showTrueIcon : ShowTrueIcon,
                                     showFalseIcon : ShowFalseIcon)
                                      : DB_WidgetTableColumnBooleanFormat =
        Row7("widget_table_column_boolean_format",
            Col("column_name", Prod(columnFormat)),
            Col("true_format", Prod(trueFormat)),
            Col("false_format", Prod(falseFormat)),
            Col("true_text", Prim(trueText)),
            Col("false_text", Prim(falseText)),
            Col("show_true_icon", Prim(showTrueIcon)),
            Col("show_false_icon", Prim(showFalseIcon)))


// WIDGET: TABLE > COLUMN: NUMBER
// ---------------------------------------------------------------------------------------------

typealias DB_WidgetTableColumnNumber = Row7<Prim<ColumnName>, ColumnName,
                                            Prim<ColumnVariablePrefix>, ColumnVariablePrefix,
                                            Prim<IsColumnNamespaced>, IsColumnNamespaced,
                                            Sum<NumberVariableValue>, NumberVariableValue,
                                            Prod<NumberColumnFormat>, NumberColumnFormat,
                                            MaybeProd<Action>, Action,
                                            Prim<NumericEditorType>, NumericEditorType>

fun dbWidgetTableColumnNumber(columnName : ColumnName,
                              variablePrefix : ColumnVariablePrefix,
                              isColumnNamespaced:  IsColumnNamespaced,
                              defaultValue : NumberVariableValue,
                              format : NumberColumnFormat,
                              action : Maybe<Action>,
                              editorType : NumericEditorType)
                                : DB_WidgetTableColumnNumber =
        Row7("widget_table_column_number",
            Col("column_name", Prim(columnName)),
            Col("variable_prefix", Prim(variablePrefix)),
            Col("is_column_namespaced", Prim(isColumnNamespaced)),
            Col("default_value", Sum(defaultValue)),
            Col("format", Prod(format)),
            Col("action", MaybeProd(action)),
            Col("editor_type", Prim(editorType)))

// WIDGET: TABLE > COLUMN: NUMBER > FORMAT
// ---------------------------------------------------------------------------------------------

typealias DB_WidgetTableColumnNumberFormat =
                        Row1<Prod<ColumnFormat>, ColumnFormat>

fun dbWidgetTableColumnNumberFormat(columnFormat : ColumnFormat)
                                     : DB_WidgetTableColumnNumberFormat =
        Row1("widget_table_column_number_format",
            Col("column_format", Prod(columnFormat)))

// WIDGET: TABLE > COLUMN: TEXT
// ---------------------------------------------------------------------------------------------

typealias DB_WidgetTableColumnText = Row7<Prim<ColumnName>, ColumnName,
                                          Prim<ColumnVariablePrefix>, ColumnVariablePrefix,
                                          Prim<IsColumnNamespaced>, IsColumnNamespaced,
                                          Sum<TextVariableValue>, TextVariableValue,
                                          Prod<TextColumnFormat>, TextColumnFormat,
                                          MaybeProd<Action>, Action,
                                          Prim<DefinesNamespace>, DefinesNamespace>

fun dbWidgetTableColumnText(columnName : ColumnName,
                            variablePrefix : ColumnVariablePrefix,
                            isColumnNamespaced:  IsColumnNamespaced,
                            defaultValue : TextVariableValue,
                            format : TextColumnFormat,
                            action : Maybe<Action>,
                            definesNamespace : DefinesNamespace)
                             : DB_WidgetTableColumnText =
        Row7("widget_table_column_text",
            Col("column_name", Prim(columnName)),
            Col("variable_prefix", Prim(variablePrefix)),
            Col("is_column_namespaced", Prim(isColumnNamespaced)),
            Col("default_value", Sum(defaultValue)),
            Col("format", Prod(format)),
            Col("action", MaybeProd(action)),
            Col("defines_namespace", Prim(definesNamespace)))

// WIDGET: TABLE > COLUMN: TEXT > FORMAT
// ---------------------------------------------------------------------------------------------

typealias DB_WidgetTableColumnTextFormat =
                        Row1<Prod<ColumnFormat>, ColumnFormat>

fun dbWidgetTableColumnTextFormat(columnFormat : ColumnFormat)
                                    : DB_WidgetTableColumnTextFormat =
        Row1("widget_table_column_text_format",
            Col("column_format", Prod(columnFormat)))

// WIDGET: TABLE > COLUMN > FORMAT
// ---------------------------------------------------------------------------------------------

typealias DB_WidgetTableColumnFormat = Row3<Prod<TextFormat>, TextFormat,
                                            Prod<ElementFormat>, ElementFormat,
                                            Prim<ColumnWidth>, ColumnWidth>

fun dbWidgetTableColumnFormat(textFormat : TextFormat,
                              elementFormat : ElementFormat,
                              width : ColumnWidth) : DB_WidgetTableColumnFormat =
        Row3("widget_table_column_format",
            Col("text_format", Prod(textFormat)),
            Col("element_format", Prod(elementFormat)),
            Col("width", Prim(width)))

// WIDGET: TABLE > FORMAT
// ---------------------------------------------------------------------------------------------

typealias DB_WidgetTableFormat = Row5<Prod<WidgetFormat>, WidgetFormat,
                                      Prod<TableWidgetRowFormat>, TableWidgetRowFormat,
                                      Prod<TableWidgetRowFormat>, TableWidgetRowFormat,
                                      MaybeProd<Divider>, Divider,
                                      Prim<Height>, Height>

fun dbWidgetTableFormat(widgetFormat : WidgetFormat,
                        headerFormat : TableWidgetRowFormat,
                        rowFormat : TableWidgetRowFormat,
                        divider : Maybe<Divider>,
                        cellHeight : Height) : DB_WidgetTableFormat =
        Row5("widget_table_format",
            Col("widget_format", Prod(widgetFormat)),
            Col("header_format", Prod(headerFormat)),
            Col("row_format", Prod(rowFormat)),
            Col("divider", MaybeProd(divider)),
            Col("cell_height", Prim(cellHeight)))

// WIDGET: TABLE > ROW
// ---------------------------------------------------------------------------------------------

typealias DB_WidgetTableRow = Row2<Prod<TableWidgetRowFormat>, TableWidgetRowFormat,
                                   Coll<TableWidgetCell>, TableWidgetCell>

fun dbWidgetTableRow(format : TableWidgetRowFormat,
                     cells : List<TableWidgetCell>) : DB_WidgetTableRow =
        Row2("widget_table_row",
            Col("format", Prod(format)),
            Col("cells", Coll(cells)))

// WIDGET: TABLE > ROW > FORMAT
// ---------------------------------------------------------------------------------------------

typealias DB_WidgetTableRowFormat = Row2<Prod<TextFormat>, TextFormat,
                                         Prod<ElementFormat>, ElementFormat>

fun dbWidgetTableRowFormat(textFormat : TextFormat,
                           elementFormat : ElementFormat) : DB_WidgetTableRowFormat =
        Row2("widget_table_row_format",
            Col("text_format", Prod(textFormat)),
            Col("element_format", Prod(elementFormat)))

// WIDGET: TEXT
// ---------------------------------------------------------------------------------------------

typealias DB_WidgetText = Row3<Prim<WidgetId>, WidgetId,
                               Prod<TextWidgetFormat>, TextWidgetFormat,
                               Prim<VariableId>, VariableId>

fun dbWidgetText(widgetId : WidgetId,
                 format : TextWidgetFormat,
                 valueVariableId : VariableId) : DB_WidgetText =
        Row3("widget_text",
            Col("widget_id", Prim(widgetId)),
            Col("format", Prod(format)),
            Col("value_variable_id", Prim(valueVariableId)))

// WIDGET: TEXT > FORMAT
// ---------------------------------------------------------------------------------------------

typealias DB_WidgetTextFormat = Row4<Prod<WidgetFormat>, WidgetFormat,
                                     Prod<TextFormat>, TextFormat,
                                     Prod<TextFormat>, TextFormat,
                                     Prod<TextFormat>, TextFormat>

fun dbWidgetTextFormat(widgetFormat : WidgetFormat,
                       insideLabelFormat : TextFormat,
                       outsideLabelFormat : TextFormat,
                       valueFormat : TextFormat) : DB_WidgetTextFormat =
        Row4("widget_text_format",
            Col("widget_format", Prod(widgetFormat)),
            Col("inside_label_format", Prod(insideLabelFormat)),
            Col("outside_label_format", Prod(outsideLabelFormat)),
            Col("value_format", Prod(valueFormat)))

// VALUE: NUMBER
// ---------------------------------------------------------------------------------------------

typealias DB_ValueNumber = Row5<Prim<ValueId>, ValueId,
                                Prim<ValueDescription>, ValueDescription,
                                MaybeProd<RulebookReference>, RulebookReference,
                                Coll<Variable>, Variable,
                                Prim<NumberValue>, NumberValue>

fun dbValueNumber(valueId : ValueId,
                  description : ValueDescription,
                  rulebookReference : Maybe<RulebookReference>,
                  variables : List<Variable>,
                  value : NumberValue) : DB_ValueNumber =
        Row5("value_number",
            Col("value_id", Prim(valueId)),
            Col("description", Prim(description)),
            Col("rulebook_reference", MaybeProd(rulebookReference)),
            Col("variables", Coll(variables)),
            Col("value", Prim(value)))

// VALUE: TEXT
// ---------------------------------------------------------------------------------------------

typealias DB_ValueText = Row5<Prim<ValueId>, ValueId,
                              Prim<ValueDescription>, ValueDescription,
                              MaybeProd<RulebookReference>, RulebookReference,
                              Coll<Variable>, Variable,
                              Prim<TextValue>, TextValue>

fun dbValueText(valueId : ValueId,
                description : ValueDescription,
                rulebookReference : Maybe<RulebookReference>,
                variables : List<Variable>,
                value : TextValue) : DB_ValueText =
        Row5("value_text",
            Col("value_id", Prim(valueId)),
            Col("description", Prim(description)),
            Col("rulebook_reference", MaybeProd(rulebookReference)),
            Col("variables", Coll(variables)),
            Col("value", Prim(value)))

// VALUE SET: BASE
// ---------------------------------------------------------------------------------------------

typealias DB_ValueSetBase = Row6<Prim<ValueSetId>, ValueSetId,
                                 Prim<ValueSetLabel>, ValueSetLabel,
                                 Prim<ValueSetLabelSingular>, ValueSetLabelSingular,
                                 Prim<ValueSetDescription>, ValueSetDescription,
                                 Prim<ValueType>, ValueType,
                                 Coll<Value>, Value>

fun dbValueSetBase(valueSetId : ValueSetId,
                   label : ValueSetLabel,
                   labelSingular : ValueSetLabelSingular,
                   description : ValueSetDescription,
                   valueType : ValueType,
                   values : List<Value>) : DB_ValueSetBase =
        Row6("widget_value_set_base",
            Col("value_set_id", Prim(valueSetId)),
            Col("label", Prim(label)),
            Col("label_singular", Prim(labelSingular)),
            Col("description", Prim(description)),
            Col("value_type", Prim(valueType)),
            Col("values", Coll(values)))

// VALUE SET: COMPOUND
// ---------------------------------------------------------------------------------------------

typealias DB_ValueSetCompound = Row6<Prim<ValueSetId>, ValueSetId,
                                     Prim<ValueSetLabel>, ValueSetLabel,
                                     Prim<ValueSetLabelSingular>, ValueSetLabelSingular,
                                     Prim<ValueSetDescription>, ValueSetDescription,
                                     Prim<ValueType>, ValueType,
                                     Prim<ValueSetIdSet>, ValueSetIdSet>

fun dbValueSetCompound(valueSetId : ValueSetId,
                       label : ValueSetLabel,
                       labelSingular : ValueSetLabelSingular,
                       description : ValueSetDescription,
                       valueType : ValueType,
                       values : List<ValueSetId>) : DB_ValueSetCompound =
        Row6("widget_value_set_compound",
            Col("value_set_id", Prim(valueSetId)),
            Col("label", Prim(label)),
            Col("label_singular", Prim(labelSingular)),
            Col("description", Prim(description)),
            Col("value_type", Prim(valueType)),
            Col("value_set_ids", Prim(ValueSetIdSet(values))))

// VARIABLE: BOOLEAN
// ---------------------------------------------------------------------------------------------

typealias DB_VariableBoolean = Row5<Prim<VariableId>, VariableId,
                                    Prim<VariableLabel>, VariableLabel,
                                    Prim<VariableDescription>, VariableDescription,
                                    Prim<VariableTagSet>, VariableTagSet,
                                    Sum<BooleanVariableValue>, BooleanVariableValue>

fun dbVariableBoolean(variableId : VariableId,
                      label : VariableLabel,
                      description : VariableDescription,
                      tags : List<VariableTag>,
                      variableValue : BooleanVariableValue) : DB_VariableBoolean =
        Row5("variable_boolean",
            Col("variable_id", Prim(variableId)),
            Col("label", Prim(label)),
            Col("description", Prim(description)),
            Col("tags", Prim(VariableTagSet(tags))),
            Col("variable_value", Sum(variableValue)))

// VARIABLE: DICE ROLL
// ---------------------------------------------------------------------------------------------

typealias DB_VariableDiceRoll = Row5<Prim<VariableId>, VariableId,
                                     Prim<VariableLabel>, VariableLabel,
                                     Prim<VariableDescription>, VariableDescription,
                                     Prim<VariableTagSet>, VariableTagSet,
                                     Sum<DiceRollVariableValue>, DiceRollVariableValue>

fun dbVariableDiceRoll(variableId : VariableId,
                       label : VariableLabel,
                       description : VariableDescription,
                       tags : List<VariableTag>,
                       variableValue : DiceRollVariableValue) : DB_VariableDiceRoll =
        Row5("variable_dice_roll",
            Col("variable_id", Prim(variableId)),
            Col("label", Prim(label)),
            Col("description", Prim(description)),
            Col("tags", Prim(VariableTagSet(tags))),
            Col("variable_value", Sum(variableValue)))

// VARIABLE: NUMBER
// ---------------------------------------------------------------------------------------------

typealias DB_VariableNumber = Row5<Prim<VariableId>, VariableId,
                                   Prim<VariableLabel>, VariableLabel,
                                   Prim<VariableDescription>, VariableDescription,
                                   Prim<VariableTagSet>, VariableTagSet,
                                   Sum<NumberVariableValue>, NumberVariableValue>

fun dbVariableNumber(variableId : VariableId,
                     label : VariableLabel,
                     description : VariableDescription,
                     tags : List<VariableTag>,
                     variableValue : NumberVariableValue) : DB_VariableNumber =
        Row5("variable_number",
            Col("variable_id", Prim(variableId)),
            Col("label", Prim(label)),
            Col("description", Prim(description)),
            Col("tags", Prim(VariableTagSet(tags))),
            Col("variable_value", Sum(variableValue)))

// VARIABLE: TEXT
// ---------------------------------------------------------------------------------------------

typealias DB_VariableText = Row5<Prim<VariableId>, VariableId,
                                 Prim<VariableLabel>, VariableLabel,
                                 Prim<VariableDescription>, VariableDescription,
                                 Prim<VariableTagSet>, VariableTagSet,
                                 Sum<TextVariableValue>, TextVariableValue>

fun dbVariableText(variableId : VariableId,
                   label : VariableLabel,
                   description : VariableDescription,
                   tags : List<VariableTag>,
                   variableValue : TextVariableValue) : DB_VariableText =
        Row5("variable_text",
            Col("variable_id", Prim(variableId)),
            Col("label", Prim(label)),
            Col("description", Prim(description)),
            Col("tags", Prim(VariableTagSet(tags))),
            Col("variable_value", Sum(variableValue)))

