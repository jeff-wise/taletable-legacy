
package com.kispoko.tome.util.yaml;


import org.yaml.snakeyaml.Yaml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * Yaml Builder
 */
public class YamlBuilder
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private Map<String,Object> mapValue;
    private List<Object>       listValue;
    private String             stringValue;
    private Integer            integerValue;
    private Boolean            booleanValue;

    private YamlObjectType     type;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    /**
     * Create a new Yaml Builder of the specified type. This constructor is private because a
     * type-specific constructor should be used instead to ensure correct behavior.
     * @param type The yaml object ytpe.
     */
    @SuppressWarnings("unchecked")
    private YamlBuilder(YamlObjectType type, Object initialValue)
    {
        this.type           = type;

        this.mapValue       = null;
        this.stringValue    = null;
        this.integerValue   = null;
        this.booleanValue   = null;

        switch (type)
        {
            case MAP:
                this.mapValue = (Map<String,Object>) initialValue;
                break;
            case LIST:
                this.listValue = (List<Object>) initialValue;
                break;
            case STRING:
                this.stringValue = (String) initialValue;
                break;
            case INTEGER:
                this.integerValue = (Integer) initialValue;
                break;
            case BOOLEAN:
                this.booleanValue = (Boolean) initialValue;
                break;
        }
    }


    /**
     * Create a Yaml Builder that represents a Yaml map.
     * @return The Yaml Builder.
     */
    public static YamlBuilder map()
    {
        return new YamlBuilder(YamlObjectType.MAP, new HashMap<>());
    }


    /**
     * Create a Yaml Builder that represents a Yaml list.
     * @return The Yaml Builder.
     */
    public static <A extends ToYaml> YamlBuilder list(List<A> toYamlList)
    {
        List<Object> yamlObjectList = new ArrayList<>();

        for (ToYaml toYaml : toYamlList) {
            yamlObjectList.add(toYaml.toYaml().value());
        }

        return new YamlBuilder(YamlObjectType.LIST, yamlObjectList);
    }


    /**
     * Create a Yaml Builder that represents a Yaml string.
     * @return The Yaml Builder.
     */
    public static YamlBuilder string(String value)
    {
        return new YamlBuilder(YamlObjectType.STRING, value);
    }


    /**
     * Create a Yaml Builder that represents a Yaml string.
     * @return The Yaml Builder.
     */
    public static YamlBuilder integer(Integer value)
    {
        return new YamlBuilder(YamlObjectType.INTEGER, value);
    }


    /**
     * Create a Yaml Builder that represents a Yaml boolean.
     * @return The Yaml Builder.
     */
    public static YamlBuilder bool(Boolean value)
    {
        return new YamlBuilder(YamlObjectType.BOOLEAN, value);
    }

    // API
    // ------------------------------------------------------------------------------------------

    // > Exporting
    // ------------------------------------------------------------------------------------------

    /**
     * The yaml as a string.
     * @return The yaml string.
     */
    public String toString()
    {
        Yaml yaml = new Yaml();
        return yaml.dump(this.mapValue);
    }


    /**
     * The Yaml Builder value as an object.
     * @return The object value.
     */
    public Object value()
    {
        switch (this.type)
        {
            case MAP:
                return this.mapValue;
            case LIST:
                return this.listValue;
            case STRING:
                return this.stringValue;
            case INTEGER:
                return this.integerValue;
            case BOOLEAN:
                return this.booleanValue;
        }

        return null;
    }


    // > Construction
    // ------------------------------------------------------------------------------------------

    /**
     * Add a key with a yaml value to the map.
     * @param key The key.
     * @param value The yaml value at the key.
     * @return The Yaml Builder.
     */
    public <A extends ToYaml> YamlBuilder putYaml(String key, A value)
    {
        YamlBuilder yamlBuilder = value.toYaml();
        return this.putYaml(key, yamlBuilder);
    }


    public YamlBuilder putYaml(String key, YamlBuilder yamlBuilder)
    {
        this.mapValue.put(key, yamlBuilder.value());
        return this;
    }


    /**
     * Add a key with a string value to the yaml map.
     * @param key The key.
     * @param value The value.
     * @return The Yaml Builder.
     */
    public YamlBuilder putString(String key, String value)
    {
        this.mapValue.put(key, value);
        return this;
    }


    /**
     * Add a key with an integer value to the yaml map.
     * @param key The key.
     * @param value The value.
     * @return The Yaml Builder.
     */
    public YamlBuilder putInteger(String key, Integer value)
    {
        this.mapValue.put(key, value);
        return this;
    }

    /**
     * Add a key with a boolean value to the yaml map.
     * @param key The key.
     * @param value The boolean value.
     * @return The Yaml Builder.
     */
    public YamlBuilder putBoolean(String key, Boolean value)
    {
        this.mapValue.put(key, value);
        return this;
    }


    /**
     * Add a key with an list value to the yaml map.
     * @param key The key.
     * @param list The list.
     * @return The Yaml Builder.
     */
    public <A extends ToYaml> YamlBuilder putList(String key, List<A> list)
    {
        List<Object> yamlObjectList = new ArrayList<>();

        for (ToYaml toYamlObject : list) {
            yamlObjectList.add(toYamlObject.toYaml().value());
        }

        this.mapValue.put(key, yamlObjectList);

        return this;
    }


    /**
     * Add a key with an array value to the yaml map.
     * @param key The key.
     * @param toYamlArray The array.
     * @param <A> The type of yaml-serializable item in the array.
     * @return The Yaml Builder.
     */
    public <A extends ToYaml> YamlBuilder putArray(String key, A[] toYamlArray)
    {
        return this.putList(key, Arrays.asList(toYamlArray));
    }


    /**
     * Add a key with a value that is a list of strings.
     * @param key The key.
     * @param stringList The string list.
     * @return The Yaml Builder.
     */
    public YamlBuilder putStringList(String key, List<String> stringList)
    {
        this.mapValue.put(key, stringList);
        return this;
    }


}
