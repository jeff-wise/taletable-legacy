
package com.kispoko.tome.sheet.component;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.kispoko.tome.Global;
import com.kispoko.tome.db.SheetContract;
import com.kispoko.tome.rules.program.ProgramInvocation;
import com.kispoko.tome.sheet.Component;
import com.kispoko.tome.util.SQL;
import com.kispoko.tome.util.TrackerId;

import java.util.Map;
import java.util.UUID;

import static com.kispoko.tome.sheet.component.ComponentValue.Type.PROGRAM;



/**
 * Component Value
 */
public class ComponentValue
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private UUID id;
    private Object value;
    private Type _type;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public ComponentValue(UUID id)
    {
        this.id = id;
    }


    public ComponentValue(UUID id, Object value, Type _type)
    {
        this.id = id;
        this.value = value;
        this._type = _type;
    }


    public static ComponentValue fromYaml(Map<String,Object> valueYaml)
    {
        // VALUES TO PARSE
        // --------------------------------------------------------------------------------------
        UUID id = UUID.randomUUID();
        Object value = null;
        Type   _type = null;

        // PARSE VALUES
        // --------------------------------------------------------------------------------------

        if (valueYaml.containsKey("value"))
            value = valueYaml.get("value");

        if (valueYaml.containsKey("type"))
            _type = Type.fromString((String) valueYaml.get("type"));

        return new ComponentValue(id, value, _type);
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


    public static ComponentValue fromDBString(UUID id, String valueString, Type _type)
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

        return new ComponentValue(id, value, _type);
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
        final ComponentValue thisValue = this;

        new AsyncTask<Void,Void,Boolean>()
        {

            @Override
            protected Boolean doInBackground(Void... args)
            {
                SQLiteDatabase database = Global.getDatabase();

                // Query the Component Value
                String query =
                    "SELECT cv.value, cv.value_type " +
                    "FROM ComponentValue cv " +
                    "WHERE cv.component_value_id =  " + SQL.quoted(componentId.toString());

                Cursor cursor = database.rawQuery(query, null);

                Object value = null;
                ComponentValue.Type _type = null;
                try
                {
                    cursor.moveToFirst();
                    _type = Type.fromString(cursor.getString(1));
                    value = ComponentValue.fromDBString(thisValue.getId(),
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
     * Save the component value by updating the Component table. If the value is a program, it must
     * update the ProgramInvocation table as well.
     */
    public void save(final String key, final UUID callerTrackerId)
    {
        final ComponentValue thisValue = this;

        new AsyncTask<Void,Void,Boolean>()
        {

            @Override
            protected Boolean doInBackground(Void... args)
            {
                SQLiteDatabase database = Global.getDatabase();

                // > Upadte value columns in Component table
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
                if (thisValue.getType() == Type.PROGRAM) {
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
