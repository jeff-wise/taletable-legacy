
package com.kispoko.tome.engine.value;


import com.kispoko.tome.ApplicationFailure;
import com.kispoko.tome.error.InvalidCaseError;
import com.kispoko.tome.error.UnknownVariantError;
import com.kispoko.tome.exception.UnionException;
import com.kispoko.tome.lib.functor.ModelFunctor;
import com.kispoko.tome.lib.functor.PrimitiveFunctor;
import com.kispoko.tome.lib.model.Model;
import com.kispoko.tome.lib.yaml.ToYaml;
import com.kispoko.tome.lib.yaml.YamlBuilder;
import com.kispoko.tome.lib.yaml.YamlParseException;
import com.kispoko.tome.lib.yaml.YamlParser;

import java.io.Serializable;
import java.util.UUID;



/**
 * Value Set Union
 */
public class ValueSetUnion extends Model
                           implements ToYaml, Serializable
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    // > Model
    // -----------------------------------------------------------------------------------------

    private UUID                            id;


    // > Functors
    // -----------------------------------------------------------------------------------------

    private ModelFunctor<BaseValueSet>      baseValueSet;
    private ModelFunctor<CompoundValueSet>  compoundValueSet;

    private PrimitiveFunctor<ValueSetType>  valueSetType;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public ValueSetUnion()
    {
        this.baseValueSet       = ModelFunctor.empty(BaseValueSet.class);
        this.compoundValueSet   = ModelFunctor.empty(CompoundValueSet.class);

        this.valueSetType       = new PrimitiveFunctor<>(null, ValueSetType.class);
    }


    private ValueSetUnion(UUID id, Object valueSet, ValueSetType valueSetType)
    {
        this.id                 = id;

        this.valueSetType       = new PrimitiveFunctor<>(valueSetType, ValueSetType.class);

        this.baseValueSet       = ModelFunctor.full(null, BaseValueSet.class);
        this.compoundValueSet   = ModelFunctor.full(null, CompoundValueSet.class);

        switch (valueSetType)
        {
            case BASE:
                this.baseValueSet.setValue((BaseValueSet) valueSet);
                break;
            case COMPOUND:
                this.compoundValueSet.setValue((CompoundValueSet) valueSet);
                break;
        }
    }


    // > Variants
    // ------------------------------------------------------------------------------------------

    /**
     * Create the "base" variant.
     * @param id The id.
     * @param baseValueSet The base value set.
     * @return The "base" Value Set Union.
     */
    public static ValueSetUnion asBase(UUID id, BaseValueSet baseValueSet)
    {
        if (baseValueSet == null) return null;
        return new ValueSetUnion(id, baseValueSet, ValueSetType.BASE);
    }


    /**
     * Create the "compound" variant.
     * @param id The id
     * @param compoundValueSet The compound value set.
     * @return The "compound" Value Set Union.
     */
    public static ValueSetUnion asCompound(UUID id, CompoundValueSet compoundValueSet)
    {
        return new ValueSetUnion(id, compoundValueSet, ValueSetType.COMPOUND);
    }


    // > From Yaml
    // ------------------------------------------------------------------------------------------

    public static ValueSetUnion fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        UUID         id   = UUID.randomUUID();

        ValueSetType type = ValueSetType.fromYaml(yaml.atKey("type"));

        switch (type)
        {
            case BASE:
                BaseValueSet baseValueSet = BaseValueSet.fromYaml(yaml.atKey("value_set"));
                return ValueSetUnion.asBase(id, baseValueSet);
            case COMPOUND:
                CompoundValueSet compoundValueSet =
                                        CompoundValueSet.fromYaml(yaml.atKey("value_set"));
                return ValueSetUnion.asCompound(id, compoundValueSet);
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
     * This method is called when the RulesEngine is completely loaded for the first time.
     */
    public void onLoad() { }


    // > To Yaml
    // ------------------------------------------------------------------------------------------

    /**
     * The Variable Union's yaml representation.
     * @return The Yaml Builder.
     */
    public YamlBuilder toYaml()
    {
        YamlBuilder unionYaml = YamlBuilder.map();

        YamlBuilder valueSetYaml = null;
        switch (this.type())
        {
            case BASE:
                valueSetYaml = this.base().toYaml();
                break;
            case COMPOUND:
                valueSetYaml = this.compound().toYaml();
                break;
            default:
                ApplicationFailure.union(
                        UnionException.unknownVariant(
                                new UnknownVariantError(ValueSetType.class.getName())));
        }

        unionYaml.putYaml("type", this.type());
        unionYaml.putYaml("value_set", valueSetYaml);

        return unionYaml;
    }


    // > State
    // ------------------------------------------------------------------------------------------

    // ** Type
    // ------------------------------------------------------------------------------------------

    /**
     * The type of the value set union.
     * @return The type.
     */
    public ValueSetType type()
    {
        return this.valueSetType.getValue();
    }


    // ** Variants
    // ------------------------------------------------------------------------------------------

    /**
     * The base variant.
     * @return The Base Value Set.
     */
    public BaseValueSet base()
    {
        if (this.type() != ValueSetType.BASE) {
            ApplicationFailure.union(
                    UnionException.invalidCase(
                            new InvalidCaseError("base", this.type().toString())));
        }
        return this.baseValueSet.getValue();
    }


    /**
     * The compound variant.
     * @return The compound value set.
     */
    public CompoundValueSet compound()
    {
        if (this.type() != ValueSetType.COMPOUND) {
            ApplicationFailure.union(
                    UnionException.invalidCase(
                            new InvalidCaseError("compound", this.type().toString())));
        }
        return this.compoundValueSet.getValue();
    }


    // ** Value Set
    // ------------------------------------------------------------------------------------------

    public ValueSet valueSet()
    {
        switch (this.type())
        {
            case BASE:
                return this.base();
            case COMPOUND:
                return this.compound();
        }

        return null;
    }
}
