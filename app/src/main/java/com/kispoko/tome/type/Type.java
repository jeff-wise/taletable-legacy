
package com.kispoko.tome.type;


import java.util.Map;



/**
 * Type
 */
public abstract class Type
{

    // > PROPERTIES
    // -------------------------------------------------------------------------------------------

    private String id;


    // > CONSTRUCTORS
    // -------------------------------------------------------------------------------------------

    public Type(String id)
    {
        this.id = id;
    }


    public static Type fromYaml(Map<String,Object> typeYaml)
    {
        String kind = (String) typeYaml.get("kind");

        switch (kind)
        {
            case "list":
                return List.fromYaml(typeYaml);
        }

        return null;
    }


    // > API
    // -------------------------------------------------------------------------------------------

    public String getId()
    {
        return this.id;
    }


    // > TYPE ID
    // -------------------------------------------------------------------------------------------

    public static class Id
    {
        private String kind;
        private String id;

        public Id(String kind, String id)
        {
            this.kind = kind;
            this.id = id;
        }

        public String getKind()
        {
            return this.kind;
        }

        public String geId()
        {
            return this.id;
        }
    }
}
