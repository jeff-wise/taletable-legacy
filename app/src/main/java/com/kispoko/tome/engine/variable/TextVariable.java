
package com.kispoko.tome.engine.variable;


import com.kispoko.tome.engine.State;
import com.kispoko.tome.engine.value.Dictionary;
import com.kispoko.tome.engine.value.ValueReference;
import com.kispoko.tome.exception.InvalidDataException;
import com.kispoko.tome.engine.program.invocation.Invocation;
import com.kispoko.tome.sheet.SheetManager;
import com.kispoko.tome.util.EnumUtils;
import com.kispoko.tome.util.database.DatabaseException;
import com.kispoko.tome.util.database.sql.SQLValue;
import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.value.ModelFunctor;
import com.kispoko.tome.util.value.PrimitiveFunctor;
import com.kispoko.tome.util.yaml.ToYaml;
import com.kispoko.tome.util.yaml.YamlBuilder;
import com.kispoko.tome.util.yaml.YamlParser;
import com.kispoko.tome.util.yaml.YamlParseException;
import com.kispoko.tome.util.yaml.error.InvalidEnumError;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;



/**
 * Text Variable
 */
public class TextVariable extends Variable
                          implements Model, ToYaml, Serializable
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

    private PrimitiveFunctor<String>        stringLiteral;
    private ModelFunctor<ValueReference>    valueReference;
    private ModelFunctor<Invocation>        invocation;

    private PrimitiveFunctor<Kind>          kind;

    private PrimitiveFunctor<String>        valueSetName;

    private PrimitiveFunctor<Boolean>       isNamespaced;
    private PrimitiveFunctor<Boolean>       definesNamespace;

    private PrimitiveFunctor<String[]>      tags;


    // > Internal
    // ------------------------------------------------------------------------------------------

    private ReactiveValue<String>           reactiveValue;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public TextVariable()
    {
        super();

        this.id                     = null;

        this.name                   = new PrimitiveFunctor<>(null, String.class);
        this.label                  = new PrimitiveFunctor<>(null, String.class);

        this.stringLiteral          = new PrimitiveFunctor<>(null, String.class);
        this.valueReference         = ModelFunctor.empty(ValueReference.class);
        this.invocation             = ModelFunctor.empty(Invocation.class);

        this.kind                   = new PrimitiveFunctor<>(null, Kind.class);

        this.valueSetName           = new PrimitiveFunctor<>(null, String.class);

        this.isNamespaced           = new PrimitiveFunctor<>(null, Boolean.class);
        this.definesNamespace       = new PrimitiveFunctor<>(null, Boolean.class);

        this.tags                   = new PrimitiveFunctor<>(null, String[].class);

        this.reactiveValue          = null;
    }


    /**
     * Create a Variable. This constructor is private to enforce use of the case specific
     * constructors, so only valid value/kind associations can be used.
     * @param id The Model id.
     * @param value The Variable value.
     * @param kind The Variable kind.
     */
    private TextVariable(UUID id,
                         String name,
                         String label,
                         Object value,
                         Kind kind,
                         String valueSetName,
                         Boolean isNamespaced,
                         Boolean definesNamespace,
                         List<String> tags)
    {
        // ** Variable Constructor
        super();

        // ** Id
        this.id                     = id;

        // ** Name
        this.name                   = new PrimitiveFunctor<>(name, String.class);

        // ** Label
        this.label                  = new PrimitiveFunctor<>(label, String.class);

        // ** Value Variants
        this.stringLiteral          = new PrimitiveFunctor<>(null, String.class);
        this.valueReference         = ModelFunctor.full(null, ValueReference.class);
        this.invocation             = ModelFunctor.full(null, Invocation.class);

        // ** Kind (Literal or Program)
        this.kind                   = new PrimitiveFunctor<>(kind, Kind.class);

        // ** Value Set Name (if any)
        this.valueSetName           = new PrimitiveFunctor<>(valueSetName, String.class);

        // ** Namespace
        if (isNamespaced == null) isNamespaced = false;
        if (definesNamespace == null) definesNamespace = false;

        this.isNamespaced           = new PrimitiveFunctor<>(isNamespaced, Boolean.class);
        this.definesNamespace       = new PrimitiveFunctor<>(definesNamespace, Boolean.class);

        // ** Tags
        if (tags != null) {
            String[] tagsArray = new String[tags.size()];
            tags.toArray(tagsArray);
            this.tags               = new PrimitiveFunctor<>(tagsArray, String[].class);
        }
        else {
            this.tags               = new PrimitiveFunctor<>(new String[0], String[].class);
        }

        // > Set the value according to variable kind
        switch (kind)
        {
            case LITERAL:
                this.stringLiteral.setValue((String) value);
                break;
            case VALUE:
                this.valueReference.setValue((ValueReference) value);
                break;
            case PROGRAM:
                this.invocation.setValue((Invocation) value);
                break;
        }

        this.initializeTextVariable();
    }


    /**
     * Create a "literal" text variable, that contains a value of kind String.
     * @param id The Model id.
     * @param stringValue The String value.
     * @return A new "literal" Text Variable.
     */
    public static TextVariable asText(UUID id,
                                      String name,
                                      String label,
                                      String stringValue,
                                      Boolean isNamespaced,
                                      Boolean definesNamespace,
                                      List<String> tags)
    {
        return new TextVariable(id, name, label, stringValue, Kind.LITERAL, null,
                                isNamespaced, definesNamespace, tags);
    }


    /**
     * Create a "literal" text variable, that contains a value of kind String.
     * @param id The Model id.
     * @param stringValue The String value.
     * @return A new "literal" Text Variable.
     */
    public static TextVariable asText(UUID id,
                                      String stringValue)
    {
        return new TextVariable(id, null, null, stringValue, Kind.LITERAL, null, null, null, null);
    }


    /**
     * Create the "value" case.
     * @param id The model id.
     * @param name The variable name.
     * @param valueReference The value reference.
     * @param valueSetName The value set the variable is restricted to (if any).
     * @param tags The variable tags.
     * @return The "value" Text Variable.
     */
    public static TextVariable asValue(UUID id,
                                       String name,
                                       String label,
                                       ValueReference valueReference,
                                       String valueSetName,
                                       Boolean isNamespaced,
                                       Boolean definesNamespace,
                                       List<String> tags)
    {
        return new TextVariable(id, name, label, valueReference, Kind.VALUE,
                                valueSetName, isNamespaced, definesNamespace, tags);
    }


    /**
     * Create a "program" valued variable.
     * @param id The Model id.
     * @param invocation The Invocation value.
     * @return A new "program" variable.
     */
    public static TextVariable asProgram(UUID id,
                                         String name,
                                         String label,
                                         Invocation invocation,
                                         Boolean isNamespaced,
                                         Boolean definesNamespace,
                                         List<String> tags)
    {
        return new TextVariable(id, name, label, invocation, Kind.PROGRAM, null,
                                isNamespaced, definesNamespace, tags);
    }


    /**
     * Create a new Variable from its Yaml representation.
     * @param yaml The Yaml parser.
     * @return The new Variable.
     * @throws YamlParseException
     */
    public static TextVariable fromYaml(YamlParser yaml)
                  throws YamlParseException
    {
        if (yaml.isNull())
            return null;

        UUID         id                 = UUID.randomUUID();

        String       name               = yaml.atMaybeKey("name").getString();
        String       label              = yaml.atMaybeKey("label").getString();
        Kind         kind               = Kind.fromYaml(yaml.atKey("type"));
        String       valueSetName       = yaml.atMaybeKey("value_set").getString();
        Boolean      isNamespaced       = yaml.atMaybeKey("namespaced").getBoolean();
        Boolean      definesNamespace   = yaml.atMaybeKey("defines_namespace").getBoolean();
        List<String> tags               = yaml.atMaybeKey("tags").getStringList();

        switch (kind)
        {
            case LITERAL:
                String stringValue = yaml.atKey("value").getString();
                return TextVariable.asText(id, name, label, stringValue, isNamespaced,
                                           definesNamespace, tags);
            case VALUE:
                ValueReference valueReference = ValueReference.fromYaml(yaml.atKey("value"));
                return TextVariable.asValue(id, name, label, valueReference, valueSetName,
                                            isNamespaced, definesNamespace, tags);
            case PROGRAM:
                Invocation invocation = Invocation.fromYaml(yaml.atKey("value"));
                return TextVariable.asProgram(id, name, label, invocation, isNamespaced,
                                              definesNamespace, tags);
        }

        // CANNOT REACH HERE. If VariableKind is null, an InvalidEnum exception would be thrown.
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
        this.initializeTextVariable();
    }


    // > To Yaml
    // ------------------------------------------------------------------------------------------

    /**
     * The Text Variable's yaml representation.
     * @return The Yaml Builder.
     */
    public YamlBuilder toYaml()
    {
        return YamlBuilder.map()
                .putString("name", this.name())
                .putString("label", this.label())
                .putYaml("type", this.kind())
                .putString("value_set", this.valueSetName())
                .putBoolean("namespaced", this.isNamespaced())
                .putBoolean("defines_namespace", this.definesNamespace())
                .putStringList("tags", this.tags());
    }


    // > Variable
    // ------------------------------------------------------------------------------------------

    @Override
    public String name()
    {
        return this.name.getValue();
    }


    @Override
    public String label()
    {
        return this.label.getValue();
    }


    @Override
    public void setName(String name)
    {
        // > Set the name
        String oldName = this.name();
        this.name.setValue(name);

        // > Reindex variable
        State.removeVariable(oldName);
        State.addVariable(this);
    }


    @Override
    public boolean isNamespaced()
    {
        return this.isNamespaced.getValue();
    }


    public boolean definesNamespace()
    {
        return this.definesNamespace.getValue();
    }


    @Override
    public List<VariableReference> dependencies()
    {
        List<VariableReference> variableDependencies = new ArrayList<>();

        if (this.kind.getValue() == Kind.PROGRAM) {
            variableDependencies = this.invocation().variableDependencies();
        }

        return variableDependencies;
    }


    @Override
    public List<String> tags()
    {
        return Arrays.asList(this.tags.getValue());
    }


    @Override
    public String valueString()
    {
        return this.value();
    }


    // > State
    // ------------------------------------------------------------------------------------------


    // ** Kind
    // ------------------------------------------------------------------------------------------

    /**
     * The text variable kind.
     * @return The kind.
     */
    public Kind kind()
    {
        return this.kind.getValue();
    }


    // ** Variants
    // ------------------------------------------------------------------------------------------

    /**
     * The string literal case.
     * @return The string literal.
     */
    public String stringLiteral()
    {
        return this.stringLiteral.getValue();
    }


    /**
     * The program invocation case.
     * @return The invocation.
     */
    private Invocation invocation()
    {
        return this.invocation.getValue();
    }


    /**
     * The value reference case.
     * @return The value reference.
     */
    public ValueReference valueReference()
    {
        return this.valueReference.getValue();
    }


    // ** Identifier
    // ------------------------------------------------------------------------------------------

    /**
     * The text variable identifier. This is roughly the same as the value, but in a more
     * concise form.
     * @return The variable identifier.
     */
    public String identifier()
    {
        switch (this.kind())
        {
            case LITERAL:
                return this.value();
            case VALUE:
                return this.valueReference().valueName();
            case PROGRAM:
                return this.value();
        }

        return "";
    }


    // ** Value
    // ------------------------------------------------------------------------------------------

    // ** Setters
    // ------------------------------------------------------------------------------------------

    /**
     * Set the value for the string literal case.
     * @param newValue The string value.
     */
    public void setLiteralValue(String newValue)
    {
        this.stringLiteral.setValue(newValue);
        this.onUpdate();
    }


    /**
     * Set the value for the value case.
     * @param valueReference The value reference.
     */
    public void setValueReference(ValueReference valueReference)
    {
        removeFromState();
        this.valueReference.setValue(valueReference);
        this.onUpdate();
        addToState();
    }


    // ** String
    // ------------------------------------------------------------------------------------------

    public String value()
    {
        switch (this.kind.getValue())
        {
            case LITERAL:
                return this.stringLiteral();
            case VALUE:
                Dictionary dictionary = SheetManager.currentSheet().engine().dictionary();
                return dictionary.textValue(this.valueReference()).value();
            case PROGRAM:
                return this.reactiveValue.value();
        }

        return null;
    }


    // ** Value Set
    // ------------------------------------------------------------------------------------------

    /**
     * The name of the value set this text variable belongs to.
     * @return The value set name.
     */
    public String valueSetName()
    {
        return this.valueSetName.getValue();
    }


    // > Null
    // ------------------------------------------------------------------------------------------

    public boolean isNull()
    {
        switch (this.kind.getValue())
        {
            case LITERAL:
                return this.stringLiteral == null;
            case VALUE:
                return this.valueReference == null;
            case PROGRAM:
                return this.invocation == null;
        }

        return true;
    }


    // ** Initialize
    // ------------------------------------------------------------------------------------------

    public void initialize()
    {
        // [1] Add any variables associated with the value to the state
        // --------------------------------------------------------------------------------------

        addToState();
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    // ** Initialize
    // ------------------------------------------------------------------------------------------

    private void initializeTextVariable()
    {
        // [1] Create reaction value (if program variable)
        // --------------------------------------------------------------------------------------

        if (this.kind.getValue() == Kind.PROGRAM) {
            this.reactiveValue = new ReactiveValue<>(this.invocation.getValue(),
                                                     VariableType.TEXT);
        }
        else {
            this.reactiveValue = null;
        }
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
        dictionary.textValue(this.valueReference()).addToState();
    }


    private void removeFromState()
    {
        if (this.kind() != Kind.VALUE)
            return;

        Dictionary dictionary = SheetManager.currentSheet().engine().dictionary();
        dictionary.textValue(this.valueReference()).removeFromState();
    }


    // Kind
    // ------------------------------------------------------------------------------------------

    public enum Kind implements ToYaml
    {

        // VALUES
        // --------------------------------------------------------------------------------------

        LITERAL,
        VALUE,
        PROGRAM;


        // CONSTRUCTORS
        // --------------------------------------------------------------------------------------

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
                        new com.kispoko.tome.util.database.error.InvalidEnumError(enumString));
            }
        }


        // TO YAML
        // --------------------------------------------------------------------------------------

        public YamlBuilder toYaml()
        {
            return YamlBuilder.string(this.name().toLowerCase());
        }


        // TO STRING
        // --------------------------------------------------------------------------------------

        @Override
        public String toString()
        {
            switch (this)
            {
                case LITERAL:
                    return "Literal";
                case PROGRAM:
                    return "Program";
                case VALUE:
                    return "Value";
            }

            return "";
        }

    }


}
