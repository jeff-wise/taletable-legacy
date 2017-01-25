
package com.kispoko.tome.engine.variable;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.kispoko.tome.ApplicationFailure;
import com.kispoko.tome.activity.NumberActivity;
import com.kispoko.tome.activity.SheetActivity;
import com.kispoko.tome.activity.SummationActivity;
import com.kispoko.tome.engine.State;
import com.kispoko.tome.error.InvalidCaseError;
import com.kispoko.tome.error.UnknownVariantError;
import com.kispoko.tome.exception.InvalidDataException;
import com.kispoko.tome.engine.program.invocation.Invocation;
import com.kispoko.tome.engine.summation.Summation;
import com.kispoko.tome.exception.UnionException;
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
 * Number Variable
 */
public class NumberVariable extends Variable
                            implements Model, ToYaml, Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID                        id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private PrimitiveFunctor<String>    name;
    private PrimitiveFunctor<String>    label;

    private PrimitiveFunctor<Integer>   literalValue;
    private ModelFunctor<Invocation>    invocationValue;
    private ModelFunctor<Summation>     summation;

    private PrimitiveFunctor<Kind>      kind;

    private PrimitiveFunctor<Boolean>   isNamespaced;

    private PrimitiveFunctor<String[]>  tags;


    // > Internal
    // ------------------------------------------------------------------------------------------

    private ReactiveValue<Integer>      reactiveValue;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public NumberVariable()
    {
        super();

        this.id                 = null;

        this.name               = new PrimitiveFunctor<>(null, String.class);
        this.label              = new PrimitiveFunctor<>(null, String.class);

        this.literalValue       = new PrimitiveFunctor<>(null, Integer.class);
        this.invocationValue    = ModelFunctor.empty(Invocation.class);
        this.summation          = ModelFunctor.empty(Summation.class);

        this.kind               = new PrimitiveFunctor<>(null, Kind.class);

        this.isNamespaced       = new PrimitiveFunctor<>(null, Boolean.class);

        this.tags               = new PrimitiveFunctor<>(null, String[].class);

        this.reactiveValue      = null;
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
                           Object value,
                           Kind kind,
                           Boolean isNamespaced,
                           List<String> tags)
    {
        super();

        this.id                     = id;

        this.name                   = new PrimitiveFunctor<>(name, String.class);
        this.label                  = new PrimitiveFunctor<>(label, String.class);

        this.literalValue = new PrimitiveFunctor<>(null, Integer.class);
        this.invocationValue        = ModelFunctor.full(null, Invocation.class);
        this.summation              = ModelFunctor.full(null, Summation.class);

        this.kind                   = new PrimitiveFunctor<>(kind, Kind.class);

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
            case PROGRAM:
                this.invocationValue.setValue((Invocation) value);
                break;
            case SUMMATION:
                this.summation.setValue((Summation) value);
                break;
        }

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
                                           Integer integerValue,
                                           Boolean isNamespaced,
                                           List<String> tags)
    {
        return new NumberVariable(id, name, label, integerValue, Kind.LITERAL, isNamespaced, tags);
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
        return new NumberVariable(id, null, null, integerValue, Kind.LITERAL, null, null);
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
                                           Invocation invocation,
                                           Boolean isNamespaced,
                                           List<String> tags)
    {
        return new NumberVariable(id, name, label, invocation, Kind.PROGRAM, isNamespaced, tags);
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
                                             Summation summation,
                                             Boolean isNamespaced,
                                             List<String> tags)
    {
        return new NumberVariable(id, name, label, summation, Kind.SUMMATION, isNamespaced, tags);
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
        Kind         kind         = Kind.fromYaml(yaml.atKey("type"));
        Boolean      isNamespaced = yaml.atMaybeKey("namespaced").getBoolean();
        List<String> tags         = yaml.atMaybeKey("tags").getStringList();

        switch (kind)
        {
            case LITERAL:
                Integer integerValue  = yaml.atKey("value").getInteger();
                return NumberVariable.asInteger(id, name, label, integerValue, isNamespaced, tags);
            case PROGRAM:
                Invocation invocation = Invocation.fromYaml(yaml.atKey("value"));
                return NumberVariable.asProgram(id, name, label, invocation, isNamespaced, tags);
            case SUMMATION:
                Summation summation = Summation.fromYaml(yaml.atKey("value"));
                return NumberVariable.asSummation(id, name, label, summation, isNamespaced, tags);
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
                .putYaml("type", this.kind())
                .putBoolean("namespaced", this.isNamespaced())
                .putStringList("tags", this.tags());
    }

    // > State
    // ------------------------------------------------------------------------------------------

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


    @Override
    public List<VariableReference> dependencies()
    {
        List<VariableReference> variableDependencies = new ArrayList<>();

        switch (this.kind.getValue())
        {
            case LITERAL:
                break;
            case PROGRAM:
                variableDependencies = this.invocation().variableDependencies();
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
           throws VariableException
    {
        switch (this.kind())
        {
            case LITERAL:
                return this.value().toString();
            case PROGRAM:
                return this.value().toString();
            case SUMMATION:
                return this.summation.getValue().valueString();
            default:
                ApplicationFailure.union(
                        UnionException.unknownVariant(
                                new UnknownVariantError(Kind.class.getName())));
        }

        return "";
    }


    // > Initialize
    // ------------------------------------------------------------------------------------------

    public void initialize()
    {
        this.addToState();
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

    /**
     * The literal value case.
     * @return The integer value.
     */
    public Integer literalValue()
    {
        return this.literalValue.getValue();
    }


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
            case PROGRAM:
                // Do Nothing?
                //this.reactiveValue.setValue(newValue);
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
           throws VariableException
    {
        switch (this.kind.getValue())
        {
            case LITERAL:
                return this.literalValue.getValue();
            case PROGRAM:
                return this.reactiveValue.value();
            case SUMMATION:
                return this.summation.getValue().value();
        }

        return null;
    }



    // > Edit Activity
    // ------------------------------------------------------------------------------------------


    /**
     * Open the activity to edit this variable.
     */
    public void openEditActivity(String widgetName)
    {
        Context context = SheetManager.currentSheetContext();

        switch (this.kind())
        {
            case SUMMATION:
                Intent intent = new Intent(context, SummationActivity.class);
                intent.putExtra("widget_name", widgetName);
                intent.putExtra("summation", this.summation());
                ((Activity) context).startActivityForResult(intent, SheetActivity.COMPONENT_EDIT);
                break;
            case LITERAL:
                Integer variableValue;
                try {
                    variableValue = this.value();
                }
                catch (VariableException exception) {
                    return;
                }
                Intent numberIntent = new Intent(context, NumberActivity.class);
                numberIntent.putExtra("widget_name", widgetName);
                numberIntent.putExtra("value", variableValue);
                ((Activity) context).startActivityForResult(numberIntent,
                                                            SheetActivity.COMPONENT_EDIT);
                break;
        }
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


    // ** Variable State
    // ------------------------------------------------------------------------------------------

    /**
     * Add any variables associated with the current value to the state.
     */
    private void addToState()
    {
    }


    private void removeFromState()
    {
    }


    // KIND
    // ------------------------------------------------------------------------------------------

    public enum Kind implements ToYaml
    {

        // VALUES
        // ------------------------------------------------------------------------------------------

        LITERAL,
        PROGRAM,
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
                        new com.kispoko.tome.util.database.error.InvalidEnumError(enumString));
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
                case PROGRAM:
                    return "Program";
                case SUMMATION:
                    return "Summation";
            }

            return "";
        }

    }



}
