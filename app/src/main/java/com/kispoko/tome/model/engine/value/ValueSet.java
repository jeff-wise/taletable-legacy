
package com.kispoko.tome.model.engine.value;


import java.util.List;



/**
 * Value Set Interface
 */
public interface ValueSet
{
    String           name();
    String           label();
    String           labelSingular();
    String           description();
    int              size();
    List<ValueUnion> values();
    int              lengthOfLongestValueString();
    boolean          hasValue(String valueName);
    ValueUnion       valueWithName(String valueName);
}
