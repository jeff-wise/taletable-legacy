
package com.kispoko.tome.engine.summation.term;


import com.kispoko.tome.engine.summation.SummationException;
import com.kispoko.tome.engine.variable.VariableException;
import com.kispoko.tome.engine.variable.VariableReference;
import com.kispoko.tome.lib.model.Model;

import java.util.List;



/**
 * Term
 */
public abstract class Term implements Model
{

    // INTERFACE
    // ------------------------------------------------------------------------------------------

    public abstract Integer value() throws SummationException;

    public abstract List<VariableReference> variableDependencies();

    public abstract TermSummary summary() throws VariableException;
}
