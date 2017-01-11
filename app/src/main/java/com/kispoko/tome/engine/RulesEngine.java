
package com.kispoko.tome.engine;


import com.kispoko.tome.engine.programming.function.builtin.BuiltInFunction;
import com.kispoko.tome.engine.programming.interpreter.Interpreter;
import com.kispoko.tome.engine.programming.function.FunctionIndex;
import com.kispoko.tome.engine.programming.mechanic.MechanicIndex;
import com.kispoko.tome.engine.programming.program.ProgramIndex;
import com.kispoko.tome.engine.refinement.RefinementIndex;
import com.kispoko.tome.engine.value.Dictionary;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.ModelFunctor;
import com.kispoko.tome.util.yaml.ToYaml;
import com.kispoko.tome.util.yaml.YamlBuilder;
import com.kispoko.tome.util.yaml.YamlParser;
import com.kispoko.tome.util.yaml.YamlParseException;

import java.io.Serializable;
import java.util.UUID;



/**
 * Rules Engine
 */
public class RulesEngine implements Model, ToYaml, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID                            id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private ModelFunctor<RefinementIndex>   refinementIndex;
    private ModelFunctor<FunctionIndex>     functionIndex;
    private ModelFunctor<ProgramIndex>      programIndex;
    private ModelFunctor<MechanicIndex>     mechanicIndex;
    private ModelFunctor<Dictionary>        dictionary;

    private Interpreter                     interpreter;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public RulesEngine()
    {
        this.id              = null;

        this.refinementIndex = ModelFunctor.empty(RefinementIndex.class);
        this.functionIndex   = ModelFunctor.empty(FunctionIndex.class);
        this.programIndex    = ModelFunctor.empty(ProgramIndex.class);
        this.mechanicIndex   = ModelFunctor.empty(MechanicIndex.class);
        this.dictionary      = ModelFunctor.empty(Dictionary.class);

        this.interpreter     = null;

        BuiltInFunction.initialize();
    }


    public RulesEngine(UUID id,
                       RefinementIndex refinementIndex,
                       FunctionIndex functionIndex,
                       ProgramIndex programIndex,
                       MechanicIndex mechanicIndex,
                       Dictionary dictionary)
    {
        this.id = id;

        this.refinementIndex = ModelFunctor.full(refinementIndex, RefinementIndex.class);
        this.functionIndex   = ModelFunctor.full(functionIndex, FunctionIndex.class);
        this.programIndex    = ModelFunctor.full(programIndex, ProgramIndex.class);
        this.mechanicIndex   = ModelFunctor.full(mechanicIndex, MechanicIndex.class);
        this.dictionary      = ModelFunctor.full(dictionary, Dictionary.class);

        this.interpreter = new Interpreter(this.programIndex.getValue(),
                                           this.functionIndex.getValue());

        BuiltInFunction.initialize();
    }


    public static RulesEngine fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        UUID            id              = UUID.randomUUID();

        RefinementIndex refinementIndex = RefinementIndex.fromYaml(yaml.atKey("refinements"));
        ProgramIndex    programIndex    = ProgramIndex.fromYaml(yaml.atKey("programs"));
        FunctionIndex   functionIndex   = FunctionIndex.fromYaml(yaml.atKey("functions"));
        MechanicIndex   mechanicIndex   = MechanicIndex.fromYaml(yaml.atKey("mechanics"));
        Dictionary      dictionary      = Dictionary.fromYaml(yaml.atKey("dictionary"));

        return new RulesEngine(id, refinementIndex, functionIndex, programIndex,
                               mechanicIndex, dictionary);
    }


    // API
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    // ** Id
    // ------------------------------------------------------------------------------------------

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
    // ------------------------------------------------------------------------------------------

    /**
     * This method is called when the RulesEngine is completely loaded for the first time.
     */
    public void onLoad()
    {
        this.interpreter = new Interpreter(this.programIndex(),
                                           this.functionIndex());
    }


    // > To Yaml
    // ------------------------------------------------------------------------------------------

    /**
     * The Rules Engine's yaml representation.
     * @return The Yaml Builder.
     */
    public YamlBuilder toYaml()
    {
        return YamlBuilder.map()
                .putYaml("refinements", this.refinementIndex())
                .putYaml("programs", this.programIndex())
                .putYaml("functions", this.functionIndex())
                .putYaml("mechanics", this.mechanicIndex())
                .putYaml("dictionary", this.dictionary());
    }


    // > State
    // ------------------------------------------------------------------------------------------

    /**
     * Get the rules' refinement index.
     * @return The RefinementIndex.
     */
    public RefinementIndex refinementIndex()
    {
        return this.refinementIndex.getValue();
    }


    /**
     * Get the program index.
     * @return The Program Index.
     */
    public ProgramIndex programIndex()
    {
        return this.programIndex.getValue();
    }


    /**
     * Get the function index.
     * @return The Function Index.
     */
    public FunctionIndex functionIndex()
    {
        return this.functionIndex.getValue();
    }


    /**
     * The mechanic index.
     * @return The Mechanic Index.
     */
    public MechanicIndex mechanicIndex()
    {
        return this.mechanicIndex.getValue();
    }


    /**
     * Get the interpreter.
     * @return The Interpreter.
     */
    public Interpreter getInterpreter()
    {
        return this.interpreter;
    }


    /**
     * The value dictionary.
     * @return the dictionary.
     */
    public Dictionary dictionary()
    {
        return this.dictionary.getValue();
    }

}
