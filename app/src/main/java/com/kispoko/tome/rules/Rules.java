
package com.kispoko.tome.rules;


import com.kispoko.tome.rules.programming.function.FunctionIndex;
import com.kispoko.tome.rules.programming.program.ProgramIndex;
import com.kispoko.tome.rules.refinement.RefinementIndex;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.ModelValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.io.Serializable;
import java.util.UUID;



/**
 * Rules Engine
 */
public class Rules implements Model, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private UUID id;

    private ModelValue<RefinementIndex> refinementIndex;
    private ModelValue<FunctionIndex>   functionIndex;
    private ModelValue<ProgramIndex>    programIndex;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Rules() { }


    public Rules(UUID id,
                 RefinementIndex refinementIndex,
                 ProgramIndex programIndex,
                 FunctionIndex functionIndex)
    {
        this.id = id;

        this.refinementIndex = new ModelValue<>(refinementIndex, this, RefinementIndex.class);
        this.functionIndex   = new ModelValue<>(functionIndex, this, FunctionIndex.class);
        this.programIndex    = new ModelValue<>(programIndex, this, ProgramIndex.class);
    }


    public static Rules fromYaml(Yaml yaml)
                  throws YamlException
    {
        UUID            id              = UUID.randomUUID();

        RefinementIndex refinementIndex = RefinementIndex.fromYaml(yaml.atKey("refinements"));
        ProgramIndex    programIndex    = ProgramIndex.fromYaml(yaml.atKey("programs"));
        FunctionIndex   functionIndex   = FunctionIndex.fromYaml(yaml.atKey("functions"));

        return new Rules(id, refinementIndex, programIndex, functionIndex);
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


    // ** On Update
    // ------------------------------------------------------------------------------------------

    public void onModelUpdate(String valueName) { }


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

}
