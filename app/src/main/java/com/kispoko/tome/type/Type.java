
package com.kispoko.tome.type;


import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

import java.io.Serializable;
import java.util.Map;



/**
 * Type
 */
public abstract class Type implements Serializable
{

    // PROPERTIES
    // -------------------------------------------------------------------------------------------

    private Type.Id id;


    // CONSTRUCTORS
    // -------------------------------------------------------------------------------------------

    public Type(Type.Id id)
    {
        this.id = id;
    }


    public static Type fromYaml(Map<String,Object> typeYaml)
    {
        String kind = (String) typeYaml.get("kind");

        switch (kind)
        {
            case "list":
                return ListType.fromYaml(typeYaml);
        }

        return null;
    }


    // > API
    // -------------------------------------------------------------------------------------------

    public Type.Id getId()
    {
        return this.id;
    }


    // > TYPE ID
    // -------------------------------------------------------------------------------------------

    public static class Id implements Serializable
    {

    }
}
