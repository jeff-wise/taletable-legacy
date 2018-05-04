
package com.kispoko.tome.model.engine.program


import com.kispoko.tome.db.*
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.orm.ProdType
import com.kispoko.tome.lib.orm.RowValue4
import com.kispoko.tome.lib.orm.RowValue5
import com.kispoko.tome.lib.orm.schema.MaybePrimValue
import com.kispoko.tome.lib.orm.schema.MaybeSumValue
import com.kispoko.tome.lib.orm.schema.PrimValue
import com.kispoko.tome.lib.orm.schema.ProdValue
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.lib.orm.sql.asSQLValue
import com.kispoko.tome.model.engine.EngineValue
import com.kispoko.tome.model.engine.EngineValueBoolean
import com.kispoko.tome.model.engine.EngineValueNumber
import com.kispoko.tome.model.engine.EngineValueText
import com.kispoko.tome.model.engine.message.Message
import com.kispoko.tome.model.engine.constraint.ConstraintNumber
import effect.apply
import effect.effError
import effect.effValue
import effect.split
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.UnknownCase
import lulo.value.ValueError
import lulo.value.ValueParser
import maybe.Just
import maybe.Maybe
import maybe.Nothing
import java.io.Serializable
import java.util.*



/**
 * Program Parameter
 */
sealed class ProgramParameter(open val name : ProgramParameterName,
                              open val label : ProgramParameterLabel,
                              open val inputMessage : Message)
                               : ToDocument, ProdType, java.io.Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ProgramParameter>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<ProgramParameter> =
            when (doc.case())
            {
                "program_parameter_boolean" -> ProgramParameterBoolean.fromDocument(doc) as ValueParser<ProgramParameter>
                "program_parameter_number"  -> ProgramParameterNumber.fromDocument(doc) as ValueParser<ProgramParameter>
                "program_parameter_text"    -> ProgramParameterText.fromDocument(doc) as ValueParser<ProgramParameter>
                else                        -> effError(UnknownCase(doc.case(), doc.path))
            }
    }


    // -----------------------------------------------------------------------------------------
    // API
    // -----------------------------------------------------------------------------------------

    fun name() : ProgramParameterName = this.name


    fun label() : ProgramParameterLabel = this.label


    fun inputMessage() : Message = this.inputMessage


    abstract fun defaultValueString() : String


    fun parameterDefaultValue() : Maybe<EngineValue> = when (this)
    {
        is ProgramParameterBoolean -> this.defaultValue() as Maybe<EngineValue>
        is ProgramParameterNumber  -> this.defaultValue() as Maybe<EngineValue>
        is ProgramParameterText    -> this.defaultValue() as Maybe<EngineValue>
    }

}


/**
 * Program Parameter Name
 */
data class ProgramParameterName(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ProgramParameterName>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<ProgramParameterName> = when (doc)
        {
            is DocText -> effValue(ProgramParameterName(doc.text))
            else       -> effError(UnexpectedType(DocType.NUMBER, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocText(this.value)


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() = this.value.asSQLValue()


    // -----------------------------------------------------------------------------------------
    // TO STRING
    // -----------------------------------------------------------------------------------------

    override fun toString() = this.value


}


/**
 * Program Parameter Label
 */
data class ProgramParameterLabel(val value : String) : ToDocument, SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<ProgramParameterLabel>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<ProgramParameterLabel> = when (doc)
        {
            is DocText -> effValue(ProgramParameterLabel(doc.text))
            else       -> effError(lulo.value.UnexpectedType(DocType.TEXT, docType(doc), doc.path))
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



data class ProgramParameterBoolean(override val id : UUID,
                                   override val name : ProgramParameterName,
                                   val defaultValue : Maybe<EngineValueBoolean>,
                                   override val label : ProgramParameterLabel,
                                   override val inputMessage : Message)
                                    : ProgramParameter(name, label, inputMessage)
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(name : ProgramParameterName,
                defaultValue : Maybe<EngineValueBoolean>,
                label : ProgramParameterLabel,
                inputMessage : Message)
        : this(UUID.randomUUID(),
               name,
               defaultValue,
               label,
               inputMessage)


    companion object : Factory<ProgramParameterBoolean>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<ProgramParameterBoolean> = when (doc)
        {
            is DocDict ->
            {
                apply(::ProgramParameterBoolean,
                      // Name
                      doc.at("name") ap { ProgramParameterName.fromDocument(it) },
                      // Default Value
                      split(doc.maybeAt("default_value"),
                            effValue<ValueError,Maybe<EngineValueBoolean>>(Nothing()),
                            { apply(::Just, EngineValueBoolean.fromDocument(it)) }),
                      // Label
                      doc.at("label") ap { ProgramParameterLabel.fromDocument(it) },
                      // Input Message
                      doc.at("input_message") ap { Message.fromDocument(it) }
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }



    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "name" to this.name().toDocument(),
        "label" to this.label().toDocument(),
        "input_message" to this.inputMessage().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun defaultValue() : Maybe<EngineValueBoolean> = this.defaultValue


    // -----------------------------------------------------------------------------------------
    // DEFAULT VALUE
    // -----------------------------------------------------------------------------------------

    override fun defaultValueString() : String = when (this.defaultValue) {
        is Just -> this.defaultValue.value.toString()
        is Nothing -> ""
    }


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_ProgramParameterBooleanValue =
        RowValue4(programParameterBooleanTable,
                  PrimValue(this.name),
                  MaybePrimValue(this.defaultValue),
                  PrimValue(this.label),
                  ProdValue(this.inputMessage))

}



data class ProgramParameterNumber(override val id : UUID,
                                  override val name : ProgramParameterName,
                                  val defaultValue : Maybe<EngineValueNumber>,
                                  val constraint : Maybe<ConstraintNumber>,
                                  override val label : ProgramParameterLabel,
                                  override val inputMessage : Message)
                                   : ProgramParameter(name, label, inputMessage)
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(name : ProgramParameterName,
                defaultValue : Maybe<EngineValueNumber>,
                constraint : Maybe<ConstraintNumber>,
                label : ProgramParameterLabel,
                inputMessage : Message)
        : this(UUID.randomUUID(),
               name,
               defaultValue,
               constraint,
               label,
               inputMessage)


    companion object : Factory<ProgramParameterNumber>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<ProgramParameterNumber> = when (doc)
        {
            is DocDict ->
            {
                apply(::ProgramParameterNumber,
                      // Name
                      doc.at("name") ap { ProgramParameterName.fromDocument(it) },
                      // Default Value
                      split(doc.maybeAt("default_value"),
                            effValue<ValueError,Maybe<EngineValueNumber>>(Nothing()),
                            { apply(::Just, EngineValueNumber.fromDocument(it)) }),
                      // Constraint
                      split(doc.maybeAt("constraint"),
                            effValue<ValueError,Maybe<ConstraintNumber>>(Nothing()),
                            { apply(::Just, ConstraintNumber.fromDocument(it)) }),
                      // Label
                      doc.at("label") ap { ProgramParameterLabel.fromDocument(it) },
                      // Input Message
                      doc.at("input_message") ap { Message.fromDocument(it) }
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }



    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "name" to this.name().toDocument(),
        "label" to this.label().toDocument(),
        "input_message" to this.inputMessage().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun defaultValue() : Maybe<EngineValueNumber> = this.defaultValue


    fun constraint() : Maybe<ConstraintNumber> = this.constraint


    // -----------------------------------------------------------------------------------------
    // DEFAULT VALUE
    // -----------------------------------------------------------------------------------------

    override fun defaultValueString() : String = when (this.defaultValue) {
        is Just -> this.defaultValue.value.toString()
        is Nothing -> ""
    }


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_ProgramParameterNumberValue =
        RowValue4(programParameterNumberTable,
                  PrimValue(this.name),
                  MaybePrimValue(this.defaultValue),
                  PrimValue(this.label),
                  ProdValue(this.inputMessage))

}



data class ProgramParameterText(override val id : UUID,
                                override val name : ProgramParameterName,
                                val defaultValue : Maybe<EngineValueText>,
                                override val label : ProgramParameterLabel,
                                override val inputMessage : Message)
                                : ProgramParameter(name, label, inputMessage)
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(name : ProgramParameterName,
                defaultValue : Maybe<EngineValueText>,
                label : ProgramParameterLabel,
                inputMessage : Message)
        : this(UUID.randomUUID(),
               name,
               defaultValue,
               label,
               inputMessage)


    companion object : Factory<ProgramParameterText>
    {
        override fun fromDocument(doc : SchemaDoc) : ValueParser<ProgramParameterText> = when (doc)
        {
            is DocDict ->
            {
                apply(::ProgramParameterText,
                      // Name
                      doc.at("name") ap { ProgramParameterName.fromDocument(it) },
                      // Default Value
                      split(doc.maybeAt("default_value"),
                            effValue<ValueError,Maybe<EngineValueText>>(Nothing()),
                            { apply(::Just, EngineValueText.fromDocument(it)) }),
                      // Label
                      doc.at("label") ap { ProgramParameterLabel.fromDocument(it) },
                      // Input Message
                      doc.at("input_message") ap { Message.fromDocument(it) }
                      )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }



    // -----------------------------------------------------------------------------------------
    // TO DOCUMENT
    // -----------------------------------------------------------------------------------------

    override fun toDocument() = DocDict(mapOf(
        "name" to this.name().toDocument(),
        "label" to this.label().toDocument(),
        "input_message" to this.inputMessage().toDocument()
    ))


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun defaultValue() : Maybe<EngineValueText> = this.defaultValue


    // -----------------------------------------------------------------------------------------
    // DEFAULT VALUE
    // -----------------------------------------------------------------------------------------

    override fun defaultValueString() : String = when (this.defaultValue) {
        is Just -> this.defaultValue.value.toString()
        is Nothing -> ""
    }


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() { }


    override val prodTypeObject = this


    override fun rowValue() : DB_ProgramParameterTextValue =
        RowValue4(programParameterTextTable,
                  PrimValue(this.name),
                  MaybePrimValue(this.defaultValue),
                  PrimValue(this.label),
                  ProdValue(this.inputMessage))

}
