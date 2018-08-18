
package com.taletable.android.model.entity


import android.content.Context
import com.kispoko.culebra.*
import com.taletable.android.app.AppEff
import com.taletable.android.app.AppOfficialError
import com.taletable.android.app.ApplicationLog
import com.taletable.android.model.engine.variable.Variable
import com.taletable.android.official.GameManifestParseError
import com.taletable.android.rts.entity.EntityId
import com.taletable.android.rts.entity.EntityIndexType
import com.taletable.android.rts.entity.EntityType
import effect.*
import lulo.document.DocDict
import lulo.document.DocType
import lulo.document.SchemaDoc
import lulo.document.docType
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import maybe.Just
import maybe.Maybe
import maybe.Nothing
import maybe.maybe



// ---------------------------------------------------------------------------------------------
// | FUNCTIONS
// ---------------------------------------------------------------------------------------------


private var entityManifest : EntityManifest? = null


fun entityManifest(context : Context) : Maybe<EntityManifest>
{
    return if (entityManifest == null) {
        val manifest = loadEntityManifest(context)
        when (manifest) {
            is Val -> Just(manifest.value)
            is Err -> {
                ApplicationLog.error(manifest.error)
                Nothing()
            }
        }
    } else {
        Nothing()
    }
}


fun loadEntityManifest(context : Context) : AppEff<EntityManifest>
{
    val manifestFilePath = "official/entity_manifest.yaml"

    val manifestParser = parseYaml(context.assets.open(manifestFilePath),
                                   EntityManifest.Companion::fromYaml)

    return when (manifestParser)
    {
        is Val -> effValue(manifestParser.value)
        is Err -> effError(AppOfficialError(
                    GameManifestParseError(manifestParser.error.toString())))
    }
}


// ---------------------------------------------------------------------------------------------
// | DATA TYPES
// ---------------------------------------------------------------------------------------------

data class EntityManifest(val persistedEntities : List<PersistedEntity>)
{

    // | Properties
    // -----------------------------------------------------------------------------------------

    private val entityById : MutableMap<EntityId,PersistedEntity> =
            persistedEntities.associateBy { it.entityId }
                    as MutableMap<EntityId,PersistedEntity>


    // | Constructors
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun fromYaml(yamlValue : YamlValue) : YamlParser<EntityManifest> =
            when (yamlValue)
            {
                is YamlDict ->
                {
                    effect.apply(::EntityManifest,
                          // Persisted Entities
                          yamlValue.array("persisted_entities") ap {
                              it.mapApply { PersistedEntity.fromYaml(it) }}
                    )
                }
                else -> error(UnexpectedTypeFound(YamlType.DICT, yamlType(yamlValue), yamlValue.path))
            }
    }


    // | Methods
    // -----------------------------------------------------------------------------------------

    fun persistedEntity(entityId : EntityId) : Maybe<PersistedEntity> =
            maybe(this.entityById[entityId])

}



data class PersistedEntity(val entityId : EntityId,
                           val entityType : EntityType,
                           val path : String,
                           val name : String,
                           val category : String,
                           val indexes : List<PersistedEntityIndex>)
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun fromYaml(yamlValue : YamlValue) : YamlParser<PersistedEntity> =
            when (yamlValue)
            {
                is YamlDict ->
                {
                    apply(::PersistedEntity,
                          // Entity Id
                          yamlValue.at("entity_id") ap { EntityId.fromYaml(it) },
                          // Entity Type
                          yamlValue.at("entity_type") ap { EntityType.fromYaml(it) },
                          // Path
                          yamlValue.text("path"),
                          // Name
                          yamlValue.text("name"),
                          // Category
                          yamlValue.text("category"),
                          // Indexes
                          split(yamlValue.maybeArray("indexes"),
                                effValue(listOf()),
                                { it.mapApply { PersistedEntityIndex.fromYaml(it) }})
                          )
                }
                else -> error(UnexpectedTypeFound(YamlType.DICT, yamlType(yamlValue), yamlValue.path))
            }
    }

}


data class PersistedEntityIndex(val entityId : EntityId,
                                val indexType : EntityIndexType,
                                val path : String)
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun fromYaml(yamlValue : YamlValue) : YamlParser<PersistedEntityIndex> =
            when (yamlValue)
            {
                is YamlDict ->
                {
                    apply(::PersistedEntityIndex,
                          // Entity Id
                          yamlValue.at("entity_id") ap { EntityId.fromYaml(it) },
                          // Index Type
                          yamlValue.at("index_type") ap { EntityIndexType.fromYaml(it) },
                          // Path
                          yamlValue.text("path")
                          )
                }
                else -> error(UnexpectedTypeFound(YamlType.DICT, yamlType(yamlValue), yamlValue.path))
            }
    }

}


/**
 * Variable Index
 */
data class VariableIndex(val variables : List<Variable>)
{

    companion object
    {
        fun fromDocument(doc : SchemaDoc) : ValueParser<VariableIndex> = when (doc)
        {
            is DocDict ->
            {
                apply(::VariableIndex,
                      // Variables
                      doc.list("variables") ap { docList ->
                          docList.map { Variable.fromDocument(it) } }
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }

}

