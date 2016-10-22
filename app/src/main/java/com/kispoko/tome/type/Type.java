
package com.kispoko.tome.type;


import java.io.Serializable;
import java.util.Map;



/**
 * Type
 */
public abstract class Type
{

    // > PROPERTIES
    // -------------------------------------------------------------------------------------------

    private Type.Id id;


    // > CONSTRUCTORS
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
        private String kind;
        private String id;

        public Id(String kind, String id)
        {
            this.kind = kind;
            this.id = id;
        }

        public boolean isNull()
        {
            return this.kind == null && this.id == null;
        }

        public String getKind()
        {
            return this.kind;
        }

        public String getId()
        {
            return this.id;
        }
    }
}
