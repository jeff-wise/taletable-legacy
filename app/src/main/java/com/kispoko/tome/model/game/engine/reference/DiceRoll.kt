
package com.kispoko.tome.model.game.engine.reference


import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.SumType
import com.kispoko.tome.lib.orm.schema.PrimValue
import com.kispoko.tome.lib.orm.schema.ProdValue
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.model.game.engine.dice.DiceRoll
import com.kispoko.tome.model.game.engine.summation.term.TermComponent
import com.kispoko.tome.model.game.engine.variable.*
import com.kispoko.tome.rts.sheet.SheetContext
import com.kispoko.tome.rts.sheet.SheetManager
import effect.*
import lulo.document.SchemaDoc
import lulo.document.ToDocument
import lulo.value.UnknownCase
import lulo.value.ValueParser
import java.io.Serializable



/**
 * Boolean Reference
 */
sealed class DiceRollReference : ToDocument, SumType, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<DiceRollReference>
    {
        override fun fromDocument(doc : SchemaDoc): ValueParser<DiceRollReference> =
            when (doc.case())
            {
                "dice_roll"          -> DiceRollReferenceLiteral.fromDocument(doc.nextCase())
                "variable_reference" -> DiceRollReferenceVariable.fromDocument(doc.nextCase())
                else                 -> effError(UnknownCase(doc.case(), doc.path))
            }
    }


    // -----------------------------------------------------------------------------------------
    // DEPENDENCIES
    // -----------------------------------------------------------------------------------------

    open fun dependencies(): Set<VariableReference> = setOf()


    // -----------------------------------------------------------------------------------------
    // COMPONENTS
    // -----------------------------------------------------------------------------------------

    abstract fun components(sheetContext : SheetContext) : List<TermComponent>

}


/**
 * Literal Dice Roll Reference
 */
data class DiceRollReferenceLiteral(val value : DiceRoll) : DiceRollReference()
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<DiceRollReference>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<DiceRollReference> =
                apply(::DiceRollReferenceLiteral, DiceRoll.fromDocument(doc))

    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = this.value.toDocument().withCase("dice_roll")


    // -----------------------------------------------------------------------------------------
    // COMPONENTS
    // -----------------------------------------------------------------------------------------

    override fun components(sheetContext : SheetContext) : List<TermComponent> = listOf()


    // -----------------------------------------------------------------------------------------
    // SUM MODEL
    // -----------------------------------------------------------------------------------------

    override fun columnValue() = ProdValue(this.value)


    override fun case() = "literal"


    override val sumModelObject = this

}


/**
 * Variable Dice Roll Reference
 */
data class DiceRollReferenceVariable(val variableReference : VariableReference)
            : DiceRollReference(), SQLSerializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<DiceRollReference>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<DiceRollReference> =
                effApply(::DiceRollReferenceVariable, VariableReference.fromDocument(doc))

    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = this.variableReference.toDocument()
                                    .withCase("variable_reference")


    // -----------------------------------------------------------------------------------------
    // COMPONENTS
    // -----------------------------------------------------------------------------------------

    override fun components(sheetContext : SheetContext) : List<TermComponent>
    {
        // TODO ensure just die roll variables
        val variables = SheetManager.sheetState(sheetContext.sheetId)
                                    .apply { it.variables(this.variableReference) }

        when (variables)
        {
            is effect.Val ->
            {
                return variables.value.mapNotNull {
                    val valueString = it.valueString(sheetContext)
                    when (valueString)
                    {
                        is effect.Val -> TermComponent(it.label().value, valueString.value)
                        is Err -> {
                            ApplicationLog.error(valueString.error)
                            null
                        }
                    }
                }
            }
            is Err -> ApplicationLog.error(variables.error)
        }

        return listOf()
    }


    // -----------------------------------------------------------------------------------------
    // SUM MODEL
    // -----------------------------------------------------------------------------------------

    override fun columnValue() = PrimValue(this)


    override fun case() = "variable"


    override val sumModelObject = this


    // -----------------------------------------------------------------------------------------
    // DEPENDENCIES
    // -----------------------------------------------------------------------------------------

    override fun dependencies(): Set<VariableReference> = setOf(variableReference)


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() = this.variableReference.asSQLValue()

}

