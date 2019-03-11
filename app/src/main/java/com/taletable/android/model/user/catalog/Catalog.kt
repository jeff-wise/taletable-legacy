
package com.taletable.android.model.user.catalog


import java.io.Serializable



data class Catalog(val collections : List<BookmarkCollection>) : Serializable
{

    // | CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun empty() = Catalog(listOf())

    }

}