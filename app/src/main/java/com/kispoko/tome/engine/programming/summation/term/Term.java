
package com.kispoko.tome.engine.programming.summation.term;


import com.kispoko.tome.engine.programming.variable.VariableException;
import com.kispoko.tome.engine.programming.variable.VariableReference;
import com.kispoko.tome.util.model.Model;

import java.util.List;



/**
 * Term
 */
public abstract class Term implements Model
{

    // INTERFACE
    // ------------------------------------------------------------------------------------------

    public abstract Integer value() throws VariableException;

    public abstract List<VariableReference> variableDependencies();
}
