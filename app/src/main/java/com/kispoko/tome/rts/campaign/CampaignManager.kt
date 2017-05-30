
package com.kispoko.tome.rts.campaign


import android.content.Context
import com.kispoko.tome.ApplicationAssets
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.load.CannotOpenSpecFile
import com.kispoko.tome.load.SpecParseError
import lulo.Spec
import lulo.SpecError
import lulo.SpecValue
import java.io.IOException



/**
 * Campaign Manager
 */
object CampaignManager
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

}
