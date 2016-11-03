
package com.kispoko.tome.sheet.component;




/**
 * Component Value
 */
public class ComponentValue
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private Object value;
    private Type _type;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public ComponentValue(Object value, Type _type)
    {
        this.value = value;
        this._type = _type;
    }


    // API
    // ------------------------------------------------------------------------------------------

    public String asString()
    {
        switch (this._type)
        {
            case LITERAL_INTEGER:
                return Integer.toString((Integer) value);
            case LITERAL_STRING:
                return (String) value;
            case LITERAL_BOOLEAN:
                return Boolean.toString((Boolean) value);
        }
    }



    // NESTED DEFINTIONS
    // ------------------------------------------------------------------------------------------

    public enum Type
    {
        LITERAL_INTEGER,
        LITERAL_STRING,
        LITERAL_BOOLEAN,
        PROGRAM;


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
