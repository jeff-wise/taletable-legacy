
package com.kispoko.tome.model.game


import com.kispoko.tome.db.*
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.ProdType
import com.kispoko.tome.lib.orm.RowValue3
import com.kispoko.tome.lib.orm.RowValue4
import com.kispoko.tome.lib.orm.RowValue5
import com.kispoko.tome.lib.orm.schema.CollValue
import com.kispoko.tome.lib.orm.schema.MaybePrimValue
import com.kispoko.tome.lib.orm.schema.PrimValue
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueError
import lulo.value.ValueParser
import maybe.Just
import maybe.Maybe
import maybe.Nothing
import java.io.Serializable
import java.util.*



// ---------------------------------------------------------------------------------------------
// RULEBOOK
// --------------------------------------------------------------------------------------------

/**
 * Rulebook
 */
data class Rulebook(override val id : UUID,
                    val rulebookId : RulebookId,
                    val title : RulebookTitle,
                    val authors : List<Author>,
                    val abstract : RulebookAbstract,
                    val introduction : RulebookIntroduction,
                    val chapters : MutableList<RulebookChapter>)
                     : ToDocument, ProdType, Serializable
{

    // -----------------------------------------------------------------------------------------
    // INDEXES
    // -----------------------------------------------------------------------------------------

    private val chapterById : MutableMap<RulebookChapterId,RulebookChapter> =
                                        chapters().associateBy { it.chapterId() }
                                                as MutableMap<RulebookChapterId,RulebookChapter>


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(rulebookId : RulebookId,
                title : RulebookTitle,
                authors : List<Author>,
                abstract : RulebookAbstract,
                introduction : RulebookIntroduction,
                chapters : List<RulebookChapter>)
        : this(UUID.randomUUID(),
               rulebookId,
               title,
               authors,
               abstract,
               introduction,
               chapters.toMutableList())


    companion object : Factory<Rulebook>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<Rulebook> = when (doc)
        {
            is DocDict ->
            {
                apply(::Rulebook,
                      // Rulebook Id
                      doc.at("rulebook_id") apply { RulebookId.fromDocument(it) },
                      // Title
                      doc.at("title") apply { RulebookTitle.fromDocument(it) },
                      // Title
                      doc.list("authors") apply { it.map { Author.fromDocument(it) } },
                      // Abstract
                      doc.at("abstract") apply { RulebookAbstract.fromDocument(it) },
                      // Introduction
                      doc.at("introduction") apply { RulebookIntroduction.fromDocument(it) },
                      // Chapters
                      doc.list("chapters") apply {
                          it.mapMut { RulebookChapter.fromDocument(it) }
                      })
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "rulebook_id" to this.rulebookId().toDocument(),
        "title" to this.title().toDocument(),
        "abstract" to this.abstract().toDocument(),
        "introduction" to this.introduction().toDocument(),
        "chapters" to DocList(this.chapters().map { it.toDocument() })
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun rulebookId() : RulebookId = this.rulebookId


    fun title() : RulebookTitle = this.title


    fun authors() : List<Author> = this.authors


    fun abstract() : RulebookAbstract = this.abstract


    fun introduction() : RulebookIntroduction = this.introduction


    fun chapters() : List<RulebookChapter> = this.chapters


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    fun subsection(rulebookReference : RulebookReference) : RulebookSubsection?
    {
        val chapter = this.chapterById[rulebookReference.chapterId()]

        // TODO use as maybe refactoring example
        if (chapter != null)
        {
            val sectionId = rulebookReference.sectionId()
            when (sectionId)
            {
                is Just ->
                {
                    val section = chapter.sectionWithId(sectionId.value)
                    if (section != null)
                    {
                        val subsectionId = rulebookReference.subsectionId()
                        when (subsectionId)
                        {
                            is Just ->
                            {
                                val subsection = section.subsectionWithId(subsectionId.value)
                                if (subsection != null)
                                    return subsection
                            }
                        }
                    }
                }
            }
        }

        return null
    }


    fun excerpt(rulebookReference : RulebookReference) : RulebookExcerpt?
    {
        val chapter = this.chapterById[rulebookReference.chapterId()]

        // TODO use as maybe refactoring example
        if (chapter != null)
        {
            val sectionId = rulebookReference.sectionId()
            when (sectionId)
            {
                is Just ->
                {
                    val section = chapter.sectionWithId(sectionId.value)
                    if (section != null)
                    {
                        val subsectionId = rulebookReference.subsectionId()
                        when (subsectionId)
                        {
                            is Just ->
                            {
                                val subsection = section.subsectionWithId(subsectionId.value)
                                if (subsection != null)
                                    return RulebookExcerpt(subsection.titleString(), subsection.body().value)
                            }
                            is Nothing -> return RulebookExcerpt(section.title().value, section.body().value)
                        }
                    }
                }
            }
        }

        return null
    }


    fun referencePath(rulebookReference : RulebookReference) : RulebookReferencePath?
    {
        val chapter = this.chapterById[rulebookReference.chapterId()]

        if (chapter != null)
        {
            val sectionId = rulebookReference.sectionId()
            when (sectionId)
            {
                is Just ->
                {
                    val section = chapter.sectionWithId(sectionId.value)
                    if (section != null)
                    {
                        val subsectionId = rulebookReference.subsectionId()
                        when (subsectionId)
                        {
                            is Just ->
                            {
                                val subsection = section.subsectionWithId(subsectionId.value)
                                if (subsection != null)
                                    return RulebookReferencePath(this.title(),
                                                                 chapter.title(),
                                                                 Just(section.title()),
                                                                Just(subsection.title()))
                            }
                            else ->{
                                return RulebookReferencePath(this.title(),
                                                             chapter.title(),
                                                             Just(section.title()),
                                                             Nothing())
                            }
                        }
                    }
                }
                else -> {
                    return RulebookReferencePath(this.title(), chapter.title(), Nothing(), Nothing())
                }
            }
        }

        return null
    }


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_RulebookValue =
        RowValue5(rulebookTable,
                  PrimValue(this.title),
                  CollValue(this.authors),
                  PrimValue(this.abstract),
                  PrimValue(this.introduction),
                  CollValue(this.chapters))

}


/**
 * Rulebook Id
 */
data class RulebookId(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<RulebookId>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<RulebookId> = when (doc)
        {
            is DocText -> effValue(RulebookId(doc.text))
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

    override fun asSQLValue() : SQLValue = SQLText({ this.value })

}


/**
 * Rulebook Title
 */
data class RulebookTitle(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<RulebookTitle>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<RulebookTitle> = when (doc)
        {
            is DocText -> effValue(RulebookTitle(doc.text))
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

    override fun asSQLValue() : SQLValue = SQLText({ this.value })

}


/**
 * Rulebook Abstract
 */
data class RulebookAbstract(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<RulebookAbstract>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<RulebookAbstract> = when (doc)
        {
            is DocText -> effValue(RulebookAbstract(doc.text))
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

    override fun asSQLValue() : SQLValue = SQLText({ this.value })

}


/**
 * Rulebook Introduction
 */
data class RulebookIntroduction(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<RulebookIntroduction>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<RulebookIntroduction> = when (doc)
        {
            is DocText -> effValue(RulebookIntroduction(doc.text))
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

    override fun asSQLValue() : SQLValue = SQLText({ this.value })

}


// ---------------------------------------------------------------------------------------------
// SECTION
// --------------------------------------------------------------------------------------------

/**
 * Rulebook Chapter
 */
data class RulebookChapter(override val id : UUID,
                           val chapterId : RulebookChapterId,
                           val title : RulebookChapterTitle,
                           val sections : MutableList<RulebookSection>)
                            : ToDocument, ProdType, Serializable
{

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
                sections : List<RulebookSection>)
        : this(UUID.randomUUID(),
               chapterId,
               title,
               sections.toMutableList())


    companion object : Factory<RulebookChapter>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<RulebookChapter> = when (doc)
        {
            is DocDict ->
            {
                apply(::RulebookChapter,
                      // Chapter Id
                      doc.at("id") apply { RulebookChapterId.fromDocument(it) },
                      // Title
                      doc.at("title") apply { RulebookChapterTitle.fromDocument(it) },
                      // Sections
                      doc.list("sections") apply {
                          it.map { RulebookSection.fromDocument(it) }
                      })
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "id" to this.chapterId().toDocument(),
        "title" to this.title().toDocument(),
        "sections" to DocList(this.sections().map { it.toDocument() } )
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun chapterId() : RulebookChapterId = this.chapterId


    fun title() : RulebookChapterTitle = this.title


    fun sections() : List<RulebookSection> = this.sections


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    fun sectionWithId(sectionId : RulebookSectionId) : RulebookSection? =
        this.sectionById[sectionId]


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_RulebookChapterValue =
        RowValue3(rulebookChapterTable, PrimValue(this.chapterId),
                                        PrimValue(this.title),
                                        CollValue(this.sections))

}


/**
 * Rulebook Chapter Id
 */
data class RulebookChapterId(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<RulebookChapterId>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<RulebookChapterId> = when (doc)
        {
            is DocText -> effValue(RulebookChapterId(doc.text))
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

    override fun asSQLValue() : SQLValue = SQLText({ this.value })

}


/**
 * Rulebook Chapter Title
 */
data class RulebookChapterTitle(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<RulebookChapterTitle>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<RulebookChapterTitle> = when (doc)
        {
            is DocText -> effValue(RulebookChapterTitle(doc.text))
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

    override fun asSQLValue() : SQLValue = SQLText({ this.value })

}


// ---------------------------------------------------------------------------------------------
// SECTION
// --------------------------------------------------------------------------------------------

/**
 * Rulebook Section
 */
data class RulebookSection(override val id : UUID,
                           val sectionId : RulebookSectionId,
                           val title : RulebookSectionTitle,
                           val body : RulebookSectionBody,
                           val subsections : MutableList<RulebookSubsection>)
                            : ToDocument, ProdType, Serializable
{

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
                subsections : List<RulebookSubsection>)
        : this(UUID.randomUUID(),
               sectionId,
               title,
               body,
               subsections.toMutableList())


    companion object : Factory<RulebookSection>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<RulebookSection> = when (doc)
        {
            is DocDict ->
            {
                apply(::RulebookSection,
                      // Section Id
                      doc.at("id") apply { RulebookSectionId.fromDocument(it) },
                      // Title
                      doc.at("title") apply { RulebookSectionTitle.fromDocument(it) },
                      // Body
                      doc.at("body") apply { RulebookSectionBody.fromDocument(it) },
                      // Subsections
                      split(doc.maybeList("subsections"),
                            effValue(listOf()),
                            { it.map { RulebookSubsection.fromDocument(it) } })
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "id" to this.sectionId().toDocument(),
        "title" to this.title().toDocument(),
        "body" to this.body().toDocument(),
        "subsections" to DocList(this.subsections().map { it.toDocument() })
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun sectionId() : RulebookSectionId = this.sectionId


    fun title() : RulebookSectionTitle = this.title


    fun body() : RulebookSectionBody = this.body


    fun subsections() : List<RulebookSubsection> = this.subsections


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    fun subsectionWithId(subsectionId : RulebookSubsectionId) : RulebookSubsection? =
        this.subsectionById[subsectionId]


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_RulebookSectionValue =
        RowValue4(rulebookSectionTable, PrimValue(this.sectionId),
                                        PrimValue(this.title),
                                        PrimValue(this.body),
                                        CollValue(this.subsections))

}


/**
 * Rulebook Section Id
 */
data class RulebookSectionId(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<RulebookSectionId>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<RulebookSectionId> = when (doc)
        {
            is DocText -> effValue(RulebookSectionId(doc.text))
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

    override fun asSQLValue() : SQLValue = SQLText({ this.value })

}


/**
 * Rulebook Section Title
 */
data class RulebookSectionTitle(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<RulebookSectionTitle>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<RulebookSectionTitle> = when (doc)
        {
            is DocText -> effValue(RulebookSectionTitle(doc.text))
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

    override fun asSQLValue() : SQLValue = SQLText({ this.value })

}


/**
 * Rulebook Section Body
 */
data class RulebookSectionBody(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<RulebookSectionBody>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<RulebookSectionBody> = when (doc)
        {
            is DocText -> effValue(RulebookSectionBody(doc.text))
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

    override fun asSQLValue() : SQLValue = SQLText({ this.value })

}


// ---------------------------------------------------------------------------------------------
// SUBSECTION
// --------------------------------------------------------------------------------------------

/**
 * Rulebook Subection
 */
data class RulebookSubsection(override val id : UUID,
                              val subsectionId : RulebookSubsectionId,
                              val title : RulebookSubsectionTitle,
                              val body : RulebookSubsectionBody)
                                : ToDocument, ProdType, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(subsectionId : RulebookSubsectionId,
                title : RulebookSubsectionTitle,
                body : RulebookSubsectionBody)
        : this(UUID.randomUUID(),
               subsectionId,
               title,
               body)


    companion object : Factory<RulebookSubsection>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<RulebookSubsection> = when (doc)
        {
            is DocDict ->
            {
                apply(::RulebookSubsection,
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
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "id" to this.subsectionId().toDocument(),
        "title" to this.title().toDocument(),
        "body" to this.body().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun subsectionId() : RulebookSubsectionId = this.subsectionId


    fun title() : RulebookSubsectionTitle = this.title


    fun titleString() : String = this.title.value


    fun body() : RulebookSubsectionBody = this.body


    fun bodyString() : String = this.body.value


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_RulebookSubsectionValue =
        RowValue3(rulebookSubsectionTable, PrimValue(this.subsectionId),
                                           PrimValue(this.title),
                                           PrimValue(this.body))

}


/**
 * Rulebook Subsection Id
 */
data class RulebookSubsectionId(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<RulebookSubsectionId>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<RulebookSubsectionId> = when (doc)
        {
            is DocText -> effValue(RulebookSubsectionId(doc.text))
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

    override fun asSQLValue() : SQLValue = SQLText({ this.value })

}


/**
 * Rulebook Subsection Title
 */
data class RulebookSubsectionTitle(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<RulebookSubsectionTitle>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<RulebookSubsectionTitle> = when (doc)
        {
            is DocText -> effValue(RulebookSubsectionTitle(doc.text))
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

    override fun asSQLValue() : SQLValue = SQLText({ this.value })

}


/**
 * Rulebook Subsection Body
 */
data class RulebookSubsectionBody(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<RulebookSubsectionBody>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<RulebookSubsectionBody> = when (doc)
        {
            is DocText -> effValue(RulebookSubsectionBody(doc.text))
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

    override fun asSQLValue() : SQLValue = SQLText({ this.value })

}


// ---------------------------------------------------------------------------------------------
// RULEBOOK REFERENCE
// --------------------------------------------------------------------------------------------

/**
 * Rulebook Reference
 */
data class RulebookReference(override val id : UUID,
                             val rulebookId : RulebookId,
                             val chapterId : RulebookChapterId,
                             val sectionId : Maybe<RulebookSectionId>,
                             val subsectionId : Maybe<RulebookSubsectionId>)
                              : ToDocument, ProdType, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(rulebookId : RulebookId,
                chapterId : RulebookChapterId,
                sectionId : Maybe<RulebookSectionId>,
                subsectionId : Maybe<RulebookSubsectionId>)
        : this(UUID.randomUUID(),
               rulebookId,
               chapterId,
               sectionId,
               subsectionId)


    companion object : Factory<RulebookReference>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<RulebookReference> = when (doc)
        {
            is DocDict ->
            {
                apply(::RulebookReference,
                      // Rulebook Id
                      doc.at("rulebook_id") apply { RulebookId.fromDocument(it) },
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
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "rulebook_id" to this.rulebookId().toDocument(),
        "chapter_id" to this.chapterId().toDocument()
    ))
    .maybeMerge(this.sectionId.apply {
        Just(Pair("section_id", it.toDocument() as SchemaDoc)) })
    .maybeMerge(this.subsectionId.apply {
        Just(Pair("subsection_id", it.toDocument() as SchemaDoc)) })


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun rulebookId() : RulebookId = this.rulebookId


    fun chapterId() : RulebookChapterId = this.chapterId


    fun sectionId() : Maybe<RulebookSectionId> = this.sectionId


    fun subsectionId() : Maybe<RulebookSubsectionId> = this.subsectionId


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_RulebookReferenceValue =
        RowValue4(rulebookReferenceTable,
                  PrimValue(this.rulebookId),
                  PrimValue(this.chapterId),
                  MaybePrimValue(this.sectionId),
                  MaybePrimValue(this.subsectionId))


}


/**
 * Rulebook Reference Path
 */
data class RulebookReferencePath(val bookTitle : RulebookTitle,
                                 val chapterTitle : RulebookChapterTitle,
                                 val sectionTitle : Maybe<RulebookSectionTitle>,
                                 val subsectionTitle : Maybe<RulebookSubsectionTitle>)
                                  : Serializable
{

    override fun toString() : String
    {
        var s = ""

        s += bookTitle.value
        s += " \u203A "
        s += chapterTitle.value

        when (this.sectionTitle) {
            is Just -> {
                s += " \u203A "
                s += this.sectionTitle.value.value
            }
        }

        when (this.subsectionTitle) {
            is Just -> {
                s += " \u203A "
                s += this.subsectionTitle.value.value
            }
        }

        return s
    }

}

data class RulebookExcerpt(val title : String, val body : String) : Serializable

