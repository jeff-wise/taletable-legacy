
package com.kispoko.tome.util;


import android.content.ContentValues;

import com.kispoko.tome.util.database.ColumnProperties;
import com.kispoko.tome.util.database.DatabaseException;
import com.kispoko.tome.util.database.SQL;
import com.kispoko.tome.util.database.query.CollectionQuery;
import com.kispoko.tome.util.database.query.ModelQuery;
import com.kispoko.tome.util.database.query.ResultRow;
import com.kispoko.tome.util.database.query.UpsertQuery;
import com.kispoko.tome.util.promise.LoadCollectionValuePromise;
import com.kispoko.tome.util.promise.LoadValuePromise;
import com.kispoko.tome.util.promise.SaveValuePromise;
import com.kispoko.tome.util.tuple.Tuple3;
import com.kispoko.tome.util.value.CollectionValue;
import com.kispoko.tome.util.value.ModelValue;
import com.kispoko.tome.util.value.PrimitiveValue;
import com.kispoko.tome.util.value.Value;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;



/**
 * Model
 */
public abstract class Model<A>
{

    // ABSTRACT METHODS
    // --------------------------------------------------------------------------------------

    abstract public void onUpdateModel(String name);


    // PROPERTIES
    // --------------------------------------------------------------------------------------

    private UUID   id;
    private String name;


    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------

    public Model(UUID id, String name)
    {
        this.id   = id;
        this.name = name;
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


    // ** Name
    // ------------------------------------------------------------------------------------------

    public String getName()
    {
        return this.name;
    }


    // > Serialization
    // ------------------------------------------------------------------------------------------

    public static <A extends Model> LoadValuePromise<A>
                                    loadModelValuePromise(final String modelName,
                                                          final UUID modelId,
                                                          final Class<A> classObject)
    {
        return new LoadValuePromise<>(new LoadValuePromise.Action<A>() {
            @Override
            public A run() {
                A loadedValue = null;
                try {
                    loadedValue = Model.modelFromDatabase(modelName, modelId, classObject);
                } catch (DatabaseException e) {
                    e.printStackTrace();
                }
                return loadedValue;
            }
        });
    }


    public static <A extends Model> LoadCollectionValuePromise<A>
                                    loadCollectionValuePromise(final String modelName,
                                                               final String parentModelName,
                                                               final UUID parentModelId,
                                                               final Class<A> classObject)
    {
        return new LoadCollectionValuePromise<>(new LoadCollectionValuePromise.Action<A>() {
            @Override
            public List<A> run() {
                List<A> loadedCollection = null;
                try {
                    loadedCollection = Model.collectionFromDatabase(modelName,
                                                                    parentModelName,
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

    /**
     * Automatically load this model from the database using reflection on its Value properties
     * and the database data stored within them.
     * @return A new instance of the moddel.
     * @throws DatabaseException
     */
    @SuppressWarnings("unchecked")
    private static <A extends Model> A modelFromDatabase(String modelName,
                                                         UUID modelId,
                                                         Class<A> classObject)
                                     throws DatabaseException
    {
        // > Get Columns
        A dummyModel = Model.newModel(classObject);
        Tuple3<Map<String,PrimitiveValue<?>>,
               Map<String,ModelValue<?>>,
               List<CollectionValue<?>>> modelValues = Model.modelValues(dummyModel);

        Set<String> columnNameSet = new HashSet<>();
        columnNameSet.addAll(modelValues.getItem1().keySet());
        columnNameSet.addAll(modelValues.getItem2().keySet());

        // > Run the query
        ModelQuery modelQuery = new ModelQuery(modelName, modelId, columnNameSet);
        ResultRow row = modelQuery.result();

        return Model.modelFromRow(classObject, row);
    }


    /**
     * @return A new instance of the moddel.
     * @throws DatabaseException
     */
    @SuppressWarnings("unchecked")
    private static <A extends Model> List<A> collectionFromDatabase(String modelName,
                                                                    String parentModelName,
                                                                    UUID parentModelId,
                                                                    Class<A> classObject)
                       throws DatabaseException
    {
        // [1] GET columns
        // --------------------------------------------------------------------------------------
        A dummyModel = Model.newModel(classObject);
        Tuple3<Map<String,PrimitiveValue<?>>,
               Map<String,ModelValue<?>>,
               List<CollectionValue<?>>> modelValues = Model.modelValues(dummyModel);

        Set<String> columnNameSet = new HashSet<>();
        columnNameSet.addAll(modelValues.getItem1().keySet());
        columnNameSet.addAll(modelValues.getItem2().keySet());

        // [2] RUN collection query
        // --------------------------------------------------------------------------------------

        CollectionQuery collectionQuery = new CollectionQuery(modelName,
                                                              parentModelName,
                                                              parentModelId,
                                                              columnNameSet);
        List<ResultRow> resultRows = collectionQuery.result();

        // [3] CREATE each of the queried models in the collection
        // --------------------------------------------------------------------------------------

        List<A> models = new ArrayList<>();

        for (ResultRow row : resultRows)
        {
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
        // [A 1] Get all of the class's Value fields
        // --------------------------------------------------------------------------------------
        List<Field> valueFields = new ArrayList<>();

        Field[] fields = this.getClass().getFields();
        for (int i = 0; i < fields.length; i++)
        {
            if (Value.class.isAssignableFrom(fields[i].getType()))
                valueFields.add(fields[i]);
        }

        // [A 2] Group values by type
        // --------------------------------------------------------------------------------------

        List<PrimitiveValue<?>>                primitiveValues  = new ArrayList<>();
        List<ModelValue<? extends Model>>      modelValues      = new ArrayList<>();
        List<CollectionValue<? extends Model>> collectionValues = new ArrayList<>();

        try
        {
            for (Field field : valueFields)
            {
                Value<?> value = (Value<?>) field.get(this);

                // Sort values by database value type
                if (PrimitiveValue.class.isAssignableFrom(field.getType())) {
                    primitiveValues.add((PrimitiveValue) value);
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

        // [B 1] Save Model row
        // --------------------------------------------------------------------------------------

        // > Save each column value into a ContentValues
        ContentValues row = new ContentValues();

        // ** Save all of the primitive values
        for (PrimitiveValue primitiveValue : primitiveValues)
        {
            ColumnProperties columnProperties = primitiveValue.getColumnProperties();
            SQL.DataType dataType             = columnProperties.getDataType();
            String       columnName           = columnProperties.getColumnName();

            switch (dataType)
            {
                case INTEGER:
                    row.put(columnName, primitiveValue.asInteger());
                    break;
                case TEXT:
                    row.put(columnName, primitiveValue.asText());
                    break;
                case BLOB:
                    row.put(columnName, primitiveValue.asBlob());
                    break;
            }
        }

        // ** Save all of the model value identifiers (as foreign keys)
        for (ModelValue<? extends Model> modelValue : modelValues)
        {
            String columnName = modelValue.getColumnProperties().getColumnName();
            row.put(columnName, modelValue.getValue().getId().toString());
        }

        // > Save the row, creating a new one if necessary.
        UpsertQuery upsertQuery = new UpsertQuery(this.name, this.id, row);
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
    private static <A extends Model> A modelFromRow(Class<A> classObject,
                                                    ResultRow row)
                                     throws DatabaseException
    {
        // [A 1] Create Model
        // --------------------------------------------------------------------------------------
        A model = Model.newModel(classObject);

        // [A 2] Get the Model's Values
        // --------------------------------------------------------------------------------------
        Tuple3<Map<String,PrimitiveValue<?>>,
               Map<String,ModelValue<?>>,
               List<CollectionValue<?>>> modelValues = Model.modelValues(model);

        Map<String,PrimitiveValue<?>> primitiveValueMap = modelValues.getItem1();
        Map<String,ModelValue<?>>     modelValueMap     = modelValues.getItem2();
        List<CollectionValue<?>>      collectionValues  = modelValues.getItem3();

        // [B 1] Evaluate primitive model values
        // --------------------------------------------------------------------------------------

        for (Map.Entry<String,PrimitiveValue<?>> entry : primitiveValueMap.entrySet())
        {
            String            columnName     = entry.getKey();
            PrimitiveValue<?> primitiveValue = entry.getValue();

            SQL.DataType valueDataType = primitiveValue.getColumnProperties().getDataType();

            switch (valueDataType)
            {
                case INTEGER:
                    primitiveValue.fromInteger(row.getIntegerResult(columnName));
                    break;
                case TEXT:
                    primitiveValue.fromText(row.getTextResult(columnName));
                    break;
                case BLOB:
                    primitiveValue.fromBlob(row.getBlobResult(columnName));
                    break;
            }
        }

        // [B 2] Evaluate model values (many-to-one values)
        // --------------------------------------------------------------------------------------

        for (Map.Entry<String,ModelValue<?>> entry : modelValueMap.entrySet())
        {
            String        columnName = entry.getKey();
            ModelValue<? extends Model> modelValue = entry.getValue();

            UUID modelValueId = UUID.fromString(row.getTextResult(columnName));
            // Unchecked assignment. Type errors here didn't make sense, but the compiler doesn't
            // know that the class in modelValue.getModelClass should be over the same type A as
            // the ModelValue parameter type A. Though I think it should know that. Really not sure
            // here but it should work, so not worth the time.
            LoadValuePromise loadValuePromise =
                    Model.loadModelValuePromise(modelValue.getValue().getName(),
                                                modelValueId,
                                                modelValue.getModelClass());
            modelValue.loadValue(loadValuePromise);
        }

        // [B 2] Evaluate collection values (one-to-many values)
        // --------------------------------------------------------------------------------------

        for (CollectionValue<? extends Model> collectionValue : collectionValues)
        {
            LoadCollectionValuePromise loadCollectionValuePromise =
                    Model.loadCollectionValuePromise(collectionValue.getChildModelName(),
                                                     model.getName(),
                                                     model.getId(),
                                                     collectionValue.getModelClass());
            collectionValue.loadValue(loadCollectionValuePromise);
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
    private static <A> Tuple3<Map<String,PrimitiveValue<?>>,
                              Map<String,ModelValue<?>>,
                              List<CollectionValue<?>>>   modelValues(A model)
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
        Map<String,PrimitiveValue<?>> primitiveValueMap = new HashMap<>();
        Map<String,ModelValue<?>>     modelValueMap     = new HashMap<>();
        List<CollectionValue<?>>      collectionValues  = new ArrayList<>();

        try
        {
            for (Field field : valueFields)
            {
                Value<?> value = (Value<?>) field.get(model);

                // Sort values by database value type
                if (PrimitiveValue.class.isAssignableFrom(field.getType())) {
                    PrimitiveValue<?> primitiveValue = (PrimitiveValue<?>) value;
                    String columName = primitiveValue.getColumnProperties().getColumnName();
                    primitiveValueMap.put(columName, primitiveValue);
                }
                else if (ModelValue.class.isAssignableFrom(field.getType())) {
                    ModelValue<?> modelValue = (ModelValue<?>) value;
                    String columnName = modelValue.getColumnProperties().getColumnName();
                    modelValueMap.put(columnName, modelValue);
                }
                else if (CollectionValue.class.isAssignableFrom(field.getType())) {
                    collectionValues.add((CollectionValue<? extends Model>) value);
                }
            }
        }
        catch (IllegalAccessException e) {
            throw new DatabaseException();
        }

        return new Tuple3<>(primitiveValueMap, modelValueMap, collectionValues);
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


}
