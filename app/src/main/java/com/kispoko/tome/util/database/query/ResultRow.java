
package com.kispoko.tome.util.database.query;


import java.util.HashMap;
import java.util.Map;



/**
 * Result Row
 */
public class ResultRow
{

    // PROPERTIES
    // --------------------------------------------------------------------------------------

    private Map<String,Object> resultMap;

    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------

    public ResultRow()
    {
        resultMap = new HashMap<>();
    }


    // API
    // --------------------------------------------------------------------------------------


    public void putResult(String columnName, Object columnValue)
    {
        this.resultMap.put(columnName, columnValue);
    }


    public String getTextResult(String columnName)
    {
        return (String) this.resultMap.get(columnName);
    }


    public Integer getIntegerResult(String columnName)
    {
        return (Integer) this.resultMap.get(columnName);
    }


    public byte[] getBlobResult(String columnName)
    {
        return (byte[]) this.resultMap.get(columnName);
    }


}
