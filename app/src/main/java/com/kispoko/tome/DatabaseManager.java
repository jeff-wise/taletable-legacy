
package com.kispoko.tome;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.kispoko.tome.campaign.Campaign;
import com.kispoko.tome.engine.RulesEngine;
import com.kispoko.tome.engine.function.FunctionIndex;
import com.kispoko.tome.engine.mechanic.Mechanic;
import com.kispoko.tome.engine.mechanic.MechanicIndex;
import com.kispoko.tome.engine.program.invocation.Invocation;
import com.kispoko.tome.engine.program.ProgramIndex;
import com.kispoko.tome.engine.program.invocation.InvocationParameterUnion;
import com.kispoko.tome.engine.program.ProgramValueUnion;
import com.kispoko.tome.engine.program.statement.Parameter;
import com.kispoko.tome.engine.summation.Summation;
import com.kispoko.tome.engine.summation.term.BooleanTermValue;
import com.kispoko.tome.engine.summation.term.ConditionalTerm;
import com.kispoko.tome.engine.summation.term.DiceRollTerm;
import com.kispoko.tome.engine.summation.term.DiceRollTermValue;
import com.kispoko.tome.engine.summation.term.IntegerTermValue;
import com.kispoko.tome.engine.summation.term.IntegerTerm;
import com.kispoko.tome.engine.summation.term.TermUnion;
import com.kispoko.tome.engine.value.CompoundValueSet;
import com.kispoko.tome.engine.value.Dictionary;
import com.kispoko.tome.engine.value.NumberValue;
import com.kispoko.tome.engine.value.TextValue;
import com.kispoko.tome.engine.value.ValueReference;
import com.kispoko.tome.engine.value.BaseValueSet;
import com.kispoko.tome.engine.value.ValueSetUnion;
import com.kispoko.tome.engine.value.ValueUnion;
import com.kispoko.tome.engine.variable.BooleanVariable;
import com.kispoko.tome.engine.variable.DiceVariable;
import com.kispoko.tome.engine.variable.NumberVariable;
import com.kispoko.tome.engine.variable.TextVariable;
import com.kispoko.tome.engine.function.Function;
import com.kispoko.tome.engine.function.Tuple;
import com.kispoko.tome.engine.program.Program;
import com.kispoko.tome.engine.program.statement.Statement;
import com.kispoko.tome.engine.variable.VariableReference;
import com.kispoko.tome.engine.variable.VariableUnion;
import com.kispoko.tome.game.Game;
import com.kispoko.tome.mechanic.dice.DiceQuantity;
import com.kispoko.tome.mechanic.dice.DiceRoll;
import com.kispoko.tome.mechanic.dice.RollModifier;
import com.kispoko.tome.sheet.Section;
import com.kispoko.tome.sheet.Settings;
import com.kispoko.tome.sheet.Spacing;
import com.kispoko.tome.sheet.Summary;
import com.kispoko.tome.sheet.group.Group;
import com.kispoko.tome.sheet.Page;
import com.kispoko.tome.sheet.Sheet;
import com.kispoko.tome.sheet.group.GroupFormat;
import com.kispoko.tome.sheet.group.GroupRow;
import com.kispoko.tome.sheet.group.GroupRowFormat;
import com.kispoko.tome.sheet.widget.BooleanWidget;
import com.kispoko.tome.sheet.widget.ButtonWidget;
import com.kispoko.tome.sheet.widget.ExpanderWidget;
import com.kispoko.tome.sheet.widget.ImageWidget;
import com.kispoko.tome.sheet.widget.ListWidget;
import com.kispoko.tome.sheet.widget.LogWidget;
import com.kispoko.tome.sheet.widget.MechanicWidget;
import com.kispoko.tome.sheet.widget.NumberWidget;
import com.kispoko.tome.sheet.widget.ActionWidget;
import com.kispoko.tome.sheet.widget.OptionWidget;
import com.kispoko.tome.sheet.widget.QuoteWidget;
import com.kispoko.tome.sheet.widget.TabWidget;
import com.kispoko.tome.sheet.widget.TableWidget;
import com.kispoko.tome.sheet.widget.TextWidget;
import com.kispoko.tome.sheet.widget.WidgetUnion;
import com.kispoko.tome.sheet.widget.action.ActionWidgetFormat;
import com.kispoko.tome.sheet.widget.bool.BooleanWidgetFormat;
import com.kispoko.tome.sheet.widget.button.ButtonWidgetFormat;
import com.kispoko.tome.sheet.widget.expander.ExpanderWidgetFormat;
import com.kispoko.tome.sheet.widget.list.ListWidgetFormat;
import com.kispoko.tome.sheet.widget.log.LogEntry;
import com.kispoko.tome.sheet.widget.log.LogWidgetFormat;
import com.kispoko.tome.sheet.widget.number.NumberWidgetFormat;
import com.kispoko.tome.sheet.widget.option.OptionWidgetFormat;
import com.kispoko.tome.sheet.widget.quote.QuoteWidgetFormat;
import com.kispoko.tome.sheet.widget.tab.Tab;
import com.kispoko.tome.sheet.widget.tab.TabWidgetFormat;
import com.kispoko.tome.sheet.widget.table.TableRow;
import com.kispoko.tome.sheet.widget.table.TableRowFormat;
import com.kispoko.tome.sheet.widget.table.TableWidgetFormat;
import com.kispoko.tome.sheet.widget.table.cell.BooleanCell;
import com.kispoko.tome.sheet.widget.table.cell.BooleanCellFormat;
import com.kispoko.tome.sheet.widget.table.cell.CellUnion;
import com.kispoko.tome.sheet.widget.table.cell.NumberCell;
import com.kispoko.tome.sheet.widget.table.cell.NumberCellFormat;
import com.kispoko.tome.sheet.widget.table.cell.TextCell;
import com.kispoko.tome.sheet.widget.table.cell.TextCellFormat;
import com.kispoko.tome.sheet.widget.table.column.BooleanColumn;
import com.kispoko.tome.sheet.widget.table.column.BooleanColumnFormat;
import com.kispoko.tome.sheet.widget.table.column.ColumnUnion;
import com.kispoko.tome.sheet.widget.table.column.NumberColumn;
import com.kispoko.tome.sheet.widget.table.column.NumberColumnFormat;
import com.kispoko.tome.sheet.widget.table.column.TextColumn;
import com.kispoko.tome.sheet.widget.table.column.TextColumnFormat;
import com.kispoko.tome.sheet.widget.text.TextWidgetFormat;
import com.kispoko.tome.sheet.widget.util.TextStyle;
import com.kispoko.tome.sheet.widget.util.WidgetData;
import com.kispoko.tome.sheet.widget.util.WidgetFormat;
import com.kispoko.tome.lib.model.Model;
import com.kispoko.tome.lib.database.orm.ORM;
import com.kispoko.tome.lib.database.DatabaseException;
import com.kispoko.tome.theme.Theme;
import com.kispoko.tome.theme.ThemeColor;

import java.util.ArrayList;
import java.util.List;



/**
 * Sheet Database Manager
 */
public class DatabaseManager extends SQLiteOpenHelper
{


    public DatabaseManager(Context context)
    {
        // TODO figure out migrations
        super(context, "main", null, 1);
    }


    public void onCreate(SQLiteDatabase database)
    {
        try
        {
            List<Class<? extends Model>> modelClasses = new ArrayList<>();

            // ** Game
            modelClasses.add(Game.class);

            // ** Campaign
            modelClasses.add(Campaign.class);

            // ** Sheet
            modelClasses.add(Sheet.class);
            modelClasses.add(Settings.class);
            modelClasses.add(Theme.class);
            modelClasses.add(ThemeColor.class);
            modelClasses.add(Summary.class);
            modelClasses.add(RulesEngine.class);
            modelClasses.add(Section.class);
            modelClasses.add(Page.class);
            modelClasses.add(Group.class);
            modelClasses.add(GroupFormat.class);
            modelClasses.add(GroupRow.class);
            modelClasses.add(GroupRowFormat.class);
            modelClasses.add(Spacing.class);

            // ** Game Mechanic
            modelClasses.add(DiceRoll.class);
            modelClasses.add(DiceQuantity.class);
            modelClasses.add(RollModifier.class);

            // ** Widget
            modelClasses.add(WidgetUnion.class);

            modelClasses.add(ActionWidget.class);
            modelClasses.add(ActionWidgetFormat.class);
            modelClasses.add(BooleanWidget.class);
            modelClasses.add(BooleanWidgetFormat.class);
            modelClasses.add(ButtonWidget.class);
            modelClasses.add(ButtonWidgetFormat.class);
            modelClasses.add(ExpanderWidget.class);
            modelClasses.add(ExpanderWidgetFormat.class);
            modelClasses.add(ImageWidget.class);
            modelClasses.add(ListWidget.class);
            modelClasses.add(ListWidgetFormat.class);
            modelClasses.add(LogWidget.class);
            modelClasses.add(LogWidgetFormat.class);
            modelClasses.add(LogEntry.class);
            modelClasses.add(MechanicWidget.class);
            modelClasses.add(NumberWidget.class);
            modelClasses.add(NumberWidgetFormat.class);
            modelClasses.add(OptionWidget.class);
            modelClasses.add(OptionWidgetFormat.class);
            modelClasses.add(QuoteWidget.class);
            modelClasses.add(QuoteWidgetFormat.class);
            modelClasses.add(TabWidget.class);
            modelClasses.add(TabWidgetFormat.class);
            modelClasses.add(Tab.class);
            modelClasses.add(TableWidget.class);
            modelClasses.add(TableWidgetFormat.class);
            modelClasses.add(TextWidget.class);
            modelClasses.add(TextWidgetFormat.class);

            modelClasses.add(TableRow.class);
            modelClasses.add(TableRowFormat.class);
            modelClasses.add(WidgetData.class);
            modelClasses.add(WidgetFormat.class);

            modelClasses.add(TextStyle.class);

            // ** Table Cell
            modelClasses.add(CellUnion.class);
            modelClasses.add(TextCell.class);
            modelClasses.add(TextCellFormat.class);
            modelClasses.add(NumberCell.class);
            modelClasses.add(NumberCellFormat.class);
            modelClasses.add(BooleanCell.class);
            modelClasses.add(BooleanCellFormat.class);

            // ** Table Column
            modelClasses.add(ColumnUnion.class);
            modelClasses.add(BooleanColumn.class);
            modelClasses.add(BooleanColumnFormat.class);
            modelClasses.add(NumberColumn.class);
            modelClasses.add(NumberColumnFormat.class);
            modelClasses.add(TextColumn.class);
            modelClasses.add(TextColumnFormat.class);

            // ** Mechanics
            modelClasses.add(Mechanic.class);
            modelClasses.add(MechanicIndex.class);

            // ** Program
            modelClasses.add(Program.class);
            modelClasses.add(ProgramIndex.class);
            modelClasses.add(Statement.class);
            modelClasses.add(Parameter.class);
            modelClasses.add(Invocation.class);
            modelClasses.add(InvocationParameterUnion.class);
            modelClasses.add(ProgramValueUnion.class);

            // ** Function
            modelClasses.add(Function.class);
            modelClasses.add(FunctionIndex.class);
            modelClasses.add(Tuple.class);

            // ** Summation
            modelClasses.add(Summation.class);
            modelClasses.add(TermUnion.class);
            modelClasses.add(IntegerTerm.class);
            modelClasses.add(DiceRollTerm.class);
            modelClasses.add(ConditionalTerm.class);
            modelClasses.add(DiceRollTermValue.class);
            modelClasses.add(IntegerTermValue.class);
            modelClasses.add(BooleanTermValue.class);

            // ** Value
            modelClasses.add(Dictionary.class);
            modelClasses.add(ValueSetUnion.class);
            modelClasses.add(BaseValueSet.class);
            modelClasses.add(CompoundValueSet.class);
            modelClasses.add(ValueUnion.class);
            modelClasses.add(TextValue.class);
            modelClasses.add(NumberValue.class);
            modelClasses.add(ValueReference.class);

            // ** Variable
            modelClasses.add(VariableUnion.class);
            modelClasses.add(TextVariable.class);
            modelClasses.add(NumberVariable.class);
            modelClasses.add(DiceVariable.class);
            modelClasses.add(BooleanVariable.class);
            modelClasses.add(VariableReference.class);

            ORM.createSchema(modelClasses, database);
        }
        catch (DatabaseException exception)
        {
            ApplicationFailure.database(exception);
        }
    }


    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        onCreate(db);
    }


}
