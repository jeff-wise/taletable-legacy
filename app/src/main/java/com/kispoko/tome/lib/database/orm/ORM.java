
package com.kispoko.tome.lib.database.orm;


import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.kispoko.tome.Global;
import com.kispoko.tome.lib.database.DatabaseException;
import com.kispoko.tome.lib.database.EventLog;
import com.kispoko.tome.lib.database.SQL;
import com.kispoko.tome.lib.database.error.FunctorError;
import com.kispoko.tome.lib.database.error.NullModelIdentifierError;
import com.kispoko.tome.lib.database.error.SerializationError;
import com.kispoko.tome.lib.database.query.CollectionQuery;
import com.kispoko.tome.lib.database.query.ModelQuery;
import com.kispoko.tome.lib.database.query.ModelQueryParameters;
import com.kispoko.tome.lib.database.query.ResultRow;
import com.kispoko.tome.lib.database.query.UpsertQuery;
import com.kispoko.tome.lib.database.sql.OneToManyRelation;
import com.kispoko.tome.lib.database.sql.SQLValue;
import com.kispoko.tome.lib.functor.FunctorException;
import com.kispoko.tome.lib.functor.OptionFunctor;
import com.kispoko.tome.lib.model.Model;
import com.kispoko.tome.util.Util;
import com.kispoko.tome.util.tuple.Tuple2;
import com.kispoko.tome.lib.functor.CollectionFunctor;
import com.kispoko.tome.lib.functor.ModelFunctor;
import com.kispoko.tome.lib.functor.PrimitiveFunctor;
import com.kispoko.tome.util.tuple.Tuple4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;



/**
 * ModelLib
 */
public class ORM
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
        return ORM.name(model.getClass());
    }


    /**
     * Automatically load this model from the database using reflection on its Value properties
     * and the database data stored within them.
     * @return A new instance of the moddel.
     * @throws DatabaseException
     */
    public static <A extends Model> A loadModel(final Class<A> modelClass,
                                                final ModelQueryParameters queryParameters)
                  throws DatabaseException
    {
        ResultRow row = runLoadQuery(modelClass, queryParameters);

        A model = ORM.resultRowToModel(modelClass, row);

        return model;
    }


    /**
     * @return A new instance of the moddel.
     * @throws DatabaseException
     */
    @SuppressWarnings("unchecked")
    public static <A extends Model> List<A> loadModelCollection(final Class<A> modelClass,
                                                                final OneToManyRelation relation)
                  throws DatabaseException
    {

        // [1] Query the collection rows
        // -------------------------------------------------------------------------------------

        List<ResultRow> resultRows = ORM.runLoadCollectionQuery(modelClass, relation);

        // [2] Create the models from the result rows
        // -------------------------------------------------------------------------------------

        final List<A> models = new ArrayList<>();

        for (ResultRow resultRow : resultRows)
        {
            A rowModel = ORM.resultRowToModel(modelClass, resultRow);
            models.add(rowModel);
        }

        return models;
    }


    /**
     * Save a model (and all its children) to the database.
     * @param model The model.
     * @throws DatabaseException
     */
    public static void saveModel(Model model)
                  throws DatabaseException
    {
        ORM.saveModel(model, false);
    }


    /**
     * Save a model (and all its children) to the database.
     * @param model The model.
     * @param isTransaction True if all the DB operations in involved in saving the model should
     *                      be run in a single transaction. This should always be true for a
     *                      top-level call to obtain reasonable performance.
     * @throws DatabaseException
     */
    public static void saveModel(Model model, boolean isTransaction)
                  throws DatabaseException
    {
        Long startTime = System.nanoTime();

        try
        {
            if (isTransaction)
                Global.getDatabase().beginTransaction();

            // [1] Save the model's row
            // -------------------------------------------------------------------------------------

            insertModelRow(model, new ArrayList<OneToManyRelation>());

            // [2] Save the model's child model rows
            // -------------------------------------------------------------------------------------

            saveModelChildRows(model);

            if (isTransaction)
                Global.getDatabase().setTransactionSuccessful();

            Long endTime = System.nanoTime();

            String timeString = Util.timeDifferenceString(startTime, endTime);

            String eventMessage = "Model: " + model.getClass().getName() + "  " +
                                  "Id: " + model.getId().toString() + "  " +
                                  timeString + " ms";

            EventLog.add(EventLog.EventType.MODEL_SAVE, eventMessage);
        }
        finally
        {
            if (isTransaction)
                Global.getDatabase().endTransaction();
        }
    }


    /**
     * Save a model (and all its children) to the database with the given one-to-many relations.
     * @throws DatabaseException
     */
    public static void saveModel(final Model model, List<OneToManyRelation> parentRelations)
                  throws DatabaseException
    {
        // [1] Save the model's row
        // -------------------------------------------------------------------------------------

        insertModelRow(model, parentRelations);

        // [2] Save the model's child model rows
        // -------------------------------------------------------------------------------------

        saveModelChildRows(model);
    }


    /**
     * Save the model to the database.
     * @throws DatabaseException
     */
    private static void saveModelChildRows(Model model)
                   throws DatabaseException
    {
        // [1] Group values by type
        // --------------------------------------------------------------------------------------

        Tuple4<List<PrimitiveFunctor<?>>,
               List<OptionFunctor<?>>,
               List<ModelFunctor<?>>,
               List<CollectionFunctor<?>>> functorsTuple;

//        try {
//            functorsTuple = Model.propertyFunctors(model);
//        }
//        catch (FunctorException exception) {
//            throw DatabaseException.functor(new FunctorError(exception));
//        }
//
//        final List<ModelFunctor<?>>      modelFunctors      = functorsTuple.getItem3();
//        final List<CollectionFunctor<?>> collectionFunctors = functorsTuple.getItem4();
//
//
//        // [2] Save Shared Value Rows
//        // --------------------------------------------------------------------------------------
//
//        for (final ModelFunctor<? extends Model> modelFunctor : modelFunctors)
//        {
//            modelFunctor.save();
//        }
//
//
//        // [B 3] Save Collection Values
//        // --------------------------------------------------------------------------------------
//
//        for (final CollectionFunctor<? extends Model> collectionFunctor : collectionFunctors)
//        {
//            List<OneToManyRelation> parentRelations = new ArrayList<>();
//            parentRelations.add(new OneToManyRelation(ORM.name(model),
//                                                      collectionFunctor.name(),
//                                                      model.getId()));
//            collectionFunctor.save(parentRelations);
//        }
    }


    /**
     * Create a model from its Result Row.
     * @return The loaded Model.
     * @throws DatabaseException
     */
    private static <A extends Model> A resultRowToModel(Class<A> modelClass, ResultRow row)
                   throws DatabaseException
    {
        // [A 1] Get the Model's Values
        // ----------------------------------------------------------------------------------

        final A model = ORM.newModel(modelClass);


        // [A 2] Get the Model's Values
        // ----------------------------------------------------------------------------------

        Tuple4<List<PrimitiveFunctor<?>>,
               List<OptionFunctor<?>>,
               List<ModelFunctor<?>>,
               List<CollectionFunctor<?>>> functorsTuple;

//        try {
//            functorsTuple = Model.propertyFunctors(model);
//        }
//        catch (FunctorException exception) {
//            throw DatabaseException.functor(new FunctorError(exception));
//        }
//
//        List<PrimitiveFunctor<?>>  primitiveFunctors  = functorsTuple.getItem1();
//        List<OptionFunctor<?>>     optionFunctors     = functorsTuple.getItem2();
//        List<ModelFunctor<?>>      modelFunctors      = functorsTuple.getItem3();
//        List<CollectionFunctor<?>> collectionFunctors = functorsTuple.getItem4();
//
//
//        // [B 1] Evaluate model values
//        // ----------------------------------------------------------------------------------
//
//        SQLValue idSqlValue = row.getSQLValue("_id");
//        model.setId(UUID.fromString(idSqlValue.getText()));
//
//
//        // [B 2] Evaluate primitive model values
//        // ----------------------------------------------------------------------------------
//
//        for (PrimitiveFunctor<?> primitiveFunctor : primitiveFunctors)
//        {
//            String columnName = primitiveFunctor.sqlColumnName();
//            primitiveFunctor.fromSQLValue(row.getSQLValue(columnName));
//        }
//
//        // [B 3] Evaluate option functors
//        // ----------------------------------------------------------------------------------
//
//        for (OptionFunctor<?> optionFunctor : optionFunctors)
//        {
//            String columnName = optionFunctor.sqlColumnName();
//            optionFunctor.fromSQLValue(row.getSQLValue(columnName));
//        }
//
//        // [B 4] Evaluate model functors
//        // ----------------------------------------------------------------------------------
//
//        for (final ModelFunctor<?> modelFunctor : modelFunctors)
//        {
//            String modelForeignKeyColumnName = modelFunctor.sqlColumnName();
//            SQLValue modelIdSqlValue = row.getSQLValue(modelForeignKeyColumnName);
//
//            // If the model does not exist at this time (the foreign key is NULL)
//            if (modelIdSqlValue.isNull())
//                continue;
//
//            UUID modelValueId = UUID.fromString(modelIdSqlValue.getText());
//            ModelQueryParameters queryParameters =
//                    new ModelQueryParameters(new ModelQueryParameters.PrimaryKey(modelValueId),
//                                             ModelQueryParameters.Type.PRIMARY_KEY);
//
//            modelFunctor.load(queryParameters);
//        }
//
//
//        // [B 2] Evaluate collection values (one-to-many values)
//        // ----------------------------------------------------------------------------------
//
//        for (final CollectionFunctor<? extends Model> collectionFunctor : collectionFunctors)
//        {
//            collectionFunctor.load(model);
//        }


        return model;
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
        Tuple4<List<PrimitiveFunctor<?>>,
               List<OptionFunctor<?>>,
               List<ModelFunctor<?>>,
               List<CollectionFunctor<?>>> functorsTuple;

//        try {
//            functorsTuple = Model.propertyFunctors(model);
//        }
//        catch (FunctorException exception) {
//            throw DatabaseException.functor(new FunctorError(exception));
//        }

        // Get all of model's database columns. Both primitive values and model values have
        // column representations
        List<Tuple2<String,SQLValue.Type>> columns = new ArrayList<>();

        // > Add MODEL columns
        columns.add(new Tuple2<>("_id", SQLValue.Type.TEXT));

        // > Add PRIMITIVE VALUE columns
//        for (PrimitiveFunctor<?> primitiveValue : functorsTuple.getItem1())
//        {
//            columns.add(new Tuple2<>(primitiveValue.sqlColumnName(),
//                                     primitiveValue.sqlType()));
//        }

//        // > Add OPTION VALUE columns
//        for (OptionFunctor<?> optionFunctor : functorsTuple.getItem2())
//        {
//            columns.add(new Tuple2<>(optionFunctor.sqlColumnName(),
//                                     SQLValue.Type.TEXT));
//        }
//
//        // > Add MODEL VALUE columns
//        for (ModelFunctor<?> modelFunctor : functorsTuple.getItem3())
//        {
//            columns.add(new Tuple2<>(modelFunctor.sqlColumnName(),
//                                     SQLValue.Type.TEXT));
//        }

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
                    new SerializationError(classObject.getName(), e));
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
            String tableName = ORM.name(modelClass);
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

//        for (Class<? extends Model> modelClass : modelClasses)
//        {
//            Model dummyModel = ORM.newModel(modelClass);
//
//            Tuple4<List<PrimitiveFunctor<?>>,
//                   List<OptionFunctor<?>>,
//                   List<ModelFunctor<?>>,
//                   List<CollectionFunctor<?>>> functorsTuple;

//            try {
//                functorsTuple = Model.propertyFunctors(dummyModel);
//            }
//            catch (FunctorException exception) {
//                throw DatabaseException.functor(new FunctorError(exception));
//            }

//            List<CollectionFunctor<?>> collectionValues = functorsTuple.getItem4();

            //String parentName = ORM.name(modelClass);

//            for (CollectionFunctor<?> collectionFunctor : collectionValues)
//            {
//                String collectionName = collectionFunctor.name();
//
//                String childModelName = ORM.name(collectionFunctor.modelClass());
//
//                if (!relationMap.containsKey(childModelName))
//                    relationMap.put(childModelName, new ArrayList<Tuple2<String, String>>());
//
//                List<Tuple2<String,String>> parents = relationMap.get(childModelName);
//                parents.add(new Tuple2<>(parentName, collectionName));
//                relationMap.put(childModelName, parents);
//            }
//        }

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
        tableBuilder.append(ORM.name(modelClass));
        tableBuilder.append(" ( ");


        // [2] Column Definitions
        // --------------------------------------------------------------------------------------

        // > Get Model values

        Model dummyModel = ORM.newModel(modelClass);

        Tuple4<List<PrimitiveFunctor<?>>,
               List<OptionFunctor<?>>,
               List<ModelFunctor<?>>,
               List<CollectionFunctor<?>>> functorsTuple;

//        try {
//            functorsTuple = Model.propertyFunctors(dummyModel);
//        }
//        catch (FunctorException exception) {
//            throw DatabaseException.functor(new FunctorError(exception));
//        }

//        List<PrimitiveFunctor<?>> primitiveFunctors = functorsTuple.getItem1();
//        List<OptionFunctor<?>>    optionFunctors    = functorsTuple.getItem2();
//        List<ModelFunctor<?>>     modelFunctors     = functorsTuple.getItem3();
//
//        // ** Model Id
//        tableBuilder.append("_id");
//        tableBuilder.append(" ");
//        tableBuilder.append(SQLValue.Type.TEXT.name().toUpperCase());
//        tableBuilder.append(" PRIMARY KEY");
//
//        // ** Primitive Values
//        for (PrimitiveFunctor<?> primitiveFunctor : primitiveFunctors)
//        {
//            String        columnName = primitiveFunctor.sqlColumnName();
//            SQLValue.Type columnType = primitiveFunctor.sqlType();
//
//            tableBuilder.append(", ");
//            tableBuilder.append(columnName);
//            tableBuilder.append(" ");
//            tableBuilder.append(columnType.name().toUpperCase());
//        }
//
//        // ** Option Values
//        for (OptionFunctor<?> optionFunctor : optionFunctors)
//        {
//            String        columnName = optionFunctor.sqlColumnName();
//            SQLValue.Type columnType = SQLValue.Type.TEXT;
//
//            tableBuilder.append(", ");
//            tableBuilder.append(columnName);
//            tableBuilder.append(" ");
//            tableBuilder.append(columnType.name().toUpperCase());
//        }
//
//        // ** Model Values
//        for (ModelFunctor<?> modelFunctor : modelFunctors)
//        {
//            String columnName = modelFunctor.sqlColumnName();
//
//            tableBuilder.append(", ");
//            tableBuilder.append(columnName);
//            tableBuilder.append(" ");
//            tableBuilder.append(SQLValue.Type.TEXT.name().toUpperCase());
//        }
//
//
//        // ** Collection Value parents
//        for (Tuple2<String,String> parentInfo : parentRelations)
//        {
//            String parentName     = parentInfo.getItem1();
//            String collectionName = parentInfo.getItem2();
//
//            String columnName = "parent_" + collectionName + "_" + parentName + "_id";
//
//            tableBuilder.append(", ");
//            tableBuilder.append(columnName);
//            tableBuilder.append(" ");
//            tableBuilder.append(SQLValue.Type.TEXT.name().toUpperCase());
//        }

        // [2] End
        // --------------------------------------------------------------------------------------

        tableBuilder.append(" )");

        return tableBuilder.toString();
    }


    private static <A extends Model> ResultRow runLoadQuery(Class<A> modelClass,
                                                            ModelQueryParameters queryParameters)
                                     throws DatabaseException
    {
        // [1] GET SQL columns
        A dummyModel = ORM.newModel(modelClass);
        List<Tuple2<String, SQLValue.Type>> sqlColumns =
                                        ORM.sqlColumns(dummyModel);

        // [2] RUN the query
        ModelQuery modelQuery = new ModelQuery(ORM.name(modelClass),
                                               sqlColumns,
                                               queryParameters);
        ResultRow row = modelQuery.result();

        return row;
    }


    private static <A extends Model> List<ResultRow> runLoadCollectionQuery(
                                                            Class<A> modelClass,
                                                            OneToManyRelation relation)
                   throws DatabaseException
    {
        // [1] GET SQL columns
        // -------------------------------------------------------------------------------------

        A dummyModel = ORM.newModel(modelClass);
        List<Tuple2<String, SQLValue.Type>> sqlColumns = ORM.sqlColumns(dummyModel);

        // [2] RUN the query
        // -------------------------------------------------------------------------------------

        CollectionQuery collectionQuery = new CollectionQuery(
                                                    ORM.name(modelClass),
                                                    relation,
                                                    sqlColumns);

        List<ResultRow> resultRows = collectionQuery.result();

        return resultRows;
    }


    private static void insertModelRow(Model model, List<OneToManyRelation> parentRelations)
                   throws DatabaseException
    {
        // [A 1] Group values by type
        // --------------------------------------------------------------------------------------

        Tuple4<List<PrimitiveFunctor<?>>,
               List<OptionFunctor<?>>,
               List<ModelFunctor<?>>,
               List<CollectionFunctor<?>>> functorsTuple;

//        try {
//            functorsTuple = Model.propertyFunctors(model);
//        }
//        catch (FunctorException exception) {
//            throw DatabaseException.functor(new FunctorError(exception));
//        }

//        final List<PrimitiveFunctor<?>>  primitiveFunctors = functorsTuple.getItem1();
//        final List<OptionFunctor<?>>     optionFunctors    = functorsTuple.getItem2();
//        final List<ModelFunctor<?>>      modelFunctors     = functorsTuple.getItem3();
//
//
//        // [B 1] Save Model row
//        // --------------------------------------------------------------------------------------
//
//        // > Save each column value into a ContentValues
//        ContentValues row = new ContentValues();
//
//        // ** Save the model values
//        row.put("_id", model.getId().toString());
//
//        // ** Save all of the primitive values
//        for (PrimitiveFunctor primitiveFunctor : primitiveFunctors)
//        {
//            SQLValue sqlValue = primitiveFunctor.toSQLValue();
//            String columnName = primitiveFunctor.sqlColumnName();
//
//            switch (sqlValue.getType()) {
//                case INTEGER:
//                    row.put(columnName, sqlValue.getInteger());
//                    break;
//                case REAL:
//                    row.put(columnName, sqlValue.getReal());
//                    break;
//                case TEXT:
//                    row.put(columnName, sqlValue.getText());
//                    break;
//                case BLOB:
//                    row.put(columnName, sqlValue.getBlob());
//                    break;
//                case NULL:
//                    row.putNull(columnName);
//                    break;
//            }
//        }

//
//        for (OptionFunctor optionFunctor : optionFunctors)
//        {
//            SQLValue sqlValue = optionFunctor.toSQLValue();
//            String columnName = optionFunctor.sqlColumnName();
//
//            switch (sqlValue.getType())
//            {
//                case TEXT:
//                    row.put(columnName, sqlValue.getText());
//                    break;
//                case NULL:
//                    row.putNull(columnName);
//                    break;
//                default:
//                    row.putNull(columnName);
//            }
//        }
//
//
//        // [B 2] Save all child models in the row by foreign key
//        // --------------------------------------------------------------------------------------
//
//        for (ModelFunctor<? extends Model> modelFunctor : modelFunctors)
//        {
//            String columnName = modelFunctor.sqlColumnName();
//
//            if (modelFunctor.isNull())
//            {
//                row.putNull(columnName);
//            }
//            else
//            {
//                UUID modelId = modelFunctor.getValue().getId();
//
//                if (modelId == null) {
//                    throw DatabaseException.nullModelId(
//                            new NullModelIdentifierError(
//                                        modelFunctor.getValue().getClass().getName()));
//                }
//
//                row.put(columnName, modelFunctor.getValue().getId().toString());
//            }
//
//        }
//
//
//        // [B 3] Save all parent models in the row by foreign key
//        // --------------------------------------------------------------------------------------
//
//        for (OneToManyRelation parentRelation : parentRelations)
//        {
//            row.put(parentRelation.childSQLColumnName(),
//                    parentRelation.getParentId().toString());
//        }
//
//        // > Save the row, creating a new one if necessary.
//        String tableNameString = ORM.name(model);
//        UpsertQuery upsertQuery = new UpsertQuery(ORM.name(model), model.getId(), row);
//
//        Long startTime = System.nanoTime();
//
//        upsertQuery.run();
//
//        Long endTime = System.nanoTime();
//
//        String timeString = Util.timeDifferenceString(startTime, endTime);
//        String eventMessage = "Table: " + tableNameString + "  " +
//                              "Id: " + model.getId().toString() + "  " +
//                              timeString + " ms";
//        EventLog.add(EventLog.EventType.ROW_INSERT, eventMessage);
    }


}



