
package com.taletable.android.model.engine.variable;


/**
 * Reactive Value
 *
 * A reactive value is created as the result of a program. It is reactive because if a variable
 * that it depends on changes, it will update itself in response.
 */
//public class ReactiveValue<A> implements Serializable
//{
//
//    // PROPERTIES
//    // ------------------------------------------------------------------------------------------
//
//    private A               currentValue;
//
//    private VariableType    type;
//
//    private Invocation invocation;
//
//
//    // CONSTRUCTORS
//    // ------------------------------------------------------------------------------------------
//
//    public ReactiveValue(Invocation invocation, VariableType type)
//    {
//        this.currentValue   = null;
//
//        this.type           = type;
//
//        this.invocation     = invocation;
//    }
//
//
//    // API
//    // ------------------------------------------------------------------------------------------
//
//    // > Value
//    // ------------------------------------------------------------------------------------------
//
//    @SuppressWarnings("unchecked")
//    public A value()
//    {
//        if (this.currentValue == null)
//        {
//            Interpreter interpreter = SheetManagerOld.currentSheet().engine().getInterpreter();
//
//            Object computedValue = null;
//
//            try
//            {
//                EngineValueUnion result = interpreter.evaluate(this.invocation);
//
//                switch (this.type)
//                {
//                    case TEXT:
//                        computedValue = result.stringValue();
//                        break;
//                    case NUMBER:
//                        computedValue = result.integerValue();
//                        if (computedValue == null)
//                            Log.d("***REACTIVE VALUE", "computer value is null");
//                        break;
//                    case BOOLEAN:
//                        computedValue = result.booleanValue();
//                        break;
//                }
//            }
//            catch (InterpreterException exception)
//            {
//                Log.d("***REACTIVE VALUE", "exception");
//                // Why??
////                if (exception.getErrorType() != InterpreterException.ErrorType.NULL_VARIABLE) {
////                    ApplicationFailure.interpreter(exception);
////                }
//                ApplicationFailure.interpreter(exception);
//            }
//
//            this.currentValue = (A) computedValue;
//        }
//
//        return this.currentValue;
//    }
//
//
//    public void setValue(A newValue)
//    {
//        this.currentValue = newValue;
//    }
//
//}
