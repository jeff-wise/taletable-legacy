
package com.kispoko.tome.model.game.engine.reference


import com.kispoko.tome.app.ApplicationLog
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.model.game.engine.dice.DiceRoll
import com.kispoko.tome.model.game.engine.summation.SummationId
import com.kispoko.tome.model.game.engine.summation.term.TermComponent
import com.kispoko.tome.model.game.engine.variable.*
import com.kispoko.tome.rts.entity.EntityId
import com.kispoko.tome.rts.entity.summation
import com.kispoko.tome.rts.entity.variables
import effect.*
import lulo.document.SchemaDoc
import lulo.document.ToDocument
import lulo.value.UnknownCase
import lulo.value.ValueParser
import java.io.Serializable



/**
 * Boolean Reference
 */
sealed class DiceRollReference : ToDocument, Serializable
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
                "summation_id"       -> DiceRollReferenceSummation.fromDocument(doc.nextCase())
                else                 -> effError(UnknownCase(doc.case(), doc.path))
            }
    }


    // -----------------------------------------------------------------------------------------
    // DEPENDENCIES
    // -----------------------------------------------------------------------------------------

    open fun dependencies(entityId : EntityId): Set<VariableReference> = setOf()


    // -----------------------------------------------------------------------------------------
    // COMPONENTS
    // -----------------------------------------------------------------------------------------

    abstract fun components(entityId : EntityId) : List<TermComponent>

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

    override fun components(entityId : EntityId) : List<TermComponent> = listOf()


    // -----------------------------------------------------------------------------------------
    // SUM MODEL
    // -----------------------------------------------------------------------------------------

//    override fun columnValue() = ProdValue(this.value)
//
//
//    override fun case() = "literal"
//
//
//    override val sumModelObject = this

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

    override fun components(entityId : EntityId) : List<TermComponent>
    {
        // TODO ensure just die roll variables
        val variables = variables(this.variableReference, entityId)

        when (variables)
        {
            is effect.Val ->
            {
                return variables.value.mapNotNull {
                    val valueString = it.valueString(entityId)
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

//    override fun columnValue() = PrimValue(this)
//
//
//    override fun case() = "partVariable"
//
//
//    override val sumModelObject = this


    // -----------------------------------------------------------------------------------------
    // DEPENDENCIES
    // -----------------------------------------------------------------------------------------

    override fun dependencies(entityId : EntityId) = setOf(variableReference)


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() = this.variableReference.asSQLValue()

}


/**
 * Summation Id Dice Roll Reference
 */
data class DiceRollReferenceSummation(val summationId : SummationId)
            : DiceRollReference(), SQLSerializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<DiceRollReference>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<DiceRollReference> =
                effApply(::DiceRollReferenceSummation, SummationId.fromDocument(doc))

    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = this.summationId.toDocument()
                                    .withCase("summation_id")


    // -----------------------------------------------------------------------------------------
    // COMPONENTS
    // -----------------------------------------------------------------------------------------

    override fun components(entityId : EntityId) : List<TermComponent>
    {
        // TODO ensure just die roll variables
//        val variables = SheetManager.sheetState(sheetContext.sheetId)
//                                    .apply { it.variables(this.variableReference) }
//
//        when (variables)
//        {
//            is effect.Val ->
//            {
//                return variables.value.mapNotNull {
//                    val valueString = it.valueString(sheetContext)
//                    when (valueString)
//                    {
//                        is effect.Val -> TermComponent(it.label().value, valueString.value)
//                        is Err -> {
//                            ApplicationLog.error(valueString.error)
//                            null
//                        }
//                    }
//                }
//            }
//            is Err -> ApplicationLog.error(variables.error)
//        }

        return listOf()
    }


    // -----------------------------------------------------------------------------------------
    // SUM MODEL
    // -----------------------------------------------------------------------------------------

//    override fun columnValue() = PrimValue(this)
//
//
//    override fun case() = "partVariable"
//
//
//    override val sumModelObject = this


    // -----------------------------------------------------------------------------------------
    // DEPENDENCIES
    // -----------------------------------------------------------------------------------------

    override fun dependencies(entityId : EntityId) : Set<VariableReference>
    {
        val summation = summation(this.summationId, entityId)
        return when (summation) {
            is Val -> {
                summation.value.dependencies(entityId)
            }
            is Err -> {
                ApplicationLog.error(summation.error)
                setOf()
            }
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() = this.summationId.asSQLValue()

}

