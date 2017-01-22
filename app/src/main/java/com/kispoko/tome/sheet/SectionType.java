
package com.kispoko.tome.sheet;


import android.content.Context;

import com.kispoko.tome.R;
import com.kispoko.tome.exception.InvalidDataException;
import com.kispoko.tome.sheet.widget.table.cell.CellType;
import com.kispoko.tome.util.EnumUtils;
import com.kispoko.tome.util.database.DatabaseException;
import com.kispoko.tome.util.database.error.InvalidEnumError;
import com.kispoko.tome.util.database.sql.SQLValue;


/**
 * Section Type
 */
public enum SectionType
{
    PROFILE,
    ENCOUNTER,
    CAMPAIGN;


    public String toString(Context context)
    {
        switch (this)
        {
            case PROFILE:
                return context.getString(R.string.section_profile);
            case ENCOUNTER:
                return context.getString(R.string.section_encounter);
            case CAMPAIGN:
                return context.getString(R.string.section_campaign);
        }

        return "";
    }


    public static SectionType fromString(String typeString)
                  throws InvalidDataException
    {
        return EnumUtils.fromString(SectionType.class, typeString);
    }


    public static SectionType fromSQLValue(SQLValue sqlValue)
                  throws DatabaseException
    {
        String enumString = "";
        try {
            enumString = sqlValue.getText();
            SectionType sectionType = SectionType.fromString(enumString);
            return sectionType;
        } catch (InvalidDataException e) {
            throw DatabaseException.invalidEnum(
                    new InvalidEnumError(enumString));
        }
    }

}
