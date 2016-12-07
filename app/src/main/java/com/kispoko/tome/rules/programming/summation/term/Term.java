
package com.kispoko.tome.rules.programming.summation.term;


import com.kispoko.tome.exception.InvalidDataException;
import com.kispoko.tome.rules.programming.summation.SummationException;
import com.kispoko.tome.util.EnumUtils;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;
import com.kispoko.tome.util.yaml.error.InvalidEnumError;



/**
 * Term
 */
public abstract class Term implements Model
{

    // INTERFACE
    // ------------------------------------------------------------------------------------------

    public abstract Integer value() throws SummationException;


    // METHODS
    // ------------------------------------------------------------------------------------------

    public static Term fromYaml(Yaml yaml)
                  throws YamlException
    {
        Type type = Type.fromYaml(yaml.atKey("type"));

        switch (type)
        {
            case LITERAL:
                return LiteralTerm.fromYaml(yaml);
            case CONDITIONAL:
                return ConditionalTerm.fromYaml(yaml);
        }

        return null;
    }


    // TYPE
    // ------------------------------------------------------------------------------------------

    private enum Type
    {
        LITERAL,
        CONDITIONAL;


        public static Type fromString(String typeString)
                      throws InvalidDataException
        {
            return EnumUtils.fromString(Type.class, typeString);
        }


        public static Type fromYaml(Yaml yaml)
                      throws YamlException
        {
            String typeString = yaml.getString();
            try {
                return Type.fromString(typeString);
            } catch (InvalidDataException e) {
                throw YamlException.invalidEnum(new InvalidEnumError(typeString));
            }
        }

    }


}
