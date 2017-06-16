
package com.kispoko.tome.model.game.engine.variable


import com.kispoko.tome.app.AppEff
import com.kispoko.tome.lib.Factory
import com.kispoko.tome.model.game.engine.program.Invocation
import effect.effApply
import effect.effError
import effect.effValue
import lulo.document.*
import lulo.value.UnexpectedType
import lulo.value.UnknownCase
import lulo.value.ValueError
import lulo.value.ValueParser
import java.io.Serializable



/**
 * Boolean Variable
 */
sealed class BooleanVariableValue : Serializable
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<BooleanVariableValue>
    {
        override fun fromDocument(doc: SpecDoc): ValueParser<BooleanVariableValue> =
            when (doc.case)
            {
                "boolean_literal"    -> BooleanVariableLiteralValue.fromDocument(doc)
                "program_invocation" -> BooleanVariableProgramValue.fromDocument(doc)
                else                 -> effError<ValueError,BooleanVariableValue>(
                                            UnknownCase(doc.case, doc.path))
            }
    }


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    open fun dependencies() : Set<VariableReference> = setOf()

    abstract fun value() : AppEff<Boolean>

}


/**
 * Literal Value
 */
data class BooleanVariableLiteralValue(val value : Boolean) : BooleanVariableValue()
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<BooleanVariableValue>
    {
        override fun fromDocument(doc : SpecDoc)
                      : ValueParser<BooleanVariableValue> = when (doc)
        {
            is DocBoolean -> effValue(BooleanVariableLiteralValue(doc.boolean))
            else          -> effError(UnexpectedType(DocType.TEXT, docType(doc), doc.path))
        }
    }

    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    override fun value() : AppEff<Boolean> = effValue(this.value)

}


/**
 * Program Value
 */
data class BooleanVariableProgramValue(val invocation : Invocation) : BooleanVariableValue()
{

    // -----------------------------------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    companion object : Factory<BooleanVariableValue>
    {
        override fun fromDocument(doc : SpecDoc) : ValueParser<BooleanVariableValue> =
                effApply(::BooleanVariableProgramValue, Invocation.fromDocument(doc))
    }


    // -----------------------------------------------------------------------------------------
    // VALUE
    // -----------------------------------------------------------------------------------------

    override fun dependencies() : Set<VariableReference> = this.invocation.dependencies()

    override fun value(): AppEff<Boolean> {
        TODO("not implemented")
    }

}



//
//public class BooleanVariable extends Variable
//                             implements ToYaml, Serializable
//{
//
//    // PROPERTIES
//    // ------------------------------------------------------------------------------------------
//
//    // > Model
//    // ------------------------------------------------------------------------------------------
//
//    private UUID id;
//
//
//    // > Functors
//    // ------------------------------------------------------------------------------------------
//
//    private PrimitiveFunctor<String>    name;
//    private PrimitiveFunctor<String>    label;
//    private PrimitiveFunctor<String>    description;
//
//    private PrimitiveFunctor<Boolean>   literalValue;
//    private ModelFunctor<Invocation>    invocationValue;
//
//    private PrimitiveFunctor<Kind>      kind;
//
//    private PrimitiveFunctor<Boolean>   isNamespaced;
//
//    private PrimitiveFunctor<String[]>  tags;
//
//
//    // > Internal
//    // ------------------------------------------------------------------------------------------
//
//    private ReactiveValue<Boolean>      reactiveValue;
//
//
//    // CONSTRUCTORS
//    // ------------------------------------------------------------------------------------------
//
//    public BooleanVariable()
//    {
//        super();
//
//        this.id                     = null;
//
//        this.name                   = new PrimitiveFunctor<>(null, String.class);
//        this.label                  = new PrimitiveFunctor<>(null, String.class);
//        this.description            = new PrimitiveFunctor<>(null, String.class);
//
//        this.literalValue           = new PrimitiveFunctor<>(null, Boolean.class);
//        this.invocationValue        = ModelFunctor.empty(Invocation.class);
//
//        this.kind                   = new PrimitiveFunctor<>(null, Kind.class);
//
//        this.isNamespaced           = new PrimitiveFunctor<>(null, Boolean.class);
//
//        this.tags                   = new PrimitiveFunctor<>(null, String[].class);
//
//        this.reactiveValue          = null;
//    }
//
//
//    /**
//     * Create a Variable. This constructor is private to enforce use of the case specific
//     * constructors, so only valid value/type associations can be used.
//     * @param id The Model id.
//     * @param value The Variable value.
//     * @param kind The Variable kind.
//     */
//    private BooleanVariable(UUID id,
//                            String name,
//                            String label,
//                            String description,
//                            Object value,
//                            Kind kind,
//                            Boolean isNamespaced,
//                            List<String> tags)
//    {
//        super();
//
//        this.id                     = id;
//
//        this.name                   = new PrimitiveFunctor<>(name, String.class);
//        this.label                  = new PrimitiveFunctor<>(label, String.class);
//        this.description            = new PrimitiveFunctor<>(description, String.class);
//
//        this.literalValue           = new PrimitiveFunctor<>(null, Boolean.class);
//        this.invocationValue        = ModelFunctor.full(null, Invocation.class);
//
//        this.kind                   = new PrimitiveFunctor<>(kind, Kind.class);
//
//        if (isNamespaced == null) isNamespaced = false;
//
//        this.isNamespaced           = new PrimitiveFunctor<>(isNamespaced, Boolean.class);
//
//        if (tags != null) {
//            String[] tagsArray = new String[tags.size()];
//            tags.toArray(tagsArray);
//            this.tags               = new PrimitiveFunctor<>(tagsArray, String[].class);
//        }
//        else {
//            this.tags               = new PrimitiveFunctor<>(new String[0], String[].class);
//        }
//
//        // Set value according to variable type
//        switch (kind)
//        {
//            case LITERAL:
//                this.literalValue.setValue((Boolean) value);
//                break;
//            case PROGRAM:
//                this.invocationValue.setValue((Invocation) value);
//                break;
//        }
//
//        initializeBooleanVariable();
//    }
//
//
//    /**
//     * Create a "boolean" valued variable.
//     * @param id The Model id.
//     * @param name The variable name.
//     * @param booleanValue The Boolean value.
//     * @param tags The variable's tags.
//     * @return A new "boolean" variable.
//     */
//    public static BooleanVariable asBoolean(UUID id,
//                                            String name,
//                                            String label,
//                                            String description,
//                                            Boolean booleanValue,
//                                            Boolean isNamespaced,
//                                            List<String> tags)
//    {
//        return new BooleanVariable(id, name, label, description, booleanValue, Kind.LITERAL,
//                                   isNamespaced, tags);
//    }
//
//
//    /**
//     * Create a "boolean" valued variable.
//     * @param id The Model id.
//     * @param booleanValue The Boolean value.
//     * @return A new "boolean" variable.
//     */
//    public static BooleanVariable asBoolean(UUID id,
//                                            Boolean booleanValue)
//    {
//        return new BooleanVariable(id, null, null, null, booleanValue, Kind.LITERAL, null, null);
//    }
//
//
//    /**
//     * Create a "program" valued variable.
//     * @param id The Model id.
//     * @param invocation The Invocation value.
//     * @return A new "program" variable.
//     */
//    public static BooleanVariable asProgram(UUID id,
//                                            String name,
//                                            String label,
//                                            String description,
//                                            Invocation invocation,
//                                            Boolean isNamespaced,
//                                            List<String> tags)
//    {
//        return new BooleanVariable(id, name, label, description, invocation,
//                                   Kind.PROGRAM, isNamespaced, tags);
//    }
//
//
//    /**
//     * Create a new Variable from its Yaml representation.
//     * @param yaml The Yaml parser.
//     * @return The new Variable.
//     * @throws YamlParseException
//     */
//    public static BooleanVariable fromYaml(YamlParser yaml)
//                  throws YamlParseException
//    {
//        if (yaml.isNull())
//            return null;
//
//        UUID         id                 = UUID.randomUUID();
//        String       name               = yaml.atMaybeKey("name").getString();
//        String       label              = yaml.atMaybeKey("label").getString();
//        String       description        = yaml.atMaybeKey("description").getString();
//        Kind         kind               = Kind.fromYaml(yaml.atKey("type"));
//        Boolean      isNamespaced       = yaml.atMaybeKey("namespaced").getBoolean();
//        List<String> tags               = yaml.atMaybeKey("tags").getStringList();
//
//        switch (kind)
//        {
//            case LITERAL:
//                Boolean booleanValue  = yaml.atKey("value").getBoolean();
//                return BooleanVariable.asBoolean(id, name, label, description, booleanValue,
//                                                 isNamespaced, tags);
//            case PROGRAM:
//                Invocation invocation = Invocation.fromYaml(yaml.atKey("value"));
//                return BooleanVariable.asProgram(id, name, label, description, invocation,
//                                                 isNamespaced, tags);
//        }
//
//        // CANNOT REACH HERE. If VariableKind is null, an InvalidEnum exception would be thrown.
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
//    public void onLoad()
//    {
//        initializeBooleanVariable();
//    }
//
//
//    // > To Yaml
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * The Boolean Variable's yaml representation.
//     * @return The Yaml Builder.
//     */
//    public YamlBuilder toYaml()
//    {
//        return YamlBuilder.map()
//                .putString("name", this.name())
//                .putString("label", this.label())
//                .putString("description", this.description())
//                .putYaml("type", this.kind())
//                .putBoolean("namespaced", this.isNamespaced())
//                .putStringList("tags", this.tags());
//    }
//
//
//    // > Variable
//    // ------------------------------------------------------------------------------------------
//
//    @Override
//    public String name()
//    {
//        return this.name.getValue();
//    }
//
//
//    @Override
//    public void setName(String name)
//    {
//        this.name.setValue(name);
//    }
//
//
//    @Override
//    public String label()
//    {
//        return this.label.getValue();
//    }
//
//
//    @Override
//    public void setLabel(String label)
//    {
//        this.label.setValue(label);
//    }
//
//
//    @Override
//    public String description()
//    {
//        return this.description.getValue();
//    }
//
//
//    @Override
//    public boolean isNamespaced()
//    {
//        return this.isNamespaced.getValue();
//    }
//
//
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
//
//    @Override
//    public List<String> tags()
//    {
//        return Arrays.asList(this.tags.getValue());
//    }
//
//
//    @Override
//    public String valueString()
//    {
//        if (this.value())
//            return "True";
//        else
//            return "False";
//    }
//
//
//    @Override
//    public void initialize()
//    {
//        // [1] Add to state
//        // --------------------------------------------------------------------------------------
//        this.addToState();
//
//        // [2] Save original name and label values in case namespaces changes multiple times
//        // --------------------------------------------------------------------------------------
//        this.originalName  = name();
//        this.originalLabel = label();
//    }
//
//
//    // > State
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * The program invocation case.
//     * @return The invocation.
//     */
//    private Invocation invocation()
//    {
//        return this.invocationValue.getValue();
//    }
//
//
//    // ** Kind
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * The Boolean Variable kind.
//     * @return The Kind.
//     */
//    public Kind kind()
//    {
//        return this.kind.getValue();
//    }
//
//
//    // ** Cases
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * The literal case.
//     * @return The boolean value.
//     */
//    public Boolean literalValue()
//    {
//        return this.literalValue.getValue();
//    }
//
//
//    // ** Value
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * Set the boolean variable integer. value
//     * @param newValue The boolean value.
//     */
//    public void setValue(Boolean newValue)
//    {
//        switch (this.kind.getValue())
//        {
//            case LITERAL:
//                this.literalValue.setValue(newValue);
//                this.onUpdate();
//                break;
//            case PROGRAM:
//                break;
//        }
//    }
//
//
//    /**
//     * Get the boolean variable's integer value.
//     * @return The boolean value.
//     */
//    public Boolean value()
//    {
//        switch (this.kind.getValue())
//        {
//            case LITERAL:
//                return this.literalValue.getValue();
//            case PROGRAM:
//                return this.reactiveValue.value();
//        }
//
//        return null;
//    }
//
//
//    // > Initialize
//    // ------------------------------------------------------------------------------------------
//
//    public void initializeBooleanVariable()
//    {
//        // [1] Create reaction value (if program variable)
//        // --------------------------------------------------------------------------------------
//
//        if (this.kind.getValue() == Kind.PROGRAM) {
//            this.reactiveValue = new ReactiveValue<>(this.invocationValue.getValue(),
//                                                     VariableType.NUMBER);
//        }
//        else {
//            this.reactiveValue = null;
//        }
//
//    }
//
//
//    // INTERNAL
//    // ------------------------------------------------------------------------------------------
//
//    // ** Variable State
//    // ------------------------------------------------------------------------------------------
//
//    /**
//     * Add any variables associated with the current value to the state.
//     */
//    private void addToState()
//    {
//    }
//
//
//    private void removeFromState()
//    {
//    }
//
//
//    // KIND
//    // ------------------------------------------------------------------------------------------
//
//    public enum Kind implements ToYaml
//    {
//
//        // VALUES
//        // ------------------------------------------------------------------------------------------
//
//        LITERAL,
//        PROGRAM;
//
//
//        // CONSTRUCTORS
//        // ------------------------------------------------------------------------------------------
//
//        public static Kind fromString(String kindString)
//                      throws InvalidDataException
//        {
//            return EnumUtils.fromString(Kind.class, kindString);
//        }
//
//
//        public static Kind fromYaml(YamlParser yaml)
//                      throws YamlParseException
//        {
//            String kindString = yaml.getString();
//            try {
//                return Kind.fromString(kindString);
//            } catch (InvalidDataException e) {
//                throw YamlParseException.invalidEnum(new InvalidEnumError(kindString));
//            }
//        }
//
//
//        public static Kind fromSQLValue(SQLValue sqlValue)
//                      throws DatabaseException
//        {
//            String enumString = "";
//            try {
//                enumString = sqlValue.getText();
//                Kind kind = Kind.fromString(enumString);
//                return kind;
//            } catch (InvalidDataException e) {
//                throw DatabaseException.invalidEnum(
//                        new com.kispoko.tome.lib.database.error.InvalidEnumError(enumString));
//            }
//        }
//
//
//        // TO YAML
//        // ------------------------------------------------------------------------------------------
//
//        public YamlBuilder toYaml()
//        {
//            return YamlBuilder.string(this.name().toLowerCase());
//        }
//
//
//        // TO STRING
//        // ------------------------------------------------------------------------------------------
//
//        @Override
//        public String toString()
//        {
//            switch (this)
//            {
//                case LITERAL:
//                    return "Literal";
//                case PROGRAM:
//                    return "Program";
//            }
//
//            return "";
//        }
//
//    }
//
//}
