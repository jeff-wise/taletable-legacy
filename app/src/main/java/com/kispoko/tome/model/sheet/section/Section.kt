
package com.kispoko.tome.model.sheet.section


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Coll
import com.kispoko.tome.lib.functor.Prim
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.model.sheet.page.Page
import com.kispoko.tome.model.sheet.style.Icon
import com.kispoko.tome.rts.sheet.SheetContext
import com.kispoko.tome.rts.sheet.SheetUIContext
import effect.effApply
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
                   val sectionName : Prim<SectionName>,
                   val pages : Coll<Page>,
                   val icon : Prim<Icon>) : Model, ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.sectionName.name   = "section_name"
        this.pages.name         = "pages"
        this.icon.name          = "icon"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(name : SectionName,
                pages : MutableList<Page>,
                icon : Icon)
        : this(UUID.randomUUID(),
               Prim(name),
               Coll(pages),
               Prim(icon))


    companion object : Factory<Section>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<Section> = when (doc)
        {
            is DocDict ->
            {
                effApply(::Section,
                         // Section Name
                         doc.at("name") ap { SectionName.fromDocument(it) },
                         // Page List
                         doc.list("pages") ap { docList ->
                             docList.mapIndexed { doc, index ->
                                 Page.fromDocument(doc, index)
                             } },
                         // Icon
                         doc.at("icon") ap { Icon.fromDocument(it) }
                         )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun name() : SectionName = this.sectionName.value

    fun nameString() : String = this.sectionName.value.value

    fun pages() : List<Page> = this.pages.list

    fun icon() : Icon = this.icon.value


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "name" to this.name().toDocument(),
        "pages" to DocList(this.pages().map { it.toDocument() }),
        "icon" to this.icon().toDocument()
    ))

//        effApply(::Section,
//                         // Campaign Name
//                         doc.at("name") ap { SectionName.fromDocument(it) },
//                         // Page List
//                         doc.list("pages") ap { docList ->
//                             docList.mapIndexed { doc, index ->
//                                 Page.fromDocument(doc, index)
//                             } },
//                         // Icon
//                         doc.at("icon") ap { Icon.fromDocument(it) }
//                         )
//            }


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name : String = "section"

    override val modelObject = this


    // -----------------------------------------------------------------------------------------
    // SHEET COMPONENT
    // -----------------------------------------------------------------------------------------

    fun onActive(sheetUIContext : SheetUIContext)
    {
        this.pages.list.forEach { it.onSheetComponentActive(sheetUIContext) }
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

