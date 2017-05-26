
package com.kispoko.tome.sheet


import android.content.Context
import android.util.Log
import com.kispoko.tome.ApplicationAssets
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.model.sheet.Sheet
import effect.Err
import effect.Val
import lulo.Spec
import lulo.SpecError
import lulo.SpecValue
import java.io.IOException
import lulo.File as LuloFile



/**
 * Sheet Manager
 *
 * Manages storing and loading sheets.
 */
object SheetManager
{


    var specification : Spec? = null


    fun loadSpecification(context : Context)
    {
        val specFilePath = ApplicationAssets.specificationDirectoryPath + "/sheet.yaml"

        try
        {
            val specFileIS = context.assets.open(specFilePath)

            val specResult = lulo.File.specification(specFileIS)

            when (specResult)
            {
                is SpecValue -> specification = specResult.spec
                is SpecError -> ApplicationLog.error(SpecParseError(specResult.error))
            }
        }
        catch (e : IOException)
        {
            ApplicationLog.error(CannotOpenSpecFile(specFilePath))
        }
    }


    fun loadSheetTemplate(templateName : String, context : Context)
    {
        val templateFilePath = ApplicationAssets.templateDirectoryPath + templateName + ".yaml"

        try
        {
            val templateFileIS = context.assets.open(templateFilePath)
            val templateFileString = templateFileIS.bufferedReader().use { it.readText() }

            val templateDocParse = specification?.documentParse(templateFileString)

            when (templateDocParse)
            {
                is Val ->
                {
                    Sheet.fromDocument(templateDocParse.value)
                }
                is Err ->
                {
                    var errorString = ""
                    for (parseError in templateDocParse.error) {
                        errorString += parseError.toString() + "\n"
                    }
                    ApplicationLog.error(TemplateParseError(errorString))
                }
            }
        }
        catch (e : IOException)
        {
            ApplicationLog.error(CannotOpenTemplateFile(templateFilePath))
        }
    }

}
