
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
                   val resultBindingName : Prim<StatementBindingName>)
                    : ToDocument, Model, Serializable
{

    // -----------------------------------------------------------------------------------------
    // INITIALIZATION
    // -----------------------------------------------------------------------------------------

    init
    {
        this.programId.name         = "program_id"
        this.label.name             = "labelString"
        this.description.name       = "description"
        this.typeSignature.name     = "type_signature"
        this.statements.name        = "statements"
        this.resultBindingName.name     = "result_binding"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(programId : ProgramId,
                label : ProgramLabel,
                description : ProgramDescription,
                typeSignature : ProgramTypeSignature,
                statements : MutableList<Statement>,
                resultBindingName: StatementBindingName)
        : this(UUID.randomUUID(),
               Prim(programId),
               Prim(label),
               Prim(description),
               Comp(typeSignature),
               Coll(statements),
               Prim(resultBindingName))


    companion object : Factory<Program>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<Program> = when (doc)
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
                         split(doc.maybeList("statements"),
                               effValue(mutableListOf()),
                               { it.mapMut { Statement.fromDocument(it) } }),
                         doc.at("result_binding_name") ap { StatementBindingName.fromDocument(it) })
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "program_id" to this.programId().toDocument(),
        "label" to this.label().toDocument(),
        "description" to this.description().toDocument(),
        "type_signature" to this.typeSignature().toDocument(),
        "statements" to DocList(this.statements().map { it.toDocument() }),
        "result_binding_name" to this.resultBindingName().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun programId() : ProgramId = this.programId.value

    fun label() : ProgramLabel = this.label.value

    fun labelString() : String = this.label.value.value

    fun description() : ProgramDescription = this.description.value

    fun descriptionString() : String = this.description.value.value

    fun typeSignature() : ProgramTypeSignature = this.typeSignature.value

    fun statements() : List<Statement> = this.statements.list

    fun resultBindingName() : StatementBindingName = this.resultBindingName.value

    fun resultBindingNameString() : String = this.resultBindingName.value.value


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
                                val resultType : Prim<EngineValueType>)
                                : ToDocument, Model, Serializable
{

    // -----------------------------------------------------------------------------------------
    // INITIALIZATION
    // -----------------------------------------------------------------------------------------

    init
    {
        this.parameter1Type.name = "parameter1_type"

        when (this.parameter2Type) {
            is Just -> this.parameter2Type.value.name = "parameter2_type"
        }

        when (this.parameter3Type) {
            is Just -> this.parameter3Type.value.name = "parameter3_type"
        }

        when (this.parameter4Type) {
            is Just -> this.parameter4Type.value.name = "parameter4_type"
        }

        when (this.parameter5Type) {
            is Just -> this.parameter5Type.value.name = "parameter5_type"
        }

        this.resultType.name                          = "result_type"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(parameter1Type : EngineValueType,
                parameter2Type : Maybe<EngineValueType>,
                parameter3Type : Maybe<EngineValueType>,
                parameter4Type : Maybe<EngineValueType>,
                parameter5Type : Maybe<EngineValueType>,
                resultType : EngineValueType)
        : this(UUID.randomUUID(),
               Prim(parameter1Type),
               maybeLiftPrim(parameter2Type),
               maybeLiftPrim(parameter3Type),
               maybeLiftPrim(parameter4Type),
               maybeLiftPrim(parameter5Type),
               Prim(resultType))


    companion object : Factory<ProgramTypeSignature>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<ProgramTypeSignature> = when (doc)
        {
            is DocDict ->
            {
                effApply(::ProgramTypeSignature,
                         // Parameter 1 Type
                         doc.at("parameter1_type") ap { EngineValueType.fromDocument(it) },
                         // Parameter 2 Type
                         split(doc.maybeAt("parameter2_type"),
                               effValue<ValueError,Maybe<EngineValueType>>(Nothing()),
                               { effApply(::Just, EngineValueType.fromDocument(it)) }),
                         // Parameter 3 Type
                         split(doc.maybeAt("parameter3_type"),
                               effValue<ValueError,Maybe<EngineValueType>>(Nothing()),
                               { effApply(::Just, EngineValueType.fromDocument(it)) }),
                         // Parameter 4 Type
                         split(doc.maybeAt("parameter4_type"),
                               effValue<ValueError,Maybe<EngineValueType>>(Nothing()),
                               { effApply(::Just, EngineValueType.fromDocument(it)) }),
                         // Parameter 5 Type
                         split(doc.maybeAt("parameter5_type"),
                               effValue<ValueError,Maybe<EngineValueType>>(Nothing()),
                               { effApply(::Just, EngineValueType.fromDocument(it)) }),
                         // Result Type
                         doc.at("result_type") ap { EngineValueType.fromDocument(it) }
                         )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }

    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
            "parameter1_type" to this.parameter1Type().toDocument(),
            "result_type" to this.resultType().toDocument()
    ))
    .maybeMerge(this.parameter2Type().apply {
        Just(Pair("parameter2_type", it.toDocument())) })
    .maybeMerge(this.parameter3Type().apply {
        Just(Pair("parameter3_type", it.toDocument())) })
    .maybeMerge(this.parameter4Type().apply {
        Just(Pair("parameter4_type", it.toDocument())) })
    .maybeMerge(this.parameter5Type().apply {
        Just(Pair("parameter5_type", it.toDocument())) })


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun parameter1Type() : EngineValueType = this.parameter1Type.value

    fun parameter2Type() : Maybe<EngineValueType> = _getMaybePrim(this.parameter2Type)

    fun parameter3Type() : Maybe<EngineValueType> = _getMaybePrim(this.parameter3Type)

    fun parameter4Type() : Maybe<EngineValueType> = _getMaybePrim(this.parameter4Type)

    fun parameter5Type() : Maybe<EngineValueType> = _getMaybePrim(this.parameter5Type)

    fun resultType() : EngineValueType = this.resultType.value


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }

    override val name = "program_type_signature"

    override val modelObject = this

}


/**
 * Program Id
 */
data class ProgramId(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ProgramId>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<ProgramId> = when (doc)

        {
            is DocText -> effValue(ProgramId(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocText(this.value)


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value})

}


/**
 * Program Label
 */
data class ProgramLabel(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ProgramLabel>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<ProgramLabel> = when (doc)
        {
            is DocText -> effValue(ProgramLabel(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocText(this.value)


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value})

}


/**
 * Program Description
 */
data class ProgramDescription(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ProgramDescription>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<ProgramDescription> = when (doc)
        {
            is DocText -> effValue(ProgramDescription(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocText(this.value)


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value})

}


/**
 * Program Parameter
 */
data class ProgramParameterIndex(val value : Int) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ProgramParameterIndex>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<ProgramParameterIndex> = when (doc)
        {
            is DocNumber -> effValue(ProgramParameterIndex(doc.number.toInt()))
            else         -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocNumber(this.value.toDouble())


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLInt({this.value})

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

