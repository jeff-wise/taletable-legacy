
package com.kispoko.tome.util.model;


import android.content.ContentValues;
import android.os.AsyncTask;

import com.kispoko.tome.util.database.DatabaseException;
import com.kispoko.tome.util.database.SQL;
import com.kispoko.tome.util.database.query.CollectionQuery;
import com.kispoko.tome.util.database.query.ModelQuery;
import com.kispoko.tome.util.database.query.ModelQueryParameters;
import com.kispoko.tome.util.database.query.ResultRow;
import com.kispoko.tome.util.database.query.UpsertQuery;
import com.kispoko.tome.util.database.sql.SQLValue;
import com.kispoko.tome.util.tuple.Tuple2;
import com.kispoko.tome.util.tuple.Tuple3;
import com.kispoko.tome.util.value.CollectionValue;
import com.kispoko.tome.util.value.ModelValue;
import com.kispoko.tome.util.value.PrimitiveValue;
import com.kispoko.tome.util.value.Value;

import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;



/**
 * ModelLib
 */
public class ModelLib
{


    public static <A> String name(Class<A> modelClass)
    {
        String modelName = modelClass.getName();

        if (modelName.lastIndexOf('.') > 0)
        {
            modelName = modelName.substring(modelName.lastIndexOf('.') + 1); // Map$Entry
            modelName = modelName.replace('$', '.');      // Map.Entry
        }

        modelName = SQL.asValidIdentifier(modelName).toLowerCase();

        return modelName;
    }


    public static String name(Model model)
    {
        return ModelLib.name(model.getClass());
    }


    /**
     * Automatically load this model from the database using reflection on its Value properties
     * and the database data stored within them.
     * @return A new instance of the moddel.
     * @throws DatabaseException
     */
    public static <A extends Model> void modelFromDatabase(
                                            final Class<A> modelClass,
                                            final ModelQueryParameters queryParameters,
                                            final ModelValue.OnLoadListener<A> onLoadListener)
    {
        new AsyncTask<Void,Void,Object> ()
        {

            @Override
            protected Object doInBackground(Void... args)
            {
                try
                {
                    // [1] GET SQL columns
                    A dummyModel = ModelLib.newModel(modelClass);
                    List<Tuple2<String, SQLValue.Type>> sqlColumns =
                                                    ModelLib.sqlColumns(dummyModel);

                    // [2] RUN the query
                    ModelQuery modelQuery = new ModelQuery(ModelLib.name(modelClass),
                                                           sqlColumns,
                                                           queryParameters);
                    ResultRow row = modelQuery.result();

                    return row;
                }
                catch (DatabaseException exception)
                {
                    return exception;
                }
            }

            @Override
            protected void onPostExecute(Object result)
            {
                if (result instanceof DatabaseException)
                {
                    DatabaseException databaseException = (DatabaseException) result;
                    onLoadListener.onLoadError(databaseException);
                }
                else if (result instanceof ResultRow)
                {
                    ResultRow row = (ResultRow) result;

                    OnModelLoadListener<A> onModelLoadListener = new OnModelLoadListener<A>()
                    {
                        @Override
                        public void onModelLoad(A model) {
                            if (onLoadListener != null)
                                onLoadListener.onLoad(model);
                        }

                        @Override
                        public void onModelLoadError(DatabaseException exception) {
                            onLoadListener.onLoadError(exception);
                        }
                    };

                    ModelLib.modelFromRow(modelClass, row, onModelLoadListener);
                }
            }

        }.execute();
    }


    /**
     * @return A new instance of the moddel.
     * @throws DatabaseException
     */
    @SuppressWarnings("unchecked")
    public static <A extends Model> void modelCollectionFromDatabase(
                                            final String parentModelName,
                                            final UUID parentModelId,
                                            final List<Class<? extends A>> modelClasses,
                                            final CollectionValue.OnLoadListener<A> onLoadListener)
    {

        new AsyncTask<Void,Void,Object> ()
        {

            @Override
            protected Object doInBackground(Void... args)
            {
                try
                {
                    List<List<ResultRow>> resultsList = new ArrayList<>();

                    for (final Class<? extends A> modelClass : modelClasses)
                    {
                        // [1] GET SQL columns
                        A dummyModel = ModelLib.newModel(modelClass);
                        List<Tuple2<String, SQLValue.Type>> sqlColumns =
                                ModelLib.sqlColumns(dummyModel);

                        // [2] RUN the query
                        CollectionQuery collectionQuery = new CollectionQuery(
                                ModelLib.name(modelClass),
                                parentModelName,
                                parentModelId,
                                sqlColumns);
                        List<ResultRow> resultRows = collectionQuery.result();

                        resultsList.add(resultRows);
                    }

                    return resultsList;
                }
                catch (DatabaseException exception)
                {
                    return exception;
                }
            }

            @Override
            protected void onPostExecute(Object result)
            {
                if (result instanceof DatabaseException)
                {
                    DatabaseException databaseException = (DatabaseException) result;
                    onLoadListener.onLoadError(databaseException);
                }
                else if (result instanceof List)
                {
                    List<List<ResultRow>> resultsList = (List<List<ResultRow>>) result;

                    Integer collectionSizeAcc = 0;
                    for (List<ResultRow> rowSet : resultsList) {
                        collectionSizeAcc += rowSet.size();
                    }
                    final Integer collectionSize = collectionSizeAcc;

                    final List<A> models = new ArrayList<>();

                    OnModelLoadListener onModelLoadListener = new OnModelLoadListener<A>()
                    {
                        @Override
                        public void onModelLoad(A model) {
                            models.add(model);
                            if (models.size() == collectionSize) {
                                onLoadListener.onLoad(models);
                            }
                        }

                        @Override
                        public void onModelLoadError(DatabaseException exception) {
                            onLoadListener.onLoadError(exception);
                        }
                    };

                    // For each model class, go through the row set of results, and create all
                    // of the models recursively
                    for (int i = 0; i < resultsList.size(); i++)
                    {
                        List<ResultRow>    rows       = resultsList.get(i);
                        Class<? extends A> modelClass = modelClasses.get(i);

                        for (ResultRow row : rows) {
                            ModelLib.modelFromRow(modelClass, row, onModelLoadListener);
                        }
                    }
                }
            }

        }.execute();

    }


    /**
     * Automatically load this model from the database using reflection on its Value properties
     * and the database data stored within them.
     * @return A new instance of the moddel.
     * @throws DatabaseException
     */
    public static void modelToDatabase(final Model model,
                                       final ModelValue.OnSaveListener onSaveListener)
    {
        new AsyncTask<Void,Void,Object> ()
        {

            @Override
            protected Object doInBackground(Void... args)
            {
                try
                {
                    saveQuery(model);
                    return null;
                }
                catch (DatabaseException exception)
                {
                    return exception;
                }
            }

            @Override
            protected void onPostExecute(Object result)
            {
                if (result instanceof DatabaseException)
                {
                    DatabaseException databaseException = (DatabaseException) result;
                    onSaveListener.onSaveError(databaseException);
                }
                else
                {
                    OnModelSaveListener onModelSaveListener = new OnModelSaveListener() {
                        @Override
                        public void onModelSave() {
                            if (onSaveListener != null)
                                onSaveListener.onSave();
                        }

                        @Override
                        public void onModelSaveError(DatabaseException exception) {
                            if (onSaveListener != null)
                                onSaveListener.onSaveError(exception);
                        }
                    };

                    modelToRow(model, onModelSaveListener);
                }
            }

        }.execute();
    }


    /**
     * @return A new instance of the moddel.
     * @throws DatabaseException
     */
    public static void modelCollectionToDatabase(
                                        final List<Model> models,
                                        final CollectionValue.OnSaveListener onSaveListener)
    {

        new AsyncTask<Void,Void,Object> ()
        {

            @Override
            protected Object doInBackground(Void... args)
            {
                try
                {
                    for (Model model : models) {
                        saveQuery(model);
                    }
                    return null;
                }
                catch (DatabaseException exception)
                {
                    return exception;
                }
            }

            @Override
            protected void onPostExecute(Object result)
            {
                if (result instanceof DatabaseException)
                {
                    DatabaseException databaseException = (DatabaseException) result;
                    onSaveListener.onSaveError(databaseException);
                }
                else
                {
                    final SaveCounter saveCounter = new SaveCounter();

                    OnModelSaveListener onModelSaveListener = new OnModelSaveListener()
                    {

                        @Override
                        public void onModelSave()
                        {
                            saveCounter.inc();

                            if (saveCounter.getCount() == models.size())
                                onSaveListener.onSave();
                        }

                        @Override
                        public void onModelSaveError(DatabaseException exception) {
                            if (onSaveListener != null)
                                onSaveListener.onSaveError(exception);
                        }
                    };

                    for (Model model : models) {
                        modelToRow(model, onModelSaveListener);
                    }
                }
            }

        }.execute();

    }


    /**
     * Save the model to the database.
     * @throws DatabaseException
     */
    private static void modelToRow(Model model, final OnModelSaveListener onModelSaveListener)
    {
        try
        {
            // [1] Group values by type
            // --------------------------------------------------------------------------------------

            Tuple3<List<PrimitiveValue<?>>,
            List<ModelValue<?>>,
            List<CollectionValue<?>>> modelValuesTuple = ModelLib.modelValues(model);

            final List<ModelValue<?>>      modelValues      = modelValuesTuple.getItem2();
            final List<CollectionValue<?>> collectionValues = modelValuesTuple.getItem3();


            // [2] Save Shared Value Rows
            // --------------------------------------------------------------------------------------

            for (final ModelValue<? extends Model> modelValue : modelValues)
            {
                ModelValue.OnSaveListener onSaveListener = new ModelValue.OnSaveListener() {
                    @Override
                    public void onSave() {
                        modelValue.setIsSaved(true);
                        if (ModelLib.valuesAreSaved(modelValues, collectionValues)) {
                            if (onModelSaveListener!= null)
                                onModelSaveListener.onModelSave();
                        }
                    }

                    @Override
                    public void onSaveError(DatabaseException exception) {
                        if (onModelSaveListener != null)
                            onModelSaveListener.onModelSaveError(exception);
                    }
                };

                modelValue.save(onSaveListener);
            }


            // [B 3] Save Collection Values
            // --------------------------------------------------------------------------------------

            for (final CollectionValue<? extends Model> collectionValue : collectionValues)
            {

                CollectionValue.OnSaveListener onSaveListener = new CollectionValue.OnSaveListener()
                {
                    @Override
                    public void onSave() {
                        collectionValue.setIsSaved(true);
                        if (ModelLib.valuesAreSaved(modelValues, collectionValues)) {
                            if (onModelSaveListener!= null)
                                onModelSaveListener.onModelSave();
                        }
                    }

                    @Override
                    public void onSaveError(DatabaseException exception) {
                        if (onModelSaveListener != null)
                            onModelSaveListener.onModelSaveError(exception);
                    }
                };

                collectionValue.save(onSaveListener);
            }


            // [C 1] If this is a leaf node (contains only Primitive values), then call onLoad
            // --------------------------------------------------------------------------------------

            if (modelValues.size() == 0 && collectionValues.size() == 0) {
                if (onModelSaveListener != null) onModelSaveListener.onModelSave();
            }
        }
        catch (DatabaseException exception)
        {
            onModelSaveListener.onModelSaveError(exception);
        }
    }


    /**
     * Notes: primitive values are always loaded if model value is updated.
     * @return
     * @throws DatabaseException
     */
    private static <A extends Model> void modelFromRow(
                                            Class<A> modelClass,
                                            ResultRow row,
                                            final OnModelLoadListener<A> onModelLoadListener)
    {
        try
        {
            // [A 1] Get the Model's Values
            // ----------------------------------------------------------------------------------

            final A model = ModelLib.newModel(modelClass);


            // [A 2] Get the Model's Values
            // ----------------------------------------------------------------------------------

            Tuple3<List<PrimitiveValue<?>>,
                    List<ModelValue<?>>,
                    List<CollectionValue<?>>> modelValuesTuple = ModelLib.modelValues(model);

            List<PrimitiveValue<?>> primitiveValues = modelValuesTuple.getItem1();
            final List<ModelValue<?>> modelValues = modelValuesTuple.getItem2();
            final List<CollectionValue<?>> collectionValues = modelValuesTuple.getItem3();


            // [B 1] Evaluate model values
            // ----------------------------------------------------------------------------------

            SQLValue idSqlValue = row.getSQLValue("_id");
            model.setId(UUID.fromString(idSqlValue.getText()));


            // [B 2] Evaluate primitive model values
            // ----------------------------------------------------------------------------------

            for (PrimitiveValue<?> primitiveValue : primitiveValues) {
                String columnName = primitiveValue.sqlColumnName();
                primitiveValue.fromSQLValue(row.getSQLValue(columnName));
            }


            // [B 3] Evaluate model values (many-to-one values)
            // ----------------------------------------------------------------------------------

            for (final ModelValue<?> modelValue : modelValues) {
                String columnName = modelValue.sqlColumnName();

                UUID modelValueId = UUID.fromString(row.getSQLValue(columnName).getText());
                ModelQueryParameters queryParameters =
                        new ModelQueryParameters(new ModelQueryParameters.PrimaryKey(modelValueId),
                                ModelQueryParameters.Type.PRIMARY_KEY);

                ModelValue.OnLoadListener onLoadListener = new ModelValue.OnLoadListener<A>() {
                    @Override
                    public void onLoad(A loadedModel) {
                        if (ModelLib.valuesAreLoaded(modelValues, collectionValues)) {
                            if (onModelLoadListener != null)
                                onModelLoadListener.onModelLoad(model);
                        }
                    }

                    @Override
                    public void onLoadError(DatabaseException exception) {
                        if (onModelLoadListener != null)
                            onModelLoadListener.onModelLoadError(exception);
                    }
                };

                modelValue.load(queryParameters, onLoadListener);
            }


            // [B 2] Evaluate collection values (one-to-many values)
            // ----------------------------------------------------------------------------------

            for (final CollectionValue<? extends Model> collectionValue : collectionValues)
            {
                CollectionValue.OnLoadListener<A> onLoadListener =
                        new CollectionValue.OnLoadListener<A>()
                {
                    @Override
                    public void onLoad(List<A> loadedModels) {
                        if (ModelLib.valuesAreLoaded(modelValues, collectionValues)) {
                            if (onModelLoadListener != null)
                                onModelLoadListener.onModelLoad(model);
                        }
                    }

                    @Override
                    public void onLoadError(DatabaseException exception) {
                        if (onModelLoadListener != null)
                            onModelLoadListener.onModelLoadError(exception);
                    }
                };

                collectionValue.load(ModelLib.name(model), model.getId(), onLoadListener);
            }


            // [C 1] If this is a leaf node (contains only Primitive values), then call onLoad
            // ----------------------------------------------------------------------------------

            if (modelValues.size() == 0 && collectionValues.size() == 0) {
                if (onModelLoadListener != null)
                    onModelLoadListener.onModelLoad(model);
            }
        }
        catch (DatabaseException exception)
        {
            onModelLoadListener.onModelLoadError(exception);
        }
    }


    /**
     * Collect all of the ModelLib's values and return them in data structures suitable for
     * further analysis.
     * @param model A ModelLib instance to get the values of.
     * @param <A> The model type.
     * @return The ModelLib's values, sorted.
     * @throws DatabaseException
     */
    private static <A> Tuple3<List<PrimitiveValue<?>>,
                              List<ModelValue<?>>,
                              List<CollectionValue<?>>> modelValues(A model)
                       throws DatabaseException
    {
        // [1] Get all of the class's Value fields
        // --------------------------------------------------------------------------------------
        List<Field> valueFields = new ArrayList<>();

        List<Field> allFields = FieldUtils.getAllFieldsList(model.getClass());
        for (Field field : allFields)
        {
            if (Value.class.isAssignableFrom(field.getType()))
                valueFields.add(field);
        }

        // [2] Store the value fields by type and map to columns
        // --------------------------------------------------------------------------------------
        List<PrimitiveValue<?>>                primitiveValues  = new ArrayList<>();
        List<ModelValue<? extends Model>>      modelValues      = new ArrayList<>();
        List<CollectionValue<? extends Model>> collectionValues = new ArrayList<>();

        try
        {
            for (Field field : valueFields)
            {
                //Value<?> value = (Value<?>) field.get(model);
                Value<?> value = (Value<?>) FieldUtils.readField(field, model, true);

                // Sort values by database value type
                if (PrimitiveValue.class.isAssignableFrom(field.getType())) {
                    PrimitiveValue primitiveValue = (PrimitiveValue) value;
                    primitiveValue.setField(field);
                    primitiveValues.add(primitiveValue);
                }
                else if (ModelValue.class.isAssignableFrom(field.getType())) {
                    ModelValue<? extends Model> modelValue = (ModelValue<? extends Model>) value;
                    modelValue.setField(field);
                    modelValues.add(modelValue);
                }
                else if (CollectionValue.class.isAssignableFrom(field.getType())) {
                    CollectionValue<? extends Model> collectionValue =
                                                     (CollectionValue<? extends Model>) value;
                    collectionValue.setField(field);
                    collectionValues.add(collectionValue);
                }
            }
        }
        catch (IllegalAccessException e)
        {
            throw new DatabaseException();
        }

        return new Tuple3<>(primitiveValues, modelValues, collectionValues);
    }


    /**
     * Get the SQL column representations for all of the values in the model. Both primitive and
     * model values have representations in the model's row. Collection values are stored in
     * their own model's table and contain the foreign key to the parent model.
     * @param model The model.
     * @return
     * @throws DatabaseException
     */
    private static List<Tuple2<String,SQLValue.Type>> sqlColumns(Model model)
                   throws DatabaseException
    {
        Tuple3<List<PrimitiveValue<?>>,
                   List<ModelValue<?>>,
                   List<CollectionValue<?>>> modelValuesTuple = ModelLib.modelValues(model);

        // Get all of model's database columns. Both primitive values and model values have
        // column representations
        List<Tuple2<String,SQLValue.Type>> columns = new ArrayList<>();

        // > Add MODEL columns
        columns.add(new Tuple2<>("_id", SQLValue.Type.TEXT));

        // > Add PRIMITIVE VALUE columns
        for (PrimitiveValue<?> primitiveValue : modelValuesTuple.getItem1())
        {
            columns.add(new Tuple2<>(primitiveValue.sqlColumnName(),
                                     primitiveValue.sqlType()));
        }

        // > Add MODEL VALUE columns
        for (ModelValue<?> modelValue : modelValuesTuple.getItem2())
        {
            columns.add(new Tuple2<>(modelValue.sqlColumnName(),
                                     SQLValue.Type.TEXT));
        }

        return columns;
    }


    /**
     * Create a new ModelLib instance of the provided class.
     * @param classObject The ModelLib class to create.
     * @param <A> The type of ModelLib.
     * @return A new model instance.
     * @throws DatabaseException
     */
    private static <A> A newModel(Class<A> classObject)
                       throws DatabaseException
    {
        A model = null;
        try {
            model = classObject.newInstance();
        }
        catch (Exception e) {
            throw new DatabaseException();
        }
        return model;
    }


    // > Tables
    // ------------------------------------------------------------------------------------------

    public static <A extends Model> String defineTableSQLString(Class<A> modelClass)
                                    throws DatabaseException
    {
        StringBuilder tableBuilder = new StringBuilder();

        // [1] Create Table
        // --------------------------------------------------------------------------------------
        tableBuilder.append("CREATE TABLE IF NOT EXISTS ");
        tableBuilder.append(ModelLib.name(modelClass));
        tableBuilder.append(" ( ");

        // [2] ColumnUnion Definitions
        // --------------------------------------------------------------------------------------

        // > Get ModelLib values

        Model dummyModel = ModelLib.newModel(modelClass);

        Tuple3<List<PrimitiveValue<?>>,
                List<ModelValue<?>>,
                List<CollectionValue<?>>> modelValuesTuple = ModelLib.modelValues(dummyModel);

        List<PrimitiveValue<?>> primitiveValues = modelValuesTuple.getItem1();
        List<ModelValue<?>>     modelValues     = modelValuesTuple.getItem2();

        // ** Model Id
        tableBuilder.append("_id");
        tableBuilder.append(" ");
        tableBuilder.append(SQLValue.Type.TEXT.name().toUpperCase());
        tableBuilder.append(" PRIMARY KEY");

        // ** Primitive Values
        for (PrimitiveValue<?> primitiveValue : primitiveValues)
        {
            String        columnName = primitiveValue.sqlColumnName();
            SQLValue.Type columnType = primitiveValue.sqlType();

            tableBuilder.append(", ");
            tableBuilder.append(columnName);
            tableBuilder.append(" ");
            tableBuilder.append(columnType.name().toUpperCase());
        }

        // ** Model Values
        for (ModelValue<?> modelValue : modelValues)
        {
            String columnName = modelValue.sqlColumnName();

            tableBuilder.append(", ");
            tableBuilder.append(columnName);
            tableBuilder.append(" ");
            tableBuilder.append(SQLValue.Type.TEXT.name().toUpperCase());
        }

        // [2] End
        // --------------------------------------------------------------------------------------

        tableBuilder.append(" )");

        return tableBuilder.toString();
    }



    private static boolean valuesAreLoaded(List<ModelValue<?>> modelValues,
                                           List<CollectionValue<?>> collectionValues)
    {
        boolean allLoaded = true;

        for (ModelValue modelValue : modelValues) {
            if (!modelValue.getIsLoaded()) {
                allLoaded = false;
                break;
            }
        }

        if (!allLoaded) return false;

        for (CollectionValue collectionValue : collectionValues) {
            if (!collectionValue.getIsLoaded()) {
                allLoaded = false;
                break;
            }
        }

        return allLoaded;
    }


    private static boolean valuesAreSaved(List<ModelValue<?>> modelValues,
                                          List<CollectionValue<?>> collectionValues)
    {
        boolean allSaved = true;

        for (ModelValue modelValue : modelValues) {
            if (!modelValue.getIsSaved()) {
                allSaved = false;
                break;
            }
        }

        if (!allSaved) return false;

        for (CollectionValue collectionValue : collectionValues) {
            if (!collectionValue.getIsSaved()) {
                allSaved = false;
                break;
            }
        }

        return allSaved;
    }



    private static void saveQuery(Model model)
                   throws DatabaseException
    {
        // [A 1] Group values by type
        // --------------------------------------------------------------------------------------

        Tuple3<List<PrimitiveValue<?>>,
                List<ModelValue<?>>,
                List<CollectionValue<?>>> modelValuesTuple = ModelLib.modelValues(model);

        final List<PrimitiveValue<?>>  primitiveValues  = modelValuesTuple.getItem1();
        final List<ModelValue<?>>      modelValues      = modelValuesTuple.getItem2();


        // [B 1] Save ModelLib row
        // --------------------------------------------------------------------------------------

        // > Save each column value into a ContentValues
        ContentValues row = new ContentValues();

        // ** Save the model values
        row.put("_id", model.getId().toString());

        // ** Save all of the primitive values
        for (PrimitiveValue primitiveValue : primitiveValues) {
            SQLValue sqlValue = primitiveValue.toSQLValue();
            String columnName = primitiveValue.sqlColumnName();

            switch (sqlValue.getType()) {
                case INTEGER:
                    row.put(columnName, sqlValue.getInteger());
                    break;
                case REAL:
                    row.put(columnName, sqlValue.getReal());
                    break;
                case TEXT:
                    row.put(columnName, sqlValue.getText());
                    break;
                case BLOB:
                    row.put(columnName, sqlValue.getBlob());
                    break;
                case NULL:
                    row.putNull(columnName);
                    break;
            }
        }

        // ** Save all of the model value identifiers (as foreign keys)
        for (ModelValue<? extends Model> modelValue : modelValues) {
            String columnName = modelValue.sqlColumnName();
            if (!modelValue.isNull())
                row.put(columnName, modelValue.getValue().getId().toString());
            else
                row.putNull(columnName);
        }

        // > Save the row, creating a new one if necessary.
        UpsertQuery upsertQuery = new UpsertQuery(ModelLib.name(model), model.getId(), row);
        upsertQuery.run();
    }



    // LISTENERS
    // ------------------------------------------------------------------------------------------

    private interface OnModelLoadListener<A> {
        void onModelLoad(A model);
        void onModelLoadError(DatabaseException exception);
    }

    private interface OnModelSaveListener {
        void onModelSave();
        void onModelSaveError(DatabaseException exception);
    }


    private static class SaveCounter
    {
        private int saveCounter;

        public SaveCounter()
        {
            this.saveCounter = 0;
        }

        public void inc()
        {
            this.saveCounter += 1;
        }

        public int getCount()
        {
            return this.saveCounter;
        }
    }


}
