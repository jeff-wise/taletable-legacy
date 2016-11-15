
package com.kispoko.tome.error;



/**
 * Error: Reading Template File
 */
public class TemplateFileReadError
{

    // PROPERTIES
    // --------------------------------------------------------------------------------------

    private String templateFileName;


    // CONSTRUCTORS
    // --------------------------------------------------------------------------------------

    public TemplateFileReadError(String templateFileName)
    {
        this.templateFileName = templateFileName;
    }


    // API
    // --------------------------------------------------------------------------------------

    public String getTemplateFileName()
    {
        return this.templateFileName;
    }


}
