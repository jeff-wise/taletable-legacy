
package com.kispoko.tome;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.kispoko.tome.rules.Rules;
import com.kispoko.tome.rules.programming.function.FunctionIndex;
import com.kispoko.tome.rules.programming.program.ProgramIndex;
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
import com.kispoko.tome.util.model.ModelLib;
import com.kispoko.tome.util.database.DatabaseException;


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


    public void onCreate(SQLiteDatabase db)
    {
        try
        {
            // ** Game
            db.execSQL(ModelLib.defineTableSQLString(Game.class));

            // ** Sheet
            db.execSQL(ModelLib.defineTableSQLString(Sheet.class));
            db.execSQL(ModelLib.defineTableSQLString(Rules.class));
            db.execSQL(ModelLib.defineTableSQLString(Roleplay.class));
            db.execSQL(ModelLib.defineTableSQLString(Page.class));
            db.execSQL(ModelLib.defineTableSQLString(Group.class));

            // ** Widget
            db.execSQL(ModelLib.defineTableSQLString(TextWidget.class));
            db.execSQL(ModelLib.defineTableSQLString(NumberWidget.class));
            db.execSQL(ModelLib.defineTableSQLString(BooleanWidget.class));
            db.execSQL(ModelLib.defineTableSQLString(ImageWidget.class));
            db.execSQL(ModelLib.defineTableSQLString(TableWidget.class));

            db.execSQL(ModelLib.defineTableSQLString(Row.class));
            db.execSQL(ModelLib.defineTableSQLString(WidgetData.class));
            db.execSQL(ModelLib.defineTableSQLString(WidgetFormat.class));

            // ** Table Cell
            db.execSQL(ModelLib.defineTableSQLString(CellUnion.class));
            db.execSQL(ModelLib.defineTableSQLString(TextCell.class));
            db.execSQL(ModelLib.defineTableSQLString(NumberCell.class));
            db.execSQL(ModelLib.defineTableSQLString(BooleanCell.class));

            // ** Table Column
            db.execSQL(ModelLib.defineTableSQLString(ColumnUnion.class));
            db.execSQL(ModelLib.defineTableSQLString(BooleanColumn.class));
            db.execSQL(ModelLib.defineTableSQLString(NumberColumn.class));
            db.execSQL(ModelLib.defineTableSQLString(TextColumn.class));

            // ** Refinements
            db.execSQL(ModelLib.defineTableSQLString(RefinementId.class));
            db.execSQL(ModelLib.defineTableSQLString(RefinementIndex.class));
            db.execSQL(ModelLib.defineTableSQLString(MemberOf.class));

            // ** Program
            db.execSQL(ModelLib.defineTableSQLString(Program.class));
            db.execSQL(ModelLib.defineTableSQLString(ProgramIndex.class));
            db.execSQL(ModelLib.defineTableSQLString(Statement.class));
            db.execSQL(ModelLib.defineTableSQLString(Parameter.class));
            db.execSQL(ModelLib.defineTableSQLString(ProgramInvocation.class));
            db.execSQL(ModelLib.defineTableSQLString(ProgramValue.class));

            // ** Function
            db.execSQL(ModelLib.defineTableSQLString(Function.class));
            db.execSQL(ModelLib.defineTableSQLString(FunctionIndex.class));
            db.execSQL(ModelLib.defineTableSQLString(Tuple.class));

            // ** Variable
            db.execSQL(ModelLib.defineTableSQLString(TextVariable.class));
            db.execSQL(ModelLib.defineTableSQLString(NumberVariable.class));
            db.execSQL(ModelLib.defineTableSQLString(BooleanVariable.class));
        }
        catch (DatabaseException e)
        {
            ApplicationFailure.database(e);
        }
    }


    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        onCreate(db);
    }


}
