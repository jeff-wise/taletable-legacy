
package com.kispoko.tome.rules.program;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.kispoko.tome.Global;
import com.kispoko.tome.db.SheetContract;
import com.kispoko.tome.util.SQL;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;



/**
 * Program Invocation
 */
public class ProgramInvocation
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private UUID id;
    private String programName;
    private List<ProgramInvocationParameter> parameters;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public ProgramInvocation(UUID id)
    {
        this.id = id;
    }


    public ProgramInvocation(UUID id, String programName,
                             List<ProgramInvocationParameter> parameters)
    {
        this.id = id;
        this.programName = programName;
        this.parameters = parameters;
    }


    public static ProgramInvocation fromYaml(Map<String,Object> invocationYaml)
    {
        // VALUES TO PARSE
        // --------------------------------------------------------------------------------------
        UUID id                                     = UUID.randomUUID();
        String programName                          = null;
        List<ProgramInvocationParameter> parameters = new ArrayList<>();

        // PARSE VALUES
        // --------------------------------------------------------------------------------------

        // > Top Level
        // --------------------------------------------------------------------------------------

        if (invocationYaml.containsKey("program"))
            programName = (String) invocationYaml.get("program");

        // > Parameters
        // --------------------------------------------------------------------------------------
        List<Map<String,Object>> parametersYaml =
                (List<Map<String,Object>>) invocationYaml.get("parameters");

        if (parametersYaml != null)
        {
            for (Map<String,Object> parameterYaml : parametersYaml) {
                parameters.add(ProgramInvocationParameter.fromYaml(parameterYaml));
            }
        }

        return new ProgramInvocation(id, programName, parameters);
    }


    // API
    // ------------------------------------------------------------------------------------------

    // > State
    // ------------------------------------------------------------------------------------------

    public UUID getId() {
        return this.id;
    }


    // ** Program Name
    // ------------------------------------------------------------------------------------------

    public String getProgramName() {
        return this.programName;
    }


    public void setProgramName(String programName) {
        this.programName = programName;
    }


    // ** Parameters
    // ------------------------------------------------------------------------------------------

    public List<ProgramInvocationParameter> getParameters() {
        return this.parameters;
    }


    public void setParameters(List<ProgramInvocationParameter> parameters) {
        this.parameters = parameters;
    }


    // > Database
    // ------------------------------------------------------------------------------------------

    public void load()
    {
        SQLiteDatabase database = Global.getDatabase();

        String query =
            "SELECT pi.program_name, pi.number_of_parameters, pi.parameter_value_1, " +
                   "pi.parameter_type_1, pi.parameter_value_2, pi.parameter_type_2, " +
                   "pi.parameter_value_3, pi.parameter_type_3 " +
            "FROM ProgramInvocation pi " +
            "WHERE pi.program_invocation_id =  " + SQL.quoted(this.getId().toString());

        Cursor cursor = database.rawQuery(query, null);

        String programName = null;
        Integer numberOfParameters = null;
        List<ProgramInvocationParameter> parameters = new ArrayList<>();
        try
        {
            cursor.moveToFirst();

            programName        = cursor.getString(0);
            numberOfParameters = cursor.getInt(1);

            for (int i = 0; i < numberOfParameters && i < 3; i++) {
                String parameterValueString = cursor.getString((i*2) + 2);
                String parameterTypeString  = cursor.getString((i*2) + 3);

                ProgramInvocationParameter.Type parameterType =
                        ProgramInvocationParameter.Type.fromString(parameterTypeString);

                parameters.add(ProgramInvocationParameter.fromDBString(parameterValueString,
                                                                       parameterType));
            }
        } catch (Exception e) {
            Log.d("***PROGRAM_INVOC", Log.getStackTraceString(e));
        }
        finally {
            cursor.close();
        }

        this.setProgramName(programName);
        this.setParameters(parameters);
    }


    public void save()
    {
        SQLiteDatabase database = Global.getDatabase();

        ContentValues row = new ContentValues();
        row.put("program_invocation_id", this.getId().toString());
        row.put("program_name", this.getProgramName());
        row.put("number_of_parameters", this.getParameters().size());

        List<ProgramInvocationParameter> parameters = this.getParameters();

        for (int i = 0; i < parameters.size() && i < 3; i++)
        {
            String valueColumnName = "parameter_value_" + Integer.toString(i);
            String typeColumnName = "parameter_type_" + Integer.toString(i);
            row.put(valueColumnName, parameters.get(i).valueAsDBString());
            row.put(typeColumnName, parameters.get(i).typeAsDBString());
        }

        database.insertWithOnConflict(SheetContract.ProgramInvocation.TABLE_NAME,
                                      null,
                                      row,
                                      SQLiteDatabase.CONFLICT_REPLACE);
    }

}
