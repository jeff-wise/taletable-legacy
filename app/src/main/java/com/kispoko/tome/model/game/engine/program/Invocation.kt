
package com.kispoko.tome.model.game.engine.program


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Func
import com.kispoko.tome.lib.functor.Prim
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.model.game.engine.reference.DataReference
import com.kispoko.tome.model.game.engine.variable.VariableReference
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
                      val programId: Prim<ProgramId>,
                      val parameters : Prim<List<DataReference>>) : Model
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(programId : ProgramId, parameters: List<DataReference>)
        : this(UUID.randomUUID(), Prim(programId), Prim(parameters))


    companion object : Factory<Invocation>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<Invocation> = when (doc)
        {
            is DocDict ->
            {
                effApply(::Invocation,
                        // Program Name
                        doc.at("program_name") ap { ProgramId.fromDocument(it) },
                        // Parameters
                        doc.list("parameters") ap { docList ->
                            docList.map { DataReference.fromDocument(it) }
                        })
            }
            else -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() {}


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    /**
     * The set of variables that the program depends on.
     */
    fun dependencies(): Set<VariableReference> =
        this.parameters.value.fold(setOf(), {
            accSet, param -> accSet.plus(param.dependencies())
        })

}

     //
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

 //   }

//}

//
///**
// * Invocation Parameter
// */
//data class InvocationParameter(val dataReference : DataReference)
//{
//
//     companion object : Factory<InvocationParameter>
//    {
//        override fun fromDocument(doc: SpecDoc) : ValueParser<InvocationParameter> =
//                effApply(::InvocationParameter, DataReference.fromDocument(doc))
//
////                when (doc)
////        {
////            is DocDict -> when (doc.case())
////            {
////                "variable" -> InvocationParameterVariable.fromDocument(doc)
////                else       -> effError<ValueError,InvocationParameter>(
////                                    UnknownCase(doc.case(), doc.path))
////            }
////            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
////        }
//    }
//}

//
//data class InvocationParameterVariable(override val id : UUID,
//                                       val variableReference : Func<VariableReference>)
//                                       : InvocationParameter()
//{
//
//    companion object : Factory<InvocationParameter>
//    {
//        override fun fromDocument(doc: SpecDoc)
//                      : ValueParser<InvocationParameter> = when (doc)
//        {
//            is DocDict -> effApply(::InvocationParameterVariable,
//                                   // Model Id
//                                   effValue(UUID.randomUUID()),
//                                   // Variable Reference
//                                   doc.at("reference") ap {
//                                       effApply(::Comp, VariableReference.fromDocument(it))
//                                   })
//            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
//        }
//    }
//
//    override fun onLoad() { }
//
//}
//
//
//
//    // ** Dependencies
//    // ------------------------------------------------------------------------------------------

