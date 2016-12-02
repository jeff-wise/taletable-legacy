
package com.kispoko.tome.rules.programming.builtin;


import com.kispoko.tome.rules.programming.builtin.error.InvalidParameterTypeError;
import com.kispoko.tome.rules.programming.builtin.error.WrongNumberOfParametersError;
import com.kispoko.tome.rules.programming.evaluation.EvaluationException;
import com.kispoko.tome.rules.programming.evaluation.error.UndefinedProgramVariableError;
import com.kispoko.tome.rules.programming.evaluation.error.UnexpectedProgramVariableTypeError;
import com.kispoko.tome.rules.programming.program.ProgramValue;
import com.kispoko.tome.rules.programming.program.ProgramValueType;

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
    }


    // API
    // ------------------------------------------------------------------------------------------

    public static boolean exists(String functionName)
    {
        return functionNames.contains(functionName);
    }


    public static ProgramValue execute(String functionName, List<ProgramValue> parameters,
                                       Map<String,ProgramValue> context)
                                throws BuiltInFunctionException,
                                       EvaluationException
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

    private static ProgramValue modifierString(List<ProgramValue> parameters)
                   throws BuiltInFunctionException
    {
        // Sanity check parameters and extract integer value
        if (parameters.size() != 1) {
            throw BuiltInFunctionException.wrongNumberOfParameters(
                    new WrongNumberOfParametersError(parameters.size(), 1));
        }

        ProgramValue modifierFunctionValue = parameters.get(0);

        if (modifierFunctionValue.getType() != ProgramValueType.INTEGER) {
            throw BuiltInFunctionException.invalidParameterType(
                    new InvalidParameterTypeError(1,
                                                  modifierFunctionValue.getType(),
                                                  ProgramValueType.INTEGER));
        }

        int modifier = modifierFunctionValue.getInteger();

        // Convert the modifier integer into a string representation
        String modifierString = null;

        if (modifier < 0)
            modifierString = "-" + Integer.toString(Math.abs(modifier));
        else if (modifier == 0)
            modifierString = Integer.toString(modifier);
        else
            modifierString = "+" + Integer.toString(modifier);

        return ProgramValue.asStringTemp(modifierString);
    }


    private static ProgramValue stringTemplate(List<ProgramValue> parameters,
                                               Map<String,ProgramValue> context)
                                 throws BuiltInFunctionException,
                                        EvaluationException
    {
        // [1] Sanity check parameters and extract integer value
        // --------------------------------------------------------------------------------------
        if (parameters.size() != 1) {
            throw BuiltInFunctionException.wrongNumberOfParameters(
                    new WrongNumberOfParametersError(parameters.size(), 1));
        }

        ProgramValue templateFunctionValue = parameters.get(0);

        if (templateFunctionValue.getType() != ProgramValueType.STRING) {
            throw BuiltInFunctionException.invalidParameterType(
                    new InvalidParameterTypeError(1,
                                                  templateFunctionValue.getType(),
                                                  ProgramValueType.STRING));
        }


        // [2] Replace all variables in template
        // --------------------------------------------------------------------------------------
        String templateString = templateFunctionValue.getString();

        Pattern templatePattern = Pattern.compile("\\{\\{(.*?)\\}\\}");
        Matcher matcher = templatePattern.matcher(templateString);

        List<String> templateVariableNames = new ArrayList<>();
        while (matcher.find()) {
            templateVariableNames.add(matcher.group());
        }


        // [3] Replace all variables with their value, assuming it is a string
        // --------------------------------------------------------------------------------------
        for (String templateVariableName : templateVariableNames)
        {
            String variableValue;
            if (context.containsKey(templateVariableName)) {
                ProgramValue variableFunctionValue = context.get(templateVariableName);
                if (variableFunctionValue.getType() != ProgramValueType.STRING) {
                    throw EvaluationException.unexpectedProgramVariableType(
                            new UnexpectedProgramVariableTypeError(templateVariableName,
                                                                   variableFunctionValue.getType(),
                                                                   ProgramValueType.STRING));
                }
                else {
                    variableValue = variableFunctionValue.getString();
                }
            } else {
                throw EvaluationException.undefinedProgramVariable(
                        new UndefinedProgramVariableError(templateVariableName));
            }

            String variableRegex = "\\{\\{" + templateVariableName + "\\}\\}";
            Pattern variablePattern = Pattern.compile(variableRegex);
            Matcher variableMatcher = variablePattern.matcher(templateString);
            templateString = variableMatcher.replaceFirst(variableValue);
        }

        return ProgramValue.asStringTemp(templateString);
    }


}
