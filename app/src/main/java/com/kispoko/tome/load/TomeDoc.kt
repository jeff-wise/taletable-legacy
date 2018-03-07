
package com.kispoko.tome.load


import android.content.Context
import com.kispoko.tome.ApplicationAssets
import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.model.book.Book
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

    var cachedSheetSchema : Schema? = null
    var cachedCampaignSchema : Schema? = null
    var cachedGameSchema : Schema? = null
    var cachedEngineSchema : Schema? = null
    var cachedThemeSchema : Schema? = null
    var cachedBookSchema : Schema? = null


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
                             gameSchema : Schema,
                             engineSchema : Schema,
                             bookSchema : Schema,
                             themeSchema : Schema) : DocLoader<SchemaDoc>
        {
            val docParse = sheetSchema.parseDocument(templateString,
                                                   listOf(campaignSchema, gameSchema, engineSchema, bookSchema, themeSchema))
            return when (docParse)
            {
                is Val -> effValue(docParse.value)
                is Err -> effError(DocumentParseError(sheetName, "sheet", docParse.error))
            }
        }

        fun sheetFromDocument(specDoc : SchemaDoc) : DocLoader<Sheet>
        {
            val sheetParse = Sheet.fromDocument(specDoc)
            return when (sheetParse)
            {
                is Val -> effValue(sheetParse.value)
                is Err -> effError(ValueParseError(sheetName,
                                                          sheetParse.error))
            }
        }

        // DO...
        val schemaDoc = templateFileString ap { fileString ->
                        sheetSchemaLoader(context) ap { sheetSchema ->
                        campaignSchemaLoader(context) ap { campaignSchema ->
                        gameSchemaLoader(context) ap { gameSchema ->
                        engineSchemaLoader(context) ap { engineSchema ->
                        bookSchemaLoader(context) ap { bookSchema ->
                        themeSchemaLoader(context) ap { themeSchema ->
                           templateDocument(fileString, sheetSchema, campaignSchema, gameSchema, engineSchema, bookSchema, themeSchema)
                        } } } } }  } }
        return schemaDoc.apply { sheetFromDocument(it) }
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
                             engineSpec : Schema,
                             gameSpec : Schema) : DocLoader<SchemaDoc>
        {
            val docParse = campaignSpec.parseDocument(templateString,
                                                      listOf(gameSpec, engineSpec))
            return when (docParse)
            {
                is Val -> effValue(docParse.value)
                is Err -> effError(DocumentParseError(campaignName,
                                                             "campaign",
                                                             docParse.error))
            }
        }

        fun campaignFromDocument(specDoc : SchemaDoc) : DocLoader<Campaign>
        {
            val sheetParse = Campaign.fromDocument(specDoc)
            return when (sheetParse)
            {
                is Val -> effValue(sheetParse.value)
                is Err -> effError(ValueParseError(campaignName,
                                                          sheetParse.error))
            }
        }

        // DO...
        return templateFileString
               .applyWith(::templateDocument,
                          campaignSchemaLoader(context),
                          engineSchemaLoader(context),
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
                             engineSchema : Schema,
                             bookSchema : Schema) : DocLoader<SchemaDoc>
        {
            val docParse = gameSchema.parseDocument(templateString, listOf(engineSchema, bookSchema))
            return when (docParse)
            {
                is Val -> effValue(docParse.value)
                is Err -> effError(DocumentParseError(gameName,
                                                             "game",
                                                             docParse.error))
            }
        }

        fun gameFromDocument(specDoc : SchemaDoc) : DocLoader<Game>
        {
            val gameParse = Game.fromDocument(specDoc)
            return when (gameParse)
            {
                is Val -> effValue(gameParse.value)
                is Err -> effError(ValueParseError(gameName,
                                                          gameParse.error))
            }
        }

        // DO...
        return templateFileString
               .applyWith(::templateDocument,
                          gameSchemaLoader(context),
                          engineSchemaLoader(context),
                          bookSchemaLoader(context))
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


    // Load > Book
    // -----------------------------------------------------------------------------------------

    fun loadBook(inputStream : InputStream,
                 bookName : String,
                 context : Context) : DocLoader<Book>
    {
        // LET...
        val templateFileString : DocLoader<String> =
            effValue(inputStream.bufferedReader().use { it.readText() })

        fun templateDocument(templateString : String,
                             bookSchema : Schema,
                             engineSchema : Schema,
                             gameSchema : Schema,
                             themeSchema : Schema,
                             sheetSchema : Schema) : DocLoader<SchemaDoc>
        {
            val docParse = bookSchema.parseDocument(templateString,
                                                   listOf(sheetSchema, themeSchema, engineSchema, gameSchema))
            return when (docParse)
            {
                is Val -> effValue(docParse.value)
                is Err -> effError(DocumentParseError(bookName, "book", docParse.error))
            }
        }

        fun bookFromDocument(specDoc : SchemaDoc) : DocLoader<Book>
        {
            val bookParse = Book.fromDocument(specDoc)
            return when (bookParse)
            {
                is Val -> effValue(bookParse.value)
                is Err -> effError(ValueParseError(bookName, bookParse.error))
            }
        }

        // DO...
        return templateFileString
               .applyWith(::templateDocument,
                          bookSchemaLoader(context),
                          engineSchemaLoader(context),
                          gameSchemaLoader(context),
                          themeSchemaLoader(context),
                          sheetSchemaLoader(context))
               .apply(::bookFromDocument)
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
        if (cachedSheetSchema == null)
        {
            val schemaLoader = loadLuloSchema("sheet", context)
            when (schemaLoader)
            {
                is Val -> {
                    cachedSheetSchema = schemaLoader.value
                    ApplicationLog.event(SchemaLoaded("sheet"))
                }
                is Err -> ApplicationLog.error(schemaLoader.error)
            }
        }

        return TomeDoc.cachedSheetSchema
    }


    /**
     * get the specification in the loader context.
     */
    fun sheetSchemaLoader(context : Context) : DocLoader<Schema>
    {
        val schema = sheetSchema(context)
        return if (schema != null)
            effValue(schema)
        else
            effError(SchemaIsNull("sheet"))
    }


    // Schemas > Campaign
    // -----------------------------------------------------------------------------------------

    /**
     * Get the Sheet specification (Lulo). If it is null, try to load it.
     */
    fun campaignSchema(context : Context) : Schema?
    {
        if (cachedCampaignSchema == null)
        {
            val schemaLoader = loadLuloSchema("campaign", context)
            when (schemaLoader)
            {
                is Val -> {
                    cachedCampaignSchema = schemaLoader.value
                    ApplicationLog.event(SchemaLoaded("campaign"))
                }
                is Err -> ApplicationLog.error(schemaLoader.error)
            }
        }

        return cachedCampaignSchema
    }


    /**
     * get the specification in the loader context.
     */
    fun campaignSchemaLoader(context : Context) : DocLoader<Schema>
    {
        val schema = campaignSchema(context)
        return if (schema != null)
            effValue(schema)
        else
            effError(SchemaIsNull("campaign"))
    }


    // Schemas > Game
    // -----------------------------------------------------------------------------------------

    /**
     * Get the Sheet specification (Lulo). If it is null, try to load it.
     */
    fun gameSchema(context : Context) : Schema?
    {
        if (cachedGameSchema == null)
        {
            val schemaLoader = loadLuloSchema("game", context)
            when (schemaLoader)
            {
                is Val -> {
                    cachedGameSchema = schemaLoader.value
                    ApplicationLog.event(SchemaLoaded("game"))
                }
                is Err -> ApplicationLog.error(schemaLoader.error)
            }
        }

        return cachedGameSchema
    }


    /**
     * get the specification in the loader context.
     */
    fun gameSchemaLoader(context : Context) : DocLoader<Schema>
    {
        val schema = gameSchema(context)
        return if (schema != null)
            effValue(schema)
        else
            effError(SchemaIsNull("game"))
    }


    // Schemas > Engine
    // -----------------------------------------------------------------------------------------

    /**
     * Get the Sheet specification (Lulo). If it is null, try to load it.
     */
    fun engineSchema(context : Context) : Schema?
    {
        if (cachedEngineSchema == null)
        {
            val schemaLoader = loadLuloSchema("engine", context)
            when (schemaLoader)
            {
                is Val -> {
                    cachedEngineSchema = schemaLoader.value
                    ApplicationLog.event(SchemaLoaded("engine"))
                }
                is Err -> ApplicationLog.error(schemaLoader.error)
            }
        }

        return cachedEngineSchema
    }


    /**
     * Get the specification in the loader context.
     */
    fun engineSchemaLoader(context : Context) : DocLoader<Schema>
    {
        val schema = engineSchema(context)
        return if (schema != null)
            effValue(schema)
        else
            effError(SchemaIsNull("engine"))
    }



    // Schemas > Theme
    // -----------------------------------------------------------------------------------------

    /**
     * Get the Sheet specification (Lulo). If it is null, try to load it.
     */
    fun themeSchema(context : Context) : Schema?
    {
        if (cachedThemeSchema == null)
        {
            val schemaLoader = loadLuloSchema("theme", context)
            when (schemaLoader)
            {
                is Val -> {
                    cachedThemeSchema = schemaLoader.value
                    ApplicationLog.event(SchemaLoaded("theme"))
                }
                is Err -> ApplicationLog.error(schemaLoader.error)
            }
        }

        return cachedThemeSchema
    }


    /**
     * get the specification in the loader context.
     */
    fun themeSchemaLoader(context : Context) : DocLoader<Schema>
    {
        val schema = themeSchema(context)
        return if (schema != null)
            effValue(schema)
        else
            effError(SchemaIsNull("theme"))
    }


    // Schemas > Book
    // -----------------------------------------------------------------------------------------

    /**
     * Get the Book specification (Lulo). If it is null, try to load it.
     */
    fun bookSchema(context : Context) : Schema?
    {
        if (cachedBookSchema == null)
        {
            val schemaLoader = loadLuloSchema("book", context)
            when (schemaLoader)
            {
                is Val -> {
                    cachedBookSchema = schemaLoader.value
                    ApplicationLog.event(SchemaLoaded("book"))
                }
                is Err -> ApplicationLog.error(schemaLoader.error)
            }
        }

        return cachedBookSchema
    }


    /**
     * get the specification in the loader context.
     */
    fun bookSchemaLoader(context : Context) : DocLoader<Schema>
    {
        val schema = bookSchema(context)
        return if (schema != null)
            effValue(schema)
        else
            effError(SchemaIsNull("book"))
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

