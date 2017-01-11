
package com.kispoko.tome.engine.variable;

import com.kispoko.tome.exception.InvalidDataException;
import com.kispoko.tome.util.EnumUtils;
import com.kispoko.tome.util.database.DatabaseException;
import com.kispoko.tome.util.database.sql.SQLValue;
import com.kispoko.tome.util.yaml.YamlParser;
import com.kispoko.tome.util.yaml.YamlParseException;
import com.kispoko.tome.util.yaml.error.InvalidEnumError;



/**
 * Variable Reference Type
 */
public enum VariableReferenceType
{

    NAME,
    TAG;


    public static VariableReferenceType fromString(String typeString)
                  throws InvalidDataException
    {
        return EnumUtils.fromString(VariableReferenceType.class, typeString);
    }


    public static VariableReferenceType fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        String typeString = yaml.getString();
        try {
            return VariableReferenceType.fromString(typeString);
        } catch (InvalidDataException e) {
            throw YamlParseException.invalidEnum(new InvalidEnumError(typeString));
        }
    }


    public static VariableReferenceType fromSQLValue(SQLValue sqlValue)
                  throws DatabaseException
    {
        String enumString = "";
        try {
            enumString = sqlValue.getText();
            VariableReferenceType typeString = VariableReferenceType.fromString(enumString);
            return typeString;
        } catch (InvalidDataException e) {
            throw DatabaseException.invalidEnum(
                    new com.kispoko.tome.util.database.error.InvalidEnumError(enumString));
        }
    }

}
