
package com.taletable.android.model.session


import android.content.Context
import com.kispoko.culebra.*
import com.taletable.android.rts.session.Session
import com.taletable.android.rts.session.SessionId
import effect.Err
import effect.Val
import maybe.Just
import maybe.Maybe
import maybe.Nothing
import maybe.maybe
import java.io.IOException



// ---------------------------------------------------------------------------------------------
// | FUNCTIONS
// ---------------------------------------------------------------------------------------------



//val sessionManifestCache : MutableMap<SessionId,SessionManifest> = mutableMapOf()

var sessionManifest : Maybe<SessionManifest> = Nothing()


/**
 * Get an official session.
 */
//fun officialSession(gameId : EntityId, sessionId : SessionId, context : Context) : Maybe<Session> =
//    sessionManifest(gameId, context).apply { manifest ->
//        Log.d("***SESSION", "session manifest exist")
//        manifest.session(sessionId)
//    }


fun sessionManifest(context : Context) : Maybe<SessionManifest>
{
    when (sessionManifest) {
        is Just -> return sessionManifest
    }

    val filePath = "official/session_manifest.yaml"


    val fileInputStream = try {
        context.assets?.open(filePath)
    }
    catch (e : IOException) {
        null
    }

    return maybe(fileInputStream).apply {
        val manifestParser : YamlParser<SessionManifest> = parseYaml(it, SessionManifest.Companion::fromYaml)
        when (manifestParser) {
            is Val -> {
                sessionManifest = Just(manifestParser.value)
                Just(manifestParser.value)
            }
            is Err -> {
                Nothing<SessionManifest>()
            }
        }
    }
}


// ---------------------------------------------------------------------------------------------
// | DATA TYPES
// ---------------------------------------------------------------------------------------------

data class SessionManifest(val summaries : List<Session>)
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    private val sessionById : MutableMap<SessionId,Session> =
            summaries.associateBy { it.sessionId }
                    as MutableMap<SessionId,Session>

//    private val loadersByEntityId : MutableMap<EntityKindId,MutableList<Session>> = mutableMapOf()


//    init {
//
//        summaries.forEach { loader ->
//            val kindId = loader.entityKindId
//            if (!loadersByEntityId.containsKey(kindId))
//                loadersByEntityId[kindId] = mutableListOf()
//            loadersByEntityId[kindId]!!.add(loader)
//        }
//    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun fromYaml(yamlValue : YamlValue) : YamlParser<SessionManifest> =
            when (yamlValue)
            {
                is YamlDict ->
                {
                    effect.apply(::SessionManifest,
                          // Summaries
                          yamlValue.array("summaries") ap {
                              it.mapApply { Session.fromYaml(it) }}
                    )
                }
                else -> error(UnexpectedTypeFound(YamlType.DICT, yamlType(yamlValue), yamlValue.path))
            }
    }


    // -----------------------------------------------------------------------------------------
    // METHODS
    // -----------------------------------------------------------------------------------------

    fun session(sessionId : SessionId) : Maybe<Session> =
            maybe(this.sessionById[sessionId])

//
//    fun sessionLoaders(entityKindId : EntityKindId) : List<SessionLoader> =
//            this.loadersByEntityId[entityKindId] ?: listOf()


}


