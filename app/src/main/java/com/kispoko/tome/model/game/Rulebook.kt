
package com.kispoko.tome.model.game


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.*
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueError
import lulo.value.ValueParser
import java.io.Serializable
import java.util.*



// ---------------------------------------------------------------------------------------------
// RULEBOOK
// --------------------------------------------------------------------------------------------

/**
 * Rulebook
 */
data class Rulebook(override val id : UUID,
                    val title : Prim<RulebookTitle>,
                    val abstract : Prim<RulebookAbstract>,
                    val chapters : Coll<RulebookChapter>)
                     : Model, Serializable
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.title.name    = "title"
        this.abstract.name = "abstract"
        this.chapters.name = "chapters"
    }


    // -----------------------------------------------------------------------------------------
    // INDEXES
    // -----------------------------------------------------------------------------------------

    private val chapterById : MutableMap<RulebookChapterId,RulebookChapter> =
                                        chapters().associateBy { it.chapterId() }
                                                as MutableMap<RulebookChapterId,RulebookChapter>


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(title : RulebookTitle,
                abstract : RulebookAbstract,
                chapters : MutableList<RulebookChapter>)
        : this(UUID.randomUUID(),
               Prim(title),
               Prim(abstract),
               Coll(chapters))


    companion object : Factory<Rulebook>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<Rulebook> = when (doc)
        {
            is DocDict ->
            {
                effApply(::Rulebook,
                         // Title
                         doc.at("title") apply { RulebookTitle.fromDocument(it) },
                         // Abstract
                         doc.at("abstract") apply { RulebookAbstract.fromDocument(it) },
                         // Chapters
                         doc.list("chapters") apply {
                             it.mapMut { RulebookChapter.fromDocument(it) }
                         })
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun title() : RulebookTitle = this.title.value

    fun abstract() : RulebookAbstract = this.abstract.value

    fun chapters() : List<RulebookChapter> = this.chapters.value


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    fun subsection(rulebookReference : RulebookReference) : RulebookSubsection?
    {
        val chapter = this.chapterById[rulebookReference.chapterId()]

        val sectionId = rulebookReference.sectionId()
        if (chapter != null && sectionId != null)
        {
            val section = chapter.sectionWithId(sectionId)
            val subsectionId = rulebookReference.subsectionId()
            if (section != null && subsectionId != null)
            {
                val subsection = section.subsectionWithId(subsectionId)
                if (subsection != null)
                    return subsection
            }
        }

        return null
    }


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name : String = "game"

    override val modelObject = this

}


/**
 * Rulebook Title
 */
data class RulebookTitle(val value : String) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<RulebookTitle>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<RulebookTitle> = when (doc)
        {
            is DocText -> effValue(RulebookTitle(doc.text))
            else       -> effError(lulo.value.UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({ this.value })

}


/**
 * Rulebook Abstract
 */
data class RulebookAbstract(val value : String) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<RulebookAbstract>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<RulebookAbstract> = when (doc)
        {
            is DocText -> effValue(RulebookAbstract(doc.text))
            else       -> effError(lulo.value.UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({ this.value })

}


// ---------------------------------------------------------------------------------------------
// SECTION
// --------------------------------------------------------------------------------------------

/**
 * Rulebook Chapter
 */
data class RulebookChapter(override val id : UUID,
                           val chapterId : Prim<RulebookChapterId>,
                           val title : Prim<RulebookChapterTitle>,
                           val sections : Coll<RulebookSection>)
                            : Model, Serializable
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.chapterId.name = "chapter_id"
        this.title.name     = "title"
        this.sections.name  = "sections"
    }


    // -----------------------------------------------------------------------------------------
    // INDEXES
    // -----------------------------------------------------------------------------------------

    private val sectionById : MutableMap<RulebookSectionId,RulebookSection> =
                                        sections().associateBy { it.sectionId() }
                                                as MutableMap<RulebookSectionId,RulebookSection>


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(chapterId : RulebookChapterId,
                title : RulebookChapterTitle,
                sections : MutableList<RulebookSection>)
        : this(UUID.randomUUID(),
               Prim(chapterId),
               Prim(title),
               Coll(sections))


    companion object : Factory<RulebookChapter>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<RulebookChapter> = when (doc)
        {
            is DocDict ->
            {
                effApply(::RulebookChapter,
                         // Chapter Id
                         doc.at("id") apply { RulebookChapterId.fromDocument(it) },
                         // Title
                         doc.at("title") apply { RulebookChapterTitle.fromDocument(it) },
                         // Sections
                         doc.list("sections") apply {
                             it.mapMut { RulebookSection.fromDocument(it) }
                         })
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun chapterId() : RulebookChapterId = this.chapterId.value

    fun title() : RulebookChapterTitle = this.title.value

    fun sections() : List<RulebookSection> = this.sections.value


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    fun sectionWithId(sectionId : RulebookSectionId) : RulebookSection? =
        this.sectionById[sectionId]


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name : String = "rulebook_section"

    override val modelObject = this

}


/**
 * Rulebook Chapter Id
 */
data class RulebookChapterId(val value : String) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<RulebookChapterId>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<RulebookChapterId> = when (doc)
        {
            is DocText -> effValue(RulebookChapterId(doc.text))
            else       -> effError(lulo.value.UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({ this.value })

}


/**
 * Rulebook Chapter Title
 */
data class RulebookChapterTitle(val value : String) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<RulebookChapterTitle>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<RulebookChapterTitle> = when (doc)
        {
            is DocText -> effValue(RulebookChapterTitle(doc.text))
            else       -> effError(lulo.value.UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({ this.value })

}


// ---------------------------------------------------------------------------------------------
// SECTION
// --------------------------------------------------------------------------------------------

/**
 * Rulebook Section
 */
data class RulebookSection(override val id : UUID,
                           val sectionId : Prim<RulebookSectionId>,
                           val title : Prim<RulebookSectionTitle>,
                           val body : Prim<RulebookSectionBody>,
                           val subsections : Coll<RulebookSubsection>) : Model, Serializable
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.sectionId.name    = "section_id"
        this.title.name        = "title"
        this.body.name         = "body"
        this.subsections.name  = "subsections"
    }


    // -----------------------------------------------------------------------------------------
    // INDEXES
    // -----------------------------------------------------------------------------------------

    private val subsectionById : MutableMap<RulebookSubsectionId,RulebookSubsection> =
                                    subsections().associateBy { it.subsectionId() }
                                            as MutableMap<RulebookSubsectionId,RulebookSubsection>


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(sectionId : RulebookSectionId,
                title : RulebookSectionTitle,
                body : RulebookSectionBody,
                subsections : MutableList<RulebookSubsection>)
        : this(UUID.randomUUID(),
               Prim(sectionId),
               Prim(title),
               Prim(body),
               Coll(subsections))


    companion object : Factory<RulebookSection>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<RulebookSection> = when (doc)
        {
            is DocDict ->
            {
                effApply(::RulebookSection,
                         // Section Id
                         doc.at("id") apply { RulebookSectionId.fromDocument(it) },
                         // Title
                         doc.at("title") apply { RulebookSectionTitle.fromDocument(it) },
                         // Body
                         doc.at("body") apply { RulebookSectionBody.fromDocument(it) },
                         // Subsections
                         doc.list("subsections") apply {
                             it.mapMut { RulebookSubsection.fromDocument(it) }
                         })
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun sectionId() : RulebookSectionId = this.sectionId.value

    fun title() : RulebookSectionTitle = this.title.value

    fun body() : RulebookSectionBody = this.body.value

    fun subsections() : List<RulebookSubsection> = this.subsections.value


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    fun subsectionWithId(subsectionId : RulebookSubsectionId) : RulebookSubsection? =
        this.subsectionById[subsectionId]


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name : String = "rulebook_section"

    override val modelObject = this

}


/**
 * Rulebook Section Id
 */
data class RulebookSectionId(val value : String) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<RulebookSectionId>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<RulebookSectionId> = when (doc)
        {
            is DocText -> effValue(RulebookSectionId(doc.text))
            else       -> effError(lulo.value.UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({ this.value })

}


/**
 * Rulebook Section Title
 */
data class RulebookSectionTitle(val value : String) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<RulebookSectionTitle>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<RulebookSectionTitle> = when (doc)
        {
            is DocText -> effValue(RulebookSectionTitle(doc.text))
            else       -> effError(lulo.value.UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({ this.value })

}


/**
 * Rulebook Section Body
 */
data class RulebookSectionBody(val value : String) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<RulebookSectionBody>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<RulebookSectionBody> = when (doc)
        {
            is DocText -> effValue(RulebookSectionBody(doc.text))
            else       -> effError(lulo.value.UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({ this.value })

}


// ---------------------------------------------------------------------------------------------
// SUBSECTION
// --------------------------------------------------------------------------------------------

/**
 * Rulebook Subection
 */
data class RulebookSubsection(override val id : UUID,
                              val subsectionId : Prim<RulebookSubsectionId>,
                              val title : Prim<RulebookSubsectionTitle>,
                              val body : Prim<RulebookSubsectionBody>)
                                : Model, Serializable
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.subsectionId.name = "subsection_id"
        this.title.name        = "title"
        this.body.name         = "body"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(subsectionId : RulebookSubsectionId,
                title : RulebookSubsectionTitle,
                body : RulebookSubsectionBody)
        : this(UUID.randomUUID(),
               Prim(subsectionId),
               Prim(title),
               Prim(body))


    companion object : Factory<RulebookSubsection>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<RulebookSubsection> = when (doc)
        {
            is DocDict ->
            {
                effApply(::RulebookSubsection,
                         // Id
                         doc.at("id") apply { RulebookSubsectionId.fromDocument(it) },
                         // Title
                         doc.at("title") apply { RulebookSubsectionTitle.fromDocument(it) },
                         // Body
                         doc.at("body") apply { RulebookSubsectionBody.fromDocument(it) })
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun subsectionId() : RulebookSubsectionId = this.subsectionId.value

    fun title() : String = this.title.value.value

    fun body() : String = this.body.value.value


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name : String = "rulebook_subsection"

    override val modelObject = this

}


/**
 * Rulebook Subsection Id
 */
data class RulebookSubsectionId(val value : String) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<RulebookSubsectionId>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<RulebookSubsectionId> = when (doc)
        {
            is DocText -> effValue(RulebookSubsectionId(doc.text))
            else       -> effError(lulo.value.UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({ this.value })

}


/**
 * Rulebook Subsection Title
 */
data class RulebookSubsectionTitle(val value : String) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<RulebookSubsectionTitle>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<RulebookSubsectionTitle> = when (doc)
        {
            is DocText -> effValue(RulebookSubsectionTitle(doc.text))
            else       -> effError(lulo.value.UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({ this.value })

}


/**
 * Rulebook Subsection Body
 */
data class RulebookSubsectionBody(val value : String) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<RulebookSubsectionBody>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<RulebookSubsectionBody> = when (doc)
        {
            is DocText -> effValue(RulebookSubsectionBody(doc.text))
            else       -> effError(lulo.value.UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({ this.value })

}


// ---------------------------------------------------------------------------------------------
// RULEBOOK REFERENCE
// --------------------------------------------------------------------------------------------

/**
 * Rulebook Reference
 */
data class RulebookReference(override val id : UUID,
                             val chapterId : Prim<RulebookChapterId>,
                             val sectionId : Maybe<Prim<RulebookSectionId>>,
                             val subsectionId : Maybe<Prim<RulebookSubsectionId>>)
                              : Model, Serializable
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.chapterId.name    = "section_id"

        when (this.sectionId) {
            is Just -> this.sectionId.value.name = "section_id"
        }

        when (this.subsectionId) {
            is Just -> this.subsectionId.value.name = "subsection_id"
        }
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(chapterId : RulebookChapterId,
                sectionId : Maybe<RulebookSectionId>,
                subsectionId : Maybe<RulebookSubsectionId>)
        : this(UUID.randomUUID(),
               Prim(chapterId),
               maybeLiftPrim(sectionId),
               maybeLiftPrim(subsectionId))


    companion object : Factory<RulebookReference>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<RulebookReference> = when (doc)
        {
            is DocDict ->
            {
                effApply(::RulebookReference,
                         // Chapter Id
                         doc.at("chapter_id") apply { RulebookChapterId.fromDocument(it) },
                         // Section Id
                         split(doc.maybeAt("section_id"),
                               effValue<ValueError,Maybe<RulebookSectionId>>(Nothing()),
                               { effApply(::Just, RulebookSectionId.fromDocument(it)) }),
                         // Subsection Id
                         split(doc.maybeAt("subsection_id"),
                               effValue<ValueError,Maybe<RulebookSubsectionId>>(Nothing()),
                               { effApply(::Just, RulebookSubsectionId.fromDocument(it)) })
                        )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun chapterId() : RulebookChapterId = this.chapterId.value

    fun sectionId() : RulebookSectionId? = getMaybePrim(this.sectionId)

    fun subsectionId() : RulebookSubsectionId? = getMaybePrim(this.subsectionId)


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name : String = "rulebook_reference"

    override val modelObject = this

}

