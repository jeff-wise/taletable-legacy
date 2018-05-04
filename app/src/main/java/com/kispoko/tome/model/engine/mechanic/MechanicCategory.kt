
package com.kispoko.tome.model.engine.mechanic


import com.kispoko.tome.db.DB_MechanicCategoryValue
import com.kispoko.tome.db.mechanicCategoryTable
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.ProdType
import com.kispoko.tome.lib.orm.RowValue3
import com.kispoko.tome.lib.orm.schema.PrimValue
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import effect.apply
import effect.effError
import effect.effValue
import effect.split
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.UnknownCase
import lulo.value.ValueParser
import java.io.Serializable
import java.util.*



/**
 * Mechanic Category
 */
data class MechanicCategory(override val id : UUID,
                            val categoryId : MechanicCategoryId,
                            val label : MechanicCategoryLabel,
                            val tags : Set<MechanicCategoryTag>,
                            val description : MechanicCategoryDescription)
                             : ToDocument, ProdType, java.io.Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(categoryId : MechanicCategoryId,
                label : MechanicCategoryLabel,
                tags : Set<MechanicCategoryTag>,
                description : MechanicCategoryDescription)
        : this(UUID.randomUUID(),
               categoryId,
               label,
               tags,
               description)


    companion object : Factory<MechanicCategory>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<MechanicCategory> = when (doc)
        {
            is DocDict ->
            {
                apply(::MechanicCategory,
                      // Category Id
                      doc.at("id") ap { MechanicCategoryId.fromDocument(it) },
                      // Label
                      doc.at("label") ap { MechanicCategoryLabel.fromDocument(it) },
                      // Tags
                      split(doc.maybeList("tags"),
                            effValue(setOf()),
                            { it.mapSet { MechanicCategoryTag.fromDocument(it) } }),
                      // Description
                      doc.at("description") ap { MechanicCategoryDescription.fromDocument(it) }
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "id" to this.categoryId().toDocument(),
        "label" to this.label().toDocument(),
        "description" to this.label().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun categoryId() : MechanicCategoryId = this.categoryId


    fun label() : MechanicCategoryLabel  = this.label


    fun labelString() : String = this.label.value


    fun tags() : Set<MechanicCategoryTag> = this.tags


    fun description() : MechanicCategoryDescription = this.description


    fun descriptionString() : String = this.description.value


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_MechanicCategoryValue =
        RowValue3(mechanicCategoryTable,
                  PrimValue(this.categoryId),
                  PrimValue(this.label),
                  PrimValue(this.description))

}


/**
 * Mechanic Category Id
 */
sealed class MechanicCategoryReference : ToDocument, SQLSerializable, Serializable
{
    companion object : Factory<MechanicCategoryReference>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<MechanicCategoryReference> =
            when (doc.case())
            {
                "mechanic_category_id"  -> MechanicCategoryId.fromDocument(doc) as ValueParser<MechanicCategoryReference>
                "mechanic_category_tag" -> MechanicCategoryTag.fromDocument(doc) as ValueParser<MechanicCategoryReference>
                else                    -> effError(UnknownCase(doc.case(), doc.path))
            }
    }
}


/**
 * Mechanic Category Id
 */
data class MechanicCategoryId(val value : String) : MechanicCategoryReference()
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<MechanicCategoryId>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<MechanicCategoryId> = when (doc)
        {
            is DocText -> effValue(MechanicCategoryId(doc.text))
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
 * Mechanic Category Tag
 */
data class MechanicCategoryTag(val value : String) : MechanicCategoryReference()
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<MechanicCategoryTag>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<MechanicCategoryTag> = when (doc)
        {
            is DocText -> effValue(MechanicCategoryTag(doc.text))
            else       -> effError(lulo.value.UnexpectedType(DocType.TEXT, docType(doc), doc.path))
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
 * Mechanic Category Label
 */
data class MechanicCategoryLabel(val value : String) : ToDocument, SQLSerializable, java.io.Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<MechanicCategoryLabel>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<MechanicCategoryLabel> = when (doc)
        {
            is DocText -> effValue(MechanicCategoryLabel(doc.text))
            else       -> effError(lulo.value.UnexpectedType(DocType.TEXT, docType(doc), doc.path))
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
 * Mechanic Category Description
 */
data class MechanicCategoryDescription(val value : String)
                        : ToDocument, SQLSerializable, java.io.Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<MechanicCategoryDescription>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<MechanicCategoryDescription> = when (doc)
        {
            is DocText -> effValue(MechanicCategoryDescription(doc.text))
            else       -> effError(lulo.value.UnexpectedType(DocType.TEXT, docType(doc), doc.path))
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
