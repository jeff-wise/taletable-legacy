
package com.kispoko.tome.engine;


import com.kispoko.tome.engine.programming.builtin.BuiltInFunction;
import com.kispoko.tome.engine.programming.interpreter.Interpreter;
import com.kispoko.tome.engine.programming.function.FunctionIndex;
import com.kispoko.tome.engine.programming.mechanic.MechanicIndex;
import com.kispoko.tome.engine.programming.program.ProgramIndex;
import com.kispoko.tome.engine.programming.variable.VariableUnion;
import com.kispoko.tome.engine.refinement.RefinementIndex;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.ModelValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.io.Serializable;
import java.util.UUID;



/**
 * Rules Engine
 */
public class RulesEngine implements Model, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private UUID id;

    private ModelValue<RefinementIndex> refinementIndex;
    private ModelValue<FunctionIndex>   functionIndex;
    private ModelValue<ProgramIndex>    programIndex;
    private ModelValue<MechanicIndex>   mechanicIndex;

    private Interpreter interpreter;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public RulesEngine()
    {
        this.id              = null;

        this.refinementIndex = ModelValue.empty(RefinementIndex.class);
        this.functionIndex   = ModelValue.empty(FunctionIndex.class);
        this.programIndex    = ModelValue.empty(ProgramIndex.class);
        this.mechanicIndex   = ModelValue.empty(MechanicIndex.class);

        this.interpreter     = null;

        BuiltInFunction.initialize();
    }


    public RulesEngine(UUID id,
                       RefinementIndex refinementIndex,
                       FunctionIndex functionIndex,
                       ProgramIndex programIndex,
                       MechanicIndex mechanicIndex)
    {
        this.id = id;

        this.refinementIndex = ModelValue.full(refinementIndex, RefinementIndex.class);
        this.functionIndex   = ModelValue.full(functionIndex, FunctionIndex.class);
        this.programIndex    = ModelValue.full(programIndex, ProgramIndex.class);
        this.mechanicIndex   = ModelValue.full(mechanicIndex, MechanicIndex.class);

        this.interpreter = new Interpreter(this.programIndex.getValue(),
                                           this.functionIndex.getValue());

        BuiltInFunction.initialize();
    }


    public static RulesEngine fromYaml(Yaml yaml)
                  throws YamlException
    {
        UUID            id              = UUID.randomUUID();

        RefinementIndex refinementIndex = RefinementIndex.fromYaml(yaml.atKey("refinements"));
        ProgramIndex    programIndex    = ProgramIndex.fromYaml(yaml.atKey("programs"));
        FunctionIndex   functionIndex   = FunctionIndex.fromYaml(yaml.atKey("functions"));
        MechanicIndex   mechanicIndex   = MechanicIndex.fromYaml(yaml.atKey("mechanics"));

        // Read the variables. They add themselves to the state.
        yaml.atKey("variables").forEach(new Yaml.ForEach<Void>() {
            @Override
            public Void forEach(Yaml yaml, int index) throws YamlException {
                VariableUnion.fromYaml(yaml);
                return null;
            }
        }, true);

        return new RulesEngine(id, refinementIndex, functionIndex, programIndex, mechanicIndex);
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
        this.interpreter = new Interpreter(this.getProgramIndex(),
                                           this.getFunctionIndex());
    }

    // > State
    // ------------------------------------------------------------------------------------------

    /**
     * Get the rules' refinement index.
     * @return The RefinementIndex.
     */
    public RefinementIndex getRefinementIndex()
    {
        return this.refinementIndex.getValue();
    }


    /**
     * Get the program index.
     * @return The Program Index.
     */
    public ProgramIndex getProgramIndex()
    {
        return this.programIndex.getValue();
    }


    /**
     * Get the function index.
     * @return The Function Index.
     */
    public FunctionIndex getFunctionIndex()
    {
        return this.functionIndex.getValue();
    }


    /**
     * Get the interpreter.
     * @return The Interpreter.
     */
    public Interpreter getInterpreter()
    {
        return this.interpreter;
    }

}
