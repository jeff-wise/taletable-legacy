
package com.kispoko.tome.load


import android.content.Context
import com.kispoko.tome.ApplicationAssets
import effect.Eff
import effect.Identity
import effect.effError
import effect.effValue
import lulo.File as LuloFile
import lulo.Spec
import lulo.SpecError
import lulo.SpecValue
import java.io.IOException
import java.io.InputStream



/**
 * Load Effect
 */
typealias Loader<A> = Eff<LoadError, Identity, A>


/**
 * Load a Lulo specification.
 */
fun loadLuloSpecification(specName : String, context : Context) : Loader<Spec>
{
    val specFilePath = "${ApplicationAssets.specificationDirectoryPath}/$specName.yaml"

    fun parsedSpecification(specInputStream : InputStream) : Loader<Spec>
    {
        val specResult = LuloFile.specification(specInputStream)

        when (specResult)
        {
            is SpecValue -> return effValue(specResult.spec)
            is SpecError -> return effError(SpecParseError(specResult.error))
        }
    }

    return assetInputStream(context, specFilePath)
               .apply(::parsedSpecification)
}


/**
 * Get an input stream for an Android asset.
 */
fun assetInputStream(context : Context, assetFilePath : String) : Loader<InputStream> =
    try {
        effValue(context.assets.open(assetFilePath))
    }
    catch (e : IOException) {
        effError(CannotOpenTemplateFile(assetFilePath))
    }


sealed class LoadResult<A>

data class LoadResultValue<A>(val sheetRecord : A) : LoadResult<A>()

data class LoadResultError<A>(val userMessage : String) : LoadResult<A>()

