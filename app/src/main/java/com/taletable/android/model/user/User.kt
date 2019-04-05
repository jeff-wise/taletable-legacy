
package com.taletable.android.model.user


import com.taletable.android.lib.Factory
import com.taletable.android.lib.orm.sql.SQLSerializable
import com.taletable.android.lib.orm.sql.SQLText
import com.taletable.android.lib.orm.sql.SQLValue
import com.taletable.android.model.user.catalog.Catalog
import effect.effError
import effect.effValue
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueError
import lulo.value.ValueParser
import java.io.Serializable
import java.util.*



data class User(val userName : UserName,
                val catalog : Catalog) : Serializable
{

}



/**
 * User Name
 */
data class UserName(val value : String) : ToDocument, SQLSerializable, Serializable
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
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocText(this.value)


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value})

}


/**
 * User Id
 */
data class UserId(val value : UUID) : ToDocument, Serializable
{

    // Constructors
    // -----------------------------------------------------------------------------------------

    companion object : Factory<UserId>
    {
        override fun fromDocument(doc : SchemaDoc): ValueParser<UserId> = when (doc)
        {
            is DocText -> {
                try {
                    effValue<ValueError,UserId>(UserId(UUID.fromString(doc.text)))
                }
                catch (e : IllegalArgumentException) {
                    effError<ValueError,UserId>(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
                }
            }
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }


        fun random() = UserId(UUID.randomUUID())
    }


    // | To Document
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocText(this.value.toString())


}

