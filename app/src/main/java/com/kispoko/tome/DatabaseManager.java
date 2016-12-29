
package com.kispoko.tome;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.kispoko.tome.engine.RulesEngine;
import com.kispoko.tome.engine.programming.function.FunctionIndex;
import com.kispoko.tome.engine.programming.mechanic.Mechanic;
import com.kispoko.tome.engine.programming.mechanic.MechanicIndex;
import com.kispoko.tome.engine.programming.program.invocation.Invocation;
import com.kispoko.tome.engine.programming.program.ProgramIndex;
import com.kispoko.tome.engine.programming.program.invocation.InvocationParameterUnion;
import com.kispoko.tome.engine.programming.program.ProgramValueUnion;
import com.kispoko.tome.engine.programming.program.statement.Parameter;
import com.kispoko.tome.engine.programming.summation.Summation;
import com.kispoko.tome.engine.programming.summation.term.BooleanTermValue;
import com.kispoko.tome.engine.programming.summation.term.ConditionalTerm;
import com.kispoko.tome.engine.programming.summation.term.DiceRollTerm;
import com.kispoko.tome.engine.programming.summation.term.DiceRollTermValue;
import com.kispoko.tome.engine.programming.summation.term.IntegerTermValue;
import com.kispoko.tome.engine.programming.summation.term.IntegerTerm;
import com.kispoko.tome.engine.programming.summation.term.TermUnion;
import com.kispoko.tome.engine.programming.variable.BooleanVariable;
import com.kispoko.tome.engine.programming.variable.DiceVariable;
import com.kispoko.tome.engine.programming.variable.NumberVariable;
import com.kispoko.tome.engine.programming.variable.TextVariable;
import com.kispoko.tome.engine.programming.function.Function;
import com.kispoko.tome.engine.programming.function.Tuple;
import com.kispoko.tome.engine.programming.program.Program;
import com.kispoko.tome.engine.programming.program.statement.Statement;
import com.kispoko.tome.engine.programming.variable.VariableUnion;
import com.kispoko.tome.engine.refinement.RefinementUnion;
import com.kispoko.tome.game.Game;
import com.kispoko.tome.engine.refinement.RefinementId;
import com.kispoko.tome.engine.refinement.RefinementIndex;
import com.kispoko.tome.mechanic.dice.DiceRoll;
import com.kispoko.tome.sheet.Section;
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
import com.kispoko.tome.engine.refinement.MemberOf;
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

            // ** Sheet
            modelClasses.add(Sheet.class);
            modelClasses.add(RulesEngine.class);
            modelClasses.add(Section.class);
            modelClasses.add(Page.class);
            modelClasses.add(Group.class);
            modelClasses.add(GroupRow.class);

            // ** Game Mechanic
            modelClasses.add(DiceRoll.class);

            // ** Widget
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

            // ** Refinements
            modelClasses.add(RefinementUnion.class);
            modelClasses.add(RefinementId.class);
            modelClasses.add(RefinementIndex.class);
            modelClasses.add(MemberOf.class);

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

            // ** Variable
            modelClasses.add(VariableUnion.class);
            modelClasses.add(TextVariable.class);
            modelClasses.add(NumberVariable.class);
            modelClasses.add(DiceVariable.class);
            modelClasses.add(BooleanVariable.class);

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
