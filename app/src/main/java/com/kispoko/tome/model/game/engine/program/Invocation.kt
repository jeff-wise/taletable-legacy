
package com.kispoko.tome.model.game.engine.program


import com.kispoko.tome.db.DB_Invocation
import com.kispoko.tome.db.dbInvocation
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.model.ProdType
import com.kispoko.tome.model.game.engine.reference.DataReference
import com.kispoko.tome.model.game.engine.variable.VariableReference
import effect.*
import lulo.document.*
import lulo.value.*
import lulo.value.UnexpectedType
import java.io.Serializable
import java.util.*



/**
 * Program Invocation
 */
data class Invocation(override val id : UUID,
                      val programId : ProgramId,
                      val parameter1 : DataReference,
                      val parameter2 : Maybe<DataReference>,
                      val parameter3 : Maybe<DataReference>,
                      val parameter4 : Maybe<DataReference>,
                      val parameter5 : Maybe<DataReference>)
                       : ToDocument, ProdType, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(programId : ProgramId,
                parameter1 : DataReference,
                parameter2 : Maybe<DataReference>,
                parameter3 : Maybe<DataReference>,
                parameter4 : Maybe<DataReference>,
                parameter5 : Maybe<DataReference>)
        : this(UUID.randomUUID(),
               programId,
               parameter1,
               parameter2,
               parameter3,
               parameter4,
               parameter5)


    companion object : Factory<Invocation>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<Invocation> = when (doc)
        {
            is DocDict ->
            {
                apply(::Invocation,
                      // Program Name
                      doc.at("program_id") ap { ProgramId.fromDocument(it) },
                      // Parameter 1
                      doc.at("parameter1") ap { DataReference.fromDocument(it) },
                      // Parameter 2
                      split(doc.maybeAt("parameter2"),
                            effValue<ValueError,Maybe<DataReference>>(Nothing()),
                            { apply(::Just, DataReference.fromDocument(it)) }),
                      // Parameter 3
                      split(doc.maybeAt("parameter3"),
                            effValue<ValueError,Maybe<DataReference>>(Nothing()),
                            { apply(::Just, DataReference.fromDocument(it)) }),
                      // Parameter 4
                      split(doc.maybeAt("parameter4"),
                            effValue<ValueError,Maybe<DataReference>>(Nothing()),
                            { apply(::Just, DataReference.fromDocument(it)) }),
                      // Parameter 5
                      split(doc.maybeAt("parameter5"),
                            effValue<ValueError,Maybe<DataReference>>(Nothing()),
                            { apply(::Just, DataReference.fromDocument(it)) })
                      )
            }
            else -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "program_id" to this.programId().toDocument(),
        "parameter1" to this.parameter1().toDocument()
    ))
    .maybeMerge(this.parameter2().apply {
        Just(Pair("parameter2", it.toDocument())) })
    .maybeMerge(this.parameter3().apply {
        Just(Pair("parameter3", it.toDocument())) })
    .maybeMerge(this.parameter4().apply {
        Just(Pair("parameter4", it.toDocument())) })
    .maybeMerge(this.parameter5().apply {
        Just(Pair("parameter5", it.toDocument())) })


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun programId() : ProgramId = this.programId


    fun parameter1() : DataReference = this.parameter1


    fun parameter2() : Maybe<DataReference> = this.parameter2


    fun parameter3() : Maybe<DataReference> = this.parameter3


    fun parameter4() : Maybe<DataReference> = this.parameter4


    fun parameter5() : Maybe<DataReference> = this.parameter5


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() {}


    override val prodTypeObject = this


    override fun row() : DB_Invocation = dbInvocation(this.programId,
                                                      this.parameter1,
                                                      this.parameter2,
                                                      this.parameter3,
                                                      this.parameter4,
                                                      this.parameter5)


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    /**
     * The set of variables that the program depends on.
     */
    fun dependencies() : Set<VariableReference>
    {
        // TODO use maybe monad?

        val deps = mutableSetOf<VariableReference>()

        deps.addAll(this.parameter1().dependencies())

        val param2 = this.parameter2()
        when (param2) {
            is Just -> deps.addAll(param2.value.dependencies())
        }

        val param3 = this.parameter3()
        when (param3) {
            is Just -> deps.addAll(param3.value.dependencies())
        }

        val param4 = this.parameter4()
        when (param4) {
            is Just -> deps.addAll(param4.value.dependencies())
        }

        val param5 = this.parameter5()
        when (param5) {
            is Just -> deps.addAll(param5.value.dependencies())
        }

        return deps
    }

}

