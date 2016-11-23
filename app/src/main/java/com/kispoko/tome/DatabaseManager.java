
package com.kispoko.tome;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
import com.kispoko.tome.sheet.Group;
import com.kispoko.tome.sheet.Page;
import com.kispoko.tome.sheet.Sheet;
import com.kispoko.tome.sheet.widget.BooleanWidget;
import com.kispoko.tome.sheet.widget.ImageWidget;
import com.kispoko.tome.sheet.widget.NumberWidget;
import com.kispoko.tome.sheet.widget.TableWidget;
import com.kispoko.tome.sheet.widget.TextWidget;
import com.kispoko.tome.sheet.widget.Widget;
import com.kispoko.tome.sheet.widget.table.cell.BooleanCell;
import com.kispoko.tome.sheet.widget.table.cell.NumberCell;
import com.kispoko.tome.sheet.widget.table.cell.TextCell;
import com.kispoko.tome.sheet.widget.util.WidgetFormat;
import com.kispoko.tome.rules.refinement.MemberOf;
import com.kispoko.tome.util.model.Modeler;
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
            db.execSQL(Modeler.defineTableSQLString(Sheet.class));
            db.execSQL(Modeler.defineTableSQLString(Game.class));
            db.execSQL(Modeler.defineTableSQLString(Page.class));
            db.execSQL(Modeler.defineTableSQLString(Group.class));
            db.execSQL(Modeler.defineTableSQLString(Widget.class));
            db.execSQL(Modeler.defineTableSQLString(TextWidget.class));
            db.execSQL(Modeler.defineTableSQLString(NumberWidget.class));
            db.execSQL(Modeler.defineTableSQLString(BooleanWidget.class));
            db.execSQL(Modeler.defineTableSQLString(ImageWidget.class));
            db.execSQL(Modeler.defineTableSQLString(TableWidget.class));
            db.execSQL(Modeler.defineTableSQLString(TextCell.class));
            db.execSQL(Modeler.defineTableSQLString(NumberCell.class));
            db.execSQL(Modeler.defineTableSQLString(BooleanCell.class));
            db.execSQL(Modeler.defineTableSQLString(WidgetFormat.class));
            db.execSQL(Modeler.defineTableSQLString(RefinementId.class));
            db.execSQL(Modeler.defineTableSQLString(MemberOf.class));
            db.execSQL(Modeler.defineTableSQLString(Program.class));
            db.execSQL(Modeler.defineTableSQLString(ProgramIndex.class));
            db.execSQL(Modeler.defineTableSQLString(Statement.class));
            db.execSQL(Modeler.defineTableSQLString(Parameter.class));
            db.execSQL(Modeler.defineTableSQLString(ProgramInvocation.class));
            db.execSQL(Modeler.defineTableSQLString(ProgramValue.class));
            db.execSQL(Modeler.defineTableSQLString(Function.class));
            db.execSQL(Modeler.defineTableSQLString(FunctionIndex.class));
            db.execSQL(Modeler.defineTableSQLString(Tuple.class));
            db.execSQL(Modeler.defineTableSQLString(TextVariable.class));
            db.execSQL(Modeler.defineTableSQLString(NumberVariable.class));
            db.execSQL(Modeler.defineTableSQLString(BooleanVariable.class));
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
