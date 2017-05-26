
package com.kispoko.tome.lib.yaml.error;



/**
 * Yaml Parsing Error: Missing Key
 */
public class MissingKeyError
{

    // PROPERTIES
    // --------------------------------------------------------------------------------------

    private String key;


    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------

    public MissingKeyError(String key)
    {
        this.key = key;
    }


    // API
    // --------------------------------------------------------------------------------------

    public String getKey()
    {
        return this.key;
    }


    public String errorMessage()
    {
        return "Missing Key Error:\n    Missing Key: " + this.key;
    }

}