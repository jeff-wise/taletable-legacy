
package com.kispoko.tome.engine.refinement;


import com.kispoko.tome.ApplicationFailure;
import com.kispoko.tome.error.InvalidCaseError;
import com.kispoko.tome.exception.UnionException;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.ModelFunctor;
import com.kispoko.tome.util.value.PrimitiveFunctor;
import com.kispoko.tome.util.yaml.ToYaml;
import com.kispoko.tome.util.yaml.YamlBuilder;
import com.kispoko.tome.util.yaml.YamlParser;
import com.kispoko.tome.util.yaml.YamlParseException;

import java.io.Serializable;
import java.util.UUID;



/**
 * Refinement Union
 */
public class RefinementUnion implements Model, ToYaml, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID                           id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private ModelFunctor<MemberOf> memberOf;

    private PrimitiveFunctor<RefinementType> type;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public RefinementUnion()
    {
        this.id        = null;

        this.memberOf  = ModelFunctor.empty(MemberOf.class);

        this.type      = new PrimitiveFunctor<>(null, RefinementType.class);
    }


    private RefinementUnion(UUID id, Object refinement, RefinementType type)
    {
        this.id          = id;


        this.memberOf    = ModelFunctor.full(null, MemberOf.class);

        this.type        = new PrimitiveFunctor<>(type, RefinementType.class);

        switch (type)
        {
            case MEMBER_OF:
                this.memberOf.setValue((MemberOf) refinement);
                break;
        }
    }


    /**
     * Create the "member_of" case.
     * @param id The Model id.
     * @param memberOf The member-of refinement
     * @return The new CellUnion as the text case.
     */
    public static RefinementUnion asMemberOf(UUID id, MemberOf memberOf)
    {
        return new RefinementUnion(id, memberOf, RefinementType.MEMBER_OF);
    }


    /**
     * Create a Cell Union from its Yaml representation.
     * @param yaml The Yaml parser.
     * @return The parsed Refinement Union.
     * @throws YamlParseException
     */
    public static RefinementUnion fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        UUID           id   = UUID.randomUUID();

        RefinementType type = RefinementType.fromYaml(yaml.atKey("type"));

        switch (type)
        {
            case MEMBER_OF:
                MemberOf memberOf = MemberOf.fromYaml(yaml);
                return RefinementUnion.asMemberOf(id, memberOf);
        }

        return null;
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
     * This method is called when the Cell Union is completely loaded for the first time.
     */
    public void onLoad() { }


    // > To Yaml
    // ------------------------------------------------------------------------------------------

    public YamlBuilder toYaml()
    {
        return YamlBuilder.map();
    }


    // > State
    // ------------------------------------------------------------------------------------------

    // ** Type
    // ------------------------------------------------------------------------------------------

    /**
     * Get the cell type.
     * @return The Cell Type.
     */
    public RefinementType type()
    {
        return this.type.getValue();
    }


    // ** Refinements
    // ------------------------------------------------------------------------------------------

    /**
     * Access the member-of case of the union.
     * @return The MemberOf, or an exception if the union is not the member-of variant.
     */
    public MemberOf memberOf()
    {
        if (this.type() != RefinementType.MEMBER_OF) {
            ApplicationFailure.union(
                    UnionException.invalidCase(
                            new InvalidCaseError("member_of", this.type.toString())));
        }
        return this.memberOf.getValue();
    }

}
