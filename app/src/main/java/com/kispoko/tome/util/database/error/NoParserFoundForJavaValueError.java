
package com.kispoko.tome.util.database.error;



/**
 * No Parser Found for Java Value Database Error
 *
 */
public class NoParserFoundForJavaValueError
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private String javaTypeName;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public NoParserFoundForJavaValueError(String javaTypeName)
    {
        this.javaTypeName = javaTypeName;
    }


    // API
    // -----------------------------------------------------------------------------------------

    public String getJavaTypeName()
    {
        return this.javaTypeName;
    }

}
