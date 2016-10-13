
package com.kispoko.tome.rules;


import com.kispoko.tome.type.List;
import com.kispoko.tome.type.Type;

import java.util.HashMap;
import java.util.Map;



/**
 * Rules Engine
 */
public class RulesEngine
{

    // > PROPERTIES
    // ------------------------------------------------------------------------------------------

    private static Map<String, Type> listTypeIndex = new HashMap<>();



    // > CONSTRUCTORS
    // ------------------------------------------------------------------------------------------



    // > API
    // ------------------------------------------------------------------------------------------

    /**
     * Add new types to the engine.
     */
    public static void addType(Type _type)
    {
        Type.Id typeId = _type.getId();
        String kind = typeId.getKind();
        switch (kind)
        {
            case "list":
                listTypeIndex.put(typeId.getId(), _type);
                break;
        }
    }


    public static Type getType(Type.Id typeId)
    {
        String kind = typeId.getKind();
        switch (kind) {
            case "list":
                return listTypeIndex.get(typeId.getId());
            default:
                return null;
        }
    }

}
