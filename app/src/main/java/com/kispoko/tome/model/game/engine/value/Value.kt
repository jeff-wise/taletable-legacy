
package com.kispoko.tome.model.game.engine.value


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Coll
import com.kispoko.tome.lib.functor.Func
import com.kispoko.tome.lib.functor.Prim
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.model.game.engine.variable.Variable
import effect.*
import lulo.document.*
import lulo.value.*
import lulo.value.UnexpectedType
import java.util.*



/**
 * Value
 */
@Suppress("UNCHECKED_CAST")
sealed class Value(open val valueId : Func<ValueId>,
                   open val description : Func<ValueDescription>) : Model
{

    companion object : Factory<Value>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<Value> = when (doc)
        {
            is DocDict ->
            {
                when (doc.case())
                {
                    "number" -> ValueNumber.fromDocument(doc)
                                    as ValueParser<Value>
                    "text"   -> ValueText.fromDocument(doc)
                                    as ValueParser<Value>
                    else     -> effError<ValueError,Value>(
                                            UnknownCase(doc.case(), doc.path))
                }
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }

    override fun onLoad() { }

}


/**
 * Number Value
 */
data class ValueNumber(override val id : UUID,
                       override val valueId : Func<ValueId>,
                       override val description: Func<ValueDescription>,
                       val value : Func<Double>,
                       val variables : Coll<Variable>)
                        : Value(valueId, description)
{

    companion object : Factory<ValueNumber>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<ValueNumber> = when (doc)
        {
            is DocDict -> effApply(::ValueNumber,
                                   // Model Id
                                   effValue(UUID.randomUUID()),
                                   // Value Id
                                   doc.at("value_id") ap {
                                       effApply(::Prim, ValueId.fromDocument(it))
                                   },
                                   // Description
                                   doc.at("description") ap {
                                       effApply(::Prim, ValueDescription.fromDocument(it))
                                   },
                                   // Value
                                   effApply(::Prim, doc.double("value")),
                                   // Variables
                                   doc.list("variables") ap { docList ->
                                       effApply(::Coll,
                                           docList.map { Variable.fromDocument(it) })
                                   })
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }

    override fun onLoad() { }

}


/**
 * Text Value
 */
data class ValueText(override val id : UUID,
                     override val valueId : Func<ValueId>,
                     override val description: Func<ValueDescription>,
                     val value : Func<String>,
                     val variables : Coll<Variable>)
                      : Value(valueId, description)
{

    companion object : Factory<ValueText>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<ValueText> = when (doc)
        {
            is DocDict -> effApply(::ValueText,
                                   // Model Id
                                   effValue(UUID.randomUUID()),
                                   // Value Id
                                   doc.at("value_id") ap {
                                       effApply(::Prim, ValueId.fromDocument(it))
                                   },
                                   // Description
                                   doc.at("description") ap {
                                       effApply(::Prim, ValueDescription.fromDocument(it))
                                   },
                                   // Value
                                   effApply(::Prim, doc.text("value")),
                                   // Variables
                                   doc.list("variables") ap { docList ->
                                       effApply(::Coll,
                                           docList.map { Variable.fromDocument(it) })
                                   })
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }

    override fun onLoad() { }

}


/**
 * Value Reference
 */
data class ValueReference(override val id : UUID,
                          val valueSetId: Func<ValueSetId>,
                          val valueId: Func<ValueId>) : Model
{

    companion object : Factory<ValueReference>
    {
        override fun fromDocument(doc: SpecDoc) : ValueParser<ValueReference> = when (doc)
        {
            is DocDict -> effApply(::ValueReference,
                                   // Model Id
                                   effValue(UUID.randomUUID()),
                                   // ValueSet Name
                                   doc.at("value_set_name") ap {
                                       effApply(::Prim, ValueSetId.fromDocument(it))
                                   },
                                   // Value Name
                                   doc.at("value_name") ap {
                                       effApply(::Prim, ValueId.fromDocument(it))
                                   })
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }

    override fun onLoad() { }
}


/**
 * Value Id
 */
data class ValueId(val value : String)
{

    companion object : Factory<ValueId>
    {
        override fun fromDocument(doc: SpecDoc) : ValueParser<ValueId> = when (doc)
        {
            is DocText -> effValue(ValueId(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }
}


/**
 * Value Description
 */
data class ValueDescription(val value : String)
{

    companion object : Factory<ValueDescription>
    {
        override fun fromDocument(doc: SpecDoc) : ValueParser<ValueDescription> = when (doc)
        {
            is DocText -> effValue(ValueDescription(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }
}

//
// Number Value
//
//    // > Variables
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * The text value's variables.
//     * @return The list of variables.
//     */
//    public List<VariableUnion> variables()
//    {
//        return this.variables.getValue();
//    }
//
//
//    public void addToState()
//    {
//        for (VariableUnion variableUnion : this.variables()) {
//            State.addVariable(variableUnion);
//        }
//    }
//
//
//    public void removeFromState()
//    {
//        for (VariableUnion variableUnion : this.variables()) {
//            State.removeVariable(variableUnion.variable().name());
//        }
//    }

