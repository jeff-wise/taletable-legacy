
package com.kispoko.tome.type;


import android.util.Log;

import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;

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


        public static Type.Id fromYaml(Yaml yaml)
                      throws YamlException
        {
            String typeId   = yaml.atKey("id").getString();
            String typeKind = yaml.atKey("kind").getString();

            return new Type.Id(typeId, typeKind);
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
