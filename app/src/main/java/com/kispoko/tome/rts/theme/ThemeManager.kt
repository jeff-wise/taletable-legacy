
package com.kispoko.tome.rts.theme


import android.content.Context
import com.kispoko.tome.app.*
import com.kispoko.tome.load.*
import com.kispoko.tome.model.theme.*
import com.kispoko.tome.official.OfficialTheme
import effect.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.run
import lulo.document.SpecDoc
import lulo.spec.Spec
import java.io.InputStream



/**
 * Theme Manager
 */
object ThemeManager
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------


    private var specification : Spec? = null

    private val themeString = "theme"

    val themeById : MutableMap<ThemeId, Theme> = mutableMapOf()

    var officialThemesLoaded = false


    // -----------------------------------------------------------------------------------------
    // SPECIFICATION
    // -----------------------------------------------------------------------------------------

    /**
     * Get the Theme specification (Lulo). If it is null, try to load it.
     */
    fun specification(context : Context) : Spec?
    {
        if (specification == null)
            loadSpecification(context)

        return specification
    }


    /**
     * Get the specification in the Loader context.
     */
    fun specificationLoader(context : Context) : Loader<Spec>
    {
        val currentSpecification = specification(context)
        if (currentSpecification != null)
            return effValue(currentSpecification)
        else
            return effError(SpecIsNull(themeString))
    }


    /**
     * Load the specification. If it fails, report any errors.
     */
    fun loadSpecification(context : Context)
    {
        val specLoader = loadLuloSpecification(themeString, context)
        when (specLoader)
        {
            is Val -> specification = specLoader.value
            is Err -> ApplicationLog.error(specLoader.error)
        }
    }


    // -----------------------------------------------------------------------------------------
    // OFFICIAL
    // -----------------------------------------------------------------------------------------

    suspend fun loadOfficialThemes(themes : List<OfficialTheme>, context : Context)
    {
        // TODO review this logic/process. what if it fails? shouldn't ever fail though.
        // TODO test that themes load correctly
        if (!officialThemesLoaded)
        {
            themes.forEach { loadOfficialTheme(it, context) }

            officialThemesLoaded = true
        }
    }


    suspend fun loadOfficialTheme(officialTheme : OfficialTheme,
                                  context : Context) : LoadResult<Theme> = run(CommonPool,
    {
        val themeLoader = _loadOfficialTheme(officialTheme, context)
        when (themeLoader)
        {
            is Val ->
            {
                val theme = themeLoader.value
                this.themeById.put(theme.themeId(), theme)
                ApplicationLog.event(OfficialThemeAdded(officialTheme))
                LoadResultValue(theme)
            }
            is Err ->
            {
                val loadError = themeLoader.error
                ApplicationLog.error(loadError)
                LoadResultError<Theme>(loadError.userMessage())
            }
        }
    })


    private fun _loadOfficialTheme(officialTheme : OfficialTheme,
                                   context : Context) : Loader<Theme>
    {
        // LET...
        fun templateFileString(inputStream: InputStream) : Loader<String> =
            effValue(inputStream.bufferedReader().use { it.readText() })

        fun templateDocument(templateString : String, gameSpec : Spec) : Loader<SpecDoc>
        {
            val docParse = gameSpec.parseDocument(templateString, listOf())
            when (docParse)
            {
                is Val -> return effValue(docParse.value)
                is Err -> return effError(DocumentParseError(officialTheme.toString(),
                                                             themeString,
                                                             docParse.error))
            }
        }

        fun themeFromDocument(specDoc : SpecDoc) : Loader<Theme>
        {
            val themeParser = Theme.fromDocument(specDoc)
            when (themeParser)
            {
                is Val -> return effValue(themeParser.value)
                is Err -> return effError(ValueParseError(officialTheme.toString(), themeParser.error))
            }
        }

        // DO...
        return assetInputStream(context, officialTheme.filePath)
                .apply(::templateFileString)
                .applyWith(::templateDocument,
                           specificationLoader(context))
                .apply(::themeFromDocument)
    }


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    /**
     * The theme with the given id.
     */
    //fun theme(themeId : ThemeId) : Theme? = this.themeById[themeId]

    fun theme(themeId : ThemeId) : AppEff<Theme> =
            note(this.themeById[themeId],
                 AppThemeError(ThemeDoesNotExist(themeId)))

}


