
package com.kispoko.tome.model.game.engine.variable


import com.kispoko.tome.app.AppEff
import com.kispoko.tome.app.AppError
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.Comp
import com.kispoko.tome.lib.functor.Func
import com.kispoko.tome.lib.functor.Prim
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.lib.orm.sql.SQLSerializable
import com.kispoko.tome.lib.orm.sql.SQLText
import com.kispoko.tome.lib.orm.sql.SQLValue
import com.kispoko.tome.model.game.engine.program.Invocation
import com.kispoko.tome.model.game.engine.value.ValueReference
import com.kispoko.tome.rts.game.GameManager
import com.kispoko.tome.rts.sheet.SheetContext
import effect.effApply
import effect.effError
import effect.effValue
import lulo.document.*
import lulo.value.*
import lulo.value.UnexpectedType
import java.io.Serializable



/**
 * Text Variable Value
 */
sealed class TextVariableValue : Serializable
{

    companion object : Factory<TextVariableValue>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<TextVariableValue> =
            when (doc.case())
            {
                "text_literal"       -> TextVariableLiteralValue.fromDocument(doc)
                "value_reference"    -> TextVariableValueValue.fromDocument(doc)
                "program_invocation" -> TextVariableProgramValue.fromDocument(doc)
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

    abstract fun value(sheetContext : SheetContext) : AppEff<String>

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
        override fun fromDocument(doc : SpecDoc)
                      : ValueParser<TextVariableValue> = when (doc)
        {
            is DocText -> effValue(TextVariableLiteralValue(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // Value
    // -----------------------------------------------------------------------------------------

    override fun value(sheetContext : SheetContext) : AppEff<String> = effValue(this.value)


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({ this.value })

}


data class TextVariableValueValue(val valueReference : ValueReference)
            : TextVariableValue(), SQLSerializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<TextVariableValue>
    {
        override fun fromDocument(doc : SpecDoc)
                      : ValueParser<TextVariableValue> =
                effApply(::TextVariableValueValue, ValueReference.fromDocument(doc))
    }


    // -----------------------------------------------------------------------------------------
    // Value
    // -----------------------------------------------------------------------------------------

    override fun value(sheetContext : SheetContext) : AppEff<String> =
        GameManager.engine(sheetContext.gameId)
                   .apply { it.textValue(this.valueReference) }
                   .apply { effValue<AppError,String>(it.value()) }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = this.valueReference.asSQLValue()

}


data class TextVariableProgramValue(val invocation : Invocation) : TextVariableValue(), Model
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<TextVariableValue>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<TextVariableValue> =
                effApply(::TextVariableProgramValue, Invocation.fromDocument(doc))
    }


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    override fun dependencies() : Set<VariableReference> = this.invocation.dependencies()

    override fun value(sheetContext : SheetContext) : AppEff<String> = TODO("Not Implemented")


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() = this.invocation.onLoad()

    override val id = this.invocation.id

    override val name = this.invocation.name

    override val modelObject : Model = this.invocation


}


fun liftTextVariableValue(varValue : TextVariableValue) : Func<TextVariableValue>
    = when (varValue)
    {
        is TextVariableLiteralValue -> Prim(varValue, "literal")
        is TextVariableValueValue   -> Prim(varValue, "value")
        is TextVariableProgramValue -> Comp(varValue, "program")
    }



//    @Override
//    public void setIsNamespaced(Boolean isNamespaced)
//    {
//        if (isNamespaced != null)
//            this.isNamespaced.setValue(isNamespaced);
//        else
//            this.isNamespaced.setValue(false);
//    }
//
//
//    // ** Defines Namespace
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * True if this text variable defines a namespace.
//     * @return Defines namespace?
//     */
//    public boolean definesNamespace()
//    {
//        return this.definesNamespace.getValue();
//    }
//
//
//    public void setDefinesNamespace(Boolean definesNamespace)
//    {
//        if (definesNamespace != null)
//            this.definesNamespace.setValue(definesNamespace);
//        else
//            this.definesNamespace.setValue(false);
//    }
//
//
//    // ** Identifier
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * The text variable identifier. This is roughly the same as the value, but in a more
//     * concise form.
//     * @return The variable identifier.
//     */
//    public Namespace namespace()
//           throws NullVariableException
//    {
//        switch (this.kind())
//        {
//            case LITERAL:
//                return new Namespace(this.value(), this.label());
//            case VALUE:
//                return new Namespace(this.valueReference().valueId(), this.label());
//            case PROGRAM:
//                return new Namespace(this.value(), this.label());
//            default:
//                return new Namespace(this.value(), this.label());
//        }
//    }
//
//
//    // ** Value
//    // ------------------------------------------------------------------------------------------
//
//    // ** Setters
//    // ------------------------------------------------------------------------------------------
//
//
//    /**
//     * Set the value for the value case.
//     * @param valueReference The value reference.
//     */
//    public void setValueReference(DataReference valueReference)
//    {
//        removeFromState();
//        this.valueReference.setValue(valueReference);
//        this.onUpdate();
//        addToState();
//    }
//
//
//    // ** String
//    // ------------------------------------------------------------------------------------------
//
//    public String value()
//           throws NullVariableException
//    {
//        // TODO make sure result isn't null. if so provide nullvariable exception
//        switch (this.kind.getValue())
//        {
//            case LITERAL:
//                return this.stringLiteral();
//            case VALUE:
//                Dictionary dictionary = SheetManagerOld.currentSheet().engine().dictionary();
//                TextValue textValue = dictionary.textValue(this.valueReference());
//                if (textValue != null)
//                    return textValue.value();
//                else
//                    throw new NullVariableException();
//            case PROGRAM:
//                return this.reactiveValue.value();
//        }
//
//        return null;
//    }
//
//
//    // ** Value Set
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * The name of the value set this text variable belongs to.
//     * @return The value set name.
//     */
//    public String valueSetId()
//    {
//        return this.valueSetId.getValue();
//    }
//
//
//    // > Null
//    // ------------------------------------------------------------------------------------------
//
//    public boolean isNull()
//    {
//        switch (this.kind.getValue())
//        {
//            case LITERAL:
//                return this.stringLiteral == null;
//            case VALUE:
//                return this.valueReference == null;
//            case PROGRAM:
//                return this.invocation == null;
//        }
//
//        return true;
//    }
//
//
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
