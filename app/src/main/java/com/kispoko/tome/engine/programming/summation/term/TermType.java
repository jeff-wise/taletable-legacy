
package com.kispoko.tome.engine.programming.summation.term;


import com.kispoko.tome.exception.InvalidDataException;
import com.kispoko.tome.util.EnumUtils;
import com.kispoko.tome.util.database.DatabaseException;
import com.kispoko.tome.util.database.sql.SQLValue;
import com.kispoko.tome.util.yaml.Yaml;
import com.kispoko.tome.util.yaml.YamlException;
import com.kispoko.tome.util.yaml.error.InvalidEnumError;



/**
 * Term Type
 */
public enum TermType
{


    LITERAL,
    DICE_ROLL,
    CONDITIONAL;


    public static TermType fromString(String typeString)
                  throws InvalidDataException
    {
        return EnumUtils.fromString(TermType.class, typeString);
    }


    /**
     * Create a ColumnType from its Yaml representation.
     * @param yaml The Yaml parser.
     * @return The parsed TermType.
     * @throws YamlException
     */
    public static TermType fromYaml(Yaml yaml)
                  throws YamlException
    {
        String typeString = yaml.getString();
        try {
            return TermType.fromString(typeString);
        } catch (InvalidDataException e) {
            throw YamlException.invalidEnum(new InvalidEnumError(typeString));
        }
    }


    public static TermType fromSQLValue(SQLValue sqlValue)
                  throws DatabaseException
    {
        String enumString = "";
        try {
            enumString = sqlValue.getText();
            TermType termType = TermType.fromString(enumString);
            return termType;
        } catch (InvalidDataException e) {
            throw DatabaseException.invalidEnum(
                    new com.kispoko.tome.util.database.error.InvalidEnumError(enumString));
        }
    }

}
