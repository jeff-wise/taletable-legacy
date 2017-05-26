
package com.kispoko.tome.model.campaign;


import com.kispoko.tome.lib.functor.PrimitiveFunctor;
import com.kispoko.tome.lib.model.Model;

import java.io.Serializable;
import java.util.UUID;



/**
 * Engine Element Selector
 */
public class EngineElementSelector extends Model
                                   implements Serializable
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    // > Model
    // -----------------------------------------------------------------------------------------

    private UUID                            id;


    // > Functors
    // -----------------------------------------------------------------------------------------

    private PrimitiveFunctor<SelectorType>  type;

    private PrimitiveFunctor<String[]>      whitelist;
    private PrimitiveFunctor<String[]>      blacklist;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public EngineElementSelector()
    {
        this.id             = null;

        this.whitelist      = new PrimitiveFunctor<>(null, String[].class);
        this.blacklist      = new PrimitiveFunctor<>(null, String[].class);
    }


    public EngineElementSelector(UUID id, String[] elements, SelectorType type)
    {
        this.id             = id;

        this.type           = new PrimitiveFunctor<>(type, SelectorType.class);

        switch (type)
        {
            case WHITELIST:
                this.whitelist  = new PrimitiveFunctor<>(elements, String[].class);
                break;
            case BLACKLIST:
                this.blacklist  = new PrimitiveFunctor<>(elements, String[].class);
                break;
        }
    }


    // API
    // -----------------------------------------------------------------------------------------

    // > Model
    // -----------------------------------------------------------------------------------------

    // ** Id
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


    // ** On Load
    // -----------------------------------------------------------------------------------------

    /**
     * This method is called when the RulesEngine is completely loaded for the first time.
     */
    public void onLoad() { }


    // SELECTOR TYPE
    // -----------------------------------------------------------------------------------------

    public enum SelectorType
    {

        // VALUES
        // -------------------------------------------------------------------------------------

        WHITELIST,
        BLACKLIST

    }


}
