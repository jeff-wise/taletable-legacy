
package com.kispoko.tome.model.game


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Prim
import com.kispoko.tome.lib.functor.maybeLiftPrim
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.model.user.UserName
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueError
import lulo.value.ValueParser
import java.io.Serializable
import java.util.*



/**
 * Author
 */
data class Author(override val id : UUID,
                  val authorName : Prim<AuthorName>,
                  val organization : Maybe<Prim<AuthorOrganization>>,
                  val userName : Maybe<Prim<UserName>>) : Model
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.authorName.name                        = "author_name"

        when (this.organization) {
            is Just -> this.organization.value.name = "organization"
        }

        when (this.userName) {
            is Just -> this.userName.value.name     = "user_name"
        }
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(name : AuthorName,
                organization : Maybe<AuthorOrganization>,
                userName : Maybe<UserName>)
        : this(UUID.randomUUID(),
               Prim(name),
               maybeLiftPrim(organization),
               maybeLiftPrim(userName))


    companion object : Factory<Author>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<Author> = when (doc)
        {
            is DocDict ->
            {
                effApply(::Author,
                         // Name
                         doc.at("name") apply { AuthorName.fromDocument(it) },
                         // Organization
                         split(doc.maybeAt("outside_label"),
                                effValue<ValueError,Maybe<AuthorOrganization>>(Nothing()),
                                { effApply(::Just, AuthorOrganization.fromDocument(it)) }),
                         // User Name
                        split(doc.maybeAt("user_name"),
                                effValue<ValueError,Maybe<UserName>>(Nothing()),
                                { effApply(::Just, UserName.fromDocument(it)) })
                        )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name : String = "author"

    override val modelObject = this

}


/**
 * Author Name
 */
data class AuthorName(val value : String) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<AuthorName>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<AuthorName> = when (doc)
        {
            is DocText -> effValue(AuthorName(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value})

}


/**
 * Author Organization
 */
data class AuthorOrganization(val value : String) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<AuthorOrganization>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<AuthorOrganization> = when (doc)
        {
            is DocText -> effValue(AuthorOrganization(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }

    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value})


}
