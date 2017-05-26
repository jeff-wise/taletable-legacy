
package com.kispoko.tome.model.campaign;


import android.support.annotation.Nullable;

import com.kispoko.tome.lib.functor.PrimitiveFunctor;
import com.kispoko.tome.lib.model.Model;

import java.io.Serializable;
import java.util.UUID;



/**
 * Game Master
 */
public class GameMaster extends Model
                        implements Serializable
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    // > Model
    // -----------------------------------------------------------------------------------------

    private UUID                        id;

    // > Functors
    // -----------------------------------------------------------------------------------------

    private PrimitiveFunctor<String>    name;
    private PrimitiveFunctor<UUID>      userId;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public GameMaster()
    {
        this.id             = null;

        this.name           = new PrimitiveFunctor<>(null, String.class);
        this.userId         = new PrimitiveFunctor<>(null, UUID.class);
    }


    public GameMaster(UUID id, String name, UUID userId)
    {
        this.id             = id;

        this.name           = new PrimitiveFunctor<>(name, String.class);
        this.userId         = new PrimitiveFunctor<>(userId, UUID.class);
    }


    // API
    // -----------------------------------------------------------------------------------------

    // API > Model
    // -----------------------------------------------------------------------------------------

    // API > Model > Id
    // -----------------------------------------------------------------------------------------

    /**
     * Get the model identifier.
     * @return The model UUID.
     */
    public UUID getId()
    {
        return this.id;
    }


    /**
     * Set the model identifier.
     * @param id The new model UUID.
     */
    public void setId(UUID id)
    {
        this.id = id;
    }


    // API > Model > On Load
    // -----------------------------------------------------------------------------------------

    /**
     * This method is called when the RulesEngine is completely loaded for the first time.
     */
    public void onLoad() { }


    // API > State
    // -----------------------------------------------------------------------------------------

    // API > State > Summary
    // -----------------------------------------------------------------------------------------

    /**
     * The game master's name.
     * @return The game master name.
     */
    public String name()
    {
        return this.name.getValue();
    }


    // API > State > User ID
    // -----------------------------------------------------------------------------------------

    /**
     * The id of the user object linked to the game mater.
     * @return The user id.
     */
    @Nullable
    public UUID userId()
    {
        return this.userId.getValue();
    }

}
