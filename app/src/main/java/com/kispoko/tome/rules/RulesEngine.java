
package com.kispoko.tome.rules;


import com.kispoko.tome.type.Type;

import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * Rules Engine
 */
public class RulesEngine
{

    // > PROPERTIES
    // ------------------------------------------------------------------------------------------

    private static Map<String, Type> typeByName;


    // > CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public RulesEngine()
    {
        typeByName = new HashMap<>();
    }



    // > API
    // ------------------------------------------------------------------------------------------

    /**
     * Add new types to the engine.
     */
    public static void addTypes(List<Type> types)
    {
        for (Type _type : types)
        {
            typeByName.put(_type.getName(), _type);
        }
    }


    public static Type getType(String name)
    {
        // TODO log exception
        return typeByName.get(name);
    }

}
