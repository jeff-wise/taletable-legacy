
package com.kispoko.tome.model.game.engine.variable.constraint


import com.kispoko.tome.app.AppEff
import com.kispoko.tome.app.AppEntityError
import com.kispoko.tome.app.AppError
import com.kispoko.tome.app.AppStateError
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.SumType
import com.kispoko.tome.lib.orm.schema.PrimValue
import com.kispoko.tome.lib.orm.sql.SQLBlob
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.model.game.engine.reference.NumberReference
import com.kispoko.tome.rts.entity.EntityId
import com.kispoko.tome.rts.entity.sheet.*
import effect.apply
import effect.effError
import effect.effValue
import effect.note
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.UnknownCase
import lulo.value.ValueParser
import org.apache.commons.lang3.SerializationUtils
import java.io.Serializable



/**
 * Number Constraint
 */
sealed class NumberConstraint : ToDocument, SumType, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<NumberConstraint>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<NumberConstraint> =
            when (doc.case())
            {
                "number_constraint_range" -> NumberConstraintRange.fromDocument(doc.nextCase())
                else                      -> effError(UnknownCase(doc.case(), doc.path))
            }
    }


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    abstract fun constrainedValue(value : Double, entityId : EntityId) : AppEff<Double>

}


/**
 * Number Constraint: Range
 * min and max are both inclusive
 */
data class NumberConstraintRange(val min : NumberReference,
                                 val max : NumberReference)
                                  : NumberConstraint(), SQLSerializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<NumberConstraint>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<NumberConstraint> = when (doc)
        {
            is DocDict ->
            {
                apply(::NumberConstraintRange,
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
    // SUM TYPE
    // -----------------------------------------------------------------------------------------

    override fun columnValue() = PrimValue(this)


    override fun case() = "number_constraint_range"


    override val sumModelObject = this


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() = SQLBlob({ SerializationUtils.serialize(this) })


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

}
