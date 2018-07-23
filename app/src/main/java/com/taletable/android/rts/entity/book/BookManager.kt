
package com.taletable.android.rts.entity.book


import com.taletable.android.model.book.Book
import com.taletable.android.rts.entity.EntityState


/**
 * Book Manager
 */
//object BookManager
//{
//
//    // -----------------------------------------------------------------------------------------
//    // PROPERTIES
//    // -----------------------------------------------------------------------------------------
//
//    val bookRecordById : MutableMap<BookId,BookRecord> = mutableMapOf()
//
//
//    // -----------------------------------------------------------------------------------------
//    // PROPERTIES
//    // -----------------------------------------------------------------------------------------
//
//    suspend fun gameBookWithId(gameId : GameId, bookId : BookId, context : Context) : Book? = run(CommonPool,
//    {
//        if (!bookRecordById.containsKey(bookId))
//            loadOfficialBook(gameId, bookId, context)
//
//        bookRecordById[bookId]
//    })
//
//
//    fun loadOfficialBook(gameId : GameId,
//                         bookId : BookId,
//                         context : Context)
//    {
//
//        val bookLoader = assetInputStream(context, officialBookFilePath(gameId, bookId))
//                            .apply { TomeDoc.loadBook(it, bookId.value, context) }
//        when (bookLoader)
//        {
//            is Val ->
//            {
//                val book = bookLoader.value
//                this.bookRecordById.put(book.bookId)
//                ApplicationLog.event(OfficialGameBookLoaded(gameId.value, bookId.value))
//            }
//            is Err -> ApplicationLog.error(bookLoader.error)
//        }
//    }
//
//
//    fun officialBookFilePath(gameId : GameId, bookId : BookId) =
//            "official/" + gameId.value +
//            "/books/" + bookId.value +  ".yaml"
//
//
//    /**
//     * Get an input stream for an Android asset.
//     */
//    fun bookFileInputStream(context : Context, assetFilePath : String) : DocLoader<InputStream> =
//        try {
//            effValue(context.assets.open(assetFilePath))
//        }
//        catch (e : IOException) {
//            effError(CannotOpenTemplateFile(assetFilePath))
//        }
//
//
//
//}


data class BookRecord(val book : Book,
                      val engineState : EntityState)
