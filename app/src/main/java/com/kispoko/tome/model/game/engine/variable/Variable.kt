
package com.kispoko.tome.model.game.engine.variable


import com.kispoko.tome.app.AppEff
import com.kispoko.tome.app.AppStateError
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.*
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.model.game.engine.dice.DiceRoll
import com.kispoko.tome.model.sheet.SheetId
import com.kispoko.tome.rts.sheet.SheetContext
import com.kispoko.tome.rts.sheet.VariableIsOfUnexpectedType
import effect.*
import lulo.document.*
import lulo.value.*
import lulo.value.UnexpectedType
import java.io.Serializable
import java.util.*



/**
 * Variable
 */
@Suppress("UNCHECKED_CAST")
sealed class Variable(open val variableId : Prim<VariableId>,
                      open val label : Maybe<Prim<VariableLabel>>,
                      open val description : Maybe<Prim<VariableDescription>>,
                      open val tags : Prim<List<VariableTag>>) : Model, Serializable
{

    // -----------------------------------------------------------------------------------------
    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    var onUpdateListener : () -> Unit = fun() : Unit {}


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<Variable>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<Variable> = when (doc)
        {
            is DocDict ->
            {
                when (doc.case())
                {
                    "boolean"   -> BooleanVariable.fromDocument(doc) as ValueParser<Variable>
                    "dice_roll" -> DiceRollVariable.fromDocument(doc) as ValueParser<Variable>
                    "number"    -> NumberVariable.fromDocument(doc) as ValueParser<Variable>
                    "text"      -> TextVariable.fromDocument(doc) as ValueParser<Variable>
                    else        -> effError<ValueError, Variable>(
                                            UnknownCase(doc.case(), doc.path))
                }
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun variableId() : VariableId = this.variableId.value

    fun label() : VariableLabel? = getMaybePrim(this.label)

    fun description() : VariableDescription? = getMaybePrim(this.description)

    fun tags() : List<VariableTag> = this.tags.value


    // -----------------------------------------------------------------------------------------
    // VARIABLE
    // -----------------------------------------------------------------------------------------

    abstract fun dependencies() : Set<VariableReference>

    abstract fun type() : VariableType


    fun booleanVariable(sheetId : SheetId) : AppEff<BooleanVariable> = when (this)
    {
        is BooleanVariable -> effValue(this)
        else               -> effError(AppStateError(
                                    VariableIsOfUnexpectedType(sheetId,
                                                               this.variableId(),
                                                               VariableType.BOOLEAN,
                                                               this.type())))
    }


    fun diceRollVariable(sheetId : SheetId) : AppEff<DiceRollVariable> = when (this)
    {
        is DiceRollVariable -> effValue(this)
        else                -> effError(AppStateError(
                                    VariableIsOfUnexpectedType(sheetId,
                                                               this.variableId(),
                                                               VariableType.DICE_ROLL,
                                                               this.type())))
    }


    fun numberVariable(sheetId : SheetId) : AppEff<NumberVariable> = when (this)
    {
        is NumberVariable -> effValue(this)
        else              -> effError(AppStateError(
                                    VariableIsOfUnexpectedType(sheetId,
                                                               this.variableId(),
                                                               VariableType.NUMBER,
                                                               this.type())))
    }


    fun textVariable(sheetId : SheetId) : AppEff<TextVariable> = when (this)
    {
        is TextVariable -> effValue(this)
        else            -> effError(AppStateError(
                                    VariableIsOfUnexpectedType(sheetId,
                                                               this.variableId(),
                                                               VariableType.TEXT,
                                                               this.type())))
    }



    /**
     * This method is called when one of the variable's dependencies has been updated, and this
     * variable must therefore be udpated.
     */
    fun onUpdate()
    {
        this.onUpdateListener()
    }

}


/**
 * Boolean Variable
 */
data class BooleanVariable(override val id : UUID,
                           override val variableId : Prim<VariableId>,
                           override val label : Maybe<Prim<VariableLabel>>,
                           override val description : Maybe<Prim<VariableDescription>>,
                           override val tags : Prim<List<VariableTag>>,
                           val variableValue : Prim<BooleanVariableValue>)
                            : Variable(variableId, label, description, tags)
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(variableId : VariableId,
                label : Maybe<VariableLabel>,
                description : Maybe<VariableDescription>,
                tags : List<VariableTag>,
                value : BooleanVariableValue)
        : this(UUID.randomUUID(),
               Prim(variableId),
               maybeLiftPrim(label),
               maybeLiftPrim(description),
               Prim(tags),
               Prim(value))


    companion object : Factory<BooleanVariable>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<BooleanVariable> = when (doc)
        {
            is DocDict ->
            {
                effApply(::BooleanVariable,
                         // Variable Id
                         doc.at("id") ap { VariableId.fromDocument(it) },
                         // Label
                         split(doc.maybeAt("label"),
                               effValue<ValueError,Maybe<VariableLabel>>(Nothing()),
                               { effApply(::Just, VariableLabel.fromDocument(it)) }),
                         // Description
                         split(doc.maybeAt("description"),
                               effValue<ValueError,Maybe<VariableDescription>>(Nothing()),
                               { effApply(::Just, VariableDescription.fromDocument(it)) }),
                         // Tags
                         doc.list("tags") ap { docList ->
                             docList.map { VariableTag.fromDocument(it) }
                         },
                         // Value
                         doc.at("value") ap { BooleanVariableValue.fromDocument(it) }
                         )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun variableValue() : BooleanVariableValue = this.variableValue.value


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() {}


    // -----------------------------------------------------------------------------------------
    // VARIABLE
    // -----------------------------------------------------------------------------------------

    override fun type(): VariableType = VariableType.BOOLEAN

    override fun dependencies() : Set<VariableReference> = this.variableValue().dependencies()


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    fun value() : AppEff<Boolean> = this.variableValue().value()

}


/**
 * Dice Variable
 */
data class DiceRollVariable(override val id : UUID,
                            override val variableId : Prim<VariableId>,
                            override val label : Maybe<Prim<VariableLabel>>,
                            override val description : Maybe<Prim<VariableDescription>>,
                            override val tags : Prim<List<VariableTag>>,
                            val variableValue: Prim<DiceRollVariableValue>)
                            : Variable(variableId, label, description, tags)
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(variableId : VariableId,
                label : Maybe<VariableLabel>,
                description : Maybe<VariableDescription>,
                tags : List<VariableTag>,
                value : DiceRollVariableValue)
        : this(UUID.randomUUID(),
               Prim(variableId),
               maybeLiftPrim(label),
               maybeLiftPrim(description),
               Prim(tags),
               Prim(value))


    companion object : Factory<DiceRollVariable>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<DiceRollVariable> = when (doc)
        {
            is DocDict ->
            {
                effApply(::DiceRollVariable,
                         // Variable Id
                         doc.at("id") ap { VariableId.fromDocument(it) },
                         // Label
                         split(doc.maybeAt("label"),
                               effValue<ValueError,Maybe<VariableLabel>>(Nothing()),
                               { effApply(::Just, VariableLabel.fromDocument(it)) }),
                         // Description
                         split(doc.maybeAt("description"),
                               effValue<ValueError,Maybe<VariableDescription>>(Nothing()),
                               { effApply(::Just, VariableDescription.fromDocument(it)) }),
                         // Tags
                         doc.list("tags") ap { docList ->
                             docList.map { VariableTag.fromDocument(it) }
                         },
                         // Value
                         doc.at("value") ap { DiceRollVariableValue.fromDocument(it) }
                         )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun variableValue() : DiceRollVariableValue = this.variableValue.value


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() {}


    // -----------------------------------------------------------------------------------------
    // VARIABLE
    // -----------------------------------------------------------------------------------------

    override fun dependencies() : Set<VariableReference> = this.variableValue.value.dependencies()

    override fun type(): VariableType = VariableType.DICE_ROLL


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    fun value() : DiceRoll = this.variableValue().value()

}


/**
 * Number Variable
 */
data class NumberVariable(override val id : UUID,
                          override val variableId : Prim<VariableId>,
                          override val label : Maybe<Prim<VariableLabel>>,
                          override val description : Maybe<Prim<VariableDescription>>,
                          override val tags : Prim<List<VariableTag>>,
                          val value : Prim<NumberVariableValue>)
                          : Variable(variableId, label, description, tags)
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(variableId : VariableId,
                label : Maybe<VariableLabel>,
                description : Maybe<VariableDescription>,
                tags : List<VariableTag>,
                value : NumberVariableValue)
        : this(UUID.randomUUID(),
               Prim(variableId),
               maybeLiftPrim(label),
               maybeLiftPrim(description),
               Prim(tags),
               Prim(value))


    companion object : Factory<NumberVariable>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<NumberVariable> = when (doc)
        {
            is DocDict ->
            {
                effApply(::NumberVariable,
                         // Variable Id
                         doc.at("id") ap { VariableId.fromDocument(it) },
                         // Label
                         split(doc.maybeAt("label"),
                               effValue<ValueError,Maybe<VariableLabel>>(Nothing()),
                               { effApply(::Just, VariableLabel.fromDocument(it)) }),
                         // Description
                         split(doc.maybeAt("label"),
                               effValue<ValueError,Maybe<VariableDescription>>(Nothing()),
                               { effApply(::Just, VariableDescription.fromDocument(it)) }),
                         // Tags
                         doc.list("tags") ap { docList ->
                             docList.map { VariableTag.fromDocument(it) }
                         },
                         // Value
                         doc.at("value") ap { NumberVariableValue.fromDocument(it) }
                         )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun variableValue() : NumberVariableValue = this.value.value


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() {}


    // -----------------------------------------------------------------------------------------
    // VARIABLE
    // -----------------------------------------------------------------------------------------

    override fun dependencies() : Set<VariableReference> = this.value.value.dependencies()

    override fun type(): VariableType = VariableType.NUMBER


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    fun value(sheetContext : SheetContext) : AppEff<Double> =
            this.variableValue().value(sheetContext)

}


/**
 * Text Variable
 */
data class TextVariable(override val id : UUID,
                        override val variableId : Prim<VariableId>,
                        override val label : Maybe<Prim<VariableLabel>>,
                        override val description : Maybe<Prim<VariableDescription>>,
                        override val tags : Prim<List<VariableTag>>,
                        val variableValue : Prim<TextVariableValue>,
                        val definesNamespace : Prim<DefinesNamespace>)
                        : Variable(variableId, label, description, tags)
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(variableId : VariableId,
                label : Maybe<VariableLabel>,
                description : Maybe<VariableDescription>,
                tags : List<VariableTag>,
                variableValue : TextVariableValue,
                definesNamespace : DefinesNamespace)
        : this(UUID.randomUUID(),
               Prim(variableId),
               maybeLiftPrim(label),
               maybeLiftPrim(description),
               Prim(tags),
               Prim(variableValue),
               Prim(definesNamespace))


    companion object : Factory<TextVariable>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<TextVariable> = when (doc)
        {
            is DocDict ->
            {
                effApply(::TextVariable,
                         // Variable Id
                         doc.at("id") ap { VariableId.fromDocument(it) },
                         // Label
                        split(doc.maybeAt("label"),
                              effValue<ValueError,Maybe<VariableLabel>>(Nothing()),
                              { effApply(::Just, VariableLabel.fromDocument(it)) }),
                         // Description
                        split(doc.maybeAt("label"),
                              effValue<ValueError,Maybe<VariableDescription>>(Nothing()),
                              { effApply(::Just, VariableDescription.fromDocument(it)) }),
                         // Tags
                         doc.list("tags") ap { docList ->
                             docList.map { VariableTag.fromDocument(it) }
                         },
                         // Value
                         doc.at("value") ap { TextVariableValue.fromDocument(it) },
                         // Defines Namespace
                         split(doc.maybeAt("defines_namespace"),
                               effValue(DefinesNamespace(false)),
                               { DefinesNamespace.fromDocument(it) })
                         )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun variableValue() : TextVariableValue = this.variableValue.value


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() {}


    // -----------------------------------------------------------------------------------------
    // VARIABLE
    // -----------------------------------------------------------------------------------------

    override fun dependencies() : Set<VariableReference> = this.variableValue().dependencies()

    override fun type(): VariableType = VariableType.TEXT


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    fun value(sheetContext : SheetContext) : AppEff<String> =
            this.variableValue().value(sheetContext)

}


enum class VariableType
{
    BOOLEAN,
    DICE_ROLL,
    NUMBER,
    TEXT
}



/**
 * Variable NameSpace
 */
data class VariableNameSpace(val value : String) : Serializable
{

    companion object : Factory<VariableNameSpace>
    {
        override fun fromDocument(doc: SpecDoc) : ValueParser<VariableNameSpace> = when (doc)
        {
            is DocText -> effValue(VariableNameSpace(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }

}


/**
 * Variable Name
 */
data class VariableName(val value : String) : Serializable
{

    companion object : Factory<VariableName>
    {
        override fun fromDocument(doc: SpecDoc) : ValueParser<VariableName> = when (doc)
        {
            is DocText -> effValue(VariableName(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }
}



/**
 * Variable Reference
 */
@Suppress("UNCHECKED_CAST")
sealed class VariableReference : Serializable
{

    companion object : Factory<VariableReference>
    {
        override fun fromDocument(doc: SpecDoc) : ValueParser<VariableReference> =
            when (doc.case)
            {
                "name" -> VariableName.fromDocument(doc) as ValueParser<VariableReference>
                "tag"  -> VariableTag.fromDocument(doc) as ValueParser<VariableReference>
                else   -> effError<ValueError,VariableReference>(
                                    UnknownCase(doc.case, doc.path))
            }
    }
}


/**
 * Variable Id
 */
data class VariableId(val namespace : Func<VariableNameSpace>,
                      val name : Prim<VariableName>) : VariableReference(), Serializable
{


    constructor(name : String)
        : this(Null(), Prim(VariableName(name)))


    companion object : Factory<VariableId>
    {
        override fun fromDocument(doc: SpecDoc) : ValueParser<VariableId> = when (doc)
        {
            is DocText -> effApply(::VariableId,
                                   // Namespace
                                   nullEff<VariableNameSpace>(),
                                   // Name
                                   effValue(Prim(VariableName(doc.text)))
                                   )
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    override fun toString(): String
    {
        var s = ""

        if (namespace.value != null)
        {
            s += namespace.value.toString()
            s += "::"
        }

        s += name.value.toString()

        return s
    }

}


/**
 * Variable Tag
 */
data class VariableTag(val value : String) : VariableReference(), Serializable
{

    companion object : Factory<VariableTag>
    {
        override fun fromDocument(doc: SpecDoc) : ValueParser<VariableTag> = when (doc)
        {
            is DocText -> effValue(VariableTag(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }
}


/**
 * Variable Label
 */
data class VariableLabel(val value : String) : Serializable
{

    companion object : Factory<VariableLabel>
    {
        override fun fromDocument(doc: SpecDoc) : ValueParser<VariableLabel> = when (doc)
        {
            is DocText -> effValue(VariableLabel(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }
}


/**
 * Variable Description
 */
data class VariableDescription(val value : String) : Serializable
{

    companion object : Factory<VariableDescription>
    {
        override fun fromDocument(doc: SpecDoc) : ValueParser<VariableDescription> = when (doc)
        {
            is DocText -> effValue(VariableDescription(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }
}


/**
 * Defines Namespace
 */
data class DefinesNamespace(val value : Boolean) : Serializable
{

    companion object : Factory<DefinesNamespace>
    {
        override fun fromDocument(doc: SpecDoc) : ValueParser<DefinesNamespace> = when (doc)
        {
            is DocBoolean -> effValue(DefinesNamespace(doc.boolean))
            else          -> effError(UnexpectedType(DocType.BOOLEAN, docType(doc), doc.path))
        }
    }
}



//
//    // > Variables
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * Lookup the referenced variables.
//     * @return The Variable List.
//     */
//    public Set<VariableUnion> variables()
//    {
//        switch (this.type())
//        {
//            case NAME:
//                Set<VariableUnion> variables = new HashSet<>();
//                VariableUnion variableUnion = State.variableWithName(this.name());
//                if (variableUnion != null)
//                    variables.add(variableUnion);
//                return variables;
//            case TAG:
//                return State.variablesWithTag(this.tag());
//            default:
//                ApplicationFailure.union(
//                        UnionException.unknownVariant(
//                                new UnknownVariantError(VariableReferenceType.class.getName())));
//                return new HashSet<>();
//        }
//    }
//
//
//    /**
//     * Special function when we assume that there are no references by tag being used, or there
//     * will only every be one variable with that tag.
//     * @return The referenced variable.
//     */
//    public VariableUnion variable()
//    {
//        switch (this.type())
//        {
//            case NAME:
//                return State.variableWithName(this.name());
//            case TAG:
//                Set<VariableUnion> variables = State.variablesWithTag(this.tag());
//
//                if (variables.size() == 0)
//                    return null;
//
//                List<VariableUnion> variableList = new ArrayList<>(variables);
//                return variableList.get(0);
//        }
//
//        return null;
//    }
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
//        // Type
//        this.type.setName("type");
//        this.type.setLabelId(R.string.activity_variable_reference_field_type_label);
//        this.type.setDescriptionId(R.string.activity_variable_reference_field_type_description);
//
//        // Name
//        this.name.setName("type_name");
//        this.name.setLabelId(R.string.activity_variable_reference_field_name_label);
//        this.name.setDescriptionId(R.string.activity_variable_reference_field_name_label);
//        this.name.caseOf("type", "name");
//
//        // Tag
//        this.tag.setName("type_tag");
//        this.tag.setLabelId(R.string.activity_variable_reference_field_tag_label);
//        this.tag.setDescriptionId(R.string.activity_variable_reference_field_tag_description);
//        this.tag.caseOf("type", "tag");
//    }


//
//public abstract class Variable extends Model
//{
//
//    // ABSTRACT METHODS
//    // ------------------------------------------------------------------------------------------
//
//    public abstract String                  name();
//    public abstract void                    setName(String name);
//    public abstract String                  label();
//    public abstract void                    setLabel(String label);
//    public abstract String                  description();
//    public abstract boolean                 isNamespaced();
//    public abstract void                    setIsNamespaced(Boolean isNamespaced);
//    public abstract List<VariableReference> dependencies();
//    public abstract List<String>            tags();
//    public abstract String                  valueString() throws NullVariableException;
//    public abstract void                    initialize();
//
//
//    // PROPERTIES
//    // ------------------------------------------------------------------------------------------
//
//    private OnUpdateListener                onUpdateListener;
//
//    protected String                        originalName;
//    protected String                        originalLabel;
//
//
//    // CONSTRUCTORS
//    // ------------------------------------------------------------------------------------------
//
//    public Variable()
//    {
//        this.onUpdateListener = null;
//    }
//
//
//    // API
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * This method should be called when the variable's value changes. This could happen directly
//     * from user input, or indirectly, if user input changes a variable that was part of this
//     * variable's value.
//     */
//    public void onUpdate()
//    {
//        // [1] Call the variable's update listener
//        // --------------------------------------------------------------------------------------
//
//        if (this.onUpdateListener != null) {
//            this.onUpdateListener.onUpdate();
//        }
//
//        // [2] Update any variables that depend on this variable
//        // --------------------------------------------------------------------------------------
//
//        State.updateVariableDependencies(this);
//    }
//
//
//    public void setOnUpdateListener(OnUpdateListener onUpdateListener)
//    {
//        this.onUpdateListener = onUpdateListener;
//    }
//
//
//    public void setNamespace(Namespace namespace)
//    {
//        // > Update name
//        String previousName = this.name();
//
//        String newName;
//        if (this.originalName != null)
//            newName = namespace.name() + "." + this.originalName;
//        else
//            newName = namespace.name();
//        this.setName(newName);
//
//        // > Update label
//        String newLabel;
//        if (this.originalLabel != null)
//            newLabel = namespace.label() + " " + this.originalLabel;
//        else
//            newLabel = namespace.label();
//        this.setLabel(newLabel);
//
//        // > Reindex variable
//        State.removeVariable(previousName);
//        State.addVariable(this);
//    }
//
//
//
//    // ON UPDATE LISTENER
//    // -----------------------------------------------------------------------------------------
//
//    public interface OnUpdateListener
//    {
//        void onUpdate();
//    }
//
//
//}
