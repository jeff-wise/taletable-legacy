
package com.kispoko.tome.engine.programming.summation.term;


import com.kispoko.tome.engine.programming.summation.SummationException;
import com.kispoko.tome.util.model.Model;

import java.util.List;



/**
 * Term
 */
public abstract class Term implements Model
{

    // INTERFACE
    // ------------------------------------------------------------------------------------------

    public abstract Integer value() throws SummationException;

    public abstract List<String> variableDependencies();
}
