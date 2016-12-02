
package com.kispoko.tome.rules.programming.function;


import com.kispoko.tome.rules.programming.program.ProgramValue;
import com.kispoko.tome.rules.programming.program.ProgramValueType;
import com.kispoko.tome.rules.programming.function.error.InvalidTupleLengthError;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.CollectionValue;
import com.kispoko.tome.util.value.PrimitiveValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;



/**
 * Function
 */
public class Function implements Model
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private UUID                               id;

    private PrimitiveValue<String>             name;
    private PrimitiveValue<ProgramValueType[]> parameterTypes;
    private PrimitiveValue<ProgramValueType>   resultType;
    private CollectionValue<Tuple>             tuples;


    // > Internal
    private Map<List<ProgramValue>,ProgramValue> functionMap;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Function()
    {
        this.id             = null;
        this.name           = new PrimitiveValue<>(null, String.class);
        this.parameterTypes = new PrimitiveValue<>(null, ProgramValueType[].class);
        this.resultType     = new PrimitiveValue<>(null, ProgramValueType.class);

        List<Class<? extends Tuple>> tupleClasses = new ArrayList<>();
        tupleClasses.add(Tuple.class);
        this.tuples         = CollectionValue.empty(tupleClasses);
    }


    public Function(UUID id,
                    String name,
                    List<ProgramValueType> parameterTypes,
                    ProgramValueType resultType,
                    List<Tuple> tuples)
           throws InvalidFunctionException
    {
        // ** Model Id
        this.id             = id;

        // ** Name
        this.name           = new PrimitiveValue<>(name, String.class);

        // ** Parameter Types
        ProgramValueType[] parameterTypeArray = parameterTypes.toArray(
                                                    new ProgramValueType[parameterTypes.size()]);
        this.parameterTypes = new PrimitiveValue<>(parameterTypeArray, ProgramValueType[].class);

        // ** Result Type
        this.resultType     = new PrimitiveValue<>(resultType, ProgramValueType.class);

        // ** Tuples
        List<Class<? extends Tuple>> tupleClasses = new ArrayList<>();
        tupleClasses.add(Tuple.class);
        this.tuples         = CollectionValue.full(tuples, tupleClasses);

        // > Validate the function definition
        this.validate();

        // > Index the tuples
        this.functionMap = new HashMap<>();
        for (Tuple tuple : tuples) {
            this.functionMap.put(tuple.getParameters(), tuple.getResult());
        }
    }


    public static Function fromYaml(Yaml yaml)
                  throws InvalidFunctionException, YamlException
    {
        // ** Model Id
        UUID id = UUID.randomUUID();

        // ** Name
        String name = yaml.atKey("name").getString();

        // ** Parameter Types
        final List<ProgramValueType> parameterTypes
                = yaml.atKey("parameter_types").forEach(new Yaml.ForEach<ProgramValueType>() {
            @Override
            public ProgramValueType forEach(Yaml yaml, int index) throws YamlException {
                return ProgramValueType.fromYaml(yaml);
            }
        });

        // ** Result Type
        final ProgramValueType resultType = ProgramValueType.fromYaml(yaml.atKey("result_type"));

        // ** Tuples
        List<Tuple> tuples = yaml.atKey("tuples").forEach(new Yaml.ForEach<Tuple>() {
            @Override
            public Tuple forEach(Yaml yaml, int index) throws YamlException {
                return Tuple.fromYaml(yaml, parameterTypes, resultType);
            }
        });

        return new Function(id, name, parameterTypes, resultType, tuples);
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
    public void onLoad() { }


    // > State
    // ------------------------------------------------------------------------------------------

    // ** Name
    // ------------------------------------------------------------------------------------------

    /**
     * Get the function name.
     * @return The function name String.
     */
    public String getName()
    {
        return this.name.getValue();
    }


    // ** Parameter Types
    // ------------------------------------------------------------------------------------------

    /**
     * Get the function's parameter type list.
     * @return The parameter type list.
     */
    public List<ProgramValueType> getParameterTypes()
    {
        return Arrays.asList(this.parameterTypes.getValue());
    }


    // ** Result Type
    // ------------------------------------------------------------------------------------------

    /**
     * Get the function's result type.
     * @return The function's result type.
     */
    public ProgramValueType getResultType()
    {
        return this.resultType.getValue();
    }


    // > Execute
    // ------------------------------------------------------------------------------------------

    /**
     * Execute the function. Returns a ProgramValue based on the provided parameters. If the
     * function does not have a case for the given parameters, it returns null.
     * @param parameters
     * @return
     */
    public ProgramValue execute(List<ProgramValue> parameters)
    {
        return this.functionMap.get(parameters);
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
            int tupleSize = tuples.get(i).getParameters().size();
            if (tupleSize != numberOfParameters)
                throw new InvalidFunctionException(
                        new InvalidTupleLengthError(i, numberOfParameters, tupleSize),
                        InvalidFunctionException.ErrorType.INVALID_TUPLE_LENGTH);
        }
    }

}
