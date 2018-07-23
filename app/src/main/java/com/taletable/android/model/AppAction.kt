
package com.taletable.android.model


import com.taletable.android.model.app.NewsArticleId
import com.taletable.android.rts.session.SessionId
import effect.apply
import effect.effError
import lulo.document.*
import lulo.value.*
import lulo.value.UnexpectedType
import java.io.Serializable



sealed class AppAction : ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun fromDocument(doc : SchemaDoc) : ValueParser<AppAction> = when (doc.case())
        {
            "app_action_open_news_article" -> AppActionOpenNewsArticle.fromDocument(doc.nextCase()) as ValueParser<AppAction>
            "app_action_open_session"      -> AppActionOpenSession.fromDocument(doc.nextCase()) as ValueParser<AppAction>
            else                           -> effError(UnknownCase(doc.case(), doc.path))
        }
    }

}


/**
 * App Action: Open News Article
 */
data class AppActionOpenNewsArticle(private val newsArticleId : NewsArticleId) : AppAction()
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun fromDocument(doc : SchemaDoc) : ValueParser<AppActionOpenNewsArticle> = when (doc)
        {
            is DocDict ->
            {
                apply(::AppActionOpenNewsArticle,
                      // News Article Id
                      doc.at("news_article_id").apply { NewsArticleId.fromDocument(it) }
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "news_article_id" to this.newsArticleId.toDocument()
    ))

}


/**
 * App Action: Open Session
 */
data class AppActionOpenSession(private val sessionId : SessionId) : AppAction()
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun fromDocument(doc : SchemaDoc) : ValueParser<AppActionOpenSession> = when (doc)
        {
            is DocDict ->
            {
                apply(::AppActionOpenSession,
                      // Session Id
                      doc.at("session_id").apply { SessionId.fromDocument(it) }
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "session_id" to this.sessionId.toDocument()
    ))

}
