
package com.kispoko.tome.type;


import android.util.Log;

import java.io.Serializable;
import java.util.Map;



/**
 * Type
 */
public abstract class Type implements Serializable
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


        @SuppressWarnings("unchecked")
        public static Type.Id fromYaml(Map<String,Object> dataYaml)
        {
            if (dataYaml.containsKey("type"))
            {
                Map<String, Object> typeYaml = (Map<String, Object>) dataYaml.get("type");

                String _typeId = null;
                String typeKind = null;

                if (typeYaml.containsKey("id"))
                    _typeId = (String) typeYaml.get("id");

                if (typeYaml.containsKey("kind"))
                    typeKind = (String) typeYaml.get("kind");

                Log.d("***TYPE", "parsing type");

                return new Type.Id(typeKind, _typeId);
            }

            return null;
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
