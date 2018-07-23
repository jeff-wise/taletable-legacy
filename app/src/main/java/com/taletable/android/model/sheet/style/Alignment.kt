
package com.taletable.android.model.sheet.style


import android.view.Gravity
import android.widget.RelativeLayout
import com.taletable.android.lib.orm.sql.SQLSerializable
import com.taletable.android.lib.orm.sql.SQLText
import com.taletable.android.lib.orm.sql.SQLValue
import effect.effError
import effect.effValue
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.UnexpectedValue
import lulo.value.ValueError
import lulo.value.ValueParser
import java.io.Serializable



/**
 * Alignment
 */
sealed class Alignment : ToDocument, SQLSerializable, Serializable
{

    object Left : Alignment()
    {

        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({"left"})

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("left")

    }


    object Center : Alignment()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({"center"})

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("center")

    }


    object Right : Alignment()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({"right"})

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("right")

    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object
    {
        fun fromDocument(doc : SchemaDoc) : ValueParser<Alignment> = when (doc)
        {
            is DocText -> when (doc.text)
            {
                "left"   -> effValue<ValueError,Alignment>(Alignment.Left)
                "center" -> effValue<ValueError,Alignment>(Alignment.Center)
                "right"  -> effValue<ValueError,Alignment>(Alignment.Right)
                else     -> effError<ValueError,Alignment>(
                                    UnexpectedValue("Alignment", doc.text, doc.path))
            }
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    fun gravityConstant() : Int = when (this)
    {
        is Left   -> Gravity.START
        is Center -> Gravity.CENTER_HORIZONTAL
        is Right  -> Gravity.END
    }


}


/**
 * Vertical Alignment
 */
sealed class VerticalAlignment : ToDocument, SQLSerializable, Serializable
{

    object Top : VerticalAlignment()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({"top"})

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("top")

    }


    object Middle : VerticalAlignment()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({"middle"})

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("middle")

    }


    object Bottom : VerticalAlignment()
    {
        // SQL SERIALIZABLE
        // -------------------------------------------------------------------------------------

        override fun asSQLValue() : SQLValue = SQLText({"bottom"})

        // TO DOCUMENT
        // -------------------------------------------------------------------------------------

        override fun toDocument() = DocText("bottom")

    }


    companion object
    {
        fun fromDocument(doc : SchemaDoc) : ValueParser<VerticalAlignment> = when (doc)
        {
            is DocText -> when (doc.text)
            {
                "top"    -> effValue<ValueError,VerticalAlignment>(VerticalAlignment.Top)
                "middle" -> effValue<ValueError,VerticalAlignment>(VerticalAlignment.Middle)
                "bottom" -> effValue<ValueError,VerticalAlignment>(VerticalAlignment.Bottom)
                else     -> effError<ValueError,VerticalAlignment>(
                                    UnexpectedValue("VerticalAlignment", doc.text, doc.path))
            }
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    fun gravityConstant() : Int = when (this)
    {
        is Top    -> Gravity.TOP
        is Middle -> Gravity.CENTER_VERTICAL
        is Bottom -> Gravity.BOTTOM
    }


    fun relativeLayoutRule() : Int = when (this)
    {
        is Top    -> RelativeLayout.ALIGN_PARENT_TOP
        is Middle -> RelativeLayout.CENTER_VERTICAL
        is Bottom -> RelativeLayout.ALIGN_PARENT_BOTTOM
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
//                    new com.taletable.tome.lib.database.error.InvalidEnumError(enumString));
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

