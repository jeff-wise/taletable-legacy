
package com.kispoko.tome.engine.variable;


import com.kispoko.tome.ApplicationFailure;
import com.kispoko.tome.R;
import com.kispoko.tome.engine.State;
import com.kispoko.tome.engine.value.Dictionary;
import com.kispoko.tome.engine.value.NumberValue;
import com.kispoko.tome.engine.value.ValueReference;
import com.kispoko.tome.engine.variable.error.UndefinedVariableError;
import com.kispoko.tome.engine.variable.error.UnexpectedVariableTypeError;
import com.kispoko.tome.error.InvalidCaseError;
import com.kispoko.tome.error.UnknownVariantError;
import com.kispoko.tome.exception.InvalidDataException;
import com.kispoko.tome.engine.program.invocation.Invocation;
import com.kispoko.tome.engine.summation.Summation;
import com.kispoko.tome.exception.UnionException;
import com.kispoko.tome.lib.database.DatabaseException;
import com.kispoko.tome.lib.functor.OptionFunctor;
import com.kispoko.tome.sheet.SheetManager;
import com.kispoko.tome.util.EnumUtils;
import com.kispoko.tome.lib.database.sql.SQLValue;
import com.kispoko.tome.lib.functor.ModelFunctor;
import com.kispoko.tome.lib.functor.PrimitiveFunctor;
import com.kispoko.tome.lib.yaml.ToYaml;
import com.kispoko.tome.lib.yaml.YamlBuilder;
import com.kispoko.tome.lib.yaml.YamlParser;
import com.kispoko.tome.lib.yaml.YamlParseException;
import com.kispoko.tome.lib.yaml.error.InvalidEnumError;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;



/**
 * Number Variable
 */
public class NumberVariable extends Variable
                            implements ToYaml, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID                            id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private PrimitiveFunctor<String>        name;
    private PrimitiveFunctor<String>        label;
    private PrimitiveFunctor<String>        description;

    private PrimitiveFunctor<Integer>       literalValue;
    private PrimitiveFunctor<String>        variableReference;
    private ModelFunctor<Invocation>        invocationValue;
    private ModelFunctor<ValueReference>    valueReference;
    private ModelFunctor<Summation>         summation;

    private OptionFunctor<Kind>             kind;

    private PrimitiveFunctor<Boolean>       isNamespaced;

    private PrimitiveFunctor<String[]>      tags;


    // > Internal
    // ------------------------------------------------------------------------------------------

    private ReactiveValue<Integer>          reactiveValue;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public NumberVariable()
    {
        super();

        this.id                 = null;

        this.name               = new PrimitiveFunctor<>(null, String.class);
        this.label              = new PrimitiveFunctor<>(null, String.class);
        this.description        = new PrimitiveFunctor<>(null, String.class);

        this.literalValue       = new PrimitiveFunctor<>(null, Integer.class);
        this.variableReference  = new PrimitiveFunctor<>(null, String.class);
        this.invocationValue    = ModelFunctor.empty(Invocation.class);
        this.valueReference     = ModelFunctor.empty(ValueReference.class);
        this.summation          = ModelFunctor.empty(Summation.class);

        this.kind               = new OptionFunctor<>(null, Kind.class);

        this.isNamespaced       = new PrimitiveFunctor<>(null, Boolean.class);

        this.tags               = new PrimitiveFunctor<>(null, String[].class);

        this.reactiveValue      = null;

        this.initializeFunctors();
    }


    /**
     * Create a Variable. This constructor is private to enforce use of the case specific
     * constructors, so only valid value/type associations can be used.
     * @param id The Model id.
     * @param value The Variable value.
     * @param kind The Number Variable kind.
     */
    private NumberVariable(UUID id,
                           String name,
                           String label,
                           String description,
                           Object value,
                           Kind kind,
                           Boolean isNamespaced,
                           List<String> tags)
    {
        super();

        this.id                     = id;

        this.name                   = new PrimitiveFunctor<>(name, String.class);
        this.label                  = new PrimitiveFunctor<>(label, String.class);
        this.description            = new PrimitiveFunctor<>(description, String.class);

        this.literalValue           = new PrimitiveFunctor<>(null, Integer.class);
        this.variableReference      = new PrimitiveFunctor<>(null, String.class);
        this.invocationValue        = ModelFunctor.full(null, Invocation.class);
        this.valueReference         = ModelFunctor.full(null, ValueReference.class);
        this.summation              = ModelFunctor.full(null, Summation.class);

        this.kind                   = new OptionFunctor<>(kind, Kind.class);

        if (isNamespaced == null) isNamespaced = false;
        this.isNamespaced           = new PrimitiveFunctor<>(isNamespaced, Boolean.class);

        if (tags != null) {
            String[] tagsArray = new String[tags.size()];
            tags.toArray(tagsArray);
            this.tags               = new PrimitiveFunctor<>(tagsArray, String[].class);
        }
        else {
            this.tags               = new PrimitiveFunctor<>(new String[0], String[].class);
        }

        // Set value according to variable type
        switch (kind)
        {
            case LITERAL:
                this.literalValue.setValue((Integer) value);
                break;
            case VARIABLE:
                this.variableReference.setValue((String) value);
                break;
            case PROGRAM:
                this.invocationValue.setValue((Invocation) value);
                break;
            case VALUE:
                this.valueReference.setValue((ValueReference) value);
                break;
            case SUMMATION:
                this.summation.setValue((Summation) value);
                break;
        }

        this.initializeFunctors();
        this.initializeNumberVariable();
    }


    /**
     * Create a "literal" number variable that contains a value of type Integer.
     * @param id The Model id.
     * @param name The variable name
     * @param integerValue The Integer value.
     * @param tags The variable's tags.
     * @return A new "literal" Integer Variable.
     */
    public static NumberVariable asInteger(UUID id,
                                           String name,
                                           String label,
                                           String description,
                                           Integer integerValue,
                                           Boolean isNamespaced,
                                           List<String> tags)
    {
        return new NumberVariable(id, name, label, description, integerValue, Kind.LITERAL,
                                  isNamespaced, tags);
    }


    /**
     * Create a "literal" number variable that contains a value of type Integer.
     * @param id The Model id.
     * @param integerValue The Integer value.
     * @return A new "literal" Integer Variable.
     */
    public static NumberVariable asInteger(UUID id,
                                           Integer integerValue)
    {
        return new NumberVariable(id, null, null, null, integerValue, Kind.LITERAL, null, null);
    }


    /**
     * Create a "variable" number.
     * @param id The Model id.
     * @param name The variable name
     * @param variableName The variable name.
     * @param tags The variable's tags.
     * @return A new "literal" Integer Variable.
     */
    public static NumberVariable asVariable(UUID id,
                                           String name,
                                           String label,
                                           String description,
                                           String variableName,
                                           Boolean isNamespaced,
                                           List<String> tags)
    {
        return new NumberVariable(id, name, label, description, variableName, Kind.VARIABLE,
                                  isNamespaced, tags);
    }


    /**
     * Create a "program" valued variable.
     * @param id The Model id.
     * @param invocation The Invocation value.
     * @return A new "program" variable.
     */
    public static NumberVariable asProgram(UUID id,
                                           String name,
                                           String label,
                                           String description,
                                           Invocation invocation,
                                           Boolean isNamespaced,
                                           List<String> tags)
    {
        return new NumberVariable(id, name, label, description, invocation, Kind.PROGRAM,
                                  isNamespaced, tags);
    }


    /**
     * Create a "value" variable.
     * @param id The Model id.
     * @param valueReference The value reference.
     * @return A new "value" variable.
     */
    public static NumberVariable asValue(UUID id,
                                         String name,
                                         String label,
                                         String description,
                                         ValueReference valueReference,
                                         Boolean isNamespaced,
                                         List<String> tags)
    {
        return new NumberVariable(id, name, label, description, valueReference, Kind.VALUE,
                                  isNamespaced, tags);
    }


    /**
     * Create the "summation" case.
     * @param id The Model id.
     * @param summation The summation.
     * @return The new number variable as a summation.
     */
    public static NumberVariable asSummation(UUID id,
                                             String name,
                                             String label,
                                             String description,
                                             Summation summation,
                                             Boolean isNamespaced,
                                             List<String> tags)
    {
        return new NumberVariable(id, name, label, description, summation, Kind.SUMMATION,
                                  isNamespaced, tags);
    }


    /**
     * Create a new Variable from its Yaml representation.
     * @param yaml The Yaml parser.
     * @return The new Variable.
     * @throws YamlParseException
     */
    public static NumberVariable fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        if (yaml.isNull())
            return null;

        UUID         id           = UUID.randomUUID();
        String       name         = yaml.atMaybeKey("name").getString();
        String       label        = yaml.atMaybeKey("label").getString();
        String       description  = yaml.atMaybeKey("description").getString();
        Kind         kind         = Kind.fromYaml(yaml.atKey("type"));
        Boolean      isNamespaced = yaml.atMaybeKey("namespaced").getBoolean();
        List<String> tags         = yaml.atMaybeKey("tags").getStringList();

        switch (kind)
        {
            case LITERAL:
                Integer integerValue  = yaml.atKey("value").getInteger();
                return NumberVariable.asInteger(id, name, label, description, integerValue,
                                                isNamespaced, tags);
            case VARIABLE:
                String variableName = yaml.atKey("value").getString();
                return NumberVariable.asVariable(id, name, label, description, variableName,
                                                 isNamespaced, tags);
            case PROGRAM:
                Invocation invocation = Invocation.fromYaml(yaml.atKey("value"));
                return NumberVariable.asProgram(id, name, label, description, invocation,
                                                isNamespaced, tags);
            case VALUE:
                ValueReference valueReference = ValueReference.fromYaml(yaml.atKey("value"));
                return NumberVariable.asValue(id, name, label, description, valueReference,
                                              isNamespaced, tags);
            case SUMMATION:
                Summation summation = Summation.fromYaml(yaml.atKey("value"));
                return NumberVariable.asSummation(id, name, label, description, summation,
                                                  isNamespaced, tags);
        }

        return null;
    }


    // API
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    // ** Id
    // ------------------------------------------------------------------------------------------

    /**
     * Get the model identifier.
     * @return The model UUID.
     */
    public UUID getId()
    {
        return this.id;
    }


    /**
     * Set the model identifier.
     * @param id The new model UUID.
     */
    public void setId(UUID id)
    {
        this.id = id;
    }


    // ** On Load
    // ------------------------------------------------------------------------------------------

    public void onLoad()
    {
        this.initializeNumberVariable();
    }


    // > To Yaml
    // ------------------------------------------------------------------------------------------

    /**
     * The Number Variable's yaml representation.
     * @return The Yaml Builder.
     */
    public YamlBuilder toYaml()
    {
        return YamlBuilder.map()
                .putString("name", this.name())
                .putString("label", this.label())
                .putString("description", this.description())
                .putYaml("type", this.kind())
                .putBoolean("namespaced", this.isNamespaced())
                .putStringList("tags", this.tags());
    }

    // > State
    // ------------------------------------------------------------------------------------------

    /**
     * The literal value case.
     * @return The literal integer value.
     */
    public Integer literalValue()
    {
        if (this.kind() != Kind.LITERAL) {
            ApplicationFailure.union(
                    UnionException.invalidCase(
                            new InvalidCaseError("literal", this.kind.name())));
        }

        return this.literalValue.getValue();
    }


    /**
     * The variable reference value.
     * @return The variable reference.
     */
    public String variableReference()
    {
        if (this.kind() != Kind.VARIABLE) {
            ApplicationFailure.union(
                    UnionException.invalidCase(
                            new InvalidCaseError("variable", this.kind.name())));
        }

        return this.variableReference.getValue();
    }


    /**
     * The invocation case.
     * @return The invocation.
     */
    public Invocation invocation()
    {
        if (this.kind() != Kind.PROGRAM) {
            ApplicationFailure.union(
                    UnionException.invalidCase(
                            new InvalidCaseError("program", this.kind.name())));
        }

        return this.invocationValue.getValue();
    }


    public ValueReference valueReference()
    {
        if (this.kind() != Kind.VALUE) {
            ApplicationFailure.union(
                    UnionException.invalidCase(
                            new InvalidCaseError("value", this.kind.name())));
        }

        return this.valueReference.getValue();
    }


    /**
     * The summation case.
     * @return The summation.
     */
    public Summation summation()
    {
        if (this.kind() != Kind.SUMMATION) {
            ApplicationFailure.union(
                    UnionException.invalidCase(
                            new InvalidCaseError("summation", this.kind.name())));
        }

        return this.summation.getValue();
    }


    // > Variable
    // ------------------------------------------------------------------------------------------

    /**
     * Get the variable name which is a unique identifier.
     * @return The variable name.
     */
    @Override
    public String name()
    {
        return this.name.getValue();

    }


    @Override
    public void setName(String name)
    {
        this.name.setValue(name);
    }


    @Override
    public String label()
    {
        return this.label.getValue();
    }


    @Override
    public void setLabel(String label)
    {
        this.label.setValue(label);
    }


    @Override
    public String description()
    {
        return this.description.getValue();
    }


    @Override
    public boolean isNamespaced()
    {
        return this.isNamespaced.getValue();
    }


    @Override
    public void setIsNamespaced(Boolean isNamespaced)
    {
        if (isNamespaced != null)
            this.isNamespaced.setValue(isNamespaced);
        else
            this.isNamespaced.setValue(false);
    }


    @Override
    public List<VariableReference> dependencies()
    {
        List<VariableReference> variableDependencies = new ArrayList<>();

        switch (this.kind.getValue())
        {
            case LITERAL:
                break;
            case VARIABLE:
                break;
            case PROGRAM:
                variableDependencies = this.invocation().variableDependencies();
                break;
            case VALUE:
                break;
            case SUMMATION:
                variableDependencies = this.summation().variableDependencies();
                break;
            default:
                ApplicationFailure.union(
                        UnionException.unknownVariant(
                                new UnknownVariantError(Kind.class.getName())));
        }

        return variableDependencies;
    }


    /**
     * The variable's tags.
     * @return The tag list.
     */
    @Override
    public List<String> tags()
    {
        return Arrays.asList(this.tags.getValue());
    }


    /**
     * Get the value string representation. If the value contains any dice rolls, then it appears
     * as a formula, otherwise it is just an integer string.
     * @return The value string.
     * @throws VariableException
     */
    public String valueString()
           throws NullVariableException
    {
        switch (this.kind())
        {
            case LITERAL:
                return this.value().toString();
            case VARIABLE:
                return this.value().toString();
            case PROGRAM:
                return this.value().toString();
            case VALUE:
                return this.value().toString();
            case SUMMATION:
                return this.summation().valueString();
            default:
                ApplicationFailure.union(
                        UnionException.unknownVariant(
                                new UnknownVariantError(Kind.class.getName())));
        }

        return "";
    }


    @Override
    public void initialize()
    {
        // [1] Add to state
        // --------------------------------------------------------------------------------------
        this.addToState();

        // [2] Save original name and label values in case namespaces changes multiple times
        // --------------------------------------------------------------------------------------
        this.originalName  = name();
        this.originalLabel = label();
    }


    // > State
    // ------------------------------------------------------------------------------------------

    // ** Kind
    // ------------------------------------------------------------------------------------------

    /**
     * Get the kind of number variable.
     * @return The number variable kind.
     */
    public Kind kind()
    {
        return this.kind.getValue();
    }


    // ** Cases
    // ------------------------------------------------------------------------------------------



    // ** Value
    // ------------------------------------------------------------------------------------------

    /**
     * Set the number variable integer. value
     * @param newValue The integer value.
     */
    public void setValue(Integer newValue)
    {
        switch (this.kind.getValue())
        {
            case LITERAL:
                this.literalValue.setValue(newValue);
                this.onUpdate();
                break;
            case VARIABLE:
                break;
            case PROGRAM:
                // Do Nothing?
                //this.reactiveValue.setLiteralValue(newValue);
                break;
            case VALUE:
                break;
            case SUMMATION:
                // Do Nothing?
                break;
        }
    }


    /**
     * Get the number variable's integer value.
     * @return The integer value.
     */
    public Integer value()
           throws NullVariableException
    {
        switch (this.kind.getValue())
        {
            case LITERAL:
                return this.literalValue.getValue();
            case VARIABLE:
                return referencedVariableValue();
            case PROGRAM:
                return this.reactiveValue.value();
            case VALUE:
                Dictionary dictionary = SheetManager.currentSheet().engine().dictionary();
                NumberValue numberValue = dictionary.numberValue(this.valueReference());
                return numberValue.value();
            case SUMMATION:
                return this.summation().value();
        }

        throw new NullVariableException();
    }


    private Integer referencedVariableValue()
            throws NullVariableException
    {
        if (!State.hasVariable(this.variableReference())) {
            ApplicationFailure.variable(
                    VariableException.undefinedVariable(
                            new UndefinedVariableError(this.variableReference())));
            throw new NullVariableException();
        }

        VariableUnion variableUnion = State.variableWithName(this.variableReference());

        // Variable is wrong type, log error, and return as null variable exception
        if (variableUnion.type() != VariableType.NUMBER) {
            ApplicationFailure.variable(
                    VariableException.unexpectedVariableType(
                            new UnexpectedVariableTypeError(this.variableReference(),
                                                            VariableType.NUMBER,
                                                            variableUnion.type())));
            throw new NullVariableException();
        }

        return variableUnion.numberVariable().value();
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    // ** Initialize
    // ------------------------------------------------------------------------------------------

    private void initializeNumberVariable()
    {
        // [1] Create reaction value (if program variable)
        // --------------------------------------------------------------------------------------

        if (this.kind.getValue() == Kind.PROGRAM) {
            this.reactiveValue = new ReactiveValue<>(this.invocationValue.getValue(),
                                                     VariableType.NUMBER);
        }
        else {
            this.reactiveValue = null;
        }
    }


    private void initializeFunctors()
    {
        // Name
        this.name.setName("name");
        this.name.setLabelId(R.string.variable_field_name_label);
        this.name.setDescriptionId(R.string.variable_field_name_description);

        // Label
        this.label.setName("label");
        this.label.setLabelId(R.string.variable_field_label_label);
        this.label.setDescriptionId(R.string.variable_field_label_description);

        // Description
        this.description.setName("description");
        this.description.setLabelId(R.string.variable_field_description_label);
        this.description.setDescriptionId(R.string.variable_field_description_description);

        // Is Namespaced
        this.isNamespaced.setName("is_namespaced");
        this.isNamespaced.setLabelId(R.string.variable_field_is_namespaced_label);
        this.isNamespaced.setDescriptionId(R.string.variable_field_is_namespaced_description);

        // Tags
        this.tags.setName("tags");
        this.tags.setLabelId(R.string.variable_field_tags_label);
        this.tags.setDescriptionId(R.string.variable_field_tags_description);

        // Kind
        this.kind.setName("kind");
        this.kind.setLabelId(R.string.variable_field_kind_label);
        this.kind.setDescriptionId(R.string.variable_field_kind_description);

        // Literal Value
        this.literalValue.setName("kind_literal");
        this.literalValue.setLabelId(R.string.number_variable_field_value_literal_label);
        this.literalValue.setDescriptionId(R.string.number_variable_field_value_literal_description);
        this.literalValue.caseOf("kind", "literal");

        // Variable Value
        this.variableReference.setName("kind_variable");
        this.variableReference.setLabelId(R.string.number_variable_field_value_variable_label);
        this.variableReference.setDescriptionId(R.string.number_variable_field_value_variable_description);
        this.variableReference.caseOf("kind", "variable");

        // Program Value
        this.invocationValue.setName("kind_program");
        this.invocationValue.setLabelId(R.string.number_variable_field_value_program_label);
        this.invocationValue.setDescriptionId(R.string.number_variable_field_value_program_description);
        this.invocationValue.caseOf("kind", "program");

        // Value (From Value Set) Value
        this.valueReference.setName("kind_value");
        this.valueReference.setLabelId(R.string.number_variable_field_value_value_label);
        this.valueReference.setDescriptionId(R.string.number_variable_field_value_value_description);
        this.valueReference.caseOf("kind", "value");

        // Summation Value
        this.summation.setName("kind_summation");
        this.summation.setLabelId(R.string.number_variable_field_value_summation_label);
        this.summation.setDescriptionId(R.string.number_variable_field_value_summation_description);
        this.summation.caseOf("kind", "summation");
    }


    // ** Variable State
    // ------------------------------------------------------------------------------------------

    /**
     * Add any variables associated with the current value to the state.
     */
    private void addToState()
    {
        if (this.kind() != Kind.VALUE)
            return;

        Dictionary dictionary = SheetManager.currentSheet().engine().dictionary();
        dictionary.numberValue(this.valueReference()).addToState();
    }


    private void removeFromState()
    {
        if (this.kind() != Kind.VALUE)
            return;

        Dictionary dictionary = SheetManager.currentSheet().engine().dictionary();
        dictionary.numberValue(this.valueReference()).removeFromState();
    }


    // KIND
    // ------------------------------------------------------------------------------------------

    public enum Kind implements ToYaml
    {

        // VALUES
        // ------------------------------------------------------------------------------------------

        LITERAL,
        VARIABLE,
        PROGRAM,
        VALUE,
        SUMMATION;


        // CONSTRUCTORS
        // ------------------------------------------------------------------------------------------

        public static Kind fromString(String kindString)
                      throws InvalidDataException
        {
            return EnumUtils.fromString(Kind.class, kindString);
        }


        public static Kind fromYaml(YamlParser yaml)
                      throws YamlParseException
        {
            String kindString = yaml.getString();
            try {
                return Kind.fromString(kindString);
            } catch (InvalidDataException e) {
                throw YamlParseException.invalidEnum(new InvalidEnumError(kindString));
            }
        }


        public static Kind fromSQLValue(SQLValue sqlValue)
                      throws DatabaseException
        {
            String enumString = "";
            try {
                enumString = sqlValue.getText();
                Kind kind = Kind.fromString(enumString);
                return kind;
            } catch (InvalidDataException e) {
                throw DatabaseException.invalidEnum(
                        new com.kispoko.tome.lib.database.error.InvalidEnumError(enumString));
            }
        }


        // TO YAML
        // ------------------------------------------------------------------------------------------

        public YamlBuilder toYaml()
        {
            return YamlBuilder.string(this.name().toLowerCase());
        }


        // TO STRING
        // ------------------------------------------------------------------------------------------

        @Override
        public String toString()
        {
            switch (this)
            {
                case LITERAL:
                    return "Literal";
                case VARIABLE:
                    return "Variable";
                case PROGRAM:
                    return "Program";
                case VALUE:
                    return "Value";
                case SUMMATION:
                    return "Summation";
            }

            return "";
        }

    }



}
