
package com.kispoko.tome.model.sheet.section


import com.kispoko.tome.db.DB_SectionValue
import com.kispoko.tome.db.sectionTable
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.ProdType
import com.kispoko.tome.lib.orm.RowValue3
import com.kispoko.tome.lib.orm.schema.CollValue
import com.kispoko.tome.lib.orm.schema.PrimValue
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.model.sheet.page.Page
import com.kispoko.tome.model.sheet.style.IconType
import com.kispoko.tome.rts.entity.sheet.SheetUIContext
import effect.apply
import effect.effError
import effect.effValue
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import java.io.Serializable
import java.util.*



/**
 * Section
 */
data class Section(override val id : UUID,
                   private var sectionName : SectionName,
                   private var pages : List<Page>,
                   private var icon : IconType)
                    : ProdType, ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(name : SectionName,
                pages : List<Page>,
                icon : IconType)
        : this(UUID.randomUUID(),
               name,
               pages,
               icon)


    companion object : Factory<Section>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<Section> = when (doc)
        {
            is DocDict ->
            {
                apply(::Section,
                      // Section Name
                      doc.at("name") ap { SectionName.fromDocument(it) },
                      // Page List
                      doc.list("pages") ap { docList ->
                          docList.mapIndexed { doc, index ->
                              Page.fromDocument(doc, index)
                          } },
                      // Icon
                      doc.at("icon") ap { IconType.fromDocument(it) }
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun name() : SectionName = this.sectionName

    fun nameString() : String = this.sectionName.value

    fun pages() : List<Page> = this.pages

    fun icon() : IconType = this.icon


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "name" to this.name().toDocument(),
        "pages" to DocList(this.pages().map { it.toDocument() }),
        "icon" to this.icon().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_SectionValue =
        RowValue3(sectionTable,
                  PrimValue(this.sectionName),
                  CollValue(this.pages),
                  PrimValue(this.icon))


    // -----------------------------------------------------------------------------------------
    // SHEET COMPONENT
    // -----------------------------------------------------------------------------------------

    fun onActive(sheetUIContext : SheetUIContext)
    {
        this.pages.forEach { it.onSheetComponentActive(sheetUIContext) }
    }

}


/**
 * Section Name
 */
data class SectionName(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<SectionName>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<SectionName> = when (doc)
        {
            is DocText -> effValue(SectionName(doc.text))
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

