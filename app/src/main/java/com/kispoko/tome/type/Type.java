package com.kispoko.tome.type;

import java.util.Map;

/**
 * Created by jeff on 10/3/16.
 */

public abstract class Type
{

    // > PROPERTIES
    // -------------------------------------------------------------------------------------------

    private String name;


    // > CONSTRUCTORS
    // -------------------------------------------------------------------------------------------

    public Type(String name)
    {
        this.name = name;
    }


    public static Type fromYaml(Map<String,Object> typeYaml)
    {
        String kind = (String) typeYaml.get("type");

        switch (kind)
        {
            case "list":
                return List.fromYaml(typeYaml);
        }

        return null;
    }


    // > API
    // -------------------------------------------------------------------------------------------

    public String getName()
    {
        return this.name;
    }

}
