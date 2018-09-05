
package com.taletable.android.model.sheet.section


import android.content.Context
import com.taletable.android.lib.Factory
import com.taletable.android.lib.orm.ProdType
import com.taletable.android.lib.orm.RowValue3
import com.taletable.android.lib.orm.schema.CollValue
import com.taletable.android.lib.orm.schema.PrimValue
import com.taletable.android.lib.orm.sql.SQLSerializable
import com.taletable.android.lib.orm.sql.SQLText
import com.taletable.android.lib.orm.sql.SQLValue
import com.taletable.android.model.sheet.page.Page
import com.taletable.android.model.sheet.style.IconType
import com.taletable.android.rts.entity.EntityId
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
data class Section(private var sectionName : SectionName,
                   private var pages : List<Page>,
                   private var icon : IconType)
                    : ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

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
    // SHEET COMPONENT
    // -----------------------------------------------------------------------------------------

    fun onActive(entityId : EntityId, context : Context)
    {
        this.pages.forEach {
            it.onSheetComponentActive(entityId, context)
        }
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

