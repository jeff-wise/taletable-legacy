
package com.kispoko.tome;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.kispoko.tome.rules.RulesEngine;
import com.kispoko.tome.rules.programming.function.FunctionIndex;
import com.kispoko.tome.rules.programming.program.ProgramIndex;
import com.kispoko.tome.rules.programming.program.ProgramInvocationParameter;
import com.kispoko.tome.rules.programming.program.ProgramValue;
import com.kispoko.tome.rules.programming.program.statement.Parameter;
import com.kispoko.tome.rules.programming.variable.BooleanVariable;
import com.kispoko.tome.rules.programming.variable.NumberVariable;
import com.kispoko.tome.rules.programming.variable.TextVariable;
import com.kispoko.tome.rules.programming.function.Function;
import com.kispoko.tome.rules.programming.function.Tuple;
import com.kispoko.tome.rules.programming.program.Program;
import com.kispoko.tome.rules.programming.program.ProgramInvocation;
import com.kispoko.tome.rules.programming.program.statement.Statement;
import com.kispoko.tome.game.Game;
import com.kispoko.tome.rules.refinement.RefinementId;
import com.kispoko.tome.rules.refinement.RefinementIndex;
import com.kispoko.tome.sheet.Group;
import com.kispoko.tome.sheet.Page;
import com.kispoko.tome.sheet.Roleplay;
import com.kispoko.tome.sheet.Sheet;
import com.kispoko.tome.sheet.widget.BooleanWidget;
import com.kispoko.tome.sheet.widget.ImageWidget;
import com.kispoko.tome.sheet.widget.NumberWidget;
import com.kispoko.tome.sheet.widget.TableWidget;
import com.kispoko.tome.sheet.widget.TextWidget;
import com.kispoko.tome.sheet.widget.table.Row;
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
import com.kispoko.tome.rules.refinement.MemberOf;
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
            modelClasses.add(Roleplay.class);
            modelClasses.add(Page.class);
            modelClasses.add(Group.class);

            // ** Widget
            modelClasses.add(TextWidget.class);
            modelClasses.add(NumberWidget.class);
            modelClasses.add(BooleanWidget.class);
            modelClasses.add(ImageWidget.class);
            modelClasses.add(TableWidget.class);

            modelClasses.add(Row.class);
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
            modelClasses.add(RefinementId.class);
            modelClasses.add(RefinementIndex.class);
            modelClasses.add(MemberOf.class);

            // ** Program
            modelClasses.add(Program.class);
            modelClasses.add(ProgramIndex.class);
            modelClasses.add(Statement.class);
            modelClasses.add(Parameter.class);
            modelClasses.add(ProgramInvocation.class);
            modelClasses.add(ProgramInvocationParameter.class);
            modelClasses.add(ProgramValue.class);

            // ** Function
            modelClasses.add(Function.class);
            modelClasses.add(FunctionIndex.class);
            modelClasses.add(Tuple.class);

            // ** Variable
            modelClasses.add(TextVariable.class);
            modelClasses.add(NumberVariable.class);
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
