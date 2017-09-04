
package com.kispoko.tome.model.user


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import effect.effError
import effect.effValue
import lulo.document.DocText
import lulo.document.DocType
import lulo.document.SchemaDoc
import lulo.document.docType
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import java.io.Serializable



/**
 * User Name
 */
data class UserName(val value : String) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<UserName>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<UserName> = when (doc)
        {
            is DocText -> effValue(UserName(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value})

}

