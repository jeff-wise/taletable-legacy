
package com.kispoko.tome.model.game;


import com.kispoko.tome.lib.functor.CollectionFunctor;
import com.kispoko.tome.lib.functor.PrimitiveFunctor;
import com.kispoko.tome.lib.model.Model;
import com.kispoko.tome.model.Author;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.UUID;



/**
 * Game Description
 */
//public class GameDescription
//                             implements Serializable
//{
//
//    // PROPERTIES
//    // -----------------------------------------------------------------------------------------
//
//    // > Model
//    // -----------------------------------------------------------------------------------------
//
//    private UUID                        id;
//
//
//    // > Functors
//    // -----------------------------------------------------------------------------------------
//
//    private PrimitiveFunctor<String>    summary;
//    private CollectionFunctor<Author>   authors;
//
//
//    // CONSTRUCTORS
//    // -----------------------------------------------------------------------------------------
//
//    public GameDescription()
//    {
//        this.id         = null;
//
//        this.summary    = new PrimitiveFunctor<>(null, String.class);
//        this.authors    = CollectionFunctor.empty(Author.class);
//    }
//
//
//    public GameDescription(UUID id,
//                           String summary,
//                           List<Author> authors)
//    {
//        this.id         = id;
//
//        this.summary    = new PrimitiveFunctor<>(summary, String.class);
//        this.authors    = CollectionFunctor.full(authors, Author.class);
//    }
//
//
//    // API
//    // -----------------------------------------------------------------------------------------
//
//    // > Model
//    // -----------------------------------------------------------------------------------------
//
//    // ** Id
//    // -----------------------------------------------------------------------------------------
//
//    /**
//     * Get the model identifier.
//     * @return The model UUID.
//     */
//    public UUID getId()
//    {
//        return this.id;
//    }
//
//
//    /**
//     * Set the model identifier.
//     * @param id The new model UUID.
//     */
//    public void setId(UUID id)
//    {
//        this.id = id;
//    }
//
//
//    // ** On Load
//    // -----------------------------------------------------------------------------------------
//
//    /**
//     * This method is called when the RulesEngine is completely loaded for the first time.
//     */
//    public void onLoad() { }
//
//
//    // API > State
//    // -----------------------------------------------------------------------------------------
//
//    // API > State > Summary
//    // -----------------------------------------------------------------------------------------
//
//    /**
//     * A brief description of the game.
//     * @return The game summary.
//     */
//    public String summary()
//    {
//        return this.summary.getValue();
//    }
//
//
//    // API > State > Authors
//    // -----------------------------------------------------------------------------------------
//
//    /**
//     * The authors of the game.
//     * @return A unmodifiable list of the game's authors.
//     */
//    public List<Author> authors()
//    {
//        return Collections.unmodifiableList(this.authors.getValue());
//    }
//
//}
