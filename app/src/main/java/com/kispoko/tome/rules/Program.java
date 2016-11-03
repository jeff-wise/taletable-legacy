
package com.kispoko.tome.rules;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.kispoko.tome.Global;
import com.kispoko.tome.db.SheetContract;
import com.kispoko.tome.util.SQL;
import com.kispoko.tome.util.TrackerId;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;



/**
 * Program
 */
public class Program
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private UUID id;
    private String name;
    private UUID sheetId;
    private List<FunctionValueType> parameterTypes;
    private FunctionValueType resultType;
    private String resultVariableName;
    private List<Statement> statements;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    /**
     * Create a program with empty data. Used for building the application data structures, and then
     * later filling in the missing data asynchronously, perhaps from a database.
     * @param id
     * @param name
     */
    public Program(UUID id, String name, UUID sheetId)
    {
        this.id = id;
        this.name = name;
        this.sheetId = sheetId;
    }


    public Program(UUID id, String name, UUID sheetId, List<FunctionValueType> parameterTypes,
                   FunctionValueType resultType, String resultVariableName,
                   List<Statement> statements)
    {
        this.id = id;
        this.name = name;
        this.sheetId = sheetId;
        this.parameterTypes = parameterTypes;
        this.resultType = resultType;
        this.resultVariableName = resultVariableName;
        this.statements = statements;
    }


    @SuppressWarnings("unchecked")
    public static Program fromYaml(UUID sheetId, Map<String,Object> programYaml)
    {
        // VALUES TO PARSE
        // --------------------------------------------------------------------------------------
        UUID id = UUID.randomUUID();
        String name = null;
        List<FunctionValueType> parameterTypes = new ArrayList<>();
        FunctionValueType resultType = null;
        String resultVariable = null;
        List<Statement> statements = new ArrayList<>();

        // PARSE VALUES
        // --------------------------------------------------------------------------------------

        // > Top Level
        // --------------------------------------------------------------------------------------

        // ** Name
        if (programYaml.containsKey("name"))
            name = (String) programYaml.get("name");

        // >> Definition
        // --------------------------------------------------------------------------------------
        Map<String,Object> definitionYaml = (Map<String,Object>) programYaml.get("definition");

        if (definitionYaml != null)
        {
            // ** Parameter Types
            if (definitionYaml.containsKey("parameter_types")) {
                List<String> parameterTypeStrings =
                        (List<String>) definitionYaml.get("parameter_types");
                for (String parameterTypeString : parameterTypeStrings) {
                    parameterTypes.add(FunctionValueType.fromString(parameterTypeString));
                }
            }

            // ** Result Type
            if (definitionYaml.containsKey("result_type")) {
                resultType = FunctionValueType.fromString(
                                (String) definitionYaml.get("result_type"));
            }

            // ** Result Variable
            if (definitionYaml.containsKey("result_variable"))
                resultVariable = (String) definitionYaml.get("result_variable");

            // ** Statements
            if (definitionYaml.containsKey("statements")) {
                List<Map<String,Object>> statementsYaml =
                        (List<Map<String,Object>>) definitionYaml.get("statements");
                for (Map<String,Object> statementYaml : statementsYaml) {
                    statements.add(Statement.fromYaml(statementYaml));
                }
            }
        }

        return new Program(id, name, sheetId, parameterTypes, resultType,
                           resultVariable, statements);
    }


    // API
    // ------------------------------------------------------------------------------------------

    // > State
    // ------------------------------------------------------------------------------------------

    // ** Identification
    // ------------------------------------------------------------------------------------------

    /**
     * Get the unique ID of the program, used mainly for persistence.
     * @return The sheet ID for the program.
     */
    public UUID getId() {
        return this.id;
    }


    /**
     * Get the unique name of the program.
     * @return The program's name.
     */
    public String getName() {
        return this.name;
    }


    // ** Result Variable Name
    // ------------------------------------------------------------------------------------------

    /**
     * Get the name of the variable that the program returns as the result value.
     * @return The statement result variable name.
     */
    public String getResultVariableName() {
        return this.resultVariableName;
    }


    /**
     * Set the result variable name.
     * @param resultVariableName The name of the program variable to be the result of the program.
     */
    public void setResultVariableName(String resultVariableName) {
        this.resultVariableName = resultVariableName;
    }


    // ** Parameter Types
    // ------------------------------------------------------------------------------------------

    /**
     * Get the parameter types of the program.
     * @return The program's parameter type list.
     */
    public List<FunctionValueType> getParameterTypes() {
        return this.parameterTypes;
    }


    /**
     * Set the parameter types of the program.
     * @param parameterTypes The types of the parameters that are passed to the program.
     */
    public void setParameterTypes(List<FunctionValueType> parameterTypes) {
        this.parameterTypes = parameterTypes;
    }


    // ** Result Type
    // ------------------------------------------------------------------------------------------

    /**
     * Get the result type of the program.
     * @return The program's result type.
     */
    public FunctionValueType getResultType() {
        return this.resultType;
    }


    /**
     * Set the result type of the program.
     * @param resultType The type of the value that the program computes.
     */
    public void setResultType(FunctionValueType resultType) {
        this.resultType = resultType;
    }


    // ** Statements
    // ------------------------------------------------------------------------------------------

    /**
     * Get all of the statements in the program.
     * @return The program's statements.
     */
    public List<Statement> getStatements() {
        return this.statements;
    }


    /**
     * Set the program's statements.
     * @param statements The statements that the program will run.
     */
    public void setStatements(List<Statement> statements) {
        this.statements = statements;
    }


    // > Database
    // ------------------------------------------------------------------------------------------

    /**
     * Load the program data from the databsae
     * @param programIndexTrackerId The ID of the caller's asynchronous tracker.
     */
    public void load(final TrackerId programIndexTrackerId)
    {

        final Program thisProgram = this;

        new AsyncTask<Void,Void,Boolean>()
        {

            @Override
            protected Boolean doInBackground(Void... args)
            {
                SQLiteDatabase database = Global.getDatabase();

                // Query Program Data
                String programQuery =
                    "SELECT p.result_type, p.number_of_parameters, p.variable_name, " +
                           "p.parameter_type_1, p.parameter_type2, p.parameter_type_3 " +
                    "FROM Program p " +
                    "WHERE p.program_id =  " + SQL.quoted(thisProgram.getId().toString());

                Cursor programCursor = database.rawQuery(programQuery, null);

                FunctionValueType resultType = null;
                Integer numberOfProgramParameters = null;
                String resultVariableName = null;
                List<FunctionValueType> parameterTypes = new ArrayList<>();

                try {
                    programCursor.moveToFirst();
                    resultType         = FunctionValueType.fromString(programCursor.getString(0));
                    numberOfProgramParameters = programCursor.getInt(1);
                    resultVariableName = programCursor.getString(2);

                    for (int i = 0; i < numberOfProgramParameters; i++) {
                        parameterTypes.add(
                                FunctionValueType.fromString(programCursor.getString(i + 3)));
                    }
                }
                finally {
                    programCursor.close();
                }

                // Query Statements
                String statementsQuery =
                    "SELECT s.variable_name, s.function_name, s.number_of_parameters, " +
                           "s.parameter_value_1, s.parameter_type_1, s.parameter_value_2, " +
                           "s.parameter_type_2, s.parameter_value_3, s.parameter_type_3 " +
                    "FROM statement s " +
                    "WHERE s.program_id = " + SQL.quoted(thisProgram.getId().toString());

                Cursor statementsCursor = database.rawQuery(statementsQuery, null);

                List<Statement> statements = new ArrayList<>();
                try
                {
                    while (statementsCursor.moveToNext())
                    {
                        String variableName        = statementsCursor.getString(0);
                        String functionName        = statementsCursor.getString(1);
                        Integer numberOfParameters = statementsCursor.getInt(2);

                        List<Statement.Parameter> parameters = new ArrayList<>();
                        for (int i = 0; i < numberOfParameters && i < 3; i++) {
                            String parameterValueString = statementsCursor.getString((i*2) + 3);
                            Statement.ParameterType parameterType =
                                    Statement.ParameterType.fromString(
                                                    statementsCursor.getString((i*2) + 4));

                            parameters.add(Statement.Parameter.fromString(parameterValueString,
                                                                          parameterType));
                        }
                        statements.add(new Statement(variableName, functionName, parameters));
                    }
                }
                finally {
                    statementsCursor.close();
                }

                thisProgram.setResultVariableName(resultVariableName);
                thisProgram.setResultType(resultType);
                thisProgram.setParameterTypes(parameterTypes);
                thisProgram.setStatements(statements);

                return true;
            }

            @Override
            protected void onPostExecute(Boolean result)
            {
                ProgramIndex.getAsyncTracker(programIndexTrackerId.getCode())
                            .markProgram(thisProgram.getName());
            }

        }.execute();

    }


    /**
     * Save the program in the database.
     * @param programIndexTrackerId The ID of the caller's asynchronous tracker.
     */
    public void save(final TrackerId programIndexTrackerId)
                //throws SaveException
    {
        final Program thisProgram = this;

//        if (this.parameterTypes.size() > 3)
//            throw new SaveException();

        new AsyncTask<Void,Void,Boolean>()
        {

            @Override
            protected Boolean doInBackground(Void... args)
            {
                SQLiteDatabase database = Global.getDatabase();

                // Update Program table row
                ContentValues programRow = new ContentValues();
                programRow.put("id", thisProgram.getId().toString());
                programRow.put("name", thisProgram.getName());
                programRow.put("number_of_parameters", thisProgram.getParameterTypes().size());
                programRow.put("result_type", FunctionValueType.asString(
                                                    thisProgram.getResultType()));
                programRow.put("variable_name", thisProgram.getResultVariableName());

                List<FunctionValueType> parameterTypes = thisProgram.getParameterTypes();
                for (int i = 0; i < parameterTypes.size() && i < 3; i++) {
                    String columnName = "parameter_type_" + Integer.toString(i);
                    programRow.put(columnName,
                                    FunctionValueType.asString(parameterTypes.get(i)));
                }

                database.insertWithOnConflict(SheetContract.Program.TABLE_NAME,
                                              null,
                                              programRow,
                                              SQLiteDatabase.CONFLICT_REPLACE);

                // Update Statement rows

                // Delete current values, before inserting new ones, since that's a lot easier than
                // tracking which values were deleted and which were changed.
                String hasProgramId = "program_id = " + SQL.quoted(thisProgram.getId().toString());
                database.delete(SheetContract.Statement.TABLE_NAME, hasProgramId, null);

                // Save each tuple
                for (Statement statement : thisProgram.getStatements())
                {
                    ContentValues statementRow = new ContentValues();
                    statementRow.put("program_id", thisProgram.getId().toString());
                    statementRow.put("variable_name", statement.getVariableName());
                    statementRow.put("function_name", statement.getFunctionName());

                    // Parameters

                    List<Statement.Parameter> parameters = statement.getParameters();

                    for (int i = 0; i < parameters.size() && i < 3; i++) {
                        String valueColumnName = "parameter_value_" + Integer.toString(i);
                        String typeColumnName = "parameter_type_" + Integer.toString(i);
                        statementRow.put(valueColumnName, parameters.get(i).valueAsString());
                        statementRow.put(typeColumnName,
                                         Statement.ParameterType.asString(
                                                                    parameters.get(i).getType()));
                    }

                    database.insert(SheetContract.Statement.TABLE_NAME, null, statementRow);
                }

                return true;
            }

            @Override
            protected void onPostExecute(Boolean result)
            {
                ProgramIndex.getAsyncTracker(programIndexTrackerId.getCode())
                            .markProgram(thisProgram.getName());
            }

        }.execute();

    }


}
