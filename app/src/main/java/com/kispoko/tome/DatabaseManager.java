
package com.kispoko.tome;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.media.Image;

import com.kispoko.tome.rules.programming.variable.Variable;
import com.kispoko.tome.rules.programming.function.Function;
import com.kispoko.tome.rules.programming.function.Tuple;
import com.kispoko.tome.rules.programming.program.Program;
import com.kispoko.tome.rules.programming.program.ProgramInvocation;
import com.kispoko.tome.rules.programming.program.statement.Statement;
import com.kispoko.tome.sheet.Game;
import com.kispoko.tome.sheet.Group;
import com.kispoko.tome.sheet.Page;
import com.kispoko.tome.sheet.Sheet;
import com.kispoko.tome.sheet.widget.NumberWidget;
import com.kispoko.tome.sheet.widget.TableWidget;
import com.kispoko.tome.sheet.widget.TextWidget;
import com.kispoko.tome.sheet.widget.Widget;
import com.kispoko.tome.sheet.widget.util.WidgetFormat;
import com.kispoko.tome.sheet.widget.table.Cell;
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
            db.execSQL(Modeler.defineTableSQLString(Boolean.class));
            db.execSQL(Modeler.defineTableSQLString(Image.class));
            db.execSQL(Modeler.defineTableSQLString(TableWidget.class));
            db.execSQL(Modeler.defineTableSQLString(Cell.class));
            db.execSQL(Modeler.defineTableSQLString(WidgetFormat.class));
            db.execSQL(Modeler.defineTableSQLString(Type.class));
            db.execSQL(Modeler.defineTableSQLString(MemberOf.class));
            db.execSQL(Modeler.defineTableSQLString(Program.class));
            db.execSQL(Modeler.defineTableSQLString(Statement.class));
            db.execSQL(Modeler.defineTableSQLString(ProgramInvocation.class));
            db.execSQL(Modeler.defineTableSQLString(Function.class));
            db.execSQL(Modeler.defineTableSQLString(Tuple.class));
            db.execSQL(Modeler.defineTableSQLString(Variable.class));
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
