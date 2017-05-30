
package com.kispoko.tome.rts.game


import android.content.Context
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.load.ContextIsNull
import com.kispoko.tome.load.Loader
import com.kispoko.tome.load.SpecIsNull
import com.kispoko.tome.load.loadLuloSpecification
import effect.Err
import effect.Val
import effect.effError
import effect.effValue
import lulo.File as LuloFile
import lulo.Spec



/**
 * Game Manager
 */
object GameManager
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private var specification : Spec? = null

    private var context : Context? = null


    // CONTEXT
    // -----------------------------------------------------------------------------------------

    fun setContext(context : Context)
    {
        this.context = context
    }


    // SPECIFICATION
    // -----------------------------------------------------------------------------------------

    /**
     * Get the Sheet specification (Lulo). If it is null, try to load it.
     */
    fun specification(context : Context) : Spec?
    {
        if (this.specification == null)
            this.loadSpecification(context)

        return this.specification
    }


    /**
     * Get the specification in the Loader context.
     */
    fun specificationLoader(context : Context) : Loader<Spec>
    {
        val currentSpecification = this.specification(context)
        if (currentSpecification != null)
            return effValue(currentSpecification)
        else
            return effError(SpecIsNull("game"))
    }


    /**
     * Load the specification. If it fails, report any errors.
     */
    fun loadSpecification(context : Context)
    {
        val specLoader = loadLuloSpecification("game", context)
        when (specLoader)
        {
            is Val -> this.specification = specLoader.value
            is Err -> ApplicationLog.error(specLoader.error)
        }
    }




}

