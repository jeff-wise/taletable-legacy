
package com.kispoko.tome.model;


import android.support.annotation.Nullable;

import com.kispoko.tome.lib.functor.PrimitiveFunctor;
import com.kispoko.tome.lib.model.Model;

import java.io.Serializable;
import java.util.UUID;



/**
 * Author
 *
 * An author of a game, campaign, or other Tome object.
 */
public class Author
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
    private PrimitiveFunctor<String>    organization;

    private PrimitiveFunctor<UUID>      userId;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public Author()
    {
        this.id             = null;

        this.name           = new PrimitiveFunctor<>(null, String.class);
        this.organization   = new PrimitiveFunctor<>(null, String.class);

        this.userId         = new PrimitiveFunctor<>(null, UUID.class);
    }


    public Author(UUID id,
                  String name,
                  String organization,
                  UUID userId)
    {
        this.id             = id;

        this.name           = new PrimitiveFunctor<>(name, String.class);
        this.organization   = new PrimitiveFunctor<>(organization, String.class);

        this.userId         = new PrimitiveFunctor<>(userId, UUID.class);
    }


    // API
    // -----------------------------------------------------------------------------------------

    // API > Model
    // -----------------------------------------------------------------------------------------

    // API > Model > ID
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

    // API > State > Name
    // -----------------------------------------------------------------------------------------

    /**
     * The name of the author, as they wish it appear.
     * @return The author's name.
     */
    public String name()
    {
        return this.name.getValue();
    }


    /**
     * Set the author's name. If null, it defaults to anonymous.
     * @param name The author's name.
     */
    public void setName(String name)
    {
        if (name != null)
            this.name.setValue(name);
        else
            this.name.setValue("Anonymous");
    }


    // API > State > Organization
    // -----------------------------------------------------------------------------------------

    /**
     * The name of the author's organization, if they want to give one.
     * @return The author's organization name.
     */
    @Nullable
    public String organization()
    {
        return this.organization.getValue();
    }


    // API > State > User Id
    // -----------------------------------------------------------------------------------------

    /**
     * The id of the author's user object, if they wish to provide it.
     * @return The author's user id.
     */
    @Nullable
    public UUID userId()
    {
        return this.userId.getValue();
    }

}
