
package com.kispoko.tome.util.model;


import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.kispoko.tome.util.database.DatabaseException;
import com.kispoko.tome.util.database.SQL;
import com.kispoko.tome.util.database.error.SerializationError;
import com.kispoko.tome.util.database.error.UninitializedFunctorError;
import com.kispoko.tome.util.database.query.CollectionQuery;
import com.kispoko.tome.util.database.query.ModelQuery;
import com.kispoko.tome.util.database.query.ModelQueryParameters;
import com.kispoko.tome.util.database.query.ResultRow;
import com.kispoko.tome.util.database.query.UpsertQuery;
import com.kispoko.tome.util.database.sql.OneToManyRelation;
import com.kispoko.tome.util.database.sql.SQLValue;
import com.kispoko.tome.util.tuple.Tuple2;
import com.kispoko.tome.util.tuple.Tuple3;
import com.kispoko.tome.util.value.CollectionFunctor;
import com.kispoko.tome.util.value.Functor;
import com.kispoko.tome.util.value.ModelFunctor;
import com.kispoko.tome.util.value.PrimitiveFunctor;

import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
                                            final ModelFunctor.OnLoadListener<A> onLoadListener)
    {
        new AsyncTask<Void,Void,Object> ()
        {

            @Override
            protected Object doInBackground(Void... args)
            {
                try
                {
                    return runLoadQuery(modelClass, queryParameters);
                }
                catch (DatabaseException exception)
                {
                    return exception;
                }
                catch (Exception exception)
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
                    onLoadListener.onLoadDBError(databaseException);
                }
                else if (result instanceof Exception)
                {
                    Exception exception = (Exception) result;
                    onLoadListener.onLoadError(exception);
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
                        public void onModelLoadDBError(DatabaseException exception) {
                            onLoadListener.onLoadDBError(exception);
                        }

                        @Override
                        public void onModelLoadError(Exception exception) {
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
                                            final OneToManyRelation oneToManyRelation,
                                            final List<Class<? extends A>> modelClasses,
                                            final CollectionFunctor.OnLoadListener<A> onLoadListener)
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
                                                                    oneToManyRelation,
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
                catch (Exception exception)
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
                    onLoadListener.onLoadDBError(databaseException);
                }
                else if (result instanceof Exception)
                {
                    Exception exception = (Exception) result;
                    onLoadListener.onLoadError(exception);
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
                        public void onModelLoadDBError(DatabaseException exception) {
                            onLoadListener.onLoadDBError(exception);
                        }

                        @Override
                        public void onModelLoadError(Exception exception) {
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

                    if (collectionSize == 0)
                        onLoadListener.onLoad(models);
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
                                       final ModelFunctor.OnSaveListener onSaveListener)
    {
        new AsyncTask<Void,Void,Object> ()
        {

            @Override
            protected Object doInBackground(Void... args)
            {
                try
                {
                    runSaveQuery(model, new ArrayList<OneToManyRelation>());
                    return null;
                }
                catch (Exception exception)
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
                    onSaveListener.onSaveDBError(databaseException);
                }
                else if (result instanceof Exception)
                {
                    Exception exception = (Exception) result;
                    onSaveListener.onSaveError(exception);
                }
                else
                {
                    OnModelSaveListener onModelSaveListener = new OnModelSaveListener()
                    {
                        @Override
                        public void onModelSave() {
                            if (onSaveListener != null)
                                onSaveListener.onSave();
                        }

                        @Override
                        public void onModelSaveDBError(DatabaseException exception) {
                            if (onSaveListener != null)
                                onSaveListener.onSaveDBError(exception);
                        }

                        @Override
                        public void onModelSaveError(Exception exception) {
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
     * @return A new instance of the model.
     * @throws DatabaseException
     */
    public static void modelCollectionToDatabase(
                                        final List<Model> models,
                                        final List<OneToManyRelation> parentRelations,
                                        final CollectionFunctor.OnSaveListener onSaveListener)
    {

        new AsyncTask<Void,Void,Object> ()
        {

            @Override
            protected Object doInBackground(Void... args)
            {
                try
                {
                    for (Model model : models) {
                        runSaveQuery(model, parentRelations);
                    }
                    return null;
                }
                catch (Exception exception)
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
                    onSaveListener.onSaveDBError(databaseException);
                }
                else if (result instanceof Exception)
                {
                    Exception exception = (Exception) result;
                    onSaveListener.onSaveError(exception);
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
                        public void onModelSaveDBError(DatabaseException exception)
                        {
                            if (onSaveListener != null)
                                onSaveListener.onSaveDBError(exception);
                        }

                        @Override
                        public void onModelSaveError(Exception exception)
                        {
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

            Tuple3<List<PrimitiveFunctor<?>>,
            List<ModelFunctor<?>>,
            List<CollectionFunctor<?>>> modelValuesTuple = ModelLib.modelValues(model);

            final List<ModelFunctor<?>>      modelValues      = modelValuesTuple.getItem2();
            final List<CollectionFunctor<?>> collectionValues = modelValuesTuple.getItem3();


            // [2] Save Shared Value Rows
            // --------------------------------------------------------------------------------------

            for (final ModelFunctor<? extends Model> modelValue : modelValues)
            {
                ModelFunctor.OnSaveListener onSaveListener = new ModelFunctor.OnSaveListener()
                {
                    @Override

                    public void onSave() {
                        modelValue.setIsSaved(true);

                        if (ModelLib.valuesAreSaved(modelValues, collectionValues)) {
                            if (onModelSaveListener != null)
                                onModelSaveListener.onModelSave();
                        }
                    }

                    @Override
                    public void onSaveDBError(DatabaseException exception)
                    {
                        if (onModelSaveListener != null)
                            onModelSaveListener.onModelSaveDBError(exception);
                    }

                    @Override
                    public void onSaveError(Exception exception)
                    {
                        if (onModelSaveListener != null)
                            onModelSaveListener.onModelSaveError(exception);
                    }
                };

                modelValue.save(onSaveListener);
            }


            // [B 3] Save Collection Values
            // --------------------------------------------------------------------------------------

            for (final CollectionFunctor<? extends Model> collectionValue : collectionValues)
            {

                CollectionFunctor.OnSaveListener onSaveListener = new CollectionFunctor.OnSaveListener()
                {
                    @Override
                    public void onSave()
                    {
                        collectionValue.setIsSaved(true);

                        if (ModelLib.valuesAreSaved(modelValues, collectionValues))
                        {
                            if (onModelSaveListener!= null)
                                onModelSaveListener.onModelSave();
                        }
                    }

                    @Override
                    public void onSaveDBError(DatabaseException exception)
                    {
                        if (onModelSaveListener != null)
                            onModelSaveListener.onModelSaveDBError(exception);
                    }

                    @Override
                    public void onSaveError(Exception exception) {
                        if (onModelSaveListener != null)
                            onModelSaveListener.onModelSaveError(exception);
                    }
                };

                List<OneToManyRelation> parentRelations = new ArrayList<>();
                parentRelations.add(new OneToManyRelation(ModelLib.name(model),
                                                          collectionValue.name(),
                                                          model.getId()));
                collectionValue.save(parentRelations, onSaveListener);
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

            Tuple3<List<PrimitiveFunctor<?>>,
                    List<ModelFunctor<?>>,
                    List<CollectionFunctor<?>>> modelValuesTuple = ModelLib.modelValues(model);

            List<PrimitiveFunctor<?>> primitiveValues = modelValuesTuple.getItem1();
            final List<ModelFunctor<?>> modelValues = modelValuesTuple.getItem2();
            final List<CollectionFunctor<?>> collectionValues = modelValuesTuple.getItem3();


            // [B 1] Evaluate model values
            // ----------------------------------------------------------------------------------

            SQLValue idSqlValue = row.getSQLValue("_id");
            model.setId(UUID.fromString(idSqlValue.getText()));


            // [B 2] Evaluate primitive model values
            // ----------------------------------------------------------------------------------

            for (PrimitiveFunctor<?> primitiveValue : primitiveValues) {
                String columnName = primitiveValue.sqlColumnName();
                primitiveValue.fromSQLValue(row.getSQLValue(columnName));
            }


            // [B 3] Evaluate model values (many-to-one values)
            // ----------------------------------------------------------------------------------

            for (final ModelFunctor<?> modelValue : modelValues)
            {
                String modelForeignKeyColumnName = modelValue.sqlColumnName();
                SQLValue modelIdSqlValue = row.getSQLValue(modelForeignKeyColumnName);

                // If there is no model currently
                if (modelIdSqlValue.isNull())
                {
                    modelValue.setIsSaved(false);

                    if (ModelLib.valuesAreLoaded(modelValues, collectionValues))
                    {
                        if (onModelLoadListener != null)
                            onModelLoadListener.onModelLoad(model);
                    }

                    continue;
                }

                UUID modelValueId = UUID.fromString(modelIdSqlValue.getText());
                ModelQueryParameters queryParameters =
                        new ModelQueryParameters(new ModelQueryParameters.PrimaryKey(modelValueId),
                                                 ModelQueryParameters.Type.PRIMARY_KEY);

                ModelFunctor.OnLoadListener onLoadListener = new ModelFunctor.OnLoadListener<A>() {
                    @Override
                    public void onLoad(A loadedModel) {
                        if (ModelLib.valuesAreLoaded(modelValues, collectionValues)) {
                            if (onModelLoadListener != null)
                                onModelLoadListener.onModelLoad(model);
                        }
                    }

                    @Override
                    public void onLoadDBError(DatabaseException exception) {
                        if (onModelLoadListener != null)
                            onModelLoadListener.onModelLoadDBError(exception);
                    }

                    @Override
                    public void onLoadError(Exception exception) {
                        if (onModelLoadListener != null)
                            onModelLoadListener.onModelLoadError(exception);
                    }
                };

                modelValue.load(queryParameters, onLoadListener);
            }


            // [B 2] Evaluate collection values (one-to-many values)
            // ----------------------------------------------------------------------------------

            for (final CollectionFunctor<? extends Model> collectionValue : collectionValues)
            {
                CollectionFunctor.OnLoadListener<A> onLoadListener =
                        new CollectionFunctor.OnLoadListener<A>()
                {
                    @Override
                    public void onLoad(List<A> loadedModels) {
                        if (ModelLib.valuesAreLoaded(modelValues, collectionValues)) {
                            if (onModelLoadListener != null)
                                onModelLoadListener.onModelLoad(model);
                        }
                    }

                    @Override
                    public void onLoadDBError(DatabaseException exception) {
                        if (onModelLoadListener != null)
                            onModelLoadListener.onModelLoadDBError(exception);
                    }

                    @Override
                    public void onLoadError(Exception exception) {
                        if (onModelLoadListener != null)
                            onModelLoadListener.onModelLoadError(exception);
                    }
                };

                collectionValue.load(model, onLoadListener);
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
            onModelLoadListener.onModelLoadDBError(exception);
        }
        catch (Exception exception)
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
    private static <A> Tuple3<List<PrimitiveFunctor<?>>,
                              List<ModelFunctor<?>>,
                              List<CollectionFunctor<?>>> modelValues(A model)
                       throws DatabaseException
    {
        // [1] Get all of the class's Value fields
        // --------------------------------------------------------------------------------------
        List<Field> valueFields = new ArrayList<>();

        List<Field> allFields = FieldUtils.getAllFieldsList(model.getClass());
        for (Field field : allFields)
        {
            if (Functor.class.isAssignableFrom(field.getType()))
                valueFields.add(field);
        }

        // [2] Store the value fields by type and map to columns
        // --------------------------------------------------------------------------------------
        List<PrimitiveFunctor<?>>                primitiveValues  = new ArrayList<>();
        List<ModelFunctor<? extends Model>>      modelValues      = new ArrayList<>();
        List<CollectionFunctor<? extends Model>> collectionValues = new ArrayList<>();

        try
        {
            for (Field field : valueFields)
            {
                Functor<?> functor = (Functor<?>) FieldUtils.readField(field, model, true);

                if (functor == null) {
                    throw DatabaseException.uninitializedFunctor(
                            new UninitializedFunctorError(model.getClass().getName(),
                                                          field.getName()));
                }

                // Sort values by database value type
                if (PrimitiveFunctor.class.isAssignableFrom(field.getType()))
                {
                    PrimitiveFunctor primitiveValue = (PrimitiveFunctor) functor;
                    primitiveValue.setName(field.getName());
                    primitiveValues.add(primitiveValue);
                }
                else if (ModelFunctor.class.isAssignableFrom(field.getType()))
                {
                    ModelFunctor<? extends Model> modelValue = (ModelFunctor<? extends Model>) functor;
                    modelValue.setName(field.getName());
                    modelValues.add(modelValue);
                }
                else if (CollectionFunctor.class.isAssignableFrom(field.getType()))
                {
                    CollectionFunctor<? extends Model> collectionValue =
                                                     (CollectionFunctor<? extends Model>) functor;
                    collectionValue.setName(field.getName());
                    collectionValues.add(collectionValue);
                }
            }
        }
        catch (IllegalAccessException e)
        {
            throw DatabaseException.serialization(
                    new SerializationError(model.getClass().getName()));
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
        Tuple3<List<PrimitiveFunctor<?>>,
                   List<ModelFunctor<?>>,
                   List<CollectionFunctor<?>>> modelValuesTuple = ModelLib.modelValues(model);

        // Get all of model's database columns. Both primitive values and model values have
        // column representations
        List<Tuple2<String,SQLValue.Type>> columns = new ArrayList<>();

        // > Add MODEL columns
        columns.add(new Tuple2<>("_id", SQLValue.Type.TEXT));

        // > Add PRIMITIVE VALUE columns
        for (PrimitiveFunctor<?> primitiveValue : modelValuesTuple.getItem1())
        {
            columns.add(new Tuple2<>(primitiveValue.sqlColumnName(),
                                     primitiveValue.sqlType()));
        }

        // > Add MODEL VALUE columns
        for (ModelFunctor<?> modelValue : modelValuesTuple.getItem2())
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
        A model;
        try {
            model = classObject.newInstance();
        }
        catch (Exception e) {
            throw DatabaseException.serialization(
                    new SerializationError(classObject.getName()));
        }
        return model;
    }


    // > Tables
    // ------------------------------------------------------------------------------------------

    public static void createSchema(List<Class<? extends Model>> modelClasses,
                                    SQLiteDatabase database)
                  throws DatabaseException
    {

        Map<String,List<Tuple2<String,String>>> childrenToParents
                                = childToParentRelations(modelClasses);

        for (Class<? extends Model> modelClass : modelClasses)
        {
            String tableName = ModelLib.name(modelClass);
            List<Tuple2<String,String>> parentRelations = childrenToParents.get(tableName);
            if (parentRelations == null)
                parentRelations = new ArrayList<>();

            String createTableQueryString = defineTableSQLString(modelClass, parentRelations);
            database.execSQL(createTableQueryString);
        }

    }


    private static Map<String,List<Tuple2<String,String>>>
                        childToParentRelations(List<Class<? extends Model>> modelClasses)
                                     throws DatabaseException
    {
        Map<String,List<Tuple2<String,String>>> relationMap = new HashMap<>();

        for (Class<? extends Model> modelClass : modelClasses)
        {
            Model dummyModel = ModelLib.newModel(modelClass);

            Tuple3<List<PrimitiveFunctor<?>>,
                    List<ModelFunctor<?>>,
                    List<CollectionFunctor<?>>> modelValuesTuple = ModelLib.modelValues(dummyModel);

            List<CollectionFunctor<?>> collectionValues = modelValuesTuple.getItem3();

            String parentName = ModelLib.name(modelClass);

            for (CollectionFunctor<?> collectionValue : collectionValues)
            {
                String collectionName = collectionValue.name();

                for (Class<?> collectionValueModelClass : collectionValue.getModelClasses())
                {
                    String childModelName = ModelLib.name(collectionValueModelClass);

                    if (!relationMap.containsKey(childModelName))
                        relationMap.put(childModelName, new ArrayList<Tuple2<String, String>>());

                    List<Tuple2<String,String>> parents = relationMap.get(childModelName);
                    parents.add(new Tuple2<>(parentName, collectionName));
                    relationMap.put(childModelName, parents);
                }
            }
        }

        return relationMap;
    }


    private static <A extends Model> String defineTableSQLString(
                                                    Class<A> modelClass,
                                                    List<Tuple2<String,String>> parentRelations)
                                    throws DatabaseException
    {
        StringBuilder tableBuilder = new StringBuilder();

        // [1] Create Table
        // --------------------------------------------------------------------------------------
        tableBuilder.append("CREATE TABLE IF NOT EXISTS ");
        tableBuilder.append(ModelLib.name(modelClass));
        tableBuilder.append(" ( ");


        // [2] Column Definitions
        // --------------------------------------------------------------------------------------

        // > Get Model values

        Model dummyModel = ModelLib.newModel(modelClass);

        Tuple3<List<PrimitiveFunctor<?>>,
                List<ModelFunctor<?>>,
                List<CollectionFunctor<?>>> modelValuesTuple = ModelLib.modelValues(dummyModel);

        List<PrimitiveFunctor<?>> primitiveValues = modelValuesTuple.getItem1();
        List<ModelFunctor<?>>     modelValues     = modelValuesTuple.getItem2();

        // ** Model Id
        tableBuilder.append("_id");
        tableBuilder.append(" ");
        tableBuilder.append(SQLValue.Type.TEXT.name().toUpperCase());
        tableBuilder.append(" PRIMARY KEY");

        // ** Primitive Values
        for (PrimitiveFunctor<?> primitiveValue : primitiveValues)
        {
            String        columnName = primitiveValue.sqlColumnName();
            SQLValue.Type columnType = primitiveValue.sqlType();

            tableBuilder.append(", ");
            tableBuilder.append(columnName);
            tableBuilder.append(" ");
            tableBuilder.append(columnType.name().toUpperCase());
        }

        // ** Model Values
        for (ModelFunctor<?> modelValue : modelValues)
        {
            String columnName = modelValue.sqlColumnName();

            tableBuilder.append(", ");
            tableBuilder.append(columnName);
            tableBuilder.append(" ");
            tableBuilder.append(SQLValue.Type.TEXT.name().toUpperCase());
        }


        // ** Collection Value parents
        for (Tuple2<String,String> parentInfo : parentRelations)
        {
            String parentName     = parentInfo.getItem1();
            String collectionName = parentInfo.getItem2();

            String columnName = "parent_" + collectionName + "_" + parentName + "_id";

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



    private static boolean valuesAreLoaded(List<ModelFunctor<?>> modelValues,
                                           List<CollectionFunctor<?>> collectionValues)
    {
        boolean allLoaded = true;

        for (ModelFunctor modelValue : modelValues)
        {
            if (modelValue.isNull() && !modelValue.getIsSaved())
                continue;

            if (!modelValue.getIsLoaded())
            {
                allLoaded = false;
                break;
            }
        }

        if (!allLoaded) return false;

        for (CollectionFunctor collectionValue : collectionValues)
        {
            if (!collectionValue.getIsLoaded()) {
                allLoaded = false;
                break;
            }
        }

        return allLoaded;
    }


    private static boolean valuesAreSaved(List<ModelFunctor<?>> modelValues,
                                          List<CollectionFunctor<?>> collectionValues)
    {
        boolean allSaved = true;

        for (ModelFunctor modelValue : modelValues) {
            if (!modelValue.getIsSaved()) {
                allSaved = false;
                break;
            }
        }

        if (!allSaved) return false;

        for (CollectionFunctor collectionValue : collectionValues) {
            if (!collectionValue.getIsSaved()) {
                allSaved = false;
                break;
            }
        }

        return allSaved;
    }


    private static <A extends Model> ResultRow runLoadQuery(Class<A> modelClass,
                                                            ModelQueryParameters queryParameters)
                                     throws DatabaseException
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


    private static void runSaveQuery(Model model, List<OneToManyRelation> parentRelations)
                   throws DatabaseException
    {
        // [A 1] Group values by type
        // --------------------------------------------------------------------------------------

        Tuple3<List<PrimitiveFunctor<?>>,
                List<ModelFunctor<?>>,
                List<CollectionFunctor<?>>> modelValuesTuple = ModelLib.modelValues(model);

        final List<PrimitiveFunctor<?>>  primitiveValues  = modelValuesTuple.getItem1();
        final List<ModelFunctor<?>>      modelValues      = modelValuesTuple.getItem2();


        // [B 1] Save Model row
        // --------------------------------------------------------------------------------------

        // > Save each column value into a ContentValues
        ContentValues row = new ContentValues();

        // ** Save the model values
        row.put("_id", model.getId().toString());

        // ** Save all of the primitive values
        for (PrimitiveFunctor primitiveValue : primitiveValues)
        {
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


        // [B 2] Save all child models in the row by foreign key
        // --------------------------------------------------------------------------------------

        for (ModelFunctor<? extends Model> modelValue : modelValues)
        {
            String columnName = modelValue.sqlColumnName();

            if (modelValue.isNull())
                row.putNull(columnName);
            else
                row.put(columnName, modelValue.getValue().getId().toString());
        }


        // [B 3] Save all parent models in the row by foreign key
        // --------------------------------------------------------------------------------------

        for (OneToManyRelation parentRelation : parentRelations)
        {
            row.put(parentRelation.childSQLColumnName(),
                    parentRelation.getParentId().toString());
        }


        // > Save the row, creating a new one if necessary.
        UpsertQuery upsertQuery = new UpsertQuery(ModelLib.name(model), model.getId(), row);
        upsertQuery.run();
    }



    // LISTENERS
    // ------------------------------------------------------------------------------------------

    private interface OnModelLoadListener<A> {
        void onModelLoad(A model);
        void onModelLoadDBError(DatabaseException exception);
        void onModelLoadError(Exception exception);
    }

    private interface OnModelSaveListener {
        void onModelSave();
        void onModelSaveDBError(DatabaseException exception);
        void onModelSaveError(Exception exception);
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
