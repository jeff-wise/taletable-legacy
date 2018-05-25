
package com.kispoko.tome.model.engine.tag


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.sql.SQLReal
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.model.sheet.style.Width
import effect.apply
import effect.effError
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.UnknownCase
import lulo.value.ValueParser
import java.io.Serializable



sealed class TagQuery : ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<TagQuery>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<TagQuery> =
            when (doc.case()) {
                "tag_query_and"  -> TagQueryAnd.fromDocument(doc.nextCase()) as ValueParser<TagQuery>
                "tag_query_or"   -> TagQueryOr.fromDocument(doc.nextCase()) as ValueParser<TagQuery>
                "tag_query_tag"  -> TagQueryTag.fromDocument(doc.nextCase()) as ValueParser<TagQuery>
                "tag_query_all"  -> TagQueryAll() as ValueParser<TagQuery>
                else             -> effError(UnknownCase(doc.case(), doc.path))
            }

    }

}


/**
 * Tag Query: Conjunction
 */
data class TagQueryAnd(val queries : List<TagQuery>) : TagQuery()
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<TagQueryAnd>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<TagQueryAnd> = when (doc)
        {
            is DocDict ->
            {
                apply(::TagQueryAnd,
                      // Tag Queries
                      doc.list("queries") ap { it.map { TagQuery.fromDocument(it) } }
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // ----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
       "queries" to DocList(this.queries.map { it.toDocument() })
    ))

}


/**
 * Tag Query: Disjunction
 */
data class TagQueryOr(val queries : List<TagQuery>) : TagQuery()
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<TagQueryOr>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<TagQueryOr> = when (doc)
        {
            is DocDict ->
            {
                apply(::TagQueryOr,
                      // Tag Queries
                      doc.list("queries") ap { it.map { TagQuery.fromDocument(it) } }
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // ----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
       "queries" to DocList(this.queries.map { it.toDocument() })
    ))

}



/**
 * Tag Query: Tag
 */
data class TagQueryTag(val tag : Tag) : TagQuery()
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<TagQueryTag>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<TagQueryTag> = when (doc)
        {
            is DocDict -> apply(::TagQueryTag, doc.at("tag") ap { Tag.fromDocument(it) })
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // ----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
       "tag" to this.tag.toDocument()
    ))

}


/**
 * Tag Query: All
 */
class TagQueryAll : TagQuery()
{
    override fun toDocument() = DocNumber(0.0)
}


