
package com.kispoko.tome.engine.function;


import android.util.Log;

import com.kispoko.tome.R;
import com.kispoko.tome.engine.EngineType;
import com.kispoko.tome.engine.EngineValueUnion;
import com.kispoko.tome.engine.function.error.InvalidTupleLengthError;
import com.kispoko.tome.lib.model.Model;
import com.kispoko.tome.lib.functor.CollectionFunctor;
import com.kispoko.tome.lib.functor.PrimitiveFunctor;
import com.kispoko.tome.lib.yaml.ToYaml;
import com.kispoko.tome.lib.yaml.YamlBuilder;
import com.kispoko.tome.lib.yaml.YamlParser;
import com.kispoko.tome.lib.yaml.YamlParseException;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;



/**
 * Function
 */
public class Function extends Model
                      implements ToYaml, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID                                    id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private PrimitiveFunctor<String>                name;
    private PrimitiveFunctor<String>                label;
    private PrimitiveFunctor<String>                description;
    private PrimitiveFunctor<EngineType[]>          parameterTypes;
    private PrimitiveFunctor<EngineType>            resultType;
    private CollectionFunctor<Tuple>                tuples;


    // > Internal
    // ------------------------------------------------------------------------------------------

    private Map<Parameters,EngineValueUnion>       functionMap;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Function()
    {
        this.id             = null;
        this.name           = new PrimitiveFunctor<>(null, String.class);
        this.label          = new PrimitiveFunctor<>(null, String.class);
        this.description    = new PrimitiveFunctor<>(null, String.class);
        this.parameterTypes = new PrimitiveFunctor<>(null, EngineType[].class);
        this.resultType     = new PrimitiveFunctor<>(null, EngineType.class);

        this.tuples         = CollectionFunctor.empty(Tuple.class);

        this.initializeFunctors();
    }


    public Function(UUID id,
                    String name,
                    String label,
                    String description,
                    List<EngineType> parameterTypes,
                    EngineType resultType,
                    List<Tuple> tuples)
           throws InvalidFunctionException
    {
        // ** Model Id
        this.id             = id;

        // ** Name
        this.name           = new PrimitiveFunctor<>(name, String.class);

        // ** Label
        this.label          = new PrimitiveFunctor<>(label, String.class);

        // ** Description
        this.description    = new PrimitiveFunctor<>(description, String.class);

        // ** Parameter Types
        EngineType[] parameterTypeArray = parameterTypes.toArray(
                                                    new EngineType[parameterTypes.size()]);
        this.parameterTypes = new PrimitiveFunctor<>(parameterTypeArray, EngineType[].class);

        // ** Result Type
        this.resultType     = new PrimitiveFunctor<>(resultType, EngineType.class);

        // ** Tuples
        this.tuples         = CollectionFunctor.full(tuples, Tuple.class);

        // > Validate the function definition
        this.validate();

        // > Index the tuples
        indexTuples();

        this.initializeFunctors();
    }


    public static Function fromYaml(YamlParser yaml)
                  throws InvalidFunctionException, YamlParseException
    {
        // ** Model Id
        UUID id = UUID.randomUUID();

        // ** Name
        String name = yaml.atKey("name").getString().trim();

        // ** Label
        String label = yaml.atMaybeKey("label").getString();
        if (label != null)  label = label.trim();

        // ** Description
        String description = yaml.atMaybeKey("description").getString();
        if (description != null)  description = description.trim();

        // ** Parameter Types
        final List<EngineType> parameterTypes
                = yaml.atKey("parameter_types").forEach(new YamlParser.ForEach<EngineType>() {
            @Override
            public EngineType forEach(YamlParser yaml, int index) throws YamlParseException {
                return EngineType.fromYaml(yaml);
            }
        });

        // ** Result Type
        final EngineType resultType = EngineType.fromYaml(yaml.atKey("result_type"));

        // ** Tuples
        List<Tuple> tuples = yaml.atKey("tuples").forEach(new YamlParser.ForEach<Tuple>() {
            @Override
            public Tuple forEach(YamlParser yaml, int index) throws YamlParseException {
                return Tuple.fromYaml(yaml, parameterTypes, resultType);
            }
        });

        return new Function(id, name, label, description, parameterTypes, resultType, tuples);
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
     * This method is called when the Function is completely loaded for the first time.
     */
    public void onLoad()
    {
        indexTuples();
    }


    // > To Yaml
    // ------------------------------------------------------------------------------------------

    /**
     * The function's yaml representation.
     * @return The Yaml Builder.
     */
    public YamlBuilder toYaml()
    {
        return YamlBuilder.map()
                .putString("name", this.name())
                .putString("label", this.label())
                .putString("description", this.description())
                .putList("parameter_types", this.parameterTypes())
                .putYaml("result_type", this.resultType())
                .putList("tuples", this.tuples());
    }


    // > State
    // ------------------------------------------------------------------------------------------

    /**
     * Get the function name.
     * @return The function name String.
     */
    public String name()
    {
        return this.name.getValue();
    }


    /**
     * The function's label.
     * @return The label.
     */
    public String label()
    {
        return this.label.getValue();
    }


    /**
     * The function's description.
     * @return The description.
     */
    public String description()
    {
        return this.description.getValue();
    }


    /**
     * Get the function's parameter type list.
     * @return The parameter type list.
     */
    public List<EngineType> parameterTypes()
    {
        return Arrays.asList(this.parameterTypes.getValue());
    }


    /**
     * Get the function's result type.
     * @return The function's result type.
     */
    public EngineType resultType()
    {
        return this.resultType.getValue();
    }


    /**
     * The function's tuples.
     * @return The Tuple List.
     */
    public List<Tuple> tuples()
    {
        return this.tuples.getValue();
    }


    /**
     * The number of parameters the function has.
     * @return The arity.
     */
    public int arity()
    {
        return this.parameterTypes().size();
    }


    // > Execute
    // ------------------------------------------------------------------------------------------

    /**
     * Execute the function. Returns a EngineValueUnion based on the provided parameters. If the
     * function does not have a case for the given parameters, it returns null.
     * @param parameters
     * @return
     */
    public EngineValueUnion execute(List<EngineValueUnion> parameters)
    {
        EngineValueUnion result = this.functionMap.get(new Parameters(parameters));
        return result;
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    // > Initialize
    // ------------------------------------------------------------------------------------------

    private void initializeFunctors()
    {
        // Name
        this.name.setName("name");
        this.name.setLabelId(R.string.function_field_name_label);
        this.name.setDescriptionId(R.string.function_field_name_description);

        // Label
        this.label.setName("label");
        this.label.setLabelId(R.string.function_field_label_label);
        this.label.setDescriptionId(R.string.function_field_label_description);

        // Description
        this.description.setName("description");
        this.description.setLabelId(R.string.function_field_description_label);
        this.description.setDescriptionId(R.string.function_field_description_description);

        // Parameter Types
        this.parameterTypes.setName("parameter_types");
        this.parameterTypes.setLabelId(R.string.function_field_parameter_types_label);
        this.parameterTypes.setDescriptionId(R.string.function_field_parameter_types_description);

        // Result Type
        this.resultType.setName("result_type");
        this.resultType.setLabelId(R.string.function_field_result_type_label);
        this.resultType.setDescriptionId(R.string.function_field_result_type_description);

        // Tuples
        this.tuples.setName("tuples");
        this.tuples.setLabelId(R.string.function_field_tuples_label);
        this.tuples.setDescriptionId(R.string.function_field_tuples_description);
    }


    // > Validate
    // ------------------------------------------------------------------------------------------

    private void validate()
            throws InvalidFunctionException
    {
        // [1] Make sure each tuple has the same number of parameters as specified by the function

        int numberOfParameters = this.parameterTypes.getValue().length;
        List<Tuple> tuples = this.tuples.getValue();

        for (int i = 0; i < tuples.size(); i++)
        {
            int tupleSize = tuples.get(i).parameters().size();
            if (tupleSize != numberOfParameters)
                throw new InvalidFunctionException(
                        new InvalidTupleLengthError(i, numberOfParameters, tupleSize),
                        InvalidFunctionException.ErrorType.INVALID_TUPLE_LENGTH);
        }
    }


    // > Index tuples
    // ------------------------------------------------------------------------------------------

    /**
     * Index the function's tuples for quick lookup when execute is called.
     */
    private void indexTuples()
    {
        this.functionMap = new HashMap<>();
        for (Tuple tuple : this.tuples.getValue()) {
            this.functionMap.put(new Parameters(tuple.parameters()), tuple.result());
        }
    }


    // > Log Function
    // ------------------------------------------------------------------------------------------

    /**
     * Print the function to the debug log.
     */
    public void logFunction()
    {
        for (Map.Entry<Parameters,EngineValueUnion> e : this.functionMap.entrySet())
        {
            Parameters params = e.getKey();
            EngineValueUnion res = e.getValue();

            StringBuilder row = new StringBuilder();
            for (EngineValueUnion param : params.getValues()) {
                row.append(param.type().toString());
                row.append("  ");
                row.append(param.toString());
                row.append("    ");
            }

            row.append("result: ");
            row.append(res.toString());

            Log.d("***FUNCTION", row.toString());
        }
    }


    // PARAMETERS CLASS
    // ------------------------------------------------------------------------------------------

    private static class Parameters implements Serializable
    {

        // PROPERTIES
        // ------------------------------------------------------------------------------------------

        private List<EngineValueUnion> values;


        // CONSTRUCTORS
        // ------------------------------------------------------------------------------------------

        public Parameters(List<EngineValueUnion> parameterValues)
        {
            values = parameterValues;
        }


        // API
        // ------------------------------------------------------------------------------------------

        // > Values
        // ------------------------------------------------------------------------------------------

        public List<EngineValueUnion> getValues()
        {
            return this.values;
        }


        // > Size
        // ------------------------------------------------------------------------------------------

        public int size()
        {
            return this.values.size();
        }


        // > HashCode / Equals
        // ------------------------------------------------------------------------------------------

        @Override
        public boolean equals(Object o)
        {

            if (o == this) return true;

            if (!(o instanceof Function.Parameters)) {
                return false;
            }

            Parameters otherParameters = (Parameters) o;

            if (otherParameters.size() != this.size())
                return false;

            for (int i = 0; i < this.size(); i++)
            {
                if (!this.values.get(i).equals(otherParameters.values.get(i)))
                    return false;
            }

            return true;
        }


        @Override
        public int hashCode()
        {
            HashCodeBuilder hashCodeBuilder = new HashCodeBuilder(17, 37);

            for (EngineValueUnion value : this.values)
            {
                switch (value.type())
                {
                    case STRING:
                        hashCodeBuilder.append(value.stringValue());
                        break;
                    case INTEGER:
                        hashCodeBuilder.append(value.integerValue());
                        break;
                    case BOOLEAN:
                        hashCodeBuilder.append(value.booleanValue());
                        break;
                }
            }

            return hashCodeBuilder.toHashCode();
        }

    }


}
