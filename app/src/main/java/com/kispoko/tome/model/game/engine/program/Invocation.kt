
package com.kispoko.tome.model.game.engine.program


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Comp
import com.kispoko.tome.lib.functor.Coll
import com.kispoko.tome.lib.functor.Func
import com.kispoko.tome.lib.functor.Prim
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.model.game.engine.variable.VariableReference
import effect.Err
import effect.effApply
import effect.effError
import effect.effValue
import lulo.document.*
import lulo.value.*
import lulo.value.UnexpectedType
import java.util.*



/**
 * Program Invocation
 */
data class Invocation(override val id : UUID,
                      val programId: Func<ProgramId>,
                      val parameters : Coll<InvocationParameter>) : Model
{

    companion object : Factory<Invocation>
    {
        override fun fromDocument(doc: SpecDoc) : ValueParser<Invocation> = when (doc)
        {
            is DocDict ->
            {
                effApply(::Invocation,
                         // Model Id
                         effValue(UUID.randomUUID()),
                         // Program Name
                         doc.at("program_name") ap {
                             effApply(::Prim, ProgramId.fromDocument(it))
                         },
                         // Parameters
                         doc.list("parameters") ap { docList ->
                             effApply(::Coll,
                                     docList.map { InvocationParameter.fromDocument(it) })
                         })
            }

            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }

    override fun onLoad() { }

}


/**
 * Invocation Parameter
 */
sealed class InvocationParameter : Model
{

     companion object : Factory<InvocationParameter>
    {
        override fun fromDocument(doc: SpecDoc)
                      : ValueParser<InvocationParameter> = when (doc)
        {
            is DocDict -> when (doc.case())
            {
                "variable" -> InvocationParameterVariable.fromDocument(doc)
                else       -> effError<ValueError,InvocationParameter>(
                                    UnknownCase(doc.case(), doc.path))
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }
}


data class InvocationParameterVariable(override val id : UUID,
                                       val variableReference : Func<VariableReference>)
                                       : InvocationParameter()
{

    companion object : Factory<InvocationParameter>
    {
        override fun fromDocument(doc: SpecDoc)
                      : ValueParser<InvocationParameter> = when (doc)
        {
            is DocDict -> effApply(::InvocationParameterVariable,
                                   // Model Id
                                   effValue(UUID.randomUUID()),
                                   // Variable Reference
                                   doc.at("reference") ap {
                                       effApply(::Comp, VariableReference.fromDocument(it))
                                   })
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }

    override fun onLoad() { }

}
//
//
//
//    // ** Dependencies
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * Get the list of variables that this program invocation depends on.
//     * @return A list of variable names.
//     */
//    public List<VariableReference> variableDependencies()
//    {
//        List<VariableReference> variableReferences = new ArrayList<>();
//
//        for (InvocationParameterUnion parameter : this.parameters())
//        {
//            switch (parameter.type())
//            {
//                case REFERENCE:
//                    variableReferences.add(VariableReference.asByName(parameter.reference()));
//                    break;
//            }
//        }
//
//        return variableReferences;
//    }

