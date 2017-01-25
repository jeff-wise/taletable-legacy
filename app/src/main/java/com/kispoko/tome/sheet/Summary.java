
package com.kispoko.tome.sheet;


import com.kispoko.tome.util.model.Model;
import com.kispoko.tome.util.tuple.Tuple2;
import com.kispoko.tome.util.value.PrimitiveFunctor;

import java.util.UUID;



/**
 * Sheet Summary
 */
public class Summary implements Model
{

    // PROPERTIES
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    private UUID id;


    // > Functors
    // ------------------------------------------------------------------------------------------

    private PrimitiveFunctor<String>    sheetName;
    private PrimitiveFunctor<Long>      lastUsed;
    private PrimitiveFunctor<String>    campaignName;

    private PrimitiveFunctor<String>    feature1Name;
    private PrimitiveFunctor<String>    feature1Value;

    private PrimitiveFunctor<String>    feature2Name;
    private PrimitiveFunctor<String>    feature2Value;

    private PrimitiveFunctor<String>    feature3Name;
    private PrimitiveFunctor<String>    feature3Value;


    // CONSTRUCTORS
    // ------------------------------------------------------------------------------------------

    public Summary()
    {
        this.id             = null;

        this.sheetName      = new PrimitiveFunctor<>(null, String.class);
        this.lastUsed       = new PrimitiveFunctor<>(null, Long.class);
        this.campaignName   = new PrimitiveFunctor<>(null, String.class);

        this.feature1Name   = new PrimitiveFunctor<>(null, String.class);
        this.feature1Value  = new PrimitiveFunctor<>(null, String.class);

        this.feature2Name   = new PrimitiveFunctor<>(null, String.class);
        this.feature2Value  = new PrimitiveFunctor<>(null, String.class);

        this.feature3Name   = new PrimitiveFunctor<>(null, String.class);
        this.feature3Value  = new PrimitiveFunctor<>(null, String.class);
    }


    public Summary(UUID id,
                   String sheetName,
                   Long lastUsed,
                   String campaignName,
                   Tuple2<String,String> feature1,
                   Tuple2<String,String> feature2,
                   Tuple2<String,String> feature3)
    {
        this.id             = id;

        this.sheetName      = new PrimitiveFunctor<>(sheetName, String.class);
        this.lastUsed       = new PrimitiveFunctor<>(lastUsed, Long.class);
        this.campaignName   = new PrimitiveFunctor<>(campaignName, String.class);

        this.feature1Name   = new PrimitiveFunctor<>(feature1.getItem1(), String.class);
        this.feature1Value  = new PrimitiveFunctor<>(feature1.getItem2(), String.class);

        this.feature2Name   = new PrimitiveFunctor<>(feature2.getItem1(), String.class);
        this.feature2Value  = new PrimitiveFunctor<>(feature2.getItem2(), String.class);

        this.feature3Name   = new PrimitiveFunctor<>(feature3.getItem1(), String.class);
        this.feature3Value  = new PrimitiveFunctor<>(feature3.getItem2(), String.class);
    }


    // API
    // ------------------------------------------------------------------------------------------

    // > Model
    // ------------------------------------------------------------------------------------------

    // ** Id
    // ------------------------------------------------------------------------------------------

    public UUID getId()
    {
        return this.id;
    }


    public void setId(UUID id)
    {
        this.id = id;
    }


    // ** On Load
    // ------------------------------------------------------------------------------------------

    /**
     * This method is called when the Roleplay is completely loaded for the first time.
     */
    public void onLoad() { }


    // API
    // ------------------------------------------------------------------------------------------

    // > State
    // ------------------------------------------------------------------------------------------

    /**
     * The sheet name.
     * @return The sheet name.
     */
    public String sheetName()
    {
        return this.sheetName.getValue();
    }


    /**
     * The time the sheet was last used.
     * @return The last used time.
     */
    public Long lastUsed()
    {
        return this.lastUsed.getValue();
    }


    /**
     * The campaign name.
     * @return The campaign name.
     */
    public String campaignName()
    {
        return this.campaignName.getValue();
    }


    /**
     * The first feature name.
     * @return The feature name.
     */
    public String feature1Name()
    {
        return this.feature1Name.getValue();
    }


    /**
     * The first feature value.
     * @return The feature value string.
     */
    public String feature1Value()
    {
        return this.feature1Value.getValue();
    }


    /**
     * The second feature name.
     * @return The feature name.
     */
    public String feature2Name()
    {
        return this.feature2Name.getValue();
    }


    /**
     * The second feature value.
     * @return The feature value string.
     */
    public String feature2Value()
    {
        return this.feature2Value.getValue();
    }


    /**
     * The third feature name.
     * @return The feature name.
     */
    public String feature3Name()
    {
        return this.feature3Name.getValue();
    }


    /**
     * The third feature value.
     * @return The feature value string.
     */
    public String feature3Value()
    {
        return this.feature3Value.getValue();
    }


}
