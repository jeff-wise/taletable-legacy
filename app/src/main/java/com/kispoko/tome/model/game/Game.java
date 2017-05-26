
package com.kispoko.tome.model.game;


import com.kispoko.tome.lib.functor.ModelFunctor;
import com.kispoko.tome.lib.functor.PrimitiveFunctor;
import com.kispoko.tome.lib.model.Model;
import com.kispoko.tome.model.engine.Engine;

import java.io.Serializable;
import java.util.UUID;



/**
 * Game
 */
public class Game extends Model
                  implements Serializable
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    // > Model
    // -----------------------------------------------------------------------------------------

    private UUID                            id;


    // > Functors
    // -----------------------------------------------------------------------------------------

    private PrimitiveFunctor<String>        label;

    private ModelFunctor<GameDescription>   description;

    private ModelFunctor<Engine>            engine;


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public Game()
    {
        this.id                 = null;

        this.label              = new PrimitiveFunctor<>(null, String.class);

        this.description        = ModelFunctor.empty(GameDescription.class);

        this.engine             = ModelFunctor.empty(Engine.class);
    }


    public Game(UUID id,
                String label,
                GameDescription description,
                Engine engine)
    {
        this.id                 = id;

        this.label              = new PrimitiveFunctor<>(label, String.class);

        this.description        = ModelFunctor.full(description, GameDescription.class);

        this.engine             = ModelFunctor.full(engine, Engine.class);
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


    // > State
    // -----------------------------------------------------------------------------------------

    /**
     * The game label is the name of the game formatted nicely to be read by uers.
     * @return The label string.
     */
    public String label()
    {
        return this.label.getValue();
    }


    /**
     * The game description contains all of the information about the game.
     * @return The game description.
     */
    public GameDescription description()
    {
        return this.description.getValue();
    }


    /**
     * The engine definition contains all of the data, rules, mechanics etc of the game.
     * @return The engine definition.
     */
    public Engine engineDefinition()
    {
        return this.engine.getValue();
    }

}
