
package com.kispoko.tome.engine.function.builtin;


import com.kispoko.tome.engine.function.builtin.error.InvalidParameterTypeError;
import com.kispoko.tome.engine.function.builtin.error.WrongNumberOfParametersError;
import com.kispoko.tome.engine.interpreter.InterpreterException;
import com.kispoko.tome.engine.interpreter.error.UndefinedProgramVariableError;
import com.kispoko.tome.engine.interpreter.error.UnexpectedProgramVariableTypeError;
import com.kispoko.tome.engine.program.ProgramValueUnion;
import com.kispoko.tome.engine.program.ProgramValueType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**
 * Built-in Function
 *
 * This class contains the implementation of all built-in functions.
 */
public class BuiltInFunction
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private static Set<String> functionNames;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public static void initialize()
    {
        functionNames = new HashSet<>();
        functionNames.add("string_template");
        functionNames.add("modifier_string");
    }


    // API
    // ------------------------------------------------------------------------------------------

    public static boolean exists(String functionName)
    {
        return functionNames.contains(functionName);
    }


    public static ProgramValueUnion execute(String functionName, List<ProgramValueUnion> parameters,
                                            Map<String,ProgramValueUnion> context)
                                throws BuiltInFunctionException,
            InterpreterException
    {
        switch (functionName)
        {
            case "string_template":
                return stringTemplate(parameters, context);
            case "modifier_string":
                return modifierString(parameters);
        }

        return null;
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    private static ProgramValueUnion modifierString(List<ProgramValueUnion> parameters)
                   throws BuiltInFunctionException
    {
        // Sanity check parameters and extract integer value
        if (parameters.size() != 1) {
            throw BuiltInFunctionException.wrongNumberOfParameters(
                    new WrongNumberOfParametersError(parameters.size(), 1));
        }

        ProgramValueUnion modifierFunctionValue = parameters.get(0);

        if (modifierFunctionValue.type() != ProgramValueType.INTEGER) {
            throw BuiltInFunctionException.invalidParameterType(
                    new InvalidParameterTypeError(1,
                                                  modifierFunctionValue.type(),
                                                  ProgramValueType.INTEGER));
        }

        int modifier = modifierFunctionValue.integerValue();

        // Convert the modifier integer into a string representation
        String modifierString;

        if (modifier < 0)
            modifierString = "-" + Integer.toString(Math.abs(modifier));
        else
            modifierString = "+" + Integer.toString(modifier);

        return ProgramValueUnion.asString(modifierString);
    }


    private static ProgramValueUnion stringTemplate(List<ProgramValueUnion> parameters,
                                                    Map<String,ProgramValueUnion> context)
                                 throws BuiltInFunctionException,
            InterpreterException
    {

        // [1] Sanity check parameters and extract integer value
        // --------------------------------------------------------------------------------------

        if (parameters.size() != 1) {
            throw BuiltInFunctionException.wrongNumberOfParameters(
                    new WrongNumberOfParametersError(parameters.size(), 1));
        }

        ProgramValueUnion templateFunctionValue = parameters.get(0);

        if (templateFunctionValue.type() != ProgramValueType.STRING) {
            throw BuiltInFunctionException.invalidParameterType(
                    new InvalidParameterTypeError(1,
                                                  templateFunctionValue.type(),
                                                  ProgramValueType.STRING));
        }


        // [2] Replace all variables in template
        // --------------------------------------------------------------------------------------

        String templateString = templateFunctionValue.stringValue();

        Pattern templatePattern = Pattern.compile("\\{\\{(.*?)\\}\\}");
        Matcher matcher = templatePattern.matcher(templateString);

        List<String> templateVariableNames = new ArrayList<>();
        while (matcher.find()) {
            templateVariableNames.add(matcher.group(1));
        }


        // [3] Replace all variables with their value, assuming it is a string
        // --------------------------------------------------------------------------------------

        for (String templateVariableName : templateVariableNames)
        {
            String variableValue;
            if (context.containsKey(templateVariableName))
            {
                ProgramValueUnion variableFunctionValue = context.get(templateVariableName);
                if (variableFunctionValue.type() != ProgramValueType.STRING) {
                    throw InterpreterException.unexpectedProgramVariableType(
                            new UnexpectedProgramVariableTypeError(templateVariableName,
                                                                   variableFunctionValue.type(),
                                                                   ProgramValueType.STRING));
                }
                else {
                    variableValue = variableFunctionValue.stringValue();
                }
            }
            else
            {
                throw InterpreterException.undefinedProgramVariable(
                        new UndefinedProgramVariableError(templateVariableName));
            }

            String variableRegex = "\\{\\{" + templateVariableName + "\\}\\}";
            Pattern variablePattern = Pattern.compile(variableRegex);
            Matcher variableMatcher = variablePattern.matcher(templateString);
            templateString = variableMatcher.replaceFirst(variableValue);
        }

        return ProgramValueUnion.asString(templateString);
    }


}
