
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
import com.kispoko.tome.engine.value.Dictionary;
import com.kispoko.tome.engine.value.NumberValue;
import com.kispoko.tome.engine.value.TextValue;
import com.kispoko.tome.engine.value.ValueReference;
import com.kispoko.tome.engine.value.ValueSet;
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
import com.kispoko.tome.mechanic.dice.DiceRoll;
import com.kispoko.tome.sheet.Section;
import com.kispoko.tome.sheet.Summary;
import com.kispoko.tome.sheet.group.Group;
import com.kispoko.tome.sheet.Page;
import com.kispoko.tome.sheet.Sheet;
import com.kispoko.tome.sheet.group.GroupRow;
import com.kispoko.tome.sheet.widget.BooleanWidget;
import com.kispoko.tome.sheet.widget.DiceWidget;
import com.kispoko.tome.sheet.widget.ImageWidget;
import com.kispoko.tome.sheet.widget.NumberWidget;
import com.kispoko.tome.sheet.widget.ActionWidget;
import com.kispoko.tome.sheet.widget.TableWidget;
import com.kispoko.tome.sheet.widget.TextWidget;
import com.kispoko.tome.sheet.widget.WidgetUnion;
import com.kispoko.tome.sheet.widget.table.TableRow;
import com.kispoko.tome.sheet.widget.table.cell.BooleanCell;
import com.kispoko.tome.sheet.widget.table.cell.CellUnion;
import com.kispoko.tome.sheet.widget.table.cell.NumberCell;
import com.kispoko.tome.sheet.widget.table.cell.TextCell;
import com.kispoko.tome.sheet.widget.table.column.BooleanColumn;
import com.kispoko.tome.sheet.widget.table.column.ColumnUnion;
import com.kispoko.tome.sheet.widget.table.column.NumberColumn;
import com.kispoko.tome.sheet.widget.table.column.TextColumn;
import com.kispoko.tome.sheet.widget.util.WidgetData;
import com.kispoko.tome.sheet.widget.util.WidgetFormat;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.model.ModelLib;
import com.kispoko.tome.util.database.DatabaseException;

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
            modelClasses.add(Summary.class);
            modelClasses.add(RulesEngine.class);
            modelClasses.add(Section.class);
            modelClasses.add(Page.class);
            modelClasses.add(Group.class);
            modelClasses.add(GroupRow.class);

            // ** Game Mechanic
            modelClasses.add(DiceRoll.class);

            // ** Widget
            modelClasses.add(WidgetUnion.class);
            modelClasses.add(TextWidget.class);
            modelClasses.add(NumberWidget.class);
            modelClasses.add(BooleanWidget.class);
            modelClasses.add(ImageWidget.class);
            modelClasses.add(TableWidget.class);
            modelClasses.add(ActionWidget.class);
            modelClasses.add(DiceWidget.class);

            modelClasses.add(TableRow.class);
            modelClasses.add(WidgetData.class);
            modelClasses.add(WidgetFormat.class);

            // ** Table Cell
            modelClasses.add(CellUnion.class);
            modelClasses.add(TextCell.class);
            modelClasses.add(NumberCell.class);
            modelClasses.add(BooleanCell.class);

            // ** Table Column
            modelClasses.add(ColumnUnion.class);
            modelClasses.add(BooleanColumn.class);
            modelClasses.add(NumberColumn.class);
            modelClasses.add(TextColumn.class);

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
            modelClasses.add(ValueSet.class);
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

            ModelLib.createSchema(modelClasses, database);
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
