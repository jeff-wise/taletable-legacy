
package com.kispoko.tome.util.yaml.error;



/**
 * Yaml Error: Missing Key
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

}
