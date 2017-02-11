
package com.kispoko.tome.util.yaml;


import com.kispoko.tome.util.yaml.error.EmptyValueError;
import com.kispoko.tome.util.yaml.error.MissingKeyError;
import com.kispoko.tome.util.yaml.error.UnexpectedTypeError;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;



/**
 * Yaml
 *
 * Design Considerations
 * -----------------------------------------------------------------------
 * - More consise
 * - Gives errors on incorrectly typed nodes and missing keys in maps.
 * - Tracks the context for better error messages.
 */
public class YamlParser
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private Object yamlObject;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    private YamlParser(Object yamlObject)
    {
        this.yamlObject = yamlObject;
    }


    public static YamlParser fromFile(InputStream yamlFileIS)
    {
        org.yaml.snakeyaml.Yaml yaml = new org.yaml.snakeyaml.Yaml();
        Object yamlObject = yaml.load(yamlFileIS);
        return new YamlParser(yamlObject);
    }


    // API
    // ------------------------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public YamlParser atKey(String key)
           throws YamlParseException
    {
        if (this.yamlObject == null)
            return new YamlParser(null);

        Object yamlObjectAtKey;
        try {
            Map<String,Object> yamlMap = (Map<String,Object>) this.yamlObject;
            if (yamlMap.containsKey(key))
                yamlObjectAtKey = yamlMap.get(key);
            else
                throw YamlParseException.missingKey(new MissingKeyError(key));
        } catch (ClassCastException e) {
            throw YamlParseException.unexpectedType(new UnexpectedTypeError(YamlObjectType.MAP));
        }

        return new YamlParser(yamlObjectAtKey);
    }


    @SuppressWarnings("unchecked")
    public YamlParser atMaybeKey(String key)
           throws YamlParseException
    {
        if (this.yamlObject == null)
            return new YamlParser(null);

        Object yamlObjectAtKey;
        try {
            Map<String,Object> yamlMap = (Map<String,Object>) this.yamlObject;
            yamlObjectAtKey = yamlMap.get(key);
        } catch (ClassCastException e) {
            throw YamlParseException.unexpectedType(new UnexpectedTypeError(YamlObjectType.MAP));
        }

        return new YamlParser(yamlObjectAtKey);
    }


    @SuppressWarnings("unchecked")
    public <A> List<A> forEach(ForEach<A> forEach, boolean canBeEmpty)
               throws YamlParseException
    {
        if (this.yamlObject == null)
        {
            if (canBeEmpty)
                return new ArrayList<>();
            else
                throw YamlParseException.emptyValue(new EmptyValueError());
        }

        List<A> list = new ArrayList<>();

        try {
            List<Object> objectList = (List<Object>) this.yamlObject;
            for (int i = 0; i < objectList.size(); i++) {
                list.add(forEach.forEach(new YamlParser(objectList.get(i)), i));
            }
        } catch (ClassCastException e) {
            throw YamlParseException.unexpectedType(new UnexpectedTypeError(YamlObjectType.LIST));
        }

        return list;
    }


    public <A> List<A> forEach(ForEach<A> forEach)
               throws YamlParseException
    {
        return this.forEach(forEach, false);
    }


    public abstract static class ForEach<A>
    {
        abstract public A forEach(YamlParser yaml, int index)
                        throws YamlParseException;
    }


    @SuppressWarnings("unchecked")
    public List<String> getStringList()
        throws YamlParseException
    {
        if (this.yamlObject == null)
            return new ArrayList<>();

        List<String> stringList;

        try {
            stringList = (List<String>) this.yamlObject;
        } catch (ClassCastException e) {
            throw YamlParseException.unexpectedType(new UnexpectedTypeError(YamlObjectType.LIST_STRING));
        }

        return stringList;
    }


    public String getString()
           throws YamlParseException
    {
        if (this.yamlObject == null)
            return null;

        String stringValue;

        try {
            stringValue = (String) this.yamlObject;
        } catch (ClassCastException e) {
            throw YamlParseException.unexpectedType(new UnexpectedTypeError(YamlObjectType.STRING));
        }

        return stringValue;
    }


    public String getTrimmedString()
           throws YamlParseException
    {
        String parsedString = this.getString();

        if (parsedString != null)
            return parsedString.trim();
        else
            return null;
    }


    public Integer getInteger()
           throws YamlParseException
    {
        if (this.yamlObject == null)
            return null;

        Integer integerValue;

        try {
            integerValue = (Integer) this.yamlObject;
        } catch (ClassCastException e) {
            throw YamlParseException.unexpectedType(new UnexpectedTypeError(YamlObjectType.INTEGER));
        }

        return integerValue;
    }


    public Boolean getBoolean()
           throws YamlParseException
    {
        if (this.yamlObject == null)
            return null;

        Boolean booleanValue;

        try {
            booleanValue = (Boolean) this.yamlObject;
        } catch (ClassCastException e) {
            throw YamlParseException.unexpectedType(new UnexpectedTypeError(YamlObjectType.BOOLEAN));
        }

        return booleanValue;
    }


    public Object getObject()
    {
        return this.yamlObject;
    }


    public boolean isNull()
    {
        return this.yamlObject == null;
    }


}
