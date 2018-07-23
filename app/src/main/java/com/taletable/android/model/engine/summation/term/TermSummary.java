
package com.taletable.android.rts.entity.engine.definition.summation.term;


import com.taletable.android.util.tuple.Tuple2;

import java.util.List;



/**
 * Term Summary
 *
 * A summary of the variables in the term, along with description values, for use
 * in a summation.
 */
public class TermSummary
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private String                      name;
    private List<Tuple2<String,String>> components;


    // CONSTRUCTORS
    // --------------- -------------------------------------------------------------------------

    public TermSummary(String name, List<Tuple2<String,String>> components)
    {
        this.name       = name;
        this.components = components;
    }


    // API
    // -----------------------------------------------------------------------------------------

    public String name()
    {
        return this.name;
    }


    public List<Tuple2<String,String>> components()
    {
        return this.components;
    }


}
