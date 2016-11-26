
package com.kispoko.tome.util.model;


import android.content.ContentValues;
import android.util.Log;

import com.kispoko.tome.ApplicationFailure;
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
 * Modeler
 */
public class Modeler
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
        return Modeler.name(model.getClass());
    }


    // Serialization
    // ------------------------------------------------------------------------------------------

    /*
    public static <A extends Model> ValuePromise<A>
                                modelValuePromise(final Class<A> classObject,
                                                  final ModelQueryParameters queryParameters)
    {
        return new ValuePromise<>(new ValuePromise.Action<A>() {
            @Override
            public A run() {
                A loadedValue = null;
                try {
                    loadedValue = Modeler.fromDatabase(classObject, queryParameters);
                } catch (DatabaseException e) {
                    e.printStackTrace();
                }
                return loadedValue;
            }
        });
    }


    public static <A extends Model> CollectionValuePromise<A>
                                collectionValuePromise(final String parentModelName,
                                                       final UUID parentModelId,
                                                       final List<Class<? extends A>> modelClasses)
    {
        return new CollectionValuePromise<>(new CollectionValuePromise.Action<A>() {
            @Override
            public List<A> run() {
                List<A> loadedCollection = null;
                try {
                    loadedCollection = Modeler.collectionFromDatabase(parentModelName,
                                                                      parentModelId,
                                                                      modelClasses);
                } catch (DatabaseException e) {
                    e.printStackTrace();
                }
                return loadedCollection;
            }
        });
    }


    public static SaveValuePromise saveValuePromise(final Model model)
    {
        return new SaveValuePromise(new SaveValuePromise.Action() {
            @Override
            public void run() {
                try {
                    Modeler.toDatabase(model);
                } catch (DatabaseException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public static SaveValuePromise saveCollectionValuePromise(final List<? extends Model> models)
    {
        return new SaveValuePromise(new SaveValuePromise.Action() {
            @Override
            public void run() {
                try {
                    for (Model model : models) {
                        Modeler.toDatabase(model);
                    }
                }
                catch (DatabaseException e) {
                    e.printStackTrace();
                }
            }
        });
    }
*/

    // INTERNAL
    // --------------------------------------------------------------------------------------

    /**
     * Automatically load this model from the database using reflection on its Value properties
     * and the database data stored within them.
     * @return A new instance of the moddel.
     * @throws DatabaseException
     */
    @SuppressWarnings("unchecked")
    public static <A extends Model> A fromDatabase(Class<A> classObject,
                                                    ModelQueryParameters queryParameters)
                                     throws DatabaseException
    {
        // GET SQL columns
        A dummyModel = Modeler.newModel(classObject);
        List<Tuple2<String,SQLValue.Type>> sqlColumns = Modeler.sqlColumns(dummyModel);

        // RUN the query
        ModelQuery modelQuery = new ModelQuery(Modeler.name(classObject),
                                               sqlColumns,
                                               queryParameters);
        ResultRow row = modelQuery.result();

        return Modeler.modelFromRow(classObject, row);
    }


    /**
     * @return A new instance of the moddel.
     * @throws DatabaseException
     */
    @SuppressWarnings("unchecked")
    public static <A extends Model> List<A> collectionFromDatabase(
                                                    String parentModelName,
                                                    UUID parentModelId,
                                                    List<Class<? extends A>> modelClasses)
                       throws DatabaseException
    {
        List<A> models = new ArrayList<>();

        // For each concrete model type, query all of the matching models
        for (Class<? extends A> modelClass : modelClasses)
        {
            // GET SQL columns
            A dummyModel = Modeler.newModel(modelClass);
            List<Tuple2<String,SQLValue.Type>> sqlColumns = Modeler.sqlColumns(dummyModel);

            // RUN the query
            CollectionQuery collectionQuery = new CollectionQuery(Modeler.name(modelClass),
                                                                  parentModelName,
                                                                  parentModelId,
                                                                  sqlColumns);
            List<ResultRow> resultRows = collectionQuery.result();

            // FOR EACH row, add a model to the collection
            for (ResultRow row : resultRows) {
                models.add(Modeler.modelFromRow(modelClass, row));
            }
        }

        return models;
    }


    /**
     * Save the model to the database.
     * @throws DatabaseException
     */
    public static void toDatabase(Model model)
                  throws DatabaseException
    {

        // [A 1] Group values by type
        // --------------------------------------------------------------------------------------

        Tuple3<List<PrimitiveValue<?>>,
               List<ModelValue<?>>,
               List<CollectionValue<?>>> modelValuesTuple = Modeler.modelValues(model);

        List<PrimitiveValue<?>>  primitiveValues  = modelValuesTuple.getItem1();
        List<ModelValue<?>>      modelValues      = modelValuesTuple.getItem2();
        List<CollectionValue<?>> collectionValues = modelValuesTuple.getItem3();

        // [B 1] Save Modeler row
        // --------------------------------------------------------------------------------------

        // > Save each column value into a ContentValues
        ContentValues row = new ContentValues();

        // ** Save the model values
        row.put("_id", model.getId().toString());

        // ** Save all of the primitive values
        for (PrimitiveValue primitiveValue : primitiveValues)
        {
            SQLValue         sqlValue         = primitiveValue.toSQLValue();
            String           columnName       = primitiveValue.sqlColumnName();

            switch (sqlValue.getType())
            {
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
        for (ModelValue<? extends Model> modelValue : modelValues)
        {
            String columnName = modelValue.sqlColumnName();
            if (!modelValue.isNull())
                row.put(columnName, modelValue.getValue().getId().toString());
            else
                row.putNull(columnName);
        }

        // > Save the row, creating a new one if necessary.
        UpsertQuery upsertQuery = new UpsertQuery(Modeler.name(model), model.getId(), row);
        upsertQuery.run();

        // [B 2] Save Shared Value Rows
        // --------------------------------------------------------------------------------------

        for (ModelValue<? extends Model> modelValue : modelValues)
        {
            if (!modelValue.isNull())
            {
                modelValue.save(new ModelValue.OnSaveListener() {
                    @Override
                    public void onSave() {
                    }

                    @Override
                    public void onSaveError(DatabaseException exception) {
                        ApplicationFailure.database(exception);
                    }
                });
            }
        }

        // [B 3] Save Collection Values
        // --------------------------------------------------------------------------------------

        for (CollectionValue<? extends Model> collectionValue : collectionValues)
        {
            collectionValue.save(new CollectionValue.OnSaveListener() {
                @Override
                public void onSave() { }

                @Override
                public void onSaveError(DatabaseException exception) {
                    ApplicationFailure.database(exception);
                }
            });
        }


    }


    /**
     * Notes: primitive values are always loaded if model value is updated.
     * @param classObject
     * @param row
     * @param <A>
     * @return
     * @throws DatabaseException
     */
    @SuppressWarnings("unchecked")
    private static <A extends Model> A modelFromRow(Class<A> classObject, ResultRow row)
                                     throws DatabaseException
    {
        // [A 1] Create Modeler
        // --------------------------------------------------------------------------------------
        A model = Modeler.newModel(classObject);

        // [A 2] Get the Modeler's Values
        // --------------------------------------------------------------------------------------
        Tuple3<List<PrimitiveValue<?>>,
               List<ModelValue<?>>,
               List<CollectionValue<?>>> modelValuesTuple = Modeler.modelValues(model);

        List<PrimitiveValue<?>>  primitiveValues  = modelValuesTuple.getItem1();
        List<ModelValue<?>>      modelValues      = modelValuesTuple.getItem2();
        List<CollectionValue<?>> collectionValues = modelValuesTuple.getItem3();

        // [B 1] Evaluate model values
        // --------------------------------------------------------------------------------------

        SQLValue idSqlValue   = row.getSQLValue("_id");
        model.setId(UUID.fromString(idSqlValue.getText()));

        // [B 2] Evaluate primitive model values
        // --------------------------------------------------------------------------------------

        for (PrimitiveValue<?> primitiveValue : primitiveValues)
        {
            String columnName = primitiveValue.sqlColumnName();
            primitiveValue.fromSQLValue(row.getSQLValue(columnName));
        }

        // [B 3] Evaluate model values (many-to-one values)
        // --------------------------------------------------------------------------------------

        for (ModelValue<?> modelValue : modelValues)
        {
            String columnName = modelValue.sqlColumnName();

            UUID modelValueId = UUID.fromString(row.getSQLValue(columnName).getText());
            // Unchecked assignment. Type errors here didn't make sense, but the compiler doesn't
            // know that the class in modelValue.getModelClass should be over the same type A as
            // the ModelValue parameter type A. Though I think it should know that. Really not sure
            // here but it should work, so not worth the time.
            ModelQueryParameters queryParameters =
                    new ModelQueryParameters(new ModelQueryParameters.PrimaryKey(modelValueId),
                                             ModelQueryParameters.Type.PRIMARY_KEY);

            modelValue.load(queryParameters);
        }

        // [B 2] Evaluate collection values (one-to-many values)
        // --------------------------------------------------------------------------------------

        for (CollectionValue<? extends Model> collectionValue : collectionValues)
        {
//            CollectionValuePromise<? extends Model> collectionValuePromise =
//                    Modeler.collectionValuePromise(
//                            Modeler.name(model),
//                            model.getId(),
//                            // TODO might be wrong...
//                            collectionValue.getModelClasses());
//                            //(List<Class<? extends Model>>) collectionValue.getModelClasses());
            collectionValue.load(Modeler.name(model), model.getId());
        }

        return model;
    }


    /**
     * Collect all of the Modeler's values and return them in data structures suitable for
     * further analysis.
     * @param model A Modeler instance to get the values of.
     * @param <A> The model type.
     * @return The Modeler's values, sorted.
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
                   List<CollectionValue<?>>> modelValuesTuple = Modeler.modelValues(model);

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
     * Create a new Modeler instance of the provided class.
     * @param classObject The Modeler class to create.
     * @param <A> The type of Modeler.
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
        tableBuilder.append(Modeler.name(modelClass));
        tableBuilder.append(" ( ");

        // [2] ColumnUnion Definitions
        // --------------------------------------------------------------------------------------

        // > Get Modeler values

        Model dummyModel = Modeler.newModel(modelClass);

        Tuple3<List<PrimitiveValue<?>>,
                List<ModelValue<?>>,
                List<CollectionValue<?>>> modelValuesTuple = Modeler.modelValues(dummyModel);

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

}
