
package com.kispoko.tome.model.game.engine.variable


import com.kispoko.tome.lib.Factory
import com.kispoko.tome.lib.functor.*
import com.kispoko.tome.lib.model.Model
import effect.*
import lulo.document.*
import lulo.value.*
import lulo.value.UnexpectedType
import java.util.*



/**
 * Variable
 */
@Suppress("UNCHECKED_CAST")
sealed class Variable(open val variableId : Func<VariableId>,
                      open val label : Func<VariableLabel>,
                      open val description : Func<VariableDescription>,
                      open val tags : Func<List<VariableTag>>
                      ) : Model
{

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

}


/**
 * Boolean Variable
 */
data class BooleanVariable(override val id : UUID,
                        override val variableId : Func<VariableId>,
                        override val label : Func<VariableLabel>,
                        override val description : Func<VariableDescription>,
                        override val tags : Func<List<VariableTag>>,
                        val value : Func<BooleanVariableValue>)
                        : Variable(variableId, label, description, tags)
{

    companion object : Factory<BooleanVariable>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<BooleanVariable> = when (doc)
        {
            is DocDict -> effApply(::BooleanVariable,
                                   // Model Id
                                   effValue(UUID.randomUUID()),
                                   // Variable Id
                                   doc.at("name") ap {
                                       val variableId =
                                           effApply(::VariableId,
                                                    effValue(UUID.randomUUID()),
                                                    effValue(Null<VariableNameSpace>()),
                                                    effApply(::Prim,
                                                             VariableName.fromDocument(it)))
                                       effApply(::Comp, variableId)
                                   },
                                   // Label
                                   doc.at("label") ap {
                                       effApply(::Prim, VariableLabel.fromDocument(it))
                                   },
                                   // Description
                                   doc.at("description") ap {
                                       effApply(::Prim, VariableDescription.fromDocument(it))
                                   },
                                   // Tags
                                   doc.list("tags") ap { docList ->
                                       effApply(::Prim,
                                                docList.map { VariableTag.fromDocument(it) })
                                   },
                                   // Value
                                   doc.at("value") ap {
                                       effApply(::Comp, BooleanVariableValue.fromDocument(it))
                                   })
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }

    override fun onLoad() {}

}


/**
 * Dice Variable
 */
data class DiceRollVariable(override val id : UUID,
                            override val variableId : Func<VariableId>,
                            override val label : Func<VariableLabel>,
                            override val description : Func<VariableDescription>,
                            override val tags : Func<List<VariableTag>>,
                            val value : Func<DiceVariableValue>)
                            : Variable(variableId, label, description, tags)
{

    companion object : Factory<DiceRollVariable>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<DiceRollVariable> = when (doc)
        {
            is DocDict -> effApply(::DiceRollVariable,
                                   // Model Id
                                   effValue(UUID.randomUUID()),
                                   // Variable Id
                                   doc.at("name") ap {
                                       val variableId =
                                           effApply(::VariableId,
                                                    effValue(UUID.randomUUID()),
                                                    effValue(Null<VariableNameSpace>()),
                                                    effApply(::Prim,
                                                             VariableName.fromDocument(it)))
                                       effApply(::Comp, variableId)
                                   },
                                   // Label
                                   doc.at("label") ap {
                                       effApply(::Prim, VariableLabel.fromDocument(it))
                                   },
                                   // Description
                                   doc.at("description") ap {
                                       effApply(::Prim, VariableDescription.fromDocument(it))
                                   },
                                   // Tags
                                   doc.list("tags") ap { docList ->
                                       effApply(::Prim,
                                                docList.map { VariableTag.fromDocument(it) })
                                   },
                                   // Value
                                   doc.at("value") ap {
                                        effApply(::Comp, DiceVariableValue.fromDocument(it))
                                   })
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    override fun onLoad() {}

}


/**
 * Number Variable
 */
data class NumberVariable(override val id : UUID,
                          override val variableId : Func<VariableId>,
                          override val label : Func<VariableLabel>,
                          override val description : Func<VariableDescription>,
                          override val tags : Func<List<VariableTag>>,
                          val value : Func<NumberVariableValue>)
                          : Variable(variableId, label, description, tags)
{

    companion object : Factory<NumberVariable>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<NumberVariable> = when (doc)
        {
            is DocDict -> effApply(::NumberVariable,
                                   // Model Id
                                   effValue(UUID.randomUUID()),
                                   // Variable Id
                                   doc.at("name") ap {
                                       val variableId =
                                           effApply(::VariableId,
                                                    effValue(UUID.randomUUID()),
                                                    effValue(Null<VariableNameSpace>()),
                                                    effApply(::Prim,
                                                             VariableName.fromDocument(it)))
                                       effApply(::Comp, variableId)
                                   },
                                   // Label
                                   doc.at("label") ap {
                                       effApply(::Prim, VariableLabel.fromDocument(it))
                                   },
                                   // Description
                                   doc.at("description") ap {
                                       effApply(::Prim, VariableDescription.fromDocument(it))
                                   },
                                   // Tags
                                   doc.list("tags") ap { docList ->
                                       effApply(::Prim,
                                                docList.map { VariableTag.fromDocument(it) })
                                   },
                                   // Value
                                   doc.at("value") ap {
                                       effApply(::Comp, NumberVariableValue.fromDocument(it))
                                   })
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }


    override fun onLoad() {}

}


/**
 * Text Variable
 */
data class TextVariable(override val id : UUID,
                        override val variableId : Func<VariableId>,
                        override val label : Func<VariableLabel>,
                        override val description : Func<VariableDescription>,
                        override val tags : Func<List<VariableTag>>,
                        val value : Func<TextVariableValue>,
                        val definesNamespace : Func<DefinesNamespace>)
                        : Variable(variableId, label, description, tags)
{

    companion object : Factory<TextVariable>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<TextVariable> = when (doc)
        {
            is DocDict ->
            {
                effApply(::TextVariable,
                         // Model Id
                         effValue(UUID.randomUUID()),
                         // Variable Id
                         doc.at("id") ap {
                             val variableId =
                                 effApply(::VariableId,
                                          effValue(UUID.randomUUID()),
                                          effValue(Null<VariableNameSpace>()),
                                          effApply(::Prim,
                                                   VariableName.fromDocument(it)))
                             effApply(::Comp, variableId)
                         },
                         // Label
                         split(doc.maybeAt("label"),
                               nullEff<VariableLabel>(),
                               { effApply(::Prim, VariableLabel.fromDocument(it)) }),
                         // Description
                         split(doc.maybeAt("description"),
                               nullEff<VariableDescription>(),
                               { effApply(::Prim, VariableDescription.fromDocument(it)) }),
                         // Tags
                         doc.list("tags") ap { docList ->
                             effApply(::Prim,
                                      docList.map { VariableTag.fromDocument(it) })
                         },
                         // Value
                         doc.at("value") ap {
                             effApply(::Prim, TextVariableValue.fromDocument(it))
                         },
                         // Defines Namespace
                         split(doc.maybeAt("defines_namespace"),
                               nullEff<DefinesNamespace>(),
                               { effApply(::Prim, DefinesNamespace.fromDocument(it)) })
                         )
            }
            else       -> effError(UnexpectedType(DocType.DICT, docType(doc), doc.path))
        }
    }

    override fun onLoad() {}

}



/**
 * Variable Id
 */
data class VariableId(override val id : UUID,
                      val namespace : Func<VariableNameSpace>,
                      val name : Func<VariableName>) : Model
{

    override fun onLoad() { }
}


/**
 * Variable NameSpace
 */
data class VariableNameSpace(val value : String)
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
data class VariableName(val value : String)
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
 * Variable Tag
 */
data class VariableTag(val value : String)
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
data class VariableLabel(val value : String)
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
data class VariableDescription(val value : String)
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
data class DefinesNamespace(val value : Boolean)
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


/**
 * Variable Reference
 */
sealed class VariableReference : Model
{

    companion object : Factory<VariableReference>
    {
        override fun fromDocument(doc: SpecDoc) : ValueParser<VariableReference> = when (doc)
        {
            is DocDict -> when (doc.case())
            {
                "name" -> VariableReferenceName.fromDocument(doc)
                "tag"  -> VariableReferenceTag.fromDocument(doc)
                else   -> effError<ValueError,VariableReference>(
                                    UnknownCase(doc.case(), doc.path))
            }
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }
}


data class VariableReferenceName(override val id : UUID,
                                 val name : Func<VariableName>) : VariableReference()
{

    companion object : Factory<VariableReference>
    {
        override fun fromDocument(doc: SpecDoc) : ValueParser<VariableReference> = when (doc)
        {
            is DocDict -> effApply(::VariableReferenceName,
                                   // Model Id
                                   effValue(UUID.randomUUID()),
                                   // Variable Name
                                   doc.at("name") ap {
                                       effApply(::Prim, VariableName.fromDocument(it))
                                   })
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }

    override fun onLoad() { }
}


data class VariableReferenceTag(override val id : UUID,
                                val tag : Func<VariableTag>) : VariableReference()
{

    companion object : Factory<VariableReference>
    {
        override fun fromDocument(doc: SpecDoc) : ValueParser<VariableReference> = when (doc)
        {
            is DocDict -> effApply(::VariableReferenceTag,
                                   // Model Id
                                   effValue(UUID.randomUUID()),
                                   // Variable Name
                                   doc.at("tag") ap {
                                       effApply(::Prim, VariableTag.fromDocument(it))
                                   })
            else       -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }

    override fun onLoad() { }
}




//
//
//public class VariableReference extends Model
//                               implements Serializable
//{
//
//    // PROPERTIES
//    // ------------------------------------------------------------------------------------------
//
//    // > Model
//    // ------------------------------------------------------------------------------------------
//
//    private UUID                                    id;
//
//
//    // > Functors
//    // ------------------------------------------------------------------------------------------
//
//    private PrimitiveFunctor<String>                name;
//    private PrimitiveFunctor<String>                tag;
//
//    private OptionFunctor<VariableReferenceType>    type;
//
//
//    // CONSTRUCTORS
//    // ------------------------------------------------------------------------------------------
//
//    public VariableReference()
//    {
//        this.id     = null;
//
//        this.name   = new PrimitiveFunctor<>(null, String.class);
//        this.tag    = new PrimitiveFunctor<>(null, String.class);
//
//        this.type   = new OptionFunctor<>(null, VariableReferenceType.class);
//
//        this.initializeFunctors();
//    }
//
//
//    private VariableReference(UUID id, Object value, VariableReferenceType type)
//    {
//        this.id     = id;
//
//        this.name   = new PrimitiveFunctor<>(null, String.class);
//        this.tag    = new PrimitiveFunctor<>(null, String.class);
//
//        this.type   = new OptionFunctor<>(type, VariableReferenceType.class);
//
//        // > Set the value depending on the case
//        switch (type)
//        {
//            case NAME:
//                this.name.setValue((String) value);
//                break;
//            case TAG:
//                this.tag.setValue((String) value);
//                break;
//        }
//
//        this.initializeFunctors();
//    }
//
//
//    // > Variants
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * Create the "by name" case. Reference a variable by its name.
//     * @param id The model id.
//     * @param variableName The variable name.
//     * @return The Variable Reference "by name".
//     */
//    public static VariableReference asByName(UUID id, String variableName)
//    {
//        return new VariableReference(id, variableName, VariableReferenceType.NAME);
//    }
//
//
//    /**
//     * Create the "by name" case, but not as a model.
//     * @param variableName The variable name.
//     * @return The Variable Reference "by name".
//     */
//    public static VariableReference asByName(String variableName)
//    {
//        return new VariableReference(null, variableName, VariableReferenceType.NAME);
//    }
//
//
//    /**
//     * Create the "by tag" case. Reference all variables that have the given tag.
//     * @param id The model id.
//     * @param tag The variable tag
//     * @return The Variable Reference "by tag".
//     */
//    public static VariableReference asByTag(UUID id, String tag)
//    {
//        return new VariableReference(id, tag, VariableReferenceType.TAG);
//    }
//
//
//    /**
//     * Create the "by tag" case, but not as a model.
//     * @param tag The variable tag.
//     * @return The Variable Reference "by tag".
//     */
//    public static VariableReference asByTag(String tag)
//    {
//        return new VariableReference(null, tag, VariableReferenceType.TAG);
//    }
//
//
//    // > Yaml
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * Create a Variable Reference from its Yaml representation.
//     * @param yaml The yaml parser.
//     * @return The parsed Variable Reference.
//     * @throws YamlParseException
//     */
//    public static VariableReference fromYaml(YamlParser yaml)
//            throws YamlParseException
//    {
//        UUID                  id   = UUID.randomUUID();
//
//        VariableReferenceType type = VariableReferenceType.fromYaml(yaml.atKey("type"));
//
//        switch (type)
//        {
//            case NAME:
//                String variableName = yaml.atKey("name").getString();
//                return VariableReference.asByName(id, variableName);
//            case TAG:
//                String tag = yaml.atKey("tag").getString();
//                return VariableReference.asByTag(id, tag);
//        }
//
//        return null;
//    }
//
//
//    // API
//    // ------------------------------------------------------------------------------------------
//
//    // > Model
//    // ------------------------------------------------------------------------------------------
//
//    // ** Id
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * Get the model identifier.
//     * @return The model UUID.
//     */
//    public UUID getId()
//    {
//        return this.id;
//    }
//
//
//    /**
//     * Set the model identifier.
//     * @param id The new model UUID.
//     */
//    public void setId(UUID id)
//    {
//        this.id = id;
//    }
//
//
//    // ** On Load
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * This method is called when the Column Union is completely loaded for the first time.
//     */
//    public void onLoad() { }
//
//
//    // > State
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * Get the kind of variable reference.
//     * @return The reference kind.
//     */
//    public VariableReferenceType type()
//    {
//        return this.type.getValue();
//    }
//
//
//    /**
//     * The variable name case.
//     * @return The variable name.
//     */
//    public String name()
//    {
//        return this.name.getValue();
//    }
//
//
//    /**
//     * The tag case.
//     * @return The tag.
//     */
//    public String tag()
//    {
//        return this.tag.getValue();
//    }
//
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
