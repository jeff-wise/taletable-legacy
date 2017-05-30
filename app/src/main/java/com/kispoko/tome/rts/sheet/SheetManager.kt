
package com.kispoko.tome.rts.sheet


import android.content.Context
import com.kispoko.tome.ApplicationAssets
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.load.*
import com.kispoko.tome.model.sheet.Sheet
import com.kispoko.tome.rts.game.GameManager
import effect.*
import lulo.Spec
import lulo.document.SpecDoc
import java.io.InputStream
import lulo.File as LuloFile



/**
 * Sheet Manager
 *
 * Manages storing and loading sheets.
 */
object SheetManager
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


    fun context() : Loader<Context>
    {
        val currentContext = this.context

        if (currentContext != null)
            return effValue(currentContext)
        else
            return effError(ContextIsNull())
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
            return effError(SpecIsNull("sheet"))
    }


    /**
     * Load the specification. If it fails, report any errors.
     */
    fun loadSpecification(context : Context)
    {
        val specLoader = loadLuloSpecification("sheet", context)
        when (specLoader)
        {
            is Val -> this.specification = specLoader.value
            is Err -> ApplicationLog.error(specLoader.error)
        }
    }


    // TEMPLATES
    // -----------------------------------------------------------------------------------------

    fun loadSheetTemplate(templateName: String, context : Context) : TemplateSheet
    {
        val sheetLoader = _loadSheetTemplate(templateName, context)
        when (sheetLoader)
        {
            is Val -> return TemplateSheetValue(sheetLoader.value)
            is Err -> {
                val loadError = sheetLoader.error
                ApplicationLog.error(loadError)
                return TemplateSheetError(loadError.userMessage())
            }
        }
    }


    fun _loadSheetTemplate(templateName : String, context : Context) : Loader<Sheet>
    {
        // LET...
        val templateFilePath = ApplicationAssets.templateDirectoryPath +
                                    "/" + templateName + ".yaml"

        fun templateFileString(inputStream: InputStream) : Loader<String> =
            effValue(inputStream.bufferedReader().use { it.readText() })

        fun templateDocument(templateString : String,
                             sheetSpec : Spec,
                             gameSpec : Spec) : Loader<SpecDoc>
        {
            val docParse = sheetSpec.documentParse(templateString, listOf(gameSpec))
            when (docParse)
            {
                is Val -> return effValue(docParse.value)
                is Err -> return effError(TemplateParseError(docParse.error))
            }
        }

        fun sheetFromDocument(specDoc : SpecDoc) : Loader<Sheet>
        {
            val sheetParse = Sheet.fromDocument(specDoc)
            when (sheetParse)
            {
                is Val -> return effValue(sheetParse.value)
                is Err -> return effError(DocumentParseError("sheet", sheetParse.error))
            }
        }

        // DO...
        return assetInputStream(context, templateFilePath)
                .apply(::templateFileString)
                .applyWith(::templateDocument,
                           SheetManager.specificationLoader(context),
                           GameManager.specificationLoader(context))
                .apply(::sheetFromDocument)
    }

}



sealed class TemplateSheet

data class TemplateSheetValue(val sheet : Sheet) : TemplateSheet()

data class TemplateSheetError(val userMessage : String) : TemplateSheet()



