
package com.kispoko.tome.rules.function;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;



/**
 * Function Value
 */
public class FunctionValue
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    Object value;
    FunctionValueType valueType;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public FunctionValue(Object value, FunctionValueType valueType)
    {
        this.value = value;
        this.valueType = valueType;
    }


    // API
    // ------------------------------------------------------------------------------------------

    public FunctionValueType getType() {
        return this.valueType;
    }


    public Integer getInteger() {
        return (Integer) this.value;
    }


    public String getString() {
        return (String) this.value;
    }


    public String asString()
    {
        switch (this.valueType)
        {
            case INTEGER:
                return Integer.toString((Integer) this.value);
            case STRING:
                return (String) this.value;
        }
        return null;
    }


    public static FunctionValue fromString(String functionValueString, FunctionValueType _type)
    {
        switch (_type)
        {
            case INTEGER:
                return new FunctionValue(Integer.parseInt(functionValueString),
                                         FunctionValueType.INTEGER);
            case STRING:
                return new FunctionValue(functionValueString,
                                         FunctionValueType.STRING);
        }
        return null;
    }



    @Override
    public boolean equals(Object o)
    {

        if (o == this) return true;

        if (!(o instanceof FunctionValue)) {
            return false;
        }

        FunctionValue functionValue = (FunctionValue) o;

        if (functionValue.getType() != this.getType())
            return false;

        switch (functionValue.getType())
        {
            case INTEGER:
                Integer thatInteger = functionValue.getInteger();
                Integer thisInteger = this.getInteger();
                return new EqualsBuilder()
                            .append(thisInteger, thatInteger)
                            .isEquals();
            case STRING:
                String thatString = functionValue.getString();
                String thisString = this.getString();
                return new EqualsBuilder()
                            .append(thisString, thatString)
                            .isEquals();
        }

        return false;
    }


    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                    .append(value)
                    .append(valueType)
                    .toHashCode();
    }



}
