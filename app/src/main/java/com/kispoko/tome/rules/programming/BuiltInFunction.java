
package com.kispoko.tome.rules.programming;


import com.kispoko.tome.rules.programming.evaluation.EvaluationException;
import com.kispoko.tome.rules.programming.evaluation.ProgramValue;
import com.kispoko.tome.rules.programming.evaluation.ProgramValueType;

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

    private static ProgramValue modifierString(List<ProgramValue> parameters)
                                 throws EvaluationException
    {
        // Sanity check parameters and extract integer value
        if (parameters.size() != 1)
            throw new EvaluationException();

        ProgramValue modifierFunctionValue = parameters.get(0);

        if (modifierFunctionValue.getType() != ProgramValueType.INTEGER)
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

        return new ProgramValue(modifierString, ProgramValueType.STRING);
    }


    private static ProgramValue stringTemplate(List<ProgramValue> parameters,
                                               Map<String,ProgramValue> context)
                                 throws EvaluationException
    {
        // Sanity check parameters and extract integer value
        if (parameters.size() != 1)
            throw new EvaluationException();

        ProgramValue templateFunctionValue = parameters.get(0);

        if (templateFunctionValue.getType() != ProgramValueType.STRING)
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
                ProgramValue variableFunctionValue = context.get(templateVariable);
                if (variableFunctionValue.getType() != ProgramValueType.STRING)
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

        return new ProgramValue(templateString, ProgramValueType.STRING);
    }


}
