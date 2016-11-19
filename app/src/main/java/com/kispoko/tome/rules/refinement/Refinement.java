
package com.kispoko.tome.rules.refinement;


import com.kispoko.tome.exception.InvalidDataException;
import com.kispoko.tome.util.EnumUtils;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.PrimitiveValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;
import com.kispoko.tome.util.yaml.error.InvalidEnumError;

import java.io.Serializable;
import java.util.UUID;



/**
 * Refinement Id
 */
public class Refinement
{

    public enum Type
    {
        MEMBER_OF;


        public static Type fromString(String typeString)
                      throws InvalidDataException
        {
            return EnumUtils.fromString(Type.class, typeString);
        }


        public static Type fromYaml(Yaml yaml)
                      throws YamlException
        {
            String sizeString = yaml.getString();
            try {
                return Type.fromString(sizeString);
            } catch (InvalidDataException e) {
                throw new YamlException(new InvalidEnumError(sizeString),
                                        YamlException.Type.INVALID_ENUM);
            }
        }

    }


    public static class Id implements Model, Serializable
    {

        // PROPERTIES
        // --------------------------------------------------------------------------------------

        private UUID                   id;
        private PrimitiveValue<String> name;
        private PrimitiveValue<Type>   type;


        // CONSTRUCTORS
        // --------------------------------------------------------------------------------------

        public Id(UUID id, String name, Type type)
        {
            this.id   = id;
            this.name = new PrimitiveValue<>(name, this, String.class);
            this.type = new PrimitiveValue<>(type, this, Type.class);
        }


        public static Id fromYaml(Yaml yaml)
                      throws YamlException
        {
            UUID   id   = UUID.randomUUID();
            String name = yaml.atKey("name").getString();
            Type   type = Type.fromYaml(yaml.atKey("type"));

            return new Id(id, name, type);
        }


        // API
        // --------------------------------------------------------------------------------------

        // > Model
        // --------------------------------------------------------------------------------------

        // ** Id
        // --------------------------------------------------------------------------------------

        public UUID getId()
        {
            return this.id;
        }


        public void setId(UUID id)
        {
            this.id = id;
        }


        // ** On Update
        // --------------------------------------------------------------------------------------

        public void onModelUpdate(String valueName) { }


        // > State
        // --------------------------------------------------------------------------------------

        public String getName()
        {
            return this.name.getValue();
        }


        public Type getType()
        {
            return this.type.getValue();
        }

    }

}
