
package com.kispoko.tome.model.game.engine.variable


import com.kispoko.tome.app.AppEff
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Comp
import com.kispoko.tome.lib.functor.Prim
import com.kispoko.tome.lib.model.SumModel
import com.kispoko.tome.lib.orm.sql.SQLInt
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.model.game.engine.program.Invocation
import com.kispoko.tome.rts.sheet.SheetContext
import effect.effApply
import effect.effError
import effect.effValue
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.UnknownCase
import lulo.value.ValueError
import lulo.value.ValueParser
import java.io.Serializable



/**
 * Boolean Variable
 */
sealed class BooleanVariableValue : SumModel, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<BooleanVariableValue>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<BooleanVariableValue> =
            when (doc.case())
            {
                "boolean_literal"    -> BooleanVariableLiteralValue.fromDocument(doc)
                "program_invocation" -> BooleanVariableProgramValue.fromDocument(doc)
                else                 -> effError<ValueError,BooleanVariableValue>(
                                            UnknownCase(doc.case(), doc.path))
            }
    }


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    open fun dependencies() : Set<VariableReference> = setOf()

    abstract fun value() : AppEff<Boolean>

    abstract fun companionVariables(sheetContext : SheetContext) : AppEff<Set<Variable>>

}


/**
 * Literal Value
 */
data class BooleanVariableLiteralValue(var value : Boolean)
                : BooleanVariableValue(), SQLSerializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<BooleanVariableValue>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<BooleanVariableValue> = when (doc)
        {
            is DocBoolean -> effValue(BooleanVariableLiteralValue(doc.boolean))
            else          -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    override fun value() : AppEff<Boolean> = effValue(this.value)


    override fun companionVariables(sheetContext : SheetContext) : AppEff<Set<Variable>> =
            effValue(setOf())


    // -----------------------------------------------------------------------------------------
    // SUM MODEL
    // -----------------------------------------------------------------------------------------

    override fun functor() = Prim(this, "literal")

    override val sumModelObject = this


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLInt({ if (this.value) 1 else 0 })

}


/**
 * Program Value
 */
data class BooleanVariableProgramValue(val invocation : Invocation) : BooleanVariableValue()
{


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<BooleanVariableValue>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<BooleanVariableValue> =
                effApply(::BooleanVariableProgramValue, Invocation.fromDocument(doc))
    }


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    override fun dependencies() : Set<VariableReference> = this.invocation.dependencies()


    override fun value(): AppEff<Boolean> {
        TODO("not implemented")
    }


    override fun companionVariables(sheetContext : SheetContext) : AppEff<Set<Variable>> =
            effValue(setOf())


    // -----------------------------------------------------------------------------------------
    // SUM MODEL
    // -----------------------------------------------------------------------------------------

    override fun functor() = Comp(this.invocation, "program")

    override val sumModelObject = this

}

