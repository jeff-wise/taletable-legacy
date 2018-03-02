
package com.kispoko.tome.app


import android.content.Context
import com.kispoko.tome.load.CannotOpenTemplateFile
import com.kispoko.tome.load.DocLoader
import effect.effError
import effect.effValue
import java.io.IOException
import java.io.InputStream



/**
 * Get an input stream for an Android asset.
 */
fun assetInputStream(context : Context, assetFilePath : String) : DocLoader<InputStream> =
    try {
        effValue(context.assets.open(assetFilePath))
    }
    catch (e : IOException) {
        effError(CannotOpenTemplateFile(assetFilePath))
    }


