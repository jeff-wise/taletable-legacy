
package com.kispoko.tome.load


import android.content.Context
import com.kispoko.tome.ApplicationAssets
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.model.campaign.Campaign
import com.kispoko.tome.model.game.Game
import com.kispoko.tome.model.sheet.Sheet
import com.kispoko.tome.model.theme.Theme
import com.kispoko.tome.rts.official.OfficialManager.assetInputStream
import effect.*
import lulo.SchemaParseError
import lulo.SchemaParseValue
import lulo.document.SchemaDoc
import lulo.parseSchema
import lulo.schema.Schema
import java.io.InputStream



object TomeDoc
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    // Properties > Schemas
    // -----------------------------------------------------------------------------------------

    private var sheetSchema : Schema? = null
    private var campaignSchema : Schema? = null
    private var gameSchema : Schema? = null
    private var engineSchema : Schema? = null
    private var themeSchema : Schema? = null


    // -----------------------------------------------------------------------------------------
    // LOAD
    // -----------------------------------------------------------------------------------------

    // Load > Sheet
    // -----------------------------------------------------------------------------------------

    fun loadSheet(inputStream : InputStream,
                  sheetName : String,
                  context : Context) : DocLoader<Sheet>
    {
        // LET...
        val templateFileString : DocLoader<String> =
            effValue(inputStream.bufferedReader().use { it.readText() })

        fun templateDocument(templateString : String,
                             sheetSchema : Schema,
                             campaignSchema : Schema,
                             engineSchema : Schema,
                             themeSchema : Schema) : DocLoader<SchemaDoc>
        {
            val docParse = sheetSchema.parseDocument(templateString,
                                                   listOf(campaignSchema, engineSchema, themeSchema))
            when (docParse)
            {
                is Val -> return effValue(docParse.value)
                is Err -> return effError(DocumentParseError(sheetName, "sheet", docParse.error))
            }
        }

        fun sheetFromDocument(specDoc : SchemaDoc) : DocLoader<Sheet>
        {
            val sheetParse = Sheet.fromDocument(specDoc)
            when (sheetParse)
            {
                is Val -> return effValue(sheetParse.value)
                is Err -> return effError(ValueParseError(sheetName,
                                                          sheetParse.error))
            }
        }

        // DO...
        return templateFileString
               .applyWith(::templateDocument,
                          sheetSchemaLoader(context),
                          campaignSchemaLoader(context),
                          engineSchemaLoader(context),
                          themeSchemaLoader(context))
               .apply(::sheetFromDocument)
    }


    // Load > Campaign
    // -----------------------------------------------------------------------------------------

    fun loadCampaign(inputStream : InputStream,
                     campaignName : String,
                     context : Context) : DocLoader<Campaign>
    {
        val templateFileString : DocLoader<String> =
                effValue(inputStream.bufferedReader().use { it.readText() })

        fun templateDocument(templateString : String,
                             campaignSpec : Schema,
                             gameSpec : Schema) : DocLoader<SchemaDoc>
        {
            val docParse = campaignSpec.parseDocument(templateString,
                                                      listOf(gameSpec))
            when (docParse)
            {
                is Val -> return effValue(docParse.value)
                is Err -> return effError(DocumentParseError(campaignName,
                                                             "campaign",
                                                             docParse.error))
            }
        }

        fun campaignFromDocument(specDoc : SchemaDoc) : DocLoader<Campaign>
        {
            val sheetParse = Campaign.fromDocument(specDoc)
            when (sheetParse)
            {
                is Val -> return effValue(sheetParse.value)
                is Err -> return effError(ValueParseError(campaignName,
                                                          sheetParse.error))
            }
        }

        // DO...
        return templateFileString
               .applyWith(::templateDocument,
                          campaignSchemaLoader(context),
                          gameSchemaLoader(context))
               .apply(::campaignFromDocument)
    }


    // Load > Game
    // -----------------------------------------------------------------------------------------

    fun loadGame(inputStream : InputStream,
                 gameName : String,
                 context : Context) : DocLoader<Game>
    {
        // LET...
        val templateFileString : DocLoader<String> =
                effValue(inputStream.bufferedReader().use { it.readText() })

        fun templateDocument(templateString : String,
                             gameSchema : Schema,
                             engineSchema : Schema) : DocLoader<SchemaDoc>
        {
            val docParse = gameSchema.parseDocument(templateString, listOf(engineSchema))
            when (docParse)
            {
                is Val -> return effValue(docParse.value)
                is Err -> return effError(DocumentParseError(gameName,
                                                             "game",
                                                             docParse.error))
            }
        }

        fun gameFromDocument(specDoc : SchemaDoc) : DocLoader<Game>
        {
            val gameParse = Game.fromDocument(specDoc)
            when (gameParse)
            {
                is Val -> return effValue(gameParse.value)
                is Err -> return effError(ValueParseError(gameName,
                                                          gameParse.error))
            }
        }

        // DO...
        return templateFileString
               .applyWith(::templateDocument,
                          gameSchemaLoader(context),
                          engineSchemaLoader(context))
               .apply(::gameFromDocument)
    }


    // Load > Theme
    // -----------------------------------------------------------------------------------------

    fun loadTheme(inputStream : InputStream,
                  themeName : String,
                  context : Context) : DocLoader<Theme>
    {
        // LET...
        val templateFileString : DocLoader<String> =
                effValue(inputStream.bufferedReader().use { it.readText() })

        fun templateDocument(templateString : String, gameSpec : Schema) : DocLoader<SchemaDoc>
        {
            val docParse = gameSpec.parseDocument(templateString, listOf())
            when (docParse)
            {
                is Val -> return effValue(docParse.value)
                is Err -> return effError(DocumentParseError(themeName,
                                                             "theme",
                                                             docParse.error))
            }
        }

        fun themeFromDocument(specDoc : SchemaDoc) : DocLoader<Theme>
        {
            val themeParser = Theme.fromDocument(specDoc)
            when (themeParser)
            {
                is Val -> return effValue(themeParser.value)
                is Err -> return effError(ValueParseError(themeName, themeParser.error))
            }
        }

        // DO...
        return templateFileString
               .applyWith(::templateDocument,
                          themeSchemaLoader(context))
               .apply(::themeFromDocument)
    }


    // -----------------------------------------------------------------------------------------
    // SCHEMAS
    // -----------------------------------------------------------------------------------------

    // Schemas > Sheet
    // -----------------------------------------------------------------------------------------

    /**
     * Get the Sheet specification (Lulo). If it is null, try to load it.
     */
    fun sheetSchema(context : Context) : Schema?
    {
        if (this.sheetSchema == null)
        {
            val schemaLoader = loadLuloSchema("sheet", context)
            when (schemaLoader)
            {
                is Val -> {
                    this.sheetSchema = schemaLoader.value
                    ApplicationLog.event(SchemaLoaded("sheet"))
                }
                is Err -> ApplicationLog.error(schemaLoader.error)
            }
        }

        return this.sheetSchema
    }


    /**
     * get the specification in the loader context.
     */
    fun sheetSchemaLoader(context : Context) : DocLoader<Schema>
    {
        val schema = this.sheetSchema(context)
        if (schema != null)
            return effValue(schema)
        else
            return effError(SchemaIsNull("sheet"))
    }


    // Schemas > Campaign
    // -----------------------------------------------------------------------------------------

    /**
     * Get the Sheet specification (Lulo). If it is null, try to load it.
     */
    fun campaignSchema(context : Context) : Schema?
    {
        if (this.campaignSchema == null)
        {
            val schemaLoader = loadLuloSchema("campaign", context)
            when (schemaLoader)
            {
                is Val -> {
                    this.campaignSchema = schemaLoader.value
                    ApplicationLog.event(SchemaLoaded("campaign"))
                }
                is Err -> ApplicationLog.error(schemaLoader.error)
            }
        }

        return this.campaignSchema
    }


    /**
     * get the specification in the loader context.
     */
    fun campaignSchemaLoader(context : Context) : DocLoader<Schema>
    {
        val schema = this.campaignSchema(context)
        if (schema != null)
            return effValue(schema)
        else
            return effError(SchemaIsNull("campaign"))
    }


    // Schemas > Game
    // -----------------------------------------------------------------------------------------

    /**
     * Get the Sheet specification (Lulo). If it is null, try to load it.
     */
    fun gameSchema(context : Context) : Schema?
    {
        if (this.gameSchema == null)
        {
            val schemaLoader = loadLuloSchema("game", context)
            when (schemaLoader)
            {
                is Val -> {
                    this.gameSchema = schemaLoader.value
                    ApplicationLog.event(SchemaLoaded("game"))
                }
                is Err -> ApplicationLog.error(schemaLoader.error)
            }
        }

        return this.gameSchema
    }


    /**
     * get the specification in the loader context.
     */
    fun gameSchemaLoader(context : Context) : DocLoader<Schema>
    {
        val schema = this.gameSchema(context)
        if (schema != null)
            return effValue(schema)
        else
            return effError(SchemaIsNull("game"))
    }


    // Schemas > Engine
    // -----------------------------------------------------------------------------------------

    /**
     * Get the Sheet specification (Lulo). If it is null, try to load it.
     */
    fun engineSchema(context : Context) : Schema?
    {
        if (this.engineSchema == null)
        {
            val schemaLoader = loadLuloSchema("engine", context)
            when (schemaLoader)
            {
                is Val -> {
                    this.engineSchema = schemaLoader.value
                    ApplicationLog.event(SchemaLoaded("engine"))
                }
                is Err -> ApplicationLog.error(schemaLoader.error)
            }
        }

        return this.engineSchema
    }


    /**
     * Get the specification in the loader context.
     */
    fun engineSchemaLoader(context : Context) : DocLoader<Schema>
    {
        val schema = this.engineSchema(context)
        if (schema != null)
            return effValue(schema)
        else
            return effError(SchemaIsNull("engine"))
    }



    // Schemas > Theme
    // -----------------------------------------------------------------------------------------

    /**
     * Get the Sheet specification (Lulo). If it is null, try to load it.
     */
    fun themeSchema(context : Context) : Schema?
    {
        if (this.themeSchema == null)
        {
            val schemaLoader = loadLuloSchema("theme", context)
            when (schemaLoader)
            {
                is Val -> {
                    this.themeSchema = schemaLoader.value
                    ApplicationLog.event(SchemaLoaded("theme"))
                }
                is Err -> ApplicationLog.error(schemaLoader.error)
            }
        }

        return this.themeSchema
    }


    /**
     * get the specification in the loader context.
     */
    fun themeSchemaLoader(context : Context) : DocLoader<Schema>
    {
        val schema = this.themeSchema(context)
        if (schema != null)
            return effValue(schema)
        else
            return effError(SchemaIsNull("theme"))
    }

}



/**
 * Load Effect
 */
typealias DocLoader<A> = Eff<DocLoadError, Identity, A>


/**
 * Load a Lulo specification.
 */
fun loadLuloSchema(schemaName : String, context : Context) : DocLoader<Schema>
{
    val specFilePath = "${ApplicationAssets.specificationDirectoryPath}/$schemaName.yaml"

    fun parsedSpecification(specInputStream : InputStream) : DocLoader<Schema>
    {
        val schemaResult = parseSchema(specInputStream)

        when (schemaResult)
        {
            is SchemaParseValue -> return effValue(schemaResult.schema)
            is SchemaParseError -> return effError(SchemaParseError(schemaResult.toString()))
        }
    }

    return assetInputStream(context, specFilePath)
               .apply(::parsedSpecification)
}



sealed class LoadResult<A>

data class LoadResultValue<A>(val value : A) : LoadResult<A>()

data class LoadResultError<A>(val userMessage : String) : LoadResult<A>()

