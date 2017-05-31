
package com.kispoko.tome.model.game


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Func
import com.kispoko.tome.lib.functor.Null
import com.kispoko.tome.lib.functor.Prim
import com.kispoko.tome.lib.functor.nullEff
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.model.user.UserName
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import java.util.*



/**
 * Author
 */
data class Author(override val id : UUID,
                  val name : Func<AuthorName>,
                  val organization : Func<AuthorOrganization>,
                  val userName : Func<UserName>) : Model
{

    companion object : Factory<Author>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<Author> = when (doc)
        {
            is DocDict ->
            {
                effApply(::Author,
                         // Model Id
                         effValue(UUID.randomUUID()),
                         // Name
                         doc.at("name") apply {
                             effApply(::Prim, AuthorName.fromDocument(it))
                         },
                         // Organization
                         split(doc.maybeAt("organization"),
                               nullEff<AuthorOrganization>(),
                               fun(d : SpecDoc) : ValueParser<Func<AuthorOrganization>> =
                                   effApply(::Prim, AuthorOrganization.fromDocument(d))),
                         // User Name
                         split(doc.maybeAt("user_name"),
                               nullEff<UserName>(),
                               fun(d : SpecDoc) : ValueParser<Func<UserName>> =
                                   effApply(::Prim, UserName.fromDocument(d)))
                         )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }

    override fun onLoad() { }

}


/**
 * Author Name
 */
data class AuthorName(val value : String)
{

    companion object : Factory<AuthorName>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<AuthorName> = when (doc)
        {
            is DocText -> effValue(AuthorName(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }

}


/**
 * Author Organization
 */
data class AuthorOrganization(val value : String)
{

    companion object : Factory<AuthorOrganization>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<AuthorOrganization> = when (doc)
        {
            is DocText -> effValue(AuthorOrganization(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }

}
