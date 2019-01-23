

package com.taletable.android.model.entity


import com.taletable.android.lib.Factory
import effect.apply
import effect.effError
import effect.effValue
import effect.split
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.UnknownCase
import lulo.value.ValueParser
import java.io.Serializable



/**
 * Search Data
 */
data class SearchData(val attributes : List<SearchAttribute>)
                       : ToDocument, Serializable
{

    // | Constructors
    // -----------------------------------------------------------------------------------------

    companion object : Factory<SearchData>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<SearchData> = when (doc)
        {
            is DocDict ->
            {
                apply(::SearchData,
                      // Attributes
                      split(doc.maybeList("attributes"),
                            effValue(listOf()),
                            { it.map { SearchAttribute.fromDocument(it) } })
                     )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun empty() : SearchData = SearchData(listOf())

    }


    // | To Document
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "attributes" to DocList(this.attributes.map { it.toDocument() } )
    ))


}



sealed class SearchAttribute : ToDocument
{


    // | Constructors
    // -----------------------------------------------------------------------------------------

    companion object : Factory<SearchAttribute>
    {
        override fun fromDocument(doc : SchemaDoc): ValueParser<SearchAttribute> =
            when (doc.case())
            {
                "search_data_attribute_keyword" -> SearchAttributeKeyword.fromDocument(doc) as ValueParser<SearchAttribute>
                else                            -> effError(UnknownCase(doc.case(), doc.path))
            }
    }

}



/**
 * Search Attribute: Keyword
 */
data class SearchAttributeKeyword(val keyword : SearchKeyword,
                                  val weight : SearchWeight)
                                   : SearchAttribute(), Serializable
{

    // | Constructors
    // -----------------------------------------------------------------------------------------

    companion object : Factory<SearchAttributeKeyword>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<SearchAttributeKeyword> = when (doc)
        {
            is DocDict ->
            {
                apply(::SearchAttributeKeyword,
                      // Keyword
                      doc.at("keyword") apply { SearchKeyword.fromDocument(it) },
                      // Weight
                      doc.at("weight") apply { SearchWeight.fromDocument(it) }
                )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // | To Document
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "keyword" to this.keyword.toDocument(),
        "weight" to this.weight.toDocument()
    ))

}



/**
 * Search Keyword
 */
data class SearchKeyword(val value : String) : ToDocument, Serializable
{

    // | Constructors
    // -----------------------------------------------------------------------------------------

    companion object : Factory<SearchKeyword>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<SearchKeyword> = when (doc)
        {
            is DocText -> effValue(SearchKeyword(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // | To Document
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocText(this.value)

}


/**
 * Search Weight
 */
data class SearchWeight(val value : Double) : ToDocument, Serializable
{

    // | Constructors
    // -----------------------------------------------------------------------------------------

    companion object : Factory<SearchWeight>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<SearchWeight> = when (doc)
        {
            is DocNumber -> effValue(SearchWeight(doc.number))
            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
        }
    }


    // | To Document
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocNumber(this.value)

}
