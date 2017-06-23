
package com.kispoko.tome.model.game.engine.program


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.*
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.lib.orm.sql.SQLInt
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.model.game.engine.EngineValueType
import effect.*
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.ValueError
import lulo.value.ValueParser
import java.io.Serializable
import java.util.*



/**
 * Program
 */
data class Program(override val id : UUID,
                   val programId : Prim<ProgramId>,
                   val label : Prim<ProgramLabel>,
                   val description : Prim<ProgramDescription>,
                   val typeSignature : Comp<ProgramTypeSignature>,
                   val statements : Coll<Statement>,
                   val resultStatement : Comp<Statement>) : Model
{

    // -----------------------------------------------------------------------------------------
    // INITIALIZATION
    // -----------------------------------------------------------------------------------------

    init
    {
        this.programId.name         = "program_id"
        this.label.name             = "label"
        this.description.name       = "description"
        this.typeSignature.name     = "type_signature"
        this.statements.name        = "statements"
        this.resultStatement.name   = "result_statement"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(programId : ProgramId,
                label : ProgramLabel,
                description : ProgramDescription,
                typeSignature : ProgramTypeSignature,
                statements : MutableList<Statement>,
                resultStatement : Statement)
        : this(UUID.randomUUID(),
               Prim(programId),
               Prim(label),
               Prim(description),
               Comp(typeSignature),
               Coll(statements),
               Comp(resultStatement))


    companion object : Factory<Program>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<Program>  = when (doc)
        {
            is DocDict ->
            {
                effApply(::Program,
                         // Program Id
                         doc.at("program_id") ap { ProgramId.fromDocument(it) },
                         // Label
                         doc.at("label") ap { ProgramLabel.fromDocument(it) },
                         // Description
                         doc.at("description") ap { ProgramDescription.fromDocument(it) },
                         // Type Signature
                         doc.at("type_signature") ap { ProgramTypeSignature.fromDocument(it) },
                         // Statements
                         doc.list("statements") ap { docList ->
                             docList.mapMut { Statement.fromDocument(it) }
                         },
                         doc.at("result_statement") ap { Statement.fromDocument(it) })
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name = "program"

    override val modelObject = this

}


/**
 * Program Type Signature
 */
data class ProgramTypeSignature(override val id : UUID,
                                val parameter1Type : Prim<EngineValueType>,
                                val parameter2Type : Maybe<Prim<EngineValueType>>,
                                val parameter3Type : Maybe<Prim<EngineValueType>>,
                                val parameter4Type : Maybe<Prim<EngineValueType>>,
                                val parameter5Type : Maybe<Prim<EngineValueType>>,
                                val resultType : Prim<EngineValueType>,
                                val arity : Prim<ProgramArity>)
                                : Model, Serializable
{

    // -----------------------------------------------------------------------------------------
    // INITIALIZATION
    // -----------------------------------------------------------------------------------------

    init
    {
        this.parameter1Type.name = "parameter_1_type"

        when (this.parameter2Type) {
            is Just -> this.parameter2Type.value.name = "parameter_2_type"
        }

        when (this.parameter3Type) {
            is Just -> this.parameter3Type.value.name = "parameter_3_type"
        }

        when (this.parameter4Type) {
            is Just -> this.parameter4Type.value.name = "parameter_4_type"
        }

        when (this.parameter5Type) {
            is Just -> this.parameter5Type.value.name = "parameter_5_type"
        }

        this.resultType.name     = "result_type"
        this.arity.name          = "arity"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(parameter1Type : EngineValueType,
                parameter2Type : Maybe<EngineValueType>,
                parameter3Type : Maybe<EngineValueType>,
                parameter4Type : Maybe<EngineValueType>,
                parameter5Type : Maybe<EngineValueType>,
                resultType : EngineValueType,
                arity : ProgramArity)
        : this(UUID.randomUUID(),
               Prim(parameter1Type),
               maybeLiftPrim(parameter2Type),
               maybeLiftPrim(parameter3Type),
               maybeLiftPrim(parameter4Type),
               maybeLiftPrim(parameter5Type),
               Prim(resultType),
               Prim(arity))


    companion object : Factory<ProgramTypeSignature>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<ProgramTypeSignature> = when (doc)
        {
            is DocDict ->
            {
                effApply(::ProgramTypeSignature,
                         // Parameter 1 Type
                         doc.at("parameter_1_type") ap { EngineValueType.fromDocument(it) },
                         // Parameter 2 Type
                         split(doc.maybeAt("parameter_2_type"),
                               effValue<ValueError,Maybe<EngineValueType>>(Nothing()),
                               { effApply(::Just, EngineValueType.fromDocument(it)) }),
                         // Parameter 3 Type
                         split(doc.maybeAt("parameter_3_type"),
                               effValue<ValueError,Maybe<EngineValueType>>(Nothing()),
                               { effApply(::Just, EngineValueType.fromDocument(it)) }),
                         // Parameter 4 Type
                         split(doc.maybeAt("parameter_4_type"),
                               effValue<ValueError,Maybe<EngineValueType>>(Nothing()),
                               { effApply(::Just, EngineValueType.fromDocument(it)) }),
                         // Parameter 5 Type
                         split(doc.maybeAt("parameter_5_type"),
                               effValue<ValueError,Maybe<EngineValueType>>(Nothing()),
                               { effApply(::Just, EngineValueType.fromDocument(it)) }),
                         // Result Type
                         doc.at("result_type") ap { EngineValueType.fromDocument(it) },
                         // Arity
                         doc.at("arity") ap { ProgramArity.fromDocument(it) }
                         )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }

    }


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name = "program_type_signature"

    override val modelObject = this

}


/**
 * Program Arity
 */
data class ProgramArity(val value : Int) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ProgramArity>
    {
        override fun fromDocument(doc: SpecDoc) : ValueParser<ProgramArity> = when (doc)
        {
            is DocNumber -> effValue(ProgramArity(doc.number.toInt()))
            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
        }
    }

    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLInt({this.value})

}


/**
 * Program Id
 */
data class ProgramId(val value : String) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ProgramId>
    {
        override fun fromDocument(doc: SpecDoc) : ValueParser<ProgramId> = when (doc)

        {
            is DocText -> effValue(ProgramId(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value})

}



/**
 * Program Label
 */
data class ProgramLabel(val value : String) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ProgramLabel>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<ProgramLabel> = when (doc)
        {
            is DocText -> effValue(ProgramLabel(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }

    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value})

}


/**
 * Program Description
 */
data class ProgramDescription(val value : String) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ProgramDescription>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<ProgramDescription> = when (doc)
        {
            is DocText -> effValue(ProgramDescription(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value})

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

