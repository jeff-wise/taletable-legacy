
package com.kispoko.tome.model.sheet.style


import effect.effError
import effect.effValue
import lulo.document.DocText
import lulo.document.DocType
import lulo.document.SpecDoc
import lulo.document.docType
import lulo.value.UnexpectedType
import lulo.value.UnexpectedValue
import lulo.value.ValueError
import lulo.value.ValueParser



/**
 * Alignment
 */
sealed class Alignment
{

    class Left : Alignment()
    class Center : Alignment()
    class Right : Alignment()


    companion object
    {
        fun fromDocument(doc : SpecDoc) : ValueParser<Alignment> = when (doc)
        {
            is DocText -> when (doc.text)
            {
                "left"   -> effValue<ValueError,Alignment>(Alignment.Left())
                "center" -> effValue<ValueError,Alignment>(Alignment.Center())
                "right"  -> effValue<ValueError,Alignment>(Alignment.Right())
                else     -> effError<ValueError,Alignment>(
                                    UnexpectedValue("Alignment", doc.text, doc.path))
            }
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


}


/**
 * Vertical Alignment
 */
sealed class VerticalAlignment
{

    class Top : VerticalAlignment()
    class Middle : VerticalAlignment()
    class Bottom : VerticalAlignment()


    companion object
    {
        fun fromDocument(doc : SpecDoc) : ValueParser<VerticalAlignment> = when (doc)
        {
            is DocText -> when (doc.text)
            {
                "top"    -> effValue<ValueError,VerticalAlignment>(VerticalAlignment.Top())
                "middle" -> effValue<ValueError,VerticalAlignment>(VerticalAlignment.Middle())
                "bottom" -> effValue<ValueError,VerticalAlignment>(VerticalAlignment.Bottom())
                else     -> effError<ValueError,VerticalAlignment>(
                                    UnexpectedValue("VerticalAlignment", doc.text, doc.path))
            }
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


}


//
//
//public enum VerticalAlignment implements ToYaml
//{
//
//
//    // VALUES
//    // ------------------------------------------------------------------------------------------
//
//    TOP,
//    MIDDLE,
//    BOTTOM;
//
//
//    // CONSTRUCTORS
//    // ------------------------------------------------------------------------------------------
//
//    public static VerticalAlignment fromString(String alignmentString)
//                  throws InvalidDataException
//    {
//        return EnumUtils.fromString(VerticalAlignment.class, alignmentString);
//    }
//
//
//    public static VerticalAlignment fromYaml(YamlParser yaml)
//                  throws YamlParseException
//    {
//        if (yaml.isNull())
//            return null;
//
//        String alignmentString = yaml.getString();
//        try {
//            return VerticalAlignment.fromString(alignmentString);
//        } catch (InvalidDataException e) {
//            throw YamlParseException.invalidEnum(new InvalidEnumError(alignmentString));
//        }
//    }
//
//
//    public static VerticalAlignment fromSQLValue(SQLValue sqlValue)
//                  throws DatabaseException
//    {
//        String enumString = "";
//        try {
//            enumString = sqlValue.getText();
//            VerticalAlignment alignment = VerticalAlignment.fromString(enumString);
//            return alignment;
//        } catch (InvalidDataException e) {
//            throw DatabaseException.invalidEnum(
//                    new com.kispoko.tome.lib.database.error.InvalidEnumError(enumString));
//        }
//    }
//
//
//    // TO YAML
//    // ------------------------------------------------------------------------------------------
//
//    public YamlBuilder toYaml()
//    {
//        return YamlBuilder.string(this.name().toLowerCase());
//    }
//
//
//    // TO STRING
//    // ------------------------------------------------------------------------------------------
//
//    @Override
//    public String toString()
//    {
//        switch (this)
//        {
//            case TOP:
//                return "Top";
//            case MIDDLE:
//                return "Middle";
//            case BOTTOM:
//                return "Bottom";
//        }
//
//        return "";
//    }
//
//
//    // GRAVITY CONSTANT
//    // ------------------------------------------------------------------------------------------
//
//    public int gravityConstant()
//    {
//        switch (this)
//        {
//            case TOP:
//                return Gravity.TOP;
//            case MIDDLE:
//                return Gravity.CENTER_VERTICAL;
//            case BOTTOM:
//                return Gravity.BOTTOM;
//            default:
//                return Gravity.CENTER_VERTICAL;
//        }
//    }

