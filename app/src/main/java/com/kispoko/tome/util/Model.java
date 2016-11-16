
package com.kispoko.tome.util;


import android.content.ContentValues;

import com.kispoko.tome.ApplicationFailure;
import com.kispoko.tome.util.database.DatabaseException;
import com.kispoko.tome.util.database.SQL;
import com.kispoko.tome.util.database.query.CollectionQuery;
import com.kispoko.tome.util.database.query.CountQuery;
import com.kispoko.tome.util.database.query.ModelQuery;
import com.kispoko.tome.util.database.query.ModelQueryParameters;
import com.kispoko.tome.util.database.query.ResultRow;
import com.kispoko.tome.util.database.query.UpsertQuery;
import com.kispoko.tome.util.database.sql.SQLValue;
import com.kispoko.tome.util.promise.CollectionValuePromise;
import com.kispoko.tome.util.promise.Promise;
import com.kispoko.tome.util.promise.ValuePromise;
import com.kispoko.tome.util.promise.SaveValuePromise;
import com.kispoko.tome.util.tuple.Tuple2;
import com.kispoko.tome.util.tuple.Tuple3;
import com.kispoko.tome.util.value.CollectionValue;
import com.kispoko.tome.util.value.ModelValue;
import com.kispoko.tome.util.value.PrimitiveValue;
import com.kispoko.tome.util.value.Value;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;



/**
 * Model
 */
public abstract class Model
{

    // ABSTRACT METHODS
    // --------------------------------------------------------------------------------------

    abstract public void onUpdateModel(String name);


    // PROPERTIES
    // --------------------------------------------------------------------------------------

    private UUID   id;


    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------

    public Model()
    {
        this.id   = null;
    }


    public Model(UUID id)
    {
        this.id   = id;
    }


    // API
    // --------------------------------------------------------------------------------------

    // > State
    // --------------------------------------------------------------------------------------

    // ** Id
    // --------------------------------------------------------------------------------------

    public UUID getId()
    {
        return this.id;
    }


    public void setId(UUID id)
    {
        this.id = id;
    }


    // > Helpers
    // ------------------------------------------------------------------------------------------

    public String name()
    {
        return SQL.asValidIdentifier(this.getClass().getName().toLowerCase());
    }


    public static <A> String name(Class<A> modelClass)
    {
        return SQL.asValidIdentifier(modelClass.getName().toLowerCase());
    }


    // > Serialization
    // ------------------------------------------------------------------------------------------

    public static <A extends Model> Promise<Integer> modelCountPromise(final Class<A> modelClass)
    {
        return new Promise<>(new Promise.Action<Integer>() {
            @Override
            public Integer run() {
                Integer count = null;
                try {
                    String tableName = Model.name(modelClass);
                    count = Model.count(tableName);
                } catch (DatabaseException e) {
                    ApplicationFailure.database(e);
                }

                return count;
            }
        });
    }


    public static <A extends Model> ValuePromise<A>
                                modelValuePromise(final Class<A> classObject,
                                                  final ModelQueryParameters queryParameters)
    {
        return new ValuePromise<>(new ValuePromise.Action<A>() {
            @Override
            public A run() {
                A loadedValue = null;
                try {
                    loadedValue = Model.fromDatabase(classObject, queryParameters);
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
                                                               final Class<A> classObject)
    {
        return new CollectionValuePromise<>(new CollectionValuePromise.Action<A>() {
            @Override
            public List<A> run() {
                List<A> loadedCollection = null;
                try {
                    loadedCollection = Model.collectionFromDatabase(parentModelName,
                                                                    parentModelId,
                                                                    classObject);
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
                    model.toDatabase();
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
                        model.toDatabase();
                    }
                }
                catch (DatabaseException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    // INTERNAL
    // --------------------------------------------------------------------------------------

    private static Integer count(String tableName)
                   throws DatabaseException
    {
        CountQuery countQuery = new CountQuery(tableName);
        return countQuery.run();
    }

    /**
     * Automatically load this model from the database using reflection on its Value properties
     * and the database data stored within them.
     * @return A new instance of the moddel.
     * @throws DatabaseException
     */
    @SuppressWarnings("unchecked")
    private static <A extends Model> A fromDatabase(Class<A> classObject,
                                                    ModelQueryParameters queryParameters)
                                     throws DatabaseException
    {
        // GET SQL columns
        A dummyModel = Model.newModel(classObject);
        List<Tuple2<String,SQLValue.Type>> sqlColumns = Model.sqlColumns(dummyModel);

        // RUN the query
        ModelQuery modelQuery = new ModelQuery(Model.name(classObject),
                                               sqlColumns,
                                               queryParameters);
        ResultRow row = modelQuery.result();

        return Model.modelFromRow(classObject, row);
    }


    /**
     * @return A new instance of the moddel.
     * @throws DatabaseException
     */
    @SuppressWarnings("unchecked")
    private static <A extends Model> List<A> collectionFromDatabase(String parentModelName,
                                                                    UUID parentModelId,
                                                                    Class<A> classObject)
                       throws DatabaseException
    {
        // GET SQL columns
        A dummyModel = Model.newModel(classObject);
        List<Tuple2<String,SQLValue.Type>> sqlColumns = Model.sqlColumns(dummyModel);

        // RUN the query
        CollectionQuery collectionQuery = new CollectionQuery(Model.name(classObject),
                                                              parentModelName,
                                                              parentModelId,
                                                              sqlColumns);
        List<ResultRow> resultRows = collectionQuery.result();

        // FOR EACH row, add a model to the collection
        List<A> models = new ArrayList<>();
        for (ResultRow row : resultRows) {
            models.add(Model.modelFromRow(classObject, row));
        }

        return models;
    }


    /**
     * Save the model to the database.
     * @throws DatabaseException
     */
    private void toDatabase()
           throws DatabaseException
    {

        // [A 1] Group values by type
        // --------------------------------------------------------------------------------------

        Tuple3<List<PrimitiveValue<?>>,
               List<ModelValue<?>>,
               List<CollectionValue<?>>> modelValuesTuple = Model.modelValues(this);

        List<PrimitiveValue<?>>  primitiveValues  = modelValuesTuple.getItem1();
        List<ModelValue<?>>      modelValues      = modelValuesTuple.getItem2();
        List<CollectionValue<?>> collectionValues = modelValuesTuple.getItem3();

        // [B 1] Save Model row
        // --------------------------------------------------------------------------------------

        // > Save each column value into a ContentValues
        ContentValues row = new ContentValues();

        // ** Save the model values
        row.put("_id", this.getId().toString());

        // ** Save all of the primitive values
        for (PrimitiveValue primitiveValue : primitiveValues)
        {
            SQLValue         sqlValue         = primitiveValue.toSQLValue();
            String           columnName       = primitiveValue.getColumnName();

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
            row.put(columnName, modelValue.getValue().getId().toString());
        }

        // > Save the row, creating a new one if necessary.
        UpsertQuery upsertQuery = new UpsertQuery(this.name(), this.id, row);
        upsertQuery.run();

        // [B 2] Save Shared Value Rows
        // --------------------------------------------------------------------------------------

        for (ModelValue<? extends Model> modelValue : modelValues)
        {
            modelValue.saveValue(Model.saveValuePromise(modelValue.getValue()));
        }

        // [B 3] Save Collection Values
        // --------------------------------------------------------------------------------------

        for (CollectionValue<? extends Model> collectionValue : collectionValues)
        {
            collectionValue.saveValue(
                    Model.saveCollectionValuePromise(collectionValue.getValue()));
        }


    }


    @SuppressWarnings("unchecked")
    private static <A extends Model> A modelFromRow(Class<A> classObject, ResultRow row)
                                     throws DatabaseException
    {
        // [A 1] Create Model
        // --------------------------------------------------------------------------------------
        A model = Model.newModel(classObject);

        // [A 2] Get the Model's Values
        // --------------------------------------------------------------------------------------
        Tuple3<List<PrimitiveValue<?>>,
               List<ModelValue<?>>,
               List<CollectionValue<?>>> modelValuesTuple = Model.modelValues(model);

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
            String columnName = primitiveValue.getColumnName();
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
            ValuePromise valuePromise =
                    Model.modelValuePromise(modelValue.getModelClass(),
                                            queryParameters);

            modelValue.loadValue(valuePromise);
        }

        // [B 2] Evaluate collection values (one-to-many values)
        // --------------------------------------------------------------------------------------

        for (CollectionValue<? extends Model> collectionValue : collectionValues)
        {
            CollectionValuePromise collectionValuePromise =
                    Model.collectionValuePromise(model.name(),
                                                 model.getId(),
                                                 collectionValue.getModelClass());
            collectionValue.loadValue(collectionValuePromise);
        }

        return model;
    }


    /**
     * Collect all of the Model's values and return them in data structures suitable for
     * further analysis.
     * @param model A Model instance to get the values of.
     * @param <A> The model type.
     * @return The Model's values, sorted.
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

        Field[] fields = model.getClass().getFields();
        for (int i = 0; i < fields.length; i++)
        {
            if (Value.class.isAssignableFrom(fields[i].getType()))
                valueFields.add(fields[i]);
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
                Value<?> value = (Value<?>) field.get(model);

                // Sort values by database value type
                if (PrimitiveValue.class.isAssignableFrom(field.getType())) {
                    PrimitiveValue primitiveValue = (PrimitiveValue) value;
                    primitiveValue.setColumnName(field.getName().toLowerCase());
                    primitiveValues.add(primitiveValue);
                }
                else if (ModelValue.class.isAssignableFrom(field.getType())) {
                    modelValues.add((ModelValue<? extends Model>) value);
                }
                else if (CollectionValue.class.isAssignableFrom(field.getType())) {
                    collectionValues.add((CollectionValue<? extends Model>) value);
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
                   List<CollectionValue<?>>> modelValuesTuple = Model.modelValues(model);

        // Get all of model's database columns. Both primitive values and model values have
        // column representations
        List<Tuple2<String,SQLValue.Type>> columns = new ArrayList<>();

        // > Add MODEL columns
        columns.add(new Tuple2<>("_id", SQLValue.Type.TEXT));
        columns.add(new Tuple2<>("model_name", SQLValue.Type.TEXT));

        // > Add PRIMITIVE VALUE columns
        for (PrimitiveValue<?> primitiveValue : modelValuesTuple.getItem1())
        {
            columns.add(new Tuple2<>(primitiveValue.getColumnName(),
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
     * Create a new Model instance of the provided class.
     * @param classObject The Model class to create.
     * @param <A> The type of Model.
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
        tableBuilder.append(modelClass.getName());
        tableBuilder.append(" ( ");

        // [2] Column Definitions
        // --------------------------------------------------------------------------------------

        // > Get Model values

        Model dummyModel = Model.newModel(modelClass);

        Tuple3<List<PrimitiveValue<?>>,
                List<ModelValue<?>>,
                List<CollectionValue<?>>> modelValuesTuple = Model.modelValues(dummyModel);

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
            String        columnName = primitiveValue.getColumnName();
            SQLValue.Type columnType = primitiveValue.sqlType();

            tableBuilder.append(", ");
            tableBuilder.append(columnName);
            tableBuilder.append(" ");
            tableBuilder.append(columnType.name().toUpperCase());
        }

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
