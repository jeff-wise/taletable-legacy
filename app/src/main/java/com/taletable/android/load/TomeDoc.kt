
package com.taletable.android.load


import android.content.Context
import com.taletable.android.ApplicationAssets
import com.taletable.android.app.ApplicationLog
import com.taletable.android.app.assetInputStream
import com.taletable.android.model.book.Book
import com.taletable.android.model.campaign.Campaign
import com.taletable.android.model.entity.VariableIndex
import com.taletable.android.model.feed.Feed
import com.taletable.android.model.game.Game
import com.taletable.android.model.sheet.Sheet
import com.taletable.android.model.sheet.group.GroupIndex
import com.taletable.android.model.theme.Theme
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

    var cachedSheetSchema           : Schema? = null
    var cachedCampaignSchema        : Schema? = null
    var cachedGameSchema            : Schema? = null
    var cachedEngineSchema          : Schema? = null
    var cachedThemeSchema           : Schema? = null
    var cachedBookSchema            : Schema? = null
    var cachedGroupIndexSchema      : Schema? = null
    var cachedVariableIndexSchema   : Schema? = null
    var cachedFeedSchema            : Schema? = null
    var cachedAppSchema             : Schema? = null


    // -----------------------------------------------------------------------------------------
    // LOAD
    // -----------------------------------------------------------------------------------------

    fun templateFileString(filepath : String, context : Context) : DocLoader<String> =
        assetInputStream(context, "official/$filepath").apply {
            effValue<DocLoadError,String>(it.bufferedReader().use { it.readText() })
        }


    // Load > Sheet
    // -----------------------------------------------------------------------------------------

    fun loadSheet(filepath : String,
                  context : Context) : DocLoader<Sheet>
    {
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
                is Err -> effError(DocumentParseError(filepath, "sheet", docParse.error))
            }
        }

        fun sheetFromDocument(specDoc : SchemaDoc) : DocLoader<Sheet>
        {
            val sheetParse = Sheet.fromDocument(specDoc)
            return when (sheetParse)
            {
                is Val -> effValue(sheetParse.value)
                is Err -> effError(ValueParseError(filepath, sheetParse.error))
            }
        }

        // DO...
        val schemaDoc = templateFileString(filepath, context) ap { fileString ->
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

    fun loadCampaign(filepath : String,
                     context : Context) : DocLoader<Campaign>
    {

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
                is Err -> effError(DocumentParseError(filepath, "campaign", docParse.error))
            }
        }

        fun campaignFromDocument(specDoc : SchemaDoc) : DocLoader<Campaign>
        {
            val sheetParse = Campaign.fromDocument(specDoc)
            return when (sheetParse)
            {
                is Val -> effValue(sheetParse.value)
                is Err -> effError(ValueParseError(filepath, sheetParse.error))
            }
        }

        // DO...
        return templateFileString(filepath, context)
               .applyWith(::templateDocument,
                          campaignSchemaLoader(context),
                          engineSchemaLoader(context),
                          gameSchemaLoader(context))
               .apply(::campaignFromDocument)
    }


    // Load > Game
    // -----------------------------------------------------------------------------------------

    fun loadGame(filepath : String,
                 context : Context) : DocLoader<Game>
    {
        fun templateDocument(templateString : String,
                             gameSchema : Schema,
                             engineSchema : Schema,
                             sheetSchema : Schema,
                             themeSchema : Schema,
                             bookSchema : Schema) : DocLoader<SchemaDoc>
        {
            val docParse = gameSchema.parseDocument(templateString, listOf(engineSchema, themeSchema, bookSchema, sheetSchema))
            return when (docParse)
            {
                is Val -> effValue(docParse.value)
                is Err -> effError(DocumentParseError(filepath, "game", docParse.error))
            }
        }

        fun gameFromDocument(specDoc : SchemaDoc) : DocLoader<Game>
        {
            val gameParse = Game.fromDocument(specDoc)
            return when (gameParse)
            {
                is Val -> effValue(gameParse.value)
                is Err -> effError(ValueParseError(filepath, gameParse.error))
            }
        }

        // DO...
        return templateFileString(filepath, context)
               .applyWith(::templateDocument,
                          gameSchemaLoader(context),
                          engineSchemaLoader(context),
                          sheetSchemaLoader(context),
                          themeSchemaLoader(context),
                          bookSchemaLoader(context))
               .apply(::gameFromDocument)
    }


    // Load > Theme
    // -----------------------------------------------------------------------------------------

    fun loadTheme(filepath : String,
                  context : Context) : DocLoader<Theme>
    {
        fun templateDocument(templateString : String, gameSpec : Schema) : DocLoader<SchemaDoc>
        {
            val docParse = gameSpec.parseDocument(templateString, listOf())
            return when (docParse)
            {
                is Val -> effValue(docParse.value)
                is Err -> effError(DocumentParseError(filepath, "theme", docParse.error))
            }
        }

        fun themeFromDocument(specDoc : SchemaDoc) : DocLoader<Theme>
        {
            val themeParser = Theme.fromDocument(specDoc)
            return when (themeParser)
            {
                is Val -> effValue(themeParser.value)
                is Err -> effError(ValueParseError(filepath, themeParser.error))
            }
        }

        // DO...
        return templateFileString(filepath, context)
               .applyWith(::templateDocument,
                          themeSchemaLoader(context))
               .apply(::themeFromDocument)
    }


    // Load > Book
    // -----------------------------------------------------------------------------------------

    fun loadBook(filepath : String, context : Context) : DocLoader<Book>
    {
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
                is Err -> effError(DocumentParseError(filepath, "book", docParse.error))
            }
        }

        fun bookFromDocument(specDoc : SchemaDoc) : DocLoader<Book>
        {
            val bookParse = Book.fromDocument(specDoc)
            return when (bookParse)
            {
                is Val -> effValue(bookParse.value)
                is Err -> effError(ValueParseError(filepath, bookParse.error))
            }
        }

        // DO...
        return templateFileString(filepath, context)
               .applyWith(::templateDocument,
                          bookSchemaLoader(context),
                          engineSchemaLoader(context),
                          gameSchemaLoader(context),
                          themeSchemaLoader(context),
                          sheetSchemaLoader(context))
               .apply(::bookFromDocument)
    }


    // Load > Group Index
    // -----------------------------------------------------------------------------------------

    fun loadGroupIndex(filepath : String,
                       context : Context) : DocLoader<GroupIndex>
    {
        fun templateDocument(templateString : String,
                             groupIndexSchema : Schema,
                             themeSchema : Schema,
                             engineSchema : Schema,
                             bookSchema : Schema,
                             sheetSchema : Schema) : DocLoader<SchemaDoc>
        {
            val docParse = groupIndexSchema.parseDocument(templateString,
                                    listOf(themeSchema, engineSchema, sheetSchema, bookSchema))
            return when (docParse)
            {
                is Val -> effValue(docParse.value)
                is Err -> effError(DocumentParseError("Group Index for $filepath", "group_index", docParse.error))
            }
        }

        fun groupIndexFromDocument(specDoc : SchemaDoc) : DocLoader<GroupIndex>
        {
            val groupIndexParse = GroupIndex.fromDocument(specDoc)
            return when (groupIndexParse)
            {
                is Val -> effValue(groupIndexParse.value)
                is Err -> effError(ValueParseError("Group Index for $filepath", groupIndexParse.error))
            }
        }

        // DO...
        return templateFileString(filepath, context)
               .applyWith(::templateDocument,
                          groupIndexSchemaLoader(context),
                          themeSchemaLoader(context),
                          engineSchemaLoader(context),
                          bookSchemaLoader(context),
                          sheetSchemaLoader(context))
               .apply(::groupIndexFromDocument)
    }


    // Load > Variable Index
    // -----------------------------------------------------------------------------------------

    fun loadVariableIndex(filepath : String,
                          context : Context) : DocLoader<VariableIndex>
    {
        fun templateDocument(templateString : String,
                             variableIndexSchema : Schema,
                             engineSchema : Schema,
                             bookSchema : Schema) : DocLoader<SchemaDoc>
        {
            val docParse = variableIndexSchema.parseDocument(templateString,
                                    listOf(engineSchema, bookSchema))
            return when (docParse)
            {
                is Val -> effValue(docParse.value)
                is Err -> effError(DocumentParseError("Variable Index for $filepath", "variable_index", docParse.error))
            }
        }

        fun variableIndexFromDocument(specDoc : SchemaDoc) : DocLoader<VariableIndex>
        {
            val variableIndexParse = VariableIndex.fromDocument(specDoc)
            return when (variableIndexParse)
            {
                is Val -> effValue(variableIndexParse.value)
                is Err -> effError(ValueParseError("Variable Index for $filepath", variableIndexParse.error))
            }
        }

        // DO...
        return templateFileString(filepath, context)
               .applyWith(::templateDocument,
                          variableIndexSchemaLoader(context),
                          engineSchemaLoader(context),
                          bookSchemaLoader(context))
               .apply(::variableIndexFromDocument)
    }


    // Load > Feed
    // -----------------------------------------------------------------------------------------

    fun loadFeed(filepath : String,
                 context : Context) : DocLoader<Feed>
    {

        fun templateDocument(templateString : String,
                             feedSchema : Schema,
                             appSchema : Schema,
                             themeSchema : Schema,
                             engineSchema : Schema,
                             sheetSchema : Schema) : DocLoader<SchemaDoc>
        {
            val docParse = feedSchema.parseDocument(templateString, listOf(engineSchema, appSchema, sheetSchema, themeSchema))
            return when (docParse) {
                is Val -> effValue(docParse.value)
                is Err -> effError(DocumentParseError("Feed at $filepath", "official/feed", docParse.error))
            }
        }

        fun feedFromDocument(specDoc : SchemaDoc) : DocLoader<Feed>
        {
            val feedParse = Feed.fromDocument(specDoc)
            return when (feedParse)
            {
                is Val -> effValue(feedParse.value)
                is Err -> effError(ValueParseError("Feed at $filepath", feedParse.error))
            }
        }

        // DO...
        return templateFileString(filepath, context)
               .applyWith(::templateDocument,
                          feedSchemaLoader(context),
                          appSchemaLoader(context),
                          themeSchemaLoader(context),
                          engineSchemaLoader(context),
                          sheetSchemaLoader(context))
               .apply(::feedFromDocument)
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


    // Schemas > Group Index
    // -----------------------------------------------------------------------------------------

    /**
     * Get the Group Index specification (Lulo). If it is null, try to load it.
     */
    fun groupIndexSchema(context : Context) : Schema?
    {
        if (cachedGroupIndexSchema == null)
        {
            val schemaLoader = loadLuloSchema("group_index", context)
            when (schemaLoader)
            {
                is Val -> {
                    cachedGroupIndexSchema = schemaLoader.value
                    ApplicationLog.event(SchemaLoaded("group_index"))
                }
                is Err -> ApplicationLog.error(schemaLoader.error)
            }
        }

        return cachedGroupIndexSchema
    }


    /**
     * get the specification in the loader context.
     */
    fun groupIndexSchemaLoader(context : Context) : DocLoader<Schema>
    {
        val schema = groupIndexSchema(context)
        return if (schema != null)
            effValue(schema)
        else
            effError(SchemaIsNull("group_index"))
    }


    // Schemas > Variable Index
    // -----------------------------------------------------------------------------------------

    /**
     * Get the Variable Index specification (Lulo). If it is null, try to load it.
     */
    fun variableIndexSchema(context : Context) : Schema?
    {
        if (cachedVariableIndexSchema == null)
        {
            val schemaLoader = loadLuloSchema("variable_index", context)
            when (schemaLoader)
            {
                is Val -> {
                    cachedVariableIndexSchema = schemaLoader.value
                    ApplicationLog.event(SchemaLoaded("variable_index"))
                }
                is Err -> ApplicationLog.error(schemaLoader.error)
            }
        }

        return cachedVariableIndexSchema
    }


    /**
     * get the specification in the loader context.
     */
    fun variableIndexSchemaLoader(context : Context) : DocLoader<Schema>
    {
        val schema = variableIndexSchema(context)
        return if (schema != null)
            effValue(schema)
        else
            effError(SchemaIsNull("variable_index"))
    }


    // Schemas > Feed
    // -----------------------------------------------------------------------------------------

    /**
     * Get the Group Index specification (Lulo). If it is null, try to load it.
     */
    fun feedSchema(context : Context) : Schema?
    {
        if (cachedFeedSchema == null)
        {
            val schemaLoader = loadLuloSchema("feed", context)
            when (schemaLoader)
            {
                is Val -> {
                    cachedFeedSchema = schemaLoader.value
                    ApplicationLog.event(SchemaLoaded("feed"))
                }
                is Err -> ApplicationLog.error(schemaLoader.error)
            }
        }

        return cachedFeedSchema
    }


    /**
     * Get the specification in the loader context.
     */
    fun feedSchemaLoader(context : Context) : DocLoader<Schema>
    {
        val schema = feedSchema(context)
        return if (schema != null)
            effValue(schema)
        else
            effError(SchemaIsNull("official/feed"))
    }


    // Schemas > App
    // -----------------------------------------------------------------------------------------

    /**
     * Get the App Schema. If it is null, try to load it.
     */
    fun appSchema(context : Context) : Schema?
    {
        if (cachedAppSchema == null)
        {
            val schemaLoader = loadLuloSchema("app", context)
            when (schemaLoader)
            {
                is Val -> {
                    cachedAppSchema = schemaLoader.value
                    ApplicationLog.event(SchemaLoaded("app"))
                }
                is Err -> ApplicationLog.error(schemaLoader.error)
            }
        }

        return cachedAppSchema
    }


    /**
     * Get the schema in the loader context.
     */
    fun appSchemaLoader(context : Context) : DocLoader<Schema>
    {
        val schema = appSchema(context)
        return if (schema != null)
            effValue(schema)
        else
            effError(SchemaIsNull("app"))
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

