
package com.kispoko.tome.engine.programming.function;


import android.util.Log;

import com.kispoko.tome.engine.programming.program.ProgramValueUnion;
import com.kispoko.tome.engine.programming.program.ProgramValueType;
import com.kispoko.tome.engine.programming.function.error.InvalidTupleLengthError;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.CollectionFunctor;
import com.kispoko.tome.util.value.PrimitiveFunctor;
import com.kispoko.tome.util.yaml.ToYaml;
import com.kispoko.tome.util.yaml.YamlBuilder;
import com.kispoko.tome.util.yaml.YamlParser;
import com.kispoko.tome.util.yaml.YamlParseException;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;



/**
 * Function
 */
public class Function implements Model, ToYaml, Serializable
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
    private PrimitiveFunctor<ProgramValueType[]>    parameterTypes;
    private PrimitiveFunctor<ProgramValueType>      resultType;
    private CollectionFunctor<Tuple>                tuples;


    // > Internal
    // ------------------------------------------------------------------------------------------

    private Map<Parameters,ProgramValueUnion>       functionMap;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Function()
    {
        this.id             = null;
        this.name           = new PrimitiveFunctor<>(null, String.class);
        this.label          = new PrimitiveFunctor<>(null, String.class);
        this.description    = new PrimitiveFunctor<>(null, String.class);
        this.parameterTypes = new PrimitiveFunctor<>(null, ProgramValueType[].class);
        this.resultType     = new PrimitiveFunctor<>(null, ProgramValueType.class);

        List<Class<? extends Tuple>> tupleClasses = new ArrayList<>();
        tupleClasses.add(Tuple.class);
        this.tuples         = CollectionFunctor.empty(tupleClasses);
    }


    public Function(UUID id,
                    String name,
                    String label,
                    String description,
                    List<ProgramValueType> parameterTypes,
                    ProgramValueType resultType,
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
        ProgramValueType[] parameterTypeArray = parameterTypes.toArray(
                                                    new ProgramValueType[parameterTypes.size()]);
        this.parameterTypes = new PrimitiveFunctor<>(parameterTypeArray, ProgramValueType[].class);

        // ** Result ErrorType
        this.resultType     = new PrimitiveFunctor<>(resultType, ProgramValueType.class);

        // ** Tuples
        List<Class<? extends Tuple>> tupleClasses = new ArrayList<>();
        tupleClasses.add(Tuple.class);
        this.tuples         = CollectionFunctor.full(tuples, tupleClasses);

        // > Validate the function definition
        this.validate();

        // > Index the tuples
        indexTuples();
    }


    public static Function fromYaml(YamlParser yaml)
                  throws InvalidFunctionException, YamlParseException
    {
        // ** Model Id
        UUID id = UUID.randomUUID();

        // ** Name
        String name = yaml.atKey("name").getString();

        // ** Label
        String label = yaml.atMaybeKey("label").getString();

        // ** Description
        String description = yaml.atMaybeKey("description").getString();

        // ** Parameter Types
        final List<ProgramValueType> parameterTypes
                = yaml.atKey("parameter_types").forEach(new YamlParser.ForEach<ProgramValueType>() {
            @Override
            public ProgramValueType forEach(YamlParser yaml, int index) throws YamlParseException {
                return ProgramValueType.fromYaml(yaml);
            }
        });

        // ** Result ErrorType
        final ProgramValueType resultType = ProgramValueType.fromYaml(yaml.atKey("result_type"));

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
    public List<ProgramValueType> parameterTypes()
    {
        return Arrays.asList(this.parameterTypes.getValue());
    }


    /**
     * Get the function's result type.
     * @return The function's result type.
     */
    public ProgramValueType resultType()
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
     * Execute the function. Returns a ProgramValueUnion based on the provided parameters. If the
     * function does not have a case for the given parameters, it returns null.
     * @param parameters
     * @return
     */
    public ProgramValueUnion execute(List<ProgramValueUnion> parameters)
    {
        ProgramValueUnion result = this.functionMap.get(new Parameters(parameters));
        return result;
    }


    // INTERNAL
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
        for (Map.Entry<Parameters,ProgramValueUnion> e : this.functionMap.entrySet())
        {
            Parameters params = e.getKey();
            ProgramValueUnion res = e.getValue();

            StringBuilder row = new StringBuilder();
            for (ProgramValueUnion param : params.getValues()) {
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

        private List<ProgramValueUnion> values;


        // CONSTRUCTORS
        // ------------------------------------------------------------------------------------------

        public Parameters(List<ProgramValueUnion> parameterValues)
        {
            values = parameterValues;
        }


        // API
        // ------------------------------------------------------------------------------------------

        // > Values
        // ------------------------------------------------------------------------------------------

        public List<ProgramValueUnion> getValues()
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

            for (ProgramValueUnion value : this.values)
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
