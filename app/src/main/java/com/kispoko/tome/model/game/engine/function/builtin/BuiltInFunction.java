
package com.kispoko.tome.model.game.engine.function.builtin;


import java.util.HashSet;
import java.util.Set;


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

//
//    public static EngineValueUnion execute(String functionName, List<EngineValueUnion> parameters,
//                                           Map<String,EngineValueUnion> context)
//                                throws BuiltInFunctionException,
//            InterpreterException
//    {
//        switch (functionName)
//        {
//            case "string_template":
//                return stringTemplate(parameters, context);
//            case "modifier_string":
//                return modifierString(parameters);
//        }
//
//        return null;
//    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------
//
//    private static EngineValueUnion modifierString(List<EngineValueUnion> parameters)
//                   throws BuiltInFunctionException
//    {
//        // Sanity check parameters and extract integer value
//        if (parameters.size() != 1) {
//            throw BuiltInFunctionException.wrongNumberOfParameters(
//                    new WrongNumberOfParametersError(parameters.size(), 1));
//        }
//
//        EngineValueUnion modifierFunctionValue = parameters.get(0);
//
//        if (modifierFunctionValue.type() != EngineDataType.INTEGER) {
//            throw BuiltInFunctionException.invalidParameterType(
//                    new InvalidParameterTypeError(1,
//                                                  modifierFunctionValue.type(),
//                                                  EngineDataType.INTEGER));
//        }
//
//        int modifier = modifierFunctionValue.integerValue();
//
//        // Convert the modifier integer into a string representation
//        String modifierString;
//
//        if (modifier < 0)
//            modifierString = "-" + Integer.toString(Math.abs(modifier));
//        else
//            modifierString = "+" + Integer.toString(modifier);
//
//        return EngineValueUnion.asString(modifierString);
//    }
//
//
//    private static EngineValueUnion stringTemplate(List<EngineValueUnion> parameters,
//                                                   Map<String,EngineValueUnion> context)
//                                 throws BuiltInFunctionException,
//            InterpreterException
//    {
//
//        // [1] Sanity check parameters and extract integer value
//        // --------------------------------------------------------------------------------------
//
//        if (parameters.size() != 1) {
//            throw BuiltInFunctionException.wrongNumberOfParameters(
//                    new WrongNumberOfParametersError(parameters.size(), 1));
//        }
//
//        EngineValueUnion templateFunctionValue = parameters.get(0);
//
//        if (templateFunctionValue.type() != EngineDataType.STRING) {
//            throw BuiltInFunctionException.invalidParameterType(
//                    new InvalidParameterTypeError(1,
//                                                  templateFunctionValue.type(),
//                                                  EngineDataType.STRING));
//        }
//
//
//        // [2] Replace all variables in template
//        // --------------------------------------------------------------------------------------
//
//        String templateString = templateFunctionValue.stringValue();
//
//        Pattern templatePattern = Pattern.compile("\\{\\{(.*?)\\}\\}");
//        Matcher matcher = templatePattern.matcher(templateString);
//
//        List<String> templateVariableNames = new ArrayList<>();
//        while (matcher.find()) {
//            templateVariableNames.add(matcher.group(1));
//        }
//
//
//        // [3] Replace all variables with their value, assuming it is a string
//        // --------------------------------------------------------------------------------------
//
//        for (String templateVariableName : templateVariableNames)
//        {
//            String variableValue;
//            if (context.containsKey(templateVariableName))
//            {
//                EngineValueUnion variableFunctionValue = context.get(templateVariableName);
//                if (variableFunctionValue.type() != EngineDataType.STRING) {
//                    throw InterpreterException.unexpectedProgramVariableType(
//                            new UnexpectedProgramVariableTypeError(templateVariableName,
//                                                                   variableFunctionValue.type(),
//                                                                   EngineDataType.STRING));
//                }
//                else {
//                    variableValue = variableFunctionValue.stringValue();
//                }
//            }
//            else
//            {
//                throw InterpreterException.undefinedProgramVariable(
//                        new UndefinedProgramVariableError(templateVariableName));
//            }
//
//            String variableRegex = "\\{\\{" + templateVariableName + "\\}\\}";
//            Pattern variablePattern = Pattern.compile(variableRegex);
//            Matcher variableMatcher = variablePattern.matcher(templateString);
//            templateString = variableMatcher.replaceFirst(variableValue);
//        }
//
//        return EngineValueUnion.asString(templateString);
//    }
//

}
