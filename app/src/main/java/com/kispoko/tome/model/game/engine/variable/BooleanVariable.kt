
package com.kispoko.tome.model.game.engine.variable


import com.kispoko.tome.app.AppEff
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Comp
import com.kispoko.tome.lib.functor.Func
import com.kispoko.tome.lib.functor.Prim
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.lib.orm.sql.SQLInt
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.model.game.engine.program.Invocation
import effect.effApply
import effect.effError
import effect.effValue
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.UnknownCase
import lulo.value.ValueError
import lulo.value.ValueParser
import java.io.Serializable
import java.util.*



/**
 * Boolean Variable
 */
sealed class BooleanVariableValue : Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<BooleanVariableValue>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<BooleanVariableValue> =
            when (doc.case)
            {
                "boolean_literal"    -> BooleanVariableLiteralValue.fromDocument(doc)
                "program_invocation" -> BooleanVariableProgramValue.fromDocument(doc)
                else                 -> effError<ValueError,BooleanVariableValue>(
                                            UnknownCase(doc.case, doc.path))
            }
    }


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    open fun dependencies() : Set<VariableReference> = setOf()

    abstract fun value() : AppEff<Boolean>

}


/**
 * Literal Value
 */
data class BooleanVariableLiteralValue(val value : Boolean)
                : BooleanVariableValue(), SQLSerializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<BooleanVariableValue>
    {
        override fun fromDocument(doc : SpecDoc)
                      : ValueParser<BooleanVariableValue> = when (doc)
        {
            is DocBoolean -> effValue(BooleanVariableLiteralValue(doc.boolean))
            else          -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    override fun value() : AppEff<Boolean> = effValue(this.value)


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLInt({ if (this.value) 1 else 0 })

}


/**
 * Program Value
 */
data class BooleanVariableProgramValue(val invocation : Invocation) : BooleanVariableValue(), Model
{


    override fun onLoad() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<BooleanVariableValue>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<BooleanVariableValue> =
                effApply(::BooleanVariableProgramValue, Invocation.fromDocument(doc))
    }


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    override fun dependencies() : Set<VariableReference> = this.invocation.dependencies()

    override fun value(): AppEff<Boolean> {
        TODO("not implemented")
    }


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override val id : UUID = this.invocation.id

    override val name : String = this.invocation.name

    override val modelObject : Model = this.invocation

}


fun liftBooleanVariableValue(varValue : BooleanVariableValue) : Func<BooleanVariableValue>
    = when (varValue)
    {
        is BooleanVariableLiteralValue -> Prim(varValue, "literal")
        is BooleanVariableProgramValue -> Comp(varValue, "program")
    }


