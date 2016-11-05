
package com.kispoko.tome.rules;


import com.kispoko.tome.rules.evaluation.EvaluationException;
import com.kispoko.tome.rules.function.FunctionValue;
import com.kispoko.tome.rules.function.FunctionValueType;

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


    public static FunctionValue execute(String functionName, List<FunctionValue> parameters,
                                        Map<String,FunctionValue> context)
                                throws EvaluationException
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

    private static FunctionValue modifierString(List<FunctionValue> parameters)
                                 throws EvaluationException
    {
        // Sanity check parameters and extract integer value
        if (parameters.size() != 1)
            throw new EvaluationException();

        FunctionValue modifierFunctionValue = parameters.get(0);

        if (modifierFunctionValue.getType() != FunctionValueType.INTEGER)
            throw new EvaluationException();

        int modifier = modifierFunctionValue.getInteger();

        // Convert the modifier integer into a string representation
        String modifierString = null;

        if (modifier < 0)
            modifierString = "-" + Integer.toString(Math.abs(modifier));
        else if (modifier == 0)
            modifierString = Integer.toString(modifier);
        else
            modifierString = "+" + Integer.toString(modifier);

        return new FunctionValue(modifierString, FunctionValueType.STRING);
    }


    private static FunctionValue stringTemplate(List<FunctionValue> parameters,
                                                Map<String,FunctionValue> context)
                                 throws EvaluationException
    {
        // Sanity check parameters and extract integer value
        if (parameters.size() != 1)
            throw new EvaluationException();

        FunctionValue templateFunctionValue = parameters.get(0);

        if (templateFunctionValue.getType() != FunctionValueType.STRING)
            throw new EvaluationException();

        // Replace all variables in template
        String templateString = templateFunctionValue.getString();

        Pattern templatePattern = Pattern.compile("\\{\\{(.*?)\\}\\}");
        Matcher matcher = templatePattern.matcher(templateString);

        List<String> templateVariables = new ArrayList<>();
        while (matcher.find()) {
            templateVariables.add(matcher.group());
        }

        // Replace all variables with their value, assuming it is a string
        for (String templateVariable : templateVariables)
        {
            String variableValue;
            if (context.containsKey(templateVariable)) {
                FunctionValue variableFunctionValue = context.get(templateVariable);
                if (variableFunctionValue.getType() != FunctionValueType.STRING)
                    throw new EvaluationException();
                else
                    variableValue = variableFunctionValue.getString();
            } else {
                throw new EvaluationException();
            }

            String variableRegex = "\\{\\{" + templateVariable + "\\}\\}";
            Pattern variablePattern = Pattern.compile(variableRegex);
            Matcher variableMatcher = variablePattern.matcher(templateString);
            templateString = variableMatcher.replaceFirst(variableValue);
        }

        return new FunctionValue(templateString, FunctionValueType.STRING);
    }


}
