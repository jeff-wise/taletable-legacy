
package com.kispoko.tome.model.campaign;


import com.kispoko.tome.lib.functor.PrimitiveFunctor;
import com.kispoko.tome.lib.model.Model;

import java.io.Serializable;
import java.util.UUID;



/**
 * Universe
 *
 * A game world.
 */
public class Universe
                      implements Serializable
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    // > Model
    // -----------------------------------------------------------------------------------------

    private UUID                            id;


    // > Functors
    // -----------------------------------------------------------------------------------------

    private PrimitiveFunctor<String>        name;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public Universe()
    {
        this.id         = null;

        this.name       = new PrimitiveFunctor<>(null, String.class);
    }


    public Universe(UUID id, String name)
    {
        this.id         = id;

        this.name       = new PrimitiveFunctor<>(name, String.class);
    }



    // API
    // -----------------------------------------------------------------------------------------

    // API > Model
    // ------------------------------------------------------------------------------------------

    // API > Model > Id
    // ------------------------------------------------------------------------------------------

    public UUID getId()
    {
        return this.id;
    }


    public void setId(UUID id)
    {
        this.id = id;
    }


    // API > Model > On Load
    // ------------------------------------------------------------------------------------------

    /**
     * This method is called when the Sheet is completely loaded for the first time.
     */
    public void onLoad() { }


    // API > State
    // -----------------------------------------------------------------------------------------

    // API > State > Name
    // -----------------------------------------------------------------------------------------

    /**
     * The universe's name.
     * @return The name string.
     */
    public String name()
    {
        return this.name.getValue();
    }


}
