
package com.kispoko.tome.rules;


import com.kispoko.tome.rules.programming.evaluation.Evaluator;
import com.kispoko.tome.rules.programming.function.FunctionIndex;
import com.kispoko.tome.rules.programming.program.ProgramIndex;
import com.kispoko.tome.rules.programming.variable.VariableIndex;
import com.kispoko.tome.rules.refinement.RefinementIndex;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.ModelValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.io.Serializable;
import java.util.UUID;



/**
 * RulesEngine Engine
 */
public class RulesEngine implements Model, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private UUID id;

    private ModelValue<RefinementIndex> refinementIndex;
    private ModelValue<FunctionIndex>   functionIndex;
    private ModelValue<ProgramIndex>    programIndex;

    private VariableIndex               variableIndex;

    private Evaluator                   evaluator;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public RulesEngine()
    {
        this.id = null;

        this.refinementIndex = ModelValue.empty(RefinementIndex.class);
        this.functionIndex   = ModelValue.empty(FunctionIndex.class);
        this.programIndex    = ModelValue.empty(ProgramIndex.class);

        this.variableIndex   = new VariableIndex();

        this.evaluator       = null;
    }


    public RulesEngine(UUID id,
                       RefinementIndex refinementIndex,
                       ProgramIndex programIndex,
                       FunctionIndex functionIndex)
    {
        this.id = id;

        this.refinementIndex = ModelValue.full(refinementIndex, RefinementIndex.class);
        this.functionIndex   = ModelValue.full(functionIndex, FunctionIndex.class);
        this.programIndex    = ModelValue.full(programIndex, ProgramIndex.class);

        this.variableIndex   = new VariableIndex();

        this.evaluator       = new Evaluator(this.programIndex.getValue(),
                                             this.functionIndex.getValue(),
                                             this.variableIndex);
    }


    public static RulesEngine fromYaml(Yaml yaml)
                  throws YamlException
    {
        UUID            id              = UUID.randomUUID();

        RefinementIndex refinementIndex = RefinementIndex.fromYaml(yaml.atKey("refinements"));
        ProgramIndex    programIndex    = ProgramIndex.fromYaml(yaml.atKey("programs"));
        FunctionIndex   functionIndex   = FunctionIndex.fromYaml(yaml.atKey("functions"));

        return new RulesEngine(id, refinementIndex, programIndex, functionIndex);
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
        this.evaluator = new Evaluator(this.getProgramIndex(),
                                       this.getFunctionIndex(),
                                       this.getVariableIndex());
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
     * Get the variable index.
     * @return The Variable Index.
     */
    public VariableIndex getVariableIndex()
    {
        return this.variableIndex;
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


}
