
package com.kispoko.tome.model.game.engine.variable


import com.kispoko.tome.app.AppEff
import com.kispoko.tome.app.AppError
import com.kispoko.tome.app.AppStateError
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.*
import com.kispoko.tome.lib.model.Model
import com.kispoko.tome.lib.orm.sql.*
import com.kispoko.tome.model.game.engine.dice.DiceRoll
import com.kispoko.tome.model.sheet.SheetId
import com.kispoko.tome.rts.sheet.SheetContext
import com.kispoko.tome.rts.sheet.SheetManager
import com.kispoko.tome.rts.sheet.VariableIsOfUnexpectedType
import com.kispoko.tome.util.Util
import effect.*
import lulo.document.*
import lulo.value.*
import lulo.value.UnexpectedType
import org.apache.commons.lang3.SerializationUtils
import java.io.Serializable
import java.util.*



/**
 * Variable
 */
@Suppress("UNCHECKED_CAST")
sealed class Variable(open var variableId : Prim<VariableId>,
                      open val label : Prim<VariableLabel>,
                      open val description : Prim<VariableDescription>,
                      open val tags : Prim<VariableTagSet>) : Model, Serializable
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
        override fun fromDocument(doc : SpecDoc) : ValueParser<Variable> =
            when (doc.case())
            {
                "variable_boolean"   -> BooleanVariable.fromDocument(doc) as ValueParser<Variable>
                "variable_dice_roll" -> DiceRollVariable.fromDocument(doc) as ValueParser<Variable>
                "variable_number"    -> NumberVariable.fromDocument(doc) as ValueParser<Variable>
                "variable_text"      -> TextVariable.fromDocument(doc) as ValueParser<Variable>
                else                 -> effError<ValueError, Variable>(
                                            UnknownCase(doc.case(), doc.path))
            }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun variableId() : VariableId = this.variableId.value

    fun label() : String = this.label.value.value

    fun description() : String = this.description.value.value

    fun tags() : Set<VariableTag> = this.tags.value.variables


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


    // -----------------------------------------------------------------------------------------
    // VARIABLE
    // -----------------------------------------------------------------------------------------

    abstract fun dependencies(sheetContext : SheetContext) : Set<VariableReference>

    abstract fun type() : VariableType

    abstract fun companionVariables(sheetontext : SheetContext) : AppEff<Set<Variable>>


    /**
     * This method is called when one of the variable's dependencies has been updated, and this
     * variable must therefore be udpated.
     */
    fun onUpdate()
    {
        this.onUpdateListener()
    }


    // -----------------------------------------------------------------------------------------
    // UPDATE
    // -----------------------------------------------------------------------------------------

    fun setVariableId(variableId : VariableId)
    {
        this.variableId.value = variableId
    }


    // -----------------------------------------------------------------------------------------
    // VALUE STRING
    // -----------------------------------------------------------------------------------------

    open fun valueString(sheetContext : SheetContext) : AppEff<String> = when (this)
    {
        is BooleanVariable  -> this.value() ap { effValue<AppError,String>(it.toString()) }
        is DiceRollVariable -> effValue(this.value().toString())
        is NumberVariable   -> this.valueString(sheetContext)
        is TextVariable     -> this.valueString(sheetContext)
    }

}


/**
 * Boolean Variable
 */
data class BooleanVariable(override val id : UUID,
                           override var variableId : Prim<VariableId>,
                           override val label : Prim<VariableLabel>,
                           override val description : Prim<VariableDescription>,
                           override val tags : Prim<VariableTagSet>,
                           var variableValue : Sum<BooleanVariableValue>)
                            : Variable(variableId, label, description, tags)
{

    // -----------------------------------------------------------------------------------------
    // INITIALIZATION
    // -----------------------------------------------------------------------------------------

    init
    {
        this.variableId.name    = "variable_id"
        this.label.name         = "label"
        this.description.name   = "description"
        this.tags.name          = "tags"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(variableId : VariableId,
                label : VariableLabel,
                description : VariableDescription,
                tags : VariableTagSet,
                value : BooleanVariableValue)
        : this(UUID.randomUUID(),
               Prim(variableId),
               Prim(label),
               Prim(description),
               Prim(tags),
               Sum(value))


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
                         doc.at("label") ap { VariableLabel.fromDocument(it) },
                         // Description
                         doc.at("description") ap { VariableDescription.fromDocument(it) },
                         // Tags
                         split(doc.maybeAt("tags"),
                               effValue(VariableTagSet.empty()),
                               { VariableTagSet.fromDocument(it) }),
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

    override val name : String = "variable_boolean"

    override val modelObject : Model = this

    override fun onLoad() {}


    // -----------------------------------------------------------------------------------------
    // VARIABLE
    // -----------------------------------------------------------------------------------------

    override fun type(): VariableType = VariableType.BOOLEAN

    override fun dependencies(sheetContext : SheetContext) = this.variableValue().dependencies()

    override fun companionVariables(sheetContext : SheetContext) : AppEff<Set<Variable>> =
            this.variableValue().companionVariables(sheetContext)


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    fun value() : AppEff<Boolean> = this.variableValue().value()


    fun updateValue(value : Boolean)
    {
        when (this.variableValue())
        {
            is BooleanVariableLiteralValue ->
                    this.variableValue = Sum(BooleanVariableLiteralValue(value))
        }
    }

}


/**
 * Dice Variable
 */
data class DiceRollVariable(override val id : UUID,
                            override var variableId : Prim<VariableId>,
                            override val label : Prim<VariableLabel>,
                            override val description : Prim<VariableDescription>,
                            override val tags : Prim<VariableTagSet>,
                            val variableValue: Func<DiceRollVariableValue>)
                            : Variable(variableId, label, description, tags)
{

    // -----------------------------------------------------------------------------------------
    // INITIALIZATION
    // -----------------------------------------------------------------------------------------

    init
    {
        this.variableId.name    = "variable_id"
        this.label.name         = "label"
        this.description.name   = "description"
        this.tags.name          = "tags"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(variableId : VariableId,
                label : VariableLabel,
                description : VariableDescription,
                tags : VariableTagSet,
                value : DiceRollVariableValue)
        : this(UUID.randomUUID(),
               Prim(variableId),
               Prim(label),
               Prim(description),
               Prim(tags),
               liftDiceRollVariableValue(value))


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
                         doc.at("label") ap { VariableLabel.fromDocument(it) },
                         // Description
                         doc.at("description") ap { VariableDescription.fromDocument(it) },
                         // Tags
                         split(doc.maybeAt("tags"),
                               effValue(VariableTagSet.empty()),
                               { VariableTagSet.fromDocument(it) }),
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

    override val name : String = "variable_dice_roll"

    override val modelObject : Model = this


    // -----------------------------------------------------------------------------------------
    // VARIABLE
    // -----------------------------------------------------------------------------------------

    override fun dependencies(sheetContext : SheetContext) =
            this.variableValue.value.dependencies()


    override fun type(): VariableType = VariableType.DICE_ROLL


    override fun companionVariables(sheetContext : SheetContext) : AppEff<Set<Variable>> =
            this.variableValue().companionVariables(sheetContext)


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    fun value() : DiceRoll = this.variableValue().value()

}


/**
 * Number Variable
 */
data class NumberVariable(override val id : UUID,
                          override var variableId : Prim<VariableId>,
                          override val label : Prim<VariableLabel>,
                          override val description : Prim<VariableDescription>,
                          override val tags : Prim<VariableTagSet>,
                          val variableValue : Sum<NumberVariableValue>)
                          : Variable(variableId, label, description, tags)
{

    // -----------------------------------------------------------------------------------------
    // INITIALIZATION
    // -----------------------------------------------------------------------------------------

    init
    {
        this.variableId.name    = "variable_id"
        this.label.name         = "label"
        this.description.name   = "description"
        this.tags.name          = "tags"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(variableId : VariableId,
                label : VariableLabel,
                description : VariableDescription,
                tags : VariableTagSet,
                value : NumberVariableValue)
        : this(UUID.randomUUID(),
               Prim(variableId),
               Prim(label),
               Prim(description),
               Prim(tags),
               Sum(value))


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
                         doc.at("label") ap { VariableLabel.fromDocument(it) },
                         // Description
                         doc.at("description") ap { VariableDescription.fromDocument(it) },
                         // Tags
                         split(doc.maybeAt("tags"),
                               effValue(VariableTagSet.empty()),
                               { VariableTagSet.fromDocument(it) }),
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

    fun variableValue() : NumberVariableValue = this.variableValue.value


    // -----------------------------------------------------------------------------------------
    // MODEL
    // -----------------------------------------------------------------------------------------

    override fun onLoad() {}

    override val name = "variable_number"

    override val modelObject : Model = this


    // -----------------------------------------------------------------------------------------
    // VARIABLE
    // -----------------------------------------------------------------------------------------

    override fun dependencies(sheetContext : SheetContext) : Set<VariableReference> =
            this.variableValue.value.dependencies(sheetContext)


    override fun type(): VariableType = VariableType.NUMBER


    override fun companionVariables(sheetContext : SheetContext) : AppEff<Set<Variable>> =
            this.variableValue().companionVariables(sheetContext)


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    fun value(sheetContext : SheetContext) : AppEff<Maybe<Double>> =
            this.variableValue().value(sheetContext)


    /**
     * The string representation of the widget's current value. This method returns 0 when the
     * value is null for some reason.
     */
    override fun valueString(sheetContext : SheetContext) : AppEff<String>
    {
        fun maybeString(mDouble : Maybe<Double>) : AppEff<String> =
            when (mDouble) {
                is Just -> effValue<AppError,String>(Util.doubleString(mDouble.value))
                else    -> effValue("")
            }

        return this.value(sheetContext).apply(::maybeString)
    }


    fun updateValue(value : Double, sheetId : SheetId)
    {
        when (this.variableValue())
        {
            is NumberVariableLiteralValue ->
            {
                this.variableValue.value = NumberVariableLiteralValue(value)
                SheetManager.onVariableUpdate(sheetId, this)
            }
        }
    }

}


/**
 * Text Variable
 */
data class TextVariable(override val id : UUID,
                        override var variableId : Prim<VariableId>,
                        override val label : Prim<VariableLabel>,
                        override val description : Prim<VariableDescription>,
                        override val tags : Prim<VariableTagSet>,
                        var variableValue : Sum<TextVariableValue>,
                        val definesNamespace : Prim<DefinesNamespace>)
                        : Variable(variableId, label, description, tags)
{

    // -----------------------------------------------------------------------------------------
    // INITIALIZATION
    // -----------------------------------------------------------------------------------------

    init
    {
        this.variableId.name        = "variable_id"
        this.label.name             = "variable_id"
        this.description.name       = "variable_id"
        this.tags.name              = "tags"
        this.definesNamespace.name  = "defines_namespace"
    }


    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(variableId : VariableId,
                label : VariableLabel,
                description : VariableDescription,
                tags : VariableTagSet,
                variableValue : TextVariableValue,
                definesNamespace : DefinesNamespace)
        : this(UUID.randomUUID(),
               Prim(variableId),
               Prim(label),
               Prim(description),
               Prim(tags),
               Sum(variableValue),
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
                         doc.at("label") ap { VariableLabel.fromDocument(it) },
                         // Description
                         doc.at("description") ap { VariableDescription.fromDocument(it) },
                         // Tags
                         split(doc.maybeAt("tags"),
                               effValue(VariableTagSet.empty()),
                               { VariableTagSet.fromDocument(it) }),
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

    override val name = "variable_text"

    override val modelObject : Model = this


    // -----------------------------------------------------------------------------------------
    // VARIABLE
    // -----------------------------------------------------------------------------------------

    override fun dependencies(sheetContext : SheetContext) = this.variableValue().dependencies()

    override fun type(): VariableType = VariableType.TEXT

    override fun companionVariables(sheetContext : SheetContext) : AppEff<Set<Variable>> =
            this.variableValue().companionVariables(sheetContext)


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    fun value(sheetContext : SheetContext) : AppEff<Maybe<String>> =
            this.variableValue().value(sheetContext)


    override fun valueString(sheetContext : SheetContext) : AppEff<String>
    {
        fun maybeString(mString : Maybe<String>) : AppEff<String> =
            when (mString) {
                is Just -> effValue<AppError,String>(mString.value)
                else    -> effValue("")
            }

        return this.value(sheetContext).apply(::maybeString)
    }


    fun updateValue(value : String, sheetId : SheetId)
    {
        when (this.variableValue())
        {
            is TextVariableLiteralValue ->
            {
                this.variableValue = Sum(TextVariableLiteralValue(value))
                SheetManager.onVariableUpdate(sheetId, this)
            }
        }
    }

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
data class VariableNameSpace(val value : String) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<VariableNameSpace>
    {
        override fun fromDocument(doc: SpecDoc) : ValueParser<VariableNameSpace> = when (doc)
        {
            is DocText -> effValue(VariableNameSpace(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value})

}


/**
 * Variable Name
 */
data class VariableName(val value : String) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<VariableName>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<VariableName> = when (doc)
        {
            is DocText -> effValue(VariableName(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }

    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value})

}



/**
 * Variable Reference
 */
@Suppress("UNCHECKED_CAST")
sealed class VariableReference : SQLSerializable, Serializable
{

    companion object : Factory<VariableReference>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<VariableReference> =
            when (doc.case())
            {
                "variable_id"  -> VariableId.fromDocument(doc) as ValueParser<VariableReference>
                "variable_tag" -> VariableTag.fromDocument(doc) as ValueParser<VariableReference>
                else           -> effError<ValueError,VariableReference>(
                                        UnknownCase(doc.case(), doc.path))
            }
    }
}


/**
 * Variable Id
 */
data class VariableId(val namespace : Maybe<Prim<VariableNameSpace>>,
                      val name : Prim<VariableName>) : VariableReference(), Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    constructor(name : String)
        : this(Nothing(), Prim(VariableName(name)))


    constructor(namespace : Maybe<VariableNameSpace>, name : VariableName)
            : this(maybeLiftPrim(namespace), Prim(name))


    companion object : Factory<VariableId>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<VariableId> = when (doc)
        {
            is DocDict ->
            {
                effApply(::VariableId,
                         // Namespace
                         split(doc.maybeAt("namespace"),
                               effValue<ValueError,Maybe<VariableNameSpace>>(Nothing()),
                               { effApply(::Just, VariableNameSpace.fromDocument(it)) }),
                         // Name
                         doc.at("name") ap { VariableName.fromDocument(it) }
                         )
            }
            is DocText -> effApply(::VariableId, effValue(doc.text))
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // GETTERS
    // -----------------------------------------------------------------------------------------

    fun nameString() : String = this.name.value.value

    fun namespaceString() : String? = getMaybePrim(this.namespace)?.value


    // -----------------------------------------------------------------------------------------
    // TO STRING
    // -----------------------------------------------------------------------------------------

    override fun toString(): String
    {
        var s = ""

        if (namespaceString() != null)
        {
            s += namespaceString()
            s += "::"
        }

        s += name.value.toString()

        return s
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue =
            SQLText({this.namespaceString() + " " + this.nameString()})




}


/**
 * Variable Tag
 */
data class VariableTag(val value : String) : VariableReference(), Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<VariableTag>
    {
        override fun fromDocument(doc: SpecDoc) : ValueParser<VariableTag> = when (doc)
        {
            is DocText -> effValue(VariableTag(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value})

}


/**
 * Variable Label
 */
data class VariableLabel(val value : String) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<VariableLabel>
    {
        override fun fromDocument(doc: SpecDoc) : ValueParser<VariableLabel> = when (doc)
        {
            is DocText -> effValue(VariableLabel(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value})

}


/**
 * Variable Description
 */
data class VariableDescription(val value : String) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<VariableDescription>
    {
        override fun fromDocument(doc: SpecDoc) : ValueParser<VariableDescription> = when (doc)
        {
            is DocText -> effValue(VariableDescription(doc.text))
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLText({this.value})

}


/**
 * Defines Namespace
 */
data class DefinesNamespace(val value : Boolean) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<DefinesNamespace>
    {
        override fun fromDocument(doc: SpecDoc) : ValueParser<DefinesNamespace> = when (doc)
        {
            is DocBoolean -> effValue(DefinesNamespace(doc.boolean))
            else          -> effError(UnexpectedType(DocType.BOOLEAN, docType(doc), doc.path))
        }
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLInt({ if (this.value) 1 else 0 })

}


/**
 * Variable Tag Set
 */
data class VariableTagSet(val variables : MutableSet<VariableTag>) : SQLSerializable, Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<VariableTagSet>
    {

        override fun fromDocument(doc : SpecDoc) : ValueParser<VariableTagSet> = when (doc)
        {
            is DocList -> effApply(::VariableTagSet,
                                   doc.mapSetMut { VariableTag.fromDocument(it) })
            else       -> effError(UnexpectedType(DocType.LIST, docType(doc), doc.path))
        }


        fun empty() : VariableTagSet = VariableTagSet(mutableSetOf())
    }


    // -----------------------------------------------------------------------------------------
    // SQL SERIALIZABLE
    // -----------------------------------------------------------------------------------------

    override fun asSQLValue() : SQLValue = SQLBlob({ SerializationUtils.serialize(this)})

}


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
