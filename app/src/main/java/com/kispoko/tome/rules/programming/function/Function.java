
package com.kispoko.tome.rules.programming.function;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.kispoko.tome.Global;
import com.kispoko.tome.db.SheetContract;
import com.kispoko.tome.util.database.SQL;
import com.kispoko.tome.util.TrackerId;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;



/**
 * Function Definition
 */
public class Function
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private UUID id;
    private String name;
    private UUID sheetId;
    private List<FunctionValueType> parameterTypes;
    private FunctionValueType resultType;
    private List<Tuple> tuples;

    private Map<List<FunctionValue>,FunctionValue> lookupMap;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Function(UUID id, String name, UUID sheetId)
    {
        this.id = id;
        this.name = name;
        this.sheetId = sheetId;
    }


    public Function(UUID id, String name, UUID sheetId, List<FunctionValueType> parameterTypes,
                    FunctionValueType resultType, List<Tuple> tuples)
    {
        this.id = id;
        this.name = name;
        this.sheetId = sheetId;
        this.parameterTypes = parameterTypes;
        this.resultType = resultType;
        this.tuples = tuples;
    }


    @SuppressWarnings("unchecked")
    public static Function fromYaml(UUID sheetId, Map<String,Object> functionDefinitionYaml)
    {
        // VALUES TO PARSE
        // --------------------------------------------------------------------------------------
        UUID id = UUID.randomUUID();
        String name = null;
        List<FunctionValueType> parameterTypes = new ArrayList<>();
        FunctionValueType resultType = null;
        List<Tuple> tuples = new ArrayList<>();

        // PARSE VALUES
        // --------------------------------------------------------------------------------------

        // > Top Level
        // --------------------------------------------------------------------------------------

        // ** Name
        if (functionDefinitionYaml.containsKey("name"))
            name = (String) functionDefinitionYaml.get("name");

        // > Definition
        // --------------------------------------------------------------------------------------

        // ** Parameter Types
        if (functionDefinitionYaml.containsKey("parameter_types")) {
            List<String> parameterTypeList =
                    ((List<String>) functionDefinitionYaml.get("parameter_types"));
            for (String parameterType : parameterTypeList) {
                parameterTypes.add(FunctionValueType.fromString(parameterType));
            }
        }

        // ** Result Type
        if (functionDefinitionYaml.containsKey("result_type")) {
            resultType = FunctionValueType.fromString(
                            (String) functionDefinitionYaml.get("result_type"));
        }
        if (functionDefinitionYaml.containsKey("tuples")) {
            List<Map<String,Object>> tuplesYaml =
                    (List<Map<String,Object>>) functionDefinitionYaml.get("tuples");
            for (Map<String,Object> tupleYaml : tuplesYaml) {
                tuples.add(Tuple.fromYaml(parameterTypes, resultType, tupleYaml));
            }
        }

        return new Function(id, name, sheetId, parameterTypes, resultType, tuples);
    }


    // API
    // ------------------------------------------------------------------------------------------

    // > State
    // ------------------------------------------------------------------------------------------

    public UUID getId() {
        return this.id;
    }


    // ** Name
    // ------------------------------------------------------------------------------------------

    public String getName() {
        return this.name;
    }


    public void setName(String name) {
        this.name = name;
    }


    // ** Sheet Id
    // ------------------------------------------------------------------------------------------

    public UUID getSheetId() {
        return this.sheetId;
    }


    // ** Parameter Types
    // ------------------------------------------------------------------------------------------

    public List<FunctionValueType> getParameterTypes() {
        return this.parameterTypes;
    }


    public void setParameterTypes(List<FunctionValueType> parameterTypes) {
        this.parameterTypes = parameterTypes;
    }


    // ** Result Type
    // ------------------------------------------------------------------------------------------

    public FunctionValueType getResultType() {
        return this.resultType;
    }


    public void setResultType(FunctionValueType resultType) {
        this.resultType = resultType;
    }


    // ** Tuples
    // ------------------------------------------------------------------------------------------

    public List<Tuple> getTuples() {
        return this.tuples;
    }


    public void setTuples(List<Tuple> tuples) {
        this.tuples = tuples;
    }


    public FunctionValue execute(List<FunctionValue> parameters)
    {
        return lookupMap.get(parameters);
    }


    // > Database
    // ------------------------------------------------------------------------------------------

    public void load(final TrackerId functionIndexTrackerId)
    {
        final Function thisFunction = this;

        new AsyncTask<Void,Void,Boolean>()
        {

            @Override
            protected Boolean doInBackground(Void... args)
            {
                SQLiteDatabase database = Global.getDatabase();

                // Query Function Data
                String functionQuery =
                    "SELECT f.name, f.result_type, f.number_of_parameters, f.parameter_type_1, " +
                           "f.parameter_type2, f.parameter_type_3 " +
                    "FROM Function f " +
                    "WHERE f.function_name =  " + SQL.quoted(thisFunction.getName());

                Cursor functionCursor = database.rawQuery(functionQuery, null);

                String name = null;
                FunctionValueType resultType = null;
                Integer numberOfParameters = null;
                List<FunctionValueType> parameterTypes = new ArrayList<>();

                try {
                    functionCursor.moveToFirst();
                    name = functionCursor.getString(0);
                    resultType = FunctionValueType.fromString(functionCursor.getString(1));
                    numberOfParameters = functionCursor.getInt(2);

                    for (int i = 0; i < numberOfParameters; i++) {
                        parameterTypes.add(
                                FunctionValueType.fromString(functionCursor.getString(i + 3)));
                    }
                }
                finally {
                    functionCursor.close();
                }

                // Query Tuples
                String tuplesQuery =
                    "SELECT t.result, t.parameter1, t.parameter2, t.parameter3 " +
                    "FROM tuple t " +
                    "WHERE t.function_name = " + SQL.quoted(thisFunction.getName());

                Cursor tuplesCursor = database.rawQuery(tuplesQuery, null);

                List<Tuple> tuples = new ArrayList<>();
                try {
                    while (tuplesCursor.moveToNext()) {
                        FunctionValue result = FunctionValue.fromString(tuplesCursor.getString(0),
                                                                        resultType);
                        List<FunctionValue> parameters = new ArrayList<>();

                        for (int i = 0; i < numberOfParameters; i++) {
                            String parameterString = tuplesCursor.getString(i + 1);
                            parameters.add(FunctionValue.fromString(parameterString,
                                                                    parameterTypes.get(i)));
                        }
                        tuples.add(new Tuple(parameters, result));
                    }
                }
                finally {
                    tuplesCursor.close();
                }

                thisFunction.setName(name);
                thisFunction.setResultType(resultType);
                thisFunction.setParameterTypes(parameterTypes);
                thisFunction.setTuples(tuples);

                return true;
            }

            @Override
            protected void onPostExecute(Boolean result)
            {
                FunctionIndex.getAsyncTracker(functionIndexTrackerId.getCode())
                        .markFunction(thisFunction.getName());
            }

        }.execute();

    }


    /**
     * Save the program in the database.
     * @param functionIndexTrackerId The ID of the caller's asynchronous tracker.
     */
    public void save(final TrackerId functionIndexTrackerId)
    {
        final Function thisFunction = this;

        new AsyncTask<Void,Void,Boolean>()
        {

            @Override
            protected Boolean doInBackground(Void... args)
            {
                SQLiteDatabase database = Global.getDatabase();

                // Update Function table row
                ContentValues functionRow = new ContentValues();
                functionRow.put("function_id", thisFunction.getId().toString());
                functionRow.put("name", thisFunction.getName());
                functionRow.put("sheet_id", thisFunction.getSheetId().toString());
                functionRow.put("number_of_parameters", thisFunction.getParameterTypes().size());

                List<FunctionValueType> parameterTypes = thisFunction.getParameterTypes();
                for (int i = 0; i < parameterTypes.size() && i < 3; i++) {
                    String columnName = "parameter_type_" + Integer.toString(i);
                    functionRow.put(columnName,
                                    FunctionValueType.asString(parameterTypes.get(i)));
                }

                functionRow.put("result_type", FunctionValueType.asString(
                                                    thisFunction.getResultType()));

                database.insertWithOnConflict(SheetContract.Function.TABLE_NAME,
                                              null,
                                              functionRow,
                                              SQLiteDatabase.CONFLICT_REPLACE);

                // Update Tuple rows

                // Delete current values, before inserting new ones, since that's a lot easier than
                // tracking which values were deleted and which were changed.
                String hasFunctionName = "function_name = " + SQL.quoted(thisFunction.getName());
                database.delete(SheetContract.Tuple.TABLE_NAME, hasFunctionName, null);

                // Save each tuple
                for (Tuple tuple : thisFunction.getTuples())
                {
                    ContentValues tupleRow = new ContentValues();
                    tupleRow.put("function_name", thisFunction.getName());

                    // Parameters
                    List<FunctionValue> parameters = tuple.getParameters();
                    for (int i = 0; i < parameters.size() && i < 3; i++) {
                        String columnName = "parameter" + Integer.toString(i);
                        tupleRow.put(columnName, parameters.get(i).asString());
                    }

                    tupleRow.put("result", tuple.getResult().asString());

                    database.insert(SheetContract.Tuple.TABLE_NAME, null, tupleRow);
                }

                return true;
            }

            @Override
            protected void onPostExecute(Boolean result)
            {
                FunctionIndex.getAsyncTracker(functionIndexTrackerId.getCode())
                             .markFunction(thisFunction.getName());
            }

        }.execute();

    }

}
