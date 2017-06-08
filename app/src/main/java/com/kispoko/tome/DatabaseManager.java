
package com.kispoko.tome;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.kispoko.tome.model.game.engine.dice.DiceQuantity;
import com.kispoko.tome.model.game.engine.dice.DiceRoll;
import com.kispoko.tome.model.game.engine.dice.RollModifier;
import com.kispoko.tome.model.game.engine.mechanic.Mechanic;
import com.kispoko.tome.model.game.engine.summation.Summation;
import com.kispoko.tome.model.game.engine.variable.BooleanVariable;
import com.kispoko.tome.model.game.engine.variable.VariableReference;
import com.kispoko.tome.model.sheet.style.TextStyle;
import com.kispoko.tome.model.sheet.widget.ActionWidgetFormat;
import com.kispoko.tome.model.sheet.widget.BooleanWidgetFormat;
import com.kispoko.tome.model.sheet.widget.ButtonWidgetFormat;
import com.kispoko.tome.model.sheet.widget.ExpanderWidget;
import com.kispoko.tome.model.sheet.widget.ExpanderWidgetFormat;
import com.kispoko.tome.model.sheet.widget.ImageWidget;
import com.kispoko.tome.model.sheet.widget.ListWidget;
import com.kispoko.tome.model.sheet.widget.ListWidgetFormat;
import com.kispoko.tome.model.sheet.widget.LogEntry;
import com.kispoko.tome.model.sheet.widget.LogWidget;
import com.kispoko.tome.model.sheet.widget.LogWidgetFormat;
import com.kispoko.tome.model.sheet.widget.MechanicWidget;
import com.kispoko.tome.model.sheet.widget.NumberWidget;
import com.kispoko.tome.model.sheet.widget.NumberWidgetFormat;
import com.kispoko.tome.model.sheet.widget.OptionWidget;
import com.kispoko.tome.model.sheet.widget.OptionWidgetFormat;
import com.kispoko.tome.model.sheet.widget.QuoteWidget;
import com.kispoko.tome.model.sheet.widget.QuoteWidgetFormat;
import com.kispoko.tome.model.sheet.widget.TabWidget;
import com.kispoko.tome.model.sheet.widget.TabWidgetFormat;
import com.kispoko.tome.model.sheet.widget.TableWidget;
import com.kispoko.tome.model.sheet.widget.TableWidgetFormat;
import com.kispoko.tome.model.sheet.widget.TextWidget;
import com.kispoko.tome.model.sheet.widget.TextWidgetFormat;
import com.kispoko.tome.model.sheet.widget.table.cell.BooleanCellFormat;
import com.kispoko.tome.model.sheet.widget.table.column.TextColumnFormat;
import com.kispoko.tome.model.sheet.widget.BooleanWidget;
import com.kispoko.tome.model.sheet.widget.ButtonWidget;
import com.kispoko.tome.model.sheet.widget.ActionWidget;
import com.kispoko.tome.lib.model.Model;
import com.kispoko.tome.lib.database.orm.ORM;
import com.kispoko.tome.lib.database.DatabaseException;

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

            // ** GameSummary
            //modelClasses.add(GameSummary.class);

            // ** Campaign
            //modelClasses.add(Campaign.class);

            // ** Sheet
//            modelClasses.add(Sheet.class);
//            modelClasses.add(Settings.class);
//            modelClasses.add(Theme.class);
//            modelClasses.add(ThemeColor.class);
//            modelClasses.add(Summary.class);
//            modelClasses.add(Engine.class);
            //modelClasses.add(Section.class);
           // modelClasses.add(Page.class);
//            modelClasses.add(Group.class);
//            modelClasses.add(GroupFormat.class);
//            modelClasses.add(GroupRow.class);
//            modelClasses.add(GroupRowFormat.class);
//            modelClasses.add(Spacing.class);

            // ** GameSummary Mechanic
            modelClasses.add(DiceRoll.class);
            modelClasses.add(DiceQuantity.class);
            modelClasses.add(RollModifier.class);

            // ** Widget
            //modelClasses.add(WidgetUnion.class);

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
            //modelClasses.add(Tab.class);
            modelClasses.add(TableWidget.class);
            modelClasses.add(TableWidgetFormat.class);
            modelClasses.add(TextWidget.class);
            modelClasses.add(TextWidgetFormat.class);

//            modelClasses.add(TableRow.class);
//            modelClasses.add(TableRowFormat.class);
//            modelClasses.add(WidgetData.class);
//            modelClasses.add(WidgetFormat.class);

            modelClasses.add(TextStyle.class);

            // ** Table Cell
//            modelClasses.add(CellUnion.class);
//            modelClasses.add(TextCell.class);
//            modelClasses.add(TextCellFormat.class);
//            modelClasses.add(NumberCell.class);
//            modelClasses.add(NumberCellFormat.class);
//            modelClasses.add(BooleanCell.class);
            modelClasses.add(BooleanCellFormat.class);

            // ** Table Column
//            modelClasses.add(ColumnUnion.class);
//            modelClasses.add(BooleanColumn.class);
//            modelClasses.add(BooleanColumnFormat.class);
//            modelClasses.add(NumberColumn.class);
//            modelClasses.add(NumberColumnFormat.class);
//            modelClasses.add(TextColumn.class);
            modelClasses.add(TextColumnFormat.class);

            // ** Mechanics
            modelClasses.add(Mechanic.class);
            //modelClasses.add(MechanicIndex.class);

            // ** Program
//            modelClasses.add(Program.class);
//            modelClasses.add(ProgramIndex.class);
//            modelClasses.add(Statement.class);
//            modelClasses.add(Parameter.class);
//            modelClasses.add(Invocation.class);
//            modelClasses.add(InvocationParameterUnion.class);
//            modelClasses.add(EngineValueUnion.class);

            // ** Function
//            modelClasses.add(Function.class);
//            modelClasses.add(FunctionIndex.class);
//            modelClasses.add(Tuple.class);

            // ** Summation
            modelClasses.add(Summation.class);
//            modelClasses.add(TermUnion.class);
//            modelClasses.add(IntegerTerm.class);
//            modelClasses.add(DiceRollTerm.class);
//            modelClasses.add(ConditionalTerm.class);
//            modelClasses.add(DiceRollTermValue.class);
//            modelClasses.add(IntegerTermValue.class);
//            modelClasses.add(BooleanTermValue.class);

            // ** Value
//            modelClasses.add(Dictionary.class);
//            modelClasses.add(ValueSetUnion.class);
//            modelClasses.add(BaseValueSet.class);
//            modelClasses.add(CompoundValueSet.class);
//            modelClasses.add(ValueUnion.class);
//            modelClasses.add(TextValue.class);
//            modelClasses.add(NumberValue.class);
//            modelClasses.add(DataReference.class);

            // ** Variable
//            modelClasses.add(VariableUnion.class);
//            modelClasses.add(TextVariable.class);
//            modelClasses.add(NumberVariable.class);
//            modelClasses.add(DiceVariable.class);
            modelClasses.add(BooleanVariable.class);
            //modelClasses.add(VariableReference.class);

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
