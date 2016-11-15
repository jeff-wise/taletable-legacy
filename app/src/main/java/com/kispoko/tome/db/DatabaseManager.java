
package com.kispoko.tome.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.media.Image;

import com.kispoko.tome.ApplicationFailure;
import com.kispoko.tome.rules.programming.Variable;
import com.kispoko.tome.rules.programming.function.Function;
import com.kispoko.tome.rules.programming.function.Tuple;
import com.kispoko.tome.rules.programming.program.Program;
import com.kispoko.tome.rules.programming.program.ProgramInvocation;
import com.kispoko.tome.rules.programming.program.Statement;
import com.kispoko.tome.sheet.Game;
import com.kispoko.tome.sheet.Group;
import com.kispoko.tome.sheet.Page;
import com.kispoko.tome.sheet.Sheet;
import com.kispoko.tome.sheet.widget.NumberWidget;
import com.kispoko.tome.sheet.widget.TableWidget;
import com.kispoko.tome.sheet.widget.TextWidget;
import com.kispoko.tome.sheet.widget.Widget;
import com.kispoko.tome.sheet.widget.format.Format;
import com.kispoko.tome.sheet.widget.table.Cell;
import com.kispoko.tome.type.ListType;
import com.kispoko.tome.type.Type;
import com.kispoko.tome.util.Model;
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
            db.execSQL(Model.defineTableSQLString(Sheet.class));
            db.execSQL(Model.defineTableSQLString(Game.class));
            db.execSQL(Model.defineTableSQLString(Page.class));
            db.execSQL(Model.defineTableSQLString(Group.class));
            db.execSQL(Model.defineTableSQLString(Widget.class));
            db.execSQL(Model.defineTableSQLString(TextWidget.class));
            db.execSQL(Model.defineTableSQLString(NumberWidget.class));
            db.execSQL(Model.defineTableSQLString(Boolean.class));
            db.execSQL(Model.defineTableSQLString(Image.class));
            db.execSQL(Model.defineTableSQLString(TableWidget.class));
            db.execSQL(Model.defineTableSQLString(Cell.class));
            db.execSQL(Model.defineTableSQLString(Format.class));
            db.execSQL(Model.defineTableSQLString(Type.class));
            db.execSQL(Model.defineTableSQLString(ListType.class));
            db.execSQL(Model.defineTableSQLString(Program.class));
            db.execSQL(Model.defineTableSQLString(Statement.class));
            db.execSQL(Model.defineTableSQLString(ProgramInvocation.class));
            db.execSQL(Model.defineTableSQLString(Function.class));
            db.execSQL(Model.defineTableSQLString(Tuple.class));
            db.execSQL(Model.defineTableSQLString(Variable.class));
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
