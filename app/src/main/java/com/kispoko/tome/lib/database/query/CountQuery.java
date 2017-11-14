
package com.kispoko.tome.lib.database.query;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.kispoko.tome.Global;
import com.kispoko.tome.lib.model.ProdType;
import com.kispoko.tome.util.promise.AsyncFunction;


/**
 * Query: Count
 *
 * Count the number of saved instances of a prodType.
 */
//public class CountQuery<A extends ProdType>
//{
//
//    // PROPERTIES
//    // ------------------------------------------------------------------------------------------
//
//    private String   queryString;
//    private Class<A> modelClass;
//
//
//    // CONSTRUCTORS
//    // ------------------------------------------------------------------------------------------
//
//    private CountQuery(Class<A> modelClass)
//    {
//        String tableName = ORM.name(modelClass);
//
//        this.queryString = CountQuery.countQueryString(tableName);
//        this.modelClass  = modelClass;
//    }
//
//
//    public static <A extends ProdType> CountQuery<A> fromModel(Class<A> modelClass)
//    {
//        return new CountQuery<>(modelClass);
//    }
//
//
//    // API
//    // ------------------------------------------------------------------------------------------
//
//    public void run(final OnCountListener listener)
//    {
//        new AsyncFunction<>(new AsyncFunction.Action<Object>()
//        {
//            @Override
//            public Object run()
//            {
//                SQLiteDatabase database = Global.getDatabase();
//                Cursor cursor = database.rawQuery(queryString, null);
//                Integer count;
//
//                try
//                {
//                    cursor.moveToFirst();
//                    count = cursor.getInt(0);
//                }
//                catch (Exception e) {
//                    return DatabaseException.query(new QueryError(queryString));
//                }
//                // Ensure cursor is closed
//                finally {
//                    cursor.close();
//                }
//
//                return count;
//            }
//        })
//        .run(new AsyncFunction.OnReady<Object>()
//        {
//            @Override
//            public void run(Object result)
//            {
//                if (result instanceof DatabaseException) {
//                    listener.onCountError((DatabaseException) result);
//                }
//                else if (result instanceof Integer) {
//                    String tableName = ORM.name(modelClass);
//                    listener.onCountResult(tableName, (Integer) result);
//                }
//            }
//        });
//    }
//
//
//    // INTERNAL
//    // ------------------------------------------------------------------------------------------
//
//    private static String countQueryString(String tableName)
//    {
//        StringBuilder queryBuilder = new StringBuilder();
//
//        queryBuilder.append("SELECT count(*) FROM ");
//        queryBuilder.append(tableName);
//
//        return queryBuilder.toString();
//    }
//
//
//    // NESTED DEFINITIONS
//    // ------------------------------------------------------------------------------------------
//
//    public interface OnCountListener
//    {
//        void onCountResult(String tableName, Integer count);
//        void onCountError(DatabaseException exception);
//    }
//
//
//}
