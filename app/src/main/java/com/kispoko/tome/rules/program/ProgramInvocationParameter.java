
package com.kispoko.tome.rules.program;


import java.util.Map;



/**
 * Program Invocation Parameter
 */
public class ProgramInvocationParameter
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private Object value;
    private Type _type;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public ProgramInvocationParameter(Object value, Type _type)
    {
        this.value = value;
        this._type = _type;
    }


    public static ProgramInvocationParameter fromYaml(Map<String,Object> parameterYaml)
    {
        // VALUES TO PARSE
        // --------------------------------------------------------------------------------------
        Object value = null;
        Type   _type = null;

        // PARSE VALUES
        // --------------------------------------------------------------------------------------

        // ** Value
        if (parameterYaml.containsKey("value"))
            value = parameterYaml.get("value");

        // ** Type
        if (parameterYaml.containsKey("type"))
            _type = Type.fromString((String) parameterYaml.get("type"));

        return new ProgramInvocationParameter(value, _type);
    }


    // API
    // ------------------------------------------------------------------------------------------

    public String valueAsDBString()
    {
        switch (this._type)
        {
            case REFERENCE:
                return (String) this.value;
        }

        // TODO should not happen
        return null;
    }


    public String typeAsDBString()
    {
        return this._type.toString().toLowerCase();
    }


    public static ProgramInvocationParameter fromDBString(String valueString, Type _type)
    {
        Object value = null;

        switch (_type)
        {
            case REFERENCE:
                value = valueString;
        }

        return new ProgramInvocationParameter(value, _type);
    }


    // NESTED DEFINITIONS
    // ------------------------------------------------------------------------------------------

    public enum Type
    {
        REFERENCE;

        public static Type fromString(String _type)
        {
            if (_type != null)
                return Type.valueOf(_type.toUpperCase());
            return null;
        }


        public static String asString(Type _type)
        {
            if (_type != null)
                return _type.toString().toLowerCase();
            return null;
        }
    }

}
