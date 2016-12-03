
package com.kispoko.tome.rules.programming.function;


import android.util.Log;

import com.kispoko.tome.rules.programming.program.ProgramValue;
import com.kispoko.tome.rules.programming.program.ProgramValueType;
import com.kispoko.tome.rules.programming.function.error.InvalidTupleLengthError;
import com.kispoko.tome.rules.programming.program.statement.Parameter;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.CollectionValue;
import com.kispoko.tome.util.value.PrimitiveValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import org.apache.commons.lang3.builder.HashCodeBuilder;

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
    private Map<Parameters,ProgramValue> functionMap;


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

        // ** Result ErrorType
        this.resultType     = new PrimitiveValue<>(resultType, ProgramValueType.class);

        // ** Tuples
        List<Class<? extends Tuple>> tupleClasses = new ArrayList<>();
        tupleClasses.add(Tuple.class);
        this.tuples         = CollectionValue.full(tuples, tupleClasses);

        // > Validate the function definition
        this.validate();

        // > Index the tuples
        indexTuples();
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

        // ** Result ErrorType
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
    public void onLoad()
    {
        indexTuples();
    }


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


    // ** Result ErrorType
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
        ProgramValue result = this.functionMap.get(new Parameters(parameters));

        ProgramValue testValue1 = ProgramValue.asInteger(5);
        List<ProgramValue> paramsList1 = new ArrayList<>();
        paramsList1.add(testValue1);
        Parameters params1 = new Parameters(paramsList1);

        ProgramValue testValue2 = ProgramValue.asInteger(5);
        List<ProgramValue> paramsList2 = new ArrayList<>();
        paramsList2.add(testValue2);
        Parameters params2 = new Parameters(paramsList2);

        Log.d("***FUNCTION", "hashcode1: " + Integer.toString(params1.hashCode()));
        Log.d("***FUNCTION", "hashcode2: " + Integer.toString(params2.hashCode()));

        if (params1.equals(params2))
            Log.d("***FUNCTION", "parameters are equal");
        else
            Log.d("***FUNCTION", "parameters are NOT equal");


        if (result == null)
            Log.d("***FUNCTION", "result is null");
        else
            Log.d("***FUNCTION", "result is NOT null");

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
            int tupleSize = tuples.get(i).getParameters().size();
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
            this.functionMap.put(new Parameters(tuple.getParameters()), tuple.getResult());
        }
    }


    // > Log Function
    // ------------------------------------------------------------------------------------------

    /**
     * Print the function to the debug log.
     */
    public void logFunction()
    {
        for (Map.Entry<Parameters,ProgramValue> e : this.functionMap.entrySet())
        {
            Parameters params = e.getKey();
            ProgramValue res = e.getValue();

            StringBuilder row = new StringBuilder();
            for (ProgramValue param : params.getValues()) {
                row.append(param.toString());
                row.append("   ");
            }

            row.append(res.toString());

            Log.d("***FUNCTION", "tuple: " + row.toString());
        }
    }


    // PARAMETERS CLASS
    // ------------------------------------------------------------------------------------------

    private static class Parameters
    {

        // PROPERTIES
        // ------------------------------------------------------------------------------------------

        private List<ProgramValue> values;


        // CONSTRUCTORS
        // ------------------------------------------------------------------------------------------

        public Parameters(List<ProgramValue> parameterValues)
        {
            values = parameterValues;
        }


        // API
        // ------------------------------------------------------------------------------------------

        // > Values
        // ------------------------------------------------------------------------------------------

        public List<ProgramValue> getValues()
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

            for (ProgramValue value : this.values)
            {
                hashCodeBuilder.append(value.getString());
                hashCodeBuilder.append(value.getInteger());
                hashCodeBuilder.append(value.getBoolean());
                hashCodeBuilder.append(value.getType());
            }

            return hashCodeBuilder.toHashCode();
        }

    }


}
