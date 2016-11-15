
package com.kispoko.tome.rules.programming;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.kispoko.tome.Global;
import com.kispoko.tome.rules.programming.program.ProgramInvocation;
import com.kispoko.tome.util.database.SQL;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.util.UUID;

import static com.kispoko.tome.rules.programming.Variable.Type.PROGRAM;



/**
 * Variable
 *
 * A variable is a piece of programmable state associated with a component. It could contain a
 * literal value, or a be a value that is generated dynamically from a script.
 */
public class Variable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private UUID id;
    private Object value;
    private Type _type;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Variable(UUID id)
    {
        this.id = id;
    }


    public Variable(UUID id, Object value, Type _type)
    {
        this.id = id;
        this.value = value;
        this._type = _type;
    }


    public static Variable fromYaml(Yaml yaml)
                  throws YamlException
    {
        UUID   id    = UUID.randomUUID();
        Object value = yaml.atKey("value").getObject();
        Type   _type = Type.fromString(yaml.atKey("type").getString());

        return new Variable(id, value, _type);
    }


    // API
    // ------------------------------------------------------------------------------------------

    // > State
    // ------------------------------------------------------------------------------------------

    // ** Id
    // ------------------------------------------------------------------------------------------

    public UUID getId() {
        return this.id;
    }


    // ** Type
    // ------------------------------------------------------------------------------------------

    public Type getType() {
        return this._type;
    }


    public void setType(Type _type) {
        this._type = _type;
    }


    // ** Values
    // ------------------------------------------------------------------------------------------

    public ProgramInvocation getProgramInvocation() {
        return (ProgramInvocation) this.value;
    }


    public String getString() {
        return (String) this.value;
    }


    public Integer getInteger() {
        return (Integer) this.value;
    }


    public Boolean getBoolean() {
        return (Boolean) this.value;
    }


    public void setValue(Object value) {
        this.value = value;
    }


    // > Serialization
    // ------------------------------------------------------------------------------------------

    public String asString()
    {
        switch (this._type)
        {
            case LITERAL_INTEGER:
                return Integer.toString((Integer) this.value);
            case LITERAL_STRING:
                return (String) this.value;
            case LITERAL_BOOLEAN:
                return Boolean.toString((Boolean) this.value);
            default:
                return "";
        }
    }

    public String valueAsDBString()
    {
        switch (this._type)
        {
            case LITERAL_INTEGER:
                return Integer.toString((Integer) this.value);
            case LITERAL_STRING:
                return (String) this.value;
            case LITERAL_BOOLEAN:
                return Boolean.toString((Boolean) this.value);
            case PROGRAM:
                return ((ProgramInvocation) this.value).getId().toString();
        }

        // TODO should not happen
        return null;
    }


    public static Variable fromDBString(UUID id, String valueString, Type _type)
    {
        Object value = null;

        switch (_type)
        {
            case LITERAL_INTEGER:
                value = Integer.parseInt(valueString);
                break;
            case LITERAL_STRING:
                value = valueString;
                break;
            case LITERAL_BOOLEAN:
                value = Boolean.parseBoolean(valueString);
                break;
            case PROGRAM:
                value = valueString;
                break;
        }

        return new Variable(id, value, _type);
    }


    public String typeAsDBString()
    {
        return this._type.toString().toLowerCase();
    }




    // > Database
    // ------------------------------------------------------------------------------------------

    /**
     * Load the component value. Literal values are loaded with the component, but program values
     * must be loaded separately.
     */
    public void load(final UUID componentId, final String key, final UUID trackerId)
    {
        final Variable thisValue = this;

        new AsyncTask<Void,Void,Boolean>()
        {

            @Override
            protected Boolean doInBackground(Void... args)
            {
                SQLiteDatabase database = Global.getDatabase();

                // ModelQuery the WidgetData Value
                String query =
                    "SELECT cv.value, cv.value_type " +
                    "FROM Variable cv " +
                    "WHERE cv.component_value_id =  " + SQL.quoted(componentId.toString());

                Cursor cursor = database.rawQuery(query, null);

                Object value = null;
                Variable.Type _type = null;
                try
                {
                    cursor.moveToFirst();
                    _type = Type.fromString(cursor.getString(1));
                    value = Variable.fromDBString(thisValue.getId(),
                                                        cursor.getString(0), _type);

                    if (_type == PROGRAM) {
                        value = new ProgramInvocation(UUID.fromString((String) value));
                        ((ProgramInvocation) value).load();
                    }

                } catch (Exception e) {
                    Log.d("***COMPONENT_VALUE", Log.getStackTraceString(e));
                }
                finally {
                    cursor.close();
                }

                thisValue.setValue(value);
                thisValue.setType(_type);

                return true;
            }

            @Override
            protected void onPostExecute(Boolean result)
            {
                Global.getTracker(trackerId).setKey(key);
            }

        }.execute();

    }


    /**
     * Save the component value by updating the WidgetData table. If the value is a program, it must
     * update the ProgramInvocation table as well.
     */
    public void save(final String key, final UUID callerTrackerId)
    {
        final Variable thisValue = this;

        new AsyncTask<Void,Void,Boolean>()
        {

            @Override
            protected Boolean doInBackground(Void... args)
            {
                SQLiteDatabase database = Global.getDatabase();

                // > Upadte value columns in WidgetData table
                // ------------------------------------------------------------------------------
                ContentValues row = new ContentValues();

                row.put("component_value_id", thisValue.getId().toString());
                row.put("value", thisValue.valueAsDBString());
                row.put("value_type", thisValue.typeAsDBString());

                database.insertWithOnConflict(SheetContract.ComponentValue.TABLE_NAME,
                                              null,
                                              row,
                                              SQLiteDatabase.CONFLICT_REPLACE);

                // > Save ProgramInvocation row, if necessary
                // ------------------------------------------------------------------------------
                if (thisValue.getType() == PROGRAM) {
                    thisValue.getProgramInvocation().save();
                }

                return true;
            }

            @Override
            protected void onPostExecute(Boolean result)
            {
                Global.getTracker(callerTrackerId).setKey(key);
            }

        }.execute();

    }


    // NESTED DEFINTIONS
    // ------------------------------------------------------------------------------------------

    public enum Type
    {
        LITERAL_INTEGER,
        LITERAL_STRING,
        LITERAL_BOOLEAN,
        PROGRAM;


        public static Type fromString(String _type)
        {
            if (_type != null)
                return Type.valueOf(_type.toUpperCase());
            return null;
        }


        public static String asString(Type _type)
        {
            if (_type != null)
                return _type.toString().toLowerCase();
            return null;
        }

    }
}
