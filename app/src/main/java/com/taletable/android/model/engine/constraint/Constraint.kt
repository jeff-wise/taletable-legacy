
package com.taletable.android.model.engine.constraint


import com.taletable.android.app.AppEff
import com.taletable.android.app.AppError
import com.taletable.android.app.AppStateError
import com.taletable.android.lib.Factory
import com.taletable.android.model.engine.EngineTextListValue
import com.taletable.android.model.engine.EngineValue
import com.taletable.android.model.engine.EngineValueNumber
import com.taletable.android.model.engine.EngineValueText
import com.taletable.android.model.engine.reference.NumberReference
import com.taletable.android.model.engine.reference.TextReference
import com.taletable.android.rts.entity.EntityId
import com.taletable.android.rts.entity.sheet.NumberReferenceDoesNotHaveValue
import com.taletable.android.rts.entity.sheet.SheetData
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.UnknownCase
import lulo.value.ValueParser
import maybe.Just
import maybe.Maybe
import maybe.Nothing
import maybe.filterJust
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
                "constraint_and"        -> ConstraintAnd.fromDocument(doc.nextCase()) as ValueParser<Constraint>
                "constraint_or"         -> ConstraintOr.fromDocument(doc.nextCase()) as ValueParser<Constraint>
                "constraint_number"     -> ConstraintNumber.fromDocument(doc.nextCase()) as ValueParser<Constraint>
                "constraint_text"       -> ConstraintText.fromDocument(doc.nextCase()) as ValueParser<Constraint>
                "constraint_text_list"  -> ConstraintTextList.fromDocument(doc.nextCase()) as ValueParser<Constraint>
                else                    -> effError(UnknownCase(doc.case(), doc.path))
            }

    }


    // -----------------------------------------------------------------------------------------
    // ABSTRACT METHODS
    // -----------------------------------------------------------------------------------------

    abstract fun matchesValue(engineValue : EngineValue, entityId : EntityId) : Boolean


    abstract fun hasConstraintType(constraintType : ConstraintType) : Boolean


    abstract fun constraintOfType(constraintType : ConstraintType) : Maybe<Constraint>

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


    override fun hasConstraintType(constraintType : ConstraintType) : Boolean =
        this.constraints.any { it.hasConstraintType(constraintType) }


    override fun constraintOfType(constraintType : ConstraintType) : Maybe<Constraint>
    {
        val constraints = this.constraints.map { it.constraintOfType(constraintType) }.filterJust()
        return if (constraints.isNotEmpty())
            Just(constraints.first())
        else
            Nothing()
    }

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


    override fun hasConstraintType(constraintType : ConstraintType) : Boolean =
            this.constraints.any { it.hasConstraintType(constraintType) }


    override fun constraintOfType(constraintType : ConstraintType) : Maybe<Constraint>
    {
        val constraints = this.constraints.map { it.constraintOfType(constraintType) }.filterJust()
        return if (constraints.isNotEmpty())
            Just(constraints.first())
        else
            Nothing()
    }

}


// ---------------------------------------------------------------------------------------------
// =============================================================================================
// CONSTRAINT: Number
// =============================================================================================
// ---------------------------------------------------------------------------------------------

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


    abstract fun hasNumberConstraintType(constraintType : ConstraintTypeNumber) : Boolean


    abstract fun numberConstraintOfType(constraintType : ConstraintTypeNumber) : Maybe<Constraint>


    // -----------------------------------------------------------------------------------------
    // CONSTRAINT
    // ----------------------------------------------------------------------------------------

    override fun matchesValue(engineValue : EngineValue, entityId : EntityId) : Boolean = when (engineValue) {
        is EngineValueNumber -> matchesNumberValue(engineValue, entityId)
        else                 -> false
    }


    override fun hasConstraintType(constraintType : ConstraintType) : Boolean = when (constraintType)
    {
        is ConstraintTypeNumber -> this.hasNumberConstraintType(constraintType)
        else                    -> false
    }

    override fun constraintOfType(constraintType : ConstraintType) : Maybe<Constraint> = when (constraintType)
    {
        is ConstraintTypeNumber -> this.numberConstraintOfType(constraintType)
        else                    -> Nothing()
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


    override fun hasNumberConstraintType(constraintType : ConstraintTypeNumber) : Boolean =
            constraintType == ConstraintTypeNumberIsEqual


    override fun numberConstraintOfType(constraintType : ConstraintTypeNumber) : Maybe<Constraint> =
        when (constraintType) {
            is ConstraintTypeNumberIsEqual -> Just(this)
            else                           -> Nothing()
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


    override fun hasNumberConstraintType(constraintType : ConstraintTypeNumber) : Boolean =
        constraintType == ConstraintTypeNumberInRange


    override fun numberConstraintOfType(constraintType : ConstraintTypeNumber) : Maybe<Constraint> =
        when (constraintType) {
            is ConstraintTypeNumberInRange -> Just(this)
            else                           -> Nothing()
        }

}


// ---------------------------------------------------------------------------------------------
// =============================================================================================
// CONSTRAINT: Text
// =============================================================================================
// ---------------------------------------------------------------------------------------------

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


    abstract fun hasTextConstraintType(constraintType : ConstraintTypeText) : Boolean


    abstract fun textConstraintOfType(constraintType : ConstraintTypeText) : Maybe<Constraint>


    // -----------------------------------------------------------------------------------------
    // CONSTRAINT
    // ----------------------------------------------------------------------------------------

    override fun matchesValue(engineValue : EngineValue,
                              entityId : EntityId) : Boolean = when (engineValue) {
        is EngineValueText -> matchesTextValue(engineValue, entityId)
        else               -> false
    }


    override fun hasConstraintType(constraintType : ConstraintType) : Boolean = when (constraintType)
    {
        is ConstraintTypeText -> this.hasTextConstraintType(constraintType)
        else                  -> false
    }


    override fun constraintOfType(constraintType : ConstraintType) : Maybe<Constraint> = when (constraintType)
    {
        is ConstraintTypeText -> this.textConstraintOfType(constraintType)
        else                  -> Nothing()
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


    override fun hasTextConstraintType(constraintType : ConstraintTypeText) : Boolean =
            constraintType == ConstraintTypeTextIsEqual


    override fun textConstraintOfType(constraintType : ConstraintTypeText) : Maybe<Constraint> =
        when (constraintType) {
            is ConstraintTypeTextIsEqual -> Just(this)
            else                         -> Nothing()
        }

}


// ---------------------------------------------------------------------------------------------
// =============================================================================================
// CONSTRAINT: Text List
// =============================================================================================
// ---------------------------------------------------------------------------------------------

/**
 * Text List Constraint
 */
sealed class ConstraintTextList : Constraint(), ToDocument, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ConstraintTextList>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<ConstraintTextList> =
            when (doc.case())
            {
                "constraint_text_list_max_size" -> TextListConstraintMaxSize.fromDocument(doc.nextCase())
                "constraint_text_list_is_set"   -> effValue(TextListConstraintIsSet())
                else                            -> effError(UnknownCase(doc.case(), doc.path))
            }
    }


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    abstract fun matchesTextListValue(value : EngineTextListValue,
                                      entityId : EntityId) : Boolean


    abstract fun hasTextConstraintType(constraintType : ConstraintTypeTextList) : Boolean


    abstract fun textListConstraintOfType(constraintType : ConstraintTypeTextList) : Maybe<Constraint>


    // -----------------------------------------------------------------------------------------
    // CONSTRAINT
    // ----------------------------------------------------------------------------------------

    override fun matchesValue(engineValue : EngineValue,
                              entityId : EntityId) : Boolean = when (engineValue) {
        is EngineTextListValue -> matchesTextListValue(engineValue, entityId)
        else                   -> false
    }


    override fun hasConstraintType(constraintType : ConstraintType) : Boolean = when (constraintType)
    {
        is ConstraintTypeTextList -> this.hasTextConstraintType(constraintType)
        else                      -> false
    }


    override fun constraintOfType(constraintType : ConstraintType) : Maybe<Constraint> = when (constraintType)
    {
        is ConstraintTypeTextList -> this.textListConstraintOfType(constraintType)
        else                      -> Nothing()
    }


}


/**
 * Text List Constraint: Max Size
 */
data class TextListConstraintMaxSize(val maxSize : NumberReference) : ConstraintTextList()
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ConstraintTextList>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<ConstraintTextList> = when (doc)
        {
            is DocDict ->
            {
                effect.apply(::TextListConstraintMaxSize,
                      // Max Size
                      doc.at("max_size") ap { NumberReference.fromDocument(it) })
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // ----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
       "max_size" to this.maxSize.toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // TEXT CONSTRAINT
    // ----------------------------------------------------------------------------------------

    override fun matchesTextListValue(value : EngineTextListValue, entityId : EntityId) : Boolean
    {
        val _maybeMaxSize = SheetData.number(this.maxSize, entityId)
        when (_maybeMaxSize) {
            is Val -> {
                val _maxSize = _maybeMaxSize.value
                when (_maxSize) {
                    is Just -> {
                        return value.value.size <= _maxSize.value
                    }
                }
            }
        }

        return false
    }


    override fun hasTextConstraintType(constraintType : ConstraintTypeTextList) : Boolean =
            constraintType == ConstraintTypeTextListMaxSize


    override fun textListConstraintOfType(constraintType : ConstraintTypeTextList) : Maybe<Constraint> =
        when (constraintType) {
            is ConstraintTypeTextListMaxSize -> Just(this)
            else                             -> Nothing()
        }

}


/**
 * Text List Constraint: Is Set
 */
class TextListConstraintIsSet() : ConstraintTextList()
{

    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // ----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
    ))


    // -----------------------------------------------------------------------------------------
    // TEXT CONSTRAINT
    // ----------------------------------------------------------------------------------------

    override fun matchesTextListValue(value : EngineTextListValue, entityId : EntityId) : Boolean =
        value.value.toSet().size == value.value.size


    override fun hasTextConstraintType(constraintType : ConstraintTypeTextList) : Boolean =
            constraintType == ConstraintTypeTextListIsSet


    override fun textListConstraintOfType(constraintType : ConstraintTypeTextList) : Maybe<Constraint> =
        when (constraintType) {
            is ConstraintTypeTextListIsSet -> Just(this)
            else                           -> Nothing()
        }

}




sealed class ConstraintType

sealed class ConstraintTypeNumber : ConstraintType()

object ConstraintTypeNumberIsEqual : ConstraintTypeNumber()
object ConstraintTypeNumberInRange : ConstraintTypeNumber()

sealed class ConstraintTypeText : ConstraintType()

object ConstraintTypeTextIsEqual : ConstraintTypeText()

sealed class ConstraintTypeTextList : ConstraintType()

object ConstraintTypeTextListIsSet : ConstraintTypeTextList()
object ConstraintTypeTextListMaxSize : ConstraintTypeTextList()

