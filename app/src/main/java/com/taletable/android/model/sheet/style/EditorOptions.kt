
package com.taletable.android.model.sheet.style


import com.taletable.android.lib.Factory
import effect.apply
import effect.effError
import effect.effValue
import effect.split
import lulo.document.*
import lulo.value.*
import lulo.value.UnexpectedType
import maybe.Just
import maybe.Maybe
import maybe.Nothing
import java.io.Serializable



@Suppress("UNCHECKED_CAST")
sealed class EditorOptions : ToDocument, Serializable
{

    companion object : Factory<EditorOptions>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<EditorOptions> =
            when (doc.case())
            {
                "editor_options_subset" -> SubsetEditorOptions.fromDocument(doc) as ValueParser<EditorOptions>
                else                    -> effError(UnknownCase(doc.case(), doc.path))
            }
    }


    fun subsetEditorOptions() : SubsetEditorOptions? = when (this)
    {
        is SubsetEditorOptions -> this
        else                   -> null
    }


}


data class SubsetEditorOptions(val sort : Maybe<SubsetEditorSort>) : EditorOptions()
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<SubsetEditorOptions>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<SubsetEditorOptions> = when (doc)
        {
            is DocDict ->
            {
                apply(::SubsetEditorOptions,
                      // Sort
                      split(doc.maybeAt("sort"),
                            effValue<ValueError,Maybe<SubsetEditorSort>>(Nothing()),
                            { apply(::Just, SubsetEditorSort.fromDocument(it)) })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }


        fun default() = SubsetEditorOptions(Nothing())
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun sort() : Maybe<SubsetEditorSort> = this.sort


}




data class SubsetEditorSort(val sortType : SubsetEditorSortType,
                            val order : SortOrder) : EditorOptions()
{

    // | Constructors
    // -----------------------------------------------------------------------------------------

    companion object : Factory<SubsetEditorSort>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<SubsetEditorSort> = when (doc)
        {
            is DocDict ->
            {
                effect.apply(::SubsetEditorSort,
                      // Sort Type
                      doc.at("sort_type") ap { SubsetEditorSortType.fromDocument(it) },
                      // Order
                      doc.at("order") ap { SortOrder.fromDocument(it) }
                      )
            }
            else       -> effError(lulo.value.UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun sortType() : SubsetEditorSortType = this.sortType


    fun order() : SortOrder = this.order

}



sealed class SubsetEditorSortType : Serializable
{

    // | Constructors
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun fromDocument(doc : SchemaDoc) : ValueParser<SubsetEditorSortType> = when (doc)
        {
            is DocText -> when (doc.text)
            {
                "alphabetical" -> effValue<ValueError,SubsetEditorSortType>(SubsetEditorSortType.Alphabetical)
                else           -> effError<ValueError,SubsetEditorSortType>(
                                    UnexpectedValue("SubsetEditorSortType", doc.text, doc.path))
            }
            else       -> effError(lulo.value.UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }



    object Alphabetical : SubsetEditorSortType()

}


sealed class SortOrder : Serializable
{

    // | Constructors
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun fromDocument(doc : SchemaDoc) : ValueParser<SortOrder> = when (doc)
        {
            is DocText -> when (doc.text)
            {
                "ascending"  -> effValue<ValueError,SortOrder>(SortOrder.Ascending)
                "descending" -> effValue<ValueError,SortOrder>(SortOrder.Descending)
                else         -> effError<ValueError,SortOrder>(
                                    UnexpectedValue("SortOrder", doc.text, doc.path))
            }
            else       -> effError(lulo.value.UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    object Ascending : SortOrder()


    object Descending : SortOrder()
}
