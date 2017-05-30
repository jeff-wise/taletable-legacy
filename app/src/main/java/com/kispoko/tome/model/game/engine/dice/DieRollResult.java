
package com.kispoko.tome.model.game.engine.dice;


import android.support.annotation.Nullable;

import java.util.Random;


/**
 * Die Roll Result
 */
public class DieRollResult
{

    // PROPERTIES
    // -----------------------------------------------------------------------------------------

    private int     diceSides;
    private int     value;
    private String  group;


    // > Internal
    // ------------------------------------------------------------------------------------------

    private static Random randomGen = new Random();


    // CONSTRUCTORS
    // -----------------------------------------------------------------------------------------

    public DieRollResult(int diceSides, int value, String group)
    {
        this.diceSides  = diceSides;
        this.value      = value;
        this.group      = group;
    }


    public static DieRollResult generate(int diceSides, String group)
    {
        return new DieRollResult(diceSides, roll(diceSides), group);
    }


    public static DieRollResult generate(int diceSides)
    {
        return DieRollResult.generate(diceSides, null);
    }


    // API
    // -----------------------------------------------------------------------------------------

    public int diceSides()
    {
        return this.diceSides;
    }


    public int value()
    {
        return this.value;
    }


    @Nullable
    public String group()
    {
        return this.group;
    }


    // INTERNAL
    // ------------------------------------------------------------------------------------------

    private static int roll(int max)
    {
        return randomGen.nextInt(max) + 1;
    }


}
