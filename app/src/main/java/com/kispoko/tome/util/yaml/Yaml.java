
package com.kispoko.tome.util.yaml;


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
public class Yaml
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private Object yamlObject;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    private Yaml(Object yamlObject)
    {
        this.yamlObject = yamlObject;
    }


    public static Yaml fromFile(InputStream yamlFileIS)
    {
        org.yaml.snakeyaml.Yaml yaml = new org.yaml.snakeyaml.Yaml();
        Object yamlObject = yaml.load(yamlFileIS);
        return new Yaml(yamlObject);
    }


    // API
    // ------------------------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public Yaml atKey(String key)
           throws YamlException
    {
        if (this.yamlObject == null)
            return new Yaml(null);

        Object yamlObjectAtKey;
        try {
            Map<String,Object> yamlMap = (Map<String,Object>) this.yamlObject;
            if (yamlMap.containsKey(key))
                yamlObjectAtKey = yamlMap.get(key);
            else
                throw new YamlException(new MissingKeyError(key),
                                        YamlException.Type.MISSING_KEY);
        } catch (ClassCastException e) {
            throw new YamlException(new UnexpectedTypeError(ObjectType.MAP),
                                    YamlException.Type.UNEXPECTED_TYPE);
        }

        return new Yaml(yamlObjectAtKey);
    }


    @SuppressWarnings("unchecked")
    public Yaml atMaybeKey(String key)
           throws YamlException
    {
        if (this.yamlObject == null)
            return new Yaml(null);

        Object yamlObjectAtKey;
        try {
            Map<String,Object> yamlMap = (Map<String,Object>) this.yamlObject;
            yamlObjectAtKey = yamlMap.get(key);
        } catch (ClassCastException e) {
            throw new YamlException(new UnexpectedTypeError(ObjectType.MAP),
                                    YamlException.Type.UNEXPECTED_TYPE);
        }

        return new Yaml(yamlObjectAtKey);
    }


    @SuppressWarnings("unchecked")
    public <A> List<A> forEach(ForEach<A> forEach)
               throws YamlException
    {
        if (this.yamlObject == null)
            return null;

        List<A> list = new ArrayList<>();

        try {
            List<Object> objectList = (List<Object>) this.yamlObject;
            for (int i = 0; i < objectList.size(); i++) {
                list.add(forEach.forEach(new Yaml(objectList.get(i)), i));
            }
        } catch (ClassCastException e) {
            throw new YamlException(new UnexpectedTypeError(ObjectType.LIST),
                                    YamlException.Type.UNEXPECTED_TYPE);
        }

        return list;
    }


    public abstract static class ForEach<A>
    {
        abstract public A forEach(Yaml yaml, int index)
                        throws YamlException;
    }


    @SuppressWarnings("unchecked")
    public List<String> getStringList()
        throws YamlException
    {
        if (this.yamlObject == null)
            return null;

        List<String> stringList;

        try {
            stringList = (List<String>) this.yamlObject;
        } catch (ClassCastException e) {
            throw new YamlException(new UnexpectedTypeError(ObjectType.LIST_STRING),
                                    YamlException.Type.UNEXPECTED_TYPE);
        }

        return stringList;
    }


    public String getString()
           throws YamlException
    {
        if (this.yamlObject == null)
            return null;

        String stringValue;

        try {
            stringValue = (String) this.yamlObject;
        } catch (ClassCastException e) {
            throw new YamlException(new UnexpectedTypeError(ObjectType.STRING),
                                    YamlException.Type.UNEXPECTED_TYPE);
        }

        return stringValue;
    }


    public Integer getInteger()
           throws YamlException
    {
        if (this.yamlObject == null)
            return null;

        Integer integerValue;

        try {
            integerValue = (Integer) this.yamlObject;
        } catch (ClassCastException e) {
            throw new YamlException(new UnexpectedTypeError(ObjectType.INTEGER),
                                    YamlException.Type.UNEXPECTED_TYPE);
        }

        return integerValue;
    }


    public Boolean getBoolean()
           throws YamlException
    {
        if (this.yamlObject == null)
            return null;

        Boolean booleanValue;

        try {
            booleanValue = (Boolean) this.yamlObject;
        } catch (ClassCastException e) {
            throw new YamlException(new UnexpectedTypeError(ObjectType.BOOLEAN),
                                    YamlException.Type.UNEXPECTED_TYPE);
        }

        return booleanValue;
    }


    public Object getObject()
    {
        return this.yamlObject;
    }


    // NESTED DEFINITIONS
    // -----------------------------------------------------------------------------------------

    public enum ObjectType
    {
        STRING,
        INTEGER,
        BOOLEAN,
        MAP,
        LIST,
        LIST_STRING
    }


}
