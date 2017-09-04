
package com.kispoko.tome.model.game.engine.variable


import com.kispoko.tome.app.AppEff
import com.kispoko.tome.app.AppError
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Comp
import com.kispoko.tome.lib.functor.Prim
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.lib.model.SumModel
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.model.game.engine.program.Invocation
import com.kispoko.tome.model.game.engine.value.ValueReference
import com.kispoko.tome.model.game.engine.value.ValueSetId
import com.kispoko.tome.rts.game.GameManager
import com.kispoko.tome.rts.sheet.SheetContext
import effect.*
import lulo.document.*
import lulo.value.*
import lulo.value.UnexpectedType
import java.io.Serializable



/**
 * Text Variable Value
 */
sealed class TextVariableValue : SumModel, Serializable
{

    companion object : Factory<TextVariableValue>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<TextVariableValue> =
            when (doc.case())
            {
                "text_literal"       -> TextVariableLiteralValue.fromDocument(doc)
                "value_reference"    -> TextVariableValueValue.fromDocument(doc)
                "program_invocation" -> TextVariableProgramValue.fromDocument(doc)
                "value_set_id"       -> TextVariableValueUnknownValue.fromDocument(doc)
                else                 -> effError<ValueError,TextVariableValue>(
                                            UnknownCase(doc.case(), doc.path))
            }
    }


    // -----------------------------------------------------------------------------------------
    // Dependencies
    // -----------------------------------------------------------------------------------------

    open fun dependencies() : Set<VariableReference> = setOf()


    // -----------------------------------------------------------------------------------------
    // Value
    // -----------------------------------------------------------------------------------------

    abstract fun value(sheetContext : SheetContext) : AppEff<Maybe<String>>


    abstract fun companionVariables(sheetContext : SheetContext) : AppEff<Set<Variable>>

}


/**
 * Literal Value
 */
data class TextVariableLiteralValue(val value : String) : TextVariableValue(), SQLSerializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<TextVariableValue>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<TextVariableValue> = when (doc)
        {
            is DocText -> effValue(TextVariableLiteralValue(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // Value
    // -----------------------------------------------------------------------------------------

    override fun value(sheetContext : SheetContext) : AppEff<Maybe<String>> =
            effValue(Just(this.value))


    override fun companionVariables(sheetContext : SheetContext) : AppEff<Set<Variable>> =
        effValue(setOf())


    // -----------------------------------------------------------------------------------------
    // SUM MODEL
    // -----------------------------------------------------------------------------------------

    override fun functor() = Prim(this, "literal")

    override val sumModelObject = this


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({ this.value })

}


/**
 * Unknown Literal Value
 */
class TextVariableUnknownLiteralValue() : TextVariableValue(), SQLSerializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<TextVariableValue>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<TextVariableValue> = when (doc)
        {
            is DocText -> when (doc.text)
            {
                "unknown_literal_value" -> effValue<ValueError,TextVariableValue>(
                                                TextVariableUnknownLiteralValue())
                else                    -> effError<ValueError,TextVariableValue>(
                                                UnexpectedValue("TextVariableUnknownLiteralValue",
                                                                doc.text,
                                                                doc.path))
            }
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // Value
    // -----------------------------------------------------------------------------------------

    override fun value(sheetContext : SheetContext) : AppEff<Maybe<String>> = effValue(Nothing())


    override fun companionVariables(sheetContext : SheetContext) : AppEff<Set<Variable>> =
        effValue(setOf())


    // -----------------------------------------------------------------------------------------
    // SUM MODEL
    // -----------------------------------------------------------------------------------------

    override fun functor() = Prim(this, "literal")

    override val sumModelObject = this


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({ "unknown_literal_value" })

}


data class TextVariableValueValue(val valueReference : ValueReference)
            : TextVariableValue(), SQLSerializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<TextVariableValue>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<TextVariableValue> =
                effApply(::TextVariableValueValue, ValueReference.fromDocument(doc))
    }


    // -----------------------------------------------------------------------------------------
    // Value
    // -----------------------------------------------------------------------------------------

    override fun value(sheetContext : SheetContext) : AppEff<Maybe<String>> =
        GameManager.engine(sheetContext.gameId)
                   .apply { it.textValue(this.valueReference, sheetContext) }
                   .apply { effValue<AppError,Maybe<String>>(Just(it.value())) }


    override fun companionVariables(sheetContext : SheetContext) : AppEff<Set<Variable>> =
        GameManager.engine(sheetContext.gameId)
                   .apply { it.value(this.valueReference, sheetContext) }
                   .apply { effValue<AppError,Set<Variable>>(it.variables()) }


    // -----------------------------------------------------------------------------------------
    // SUM MODEL
    // -----------------------------------------------------------------------------------------

    override fun functor() = Prim(this, "value")

    override val sumModelObject = this


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = this.valueReference.asSQLValue()

}


data class TextVariableValueUnknownValue(val valueSetId : ValueSetId)
            : TextVariableValue(), SQLSerializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<TextVariableValue>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<TextVariableValue> =
                effApply(::TextVariableValueUnknownValue, ValueSetId.fromDocument(doc))
    }


    // -----------------------------------------------------------------------------------------
    // Value
    // -----------------------------------------------------------------------------------------

    override fun value(sheetContext : SheetContext) : AppEff<Maybe<String>> = effValue(Nothing())


    override fun companionVariables(sheetContext : SheetContext) : AppEff<Set<Variable>> =
        effValue(setOf())



    // -----------------------------------------------------------------------------------------
    // SUM MODEL
    // -----------------------------------------------------------------------------------------

    override fun functor() = Prim(this, "value")

    override val sumModelObject = this


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = this.valueSetId.asSQLValue()

}


data class TextVariableProgramValue(val invocation : Invocation) : TextVariableValue(), Model
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<TextVariableValue>
    {
        override fun fromDocument(doc: SchemaDoc): ValueParser<TextVariableValue> =
                effApply(::TextVariableProgramValue, Invocation.fromDocument(doc))
    }


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    override fun dependencies() : Set<VariableReference> = this.invocation.dependencies()


    override fun value(sheetContext : SheetContext) = TODO("Not Implemented")


    override fun companionVariables(sheetContext : SheetContext) : AppEff<Set<Variable>> =
            effValue(setOf())


    // -----------------------------------------------------------------------------------------
    // SUM MODEL
    // -----------------------------------------------------------------------------------------

    override fun functor() = Comp(this, "program")

    override val sumModelObject = this


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() = this.invocation.onLoad()

    override val id = this.invocation.id

    override val name = this.invocation.name

    override val modelObject : Model = this.invocation


}



//    // INTERNAL
//    // ------------------------------------------------------------------------------------------
//
//    // ** Initialize
//    // ------------------------------------------------------------------------------------------
//
//    private void initializeTextVariable()
//    {
//        // [1] Create reaction value (if program variable)
//        // --------------------------------------------------------------------------------------
//
//        if (this.kind.getValue() == Kind.PROGRAM) {
//            this.reactiveValue = new ReactiveValue<>(this.invocation.getValue(),
//                                                     VariableType.TEXT);
//        }
//        else {
//            this.reactiveValue = null;
//        }
//    }
//
//
//    // ** Variable State
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * Add any variables associated with the current value to the state.
//     */
//    private void addToState()
//    {
//        if (this.kind() != Kind.VALUE)
//            return;
//
//        Dictionary dictionary = SheetManagerOld.dictionary();
//        if (dictionary != null)
//        {
//            TextValue textValue = dictionary.textValue(this.valueReference());
//            if (textValue != null)
//                textValue.addToState();
//        }
//    }
//
//
//    private void removeFromState()
//    {
//        if (this.kind() != Kind.VALUE)
//            return;
//
//        Dictionary dictionary = SheetManagerOld.currentSheet().engine().dictionary();
//        dictionary.textValue(this.valueReference()).removeFromState();
//    }
//
//}
