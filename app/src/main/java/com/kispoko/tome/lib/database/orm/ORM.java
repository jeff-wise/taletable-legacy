
package com.kispoko.tome.lib.database.orm;


/**
 * ModelLib
 */
//public class ORM
//{
//
//
//    public static <A> String name(Class<A> modelClass)
//    {
//        String modelName = modelClass.getName();
//
//        if (modelName.lastIndexOf('.') > 0)
//        {
//            modelName = modelName.substring(modelName.lastIndexOf('.') + 1); // Map$Entry
//            modelName = modelName.replace('$', '.');      // Map.Entry
//        }
//
//        modelName = SQL.asValidIdentifier(modelName).toLowerCase();
//
//        return modelName;
//    }
//
//
//    public static String name(ProdType model)
//    {
//        return ORM.name(model.getClass());
//    }
//
//
//    /**
//     * Automatically load this prodType from the database using reflection on its Value properties
//     * and the database data stored within them.
//     * @return A new instance of the moddel.
//     * @throws DatabaseException
//     */
//    public static <A extends ProdType> A loadModel(final Class<A> modelClass,
//                                                   final ModelQueryParameters queryParameters)
//                  throws DatabaseException
//    {
//        ResultRow row = runLoadQuery(modelClass, queryParameters);
//
//        A model = ORM.resultRowToModel(modelClass, row);
//
//        return model;
//    }
//
//
//    /**
//     * @return A new instance of the moddel.
//     * @throws DatabaseException
//     */
//    @SuppressWarnings("unchecked")
//    public static <A extends ProdType> List<A> loadModelCollection(final Class<A> modelClass,
//                                                                   final OneToManyRelation relation)
//                  throws DatabaseException
//    {
//
//        // [1] Query the collection rows
//        // -------------------------------------------------------------------------------------
//
//        List<ResultRow> resultRows = ORM.runLoadCollectionQuery(modelClass, relation);
//
//        // [2] Create the models from the result rows
//        // -------------------------------------------------------------------------------------
//
//        final List<A> models = new ArrayList<>();
//
//        for (ResultRow resultRow : resultRows)
//        {
//            A rowModel = ORM.resultRowToModel(modelClass, resultRow);
//            models.add(rowModel);
//        }
//
//        return models;
//    }
//
//
//    /**
//     * Save a prodType (and all its children) to the database.
//     * @param model The prodType.
//     * @throws DatabaseException
//     */
//    public static void saveModel(ProdType model)
//                  throws DatabaseException
//    {
//        ORM.saveModel(model, false);
//    }
//
//
//    /**
//     * Save a prodType (and all its children) to the database.
//     * @param model The prodType.
//     * @param isTransaction True if all the DB operations in involved in saving the prodType should
//     *                      be run in a single transaction. This should always be true for a
//     *                      top-level call to obtain reasonable performance.
//     * @throws DatabaseException
//     */
//    public static void saveModel(ProdType model, boolean isTransaction)
//                  throws DatabaseException
//    {
//        Long startTime = System.nanoTime();
//
//        try
//        {
//            if (isTransaction)
//                Global.getDatabase().beginTransaction();
//
//            // [1] Save the prodType's row
//            // -------------------------------------------------------------------------------------
//
//            insertModelRow(model, new ArrayList<OneToManyRelation>());
//
//            // [2] Save the prodType's child prodType rows
//            // -------------------------------------------------------------------------------------
//
//            saveModelChildRows(model);
//
//            if (isTransaction)
//                Global.getDatabase().setTransactionSuccessful();
//
//            Long endTime = System.nanoTime();
//
//            String timeString = Util.timeDifferenceString(startTime, endTime);
//
//            String eventMessage = "ProdType: " + model.getClass().getName() + "  " +
//                                  "Id: " + model.getId().toString() + "  " +
//                                  timeString + " ms";
//
//            EventLog.add(EventLog.EventType.MODEL_SAVE, eventMessage);
//        }
//        finally
//        {
//            if (isTransaction)
//                Global.getDatabase().endTransaction();
//        }
//    }
//
//
//    /**
//     * Save a prodType (and all its children) to the database with the given one-to-many relations.
//     * @throws DatabaseException
//     */
//    public static void saveModel(final ProdType model, List<OneToManyRelation> parentRelations)
//                  throws DatabaseException
//    {
//        // [1] Save the prodType's row
//        // -------------------------------------------------------------------------------------
//
//        insertModelRow(model, parentRelations);
//
//        // [2] Save the prodType's child prodType rows
//        // -------------------------------------------------------------------------------------
//
//        saveModelChildRows(model);
//    }
//
//
//    /**
//     * Save the prodType to the database.
//     * @throws DatabaseException
//     */
//    private static void saveModelChildRows(ProdType model)
//                   throws DatabaseException
//    {
//        // [1] Group values by type
//        // --------------------------------------------------------------------------------------
////
////        Tuple4<List<PrimitiveFunctor<?>>,
////               List<OptionFunctor<?>>,
////               List<ModelFunctor<?>>,
////               List<CollectionFunctor<?>>> functorsTuple;
//
////        try {
////            functorsTuple = ProdType.propertyFunctors(prodType);
////        }
////        catch (FunctorException exception) {
////            throw DatabaseException.functor(new FunctorError(exception));
////        }
////
////        final List<ModelFunctor<?>>      modelFunctors      = functorsTuple.getItem3();
////        final List<CollectionFunctor<?>> collectionFunctors = functorsTuple.getItem4();
////
////
////        // [2] Save Shared Value Rows
////        // --------------------------------------------------------------------------------------
////
////        for (final ModelFunctor<? extends ProdType> modelFunctor : modelFunctors)
////        {
////            modelFunctor.saveSheet();
////        }
////
////
////        // [B 3] Save Collection Values
////        // --------------------------------------------------------------------------------------
////
////        for (final CollectionFunctor<? extends ProdType> collectionFunctor : collectionFunctors)
////        {
////            List<OneToManyRelation> parentRelations = new ArrayList<>();
////            parentRelations.add(new OneToManyRelation(ORM.name(prodType),
////                                                      collectionFunctor.name(),
////                                                      prodType.getId()));
////            collectionFunctor.saveSheet(parentRelations);
////        }
//    }
//
//
//    /**
//     * Create a prodType from its Result Row.
//     * @return The loaded ProdType.
//     * @throws DatabaseException
//     */
//    private static <A extends ProdType> A resultRowToModel(Class<A> modelClass, ResultRow row)
//                   throws DatabaseException
//    {
//        // [A 1] Get the ProdType's Values
//        // ----------------------------------------------------------------------------------
//
//        final A model = ORM.newModel(modelClass);
//
//
//        // [A 2] Get the ProdType's Values
//        // ----------------------------------------------------------------------------------
//
////        Tuple4<List<PrimitiveFunctor<?>>,
////               List<OptionFunctor<?>>,
////               List<ModelFunctor<?>>,
////               List<CollectionFunctor<?>>> functorsTuple;
//
////        try {
////            functorsTuple = ProdType.propertyFunctors(prodType);
////        }
////        catch (FunctorException exception) {
////            throw DatabaseException.functor(new FunctorError(exception));
////        }
////
////        List<PrimitiveFunctor<?>>  primitiveFunctors  = functorsTuple.getItem1();
////        List<OptionFunctor<?>>     optionFunctors     = functorsTuple.getItem2();
////        List<ModelFunctor<?>>      modelFunctors      = functorsTuple.getItem3();
////        List<CollectionFunctor<?>> collectionFunctors = functorsTuple.getItem4();
////
////
////        // [B 1] Evaluate prodType values
////        // ----------------------------------------------------------------------------------
////
////        SQLValue idSqlValue = row.getSQLValue("_id");
////        prodType.setId(UUID.fromString(idSqlValue.getText()));
////
////
////        // [B 2] Evaluate primitive prodType values
////        // ----------------------------------------------------------------------------------
////
////        for (PrimitiveFunctor<?> primitiveFunctor : primitiveFunctors)
////        {
////            String columnName = primitiveFunctor.sqlColumnName();
////            primitiveFunctor.fromSQLValue(row.getSQLValue(columnName));
////        }
////
////        // [B 3] Evaluate option functors
////        // ----------------------------------------------------------------------------------
////
////        for (OptionFunctor<?> optionFunctor : optionFunctors)
////        {
////            String columnName = optionFunctor.sqlColumnName();
////            optionFunctor.fromSQLValue(row.getSQLValue(columnName));
////        }
////
////        // [B 4] Evaluate prodType functors
////        // ----------------------------------------------------------------------------------
////
////        for (final ModelFunctor<?> modelFunctor : modelFunctors)
////        {
////            String modelForeignKeyColumnName = modelFunctor.sqlColumnName();
////            SQLValue modelIdSqlValue = row.getSQLValue(modelForeignKeyColumnName);
////
////            // If the prodType does not exist at this time (the foreign key is NULL)
////            if (modelIdSqlValue.isNull())
////                continue;
////
////            UUID modelValueId = UUID.fromString(modelIdSqlValue.getText());
////            ModelQueryParameters queryParameters =
////                    new ModelQueryParameters(new ModelQueryParameters.PrimaryKey(modelValueId),
////                                             ModelQueryParameters.Type.PRIMARY_KEY);
////
////            modelFunctor.load(queryParameters);
////        }
////
////
////        // [B 2] Evaluate collection values (one-to-many values)
////        // ----------------------------------------------------------------------------------
////
////        for (final CollectionFunctor<? extends ProdType> collectionFunctor : collectionFunctors)
////        {
////            collectionFunctor.load(prodType);
////        }
//
//
//        return model;
//    }
//
//
//    /**
//     * Get the SQL column representations for all of the values in the prodType. Both primitive and
//     * prodType values have representations in the prodType's row. Collection values are stored in
//     * their own prodType's table and contain the foreign key to the parent prodType.
//     * @param model The prodType.
//     * @return
//     * @throws DatabaseException
//     */
//    private static List<Tuple2<String,SQLValue.Type>> sqlColumns(ProdType model)
//                   throws DatabaseException
//    {
////        Tuple4<List<PrimitiveFunctor<?>>,
////               List<OptionFunctor<?>>,
////               List<ModelFunctor<?>>,
////               List<CollectionFunctor<?>>> functorsTuple;
//
////        try {
////            functorsTuple = ProdType.propertyFunctors(prodType);
////        }
////        catch (FunctorException exception) {
////            throw DatabaseException.functor(new FunctorError(exception));
////        }
//
//        // Get all of prodType's database columns. Both primitive values and prodType values have
//        // column representations
//        List<Tuple2<String,SQLValue.Type>> columns = new ArrayList<>();
//
//        // > Add MODEL columns
//        columns.add(new Tuple2<>("_id", SQLValue.Type.TEXT));
//
//        // > Add PRIMITIVE VALUE columns
////        for (PrimitiveFunctor<?> primitiveValue : functorsTuple.getItem1())
////        {
////            columns.add(new Tuple2<>(primitiveValue.sqlColumnName(),
////                                     primitiveValue.sqlType()));
////        }
//
////        // > Add OPTION VALUE columns
////        for (OptionFunctor<?> optionFunctor : functorsTuple.getItem2())
////        {
////            columns.add(new Tuple2<>(optionFunctor.sqlColumnName(),
////                                     SQLValue.Type.TEXT));
////        }
////
////        // > Add MODEL VALUE columns
////        for (ModelFunctor<?> modelFunctor : functorsTuple.getItem3())
////        {
////            columns.add(new Tuple2<>(modelFunctor.sqlColumnName(),
////                                     SQLValue.Type.TEXT));
////        }
//
//        return columns;
//    }
//
//
//    /**
//     * Create a new ModelLib instance of the provided class.
//     * @param classObject The ModelLib class to create.
//     * @param <A> The type of ModelLib.
//     * @return A new prodType instance.
//     * @throws DatabaseException
//     */
//    private static <A> A newModel(Class<A> classObject)
//                       throws DatabaseException
//    {
//        A model;
//        try {
//            model = classObject.newInstance();
//        }
//        catch (Exception e) {
//            throw DatabaseException.serialization(
//                    new SerializationError(classObject.getName(), e));
//        }
//        return model;
//    }
//
//
//    // > Tables
//    // ------------------------------------------------------------------------------------------
//
//    public static void createSchema(List<Class<? extends ProdType>> modelClasses,
//                                    SQLiteDatabase database)
//                  throws DatabaseException
//    {
//
//        Map<String,List<Tuple2<String,String>>> childrenToParents
//                                = childToParentRelations(modelClasses);
//
//        for (Class<? extends ProdType> modelClass : modelClasses)
//        {
//            String tableName = ORM.name(modelClass);
//            List<Tuple2<String,String>> parentRelations = childrenToParents.get(tableName);
//            if (parentRelations == null)
//                parentRelations = new ArrayList<>();
//
//            String createTableQueryString = defineTableSQLString(modelClass, parentRelations);
//            database.execSQL(createTableQueryString);
//        }
//
//    }
//
//
//    private static Map<String,List<Tuple2<String,String>>>
//                        childToParentRelations(List<Class<? extends ProdType>> modelClasses)
//                                     throws DatabaseException
//    {
//        Map<String,List<Tuple2<String,String>>> relationMap = new HashMap<>();
//
////        for (Class<? extends ProdType> modelClass : modelClasses)
////        {
////            ProdType dummyModel = ORM.newModel(modelClass);
////
////            Tuple4<List<PrimitiveFunctor<?>>,
////                   List<OptionFunctor<?>>,
////                   List<ModelFunctor<?>>,
////                   List<CollectionFunctor<?>>> functorsTuple;
//
////            try {
////                functorsTuple = ProdType.propertyFunctors(dummyModel);
////            }
////            catch (FunctorException exception) {
////                throw DatabaseException.functor(new FunctorError(exception));
////            }
//
////            List<CollectionFunctor<?>> collectionValues = functorsTuple.getItem4();
//
//            //String parentName = ORM.name(modelClass);
//
////            for (CollectionFunctor<?> collectionFunctor : collectionValues)
////            {
////                String collectionName = collectionFunctor.name();
////
////                String childModelName = ORM.name(collectionFunctor.modelClass());
////
////                if (!relationMap.containsKey(childModelName))
////                    relationMap.put(childModelName, new ArrayList<Tuple2<String, String>>());
////
////                List<Tuple2<String,String>> parents = relationMap.get(childModelName);
////                parents.add(new Tuple2<>(parentName, collectionName));
////                relationMap.put(childModelName, parents);
////            }
////        }
//
//        return relationMap;
//    }
//
//
//    private static <A extends ProdType> String defineTableSQLString(
//                                                    Class<A> modelClass,
//                                                    List<Tuple2<String,String>> parentRelations)
//                                    throws DatabaseException
//    {
//        StringBuilder tableBuilder = new StringBuilder();
//
//        // [1] Create Table
//        // --------------------------------------------------------------------------------------
//        tableBuilder.append("CREATE TABLE IF NOT EXISTS ");
//        tableBuilder.append(ORM.name(modelClass));
//        tableBuilder.append(" ( ");
//
//
//        // [2] Column Definitions
//        // --------------------------------------------------------------------------------------
//
//        // > Get ProdType values
//
//        ProdType dummyModel = ORM.newModel(modelClass);
////
////        Tuple4<List<PrimitiveFunctor<?>>,
////               List<OptionFunctor<?>>,
////               List<ModelFunctor<?>>,
////               List<CollectionFunctor<?>>> functorsTuple;
//
////        try {
////            functorsTuple = ProdType.propertyFunctors(dummyModel);
////        }
////        catch (FunctorException exception) {
////            throw DatabaseException.functor(new FunctorError(exception));
////        }
//
////        List<PrimitiveFunctor<?>> primitiveFunctors = functorsTuple.getItem1();
////        List<OptionFunctor<?>>    optionFunctors    = functorsTuple.getItem2();
////        List<ModelFunctor<?>>     modelFunctors     = functorsTuple.getItem3();
////
////        // ** ProdType Id
////        tableBuilder.append("_id");
////        tableBuilder.append(" ");
////        tableBuilder.append(SQLValue.Type.TEXT.name().toUpperCase());
////        tableBuilder.append(" PRIMARY KEY");
////
////        // ** Primitive Values
////        for (PrimitiveFunctor<?> primitiveFunctor : primitiveFunctors)
////        {
////            String        columnName = primitiveFunctor.sqlColumnName();
////            SQLValue.Type columnType = primitiveFunctor.sqlType();
////
////            tableBuilder.append(", ");
////            tableBuilder.append(columnName);
////            tableBuilder.append(" ");
////            tableBuilder.append(columnType.name().toUpperCase());
////        }
////
////        // ** Option Values
////        for (OptionFunctor<?> optionFunctor : optionFunctors)
////        {
////            String        columnName = optionFunctor.sqlColumnName();
////            SQLValue.Type columnType = SQLValue.Type.TEXT;
////
////            tableBuilder.append(", ");
////            tableBuilder.append(columnName);
////            tableBuilder.append(" ");
////            tableBuilder.append(columnType.name().toUpperCase());
////        }
////
////        // ** ProdType Values
////        for (ModelFunctor<?> modelFunctor : modelFunctors)
////        {
////            String columnName = modelFunctor.sqlColumnName();
////
////            tableBuilder.append(", ");
////            tableBuilder.append(columnName);
////            tableBuilder.append(" ");
////            tableBuilder.append(SQLValue.Type.TEXT.name().toUpperCase());
////        }
////
////
////        // ** Collection Value parents
////        for (Tuple2<String,String> parentInfo : parentRelations)
////        {
////            String parentName     = parentInfo.getItem1();
////            String collectionName = parentInfo.getItem2();
////
////            String columnName = "parent_" + collectionName + "_" + parentName + "_id";
////
////            tableBuilder.append(", ");
////            tableBuilder.append(columnName);
////            tableBuilder.append(" ");
////            tableBuilder.append(SQLValue.Type.TEXT.name().toUpperCase());
////        }
//
//        // [2] End
//        // --------------------------------------------------------------------------------------
//
//        tableBuilder.append(" )");
//
//        return tableBuilder.toString();
//    }
//
//
//    private static <A extends ProdType> ResultRow runLoadQuery(Class<A> modelClass,
//                                                               ModelQueryParameters queryParameters)
//                                     throws DatabaseException
//    {
//        // [1] GET SQL columns
//        A dummyModel = ORM.newModel(modelClass);
//        List<Tuple2<String, SQLValue.Type>> sqlColumns =
//                                        ORM.sqlColumns(dummyModel);
//
//        // [2] RUN the query
//        ModelQuery modelQuery = new ModelQuery(ORM.name(modelClass),
//                                               sqlColumns,
//                                               queryParameters);
//        ResultRow row = modelQuery.result();
//
//        return row;
//    }
//
//
//    private static <A extends ProdType> List<ResultRow> runLoadCollectionQuery(
//                                                            Class<A> modelClass,
//                                                            OneToManyRelation relation)
//                   throws DatabaseException
//    {
//        // [1] GET SQL columns
//        // -------------------------------------------------------------------------------------
//
//        A dummyModel = ORM.newModel(modelClass);
//        List<Tuple2<String, SQLValue.Type>> sqlColumns = ORM.sqlColumns(dummyModel);
//
//        // [2] RUN the query
//        // -------------------------------------------------------------------------------------
//
//        CollectionQuery collectionQuery = new CollectionQuery(
//                                                    ORM.name(modelClass),
//                                                    relation,
//                                                    sqlColumns);
//
//        List<ResultRow> resultRows = collectionQuery.result();
//
//        return resultRows;
//    }
//
//
//    private static void insertModelRow(ProdType model, List<OneToManyRelation> parentRelations)
//                   throws DatabaseException
//    {
//        // [A 1] Group values by type
//        // --------------------------------------------------------------------------------------
//
////        Tuple4<List<PrimitiveFunctor<?>>,
////               List<OptionFunctor<?>>,
////               List<ModelFunctor<?>>,
////               List<CollectionFunctor<?>>> functorsTuple;
//
////        try {
////            functorsTuple = ProdType.propertyFunctors(prodType);
////        }
////        catch (FunctorException exception) {
////            throw DatabaseException.functor(new FunctorError(exception));
////        }
//
////        final List<PrimitiveFunctor<?>>  primitiveFunctors = functorsTuple.getItem1();
////        final List<OptionFunctor<?>>     optionFunctors    = functorsTuple.getItem2();
////        final List<ModelFunctor<?>>      modelFunctors     = functorsTuple.getItem3();
////
////
////        // [B 1] Save ProdType row
////        // --------------------------------------------------------------------------------------
////
////        // > Save each column value into a ContentValues
////        ContentValues row = new ContentValues();
////
////        // ** Save the prodType values
////        row.put("_id", prodType.getId().toString());
////
////        // ** Save all of the primitive values
////        for (PrimitiveFunctor primitiveFunctor : primitiveFunctors)
////        {
////            SQLValue sqlValue = primitiveFunctor.toSQLValue();
////            String columnName = primitiveFunctor.sqlColumnName();
////
////            switch (sqlValue.getType()) {
////                case INTEGER:
////                    row.put(columnName, sqlValue.getInteger());
////                    break;
////                case REAL:
////                    row.put(columnName, sqlValue.getReal());
////                    break;
////                case TEXT:
////                    row.put(columnName, sqlValue.getText());
////                    break;
////                case BLOB:
////                    row.put(columnName, sqlValue.getBlob());
////                    break;
////                case NULL:
////                    row.putNull(columnName);
////                    break;
////            }
////        }
//
////
////        for (OptionFunctor optionFunctor : optionFunctors)
////        {
////            SQLValue sqlValue = optionFunctor.toSQLValue();
////            String columnName = optionFunctor.sqlColumnName();
////
////            switch (sqlValue.getType())
////            {
////                case TEXT:
////                    row.put(columnName, sqlValue.getText());
////                    break;
////                case NULL:
////                    row.putNull(columnName);
////                    break;
////                default:
////                    row.putNull(columnName);
////            }
////        }
////
////
////        // [B 2] Save all child models in the row by foreign key
////        // --------------------------------------------------------------------------------------
////
////        for (ModelFunctor<? extends ProdType> modelFunctor : modelFunctors)
////        {
////            String columnName = modelFunctor.sqlColumnName();
////
////            if (modelFunctor.isNull())
////            {
////                row.putNull(columnName);
////            }
////            else
////            {
////                UUID modelId = modelFunctor.getValue().getId();
////
////                if (modelId == null) {
////                    throw DatabaseException.nullModelId(
////                            new NullModelIdentifierError(
////                                        modelFunctor.getValue().getClass().getName()));
////                }
////
////                row.put(columnName, modelFunctor.getValue().getId().toString());
////            }
////
////        }
////
////
////        // [B 3] Save all parent models in the row by foreign key
////        // --------------------------------------------------------------------------------------
////
////        for (OneToManyRelation parentRelation : parentRelations)
////        {
////            row.put(parentRelation.childSQLColumnName(),
////                    parentRelation.getParentId().toString());
////        }
////
////        // > Save the row, creating a new one if necessary.
////        String tableNameString = ORM.name(prodType);
////        UpsertQuery upsertQuery = new UpsertQuery(ORM.name(prodType), prodType.getId(), row);
////
////        Long startTime = System.nanoTime();
////
////        upsertQuery.run();
////
////        Long endTime = System.nanoTime();
////
////        String timeString = Util.timeDifferenceString(startTime, endTime);
////        String eventMessage = "Table: " + tableNameString + "  " +
////                              "Id: " + prodType.getId().toString() + "  " +
////                              timeString + " ms";
////        EventLog.add(EventLog.EventType.ROW_INSERT, eventMessage);
//    }
//
//
//}
//
//

