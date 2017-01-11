
package com.kispoko.tome.engine.refinement;


import com.kispoko.tome.exception.InvalidDataException;
import com.kispoko.tome.util.EnumUtils;
import com.kispoko.tome.util.database.DatabaseException;
import com.kispoko.tome.util.database.sql.SQLValue;
import com.kispoko.tome.util.yaml.ToYaml;
import com.kispoko.tome.util.yaml.YamlBuilder;
import com.kispoko.tome.util.yaml.YamlParser;
import com.kispoko.tome.util.yaml.YamlParseException;
import com.kispoko.tome.util.yaml.error.InvalidEnumError;



/**
* Refinement ErrorType
*/
public enum RefinementType implements ToYaml
{
    MEMBER_OF;


    public static RefinementType fromString(String typeString)
                  throws InvalidDataException
    {
        return EnumUtils.fromString(RefinementType.class, typeString);
    }


    public static RefinementType fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        String sizeString = yaml.getString();
        try {
            return RefinementType.fromString(sizeString);
        } catch (InvalidDataException e) {
            throw YamlParseException.invalidEnum(new InvalidEnumError(sizeString));
        }
    }


    public static RefinementType fromSQLValue(SQLValue sqlValue)
                  throws DatabaseException
    {
        String enumString = "";
        try {
            enumString = sqlValue.getText();
            RefinementType refinementType = RefinementType.fromString(enumString);
            return refinementType;
        } catch (InvalidDataException e) {
            throw DatabaseException.invalidEnum(
                    new com.kispoko.tome.util.database.error.InvalidEnumError(enumString));
        }
    }


    public YamlBuilder toYaml()
    {
        return YamlBuilder.string(this.name().toLowerCase());
    }

}


