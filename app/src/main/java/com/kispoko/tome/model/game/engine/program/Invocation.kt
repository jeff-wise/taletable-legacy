
package com.kispoko.tome.model.game.engine.program


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Prim
import com.kispoko.tome.lib.functor.Sum
import com.kispoko.tome.lib.functor.getMaybeSum
import com.kispoko.tome.lib.functor.maybeLiftSum
import com.kispoko.tome.lib.model.Model
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
                      val programId : Prim<ProgramId>,
                      val parameter1 : Sum<DataReference>,
                      val parameter2 : Maybe<Sum<DataReference>>,
                      val parameter3 : Maybe<Sum<DataReference>>,
                      val parameter4 : Maybe<Sum<DataReference>>,
                      val parameter5 : Maybe<Sum<DataReference>>) : Model, Serializable
{

    // -----------------------------------------------------------------------------------------
    // INIT
    // -----------------------------------------------------------------------------------------

    init
    {
        this.programId.name                         = "program_id"
        this.parameter1.name                        = "parameter1"

        when (this.parameter2) {
            is Just -> this.parameter2.value.name   = "parameter2"
        }

        when (this.parameter3) {
            is Just -> this.parameter3.value.name   = "parameter3"
        }

        when (this.parameter4) {
            is Just -> this.parameter4.value.name   = "parameter4"
        }

        when (this.parameter5) {
            is Just -> this.parameter5.value.name   = "parameter5"
        }
    }


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
               Prim(programId),
               Sum(parameter1),
               maybeLiftSum(parameter2),
               maybeLiftSum(parameter3),
               maybeLiftSum(parameter4),
               maybeLiftSum(parameter5))


    companion object : Factory<Invocation>
    {
        override fun fromDocument(doc : SpecDoc): ValueParser<Invocation> = when (doc)
        {
            is DocDict ->
            {
                effApply(::Invocation,
                         // Program Name
                         doc.at("program_id") ap { ProgramId.fromDocument(it) },
                         // Parameter 1
                         doc.at("parameter1") ap { DataReference.fromDocument(it) },
                         // Parameter 2
                         split(doc.maybeAt("parameter2"),
                               effValue<ValueError,Maybe<DataReference>>(Nothing()),
                               { effApply(::Just, DataReference.fromDocument(it)) }),
                         // Parameter 3
                         split(doc.maybeAt("parameter3"),
                               effValue<ValueError,Maybe<DataReference>>(Nothing()),
                               { effApply(::Just, DataReference.fromDocument(it)) }),
                         // Parameter 4
                         split(doc.maybeAt("parameter4"),
                               effValue<ValueError,Maybe<DataReference>>(Nothing()),
                               { effApply(::Just, DataReference.fromDocument(it)) }),
                         // Parameter 5
                         split(doc.maybeAt("parameter5"),
                               effValue<ValueError,Maybe<DataReference>>(Nothing()),
                               { effApply(::Just, DataReference.fromDocument(it)) })
                         )
            }
            else -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun programId() : ProgramId = this.programId.value

    fun parameter1() : DataReference = this.parameter1.value

    fun parameter2() : Maybe<DataReference> = getMaybeSum(this.parameter2)

    fun parameter3() : Maybe<DataReference> = getMaybeSum(this.parameter3)

    fun parameter4() : Maybe<DataReference> = getMaybeSum(this.parameter4)

    fun parameter5() : Maybe<DataReference> = getMaybeSum(this.parameter5)


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() {}

    override val name = "invocation"

    override val modelObject = this


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

