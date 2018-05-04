
package com.kispoko.tome.model.engine.constraint


import android.util.Log
import com.kispoko.tome.R.string.value
import com.kispoko.tome.app.AppEff
import com.kispoko.tome.app.AppError
import com.kispoko.tome.app.AppStateError
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.model.engine.EngineValue
import com.kispoko.tome.model.engine.EngineValueNumber
import com.kispoko.tome.model.engine.EngineValueText
import com.kispoko.tome.model.engine.reference.NumberReference
import com.kispoko.tome.model.engine.reference.TextReference
import com.kispoko.tome.rts.entity.EntityId
import com.kispoko.tome.rts.entity.sheet.NumberReferenceDoesNotHaveValue
import com.kispoko.tome.rts.entity.sheet.SheetData
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.UnknownCase
import lulo.value.ValueParser
import maybe.Just
import java.io.Serializable



sealed class Constraint : ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<Constraint>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<Constraint> =
            when (doc.case()) {
                "constraint_and"    -> ConstraintAnd.fromDocument(doc.nextCase()) as ValueParser<Constraint>
                "constraint_or"     -> ConstraintOr.fromDocument(doc.nextCase()) as ValueParser<Constraint>
                "constraint_number" -> ConstraintNumber.fromDocument(doc.nextCase()) as ValueParser<Constraint>
                "constraint_text"   -> ConstraintText.fromDocument(doc.nextCase()) as ValueParser<Constraint>
                else                -> effError(UnknownCase(doc.case(), doc.path))
            }
    }


    // -----------------------------------------------------------------------------------------
    // ABSTRACT METHODS
    // -----------------------------------------------------------------------------------------

    abstract fun matchesValue(engineValue : EngineValue, entityId : EntityId) : Boolean

}


/**
 * Constraint: Conjunction
 */
data class ConstraintAnd(val constraints : List<Constraint>) : Constraint()
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ConstraintAnd>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<ConstraintAnd> = when (doc)
        {
            is DocDict ->
            {
                effect.apply(::ConstraintAnd,
                      // Contraints
                      doc.list("constraints") ap { it.map { Constraint.fromDocument(it) } }
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // ----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
       "contraints" to DocList(this.constraints.map { it.toDocument() })
    ))


    // -----------------------------------------------------------------------------------------
    // CONSTRAINT
    // ----------------------------------------------------------------------------------------

    override fun matchesValue(engineValue : EngineValue, entityId : EntityId) : Boolean =
        this.constraints.map { it.matchesValue(engineValue, entityId) }.all { it}

}


/**
 * Constraint: Disjunction
 */
data class ConstraintOr(val constraints : List<Constraint>) : Constraint()
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ConstraintAnd>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<ConstraintAnd> = when (doc)
        {
            is DocDict ->
            {
                effect.apply(::ConstraintAnd,
                      // Contraints
                      doc.list("constraints") ap { it.map { Constraint.fromDocument(it) } }
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // ----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
       "contraints" to DocList(this.constraints.map { it.toDocument() })
    ))


    // -----------------------------------------------------------------------------------------
    // CONSTRAINT
    // ----------------------------------------------------------------------------------------

    override fun matchesValue(engineValue : EngineValue, entityId : EntityId) : Boolean =
        this.constraints.map { it.matchesValue(engineValue, entityId) }.any()

}



/**
 * Number Constraint
 */
sealed class ConstraintNumber : Constraint(), ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ConstraintNumber>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<ConstraintNumber> =
            when (doc.case())
            {
                "number_constraint_range"    -> NumberConstraintRange.fromDocument(doc.nextCase())
                "number_constraint_equal_to" -> NumberConstraintEqualTo.fromDocument(doc.nextCase())
                else                         -> effError(UnknownCase(doc.case(), doc.path))
            }
    }


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    abstract fun constrainedValue(value : Double, entityId : EntityId) : AppEff<Double>


    abstract fun matchesNumberValue(numberValue : EngineValueNumber,
                                    entityId : EntityId) : Boolean


    // -----------------------------------------------------------------------------------------
    // CONSTRAINT
    // ----------------------------------------------------------------------------------------

    override fun matchesValue(engineValue : EngineValue, entityId : EntityId) : Boolean = when (engineValue) {
        is EngineValueNumber -> matchesNumberValue(engineValue, entityId)
        else                 -> false
    }

}



/**
 * Number Constraint: Equal
 */
data class NumberConstraintEqualTo(val equalTo : NumberReference)
                                    : ConstraintNumber()
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ConstraintNumber>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<ConstraintNumber> = when (doc)
        {
            is DocDict ->
            {
                apply(::NumberConstraintEqualTo,
                      // Equal To
                      doc.at("equal_to") ap { NumberReference.fromDocument(it) })
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // ----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
       "equal_to" to this.equalTo.toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // NUMBER CONSTRAINT
    // ----------------------------------------------------------------------------------------

    override fun constrainedValue(value: Double, entityId: EntityId) : AppEff<Double> =
        SheetData.number(this.equalTo, entityId) ap { maybeEqualTo ->
        note<AppError,Double>(maybeEqualTo, AppStateError(NumberReferenceDoesNotHaveValue(this.equalTo))) ap { equalTo ->
        effValue<AppError,Double>(equalTo)
        } }


    override fun matchesNumberValue(numberValue : EngineValueNumber,
                                    entityId : EntityId) : Boolean
    {
        val equalTo = SheetData.number(this.equalTo, entityId)
        return when (equalTo) {
            is Val -> {
                val maybeEqualtTo = equalTo.value
                when (maybeEqualtTo) {
                    is Just -> maybeEqualtTo.value == numberValue.value
                    else    -> false
                }
            }
            else -> false
        }
    }

}


/**
 * Number Constraint: Range
 * min and max are both inclusive
 */
data class NumberConstraintRange(val min : NumberReference,
                                 val max : NumberReference)
                                  : ConstraintNumber()
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ConstraintNumber>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<ConstraintNumber> = when (doc)
        {
            is DocDict ->
            {
                effect.apply(::NumberConstraintRange,
                      // Minimum
                      doc.at("min") ap { NumberReference.fromDocument(it) },
                      // Maximum
                      doc.at("max") ap { NumberReference.fromDocument(it) })
            }
            else       -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // ----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
       "minimum" to this.min.toDocument(),
       "maximum" to this.max.toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    override fun constrainedValue(value : Double, entityId : EntityId) : AppEff<Double> =
        SheetData.number(this.min, entityId) ap { maybeMin ->
        SheetData.number(this.max, entityId) ap { maybeMax ->
        note<AppError,Double>(maybeMin, AppStateError(NumberReferenceDoesNotHaveValue(this.min))) ap { min ->
        note<AppError,Double>(maybeMax, AppStateError(NumberReferenceDoesNotHaveValue(this.max))) ap { max ->
            when {
                value < min -> effValue(min)
                value > max -> effValue(max)
                else        -> effValue<AppError,Double>(value)
            }
        } } } }



    override fun matchesNumberValue(numberValue : EngineValueNumber,
                                    entityId : EntityId) : Boolean
    {
        val result = SheetData.number(this.min, entityId) ap { maybeMin ->
            SheetData.number(this.max, entityId) ap { maybeMax ->
            note<AppError,Double>(maybeMin, AppStateError(NumberReferenceDoesNotHaveValue(this.min))) ap { min ->
            note<AppError,Double>(maybeMax, AppStateError(NumberReferenceDoesNotHaveValue(this.max))) ap { max ->
                if (numberValue.value in min..max)
                    effValue<AppError,Boolean>(true)
                else
                    effValue(false)
            } } } }

        return when (result) {
            is Val -> result.value
            is Err -> false
        }
    }
}





/**
 * Text Constraint
 */
sealed class ConstraintText : Constraint(), ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ConstraintText>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<ConstraintText> =
            when (doc.case())
            {
                "text_constraint_equal_to" -> TextConstraintEqualTo.fromDocument(doc.nextCase())
                else                       -> effError(UnknownCase(doc.case(), doc.path))
            }
    }


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    abstract fun matchesTextValue(textValue : EngineValueText, entityId : EntityId) : Boolean


    // -----------------------------------------------------------------------------------------
    // CONSTRAINT
    // ----------------------------------------------------------------------------------------

    override fun matchesValue(engineValue : EngineValue,
                              entityId : EntityId) : Boolean = when (engineValue) {
        is EngineValueText -> matchesTextValue(engineValue, entityId)
        else               -> false
    }

}


/**
 * Text Constraint: Equal To
 */
data class TextConstraintEqualTo(val equalTo : TextReference) : ConstraintText()
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ConstraintText>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<ConstraintText> = when (doc)
        {
            is DocDict ->
            {
                effect.apply(::TextConstraintEqualTo,
                      // Equal To
                      doc.at("equal_to") ap { TextReference.fromDocument(it) })
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // ----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
       "equal_to" to this.equalTo.toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // TEXT CONSTRAINT
    // ----------------------------------------------------------------------------------------

    override fun matchesTextValue(textValue : EngineValueText, entityId : EntityId) : Boolean
    {
        val equalTo = SheetData.text(this.equalTo, entityId)
        return when (equalTo) {
            is Val -> {
                val maybeEqualtTo = equalTo.value
                when (maybeEqualtTo) {
                    is Just -> {
//                        Log.d("****CONSTRAINT", "equal to: ${maybeEqualtTo.value}")
//                        Log.d("****CONSTRAINT", "text value: ${textValue.value}")
                        maybeEqualtTo.value == textValue.value
                    }
                    else    -> false
                }
            }
            else -> false
        }
    }

}
