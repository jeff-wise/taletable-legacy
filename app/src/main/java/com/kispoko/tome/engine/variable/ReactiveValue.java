
package com.kispoko.tome.engine.variable;


import com.kispoko.tome.ApplicationFailure;
import com.kispoko.tome.engine.programming.interpreter.Interpreter;
import com.kispoko.tome.engine.programming.interpreter.InterpreterException;
import com.kispoko.tome.engine.programming.program.invocation.Invocation;
import com.kispoko.tome.engine.programming.program.ProgramValueUnion;
import com.kispoko.tome.sheet.SheetManager;

import java.io.Serializable;



/**
 * Reactive Value
 *
 * A reactive value is created as the result of a program. It is reactive because if a variable
 * that it depends on changes, it will update itself in response.
 */
public class ReactiveValue<A> implements Serializable
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    private A                 currentValue;

    private VariableType      type;

    private Invocation invocation;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public ReactiveValue(Invocation invocation, VariableType type)
    {
        this.currentValue      = null;

        this.type              = type;

        this.invocation = invocation;
    }


    // API
    // ------------------------------------------------------------------------------------------

    // > Value
    // ------------------------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public A value()
    {
        if (this.currentValue == null) {
            Interpreter interpreter = SheetManager.currentSheet().rulesEngine().getInterpreter();

            Object computedValue = null;
            try
            {
                ProgramValueUnion result = interpreter.evaluate(this.invocation);

                switch (this.type)
                {
                    case TEXT:
                        computedValue = result.getString();
                        break;
                    case NUMBER:
                        computedValue = result.getInteger();
                        break;
                    case BOOLEAN:
                        computedValue = result.getBoolean();
                        break;
                }
            }
            catch (InterpreterException exception)
            {
                if (exception.getErrorType() != InterpreterException.ErrorType.NULL_VARIABLE) {
                    ApplicationFailure.interpreter(exception);
                }
            }

            this.currentValue = (A) computedValue;
        }

        return this.currentValue;
    }


    public void setValue(A newValue)
    {
        this.currentValue = newValue;
    }

}