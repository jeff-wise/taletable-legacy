
package com.kispoko.tome.model.game.engine.program


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.*
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.model.game.engine.EngineValueType
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueParser
import lulo.value.valueResult
import java.util.*



/**
 * Program
 */
data class Program(override val id : UUID,
                   val programId : Func<ProgramId>,
                   val label : Func<ProgramLabel>,
                   val description : Func<ProgramDescription>,
                   val parameterTypes : Func<List<EngineValueType>>,
                   val resultType : Func<EngineValueType>,
                   val statements : Coll<Statement>,
                   val resultStatement : Func<Statement>) : Model
{

    companion object : Factory<Program>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<Program>  = when (doc)
        {
            is DocDict ->
            {
                effApply(::Program,
                         // Model Id
                         valueResult(UUID.randomUUID()),
                         // Program Id
                         doc.at("program_id") ap {
                             effApply(::Prim, ProgramId.fromDocument(it))
                         },
                         // Label
                         split(doc.maybeAt("label"),
                               valueResult<Func<ProgramLabel>>(Null()),
                               fun(d : SpecDoc) : ValueParser<Func<ProgramLabel>> =
                                       effApply(::Prim, ProgramLabel.fromDocument(d))),
                         // Description
                         split(doc.maybeAt("description"),
                               valueResult<Func<ProgramDescription>>(Null()),
                               fun(d : SpecDoc) : ValueParser<Func<ProgramDescription>> =
                                       effApply(::Prim, ProgramDescription.fromDocument(d))),
                         // Parameter Types
                         doc.list("parameter_types") ap { docList ->
                             effApply(::Prim, docList.enumList<EngineValueType>())
                         },
                         // Result Type
                         effApply(::Prim, doc.enum<EngineValueType>("result_type")),
                         // Statements
                         doc.list("statements") ap { docList ->
                             effApply(::Coll, docList.map { Statement.fromDocument(it) })
                         },
                         doc.at("result_statement") ap {
                             effApply(::Comp, Statement.fromDocument(it) )
                         })
            }
            else       -> Err(UnexpectedType(DocType.DICT, docType(doc)), doc.path)
        }
    }

    override fun onLoad() { }

}



/**
 * Program Name
 */
data class ProgramId(val value : String)
{

    companion object : Factory<ProgramId>
    {
        override fun fromDocument(doc: SpecDoc) : ValueParser<ProgramId> = when (doc)
        {
            is DocText -> valueResult(ProgramId(doc.text))
            else       -> Err(UnexpectedType(DocType.TEXT, docType(doc)), doc.path)
        }
    }
}



/**
 * Program Label
 */
data class ProgramLabel(val value : String)
{

    companion object : Factory<ProgramLabel>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<ProgramLabel> = when (doc)
        {
            is DocText -> valueResult(ProgramLabel(doc.text))
            else       -> Err(UnexpectedType(DocType.TEXT, docType(doc)), doc.path)
        }
    }
}


/**
 * Program Description
 */
data class ProgramDescription(val value : String)
{

    companion object : Factory<ProgramDescription>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<ProgramDescription> = when (doc)
        {
            is DocText -> valueResult(ProgramDescription(doc.text))
            else       -> Err(UnexpectedType(DocType.TEXT, docType(doc)), doc.path)
        }
    }
}

//
//
//
//    // INTERNAL
//    // ------------------------------------------------------------------------------------------
//
//    // > Initialize
//    // ------------------------------------------------------------------------------------------
//
//    private void initializeFunctors()
//    {
//        // Name
//        this.name.setName("name");
//        this.name.setLabelId(R.string.program_field_name_label);
//        this.name.setDescriptionId(R.string.program_field_name_description);
//
//        // Label
//        this.label.setName("label");
//        this.label.setLabelId(R.string.program_field_label_label);
//        this.label.setDescriptionId(R.string.program_field_label_description);
//
//        // Description
//        this.description.setName("description");
//        this.description.setLabelId(R.string.program_field_description_label);
//        this.description.setDescriptionId(R.string.program_field_description_description);
//
//        // Parameter Types
//        this.parameterTypes.setName("parameter_types");
//        this.parameterTypes.setLabelId(R.string.function_field_parameter_types_label);
//        this.parameterTypes.setDescriptionId(R.string.function_field_parameter_types_description);
//
//        // Result Type
//        this.resultType.setName("result_type");
//        this.resultType.setLabelId(R.string.function_field_result_type_label);
//        this.resultType.setDescriptionId(R.string.function_field_result_type_description);
//    }

