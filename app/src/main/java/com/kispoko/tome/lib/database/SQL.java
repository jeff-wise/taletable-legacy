
package com.kispoko.tome.lib.database;


import android.content.ContentValues;

import java.util.HashSet;
import java.util.Set;



/**
 * SQL Utility Class
 */
public class SQL
{


    private static Set<String> keywords = new HashSet<>();


    public static void initialize()
    {
        addKeywords();
    }

    public static String quoted(String innerString)
    {
        return "'" + innerString + "'";
    }


    public static void putOptString(ContentValues row, String columnName, Object object)
    {
        if (object != null)
            row.put(columnName, object.toString());
        else
            row.putNull(columnName);
    }


    public static Integer boolAsInt(Boolean bool)
    {
        if (bool != null)
            return bool ? 1 : 0;
        return null;
    }


    public static Boolean intAsBool(Integer i)
    {
        if (i != null)
            return i != 0;
        return null;
    }



    public static String asValidIdentifier(String id)
    {
        if (keywords.contains(id.toLowerCase()))
            return "_" + id;
        else
            return id;
    }


    // TYPES
    // ------------------------------------------------------------------------------------------

    public enum Constraint
    {
        PRIMARY_KEY
    }



    // INTERNAL
    // ------------------------------------------------------------------------------------------

    private static void addKeywords()
    {
        keywords.add("action");
        keywords.add("case");
        keywords.add("cast");
        keywords.add("check");
        keywords.add("column");
        keywords.add("commit");
        keywords.add("conflict");
        keywords.add("constraint");
        keywords.add("cross");
        keywords.add("database");
        keywords.add("default");
        keywords.add("deferrable");
        keywords.add("deferred");
        keywords.add("delete");
        keywords.add("desc");
        keywords.add("distinct");
        keywords.add("drop");
        keywords.add("each");
        keywords.add("else");
        keywords.add("end");
        keywords.add("escape");
        keywords.add("except");
        keywords.add("exclusive");
        keywords.add("exists");
        keywords.add("explain");
        keywords.add("fail");
        keywords.add("for");
        keywords.add("foreign");
        keywords.add("from");
        keywords.add("full");
        keywords.add("group");
        keywords.add("immediate");
        keywords.add("in");
        keywords.add("index");
        keywords.add("inner");
        keywords.add("insert");
        keywords.add("intersect");
        keywords.add("is");
        keywords.add("join");
        keywords.add("key");
        keywords.add("like");
        keywords.add("limit");
        keywords.add("match");
        keywords.add("no");
        keywords.add("not");
        keywords.add("null");
        keywords.add("of");
        keywords.add("offset");
        keywords.add("on");
        keywords.add("or");
        keywords.add("order");
        keywords.add("outer");
        keywords.add("plan");
        keywords.add("primary");
        keywords.add("query");
        keywords.add("recursive");
        keywords.add("release");
        keywords.add("replace");
        keywords.add("restrict");
        keywords.add("right");
        keywords.add("rollback");
        keywords.add("row");
        keywords.add("savepoint");
        keywords.add("select");
        keywords.add("set");
        keywords.add("table");
        keywords.add("temp");
        keywords.add("temporary");
        keywords.add("then");
        keywords.add("to");
        keywords.add("transaction");
        keywords.add("trigger");
        keywords.add("union");
        keywords.add("unique");
        keywords.add("update");
        keywords.add("vacuum");
        keywords.add("values");
        keywords.add("view");
        keywords.add("when");
        keywords.add("where");
        keywords.add("with");
        keywords.add("without");
    }



}